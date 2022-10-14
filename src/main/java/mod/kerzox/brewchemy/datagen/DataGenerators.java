package mod.kerzox.brewchemy.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void doDataGeneration(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(event.includeServer(), new GenerateRecipes(gen));
        gen.addProvider(event.includeServer(), new GenerateGlobalLootModifiers(gen));
        gen.addProvider(event.includeServer(), GenerateTags.Blocks(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), GenerateTags.Fluids(gen, existingFileHelper));
        gen.addProvider(event.includeClient(), new GenerateBlockModels(gen, existingFileHelper));
        gen.addProvider(event.includeClient(), new GenerateItemModels(gen, existingFileHelper));
        gen.addProvider(event.includeServer(), new GenerateLootTables(gen));
    }


}
