package mod.kerzox.brewchemy.common.fluid;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;
import java.util.function.Supplier;

/*
  straight copied over from my exotek mod
 */

public class BrewchemyFluid extends FluidType {

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private ResourceLocation overlayTexture;
    private ResourceLocation viewOverlayTexture;
    private boolean gas;
    private final int colour;

    public BrewchemyFluid(Properties properties,
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

    public BrewchemyFluid(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int colour, boolean gas) {
        this(properties, stillTexture, flowingTexture, null, null, colour);
        this.gas = gas;
    }


    public static BrewchemyFluid createColoured(Properties properties, int tint, boolean gas) {
        return new BrewchemyFluid(properties,
                new ResourceLocation("block/water_still"),
                new ResourceLocation("block/water_flowing"),
                tint, gas);
    }

    public static BrewchemyFluid createColoured(int tint, boolean gas) {
        return new BrewchemyFluid(Properties.create(),
                new ResourceLocation("block/water_still"),
                new ResourceLocation("block/water_flowing"),
                tint, gas);
    }


    public boolean isGaseous() {
        return gas;
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

            public boolean isGaseous() {
                return gas;
            }
        });
    }

    public static class Block extends LiquidBlock {

        public Block(FlowingFluid p_54694_, Properties p_54695_) {
            super(p_54694_, p_54695_);
        }

        public Block(Supplier<? extends FlowingFluid> p_54694_, Properties p_54695_) {
            super(p_54694_, p_54695_);
        }


        @Override
        public void onPlace(BlockState p_54754_, Level p_54755_, BlockPos p_54756_, BlockState p_54757_, boolean p_54758_) {

        }

    }


}
