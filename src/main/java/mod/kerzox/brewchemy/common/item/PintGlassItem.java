package mod.kerzox.brewchemy.common.item;

import mod.kerzox.brewchemy.common.capabilities.fluid.FluidStorageTank;
import mod.kerzox.brewchemy.common.item.base.BrewchemyItem;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PintGlassItem extends BrewchemyItem {

    public static final int PINT_SIZE = 550;
    public static final int PINTS_PER_KEG = 120;
    public static final int KEG_VOLUME = PINT_SIZE * PINTS_PER_KEG;

    public PintGlassItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        FluidStorageTank tank = new FluidStorageTank(PINT_SIZE);
        return new ICapabilityProvider() {
            private final LazyOptional<FluidStorageTank> handler = LazyOptional.of(() -> tank);
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? handler.cast() : LazyOptional.empty();
            }
        };
    }
}
