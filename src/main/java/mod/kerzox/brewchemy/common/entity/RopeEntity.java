package mod.kerzox.brewchemy.common.entity;

import mod.kerzox.brewchemy.client.render.entity.RopeEntityRenderer;
import mod.kerzox.brewchemy.common.block.RopeTiedPostBlock;
import mod.kerzox.brewchemy.common.blockentity.RopeTiedPost;
import mod.kerzox.brewchemy.common.item.RopeItem;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RopeEntity extends Entity {

    enum NotifyReason {
        REMOVAL,
        UPDATE,
        INTERSECT
    }

    private AABB boundingBox = null;
    private HashMap<AABB, List<RopeEntity>> intersections = new HashMap<>();
    private static final EntityDataAccessor<Boolean> UPDATE = SynchedEntityData.defineId(RopeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockPos> DATA_FIRST_POSITION = SynchedEntityData.defineId(RopeEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> DATA_SECOND_POSITION = SynchedEntityData.defineId(RopeEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> CAN_SUPPORT = SynchedEntityData.defineId(RopeEntity.class, EntityDataSerializers.BOOLEAN);

    public RopeEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public RopeEntity(Level p_19871_, double x, double y, double z) {
        super(BrewchemyRegistry.Entities.ROPE_ENTITY.get(), p_19871_);
        setPos(x + 0.5f, y + (6 / 16f), z + 0.5f);

    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean mayInteract(Level p_146843_, BlockPos p_146844_) {
        return super.mayInteract(p_146843_, p_146844_);
    }

    @Override
    protected AABB makeBoundingBox() {
        BlockPos[] pos = getPositions();
        double centerX = (double) (pos[0].getX() + pos[1].getX()) / 2;
        double centerZ = (double) (pos[0].getZ() + pos[1].getZ()) / 2;

        if (pos[0].equals(BlockPos.ZERO) && pos[1].equals(BlockPos.ZERO)) {
            return super.makeBoundingBox();
        }


        double minX = Math.min(pos[0].getX(), pos[1].getX());
        double maxX = Math.max(pos[0].getX(), pos[1].getX());

        double minZ = Math.min(pos[0].getZ(), pos[1].getZ());
        double maxZ = Math.max(pos[0].getZ(), pos[1].getZ());

        double minY = Math.min(pos[0].getY(), pos[1].getY());
        double maxY = Math.max(pos[0].getY(), pos[1].getY());

        BlockPos normalized = pos[1].subtract(pos[0]);
        Direction direction = Direction.fromDelta(normalized.getX(), normalized.getY(), normalized.getZ());

        if (direction == null) {
            System.out.println("direction is null");
        }

        double size = (7 / 16f);

        AABB bb = null;

        if (direction.getAxis() == Direction.Axis.Z) {
             bb = new AABB(centerX + size, pos[0].getY() + size, maxZ + 1 - size, centerX + 1 - size, pos[0].getY() + 1 - size, minZ + size);
        } else if (direction.getAxis() == Direction.Axis.X) {
             bb = new AABB(minX + size, pos[0].getY() + size, centerZ + size, maxX + 1 - size, pos[0].getY() + 1 - size, centerZ + 1 - size);
        } else {
             bb = new AABB(centerX + size, minY + size, centerZ + size, centerX + 1 - size, maxY + 1 - size, centerZ + 1 - size);
        }

        boundingBox = bb;

        return bb;

    }

/*    public AABB pog(double p_20385_, double p_20386_, double p_20387_) {
        float $$3 = this.width / 2.0F;
        float $$4 = this.height;
        return new AABB(p_20385_ - (double)$$3, p_20386_, p_20387_ - (double)$$3, p_20385_ + (double)$$3, p_20386_ + (double)$$4, p_20387_ + (double)$$3);
    }*/

    @Override
    public boolean hurt(DamageSource p_19946_, float p_19947_) {
        drop();
        return true;
    }

    @Override
    public void remove(RemovalReason p_146834_) {
        notifyIntersectedRopes(NotifyReason.REMOVAL, false);
        super.remove(p_146834_);
    }

    public void intersectedRopeUpdate(AABB intersectPoint, RopeEntity entityResponsible, boolean notify, NotifyReason reason) {

        System.out.println("we got updated: " + position() + " : " + level().isClientSide);

        // entity responsible is now being removed
        if (reason == NotifyReason.REMOVAL) {
            getIntersections().clear();
            findIntersectingRopes(entityResponsible, null);
        }

        if (reason == NotifyReason.INTERSECT) {
            findIntersectingRopes(null, null);
        }

        if (!level().isClientSide) {
            // check if we are not a structural rope
            if (!canSupport()) {
                // search through all intersecting ropes find first structural rope
                for (List<RopeEntity> entities : getIntersections().values()) {
                    for (RopeEntity ropeEntity : entities) {
                        if (ropeEntity.canSupport()) {
                            // we are valid to survive
                            return;
                        }
                    }
                }
            } else {
                return;
            }

            // get here we are not valid anymore
            drop();
        }


    }

    public void notifyIntersectedRopes(NotifyReason reason, boolean chainNotify) {

        for (AABB aabb : getIntersections().keySet()) {
            for (RopeEntity entityToUpdate : getIntersections().get(aabb)) {
                if (!entityToUpdate.isRemoved()) entityToUpdate.intersectedRopeUpdate(aabb, this, chainNotify, reason);
            }
        }

    }

    public void drop() {
        kill();
        spawnAtLocation(BrewchemyRegistry.Items.ROPE_ITEM.get());
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 positionClicked, InteractionHand hand) {

        Level level = player.getCommandSenderWorld();


        BlockPos position = BlockPos.containing(position().subtract(new
                Vec3(Math.abs(positionClicked.x) > 0.5f ? -positionClicked.x : 0,
                Math.abs(positionClicked.y) > 0.5f ? -positionClicked.y: 0,
                Math.abs(positionClicked.z) > 0.5f ? -positionClicked.z : 0)));

        if (!level.isClientSide) {

            if (hand == InteractionHand.MAIN_HAND) {

                player.sendSystemMessage(Component.literal("Position clicked on rope: "+position.toShortString()));
                player.sendSystemMessage(Component.literal("Is structural: "+(canSupport() ? "Yes" : "No")));

                for (AABB aabb : getIntersections().keySet()) {
                    player.sendSystemMessage(Component.literal("IP: " + aabb + " : " + getIntersections().get(aabb).size()));
                }

                ItemStack stack = player.getMainHandItem();

                Item item = stack.getItem();

                if (item instanceof RopeItem ropeItem) {

                    if (ropeItem.getData(stack).isEmpty()) {
                        // tag is empty which means we want to set first position
                        ropeItem.updatePosition(stack, position);
                        player.sendSystemMessage(Component.literal("First position selected"));

                    } else { // we have the next position to choose

                        BlockPos firstPos = NbtUtils.readBlockPos(ropeItem.getData(stack).getCompound("position"));

                        double dx = Math.abs(firstPos.getX() - position.getX());
                        double dy = Math.abs(firstPos.getY() - position.getY());
                        double dz = Math.abs(firstPos.getZ() - position.getZ());

                        int differentAxes = (dx > 0 ? 1 : 0) + (dy > 0 ? 1 : 0) + (dz > 0 ? 1 : 0);

                        if (differentAxes == 2) {
                            stack.setTag(new CompoundTag());
                            player.sendSystemMessage(Component.literal("Invalid rope selection as rope wasn't straight"));
                            return InteractionResult.SUCCESS;
                        }

                        // create rope entity
                        level.addFreshEntity(new RopeEntity(level, firstPos, position));

                        // shrink stack
                        // stack.shrink(1);

                        // clear the rope tag
                        stack.setTag(null);
                        player.sendSystemMessage(Component.literal("Rope Made"));
                    }
                }
            }


        }

        return super.interact(player, hand);
    }

    public RopeEntity(Level level, BlockPos firstPos, BlockPos secondPos) {
        super(BrewchemyRegistry.Entities.ROPE_ENTITY.get(), level);
        BlockPos normalized = secondPos.subtract(firstPos);
        Direction direction = Direction.fromDelta(normalized.getX(), normalized.getY(), normalized.getZ());

        boundingBox = getBoundingBox();

        double minX = Math.min(firstPos.getX(), secondPos.getX());
        double maxX = Math.max(firstPos.getX(), secondPos.getX());

        double minZ = Math.min(firstPos.getZ(), secondPos.getZ());
        double maxZ = Math.max(firstPos.getZ(), secondPos.getZ());

        double minY = Math.min(firstPos.getZ(), secondPos.getZ());
        double maxY = Math.max(firstPos.getZ(), secondPos.getZ());

        double centerX = (double) (firstPos.getX() + secondPos.getX()) / 2;
        double centerZ = (double) (firstPos.getZ() + secondPos.getZ()) / 2;

        setFirstPosition(firstPos);
        setSecondPosition(secondPos);

        double size = (7 / 16f);

        this.setPos(centerX + 0.5f, firstPos.getY() + 6 / 16f, centerZ + 0.5f);

        // check if we are a structural rope

       if (level.getBlockEntity(firstPos) instanceof RopeTiedPost && level.getBlockEntity(secondPos) instanceof RopeTiedPost) {
            // both end points are rope tied fences which means we can support other ropes
            setCanSupport(true);
        }


    }


    @Override
    public void tick() {
        super.tick();

        if (boundingBox == getBoundingBox()) {
            boundingBox = null;
            findIntersectingRopes(null, null);
        }
    }

    @Override
    protected void tryCheckInsideBlocks() {
        super.tryCheckInsideBlocks();
    }

    @Override
    protected void checkInsideBlocks() {
        super.checkInsideBlocks();
    }

    private void findIntersectingRopes(@Nullable RopeEntity ignore, @Nullable AABB boundingBox) {

        List<RopeEntity> entities = level().getEntitiesOfClass(RopeEntity.class, boundingBox != null ? boundingBox : getBoundingBox());

        for (RopeEntity ropeEntity : entities) {
            if (ropeEntity == this || ignore == ropeEntity) { continue;}
            //TODO add this as a cached value in the entity
            AABB intersection = boundingBox != null ? boundingBox : getBoundingBox().intersect(ropeEntity.getBoundingBox());

            if (intersections.containsKey(intersection)) {
                if (!intersections.get(intersection).contains(ropeEntity)) {
                    intersections.get(intersection).add(ropeEntity);
                    notifyIntersectedRopes(NotifyReason.INTERSECT, false);
                }
            } else {
                intersections.put(intersection, new ArrayList<>());
                intersections.get(intersection).add(ropeEntity);
                notifyIntersectedRopes(NotifyReason.INTERSECT, false);
            }



        }
    }

    public HashMap<AABB, List<RopeEntity>> getIntersections() {
        return intersections;
    }

    @Override
    public boolean canCollideWith(Entity p_20303_) {
        return super.canCollideWith(p_20303_);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public void setFirstPosition(BlockPos pos) {
        this.getEntityData().set(DATA_FIRST_POSITION, pos);
    }

    public void setSecondPosition(BlockPos pos) {
        this.getEntityData().set(DATA_SECOND_POSITION, pos);
    }

    public BlockPos[] getPositions() {
        return new BlockPos[]{
                this.getEntityData().get(DATA_FIRST_POSITION), this.getEntityData().get(DATA_SECOND_POSITION)
        };
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (DATA_FIRST_POSITION.equals(dataAccessor)) {
            this.setFirstPosition(this.entityData.get(DATA_FIRST_POSITION));
        }

        if (DATA_SECOND_POSITION.equals(dataAccessor)) {
            this.setSecondPosition(this.entityData.get(DATA_SECOND_POSITION));
        }

        if (UPDATE.equals(dataAccessor)) {
            this.setUpdate(this.entityData.get(UPDATE));
        }

        if (CAN_SUPPORT.equals(dataAccessor)) {
            this.setCanSupport(this.entityData.get(CAN_SUPPORT));
        }

    }

    public void setCanSupport(boolean value) {
        this.getEntityData().set(CAN_SUPPORT, value);
    }

    public boolean getUpdateState() {
        return this.getEntityData().get(UPDATE);
    }

    public boolean canSupport() {
        return this.getEntityData().get(CAN_SUPPORT);
    }


    public void setUpdate(boolean update) {
        this.getEntityData().set(UPDATE, update);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_FIRST_POSITION, new BlockPos(BlockPos.ZERO));
        this.getEntityData().define(DATA_SECOND_POSITION, new BlockPos(BlockPos.ZERO));
        this.getEntityData().define(UPDATE, true);
        this.getEntityData().define(CAN_SUPPORT, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setFirstPosition(NbtUtils.readBlockPos(tag.getCompound("pos1")));
        setSecondPosition(NbtUtils.readBlockPos(tag.getCompound("pos2")));
        setUpdate(tag.getBoolean("update"));
        setCanSupport(tag.getBoolean("can_support"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("pos1", NbtUtils.writeBlockPos(getPositions()[0]));
        tag.put("pos2", NbtUtils.writeBlockPos(getPositions()[1]));
        tag.putBoolean("update", getUpdateState());
        tag.putBoolean("can_support", canSupport());
    }
}
