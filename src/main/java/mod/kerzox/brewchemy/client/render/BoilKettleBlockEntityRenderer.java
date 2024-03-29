package mod.kerzox.brewchemy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import mod.kerzox.brewchemy.client.util.RenderingUtil;
import mod.kerzox.brewchemy.common.blockentity.BoilKettleBlockEntity;
import mod.kerzox.brewchemy.common.crafting.recipes.BrewingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import static mod.kerzox.brewchemy.client.util.RenderingUtil.addVertex;

public class BoilKettleBlockEntityRenderer implements BlockEntityRenderer<BoilKettleBlockEntity> {

    public BoilKettleBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(BoilKettleBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        VertexConsumer quadVertex = pBufferSource.getBuffer(RenderType.solid());


        FluidStack inFluid = pBlockEntity.getSidedFluidTank().getInputHandler().getFluidInTank(0);
        FluidStack outFluid = pBlockEntity.getSidedFluidTank().getOutputHandler().getFluidInTank(0);
        if (!inFluid.isEmpty()) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(inFluid.getFluid()).getStillTexture());
            pPoseStack.pushPose();

            float percentage = ( (float) pBlockEntity.getSidedFluidTank().getInputHandler().getFluidInTank(0).getAmount() / (float) pBlockEntity.getSidedFluidTank().getInputHandler().getTankCapacity(0)) * 22 / 16f;
            if (percentage <= 0.15) {
                percentage = 0.15f;
            }
            addVertex(quadVertex, pPoseStack, 0.1f, percentage, .9f, sprite.getU(0), sprite.getV(16), IClientFluidTypeExtensions.of(inFluid.getFluid()).getTintColor());
            addVertex(quadVertex, pPoseStack, .9f, percentage, .9f, sprite.getU(16), sprite.getV(16), IClientFluidTypeExtensions.of(inFluid.getFluid()).getTintColor());
            addVertex(quadVertex, pPoseStack, .9f, percentage, 0.1f, sprite.getU(16), sprite.getV(0), IClientFluidTypeExtensions.of(inFluid.getFluid()).getTintColor());
            addVertex(quadVertex, pPoseStack, 0.1f, percentage, 0.1f, sprite.getU(0), sprite.getV(0), IClientFluidTypeExtensions.of(inFluid.getFluid()).getTintColor());
            pPoseStack.popPose();
        }
        if (!outFluid.isEmpty()) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(outFluid.getFluid()).getStillTexture());
            pPoseStack.pushPose();

            float percentage = ( (float) pBlockEntity.getSidedFluidTank().getOutputHandler().getFluidInTank(0).getAmount() / (float) pBlockEntity.getSidedFluidTank().getOutputHandler().getTankCapacity(0)) * 22 / 16f;
            if (percentage <= 0.15) {
                percentage = 0.15f;
            }
            addVertex(quadVertex, pPoseStack, 0.1f, percentage, .9f, sprite.getU(0), sprite.getV(16), IClientFluidTypeExtensions.of(outFluid.getFluid()).getTintColor());
            addVertex(quadVertex, pPoseStack, .9f, percentage, .9f, sprite.getU(16), sprite.getV(16), IClientFluidTypeExtensions.of(outFluid.getFluid()).getTintColor());
            addVertex(quadVertex, pPoseStack, .9f, percentage, 0.1f, sprite.getU(16), sprite.getV(0), IClientFluidTypeExtensions.of(outFluid.getFluid()).getTintColor());
            addVertex(quadVertex, pPoseStack, 0.1f, percentage, 0.1f, sprite.getU(0), sprite.getV(0), IClientFluidTypeExtensions.of(outFluid.getFluid()).getTintColor());
            pPoseStack.popPose();
        }


        VertexConsumer lineVertex = pBufferSource.getBuffer(RenderType.lines());

        pPoseStack.pushPose();
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f matrix3f = pPoseStack.last().normal();

        Direction facing = pBlockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        pPoseStack.translate(.5f, 0 + 5.3f / 16f, .5f + -0.01f / 16f);

        if (facing == Direction.WEST) {
            if (pBlockEntity.getHeat() < 125) {
                pPoseStack.mulPose(Vector3f.XP.rotationDegrees((45)));
            } else {
                pPoseStack.mulPose(Vector3f.XP.rotationDegrees(((float) pBlockEntity.getHeat() / BrewingRecipe.SUPERHEATED) * 310));
            }
            pPoseStack.translate(-.5f, 0, .5f);
        }
        else if (facing == Direction.SOUTH) {
            pPoseStack.translate(0, 0, 1f);
            if (pBlockEntity.getHeat() < 125) {
                pPoseStack.mulPose(Vector3f.ZN.rotationDegrees((45)));
            } else {
                pPoseStack.mulPose(Vector3f.ZN.rotationDegrees(((float) pBlockEntity.getHeat() / BrewingRecipe.SUPERHEATED) * 310));
            }
        }
        else if (facing == Direction.EAST) {
            if (pBlockEntity.getHeat() < 125) {
                pPoseStack.mulPose(Vector3f.XN.rotationDegrees((45)));
            } else {
                pPoseStack.mulPose(Vector3f.XN.rotationDegrees(((float) pBlockEntity.getHeat() / BrewingRecipe.SUPERHEATED) * 310));
            }
            pPoseStack.translate(.5f, 0, .5f);
        }
        else {
            if (pBlockEntity.getHeat() < 125) {
                pPoseStack.mulPose(Vector3f.ZP.rotationDegrees((45)));
            } else {
                pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(((float) pBlockEntity.getHeat() / BrewingRecipe.SUPERHEATED) * 310));
            }
        }

        // translate back
        pPoseStack.translate(-0.5f, -(5.3f + 1.3f) / 16f, -.5f + 0.01f / 16f);

        lineVertex.vertex(matrix4f, .5f, 5.3f / 16f, -0.01f / 16f).color(0.0f, 0.0f, 0.0f, 1.0f).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        lineVertex.vertex(matrix4f, .5f, (5.3f + 1.3f) / 16f, -0.01f / 16f).color(0.0f, 0.0f, 0.0f, 1.0f).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();

        pPoseStack.popPose();

    }
}
