package mod.kerzox.brewchemy.client.render.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderingUtil {

    public static int custom(String colour, int opacity) {
        String alpha = Integer.toHexString(opacity * 255 / 100);
        String decode = colour.replace("0x", "");
        int r = Integer.valueOf(decode.substring(0, 2), 16);
        int g = Integer.valueOf(decode.substring(2, 4), 16);
        int b = Integer.valueOf(decode.substring(4, 6), 16);
        int a = Integer.parseInt(alpha, 16);
        return new Color(r, g, b, a).getRGB();
    }

    public static int rgbToHex(int r, int g, int b) {
        return Integer.parseInt(String.format("0xFF%02X%02X%02X", r, g, b));
    }

    public static int[] interpolateColor(int[] color1, int[] color2, double factor) {
        int r = (int) (color1[0] + factor * (color2[0] - color1[0]));
        int g = (int) (color1[1] + factor * (color2[1] - color1[1]));
        int b = (int) (color1[2] + factor * (color2[2] - color1[2]));
        return new int[]{r, g, b};
    }

    public static void renderBlockModel(PoseStack poseStack, MultiBufferSource buffer, BlockState blockState, RenderType type, int brightness) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState,
                poseStack,
                buffer,
                brightness,
                0,
                ModelData.EMPTY,
                type);
    }

    public static void drawSpriteGrid(GuiGraphics mStack, int xPos, int yPos, int xSize, int ySize, TextureAtlasSprite sprite, int repeatX, int repeatY) {
        for (int iX = 0; iX < repeatX; iX++) {
            for (int iY = 0; iY < repeatY; iY++) {
                mStack.blit(xPos + (xSize * iX), yPos + (ySize * iY), 0, xSize, ySize, sprite);
            }
        }
    }

    public static int[] covertColour(String hex) {
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);
        return new int[]{r, g, b};
    }

    public static float[] covertColour(int color) {
        float alpha = ((color >> 24) & 0xFF) / 255F;
        float red = ((color >> 16) & 0xFF) / 255F;
        float green = ((color >> 8) & 0xFF) / 255F;
        float blue = ((color) & 0xFF) / 255F;
        return new float[]{red, green, blue, alpha};
    }

    private static void addVertex(VertexConsumer renderer, PoseStack stack, int packedLight, float x, float y, float z, float u, float v, int tint) {
        renderer.vertex(stack.last().pose(), x, y, z)
                .color(tint)
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

    public static void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v) {
        renderer.vertex(stack.last().pose(), x, y, z)
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .uv(u, v)
                .uv2(0, 240)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, int color) {
        renderer.vertex(stack.last().pose(), x, y, z)
                .color(color)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void addVertex(VertexConsumer renderer, PoseStack stack, int packedLight, float x, float y, float z, int color) {
        renderer.vertex(stack.last().pose(), x, y, z)
                .color(color)
                .normal(1, 0, 0)
                .uv2(packedLight)
                .endVertex();
    }

    public static void drawSpriteAsQuads(PoseStack pPoseStack, VertexConsumer vertexConsumer, TextureAtlasSprite sprite, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int tint) {
        drawSpriteAsQuads(pPoseStack, vertexConsumer, sprite, minX, minY, minZ, maxX, maxY, maxZ, tint, false, false);
    }

    public static void rotateBlock(Direction facing, PoseStack poseStack) {
        switch (facing) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case DOWN -> poseStack.mulPose(Axis.XN.rotationDegrees(90));
        }
    }


    public static void drawQuad(PoseStack pPoseStack, VertexConsumer vertexConsumer, Direction direction, int packedLight, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int tint) {
        if (direction == Direction.NORTH) {
            // north
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, minZ, tint);


        } else if (direction == Direction.SOUTH) {
            // south
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, maxZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, maxZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, maxZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, maxZ, tint);


        } else if (direction == Direction.EAST) {
            // east
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, maxZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, maxZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, minZ, tint);

        } else if (direction == Direction.WEST) {

            // west
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, maxZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, maxZ, tint);


        } else if (direction == Direction.UP) {
            // top
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, maxZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, maxZ, tint);


        } else if (direction == Direction.DOWN) {
            // bottom
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, minZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, maxZ, tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, maxZ, tint);
        }
    }


    public static void drawSpriteQuad(PoseStack pPoseStack, VertexConsumer vertexConsumer, TextureAtlasSprite sprite, Direction direction, int packedLight, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int tint) {
        if (direction == Direction.NORTH) {
            // north
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, minZ, sprite.getU(0), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, minZ, sprite.getU(16), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, minZ, sprite.getU(16), sprite.getV(0), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, minZ, sprite.getU(0), sprite.getV(0), tint);

        } else if (direction == Direction.SOUTH) {
            // south
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, maxZ, sprite.getU(0), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, maxZ, sprite.getU(16), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, maxZ, sprite.getU(16), sprite.getV(0), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, maxZ, sprite.getU(0), sprite.getV(0), tint);


        } else if (direction == Direction.EAST) {
            // east
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, minZ, sprite.getU(0), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, maxZ, sprite.getU(16), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, maxZ, sprite.getU(16), sprite.getV(0), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, minZ, sprite.getU(0), sprite.getV(0), tint);

        } else if (direction == Direction.WEST) {

            // west
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, maxZ, sprite.getU(0), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, minZ, sprite.getU(16), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, minZ, sprite.getU(16), sprite.getV(0), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, maxZ, sprite.getU(0), sprite.getV(0), tint);


        } else if (direction == Direction.UP) {
            // top
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, minZ, sprite.getU(0), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, minZ, sprite.getU(16), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, maxZ, sprite.getU(16), sprite.getV(0), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, maxZ, sprite.getU(0), sprite.getV(0), tint);


        } else if (direction == Direction.DOWN) {
            // bottom
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, maxY, minZ, sprite.getU(0), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, maxY, minZ, sprite.getU(16), sprite.getV(16), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, maxX, minY, minZ, sprite.getU(16), sprite.getV(0), tint);
            addVertex(vertexConsumer, pPoseStack, packedLight, minX, minY, minZ, sprite.getU(0), sprite.getV(0), tint);
        }
    }


    public static void drawSpriteAsQuads(PoseStack pPoseStack, VertexConsumer vertexConsumer, TextureAtlasSprite sprite,
                                         float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                         int tint, boolean drawTop, boolean drawBottom) {

        // north
        addVertex(vertexConsumer, pPoseStack, minX, maxY, minZ, sprite.getU(0), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, maxX, maxY, minZ, sprite.getU(16), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, maxX, minY, minZ, sprite.getU(16), sprite.getV(0), tint);
        addVertex(vertexConsumer, pPoseStack, minX, minY, minZ, sprite.getU(0), sprite.getV(0), tint);

        // south
        addVertex(vertexConsumer, pPoseStack, maxX, maxY, maxZ, sprite.getU(0), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, minX, maxY, maxZ, sprite.getU(16), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, minX, minY, maxZ, sprite.getU(16), sprite.getV(0), tint);
        addVertex(vertexConsumer, pPoseStack, maxX, minY, maxZ, sprite.getU(0), sprite.getV(0), tint);

        // east
        addVertex(vertexConsumer, pPoseStack, maxX, maxY, minZ, sprite.getU(0), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, maxX, maxY, maxZ, sprite.getU(16), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, maxX, minY, maxZ, sprite.getU(16), sprite.getV(0), tint);
        addVertex(vertexConsumer, pPoseStack, maxX, minY, minZ, sprite.getU(0), sprite.getV(0), tint);

        // west
        addVertex(vertexConsumer, pPoseStack, minX, maxY, maxZ, sprite.getU(0), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, minX, maxY, minZ, sprite.getU(16), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, minX, minY, minZ, sprite.getU(16), sprite.getV(0), tint);
        addVertex(vertexConsumer, pPoseStack, minX, minY, maxZ, sprite.getU(0), sprite.getV(0), tint);

        // top
        addVertex(vertexConsumer, pPoseStack, maxX, maxY, minZ, sprite.getU(0), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, minX, maxY, minZ, sprite.getU(16), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, minX, maxY, maxZ, sprite.getU(16), sprite.getV(0), tint);
        addVertex(vertexConsumer, pPoseStack, maxX, maxY, maxZ, sprite.getU(0), sprite.getV(0), tint);

        // bottom
        pPoseStack.pushPose();
        WrappedPose pose = new WrappedPose(pPoseStack);
        pose.translate(0, (-(1 - maxY)) + minY, 0);
        pose.rotateX(180);
        addVertex(vertexConsumer, pPoseStack, maxX, maxY, minZ, sprite.getU(0), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, minX, maxY, minZ, sprite.getU(16), sprite.getV(16), tint);
        addVertex(vertexConsumer, pPoseStack, minX, maxY, maxZ, sprite.getU(16), sprite.getV(0), tint);
        addVertex(vertexConsumer, pPoseStack, maxX, maxY, maxZ, sprite.getU(0), sprite.getV(0), tint);
        pPoseStack.popPose();

    }

    public static QuadBakingVertexConsumer addVertex(QuadBakingVertexConsumer baker, Vector3f pos, float u, float v, int color, Direction direction) {
        baker.vertex(pos.x(), pos.y(), pos.z());
        baker.color(color);
        baker.uv(u, v);
        baker.uv2(0, 240);
        baker.normal(1, 0, 0);
        baker.setDirection(direction);
        baker.endVertex();
        return baker;
    }

    public static Vector3f[] getVerticesFromDirection(Direction direction, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        switch (direction) {
            case NORTH -> {
                return new Vector3f[]{new Vector3f(minX, maxY, minZ), new Vector3f(maxX, maxY, minZ), new Vector3f(maxX, minY, minZ), new Vector3f(minX, minY, minZ)};
            }
            case SOUTH -> {
                return new Vector3f[]{new Vector3f(maxX, maxY, maxZ), new Vector3f(minX, maxY, maxZ), new Vector3f(minX, minY, maxZ), new Vector3f(maxX, minY, maxZ)};
            }
            case EAST -> {
                return new Vector3f[]{new Vector3f(maxX, maxY, minZ), new Vector3f(maxX, maxY, maxZ), new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, minY, minZ)};
            }
            case WEST -> {
                return new Vector3f[]{new Vector3f(minX, maxY, maxZ), new Vector3f(minX, maxY, minZ), new Vector3f(minX, minY, minZ), new Vector3f(minX, minY, maxZ)};
            }
            case UP -> {
                return new Vector3f[]{new Vector3f(maxX, maxY, minZ), new Vector3f(minX, maxY, minZ), new Vector3f(minX, maxY, maxZ), new Vector3f(maxX, maxY, maxZ)};
            }
            case DOWN -> {
                return new Vector3f[]{new Vector3f(minX, minY, minZ), new Vector3f(maxX, minY, minZ), new Vector3f(maxX, minY, maxZ), new Vector3f(minX, minY, maxZ)};
            }
        }
        return null;
    }

    public static List<BakedQuad> bakedQuadList(
            float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
            float u1, float v1, float u2, float v2,
            TextureAtlasSprite sprite, int tint) {
        List<BakedQuad> quads = new ArrayList<>();
        QuadBakingVertexConsumer baker = new QuadBakingVertexConsumer(quads::add);
        baker.setSprite(sprite);
        for (Direction direction : Direction.values()) {
            Vector3f[] vertices = getVerticesFromDirection(direction, minX, minY, minZ, maxX, maxY, maxZ);
            if (vertices == null) return null;
            addVertex(baker, vertices[0], sprite.getU0(), sprite.getV1(), tint, direction);
            addVertex(baker, vertices[1], sprite.getU1(), sprite.getV1(), tint, direction);
            addVertex(baker, vertices[2], sprite.getU1(), sprite.getV0(), tint, direction);
            addVertex(baker, vertices[3], sprite.getU0(), sprite.getV0(), tint, direction);
        }
        return quads;
    }

    public static BakedQuad bakeQuad(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float u1, float v1, float u2, float v2, TextureAtlasSprite sprite, int tint, Direction direction) {
        BakedQuad[] quad = new BakedQuad[1];
        QuadBakingVertexConsumer baker = new QuadBakingVertexConsumer(q -> quad[0] = q);
        baker.setSprite(sprite);
        Vector3f[] vertices = getVerticesFromDirection(direction, minX, minY, minZ, maxX, maxY, maxZ);
        if (vertices == null) return null;
        addVertex(baker, vertices[0], u1, v2, tint, direction);
        addVertex(baker, vertices[1], u2, v2, tint, direction);
        addVertex(baker, vertices[2], u2, v1, tint, direction);
        addVertex(baker, vertices[3], u1, v1, tint, direction);
        return quad[0];
    }

    public static List<BakedQuad> getQuads(BakedModel model, BlockEntity tile, Direction side, RenderType type) {
        return model.getQuads(null, null, RandomSource.create(Mth.getSeed(tile.getBlockPos())), ModelData.EMPTY, type);
    }

    public static void renderQuads(PoseStack.Pose matrixEntry,
                                   VertexConsumer builder,
                                   float red, float green, float blue, float alpha,
                                   List<BakedQuad> listQuads,
                                   int combinedLightsIn,
                                   int combinedOverlayIn) {
        for (BakedQuad bakedquad : listQuads) {
            float f;
            float f1;
            float f2;

            f = red * 1f;
            f1 = green * 1f;
            f2 = blue * 1f;

            builder.putBulkData(matrixEntry, bakedquad, f, f1, f2, alpha, combinedLightsIn, combinedOverlayIn, true);
        }
    }

    public static void drawFluidQuad(FluidStack stack, int x, int y, float z, int width, int height, int color) {

    }

    public static void drawTestFluidRect(BufferBuilder consumer, float x, float y, float width, float height, float z, float u, float v, float maxU, float maxV) {
        consumer.vertex(x, y + height, z).uv(u, maxV).endVertex();
        consumer.vertex(x + width, y + height, z).uv(u, maxV).endVertex();
        consumer.vertex(x + width, y, z).uv(u, maxV).endVertex();
        consumer.vertex(x, y, z).uv(u, maxV).endVertex();
        consumer.end();
    }



    private static void putVertex(VertexConsumer builder, PoseStack ms, float x, float y, float z, int color, float u,
                                  float v, Direction face, int light) {

        Vec3i normal = face.getNormal();
        PoseStack.Pose peek = ms.last();
        int a = color >> 24 & 0xff;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        builder.vertex(peek.pose(), x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(peek.normal(), normal.getX(), normal.getY(), normal.getZ())
                .endVertex();
    }

    public static void renderSolidBlockEntity(PoseStack pPoseStack, BakedModel model, BlockEntity pBlockEntity, MultiBufferSource pBufferSource, int pPackedLight) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                .tesselateWithAO(Minecraft.getInstance().level, model,
                        pBlockEntity.getBlockState(),
                        pBlockEntity.getBlockPos(),
                        pPoseStack, pBufferSource.getBuffer(RenderType.solid()),
                        true,
                        Minecraft.getInstance().level.getRandom(), pBlockEntity.getBlockPos().asLong(), pPackedLight, ModelData.EMPTY,
                        RenderType.solid());
    }

    public static void renderModel(PoseStack pPoseStack, BakedModel model, MultiBufferSource pBufferSource, int pPackedLight) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                pPoseStack.last(),
                pBufferSource.getBuffer(RenderType.solid()),
                null,
                model,
                1f,
                1f,
                1f, pPackedLight,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.solid()
        );
    }



    public static void renderCutoutBlockEntity(PoseStack pPoseStack, BakedModel model, BlockEntity pBlockEntity, MultiBufferSource pBufferSource, int pPackedLight) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                .tesselateWithAO(Minecraft.getInstance().level, model,
                        pBlockEntity.getBlockState(),
                        pBlockEntity.getBlockPos(),
                        pPoseStack, pBufferSource.getBuffer(RenderType.cutout()),
                        true,
                        Minecraft.getInstance().level.getRandom(), pBlockEntity.getBlockPos().asLong(), pPackedLight, ModelData.EMPTY,
                        RenderType.cutout());
    }

}
