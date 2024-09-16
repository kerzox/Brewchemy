package mod.kerzox.brewchemy.common.item;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BarleyItem extends Item {

    public BarleyItem(Properties p_41383_) {
        super(p_41383_);
    }

    public static class Seed extends BlockItem {

        public Seed(Properties p_40566_) {
            super(BrewchemyRegistry.Blocks.BARLEY_CROP_BLOCK.get(), p_40566_);
        }

    }

}
