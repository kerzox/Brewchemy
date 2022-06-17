package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyCropBlock;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class HopsCropBlock extends BrewchemyCropBlock {

    private final int maturationAge = 7;
    private final int extraGrowth = 3;
    public final IntegerProperty growthStages = IntegerProperty.create("age", 0, maturationAge);
    public final VoxelShape[] currentShapeByGrowth = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    public HopsCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return this.growthStages;
    }

    @Override
    protected int getAge(BlockState pState) {
        return pState.getValue(this.getAgeProperty());
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        /* TODO
            Hops must have upwards facing twine to grow as trellis. Needs light level higher than 10.
         */
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return currentShapeByGrowth[pState.getValue(this.getAgeProperty())];
    }

    @Override
    public int getMaxAge() {
        return this.maturationAge;
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        /* TODO
            Twine block will need to have direction. (hops can only grow upwards).
         */
        return pState.is(BrewchemyRegistry.Blocks.TWINE_BLOCK.get());
    }

    @Override
    public @NotNull BlockState getStateForAge(int pAge) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), pAge);
    }

    @Override
    public void growCrops(Level pLevel, BlockPos pPos, BlockState pState) {
        /* TODO
            Continue to grow crops after maturation age to what ever extraGrowth variable is.
         */
        int currentAge = this.getAge(pState) + this.getBonemealAgeIncrease(pLevel);
        int maxAge = this.getMaxAge();
        if (currentAge > maxAge) {
            currentAge = maxAge;
        }

        pLevel.setBlock(pPos, this.getStateForAge(currentAge), 2);


    }
}
