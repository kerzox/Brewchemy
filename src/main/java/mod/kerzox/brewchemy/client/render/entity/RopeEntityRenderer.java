package mod.kerzox.brewchemy.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.render.types.BrewchemyRenderTypes;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.common.entity.RopeEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class RopeEntityRenderer extends EntityRenderer<RopeEntity> {

    private EntityRendererProvider.Context context;

    public RopeEntityRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        context = p_174008_;
    }

    @Override
    public void render(RopeEntity entity,
                       float p_114486_,
                       float partialTicks,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int combinedLight) {

        super.render(entity, p_114486_, partialTicks, poseStack, bufferSource, combinedLight);

        int tint = 0xFFbc8c17;
        int packedLight = LightTexture.pack(Minecraft.getInstance().level.getBrightness(LightLayer.BLOCK, entity.getOnPos()), Minecraft.getInstance().level.getBrightness(LightLayer.SKY, entity.getOnPos()));
        BakedModel model = context.getBlockRenderDispatcher().getBlockModel(BrewchemyRegistry.Blocks.ROPE_TIED_POST_BLOCK.get().defaultBlockState());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation(Brewchemy.MODID, "block/rope_block"));

        WrappedPose wp = new WrappedPose(poseStack);
        wp.push();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.solid());

        BlockPos[] positions = entity.getPositions();

        BlockPos normalized = positions[1].subtract(positions[0]);
        Direction direction = Direction.fromDelta(normalized.getX(), normalized.getY(), normalized.getZ());

        if (direction.getAxis() == Direction.Axis.Z) {
            wp.rotateY(90);
            wp.translate(1f, 0, 0);
        }

        wp.enclosedTranslate(0.5f, -6 / 16f, -0.5f, () -> {

            if (direction.getAxis() == Direction.Axis.X) {

                int min = Math.min(positions[0].getX(), positions[1].getX());
                int max = Math.max(positions[0].getX(), positions[1].getX());

                for (int i = min; i < max; i++) {
                    wp.enclosedTranslate((float) (i - entity.position().x), 0, 0, () -> drawRope(poseStack, bufferSource, wp, sprite, packedLight));
                }
                return;
            }

            if (direction.getAxis() == Direction.Axis.Z) {

                int min = Math.min(positions[0].getZ(), positions[1].getZ());
                int max = Math.max(positions[0].getZ(), positions[1].getZ());

                for (int i = min; i < max; i++) {
                    wp.enclosedTranslate((float) (i - entity.position().z), 0, 0, () -> drawRope(poseStack, bufferSource, wp, sprite, packedLight));
                }
                return;
            }

            int min = Math.min(positions[0].getY(), positions[1].getY());
            int max = Math.max(positions[0].getY(), positions[1].getY());

            wp.translate(-1, 14/16f, 0);
            wp.rotateZ(90);

            for (int i = min; i < max; i++) {
                wp.enclosedTranslate((float) (i - entity.position().y), 0, 0, () -> drawRope(poseStack, bufferSource, wp, sprite, packedLight));
            }


        });

        wp.pop();

/*        for (RopeEntity ropeEntity : entity.level().getEntitiesOfClass(RopeEntity.class, entity.getBoundingBox())) {
            if (ropeEntity != entity) {
                //TODO add this as a cached value in the entity
                AABB intersection = entity.getBoundingBox().intersect(ropeEntity.getBoundingBox());


            }
        }*/

        for (AABB intersection : entity.getIntersections().keySet()) {
            for (RopeEntity ropeEntity : entity.getIntersections().get(intersection)) {
                LevelRenderer.renderLineBox(wp.asStack(),
                        Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
                        intersection.minX - entity.position().x,
                        intersection.minY - entity.position().y,
                        intersection.minZ - entity.position().z,
                        intersection.maxX - entity.position().x,
                        intersection.maxY - entity.position().y,
                        intersection.maxZ - entity.position().z, direction.getAxis() == Direction.Axis.Z ? 1f : 0, direction.getAxis() == Direction.Axis.X ? 1f : 0, direction.getAxis() == Direction.Axis.Y ? 1f : 0, 1.0F);
            }
        }


    }

    private static void drawRope(PoseStack poseStack, MultiBufferSource bufferSource, WrappedPose wp, TextureAtlasSprite sprite, int packedLight) {
        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack,
                bufferSource.getBuffer(RenderType.solid()),
                sprite,
                Direction.NORTH,
                packedLight,
                0, 7 / 16f, 7 / 16f, 1, 9 / 16f, 1,
                0xFFffffff));

        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack,
                bufferSource.getBuffer(RenderType.solid()),
                sprite,
                Direction.SOUTH,
                packedLight,
                0, 7 / 16f, 1, 1, 9 / 16f, 9 / 16f,
                0xFFffffff));

        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack,
                bufferSource.getBuffer(RenderType.solid()),
                sprite,
                Direction.UP,
                packedLight,
                0, 0, 9 / 16f, 1, 7 / 16f, 7 / 16f,
                0xFFffffff));

        // add a check incase we want to cull face

        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack,
                bufferSource.getBuffer(RenderType.solid()),
                sprite,
                Direction.UP,
                packedLight,
                0, 0, 0, 0, 0, 0,
                0xFFffffff));

        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack,
                bufferSource.getBuffer(RenderType.solid()),
                sprite,
                Direction.UP,
                packedLight,
                0, 0, 0, 0, 0, 0,
                0xFFffffff));

        wp.push();
        wp.rotateX(180);

        wp.enclosedTranslate(0, 0, 0, () -> RenderingUtil.drawSpriteQuad(poseStack,
                bufferSource.getBuffer(RenderType.solid()),
                sprite,
                Direction.UP,
                packedLight,
                0, 0, 9 / 16f, 1, 7 / 16f, 7 / 16f,
                0xFFffffff));

        wp.pop();
    }

    @Override
    public ResourceLocation getTextureLocation(RopeEntity ropeEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
