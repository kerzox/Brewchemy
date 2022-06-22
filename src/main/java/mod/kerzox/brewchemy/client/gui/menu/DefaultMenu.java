package mod.kerzox.brewchemy.client.gui.menu;

import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class DefaultMenu<T extends BrewchemyBlockEntity> extends AbstractContainerMenu {

    protected final T blockEntity;
    protected final Inventory playerInventory;
    protected final Player player;

    protected DefaultMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory playerInventory, Player player, T blockEntity) {
        super(pMenuType, pContainerId);
        this.blockEntity = blockEntity;
        this.playerInventory = playerInventory;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getPlayerInventory() {
        return playerInventory;
    }

    public T getBlockEntity() {
        return blockEntity;
    }

    public void syncToServer() {
        this.blockEntity.syncBlockEntity();
    }

    // inventory layout



}
