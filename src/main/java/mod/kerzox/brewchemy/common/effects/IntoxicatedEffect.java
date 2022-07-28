package mod.kerzox.brewchemy.common.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class IntoxicatedEffect extends MobEffect {

    private static final ResourceLocation NAUSEA_LOCATION = new ResourceLocation("textures/misc/nausea.png");

    public static void overlay(PoseStack poseStack, int renderTick, float partialTick) {
        if (Minecraft.getInstance().player != null) {
            if (Minecraft.getInstance().player.hasEffect(BrewchemyRegistry.Effects.INTOXICATED.get())) {
                poseStack.pushPose();
                float f2 = 5.0F / (.02f + 5.0F) - .02f * 0.04F;
                f2 *= f2;
                Vector3f vector3f = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
                poseStack.mulPose(vector3f.rotationDegrees(((float) renderTick + partialTick) * (float) 7));
                poseStack.scale(1.0F / f2, 1.0F, 1.0F);
                float f3 = -((float) renderTick + partialTick) * (float) 7;
                poseStack.mulPose(vector3f.rotationDegrees(f3));
                poseStack.popPose();
            }
        }
    }

    public IntoxicatedEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity instanceof Player player) {
            if (!pLivingEntity.level.isClientSide) {
                player.getFoodData().eat(pAmplifier + 1, .5F);
                if (pLivingEntity.getHealth() < pLivingEntity.getMaxHealth()) {
                    pLivingEntity.heal(.05F * pAmplifier);
                }
            }
        }
    }
}
