package mod.kerzox.brewchemy.common.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.extensions.IForgeFriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.swing.plaf.basic.ComboPopup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
    Fluid ingredient can be initalized from a fluid or a tag
    Needs to know if we are a tag or just fluid

 */

public class FluidIngredient extends AbstractIngredient  {

    protected List<FluidStack> fluidStacks;
    protected TagKey<Fluid> tag;
    protected int amount;
    protected CompoundTag nbt;

    public FluidIngredient(List<FluidStack> stacks, TagKey<Fluid> tag, int amount, CompoundTag nbt) {
        this.fluidStacks = stacks;
        this.tag = tag;
        this.amount = amount;
        this.nbt = nbt;
    }

    public static FluidIngredient of(TagKey<Fluid> tag, int amount, CompoundTag nbt) {
        return new FluidIngredient(new ArrayList<>(), tag, amount, nbt);
    }

    public static FluidIngredient of(TagKey<Fluid> tag, int amount) {
        return new FluidIngredient(new ArrayList<>(), tag, amount, null);
    }

    public static FluidIngredient of(FluidStack... fluids) {
        return new FluidIngredient(Arrays.stream(fluids).toList(), null, fluids[0].getAmount(), null);
    }

    public static FluidIngredient of(List<FluidStack> fluids) {
        return new FluidIngredient(fluids, null, fluids.get(0).getAmount(), null);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public List<FluidStack> getFluidStacks() {
        if (tag != null) {
            for (Fluid fluid : ForgeRegistries.FLUIDS.tags().getTag(tag)) {
                this.fluidStacks.add(new FluidStack(fluid, amount));
            }
        }
        return fluidStacks;
    }

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        if (pStack == null) return false;
        return false;
    }

    public boolean testFluid(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) return false;
        return fluidStacks.stream().anyMatch(f -> f.isFluidEqual(fluidStack));
    }

    public boolean testFluidWithAmount(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) return false;
        return fluidStacks.stream().allMatch(f -> f.isFluidEqual(fluidStack) && f.getAmount() <= fluidStack.getAmount());
    }

    public boolean testExactMatch(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) return false;
        return fluidStacks.stream().allMatch(f -> f.isFluidStackIdentical(fluidStack));
    }

    public boolean test(@Nullable FluidStack pStack) {
        if (pStack == null) return false;
        return testFluidWithAmount(pStack);
    }

    @Override
    public JsonElement toJson() {
        JsonObject ret = new JsonObject();

        if (tag != null) {
            ret.addProperty("tag", tag.location().toString());
        } else {
            ret.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(fluidStacks.get(0).getFluid()).toString());
        }

        ret.addProperty("amount", amount);

        return ret;
    }

    public static class Serializer implements IIngredientSerializer<FluidIngredient> {
        public static final FluidIngredient.Serializer INSTANCE = new FluidIngredient.Serializer();

        @Override
        public FluidIngredient parse(FriendlyByteBuf buffer) {
            boolean tag = buffer.readBoolean();
            List<FluidStack> fluidStacks1 = new ArrayList<>();
            if (tag) {
                ResourceLocation tagLoc = buffer.readResourceLocation();
                int amount = buffer.readInt();
                return FluidIngredient.of(FluidTags.create(tagLoc), amount);
            } else {
                int size = buffer.readVarInt();
                for (int i = 0; i < size; i++) {
                    fluidStacks1.add(buffer.readFluidStack());
                }
                int amount = buffer.readInt();
                return FluidIngredient.of(fluidStacks1);
            }
        }

        @Override
        public FluidIngredient parse(JsonObject json) {
            int amount = JsonUtils.getIntOr("amount", json, 1);

            if (json.has("tag")) {
                String tag = JsonUtils.getStringOr("tag", json, "");
                return FluidIngredient.of(FluidTags.create(new ResourceLocation(tag)), amount);

            } else {
                String fluid = JsonUtils.getStringOr("fluid", json, "");
                return FluidIngredient.of(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluid)), amount));
            }
        }

        @Override
        public void write(FriendlyByteBuf buffer, FluidIngredient ingredient) {
            boolean tag = ingredient.tag != null;
            buffer.writeBoolean(tag);
            if (tag) {
               buffer.writeResourceLocation(ingredient.tag.location());
            }
            else {
                buffer.writeCollection(ingredient.fluidStacks, IForgeFriendlyByteBuf::writeFluidStack);
            }
            buffer.writeInt(ingredient.amount);

        }
    }

}
