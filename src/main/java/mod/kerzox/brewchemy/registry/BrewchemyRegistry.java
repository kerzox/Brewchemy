package mod.kerzox.brewchemy.registry;

import mod.kerzox.brewchemy.common.block.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.BrewingPotBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static mod.kerzox.brewchemy.Brewchemy.MODID;
import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.BlockEntities.BREWING_POT;
import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.Blocks.BREWING_POT_BLOCK;

public class BrewchemyRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ITEMS.register(bus);

        Blocks.init();
        BlockEntities.init();

    }

    public static final class Blocks {

        public static void init(){}

        public static final makeBlock<BrewchemyEntityBlock<BrewingPotBlockEntity>> BREWING_POT_BLOCK = makeBlock.build("brewing_pot_block", p -> new BrewchemyEntityBlock<>(BREWING_POT.getType(), p), BlockBehaviour.Properties.of(Material.METAL));

    }

    public static final class BlockEntities {

        public static void init(){}

        public static final makeBlockEntity<BrewingPotBlockEntity> BREWING_POT = makeBlockEntity.build("brewing_pot_be", BrewingPotBlockEntity::new, BREWING_POT_BLOCK);

    }

    public static class makeBlockEntity<T extends BlockEntity> implements Supplier<BlockEntityType<T>> {

        private final RegistryObject<BlockEntityType<T>> type;

        public static <T extends BlockEntity> makeBlockEntity<T> build(
                String name,
                BlockEntityType.BlockEntitySupplier<T> resource_name,
                Supplier<? extends Block> valid) {
            return new makeBlockEntity<T>(BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(resource_name, valid.get()).build(null)));
        }

        public makeBlockEntity(RegistryObject<BlockEntityType<T>> type) {
            this.type = type;
        }

        @Override
        public BlockEntityType<T> get() {
            return this.getType().get();
        }

        public RegistryObject<BlockEntityType<T>> getType() {
            return type;
        }
    }

    public static class makeBlock<T extends Block> implements Supplier<T> {

        public static final List<makeBlock<?>> ENTRIES = new ArrayList<>();

        private final RegistryObject<T> block;

        private makeBlock(RegistryObject<T> block) {
            this.block = block;
            ENTRIES.add(this);
        }

        public static <T extends Block> makeBlock<T> build(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties prop) {
            RegistryObject<T> ret = BLOCKS.register(name, () -> block.apply(prop));
            ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
            return new makeBlock<>(ret);
        }

        @Override
        public T get() {
            return this.block.get();
        }
    }

}
