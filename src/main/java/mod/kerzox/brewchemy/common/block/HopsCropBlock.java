package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyCropBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeConnections;
import mod.kerzox.brewchemy.common.blockentity.RopeBlockEntity;
import mod.kerzox.brewchemy.common.util.IRopeConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static mod.kerzox.brewchemy.common.block.rope.RopeBlock.DOWN;
import static mod.kerzox.brewchemy.common.block.rope.RopeBlock.HAS_TRELLIS;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class HopsCropBlock extends BrewchemyCropBlock implements IRopeConnectable {

    private static final int maturationAge = 7;
    private final int extraGrowth = 3;
    private static final IntegerProperty growthStages = IntegerProperty.create("age", 0, maturationAge);
    public final VoxelShape[] currentShapeByGrowth = new VoxelShape[]{
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D),
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D),
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D),
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 8.0D, 11.0D),
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D),
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 12.0D, 11.0D),
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 14.0D, 11.0D),
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D)};



    public HopsCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HAS_TRELLIS, false));
    }

    private boolean isSupported(BlockState state) {
        return state.getValue(HAS_TRELLIS);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return growthStages;
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
        if (!isSupported(pState)) return false;
        if (!(pLevel.getBlockState(pPos.below()).is(Blocks.FARMLAND) || (pLevel.getBlockState(pPos.below()).is(this)))) {
            return false;
        }
        return true;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            if (pLevel.getBlockState(pPos).getBlock() instanceof RopeBlock) {
                pState.setValue(HAS_TRELLIS, true);
            }
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = super.getStateForPlacement(context);
        if (level.getBlockState(pos).getBlock() instanceof RopeBlock) {
            return state.setValue(HAS_TRELLIS, true);
        }
        return state;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return currentShapeByGrowth[pState.getValue(this.getAgeProperty())];
    }

    private boolean isFruiting(BlockState state) {
        return getAge(state) == getMaxAge();
    }

    @Override
    public int getMaxAge() {
        return maturationAge;
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(growthStages, HAS_TRELLIS);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel p_221051_, BlockPos p_221052_, RandomSource p_221053_) {
        if (!p_221051_.isAreaLoaded(p_221052_, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (p_221051_.getRawBrightness(p_221052_, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(this, p_221051_, p_221052_);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_221051_, p_221052_, state, p_221053_.nextInt((int)(25.0F / f) + 1) == 0)) {
                    p_221051_.setBlock(p_221052_, state.setValue(growthStages, i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_221051_, p_221052_, state);
                }
            } else if (i == this.getMaxAge()) {
                float f = getGrowthSpeed(this, p_221051_, p_221052_);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_221051_, p_221052_, state, p_221053_.nextInt((int)(25.0F / f) + 1) == 0)) {
                    p_221051_.setBlock(p_221052_, state.setValue(growthStages, i), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_221051_, p_221052_, state);
                }
            }
        }
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

        pLevel.setBlock(pPos, pState.setValue(growthStages, currentAge), 2);

        if (currentAge != maxAge) return;

        // add a new plant above
        if (pLevel.getBlockEntity(pPos.above()) instanceof RopeBlockEntity ropeBlockEntity) {
            if (!ropeBlockEntity.hasHorizontalConnections()) growBine(pPos.above(), pLevel);
        }
    }

    @Override
    protected void harvest(Level level, BlockPos pos, BlockState state) {
        ItemStack drop = new ItemStack(this.asItem(), level.random.nextInt(1, 3));
        level.setBlockAndUpdate(pos, this.getStateForAge(4).setValue(HAS_TRELLIS, true));
        ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), drop);
        level.addFreshEntity(entity);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (isFruiting(pState)) {
            if (!pLevel.isClientSide) {
                harvest(pLevel, pPos, pState);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    private void growBine(BlockPos pos, Level pLevel) {
        pLevel.setBlockAndUpdate(pos, this.getStateForAge(2).setValue(HAS_TRELLIS, true));
    }


    @Override
    public boolean canConnectTo(BlockState state, Direction connectingFrom) {
        return connectingFrom == Direction.UP || connectingFrom == Direction.DOWN;
    }
}
