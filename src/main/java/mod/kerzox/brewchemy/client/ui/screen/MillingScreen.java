package mod.kerzox.brewchemy.client.ui.screen;

import mod.kerzox.brewchemy.client.ui.component.EnergyBarComponent;
import mod.kerzox.brewchemy.client.ui.component.ProgressComponent;
import mod.kerzox.brewchemy.client.ui.menu.MillingMenu;
import mod.kerzox.brewchemy.client.ui.screen.base.DefaultScreen;
import mod.kerzox.brewchemy.common.network.SyncContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;


public class MillingScreen extends DefaultScreen<MillingMenu> {

    private EnergyBarComponent energyBar = EnergyBarComponent.small(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 7, 17, ProgressComponent.Direction.UP);

    public MillingScreen(MillingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "milling.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(energyBar);
    }

    @Override
    protected void menuTick() {
        SyncContainer.handle();
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y, float partialTick) {

    }
}
