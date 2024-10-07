package mod.kerzox.brewchemy.common.data;

import mod.kerzox.brewchemy.common.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.Map;

public class SeatHandler {

    private static Map<BlockPos, SeatEntity> SEAT_ENTITIES = new HashMap<>();

    private CompoundTag tag = new CompoundTag();

    public static void removeThis(BlockPos pos) {
        SeatEntity seat = SEAT_ENTITIES.get(pos);
        if (seat != null) {
            seat.kill();
            SEAT_ENTITIES.remove(pos);
        }
    }

    public static void addSeatHere(BlockPos pos, Level level) {
        SeatEntity seat = new SeatEntity(level, pos);
        level.addFreshEntity(seat);
        SEAT_ENTITIES.put(pos, seat);
    }

    public static void removeSeat(BlockPos pos) {
        SEAT_ENTITIES.remove(pos);
    }

    public static void setSeatAtPosition(BlockPos pos, SeatEntity seat) {
        SEAT_ENTITIES.put(pos, seat);
    }

    public static SeatEntity getEntityFromPosition(BlockPos pos) {
        return SEAT_ENTITIES.get(pos);
    }

    public static InteractionResult interactWithSeat(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {

            SeatEntity seat = SEAT_ENTITIES.get(pPos);
            if (seat == null) {
                return InteractionResult.PASS;
            }
            pPlayer.startRiding(seat);
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return InteractionResult.PASS;
    }
/*
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag tag1 = new ListTag();
        for (BlockPos pos : SEAT_ENTITIES.keySet()) {
            tag1.add(NbtUtils.writeBlockPos(pos));

        }
        tag.put("seat_positions", tag1);
        return tag;
    }

    private void read(CompoundTag tag) {
        ListTag list = tag.getList("seat_positions", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag2 = list.getCompound(i);

        }
    }

    public SeatHandler create() {
        return new SeatHandler();
    }

    public SeatHandler load(CompoundTag tag) {
        SeatHandler handler = create();
        handler.read(tag);
        return handler;
    }*/
}
