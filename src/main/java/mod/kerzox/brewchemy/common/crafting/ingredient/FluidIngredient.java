package mod.kerzox.brewchemy.common.crafting.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import javax.json.Json;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FluidIngredient implements Predicate<FluidStack> {

    private FluidStack[] stacks;
    private boolean isTag;
    private TagKey<Fluid> tag;

    public FluidIngredient(FluidStack[] stacks, boolean isTag, TagKey<Fluid> tag) {
        this.stacks = stacks;
        this.isTag = isTag;
        this.tag = tag;
    }

    public FluidStack[] getStacks() {
        return stacks;
    }

    public static FluidStack fromResourceId(ResourceLocation fluidId, int amount) {
        return new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidId), amount);
    }

    public static FluidIngredient of(FluidStack... fluid) {
        return new FluidIngredient(fluid, false, null);
    }

    public static FluidIngredient of(FluidStack[] fluid, boolean isTag, TagKey<Fluid> tag) {
        return new FluidIngredient(fluid, isTag, tag);
    }

    public static FluidIngredient of(List<FluidStack> fluid) {
        return new FluidIngredient(fluid.toArray(new FluidStack[0]), false, null);
    }

    public static FluidIngredient of(List<FluidStack> fluid, boolean isTag, TagKey<Fluid> tag) {
        return new FluidIngredient(fluid.toArray(new FluidStack[0]), isTag, tag);
    }

    public static FluidIngredient fromTag(TagKey<Fluid> tag, int amount) {
        List<FluidStack> ret = new ArrayList<>();
        for (Fluid fluid : ForgeRegistries.FLUIDS.tags().getTag(tag)) {
            ret.add(new FluidStack(fluid, amount));
        }
        return FluidIngredient.of(ret, true, tag);
    }

    @Override
    public boolean test(FluidStack fluidStack) {
        if (fluidStack == null) return false;
        for (FluidStack stack : stacks) {
            if (stack.isFluidStackIdentical(fluidStack)) return true;
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

    public static FluidIngredient deserialize(JsonElement jsonElement) {
        List<FluidStack> stacks = new ArrayList<>();
        if (jsonElement.isJsonArray()) {
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                if (element instanceof JsonObject data) {
                    if (data.has("fluid")) {
                        ResourceLocation fluid = new ResourceLocation(JsonUtils.getStringOr("fluid", data, ""));
                        int amount = JsonUtils.getIntOr("amount", data, 0);
                        stacks.add(fromResourceId(fluid, amount));
                    }
                }
            }
        } else {
            JsonObject data = jsonElement.getAsJsonObject();
            if (data.has("fluid")) {
                ResourceLocation fluid = new ResourceLocation(JsonUtils.getStringOr("fluid", data, ""));
                int amount = JsonUtils.getIntOr("amount", data, 0);
                stacks.add(fromResourceId(fluid, amount));
            }
        }
        return FluidIngredient.of(stacks);
    }

    public void toNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeCollection(Arrays.asList(this.stacks), FriendlyByteBuf::writeFluidStack);
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf pBuffer) {
        var size = pBuffer.readVarInt();
        FluidStack[] array = new FluidStack[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = pBuffer.readFluidStack();
        }
        return FluidIngredient.of(array);
    }

}
