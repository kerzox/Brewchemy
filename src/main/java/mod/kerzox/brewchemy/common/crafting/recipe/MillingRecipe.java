package mod.kerzox.brewchemy.common.crafting.recipe;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeFactory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.brewchemy.common.util.BrewchemyJsonUtil;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MillingRecipe extends AbstractRecipe<RecipeInventory> {

    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final Map<Ingredient, Boolean> matching = new HashMap<>();
    private final ItemStack result;

    public MillingRecipe(RecipeType<?> type, ResourceLocation id, String group, ItemStack result, SizeSpecificIngredient ingredient, int duration) {
        super(type, id, group, duration, BrewchemyRegistry.Recipes.MILLING_RECIPE_SERIALIZER.get());
        this.result = result;
        this.ingredients.addAll(Arrays.asList(ingredient));
        this.ingredients.forEach(i -> matching.put(i, false));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matches(RecipeInventory inv, Level p_44003_) {
        return ingredients.get(0).test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInventory p_44001_, RegistryAccess p_267165_) {
        return this.result.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.result;
    }

    public static class Serializer implements RecipeSerializer<MillingRecipe> {

        @Override
        public MillingRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            SizeSpecificIngredient[] ingredients = BrewchemyJsonUtil.deserializeSizeSpecificIngredients(json);
            ItemStack resultStack = BrewchemyJsonUtil.deserializeItemStack(json);
            int duration = JsonUtils.getIntOr("duration", json, 0);
            return new MillingRecipe(BrewchemyRegistry.Recipes.MILLING_RECIPE.get(), pRecipeId, group, resultStack, ingredients[0], duration);
        }


        @Override
        public @Nullable MillingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            int ingredientCount = pBuffer.readVarInt();
            SizeSpecificIngredient[] ingredients = new SizeSpecificIngredient[ingredientCount];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = (SizeSpecificIngredient) Ingredient.fromNetwork(pBuffer);
            }
            ItemStack resultStack = pBuffer.readItem();
            int duration = pBuffer.readVarInt();
            return new MillingRecipe(BrewchemyRegistry.Recipes.MILLING_RECIPE.get(), pRecipeId, group, resultStack, ingredients[0], duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MillingRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pBuffer.writeVarInt(pRecipe.getIngredients().size());
            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeItem(pRecipe.getResultItem(RegistryAccess.EMPTY));
            pBuffer.writeVarInt(pRecipe.getDuration());
        }
    }

    public static class RecipeBuilder extends RecipeFactory {

        protected ItemStack result;
        protected SizeSpecificIngredient ingredient;

        public RecipeBuilder(ResourceLocation name, ItemStack result, SizeSpecificIngredient ingredient, int duration) {
            super(name, duration, BrewchemyRegistry.Recipes.MILLING_RECIPE_SERIALIZER.get());
            this.ingredient = ingredient;
            this.result = result;
        }

        @Override
        protected void writeRecipe(JsonObject json) {
            json.add("ingredient", ingredient.toJson());
            json.add("result", Util.serializeItemStack(result));
        }
    }

}
