package mod.kerzox.brewchemy.common.entity;

import mod.kerzox.brewchemy.common.block.BenchSeatBlock;
import mod.kerzox.brewchemy.common.data.SeatHandler;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SeatEntity extends Entity {

    public SeatEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public SeatEntity(Level p_19871_, BlockPos pos) {
        super(BrewchemyRegistry.Entities.SEAT_ENTITY.get(), p_19871_);
        setPos(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
    }

    public InteractionResult interactAt(Player p_19980_, Vec3 p_19981_, InteractionHand p_19982_) {
/*        if (!this.level().isClientSide) {
            p_19980_.startRiding(this);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }*/

        return InteractionResult.PASS;
    }

    @Override
    protected void positionRider(Entity p_19957_, MoveFunction p_19958_) {
        super.positionRider(p_19957_, p_19958_);
    }

    @Override
    public double getPassengersRidingOffset() {
        return 3/16f;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return false;
    }


    @Override
    protected boolean canRide(Entity p_20339_) {
        return super.canRide(p_20339_);
    }

    @Override
    public InteractionResult interact(Player p_19978_, InteractionHand p_19979_) {
        return InteractionResult.PASS;
    }

    @Override
    public void load(CompoundTag p_20259_) {
        super.load(p_20259_);
        SeatHandler.setSeatAtPosition(this.blockPosition(), this);
    }

    @Override
    public void kill() {
        // as a failsafe
        SeatHandler.removeSeat(this.blockPosition());
        super.kill();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean mayInteract(Level p_146843_, BlockPos p_146844_) {
        return super.mayInteract(p_146843_, p_146844_);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }
}
