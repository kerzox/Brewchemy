package mod.kerzox.brewchemy.client.ui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.client.ui.screen.base.ICustomScreen;
import mod.kerzox.brewchemy.common.network.FluidTankClick;
import mod.kerzox.brewchemy.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TankComponent extends TexturedWidgetComponent {

    protected TextureAtlasSprite sprite;
    protected float percentage;
    protected int color;
    protected IFluidHandler handler;
    protected boolean flash = false;
    protected int index = 0;

    protected int u1, v1, u2, v2;

    public TankComponent(ICustomScreen screen, ResourceLocation texture, IFluidHandler tank, int x, int y, int width, int height, int u1, int v1, int u2, int v2, Component component) {
        super(screen, x, y, width, height, u1, v1, texture, component);
        setTextureOffset(u1, v1);
        this.handler = tank;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
    }

    public TankComponent(ICustomScreen screen, ResourceLocation texture, IFluidHandler handler, int index, int x, int y, int width, int height, int u1, int v1, int u2, int v2) {
        super(screen, x, y, width, height, u1, v1, texture, Component.translatable(handler.getFluidInTank(index).getTranslationKey()));
        setTextureOffset(u1, v1);
        this.handler = handler;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.index = index;
        this.v2 = v2;
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return true;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        PacketHandler.sendToServer(new FluidTankClick(index, button));
    }

    @Override
    public void tick() {
        updateState();
    }

    public void updateState() {
        if (handler != null) {
            FluidStack fluidStack = handler.getFluidInTank(index);
            if (!fluidStack.isEmpty()) {
                setSpriteFromFluidStack(fluidStack);

            }
            update(fluidStack.getAmount(), handler.getTankCapacity(index));
        }
    }

    public IFluidHandler getHandler() {
        return handler;
    }

    public void setSpriteFromFluidStack(FluidStack fluidStack) {
        IClientFluidTypeExtensions clientStuff = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(clientStuff.getStillTexture());
        color = clientStuff.getTintColor();
        setTextureOffset(this.u2, this.v2);
    }

    public void update(int amount, int capacity) {
        percentage = ((float) amount / capacity);
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.pose().pushPose();
        if (sprite != null) {
            drawFluid(graphics, getCorrectX(), getCorrectY(), this.width, height * percentage);
        }
        this.draw(graphics, this.getCorrectX(), this.getCorrectY(), this.width, this.height);
        graphics.pose().popPose();
    }

    //TODO make a horizontal version

    protected void drawFluid(GuiGraphics graphics, int x, int y, float width, float height) {
        RenderSystem.setShaderColor(RenderingUtil.convertColor(color)[0],
                RenderingUtil.convertColor(color)[1],
                RenderingUtil.convertColor(color)[2],
                1f);
        float size = this.height * percentage;
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float fullTileHeight = 16.0f;  // Each tile is 16 pixels high
        float remainingHeight = height;  // Track how much height remains to render

        float u1 = sprite.getU(0);
        float u2 = sprite.getU(16);
        float v1 = sprite.getV(0);
        float v2 = sprite.getV(16);

        float dy = y;

        while (remainingHeight > 0) {
            // Determine the height for this tile (either full 16 pixels or the remainder)
            float tileHeight = Math.min(remainingHeight, fullTileHeight);

            // Calculate the UV coordinates for the current tile
            float tileV2 = v1 + (v2 - v1) * (tileHeight / fullTileHeight);  // Crop the last tile if needed

            // Move the y position up by the tile height (since we are rendering bottom to top)
            dy -= tileHeight;

            // Render the current tile
            bufferbuilder.vertex(matrix4f, (float)x, dy + tileHeight, (float)1).uv(u1, tileV2).endVertex();
            bufferbuilder.vertex(matrix4f, (float)x + width, dy + tileHeight, (float)1).uv(u2, tileV2).endVertex();
            bufferbuilder.vertex(matrix4f, (float)x + width, dy, (float)1).uv(u2, v1).endVertex();
            bufferbuilder.vertex(matrix4f, (float)x, dy, (float)1).uv(u1, v1).endVertex();

            // Subtract the rendered height from the remaining height
            remainingHeight -= tileHeight;
        }

        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    public void onHover(GuiGraphics graphics, int pMouseX, int pMouseY, float partialTicks) {
        if (!getHandler().getFluidInTank(index).isEmpty()) {
            List<Component> components = new ArrayList<>();
            components.add(this.getHandler().getFluidInTank(index).isEmpty() ? Component.literal("Empty") : Component.translatable(this.getHandler().getFluidInTank(index).getTranslationKey()));
            components.add(Component.literal("Fluid Amount:" +
                    (!this.getHandler().getFluidInTank(index).isEmpty() ? String.format("%, .0f",
                            Double.parseDouble(String.valueOf(this.getHandler().getFluidInTank(index).getAmount()))) + " mB" : 0)));
            if(getScreen().getMenu().getCarried().getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                components.add(Component.literal("Left click to empty held item"));
                components.add(Component.literal("Right click to fill held item"));
            }


            graphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);

        }
    }

}
