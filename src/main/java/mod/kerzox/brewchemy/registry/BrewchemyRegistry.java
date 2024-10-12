package mod.kerzox.brewchemy.registry;

import mod.kerzox.brewchemy.client.ui.menu.BrewingMenu;
import mod.kerzox.brewchemy.client.ui.menu.MillingMenu;
import mod.kerzox.brewchemy.common.block.*;
import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.*;
import mod.kerzox.brewchemy.common.crafting.recipe.BrewingRecipe;
import mod.kerzox.brewchemy.common.crafting.recipe.CultureJarRecipe;
import mod.kerzox.brewchemy.common.crafting.recipe.FermentationRecipe;
import mod.kerzox.brewchemy.common.crafting.recipe.MillingRecipe;
import mod.kerzox.brewchemy.common.data.BrewingKettleHeating;
import mod.kerzox.brewchemy.common.effects.BlackoutEffect;
import mod.kerzox.brewchemy.common.effects.BuzzedEffect;
import mod.kerzox.brewchemy.common.effects.IntoxicatedEffect;
import mod.kerzox.brewchemy.common.effects.WastedEffect;
import mod.kerzox.brewchemy.common.entity.RopeEntity;
import mod.kerzox.brewchemy.common.entity.SeatEntity;
import mod.kerzox.brewchemy.common.event.TickUtils;
import mod.kerzox.brewchemy.common.fluid.BrewchemyFluid;
import mod.kerzox.brewchemy.common.fluid.alcohol.AlcoholicFluid;
import mod.kerzox.brewchemy.common.item.*;
import mod.kerzox.brewchemy.common.particle.FermentationParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,  MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);


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
        PARTICLE_TYPES.register(bus);
        CREATIVE_MODE_TABS.register(bus);
        ENTITIES.register(bus);
        SOUND_EVENTS.register(bus);
        // these are for all the machine and special items
        Blocks.init();
        BlockEntities.init();
        Fluids.init();
        Items.init();
        Particles.init();
        Effects.init();
        Menus.init();
        Tags.init();
        Entities.init();
        DataPacks.init();
        Recipes.init();
        Sounds.init();

    }

    public static final RegistryObject<CreativeModeTab> BREWCHEMY_TAG = CREATIVE_MODE_TABS.register("brewchemy_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.literal("Brewchemy"))
            .icon(() -> Items.BARLEY_ITEM.get().getDefaultInstance()).build());

    public static class Tags {

        public static final TagKey<Item> BLOCKS = ItemTags.create(new ResourceLocation("forge", "blocks"));
        public static final TagKey<Item> YEAST = ItemTags.create(new ResourceLocation("forge", "yeast"));

        public static void init() {
        }
    }

    public static class DataPacks {

        public static final ResourceKey<Registry<BrewingKettleHeating>> KETTLE_HEATING_REGISTRY_KEY =
                ResourceKey.createRegistryKey(new ResourceLocation(MODID, "kettle_heating"));

        public static void init() {

        }
    }

    public static class Sounds {

        public static final Supplier<SoundEvent> FERMENTING_BUBBLES = SOUND_EVENTS.register(
                "bubbling",
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "bubbling"))
        );

        public static void init() {

        }
    }

    public static class Particles {
        public static final Supplier<FermentationParticleType> FERMENTATION_STAGE_PARTICLE = PARTICLE_TYPES.register(
                "fermentation",
                () -> new FermentationParticleType(true)
        );

        public static void init() {

        }
    }

    public static final class Effects {
        public static void init() {

        }

        public static final RegistryObject<MobEffect> BUZZED = EFFECTS.register("buzzed", () -> new BuzzedEffect(MobEffectCategory.NEUTRAL, 0xFFEE82EE));

        public static final RegistryObject<MobEffect> INTOXICATED = EFFECTS.register("intoxicated", () -> new IntoxicatedEffect(MobEffectCategory.NEUTRAL, 0xFFEE82EE));

        public static final RegistryObject<MobEffect> WASTED = EFFECTS.register("wasted", () -> new WastedEffect(MobEffectCategory.NEUTRAL, 0xFFEE82EE));

        public static final RegistryObject<MobEffect> BLACK_OUT = EFFECTS.register("blackout", () -> new BlackoutEffect(MobEffectCategory.NEUTRAL, 0xFFEE82EE));


    }

    public static class Recipes {

        public static final RegistryObject<RecipeType<MillingRecipe>> MILLING_RECIPE = RECIPE_TYPES.register("milling", () -> RecipeType.simple(new ResourceLocation(MODID, "milling")));
        public static final RegistryObject<MillingRecipe.Serializer> MILLING_RECIPE_SERIALIZER = RECIPES.register("milling", MillingRecipe.Serializer::new);

        public static final RegistryObject<RecipeType<BrewingRecipe>> BREWING_RECIPE = RECIPE_TYPES.register("brewing", () -> RecipeType.simple(new ResourceLocation(MODID, "brewing")));
        public static final RegistryObject<BrewingRecipe.Serializer> BREWING_RECIPE_SERIALIZER = RECIPES.register("brewing", BrewingRecipe.Serializer::new);

        public static final RegistryObject<RecipeType<CultureJarRecipe>> CULTURE_JAR_RECIPE = RECIPE_TYPES.register("culture", () -> RecipeType.simple(new ResourceLocation(MODID, "culture")));
        public static final RegistryObject<CultureJarRecipe.Serializer> CULTURE_JAR_RECIPE_SERIALIZER = RECIPES.register("culture", CultureJarRecipe.Serializer::new);

        public static final RegistryObject<RecipeType<FermentationRecipe>> FERMENTATION_RECIPE = RECIPE_TYPES.register("fermentation", () -> RecipeType.simple(new ResourceLocation(MODID, "fermentation")));
        public static final RegistryObject<FermentationRecipe.Serializer> FERMENTATION_RECIPE_SERIALIZER = RECIPES.register("fermentation", FermentationRecipe.Serializer::new);

        public static void init() {

        }

    }

    public static class Menus {

        public static final RegistryObject<MenuType<MillingMenu>> MILLING_MENU = MENUS.register("milling_menu", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new MillingMenu(windowId, inv, inv.player, (MillingBlockEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<BrewingMenu>> BREWING_MENU = MENUS.register("brewing_menu", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new BrewingMenu(windowId, inv, inv.player, (BrewingKettleBlockEntity) level.getBlockEntity(pos));
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

        public static final RegistryObject<Item> ROASTED_BARLEY_ITEM = register(
                true,
                "roasted_barley_item", () -> new Item(new Item.Properties()));

        public static final RegistryObject<Item> ROPE_ITEM = register(
                true,
                "rope_item", () -> new RopeItem(new Item.Properties()));

        public static final RegistryObject<Item> HOPS_ITEM = register(
                true,
                "hops_item", () -> new Item(new Item.Properties()));

        public static final RegistryObject<Item> BARREL_TAP = register(
                true,
                "barrel_tap_item", () -> new Item(new Item.Properties()));

        public static final RegistryObject<Item> BREWERS_YEAST_ITEM = register(
                true,
                "brewers_yeast_item", () -> new Item(new Item.Properties()));

        public static final RegistryObject<Item> LAGER_YEAST_ITEM = register(
                true,
                "lager_yeast_item", () -> new Item(new Item.Properties()));

        public static final RegistryObject<Item> WILD_YEAST_ITEM = register(
                true,
                "wild_yeast_item", () -> new Item(new Item.Properties()));

        public static final RegistryObject<Item> PINT_ITEM = register(
                false,
                "pint_item", () -> new PintItem(new Item.Properties().stacksTo(1)));

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
                        .requiresCorrectToolForDrops().strength(1.5F, 3.0F)
                        .pushReaction(PushReaction.DESTROY)), false);

        public static final makeBlock<CultureJarBlock> CULTURE_JAR_BLOCK
                = makeBlock.build("culture_jar_block",
                CultureJarBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .sound(SoundType.DECORATED_POT)
                        .noOcclusion()
                        .pushReaction(PushReaction.DESTROY)), true);

        public static final makeBlock<BrewchemyEntityBlock<PintGlassBlockEntity>> PINT_GLASS_BLOCK
                = makeBlock.build("pint_glass_block",
                (p) -> new BrewchemyEntityBlock<>(BlockEntities.PINT_GLASS_BLOCK_ENTITY.getType(), p) {

                    @Override
                    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {

                        BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
                        if (blockentity instanceof PintGlassBlockEntity pintGlassBlockEntity) {
                            BlockPos pos = pintGlassBlockEntity.getBlockPos();
                            return pintGlassBlockEntity.getBeers();
                        }

                        return new ArrayList<>();
                    }

                    @Override
                    public RenderShape getRenderShape(BlockState p_60550_) {
                        return RenderShape.ENTITYBLOCK_ANIMATED;
                    }

                    @Override
                    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
                        VoxelShape shape = Shapes.empty();
                        if (level.getBlockEntity(pos) instanceof PintGlassBlockEntity pintGlassBlockEntity) {
                            int size = pintGlassBlockEntity.getBeers().stream().filter(p->!p.isEmpty()).toList().size();
                            if (size > 1) {
                                if (state.getValue(HorizontalDirectionalBlock.FACING).getAxis() == Direction.Axis.X) {
                                    shape = Shapes.join(shape, Shapes.box(0.5, 0, 0.0625, 0.875, 0.5625, 0.4375), BooleanOp.OR);
                                    shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.5625, 0.5, 0.5625, 0.9375), BooleanOp.OR);
                                } else {
                                    shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.125, 0.4375, 0.5625, 0.5), BooleanOp.OR);
                                    shape = Shapes.join(shape, Shapes.box(0.5625, 0, 0.5, 0.9375, 0.5625, 0.875), BooleanOp.OR);
                                }
                                } else {
                                shape = Shapes.join(shape, Shapes.box(0.4375, 0.1875, 0.1875, 0.5625, 0.4375, 0.3125), BooleanOp.OR);
                                shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.5625, 0.6875), BooleanOp.OR);
                            }
                        }
                        return shape;
                    }
                },
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .sound(SoundType.DECORATED_POT)
                        .noOcclusion()
                        .pushReaction(PushReaction.DESTROY)), false);

        public static final makeBlock<FermentationBarrelBlock> FERMENTATION_BARREL_BLOCK
                = makeBlock.build("fermentation_barrel_block",
                FermentationBarrelBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .sound(SoundType.WOOD)
                        .noOcclusion()), true);

        public static final makeBlock<BenchSeatBlock> BENCH_SEAT_BLOCK
                = makeBlock.build("bench_seat_block",
                BenchSeatBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .sound(SoundType.WOOD)
                        .noOcclusion()), true);

        public static final makeBlock<TableBlock> TABLE_BLOCK
                = makeBlock.build("table_block",
                TableBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .sound(SoundType.WOOD)
                        .noOcclusion()), true);


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

        public static final makeBlockEntity<CultureJarBlockEntity> CULTURE_JAR_BLOCK_ENTITY
                = makeBlockEntity.build("culture_jar_block_entity", CultureJarBlockEntity::new, Blocks.CULTURE_JAR_BLOCK);

        public static final makeBlockEntity<PintGlassBlockEntity> PINT_GLASS_BLOCK_ENTITY
                = makeBlockEntity.build("pint_glass_block_entity", PintGlassBlockEntity::new, Blocks.PINT_GLASS_BLOCK);

        public static final makeBlockEntity<FermentationBarrelBlockEntity> FERMENTATION_BARREL_BLOCK_ENTITY
                = makeBlockEntity.build("fermentation_barrel", FermentationBarrelBlockEntity::new, Blocks.FERMENTATION_BARREL_BLOCK);

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

        public static final RegistryObject<EntityType<SeatEntity>> SEAT_ENTITY =
                ENTITIES.register("seat_entity",
                        () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new,
                                        MobCategory.MISC)
                                .sized(1,1)
                                .clientTrackingRange(6)
                                .updateInterval(20)
                                .build("seat_entity"));

        public static void init() {

        }

    }

    public static class Fluids {

        public static final HashMap<String, makeFluid<?>> ALL_FLUIDS = new HashMap<>();

        public static final makeFluid<BrewchemyFluid> WORT = makeFluid.build("wort",
                false, true, () -> BrewchemyFluid.createColoured(0xFF92791e, false));

        public static final makeFluid<BrewchemyFluid> BEER_ALE = makeFluid.build("beer_ale",
                false, true, () -> AlcoholicFluid.create(0xFFf99100,
                        new int[] { TickUtils.minecraftDaysToTicks(3), TickUtils.minecraftDaysToTicks(3) + TickUtils.minutesToTicks(5) },
                        TickUtils.minecraftDaysToTicks(2),
                        TickUtils.minecraftDaysToTicks(4)));

        public static final makeFluid<BrewchemyFluid> BEER_LAGER = makeFluid.build("beer_lager",
                false, true, () -> AlcoholicFluid.create(0xFFd16401,
                        new int[] { TickUtils.minecraftDaysToTicks(6), TickUtils.minecraftDaysToTicks(8) - TickUtils.minutesToTicks(1) }, TickUtils.minecraftDaysToTicks(6), TickUtils.minecraftDaysToTicks(8)));

        public static final makeFluid<BrewchemyFluid> BEER_PALE_ALE = makeFluid.build("beer_pale_ale",
                false, true, () -> AlcoholicFluid.create(0xFFffb54e,
                        new int[] { TickUtils.minecraftDaysToTicks(5), TickUtils.minecraftDaysToTicks(5) + TickUtils.minutesToTicks(1) }, TickUtils.minecraftDaysToTicks(4), TickUtils.minecraftDaysToTicks(6)));

        public static final makeFluid<BrewchemyFluid> BEER_STOUT = makeFluid.build("beer_stout",
                false, true, () -> AlcoholicFluid.create(0xFF803d00,
                        new int[] { TickUtils.minecraftDaysToTicks(7), TickUtils.minecraftDaysToTicks(8) }, TickUtils.minecraftDaysToTicks(6), TickUtils.minecraftDaysToTicks(10)));

        public static void init() {

        }

        public static class makeFluid<T extends FluidType> implements Supplier<T> {

            private RegistryObject<T> fluidType;
            private RegistryObject<Fluid> fluid;
            private RegistryObject<FlowingFluid> flowingFluid;
            private RegistryObject<BrewchemyFluid.Block> block;
            private RegistryObject<Item> bucket;
            private ForgeFlowingFluid.Properties properties;

            private final String name;

            public makeFluid(RegistryObject<T> fluidType, String name, boolean placeable, boolean needsBucket) {
                this.fluidType = fluidType;
                this.name = name;
                this.properties = new ForgeFlowingFluid.Properties(this.fluidType, makeSource(name), makeFlowing(name));
                if (placeable) {
                    this.properties.block(makeBlock(name));
                }
                if (needsBucket) {
                    this.properties.bucket(makeBucket(name));
                }
            }

            public static <T extends FluidType> makeFluid<T> build(String name, boolean placeable, boolean bucket, Supplier<T> fluid) {
                RegistryObject<T> type = FLUID_TYPES.register(name, fluid);
                makeFluid<T> tmakeFluid = new makeFluid<>(type, name, placeable, bucket);
                ALL_FLUIDS.put(name, tmakeFluid);
                return tmakeFluid;
            }


            private RegistryObject<Fluid> makeSource(String name) {
                this.fluid = FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(this.properties));
                return fluid;
            }

            private RegistryObject<FlowingFluid> makeFlowing(String name) {
                this.flowingFluid = FLUIDS.register(name + "_flowing", () -> new ForgeFlowingFluid.Flowing(this.properties));
                return flowingFluid;
            }

            private RegistryObject<BrewchemyFluid.Block> makeBlock(String name) {
                this.block = BLOCKS.register(name + "_block",
                        () -> new BrewchemyFluid.Block(this.flowingFluid, BlockBehaviour.Properties.of()
                                .mapColor(MapColor.NONE)
                                .replaceable()
                                .noCollission()
                                .strength(100.0F)
                                .pushReaction(PushReaction.DESTROY)
                                .noLootTable()
                                .liquid()
                                .sound(SoundType.EMPTY)));
                return block;
            }

            private RegistryObject<Item> makeBucket(String name) {
                this.bucket = ITEMS.register(name + "_bucket",
                        () -> new BucketItem(this.fluid, new Item.Properties()
                                .craftRemainder(net.minecraft.world.item.Items.BUCKET)
                                .stacksTo(1)));
                Items.ALL_ITEMS.put(name, this.bucket);
                return bucket;
            }

            @Override
            public T get() {
                return this.fluidType.get();
            }

            public String getName() {
                return name;
            }

            public ForgeFlowingFluid.Properties getProperties() {
                return properties;
            }

            public RegistryObject<FlowingFluid> getFlowingFluid() {
                return flowingFluid;
            }

            public RegistryObject<Fluid> getFluid() {
                return fluid;
            }

            public RegistryObject<Item> getBucket() {
                return bucket;
            }

            public RegistryObject<BrewchemyFluid.Block> getBlock() {
                return block;
            }

            public RegistryObject<T> getType() {
                return fluidType;
            }
        }


    }

}
