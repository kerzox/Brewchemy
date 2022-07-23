package mod.kerzox.brewchemy.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.components.MouseOverComponent;
import mod.kerzox.brewchemy.client.gui.components.ProgressComponent;
import mod.kerzox.brewchemy.client.gui.components.StateChangeComponent;
import mod.kerzox.brewchemy.client.gui.menu.FermentationBarrelMenu;
import mod.kerzox.brewchemy.client.gui.menu.GerminationChamberMenu;
import mod.kerzox.brewchemy.client.util.RenderingUtil;
import mod.kerzox.brewchemy.common.blockentity.KegBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.FermentingUtil;
import mod.kerzox.brewchemy.common.item.PintGlassItem;
import mod.kerzox.brewchemy.common.util.FermentationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class FermentationBarrelScreen extends DefaultScreen<FermentationBarrelMenu> {

    private MouseOverComponent<FermentationBarrelMenu> fluidToolTip = new MouseOverComponent<>(this, 60, 20, 50, 50);

    public FermentationBarrelScreen(FermentationBarrelMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, new ResourceLocation(Brewchemy.MODID, "textures/gui/wooden_barrel.png"));
    }

    @Override
    protected void init() {
        super.init();
        addWidgetComponent(fluidToolTip);
    }

    @Override
    protected void containerTick() {
        getMenu().getUpdateTag();
    }

    private void updateFluidToolTip(FluidStack stack) {
        if (stack.isEmpty()) {
            this.fluidToolTip.setTextToDisplay(Component.literal("Empty"));
        } else this.fluidToolTip.setTextToDisplay(
                Component.literal("Fluid information"),
                stack.getDisplayName(),
                Component.literal(String.format("Pints: %.2f", (float) stack.getAmount() / PintGlassItem.PINT_SIZE)),
                Component.literal("Fermentation Progress: %" + Math.round(((float) FermentationHelper.getFermentationTime(stack) / (float) FermentationHelper.Stages.MATURE.getTime()) * 100)));
    }

    @Override
    public void addToBackground(PoseStack stack, float partialTicks, int x, int y) {
        FluidStack fluid = getMenu().getBlockEntity().getFluidTank().getInputHandler().getFluidInTank(0);
        updateFluidToolTip(fluid);
        if (!fluid.isEmpty()) {
            IClientFluidTypeExtensions clientStuff = IClientFluidTypeExtensions.of(fluid.getFluid());
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(clientStuff.getStillTexture());
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            int color = clientStuff.getTintColor();
            RenderSystem.setShaderColor(RenderingUtil.convertColor(color)[0],
                    RenderingUtil.convertColor(color)[1],
                    RenderingUtil.convertColor(color)[2],
                    1f);
            int amount = 0;

            float percentage = ( (float) fluid.getAmount() / getMenu().getBlockEntity().getFluidTank().getTankCapacity(0) ) * 50;
            if (percentage <= 2) {
                percentage = 2;
            }
            RenderingUtil.drawSpriteGrid(stack,
                    this.getGuiLeft() + 60,
                    this.getGuiTop() + 20,
                    this.getBlitOffset(),
                    sprite.getWidth(),
                    sprite.getHeight(),
                    sprite,
                    3, 3);

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            fill(stack, getGuiLeft() + 108, (int) ((getGuiTop() + 69) - percentage), getGuiLeft() + 60, (getGuiTop() + 19), 0xFF8b8b8b);
            RenderSystem.setShaderTexture(0, this.texture);
            this.blit(stack,this.getGuiLeft() + 59,
                    this.getGuiTop() + 19,
                    176,
                    0,
                    50,
                    50);
        }
       fluid = getMenu().getBlockEntity().getFluidTank().getOutputHandler().getFluidInTank(0);
        if (!fluid.isEmpty()) {
            updateFluidToolTip(fluid);
            IClientFluidTypeExtensions clientStuff = IClientFluidTypeExtensions.of(fluid.getFluid());
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(clientStuff.getStillTexture());
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            int color = clientStuff.getTintColor();
            RenderSystem.setShaderColor(RenderingUtil.convertColor(color)[0],
                    RenderingUtil.convertColor(color)[1],
                    RenderingUtil.convertColor(color)[2],
                    1f);
            int amount = 0;

            float percentage = ( (float) fluid.getAmount() / getMenu().getBlockEntity().getFluidTank().getTankCapacity(0) ) * 50;
            if (percentage <= 2) {
                percentage = 2;
            }
            RenderingUtil.drawSpriteGrid(stack,
                    this.getGuiLeft() + 60,
                    this.getGuiTop() + 20,
                    this.getBlitOffset(),
                    sprite.getWidth(),
                    sprite.getHeight(),
                    sprite,
                    3, 3);

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            fill(stack, getGuiLeft() + 108, (int) ((getGuiTop() + 69) - percentage), getGuiLeft() + 60, (getGuiTop() + 19), 0xFF8b8b8b);
            RenderSystem.setShaderTexture(0, this.texture);
            this.blit(stack,this.getGuiLeft() + 59,
                    this.getGuiTop() + 19,
                    176,
                    0,
                    50,
                    50);
        }
    }



    @Override
    protected void mouseTracked(PoseStack pPoseStack, int pMouseX, int pMouseY) {

    }

    @Override
    public void addToForeground (PoseStack stack,int x, int y){

    }
}