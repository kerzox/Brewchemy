package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.crafting.ingredient.CountSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.OldFluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.recipes.*;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class GenerateRecipes extends RecipeProvider {

    public GenerateRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {

        SimpleCookingRecipeBuilder.smoking(
                Ingredient.of(BrewchemyRegistry.Items.SOAKED_BARLEY_ITEM.get()),
                BrewchemyRegistry.Items.MALTED_BARLEY_ITEM.get(),
                0.35f, 100);

        MillstoneRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "milled_barley_from_malted_barley"),
                new ItemStack(BrewchemyRegistry.Items.MILLED_BARLEY_ITEM.get()),
                Ingredient.of(BrewchemyRegistry.Items.MALTED_BARLEY_ITEM.get()), 180)
                .build(pFinishedRecipeConsumer);

        FermentJarRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "brewers_yeast_from_wort"),
                new ItemStack(BrewchemyRegistry.Items.BREWERS_YEAST.get()),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 125)), 200)
                .build(pFinishedRecipeConsumer);

        // for every 500 mb of water you need 1 milled barley to make 500 wortv
        BrewingRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "brewing_wort"),
                new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 500),
                SizeSpecificIngredient.of(new ItemStack(BrewchemyRegistry.Items.MILLED_BARLEY_ITEM.get(),1)),
                FluidIngredient.of(new FluidStack(Fluids.WATER, 500)), 200, BrewingRecipe.FIRE)
                .build(pFinishedRecipeConsumer);

        BrewingRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "brewing_beer1"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER.getFluid().get(), 1000),
                SizeSpecificIngredient.of(new ItemStack(BrewchemyRegistry.Items.HOPS_ITEM.get(), 2)),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 1000)), 1000, BrewingRecipe.FIRE)
                .build(pFinishedRecipeConsumer);

        FermentationRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "fermentation_beer"),
                SizeSpecificIngredient.of(new ItemStack(BrewchemyRegistry.Items.BREWERS_YEAST.get(), 1)),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.BEER.getFluid().get(), 1000)),
                new FluidStack(BrewchemyRegistry.Fluids.BEER.getFluid().get(), 5000)).build(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BrewchemyRegistry.Blocks.WOODEN_BARREL_BLOCK.get())
                .pattern("psp")
                .pattern("pip")
                .pattern("psp")
                .define('p', ItemTags.PLANKS)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('s', ItemTags.SLABS)
                .group("brewchemy")
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BrewchemyRegistry.Blocks.MILL_STONE_BLOCK.get())
                .define('S', Items.SMOOTH_STONE)
                .define('s', Tags.Items.STONE)
                .define('W', ItemTags.PLANKS)
                .pattern("WsW").pattern("W W").pattern("SSS").unlockedBy("has_smooth_stone", has(Items.SMOOTH_STONE)).save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BrewchemyRegistry.Blocks.MILLSTONE_CRANK_BLOCK.get())
                .define('S', Items.SMOOTH_STONE)
                .define('L', ItemTags.LOGS)
                .pattern(" SL").pattern("S S").pattern(" S ").unlockedBy("has_smooth_stone", has(Items.SMOOTH_STONE)).save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BrewchemyRegistry.Blocks.ROPE_BLOCK.get())
                .define('S', Tags.Items.STRING)
                .pattern(" S ").pattern(" S ").pattern(" S ").unlockedBy("has_string", has(Tags.Items.STRING)).save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BrewchemyRegistry.Blocks.FERMENTS_JAR_BLOCK.get())
                .define('G', Tags.Items.GLASS)
                .define('S', ItemTags.WOODEN_SLABS)
                .pattern(" S ").pattern("G G").pattern("GGG").unlockedBy("has_glass", has(Tags.Items.GLASS)).save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BrewchemyRegistry.Blocks.WAREHOUSE_BLOCK.get())
                .define('W', ItemTags.PLANKS)
                .define('I', Tags.Items.INGOTS_IRON)
                .pattern("IWI").pattern("WWW").pattern("IWI").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BrewchemyRegistry.Items.SOFT_MALLET.get())
                .define('W', ItemTags.PLANKS)
                .define('s', Tags.Items.RODS)
                .pattern("WWW").pattern("WWW").pattern(" s ").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get())
                .define('C', Items.CAULDRON)
                .define('I', Tags.Items.INGOTS_IRON)
                .pattern(" I ").pattern("I I").pattern("ICI").unlockedBy("has_cauldron", has(Items.CAULDRON)).save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(BrewchemyRegistry.Items.PINT_GLASS.get())
                .define('G', Tags.Items.GLASS)
                .pattern("G G").pattern("G G").pattern(" G ").unlockedBy("has_glass", has(Tags.Items.GLASS)).save(pFinishedRecipeConsumer);

        ShapelessRecipeBuilder.shapeless(BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get()).requires(BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get()).unlockedBy("has_brewkettle", has(BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get())).save(pFinishedRecipeConsumer,
                new ResourceLocation(Brewchemy.MODID, "boil_kettle_block_reset"));

        SimpleCookingRecipeBuilder.smoking(Ingredient.of(BrewchemyRegistry.Items.SOAKED_BARLEY_ITEM.get()), BrewchemyRegistry.Items.MALTED_BARLEY_ITEM.get(), 2f, 100);


    }
}
