package mod.kerzox.brewchemy.common.crafting.recipes;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.util.SomeJsonUtil;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

public class BrewingRecipe extends AbstractRecipe {

    public static final int NO_HEAT = 0;
    public static final int FIRE = 600;
    public static final int SUPERHEATED = 1000;

    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final FluidStack result;
    private final int heat;

    public BrewingRecipe(RecipeType<?> type, ResourceLocation id, String group, FluidStack result, Ingredient[] ingredients, FluidIngredient[] fingredients, int duration, int heat) {
        super(type, id, group, duration);
        this.result = result;
        this.heat = heat;
        this.ingredients.addAll(Arrays.asList(ingredients));
        this.fluidIngredients.addAll(Arrays.asList(fingredients));
    }

    @Override
    public boolean matches(RecipeInventoryWrapper inv, Level pLevel) {
        boolean fluidMatches = false;
        boolean itemMatches = false;
        for (int i = 0; i < inv.getFluidInventory().getTanks(); i++) {
            FluidStack fluid = inv.getFluidInventory().getFluidInTank(i);
            for (FluidIngredient fluidIngredient : fluidIngredients) {
                if (fluidIngredient.test(new FluidStack(fluid.getFluid(), fluidIngredient.getAmountFromIngredient()))) {
                    if (fluid.getAmount() >= fluidIngredient.getAmountFromIngredient()) fluidMatches = true;
                }
            }
        }
        for (Ingredient ingredient : ingredients) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (ingredient.test(inv.getItem(i))) {
                    itemMatches = true;
                }
            }
        }

        if (!fluidMatches) return false;
        if (!itemMatches) return false;
        return true;
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return this.fluidIngredients;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public FluidStack assembleFluid(RecipeInventoryWrapper wrapper) {
        return this.result.copy();
    }

    @Override
    public ItemStack assemble(RecipeInventoryWrapper pContainer) {
        return ItemStack.EMPTY;
    }

    public FluidStack getResultFluid() {
        return this.result;
    }

    public int getHeat() {
        return heat;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<BrewingRecipe> {

        @Override
        public BrewingRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            Ingredient[] ingredients = SomeJsonUtil.deserializeIngredients(json);
            FluidIngredient[] fluidIngredients = SomeJsonUtil.deserializeFluidIngredients(json);
            FluidStack resultStack = FluidStack.EMPTY;
            if (json.has("result")) {
                ResourceLocation fluid = new ResourceLocation(JsonUtils.getStringOr("fluid", json.getAsJsonObject("result"), ""));
                int amount = JsonUtils.getIntOr("amount", json.getAsJsonObject("result"), 0);
                resultStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid), amount);
            }
            int duration = JsonUtils.getIntOr("duration", json, 0);
            int heat = JsonUtils.getIntOr("heat", json, 0);
            return new BrewingRecipe(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), pRecipeId, group, resultStack, ingredients, fluidIngredients, duration, heat);
        }


        @Override
        public @Nullable BrewingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            int ingredientCount = pBuffer.readVarInt();
            Ingredient[] ingredients = new Ingredient[ingredientCount];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = Ingredient.fromNetwork(pBuffer);
            }
            int fluidIngredientCount = pBuffer.readVarInt();
            FluidIngredient[] fluidIngredients = new FluidIngredient[fluidIngredientCount];
            for (int i = 0; i < ingredients.length; i++) {
                fluidIngredients[i] = FluidIngredient.fromNetwork(pBuffer);
            }
            FluidStack resultStack = pBuffer.readFluidStack();
            int duration = pBuffer.readVarInt();
            int heat = pBuffer.readVarInt();
            return new BrewingRecipe(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), pRecipeId, group, resultStack, ingredients, fluidIngredients, duration, heat);
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
            pBuffer.writeFluidStack(pRecipe.getResultFluid());
            pBuffer.writeVarInt(pRecipe.getDuration());
            pBuffer.writeVarInt(pRecipe.getHeat());
        }
    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final FluidStack result;
        private final Ingredient ingredient;
        private final FluidIngredient fluidIngredient;
        private String group;
        private final int duration;
        private final int heat;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, FluidStack result, Ingredient ingredient, FluidIngredient fluidIngredient, int duration, int heat, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.ingredient = ingredient;
            this.fluidIngredient = fluidIngredient;
            this.heat = heat;
            this.group = group;
            this.duration = duration;
            this.supplier = supplier;
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, FluidStack result, Ingredient ingredient, FluidIngredient fluidIngredient, int duration, int heat) {
            return new DatagenBuilder(name, result, ingredient, fluidIngredient, duration, heat, BrewchemyRegistry.Recipes.BREWING_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.result,
                    this.ingredient,
                    this.fluidIngredient,
                    this.duration,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final FluidStack result;
            private final Ingredient ingredient;
            private final FluidIngredient fluidIngredient;
            private String group;
            final int duration;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, FluidStack result, Ingredient ingredient, FluidIngredient fluidIngredient, int duration, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.result = result;
                this.ingredient = ingredient;
                this.fluidIngredient = fluidIngredient;
                this.duration = duration;
                this.supplier = supplier;
            }

            private JsonObject serializeFluidStacks(FluidStack stack) {
                JsonObject json = new JsonObject();
                json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
                if (stack.getAmount() != 0) {
                    json.addProperty("amount", stack.getAmount());
                }
                return json;
            }

            @Override
            public void serializeRecipeData(JsonObject json) {
                if (!this.group.isEmpty()) {
                    json.addProperty("group", this.group);
                }
                json.addProperty("duration", this.duration);
                JsonObject ingredient = new JsonObject();
                ingredient.add("item", this.ingredient.toJson());
                ingredient.add("fluid", this.fluidIngredient.serialize());
                json.add("ingredient", ingredient);
                json.add("result", serializeFluidStacks(this.result));
            }

            @Override
            public ResourceLocation getId() {
                return this.name;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return this.supplier;
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        }
    }

}
