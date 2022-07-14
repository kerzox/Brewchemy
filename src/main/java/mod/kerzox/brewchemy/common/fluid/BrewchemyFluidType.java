package mod.kerzox.brewchemy.common.fluid;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraftforge.fluids.FluidType.Properties;

public class BrewchemyFluidType extends FluidType {

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private ResourceLocation overlayTexture;
    private ResourceLocation viewOverlayTexture;
    private final int colour;

    public BrewchemyFluidType(Properties properties,
                              ResourceLocation stillTexture,
                              ResourceLocation flowingTexture,
                              ResourceLocation overlayTexture,
                              ResourceLocation viewOverlayTexture,
                              int colour) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.viewOverlayTexture = viewOverlayTexture;
        this.colour = colour;
    }

    public BrewchemyFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int colour) {
        this(properties, stillTexture, flowingTexture, null, null, colour);
    }

    /**
     * use this if you just want a tinted water fluid ;)
     * @param tint
     * @return fluid type
     */

    public static BrewchemyFluidType createColoured(int tint) {
        return new BrewchemyFluidType(FluidType.Properties.create(),
                new ResourceLocation("block/water_still"),
                new ResourceLocation("block/water_flowing"),
                tint);
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture()
            {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return flowingTexture;
            }

            @Override
            public ResourceLocation getOverlayTexture()
            {
                return overlayTexture;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc)
            {
                return viewOverlayTexture;
            }

            @Override
            public int getTintColor()
            {
                return colour;
            }
        });
    }
}
