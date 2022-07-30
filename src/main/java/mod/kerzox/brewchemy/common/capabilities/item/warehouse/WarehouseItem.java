package mod.kerzox.brewchemy.common.capabilities.item.warehouse;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.item.Items.AIR;

public class WarehouseItem {

    private Item item;
    private int count;

    public static WarehouseItem EMPTY = new WarehouseItem(AIR, 0);

    public WarehouseItem(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public static WarehouseItem of(CompoundTag nbt) {
        WarehouseItem item = new WarehouseItem(AIR, 0);
        item.deSerialize(nbt);
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

        return new ItemStack(copied.item, extract);
    }

    public ItemStack getAsItemStack() {
        return this.getItemStacksByAmount(64);
    }

    public ItemStack getFakeItemStack() {
        return new ItemStack(this.item, Math.min(64, count));
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

    public boolean isEmpty() {
        return this.item == AIR || count == 0;
    }

    public WarehouseItem copy() {
        return new WarehouseItem(this.item, this.count);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.item);
        tag.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        tag.putInt("count", this.count);
        return tag;
    }

    public void deSerialize(CompoundTag tag) {
        ResourceLocation location = new ResourceLocation(tag.getString("id"));
        this.item = ForgeRegistries.ITEMS.getValue(location);
        this.count = tag.getInt("count");
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
