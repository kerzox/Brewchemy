package mod.kerzox.brewchemy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.util.RenderingUtil;
import mod.kerzox.brewchemy.common.blockentity.MillstoneCrankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MillstoneCrankBlockEntityRenderer implements BlockEntityRenderer<MillstoneCrankBlockEntity> {

    public MillstoneCrankBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    public static ResourceLocation CRANK_MODEL = new ResourceLocation(Brewchemy.MODID, "block/crank");
    public static ResourceLocation CRANK_TEXTURE = new ResourceLocation(Brewchemy.MODID, "block/crank_texture");

    private boolean wasClicked;

    @Override
    public void render(MillstoneCrankBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(CRANK_MODEL);

        pPoseStack.pushPose();

        if (pBlockEntity.getInUse() != 0) {
            pBlockEntity.setRotation(performAnimation(pPartialTick, pBlockEntity));
        }

        if (pBlockEntity.getRotation() != null) {
            pBlockEntity.setPrevRotation(pBlockEntity.getRotation());
            // translate to origin
            pPoseStack.translate(0.5f, 0.5f, 0.5f);
            // do rotation animation
            pPoseStack.mulPose(pBlockEntity.getRotation());
            // translate back
            pPoseStack.translate(-0.5f, -0.5f, -0.5f);
        }

        RenderingUtil.renderSolidModel(
                pPoseStack,
                pBufferSource,
                Minecraft.getInstance().getModelManager().getModel(CRANK_MODEL),
                pBlockEntity,
                pBlockEntity.getBlockPos(),
                pPackedOverlay);

        pPoseStack.popPose();


    }

    private Quaternion performAnimation(float pPartialTick, MillstoneCrankBlockEntity pBlockEntity) {
        return Vector3f.YP.rotationDegrees((float) Mth.lerp((float) ((pBlockEntity.getLevel().getGameTime() + pPartialTick) * 0.075), 0, 360));
    }

}
