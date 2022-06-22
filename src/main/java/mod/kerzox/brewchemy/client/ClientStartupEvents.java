package mod.kerzox.brewchemy.client;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.baked.RopeTiedFenceBakedModel;
import mod.kerzox.brewchemy.client.render.MillstoneCrankBlockEntityRenderer;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static mod.kerzox.brewchemy.client.render.MillstoneCrankBlockEntityRenderer.CRANK_MODEL;
import static mod.kerzox.brewchemy.client.render.MillstoneCrankBlockEntityRenderer.CRANK_TEXTURE;

@Mod.EventBusSubscriber(modid = Brewchemy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientStartupEvents {

    public static void init() {
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(CRANK_MODEL);
    }

    @SubscribeEvent
    public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location() == TextureAtlas.LOCATION_BLOCKS) {
//            event.addSprite(CRANK_TEXTURE);
        }
    }

    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event) {
        System.out.println("Model bake");
        for (BlockState blockState : BrewchemyRegistry.Blocks.ROPE_FENCE_BLOCK.get().getStateDefinition().getPossibleStates()) {
            ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
            BakedModel existingModel = event.getModelRegistry().get(variantMRL);
            if (existingModel == null) {
                System.out.println("Did not find the expected vanilla baked model(s) for blockCamouflage in registry");
            } else if (existingModel instanceof RopeTiedFenceBakedModel) {
                System.out.println("Tried to replace CamouflagedBakedModel twice");
            } else {
                RopeTiedFenceBakedModel customModel = new RopeTiedFenceBakedModel(existingModel);
                event.getModelRegistry().put(variantMRL, customModel);
            }
        }
    }


}
