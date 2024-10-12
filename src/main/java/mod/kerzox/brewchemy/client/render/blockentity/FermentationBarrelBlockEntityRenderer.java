package mod.kerzox.brewchemy.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.common.blockentity.FermentationBarrelBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class FermentationBarrelBlockEntityRenderer implements BlockEntityRenderer<FermentationBarrelBlockEntity> {

    public static ResourceLocation SINGLE = new ResourceLocation(Brewchemy.MODID, "block/fermentation_barrel_single");
    public static ResourceLocation MULTIBLOCK = new ResourceLocation(Brewchemy.MODID, "block/obj/fermentation_barrel/fermentation_barrel_2x2");
    public static ResourceLocation TAP = new ResourceLocation(Brewchemy.MODID, "block/barrel_tap");

    private BakedModel single;
    private BakedModel multi;
    private BakedModel tap;

    public FermentationBarrelBlockEntityRenderer(BlockEntityRendererProvider.Context p_174008_) {
        single = Minecraft.getInstance().getModelManager().getModel(SINGLE);
        multi = Minecraft.getInstance().getModelManager().getModel(MULTIBLOCK);
        tap = Minecraft.getInstance().getModelManager().getModel(TAP);
    }

    @Override
    public void render(FermentationBarrelBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        WrappedPose pose = new WrappedPose(pPoseStack);

        Direction facing = pBlockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        boolean formed = pBlockEntity.getController().isFormed();



        if (pBlockEntity.getController().getMasterBlock() != pBlockEntity) return;

        pose.push();

        int masterX = pBlockEntity.getBlockPos().getX();
        int masterY = pBlockEntity.getBlockPos().getY();
        int masterZ = pBlockEntity.getBlockPos().getZ();

        int[] mm = pBlockEntity.getController().getMinMax();

        if (formed) {

            float relativeMinX = mm[0] - masterX;
            float relativeMinY = mm[1] - masterY;
            float relativeMinZ = mm[2] - masterZ;

            float relativeMaxX = mm[3] - masterX;
            float relativeMaxY = mm[4] - masterY;
            float relativeMaxZ = mm[5] - masterZ;

            float centerX = (relativeMinX + relativeMaxX) / 2.0F;
            float centerY = (relativeMinY + relativeMaxY) / 2.0F;
            float centerZ = (relativeMinZ + relativeMaxZ) / 2.0F;

            pose.translate(centerX, centerY, centerZ);
            pose.rotateByDirection(pBlockEntity.getController().getFormingDirection());
            pose.translate(1, 0, 0);

            Direction facing2 = pBlockEntity.getController().getFormingDirection();

        } else pose.rotateByDirection(facing);

        if (formed) {
            RenderingUtil.renderModel(pose.asStack(), multi, pBufferSource, pPackedLight);
            pose.translate(-1f, 0, -0.5f);
            if (pBlockEntity.isTapped()) RenderingUtil.renderSolidBlockEntity(pose.asStack(), tap, pBlockEntity, pBufferSource, pPackedLight);
        } else {
            RenderingUtil.renderSolidBlockEntity(pose.asStack(), single, pBlockEntity, pBufferSource, pPackedLight);
            if (pBlockEntity.isTapped()) RenderingUtil.renderSolidBlockEntity(pose.asStack(), tap, pBlockEntity, pBufferSource, pPackedLight);
        }

        pose.pop();

    }



}
