package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.SupportStickEntityBlock;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class SupportStickBlock extends BrewchemyEntityBlock<SupportStickEntityBlock> {

    public SupportStickBlock(Properties properties) {
        super(BrewchemyRegistry.BlockEntities.SUPPORT_STICK.getType(), properties);
    }
}
