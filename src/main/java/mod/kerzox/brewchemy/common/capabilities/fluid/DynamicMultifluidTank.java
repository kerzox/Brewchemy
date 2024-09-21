package mod.kerzox.brewchemy.common.capabilities.fluid;

import java.util.stream.IntStream;

/**
 * Changes capacity by current fluid amounts in every tank
 * I.E Two internal tanks have a total capacity of 16000
 * Tank 1 contains 4000 fluid Tank 2 is empty
 * The remaining capacity on either tank is now 16000 - 4000 = 12000
 * Tank 1 contains now 15000 fluid. Tank 1 can take another 1000 or tank 2 can tank the last 1000.
 */

public class DynamicMultifluidTank extends MultifluidTank {

    private int totalCapacity;

    public DynamicMultifluidTank(int tanks, int totalCapacity) {
        super(IntStream.range(0, tanks).mapToObj(i -> new FluidStorage(totalCapacity)));
        this.totalCapacity = totalCapacity;
    }

    public static DynamicMultifluidTank of(int tanks, int capacity) {
        return new DynamicMultifluidTank(tanks, capacity);
    }

    @Override
    public int getTankCapacity(int tank) {
        return getRemainingCapacity(tank);
    }

    public int getRemainingCapacity(int tank) {
        int remaining = totalCapacity;
        for (int i = 0; i < this.getTanks(); i++) {
            if (i == tank) continue;
            remaining -= this.getFluidInTank(i).getAmount();
        }
        return remaining;
    }


    public int getTotalCapacity() {
        return totalCapacity;
    }
}
