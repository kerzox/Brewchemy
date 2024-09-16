package mod.kerzox.brewchemy.common.util;

import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BrewchemyUtils {

    public static Item getItemUnsafe(String name) {
        return BrewchemyRegistry.Items.ALL_ITEMS.get(name).get();
    }

    public static Block getBlockUnsafe(String name) {
        return BrewchemyRegistry.Blocks.ALL_BLOCKS.get(name).get();
    }

    public static Item getItem(String name) {
        if (BrewchemyRegistry.Items.ALL_ITEMS.containsKey(name)) {
            return BrewchemyRegistry.Items.ALL_ITEMS.get(name).get();
        }
        else {
            return null;
        }
    }

    public static Block getBlock(String name) {
        if (BrewchemyRegistry.Blocks.ALL_BLOCKS.containsKey(name)) {
            return BrewchemyRegistry.Blocks.ALL_BLOCKS.get(name).get();
        }
        else {
            return null;
        }
    }

}
