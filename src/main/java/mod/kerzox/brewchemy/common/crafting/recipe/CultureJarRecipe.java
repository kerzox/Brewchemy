package mod.kerzox.brewchemy.common.crafting.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import mod.kerzox.brewchemy.common.crafting.AbstractFluidRecipe;
import mod.kerzox.brewchemy.common.crafting.AbstractItemRecipe;
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
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CultureJarRecipe extends AbstractItemRecipe<RecipeInventory> {

    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final Map<Ingredient, Boolean> matching = new HashMap<>();
    private final Map<FluidIngredient, Boolean> fluid_matching = new HashMap<>();
    private final int heat;

    public CultureJarRecipe(RecipeType<?> type, ResourceLocation id, String group, ItemStack result, FluidIngredient fluids, int duration, int heat) {
        super(type, id, group, duration, result, BrewchemyRegistry.Recipes.CULTURE_JAR_RECIPE_SERIALIZER.get());
        this.ingredients.forEach(i -> matching.put(i, false));
        this.fluidIngredients.add(fluids);
        this.fluidIngredients.forEach(i -> fluid_matching.put(i, false));
        this.heat = heat;
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return fluidIngredients;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matches(RecipeInventory inv, Level level) {
        this.fluidIngredients.forEach(i -> fluid_matching.put(i, false));

        if (!inv.canStorageFluid()) throw new IllegalStateException("You can't have recipe inventory for this recipe without fluid");

        getFluidIngredients().forEach(((ingredient) -> {
            for (int i = 0; i < inv.getFluidHandler().getTanks(); i++) {
                if (ingredient.test(inv.getFluidHandler().getFluidInTank(i))) {
                    fluid_matching.put(ingredient, true);
                }
            }
        }));

        return !fluid_matching.containsValue(false);
    }

    public int getHeat() {
        return heat;
    }

    public static class Serializer implements RecipeSerializer<CultureJarRecipe> {

        @Override
        public CultureJarRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            FluidIngredient fluidIngredients = BrewchemyJsonUtil.deserializeFluidIngredients(json)[0];
            ItemStack resultStack = BrewchemyJsonUtil.deserializeItemStack(json);
            int duration = JsonUtils.getIntOr("duration", json, 0);
            int heat = JsonUtils.getIntOr("heat", json, 0);
            return new CultureJarRecipe(BrewchemyRegistry.Recipes.CULTURE_JAR_RECIPE.get(), pRecipeId, group, resultStack, fluidIngredients, duration, heat);
        }


        @Override
        public @Nullable CultureJarRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            FluidIngredient fluid_ingredients = FluidIngredient.Serializer.INSTANCE.parse(pBuffer);
            ItemStack resultStack = pBuffer.readItem();
            int duration = pBuffer.readVarInt();
            int heat = pBuffer.readVarInt();
            return new CultureJarRecipe(BrewchemyRegistry.Recipes.CULTURE_JAR_RECIPE.get(), pRecipeId, group, resultStack, fluid_ingredients, duration, heat);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CultureJarRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pBuffer.writeVarInt(pRecipe.getFluidIngredients().size());
            for (FluidIngredient ingredient : pRecipe.getFluidIngredients()) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeItem(pRecipe.getResultItem(RegistryAccess.EMPTY));
            pBuffer.writeVarInt(pRecipe.getDuration());
            pBuffer.writeVarInt(pRecipe.getHeat());
        }
    }

    public static class RecipeBuilder extends RecipeFactory {

        protected ItemStack result;
        protected FluidIngredient fingredient;
        protected int heat;

        public RecipeBuilder(ResourceLocation name, ItemStack result, FluidIngredient fluidIngredient, int duration, int heat) {
            super(name, duration, BrewchemyRegistry.Recipes.CULTURE_JAR_RECIPE_SERIALIZER.get());
            this.fingredient = fluidIngredient;
            this.result = result;
            this.heat = heat;
        }

        @Override
        protected void writeRecipe(JsonObject json) {
            JsonObject ingredients = new JsonObject();
            ingredients.add("fluid_ingredient", fingredient.toJson());
            json.add("recipe_ingredients", ingredients);
            json.add("result", Util.serializeItemStack(result));
            json.addProperty("heat", this.heat);
        }
    }

}
