package mod.kerzox.brewchemy.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.kerzox.brewchemy.common.crafting.ingredient.CountSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.OldFluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Date;

public class SomeJsonUtil {

    public static String getStringOr(String pKey, JsonObject pJson, String pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsString();
        } else {
            return pDefaultValue;
        }
    }

    public static int getIntOr(String pKey, JsonObject pJson, int pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsInt();
        } else {
            return pDefaultValue;
        }
    }

    public static long getLongOr(String pKey, JsonObject pJson, long pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsLong();
        } else {
            return pDefaultValue;
        }
    }

    public static boolean getBooleanOr(String pKey, JsonObject pJson, boolean pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsBoolean();
        } else {
            return pDefaultValue;
        }
    }

    public static Date getDateOr(String pKey, JsonObject pJson) {
        JsonElement jsonelement = pJson.get(pKey);
        return jsonelement != null ? new Date(Long.parseLong(jsonelement.getAsString())) : new Date();
    }

    public static ItemStack deserializeItemStack(JsonObject json) {
        ItemStack resultStack;
        if (json.get("result").isJsonObject()) {
            resultStack = ShapedRecipe.itemStackFromJson(json.get("result").getAsJsonObject());
        } else {
            String s1 = SomeJsonUtil.getStringOr("result", json, "");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            resultStack = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation));
        }
        return resultStack;
    }

    public static Ingredient[] deserializeIngredients(JsonObject json) {
        Ingredient[] ingredients = null;
        if (json.has("ingredient")) {
            JsonObject ingredient = json.get("ingredient").getAsJsonObject();
            if (ingredient.has("item")) {
                if (ingredient.get("item").isJsonArray()) {
                    JsonArray arr = ingredient.getAsJsonArray();
                    ingredients = new Ingredient[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        ingredients[i] = Ingredient.fromJson(arr.get(i));
                    }
                } else {
                    if (ingredient.has("item")) {
                        ingredients = new Ingredient[1];
                        ingredients[0] = Ingredient.fromJson(ingredient);
                    } else {
                        ingredients = new Ingredient[1];
                        ingredients[0] = Ingredient.fromJson(ingredient.getAsJsonObject());
                    }
                }
            }
        }
        return ingredients;
    }

    public static CountSpecificIngredient[] deserializeCountSpecificIngredients(JsonObject json) {
        CountSpecificIngredient[] ingredients = null;
        if (json.has("ingredients")) {
            JsonObject ingredient = json.get("ingredients").getAsJsonObject().getAsJsonObject("item_ingredient");
            if (ingredient.has("item")) {
                if (ingredient.get("item").isJsonArray()) {
                    JsonArray arr = ingredient.getAsJsonArray();
                    ingredients = new CountSpecificIngredient[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        ingredients[i] = CountSpecificIngredient.Serializer.INSTANCE.parse(arr.get(i).getAsJsonObject());
                    }
                } else {
                    ingredients = new CountSpecificIngredient[1];
                    ingredients[0] = CountSpecificIngredient.Serializer.INSTANCE.parse(ingredient);
                }
            }
        } else if (json.has("item_ingredient")) {
            JsonObject ingredient = json.getAsJsonObject("item_ingredient");
            if (ingredient.get("item").isJsonArray()) {
                JsonArray arr = ingredient.getAsJsonArray();
                ingredients = new CountSpecificIngredient[arr.size()];
                for (int i = 0; i < arr.size(); i++) {
                    ingredients[i] = CountSpecificIngredient.Serializer.INSTANCE.parse(arr.get(i).getAsJsonObject());
                }
            } else {
                ingredients = new CountSpecificIngredient[1];
                ingredients[0] = CountSpecificIngredient.Serializer.INSTANCE.parse(ingredient);
            }
        }
        return ingredients;
    }

    public static FluidStack deserializeFluidStack(JsonObject json) {
        FluidStack resultStack = FluidStack.EMPTY;
        if (json.has("result")) {
            JsonObject result = json.getAsJsonObject("result");
            ResourceLocation fluid = new ResourceLocation(SomeJsonUtil.getStringOr("fluid", result, ""));
            int amount = SomeJsonUtil.getIntOr("amount", result, 0);
            resultStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid), amount);
        }
        return resultStack;
    }

    public static OldFluidIngredient[] deserializeFluidIngredients(JsonObject json) {
        OldFluidIngredient[] ingredients = null;
        if (json.has("ingredients")) {
            JsonObject fluid = json.get("ingredients").getAsJsonObject().getAsJsonObject("fluid_ingredient");
            if (fluid.has("fluid")) {
                if (fluid.get("fluid").isJsonArray()) {
                    JsonArray arr = fluid.getAsJsonArray();
                    ingredients = new OldFluidIngredient[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        ingredients[i] = OldFluidIngredient.deserialize(arr.get(i));
                    }
                } else {
                    ingredients = new OldFluidIngredient[1];
                    ingredients[0] = OldFluidIngredient.deserialize(fluid);
                }
            }
        }
        else if (json.has("fluid_ingredient")) {
            JsonObject fluid = json.get("fluid_ingredient").getAsJsonObject();
            if (fluid.has("fluid")) {
                if (fluid.get("fluid").isJsonArray()) {
                    JsonArray arr = fluid.getAsJsonArray();
                    ingredients = new OldFluidIngredient[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        ingredients[i] = OldFluidIngredient.deserialize(arr.get(i));
                    }
                } else {
                    ingredients = new OldFluidIngredient[1];
                    ingredients[0] = OldFluidIngredient.deserialize(fluid);
                }
            }
        }
        return ingredients;
    }

}
