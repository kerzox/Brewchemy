package mod.kerzox.brewchemy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.baked.PintGlassBakedModel;
import mod.kerzox.brewchemy.client.baked.RopeTiedFenceBakedModel;
import mod.kerzox.brewchemy.client.overlay.WarehouseOverlay;
import mod.kerzox.brewchemy.client.render.MillstoneCrankBlockEntityRenderer;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static mod.kerzox.brewchemy.client.overlay.WarehouseOverlay.WAREHOUSE_STOCK;
import static mod.kerzox.brewchemy.client.render.MillstoneCrankBlockEntityRenderer.CRANK_MODEL;

@Mod.EventBusSubscriber(modid = Brewchemy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientStartupEvents {

    public static void init() {
    }

    @SubscribeEvent
    public static void onColorsEventItem(RegisterColorHandlersEvent.Item event) {
        event.register(new DynamicFluidContainerModel.Colors(), BrewchemyRegistry.Fluids.WORT.getBucket().get());
        event.register(new DynamicFluidContainerModel.Colors(), BrewchemyRegistry.Fluids.BEER.getBucket().get());
    }

    @SubscribeEvent
    public static void onModelRegister(ModelEvent.RegisterAdditional event) {
        event.register(CRANK_MODEL);
    }

    @SubscribeEvent
    public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location() == TextureAtlas.LOCATION_BLOCKS) {
//            event.addSprite(CRANK_TEXTURE);
        }
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("warehouse_stock_view", WAREHOUSE_STOCK);
    }

    @SubscribeEvent
    public static void onModelBakeEvent(BakingCompleted event) {
        System.out.println("Model bake");
        for (BlockState blockState : BrewchemyRegistry.Blocks.ROPE_FENCE_BLOCK.get().getStateDefinition().getPossibleStates()) {
            ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
            BakedModel existingModel = event.getModels().get(variantMRL);
            if (existingModel == null) {
                System.out.println("Did not find the expected vanilla baked model(s) for blockCamouflage in registry");
            } else if (existingModel instanceof RopeTiedFenceBakedModel) {
                System.out.println("Tried to replace CamouflagedBakedModel twice");
            } else {
                RopeTiedFenceBakedModel customModel = new RopeTiedFenceBakedModel(existingModel);
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
