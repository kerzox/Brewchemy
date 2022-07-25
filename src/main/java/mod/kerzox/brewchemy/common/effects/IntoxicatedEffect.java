package mod.kerzox.brewchemy.common.effects;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class IntoxicatedEffect extends MobEffect {

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
