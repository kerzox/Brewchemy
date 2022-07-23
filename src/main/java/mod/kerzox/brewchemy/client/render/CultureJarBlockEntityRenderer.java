package mod.kerzox.brewchemy.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.brewchemy.client.util.RenderingUtil;
import mod.kerzox.brewchemy.common.blockentity.FermentsJarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import static mod.kerzox.brewchemy.client.util.RenderingUtil.addVertex;

public class CultureJarBlockEntityRenderer implements BlockEntityRenderer<FermentsJarBlockEntity> {

    public CultureJarBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(FermentsJarBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        FluidStack fluid = pBlockEntity.getTank().getFluid();
        if (!fluid.isEmpty()) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
            pPoseStack.pushPose();
            float percentage = 9/16f * ((float) pBlockEntity.getTank().getFluid().getAmount() / pBlockEntity.getTank().getCapacity());
            RenderingUtil.drawSpriteAsQuads(pPoseStack, pBufferSource.getBuffer(RenderType.solid()), sprite,5f/16f, 1/16f, 5f/16f,11/16f, percentage ,11f/16f, IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor());
            pPoseStack.popPose();
        }
    }


}
