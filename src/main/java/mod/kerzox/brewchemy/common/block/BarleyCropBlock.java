package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.block.base.BrewchemyCropBlock;

import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static mod.kerzox.brewchemy.common.block.rope.RopeBlock.HAS_TRELLIS;

public class BarleyCropBlock extends BrewchemyCropBlock {

    public BarleyCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void harvest(Level level, BlockPos pos, BlockState state) {
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
