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
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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
    }

}
