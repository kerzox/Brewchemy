package mod.kerzox.brewchemy.common.block;

import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.FermentationBarrelBlockEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FermentationBarrelBlock extends BrewchemyEntityBlock<FermentationBarrelBlockEntity> {

    public FermentationBarrelBlock(Properties p_49795_) {
        super(BrewchemyRegistry.BlockEntities.FERMENTATION_BARREL_BLOCK_ENTITY.getType(), p_49795_);
    }

    @Override
    protected boolean tryFluidInteraction(Player pPlayer,
                                          InteractionHand pHand,
                                          Level pLevel,
                                          ItemStack itemInHand,
                                          IFluidHandlerItem fluidHandlerItem,
                                          BlockPos blockPos, Direction direction) {

        if (pLevel.getBlockEntity(blockPos) instanceof FermentationBarrelBlockEntity block) {
            if (block.isTapped()) {
                FluidActionResult fluidActionResult = FluidUtil.getFluidHandler(pLevel, blockPos, direction).map(handler ->
                        FluidUtil.tryFillContainerAndStow(
                                itemInHand,
                                handler,
                                pPlayer.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get(),
                                Integer.MAX_VALUE,
                                pPlayer,
                                true)).get();

                if (fluidActionResult.isSuccess()) {
                    pPlayer.setItemInHand(pHand, fluidActionResult.getResult());
                }

                return true;
            }
            FluidActionResult fluidActionResult =  FluidUtil.getFluidHandler(pLevel, blockPos, direction).map(handler ->
                    FluidUtil.tryEmptyContainerAndStow(
                            itemInHand,
                            handler,
                            pPlayer.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get(),
                            Integer.MAX_VALUE,
                            pPlayer,
                            true)).get();

            if (fluidActionResult.isSuccess()) {
                pPlayer.setItemInHand(pHand, fluidActionResult.getResult());
            }

            return true;
        }
        return false;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
        if (p_60516_.getBlockEntity(p_60517_) instanceof FermentationBarrelBlockEntity block) {
            block.getController().disassemble(block);
        }
        super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
    }
}
