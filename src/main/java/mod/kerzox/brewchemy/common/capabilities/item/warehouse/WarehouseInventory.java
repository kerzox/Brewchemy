package mod.kerzox.brewchemy.common.capabilities.item.warehouse;

import mod.kerzox.brewchemy.common.blockentity.WarehouseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WarehouseInventory implements IItemHandler, ICapabilitySerializable<CompoundTag> {

    public static int SINGLE_CHEST = 27;

    private WarehouseBlockEntity warehouse;
    protected LazyOptional<WarehouseInventory> handler = LazyOptional.of(() -> this);
    protected WarehouseSlot[] itemStackHandlers;

    public WarehouseInventory(WarehouseBlockEntity warehouse, int numberOfInventories) {
        this(warehouse, numberOfInventories, null);
    }

    public WarehouseInventory(WarehouseBlockEntity warehouse, int numberOfInventories, List<BlockPos> positions) {
        this.itemStackHandlers = new WarehouseSlot[numberOfInventories];
        this.warehouse = warehouse;
        for (int i = 0; i < this.itemStackHandlers.length; i++) {
            if (positions != null) this.itemStackHandlers[i] = new WarehouseSlot(this, positions.get(i));
            else this.itemStackHandlers[i] = new WarehouseSlot(this, null);
        }
    }

    public WarehouseSlot getSlotFromBlockPos(BlockPos pos) {
        for (WarehouseSlot warehouseSlot : this.itemStackHandlers) {
            if (warehouseSlot.validSlot() && warehouseSlot.validPosition(pos)) {
                return warehouseSlot;
            }
        }
        return null;
    }

    public static WarehouseInventory recreateInventoryFromTag(WarehouseBlockEntity warehouse, int slots, List<BlockPos> positions, WarehouseInventory oldInv) {
        WarehouseInventory inv = new WarehouseInventory(warehouse, slots, positions);
        inv.deserializeNBT(oldInv.serializeNBT(), true);
        List<WarehouseSlot> temp = new ArrayList<>(List.of(oldInv.getWarehouseSlots()));
        if (inv.getWarehouseSlots().length < oldInv.getWarehouseSlots().length) {
            for (WarehouseSlot oldSlot : oldInv.getWarehouseSlots()) {
                for (WarehouseSlot slot : inv.getWarehouseSlots()) {
                    if (slot.getFullWarehouseItem().getItem() == oldSlot.getFullWarehouseItem().getItem() && oldSlot.getFullWarehouseItem().getCount() == slot.getFullWarehouseItem().getCount()) {
                        temp.remove(oldSlot);
                    }
                }
            }

            for (WarehouseSlot slot : temp) {
                for (ItemStack itemStack : slot.getFullWarehouseItem().getEntireWarehouseItemAsStacks()) {
                    ItemEntity entity = new ItemEntity(warehouse.getLevel(),
                            warehouse.getBlockPos().getX(),
                            warehouse.getBlockPos().getY(),
                            warehouse.getBlockPos().getZ(),
                            itemStack);
                    warehouse.getLevel().addFreshEntity(entity);
                }

            }
        }

        return inv;
    }

    public WarehouseSlot[] getWarehouseSlots() {
        return itemStackHandlers;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < this.itemStackHandlers.length; i++) {
            list.add(this.itemStackHandlers[i].serializeNBT());
        }
        tag.put("warehouseHandlers", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.deserializeNBT(nbt, false);
    }

    public void deserializeNBT(CompoundTag nbt, boolean limitTags) {
        ListTag list = nbt.getList("warehouseHandlers", Tag.TAG_COMPOUND);
        for (int i = 0; i < this.itemStackHandlers.length; i++) {
            if (limitTags) this.itemStackHandlers[i].deserializeNBTWithoutPositions(list.getCompound(i));
            else this.itemStackHandlers[i].deserializeNBT(list.getCompound(i));
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return handler.cast();
    }

    public WarehouseItem getStackByPos(BlockPos pos) {
        for (WarehouseSlot warehouseSlot : this.itemStackHandlers) {
            if (warehouseSlot.fromPos(pos) != null) {
                return warehouseSlot.getFullWarehouseItem();
            }
        }
        return null;
    }

    public ItemStack addToFreeSlot(ItemStack stack) {
        for (WarehouseSlot stackHandler : this.itemStackHandlers) {
            if (stackHandler.addItem(stack, true).isEmpty()) {
                return stackHandler.addItem(stack, false);
            }
        }
        return stack;
    }

    @Override
    public int getSlots() {
        return itemStackHandlers.length;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return new ItemStack(this.itemStackHandlers[slot].getFullWarehouseItem().getItem(), this.itemStackHandlers[slot].getFullWarehouseItem().getCount());
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.itemStackHandlers[slot].addItem(stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.itemStackHandlers[slot].retrieveWarehouseItem(amount, simulate).getAsItemStack();
    }

    @Override
    public int getSlotLimit(int slot) {
        return itemStackHandlers[slot].getSlotLimit();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    public void onContentsChanged(WarehouseSlot slot) {
        warehouse.syncBlockEntity();
    }

}
