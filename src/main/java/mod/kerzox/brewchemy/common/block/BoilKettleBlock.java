package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.blockentity.BoilKettleBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.KegBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class BoilKettleBlock extends FluidBarrelBlock<BoilKettleBlockEntity> {
    public BoilKettleBlock(RegistryObject<BlockEntityType<BoilKettleBlockEntity>> type, Properties properties) {
        super(type, properties);
    }
}
