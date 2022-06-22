package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FermentsJarBlockEntity extends BrewchemyBlockEntity implements IServerTickable {

    public FermentsJarBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.FERMENTS_JAR.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void onServer() {

    }
}
