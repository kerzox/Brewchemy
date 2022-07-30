package mod.kerzox.brewchemy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.client.util.RenderingUtil;
import mod.kerzox.brewchemy.common.blockentity.warehouse.WarehouseBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.LightLayer;

public class WarehouseBlockEntityRenderer implements BlockEntityRenderer<WarehouseBlockEntity> {

    public WarehouseBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    // render an overlay into world

    @Override
    public void render(WarehouseBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        for (BlockPos cachedPosition : pBlockEntity.getCachedPositions()) {
            WarehouseSlot warehouseSlot = pBlockEntity.getWarehouseInventory().getSlotFromBlockPos(cachedPosition);
            if (warehouseSlot != null) {
                if (!warehouseSlot.isEmpty()) {
                    pPoseStack.pushPose();
                    BlockPos offsetPos = cachedPosition.subtract(pBlockEntity.getBlockPos());
                    pPoseStack.translate(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
                    int blockLight = pBlockEntity.getLevel().getBrightness(LightLayer.BLOCK, offsetPos);
                    int skyLight = pBlockEntity.getLevel().getBrightness(LightLayer.SKY, offsetPos);
                    if (warehouseSlot.getFullWarehouseItem().getItem() instanceof BlockItem blockItem) {
                        RenderingUtil.renderBlockModel(pPoseStack, Minecraft.getInstance().renderBuffers().bufferSource(), blockItem.getBlock().defaultBlockState(), RenderType.cutout(), LightTexture.pack(blockLight, skyLight));
                    }
                    pPoseStack.popPose();
                }
            }
        }

    }


}
