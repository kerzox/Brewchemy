package mod.kerzox.brewchemy.common.blockentity.base;

import com.google.common.collect.Lists;
import mod.kerzox.brewchemy.common.capabilities.CapabilityHolder;
import mod.kerzox.brewchemy.common.capabilities.ICompoundSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class CapabilityBlockEntity extends SyncedBlockEntity {

    private List<CapabilityHolder<?>> capabilities = new ArrayList<>();

    public static final String MACHINE_CAPABILITY_LIST_TAG = "all_capabilities_data";
    public static final String ITEM_HANDLER_TAG = "itemHandler";
    public static final String FLUID_HANDLER_TAG = "fluidHandler";
    public static final String ENERGY_HANDLER_TAG = "energyHandler";

    public CapabilityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void addCapabilities(CapabilityHolder<?>... capabilityHolder) {
        capabilities = Lists.newArrayList(capabilityHolder);
    }

    public CapabilityHolder<?> getHolderFrom(int index) {
        return capabilities.get(index);
    }

    public List<CapabilityHolder<?>> getCapabilityHolders() {
        return capabilities;
    }


    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleDataPacket(net, pkt);
        read(pkt.getTag());
        ListTag list = pkt.getTag().getList(MACHINE_CAPABILITY_LIST_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            if (capabilities.get(i) instanceof ICompoundSerializer serializer) {
                serializer.deserialize(tag);
            }
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        deserializeCapabilityHolders(pTag, this.capabilities);
    }

    protected void deserializeCapabilityHolders(CompoundTag pTag, List<CapabilityHolder<?>> capabilities) {
        ListTag list = pTag.getList(MACHINE_CAPABILITY_LIST_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            if (capabilities.get(i) instanceof ICompoundSerializer serializer) {
                serializer.deserialize(tag);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        saveCapabilities(pTag);
    }

    private void saveCapabilities(CompoundTag pTag) {
        ListTag list = new ListTag();
        for (CapabilityHolder<?> holder : capabilities) {
            if (holder.getInstance() instanceof ICompoundSerializer serializer) {
                list.add(serializer.serialize());
            }
        }
        pTag.put(MACHINE_CAPABILITY_LIST_TAG, list);
    }

    protected void replaceCapability(int index, CapabilityHolder<?> holder) {
        capabilities.get(index).invalidate();
        capabilities.set(index, holder);
    }

    protected void addToUpdateTag(CompoundTag tag) {
        write(tag);
        saveCapabilities(tag);
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {

    }

    @Override
    public void invalidateCaps() {
        for (CapabilityHolder<?> holder : capabilities) {
            holder.invalidate();
        }
        super.invalidateCaps();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        for (CapabilityHolder<?> holder : capabilities) {
            if (holder.getType() == cap) return (LazyOptional<T>) holder.getCapabilityHandler(side);
        }
        return super.getCapability(cap, side);
    }
}
