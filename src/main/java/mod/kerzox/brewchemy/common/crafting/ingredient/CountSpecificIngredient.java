package mod.kerzox.brewchemy.common.crafting.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CountSpecificIngredient extends AbstractIngredient {

    protected Ingredient vanillaIngredient;
    private final boolean partialMatch;
    private final ItemStack[] realItemStacks;
    private final int count;

    protected CountSpecificIngredient(Ingredient ingredient, int count, boolean partial) {
        super(Arrays.stream(ingredient.getItems()).map((Function<ItemStack, Value>) ItemValue::new));
        this.partialMatch = partial;
        this.vanillaIngredient = ingredient;
        this.count = count;
        this.realItemStacks = new ItemStack[ingredient.getItems().length];
        for (int i = 0; i < this.realItemStacks.length; i++) {
            this.realItemStacks[i] = new ItemStack(ingredient.getItems()[i].getItem(), count);
        }
    }

    public static CountSpecificIngredient of(Ingredient ingredient, int count) {
        return new CountSpecificIngredient(ingredient, count,true);
    }

    public static CountSpecificIngredient of(Ingredient ingredient, int count, boolean partialMatch) {
        return new CountSpecificIngredient(ingredient, count, partialMatch);
    }

    @Override
    public ItemStack[] getItems() {
        return this.realItemStacks;
    }

    @Override
    public boolean test(@Nullable ItemStack stack1) {
        if (stack1 == null) {
            return false;
        } else {
            this.invalidate();
            if (this.getItems().length == 0) {
                return stack1.isEmpty();
            } else {
                for(ItemStack itemstack : this.getItems()) {
                    if (partialMatch) {
                        if (itemstack.is(stack1.getItem()) && itemstack.getCount() <= stack1.getCount()) {
                            return true;
                        }
                    }
                    else if (itemstack.is(stack1.getItem()) && itemstack.getCount() == stack1.getCount()) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject ret = new JsonObject();
        JsonElement json = vanillaIngredient.toJson();
        if (!json.isJsonArray()) {
            JsonObject object = json.getAsJsonObject();
            object.addProperty("partial_match", partialMatch);
            object.addProperty("count", count);
        } else {
            ret.addProperty("partial_match", partialMatch);
            ret.addProperty("count", count);
            ret.add("items", json);
        }
        return json;
    }

    public Ingredient asIngredient() {
        return this.vanillaIngredient;
    }

    public static class Serializer implements IIngredientSerializer<CountSpecificIngredient>
    {
        public static final CountSpecificIngredient.Serializer INSTANCE = new CountSpecificIngredient.Serializer();

        @Override
        public CountSpecificIngredient parse(FriendlyByteBuf buffer) {
            boolean partial = buffer.readBoolean();
            int count = buffer.readInt();
            return CountSpecificIngredient.of(Ingredient.fromNetwork(buffer), count, partial);
        }

        @Override
        public CountSpecificIngredient parse(JsonObject json) {
            boolean partial = JsonUtils.getBooleanOr("partial_match", json, true);
            int count = JsonUtils.getIntOr("count", json, 1);
            return CountSpecificIngredient.of(Ingredient.fromJson(json), count, partial);
        }

        @Override
        public void write(FriendlyByteBuf buffer, CountSpecificIngredient ingredient) {
            buffer.writeBoolean(ingredient.partialMatch);
            buffer.writeInt(ingredient.count);
            ingredient.vanillaIngredient.toNetwork(buffer);
        }
    }
}

