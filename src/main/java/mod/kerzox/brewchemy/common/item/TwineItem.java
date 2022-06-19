package mod.kerzox.brewchemy.common.item;

import mod.kerzox.brewchemy.common.capabilities.BrewchemyCapabilities;
import mod.kerzox.brewchemy.common.item.base.BrewchemyItem;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TwineItem extends BrewchemyItem {

    public TwineItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();

        if (!level.isClientSide) {
            level.getBlockState(pos);
            if (level.getBlockState(pos).getBlock() instanceof FenceBlock post || level.getBlockState(pos).getBlock() instanceof FarmBlock farmLand) {
                ctx.getItemInHand().getCapability(BrewchemyCapabilities.TWINE_PLACEMENT_CAPABILITY).ifPresent(cap -> {
                    if (cap.addToSelected(pos, player)) place(level, cap.selected);
                });
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(ctx);
    }

    private void place(Level level, BlockPos[] selected) {
        Set<BlockPos> positions = new HashSet<>();
        if (selected.length < 2) return;
        int distance = selected[0].distManhattan(selected[1]);
        for (Direction dir : Direction.values()) {
            if (!selected[0].relative(dir, distance).equals(selected[1])) continue;
            for (int i = 0; i < distance + 1; i++) {
                BlockPos pos = selected[0].relative(dir, i);
                positions.add(selected[0].relative(dir, i));
            }
        }
        Arrays.asList(selected).forEach(positions::remove);
        positions.forEach(b -> level.setBlockAndUpdate(b, BrewchemyRegistry.Blocks.ROPE_BLOCK.get().defaultBlockState()));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        PositionSelectionCapability cap = new PositionSelectionCapability();
        return new ICapabilityProvider() {
            private final LazyOptional<PositionSelectionCapability> lazy = LazyOptional.of(() -> cap);
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return lazy.cast();
            }
        };
    }

    public static class PositionSelectionCapability {
        private final BlockPos[] selected = new BlockPos[2];
        private int index = 0;

        public PositionSelectionCapability() {

        }

        public boolean addToSelected(BlockPos pos, Player player) {
            selected[index] = pos;
            index++;
            player.sendSystemMessage(Component.literal("Position " + index + "selected: " + pos));
            if (index == 2) {
                index = 0;
                return true;
            }
            return false;
        }
    }

}
