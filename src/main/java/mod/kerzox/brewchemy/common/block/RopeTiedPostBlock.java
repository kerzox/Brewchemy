package mod.kerzox.brewchemy.common.block;


import mod.kerzox.brewchemy.common.blockentity.RopeTiedPost;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RopeTiedPostBlock extends FenceBlock implements EntityBlock {

    public RopeTiedPostBlock(Properties p_53302_) {
        super(p_53302_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RopeTiedPost(blockPos, blockState);
    }
}
