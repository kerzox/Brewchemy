package mod.kerzox.brewchemy.common.blockentity.warehouse;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseInventory;
import mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseItem;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseInventory.SINGLE_CHEST;

public class WarehouseBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    public enum Type implements StringRepresentable {
        INVISIBLE("type_0"),
        VISIBLE("type_1");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name.toLowerCase();
        }
    }

    private Type currentType = Type.VISIBLE;
    private List<BlockPos> positions = new ArrayList<>();
    private BlockPos position1 = new BlockPos(0, 0, 0);
    private BlockPos position2 = new BlockPos(5, 5, 5);
    private boolean refresh = false;
    private WarehouseInventory warehouseInventory = new WarehouseInventory(this, 1);


    public WarehouseBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.WAREHOUSE.get(), pWorldPosition, pBlockState);
    }

    public WarehouseBlockEntity.Type getWarehouseType() {
        return currentType;
    }

    @Override
    public void onServer() {
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (stack.isEmpty()) {
            if (!level.isClientSide) pPlayer.sendSystemMessage(Component.literal("You have no items to store"));
            return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
        }

        // attempt item transfer

        if (!level.isClientSide) {
            if (warehouseInventory.hasValidSlot(stack)) {
                ItemStack ret = warehouseInventory.addToFreeSlot(stack);
                pPlayer.setItemInHand(pHand, ret);
            }
        }
        return true;
    }

    public int getStorageSlots() {
        return positions.size();
    }
    
    public void
    calculatePositions() {
        Direction facing = getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite();
        for (BlockPos position : positions) {
            level.setBlockAndUpdate(position, Blocks.AIR.defaultBlockState());
        }
        positions.clear();
        for (int x = position1.getX(); x < position2.getX(); x++) {
            for (int y = position1.getY(); y < position2.getY(); y++) {
                for (int z = position1.getZ(); z < position2.getZ(); z++) {
                    BlockPos relativePos = getBlockPos().relative(facing).offset(x, y, z);
                    if (facing == Direction.SOUTH) {
                        relativePos = relativePos.offset(-position2.getX() + 1, 0, 0);
                    } else if (facing == Direction.NORTH) {
                        relativePos = relativePos.offset(0, 0, -position2.getZ() + 1);
                    } else if (facing == Direction.WEST) {
                        relativePos = relativePos.offset(-position2.getX() + 1, 0, -position2.getZ() + 1);
                    }
                    positions.add(relativePos);
                }
            }
        }
        this.warehouseInventory = WarehouseInventory.recreateInventoryFromTag(this, positions.size(), positions, this.warehouseInventory);
        for (BlockPos position : positions) {
              WarehouseStorageBlockEntity.addStorageBlock(this, position);
        }
        syncBlockEntity();
    }

    public void moveX(int x) {
        this.position2 = new BlockPos(this.position2.getX() + x, this.position2.getY(), this.position2.getZ());
        calculatePositions();
    }

    public void moveY(int y) {
        this.position2 = new BlockPos(this.position2.getX(), this.position2.getY() + y, this.position2.getZ());
        calculatePositions();
    }

    public void moveZ(int z) {
        this.position2 = new BlockPos(this.position2.getX(), this.position2.getY(), this.position2.getZ() + z);
        calculatePositions();
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
        syncBlockEntity();
    }

    public boolean refresh() {
        return refresh;
    }

    public BlockPos getPosition1() {
        return position1;
    }

    public BlockPos getPosition2() {
        return position2;
    }

    public List<BlockPos> getCachedPositions() {
        return positions;
    }

    private void savePositions(CompoundTag tag) {
        ListTag list = new ListTag();
        for (BlockPos position : positions) {
            list.add(NbtUtils.writeBlockPos(position));
        }
        tag.put("positions", list);
    }

    private void readPositionsFromTag(CompoundTag tag) {
        if (tag.contains("positions")) {
            positions.clear();
            ListTag list = tag.getList("positions", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                positions.add(NbtUtils.readBlockPos(list.getCompound(i)));
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return warehouseInventory.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }

    public WarehouseInventory getWarehouseInventory() {
        return warehouseInventory;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        calculatePositions();
    }

    @Override
    protected void write(CompoundTag pTag) {
        CompoundTag tag1 = new CompoundTag();
        tag1.put("pos1", NbtUtils.writeBlockPos(position1));
        tag1.put("pos2", NbtUtils.writeBlockPos(position2));
        pTag.put("opposing", tag1);
        savePositions(pTag);
        pTag.putString("warehouse_type", this.getWarehouseType().getSerializedName());
        pTag.put("itemHandler", this.warehouseInventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag pTag) {
        readPositionsFromTag(pTag);
        if (pTag.contains("opposing")) {
            CompoundTag tag = pTag.getCompound("opposing");
            this.position1 = NbtUtils.readBlockPos(tag.getCompound("pos1"));
            this.position2 = NbtUtils.readBlockPos(tag.getCompound("pos2"));
        }
        if (!positions.isEmpty()) warehouseInventory = new WarehouseInventory(this, getStorageSlots());
        this.currentType = Type.valueOf("INVISIBLE");
        this.warehouseInventory.deserializeNBT(pTag.getCompound("itemHandler"));
    }

    @Override
    public void addToUpdateTag(CompoundTag tag) {
        super.addToUpdateTag(tag);
    }
}
