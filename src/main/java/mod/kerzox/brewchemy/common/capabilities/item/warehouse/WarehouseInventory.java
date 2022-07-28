package mod.kerzox.brewchemy.common.capabilities.item.warehouse;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarehouseInventory implements ICapabilitySerializable<CompoundTag> {

    public static int SINGLE_CHEST = 27;

    protected LazyOptional<WarehouseInventory> handler = LazyOptional.of(() -> this);
    protected ItemStackHandler[] itemStackHandlers;

    public WarehouseInventory(int numberOfInventories) {
        this.itemStackHandlers = new ItemStackHandler[numberOfInventories];
        for (int i = 0; i < this.itemStackHandlers.length; i++) {
            this.itemStackHandlers[i] = new ItemStackHandler(1) {
                @Override
                public int getSlotLimit(int slot) {
                    return SINGLE_CHEST & 64;
                }
            };
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < this.itemStackHandlers.length; i++) {
            list.add(this.itemStackHandlers[i].serializeNBT());
        }
        tag.put("warehouseHandlers", this.serializeNBT());
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag list = nbt.getList("warehouseHandlers", Tag.TAG_COMPOUND);
        for (int i = 0; i < this.itemStackHandlers.length; i++) {
            this.itemStackHandlers[i].deserializeNBT(list.getCompound(i));
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return handler.cast();
    }
}
