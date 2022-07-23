package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyCropBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;

import static mod.kerzox.brewchemy.common.block.rope.RopeBlock.HAS_TRELLIS;

public class BarleyCropBlock extends BrewchemyCropBlock {

    public BarleyCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void harvest(Level level, BlockPos pos, BlockState state) {
        ItemStack drop = new ItemStack(this.asItem(), level.random.nextInt(1, 3));
        level.setBlockAndUpdate(pos, this.defaultBlockState());
        ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), drop);
        level.addFreshEntity(entity);
    }

}
