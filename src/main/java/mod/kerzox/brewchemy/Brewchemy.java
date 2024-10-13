package mod.kerzox.brewchemy;

import com.mojang.logging.LogUtils;
import mod.kerzox.brewchemy.client.ClientSetup;
import mod.kerzox.brewchemy.common.capabilities.drunk.IntoxicationManager;
import mod.kerzox.brewchemy.common.data.BrewingKettleHeating;
import mod.kerzox.brewchemy.common.event.CommonEvents;
import mod.kerzox.brewchemy.common.event.TickUtils;
import mod.kerzox.brewchemy.common.item.PintItem;
import mod.kerzox.brewchemy.common.network.PacketHandler;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.*;
import net.minecraftforge.common.crafting.conditions.*;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onEntityRenderRegister);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new TickUtils());
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(ClientSetup::init));
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerDatapackRegistries);
    }


    private void commonLoad(final FMLCommonSetupEvent event) {

    }

    private void onEntityRenderRegister(EntityRenderersEvent.RegisterRenderers e) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == BrewchemyRegistry.BREWCHEMY_TAG.getKey()) {
            for (ItemStack stack : PintItem.makeDrinks()) {
                 event.accept(stack);
            }
            for (BrewchemyRegistry.Fluids.makeFluid<?> value : BrewchemyRegistry.Fluids.ALL_FLUIDS.values()) {
                if (value.getBucket() != null) {
                    event.accept(value.getBucket());
                }
            }
            event.accept(BrewchemyRegistry.Blocks.BARLEY_CROP_BLOCK.get());
            event.accept(BrewchemyRegistry.Items.BARLEY_ITEM.get());
            event.accept(BrewchemyRegistry.Items.ROASTED_BARLEY_ITEM.get());
            event.accept(BrewchemyRegistry.Items.MILLED_BARLEY_ITEM.get());
            event.accept(BrewchemyRegistry.Blocks.HOPS_CROP_BLOCK.get());
            event.accept(BrewchemyRegistry.Items.HOPS_ITEM.get());
            event.accept(BrewchemyRegistry.Items.BREWERS_YEAST_ITEM.get());
            event.accept(BrewchemyRegistry.Items.WILD_YEAST_ITEM.get());
            event.accept(BrewchemyRegistry.Items.LAGER_YEAST_ITEM.get());
            event.accept(BrewchemyRegistry.Items.ROPE_ITEM.get());
            event.accept(BrewchemyRegistry.Blocks.BREWING_KETTLE_BLOCK.get());
            event.accept(BrewchemyRegistry.Blocks.CULTURE_JAR_BLOCK.get());
            event.accept(BrewchemyRegistry.Items.FERMENTATION_BARREL_ITEM.get());
            event.accept(BrewchemyRegistry.Items.BARREL_TAP.get());
            event.accept(BrewchemyRegistry.Blocks.BARLEY_CROP_BLOCK.get());
            event.accept(BrewchemyRegistry.Blocks.MILLING_BLOCK.get());
            event.accept(BrewchemyRegistry.Blocks.TABLE_BLOCK.get());
            event.accept(BrewchemyRegistry.Blocks.BENCH_SEAT_BLOCK.get());
        }
    }



    @SubscribeEvent
    public void registerServerReloadListener(AddReloadListenerEvent event) {
        event.addListener(BrewingKettleHeating.ReloadListener.INSTANCE);
    }

    private void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                BrewchemyRegistry.DataPacks.KETTLE_HEATING_REGISTRY_KEY,
                BrewingKettleHeating.KETTLE_HEATING_CODEC,
                BrewingKettleHeating.KETTLE_HEATING_CODEC
        );
    }

}

