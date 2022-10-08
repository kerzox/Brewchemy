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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
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
            if (level.getBlockEntity(pos) != null) {
                level.getBlockEntity(pos).getCapability(handler.getCurrent()).ifPresent(blockCapability -> {
                    if (blockCapability instanceof IStrictSided strictSided) {
                        if (player.isShiftKeyDown()) {
                            if (handler.getUseAmount() == 0) {
                                strictSided.removeOutput(clicked.getOpposite());
                                strictSided.addInput(clicked.getOpposite());
                                player.sendSystemMessage(Component.literal("Added input to side: " + clicked.getOpposite()));
                                handler.add();
                            } else if (handler.getUseAmount() == 1) {
                                strictSided.addOutput(clicked.getOpposite());
                                strictSided.removeInput(clicked.getOpposite());
                                player.sendSystemMessage(Component.literal("Added Output to side: " + clicked.getOpposite()));
                                handler.add();
                            } else if (handler.getUseAmount() == 2) {
                                strictSided.addInput(clicked.getOpposite());
                                strictSided.addOutput(clicked.getOpposite());
                                player.sendSystemMessage(Component.literal("Added Input/Output to side: " + clicked.getOpposite()));
                                handler.add();
                            } else {
                                strictSided.removeOutput(clicked.getOpposite());
                                strictSided.removeInput(clicked.getOpposite());
                                player.sendSystemMessage(Component.literal("Removed all connections to side: " + clicked.getOpposite()));
                                handler.add();
                            }
                        } else {
                            if (handler.getUseAmount() == 0) {
                                strictSided.removeOutput(clicked);
                                strictSided.addInput(clicked);
                                player.sendSystemMessage(Component.literal("Added input to side: " + clicked));
                                handler.add();
                            } else if (handler.getUseAmount() == 1) {
                                strictSided.addOutput(clicked);
                                strictSided.removeInput(clicked);
                                player.sendSystemMessage(Component.literal("Added Output to side: " + clicked));
                                handler.add();
                            } else if (handler.getUseAmount() == 2) {
                                strictSided.addInput(clicked);
                                strictSided.addOutput(clicked);
                                player.sendSystemMessage(Component.literal("Added Input/Output to side: " + clicked));
                                handler.add();
                            } else {
                                strictSided.removeOutput(clicked);
                                strictSided.removeInput(clicked);
                                player.sendSystemMessage(Component.literal("Removed all connections to side: " + clicked));
                                handler.add();
                            }
                        }
                    }
                });
            }
        }

        return super.useOn(pContext);
    }


    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new UtilityHandler();
    }
}
