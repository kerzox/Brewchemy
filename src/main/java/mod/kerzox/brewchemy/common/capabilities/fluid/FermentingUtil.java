package mod.kerzox.brewchemy.common.capabilities.fluid;

import net.minecraft.util.StringRepresentable;

public class FermentingUtil {

     enum FermentationStage implements StringRepresentable {
        STAGE_ZERO("fermentation_zero"),
        STAGE_ONE("fermentation_one"),
        STAGE_TWO("fermentation_two"),
        STAGE_THREE("fermentation_three"),
        ;

        private String name;

        FermentationStage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static boolean isMature(FermentationStage stage) {
            return stage == STAGE_THREE;
        }

        @Override
        public String getSerializedName() {
            return getName().toLowerCase();
        }
    }

}
