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
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class BrewingKettleBlock extends BrewchemyEntityBlock<BrewingKettleBlockEntity> {


    private Map<Direction, VoxelShape> shapes = createShapes();

    private Map<Direction, VoxelShape> createShapes() {
        Map<Direction, VoxelShape> createdShapes = new HashMap<>();

        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.40625, 0, 1, 0.59375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 1.875, 0.0625, 0.9375, 1.9375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 1.9375, 0.4375, 0.5625, 2, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0, 0, 1, 0.40625, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.1875, 0.40625, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.8125, 0.1875, 0.40625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0, 0.8125, 1, 0.40625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.0625, 0, 0.625, 0.3125, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.0015624999999999112, 0.0625, 0.9375, 1.8764999999999998, 0.9375), BooleanOp.OR);

        createdShapes.put(Direction.NORTH, shape);

        VoxelShape shapeE = Shapes.empty();
        shapeE = Shapes.join(shapeE, Shapes.box(0, 0.40625, 0, 1, 0.59375, 1), BooleanOp.OR);
        shapeE = Shapes.join(shapeE, Shapes.box(0.0625, 1.875, 0.0625, 0.9375, 1.9375, 0.9375), BooleanOp.OR);
        shapeE = Shapes.join(shapeE, Shapes.box(0.4375, 1.921875, 0.4375, 0.5625, 1.984375, 0.5625), BooleanOp.OR);
        shapeE = Shapes.join(shapeE, Shapes.box(0.8125, 0, 0.8125, 1, 0.40625, 1), BooleanOp.OR);
        shapeE = Shapes.join(shapeE, Shapes.box(0.8125, 0, 0, 1, 0.40625, 0.1875), BooleanOp.OR);
        shapeE = Shapes.join(shapeE, Shapes.box(0, 0, 0, 0.1875, 0.40625, 0.1875), BooleanOp.OR);
        shapeE = Shapes.join(shapeE, Shapes.box(0, 0, 0.8125, 0.1875, 0.40625, 1), BooleanOp.OR);
        shapeE = Shapes.join(shapeE, Shapes.box(0.9375, 0.0625, 0.375, 1, 0.3125, 0.625), BooleanOp.OR);
        shapeE = Shapes.join(shapeE, Shapes.box(-0.0625, 0.3125, 0.3125, 0.3125, 0.6875, 0.6875), BooleanOp.OR);
        shapeE = Shapes.join(shapeE, Shapes.box(0.0625, 0.0015624999999999112, 0.0625, 0.9375, 1.8764999999999998, 0.9375), BooleanOp.OR);

        createdShapes.put(Direction.EAST, shapeE);

        VoxelShape shapeS = Shapes.empty();
        shapeS = Shapes.join(shapeS, Shapes.box(0, 0.40625, 0, 1, 0.59375, 1), BooleanOp.OR);
        shapeS = Shapes.join(shapeS, Shapes.box(0.0625, 1.875, 0.0625, 0.9375, 1.9375, 0.9375), BooleanOp.OR);
        shapeS = Shapes.join(shapeS, Shapes.box(0.4375, 1.921875, 0.4375, 0.5625, 1.984375, 0.5625), BooleanOp.OR);
        shapeS = Shapes.join(shapeS, Shapes.box(0, 0, 0.8125, 0.1875, 0.40625, 1), BooleanOp.OR);
        shapeS = Shapes.join(shapeS, Shapes.box(0.8125, 0, 0.8125, 1, 0.40625, 1), BooleanOp.OR);
        shapeS = Shapes.join(shapeS, Shapes.box(0.8125, 0, 0, 1, 0.40625, 0.1875), BooleanOp.OR);
        shapeS = Shapes.join(shapeS, Shapes.box(0, 0, 0, 0.1875, 0.40625, 0.1875), BooleanOp.OR);
        shapeS = Shapes.join(shapeS, Shapes.box(0.375, 0.0625, 0.9375, 0.625, 0.3125, 1), BooleanOp.OR);
        shapeS = Shapes.join(shapeS, Shapes.box(0.3125, 0.3125, -0.0625, 0.6875, 0.6875, 0.3125), BooleanOp.OR);
        shapeS = Shapes.join(shapeS, Shapes.box(0.0625, 0.0015624999999999112, 0.0625, 0.9375, 1.8764999999999998, 0.9375), BooleanOp.OR);

        createdShapes.put(Direction.SOUTH, shapeS);

        VoxelShape shapeW = Shapes.empty();
        shapeW = Shapes.join(shapeW, Shapes.box(0, 0.40625, 0, 1, 0.59375, 1), BooleanOp.OR);
        shapeW = Shapes.join(shapeW, Shapes.box(0.0625, 1.875, 0.0625, 0.9375, 1.9375, 0.9375), BooleanOp.OR);
        shapeW = Shapes.join(shapeW, Shapes.box(0.4375, 1.9375, 0.4375, 0.5625, 2, 0.5625), BooleanOp.OR);
        shapeW = Shapes.join(shapeW, Shapes.box(0, 0, 0, 0.1875, 0.40625, 0.1875), BooleanOp.OR);
        shapeW = Shapes.join(shapeW, Shapes.box(0, 0, 0.8125, 0.1875, 0.40625, 1), BooleanOp.OR);
        shapeW = Shapes.join(shapeW, Shapes.box(0.8125, 0, 0.8125, 1, 0.40625, 1), BooleanOp.OR);
        shapeW = Shapes.join(shapeW, Shapes.box(0.8125, 0, 0, 1, 0.40625, 0.1875), BooleanOp.OR);
        shapeW = Shapes.join(shapeW, Shapes.box(0, 0.0625, 0.375, 0.0625, 0.3125, 0.625), BooleanOp.OR);
        shapeW = Shapes.join(shapeW, Shapes.box(0.6875, 0.3125, 0.3125, 1.0625, 0.6875, 0.6875), BooleanOp.OR);
        shapeW = Shapes.join(shapeW, Shapes.box(0.0625, 0.0015624999999999112, 0.0625, 0.9375, 1.8764999999999998, 0.9375), BooleanOp.OR);
        createdShapes.put(Direction.WEST, shapeW);

        return createdShapes;
    }

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
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return Shapes.box(0.25, 0.25, 0.25, 0.5, 0.5, 0.5);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return shapes.get(state.getValue(HorizontalDirectionalBlock.FACING));
    }
    public static class Top extends BrewchemyBlock {

        private Map<Direction, VoxelShape> shapes = createShapes();

        public VoxelShape closedShape() {
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

        private Map<Direction, VoxelShape> createShapes() {
            Map<Direction, VoxelShape> createdShapes = new HashMap<>();
            int offsetY = 1;
            VoxelShape shape = Shapes.empty();
            shape = Shapes.join(shape, Shapes.box(0, -0.59375, 0, 1, -0.40625, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.0625, 0.875, 0.0625, 0.9375, 0.9375, 0.9375), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.4375, 0.9375, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.8125, -1, 0, 1, -0.59375, 0.1875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, -1, 0, 0.1875, -0.59375, 0.1875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, -1, 0.8125, 0.1875, -0.59375, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.8125, -1, 0.8125, 1, -0.59375, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.375, -0.9375, 0, 0.625, -0.6875, 0.0625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.3125, -0.6875, 0.6875, 0.6875, -0.3125, 1.0625), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.0625, -0.9984375000000001, 0.0625, 0.9375, 0.8764999999999998, 0.9375), BooleanOp.OR);

            createdShapes.put(Direction.NORTH, shape);

            VoxelShape shapeE = Shapes.empty();
            shapeE = Shapes.join(shapeE, Shapes.box(0, -0.59375, -0.015625, 1, -0.40625, 0.984375), BooleanOp.OR);
            shapeE = Shapes.join(shapeE, Shapes.box(0.0625, 0.875, 0.046875, 0.9375, 0.9375, 0.921875), BooleanOp.OR);
            shapeE = Shapes.join(shapeE, Shapes.box(0.4375, 0.9375, 0.421875, 0.5625, 1, 0.546875), BooleanOp.OR);
            shapeE = Shapes.join(shapeE, Shapes.box(0.8125, -1, 0.796875, 1, -0.59375, 0.984375), BooleanOp.OR);
            shapeE = Shapes.join(shapeE, Shapes.box(0.8125, -1, -0.015625, 1, -0.59375, 0.171875), BooleanOp.OR);
            shapeE = Shapes.join(shapeE, Shapes.box(0, -1, -0.015625, 0.1875, -0.59375, 0.171875), BooleanOp.OR);
            shapeE = Shapes.join(shapeE, Shapes.box(0, -1, 0.796875, 0.1875, -0.59375, 0.984375), BooleanOp.OR);
            shapeE = Shapes.join(shapeE, Shapes.box(0.9375, -0.9375, 0.359375, 1, -0.6875, 0.609375), BooleanOp.OR);
            shapeE = Shapes.join(shapeE, Shapes.box(-0.0625, -0.6875, 0.296875, 0.3125, -0.3125, 0.671875), BooleanOp.OR);
            shapeE = Shapes.join(shapeE, Shapes.box(0.0625, -0.9984375000000001, 0.046875, 0.9375, 0.8764999999999998, 0.921875), BooleanOp.OR);

            createdShapes.put(Direction.EAST, shapeE);

            VoxelShape shapeS = Shapes.empty();
            shapeS = Shapes.join(shapeS, Shapes.box(0, -0.59375, 0, 1, -0.40625, 1), BooleanOp.OR);
            shapeS = Shapes.join(shapeS, Shapes.box(0.0625, 0.875, 0.0625, 0.9375, 0.9375, 0.9375), BooleanOp.OR);
            shapeS = Shapes.join(shapeS, Shapes.box(0.4375, 0.9375, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
            shapeS = Shapes.join(shapeS, Shapes.box(0, -1, 0.8125, 0.1875, -0.59375, 1), BooleanOp.OR);
            shapeS = Shapes.join(shapeS, Shapes.box(0.8125, -1, 0.8125, 1, -0.59375, 1), BooleanOp.OR);
            shapeS = Shapes.join(shapeS, Shapes.box(0.8125, -1, 0, 1, -0.59375, 0.1875), BooleanOp.OR);
            shapeS = Shapes.join(shapeS, Shapes.box(0, -1, 0, 0.1875, -0.59375, 0.1875), BooleanOp.OR);
            shapeS = Shapes.join(shapeS, Shapes.box(0.375, -0.9375, 0.9375, 0.625, -0.6875, 1), BooleanOp.OR);
            shapeS = Shapes.join(shapeS, Shapes.box(0.3125, -0.6875, -0.0625, 0.6875, -0.3125, 0.3125), BooleanOp.OR);
            shapeS = Shapes.join(shapeS, Shapes.box(0.0625, -0.9984375000000001, 0.0625, 0.9375, 0.8764999999999998, 0.9375), BooleanOp.OR);

            createdShapes.put(Direction.SOUTH, shapeS);

            VoxelShape shapeW = Shapes.empty();
            shapeW = Shapes.join(shapeW, Shapes.box(0, -0.59375, 0, 1, -0.40625, 1), BooleanOp.OR);
            shapeW = Shapes.join(shapeW, Shapes.box(0.0625, 0.875, 0.0625, 0.9375, 0.9375, 0.9375), BooleanOp.OR);
            shapeW = Shapes.join(shapeW, Shapes.box(0.4375, 0.9375, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
            shapeW = Shapes.join(shapeW, Shapes.box(0, -1, 0, 0.1875, -0.59375, 0.1875), BooleanOp.OR);
            shapeW = Shapes.join(shapeW, Shapes.box(0, -1, 0.8125, 0.1875, -0.59375, 1), BooleanOp.OR);
            shapeW = Shapes.join(shapeW, Shapes.box(0.8125, -1, 0.8125, 1, -0.59375, 1), BooleanOp.OR);
            shapeW = Shapes.join(shapeW, Shapes.box(0.8125, -1, 0, 1, -0.59375, 0.1875), BooleanOp.OR);
            shapeW = Shapes.join(shapeW, Shapes.box(0, -0.9375, 0.375, 0.0625, -0.6875, 0.625), BooleanOp.OR);
            shapeW = Shapes.join(shapeW, Shapes.box(0.6875, -0.6875, 0.3125, 1.0625, -0.3125, 0.6875), BooleanOp.OR);
            shapeW = Shapes.join(shapeW, Shapes.box(0.0625, -0.9984375000000001, 0.0625, 0.9375, 0.8764999999999998, 0.9375), BooleanOp.OR);

            createdShapes.put(Direction.WEST, shapeW);
            return createdShapes;
        }
        public Top(Properties p_49795_) {
            super(p_49795_);
        }

        @Override
        public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {

            if (p_60556_.getBlockState(p_60557_.below()).getBlock() instanceof BrewingKettleBlock) {
                return shapes.get(p_60556_.getBlockState(p_60557_.below()).getValue(HorizontalDirectionalBlock.FACING));
            }

            return Shapes.empty();
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
        public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
            return Shapes.box(0.25, 0.25, 0.25, 0.5, 0.5, 0.5);
        }



    }
}
