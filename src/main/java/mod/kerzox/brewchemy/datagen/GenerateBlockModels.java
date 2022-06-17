package mod.kerzox.brewchemy.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.block.base.BrewchemyCropBlock;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.IGeneratedBlockstate;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class GenerateBlockModels extends BlockStateProvider {
    
    public GenerateBlockModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Brewchemy.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (BrewchemyRegistry.makeBlock<?> block : BrewchemyRegistry.makeBlock.ENTRIES) {
            if (block.get() instanceof BrewchemyCropBlock crop) {
                CropModelBuilder blockstate = new CropModelBuilder(block.getName(), 7);
                for (var i = 0; i < 7; i++) {
                    String textureName = "block/"+block.getName()+"_stage"+i;
                    ResourceLocation cropModel = modLoc(textureName);
                    BlockModelBuilder builder = models().getBuilder(modLoc(textureName).getPath())
                            .parent(models().getExistingFile(mcLoc("crop")))
                            .texture("crop", cropModel);
                    registeredBlocks.put(crop, blockstate);

                }
            }
        }
    }

    public static class CropModelBuilder implements IGeneratedBlockstate {

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
                model.addProperty("model", Brewchemy.MODID + ":block/" + modelName+"_stage"+i);
                variants.add("age="+i, model);
            }
            root.add("variants", variants);
            return root;
        }
    }

}
