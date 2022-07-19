package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.block.base.BrewchemyCropBlock;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class GenerateItemModels extends ItemModelProvider {

    public GenerateItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Brewchemy.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
