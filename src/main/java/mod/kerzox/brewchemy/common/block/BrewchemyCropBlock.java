package mod.kerzox.brewchemy.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;

public abstract class BrewchemyCropBlock extends CropBlock {

    protected int maxAge = 7;

    public BrewchemyCropBlock(Properties p_52247_) {
        super(p_52247_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52286_) {
        p_52286_.add(AGE);
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return super.getAgeProperty();
    }

    @Override
    public void growCrops(Level p_52264_, BlockPos p_52265_, BlockState p_52266_) {
        super.growCrops(p_52264_, p_52265_, p_52266_);
    }

    @Override
    public int getMaxAge() {
        return this.maxAge;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (isHarvestable(pState)) {
            if (!pLevel.isClientSide) {
                harvest(pLevel, pPos, pState, pPlayer);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isAreaLoaded(pPos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (pLevel.getRawBrightness(pPos, 0) >= 9) {
            int i = this.getAge(pState);
            if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(this, pLevel, pPos);
                if (ForgeHooks.onCropsGrowPre(pLevel, pPos, pState, pRandom.nextInt((int)(25.0F / f) + 1) == 0)) {
                    if (onGrow(pLevel, pPos, pRandom, pState, i + 1)) {
                        ageCrop(pLevel, pPos, pRandom, pState, i + 1);
                        ForgeHooks.onCropsGrowPost(pLevel, pPos, pState);
                    }
                }
            } else {
                if (onGrow(pLevel, pPos, pRandom, pState, i)) {
                    ageCrop(pLevel, pPos, pRandom, pState, i);
                    ForgeHooks.onCropsGrowPost(pLevel, pPos, pState);
                }
            }
        }
    }

    public boolean onGrow(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, BlockState pState, int age) {
        return true;
    }

    public void ageCrop(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, BlockState pState, int age) {
        pLevel.setBlock(pPos, pState.setValue(getAgeProperty(), age), 2);
    }

    public boolean isHarvestable(BlockState pState) {
        return getAge(pState) == getMaxAge();
    }

    public abstract void harvest(Level level, BlockPos pos, BlockState state, Player pPlayer);

}
