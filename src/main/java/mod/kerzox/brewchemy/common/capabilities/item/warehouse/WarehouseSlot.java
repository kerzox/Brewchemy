package mod.kerzox.brewchemy.common.capabilities.item.warehouse;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

import static mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseInventory.SINGLE_CHEST;

public class WarehouseSlot implements INBTSerializable<CompoundTag> {

    private WarehouseInventory owner;
    private BlockPos pos;
    private WarehouseItem stored;

    public WarehouseSlot(WarehouseInventory owner, BlockPos pos) {
        this.owner = owner;
        this.pos = pos;
        this.stored = WarehouseItem.EMPTY;
    }

    public WarehouseItem addItem(WarehouseItem stack, boolean simulation) {
        if (stack.isEmpty()) return WarehouseItem.EMPTY;

        int limit = getSlotLimit();

        if (!stored.isEmpty()) {
            if (!WarehouseItem.isValidInsert(stored, stack)) {
                return stack;
            }
            limit -= stored.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulation) {
            if (!stored.isEmpty()) {
                if (WarehouseItem.isValidInsert(stored, stack)) {
                    this.stored.grow(reachedLimit ? limit : stack.getCount());
                }
            } else {
                this.stored = new WarehouseItem(stack.getItem(), reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(this);
        }
        return reachedLimit ? new WarehouseItem(stack.getItem(), stack.getCount() - limit) : WarehouseItem.EMPTY;
    }

    public ItemStack addItem(ItemStack stack, boolean simulation) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        int limit = getSlotLimit();

        if (!stored.isEmpty()) {
            if (!WarehouseItem.isValidInsert(stored, stack)) {
                return stack;
            }
            limit -= stored.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulation) {
            if (!stored.isEmpty()) {
                if (WarehouseItem.isValidInsert(stored, stack)) {
                    this.stored.grow(reachedLimit ? limit : stack.getCount());
                }
            } else {
                this.stored = new WarehouseItem(stack.getItem(), reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(this);
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    public ItemStack getSimulationStack() {
        return getFullWarehouseItem().getFakeItemStack();
    }

    public ItemStack getItemStack() {
        return this.getItemStack(64);
    }

    public ItemStack getItemStack(int amount) {
        return getFullWarehouseItem().getItemStacksByAmount(amount);
    }

    public WarehouseItem getFullWarehouseItem() {
        return stored;
    }

    public WarehouseItem retrieveWarehouseItem(int amount, boolean simulation) {
        int toExtract = Math.min(amount, stored.getCount());
        WarehouseItem copied = stored.copy();

        if (stored.getCount() <= toExtract) {
            if (!simulation) {
                this.stored = WarehouseItem.EMPTY;
                onContentsChanged(this);
                return copied;
            } else {
                return copied;
            }
        } else {
            if (!simulation) {
                this.stored = new WarehouseItem(copied.getItem(), copied.getCount() - toExtract);
                onContentsChanged(this);
            }
            return new WarehouseItem(copied.getItem(), toExtract);
        }
    }

    public int getSlotLimit() {
        return SINGLE_CHEST * 64;
    }

    public boolean validPosition(BlockPos pos) {
        return this.pos.equals(pos);
    }

    public boolean validSlot() {
        return pos != null;
    }

    public BlockPos getPos() {
        return pos;
    }

    protected void onContentsChanged(WarehouseSlot slot) {
        owner.onContentsChanged(slot);
    }

    public boolean isEmpty() {
        return this.stored.isEmpty();
    }

    public WarehouseSlot fromPos(BlockPos pos) {
        return validPosition(pos) ? this : null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("warehouse_item", this.stored.serialize());
        if (validSlot()) tag.put("position", NbtUtils.writeBlockPos(pos));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.stored = WarehouseItem.of(nbt.getCompound("warehouse_item"));
        this.pos = NbtUtils.readBlockPos(nbt.getCompound("position"));
    }

    public void deserializeNBTWithoutPositions(CompoundTag nbt) {
        this.stored = WarehouseItem.of(nbt.getCompound("warehouse_item"));
    }
}
