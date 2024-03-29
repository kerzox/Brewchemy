package mod.kerzox.brewchemy.common.block.base;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.common.blockentity.WoodenBarrelBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.util.IClientTickable;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BrewchemyEntityBlock<T extends BlockEntity> extends BrewchemyBlock implements EntityBlock {

    protected RegistryObject<BlockEntityType<T>> type;
    protected boolean shouldTick;

    public BrewchemyEntityBlock(RegistryObject<BlockEntityType<T>> type, Properties properties) {
        super(properties);
        this.type = type;
        shouldTick = true;
    }

    public BrewchemyEntityBlock(RegistryObject<BlockEntityType<T>> type, Properties properties, boolean shouldTick) {
        super(properties);
        this.type = type;
        this.shouldTick = shouldTick;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getMainHandItem().getItem() == BrewchemyRegistry.Items.SOFT_MALLET.get() && pState.getBlock() != BrewchemyRegistry.Blocks.WAREHOUSE_BLOCK.get()) {
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
        if (FluidUtil.getFluidHandler(pPlayer.getItemInHand(pHand)).isPresent()) {
            if (!pLevel.isClientSide) {
                if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pHit.getBlockPos(), pHit.getDirection())) {
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (pLevel.getBlockEntity(pPos) instanceof BrewchemyBlockEntity onClick && pHand == InteractionHand.MAIN_HAND) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return this.type.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return shouldTick ? (pLevel1, pPos, pState1, pBlockEntity) -> {
            if (!pLevel1.isClientSide && pBlockEntity instanceof IServerTickable tick) {
                tick.onServer();
            }
            if (pLevel1.isClientSide && pBlockEntity instanceof IClientTickable tick) {
                tick.onClient();
            }
        } : null;
    }
}
