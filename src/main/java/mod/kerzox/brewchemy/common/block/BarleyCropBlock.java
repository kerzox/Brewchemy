package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyCropBlock;

import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BarleyCropBlock extends BrewchemyCropBlock {

    public BarleyCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void harvest(Level level, BlockPos pos, BlockState state, Player pPlayer) {
        ItemStack seedDrop = new ItemStack(BrewchemyRegistry.Blocks.BARLEY_CROP_BLOCK.get(), level.random.nextInt(0, 2));
        ItemStack itemDrop = new ItemStack(BrewchemyRegistry.Items.BARLEY_ITEM.get() , 1);
        level.setBlockAndUpdate(pos, this.defaultBlockState());

        ItemEntity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), itemDrop);
        level.addFreshEntity(item);
        if (!seedDrop.isEmpty()) {
            ItemEntity seed = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), seedDrop);
            level.addFreshEntity(seed);
        }
    }

}
