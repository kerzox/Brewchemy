package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.MillingBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class MillingBlock extends BrewchemyEntityBlock<MillingBlockEntity> {

    public MillingBlock(RegistryObject<BlockEntityType<MillingBlockEntity>> type, Properties p_49795_) {
        super(type, p_49795_);
    }

}
