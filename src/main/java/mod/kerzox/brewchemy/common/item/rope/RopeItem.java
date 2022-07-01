package mod.kerzox.brewchemy.common.item.rope;

import mod.kerzox.brewchemy.common.blockentity.RopeTiedFenceBlockEntity;
import mod.kerzox.brewchemy.common.item.base.BrewchemyItem;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RopeItem extends BrewchemyItem {

    public RopeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        pContext.getLevel().setBlockAndUpdate(pContext.getClickedPos(), BrewchemyRegistry.Blocks.ROPE_BLOCK.get().defaultBlockState());
        return InteractionResult.CONSUME;
    }
}
