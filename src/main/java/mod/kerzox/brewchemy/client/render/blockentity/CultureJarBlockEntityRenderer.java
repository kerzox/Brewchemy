package mod.kerzox.brewchemy.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.common.blockentity.CultureJarBlockEntity;
import mod.kerzox.brewchemy.common.network.PacketHandler;
import mod.kerzox.brewchemy.common.network.RequestDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

public class CultureJarBlockEntityRenderer implements BlockEntityRenderer<CultureJarBlockEntity> {

    public CultureJarBlockEntityRenderer(BlockEntityRendererProvider.Context p_174008_) {

    }

    @Override
    public void render(CultureJarBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pBlockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(cap -> {
            RequestDataPacket.get(pBlockEntity.getBlockPos());
            FluidStack fluid = cap.getFluidInTank(0);
            if (!fluid.isEmpty()) {
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
                pPoseStack.pushPose();
                float percentage = 9/16f * ((float) cap.getFluidInTank(0).getAmount() /  cap.getTankCapacity(0));
                RenderingUtil.drawSpriteAsQuads(
                        pPoseStack,
                        pBufferSource.getBuffer(RenderType.translucent()),
                        sprite,
                        5f/16f, 1/16f, 5f/16f,11/16f,
                        percentage,
                        11f/16f, IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor());
                pPoseStack.popPose();
            }
        });
        }
}
