package mod.kerzox.brewchemy.common.block;


import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.WarehouseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class WarehouseBlock extends BrewchemyEntityBlock<WarehouseBlockEntity> {

    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");

    public WarehouseBlock(RegistryObject<BlockEntityType<WarehouseBlockEntity>> type, Properties properties) {
        super(type, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(INVISIBLE, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    public RenderShape getRenderShape(BlockState p_48758_) {
        return shouldRenderInvisible(p_48758_) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shouldRenderInvisible(pState) ? Shapes.empty() : Shapes.block();
    }

    public boolean shouldRenderInvisible(BlockState pState) {
        return pState.getValue(INVISIBLE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(INVISIBLE, HorizontalDirectionalBlock.FACING);
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
}
