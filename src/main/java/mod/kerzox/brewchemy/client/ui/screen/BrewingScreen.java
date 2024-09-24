package mod.kerzox.brewchemy.client.ui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.render.util.RenderingUtil;
import mod.kerzox.brewchemy.client.render.util.WrappedPose;
import mod.kerzox.brewchemy.client.ui.animation.brewing.BrewingKettleAnimationHandler;
import mod.kerzox.brewchemy.client.ui.component.*;
import mod.kerzox.brewchemy.client.ui.menu.BrewingMenu;
import mod.kerzox.brewchemy.client.ui.screen.base.DefaultScreen;
import mod.kerzox.brewchemy.common.blockentity.BrewingKettleBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.fluid.DynamicMultifluidTank;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidStorage;
import mod.kerzox.brewchemy.common.capabilities.fluid.MultifluidInventory;
import mod.kerzox.brewchemy.common.capabilities.item.ItemInventory;
import mod.kerzox.brewchemy.common.network.BrewingKettleGuiClick;
import mod.kerzox.brewchemy.common.network.CompoundTagPacket;
import mod.kerzox.brewchemy.common.network.PacketHandler;
import mod.kerzox.brewchemy.common.network.SyncContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.joml.Matrix4f;

import java.util.*;


public class BrewingScreen extends DefaultScreen<BrewingMenu> {

    private TankComponent dynamicTank = new TankComponent(this, new ResourceLocation(Brewchemy.MODID, "textures/gui/brewing_kettle2.png"), this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get(), 69, 5, 35, 64, 176, 0, 212, 0, Component.empty()) {

        @Override
        public void updateState() {
            super.updateState();
        }

        @Override
        public void update(int amount, int capacity) {
            super.update(amount, capacity);
        }

        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            graphics.pose().pushPose();

            int dy = getCorrectY() + 64;

            /*
                Draws fluid on top of one another
             */

            for (int i = 0; i < handler.getTanks(); i++) {
                MultifluidInventory.InternalWrapper internalWrapper = (MultifluidInventory.InternalWrapper) ((MultifluidInventory)handler).getHandlerFromSlot(i);
                FluidStack stack = handler.getFluidInTank(i);
                if (stack.isEmpty()) continue;
                float height = this.height * ((float) stack.getAmount() / ((DynamicMultifluidTank)internalWrapper.get()).getTotalCapacity());
                IClientFluidTypeExtensions clientStuff = IClientFluidTypeExtensions.of(stack.getFluid());
                sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(clientStuff.getStillTexture());
                color = clientStuff.getTintColor();
                drawFluid(graphics, getCorrectX(), dy, this.width, height);
                dy -= (int) height;
            }

            this.draw(graphics, this.getCorrectX(), this.getCorrectY(), this.width, this.height);
            graphics.pose().popPose();
        }

        @Override
        public void onClick(double mouseX, double mouseY, int button) {

        }

        @Override
        protected boolean isValidClickButton(int p_93652_) {
            return false;
        }

        @Override
        public void onHover(GuiGraphics graphics, int pMouseX, int pMouseY, float partialTicks) {

        }
    };

    private TexturedWidgetComponent clickableSpace = new TexturedWidgetComponent(this, 69, 5, 35, 64, 0, 212, new ResourceLocation(Brewchemy.MODID, "textures/gui/brewing_kettle2.png"), Component.empty()) {

        @Override
        protected List<Component> getComponents() {

            List<Component> components = new ArrayList<>();

            components.add(Component.literal("Inside pot"));

            IFluidHandler handler = getMenu().getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get();

            for (int i = 0; i < handler.getTanks(); i++) {
                if (!handler.getFluidInTank(i).isEmpty())
                    components.add(Component.translatable(handler.getFluidInTank(i).getTranslationKey()).append(Component.literal(": " + handler.getFluidInTank(i).getAmount())));
            }

            for (ItemStack itemStack : getScreenData().getClientInventory()) {
                if (!itemStack.isEmpty())
                    components.add(Component.translatable(itemStack.getDescriptionId()).append(Component.literal(": " + itemStack.getCount())));
            }

            return components;
        }

        @Override
        protected void drawComponent(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        }

        @Override
        public void onClick(double mouseX, double mouseY, int button) {

            Player player = Minecraft.getInstance().player;
            ItemStack itemInHand = getScreen().getMenu().getCarried();

            PacketHandler.sendToServer(new BrewingKettleGuiClick(itemInHand, button));

        }

        @Override
        protected boolean isValidClickButton(int p_93652_) {
            return true;
        }


    };

    private Stack<ItemStack> toSpawn = new Stack<>();

    public BrewingScreen(BrewingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "brewing_kettle2.png", 176, 182);
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(dynamicTank);
        addRenderableWidget(clickableSpace);
        SyncContainer.onOpen();
    }

    private void saveScreenState() {
        CompoundTag clientTag = getScreenData().saveScreenData();
        getScreenData().setClientTag(clientTag);
        CompoundTag tag1 = new CompoundTag();
        tag1.put("client", clientTag);
        PacketHandler.sendToServer(new CompoundTagPacket(tag1));
    }

    private ItemInventory getServerInventory() {
        return (ItemInventory) getMenu().getBlockEntity().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();
    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {

    }

    protected BrewingKettleBlockEntity.ScreenData getScreenData() {
        return getMenu().getBlockEntity().getScreenData();
    }

    @Override
    protected void menuTick() {
        SyncContainer.handle();

        while (!getScreenData().getToSpawnOnClient().isEmpty()) {
            Pair<Integer, ItemStack> itemToSpawn = getScreenData().getToSpawnOnClient().pop();
            // create an animation object for this newly created item

            if (itemToSpawn.getSecond().isEmpty()) {
                // remove the item
                getScreenData().getClientInventory().set(itemToSpawn.getFirst(), ItemStack.EMPTY);
                getScreenData().getItemStackAnimationStates().get(itemToSpawn.getFirst()).setAnimationState("none");
                getScreenData().getItemStackAnimationStates().get(itemToSpawn.getFirst()).setItemStack(ItemStack.EMPTY);
                saveScreenState();
            } else {
                BrewingKettleAnimationHandler ani = new BrewingKettleAnimationHandler();
                ani.setItemStack(itemToSpawn.getSecond());
                spawnItem(itemToSpawn.getFirst(), itemToSpawn.getSecond(), ani);
            }


        }


    }

    protected void spawnItem(int slot, ItemStack newStack, BrewingKettleAnimationHandler ani) {
        ani.setAnimationState("start");
        getScreenData().getItemStackAnimationStates().set(slot, ani);
        getScreenData().getClientInventory().set(slot, newStack);
        saveScreenState();
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {
        WrappedPose wp = new WrappedPose(graphics.pose());

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y, float partialTick) {
        WrappedPose wp = new WrappedPose(graphics.pose());
        float pivotX = getGuiLeft() + 102;
        float pivotY = getGuiTop() + 68;
        wp.push();
        wp.translate(pivotX, pivotY, 0);
        float heat = getMenu().getBlockEntity().getHeat();
        float rotationAngle = (heat / 500.0f) * 180.0f;
        wp.rotateZ(rotationAngle);
        wp.translate(-5, 0, 0);
        graphics.blit(texture, 0, 0, 176, 64, 5, 1, 256, 256);
        wp.pop();

        for (int i = 0; i < getScreenData().getItemStackAnimationStates().size(); i++) {
            BrewingKettleAnimationHandler handler = getScreenData().getItemStackAnimationStates().get(i);
            ItemStack stack = getScreenData().getClientInventory().get(i);
            if (!stack.isEmpty()) {
                handler.setScreen(this);
                handler.setGraphics(graphics);
                handler.setWrappedPose(wp);
                handler.runAnimation(partialTick);
                saveScreenState();
            }
        }

        clickableSpace.render(graphics, x, y, partialTick);


    }

}
