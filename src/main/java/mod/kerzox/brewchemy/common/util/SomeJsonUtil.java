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
            JsonObject item = json.get("ingredient").getAsJsonObject();
            if (item.has("item")) {
                if (item.get("item").isJsonArray()) {
                    JsonArray arr = item.getAsJsonArray();
                    ingredients = new Ingredient[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        ingredients[i] = Ingredient.fromJson(arr.get(i));
                    }
                } else {
                    ingredients = new Ingredient[1];
                    ingredients[0] = Ingredient.fromJson(item.getAsJsonObject());
                }
            }
        }
        return ingredients;
    }

    public static FluidStack deserializeFluidStack(JsonObject json) {
        FluidStack resultStack = FluidStack.EMPTY;
        if (json.has("result")) {
            ResourceLocation fluid = new ResourceLocation(JsonUtils.getStringOr("fluid", json, ""));
            int amount = JsonUtils.getIntOr("amount", json, 0);
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
                    ingredients = new FluidIngredient[1];
                    ingredients[0] = FluidIngredient.deserialize(fluid);
                }
            }
        }
        return ingredients;
    }

}
