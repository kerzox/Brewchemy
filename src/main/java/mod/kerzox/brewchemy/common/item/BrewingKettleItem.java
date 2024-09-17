package mod.kerzox.brewchemy.common.item;

import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class BrewingKettleItem extends BlockItem {

    public BrewingKettleItem(Properties p_40566_) {
        super(BrewchemyRegistry.Blocks.BREWING_KETTLE_BLOCK.get(), p_40566_);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext p_40611_, BlockState p_40612_) {
        Player player = p_40611_.getPlayer();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return p_40611_.getLevel().isUnobstructed(p_40612_, p_40611_.getClickedPos(), collisioncontext) && p_40611_.getLevel().getBlockState(p_40611_.getClickedPos().above()).getBlock() instanceof AirBlock;
    }
}
