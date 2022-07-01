package mod.kerzox.brewchemy.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.components.StateChangeComponent;
import mod.kerzox.brewchemy.client.gui.menu.FermentationBarrelMenu;
import mod.kerzox.brewchemy.client.gui.menu.GerminationChamberMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FermentationScreen extends DefaultScreen<FermentationBarrelMenu> {

    public FermentationScreen(FermentationBarrelMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, new ResourceLocation(Brewchemy.MODID, "textures/gui/wooden_barrel.png"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void containerTick() {

    }

    @Override
    public void addToBackground(PoseStack stack, float partialTicks, int x, int y) {

    }

    @Override
    public void addToForeground(PoseStack stack, int x, int y) {

    }
}
