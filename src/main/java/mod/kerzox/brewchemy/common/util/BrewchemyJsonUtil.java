package mod.kerzox.brewchemy.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public class BrewchemyJsonUtil {

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
