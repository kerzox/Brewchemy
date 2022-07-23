package mod.kerzox.brewchemy.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.client.gui.menu.DefaultMenu;
import mod.kerzox.brewchemy.client.gui.screen.DefaultScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class WidgetComponent<T extends DefaultMenu<?>> extends GuiComponent implements Widget, GuiEventListener {

    protected ResourceLocation widgetTexture;
    protected final int x1;
    protected final int y1;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int u;
    protected int v;
    protected boolean isHovered;
    public boolean active = true;
    public boolean visible = true;
    private boolean focused;

    protected DefaultScreen<T> screen;

    public WidgetComponent(DefaultScreen<T> screen, int x, int y, int width, int height, ResourceLocation texture) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.x1 = x;
        this.y1 = y;
        this.u = 0;
        this.v = 0;
        this.width = width;
        this.height = height;
        this.widgetTexture = texture;
    }

    public void setTextureOffset(int u, int v) {
        this.u = u;
        this.v = v;
    }

    public void updateTexture(ResourceLocation widgetTexture) {
        this.widgetTexture = widgetTexture;
    }

    public void updatePositionToScreen() {
        this.x = screen.getGuiLeft() + x;
        this.y = screen.getGuiTop() + y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ResourceLocation getWidgetTexture() {
        return widgetTexture;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getV() {
        return v;
    }

    public int getU() {
        return u;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setColor(float red, float green, float blue, float alpha) {
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    public void setColor(int color) {
        float alpha = ((color >> 24) & 0xFF) / 255F;
        float red = ((color >> 16) & 0xFF) / 255F;
        float green = ((color >> 8) & 0xFF) / 255F;
        float blue = ((color) & 0xFF) / 255F;
        setColor(red, green, blue, alpha);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!visible) return;
        if (this.widgetTexture != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, this.widgetTexture);
        }
        this.x = this.screen.getGuiLeft() + this.x1;
        this.y = this.screen.getGuiTop() + this.y1;
        drawComponent(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    protected void draw(PoseStack pPoseStack, int pX, int pY, int pWidth, int pHeight) {
        this.draw(pPoseStack, pX, pY, pWidth, pHeight, 256, 256);
    }

    protected void draw(PoseStack pPoseStack, int pX, int pY, int pWidth, int pHeight, int texHeight, int texWidth) {
        blit(pPoseStack, pX, pY, screen.getBlitOffset(), this.u, this.v, pWidth, pHeight, texHeight, texWidth);
    }

    public abstract void drawComponent(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick);
}
