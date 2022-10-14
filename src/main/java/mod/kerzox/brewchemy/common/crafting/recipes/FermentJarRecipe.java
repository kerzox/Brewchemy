package mod.kerzox.brewchemy.common.crafting.recipes;

import com.google.gson.JsonObject;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.crafting.AbstractRecipe;
import mod.kerzox.brewchemy.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
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
import java.util.Objects;
import java.util.function.Consumer;

public class FermentJarRecipe extends AbstractRecipe {

    private final NonNullList<FluidIngredient> ingredients = NonNullList.create();
    private final ItemStack result;

    public FermentJarRecipe(RecipeType<?> type, ResourceLocation id, String group, ItemStack result, FluidIngredient ingredients, int duration) {
        super(type, id, group, duration, BrewchemyRegistry.Recipes.FERMENTS_JAR_RECIPE_SERIALIZER.get());
        this.result = result;
        this.ingredients.add(ingredients);
    }

    @Override
    public boolean matches(RecipeInventoryWrapper inv, Level pLevel) {
        if (!inv.canStorageFluid()) return false;
        return getFluidIngredient().test(inv.getFluidInventory().getFluidInTank(0));
    }

    public FluidIngredient getFluidIngredient() {
        return this.ingredients.get(0);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    @Override
    public ItemStack assemble(RecipeInventoryWrapper pContainer) {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    public static class Serializer implements RecipeSerializer<FermentJarRecipe> {

        @Override
        public FermentJarRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            String group = SomeJsonUtil.getStringOr("group", json, "");
            FluidIngredient ingredient = FluidIngredient.of(json.getAsJsonObject("fluid_ingredient"));
            ItemStack resultStack = SomeJsonUtil.deserializeItemStack(json);
            int duration = SomeJsonUtil.getIntOr("duration", json, 0);
            return new FermentJarRecipe(BrewchemyRegistry.Recipes.FERMENTS_JAR_RECIPE.get(), pRecipeId, group, resultStack, ingredient, duration);
        }


        @Override
        public @Nullable FermentJarRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            FluidIngredient ingredient = (FluidIngredient) Ingredient.fromNetwork(pBuffer);
            ItemStack resultStack = pBuffer.readItem();
            int duration = pBuffer.readVarInt();
            return new FermentJarRecipe(BrewchemyRegistry.Recipes.FERMENTS_JAR_RECIPE.get(), pRecipeId, group, resultStack, ingredient, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, FermentJarRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pRecipe.getFluidIngredient().toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.getResultItem());
            pBuffer.writeVarInt(pRecipe.getDuration());
        }
    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final ItemStack result;
        private final FluidIngredient ingredient;
        private String group;
        final int duration;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, ItemStack result, FluidIngredient ingredient, int duration, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.ingredient = ingredient;
            this.group = Brewchemy.MODID;
            this.duration = duration;
            this.supplier = supplier;
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, ItemStack result, FluidIngredient ingredient, int duration) {
            return new DatagenBuilder(name, result, ingredient, duration, BrewchemyRegistry.Recipes.FERMENTS_JAR_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.result,
                    this.ingredient,
                    this.duration,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final ItemStack result;
            private final FluidIngredient ingredient;
            private String group;
            final int duration;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, ItemStack result, FluidIngredient ingredient, int duration, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.result = result;
                this.ingredient = ingredient;
                this.duration = duration;
                this.supplier = supplier;
            }

            private JsonObject serializeItemStacks(ItemStack stack) {
                JsonObject json = new JsonObject();
                json.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
                if (stack.getCount() != 0) {
                    json.addProperty("count", stack.getCount());
                }
                return json;
            }

            @Override
            public void serializeRecipeData(JsonObject json) {
                json.addProperty("group", this.group);
                json.addProperty("duration", this.duration);
                json.add("fluid_ingredient", this.ingredient.toJson());
                json.add("result", serializeItemStacks(this.result));
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
