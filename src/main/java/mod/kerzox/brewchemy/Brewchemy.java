package mod.kerzox.brewchemy;

import com.mojang.logging.LogUtils;
import mod.kerzox.brewchemy.common.capabilities.BrewchemyCapabilities;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
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

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientLoad);
        FMLJavaModLoadingContext.get().getModEventBus().register(new BrewchemyCapabilities());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonLoad(final FMLCommonSetupEvent event) {

    }

    private void clientLoad(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(BARLEY_CROP_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(HOPS_CROP_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ROPE_BLOCK.get(), RenderType.cutout());
    }

}
