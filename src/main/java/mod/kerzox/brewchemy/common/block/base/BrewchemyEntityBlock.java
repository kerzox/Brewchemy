package mod.kerzox.brewchemy.common.block.base;

import mod.kerzox.brewchemy.common.util.IClientTickable;
import mod.kerzox.brewchemy.common.util.IServerTickable;
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
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BrewchemyEntityBlock<T extends BlockEntity> extends BrewchemyBlock implements EntityBlock {

    protected RegistryObject<BlockEntityType<T>> type;

    public BrewchemyEntityBlock(RegistryObject<BlockEntityType<T>> type, Properties properties) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof MenuProvider menu) {
            if (pPlayer.getMainHandItem().getItem() instanceof BlockItem || pPlayer.getOffhandItem().getItem() instanceof BlockItem)
                return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
            NetworkHooks.openGui((ServerPlayer) pPlayer, menu, pPos);
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
        return (pLevel1, pPos, pState1, pBlockEntity) -> {
            if (pBlockEntity instanceof IServerTickable tick) {
                tick.onServer();
            }
            if (pBlockEntity instanceof IClientTickable tick) {
                tick.onClient();
            }
        };
    }
}
