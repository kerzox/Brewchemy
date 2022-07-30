package mod.kerzox.brewchemy.client;

import com.mojang.blaze3d.vertex.*;
import mod.kerzox.brewchemy.common.blockentity.warehouse.WarehouseBlockEntity;
import mod.kerzox.brewchemy.common.effects.IntoxicatedEffect;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.*;

public class RenderEvent {


    @SubscribeEvent
    public void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() == AFTER_SKY) {
            IntoxicatedEffect.overlay(event.getPoseStack(), event.getRenderTick(), event.getPartialTick());
        } else if (event.getStage() == AFTER_SOLID_BLOCKS) {
            Player player = Minecraft.getInstance().player;
            PoseStack poseStack = event.getPoseStack();
            int renderTick = event.getRenderTick();
            ClientLevel level = Minecraft.getInstance().level;
            float partialTicks = event.getPartialTick();
            if (player != null) {
                // get view position
                Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
                Vec3 projectedView = renderInfo.getPosition();
                HitResult ray = event.getCamera().getEntity().pick(20.0D, 0.0F, false);
                if (ray instanceof BlockHitResult block) {
                    if (player.getMainHandItem().is(BrewchemyRegistry.Blocks.WAREHOUSE_BLOCK.get().asItem())) {
                        // render the overlay
                        Direction facing = player.getDirection();
                        BlockPos relative = block.getBlockPos().relative(player.getDirection()).above();

                        float red = 0;
                        float green = 1f;
                        float blue = 0;

                        for (int x = relative.getX(); x < relative.getX() + 5; x++) {
                            for (int y = relative.getY(); y < relative.getY() + 5; y++) {
                                for (int z = relative.getZ(); z < relative.getZ() + 5; z++) {
                                    BlockPos relativePos = new BlockPos(x, y, z);
                                    if (player.getDirection() == Direction.SOUTH) {
                                        relativePos = relativePos.offset(-4, 0, 0);
                                    } else if (player.getDirection() == Direction.NORTH) {
                                        relativePos = relativePos.offset(0, 0, -4);
                                    } else if (player.getDirection() == Direction.WEST) {
                                        relativePos = relativePos.offset(-4, 0, -4);
                                    }
                                    if (!(level.getBlockState(relativePos).getBlock() instanceof AirBlock)) {
                                        red = 1f;
                                        green = 0f;
                                    }
                                }
                            }
                        }

                        poseStack.pushPose();

                        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                        if (facing == Direction.SOUTH) {
                            poseStack.translate(-4, 0, 0);
                        } else if (facing == Direction.NORTH) {
                            poseStack.translate(0, 0, -4);
                        } else if (facing == Direction.WEST) {
                            poseStack.translate(-4, 0, -4);
                        } else {
                            poseStack.translate(0, 0, 0);
                        }

                        LevelRenderer.renderLineBox(poseStack,
                                Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
                                relative.getX(),
                                relative.getY(),
                                relative.getZ(),
                                relative.getX() + 5,
                                relative.getY() + 5,
                                relative.getZ() + 5, red, green, blue, 1.0F);
                        poseStack.popPose();

                    } else if (level.getBlockEntity(block.getBlockPos()) instanceof WarehouseBlockEntity warehouse) {
                        Direction facing = warehouse.getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite();

                        poseStack.pushPose();
                        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                        if (facing == Direction.SOUTH) {
                            poseStack.translate(warehouse.getBlockPos().getX() - warehouse.getPosition2().getX() + 1, warehouse.getBlockPos().getY(), warehouse.getBlockPos().getZ() + 1);
                        } else if (facing == Direction.NORTH) {
                            poseStack.translate(warehouse.getBlockPos().getX(), warehouse.getBlockPos().getY(), warehouse.getBlockPos().getZ() - warehouse.getPosition2().getZ());
                        } else if (facing == Direction.WEST) {
                            poseStack.translate(warehouse.getBlockPos().getX() - warehouse.getPosition2().getX(), warehouse.getBlockPos().getY(), warehouse.getBlockPos().getZ() - warehouse.getPosition2().getZ() + 1);
                        } else {
                            poseStack.translate(warehouse.getBlockPos().getX() + 1, warehouse.getBlockPos().getY(), warehouse.getBlockPos().getZ());
                        }

                        float red = 0;
                        float green = 1f;
                        float blue = 0;

                        for (BlockPos cachedPosition : warehouse.getCachedPositions()) {
                            if (!(level.getBlockState(cachedPosition).getBlock() instanceof AirBlock)) {
                                red = 1f;
                                green = 0f;
                            }
                        }

                        LevelRenderer.renderLineBox(poseStack,
                                Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
                                warehouse.getPosition1().getX(),
                                warehouse.getPosition1().getY(),
                                warehouse.getPosition1().getZ(),
                                warehouse.getPosition2().getX(),
                                warehouse.getPosition2().getY(),
                                warehouse.getPosition2().getZ(), red, green, blue, 1.0F);
                        poseStack.popPose();
                    }
                }
            }
        }
    }
}
