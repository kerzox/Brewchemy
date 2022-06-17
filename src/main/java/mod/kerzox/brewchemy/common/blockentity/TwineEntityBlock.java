package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TwineEntityBlock extends BrewchemyBlockEntity implements IServerTickable {

    public TwineEntityBlock(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.TWINE.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void onServer() {

    }
}
