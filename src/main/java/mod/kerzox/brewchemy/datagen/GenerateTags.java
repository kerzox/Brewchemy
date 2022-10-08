package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class GenerateTags {

    public static Blocks Blocks (DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        return new Blocks(pGenerator, Brewchemy.MODID, existingFileHelper);
    }

    public static class Blocks extends BlockTagsProvider {

        private Tagger builder = new Tagger(this);

        private Blocks(DataGenerator pGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, modId, existingFileHelper);
        }

        public class Tagger {

            private Blocks provider;
            private Block block;

            private Tagger(Blocks provider) {
                this.provider = provider;
            }

            public Tagger axe(Block block) {
                TagsProvider.TagAppender<Block> tag = provider.tag(BlockTags.MINEABLE_WITH_PICKAXE);
                tag.add(block);
                this.block = block;
                return this;
            }

            public Tagger pickaxe(Block block) {
                TagsProvider.TagAppender<Block> tag = provider.tag(BlockTags.MINEABLE_WITH_PICKAXE);
                tag.add(block);
                this.block = block;
                return this;
            }

            public void harvest(int level) {
                switch (level) {
                    case -1 -> {
                        provider.tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(block);
                    }
                    case 0 -> {
                        provider.tag(Tags.Blocks.NEEDS_GOLD_TOOL).add(block);
                    }
                    case 1 -> {
                        provider.tag(BlockTags.NEEDS_STONE_TOOL).add(block);
                    }
                    case 2 -> {
                        provider.tag(BlockTags.NEEDS_IRON_TOOL).add(block);
                    }
                    case 3 -> {
                        provider.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
                    }
                    case 4 -> {
                        provider.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(block);
                    }
                }
            }
        }

        @Override
        protected void addTags() {
            builder.pickaxe(BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get()).harvest(-1);
            builder.pickaxe(BrewchemyRegistry.Blocks.BOIL_KETTLE_TOP_BLOCK.get()).harvest(-1);
            builder.pickaxe(BrewchemyRegistry.Blocks.FERMENTS_JAR_BLOCK.get()).harvest(-1);
            builder.pickaxe(BrewchemyRegistry.Blocks.MILL_STONE_BLOCK.get()).harvest(-1);
            builder.pickaxe(BrewchemyRegistry.Blocks.MILLSTONE_CRANK_BLOCK.get()).harvest(-1);
            builder.axe(BrewchemyRegistry.Blocks.WAREHOUSE_BLOCK.get()).harvest(-1);
            builder.axe(BrewchemyRegistry.Blocks.WOODEN_BARREL_BLOCK.get()).harvest(-1);
            this.tag(BlockTags.CLIMBABLE).add(BrewchemyRegistry.Blocks.ROPE_BLOCK.get());
        }

    }
}