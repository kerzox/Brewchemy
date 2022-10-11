package mod.kerzox.compatibility.jei;


import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.*;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import mod.kerzox.compatibility.jei.categories.BrewingRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

@JeiPlugin
public class BrewchemyJeiPlugin implements IModPlugin {

    public static final ResourceLocation GUI_ELEMENTS = new ResourceLocation(Brewchemy.MODID, "textures/gui/gui_elements.png");

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Brewchemy.MODID, "jei_compat");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        System.out.println("HELLO WE ARE REGISTERING MY RECIPES CATS!!!!");
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new BrewingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get()), BrewingRecipeCategory.recipeType);
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        System.out.println("HELLO WE ARE REGISTERING MY RECIPES!!!!");
        registration.addRecipes(BrewingRecipeCategory.recipeType, getRecipes(BrewchemyRegistry.Recipes.BREWING_RECIPE.get()));
    }

    public <T extends AbstractRecipe> List<T> getRecipes(RecipeType<T> type) {
        return Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(type);
    }

}
