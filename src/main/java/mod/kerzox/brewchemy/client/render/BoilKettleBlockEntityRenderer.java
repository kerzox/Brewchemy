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
import net.minecraft.util.Mth;
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


        FluidStack fluid = pBlockEntity.getSidedFluidTank().getInputHandler().getFluidInTank(0);
        if (!fluid.isEmpty()) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
            pPoseStack.pushPose();

            float percentage = ( (float) pBlockEntity.getSidedFluidTank().getInputHandler().getFluidInTank(0).getAmount() / (float) pBlockEntity.getSidedFluidTank().getInputHandler().getTankCapacity(0)) * 100;

            if (percentage < 10 && percentage != 0) {
                percentage = 10;
            }
            addVertex(quadVertex, pPoseStack, 0.1f, (percentage / 75f), .9f, sprite.getU(0), sprite.getV(16), IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor());
            addVertex(quadVertex, pPoseStack, .9f, (percentage / 75f), .9f, sprite.getU(16), sprite.getV(16), IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor());
            addVertex(quadVertex, pPoseStack, .9f, (percentage / 75f), 0.1f, sprite.getU(16), sprite.getV(0), IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor());
            addVertex(quadVertex, pPoseStack, 0.1f, (percentage / 75f), 0.1f, sprite.getU(0), sprite.getV(0), IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor());
            pPoseStack.popPose();
        }


        VertexConsumer lineVertex = pBufferSource.getBuffer(RenderType.lines());

        pPoseStack.pushPose();
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f matrix3f = pPoseStack.last().normal();

        pPoseStack.translate(.5f, 0 + 5.3f / 16f, .5f + -0.01f / 16f);
        // do rotation animation

        if (pBlockEntity.getHeat() < 125) {
            pPoseStack.mulPose(Vector3f.ZP.rotationDegrees((45)));
        } else {
            pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(((float) pBlockEntity.getHeat() / BrewingRecipe.SUPERHEATED) * 310));
        }
        // translate back
        pPoseStack.translate(-0.5f, -(5.3f + 1.3f) / 16f, -.5f + 0.01f / 16f);

        lineVertex.vertex(matrix4f, .5f, 5.3f / 16f, -0.01f / 16f).color(0.0f, 0.0f, 0.0f, 1.0f).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        lineVertex.vertex(matrix4f, .5f, (5.3f + 1.3f) / 16f, -0.01f / 16f).color(0.0f, 0.0f, 0.0f, 1.0f).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();

        pPoseStack.popPose();

    }
}
