package mod.kerzox.brewchemy.datagen;


import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GenerateItemModels extends ItemModelProvider {

    private final ModelFile generated = getExistingFile(mcLoc("item/generated"));

    public GenerateItemModels(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, Brewchemy.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        BrewchemyRegistry.Items.ALL_ITEMS.forEach((name, reg) -> {
            if (reg.get() instanceof BucketItem bucketItem) {
                ItemModelBuilder builder = getBuilder(ForgeRegistries.ITEMS.getKey(reg.get()).getPath());
                builder
                        .parent(getExistingFile(new ResourceLocation("forge:item/bucket")))
                        .customLoader(DynamicFluidContainerModelBuilder::begin)
                        .fluid(bucketItem.getFluid());

            }
        });
    }

    private void addTint(ItemModelBuilder builder, int tint) {
        builder.element().from(0, 0, 0).to(16, 16, 16)
                .allFaces((direction, faceBuilder) -> faceBuilder.texture("#layer1").tintindex(tint)).end();
    }

}
