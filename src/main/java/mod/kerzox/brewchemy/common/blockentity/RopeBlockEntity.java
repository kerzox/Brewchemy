package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.block.HopsCropBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeConnections;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static mod.kerzox.brewchemy.common.block.rope.RopeBlock.*;

public class RopeBlockEntity extends BrewchemyBlockEntity {

    public RopeBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.ROPE.get(), pWorldPosition, pBlockState);
    }

    public boolean hasHorizontalConnections() {
        BlockState state = getBlockState();
        return state.getValue(WEST) == RopeConnections.CONNECTED
                || state.getValue(EAST) == RopeConnections.CONNECTED
                || state.getValue(NORTH) == RopeConnections.CONNECTED
                || state.getValue(SOUTH) == RopeConnections.CONNECTED;
    }

    public static boolean isStable(Level level, BlockPos startingPos) {
        RopeBlockEntity entity = findRope(level, startingPos);
        if (entity == null) return false;
        RopeBlockEntity topRope = entity.getTopMostRope();
        if (!topRope.hasHorizontalConnections()) return false;
        int fences = 0;

        if (level.getBlockEntity(topRope.getBlockPos().west()) instanceof RopeTiedFenceBlockEntity fence) {
            fences++;
        }
        if (level.getBlockEntity(topRope.getBlockPos().east()) instanceof RopeTiedFenceBlockEntity fence) {
            fences++;
        }
        if (level.getBlockEntity(topRope.getBlockPos().north()) instanceof RopeTiedFenceBlockEntity fence) {
            fences++;
        }
        if (level.getBlockEntity(topRope.getBlockPos().south()) instanceof RopeTiedFenceBlockEntity fence) {
            fences++;
        }

        return fences >= 2;
    }

    public static RopeBlockEntity findRope(Level level, BlockPos pos) {
        int steps = 1;
        while (true) {
            BlockEntity be = level.getBlockEntity(pos.above(steps));
            if (be == null) {
                if (!(level.getBlockState(pos).getBlock() instanceof HopsCropBlock hop)) {
                    break;
                }
            }
            if (be instanceof RopeBlockEntity ropeBlockEntity) {
                return ropeBlockEntity;
            }
            steps++;
        }
        return null;
    }

    public RopeBlockEntity getTopMostRope() {
        int steps = 0;
        while (level.getBlockEntity(getBlockPos().above(steps)) instanceof RopeBlockEntity rope) {
            steps++;
        }
        return (RopeBlockEntity) level.getBlockEntity(getBlockPos().above(steps - 1));
    }

    public int getHeight() {
        int count;
        int steps = 0;
        BlockPos bottom = null;
        BlockPos top = null;
        while (level.getBlockEntity(getBlockPos().below(steps)) instanceof RopeBlockEntity rope) {
            steps++;
        }
        bottom = level.getBlockEntity(getBlockPos().below(steps)).getBlockPos();
        steps = 0;
        while (level.getBlockEntity(getBlockPos().above(steps)) instanceof RopeBlockEntity rope) {
            steps++;
        }
        top = level.getBlockEntity(getBlockPos().above(steps)).getBlockPos();
        count = bottom.distManhattan(top);
        return count;

    }



}
