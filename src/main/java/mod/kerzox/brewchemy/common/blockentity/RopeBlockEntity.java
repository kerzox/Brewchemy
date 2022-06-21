package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.block.rope.RopeConnections;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
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
