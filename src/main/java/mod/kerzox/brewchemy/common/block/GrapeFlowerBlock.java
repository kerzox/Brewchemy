package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyCropBlock;
import mod.kerzox.brewchemy.common.blockentity.RopeBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import static mod.kerzox.brewchemy.common.block.rope.RopeBlock.HAS_TRELLIS;

public class GrapeFlowerBlock extends BrewchemyCropBlock {

    private static final int maturationAge = 3;
    private static final IntegerProperty GROWTH_STAGES = IntegerProperty.create("age", 0, maturationAge);

    public GrapeFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(GROWTH_STAGES, HAS_TRELLIS);
    }


    @Override
    public IntegerProperty getAgeProperty() {
        return GROWTH_STAGES;
    }

    @Override
    public int getMaxAge() {
        return maturationAge;
    }

    @Override
    protected int getAge(BlockState pState) {
        return pState.getValue(this.getAgeProperty());
    }


    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public void harvest(Level level, BlockPos pos, BlockState state, Player pPlayer) {
        ItemStack drop = new ItemStack(BrewchemyRegistry.Items.GRAPE_ITEM.get(), level.random.nextInt(1, 3));
        level.setBlockAndUpdate(pos, this.getStateForAge(4).setValue(HAS_TRELLIS, true));
        ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), drop);
        level.addFreshEntity(entity);
    }


    public static class GrapeTrunkBlock extends BrewchemyCropBlock {

        private static final int maturationAge = 2;
        private static final IntegerProperty GROWTH_STAGES = IntegerProperty.create("age", 0, maturationAge);

        public GrapeTrunkBlock(Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any());
        }

        @Override
        public IntegerProperty getAgeProperty() {
            return GROWTH_STAGES;
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
            pBuilder.add(GROWTH_STAGES, HAS_TRELLIS);
        }

        @Override
        protected int getAge(BlockState pState) {
            return pState.getValue(this.getAgeProperty());
        }


        @Override
        public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {

            for (int i = 1; i < maturationAge + 1; i++) {
                if (pLevel.getBlockEntity(pPos.above(i)) instanceof RopeBlockEntity) {
                    return i != 1;
                }
            }

            // we didn't find rope check if we are below another trunk or flower
            return pLevel.getBlockState(pPos.above()).getBlock() instanceof GrapeTrunkBlock || pLevel.getBlockState(pPos.above()).getBlock() instanceof GrapeFlowerBlock;
        }

        @Override
        public boolean onGrow(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, BlockState pState, int age) {
            return canSurvive(pState, pLevel, pPos);
        }

        @Override
        public void ageCrop(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, BlockState currentState, int age) {

            if (maturationAge != age) {
                // we age up the trunk
                pLevel.setBlock(pPos, currentState.setValue(GROWTH_STAGES, age), 2);
                return;
            }

            // we try to grow a trunk above us or a flower block

            if (pLevel.getBlockEntity(pPos.above()) instanceof RopeBlockEntity rope) {
                if (rope.hasHorizontalConnections()) {
                    pLevel.setBlockAndUpdate(pPos.above(), BrewchemyRegistry.Blocks.GRAPE_FLOWER_BLOCK.get().defaultBlockState());
                } else pLevel.setBlockAndUpdate(pPos.above(), BrewchemyRegistry.Blocks.GRAPE_TRUNK_BLOCK.get().defaultBlockState());
            }
        }

        @Override
        public int getMaxAge() {
            return maturationAge;
        }


        @Override
        public void harvest(Level level, BlockPos pos, BlockState state, Player pPlayer) {
            // do nothing
            pPlayer.sendSystemMessage(Component.literal("Careful you might get a splinter"));
        }
    }

}
