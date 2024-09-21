package mod.kerzox.brewchemy.client;

import mod.kerzox.brewchemy.common.event.TickUtils;
import net.minecraft.nbt.CompoundTag;

abstract public class Animation {

    protected int currentTick;
    protected int prevTick = 0;
    protected int maxTicks = 0;
    protected boolean running = false;
    protected boolean killed = false;
    protected boolean looping = false;
    protected int lastClientTick;

    public Animation(int maxTicks, boolean looping) {
        this.currentTick = 0;
        this.prevTick = 0;
        this.maxTicks = maxTicks;
        this.lastClientTick = TickUtils.getLastClientTick();
        this.looping = looping;
    }

    public Animation(int maxTicks) {
        this(maxTicks, false);
    }

    public void animationTick(float partialTicks) {
        int currentClientTick = TickUtils.getClientTick();  // Get the current client tick

        // Only update animation tick if the client tick has progressed
        if (currentClientTick > lastClientTick) {
            prevTick = currentTick;  // Store previous tick for interpolation
            lastClientTick = currentClientTick; // set last client tick to current
            currentTick++;  // Increment the animation tick
        }

        // Check if the animation has reached the maximum number of ticks
        if (currentTick >= maxTicks) {
            // Update animation progress and render smoothly
            if (looping) {
                currentTick = 0;  // Reset to 0 if looping is enabled
            } else {
                currentTick = maxTicks;  // Cap at maxTicks if not looping
                onFinish();
                return;
            }
        }

        onAnimationProgress(partialTicks);
    }

    protected abstract void onAnimationProgress(float partialTicks);
    protected abstract void onFinish();

    public float getAnimationProgress(float partialTicks) {
        return currentTick + (prevTick - currentTick) * partialTicks;
    }

    public boolean isAnimationComplete() {
        return currentTick >= maxTicks;
    }

    public void reset() {
        currentTick = 0;
        prevTick = 0;
    }

    protected CompoundTag saveAdditional() {
        return new CompoundTag();
    }

    protected void readAdditional(CompoundTag tag) {

    }

    public CompoundTag saveToTag() {
        CompoundTag tag1 = new CompoundTag();
        tag1.putInt("tick", this.currentTick);
        tag1.putInt("prevtick", this.prevTick);
        tag1.putInt("maxTicks", this.maxTicks);
        tag1.putBoolean("running", this.running);
        tag1.putBoolean("killed", this.killed);
        tag1.put("additional", saveAdditional());
        return tag1;
    }

    public void readFromTag(CompoundTag compound) {
        this.currentTick = compound.getInt("tick");
        this.prevTick = compound.getInt("prevtick");
        this.running = compound.getBoolean("running");
        this.killed = compound.getBoolean("killed");
        this.maxTicks = compound.getInt("maxTicks");
        this.readAdditional(compound.getCompound("additional"));
    }

    public boolean isKilled() {
        return killed;
    }
}
