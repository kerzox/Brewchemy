package mod.kerzox.brewchemy.common.capabilities.energy;

import mod.kerzox.brewchemy.common.capabilities.CapabilityHolder;
import mod.kerzox.brewchemy.common.capabilities.ICapabilitySerializer;
import mod.kerzox.brewchemy.common.capabilities.IStrictInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashSet;

/**
 * Energy Inventory
 * Has an internal wrapper for input but only one thing for capacity
 */

public class EnergyInventory extends EnergyStorage implements IStrictInventory<EnergyInventory>, ICapabilitySerializer, CapabilityHolder<EnergyInventory> {

    private HashSet<Direction> inputSides = new HashSet<>();
    private HashSet<Direction> outputSides = new HashSet<>();

    private InternalWrapper inputWrapper = new InternalWrapper(this, true);
    private InternalWrapper outputWrapper = new InternalWrapper(this, false);

    private LazyOptional<EnergyInventory> combined = LazyOptional.of(() -> this);
    private LazyOptional<InternalWrapper> input = LazyOptional.of(() -> inputWrapper);
    private LazyOptional<InternalWrapper> output = LazyOptional.of(() -> outputWrapper);

    private EnergyInventory(int capacity) {
        super(capacity);
    }

    private EnergyInventory(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    private EnergyInventory(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    private EnergyInventory(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public static EnergyInventory of(int capacity) {
        return new EnergyInventory(capacity);
    }

    public static EnergyInventory of(int capacity, int maxTransfer) {
        return new EnergyInventory(capacity, maxTransfer);
    }

    public static EnergyInventory of(int capacity, int maxReceive, int maxExtract) {
        return new EnergyInventory(capacity, maxReceive, maxExtract);
    }

    public static EnergyInventory of(int capacity, int maxReceive, int maxExtract, int energy) {
        return new EnergyInventory(capacity, maxReceive, maxExtract, energy);
    }

    public void consumeEnergy(int amount) {
        energy -= Math.min(amount, energy);
    }

    public void addEnergy(int amount) {
        this.energy += Math.min(capacity - energy, amount);
    }

    public boolean hasEnough(int amount) {
        return getEnergyStored() >= amount;
    }

    @Override
    public HashSet<Direction> getInputs() {
        return this.inputSides;
    }

    @Override
    public HashSet<Direction> getOutputs() {
        return this.outputSides;
    }

    @Override
    public EnergyInventory getInstance() {
        return this;
    }

    @Override
    public Capability<?> getType() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public LazyOptional<EnergyInventory> getCapabilityHandler(Direction side) {
        // return combined
        if (side == null) return combined.cast();
        else if (getInputs().contains(side) && getOutputs().contains(side)) return combined.cast();
            // return only input
        else if (getInputs().contains(side)) return input.cast();
            // return only output
        else if (getOutputs().contains(side)) return output.cast();
        // return empty
        return LazyOptional.empty();
    }

    @Override
    public void invalidate() {

    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", this.energy);
        tag.put("io", serializeInputAndOutput());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.energy = tag.getInt("energy");
        deserializeInputAndOutput(tag.getCompound("io"));
    }

    public static class InternalWrapper implements IEnergyStorage {

        private EnergyInventory owner;
        private boolean input;

        public InternalWrapper(EnergyInventory owner, boolean input) {
            this.owner = owner;
            this.input = input;
        }

        public int internalReceiveEnergy(int maxReceive, boolean simulate) {
            return this.owner.receiveEnergy(maxReceive, simulate);
        }

        public int internalExtractEnergy(int maxExtract, boolean simulate) {
            return this.owner.extractEnergy(maxExtract, simulate);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return this.input ? this.owner.receiveEnergy(maxReceive, simulate) : 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return !this.input ? this.owner.extractEnergy(maxExtract, simulate) : 0;
        }

        @Override
        public int getEnergyStored() {
            return this.owner.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return this.owner.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return this.owner.canExtract();
        }

        @Override
        public boolean canReceive() {
            return this.canReceive();
        }
    }

}
