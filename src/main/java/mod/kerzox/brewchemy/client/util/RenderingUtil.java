package mod.kerzox.brewchemy.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

public class RenderingUtil {

    public static void renderModelCorrectLighting(PoseStack pMatrixStack, MultiBufferSource pBuffer, BakedModel model, BlockEntity te, BlockPos pos, int overlay) {
        Level world = te.getLevel();

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(
                world,
                model,
                te.getBlockState(),
                pos,
                pMatrixStack,
                pBuffer.getBuffer(RenderType.cutout()),
                false,
                RandomSource.create(),
                0,
                overlay, EmptyModelData.INSTANCE);
    }

}
