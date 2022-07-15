package mod.kerzox.brewchemy.client.baked;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.block.RopeTiedFenceBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeConnections;
import mod.kerzox.brewchemy.common.blockentity.RopeBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.RopeTiedFenceBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static mod.kerzox.brewchemy.common.block.rope.RopeBlock.SIDES;

public class RopeTiedFenceBakedModel implements IDynamicBakedModel {

    private final Map<String, List<BakedQuad>> quadCache = new HashMap<>();
    private BakedModel model;
    private MultiPartBakedModel multiPart;

    public RopeTiedFenceBakedModel(BakedModel bakedModel) {
        this.model = bakedModel;
        if (bakedModel instanceof MultiPartBakedModel multi) {
            this.multiPart = multi;
        }
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>(this.multiPart.getQuads(state, side, rand));
        quads.addAll(new ArrayList<>(this.getCamoModelFromData(extraData).getQuads(state, side, rand)));
        return quads;
    }


    private BakedModel getCamoModelFromData(ModelData data) {
        BakedModel model = this.model;
        BlockState mimic = data.get(RopeTiedFenceBlockEntity.MIMIC);
        if (mimic != null && !(mimic.getBlock() instanceof RopeTiedFenceBlock)) {
            model = Minecraft.getInstance().getBlockRenderer().getBlockModel(mimic);
        }
        return model;
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
        return model.getOverrides();
    }


}
