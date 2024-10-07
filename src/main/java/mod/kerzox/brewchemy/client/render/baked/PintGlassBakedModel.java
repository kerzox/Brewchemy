package mod.kerzox.brewchemy.client.render.baked;

import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.common.block.RopeTiedPostBlock;
import mod.kerzox.brewchemy.common.blockentity.RopeTiedPostBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidInventoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PintGlassBakedModel implements IDynamicBakedModel {

    public static ModelResourceLocation modelResourceLocation = new ModelResourceLocation(new ResourceLocation("brewchemy", "pint_item"),"inventory");
    protected BakedModel model;

    public PintGlassBakedModel(BakedModel bakedModel) {
        this.model = bakedModel;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        return model.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return model.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides(){
            @Nullable
            @Override
            public BakedModel resolve(BakedModel pModel, ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
                FluidInventoryItem cap = (FluidInventoryItem) pStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElseThrow(() -> new IllegalArgumentException("Capability is empty"));
                if (!cap.getFluid().isEmpty()) return new PintGlassBakedModelWithFluid(model, cap.getFluid());
                return model;
            }
        };
    }

    public static class PintGlassBakedModelWithFluid extends PintGlassBakedModel {

        protected FluidStack fluidStack;

        public PintGlassBakedModelWithFluid(BakedModel bakedModel, FluidStack fluidStack) {
            super(bakedModel);
            this.fluidStack = fluidStack;
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
            List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand, extraData, renderType));
            quads.addAll(new ArrayList<>(getQuadsFromFluid()));
            return quads;
        }

        private List<BakedQuad> getQuadsFromFluid() {
            List<BakedQuad> quads = new ArrayList<>();
            if (fluidStack != null && fluidStack.getFluid() != null) {
                IClientFluidTypeExtensions clientStuff = IClientFluidTypeExtensions.of(fluidStack.getFluid());
                if (clientStuff != null) {
                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(clientStuff.getStillTexture());
                    quads.addAll(Objects.requireNonNull(RenderingUtil.bakedQuadList(
                            5.25f / 16f,
                            0.25f / 16f,
                            5.25f / 16f,
                            (5.25f + 5.5f) / 16f,
                            8 / 16f,
                            (5.25f + 5.5f) / 16f,
                            sprite.getU(0), sprite.getV(16), sprite.getU(16), sprite.getV(0), sprite, clientStuff.getTintColor())));

                }
            }
            return quads;
        }

        @Override
        public ItemTransforms getTransforms() {
            return this.model.getTransforms();
        }
    }

}