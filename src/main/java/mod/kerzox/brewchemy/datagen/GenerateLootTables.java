package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.datagen.loot.BaseLootTableProvider;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class GenerateLootTables extends BaseLootTableProvider {

    public GenerateLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get(), createTableWithCapability("boil_kettle", BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get(), BrewchemyRegistry.BlockEntities.BREWING_POT.get(), ForgeCapabilities.ITEM_HANDLER, ForgeCapabilities.FLUID_HANDLER));
        lootTables.put(BrewchemyRegistry.Blocks.FERMENTS_JAR_BLOCK.get(), createTableWithCapability("ferments_jar", BrewchemyRegistry.Blocks.FERMENTS_JAR_BLOCK.get(), BrewchemyRegistry.BlockEntities.FERMENTS_JAR.get(), ForgeCapabilities.ITEM_HANDLER, ForgeCapabilities.FLUID_HANDLER));
        lootTables.put(BrewchemyRegistry.Blocks.WOODEN_BARREL_BLOCK.get(), createTableWithCapability("wooden_barrel", BrewchemyRegistry.Blocks.WOODEN_BARREL_BLOCK.get(), BrewchemyRegistry.BlockEntities.WOODEN_BARREL.get(), ForgeCapabilities.ITEM_HANDLER, ForgeCapabilities.FLUID_HANDLER));
        lootTables.put(BrewchemyRegistry.Blocks.MILL_STONE_BLOCK.get(), createTableWithCapability("millstone", BrewchemyRegistry.Blocks.MILL_STONE_BLOCK.get(), BrewchemyRegistry.BlockEntities.MILL_STONE.get(), ForgeCapabilities.ITEM_HANDLER));
        lootTables.put(BrewchemyRegistry.Blocks.MILLSTONE_CRANK_BLOCK.get(), createSimpleTable("millstone_crank", BrewchemyRegistry.Blocks.MILL_STONE_BLOCK.get()));
        lootTables.put(BrewchemyRegistry.Blocks.WAREHOUSE_BLOCK.get(), createTableWithCapability("warehouse", BrewchemyRegistry.Blocks.WAREHOUSE_BLOCK.get(), BrewchemyRegistry.BlockEntities.WAREHOUSE.get(), ForgeCapabilities.ITEM_HANDLER));
        lootTables.put(BrewchemyRegistry.Blocks.HOPS_CROP_BLOCK.get(), createSimpleTable("hops_crop", BrewchemyRegistry.Blocks.HOPS_CROP_BLOCK.get()));
        lootTables.put(BrewchemyRegistry.Blocks.BARLEY_CROP_BLOCK.get(), createSimpleTable("barley_crop", BrewchemyRegistry.Blocks.BARLEY_CROP_BLOCK.get()));
        lootTables.put(BrewchemyRegistry.Blocks.ROPE_BLOCK.get(), createSimpleTable("rope_block", BrewchemyRegistry.Blocks.ROPE_BLOCK.get()));
    }
}
