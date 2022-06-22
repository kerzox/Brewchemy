package mod.kerzox.brewchemy.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public class SomeJsonUtil {

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

    public static Ingredient[] deserializeIngredients(JsonObject json) {
        Ingredient[] ingredients = null;
        if (json.has("ingredient")) {
            if (json.get("ingredient").isJsonArray()) {
                JsonArray arr = (JsonArray) json.get("ingredient");
                ingredients = new Ingredient[arr.size()];
                for (int i = 0; i < arr.size(); i++) {
                    ingredients[i] = Ingredient.fromJson(arr.get(i));
                }
            } else {
                ingredients = new Ingredient[1];
                ingredients[0] = Ingredient.fromJson(json.get("ingredient").getAsJsonObject());
            }
        }
        return ingredients;
    }

}
