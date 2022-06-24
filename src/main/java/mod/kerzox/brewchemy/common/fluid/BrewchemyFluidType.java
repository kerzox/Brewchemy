package mod.kerzox.brewchemy.common.fluid;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.function.Consumer;

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

    @Override
    public void initializeClient(Consumer<IFluidTypeRenderProperties> consumer) {
        consumer.accept(new IFluidTypeRenderProperties() {
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
            public int getColorTint()
            {
                return colour;
            }
        });
    }
}
