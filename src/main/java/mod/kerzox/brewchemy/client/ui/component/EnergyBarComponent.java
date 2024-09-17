package mod.kerzox.brewchemy.client.ui.component;

import mod.kerzox.brewchemy.Brewchemy;
import mod.kerzox.brewchemy.client.ui.screen.base.ICustomScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class EnergyBarComponent extends ProgressComponent {

    private IEnergyStorage storage;

    public EnergyBarComponent(ICustomScreen screen, IEnergyStorage storage, int x, int y, int width, int height, int u1, int v1, int u2, int v2, Direction direction) {
        super(screen, new ResourceLocation(Brewchemy.MODID, "textures/gui/widgets.png"), x, y, width, height, u1, v1, u2, v2, Component.literal("Energy Bar"), direction);
        this.storage = storage;
    }

    public static EnergyBarComponent small(ICustomScreen screen, IEnergyStorage storage, int x, int y, Direction direction) {
        return new EnergyBarComponent(screen, storage, x, y, 6, 54, 0, 0, 6, 0, direction);
    }

    @Override
    public void onInit() {
        if (storage != null) {
            update(storage.getEnergyStored(), storage.getMaxEnergyStored());
        }
    }

    @Override
    public void tick() {
        if (storage != null) {
            update(storage.getEnergyStored(), storage.getMaxEnergyStored());
        }
    }

    @Override
    protected List<Component> getComponents() {
        return List.of(Component.literal("Energy: " + getMinimum() + "/" + getMaximum()));
    }
}
