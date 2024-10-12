package mod.kerzox.brewchemy.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * handler for playing sounds
 * Only call methods on the server that are marked safe otherwise they are client only
 */

public class SoundHandler {

    private static HashMap<BlockPos, SoundInstance> SOUNDS_AT_POS = new HashMap<>();

    private Level level;

    public SoundHandler(Level level) {
        this.level = level;
    }

    public static boolean isSoundPlayingAtPos(BlockPos pos) {
        return SOUNDS_AT_POS.get(pos) != null;
    }

    public static void removeSoundAt(BlockPos pos) {
        if (isSoundPlayingAtPos(pos)) {
            Minecraft.getInstance().getSoundManager().stop(SOUNDS_AT_POS.get(pos));
            SOUNDS_AT_POS.remove(pos);
        }
    }

    public void play(SoundInstance instance) {
        BlockPos pos = BlockPos.containing(instance.getX(), instance.getY(), instance.getZ());

        // remove our sound at position if client is not near it
        if (!isClientPlayerInRange(pos)) {
            if (isSoundPlayingAtPos(pos)) {
                removeSoundAt(pos);
                return;
            }
        }
        if (isClientPlayerInRange(pos)) {
            if (!isSoundPlayingAtPos(pos)) {
                SOUNDS_AT_POS.put(pos, instance);
                Minecraft.getInstance().getSoundManager().play(SOUNDS_AT_POS.get(pos));
            }
        }
    }

    public static boolean isClientPlayerInRange(BlockPos pos) {
        Player player = Minecraft.getInstance().player;
        SoundInstance instance = SOUNDS_AT_POS.get(pos);
        double dist = player.position().distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

        if (instance != null) {
            Sound sound = instance.getSound();
            float f = instance.getVolume();
            float f1 = Math.max(f, 1.0F) * (float)sound.getAttenuationDistance();
            SoundSource soundsource = instance.getSource();
            float f2 = calculateVolume(f, soundsource);
            return dist < (double)(f1 * f1);
        }

        return true;

    }


    // copied from Minecraft SoundEngine

    private static float calculateVolume(float p_235258_, SoundSource p_235259_) {
        return Mth.clamp(p_235258_ * getVolume(p_235259_), 0.0F, 1.0F);
    }

    private static float getVolume(@Nullable SoundSource p_120259_) {
        return p_120259_ != null && p_120259_ != SoundSource.MASTER ? Minecraft.getInstance().options.getSoundSourceVolume(p_120259_) : 1.0F;
    }

}
