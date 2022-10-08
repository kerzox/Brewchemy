package mod.kerzox.brewchemy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.client.util.RenderingUtil;
import mod.kerzox.brewchemy.common.block.WarehouseBlock;
import mod.kerzox.brewchemy.common.blockentity.warehouse.WarehouseBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseSlot;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.registries.ForgeRegistries;

public class WarehouseBlockEntityRenderer implements BlockEntityRenderer<WarehouseBlockEntity> {

    public WarehouseBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    // render an overlay into world

    @Override
    public void render(WarehouseBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        Player player = Minecraft.getInstance().player;
        if (!pBlockEntity.getBlockState().getValue(WarehouseBlock.INVISIBLE)) {
            return;
        }
        if (player.getMainHandItem().getItem() == BrewchemyRegistry.Items.SOFT_MALLET.get() || player.getOffhandItem().getItem() == BrewchemyRegistry.Items.SOFT_MALLET.get()) {
            int blockLight = pBlockEntity.getLevel().getBrightness(LightLayer.BLOCK, pBlockEntity.getBlockPos());
            int skyLight = pBlockEntity.getLevel().getBrightness(LightLayer.SKY, pBlockEntity.getBlockPos());
            LevelRenderer.renderLineBox(pPoseStack,
                    Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES), 0, 0, 0, 1, 1, 1, 0, 0, 1f, 1.0F);
        }

    }


}
