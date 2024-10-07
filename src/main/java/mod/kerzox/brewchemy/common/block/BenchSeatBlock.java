package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.ConnectedModelBlock;
import mod.kerzox.brewchemy.common.data.SeatHandler;
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

public class BenchSeatBlock extends ConnectedModelBlock {

    public BenchSeatBlock(Properties p_49795_) {
        super(p_49795_, "straight");
        this.registerDefaultState(
                this.stateDefinition.any().setValue(STRAIGHT_LINE, StraightLineFormation.SINGLE)
        );
    }

    @Override
    public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, p_60567_, p_60568_, p_60569_, p_60570_);
        SeatHandler.addSeatHere(p_60568_, p_60567_);
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        return SeatHandler.interactWithSeat(p_60504_, p_60506_, p_60505_, p_60507_, p_60508_);
    }

    @Override
    public void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
        SeatHandler.removeThis(p_60517_);
        super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.box(0, 0, 0, 1, 7/16f, 1);

        return shape;
    }

    @Override
    protected boolean isValidConnection(BlockState state) {
        return state.getBlock() instanceof BenchSeatBlock;
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
