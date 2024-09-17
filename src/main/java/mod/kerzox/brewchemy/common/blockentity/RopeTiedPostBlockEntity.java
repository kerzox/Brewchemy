package mod.kerzox.brewchemy.common.blockentity;

import mod.kerzox.brewchemy.common.blockentity.base.SyncedBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class RopeTiedPostBlockEntity extends SyncedBlockEntity {

    public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();
    // mimic state
    private BlockState mimicState = Blocks.OAK_FENCE.defaultBlockState();

    public RopeTiedPostBlockEntity(BlockPos pos, BlockState state) {
        super(BrewchemyRegistry.BlockEntities.ROPE_TIED_POST_BLOCK_ENTITY.get(), pos, state);
    }

    public void setFenceToMimic(BlockState newState) {
        this.mimicState = newState;
        syncBlockEntity();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    protected void handleDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (level != null) {
            BlockState old = mimicState;
            requestModelDataUpdate();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("mimicState", NbtUtils.writeBlockState(mimicState));
    }

    @Override
    protected void read(CompoundTag pTag) {
       this.mimicState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), pTag.getCompound("mimicState"));
    }
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(MIMIC, this.mimicState).build();
    }

    public BlockState getMimicState() {
        return this.mimicState;
    }
}
