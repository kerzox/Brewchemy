package mod.kerzox.brewchemy.common.blockentity;

import com.mojang.math.Quaternion;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.util.IClientTickable;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class MillstoneCrankBlockEntity extends BrewchemyBlockEntity implements IServerTickable, IClientTickable {

    protected MillStoneBlockEntity millstone;
    protected int progress = 0;
    protected int prevProgress = 0;
    public int inUse;
    private Quaternion rotation;
    private Quaternion prevRotation;
    private boolean clicked;

    private long prevTime;

    public MillstoneCrankBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.MILL_STONE_CRANK.get(), pWorldPosition, pBlockState);
    }

    public void setMillstone(MillStoneBlockEntity millstone) {
        this.millstone = millstone;
    }

    public MillStoneBlockEntity getMillstone() {
        return millstone;
    }

    public long getPrevTime() {
        return prevTime;
    }

    @Override
    public void onServer() {
        if (inUse > 0) {
            inUse--;
            if (inUse == 0) {
                clicked = false;
            }
            if (level.getBlockEntity(getBlockPos().below()) instanceof MillStoneBlockEntity mill) {
                mill.updateProgress();
            }
        }
    }

    @Override
    public void onClient() {
        if (inUse > 0) {
            inUse--;
            if (inUse == 0) {
                clicked = false;
                prevTime = level.getGameTime();
            }
        }
    }

    public int getInUse() {
        return inUse;
    }

    public InteractionResult action(Player pPlayer) {
        inUse = 4;
        clicked = true;
        syncBlockEntity();
        return InteractionResult.CONSUME;
    }

    @Override
    protected void write(CompoundTag pTag) {
        if (millstone != null) pTag.put("millstone_pos", NbtUtils.writeBlockPos(millstone.getBlockPos()));
        pTag.putInt("progress", this.progress);
    }

    @Override
    protected void read(CompoundTag pTag) {
        if (level != null) {
            if (level.getBlockEntity(NbtUtils.readBlockPos(pTag.getCompound("millstone_pos"))) instanceof MillStoneBlockEntity mill) {
                setMillstone(mill);
            }
        }
        this.progress = pTag.getInt("progress");
        this.inUse = pTag.getInt("use");
        this.clicked = pTag.getBoolean("clicked");
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        tag.putInt("progress", this.progress);
        tag.putInt("use", this.inUse);
        tag.putBoolean("clicked", this.clicked);
    }

    public int getProgress() {
        return progress;
    }

    public int getPrevProgress() {
        return prevProgress;
    }

    public boolean clicked() {
        return clicked;
    }

    public void setPrevProgress(int prevProgress) {
        this.prevProgress = prevProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    // these are only client sided

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setPrevRotation(Quaternion prevRotation) {
        this.prevRotation = prevRotation;
    }

    public Quaternion getPrevRotation() {
        return prevRotation;
    }
}
