package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GenerateItemTags extends ItemTagsProvider {


    public GenerateItemTags(PackOutput p_275343_,
                            CompletableFuture<HolderLookup.Provider> p_275729_,
                            CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_,
                            @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, Brewchemy.MODID, existingFileHelper);
    }

    public void forgeTag(String tagName, Item... items) {
        this.tag(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", tagName))).add(items);
    }


    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {

        this.tag(BrewchemyRegistry.Tags.YEAST).add(BrewchemyRegistry.Items.BREWERS_YEAST_ITEM.get(), BrewchemyRegistry.Items.LAGER_YEAST_ITEM.get(), BrewchemyRegistry.Items.WILD_YEAST_ITEM.get());
        forgeTag("hops", BrewchemyRegistry.Items.HOPS_ITEM.get());
        forgeTag("barley", BrewchemyRegistry.Items.BARLEY_ITEM.get());

    }

}
