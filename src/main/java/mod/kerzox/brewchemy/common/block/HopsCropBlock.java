package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyCropBlock;
import mod.kerzox.brewchemy.common.entity.RopeEntity;
import mod.kerzox.brewchemy.common.util.BrewchemyUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * Hops
 * Crop must have a vertical rope to grow on (they grow as a column think sugar cane, cactus etc)
 * Rope must be structurally supported (ropes handle this)
 * Hops can only grow on farm block or another hop plant
 * Growing
 * Hops have a max height they can grow up to.
 * Hops will stop at the top of the vertical rope where the rope would be tied to the structural rope. (structural rope are horizontal ropes connected to two fence posts)
 * Hops will have a normal growth period where they start of seeds to a full growth crop.
 * Once fully matured they will have a chance to grow vertically. Vertically grown hops will start at a semi mature state.
 * Harvest
 * Hops can be right-clicked to harvest.
 * Shift right click to harvest the entire column of hops.
 */

public class HopsCropBlock extends BrewchemyCropBlock {

    public static final IntegerProperty growthStages = IntegerProperty.create("age", 0, 7);

    public HopsCropBlock(Properties p_52247_) {
        super(p_52247_);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState below = pLevel.getBlockState(pPos.below());
        // block below is valid
        boolean hasValidBlock = (below.getBlock() instanceof FarmBlock) || (below.getBlock() instanceof HopsCropBlock);
        return hasValidBlock;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        boolean valid = false;
        for (RopeEntity rope : pLevel.getEntitiesOfClass(RopeEntity.class, new AABB(pPos))) {
            if (rope.isVertical()) {
                System.out.println("We found a valid rope to grown on");
                valid = true;
                rope.getCropPositions().add(pPos);
            }
        }

        if (!valid) {
            System.out.println("Failed to find a valid rope");
            // destroy i guess
            pLevel.destroyBlock(pPos, true);
        }


    }

    @Override
    public void entityInside(BlockState p_52277_, Level p_52278_, BlockPos p_52279_, Entity entity) {
        super.entityInside(p_52277_, p_52278_, p_52279_, entity);



    }

    @Override
    public void ageCrop(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, BlockState pState, int age) {
        pLevel.setBlock(pPos, pState.setValue(growthStages, age), 2);

        if (age != getMaxAge()) return;
        boolean valid = true;
        for (RopeEntity rope : pLevel.getEntitiesOfClass(RopeEntity.class, new AABB(pPos))) {
            if (!rope.isVertical()) {
                valid = false;
            }
        }

        if (valid) {
            // grow a plant above if there is space
            if (pLevel.getBlockState(pPos.above()).getBlock() instanceof AirBlock) {
                pLevel.setBlockAndUpdate(pPos.above(), this.getStateForAge(2));
            }

        }

    }

    @Override
    public boolean onGrow(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, BlockState pState, int age) {
        return true;
    }

    @Override
    public void harvest(Level level, BlockPos pos, BlockState state, Player pPlayer) {
        if (pPlayer.isShiftKeyDown()) {
            for (RopeEntity rope : level.getEntitiesOfClass(RopeEntity.class, new AABB(pos))) {

                List<BlockPos> temp = new ArrayList<>(rope.getCropPositions());

               if (temp.contains(pos)) {
                   for (BlockPos position : temp) {
                       if (level.getBlockState(position).getBlock() instanceof HopsCropBlock hops) {
                           hops.dropItems(level, position);
                       }
                   }
               }
            }
        } else {
            dropItems(level, pos);
        }

    }

    public void dropItems(Level level, BlockPos pos) {
        ItemStack seedDrop = new ItemStack(BrewchemyUtils.getItemUnsafe("hops_crop_block"), level.random.nextInt(0, 2));
        ItemStack itemDrop = new ItemStack(BrewchemyUtils.getItemUnsafe("hops_item"), level.random.nextInt(1, 3));
        level.setBlockAndUpdate(pos, this.getStateForAge(2));

        ItemEntity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), itemDrop);
        level.addFreshEntity(item);
        if (!seedDrop.isEmpty()) {
            ItemEntity seed = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), seedDrop);
            level.addFreshEntity(seed);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52286_) {
        p_52286_.add(growthStages);
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return growthStages;
    }


}
