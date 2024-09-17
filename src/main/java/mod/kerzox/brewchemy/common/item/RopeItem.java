package mod.kerzox.brewchemy.common.item;

import mod.kerzox.brewchemy.common.block.RopeTiedPostBlock;
import mod.kerzox.brewchemy.common.blockentity.RopeTiedPostBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.rope.IRopeConnectable;
import mod.kerzox.brewchemy.common.entity.RopeEntity;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RopeItem extends Item {

    public static final String ROPE_NBT_TAG = "rope_nbt_tag";

    public RopeItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Block block = pContext.getLevel().getBlockState(pContext.getClickedPos()).getBlock();
        ItemStack stack = pContext.getItemInHand();



        if (!pContext.getLevel().isClientSide) {

            if (pContext.getPlayer().isShiftKeyDown()) {
                if (!getData(stack).isEmpty()) {

                    BlockPos firstPos = NbtUtils.readBlockPos(getData(stack).getCompound("position"));

                    if (pContext.getLevel().getBlockEntity(firstPos) instanceof RopeTiedPostBlockEntity rope) {
                        pContext.getLevel().setBlockAndUpdate(firstPos, rope.getMimicState());
                    }


                }

                stack.setTag(null);
                pContext.getPlayer().sendSystemMessage(Component.literal("Rope selection cleared"));
                return InteractionResult.SUCCESS;
            }

            if (block instanceof FenceBlock fence) {
                if (block instanceof RopeTiedPostBlock ropeTiedFenceBlock){
                    return super.useOn(pContext);
                }
                BlockState oldState = pContext.getLevel().getBlockState(pContext.getClickedPos());
                BlockPos pos = pContext.getClickedPos();
                //
                pContext.getLevel().setBlockAndUpdate(pContext.getClickedPos(), BrewchemyRegistry.Blocks.ROPE_TIED_POST_BLOCK.get().defaultBlockState());
                if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof RopeTiedPostBlockEntity rope) {
                    rope.setFenceToMimic(oldState);

                    if (getData(stack).isEmpty()) {
                        // tag is empty which means we want to set first position
                        updatePosition(stack, pContext.getClickedPos());
                        pContext.getPlayer().sendSystemMessage(Component.literal("First position selected"));

                    } else { // we have the next position to choose

                        BlockPos firstPos = NbtUtils.readBlockPos(getData(stack).getCompound("position"));
                        // create rope entity
                        pContext.getLevel().addFreshEntity(new RopeEntity(pContext.getLevel(), firstPos, pContext.getClickedPos()));

                        // shrink stack
                        stack.shrink(1);

                        // clear the rope tag
                        stack.setTag(null);
                        pContext.getPlayer().sendSystemMessage(Component.literal("Rope Made"));
                    }

                    return InteractionResult.SUCCESS;
                }
            }

            if (block instanceof FarmBlock) {
                if (getData(stack).isEmpty()) {
                    // tag is empty which means we want to set first position
                    updatePosition(stack, pContext.getClickedPos());
                    pContext.getPlayer().sendSystemMessage(Component.literal("First position selected"));

                } else { // we have the next position to choose

                    BlockPos firstPos = NbtUtils.readBlockPos(getData(stack).getCompound("position"));

                    double dx = Math.abs(firstPos.getX() - pContext.getClickedPos().getX());
                    double dy = Math.abs(firstPos.getY() - pContext.getClickedPos().getY());
                    double dz = Math.abs(firstPos.getZ() - pContext.getClickedPos().getZ());

                    int differentAxes = (dx > 0 ? 1 : 0) + (dy > 0 ? 1 : 0) + (dz > 0 ? 1 : 0);

                    if (differentAxes == 2) {
                        stack.setTag(new CompoundTag());
                        pContext.getPlayer().sendSystemMessage(Component.literal("Invalid rope selection as rope wasn't straight"));
                        return InteractionResult.SUCCESS;
                    }

                    // create rope entity
                    pContext.getLevel().addFreshEntity(new RopeEntity(pContext.getLevel(), firstPos, pContext.getClickedPos()));

                    // shrink stack
                    stack.shrink(1);

                    // clear the rope tag
                    stack.setTag(null);
                    pContext.getPlayer().sendSystemMessage(Component.literal("Rope Made"));
                }

                return InteractionResult.SUCCESS;
            }

            if (block instanceof IRopeConnectable) {



            }

        }
        return super.useOn(pContext);
    }

    public CompoundTag getData(ItemStack stack) {
        if (!stack.hasTag()) return new CompoundTag();
        if (stack.getTag().contains(ROPE_NBT_TAG))
        {
            return stack.getOrCreateTag().getCompound(ROPE_NBT_TAG);
        }
        return new CompoundTag();
    }

    public void updatePosition(ItemStack stack, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.put("position", NbtUtils.writeBlockPos(pos));
        stack.getOrCreateTag().put(ROPE_NBT_TAG, tag);
    }

}
