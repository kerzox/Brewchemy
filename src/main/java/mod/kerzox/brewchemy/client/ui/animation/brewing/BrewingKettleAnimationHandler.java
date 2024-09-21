package mod.kerzox.brewchemy.client.ui.animation.brewing;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.kerzox.brewchemy.client.Animation;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.client.ui.screen.BrewingScreen;
import mod.kerzox.brewchemy.common.event.TickUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class BrewingKettleAnimationHandler {

    enum State {
        START,
        BOBBING,
        FINISH,
        NONE
    }

    private BrewingScreen screen;
    private WrappedPose wp;
    private ItemStack itemStack;
    private GuiGraphics graphics;
    private State animationState = State.NONE;

    private int itemX = 0;

    public BrewingKettleAnimationHandler() {
        itemX = 86 - 8; // center of kettle
        itemX = (int) (itemX - (Math.random() * 8));
    }

    public void setGraphics(GuiGraphics graphics) {
        this.graphics = graphics;
    };

    public void setScreen(BrewingScreen brewingScreen) {
        this.screen = brewingScreen;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setWrappedPose(WrappedPose wp) {
        this.wp = wp;
    }

    public void setAnimationState(String state) {
        this.animationState = State.valueOf(state.toUpperCase());
    }

    public void runAnimation(float partialTick) {
        if (this.screen == null) return;
        if (this.animations.containsKey(this.animationState))
            this.animations.get(this.animationState).animationTick(partialTick);
    }

    protected HashMap<State, Animation> animations = new HashMap<>() {{
        put(State.START, new Animation(TickUtils.secondsToTicks(1)) {

            @Override
            protected void onAnimationProgress(float partialTicks) {
                long currentTime = System.currentTimeMillis();
                float progress = getAnimationProgress(partialTicks) / this.maxTicks;
                float easeOutT = progress * (2 - progress);

                float startY = (float) (screen.getGuiTop());
                float endY = screen.getGuiTop() + 54;

                float currentY = startY + easeOutT * (endY - startY);

                wp.push();
                wp.translate(itemX + screen.getGuiLeft(), currentY, 0);
                wp.asStack().scale(0.75f, 0.75f, 1);
                wp.translate(16 * 0.25f, 16 * 0.25f, 0);
                graphics.renderItem(itemStack, 0, 0);

                wp.pop();
            }

            @Override
            protected void onFinish() {
                setAnimationState("bobbing");
            }
        });
        put(State.BOBBING, new Animation(TickUtils.secondsToTicks(2), true) {

            @Override
            protected void onAnimationProgress(float partialTicks) {
                float endY = screen.getGuiTop() - 52;

                float t = (float) currentTick / this.maxTicks;
                float Bobbing = 1.5f * (float) Math.sin(2 * Math.PI / 2 * t);
                wp.push();
                wp.translate(itemX + screen.getGuiLeft(), screen.getGuiTop() + 52 + (Bobbing), 0);
                wp.asStack().scale(0.75f, 0.75f, 1);
                wp.translate(16 * 0.25f, 16 * 0.25f, 0);
                graphics.renderItem(itemStack, 0, 0, 0);
                wp.pop();
            }

            @Override
            protected void onFinish() {
                //setAnimationState("finish");
            }
        });
        put(State.FINISH, new Animation(TickUtils.secondsToTicks(2)) {

            @Override
            protected void onAnimationProgress(float partialTicks) {
                System.out.println(animationState + ":" + this.currentTick);
            }

            @Override
            protected void onFinish() {

            }
        });
    }};

    public CompoundTag saveToTag() {
        CompoundTag tag = new CompoundTag();
        for (State state : this.animations.keySet()) {
            CompoundTag tag1 = new CompoundTag();
            tag.put(state.toString().toLowerCase(), this.animations.get(state).saveToTag());
        }
        tag.putString("animation_state", this.animationState.toString().toLowerCase());
        if (this.itemStack != null) {
            this.itemStack.save(tag);
        }
        return tag;
    }

    public void readTag(CompoundTag tag) {
        for (State state : this.animations.keySet()) {
            this.animations.get(state).readFromTag(tag.getCompound(state.toString().toLowerCase()));
        }
        this.animationState = State.valueOf(tag.getString("animation_state").toUpperCase());
    }


}
