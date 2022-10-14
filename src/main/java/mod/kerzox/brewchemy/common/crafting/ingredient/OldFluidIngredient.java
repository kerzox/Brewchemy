package mod.kerzox.brewchemy.common.crafting.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.kerzox.brewchemy.common.util.SomeJsonUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OldFluidIngredient extends AbstractIngredient {

    private FluidStack[] stacks;
    private boolean isTag;
    private TagKey<Fluid> tag;

    public OldFluidIngredient(FluidStack[] stacks, boolean isTag, TagKey<Fluid> tag) {
        this.stacks = stacks;
        this.isTag = isTag;
        this.tag = tag;
    }

    public FluidStack[] getStacks() {
        return stacks;
    }

    public int getAmountFromIngredient(FluidStack fluid) {
        for (FluidStack stack : stacks) {
            if (fluid.getFluid() == stack.getFluid()) return stack.getAmount();
        }
        return 0;
    }

    public static OldFluidIngredient of(CompoundTag tag) {
        List<FluidStack> fluidStacks = new ArrayList<>();
        fluidStacks.add(FluidStack.loadFluidStackFromNBT(tag));
        return new OldFluidIngredient((FluidStack[]) fluidStacks.toArray(), false, null);
    }

    public static FluidStack fromResourceId(ResourceLocation fluidId, int amount) {
        return new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidId)), amount);
    }

    public static OldFluidIngredient of(FluidStack... fluid) {
        return new OldFluidIngredient(fluid, false, null);
    }

    public static OldFluidIngredient of(FluidStack[] fluid, boolean isTag, TagKey<Fluid> tag) {
        return new OldFluidIngredient(fluid, isTag, tag);
    }

    public static OldFluidIngredient of(List<FluidStack> fluid) {
        return new OldFluidIngredient(fluid.toArray(new FluidStack[0]), false, null);
    }

    public static OldFluidIngredient of(List<FluidStack> fluid, boolean isTag, TagKey<Fluid> tag) {
        return new OldFluidIngredient(fluid.toArray(new FluidStack[0]), isTag, tag);
    }

    public static OldFluidIngredient fromTag(TagKey<Fluid> tag, int amount) {
        List<FluidStack> ret = new ArrayList<>();
        for (Fluid fluid : ForgeRegistries.FLUIDS.tags().getTag(tag)) {
            ret.add(new FluidStack(fluid, amount));
        }
        return OldFluidIngredient.of(ret, true, tag);
    }


    public boolean test(FluidStack fluidStack) {
        if (fluidStack == null) return false;
        for (FluidStack stack : stacks) {
            if (stack.getFluid().isSame(fluidStack.getFluid())) {
                if (stack.getAmount() <= fluidStack.getAmount()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean testPartial(FluidStack fluidStack) {
        if (fluidStack == null) return false;
        for (FluidStack stack : stacks) {
            if (stack.getFluid().isSame(fluidStack.getFluid())) {
                return true;
            }
        }
        return false;
    }

    public JsonElement serialize() {
        JsonArray arr = new JsonArray();
        if (stacks.length == 1) {
            JsonObject fluid = new JsonObject();
            if (isTag) {
                fluid.addProperty("tag", ForgeRegistries.FLUIDS.tags().getTag(tag).contains(stacks[0].getFluid()));
                fluid.addProperty("amount", stacks[0].getAmount());
            } else {
                fluid.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stacks[0].getFluid()).toString());
                fluid.addProperty("amount", stacks[0].getAmount());
            }
            return fluid;
        }
        for (FluidStack stack : stacks) {
            JsonObject fluid = new JsonObject();
            if (isTag) {
                fluid.addProperty("tag", ForgeRegistries.FLUIDS.tags().getTag(tag).contains(stack.getFluid()));
                fluid.addProperty("amount", stack.getAmount());
            } else {
                fluid.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
                fluid.addProperty("amount", stack.getAmount());
            }
            arr.add(fluid);
        }
        return arr;
    }

    public static OldFluidIngredient deserialize(JsonElement jsonElement) {
        List<FluidStack> stacks = new ArrayList<>();
        if (jsonElement.isJsonArray()) {
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                if (element instanceof JsonObject data) {
                    if (data.has("fluid")) {
                        ResourceLocation fluid = new ResourceLocation(SomeJsonUtil.getStringOr("fluid", data, ""));
                        int amount = SomeJsonUtil.getIntOr("amount", data, 0);
                        stacks.add(fromResourceId(fluid, amount));
                    }
                }
            }
        } else {
            JsonObject data = jsonElement.getAsJsonObject();
            if (data.has("fluid")) {
                ResourceLocation fluid = new ResourceLocation(SomeJsonUtil.getStringOr("fluid", data, ""));
                int amount = SomeJsonUtil.getIntOr("amount", data, 0);
                stacks.add(fromResourceId(fluid, amount));
            }
        }
        return OldFluidIngredient.of(stacks);
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
        return serialize();
    }

    public static OldFluidIngredient fromNetwork(FriendlyByteBuf pBuffer) {
        var size = pBuffer.readVarInt();
        FluidStack[] array = new FluidStack[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = pBuffer.readFluidStack();
        }
        return OldFluidIngredient.of(array);
    }

    public static class Serializer implements IIngredientSerializer<OldFluidIngredient>
    {
        public static final OldFluidIngredient.Serializer INSTANCE = new OldFluidIngredient.Serializer();

        @Override
        public OldFluidIngredient parse(FriendlyByteBuf buffer) {
            return OldFluidIngredient.fromNetwork(buffer);
//            boolean partial = buffer.readBoolean();
//            int count = buffer.readInt();
//            return FluidIngredient.of(Ingredient.fromNetwork(buffer), count, partial);
        }

        @Override
        public OldFluidIngredient parse(JsonObject json) {
            return OldFluidIngredient.deserialize(json);
//            boolean partial = SomeJsonUtil.getBooleanOr("partial_match", json, true);
//            int count = SomeJsonUtil.getIntOr("count", json, 1);
//            return FluidIngredient.of(Ingredient.fromJson(json), count, partial);
        }

        @Override
        public void write(FriendlyByteBuf buffer, OldFluidIngredient ingredient) {
            buffer.writeVarInt(ingredient.getStacks().length);
            for (FluidStack stack : ingredient.getStacks()) {
                buffer.writeFluidStack(stack);
            }

//            buffer.writeBoolean(ingredient.partialMatch);
//            buffer.writeInt(ingredient.count);
//            ingredient.vanillaIngredient.toNetwork(buffer);
        }
    }
}
