package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.recipes.*;
import mod.kerzox.brewchemy.common.item.PintGlassItem;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class GenerateRecipes extends RecipeProvider {

    public GenerateRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {

        SimpleCookingRecipeBuilder.smoking(
                Ingredient.of(BrewchemyRegistry.Items.GERMINATED_BARLEY_ITEM.get()),
                BrewchemyRegistry.Items.MALTED_BARLEY_ITEM.get(),
                0.35f,   100);

        MillstoneRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "milled_barley_from_malted_barley"),
                new ItemStack(BrewchemyRegistry.Items.MILLED_BARLEY_ITEM.get()),
                Ingredient.of(BrewchemyRegistry.Items.MALTED_BARLEY_ITEM.get()),180)
                .build(pFinishedRecipeConsumer);

        GerminationRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "germinate_barley"),
                new ItemStack(BrewchemyRegistry.Items.GERMINATED_BARLEY_ITEM.get()),
                Ingredient.of(BrewchemyRegistry.Items.SOAKED_BARLEY_ITEM.get()),1200)
                .build(pFinishedRecipeConsumer);

        FermentJarRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "brewers_yeast_from_wort"),
                new ItemStack(BrewchemyRegistry.Items.BREWERS_YEAST.get()),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT_FLUID.getFluid().get(), 125)),200)
                .build(pFinishedRecipeConsumer);

        BrewingRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "brewing_beer_from_wort"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER.getFluid().get(), PintGlassItem.KEG_VOLUME),
                Ingredient.of(new ItemStack(BrewchemyRegistry.Blocks.HOPS_CROP_BLOCK.get())),
                FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT_FLUID.getFluid().get(), PintGlassItem.KEG_VOLUME)),30, BrewingRecipe.FIRE)
                .build(pFinishedRecipeConsumer);

        FermentationRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "fermentation_beer"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER.getFluid().get(), PintGlassItem.KEG_VOLUME)).build(pFinishedRecipeConsumer);

    }
}
