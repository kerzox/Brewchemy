package mod.kerzox.brewchemy.registry;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.menu.FermentationBarrelMenu;
import mod.kerzox.brewchemy.client.gui.menu.GerminationChamberMenu;
import mod.kerzox.brewchemy.client.gui.menu.MillstoneMenu;
import mod.kerzox.brewchemy.client.particles.BoilingBubbleParticle;
import mod.kerzox.brewchemy.common.block.*;
import mod.kerzox.brewchemy.common.block.base.BrewchemyInvisibleBlock;
import mod.kerzox.brewchemy.common.block.rope.RopeBlock;
import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.*;
import mod.kerzox.brewchemy.common.blockentity.warehouse.WarehouseBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.warehouse.WarehouseStorageBlockEntity;
import mod.kerzox.brewchemy.common.crafting.ingredient.CountSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.OldFluidIngredient;
import mod.kerzox.brewchemy.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.brewchemy.common.crafting.recipes.*;
import mod.kerzox.brewchemy.common.effects.IntoxicatedEffect;
import mod.kerzox.brewchemy.common.fluid.BrewchemyFluidType;
import mod.kerzox.brewchemy.common.fluid.BrewchemyLiquidBlock;
import mod.kerzox.brewchemy.common.item.PintGlassItem;
import mod.kerzox.brewchemy.common.item.SoftMalletUtilityItem;
import mod.kerzox.brewchemy.common.item.base.BrewchemyItem;
import mod.kerzox.brewchemy.common.loot.BrewchemyLootRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
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
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jline.terminal.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static mod.kerzox.brewchemy.Brewchemy.MODID;
import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.BlockEntities.*;
import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.Blocks.*;

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
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

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

        Items.init();
        Effects.init();
        Blocks.init();
        BlockEntities.init();
        Fluids.init();
        Recipes.init();
        Menus.init();
        BrewchemyLootRegistry.init(bus);
//        Particles.init();

        CraftingHelper.register(new ResourceLocation(Brewchemy.MODID, "size_specific_ingredient"), SizeSpecificIngredient.Serializer.INSTANCE);
        CraftingHelper.register(new ResourceLocation(Brewchemy.MODID, "fluid_ingredient"), FluidIngredient.Serializer.INSTANCE);
    }

    public static final CreativeModeTab BREWCHEMY_TAB = new CreativeModeTab("brewchemy") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.PINT_GLASS.get());
        }
    };

    public static class Particles {

        public static void init() {

        }

        public static final RegistryObject<BoilingBubbleParticle.BoilingBubbleType> BOILING_BUBBLE_TYPE = PARTICLE_TYPES.register("boilingbubble",
                () -> new BoilingBubbleParticle.BoilingBubbleType(false));

    }

    public static final class Effects {
        public static void init() {

        }

        public static final RegistryObject<MobEffect> INTOXICATED = EFFECTS.register("intoxicated", () -> new IntoxicatedEffect(MobEffectCategory.NEUTRAL, 0xFFEE82EE));

    }

    public static final class Menus {
        public static void init() {
        }

        public static final RegistryObject<MenuType<MillstoneMenu>> MILLSTONE_GUI = MENUS.register("millstone", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.getLevel();
            return new MillstoneMenu(windowId, inv, inv.player, (MillStoneBlockEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<GerminationChamberMenu>> GERMINATION_CHAMBER_GUI = MENUS.register("germination", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.getLevel();
            return new GerminationChamberMenu(windowId, inv, inv.player, (GerminationChamberBlockEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<FermentationBarrelMenu>> FERMENTATION_BARREL_MENU = MENUS.register("fermentation", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.getLevel();
            return new FermentationBarrelMenu(windowId, inv, inv.player, (WoodenBarrelBlockEntity) level.getBlockEntity(pos));
        }));

    }

    public static final class Recipes {
        public static void init() {
        }

        public static final RegistryObject<RecipeType<MillstoneRecipe>> MILLSTONE_RECIPE = RECIPE_TYPES.register("millstone", () -> RecipeType.simple(new ResourceLocation(MODID, "millstone")));
        public static final RegistryObject<MillstoneRecipe.Serializer> MILLSTONE_RECIPE_SERIALIZER = RECIPES.register("millstone_recipe_serializer", MillstoneRecipe.Serializer::new);
        public static final RegistryObject<RecipeType<FermentJarRecipe>> FERMENTS_JAR_RECIPE = RECIPE_TYPES.register("fermentsjar", () -> RecipeType.simple(new ResourceLocation(MODID, "fermentsjar")));
        public static final RegistryObject<FermentJarRecipe.Serializer> FERMENTS_JAR_RECIPE_SERIALIZER = RECIPES.register("fermentsjar_serializer", FermentJarRecipe.Serializer::new);
        public static final RegistryObject<RecipeType<FermentationRecipe>> FERMENTATION_RECIPE = RECIPE_TYPES.register("fermentation", () -> RecipeType.simple(new ResourceLocation(MODID, "fermentation")));
        public static final RegistryObject<FermentationRecipe.Serializer> FERMENTATION_RECIPE_SERIALIZER = RECIPES.register("fermentation_serializer", FermentationRecipe.Serializer::new);
        public static final RegistryObject<RecipeType<BrewingRecipe>> BREWING_RECIPE = RECIPE_TYPES.register("brewing", () -> RecipeType.simple(new ResourceLocation(MODID, "brewing")));
        public static final RegistryObject<BrewingRecipe.Serializer> BREWING_RECIPE_SERIALIZER = RECIPES.register("brewing_serializer", BrewingRecipe.Serializer::new);


    }

    public static final class Items {
        public static void init() {
        }

        public static final RegistryObject<BrewchemyItem> HOPS_ITEM = ITEMS.register("hops_item", () -> new BrewchemyItem(new Item.Properties().tab(BREWCHEMY_TAB)));
        public static final RegistryObject<BrewchemyItem> BARLEY_ITEM = ITEMS.register("barley_item", () -> new BrewchemyItem(new Item.Properties().tab(BREWCHEMY_TAB)));
        public static final RegistryObject<BrewchemyItem> GRAPE_ITEM = ITEMS.register("grape_item", () -> new BrewchemyItem(new Item.Properties().tab(BREWCHEMY_TAB)));
        public static final RegistryObject<BrewchemyItem> SOAKED_BARLEY_ITEM = ITEMS.register("soaked_barley_item", () -> new BrewchemyItem(new Item.Properties().tab(BREWCHEMY_TAB)));
        //public static final RegistryObject<BrewchemyItem> GERMINATED_BARLEY_ITEM = ITEMS.register("germinated_barley_item", () -> new BrewchemyItem(new Item.Properties().tab(BREWCHEMY_TAB)));
        public static final RegistryObject<BrewchemyItem> MALTED_BARLEY_ITEM = ITEMS.register("malted_barley_item", () -> new BrewchemyItem(new Item.Properties().tab(BREWCHEMY_TAB)));
        public static final RegistryObject<BrewchemyItem> MILLED_BARLEY_ITEM = ITEMS.register("milled_barley_item", () -> new BrewchemyItem(new Item.Properties().tab(BREWCHEMY_TAB)));
        public static final RegistryObject<BrewchemyItem> BREWERS_YEAST = ITEMS.register("brewers_yeast_item", () -> new BrewchemyItem(new Item.Properties().tab(BREWCHEMY_TAB)));
        public static final RegistryObject<PintGlassItem> PINT_GLASS = ITEMS.register("pint_glass_item", () -> new PintGlassItem(new Item.Properties().tab(BREWCHEMY_TAB).stacksTo(1)));
        public static final RegistryObject<SoftMalletUtilityItem> SOFT_MALLET = ITEMS.register("soft_mallet", () -> new SoftMalletUtilityItem(new Item.Properties().tab(BREWCHEMY_TAB).stacksTo(1)));
    }

    public static final class Blocks {

        public static void init() {
        }

        public static final makeBlock<BrewchemyInvisibleBlock> INVISIBLE_BLOCK = makeBlock.build("invisible_block", BrewchemyInvisibleBlock::new, BlockBehaviour.Properties.of(Material.GLASS).noCollission().noLootTable(), false);
        public static final makeBlock<BrewchemyEntityBlock<MillStoneBlockEntity>> MILL_STONE_BLOCK = makeBlock.build("millstone_block", p -> new BrewchemyEntityBlock<>(MILL_STONE.getType(), p), BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(1.5F), true);
        public static final makeBlock<BarleyCropBlock> BARLEY_CROP_BLOCK = makeBlock.build("barley_crop", BarleyCropBlock::new, BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP), true);
        public static final makeBlock<HopsCropBlock> HOPS_CROP_BLOCK = makeBlock.build("hops_crop", HopsCropBlock::new, BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP), true);
        //public static final makeBlock<SupportStickBlock> SUPPORT_STICK_BLOCK = makeBlock.build("support_stick_block", SupportStickBlock::new, BlockBehaviour.Properties.of(Material.WOOD), true);
        public static final makeBlock<RopeBlock> ROPE_BLOCK = makeBlock.buildCustomSuppliedItem("rope_block", RopeBlock::new, BlockBehaviour.Properties.of(Material.AIR).sound(SoundType.WOOL), () -> new RopeBlock.Item(new Item.Properties().tab(BREWCHEMY_TAB)));
        public static final makeBlock<RopeTiedFenceBlock> ROPE_FENCE_BLOCK = makeBlock.build("rope_fence_block", RopeTiedFenceBlock::new, BlockBehaviour.Properties.of(Material.WOOD).strength(1F), false);
        public static final makeBlock<FermentsJarBlock> FERMENTS_JAR_BLOCK = makeBlock.build("ferments_jar_block", FermentsJarBlock::new, BlockBehaviour.Properties.of(Material.GLASS).strength(0.5F), true);
        public static final makeBlock<MillstoneCrankBlock> MILLSTONE_CRANK_BLOCK = makeBlock.build("millstone_crank_block", MillstoneCrankBlock::new, BlockBehaviour.Properties.of(Material.METAL).noCollission().requiresCorrectToolForDrops().strength(1.5F), true);
        //public static final makeBlock<BrewchemyEntityBlock<GerminationChamberBlockEntity>> GERMINATION_CHAMBER_BLOCK = makeBlock.build("germination_chamber_block", p -> new BrewchemyEntityBlock<>(GERMINATION_CHAMBER.getType(), p), BlockBehaviour.Properties.of(Material.METAL), true);
        public static final makeBlock<FluidBarrelBlock<WoodenBarrelBlockEntity>> WOODEN_BARREL_BLOCK = makeBlock.build("wooden_barrel_block", p -> new FluidBarrelBlock<>(WOODEN_BARREL.getType(), p), BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(1.0F), true);
        public static final makeBlock<BoilKettleBlock> BOIL_KETTLE_BLOCK = makeBlock.build("boil_kettle_block", p -> new BoilKettleBlock(BREWING_POT.getType(), p), BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(3F), true);
        public static final makeBlock<BoilKettleBlock.BoilKettleTop> BOIL_KETTLE_TOP_BLOCK = makeBlock.build("boil_kettle_top_block", BoilKettleBlock.BoilKettleTop::new, BlockBehaviour.Properties.of(Material.METAL).noCollission().noLootTable().requiresCorrectToolForDrops().strength(3F), false);
        public static final makeBlock<WarehouseBlock> WAREHOUSE_BLOCK = makeBlock.buildCustomSuppliedItem("warehouse_block",  p -> new WarehouseBlock(WAREHOUSE.getType(), p), BlockBehaviour.Properties.of(Material.WOOD).strength(1.5F), () -> new WarehouseBlock.Item(new Item.Properties().tab(BREWCHEMY_TAB)));
        public static final makeBlock<WarehouseBlock.WarehouseStorageBlock> WAREHOUSE_STORAGE_BLOCK = makeBlock.build("warehouse_storage_block", WarehouseBlock.WarehouseStorageBlock::new, BlockBehaviour.Properties.of(Material.GLASS).strength(-1.0F, 3600000.0F).noLootTable(), false);
        public static final makeBlock<GrapeFlowerBlock.GrapeTrunkBlock> GRAPE_TRUNK_BLOCK = makeBlock.build("grape_trunk_block", GrapeFlowerBlock.GrapeTrunkBlock::new, BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.WOOD), true);
        public static final makeBlock<GrapeFlowerBlock> GRAPE_FLOWER_BLOCK = makeBlock.build("grape_flower_block", GrapeFlowerBlock::new, BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP), false);

        public static class makeBlock<T extends Block> implements Supplier<T> {

            public static final List<makeBlock<?>> ENTRIES = new ArrayList<>();

            private final RegistryObject<T> block;

            private final String name;

            private makeBlock(String name, RegistryObject<T> block) {
                this.name = name;
                this.block = block;
                ENTRIES.add(this);
            }

            public static <T extends Block> makeBlock<T> build(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties prop, boolean asItem) {
                RegistryObject<T> ret = BLOCKS.register(name, () -> block.apply(prop));
                if (asItem)
                    ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(BREWCHEMY_TAB)));
                return new makeBlock<>(name, ret);
            }

            public static <T extends Block> makeBlock<T> buildCustomSuppliedItem(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties prop, Supplier<BlockItem> itemSupplier) {
                RegistryObject<T> ret = BLOCKS.register(name, () -> block.apply(prop));
                ITEMS.register(name, itemSupplier);
                return new makeBlock<>(name, ret);
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

    public static final class BlockEntities {

        public static void init() {
        }

        public static final makeBlockEntity<MillstoneCrankBlockEntity> MILL_STONE_CRANK = makeBlockEntity.build("millstone_crank_be", MillstoneCrankBlockEntity::new, MILLSTONE_CRANK_BLOCK);
        public static final makeBlockEntity<MillStoneBlockEntity> MILL_STONE = makeBlockEntity.build("millstone_be", MillStoneBlockEntity::new, MILL_STONE_BLOCK);
        public static final makeBlockEntity<FermentsJarBlockEntity> FERMENTS_JAR = makeBlockEntity.build("ferments_jar_be", FermentsJarBlockEntity::new, FERMENTS_JAR_BLOCK);
        public static final makeBlockEntity<BoilKettleBlockEntity> BREWING_POT = makeBlockEntity.build("brewing_pot_be", BoilKettleBlockEntity::new, BOIL_KETTLE_BLOCK);
        public static final makeBlockEntity<BoilKettleBlockEntity.TopBlockEntity> BREWING_TOP_POT = makeBlockEntity.build("brewing_pot_top_be", BoilKettleBlockEntity.TopBlockEntity::new, BOIL_KETTLE_TOP_BLOCK);
       // public static final makeBlockEntity<SupportStickEntityBlock> SUPPORT_STICK = makeBlockEntity.build("support_stick_be", SupportStickEntityBlock::new, SUPPORT_STICK_BLOCK);
        public static final makeBlockEntity<RopeBlockEntity> ROPE = makeBlockEntity.build("rope_be", RopeBlockEntity::new, ROPE_BLOCK);
        public static final makeBlockEntity<RopeTiedFenceBlockEntity> ROPE_FENCE = makeBlockEntity.build("rope_fence_be", RopeTiedFenceBlockEntity::new, ROPE_FENCE_BLOCK);
        //public static final makeBlockEntity<GerminationChamberBlockEntity> GERMINATION_CHAMBER = makeBlockEntity.build("germination_chamber_be", GerminationChamberBlockEntity::new, GERMINATION_CHAMBER_BLOCK);
        public static final makeBlockEntity<WoodenBarrelBlockEntity> WOODEN_BARREL = makeBlockEntity.build("wooden_barrel_be", WoodenBarrelBlockEntity::new, WOODEN_BARREL_BLOCK);
        public static final makeBlockEntity<WarehouseBlockEntity> WAREHOUSE = makeBlockEntity.build("warehouse_be", WarehouseBlockEntity::new, WAREHOUSE_BLOCK);
        public static final makeBlockEntity<WarehouseStorageBlockEntity> WAREHOUSE_STORAGE = makeBlockEntity.build("warehouse_storage_be", WarehouseStorageBlockEntity::new, WAREHOUSE_STORAGE_BLOCK);
        public static class makeBlockEntity<T extends BlockEntity> implements Supplier<BlockEntityType<T>> {

            private final RegistryObject<BlockEntityType<T>> type;

            public static <T extends BlockEntity> makeBlockEntity<T> build(
                    String name,
                    BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier,
                    Supplier<? extends Block> valid) {
                return new makeBlockEntity<T>(BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, valid.get()).build(null)));
            }

            public static <T extends BlockEntity> makeBlockEntity<T> buildEntityAndBlock(
                    String name,
                    BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier,
                    makeBlock<?> valid) {
                return new makeBlockEntity<T>(BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, valid.get()).build(null)));
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

    public static final class Fluids {

        public static void init() {
        }

        public static final List<makeFluid<?>> FLUID_LIST = new ArrayList<>();

        public static final makeFluid<BrewchemyFluidType> WORT = makeFluid.build("wort", false, true, () -> BrewchemyFluidType.createColoured(0xFF56230E, false));
        public static final makeFluid<BrewchemyFluidType> BEER = makeFluid.build("beer", false, true, () -> BrewchemyFluidType.createColoured(0xFFfef068, true));

        public static class makeFluid<T extends FluidType> implements Supplier<T> {

            private RegistryObject<T> fluidType;
            private RegistryObject<Fluid> fluid;
            private RegistryObject<FlowingFluid> flowingFluid;
            private RegistryObject<BrewchemyLiquidBlock> block;
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
                FLUID_LIST.add(this);
            }

            public static <T extends FluidType> makeFluid<T> build(String name, boolean placeable, boolean bucket, Supplier<T> fluid) {
                RegistryObject<T> type = FLUID_TYPES.register(name, fluid);
                return new makeFluid<>(type, name, placeable, bucket);
            }


            private RegistryObject<Fluid> makeSource(String name) {
                this.fluid = FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(this.properties));
                return fluid;
            }

            private RegistryObject<FlowingFluid> makeFlowing(String name) {
                this.flowingFluid = FLUIDS.register(name+"_flowing", () -> new ForgeFlowingFluid.Flowing(this.properties));
                return flowingFluid;
            }

            private RegistryObject<BrewchemyLiquidBlock> makeBlock(String name) {
                this.block = BLOCKS.register(name+"_block",
                        () -> new BrewchemyLiquidBlock(this.flowingFluid, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable()));
                return block;
            }

            private RegistryObject<Item> makeBucket(String name) {
                this.bucket = ITEMS.register(name+"_bucket",
                        () -> new BucketItem(this.fluid, new Item.Properties()
                                .craftRemainder(net.minecraft.world.item.Items.BUCKET)
                                .stacksTo(1)
                                .tab(BREWCHEMY_TAB)));
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

            public RegistryObject<BrewchemyLiquidBlock> getBlock() {
                return block;
            }

            public RegistryObject<T> getType () {
                return fluidType;
            }
        }

    }

}
