package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.FermentsJarBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class FermentsJarBlock extends BrewchemyEntityBlock<FermentsJarBlockEntity> {

    public FermentsJarBlock(Properties properties) {
        super(BrewchemyRegistry.BlockEntities.FERMENTS_JAR.getType(), properties);
    }

}
