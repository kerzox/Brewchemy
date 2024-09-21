package mod.kerzox.brewchemy.datagen;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.recipe.BrewingRecipe;
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
        new BrewingRecipe.RecipeBuilder(
                new ResourceLocation(Brewchemy.MODID, "brewing_ale"),
                new FluidStack(BrewchemyRegistry.Fluids.BEER_ALE.getFluid().get(), 500),
                new SizeSpecificIngredient[]{ SizeSpecificIngredient.of(BrewchemyRegistry.Items.HOPS_ITEM.get(), 1) },
                new FluidIngredient[] { FluidIngredient.of(new FluidStack(BrewchemyRegistry.Fluids.WORT.getFluid().get(), 500)) },
                20 * 2,
                true,
                100).build(consumer);

    }

}
