package mod.kerzox.brewchemy.client.baked;

import mod.kerzox.brewchemy.client.util.RenderingUtil;
import mod.kerzox.brewchemy.common.block.RopeTiedFenceBlock;
import mod.kerzox.brewchemy.common.blockentity.RopeTiedFenceBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.ItemStackTankFluidCapability;
import mod.kerzox.brewchemy.common.item.PintGlassItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PintGlassBakedModel implements IDynamicBakedModel {

    private BakedModel model;
    public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("brewchemy:pint_glass_item", "inventory");


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
    public ItemTransforms getTransforms() {
        return this.model.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides(){
            @Nullable
            @Override
            public BakedModel resolve(BakedModel pModel, ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
                ItemStackTankFluidCapability cap = (ItemStackTankFluidCapability) pStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).orElseThrow(() -> new IllegalArgumentException("Capability is empty"));
                if (!cap.getFluid().isEmpty()) return new PintGlassBakedModelWithFluid(model, cap.getFluid());
                return super.resolve(pModel, pStack, pLevel, pEntity, pSeed);
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
            if (fluidStack != null) {
                IClientFluidTypeExtensions clientStuff = IClientFluidTypeExtensions.of(fluidStack.getFluid());
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(clientStuff.getStillTexture());
                quads.add(RenderingUtil.bakeQuad(4 / 16f, 1 / 16f, 8/16f, 10 /16f, 13 /16f, 8/16f, sprite.getU(0), sprite.getV(16), sprite.getU(16), sprite.getV(0), sprite, clientStuff.getTintColor(), Direction.NORTH));
                quads.add(RenderingUtil.bakeQuad(4 / 16f, 1 / 16f, 8/16f, 10 /16f, 13 /16f, 8/16f, sprite.getU(0), sprite.getV(16), sprite.getU(16), sprite.getV(0), sprite, clientStuff.getTintColor(), Direction.SOUTH));
            }
            return quads;
        }

    }

}
