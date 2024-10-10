package mod.kerzox.brewchemy.common.event;

import mod.kerzox.brewchemy.common.capabilities.drunk.IntoxicationManager;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidInventoryItem;
import mod.kerzox.brewchemy.common.fluid.alcohol.AgeableAlcoholStack;
import mod.kerzox.brewchemy.common.fluid.alcohol.AlcoholicFluid;
import mod.kerzox.brewchemy.common.item.PintItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import static mod.kerzox.brewchemy.Brewchemy.MODID;

public class CommonEvents {

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            event.getOriginal().getCapability(IntoxicationManager.INTOXICATION_CAPABILITY).ifPresent(intoxicationManager ->
                    event.getEntity().getCapability(IntoxicationManager.INTOXICATION_CAPABILITY).ifPresent(nIM -> nIM.deserializeNBT(intoxicationManager.serializeNBT())));
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public void onCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(new ResourceLocation(MODID, "intoxication"), new IntoxicationManager(player));
        }
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent.Tick event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = event.getItem();


                stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
                    if (handler instanceof FluidInventoryItem fluidInventoryItem) {
                        if (fluidInventoryItem.getFluid().isEmpty()) return;
                        int drinkingRate = Math.min(fluidInventoryItem.getFluid().getAmount(), PintItem.DRINKING_RATE);
                        fluidInventoryItem.drain(drinkingRate, IFluidHandler.FluidAction.EXECUTE);
                        if (fluidInventoryItem.getFluid().getFluid().getFluidType() instanceof AlcoholicFluid) {
                            double content = IntoxicationManager.calculateContentFromFluid(fluidInventoryItem.getTankCapacity(0), new AgeableAlcoholStack(
                                    new FluidStack(fluidInventoryItem.getFluid(), drinkingRate))
                            );
                            if (!player.level().isClientSide) {
                                IntoxicationManager.applyAlcoholToPlayer(player, content);
                            }
                        }
                    };
                });


        }

    }

}
