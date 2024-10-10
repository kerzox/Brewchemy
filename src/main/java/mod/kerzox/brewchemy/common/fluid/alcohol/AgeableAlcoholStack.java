package mod.kerzox.brewchemy.common.fluid.alcohol;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A wrapper over the fluidstack just to simplify tag manipulation
 */

public class AgeableAlcoholStack {

    private FluidStack stack;

    public AgeableAlcoholStack(FluidStack stack) {
        if (stack.getFluid().getFluidType() instanceof AlcoholicFluid) {
            this.stack = stack;
        } else throw new IllegalArgumentException("Fluidstack must be fluid type that is Alcoholic Fluid");
    }

    public void setAge(int age) {
        stack.getOrCreateTag().putInt("age", age);
    }

    public void ageAlcohol(int amount) {
        setAge(getAge() + amount);
    }

    public AlcoholicFluid getAsType() {
        if (stack.getFluid().getFluidType() instanceof AlcoholicFluid fluid) return fluid;
        else return null;
    }

    public int getAge() {
        if (!stack.isEmpty() && stack.getTag() != null && stack.getTag().contains("age")) {
            return stack.getTag().getInt("age");
        }
        return 0;
    }

    public int[] getPerfectionRange() {
        if (!stack.isEmpty()) {
            return getAsType().getPerfectionRange();
        }
        return new int[2];
    }

    public int getMaturationStart() {
        if (!stack.isEmpty()) {
            return getAsType().getMatureTick();
        }
        return 0;
    }

    public int getSpoiledStart() {
        if (!stack.isEmpty()) {
            return getAsType().getSpoilTick();
        }
        return 0;
    }

    public boolean overFermented() {
        return !stack.isEmpty() && getAge() >= getSpoiledStart();
    }

    public boolean inPerfectionRange() {
        return !stack.isEmpty() && getAge() <= getPerfectionRange()[1] && getAge() >= getPerfectionRange()[0];
    }

    public static int getAge(FluidStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("age")) {
            return stack.getTag().getInt("age");
        }
        return 0;
    }

    public int getAmount() {
        return this.stack.getAmount();
    }

    public FluidStack getFluidStack() {
        return stack;
    }

    public String getState() {
        int age = getAge();
        if (inPerfectionRange()) return "Perfect";
        return age > 0 ? "Aged" : "Young";
    }
}
