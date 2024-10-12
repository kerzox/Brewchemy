package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.block.BarleyCropBlock;
import mod.kerzox.brewchemy.common.block.HopsCropBlock;
import mod.kerzox.brewchemy.common.block.base.BrewchemyBlock;
import mod.kerzox.brewchemy.common.blockentity.base.CapabilityBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class GenerateLootTables extends VanillaBlockLoot {

    @Override
    protected void generate() {

        dropNothing(BrewchemyRegistry.Blocks.ROPE_TIED_POST_BLOCK.get());
        dropNothing(BrewchemyRegistry.Blocks.PINT_GLASS_BLOCK.get());
        //dropNothing(BrewchemyRegistry.Blocks.BREWING_KETTLE_TOP_BLOCK.get());

        dropSelf(BrewchemyRegistry.Blocks.BENCH_SEAT_BLOCK.get());
        dropSelf(BrewchemyRegistry.Blocks.TABLE_BLOCK.get());

        createCrop(BarleyCropBlock.growthStages, 5,
                BrewchemyRegistry.Blocks.BARLEY_CROP_BLOCK.get(),
                BrewchemyRegistry.Items.BARLEY_ITEM.get(),
                BrewchemyRegistry.Blocks.BARLEY_CROP_BLOCK.get().asItem());
        createCrop(HopsCropBlock.growthStages, 5,
                BrewchemyRegistry.Blocks.HOPS_CROP_BLOCK.get(),
                BrewchemyRegistry.Items.HOPS_ITEM.get(),
                BrewchemyRegistry.Blocks.HOPS_CROP_BLOCK.get().asItem());
        dropOther(BrewchemyRegistry.Blocks.BREWING_KETTLE_TOP_BLOCK.get(), BrewchemyRegistry.Blocks.BREWING_KETTLE_BLOCK.get());
        createStandardTable(
                BrewchemyRegistry.Blocks.BREWING_KETTLE_BLOCK.get(),
                BrewchemyRegistry.BlockEntities.BREWING_KETTLE_BLOCK_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                BrewchemyRegistry.Blocks.CULTURE_JAR_BLOCK.get(),
                BrewchemyRegistry.BlockEntities.CULTURE_JAR_BLOCK_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                BrewchemyRegistry.Blocks.FERMENTATION_BARREL_BLOCK.get(),
                BrewchemyRegistry.BlockEntities.FERMENTATION_BARREL_BLOCK_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                BrewchemyRegistry.Blocks.MILLING_BLOCK.get(),
                BrewchemyRegistry.BlockEntities.MILLING_BLOCK_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
    }

    private void dropNothing(Block block) {
        this.dropOther(block, Blocks.AIR);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getEntries().stream()
                .filter(e -> e.getKey().location().getNamespace().equals(Brewchemy.MODID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private void createCrop(Property<Integer> property, int age, Block block, Item crop, Item seed) {
        this.add(block, this.createCropDrops(block, crop, seed, blockStateCondition(block, property, age)));
    }

    private LootItemCondition.Builder blockStateCondition(Block block, Property<Integer> property, int age) {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, age));
    }


    private void createStandardTable(Block block, BlockEntityType<?> type, String... tags) {
        LootPoolSingletonContainer.Builder<?> lti = LootItem.lootTableItem(block);
        lti.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY));
        for (String tag : tags) {
            lti.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(tag, "BlockEntityTag." + tag,
                    CopyNbtFunction.MergeStrategy.REPLACE));
        }
        lti.apply(SetContainerContents.setContents(type)
                .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))));

        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(lti);
        add(block, LootTable.lootTable().withPool(builder));
    }

    private void createStandardTableWithOtherDrop(Block block, Item drop, BlockEntityType<?> type, String... tags) {
        LootPoolSingletonContainer.Builder<?> lti = LootItem.lootTableItem(drop);
        lti.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY));
        for (String tag : tags) {
            lti.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(tag, "BlockEntityTag." + tag,
                    CopyNbtFunction.MergeStrategy.REPLACE));
        }
        lti.apply(SetContainerContents.setContents(type).withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))));

        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(lti);
        add(block, LootTable.lootTable().withPool(builder));
    }

}