package mod.kerzox.brewchemy.common.fluid.alcohol;

import mod.kerzox.brewchemy.common.fluid.BrewchemyFluid;
import net.minecraft.resources.ResourceLocation;

public class AlcoholicFluid extends BrewchemyFluid {

    private final int[] perfectionRange;
    private final int matureTick;
    private final int spoilTick;

    public AlcoholicFluid(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, ResourceLocation overlayTexture, ResourceLocation viewOverlayTexture, int colour, int[] perfectionRange, int matureTick, int spoilTick) {
        super(properties, stillTexture, flowingTexture, overlayTexture, viewOverlayTexture, colour);
        this.perfectionRange = perfectionRange;
        this.matureTick = matureTick;
        this.spoilTick = spoilTick;
    }

    public AlcoholicFluid(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int colour, int[] perfectionRange, int matureTick, int spoilTick) {
        super(properties, stillTexture, flowingTexture, colour, false);
        this.perfectionRange = perfectionRange;
        this.matureTick = matureTick;
        this.spoilTick = spoilTick;
    }

    public static AlcoholicFluid create(int tint, int[] perfectionRange, int mature, int spoil) {
        return new AlcoholicFluid(Properties.create(),
                new ResourceLocation("block/water_still"),
                new ResourceLocation("block/water_flowing"),
                tint, perfectionRange, mature, spoil);
    }

    public int[] getPerfectionRange() {
        return perfectionRange;
    }

    public int getMatureTick() {
        return matureTick;
    }

    public int getSpoilTick() {
        return spoilTick;
    }
}
