package mod.kerzox.brewchemy.common.crafting;

import com.google.gson.JsonObject;
import mod.kerzox.brewchemy.Brewchemy;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class RecipeFactory implements FinishedRecipe {
    private final ResourceLocation name;
    private String group;
    final int duration;
    private final RecipeSerializer<?> supplier;

    public RecipeFactory(ResourceLocation name, int duration, RecipeSerializer<?> supplier) {
        this.name = name;
        this.group = Brewchemy.MODID;
        this.duration = duration;
        this.supplier = supplier;
    }

    public void build(Consumer<FinishedRecipe> consumer) {
        consumer.accept(this);
    }

    private JsonObject serializeItemStacks(ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
        if (stack.getCount() != 0) {
            json.addProperty("count", stack.getCount());
        }
        return json;
    }

    protected abstract void writeRecipe(JsonObject json);

    @Override
    public void serializeRecipeData(JsonObject json) {
        if (!this.group.isEmpty()) {
            json.addProperty("group", this.group);
        }
        json.addProperty("duration", this.duration);
        writeRecipe(json);
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

    protected static class Util {

        public static JsonObject serializeItemStack(ItemStack stack) {
            JsonObject json = new JsonObject();
            json.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
            if (stack.getCount() != 0) {
                json.addProperty("count", stack.getCount());
            }
            return json;
        }

        public static JsonObject serializeFluidStack(FluidStack stack) {
            JsonObject json = new JsonObject();
            json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
            if (stack.getAmount() != 0) {
                json.addProperty("amount", stack.getAmount());
            }
            return json;
        }

    }

}