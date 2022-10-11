package mod.kerzox.compatibility.jei.categories;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class AbstractRecipeCategory <T extends Recipe<?>> implements IRecipeCategory<T> {

    private IDrawableStatic background;


    public AbstractRecipeCategory(IDrawableStatic background) {
        this.background = background;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }
}
