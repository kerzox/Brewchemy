package mod.kerzox.brewchemy.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public class BrewchemyJsonUtil {

    public static FluidIngredient[] deserializeFluidIngredients(JsonObject json) {
        FluidIngredient[] deserialized = null;
        //
        if (json.has("recipe_ingredients")) {
            JsonObject recipe_ingredients = json.get("recipe_ingredients").getAsJsonObject();
            // check if it's either an array or a ingredient
            if(recipe_ingredients.has("fluid_ingredient")) { // a single item
                JsonObject ingredient = recipe_ingredients.get("fluid_ingredient").getAsJsonObject();
                deserialized = new FluidIngredient[1];
                deserialized[0] = FluidIngredient.Serializer.INSTANCE.parse(ingredient);
            }
            else if(recipe_ingredients.has("fluid_ingredients")) { // an array of items
                JsonArray ingredientArr = recipe_ingredients.get("fluid_ingredients").getAsJsonArray();
                deserialized = new FluidIngredient[ingredientArr.size()];
                for (int i = 0; i < ingredientArr.size(); i++) {
                    JsonObject ingredient = ingredientArr.get(i).getAsJsonObject();
                    deserialized[i] = FluidIngredient.Serializer.INSTANCE.parse(ingredient);
                }
            }

        }
        return deserialized;
    }

    public static Ingredient[] deserializeIngredients(JsonObject json) {
        Ingredient[] deserialized = null;
        //
        if (json.has("recipe_ingredients")) {

            JsonObject recipe_ingredients = json.get("recipe_ingredients").getAsJsonObject();

            // check if it's either an array or a ingredient
            if(recipe_ingredients.has("ingredient")) { // a single item
                JsonObject ingredient = recipe_ingredients.get("ingredient").getAsJsonObject();

                // check if we are a ingredient or a sizeSpecific
                if (ingredient.has("size")) {
                    if (ingredient.has("item")) {
                        deserialized = new SizeSpecificIngredient[1];
                        deserialized[0] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient);
                    } else {
                        deserialized = new SizeSpecificIngredient[1];
                        deserialized[0] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient.getAsJsonObject());
                    }
                } else { // vanilla ingredient
                    if (ingredient.has("item")) {
                        deserialized = new Ingredient[1];
                        deserialized[0] = Ingredient.fromJson(ingredient);
                    } else {
                        deserialized = new SizeSpecificIngredient[1];
                        deserialized[0] = Ingredient.fromJson(ingredient.getAsJsonObject());
                    }
                }
            }
            else if(recipe_ingredients.has("ingredients")) { // an array of items
                JsonArray ingredientArr = recipe_ingredients.get("ingredients").getAsJsonArray();
                deserialized = new SizeSpecificIngredient[ingredientArr.size()];
                for (int i = 0; i < ingredientArr.size(); i++) {
                    if (ingredientArr.get(i).isJsonArray()) {
                        JsonArray arr = ingredientArr.get(i).getAsJsonArray();
                        deserialized = new SizeSpecificIngredient[arr.size()];
                        for (int j = 0; j < arr.size(); j++) {
                            deserialized[j] = SizeSpecificIngredient.Serializer.INSTANCE.parse(arr.get(j).getAsJsonObject());
                        }
                    } else {
                        JsonObject ingredient = ingredientArr.get(i).getAsJsonObject();
                        if (ingredient.has("item")) {
                            deserialized[i] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient);
                        } else {
                            deserialized = new SizeSpecificIngredient[1];
                            deserialized[i] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient.getAsJsonObject());
                        }
                    }
                }
            }

        }
        return deserialized;
    }

    public static SizeSpecificIngredient[] deserializeSizeSpecificIngredients(JsonObject json) {
        SizeSpecificIngredient[] ingredients = null;
        if (json.has("ingredient")) {
            if (json.get("ingredient").isJsonArray()) {
                JsonArray ingredientArr = json.get("ingredient").getAsJsonArray();
                ingredients = new SizeSpecificIngredient[ingredientArr.size()];
                for (int i = 0; i < ingredientArr.size(); i++) {
                    if (ingredientArr.get(i).isJsonArray()) {
                        JsonArray arr = ingredientArr.get(i).getAsJsonArray();
                        ingredients = new SizeSpecificIngredient[arr.size()];
                        for (int j = 0; j < arr.size(); j++) {
                            ingredients[j] = SizeSpecificIngredient.Serializer.INSTANCE.parse(arr.get(j).getAsJsonObject());
                        }
                    } else {
                        JsonObject ingredient = ingredientArr.get(i).getAsJsonObject();
                        if (ingredient.has("item")) {
                            ingredients[i] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient);
                        } else {
                            ingredients = new SizeSpecificIngredient[1];
                            ingredients[i] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient.getAsJsonObject());
                        }
                    }
                }
            } else if (json.get("ingredient").isJsonObject()) {
                JsonObject ingredient = json.get("ingredient").getAsJsonObject();
                if (ingredient.get("item").isJsonArray()) {
                    JsonArray arr = ingredient.getAsJsonArray();
                    ingredients = new SizeSpecificIngredient[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        ingredients[i] = SizeSpecificIngredient.Serializer.INSTANCE.parse(arr.get(i).getAsJsonObject());
                    }
                } else {
                    if (ingredient.has("item")) {
                        ingredients = new SizeSpecificIngredient[1];
                        ingredients[0] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient);
                    } else {
                        ingredients = new SizeSpecificIngredient[1];
                        ingredients[0] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient.getAsJsonObject());
                    }
                }
            }
        }
        return ingredients;
    }

    public static ItemStack deserializeItemStack(JsonObject json) {
        ItemStack resultStack;
        if (json.get("result").isJsonObject()) {
            resultStack = ShapedRecipe.itemStackFromJson(json.get("result").getAsJsonObject());
        } else {
            String s1 = JsonUtils.getStringOr("result", json, "");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            resultStack = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation));
        }
        return resultStack;
    }

    public static ItemStack[] deserializeItemStacks(JsonObject json) {
        ItemStack[] resultStack = null;

        if (json.has("item_result")) {
            JsonArray arr = json.getAsJsonArray("item_result");
            resultStack = new ItemStack[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                resultStack[i] = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(arr.get(i).getAsJsonObject(), true, false);
            }
        }
        return resultStack;
    }

}
