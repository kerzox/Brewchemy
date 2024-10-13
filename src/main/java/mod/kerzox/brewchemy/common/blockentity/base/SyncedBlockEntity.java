package mod.kerzox.brewchemy.common.blockentity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SyncedBlockEntity extends BlockEntity {


    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void syncBlockEntity() {
        if (level != null) {
            this.setChanged(); // mark dirty
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL); // call update to the block
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        addToUpdateTag(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleDataPacket(net, pkt);
        read(pkt.getTag());

    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        read(tag);
        onlyClientSideUpdate(tag);
    }

    private void onlyClientSideUpdate(CompoundTag tag) {

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        read(pTag);
    }

    /** DON"T OVERRIDE THIS USE WRITE
     *
     * @param pTag
     */

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        write(pTag);
    }

    /* overwrite these ones!
        makes sure we don't fuck up super calls etc.
     */

    protected void handleDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {

    }

    protected void write(CompoundTag pTag) {

    }


    /** read
     * When load is called this will be called, (on world load, nbt read and updateBlockEntity function)
     * @param pTag nbt data
     */
    protected void read(CompoundTag pTag) {

    }


    public void onMenuSync(ServerPlayer playerInteracting, int state) {

    }

    public void updateFromNetwork(CompoundTag tag) {

    }

    protected void addToUpdateTag(CompoundTag tag) {
        write(tag);
    }

    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        return false;
    }

    public void loadFromItem(CompoundTag fromBlockEntity, CompoundTag copyOfBlockEntityTag, CompoundTag mergedFromItemStack, ItemStack itemStack) {
        load(mergedFromItemStack);
    }
}
