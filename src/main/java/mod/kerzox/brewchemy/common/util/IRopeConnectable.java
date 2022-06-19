package mod.kerzox.brewchemy.common.util;

import mod.kerzox.brewchemy.common.block.rope.RopeConnections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.HashMap;
import java.util.Map;

import static mod.kerzox.brewchemy.common.block.rope.RopeBlock.*;

public interface IRopeConnectable {


    boolean canConnectTo(Direction connectingFrom);

    default BlockState attachValidToNeighbours(BlockState ourState, LevelAccessor level, BlockPos pos) {
        HashMap<Direction, RopeConnections> newSides = new HashMap<>(Map.of(
                Direction.NORTH, RopeConnections.NONE,
                Direction.SOUTH, RopeConnections.NONE,
                Direction.EAST, RopeConnections.NONE,
                Direction.WEST, RopeConnections.NONE,
                Direction.UP, RopeConnections.NONE,
                Direction.DOWN, RopeConnections.NONE));
        for (Direction direction : Direction.values()) {
            BlockState state = level.getBlockState(pos.relative(direction));
            if (state.getBlock() instanceof IRopeConnectable connectable) {
                if (connectable.canConnectTo(direction.getOpposite()))
                newSides.put(direction, RopeConnections.CONNECTED);
            }
        }
        BlockState test = ourState
                .setValue(NORTH, newSides.get(Direction.NORTH))
                .setValue(EAST, newSides.get(Direction.EAST))
                .setValue(SOUTH, newSides.get(Direction.SOUTH))
                .setValue(WEST, newSides.get(Direction.WEST))
                .setValue(UP, newSides.get(Direction.UP))
                .setValue(DOWN, newSides.get(Direction.DOWN));

        return test;
    }

    default BlockState attachValidToNeighbours(BlockState ourState, Level level, BlockPos pos) {
        HashMap<Direction, RopeConnections> newSides = new HashMap<>(Map.of(
                Direction.NORTH, RopeConnections.NONE,
                Direction.SOUTH, RopeConnections.NONE,
                Direction.EAST, RopeConnections.NONE,
                Direction.WEST, RopeConnections.NONE,
                Direction.UP, RopeConnections.NONE,
                Direction.DOWN, RopeConnections.NONE));
        for (Direction direction : Direction.values()) {
            BlockState state = level.getBlockState(pos.relative(direction));
            if (state.getBlock() instanceof IRopeConnectable connectable) {
                newSides.put(direction, RopeConnections.CONNECTED);
            }
        }
        BlockState test = ourState
                .setValue(NORTH, newSides.get(Direction.NORTH))
                .setValue(EAST, newSides.get(Direction.EAST))
                .setValue(SOUTH, newSides.get(Direction.SOUTH))
                .setValue(WEST, newSides.get(Direction.WEST))
                .setValue(UP, newSides.get(Direction.UP))
                .setValue(DOWN, newSides.get(Direction.DOWN));

        return test;
    }

//
//    default HashMap<Direction, TwineBlock.RopeConnection> findConnections(Level level, BlockPos pos) {
//
//        HashMap<Direction, TwineBlock.RopeConnection> connections = new HashMap<>(Map.of(
//                Direction.NORTH, TwineBlock.RopeConnection.NONE,
//                Direction.SOUTH, TwineBlock.RopeConnection.NONE,
//                Direction.WEST, TwineBlock.RopeConnection.NONE,
//                Direction.EAST, TwineBlock.RopeConnection.NONE,
//                Direction.UP, TwineBlock.RopeConnection.NONE,
//                Direction.DOWN, TwineBlock.RopeConnection.NONE));
//
//        for (Direction direction : Direction.values()) {
//            BlockState state = level.getBlockState(pos.relative(direction));
//            if (state instanceof ITwineConnectable connectable) {
//                connections.put(direction, TwineBlock.RopeConnection.CONNECTED);
//            }
//        }
//
//        return connections;
//    }

}
