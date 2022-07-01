package mod.kerzox.brewchemy.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

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
            JsonObject ingredient = json.get("ingredient").getAsJsonObject();
            if (ingredient.has("item")) {
                if (ingredient.get("item").isJsonArray()) {
                    JsonArray arr = ingredient.getAsJsonArray();
                    ingredients = new Ingredient[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        ingredients[i] = Ingredient.fromJson(arr.get(i));
                    }
                } else {
                    if (ingredient.has("fluid")) {
                        ingredients = new Ingredient[1];
                        ingredients[0] = Ingredient.fromJson(ingredient.getAsJsonObject("item"));
                    } else {
                        ingredients = new Ingredient[1];
                        ingredients[0] = Ingredient.fromJson(ingredient.getAsJsonObject());
                    }
                }
            }
        }
        return ingredients;
    }

    public static FluidStack deserializeFluidStack(JsonObject json) {
        FluidStack resultStack = FluidStack.EMPTY;
        if (json.has("result")) {
            JsonObject result = json.getAsJsonObject("result");
            ResourceLocation fluid = new ResourceLocation(JsonUtils.getStringOr("fluid", result, ""));
            int amount = JsonUtils.getIntOr("amount", result, 0);
            resultStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid), amount);
        }
        return resultStack;
    }

    public static FluidIngredient[] deserializeFluidIngredients(JsonObject json) {
        FluidIngredient[] ingredients = null;
        if (json.has("ingredient")) {
            JsonObject fluid = json.get("ingredient").getAsJsonObject();
            if (fluid.has("fluid")) {
                if (fluid.get("fluid").isJsonArray()) {
                    JsonArray arr = fluid.getAsJsonArray();
                    ingredients = new FluidIngredient[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        ingredients[i] = FluidIngredient.deserialize(arr.get(i));
                    }
                } else {
                    if (fluid.has("item")) {
                        ingredients = new FluidIngredient[1];
                        ingredients[0] = FluidIngredient.deserialize(fluid.getAsJsonObject("fluid"));
                    } else {
                        ingredients = new FluidIngredient[1];
                        ingredients[0] = FluidIngredient.deserialize(fluid);
                    }
                }
            }
        }
        return ingredients;
    }

}
