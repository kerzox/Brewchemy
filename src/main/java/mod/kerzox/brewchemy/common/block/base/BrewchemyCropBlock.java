package mod.kerzox.brewchemy.common.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BrewchemyCropBlock extends CropBlock {

    public BrewchemyCropBlock(Properties properties) {
        super(properties);
    }

    protected void harvest(Level level, BlockPos pos, BlockState state) {

    }

}
