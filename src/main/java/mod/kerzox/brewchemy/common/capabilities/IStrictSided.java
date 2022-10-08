package mod.kerzox.brewchemy.common.capabilities;

import net.minecraft.core.Direction;

import java.util.Arrays;
import java.util.Set;

public interface IStrictSided {
    Set<Direction> getOutputs();
    Set<Direction> getInputs();

    default void addInput(Direction side) {
        getInputs().add(side);
    }

    default void addInput(Direction... side) {
        getInputs().addAll(Arrays.asList(side));
    }

    default void addOutput(Direction side) {
        getOutputs().add(side);
    }

    default void addOutput(Direction... side) {
        getOutputs().addAll(Arrays.asList(side));
    }

    default void removeInput(Direction side) {
        getInputs().remove(side);
    }

    default void removeOutput(Direction side) {
        getOutputs().remove(side);
    }

    default boolean hasInput(Direction side) {
        return getInputs().contains(side);
    }

    default boolean hasOutput(Direction side) {
        return getOutputs().contains(side);
    }

}
