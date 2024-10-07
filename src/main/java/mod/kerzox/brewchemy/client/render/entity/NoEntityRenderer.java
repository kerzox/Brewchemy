package mod.kerzox.brewchemy.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.common.entity.SeatEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * Is this really the only way to make a no rendering entity?
 */

public class NoEntityRenderer extends EntityRenderer<Entity> {

    private EntityRendererProvider.Context context;

    public NoEntityRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        context = p_174008_;
    }

    @Override
    public void render(Entity entity, float p_114486_, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {

    }

    @Override
    public ResourceLocation getTextureLocation(Entity p_114482_) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
