package mod.kerzox.brewchemy.registry;

import mod.kerzox.brewchemy.client.ui.menu.MillingMenu;
import mod.kerzox.brewchemy.common.block.*;
import mod.kerzox.brewchemy.common.blockentity.BrewingKettleBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.MillingBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.RopeTiedPostBlockEntity;
import mod.kerzox.brewchemy.common.crafting.recipe.BrewingRecipe;
import mod.kerzox.brewchemy.common.crafting.recipe.MillingRecipe;
import mod.kerzox.brewchemy.common.entity.RopeEntity;
import mod.kerzox.brewchemy.common.item.BarleyItem;
import mod.kerzox.brewchemy.common.item.BrewingKettleItem;
import mod.kerzox.brewchemy.common.item.RopeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static mod.kerzox.brewchemy.Brewchemy.MODID;
public class BrewchemyRegistry {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,  MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ITEMS.register(bus);
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
        RECIPE_TYPES.register(bus);
        RECIPES.register(bus);
        MENUS.register(bus);
        EFFECTS.register(bus);
        ENTITIES.register(bus);
        PARTICLE_TYPES.register(bus);
        CREATIVE_MODE_TABS.register(bus);

        Blocks.init();
        Items.init();
        Recipes.init();
        BlockEntities.init();
        Menus.init();
        Entities.init();
    }

    public static final RegistryObject<CreativeModeTab> BREWCHEMY_TAG = CREATIVE_MODE_TABS.register("brewchemy_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.literal("Brewchemy"))
            .icon(() -> Items.BARLEY_ITEM.get().getDefaultInstance()).build());

    public static class Recipes {

        public static final RegistryObject<RecipeType<MillingRecipe>> MILLING_RECIPE = RECIPE_TYPES.register("milling", () -> RecipeType.simple(new ResourceLocation(MODID, "milling")));
        public static final RegistryObject<MillingRecipe.Serializer> MILLING_RECIPE_SERIALIZER = RECIPES.register("milling_serializer", MillingRecipe.Serializer::new);

        public static final RegistryObject<RecipeType<BrewingRecipe>> BREWING_RECIPE = RECIPE_TYPES.register("brewing", () -> RecipeType.simple(new ResourceLocation(MODID, "brewing")));
        public static final RegistryObject<BrewingRecipe.Serializer> BREWING_RECIPE_SERIALIZER = RECIPES.register("brewing_serializer", BrewingRecipe.Serializer::new);

        public static void init() {

        }

    }

    public static class Menus {
        public static final RegistryObject<MenuType<MillingMenu>> MILLING_MENU = MENUS.register("milling_menu", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new MillingMenu(windowId, inv, inv.player, (MillingBlockEntity) level.getBlockEntity(pos));
        }));

        public static void init() {

        }
    }

    public static class Items {

        //Cached map of all items
        public static final HashMap<String, RegistryObject<Item>> ALL_ITEMS = new HashMap<>();

        public static final RegistryObject<Item> BARLEY_ITEM = register(
                true,
                "barley_item", () -> new BarleyItem(new Item.Properties()));

        public static final RegistryObject<Item> MILLED_BARLEY_ITEM = register(
                true,
                "milled_barley_item", () -> new Item(new Item.Properties()));

        public static final RegistryObject<Item> ROPE_ITEM = register(
                true,
                "rope_item", () -> new RopeItem(new Item.Properties()));

        public static final RegistryObject<Item> HOPS_ITEM = register(
                true,
                "hops_item", () -> new BarleyItem(new Item.Properties()));

        public static void init() {

        }

        private static <T extends Item> RegistryObject<Item> register(boolean cache, String name, Supplier<T> item) {
            RegistryObject<Item> ret = ITEMS.register(name, item);
            if (cache) ALL_ITEMS.put(name, ret);
            return ret;
        }



    }

    public static class Blocks {

        //Cached map of all items
        public static final HashMap<String, RegistryObject<Block>> ALL_BLOCKS = new HashMap<>();

        public static final makeBlock<BarleyCropBlock> BARLEY_CROP_BLOCK
                = makeBlock.buildCustomSuppliedItem("barley_crop_block",
                BarleyCropBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .randomTicks()
                        .instabreak()
                        .sound(SoundType.CROP)
                        .pushReaction(PushReaction.DESTROY)), () -> new BarleyItem.Seed(new Item.Properties()));

        public static final makeBlock<HopsCropBlock> HOPS_CROP_BLOCK
                = makeBlock.build("hops_crop_block",
                HopsCropBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .randomTicks()
                        .instabreak()
                        .sound(SoundType.CROP)
                        .pushReaction(PushReaction.DESTROY)), true);

        public static final makeBlock<RopeTiedPostBlock> ROPE_TIED_POST_BLOCK
                = makeBlock.build("rope_tied_post_block",
                RopeTiedPostBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .sound(SoundType.WOOD)
                        .pushReaction(PushReaction.DESTROY)), false);

        public static final makeBlock<MillingBlock> MILLING_BLOCK
                = makeBlock.build("milling_block",
                p -> new MillingBlock(BlockEntities.MILLING_BLOCK_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.STONE)
                        .sound(SoundType.STONE)
                        .requiresCorrectToolForDrops().strength(1.5F, 3.0F)
                        .pushReaction(PushReaction.NORMAL)), true);

        public static final makeBlock<BrewingKettleBlock> BREWING_KETTLE_BLOCK
                = makeBlock.buildCustomSuppliedItem("brewing_kettle_block",
                p -> new BrewingKettleBlock(BlockEntities.BREWING_KETTLE_BLOCK_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_ORANGE)
                        .sound(SoundType.NETHERITE_BLOCK)
                        .noOcclusion()
                        .requiresCorrectToolForDrops().strength(1.5F, 3.0F)
                        .pushReaction(PushReaction.DESTROY)), () -> new BrewingKettleItem(new Item.Properties()));

        public static final makeBlock<BrewingKettleBlock.Top> BREWING_KETTLE_TOP_BLOCK
                = makeBlock.build("brewing_kettle_top_block",
                BrewingKettleBlock.Top::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_ORANGE)
                        .sound(SoundType.NETHERITE_BLOCK)
                        .noOcclusion()
                        .noCollission()
                        .requiresCorrectToolForDrops().strength(1.5F, 3.0F)
                        .pushReaction(PushReaction.DESTROY)), false);


        public static void init() {

        }

        public static class makeBlock<T extends Block> implements Supplier<T> {

            private final RegistryObject<T> block;

            private final String name;

            private makeBlock(String name, RegistryObject<T> block) {
                this.name = name;
                this.block = block;
            }

            public static <T extends Block> makeBlock<T> build(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties prop, boolean asItem) {
                RegistryObject<T> ret = BLOCKS.register(name, () -> block.apply(prop));
                if (asItem) {
                    RegistryObject<Item> item = ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties()));
                    Items.ALL_ITEMS.put(name, item);
                }
                ALL_BLOCKS.put(name, (RegistryObject<Block>) ret);
                makeBlock<T> block1  =new makeBlock<>(name, ret);
                return block1;
            }

            public static <T extends Block> makeBlock<T> buildCustomSuppliedItem(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties prop, Supplier<BlockItem> itemSupplier) {
                RegistryObject<T> ret = BLOCKS.register(name, () -> block.apply(prop));
                RegistryObject<Item> item = ITEMS.register(name, itemSupplier);
                Items.ALL_ITEMS.put(name, item);
                ALL_BLOCKS.put(name, (RegistryObject<Block>) ret);
                makeBlock<T> block1 = new makeBlock<>(name, ret);
                return block1;
            }

            public RegistryObject<T> getRegistry() {
                return block;
            }

            @Override
            public T get() {
                return this.block.get();
            }

            public String getName() {
                return name;
            }
        }

    }

    public static class BlockEntities {

        public static final makeBlockEntity<RopeTiedPostBlockEntity> ROPE_TIED_POST_BLOCK_ENTITY
                = makeBlockEntity.build("rope_tied_post_block_entity", RopeTiedPostBlockEntity::new, Blocks.ROPE_TIED_POST_BLOCK);

        public static final makeBlockEntity<MillingBlockEntity> MILLING_BLOCK_ENTITY
                = makeBlockEntity.build("milling_block_entity", MillingBlockEntity::new, Blocks.MILLING_BLOCK);

        public static final makeBlockEntity<BrewingKettleBlockEntity> BREWING_KETTLE_BLOCK_ENTITY
                = makeBlockEntity.build("brewing_kettle_block_entity", BrewingKettleBlockEntity::new, Blocks.BREWING_KETTLE_BLOCK);

        public static void init() {

        }

        public static class makeBlockEntity<T extends BlockEntity> implements Supplier<BlockEntityType<T>> {

            private final RegistryObject<BlockEntityType<T>> type;

            public static <T extends BlockEntity> makeBlockEntity<T> build(
                    String name,
                    BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier,
                    Supplier<? extends Block> valid) {
                return new makeBlockEntity<T>(BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, valid.get()).build(null)));
            }

            public static <T extends BlockEntity> makeBlockEntity<T> build(
                    String name,
                    BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier,
                    Block... valid) {
                return new makeBlockEntity<T>(BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, valid).build(null)));
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

    }

    public static class Entities {

        public static final RegistryObject<EntityType<RopeEntity>> ROPE_ENTITY =
                ENTITIES.register("rope_entity",
                        () -> EntityType.Builder.<RopeEntity>of(RopeEntity::new,
                                        MobCategory.MISC)
                                .sized(4/16f, 4/16f)
                                .clientTrackingRange(6)
                                .updateInterval(20)
                                .build("rope_entity"));

        public static void init() {

        }

    }

}
