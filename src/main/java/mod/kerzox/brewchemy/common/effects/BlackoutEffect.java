package mod.kerzox.brewchemy.common.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BlackoutEffect extends MobEffect {

    private static final ResourceLocation NAUSEA_LOCATION = new ResourceLocation("textures/misc/nausea.png");

    public BlackoutEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity instanceof Player player) {
            if (!pLivingEntity.level().isClientSide) {
                if (pAmplifier == 0) {
                    pAmplifier = 1;
                }
                if (pLivingEntity.getHealth() < pLivingEntity.getMaxHealth()) {
                    pLivingEntity.heal(.05F * pAmplifier);
                }
            }
        }
    }
}