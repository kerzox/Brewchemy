package mod.kerzox.brewchemy.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.capabilities.fluid.SidedMultifluidTank;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.jline.terminal.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class BrewingRecipe extends AbstractRecipe {

    public static final int NO_HEAT = 0;
    public static final int FIRE = 600;
    public static final int SUPERHEATED = 1000;

    private final NonNullList<SizeSpecificIngredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final FluidStack result;
    private final int heat;

    public BrewingRecipe(RecipeType<?> type, ResourceLocation id, String group, FluidStack result, SizeSpecificIngredient[] ingredients, FluidIngredient fingredients, int duration, int heat) {
        super(type, id, group, duration, BrewchemyRegistry.Recipes.BREWING_RECIPE_SERIALIZER.get());
        this.result = result;
        this.heat = heat;
        this.ingredients.addAll(Arrays.asList(ingredients));
        this.fluidIngredients.add(fingredients);
    }

    @Override
    public boolean matches(RecipeInventoryWrapper inv, Level pLevel) {
        if (!inv.canStorageFluid()) return false;

        boolean[] itemsMatch = new boolean[this.ingredients.size()];
        for (SizeSpecificIngredient ingredient : this.ingredients) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (ingredient.test(inv.getItem(i))) {
                    itemsMatch[this.ingredients.indexOf(ingredient)] = true;
                }
            }
        }
        for (boolean match : itemsMatch) {
            if (!match) return false;
        }

        if (inv.getFluidInventory() instanceof SidedMultifluidTank sidedTank) {

            for (int i = 0; i < sidedTank.getInputHandler().getTanks(); i++) {
                if (getFluidIngredient().test(sidedTank.getInputHandler().getFluidInTank(i))) {
                    return true;
                }
            }

            for (int i = 0; i < sidedTank.getOutputHandler().getTanks(); i++) {
                FluidStack fluidInUse = sidedTank.getOutputHandler().getFluidInTank(i);
                if (getFluidIngredient().test(sidedTank.getOutputHandler().getFluidInTank(i))) {
                    // we need to move the output fluid back to the input
                    if (inv.sideAgnostic()) {
                        if (sidedTank.fill(fluidInUse, IFluidHandler.FluidAction.SIMULATE) > 0) {
                            sidedTank.fill(fluidInUse, IFluidHandler.FluidAction.EXECUTE);
                            sidedTank.getOutputHandler().getFluidInTank(i).shrink(fluidInUse.getAmount());

                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }



        } else {
            for (int i = 0; i < inv.getFluidInventory().getTanks(); i++) {
                if (getFluidIngredient().test(inv.getFluidInventory().getFluidInTank(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    public FluidIngredient getFluidIngredient() {
        return this.fluidIngredients.get(0);
    }

    public NonNullList<SizeSpecificIngredient> getSizedIngredients() {
        return ingredients;
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
            String group = SomeJsonUtil.getStringOr("group", json, "");
            JsonObject ingredients = json.getAsJsonObject("ingredients");

            List<SizeSpecificIngredient> ingredientList = new ArrayList<>();

            if (!ingredients.isJsonArray()) {
                ingredientList.add(SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredients.getAsJsonObject("ingredient")));
            } else {
                for (JsonElement element : ingredients.getAsJsonArray()) {
                    ingredientList.add(SizeSpecificIngredient.Serializer.INSTANCE.parse(element.getAsJsonObject()));
                }
            }


            FluidIngredient fluidIngredient = FluidIngredient.of(ingredients.getAsJsonObject("fluid_ingredient"));
            FluidStack result = SomeJsonUtil.deserializeFluidStack(json);
            int duration = SomeJsonUtil.getIntOr("duration", json, 0);
            int heat = SomeJsonUtil.getIntOr("heat", json, 0);
            return new BrewingRecipe(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), pRecipeId, group, result, ingredientList.toArray(SizeSpecificIngredient[]::new), fluidIngredient, duration, heat);
        }


        @Override
        public @Nullable BrewingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();

            SizeSpecificIngredient[] ingredients = new SizeSpecificIngredient[pBuffer.readVarInt()];

            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = (SizeSpecificIngredient) Ingredient.fromNetwork(pBuffer);
            }

            FluidIngredient fluidIngredient = (FluidIngredient) Ingredient.fromNetwork(pBuffer);
            FluidStack result = pBuffer.readFluidStack();
            int duration = pBuffer.readVarInt();
            int heat = pBuffer.readVarInt();
            return new BrewingRecipe(BrewchemyRegistry.Recipes.BREWING_RECIPE.get(), pRecipeId, group, result, ingredients, fluidIngredient, duration, heat);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, BrewingRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());

            pBuffer.writeVarInt(pRecipe.ingredients.size());
            for (SizeSpecificIngredient ingredient : pRecipe.ingredients) {
                ingredient.toNetwork(pBuffer);
            }

            pRecipe.getFluidIngredient().toNetwork(pBuffer);
            pBuffer.writeFluidStack(pRecipe.getResultFluid());
            pBuffer.writeVarInt(pRecipe.getDuration());
            pBuffer.writeVarInt(pRecipe.getHeat());
        }
    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final FluidStack result;
        private final SizeSpecificIngredient[] ingredient;
        private final FluidIngredient fluidIngredient;
        private String group;
        private final int duration;
        private final int heat;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, FluidStack result, SizeSpecificIngredient[] ingredient, FluidIngredient fluidIngredient, int duration, int heat, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.ingredient = ingredient;
            this.fluidIngredient = fluidIngredient;
            this.heat = heat;
            this.group = Brewchemy.MODID;
            this.duration = duration;
            this.supplier = supplier;
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, FluidStack result, SizeSpecificIngredient[] ingredient, FluidIngredient fluidIngredient, int duration, int heat) {
            return new DatagenBuilder(name, result, ingredient, fluidIngredient, duration, heat, BrewchemyRegistry.Recipes.BREWING_RECIPE_SERIALIZER.get());
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, FluidStack result, SizeSpecificIngredient ingredient, FluidIngredient fluidIngredient, int duration, int heat) {
            return new DatagenBuilder(name, result, new SizeSpecificIngredient[] {ingredient} , fluidIngredient, duration, heat, BrewchemyRegistry.Recipes.BREWING_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.result,
                    this.ingredient,
                    this.fluidIngredient,
                    this.duration,
                    this.heat,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final FluidStack result;
            private final SizeSpecificIngredient[] ingredient;
            private final FluidIngredient fluidIngredient;
            private int heat;
            private String group;
            final int duration;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, FluidStack result, SizeSpecificIngredient[] ingredient, FluidIngredient fluidIngredient, int duration, int heat, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.result = result;
                this.ingredient = ingredient;
                this.fluidIngredient = fluidIngredient;
                this.duration = duration;
                this.heat = heat;
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
                json.addProperty("heat", this.heat);
                JsonObject ingredient = new JsonObject();

                if (this.ingredient.length > 1) {
                    JsonArray arr = new JsonArray();
                    for (SizeSpecificIngredient sizeSpecificIngredient : this.ingredient) {
                        arr.add(sizeSpecificIngredient.toJson());
                    }
                    ingredient.add("ingredient", arr);
                } else {
                    ingredient.add("ingredient", this.ingredient[0].toJson());
                }

                ingredient.add("fluid_ingredient", this.fluidIngredient.toJson());
                json.add("ingredients", ingredient);
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
