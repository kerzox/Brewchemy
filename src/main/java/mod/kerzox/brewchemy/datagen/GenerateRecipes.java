package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.recipes.FermentJarRecipe;
import mod.kerzox.brewchemy.common.crafting.recipes.GerminationRecipe;
import mod.kerzox.brewchemy.common.crafting.recipes.MillstoneRecipe;
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
                Ingredient.of(BrewchemyRegistry.Items.MALTED_BARLEY_ITEM.get()),30)
                .build(pFinishedRecipeConsumer);

        GerminationRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "germinate_barley"),
                new ItemStack(BrewchemyRegistry.Items.GERMINATED_BARLEY_ITEM.get()),
                Ingredient.of(BrewchemyRegistry.Items.SOAKED_BARLEY_ITEM.get()),30)
                .build(pFinishedRecipeConsumer);

        FermentJarRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Brewchemy.MODID, "test_fluid_recipe"),
                new ItemStack(BrewchemyRegistry.Items.GERMINATED_BARLEY_ITEM.get()),
                FluidIngredient.of(new FluidStack(Fluids.WATER, 1000)),30)
                .build(pFinishedRecipeConsumer);


    }
}
