package mod.kerzox.brewchemy.common.item;
import mod.kerzox.brewchemy.common.capabilities.fluid.FluidStorageTank;
import mod.kerzox.brewchemy.common.capabilities.fluid.ItemStackTankFluidCapability;
import mod.kerzox.brewchemy.common.fluid.BrewchemyFluidType;
import mod.kerzox.brewchemy.common.item.base.BrewchemyItem;
import mod.kerzox.brewchemy.common.util.FermentationHelper;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import net.minecraft.world.item.Item.Properties;

public class PintGlassItem extends BrewchemyItem {

    public static final int PINT_SIZE = 500;
    public static final int PINTS_PER_KEG = 200;
    public static final int KEG_VOLUME = PINT_SIZE * PINTS_PER_KEG;

    public static final ModelProperty<FluidStack> FLUID = new ModelProperty<>();

    public PintGlassItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        Optional<IFluidHandlerItem> handler = pPlayer.getItemInHand(pUsedHand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
        if (handler.isPresent()) {
            if (handler.get() instanceof ItemStackTankFluidCapability capability) {
                if (capability.getFluid().isEmpty()) return super.use(pLevel, pPlayer, pUsedHand);
            }
        }
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        Player player = (Player) pLivingEntity;
        Optional<IFluidHandlerItem> handler = pStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
        if (handler.isPresent()) {
            if (handler.get() instanceof ItemStackTankFluidCapability capability) {

                if (FermentationHelper.getFermentationStage(capability.getFluid()) == FermentationHelper.Stages.MATURE) {
                    player.addEffect(new MobEffectInstance(BrewchemyRegistry.Effects.INTOXICATED.get(), 200, 10));
                }

                if (!pLevel.isClientSide) {
                    if (capability.getFluid().isEmpty()) return pStack;
                    // do something
                }

                if (!player.getAbilities().instabuild) {
                    capability.drain(500, IFluidHandler.FluidAction.EXECUTE);
                }

                player.awardStat(Stats.ITEM_USED.get(this));

            }

        }
        return pStack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof ItemStackTankFluidCapability handler) {
                if (!handler.getFluid().isEmpty()) {
                    pTooltipComponents.add(Component.literal("Glass of ").append(Component.translatable(handler.getFluid().getTranslationKey())));
                    if (handler.getFluid().getFluid().getFluidType() instanceof BrewchemyFluidType brewchemyFluidType) {
                        if (brewchemyFluidType.isAlcoholic()) pTooltipComponents.add(Component.literal("Maturity " + FermentationHelper.getFermentationStage(handler.getFluid()).getSerializedName()));
                    } else {
                        pTooltipComponents.add(Component.literal("Non alcoholic"));
                    }
                }
            }
        });
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemStackTankFluidCapability(stack, PINT_SIZE);
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pCategory)) {
            pItems.add(new ItemStack(this));
            for (BrewchemyRegistry.Fluids.makeFluid<?> makeFluid : BrewchemyRegistry.Fluids.FLUID_LIST) {
                if (makeFluid.get() instanceof BrewchemyFluidType type) {
                    if (type.isAlcoholic()) {
                        ItemStack stack1 = new ItemStack(this);
                        stack1.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(cap -> {
                            if (cap instanceof ItemStackTankFluidCapability capability) {
                                FluidStack fluidStack = new FluidStack(makeFluid.getFluid().get(), PINT_SIZE);
                                FermentationHelper.ageFluidStack(fluidStack, FermentationHelper.Stages.MATURE.getTime());
                                capability.setFluid(fluidStack);
                                pItems.add(stack1);
                            }
                        });
                    }
                }
            }
        }
    }
}
