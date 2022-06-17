package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.TwineEntityBlock;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;

public class TwineBlock extends BrewchemyEntityBlock<TwineEntityBlock> {
    public TwineBlock(Properties properties) {
        super(BrewchemyRegistry.BlockEntities.TWINE.getType(), properties);
    }
}
