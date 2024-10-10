package mod.kerzox.brewchemy.client.render.overlay;

import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class WastedOverlay implements IGuiOverlay {

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        Screen screen = Minecraft.getInstance().screen;

        boolean canEffect = (screen == null || screen instanceof InventoryScreen);

        if (canEffect) {

            if (player != null && !(player.isSpectator() || player.isCreative()) && player.hasEffect(BrewchemyRegistry.Effects.WASTED.get())) {
                int width = mc.getWindow().getGuiScaledWidth();
                int height = mc.getWindow().getGuiScaledHeight();
                WrappedPose pose = new WrappedPose(guiGraphics.pose());
                float midpoint = (float) (-5 + 5) / 2;
                float amplitude = (float) (5 - -5) / 2;
                float period = 20 * 6;
                float period2 = 20 * 4;
                float amplitude2 = amplitude * 0.2f;
                double opacity = midpoint
                        + amplitude * Math.sin(2 * Math.PI * gui.getGuiTicks() / period)   // First, base wave
                        + amplitude2 * Math.sin(2 * Math.PI * gui.getGuiTicks() / period2);  // Second, chaotic wave

                pose.translate((float) opacity, (float) -opacity, (float) opacity);
            }
        }
    }
}
