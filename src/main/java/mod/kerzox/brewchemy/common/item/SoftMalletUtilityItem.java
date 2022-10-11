package mod.kerzox.brewchemy.common.item;

import mod.kerzox.brewchemy.common.capabilities.IStrictSided;
import mod.kerzox.brewchemy.common.capabilities.utility.IUtilityItem;
import mod.kerzox.brewchemy.common.capabilities.utility.UtilityHandler;
import mod.kerzox.brewchemy.common.item.base.BrewchemyItem;
import mod.kerzox.brewchemy.common.util.IUtilityInteractable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static mod.kerzox.brewchemy.common.capabilities.BrewchemyCapabilities.UTILITY_CAPABILITY;

public class SoftMalletUtilityItem extends BrewchemyItem {

    public SoftMalletUtilityItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {

        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();

        Direction clicked = pContext.getClickedFace();

        Optional<IUtilityItem> capability = pContext.getItemInHand().getCapability(UTILITY_CAPABILITY).resolve();

        if (level.isClientSide) return super.useOn(pContext);

        if (capability.isPresent()) {
            UtilityHandler handler = (UtilityHandler) capability.get();
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                be.getCapability(handler.getCurrent()).ifPresent(blockCapability -> {
                    if (blockCapability instanceof IStrictSided strictSided) {
                        if (player.isShiftKeyDown()) {
                            Direction opp = clicked.getOpposite();
                            if (strictSided.hasOutput(opp) && strictSided.hasInput(opp)) {
                                strictSided.removeInput(opp);
                                strictSided.removeOutput(opp);
                                player.sendSystemMessage(Component.literal("Removed all connections to side: " + opp));
                            } else if (strictSided.hasInput(opp)) {
                                strictSided.addOutput(opp);
                                strictSided.removeInput(opp);
                                player.sendSystemMessage(Component.literal("Added Output to side: " + opp));
                            } else if (strictSided.hasOutput(opp)) {
                                strictSided.addInput(opp);
                                strictSided.addOutput(opp);
                                player.sendSystemMessage(Component.literal("Added Input/Output to side: " + opp));
                            } else {
                                strictSided.addInput(opp);
                                strictSided.removeOutput(opp);
                                player.sendSystemMessage(Component.literal("Added input to side: " + opp));
                            }
                        } else {
                            if (strictSided.hasOutput(clicked) && strictSided.hasInput(clicked)) {
                                strictSided.removeInput(clicked);
                                strictSided.removeOutput(clicked);
                                player.sendSystemMessage(Component.literal("Removed all connections to side: " + clicked));
                            } else if (strictSided.hasInput(clicked)) {
                                strictSided.addOutput(clicked);
                                strictSided.removeInput(clicked);
                                player.sendSystemMessage(Component.literal("Added Output to side: " + clicked));
                            } else if (strictSided.hasOutput(clicked)) {
                                strictSided.addInput(clicked);
                                strictSided.addOutput(clicked);
                                player.sendSystemMessage(Component.literal("Added Input/Output to side: " + clicked));
                            } else {
                                strictSided.addInput(clicked);
                                strictSided.removeOutput(clicked);
                                player.sendSystemMessage(Component.literal("Added input to side: " + clicked));
                            }
                        }
                        if (be instanceof IUtilityInteractable utilityInteractable) {
                            utilityInteractable.update();
                        }
                    }
                });
            }
        }

        return super.useOn(pContext);
    }


    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return UtilityHandler.of(ForgeCapabilities.FLUID_HANDLER, ForgeCapabilities.ITEM_HANDLER);
    }
}
