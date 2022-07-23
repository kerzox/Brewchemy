package mod.kerzox.brewchemy.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.*;

public class RenderEvent {

    private static final ResourceLocation NAUSEA_LOCATION = new ResourceLocation("textures/misc/nausea.png");

    @SubscribeEvent
    public void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() == AFTER_SKY) {
            if (Minecraft.getInstance().player != null) {
                if (Minecraft.getInstance().player.hasEffect(BrewchemyRegistry.Effects.INTOXICATED.get())) {
                    float f2 = 5.0F / (.02f + 5.0F) - .02f * 0.04F;
                    f2 *= f2;
                    Vector3f vector3f = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
                    event.getPoseStack().mulPose(vector3f.rotationDegrees(((float) event.getRenderTick() + event.getPartialTick()) * (float) 7));
                    event.getPoseStack().scale(1.0F / f2, 1.0F, 1.0F);
                    float f3 = -((float) event.getRenderTick() + event.getPartialTick()) * (float) 7;
                    event.getPoseStack().mulPose(vector3f.rotationDegrees(f3));
                }
            }
        }
    }

}
