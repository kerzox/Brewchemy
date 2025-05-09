package mod.kerzox.brewchemy.common.item;

import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class PlantItem extends Item {

    public PlantItem(Properties p_41383_) {
        super(p_41383_);
    }

    public static class Seed extends BlockItem {

        public Seed(Supplier<? extends Block> block, Properties p_40566_) {
            super(block.get(), p_40566_);
        }

        @Override
        public String getDescriptionId() {
            return this.getOrCreateDescriptionId();
        }
    }

}
