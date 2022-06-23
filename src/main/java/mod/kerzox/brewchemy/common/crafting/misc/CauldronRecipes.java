package mod.kerzox.brewchemy.common.crafting.misc;

import mod.kerzox.brewchemy.common.block.BarleyCropBlock;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LayeredCauldronBlock;

import static net.minecraft.core.cauldron.CauldronInteraction.WATER;

public class CauldronRecipes {

    private static final CauldronInteraction SOAKED_BARLEY = (blockState, level, pos, player, hand, stack) -> {
        if (stack.getItem() instanceof BlockItem item) {
            if (item.getBlock() instanceof BarleyCropBlock) {
                if (!level.isClientSide) {
                    ItemStack result = new ItemStack(BrewchemyRegistry.Items.SOAKED_BARLEY_ITEM.get());
                    // shrink stack by one
                    stack.shrink(1);

                    // try to input the result into inventory if returns false spawn entity instead.
                    if (!player.getInventory().add(result)) {
                        ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), result);
                        level.addFreshEntity(itemEntity);
                    } else {
                        // sync client
                        player.inventoryMenu.sendAllDataToRemote();
                    }
                    //LayeredCauldronBlock.lowerFillLevel(blockState, level, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    };


    public static void register() {
        WATER.put(BrewchemyRegistry.Blocks.BARLEY_CROP_BLOCK.get().asItem(), SOAKED_BARLEY);
    }



}
