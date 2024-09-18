package mod.kerzox.brewchemy.common.crafting.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeFactory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
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
import java.util.List;
import java.util.Map;

public class BrewingRecipe extends AbstractRecipe<RecipeInventory> {

    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final Map<Ingredient, Boolean> matching = new HashMap<>();
    private final Map<FluidIngredient, Boolean> fluid_matching = new HashMap<>();
    private final ItemStack result;

    public BrewingRecipe(RecipeType<?> type, ResourceLocation id, String group, ItemStack result, SizeSpecificIngredient[] ingredients, FluidIngredient[] fluids,  int duration) {
        super(type, id, group, duration, BrewchemyRegistry.Recipes.BREWING_RECIPE_SERIALIZER.get());
        this.result = result;
        this.ingredients.addAll(Arrays.stream(ingredients).toList());
        this.ingredients.forEach(i -> matching.put(i, false));
        this.fluidIngredients.addAll(Arrays.stream(fluids).toList());
        this.fluidIngredients.forEach(i -> fluid_matching.put(i, false));
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return fluidIngredients;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matches(RecipeInventory inv, Level p_44003_) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInventory p_44001_, RegistryAccess p_267165_) {
        return this.result.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.result;
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {

        @Override
        public BrewingRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            Ingredient[] ingredients = BrewchemyJsonUtil.deserializeIngredients(json);
            FluidIngredient[] fluidIngredients = BrewchemyJsonUtil.deserializeFluidIngredients(json);
            ItemStack resultStack = BrewchemyJsonUtil.deserializeItemStack(json);
            int duration = JsonUtils.getIntOr("duration", json, 0);
            return new BrewingRecipe(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), pRecipeId, group, resultStack, (SizeSpecificIngredient[]) ingredients, fluidIngredients, duration);
        }


        @Override
        public @Nullable BrewingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            int ingredientCount = pBuffer.readVarInt();
            SizeSpecificIngredient[] ingredients = new SizeSpecificIngredient[ingredientCount];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = (SizeSpecificIngredient) Ingredient.fromNetwork(pBuffer);
            }
            FluidIngredient[] fluid_ingredients = new FluidIngredient[ingredientCount];
            for (int i = 0; i < fluid_ingredients.length; i++) {
                fluid_ingredients[i] = FluidIngredient.Serializer.INSTANCE.parse(pBuffer);
            }
            ItemStack resultStack = pBuffer.readItem();
            int duration = pBuffer.readVarInt();
            return new BrewingRecipe(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), pRecipeId, group, resultStack, ingredients, fluid_ingredients, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, BrewingRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pBuffer.writeVarInt(pRecipe.getIngredients().size());
            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeVarInt(pRecipe.getFluidIngredients().size());
            for (FluidIngredient ingredient : pRecipe.getFluidIngredients()) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeItem(pRecipe.getResultItem(RegistryAccess.EMPTY));
            pBuffer.writeVarInt(pRecipe.getDuration());
        }
    }

    public static class RecipeBuilder extends RecipeFactory {

        protected ItemStack result;
        protected SizeSpecificIngredient[] ingredient;
        protected FluidIngredient[] fingredient;

        public RecipeBuilder(ResourceLocation name, ItemStack result, SizeSpecificIngredient[] ingredient, FluidIngredient[] fluidIngredients, int duration) {
            super(name, duration, BrewchemyRegistry.Recipes.BREWING_RECIPE_SERIALIZER.get());
            this.ingredient = ingredient;
            this.fingredient = fluidIngredients;
            this.result = result;
        }

        @Override
        protected void writeRecipe(JsonObject json) {

            JsonObject ingredients = new JsonObject();

            if (ingredient.length > 1) {
                JsonArray arr = new JsonArray();
                for (int i = 0; i < ingredient.length; i++) {
                    arr.add(ingredient[i].toJson());
                }
                ingredients.add("ingredients", arr);
            } else {
                ingredients.add("ingredient", ingredient[0].toJson());
            }

            if (fingredient.length > 1) {
                JsonArray arr = new JsonArray();
                for (int i = 0; i < fingredient.length; i++) {
                    arr.add(fingredient[i].toJson());
                }
                ingredients.add("fluid_ingredients", arr);
            } else {
                ingredients.add("fluid_ingredient", fingredient[0].toJson());
            }

            json.add("recipe_ingredients", ingredients);
            json.add("result", Util.serializeItemStack(result));
        }
    }

}
