package mod.kerzox.compatibility.jei.categories;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.crafting.recipes.BrewingRecipe;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import mod.kerzox.compatibility.jei.BrewchemyJeiPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

public class BrewingRecipeCategory extends AbstractRecipeCategory<BrewingRecipe> {

    public static final RecipeType<BrewingRecipe> recipeType = RecipeType.create(Brewchemy.MODID, "brewing_recipe", BrewingRecipe.class);
    private IDrawableStatic foreground;
    private IDrawableStatic tankOverlay;
    private IDrawable icon;

    public BrewingRecipeCategory(IGuiHelper helper) {
        super(helper.createBlankDrawable(125, 50));
        this.foreground = helper.createDrawable(new ResourceLocation(Brewchemy.MODID, "textures/gui/brewing_pot.png"), 0,0, 173, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BrewchemyRegistry.Blocks.BOIL_KETTLE_BLOCK.get()));
        this.tankOverlay = helper.createDrawable(BrewchemyJeiPlugin.GUI_ELEMENTS, 26, 50, 26, 50);

    }

    @Override
    public void draw(BrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        foreground.draw(stack);
    }

    @Override
    public RecipeType<BrewingRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Brewing Recipe");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BrewingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 13, 16).addIngredients(recipe.getCIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 46, 0)
                .setFluidRenderer(10000, false, 24, 49)
                .addIngredients(ForgeTypes.FLUID_STACK, List.of(recipe.getFluidIngredients().get(0).getStacks()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 0)
                .setFluidRenderer(10000, false, 24, 49)
                .addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(recipe.getResultFluid().getFluid(), recipe.getResultFluid().getAmount()));
    }
}
