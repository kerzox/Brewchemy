package mod.kerzox.brewchemy.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.render.baked.PintGlassBakedModel;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.common.blockentity.CultureJarBlockEntity;
import mod.kerzox.brewchemy.common.blockentity.PintGlassBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidInventoryItem;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.event.TickUtils;
import mod.kerzox.brewchemy.common.network.RequestDataPacket;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.List;

public class PintGlassBlockEntityRenderer implements BlockEntityRenderer<PintGlassBlockEntity> {

    public static ResourceLocation[] PINT_GLASS = new ResourceLocation[] {
            new ResourceLocation(Brewchemy.MODID, "block/pint_glass_one"),
            new ResourceLocation(Brewchemy.MODID, "block/pint_glass_two"),
            new ResourceLocation(Brewchemy.MODID, "block/pint_glass_three")
    };

    public PintGlassBlockEntityRenderer(BlockEntityRendererProvider.Context p_174008_) {

    }

    @Override
    public void render(PintGlassBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        Direction facing = pBlockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        WrappedPose pose = new WrappedPose(pPoseStack);
        pose.push();

        if (facing == Direction.SOUTH) {
            pose.rotateY(180);
        }

        pBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            if (cap instanceof ItemInventory inventory) {
                List<ItemStack> beers = inventory.getInputHandler().getStacks().stream().filter(p -> !p.isEmpty()).toList();
                if (beers.isEmpty()) RequestDataPacket.get(pBlockEntity.getBlockPos());

                for (int i = 0; i < beers.size(); i++) {
                    ItemStack stack = beers.get(i);
                    IFluidHandlerItem handlerItem = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
                    if (handlerItem instanceof FluidInventoryItem itemFluidHandler) {
                        BakedModel model = new PintGlassBakedModel.PintGlassBakedModelWithFluid(
                                Minecraft.getInstance().getModelManager().getModel(
                                        PINT_GLASS[0]),
                                itemFluidHandler);

                        pose.push();

                        if (beers.size() == 2) {

                            if (i == 0) {
                                pose.rotateY(180F);
                                pose.translate(-4/16f, 0, -3/16f);
                            }

                            if (i == 1) {
                                pose.translate(-4/16f, 0, -3/16f);
                            }

                        }

                        Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                                .tesselateWithAO(Minecraft.getInstance().level, itemFluidHandler.getFluid().isEmpty() ? Minecraft.getInstance().getModelManager().getModel(PINT_GLASS[0]) : model,
                                        pBlockEntity.getBlockState(),
                                        pBlockEntity.getBlockPos(),
                                        pPoseStack, pBufferSource.getBuffer(RenderType.cutout()),
                                        true,
                                        Minecraft.getInstance().level.getRandom(), pBlockEntity.getBlockPos().asLong(), pPackedLight, ModelData.EMPTY,
                                        RenderType.cutout());
                        pose.pop();
                    }
                }


            }
        });
        pose.pop();
    }

/*
                        if (beers.size() == 2) {

                            if (i == 0) {
                                pose.translate(-0.5f, 0, -0.5f);
                                pose.rotateY(90F);
                                pose.translate(-4/16f, 0, 5/16f);
                            }

                            if (i == 1) {
                                pose.translate(8/16f, 0, 10/16f);
                                pose.rotateY(230f);
                                pose.translate(-6/16f + 4/16f, 0, 6/16f + 2/16f);
                            }

                        }

                        if (beers.size() == 3) {
                            if (i == 0) {
                                pose.translate(7/16f, 0, 4.5f/16f);
                                pose.rotateY(325);
                                pose.translate(-6/16f - 1.5f/16f, 0, -6/16f + 1.5f/16f);
                            }

                            if (i == 1) {
                                pose.translate(2.25f/16f, 0, 4.75f/16f);
                                pose.rotateY(45);
                                pose.translate(-2.25f/16f, 0, -4.75f/16f);
                                pose.translate(2/16f, 0, -3/16f);
                            }
                            if (i == 2) {
                                pose.translate(6.25f/16f, 0, 10.5f/16f);
                                pose.rotateY(240);
                                pose.translate(-4f/16f, 0, 7/16f);
                            }

                        }
 */

}
