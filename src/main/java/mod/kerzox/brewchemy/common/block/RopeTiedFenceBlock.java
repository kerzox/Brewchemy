package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeConnections;
import mod.kerzox.brewchemy.common.blockentity.RopeTiedFenceBlockEntity;
import mod.kerzox.brewchemy.common.util.IRopeConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class RopeTiedFenceBlock extends FenceBlock implements EntityBlock, IRopeConnectable {

    public RopeTiedFenceBlock(Properties pos) {
        super(pos);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(RopeBlock.NORTH, RopeConnections.NONE)
                .setValue(RopeBlock.EAST, RopeConnections.NONE)
                .setValue(RopeBlock.SOUTH, RopeConnections.NONE)
                .setValue(RopeBlock.WEST, RopeConnections.NONE));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            if (pLevel.getBlockEntity(pPos) instanceof RopeTiedFenceBlockEntity entity) {
                if (!pPlayer.getMainHandItem().isEmpty() && pPlayer.getMainHandItem().getItem() instanceof BlockItem block)
                    if (block.getBlock() instanceof FenceBlock fence) {
                        if (fence.defaultBlockState() == entity.getMimic()) return InteractionResult.PASS;
                        else entity.setFenceToMimic(fence.defaultBlockState());
                    }
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return attachValidToNeighbours(pState, pLevel, pCurrentPos, true);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return attachValidToNeighbours(pContext.getLevel().getBlockState(pContext.getClickedPos()), pContext.getLevel(), pContext.getClickedPos(), true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(RopeBlock.NORTH, RopeBlock.SOUTH, RopeBlock.EAST, RopeBlock.WEST, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RopeTiedFenceBlockEntity(pPos, pState);
    }

    @Override
    public boolean canConnectTo(BlockState state, Direction connectingFrom) {
        return connectingFrom.getAxis() == Direction.Axis.X || connectingFrom.getAxis() == Direction.Axis.Z;
    }
}
