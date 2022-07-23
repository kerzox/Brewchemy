package mod.kerzox.brewchemy.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.client.gui.menu.DefaultMenu;
import mod.kerzox.brewchemy.client.gui.screen.DefaultScreen;
import mod.kerzox.brewchemy.common.util.FermentationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MouseOverComponent <T extends DefaultMenu<?>> extends WidgetComponent<T>  {

    protected Component[] textToDisplay;

    public MouseOverComponent(DefaultScreen<T> screen, int x, int y, int width, int height) {
        super(screen, x, y, width, height, null);
    }

    public void setTextToDisplay(Component... textToDisplay) {
        this.textToDisplay = textToDisplay;
    }

    @Override
    public void drawComponent(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (isMouseOver(pMouseX, pMouseY)) {
            this.screen.renderTooltip(pPoseStack, Arrays.stream(textToDisplay).toList(), Optional.empty(), pMouseX, pMouseY);
        }
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return ((pMouseX > this.x) && (pMouseX < this.x + this.width)) &&
                ((pMouseY > this.y) && (pMouseY < this.y + this.height));
    }
}
