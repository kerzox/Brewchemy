package mod.kerzox.brewchemy.common.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class BrewchemyCropBlock extends CropBlock {

    public BrewchemyCropBlock(Properties properties) {
        super(properties);
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

    public boolean isHarvestable(BlockState pState) {
        return getAge(pState) == getMaxAge();
    }

    public abstract void harvest(Level level, BlockPos pos, BlockState state, Player pPlayer);

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isAreaLoaded(pPos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (pLevel.getRawBrightness(pPos, 0) >= 9) {
            int i = this.getAge(pState);
            if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(this, pLevel, pPos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(pLevel, pPos, pState, pRandom.nextInt((int)(25.0F / f) + 1) == 0)) {
                    if (onGrow(pLevel, pPos, pRandom, pState, i + 1)) {
                        ageCrop(pLevel, pPos, pRandom, pState, i + 1);
                        net.minecraftforge.common.ForgeHooks.onCropsGrowPost(pLevel, pPos, pState);
                    }
                }
            } else {
                if (onGrow(pLevel, pPos, pRandom, pState, i)) {
                    ageCrop(pLevel, pPos, pRandom, pState, i);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(pLevel, pPos, pState);
                }
            }
        }
    }

    public boolean onGrow(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, BlockState pState, int age) {
        return true;
    }

    public void ageCrop(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, BlockState pState, int age) {

    }
}
