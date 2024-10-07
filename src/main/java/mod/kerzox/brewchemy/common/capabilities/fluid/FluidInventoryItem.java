package mod.kerzox.brewchemy.common.capabilities.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

public class FluidInventoryItem extends FluidHandlerItemStack {

    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public FluidInventoryItem(@NotNull ItemStack container, int capacity) {
        super(container, capacity);
    }

    public void setFluid(FluidStack fluid)
    {
        if (!container.hasTag())
        {
            container.setTag(new CompoundTag());
        }

        CompoundTag fluidTag = new CompoundTag();
        fluid.writeToNBT(fluidTag);
        container.getTag().put(FLUID_NBT_KEY, fluidTag);
    }
}
