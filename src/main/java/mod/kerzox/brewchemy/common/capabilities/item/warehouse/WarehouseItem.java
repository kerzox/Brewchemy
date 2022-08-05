package mod.kerzox.brewchemy.common.capabilities.item.warehouse;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.item.Items.AIR;

public class WarehouseItem {

    private Item item;
    private int count;
    private CompoundTag tag;

    public static WarehouseItem EMPTY = new WarehouseItem(AIR, 0);

    public WarehouseItem(Item item, int count) {
        this(item, count, null);
    }

    public WarehouseItem(Item item, int count, CompoundTag tag) {
        this.item = item;
        this.count = count;
        this.tag = tag;
    }

    public static WarehouseItem ofWithSize(ItemStack itemstack, int count) {
        ItemStack copy = itemstack.copy();
        CompoundTag tag = copy.serializeNBT();
        if (tag.contains("tag")) {
            return new WarehouseItem(copy.getItem(), count, tag.getCompound("tag"));
        } else return new WarehouseItem(copy.getItem(), count, null);


//        if (tag.contains("ForgeCaps")) return new WarehouseItem(itemstack.getItem(), itemstack.getCount(), tag.getCompound("ForgeCaps"));

    }

    public static WarehouseItem of(ItemStack itemstack) {
        return ofWithSize(itemstack, itemstack.getCount());
    }

    public static WarehouseItem of(CompoundTag nbt) {
        WarehouseItem item = new WarehouseItem(AIR, 0);
        item.deserialize(nbt);
        return item;
    }

    public List<ItemStack> getEntireWarehouseItemAsStacks() {
        List<ItemStack> list = new ArrayList<>();
        while (this.count != 0) {
            list.add(getAsItemStack());
        }
        return list;
    }

    public ItemStack getItemStacksByAmount(int amount) {
        if (isEmpty()) return ItemStack.EMPTY;
        if (amount > 64) amount = 64;
        int extract = Math.min(amount, count);
        WarehouseItem copied = copy();

        if (this.count <= extract) {
            this.item = EMPTY.getItem();
            this.count = EMPTY.getCount();
        } else {
            this.count -= extract;
        }

        ItemStack stack = new ItemStack(copied.item, extract);
        stack.setTag(copied.tag);
        return stack;
    }

    public ItemStack getAsItemStack() {
        return this.getItemStacksByAmount(64);
    }

    public ItemStack getFakeItemStack() {
        ItemStack stack = new ItemStack(this.item, Math.max(64, this.count));
        stack.setTag(this.tag);
        return stack;
    }

    public void shrink(int count) {
        this.count -= count;
    }

    public void grow(int count) {
        this.count += count;
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public boolean isEmpty() {
        return this.item == AIR || count == 0;
    }

    public WarehouseItem copy() {
        return new WarehouseItem(this.item, this.count, this.tag);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.item);
        tag.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        tag.putInt("count", this.count);
        if (this.tag != null) {
            tag.put("item_tags", this.tag);
        }
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        ResourceLocation location = new ResourceLocation(tag.getString("id"));
        this.item = ForgeRegistries.ITEMS.getValue(location);
        this.count = tag.getInt("count");
        if (tag.contains("item_tags")) {
            this.tag = tag.getCompound("item_tags");
        }
    }

    public boolean isValidInsert(ItemStack other) {
        if (this.item != other.getItem()) return false;
        else if (this.tag == null && other.getTag() != null) return false;
        else return this.tag == null || this.tag.equals(other.getTag());
    }

    /*
    TODO: Add nbt tags to this class
    1. Capability tags and other item nbt!
     */

    public static boolean isValidInsert(WarehouseItem warehouseItem, ItemStack stack) {

        if (warehouseItem.isEmpty() || stack.isEmpty()) return false;

        if (warehouseItem.getItem() != stack.getItem()) {
            return false;
        }
        return true;
    }

    public static boolean isValidInsert(WarehouseItem warehouseItem, WarehouseItem other) {

        if (warehouseItem.isEmpty() || other.isEmpty()) return false;

        if (warehouseItem.getItem() != other.getItem()) {
            return false;
        }
        return true;
    }

    public boolean isSame(WarehouseSlot slot) {
        return this.item == slot.getFullWarehouseItem().getItem() && slot.getFullWarehouseItem().getCount() == this.count;
    }

}
