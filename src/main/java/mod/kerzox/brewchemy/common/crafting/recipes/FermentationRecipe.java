package mod.kerzox.brewchemy.common.crafting.recipes;

import com.google.gson.JsonObject;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.CountSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.OldFluidIngredient;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.Recipes.FERMENTATION_RECIPE_SERIALIZER;

public class FermentationRecipe extends AbstractRecipe {

    private final NonNullList<SizeSpecificIngredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final FluidStack result;

    public FermentationRecipe(RecipeType<?> type, ResourceLocation id, String group, FluidStack result, FluidIngredient fluidIngredient, SizeSpecificIngredient ingredients) {
        super(type, id, group, 0, FERMENTATION_RECIPE_SERIALIZER.get());
        this.result = result;

        // fermentation will only take 1 ingredient and 1 fluid ingredient
        this.ingredients.add(ingredients);
        this.fluidIngredients.add(fluidIngredient);
    }

    public FluidStack getResultFluid() {
        return result;
    }

    public FluidStack assembleFluid(RecipeInventoryWrapper wrapper) {
        return this.result.copy();
    }

    @Override
    public boolean matches(RecipeInventoryWrapper pContainer, Level pLevel) {
        if (!pContainer.canStorageFluid()) return false;
        return getFluidIngredient().test(pContainer.getFluidInventory().getFluidInTank(0)) && hasCatalyst(pContainer.getItem(0));
    }

    public boolean hasCatalyst(ItemStack cat) {
        return getCatalystIngredient().test(cat);
    }

    public FluidIngredient getFluidIngredient() {
        return fluidIngredients.get(0);
    }

    public SizeSpecificIngredient getCatalystIngredient() {
        return this.ingredients.get(0);
    }

    @Override
    public ItemStack assemble(RecipeInventoryWrapper pContainer) {
        return FluidUtil.getFilledBucket(result.copy());
    }

    @Override
    public ItemStack getResultItem() {
        return FluidUtil.getFilledBucket(result);
    }

    public static class Serializer implements RecipeSerializer<FermentationRecipe> {

        @Override
        public FermentationRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            String group = SomeJsonUtil.getStringOr("group", pSerializedRecipe, "");
            JsonObject ingredients = pSerializedRecipe.getAsJsonObject("ingredients");
            SizeSpecificIngredient ingredient = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredients.getAsJsonObject("ingredient"));
            FluidIngredient fluidIngredient = FluidIngredient.of(ingredients.getAsJsonObject("fluid_ingredient"));
            FluidStack result = SomeJsonUtil.deserializeFluidStack(pSerializedRecipe);
            return new FermentationRecipe(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), pRecipeId, group, result, fluidIngredient, ingredient);
        }

        @Override
        public @Nullable FermentationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            SizeSpecificIngredient ingredient = (SizeSpecificIngredient) Ingredient.fromNetwork(pBuffer);
            FluidIngredient fluidIngredient = (FluidIngredient) Ingredient.fromNetwork(pBuffer);
            FluidStack result = pBuffer.readFluidStack();
            return new FermentationRecipe(BrewchemyRegistry.Recipes.FERMENTATION_RECIPE.get(), pRecipeId, group, result, fluidIngredient, ingredient);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, FermentationRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pRecipe.getCatalystIngredient().toNetwork(pBuffer);
            pRecipe.getFluidIngredient().toNetwork(pBuffer);
            pBuffer.writeFluidStack(pRecipe.getResultFluid());
        }
    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final SizeSpecificIngredient ingredient;
        private final FluidIngredient fluidIngredient;
        private final FluidStack result;
        private String group;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, String group, SizeSpecificIngredient ingredient, FluidIngredient fluidIngredient, FluidStack result, RecipeSerializer<?> supplier) {
            this.name = name;
            this.ingredient = ingredient;
            this.fluidIngredient = fluidIngredient;
            this.result = result;
            this.group = group;
            this.supplier = supplier;
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, SizeSpecificIngredient ingredient, FluidIngredient fluidIngredient, FluidStack result) {
            return new DatagenBuilder(name, Brewchemy.MODID, ingredient, fluidIngredient, result, FERMENTATION_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.ingredient,
                    this.fluidIngredient,
                    this.result,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final SizeSpecificIngredient ingredient;
            private final FluidIngredient fluidIngredient;
            private final FluidStack result;
            private String group;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, SizeSpecificIngredient ingredient, FluidIngredient fluidIngredient, FluidStack result, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.ingredient = ingredient;
                this.fluidIngredient = fluidIngredient;
                this.result = result;
                this.supplier = supplier;
            }

            private JsonObject serializeFluidStack(FluidStack stack) {
                JsonObject json = new JsonObject();
                json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
                if (stack.getAmount() != 0) {
                    json.addProperty("amount", stack.getAmount());
                }
                return json;
            }

            @Override
            public void serializeRecipeData(JsonObject pJson) {
                pJson.addProperty("group", group);
                JsonObject ingredient = new JsonObject();
                ingredient.add("ingredient", this.ingredient.toJson());
                ingredient.add("fluid_ingredient", this.fluidIngredient.toJson());
                pJson.add("ingredients", ingredient);
                pJson.add("result", serializeFluidStack(result));
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
