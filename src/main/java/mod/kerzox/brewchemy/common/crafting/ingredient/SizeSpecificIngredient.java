package mod.kerzox.brewchemy.common.crafting.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.kerzox.brewchemy.common.util.SomeJsonUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SizeSpecificIngredient extends AbstractIngredient {

    protected Ingredient actual;
    private int size;

    public SizeSpecificIngredient(Ingredient ingredient, int size) {
        super(Arrays.stream(ingredient.getItems()).map((Function<ItemStack, Value>) ItemValue::new));
        this.size = size;
        this.actual = ingredient;
    }

    public static SizeSpecificIngredient of(Ingredient ingredient, int size) {
        return new SizeSpecificIngredient(ingredient, size);
    }

    public static SizeSpecificIngredient of(ItemStack itemStack) {
        return new SizeSpecificIngredient(Ingredient.of(itemStack), itemStack.getCount());
    }

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        if (pStack == null) return false;
        return Arrays.stream(getItems()).anyMatch(i -> i.is(pStack.getItem())) && pStack.getCount() >= size;
    }

    public boolean testExactMatch(@Nullable ItemStack pStack) {
        if (pStack == null) return false;
        return Arrays.stream(getItems()).anyMatch(i -> i.is(pStack.getItem())) && pStack.getCount() == size;
    }

    public int getSize() {
        return size;
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
        JsonElement json = actual.toJson();
        if (!json.isJsonArray()) {
            JsonObject object = json.getAsJsonObject();
            object.addProperty("size", size);
        } else {
            ret.addProperty("size", size);
            ret.add("items", json);
        }
        return json;
    }


    public static class Serializer implements IIngredientSerializer<SizeSpecificIngredient>
    {
        public static final SizeSpecificIngredient.Serializer INSTANCE = new SizeSpecificIngredient.Serializer();

        @Override
        public SizeSpecificIngredient parse(FriendlyByteBuf buffer) {
            int size = buffer.readInt();
            return new SizeSpecificIngredient(Ingredient.fromNetwork(buffer), size);
        }

        @Override
        public SizeSpecificIngredient parse(JsonObject json) {
            int size = SomeJsonUtil.getIntOr("size", json, 1);
            return new SizeSpecificIngredient(Ingredient.fromJson(json), size);
        }

        @Override
        public void write(FriendlyByteBuf buffer, SizeSpecificIngredient ingredient) {
            buffer.writeInt(ingredient.size);
            ingredient.actual.toNetwork(buffer);
        }
    }

}
