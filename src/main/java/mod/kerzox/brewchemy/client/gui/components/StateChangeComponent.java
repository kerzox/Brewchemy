package mod.kerzox.brewchemy.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.client.gui.menu.DefaultMenu;
import mod.kerzox.brewchemy.client.gui.menu.GerminationChamberMenu;
import mod.kerzox.brewchemy.client.gui.screen.DefaultScreen;
import net.minecraft.resources.ResourceLocation;

public class StateChangeComponent<T extends DefaultMenu<?>> extends WidgetComponent<T> {

    private int u1, u2, v1, v2;
    private int minimum, maximum;
    private boolean swap;

    public StateChangeComponent(DefaultScreen<T> screen, ResourceLocation texture, int x, int y, int width, int height, int u1, int v1, int u2, int v2) {
        super(screen, x, y, width, height, texture);
        this.u1 = u1;
        this.u2 = u2;
        this.v1 = v1;
        this.v2 = v2;
    }

    public void changeState(boolean state) {
        this.swap = state;
    }

    @Override
    public void drawComponent(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!swap) {
            this.setTextureOffset(this.u1, this.v1);
            this.draw(pPoseStack, this.x, this.y, this.width, this.height);
        } else {
            this.setTextureOffset(this.u2, this.v2);
            this.draw(pPoseStack, this.x, this.y, this.width, this.height);
        }
    }
}
