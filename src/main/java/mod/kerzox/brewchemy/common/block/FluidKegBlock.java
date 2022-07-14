package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.blockentity.KegBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class FluidKegBlock extends FluidBarrelBlock<KegBlockEntity> {
    public FluidKegBlock(RegistryObject<BlockEntityType<KegBlockEntity>> type, Properties properties) {
        super(type, properties);
    }
}
