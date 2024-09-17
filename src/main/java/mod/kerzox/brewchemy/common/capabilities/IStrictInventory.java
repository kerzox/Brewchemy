package mod.kerzox.brewchemy.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Interface to add sided input/output to capabilities
 */

public interface IStrictInventory<T> {

    HashSet<Direction> getInputs();
    HashSet<Direction> getOutputs();

    default T addInput(Direction... side) {
        getInputs().addAll(Arrays.asList(side));
        return (T) this;
    }

    default T addOutput(Direction... side) {
        getOutputs().addAll(Arrays.asList(side));
        return (T) this;
    }

    default T removeInputs(Direction... side) {
        Arrays.asList(side).forEach(getInputs()::remove);
        return (T) this;
    }

    default T removeOutputs(Direction... side) {
        Arrays.asList(side).forEach(getOutputs()::remove);
        return (T) this;
    }

    default boolean hasInput(Direction side) {
        return getInputs().contains(side);
    }

    default boolean hasOutput(Direction side) {
        return getOutputs().contains(side);
    }

    default CompoundTag serializeInputAndOutput() {
        CompoundTag tag = new CompoundTag();
        int[] dir = new int[] {-1, -1, -1, -1, -1, -1};
        for (int i = 0; i < Direction.values().length; i++) {
           if (getInputs().contains(Direction.values()[i])) {
                dir[i] = 1;
           }
           if (getOutputs().contains(Direction.values()[i])) {
               if (getInputs().contains(Direction.values()[i])) dir[i] = 2;
               else dir[i] = 0;
           }
        }
        tag.putIntArray("directions", dir);
        return tag;
    }

    default void deserializeInputAndOutput(CompoundTag tag) {
        int[] dir = tag.getIntArray("directions");

        if (dir.length > 0) {
            getInputs().clear();
            getOutputs().clear();
            for (int i = 0; i < dir.length; i++) {
                if (dir[i] == -1) {
                    removeOutputs(Direction.values()[i]);
                    removeInputs(Direction.values()[i]);
                }
                if (dir[i] == 0) addOutput(Direction.values()[i]);
                if (dir[i] == 1) addInput(Direction.values()[i]);
                if (dir[i] == 2) {
                    addOutput(Direction.values()[i]);
                    addInput(Direction.values()[i]);
                }
            }
        }

    }

}
