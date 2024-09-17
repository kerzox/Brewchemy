package mod.kerzox.brewchemy.client.ui.screen.base;

import com.google.common.collect.Lists;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.ui.component.WidgetComponent;
import mod.kerzox.brewchemy.client.ui.menu.base.DefaultMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public abstract class DefaultScreen<T extends DefaultMenu<?>> extends AbstractContainerScreen<T> implements ICustomScreen {


    public final List<Renderable> backgroundRenderables = Lists.newArrayList();
    public static final int DEFAULT_X_POS = 0;
    public static final int DEFAULT_Y_POS = 0;
    public static final int DEFAULT_WIDTH = 176;
    public static final int DEFAULT_HEIGHT = 166;

    protected ResourceLocation texture;
    protected int guiX;
    protected int guiY;
    protected boolean settingsVisible;

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture, int x, int y, int width, int height) {
        super(pMenu, pPlayerInventory, pTitle);
        this.texture = texture;
        this.guiX = x;
        this.guiY = y;
        this.width = width;
        this.height = height;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    @Override
    protected void containerTick() {
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof WidgetComponent component) component.tick();
        }
        menuTick();
    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        return super.mouseClicked(p_97748_, p_97749_, p_97750_);
    }

    protected void menuTick() {


    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addBackgroundWidget(T p_169406_) {
        this.backgroundRenderables.add(p_169406_);
        return this.addWidget(p_169406_);
    }

    @Override
    protected void init() {
        super.init();
        onOpen();

    }

    protected void onOpen() {

    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture, int x, int y) {
        this(pMenu, pPlayerInventory, pTitle, texture, x, y, 176, 166);
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture) {
        this(pMenu, pPlayerInventory, pTitle, texture, DEFAULT_X_POS, DEFAULT_Y_POS, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, String texture) {
        this(pMenu, pPlayerInventory, pTitle, new ResourceLocation(Brewchemy.MODID, "textures/gui/"+texture), 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, String texture, int width, int height) {
        this(pMenu, pPlayerInventory, pTitle, new ResourceLocation(Brewchemy.MODID, "textures/gui/"+texture), 0, 0, width, height);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2 + guiX;
        int j = (this.height - this.imageHeight) / 2 + guiY;
        //this.upgradePage.render(graphics, pMouseX, pMouseY, partialTick);
        for (Renderable renderable : backgroundRenderables) {
            renderable.render(graphics, pMouseX, pMouseY, partialTick);
        }
        renderBeforeBackground(graphics, partialTick, pMouseX, pMouseY);
        graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
        addToBackground(graphics, partialTick, pMouseX, pMouseY);
    }

    protected void renderBeforeBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    protected abstract void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY);

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float partialTick) {
        super.renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, partialTick);
        this.renderTooltip(graphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics p_283594_, int p_282171_, int p_281909_) {
        super.renderTooltip(p_283594_, p_282171_, p_281909_);
        addToForeground(p_283594_, p_282171_, p_281909_);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.getChildAt(mouseX, mouseY).filter((p_94708_) -> p_94708_.mouseDragged(mouseX, mouseY, button, dragX, dragY)).isPresent()) return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    protected abstract void addToForeground(GuiGraphics graphics, int x, int y);

    @Override
    public T getMenu() {
        return super.getMenu();
    }


}
