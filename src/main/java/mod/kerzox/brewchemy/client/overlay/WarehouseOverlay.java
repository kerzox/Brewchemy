package mod.kerzox.brewchemy.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.brewchemy.common.blockentity.warehouse.WarehouseStorageBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarehouseOverlay implements IGuiOverlay {

    public static WarehouseOverlay WAREHOUSE_STOCK = new WarehouseOverlay();

    public int hoverTicks = 0;
    public BlockPos lastHovered = null;

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        HitResult objectMouseOver = mc.hitResult;
        if (!(objectMouseOver instanceof BlockHitResult)) {
            lastHovered = null;
            hoverTicks = 0;
            return;
        }

        BlockHitResult result = (BlockHitResult) objectMouseOver;
        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        BlockEntity te = world.getBlockEntity(pos);

        int prevHoverTicks = hoverTicks;
        if (lastHovered == null || lastHovered.equals(pos))
            hoverTicks++;
        else
            hoverTicks = 0;
        lastHovered = pos;

        List<Component> tooltip = new ArrayList<>();

        if (te instanceof WarehouseStorageBlockEntity storage) {
            WarehouseSlot slot = storage.getSlot();
            if (slot != null) {
                if (!slot.isEmpty()) {
                    Gui.drawCenteredString(poseStack, mc.font, slot.getFullWarehouseItem().getItem().getDescription(), screenWidth / 2, screenHeight - 45, FastColor.ARGB32.color(255, 255, 255, 255));
                    Gui.drawCenteredString(poseStack, mc.font, "Stored: " + slot.getAmountInStorage(), screenWidth / 2, screenHeight - 35, FastColor.ARGB32.color(255, 255, 255, 255));
                }
            }
        }
    }
}
