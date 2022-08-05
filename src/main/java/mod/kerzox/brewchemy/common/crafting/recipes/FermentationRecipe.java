package mod.kerzox.brewchemy.common.crafting.recipes;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.CountSpecificIngredient;
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
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

public class FermentationRecipe extends AbstractRecipe {

    private final NonNullList<CountSpecificIngredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final FluidStack result;

    public FermentationRecipe(RecipeType<?> type, ResourceLocation id, String group, FluidStack result, CountSpecificIngredient[] ingredient) {
        super(type, id, group, 0);
        this.result = result;
        this.ingredients.addAll(Arrays.asList(ingredient));
        this.fluidIngredients.addAll(Collections.singleton(FluidIngredient.of(result)));
    }

    @Override
    public boolean matches(RecipeInventoryWrapper inv, Level pLevel) {
        boolean fluid = false;
        for (FluidIngredient fluidIngredient : fluidIngredients) {
            if (fluidIngredient.testPartial(inv.getFluidInventory().getFluidInTank(0))) {
                fluid = true;
            }
        }
        return fluid && hasCatalyst(inv.getItem(0), inv.getFluidInventory().getFluidInTank(0));
    }

    public boolean hasCatalyst(ItemStack cat, FluidStack stack) {
        return getCatalystIngredient().test(cat);
    }

    public FluidIngredient getFermentationFluid() {
        return this.fluidIngredients.get(0);
    }

    public CountSpecificIngredient getCatalystIngredient() {
        return this.ingredients.get(0);
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return this.fluidIngredients;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    public NonNullList<CountSpecificIngredient> getCountSpecificIngredients() {
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


    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<FermentationRecipe> {

        @Override
        public FermentationRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            CountSpecificIngredient[] ingredient = SomeJsonUtil.deserializeCountSpecificIngredients(json);
            FluidStack resultStack = SomeJsonUtil.deserializeFluidStack(json);
            return new FermentationRecipe(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), pRecipeId, group, resultStack, ingredient);
        }


        @Override
        public @Nullable FermentationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            int ingredientCount = pBuffer.readVarInt();
            CountSpecificIngredient[] ingredients = new CountSpecificIngredient[ingredientCount];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = CountSpecificIngredient.Serializer.INSTANCE.parse(pBuffer);
            }
            FluidStack resultStack = pBuffer.readFluidStack();
            return new FermentationRecipe(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), pRecipeId, group, resultStack, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, FermentationRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pBuffer.writeVarInt(pRecipe.getIngredients().size());
            for (CountSpecificIngredient ingredient : pRecipe.getCountSpecificIngredients()) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeFluidStack(pRecipe.getResultFluid());
        }
    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final CountSpecificIngredient ingredient;
        private final FluidStack result;
        private String group;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, CountSpecificIngredient ingredient, FluidStack result, RecipeSerializer<?> supplier) {
            this.name = name;
            this.ingredient = ingredient;
            this.result = result;
            this.group = group;
            this.supplier = supplier;
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, CountSpecificIngredient ingredient, FluidStack result) {
            return new DatagenBuilder(name, ingredient, result, BrewchemyRegistry.Recipes.FERMENTATION_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.ingredient,
                    this.result,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final CountSpecificIngredient ingredient;
            private final FluidStack result;
            private String group;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, CountSpecificIngredient ingredient, FluidStack result, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.ingredient = ingredient;
                this.result = result;
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
                json.add("item_ingredient", this.ingredient.toJson());
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
