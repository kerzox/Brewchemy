package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SupportStickEntityBlock extends BrewchemyBlockEntity implements IServerTickable {

    public SupportStickEntityBlock(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.SUPPORT_STICK.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void onServer() {

    }
}
