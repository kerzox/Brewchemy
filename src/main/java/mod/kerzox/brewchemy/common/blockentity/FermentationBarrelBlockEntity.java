package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.client.sounds.LoopingSoundInstance;
import mod.kerzox.brewchemy.common.block.base.IClientTickable;
import mod.kerzox.brewchemy.common.blockentity.base.RecipeBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.ICompoundSerializer;
import mod.kerzox.brewchemy.common.capabilities.fluid.SingleFluidInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackHandlerUtils;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.recipe.FermentationRecipe;
import mod.kerzox.brewchemy.common.event.TickUtils;
import mod.kerzox.brewchemy.common.fluid.alcohol.AgeableAlcoholStack;
import mod.kerzox.brewchemy.common.network.RequestDataPacket;
import mod.kerzox.brewchemy.common.particle.FermentationParticleType;
import mod.kerzox.brewchemy.common.util.SoundHandler;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/*

    Fermentation barrel can be a single block or 2x2
    the size of the barrel changes only the internal fluid inventory size


 */

/*
    TODO
    The user is blind to the fermentation process they will not have any clue on how far it is besides the time they put it in.

    Opting to provide the client with a visual confirmation of each stage of the process

    Fermentation will last days, each day of fermenting should provide a new coloured particle effect so reached 1 full day we show a white particle effect, then day 2, green etc.
    Provide a sound effect when the fluid is has reached its perfection range (this range is fairly safe and can be quite long depending on the alcohol.)
    Provide a sound effect when the fluid has fermented too long, I.E its spoil tick.


 */

public class FermentationBarrelBlockEntity extends RecipeBlockEntity<FermentationRecipe> implements IClientTickable {

    enum FermentationState {
        MATURING,
        PERFECT,
        SPOILT,
        NONE
    }

    private Controller controller = new Controller(this);
    private CompoundTag tag;

    private final ItemInventory itemInventory = ItemInventory.of(1, 1).addInput(Direction.values());
    private final SingleFluidInventory.Simple fluidInventory = (SingleFluidInventory.Simple) SingleFluidInventory.simple(16000).addInput(Direction.values());

    private int fermentationStartingTick = 0;
    private int catalystRemaining = 0;

    private FermentationState state = FermentationState.NONE;
    private AgeableAlcoholStack inputFluid;

    private Queue<ParticleOptions> particleSpawnQueue = new LinkedList<>();

    // when tapped the barrel will no longer ferment
    private boolean tapped;


    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(BrewchemyRegistry.BlockEntities.FERMENTATION_BARREL_BLOCK_ENTITY.get(), BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), pos, state);
        addCapabilities(itemInventory, fluidInventory);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level.isClientSide) {
            RequestDataPacket.get(getBlockPos());
        } else {
            // if we load from a tag, and we don't already have a controller
            if (tag != null) {
                // create a temp controller and read the tag
                Controller temp = new Controller(this);
                temp.deserialize(tag);

                // we check if we are the master of the old controller from disk
                if (temp.masterBlock == this) {
                    // loop over the saved barrels and set their controller to us.
                    for (FermentationBarrelBlockEntity barrel : temp.getBarrels()) {
                        barrel.setController(temp);
                    }
                    this.controller = temp;
                    this.controller.sync();
                }
            }
            else {
                this.controller = new Controller(this);
                controller.tryFormation(this);
            }
        }
    }

    @Override
    public void tick() {
        if (this.controller.masterBlock == this) {
            super.tick();
            if (!particleSpawnQueue.isEmpty()) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(particleSpawnQueue.poll(), getBlockPos().getX() + 0.5f, getBlockPos().getY() + 0.5d, getBlockPos().getZ() + 0.5f, 12,.5d, .25d, .5d, 0);
                }
            }
        }
    }

    @Override
    public void clientTick(SoundHandler soundHandler) {
        if (isWorking()) {
            soundHandler.play(LoopingSoundInstance.create(worldPosition, BrewchemyRegistry.Sounds.FERMENTING_BUBBLES.get(), SoundSource.BLOCKS, 0.5f));
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // if we are part of a multiblock and we aren't the controller master then we must return the master inventory
        if (controller.isFormed() && controller.getMasterBlock() != this) {
            return controller.getMasterBlock().getCapability(cap, side);
        }

        return super.getCapability(cap, side);
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public RecipeInventory getRecipeInventory() {
        return new RecipeInventory(itemInventory, fluidInventory);
    }

    @Override
    protected boolean hasAResult(FermentationRecipe workingRecipe) {
        return workingRecipe.assembleResultItems(getRecipeInventory(), RegistryAccess.EMPTY).length > 0;
    }

    public void setTapped(boolean bool) {
        this.tapped = bool;
        if (tapped) fluidInventory.addOutput(controller.formingDirection != null ? controller.formingDirection : getBlockState().getValue(HorizontalDirectionalBlock.FACING));
        else fluidInventory.removeOutputs(Direction.values());
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {

        if (!pLevel.isClientSide) {
            ItemStack held = pPlayer.getItemInHand(pHand);
            if (pHand == InteractionHand.MAIN_HAND) {

                SingleFluidInventory.Simple simpleFluidInv = (SingleFluidInventory.Simple) getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get();

                pPlayer.sendSystemMessage(Component.literal("Capacity: " + simpleFluidInv.getTankCapacity(0)));
                pPlayer.sendSystemMessage(Component.literal("Fluid Stored: " + simpleFluidInv.getFluidInTank().getAmount() + " ").append(simpleFluidInv.getFluidInTank().getDisplayName()));

                if (!simpleFluidInv.getFluidInTank().isEmpty()) {
                    pPlayer.sendSystemMessage(Component.literal("Age: " + new AgeableAlcoholStack(simpleFluidInv.getFluidInTank()).getAge()));
                }

                if (held.is(BrewchemyRegistry.Items.BARREL_TAP.get()) && !controller.masterBlock.tapped) {
                    controller.masterBlock.setTapped(true);
                    held.shrink(1);
                    controller.sync();
                }
                else if (held.isEmpty() && pPlayer.isShiftKeyDown() && controller.masterBlock.tapped) {
                    controller.masterBlock.setTapped(false);
                    pPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(BrewchemyRegistry.Items.BARREL_TAP.get()));
                    controller.sync();
                }

            }

            if (held.is(BrewchemyRegistry.Tags.YEAST)) {
                ItemStackHandlerUtils.insertAndModifyStack(controller.masterBlock.itemInventory.getInputHandler(), held, 1);
            }

        }

        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    public void startRecipe(FermentationRecipe recipeToWork) {
        super.startRecipe(recipeToWork);
        catalystRemaining = TickUtils.minecraftDaysToTicks(1) * itemInventory.getInputHandler().getStackInSlot(0).getCount();
        fermentationStartingTick = tick;
        inputFluid = new AgeableAlcoholStack(fluidInventory.getFluidInTank());
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; i++) {
                particleSpawnQueue.add(new FermentationParticleType.Options(FermentationParticleType.PARTICLE_COLOURS[0]));
            }
        }
    }

    @Override
    protected void onRecipeFinish(FermentationRecipe workingRecipe) {
        if (tapped) return;
        if (inputFluid == null) return;
        if (inputFluid.getFluidStack() == null) return;

        inputFluid.ageAlcohol(1);

        int age = inputFluid.getAge();
        int maturationStart = inputFluid.getAsType().getMatureTick();
        int spoilStart = inputFluid.getAsType().getSpoilTick();
        int[] perfectionWindow = inputFluid.getAsType().getPerfectionRange();

        int ticksPast = tick - fermentationStartingTick;
        int daysPast = ticksPast / TickUtils.minecraftDaysToTicks(1);
        int ticksPerDay = TickUtils.minecraftDaysToTicks(1);

        if (ticksPast % ticksPerDay == 0) {
            int day = ticksPast / ticksPerDay;
            int[] colours = FermentationParticleType.PARTICLE_COLOURS;
            if (level instanceof ServerLevel serverLevel) {
                int color = (day >= colours.length) ? 0xFF542d18 : colours[day];
                for (int i = 0; i < 5; i++) {
                    particleSpawnQueue.add(new FermentationParticleType.Options(color));
                }
            }
        }

        switch (state) {
            case NONE:
                if (age >= maturationStart) {
                    transitionTo(FermentationState.MATURING);
                }
                break;

            case MATURING:
                if (age >= spoilStart) {
                    transitionTo(FermentationState.SPOILT);
                } else if (age >= perfectionWindow[0] && age <= perfectionWindow[1]) {
                    transitionTo(FermentationState.PERFECT);
                }
                break;

            case PERFECT:
                if (age > perfectionWindow[1]) {
                    transitionTo(FermentationState.MATURING);
                } else if (age >= spoilStart) {
                    transitionTo(FermentationState.SPOILT);
                }
                break;

            case SPOILT:
                break;
        }

        catalystRemaining--;

    }

    private void transitionTo(FermentationState newState) {
        state = newState;
    }

    public boolean isTapped() {
        return tapped;
    }

    public FermentationState getState() {
        return state;
    }

    @Override
    protected boolean canProgress(FermentationRecipe workingRecipe) {
        return catalystRemaining > 0 && !tapped;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void read(CompoundTag pTag) {
        if (this.controller != null) {
            this.controller.deserialize(pTag.getCompound("controller"));
        }
        this.tapped = pTag.getBoolean("tapped");
        this.fermentationStartingTick = pTag.getInt("fermentation_starting_tick");
        this.catalystRemaining = pTag.getInt("catalyst_remaining");
        this.state = FermentationState.valueOf(pTag.getString("fermentation.json").toUpperCase());
        this.working = pTag.getBoolean("working");
        tag = pTag.getCompound("controller");
    }

    @Override
    protected void write(CompoundTag pTag) {
        if (this.controller != null) {
            pTag.put("controller", this.getController().serialize());
        }
        pTag.putBoolean("tapped", this.tapped);
        pTag.putInt("fermentation_starting_tick", fermentationStartingTick);
        pTag.putInt("catalyst_remaining", this.catalystRemaining);
        pTag.putString("fermentation.json", state.toString().toLowerCase());
        pTag.putBoolean("working", this.working);
    }

    @Override
    public void setRemoved() {
        SoundHandler.removeSoundAt(worldPosition);
        super.setRemoved();
    }

    public static class Controller implements ICompoundSerializer {

        private Direction formingDirection;
        private FermentationBarrelBlockEntity masterBlock;
        private List<FermentationBarrelBlockEntity> barrels = new ArrayList<>();

        private boolean formed;

        public Controller(FermentationBarrelBlockEntity b) {
            this.masterBlock = b;
            this.barrels.add(b);
        }

        public void tryFormation(FermentationBarrelBlockEntity block) {
            if (testStructure(block)) {
                this.formed = true;
                formingDirection = block.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            } else if (this.formed) {
                disassemble(block);
            }
            sync();
        }

        public void sync() {
            for (FermentationBarrelBlockEntity barrel : barrels) {
                barrel.syncBlockEntity();
            }
        }

        /*

            look in all directions from the starting block
            find three barrels in each axis. So A barrel should be neighbouring on the Y, X, Z
            If we know the barrel has 3 neighbours is safe to then infer from the positions to get the remaining barrels
            if we do find them form the multiblock

            Requirements
                - Multiblock can only be formed by a 2x2 structure
                - Must not already be part of a multiblock

         */

        private boolean testStructure(FermentationBarrelBlockEntity formingBarrel) {

            HashMap<Direction.Axis, FermentationBarrelBlockEntity> neighbours = new HashMap<>();

            for (Direction direction : Direction.values()) {
                if (formingBarrel.getLevel().getBlockEntity(formingBarrel.worldPosition.relative(direction)) instanceof FermentationBarrelBlockEntity neighbour) {
                    // check if the neighbouring barrel is already formed or neighbour is removed, if so we skip this direction
                    if ((neighbour.getController().isFormed() && neighbour.getController().getMasterBlock() != this.masterBlock) || neighbour.isRemoved()) continue;

                    // make sure to find any duplicate axis as we know the formation is wrong
                    if (neighbours.containsKey(direction.getAxis())) {
                        return false;
                    }

                    neighbours.put(direction.getAxis(), neighbour);
                }
            }

            // fail as soon as possible we know if our size is not exactly 3 we are not correct
            if (neighbours.size() != 3) return false;

            int maxX = Math.max(formingBarrel.getBlockPos().getX(), neighbours.get(Direction.Axis.X).getBlockPos().getX());
            int maxY = Math.max(formingBarrel.getBlockPos().getY(), neighbours.get(Direction.Axis.Y).getBlockPos().getY());
            int maxZ = Math.max(formingBarrel.getBlockPos().getZ(), neighbours.get(Direction.Axis.Z).getBlockPos().getZ());
            int minX = Math.min(formingBarrel.getBlockPos().getX(), neighbours.get(Direction.Axis.X).getBlockPos().getX());
            int minY = Math.min(formingBarrel.getBlockPos().getY(), neighbours.get(Direction.Axis.Y).getBlockPos().getY());
            int minZ = Math.min(formingBarrel.getBlockPos().getZ(), neighbours.get(Direction.Axis.Z).getBlockPos().getZ());

            HashSet<FermentationBarrelBlockEntity> temp = new HashSet<>();

            List<FluidStack> toEmpty = new ArrayList<>();
            FluidStack fluidInTank = masterBlock.fluidInventory.getFluidInTank();

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        // check if every place is a valid block
                        if (formingBarrel.getLevel().getBlockEntity(new BlockPos(x, y, z)) instanceof FermentationBarrelBlockEntity block) {
                            // once again check if this block is already a part of a multiblock structure or is in process of being removed from the world
                            if ((block.getController().isFormed() && block.getController().getMasterBlock() != this.masterBlock) || block.isRemoved()) return false;

                            // if our barrel connecting has a fluid we need to either merge it into the master or fail the validation if conflicts
                            FluidStack conflictingStack = block.fluidInventory.getFluidInTank();

                            if (!conflictingStack.isEmpty()) {

                                // check if master is empty
                                if (fluidInTank.isEmpty()) {
                                    // set the temp fluid stack to this
                                    fluidInTank = conflictingStack.copy();
                                } else {
                                    // master has a fluid now we need to find conflicts that could cause a structure fail
                                    // if the fluid is not equal I.E the fluid is completely different or the tags aren't the same
                                    if (!fluidInTank.isFluidEqual(conflictingStack)) {
                                        return false;
                                    }
                                    // if we get here that means the fluidstack is safe to merge
                                    fluidInTank.grow(conflictingStack.copy().getAmount());
                                }

                                toEmpty.add(conflictingStack);

                            }

                            // add to our temp
                            temp.add(block);
                        } else return false;
                    }
                }
            }

            this.masterBlock.fluidInventory.setCapacity(16000 * 8);

            for (FluidStack stack : toEmpty) {
                stack.shrink(stack.getAmount());
            }

            masterBlock.fluidInventory.fill(fluidInTank, IFluidHandler.FluidAction.EXECUTE);

            // set each entity to this controller
            for (FermentationBarrelBlockEntity entity : temp) {
                entity.setController(this);
            }

            // add our barrels for easy access
            this.barrels = new ArrayList<>(temp);

            return true;
        }

        public void disassemble(FermentationBarrelBlockEntity block) {
            this.formed = false;

            FluidStack fluid = masterBlock.fluidInventory.getFluidInTank().copy();
            if (!fluid.isEmpty()) masterBlock.fluidInventory.getFluidInTank().shrink(fluid.getAmount());

            for (FermentationBarrelBlockEntity barrel : barrels) {
                if (block == barrel) continue;
                barrel.setController(new Controller(barrel));
                barrel.fluidInventory.setCapacity(16000);
                FluidStack copied = new FluidStack(fluid.getFluid(), Math.min(fluid.getAmount(), 16000));
                if (!copied.isEmpty()) {
                    barrel.fluidInventory.fill(copied, IFluidHandler.FluidAction.EXECUTE);
                    fluid.shrink(copied.getAmount());
                }
                barrel.syncBlockEntity();
            }
        }

        public boolean isFormed() {
            return formed;
        }


        @Override
        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            ListTag tag1 = new ListTag();
            for (FermentationBarrelBlockEntity b : this.barrels) {
                tag1.add(NbtUtils.writeBlockPos(b.getBlockPos()));
            }
            tag.put("positions", tag1);
            tag.putBoolean("formed", this.formed);
            tag.put("master_pos", NbtUtils.writeBlockPos(masterBlock.getBlockPos()));
            if (formingDirection != null) {
                tag.putString("direction", formingDirection.getSerializedName());
            }
            return tag;
        }

        @Override
        public void deserialize(CompoundTag tag) {
            if (masterBlock.level != null) {
                ListTag list = tag.getList("positions", Tag.TAG_COMPOUND);
                barrels.clear();
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag tag2 = list.getCompound(i);
                    if (masterBlock.getLevel().getBlockEntity(NbtUtils.readBlockPos(tag2)) instanceof FermentationBarrelBlockEntity be) {
                        barrels.add(be);
                    }
                }

                if (masterBlock.getLevel().getBlockEntity(NbtUtils.readBlockPos(tag.getCompound("master_pos"))) instanceof FermentationBarrelBlockEntity be) {
                    this.masterBlock = be;
                }
            }

            if (tag.contains("direction")) this.formingDirection = Direction.valueOf(tag.getString("direction").toUpperCase());

            this.formed = tag.getBoolean("formed");
        }

        public FermentationBarrelBlockEntity getMasterBlock() {
            return this.masterBlock;
        }

        public List<FermentationBarrelBlockEntity> getBarrels() {
            return this.barrels;
        }

        public Direction getFormingDirection() {
            return formingDirection;
        }

        public int[] getMinMax() {

            int masterX = masterBlock.getBlockPos().getX();
            int masterY = masterBlock.getBlockPos().getY();
            int masterZ = masterBlock.getBlockPos().getZ();

            int maxX = masterX;
            int maxY = masterY;
            int maxZ = masterZ;
            int minX = masterX;
            int minY = masterY;
            int minZ = masterZ;

            for (FermentationBarrelBlockEntity barrel : getBarrels()) {
                BlockPos barrelPos = barrel.getBlockPos(); // Cache the BlockPos
                int barrelX = barrelPos.getX(); int barrelY = barrelPos.getY(); int barrelZ = barrelPos.getZ();

                maxX = Math.max(maxX, barrelX);
                maxY = Math.max(maxY, barrelY);
                maxZ = Math.max(maxZ, barrelZ);
                minX = Math.min(minX, barrelX);
                minY = Math.min(minY, barrelY);
                minZ = Math.min(minZ, barrelZ);
            }

            return new int[] {minX, minY, minZ, maxX, maxY, maxZ};
        }

    }

}
