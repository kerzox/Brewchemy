package mod.kerzox.brewchemy.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.common.blockentity.CultureJarBlockEntity;
import mod.kerzox.brewchemy.common.event.TickUtils;
import mod.kerzox.brewchemy.common.network.PacketHandler;
import mod.kerzox.brewchemy.common.network.RequestDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector3f;

public class CultureJarBlockEntityRenderer implements BlockEntityRenderer<CultureJarBlockEntity> {

    public CultureJarBlockEntityRenderer(BlockEntityRendererProvider.Context p_174008_) {

    }

    @Override
    public void render(CultureJarBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        pBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            ItemStack stack = cap.getStackInSlot(1);
            if (!stack.isEmpty()) {
                pPoseStack.pushPose();
                float rotationSpeed = 2.0f; // Speed of rotation, adjust this value for faster/slower rotation

                float rotationAngle = (TickUtils.getClientTick() * rotationSpeed) % 360; // Mod 360 to keep the angle within [0, 360]

                pPoseStack.translate((1 - 8/16f) * 0.5, 5/16f, (1 - 8/16f) * 0.5);
                pPoseStack.scale(0.5f, 0.5f, 0.5f);
                WrappedPose pose = new WrappedPose(pPoseStack);
                pose.rotateY(rotationAngle);
                pPoseStack.translate(8/16f, 0, 8/16f);
                ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                BakedModel bakedModel = itemRenderer.getModel(stack, null, null, 0);
                itemRenderer
                        .render(stack, ItemDisplayContext.NONE,
                                false, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, bakedModel);
                pPoseStack.popPose();
            }
        });

        pBlockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(cap -> {
            RequestDataPacket.get(pBlockEntity.getBlockPos());
            FluidStack fluid = cap.getFluidInTank(0);
            if (!fluid.isEmpty()) {
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
                pPoseStack.pushPose();
                float percentage = 9 / 16f * ((float) cap.getFluidInTank(0).getAmount() / cap.getTankCapacity(0));
                RenderingUtil.drawSpriteAsQuads(
                        pPoseStack,
                        pBufferSource.getBuffer(RenderType.translucent()),
                        sprite,
                        5f / 16f, 1 / 16f, 5f / 16f, 11 / 16f,
                        percentage,
                        11f / 16f, IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor());
                pPoseStack.popPose();
            }
        });


    }
}
