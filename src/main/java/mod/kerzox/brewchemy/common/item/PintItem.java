package mod.kerzox.brewchemy.common.item;

import mod.kerzox.brewchemy.common.blockentity.PintGlassBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.RopeTiedPostBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.drunk.IntoxicationManager;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidInventoryItem;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackHandlerUtils;
import mod.kerzox.brewchemy.common.event.TickUtils;
import mod.kerzox.brewchemy.common.fluid.alcohol.AgeableAlcoholStack;
import mod.kerzox.brewchemy.common.fluid.alcohol.AlcoholicFluid;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PintItem extends BlockItem {

    public static final int MAX_SIZE = 500;
    public static final int DRINKING_RATE = 5;

    public PintItem(Properties p_40566_) {
        super(BrewchemyRegistry.Blocks.PINT_GLASS_BLOCK.get(), p_40566_);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
//        ItemStack stack = pContext.getItemInHand();
//        if (!pContext.getLevel().isClientSide) {
//            if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof PintGlassBlockEntity pint) {
//                IItemHandler handler = pint.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
//                if (handler instanceof ItemInventory inventory) {
//
//                    Direction facing = pint.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
//                    Direction clickedFace = pContext.getClickedFace();
//
//                    if (facing == Direction.NORTH) {
//
//                        if (clickedFace == Direction.SOUTH) {
//                            ItemStack stack1 = inventory.getStackInSlot(0).copy();
//                            if (inventory.getStackInSlot(1).isEmpty())
//                            {
//                                inventory.setStackInSlot(1, stack1);
//                                inventory.setStackInSlot(0, stack);
//                            }
//                        } else {
//                            ItemStackHandlerUtils.insertAndModifyStack(inventory.getInputHandler(), stack);
//                        }
//
//                    }
//
//                    if (facing == Direction.SOUTH) {
//
//                        if (clickedFace == Direction.SOUTH) {
//                            ItemStackHandlerUtils.insertAndModifyStack(inventory.getInputHandler(), stack);
//                        } else {
//                            ItemStack stack1 = inventory.getStackInSlot(0).copy();
//                            if (inventory.getStackInSlot(1).isEmpty())
//                            {
//                                inventory.setStackInSlot(1, stack1);
//                                inventory.setStackInSlot(0, stack);
//                            }
//
//                        }
//
//                    }
//
//                  //  ItemStackHandlerUtils.insertAndModifyStack(inventory.getInputHandler(), stack);
//                    pint.syncBlockEntity();
//                    return InteractionResult.SUCCESS;
//                }
//            }
//        }
        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(cap -> {
            if (cap instanceof FluidInventoryItem handler) {
                if (!handler.getFluid().isEmpty()) {
                    pTooltipComponents.add(Component.literal("Glass of ").append(Component.translatable(handler.getFluid().getTranslationKey())));
                    if (handler.getFluid().getFluid().getFluidType() instanceof AlcoholicFluid fluid) {
                        AgeableAlcoholStack age = new AgeableAlcoholStack(handler.getFluid());
                        pTooltipComponents.add(Component.literal("Fermentation State: " + age.getState()));
                    }


                }
            }
        });
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        ItemStack stack = pContext.getItemInHand().copy();
        InteractionResult result = super.place(pContext);
        if (!pContext.getLevel().isClientSide && result.consumesAction()) {
            if (!pContext.getLevel().isClientSide) {
                if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof PintGlassBlockEntity pint) {
                    IItemHandler handler = pint.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
                    if (handler instanceof ItemInventory inventory) {
                        ItemStackHandlerUtils.insertAndModifyStack(inventory.getInputHandler(), stack);
                        return result;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.DRINK;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext p_40611_, BlockState p_40612_) {
        return super.canPlace(p_40611_, p_40612_);
    }

    @Override
    protected boolean mustSurvive() {
        return super.mustSurvive();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        if (!p_41432_.isClientSide) {
            ItemStack stack = p_41433_.getMainHandItem();
            stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
                if (!handler.getFluidInTank(0).isEmpty()) p_41433_.startUsingItem(p_41434_);
            });
        }
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack p_41409_, Level p_41410_, LivingEntity p_41411_) {
        return super.finishUsingItem(p_41409_, p_41410_, p_41411_);
    }

    @Override
    public int getUseDuration(ItemStack p_41454_) {
        return MAX_SIZE / DRINKING_RATE;
    }

    @Override
    public boolean isBarVisible(ItemStack p_150899_) {
        Optional<IFluidHandlerItem> storage = p_150899_.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        if (storage.get() instanceof FluidInventoryItem inventoryItem) {
            if (inventoryItem.getFluid().isEmpty() || inventoryItem.getFluid().getAmount() == inventoryItem.getTankCapacity(0)) return false;
            else return true;
        }
        return false;
    }

    @Override
    public int getBarWidth(ItemStack p_150900_) {
        Optional<IFluidHandlerItem> storage = p_150900_.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
        if (storage.get() instanceof FluidInventoryItem inventoryItem) {
            return Math.round(((float) (13 * inventoryItem.getFluid().getAmount()) / inventoryItem.getTankCapacity(0)));
        }
        return 0;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidInventoryItem(stack, 500);
    }

    private static ItemStack pintOf(Fluid fluid) {
        ItemStack temp = new ItemStack(BrewchemyRegistry.Items.PINT_ITEM.get());
        IFluidHandlerItem handler = temp.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (handler instanceof FluidInventoryItem item) {
            item.setFluid(new FluidStack(fluid, 500));
        }
        return temp;
    }

    private static ItemStack alcoholicPint(Fluid fluid, int amount) {
        ItemStack temp = new ItemStack(BrewchemyRegistry.Items.PINT_ITEM.get());
        IFluidHandlerItem handler = temp.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (handler instanceof FluidInventoryItem item && fluid.getFluidType() instanceof AlcoholicFluid fluid1) {

            AgeableAlcoholStack stack = new AgeableAlcoholStack(new FluidStack(fluid, 500));

            stack.ageAlcohol(amount == 0 ? fluid1.getPerfectionRange()[0] : amount);

            item.setFluid(stack.getFluidStack());
        }
        return temp;
    }

    public static List<ItemStack> makeDrinks() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(pintOf(Fluids.WATER));
        stacks.add(alcoholicPint(BrewchemyRegistry.Fluids.BEER_ALE.getFluid().get(), 0));
        stacks.add(alcoholicPint(BrewchemyRegistry.Fluids.BEER_LAGER.getFluid().get(), 0));
        stacks.add(alcoholicPint(BrewchemyRegistry.Fluids.BEER_STOUT.getFluid().get(), 0));
        stacks.add(alcoholicPint(BrewchemyRegistry.Fluids.BEER_PALE_ALE.getFluid().get(), 0));
        stacks.add(alcoholicPint(BrewchemyRegistry.Fluids.BEER_ALE.getFluid().get(), TickUtils.minecraftDaysToTicks(5)));
        stacks.add(pintOf(Fluids.EMPTY));
        return stacks;
    }
}
