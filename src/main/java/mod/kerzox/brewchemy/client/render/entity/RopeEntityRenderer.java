package mod.kerzox.brewchemy.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.common.entity.RopeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;

public class RopeEntityRenderer extends EntityRenderer<RopeEntity> {

    public static ResourceLocation KNOT = new ResourceLocation(Brewchemy.MODID, "block/rope_knot");
    private EntityRendererProvider.Context context;

    public RopeEntityRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        context = p_174008_;
    }

    @Override
    public void render(RopeEntity entity, float p_114486_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
        super.render(entity, p_114486_, partialTicks, poseStack, bufferSource, combinedLight);

        int packedLight = LightTexture.pack(Minecraft.getInstance().level.getBrightness(LightLayer.BLOCK, entity.getOnPos()), Minecraft.getInstance().level.getBrightness(LightLayer.SKY, entity.getOnPos()));
        BakedModel knot = Minecraft.getInstance().getModelManager().getModel(KNOT);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation(Brewchemy.MODID, "block/rope_block"));

        WrappedPose wp = new WrappedPose(poseStack);
        wp.push();

        BlockPos[] positions = entity.getPositions();

        BlockPos normalized = positions[1].subtract(positions[0]);
        Direction direction = Direction.fromDelta(normalized.getX(), normalized.getY(), normalized.getZ());

        int minX = Math.min(positions[0].getX(), positions[1].getX());
        int maxX = Math.max(positions[0].getX(), positions[1].getX());

        int minZ = Math.min(positions[0].getZ(), positions[1].getZ());
        int maxZ = Math.max(positions[0].getZ(), positions[1].getZ());

        int minY = Math.min(positions[0].getY(), positions[1].getY());
        int maxY = Math.max(positions[0].getY(), positions[1].getY());

        if (entity.isStructural()) {
            wp.push();
            wp.translate((float) (minX - entity.position().x), -6/16f, (float) (minZ - entity.position().z));
            RenderingUtil.renderModel(wp.asStack(), knot, bufferSource, packedLight);
            wp.pop();

            wp.push();
            wp.translate( (float) (maxX - entity.position().x), -6/16f,  (float) (maxZ - entity.position().z));
            RenderingUtil.renderModel(wp.asStack(), knot, bufferSource, packedLight);
            wp.pop();
        }

        if (direction.getAxis() == Direction.Axis.Z) {
            wp.rotateY(90);
            wp.translate(1f, 0, 0);
        }

        wp.enclosedTranslate(0.5f, -6 / 16f, -0.5f, () -> {

            if (direction.getAxis() == Direction.Axis.X) {


                for (int i = minX; i < maxX; i++) {
                    wp.enclosedTranslate((float) (i - entity.position().x), 0, 0, () -> drawRope(poseStack, bufferSource, wp, sprite, packedLight));
                }
                return;
            }

            if (direction.getAxis() == Direction.Axis.Z) {

                for (int i = minZ; i < maxZ; i++) {
                    wp.enclosedTranslate((float) (i - entity.position().z), 0, 0, () -> drawRope(poseStack, bufferSource, wp, sprite, packedLight));
                }
                return;
            }

            wp.translate(-1, 14 / 16f, 0);
            wp.rotateZ(90);

            for (int i = minY; i < maxY; i++) {
                wp.enclosedTranslate((float) (i - entity.position().y), 0, 0, () -> drawRope(poseStack, bufferSource, wp, sprite, packedLight));
            }


        });

        wp.pop();


    }

    private static void drawRope(PoseStack poseStack, MultiBufferSource bufferSource, WrappedPose wp, TextureAtlasSprite sprite, int packedLight) {
        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack, bufferSource.getBuffer(RenderType.solid()), sprite, Direction.NORTH, packedLight, 0, 7 / 16f, 7 / 16f, 1, 9 / 16f, 1, 0xFFffffff));
        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack, bufferSource.getBuffer(RenderType.solid()), sprite, Direction.SOUTH, packedLight, 0, 7 / 16f, 1, 1, 9 / 16f, 9 / 16f, 0xFFffffff));
        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack, bufferSource.getBuffer(RenderType.solid()), sprite, Direction.UP, packedLight, 0, 0, 9 / 16f, 1, 7 / 16f, 7 / 16f, 0xFFffffff));
        wp.push();
        wp.rotateX(180);
        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack, bufferSource.getBuffer(RenderType.solid()), sprite, Direction.UP, packedLight, 0, 0, 9 / 16f, 1, 7 / 16f, 7 / 16f, 0xFFffffff));
        wp.pop();
    }

    @Override
    public ResourceLocation getTextureLocation(RopeEntity ropeEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
