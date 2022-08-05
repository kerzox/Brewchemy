package mod.kerzox.brewchemy;

import com.mojang.logging.LogUtils;
import mod.kerzox.brewchemy.client.ClientStartupEvents;
import mod.kerzox.brewchemy.client.RenderEvent;
import mod.kerzox.brewchemy.client.gui.screen.FermentationBarrelScreen;
import mod.kerzox.brewchemy.client.gui.screen.GerminationScreen;
import mod.kerzox.brewchemy.client.gui.screen.MillstoneScreen;
import mod.kerzox.brewchemy.client.particles.BoilingBubbleParticle;
import mod.kerzox.brewchemy.client.render.*;
import mod.kerzox.brewchemy.common.capabilities.BrewchemyCapabilities;
import mod.kerzox.brewchemy.common.crafting.misc.CauldronRecipes;
import mod.kerzox.brewchemy.common.network.PacketHandler;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


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
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerParticleProvider);
        FMLJavaModLoadingContext.get().getModEventBus().register(new BrewchemyCapabilities());
        MinecraftForge.EVENT_BUS.register(new RenderEvent());
        MinecraftForge.EVENT_BUS.register(this);
    }

//    private void registerParticleProvider(RegisterParticleProvidersEvent event) {
//        event.register(BrewchemyRegistry.Particles.BOILING_BUBBLE_TYPE.get(), () -> new BoilingBubbleParticle.Provider());
//    }

    private void commonLoad(final FMLCommonSetupEvent event) {
        event.enqueueWork(CauldronRecipes::register);
    }

    private void clientLoad(final FMLClientSetupEvent event) {
        ClientStartupEvents.init();
        MenuScreens.register(BrewchemyRegistry.Menus.MILLSTONE_GUI.get(), MillstoneScreen::new);
       // MenuScreens.register(BrewchemyRegistry.Menus.GERMINATION_CHAMBER_GUI.get(), GerminationScreen::new);
        MenuScreens.register(BrewchemyRegistry.Menus.FERMENTATION_BARREL_MENU.get(), FermentationBarrelScreen::new);
    }

    private void onEntityRenderRegister(EntityRenderersEvent.RegisterRenderers e) {
        System.out.println("Registering Entity Renderers");
        e.registerBlockEntityRenderer(BrewchemyRegistry.BlockEntities.MILL_STONE_CRANK.get(), MillstoneCrankBlockEntityRenderer::new);
        e.registerBlockEntityRenderer(BrewchemyRegistry.BlockEntities.BREWING_POT.get(), BoilKettleBlockEntityRenderer::new);
        e.registerBlockEntityRenderer(BrewchemyRegistry.BlockEntities.FERMENTS_JAR.get(), CultureJarBlockEntityRenderer::new);
        //e.registerBlockEntityRenderer(BrewchemyRegistry.BlockEntities.WAREHOUSE.get(), WarehouseBlockEntityRenderer::new);
        e.registerBlockEntityRenderer(BrewchemyRegistry.BlockEntities.WAREHOUSE_STORAGE.get(), WarehouseSlotBlockEntityRenderer::new);
    }

}

