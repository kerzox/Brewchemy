package mod.kerzox.brewchemy.common.block.rope;

import mod.kerzox.brewchemy.common.block.HopsCropBlock;
import mod.kerzox.brewchemy.common.block.RopeTiedFenceBlock;
import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.RopeBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.RopeTiedFenceBlockEntity;
import mod.kerzox.brewchemy.common.util.IRopeConnectable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.BlockEntities.ROPE;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class RopeBlock extends BrewchemyEntityBlock<RopeBlockEntity> implements IRopeConnectable {

    public static final EnumProperty<RopeConnections> NORTH = EnumProperty.create("north_rope", RopeConnections.class);
    public static final EnumProperty<RopeConnections> SOUTH = EnumProperty.create("south_rope", RopeConnections.class);
    public static final EnumProperty<RopeConnections> WEST = EnumProperty.create("west_rope", RopeConnections.class);
    public static final EnumProperty<RopeConnections> EAST = EnumProperty.create("east_rope", RopeConnections.class);
    public static final EnumProperty<RopeConnections> UP = EnumProperty.create("up_rope", RopeConnections.class);
    public static final EnumProperty<RopeConnections> DOWN = EnumProperty.create("down_rope", RopeConnections.class);

    public static final BooleanProperty HAS_TRELLIS = BooleanProperty.create("trellis");

    private HashMap<BlockState, VoxelShape> cache = new HashMap<>();
    private VoxelShape CORE = Shapes.or(Block.box(7, 7, 7, 9, 9, 9));
    private VoxelShape[] allValidSides = new VoxelShape[]{
            Shapes.or(Block.box(7, 0, 7, 9, 9, 9)), // down 0
            Shapes.or(Block.box(7, 7, 7, 9, 9+7, 9)), // up 1
            Shapes.or(Block.box(7, 7, 0, 9, 9, 9)), // north 2
            Shapes.or(Block.box(7, 7, 7, 9, 9, 9+7)), // south 3
            Shapes.or(Block.box(0, 7, 7, 9, 9, 9)), // west 4
            Shapes.or(Block.box(7, 7, 7, 9+7, 9, 9)), // east 5
    };

    public static final Map<Direction, EnumProperty<RopeConnections>> SIDES = Map.of(
            Direction.NORTH, NORTH,
            Direction.SOUTH, SOUTH,
            Direction.WEST, WEST,
            Direction.EAST, EAST,
            Direction.UP, UP,
            Direction.DOWN, DOWN);

    public RopeBlock(Properties properties) {
        super(ROPE.getType(), properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, RopeConnections.NONE)
                .setValue(EAST, RopeConnections.NONE)
                .setValue(SOUTH, RopeConnections.NONE)
                .setValue(WEST, RopeConnections.NONE)
                .setValue(UP, RopeConnections.NONE)
                .setValue(DOWN, RopeConnections.NONE));
        initializeShapeCache();
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return cache.get(pState);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return attachValidToNeighbours(state, pContext.getLevel(), pContext.getClickedPos(), false);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        BlockState state = super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
        initializeShapeCache();
        return attachValidToNeighbours(state, pLevel, pCurrentPos, false);
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        if (pUseContext.getItemInHand().getItem() == BrewchemyRegistry.Blocks.HOPS_CROP_BLOCK.get().asItem()) {
            return true;
        }
        return super.canBeReplaced(pState, pUseContext);
    }

    private void initializeShapeCache() {
        for (BlockState blockState : this.stateDefinition.getPossibleStates()) {
            VoxelShape combinedShape = CORE;
            if (blockState.getValue(UP) == RopeConnections.CONNECTED) {
                combinedShape = Shapes.or(combinedShape, allValidSides[1]);
            }
            if (blockState.getValue(DOWN) == RopeConnections.CONNECTED) {
                combinedShape = Shapes.or(combinedShape, allValidSides[0]);
            }
            if (blockState.getValue(WEST) == RopeConnections.CONNECTED) {
                combinedShape = Shapes.or(combinedShape, allValidSides[4]);
            }
            if (blockState.getValue(EAST) == RopeConnections.CONNECTED) {
                combinedShape = Shapes.or(combinedShape, allValidSides[5]);
            }
            if (blockState.getValue(NORTH) == RopeConnections.CONNECTED) {
                combinedShape = Shapes.or(combinedShape, allValidSides[2]);
            }
            if (blockState.getValue(SOUTH) == RopeConnections.CONNECTED) {
                combinedShape = Shapes.or(combinedShape, allValidSides[3]);
            }
            cache.put(blockState, combinedShape);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Override
    public boolean canConnectTo(BlockState state, Direction connectingFrom) {
        return true;
    }

    public static class Item extends BlockItem {

        public Item(Properties pProperties) {
            super(BrewchemyRegistry.Blocks.ROPE_BLOCK.get(), pProperties);
        }

        @Override
        public InteractionResult useOn(UseOnContext pContext) {
            Block block = pContext.getLevel().getBlockState(pContext.getClickedPos()).getBlock();
            if (block instanceof FenceBlock fence) {
                if (block instanceof RopeTiedFenceBlock ropeTiedFenceBlock){
                    return super.useOn(pContext);
                }
                BlockState oldState = pContext.getLevel().getBlockState(pContext.getClickedPos());
                pContext.getLevel().setBlockAndUpdate(pContext.getClickedPos(), BrewchemyRegistry.Blocks.ROPE_FENCE_BLOCK.get().defaultBlockState());
                if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof RopeTiedFenceBlockEntity rope) {
                    rope.setFenceToMimic(oldState);
                    pContext.getItemInHand().shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
            return super.useOn(pContext);
        }
    }
}
