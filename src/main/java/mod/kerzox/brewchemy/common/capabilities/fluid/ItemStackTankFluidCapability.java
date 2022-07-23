package mod.kerzox.brewchemy.common.capabilities.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackTankFluidCapability extends FluidHandlerItemStack {

    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public ItemStackTankFluidCapability(@NotNull ItemStack container, int capacity) {
        super(container, capacity);
    }

//
//    public void write(CompoundTag tag) {
//        CompoundTag tag1 = new CompoundTag();
//        this.getFluid().writeToNBT(tag1);
//        tag.put("fluidHandler", tag1);
//    }
//
//    public void read(CompoundTag tag) {
//        if (tag.contains("fluidHandler")) {
//            CompoundTag tag1 = tag.getCompound("fluidHandler");
//            this.setFluid(FluidStack.loadFluidStackFromNBT(tag1));
//        }
//    }

}
