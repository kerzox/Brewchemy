package mod.kerzox.brewchemy.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.client.gui.components.WidgetComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class DefaultScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    protected static final int DEFAULT_X_POS = 0;
    protected static final int DEFAULT_Y_POS = 0;
    protected static final int DEFAULT_WIDTH = 176;
    protected static final int DEFAULT_HEIGHT = 166;

    protected ResourceLocation texture;
    protected int guiX;
    protected int guiY;

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture, int x, int y, int width, int height) {
        super(pMenu, pPlayerInventory, pTitle);
        this.texture = texture;
        this.guiX = x;
        this.guiY = y;
        this.width = width;
        this.height = height;
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture, int x, int y) {
        this(pMenu, pPlayerInventory, pTitle, texture, x, y, 176, 166);
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture) {
        this(pMenu, pPlayerInventory, pTitle, texture, DEFAULT_X_POS, DEFAULT_Y_POS, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
        mouseTracked(pPoseStack, pMouseX, pMouseY);
    }

    protected void mouseTracked(PoseStack pPoseStack, int pMouseX, int pMouseY) {

    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(pPoseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        addToBackground(pPoseStack, pPartialTick, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        super.renderLabels(pPoseStack, pMouseX, pMouseY);
        addToForeground(pPoseStack, pMouseX, pMouseY);
    }

    protected void addWidgetComponent(WidgetComponent<?> widget) {
        addRenderableOnly(widget);
    }

    public abstract void addToBackground(PoseStack stack, float partialTicks, int x, int y);

    public abstract void addToForeground(PoseStack stack, int x, int y);

}
