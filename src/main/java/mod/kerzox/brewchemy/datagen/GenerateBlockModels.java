package mod.kerzox.brewchemy.datagen;

import com.google.gson.JsonObject;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.block.BrewchemyCropBlock;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class GenerateBlockModels extends BlockStateProvider {

    public GenerateBlockModels(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, Brewchemy.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        for (RegistryObject<Block> registryObject : BrewchemyRegistry.Blocks.ALL_BLOCKS.values()) {

            Block block = registryObject.get();

            if (block instanceof BrewchemyCropBlock crop) {

                String name = BuiltInRegistries.BLOCK.getKey(crop).getPath();

                CropModelBuilder blockstate = new CropModelBuilder(name, crop.getMaxAge() + 1);
                for (var i = 0; i < crop.getMaxAge() + 1; i++) {
                    String textureName = "block/"+name+"_stage_"+i;
                    ResourceLocation cropModel = modLoc(textureName);
                    models().getBuilder(modLoc(textureName).getPath())
                            .parent(models().getExistingFile(modLoc("brewchemycrops")))
                            .texture("crop", cropModel);
                    registeredBlocks.put(crop, blockstate);

                }
            }
        }


    }

    public static class CropModelBuilder implements IGeneratedBlockState {

        private final String modelName;
        private final int age;

        public CropModelBuilder(String modelName, int age) {
            this.modelName = modelName;
            this.age = age;
        }

        @Override
        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            JsonObject variants = new JsonObject();

            for (var i = 0; i < age; i++) {
                JsonObject model = new JsonObject();
                model.addProperty("model", Brewchemy.MODID + ":block/" + modelName+"_stage_"+i);
                variants.add("age="+i, model);
            }
            root.add("variants", variants);
            return root;
        }
    }

    public static class SimpleModelBuilder implements IGeneratedBlockState {

        private String modelName;

        public SimpleModelBuilder(String model) {
            this.modelName = model;
        }

        @Override
        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            JsonObject model = new JsonObject();
            model.addProperty("model", Brewchemy.MODID + ":block/" + modelName);
            JsonObject variants = new JsonObject();
            variants.add("", model);
            root.add("variants", variants);
            return root;
        }
    }

}
