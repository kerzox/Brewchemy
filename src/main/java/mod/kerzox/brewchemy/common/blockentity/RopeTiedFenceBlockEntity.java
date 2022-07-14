package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.BlockEntities.ROPE_FENCE;

public class RopeTiedFenceBlockEntity extends BrewchemyBlockEntity {

    public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();

    private BlockState mimic;

    public RopeTiedFenceBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ROPE_FENCE.get(), pWorldPosition, pBlockState);
    }

    public void setFenceToMimic(BlockState mimic) {
        this.mimic = mimic;
        this.setChanged();

        // update block
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (level != null) {
            BlockState old = mimic;
            super.onDataPacket(net, pkt);
            requestModelDataUpdate();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public BlockState getMimic() {
        return mimic;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        writeMimic(tag);
        return tag;
    }

    private void writeMimic(CompoundTag tag) {
        if (mimic != null) {
            tag.put("mimic", NbtUtils.writeBlockState(mimic));
        }
    }

    private void readMimic(CompoundTag tag) {
        if (tag.contains("mimic")) {
            mimic = NbtUtils.readBlockState(tag.getCompound("mimic"));
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        readMimic(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        writeMimic(pTag);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(MIMIC, mimic).build();
    }
}
