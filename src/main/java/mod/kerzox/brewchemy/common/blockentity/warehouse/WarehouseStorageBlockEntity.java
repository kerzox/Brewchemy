package mod.kerzox.brewchemy.common.blockentity.warehouse;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.CapabilityUtils;
import mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseSlot;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WarehouseStorageBlockEntity extends BrewchemyBlockEntity {

    private WarehouseBlockEntity warehouse;
    private BlockPos warehousePos;

    public WarehouseStorageBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.WAREHOUSE_STORAGE.get(), pWorldPosition, pBlockState);
    }

    public static void addStorageBlock(WarehouseBlockEntity warehouse, BlockPos pos) {
        warehouse.getLevel().setBlockAndUpdate(pos, BrewchemyRegistry.Blocks.WAREHOUSE_STORAGE_BLOCK.get().defaultBlockState());
        if (warehouse.getLevel().getBlockEntity(pos) instanceof WarehouseStorageBlockEntity storage) {
            storage.warehouse = warehouse;
        }
    }

    public WarehouseSlot getSlot() {
        if (warehouse != null) {
            return warehouse.getWarehouseInventory().getSlotFromBlockPos(getBlockPos());
        }
        return null;
    }

    public WarehouseBlockEntity getWarehouse() {
        if (warehouse != null) {
            if (warehousePos != null) {
                if (level.getBlockEntity(warehousePos) instanceof WarehouseBlockEntity ware) {
                    this.warehouse = ware;
                }
            }
        }
        return warehouse;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("warehouse", NbtUtils.writeBlockPos(warehouse.getBlockPos()));
    }

    @Override
    protected void read(CompoundTag pTag) {
        warehousePos = NbtUtils.readBlockPos(pTag.getCompound("warehouse"));
        if (level != null) {
            if (level.getBlockEntity(warehousePos) instanceof WarehouseBlockEntity ware) {
                this.warehouse = ware;
            }
        }
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            pPlayer.addItem(warehouse.getWarehouseInventory().extractItem(warehouse.getWarehouseInventory().getIndexFromPos(pPos), pPlayer.isShiftKeyDown() ? 64 : 1, false));
            syncBlockEntity();
            return true;

        }
        return false;
    }

    @Override
    public void onLoad() {
        if (warehousePos != null) {
            if (level.getBlockEntity(warehousePos) instanceof WarehouseBlockEntity ware) {
                this.warehouse = ware;
            }
        }
    }
}
