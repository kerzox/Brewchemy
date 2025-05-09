package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GenerateBlockTags extends BlockTagsProvider {

    public GenerateBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Brewchemy.MODID, existingFileHelper);
    }

    public void axe(Block block, int level) {
        tag(BlockTags.MINEABLE_WITH_AXE).add(block);
        harvest(level, block);
    }

    public void pickaxe(Block block, int level) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
        harvest(level, block);
    }

    public void harvest(int level, Block block) {
        switch (level) {
            case -1 -> {
                tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(block);
            }
            case 0 -> {
                tag(Tags.Blocks.NEEDS_GOLD_TOOL).add(block);
            }
            case 1 -> {
                tag(BlockTags.NEEDS_STONE_TOOL).add(block);
            }
            case 2 -> {
                tag(BlockTags.NEEDS_IRON_TOOL).add(block);
            }
            case 3 -> {
                tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
            }
            case 4 -> {
                tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(block);
            }
        }
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        axe(BrewchemyRegistry.Blocks.TABLE_BLOCK.get(), -1);
        axe(BrewchemyRegistry.Blocks.BENCH_SEAT_BLOCK.get(), -1);
        axe(BrewchemyRegistry.Blocks.FERMENTATION_BARREL_BLOCK.get(), -1);
        pickaxe(BrewchemyRegistry.Blocks.MILLING_BLOCK.get(), -1);
        pickaxe(BrewchemyRegistry.Blocks.BREWING_KETTLE_BLOCK.get(), -1);
        pickaxe(BrewchemyRegistry.Blocks.BREWING_KETTLE_TOP_BLOCK.get(), -1);
        pickaxe(BrewchemyRegistry.Blocks.CULTURE_JAR_BLOCK.get(), -1);
        pickaxe(BrewchemyRegistry.Blocks.PINT_GLASS_BLOCK.get(), -1);
    }
}
