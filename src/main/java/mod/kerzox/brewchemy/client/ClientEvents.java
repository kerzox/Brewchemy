package mod.kerzox.brewchemy.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.kerzox.brewchemy.client.render.overlay.BlackoutOverlay;
import mod.kerzox.brewchemy.client.render.types.BrewchemyRenderTypes;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector3f;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_SKY;

public class ClientEvents {

    @SubscribeEvent
    public void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() == AFTER_SKY) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;



            if (player != null && !(player.isSpectator() || (player.isCreative())) && player.hasEffect(BrewchemyRegistry.Effects.WASTED.get()) || player.hasEffect(BrewchemyRegistry.Effects.BLACK_OUT.get())) {
                int width = mc.getWindow().getGuiScaledWidth();
                int height = mc.getWindow().getGuiScaledHeight();
                float f2 = 5.0F / (.02f + 5.0F) - .02f * 0.04F;
                f2 *= f2;
                Vector3f vector3f = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
                WrappedPose pose = new WrappedPose(event.getPoseStack());
                float midpoint = (float) (-5 + 5) / 2;
                float amplitude = (float) (5 - -5) / 2;
                float period = 20 * 6;
                float period2 = 20 * 4;
                float amplitude2 = amplitude * 0.2f;
                double x = midpoint
                        + (amplitude * -0.5) * Math.sin(2 * Math.PI * event.getRenderTick() / period)   // First, base wave
                        + amplitude2 * Math.sin(2 * Math.PI *  event.getRenderTick() / period2);  // Second, chaotic wave

                double y = midpoint
                        + amplitude * Math.sin(2 * Math.PI * event.getRenderTick() / period)   // First, base wave
                        + amplitude2 * Math.sin(2 * Math.PI *  event.getRenderTick() / period2);  // Second, chaotic wave

                double z = midpoint
                        + amplitude * Math.sin(2 * Math.PI * event.getRenderTick() / period)   // First, base wave
                        + (amplitude2 * -0.2) * Math.sin(2 * Math.PI *  event.getRenderTick() / period2);  // Second, chaotic wave

                pose.rotateX((float) x);
                pose.rotateZ((float) z);
            }
        }
    }
}
