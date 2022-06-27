package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyBlock;
import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class FluidBarrelBlock<T extends BrewchemyBlockEntity> extends BrewchemyEntityBlock<T> {

    public FluidBarrelBlock(RegistryObject<BlockEntityType<T>> type, Properties properties) {
        super(type, properties);
    }

}
