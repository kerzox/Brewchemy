package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.loot.modifer.GrassDropModifiers;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class GenerateGlobalLootModifiers extends GlobalLootModifierProvider {

    public GenerateGlobalLootModifiers(DataGenerator gen){
        super(gen, Brewchemy.MODID);
    }

    @Override
    protected void start() {

        add("grass_drops", new GrassDropModifiers(
                new LootItemCondition[] {
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.GRASS).build(),
                        LootItemRandomChanceCondition.randomChance(0.125F).build()
                }
        ));

    }
}
