package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyBlock;
import mod.kerzox.brewchemy.common.block.base.BrewchemyDirectionalBlock;
import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.BrewingKettleBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

public class BrewingKettleBlock extends BrewchemyEntityBlock<BrewingKettleBlockEntity> {


    private VoxelShape[] shapes = new VoxelShape[] {
            closedShape(),
            Shapes.block()
    };

    public BrewingKettleBlock(RegistryObject<BlockEntityType<BrewingKettleBlockEntity>> type, Properties p_49795_) {
        super(type, p_49795_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos p_60527_) {
        return levelReader.getBlockState(p_60527_.above()).getBlock() instanceof BrewingKettleBlock.Top;
    }

    @Override
    public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
        return canSurvive(p_60541_, p_60544_, p_60545_) ? super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_) : Blocks.AIR.defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity pPlacer, ItemStack pStack) {

        if (level.getBlockState(pos.above()).getBlock() instanceof AirBlock) {
            createKettle(level,  pos.above());
        }

        super.setPlacedBy(level, pos, state, pPlacer, pStack);
    }

    private static void createKettle(Level level, BlockPos pos) {
        BlockState state = BrewchemyRegistry.Blocks.BREWING_KETTLE_TOP_BLOCK.get().defaultBlockState();
        level.setBlockAndUpdate(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return super.getRenderShape(p_60550_);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return closedShape();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
        return super.getCollisionShape(p_60572_, p_60573_, p_60574_, p_60575_);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return Shapes.empty();
    }

    public VoxelShape closedShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.5, 0, 1, 0.6875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 1.875, 0.0625, 0.9375, 1.9375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 1.9375, 0.4375, 0.5625, 2, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0, 0, 1, 0.5, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.1875, 0.5, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.8125, 0.1875, 0.5, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0, 0.8125, 1, 0.5, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0, 0.625, 0.4375, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.375, 0.6875, 0.6875, 0.75, 1.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.3125, 0.0625, 0.9375, 1.875, 0.9375), BooleanOp.OR);

        return shape;
    }

    public static class Top extends BrewchemyBlock {


        public Top(Properties p_49795_) {
            super(p_49795_);
        }

        @Override
        public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos p_60527_) {
            return levelReader.getBlockState(p_60527_.below()).getBlock() instanceof BrewingKettleBlock;
        }

        @Override
        public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
            return canSurvive(p_60541_, p_60544_, p_60545_) ? super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_) : Blocks.AIR.defaultBlockState();
        }

        @Override
        public VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
            return super.getCollisionShape(p_60572_, p_60573_, p_60574_, p_60575_);
        }

        @Override
        public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
            return Shapes.empty();
        }


        @Override
        public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
            VoxelShape shape = Shapes.empty();
            shape = Shapes.join(shape, Shapes.box(0, -0.5, 0, 1, -0.3125, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.0625, 0.875, 0.0625, 0.9375, 0.9375, 0.9375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.4375, 0.9375, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.8125, -1, 0, 1, -0.5, 0.1875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, -1, 0, 0.1875, -0.5, 0.1875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, -1, 0.8125, 0.1875, -0.5, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.8125, -1, 0.8125, 1, -0.5, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.375, -0.8125, 0, 0.625, -0.5625, 0.0625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.3125, -0.625, 0.6875, 0.6875, -0.25, 1.0625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.0625, -0.6875, 0.0625, 0.9375, 0.875, 0.9375), BooleanOp.OR);

            return shape;
        }

    }
}
