package mod.kerzox.brewchemy.registry;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.menu.GerminationChamberMenu;
import mod.kerzox.brewchemy.client.gui.menu.MillstoneMenu;
import mod.kerzox.brewchemy.common.block.*;
import mod.kerzox.brewchemy.common.block.rope.RopeBlock;
import mod.kerzox.brewchemy.common.block.base.BrewchemyEntityBlock;
import mod.kerzox.brewchemy.common.blockentity.*;
import mod.kerzox.brewchemy.common.crafting.recipes.GerminationRecipe;
import mod.kerzox.brewchemy.common.crafting.recipes.MillstoneRecipe;
import mod.kerzox.brewchemy.common.item.base.BrewchemyItem;
import mod.kerzox.brewchemy.common.item.rope.RopeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static mod.kerzox.brewchemy.Brewchemy.MODID;
import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.BlockEntities.*;
import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.Blocks.*;

public class BrewchemyRegistry {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ITEMS.register(bus);
        RECIPE_TYPES.register(bus);
        RECIPES.register(bus);
        MENUS.register(bus);

        Items.init();
        Blocks.init();
        BlockEntities.init();
        Recipes.init();
        Menus.init();
    }

    public static final class Menus {
        public static void init(){}

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

    }

    public static final class Recipes {
        public static void init(){}

        public static final RegistryObject<RecipeType<MillstoneRecipe>> MILLSTONE_RECIPE = RECIPE_TYPES.register("millstone",() -> RecipeType.simple(new ResourceLocation(MODID, "millstone")));
        public static final RegistryObject<MillstoneRecipe.Serializer> MILLSTONE_RECIPE_SERIALIZER = RECIPES.register("millstone_recipe_serializer", MillstoneRecipe.Serializer::new);
        public static final RegistryObject<RecipeType<GerminationRecipe>> GERMINATION_RECIPE = RECIPE_TYPES.register("germination",() -> RecipeType.simple(new ResourceLocation(MODID, "germination")));
        public static final RegistryObject<GerminationRecipe.Serializer> GERMINATION_RECIPE_SERIALIZER = RECIPES.register("germination_recipe_serializer", GerminationRecipe.Serializer::new);


    }

    public static final class Items {
        public static void init(){}

        public static final RegistryObject<RopeItem> ROPE = ITEMS.register("rope_item", () -> new RopeItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        public static final RegistryObject<BrewchemyItem> SOAKED_BARLEY_ITEM = ITEMS.register("soaked_barley_item", () -> new BrewchemyItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));
        public static final RegistryObject<BrewchemyItem> GERMINATED_BARLEY_ITEM = ITEMS.register("germinated_barley_item", () -> new BrewchemyItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));
        public static final RegistryObject<BrewchemyItem> MALTED_BARLEY_ITEM = ITEMS.register("malted_barley_item", () -> new BrewchemyItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));
        public static final RegistryObject<BrewchemyItem> MILLED_BARLEY_ITEM = ITEMS.register("milled_barley_item", () -> new BrewchemyItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));
        public static final RegistryObject<BrewchemyItem> BREWERS_YEAST = ITEMS.register("brewers_yeast_item", () -> new BrewchemyItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));

    }

    public static final class Blocks {

        public static void init(){}

        public static final makeBlock<BrewchemyEntityBlock<MillStoneBlockEntity>> MILL_STONE_BLOCK = makeBlock.build("millstone_block", p -> new BrewchemyEntityBlock<>(MILL_STONE.getType(), p), BlockBehaviour.Properties.of(Material.METAL), true);
        public static final makeBlock<BrewchemyEntityBlock<BoilKettleBlockEntity>> BREWING_POT_BLOCK = makeBlock.build("brewing_pot_block", p -> new BrewchemyEntityBlock<>(BREWING_POT.getType(), p), BlockBehaviour.Properties.of(Material.METAL), true);
        public static final makeBlock<BarleyCropBlock> BARLEY_CROP_BLOCK = makeBlock.build("barley_crop", BarleyCropBlock::new, BlockBehaviour.Properties.of(Material.PLANT), true);
        public static final makeBlock<HopsCropBlock> HOPS_CROP_BLOCK = makeBlock.build("hops_crop", HopsCropBlock::new, BlockBehaviour.Properties.of(Material.PLANT), true);
        public static final makeBlock<SupportStickBlock> SUPPORT_STICK_BLOCK = makeBlock.build("support_stick_block", SupportStickBlock::new, BlockBehaviour.Properties.of(Material.WOOD), true);
        public static final makeBlock<RopeBlock> ROPE_BLOCK = makeBlock.build("rope_block", RopeBlock::new, BlockBehaviour.Properties.of(Material.AIR).sound(SoundType.WOOL), true);
        public static final makeBlock<RopeTiedFenceBlock> ROPE_FENCE_BLOCK = makeBlock.build("rope_fence_block", RopeTiedFenceBlock::new, BlockBehaviour.Properties.of(Material.WOOD), false);
        public static final makeBlock<FermentsJarBlock> FERMENTS_JAR_BLOCK = makeBlock.build("ferments_jar_block", FermentsJarBlock::new, BlockBehaviour.Properties.of(Material.GLASS), true);
        public static final makeBlock<MillstoneCrankBlock> MILLSTONE_CRANK_BLOCK = makeBlock.build("millstone_crank_block", MillstoneCrankBlock::new, BlockBehaviour.Properties.of(Material.METAL).noCollission(), true);
        public static final makeBlock<BrewchemyEntityBlock<GerminationChamberBlockEntity>> GERMINATION_CHAMBER_BLOCK = makeBlock.build("germination_chamber_block", p -> new BrewchemyEntityBlock<>(GERMINATION_CHAMBER.getType(), p), BlockBehaviour.Properties.of(Material.METAL), true);
    }

    public static final class BlockEntities {

        public static void init(){}

        public static final makeBlockEntity<MillstoneCrankBlockEntity> MILL_STONE_CRANK = makeBlockEntity.build("millstone_crank_be", MillstoneCrankBlockEntity::new, MILLSTONE_CRANK_BLOCK);
        public static final makeBlockEntity<MillStoneBlockEntity> MILL_STONE = makeBlockEntity.build("millstone_be", MillStoneBlockEntity::new, MILL_STONE_BLOCK);
        public static final makeBlockEntity<FermentsJarBlockEntity> FERMENTS_JAR = makeBlockEntity.build("ferments_jar_be", FermentsJarBlockEntity::new, FERMENTS_JAR_BLOCK);
        public static final makeBlockEntity<BoilKettleBlockEntity> BREWING_POT = makeBlockEntity.build("brewing_pot_be", BoilKettleBlockEntity::new, BREWING_POT_BLOCK);
        public static final makeBlockEntity<SupportStickEntityBlock> SUPPORT_STICK = makeBlockEntity.build("support_stick_be", SupportStickEntityBlock::new, SUPPORT_STICK_BLOCK);
        public static final makeBlockEntity<RopeBlockEntity> ROPE = makeBlockEntity.build("rope_be", RopeBlockEntity::new, ROPE_BLOCK);
        public static final makeBlockEntity<RopeTiedFenceBlockEntity> ROPE_FENCE = makeBlockEntity.build("rope_fence_be", RopeTiedFenceBlockEntity::new, ROPE_FENCE_BLOCK);
        public static final makeBlockEntity<GerminationChamberBlockEntity> GERMINATION_CHAMBER = makeBlockEntity.build("germination_chamber_be", GerminationChamberBlockEntity::new, GERMINATION_CHAMBER_BLOCK);
    }

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
            if (asItem) ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
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
