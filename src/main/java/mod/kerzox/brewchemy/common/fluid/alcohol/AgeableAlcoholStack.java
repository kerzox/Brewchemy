package mod.kerzox.brewchemy.common.fluid.alcohol;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class AgeableAlcoholStack extends FluidStack {

    public AgeableAlcoholStack(Fluid fluid, int amount, int age) {
        super(fluid, amount);
        setAge(age);
    }

    public AgeableAlcoholStack(Fluid fluid, int amount) {
        this(fluid, amount, 0);
    }

    public AgeableAlcoholStack(FluidStack stack) {
        this(stack.getFluid(), stack.getAmount(), getAge(stack));
    }

    public void setAge(int age) {
        this.getOrCreateTag().putInt("age", age);
    }

    public void ageAlcohol(int amount) {
        setAge(getAge() + amount);
    }

    public AlcoholicFluid getAsType() {
        if (this.getFluid().getFluidType() instanceof AlcoholicFluid fluid) return fluid;
        else return null;
    }

    public int getAge() {
        if (this.getTag() != null && this.getTag().contains("age")) {
            return this.getTag().getInt("age");
        }
        return 0;
    }

    public static int getAge(FluidStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("age")) {
            return stack.getTag().getInt("age");
        }
        return 0;
    }
}
