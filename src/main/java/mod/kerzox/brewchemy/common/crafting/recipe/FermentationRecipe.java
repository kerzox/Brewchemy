package mod.kerzox.brewchemy.common.crafting.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import mod.kerzox.brewchemy.common.crafting.AbstractFluidRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeFactory;
import mod.kerzox.brewchemy.common.crafting.RecipeInventory;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.brewchemy.common.event.TickUtils;
import mod.kerzox.brewchemy.common.fluid.alcohol.AgeableAlcoholStack;
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

public class FermentationRecipe extends AbstractFluidRecipe<RecipeInventory> {
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final Map<Ingredient, Boolean> matching = new HashMap<>();
    private final Map<FluidIngredient, Boolean> matchingFluids = new HashMap<>();

    public FermentationRecipe(RecipeType<?> type, ResourceLocation id, String group, FluidStack result, SizeSpecificIngredient[] ingredients, FluidIngredient[] fluids, int duration) {
        super(type, id, group, duration, result, BrewchemyRegistry.Recipes.FERMENTATION_RECIPE_SERIALIZER.get());
        this.ingredients.addAll(Arrays.stream(ingredients).toList());
        this.ingredients.forEach(i -> matching.put(i, false));
        this.fluidIngredients.addAll(Arrays.stream(fluids).toList());
        this.fluidIngredients.forEach(i -> matchingFluids.put(i, false));
    }

    public AgeableAlcoholStack[] assembleAsAlcohlic(RecipeInventory inv, RegistryAccess access) {
        AgeableAlcoholStack[] ageableAlcoholStacks = new AgeableAlcoholStack[this.results.length];
        for (int i = 0; i < this.results.length; i++) {
            ageableAlcoholStacks[i] = new AgeableAlcoholStack(this.results[i]);
        }
        return ageableAlcoholStacks;
    }

    public Ingredient getCatalyst() {
        return this.ingredients.get(0);
    }


    public NonNullList<FluidIngredient> getFluidIngredients() {
        return fluidIngredients;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matches(RecipeInventory pContainer, Level pLevel) {
        this.ingredients.forEach(i -> matching.put(i, false));
        this.fluidIngredients.forEach(i -> matchingFluids.put(i, false));

        if (!pContainer.canStorageFluid()) throw new IllegalStateException("You can't have recipe inventory for this recipe without fluid");

        getFluidIngredients().forEach(((ingredient) -> {
            for (int i = 0; i < pContainer.getFluidHandler().getTanks(); i++) {
                if (ingredient.test(pContainer.getFluidHandler().getFluidInTank(i), true)) {
                    matchingFluids.put(ingredient, true);
                }
            }
        }));

        ingredients.forEach(((ingredient) -> {
            for (int i = 0; i < pContainer.getContainerSize(); i++) {
                if (ingredient.test(pContainer.getItem(i))) {
                    matching.put(ingredient, true);
                }
            }
        }));

        return !matching.containsValue(false) && !matchingFluids.containsValue(false);
    }

    public static class Serializer implements RecipeSerializer<FermentationRecipe> {

        @Override
        public FermentationRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            Ingredient[] ingredients = BrewchemyJsonUtil.deserializeIngredients(json);
            FluidIngredient[] fluidIngredients = BrewchemyJsonUtil.deserializeFluidIngredients(json);
            FluidStack resultStack = BrewchemyJsonUtil.deserializeFluidStack(json);
            int duration = JsonUtils.getIntOr("duration", json, 0);
            JsonObject obj = json.getAsJsonObject("perfection_range");
            return new FermentationRecipe(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), pRecipeId, group, resultStack, (SizeSpecificIngredient[]) ingredients, fluidIngredients, duration);
        }


        @Override
        public @Nullable FermentationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
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
            FluidStack resultStack = pBuffer.readFluidStack();
            int duration = pBuffer.readVarInt();

            return new FermentationRecipe(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), pRecipeId, group, resultStack, ingredients, fluid_ingredients, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, FermentationRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pBuffer.writeVarInt(pRecipe.getIngredients().size());
            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeVarInt(pRecipe.getFluidIngredients().size());
            for (FluidIngredient ingredient : pRecipe.getFluidIngredients()) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeFluidStack(pRecipe.getRecipeResults(RegistryAccess.EMPTY)[0]);
            pBuffer.writeVarInt(pRecipe.getDuration());
        }
    }

    public static class RecipeBuilder extends RecipeFactory {

        protected final FluidStack result;
        protected final SizeSpecificIngredient[] ingredient;
        protected final FluidIngredient[] fingredient;

        public RecipeBuilder(ResourceLocation name, FluidStack result, SizeSpecificIngredient catalyst, FluidIngredient alcohol, int duration) {
            super(name, duration, BrewchemyRegistry.Recipes.FERMENTATION_RECIPE_SERIALIZER.get());
            this.ingredient = new SizeSpecificIngredient[] { catalyst };
            this.fingredient = new FluidIngredient[] { alcohol };
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
            json.add("result", Util.serializeFluidStack(result));
        }
    }

}
