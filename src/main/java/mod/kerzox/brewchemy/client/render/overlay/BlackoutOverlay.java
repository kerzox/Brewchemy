package mod.kerzox.brewchemy.client.render.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BlackoutOverlay implements IGuiOverlay {

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player != null && player.hasEffect(BrewchemyRegistry.Effects.BLACK_OUT.get())) {
            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 250);
            float midpoint = (float) (60 + 95) / 2;
            float amplitude = (float) (95 - 60) / 2;
            float period = 20 * 6;
            float period2 = 20 * 2;
            float amplitude2 = amplitude * 0.3f;
            double opacity = midpoint
                    + amplitude * Math.sin(2 * Math.PI * gui.getGuiTicks() / period)   // First, base wave
                    + amplitude2 * Math.sin(2 * Math.PI *  gui.getGuiTicks() / period2);  // Second, chaotic wave

            guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), RenderingUtil.custom("0x000000", (int) opacity));
            guiGraphics.pose().popPose();
        }
    }
}
