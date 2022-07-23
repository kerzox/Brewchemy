package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.block.base.BrewchemyInvisibleBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeBlock;
import mod.kerzox.brewchemy.common.blockentity.BoilKettleBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.KegBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class BoilKettleBlock extends BrewchemyEntityBlock<BoilKettleBlockEntity> {

    private static final BooleanProperty LID = BooleanProperty.create("lid");

    public BoilKettleBlock(RegistryObject<BlockEntityType<BoilKettleBlockEntity>> type, Properties properties) {
        super(type, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LID, true).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    public BlockState closeLid(BlockState state) {
        return state.setValue(LID, true);
    }

    public BlockState openLid(BlockState state) {
        return state.setValue(LID, false);
    }

    public boolean isOpened(BlockState state) {
        return !state.getValue(LID);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LID, HorizontalDirectionalBlock.FACING);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRotation.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(HorizontalDirectionalBlock.FACING, pContext.getHorizontalDirection().getOpposite());
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(pPos.above(), BrewchemyRegistry.Blocks.BOIL_KETTLE_TOP_BLOCK.get().defaultBlockState(), 3);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        return pLevel.getBlockState(pCurrentPos.above()).getBlock() == BrewchemyRegistry.Blocks.BOIL_KETTLE_TOP_BLOCK.get() ? super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos) : Blocks.AIR.defaultBlockState();
    }

    private VoxelShape shapeLidOpen() {
        return Stream.of(
                Block.box(14, 2, 1, 15, 24, 14),
                Block.box(2, 2, 14, 15, 24, 15),
                Block.box(1, 2, 2, 2, 24, 15),
                Block.box(1, 2, 1, 14, 24, 2),
                Block.box(2, 0, 2, 14, 2, 14),
                Block.box(-1, 25, 7, 3, 26, 9),
                Block.box(-6, 24, 1, 8, 25, 15),
                Block.box(5.5, 3, 0, 10.5, 8, 1)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    private VoxelShape shapeLidClosed() {
        return Stream.of(
                Block.box(14, 2, 1, 15, 24, 14),
                Block.box(2, 2, 14, 15, 24, 15),
                Block.box(1, 2, 2, 2, 24, 15),
                Block.box(1, 2, 1, 14, 24, 2),
                Block.box(2, 0, 2, 14, 2, 14),
                Block.box(6, 25, 7, 10, 26, 9),
                Block.box(1, 24, 1, 15, 25, 15),
                Block.box(5.5, 3, 0, 10.5, 8, 1)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return isOpened(pState) ? shapeLidOpen() : shapeLidClosed();
    }

    public static class BoilKettleTop extends BrewchemyInvisibleBlock {

        public BoilKettleTop(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
            if (pLevel.getBlockState(pPos.below()).getBlock() instanceof BoilKettleBlock kettle) {
                return kettle.use(pLevel.getBlockState(pPos.below()), pLevel, pPos.below(), pPlayer, pHand, pHit);
            }
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }

        @Override
        public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
            return pLevel.getBlockState(pPos.below()).getBlock() == BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get();
        }

        @Override
        public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
            return canSurvive(pState, pLevel, pCurrentPos) ? super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos) : Blocks.AIR.defaultBlockState();
        }

        private VoxelShape shapeLidOpen() {
            return Stream.of(
                    Block.box(14, -14, 1, 15, 8, 14),
                    Block.box(2, -14, 14, 15, 8, 15),
                    Block.box(1, -14, 2, 2, 8, 15),
                    Block.box(1, -14, 1, 14, 8, 2),
                    Block.box(2, -16, 2, 14, -14, 14),
                    Block.box(-1, 9, 7, 3, 10, 9),
                    Block.box(-6, 8, 1, 8, 9, 15),
                    Block.box(1, 0, 1, 15, 1, 15),
                    Block.box(5.5, -13, 0, 10.5, -8, 1)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
        }

        private VoxelShape shapeLidClosed() {
            return Stream.of(
                    Block.box(14, -14, 1, 15, 8, 14),
                    Block.box(2, -14, 14, 15, 8, 15),
                    Block.box(1, -14, 2, 2, 8, 15),
                    Block.box(1, -14, 1, 14, 8, 2),
                    Block.box(2, -16, 2, 14, -14, 14),
                    Block.box(6, 9, 7, 10, 10, 9),
                    Block.box(1, 8, 1, 15, 9, 15),
                    Block.box(5.5, -13, 0, 10.5, -8, 1)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
        }



        @Override
        public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
            if (pLevel.getBlockState(pPos.below()).getBlock() instanceof BoilKettleBlock kettle) {
                return kettle.isOpened(pLevel.getBlockState(pPos.below())) ? shapeLidOpen() : shapeLidClosed();
            }
            return Shapes.empty();
        }
    }

}
