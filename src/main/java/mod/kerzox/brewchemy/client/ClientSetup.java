package mod.kerzox.brewchemy.client;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.render.baked.PintGlassBakedModel;
import mod.kerzox.brewchemy.client.render.baked.RopeTiedPostBakedModel;
import mod.kerzox.brewchemy.client.render.blockentity.CultureJarBlockEntityRenderer;
import mod.kerzox.brewchemy.client.render.blockentity.FermentationBarrelBlockEntityRenderer;
import mod.kerzox.brewchemy.client.render.blockentity.PintGlassBlockEntityRenderer;
import mod.kerzox.brewchemy.client.render.entity.RopeEntityRenderer;
import mod.kerzox.brewchemy.client.render.entity.NoEntityRenderer;
import mod.kerzox.brewchemy.client.render.overlay.BlackoutOverlay;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.client.ui.screen.BrewingScreen;
import mod.kerzox.brewchemy.client.ui.screen.MillingScreen;
import mod.kerzox.brewchemy.common.effects.IntoxicatedEffect;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_SKY;
import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS;

@Mod.EventBusSubscriber(modid = Brewchemy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            MenuScreens.register(BrewchemyRegistry.Menus.MILLING_MENU.get(), MillingScreen::new);
            MenuScreens.register(BrewchemyRegistry.Menus.BREWING_MENU.get(), BrewingScreen::new);
            EntityRenderers.register(BrewchemyRegistry.Entities.ROPE_ENTITY.get(), RopeEntityRenderer::new);
            EntityRenderers.register(BrewchemyRegistry.Entities.SEAT_ENTITY.get(), NoEntityRenderer::new);
            BlockEntityRenderers.register(BrewchemyRegistry.BlockEntities.CULTURE_JAR_BLOCK_ENTITY.get(), CultureJarBlockEntityRenderer::new);
            BlockEntityRenderers.register(BrewchemyRegistry.BlockEntities.PINT_GLASS_BLOCK_ENTITY.get(), PintGlassBlockEntityRenderer::new);
            BlockEntityRenderers.register(BrewchemyRegistry.BlockEntities.FERMENTATION_BARREL.get(), FermentationBarrelBlockEntityRenderer::new);
        });

        MinecraftForge.EVENT_BUS.register(new ClientEvents());

    }

    @SubscribeEvent
    public static void onColorsEventItem(RegisterColorHandlersEvent.Item event) {

    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiOverlaysEvent event) {
        // Register custom overlay layer
        event.registerAboveAll("blackout_overlay", new BlackoutOverlay());
    }

    @SubscribeEvent
    public static void onModelRegister(ModelEvent.RegisterAdditional event) {
        for (ResourceLocation glass : PintGlassBlockEntityRenderer.PINT_GLASS) {
            event.register(glass);
        }
        event.register(FermentationBarrelBlockEntityRenderer.SINGLE);
        event.register(FermentationBarrelBlockEntityRenderer.MULTIBLOCK);
        event.register(FermentationBarrelBlockEntityRenderer.TAP);
    }


    @SubscribeEvent
    public static void onModelBakeEvent(ModelEvent.ModifyBakingResult event) {
        for (BlockState blockState : BrewchemyRegistry.Blocks.ROPE_TIED_POST_BLOCK.get().getStateDefinition().getPossibleStates()) {
            ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
            BakedModel existingModel = event.getModels().get(variantMRL);
            if (existingModel == null) {
                System.out.println("Did not find the expected vanilla baked model(s) for blockCamouflage in registry");
            } else if (existingModel instanceof RopeTiedPostBakedModel) {
                System.out.println("Tried to replace CamouflagedBakedModel twice");
            } else {
                RopeTiedPostBakedModel customModel = new RopeTiedPostBakedModel(existingModel);
                event.getModels().put(variantMRL, customModel);
            }
        }

        ModelResourceLocation itemModelResourceLocation = PintGlassBakedModel.modelResourceLocation;
        BakedModel existingModel = event.getModels().get(itemModelResourceLocation);
        if (existingModel == null) {
            System.out.println("Did not find the expected vanilla baked model for Pouch Model in registry");
        } else if (existingModel instanceof PintGlassBakedModel) {
            System.out.println("Tried to replace vial pouch model twice");
        } else {
            PintGlassBakedModel customModel = new PintGlassBakedModel(existingModel);
            event.getModels().put(itemModelResourceLocation, customModel);
        }

    }

}