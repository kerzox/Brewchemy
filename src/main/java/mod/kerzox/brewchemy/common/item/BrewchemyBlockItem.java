package mod.kerzox.brewchemy.common.item;

import mod.kerzox.brewchemy.common.blockentity.base.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BrewchemyBlockItem extends BlockItem {

    public BrewchemyBlockItem(Block block, Properties p_41383_) {
        super(block, p_41383_);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos p_40597_, Level p_40598_, @Nullable Player p_40599_, ItemStack p_40600_, BlockState p_40601_) {
        MinecraftServer minecraftserver = p_40598_.getServer();
        if (minecraftserver == null) {
            return false;
        } else {
            CompoundTag compoundtag = getBlockEntityData(p_40600_);
            if (compoundtag != null &&  p_40598_.getBlockEntity(p_40597_) instanceof SyncedBlockEntity blockentity) {
                if (!p_40598_.isClientSide && blockentity.onlyOpCanSetNbt() && (p_40599_ == null || !p_40599_.canUseGameMasterBlocks())) {
                    return false;
                }

                CompoundTag merged = blockentity.saveWithoutMetadata().copy();
                CompoundTag compoundtag2 = merged.copy();
                merged.merge(compoundtag);
                if (!merged.equals(compoundtag2)) {
                    blockentity.loadFromItem(blockentity.saveWithoutMetadata(), compoundtag2, merged, p_40600_);
                    blockentity.setChanged();
                    return true;
                }
            }

            return false;
        }
    }

}
