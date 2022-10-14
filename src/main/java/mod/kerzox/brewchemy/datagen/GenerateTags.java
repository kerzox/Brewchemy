package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class GenerateTags {

    public static Blocks Blocks (DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        return new Blocks(pGenerator, Brewchemy.MODID, existingFileHelper);
    }

    public static Fluids Fluids (DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        return new Fluids(pGenerator, Brewchemy.MODID, existingFileHelper);
    }

    public static Items Items (DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        return new Items(pGenerator, pBlockTagsProvider, Brewchemy.MODID, existingFileHelper);
    }

    public static class Items extends ItemTagsProvider {

        public Items(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, pBlockTagsProvider, modId, existingFileHelper);
        }

        public void itemTag(ResourceLocation rl, Item... items) {
            this.tag(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), rl)).add(items);
        }

        @Override
        protected void addTags() {
            itemTag(new ResourceLocation("forge", "crops/hops"), BrewchemyRegistry.Items.HOPS_ITEM.get());
            itemTag(new ResourceLocation("forge", "crops/barley"), BrewchemyRegistry.Items.BARLEY_ITEM.get());
        }


    }

    public static class Fluids extends FluidTagsProvider {

        private Fluids(DataGenerator pGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, modId, existingFileHelper);
        }

        public void fluidTag(ResourceLocation rl, Fluid... fluids) {
            this.tag(TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), rl)).add(fluids);
        }

        @Override
        protected void addTags() {
            fluidTag(new ResourceLocation("forge", "beer"), BrewchemyRegistry.Fluids.BEER.getFluid().get());
            fluidTag(new ResourceLocation("forge", "wort"), BrewchemyRegistry.Fluids.WORT.getFluid().get());
//            this.tag(FluidTags.create(new ResourceLocation(Brewchemy.MODID, "beer_fluid"))).add(BrewchemyRegistry.Fluids.BEER.getFluid().get());
//            this.tag(FluidTags.create(new ResourceLocation(Brewchemy.MODID, "wort_fluid"))).add(BrewchemyRegistry.Fluids.WORT.getFluid().get());
        }
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