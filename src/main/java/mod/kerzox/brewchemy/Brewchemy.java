package mod.kerzox.brewchemy;

import ca.weblite.objc.Client;
import com.mojang.logging.LogUtils;
import mod.kerzox.brewchemy.client.ClientStartupEvents;
import mod.kerzox.brewchemy.client.gui.screen.FermentationScreen;
import mod.kerzox.brewchemy.client.gui.screen.GerminationScreen;
import mod.kerzox.brewchemy.client.gui.screen.MillstoneScreen;
import mod.kerzox.brewchemy.client.render.MillstoneCrankBlockEntityRenderer;
import mod.kerzox.brewchemy.common.capabilities.BrewchemyCapabilities;
import mod.kerzox.brewchemy.common.crafting.misc.CauldronRecipes;
import mod.kerzox.brewchemy.common.events.CommonEvents;
import mod.kerzox.brewchemy.common.network.PacketHandler;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import static mod.kerzox.brewchemy.registry.BrewchemyRegistry.Blocks.*;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Brewchemy.MODID)
public class Brewchemy
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "brewchemy";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Brewchemy()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BrewchemyRegistry.init(modEventBus);
        PacketHandler.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onEntityRenderRegister);
        FMLJavaModLoadingContext.get().getModEventBus().register(new BrewchemyCapabilities());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonLoad(final FMLCommonSetupEvent event) {
        event.enqueueWork(CauldronRecipes::register);
    }

    private void clientLoad(final FMLClientSetupEvent event) {
        ClientStartupEvents.init();
        ItemBlockRenderTypes.setRenderLayer(BARLEY_CROP_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(HOPS_CROP_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ROPE_BLOCK.get(), RenderType.cutout());
        MenuScreens.register(BrewchemyRegistry.Menus.MILLSTONE_GUI.get(), MillstoneScreen::new);
        MenuScreens.register(BrewchemyRegistry.Menus.GERMINATION_CHAMBER_GUI.get(), GerminationScreen::new);
        MenuScreens.register(BrewchemyRegistry.Menus.FERMENTATION_BARREL_MENU.get(), FermentationScreen::new);
    }

    private void onEntityRenderRegister(EntityRenderersEvent.RegisterRenderers e) {
        System.out.println("Registering Entity Renderers");
        e.registerBlockEntityRenderer(BrewchemyRegistry.BlockEntities.MILL_STONE_CRANK.get(), MillstoneCrankBlockEntityRenderer::new);
    }

}

