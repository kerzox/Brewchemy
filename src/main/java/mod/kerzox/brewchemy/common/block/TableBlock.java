package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.ConnectedModelBlock;
import mod.kerzox.brewchemy.common.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TableBlock extends ConnectedModelBlock {

    public TableBlock(Properties p_49795_) {
        super(p_49795_, "straight");
        this.registerDefaultState(
                this.stateDefinition.any().setValue(STRAIGHT_LINE, StraightLineFormation.SINGLE)
        );
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        VoxelShape shape = Shapes.empty();

        return Shapes.box(0, 0, 0, 1, 1, 1);
    }

    @Override
    protected boolean isValidConnection(BlockState state) {
        return state.getBlock() instanceof TableBlock;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(STRAIGHT_LINE);
        super.createBlockStateDefinition(pBuilder);
    }


}
