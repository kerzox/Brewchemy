package mod.kerzox.brewchemy.common.block.base;

import mod.kerzox.brewchemy.common.blockentity.base.IServerTickable;
import mod.kerzox.brewchemy.common.blockentity.base.SyncedBlockEntity;
import mod.kerzox.brewchemy.common.util.SoundHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBrewchemyEntityBlock extends BrewchemyDirectionalBlock implements EntityBlock {


    public AbstractBrewchemyEntityBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (FluidUtil.getFluidHandler(pPlayer.getItemInHand(pHand)).isPresent()) {
            if (!pLevel.isClientSide) {
                if (tryFluidInteraction(pPlayer, pHand, pLevel, pPlayer.getItemInHand(pHand), FluidUtil.getFluidHandler(pPlayer.getItemInHand(pHand)).resolve().get(), pHit.getBlockPos(), pHit.getDirection()))
                {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (pLevel.getBlockEntity(pPos) instanceof SyncedBlockEntity onClick) {
            if (onClick.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit)) {
                return InteractionResult.SUCCESS;
            }
        }
        if (pLevel.getBlockEntity(pPos) instanceof MenuProvider menu) {
            if (pLevel.isClientSide) return InteractionResult.SUCCESS;
            NetworkHooks.openScreen((ServerPlayer) pPlayer, menu, pPos);
            return InteractionResult.SUCCESS;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }



    protected boolean tryFluidInteraction(Player pPlayer, InteractionHand pHand, Level pLevel, ItemStack itemInHand, IFluidHandlerItem fluidHandlerItem, BlockPos blockPos, Direction direction) {
        return FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, blockPos, direction);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (pLevel1, pPos, pState1, pBlockEntity) -> {
            if (!pLevel1.isClientSide && pBlockEntity instanceof IServerTickable tick) {
                tick.tick();
            }
            if (pLevel1.isClientSide && pBlockEntity instanceof IClientTickable tick) {
                tick.clientTick(new SoundHandler(pLevel1));
            }
        };
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_221121_, T p_221122_) {
        return EntityBlock.super.getListener(p_221121_, p_221122_);
    }
}
