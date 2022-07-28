package mod.kerzox.brewchemy.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.effects.IntoxicatedEffect;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.*;

public class RenderEvent {


    @SubscribeEvent
    public void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() == AFTER_SKY) {
            IntoxicatedEffect.overlay(event.getPoseStack(), event.getRenderTick(), event.getPartialTick());
        }
        else if (event.getStage() == AFTER_SOLID_BLOCKS) {
            Player player = Minecraft.getInstance().player;
            PoseStack poseStack = event.getPoseStack();
            int renderTick = event.getRenderTick();
            ClientLevel level = Minecraft.getInstance().level;
            float partialTicks = event.getPartialTick();
            if (player != null) {
                if (player.getMainHandItem().is(BrewchemyRegistry.Blocks.WAREHOUSE_BLOCK.get().asItem())) {
                    // render the overlay
                }
            }
        }
    }



}
