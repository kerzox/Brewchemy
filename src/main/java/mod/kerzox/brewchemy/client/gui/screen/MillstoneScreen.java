package mod.kerzox.brewchemy.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.gui.components.ProgressComponent;
import mod.kerzox.brewchemy.client.gui.menu.MillstoneMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MillstoneScreen extends DefaultScreen<MillstoneMenu> {

    public MillstoneScreen(MillstoneMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, new ResourceLocation(Brewchemy.MODID, "textures/gui/millstone.png"));
    }

    @Override
    public void addToBackground(PoseStack stack, float partialTicks, int x, int y) {

    }

    @Override
    public void addToForeground(PoseStack stack, int x, int y) {

    }
}
