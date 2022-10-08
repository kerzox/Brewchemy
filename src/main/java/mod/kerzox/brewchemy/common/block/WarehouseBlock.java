package mod.kerzox.brewchemy.common.block;


import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.block.base.BrewchemyInvisibleBlock;
import mod.kerzox.brewchemy.common.blockentity.WoodenBarrelBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.warehouse.WarehouseBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.warehouse.WarehouseStorageBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseSlot;
import mod.kerzox.brewchemy.common.item.base.BrewchemyItem;
import mod.kerzox.brewchemy.common.util.IServerTickable;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import static mod.kerzox.brewchemy.common.block.rope.RopeBlock.HAS_TRELLIS;

public class WarehouseBlock extends BrewchemyEntityBlock<WarehouseBlockEntity> {

    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");

    public WarehouseBlock(RegistryObject<BlockEntityType<WarehouseBlockEntity>> type, Properties properties) {
        super(type, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(INVISIBLE, false).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    public RenderShape getRenderShape(BlockState p_48758_) {
        return shouldRenderInvisible(p_48758_) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shouldRenderInvisible(pState) ? Shapes.empty() : getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return shouldRenderInvisible(pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pContext.isHoldingItem(BrewchemyRegistry.Items.SOFT_MALLET.get())) {
            return Shapes.block();
        }
        return shouldRenderInvisible(pState) ? Shapes.empty() : Shapes.block();
    }

    public boolean shouldRenderInvisible(BlockState pState) {
        return pState.getValue(INVISIBLE);
    }

    public BlockState toggleInvisibility(BlockState state) {
        if (shouldRenderInvisible(state)) return state.setValue(INVISIBLE, false);
        return state.setValue(INVISIBLE, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(INVISIBLE, HorizontalDirectionalBlock.FACING);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(HorizontalDirectionalBlock.FACING, pRotation.rotate(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(HorizontalDirectionalBlock.FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getItemInHand(pHand).getItem() == BrewchemyRegistry.Items.SOFT_MALLET.get()) {
            pLevel.setBlockAndUpdate(pPos, toggleInvisibility(pState));
            return InteractionResult.CONSUME;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pLevel.getBlockEntity(pPos) instanceof WarehouseBlockEntity warehouse) {
            warehouse.calculatePositions();
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pLevel.getBlockEntity(pPos) instanceof WarehouseBlockEntity warehouse) {
            for (BlockPos position : warehouse.getCachedPositions()) {
                pLevel.setBlockAndUpdate(position, Blocks.AIR.defaultBlockState());
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    public static class Item extends BlockItem {

        public Item(Properties pProperties) {
            super(BrewchemyRegistry.Blocks.WAREHOUSE_BLOCK.get(), pProperties);
        }

        @Override
        protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
            Direction facing = pContext.getNearestLookingDirection();
            BlockPos relative = pContext.getClickedPos().relative(facing);
            for (int x = relative.getX(); x < relative.getX() + 5; x++) {
                for (int y = relative.getY(); y < relative.getY() + 5; y++) {
                    for (int z = relative.getZ(); z < relative.getZ() + 5; z++) {
                        BlockPos relativePos = new BlockPos(x, y, z);
                        if (facing == Direction.SOUTH) {
                            relativePos = relativePos.offset(-4, 0, 0);
                        } else if (facing == Direction.NORTH) {
                            relativePos = relativePos.offset(0, 0, -4);
                        } else if (facing == Direction.WEST) {
                            relativePos = relativePos.offset(-4, 0, -4);
                        }
                        if (!(pContext.getLevel().getBlockState(relativePos).getBlock() instanceof AirBlock)) {
                            return false;
                        }
                    }
                }
            }
            return super.canPlace(pContext, pState);
        }



    }
    public static class WarehouseStorageBlock extends BrewchemyInvisibleBlock implements EntityBlock {

        public WarehouseStorageBlock(Properties properties) {
            super(properties);
        }

        @Override
        public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
            return false;
        }

        @Override
        public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
            if (level.getBlockEntity(pos) instanceof WarehouseStorageBlockEntity warehouseStorageBlockEntity) {
                if (warehouseStorageBlockEntity.getSlot() != null) {
                    return new ItemStack(warehouseStorageBlockEntity.getSlot().getFullWarehouseItem().getItem());
                }
            }
            return super.getCloneItemStack(state, target, level, pos, player);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
            return new WarehouseStorageBlockEntity(pPos, pState);
        }

        @Override
        public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
            if (pLevel.getBlockEntity(pPos) instanceof BrewchemyBlockEntity onClick && pHand == InteractionHand.MAIN_HAND) {

                if (onClick.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit)) {
                    return InteractionResult.SUCCESS;
                }
            }
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }

        @Override
        public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
            if (pLevel.getBlockEntity(pPos) instanceof WarehouseStorageBlockEntity warehouseStorageBlockEntity) {
                if (warehouseStorageBlockEntity.getWarehouse() != null) {
                    WarehouseSlot slot = warehouseStorageBlockEntity.getWarehouse().getWarehouseInventory().getSlotFromBlockPos(warehouseStorageBlockEntity.getBlockPos());
                    if (slot.isEmpty()) return super.getShape(pState, pLevel, pPos, pContext);
                    return Shapes.block();
                }
            }
            return super.getShape(pState, pLevel, pPos, pContext);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
            return ((pLevel1, pPos, pState1, pBlockEntity) -> {
                if (!pLevel1.isClientSide && pBlockEntity instanceof IServerTickable tick) {
                    tick.onServer();
                }
            });
        }
    }
}
