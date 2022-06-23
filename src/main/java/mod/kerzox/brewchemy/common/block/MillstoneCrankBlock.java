package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.MillStoneBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.MillstoneCrankBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class MillstoneCrankBlock extends BrewchemyEntityBlock<MillstoneCrankBlockEntity> {

    public MillstoneCrankBlock(Properties properties) {
        super(BrewchemyRegistry.BlockEntities.MILL_STONE_CRANK.getType(), properties);
    }

    public RenderShape getRenderShape(BlockState p_48758_) {
        return RenderShape.INVISIBLE;
    }

    public VoxelShape getShape(BlockState p_48760_, BlockGetter p_48761_, BlockPos p_48762_, CollisionContext p_48763_) {
        return Shapes.block();
    }

    private boolean allowedBlock(BlockState state) {
        return state.is(BrewchemyRegistry.Blocks.MILL_STONE_BLOCK.get());
    }

    private boolean allowedDirection(Direction direction) {
        return direction == Direction.UP;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof MillstoneCrankBlockEntity entity) {
            return entity.action(pPlayer);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pLevel.getBlockEntity(pPos) instanceof MillstoneCrankBlockEntity crank) {
            if (pLevel.getBlockEntity(pPos.below()) instanceof MillStoneBlockEntity mill) {
                crank.setMillstone(mill);
            }
            else {
                System.out.println("Millstone not found");
                // remove this block
            }
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        //TODO
        // only allow placement on top of millstone
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState blockClickedOn = pContext.getLevel().getBlockState(pos.below());

        if (!allowedBlock(blockClickedOn) || !allowedDirection(pContext.getClickedFace())) {
            return null; // returning null blockItem will do placement fail
        }

        return super.getStateForPlacement(pContext);
    }


}
