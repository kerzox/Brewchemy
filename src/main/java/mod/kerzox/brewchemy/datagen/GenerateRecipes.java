package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.recipe.BrewingRecipe;
import mod.kerzox.brewchemy.common.crafting.recipe.CultureJarRecipe;
import mod.kerzox.brewchemy.common.crafting.recipe.FermentationRecipe;
import mod.kerzox.brewchemy.common.crafting.recipe.MillingRecipe;
import mod.kerzox.brewchemy.common.fluid.BrewchemyFluid;
import mod.kerzox.brewchemy.common.fluid.alcohol.AgeableAlcoholStack;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class GenerateRecipes extends RecipeProvider {
    public GenerateRecipes(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BrewchemyRegistry.Items.ROPE_ITEM.get(), 2)
                .requires(Tags.Items.STRING)
                .requires(Tags.Items.STRING)
                .requires(Tags.Items.STRING)
                .group("brewchemy")
                .unlockedBy("has_string", has(Tags.Items.STRING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, BrewchemyRegistry.Items.FERMENTATION_BARREL_ITEM.get())
                .pattern("psp")
                .pattern("pip")
                .pattern("psp")
                .define('p', ItemTags.PLANKS)
                .define('i', Tags.Items.INGOTS_COPPER)
                .define('s', ItemTags.WOODEN_SLABS)
                .group("brewchemy")
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BrewchemyRegistry.Blocks.TABLE_BLOCK.get())
                .pattern("www")
                .pattern("s s")
                .pattern("s s")
                .define('w', ItemTags.WOODEN_SLABS)
                .define('s', Tags.Items.RODS_WOODEN)
                .group("brewchemy")
                .unlockedBy("has_sticks", has(Tags.Items.RODS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BrewchemyRegistry.Blocks.BENCH_SEAT_BLOCK.get())
                .pattern("   ")
                .pattern(" w ")
                .pattern("s s")
                .define('s', Tags.Items.RODS_WOODEN)
                .define('w', ItemTags.WOODEN_SLABS)
                .group("brewchemy")
                .unlockedBy("has_sticks", has(Tags.Items.RODS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BrewchemyRegistry.Blocks.MILLING_BLOCK.get())
                .pattern("sss")
                .pattern("wiw")
                .pattern("sss")
                .define('w', ItemTags.PLANKS)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('s', Blocks.SMOOTH_STONE_SLAB)
                .group("brewchemy")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, BrewchemyRegistry.Blocks.CULTURE_JAR_BLOCK.get())
                .define('G', Tags.Items.GLASS)
                .define('S', ItemTags.WOODEN_SLABS)
                .pattern(" S ")
                .pattern("G G")
                .pattern("GGG")
                .group("brewchemy")
                .unlockedBy("has_glass", has(Tags.Items.GLASS)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, BrewchemyRegistry.Blocks.BREWING_KETTLE_BLOCK.get())
                .pattern("CCC")
                .pattern("C C")
                .pattern("WCW")
                .define('C', Tags.Items.INGOTS_COPPER)
                .define('W', ItemTags.PLANKS)
                .group("brewchemy")
                .unlockedBy("has_copper", has(Tags.Items.INGOTS_COPPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, BrewchemyRegistry.Items.BARREL_TAP.get())
                .pattern(" C ")
                .pattern("CCC")
                .pattern("   ")
                .define('C', Tags.Items.INGOTS_COPPER)
                .group("brewchemy")
                .unlockedBy("has_copper", has(Tags.Items.INGOTS_COPPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, BrewchemyRegistry.Items.PINT_ITEM.get())
                .pattern("   ")
                .pattern("g g")
                .pattern("ggg")
                .define('g', Tags.Items.GLASS)
                .group("brewchemy")
                .unlockedBy("has_glass", has(Tags.Items.GLASS))
                .save(consumer);

        SimpleCookingRecipeBuilder.smoking(
                Ingredient.of(BrewchemyRegistry.Tags.MILLED_BARLEY), RecipeCategory.FOOD,
                BrewchemyRegistry.Items.ROASTED_BARLEY_ITEM.get(),
                2f, 20 * 10).unlockedBy("has_milled_barley", has(BrewchemyRegistry.Tags.MILLED_BARLEY)).save(consumer,
                new ResourceLocation(Brewchemy.MODID, "roasted_barley_from_milled_barley"));
        new MillingRecipe.RecipeBuilder(new ResourceLocation(Brewchemy.MODID, "milled_barley_from_barley"),
                new ItemStack(BrewchemyRegistry.Items.MILLED_BARLEY_ITEM.get()),
                SizeSpecificIngredient.of(BrewchemyRegistry.Items.BARLEY_ITEM.get(), 1), 180)
                .build(consumer);
        new CultureJarRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "brewers_yeast"),
                new ItemStack(BrewchemyRegistry.Items.BREWERS_YEAST_ITEM.get()),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 250)),
                20 * 10,
                0)
                .build(consumer);
        new CultureJarRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "lager_yeast"),
                new ItemStack(BrewchemyRegistry.Items.LAGER_YEAST_ITEM.get()),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 250)),
                20 * 10,
                -1)
                .build(consumer);


        // brewing recipes

        new BrewingRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "brewing_wort"),
                new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 250),
                new SizeSpecificIngredient[]{ SizeSpecificIngredient.of(BrewchemyRegistry.Items.MILLED_BARLEY_ITEM.get(), 1) },
                new FluidIngredient[] { FluidIngredient.of(FluidTags.WATER, 250) },
                20 * 6,
                false,
                100).build(consumer);
        new BrewingRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "brewing_ale"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER_ALE.getFluid().get(), 500),
                new SizeSpecificIngredient[]{ SizeSpecificIngredient.of(BrewchemyRegistry.Items.HOPS_ITEM.get(), 1) },
                new FluidIngredient[] { FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 500)) },
                20 * 5,
                true,
                100).build(consumer);
        new BrewingRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "brewing_lager"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER_LAGER.getFluid().get(), 500),
                new SizeSpecificIngredient[]{ SizeSpecificIngredient.of(BrewchemyRegistry.Items.HOPS_ITEM.get(), 1) },
                new FluidIngredient[] { FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 500)) },
                20 * 5,
                true,
                -100).build(consumer);
        new BrewingRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "brewing_pale_ale"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER_PALE_ALE.getFluid().get(), 500),
                new SizeSpecificIngredient[]{ SizeSpecificIngredient.of(BrewchemyRegistry.Items.HOPS_ITEM.get(), 1) },
                new FluidIngredient[] { FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.BEER_ALE.getFluid().get(), 500)) },
                20 * 5,
                true,
                100).build(consumer);
        new BrewingRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "brewing_stout"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER_STOUT.getFluid().get(), 500),
                new SizeSpecificIngredient[]{ SizeSpecificIngredient.of(BrewchemyRegistry.Items.HOPS_ITEM.get(), 1), SizeSpecificIngredient.of(BrewchemyRegistry.Items.ROASTED_BARLEY_ITEM.get(), 1) },
                new FluidIngredient[] { FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 500)) },
                20 * 5,
                true,
                +100).build(consumer);

        // fermentation

        new FermentationRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "fermenting_ale"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER_ALE.getFluid().get(), 1),
                SizeSpecificIngredient.of(BrewchemyRegistry.Items.BREWERS_YEAST_ITEM.get(), 1),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.BEER_ALE.getFluid().get(), 1)),
                1).build(consumer);

        new FermentationRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "fermenting_pale_ale"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER_PALE_ALE.getFluid().get(), 1),
                SizeSpecificIngredient.of(BrewchemyRegistry.Items.BREWERS_YEAST_ITEM.get(), 1),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.BEER_PALE_ALE.getFluid().get(), 1)),
                1).build(consumer);

        new FermentationRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "fermenting_lager"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER_LAGER.getFluid().get(), 1),
                SizeSpecificIngredient.of(BrewchemyRegistry.Items.LAGER_YEAST_ITEM.get(), 1),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.BEER_LAGER.getFluid().get(), 1)),
                1).build(consumer);

        new FermentationRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "fermenting_stout"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER_STOUT.getFluid().get(), 1),
                SizeSpecificIngredient.of(BrewchemyRegistry.Items.BREWERS_YEAST_ITEM.get(), 1),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.BEER_STOUT.getFluid().get(), 1)),
                1).build(consumer);
    }

}
