package mod.kerzox.brewchemy.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.model.data.ModelData;

import java.util.Objects;
import java.util.Random;

public class RenderingUtil {

    public static void renderSolidModel(PoseStack pMatrixStack, MultiBufferSource pBuffer, BakedModel model, BlockEntity te, BlockPos pos, int overlay) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(
                Objects.requireNonNull(te.getLevel()),
                model,
                te.getBlockState(),
                pos,
                pMatrixStack,
                pBuffer.getBuffer(RenderType.solid()),
                false,
                RandomSource.create(),
                0,
                overlay, ModelData.EMPTY, RenderType.solid());
    }

    public static void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v) {
        renderer.vertex(stack.last().pose(), x, y, z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(u, v)
                .uv2(0, 240)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, int color) {
        renderer.vertex(stack.last().pose(), x, y, z)
                .color(color)
                .uv(u, v)
                .uv2(0, 240)
                .normal(1, 0, 0)
                .endVertex();
    }

}
