package mod.kerzox.brewchemy.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.client.gui.menu.DefaultMenu;
import mod.kerzox.brewchemy.client.gui.screen.DefaultScreen;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;

public class ProgressComponent<T extends DefaultMenu<?>> extends WidgetComponent<T> {

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
    }

    private int u1, u2, v1, v2;
    private int minimum, maximum;
    private Direction direction;

    public ProgressComponent(DefaultScreen<T> screen, ResourceLocation texture, int x, int y, int width, int height, int u1, int v1, int u2, int v2) {
        super(screen, x, y, width, height, texture);
        this.u1 = u1;
        this.u2 = u2;
        this.v1 = v1;
        this.v2 = v2;
    }

    public void update(int min, int max, Direction direction) {
        this.minimum = min;
        this.maximum = max;
        this.direction = direction;
    }

    @Override
    public void drawComponent(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (widgetTexture == null) return;
        int size = 0;

        this.setTextureOffset(this.u1, this.v1);
        this.draw(pPoseStack, this.x, this.y, this.width, this.height);

        if (this.maximum != 0) {
            switch (direction) {
                case UP -> {
                    size = this.height * minimum / maximum;
                    this.setTextureOffset(this.u2, this.v2);
                    this.draw(pPoseStack, this.x, this.y, this.width, this.height);
                    this.setTextureOffset(this.u1, this.v1);
                    this.draw(pPoseStack, this.x, this.y, this.width, this.height - size);
                }
                case DOWN -> {
                    size = this.height * minimum / maximum;
                    this.setTextureOffset(this.u2, this.v2);
                    this.draw(pPoseStack, this.x, this.y, this.width, size);
                }
                case LEFT -> {
                    size = this.height * minimum / maximum;
                    this.setTextureOffset(this.u2, this.v2);
                    this.draw(pPoseStack, this.x, this.y, this.width - size, this.height);
                }
                case RIGHT -> {
                    size = this.height * minimum / maximum;
                    this.setTextureOffset(this.u2, this.v2);
                    this.draw(pPoseStack, this.x, this.y, this.width, this.height);
                    this.setTextureOffset(this.u1, this.v1);
                    this.draw(pPoseStack, this.x, this.y, size, this.height - size);
                }
            }
        }

    }
}
