package mod.kerzox.brewchemy.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.fluids.FluidStack;

public class FermentationHelper {

    public enum Stages implements StringRepresentable {
        YOUNG("stage_one", 0),
        MATURE("stage_three", 200);

        private String name;
        private int time;

        Stages(String name, int time) {
            this.name = name;
            this.time = time;
        }

        public static Stages getNextStage(int time) {
            if (time >= MATURE.time) {
                return MATURE;
            }
            System.out.println(time);
            return YOUNG;
        }

        @Override
        public String getSerializedName() {
            return this.name.toLowerCase();
        }
    }

    public static CompoundTag getFermentationTag(FluidStack stack) {
        return stack.getOrCreateChildTag("fermentation");
    }

    public static Stages getFermentationStage(FluidStack stack) {
        return Stages.getNextStage(getFermentationTag(stack).getInt("time"));
    }

    public static void ageFluidStack(FluidStack stack, int time) {
        getFermentationTag(stack).putInt("time", time);
        getFermentationTag(stack).putString("age", Stages.getNextStage(time).getSerializedName());
    }
}
