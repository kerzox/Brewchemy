package mod.kerzox.brewchemy.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.components.ProgressComponent;
import mod.kerzox.brewchemy.client.gui.components.StateChangeComponent;
import mod.kerzox.brewchemy.client.gui.menu.GerminationChamberMenu;
import mod.kerzox.brewchemy.client.gui.menu.MillstoneMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GerminationScreen extends DefaultScreen<GerminationChamberMenu> {

    private StateChangeComponent<GerminationChamberMenu> sun = new StateChangeComponent<>(this, new ResourceLocation(Brewchemy.MODID, "textures/gui/germination.png"), 53, 50, 18, 18, 53, 50, 176, 2);

    public GerminationScreen(GerminationChamberMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, new ResourceLocation(Brewchemy.MODID, "textures/gui/germination.png"));
    }

    @Override
    protected void init() {
        super.init();
        addWidgetComponent(sun);
    }

    @Override
    protected void containerTick() {
        this.sun.changeState(menu.getUpdateTag().getBoolean("hasSun"));
    }

    @Override
    public void addToBackground(PoseStack stack, float partialTicks, int x, int y) {

    }

    @Override
    public void addToForeground(PoseStack stack, int x, int y) {

    }
}
