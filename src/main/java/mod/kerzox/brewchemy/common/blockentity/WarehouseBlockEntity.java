package mod.kerzox.brewchemy.common.blockentity;

import com.mojang.math.Vector3f;
import mod.kerzox.brewchemy.common.block.WarehouseBlock;
import mod.kerzox.brewchemy.common.blockentity.base.BrewchemyBlockEntity;
import mod.kerzox.brewchemy.common.capabilities.item.ItemStackInventory;
import mod.kerzox.brewchemy.common.capabilities.item.warehouse.WarehouseInventory;
import mod.kerzox.brewchemy.registry.BrewchemyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class WarehouseBlockEntity extends BrewchemyBlockEntity {

    public enum Type implements StringRepresentable {
        INVISIBLE("type_0"),
        VISIBLE("type_1");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name.toLowerCase();
        }
    }

    private Type currentType = Type.VISIBLE;
    private WarehouseInventory warehouseInventory = new WarehouseInventory(1);
    private List<BlockPos> positions = new ArrayList<>();
    private BlockPos position1 = new BlockPos(0, 0, 0);
    private BlockPos position2 = new BlockPos(5, 5, 5);


    public WarehouseBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BrewchemyRegistry.BlockEntities.WAREHOUSE.get(), pWorldPosition, pBlockState);
    }

    public WarehouseBlockEntity.Type getWarehouseType() {
        return currentType;
    }

    public int getStorageSlots() {
        return positions.size();
    }

    public void moveX(int x) {
        this.position2 = new BlockPos(this.position2.getX() + x, this.position2.getY(), this.position2.getZ());
        syncBlockEntity();
    }

    public void moveY(int y) {
        this.position2 = new BlockPos(this.position2.getX(), this.position2.getY() + y, this.position2.getZ());
        syncBlockEntity();
    }

    public void moveZ(int z) {
        this.position2 = new BlockPos(this.position2.getX(), this.position2.getY(), this.position2.getZ() + z);
        syncBlockEntity();
    }

    public List<BlockPos> calculatePositions() {
        List<BlockPos> temp = new ArrayList<>();
        for (int x = position1.getX(); x < position2.getX(); x++) {
            for (int y = position1.getY(); y < position2.getY(); y++) {
                for (int z = position1.getZ(); z < position2.getZ(); z++) {
                    temp.add(new BlockPos(x, y, z));
                }
            }
        }
        syncBlockEntity();
        return temp;
    }

    public List<BlockPos> calculatePositionsAndCache() {
        for (int x = position1.getX(); x < position2.getX(); x++) {
            for (int y = position1.getY(); y < position2.getY(); y++) {
                for (int z = position1.getZ(); z < position2.getZ(); z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        syncBlockEntity();
        return positions;
    }

    public void setPositions(Direction facing, int spacesFromBlock) {
        this.position1.relative(facing, spacesFromBlock);
        this.position2.relative(facing);
        syncBlockEntity();
    }

    private void savePositions(CompoundTag tag) {
        ListTag list = new ListTag();
        for (BlockPos position : positions) {
            list.add(NbtUtils.writeBlockPos(position));
        }
        tag.put("positions", list);
    }

    private void readPositionsFromTag(CompoundTag tag) {
        if (tag.contains("positions")) {
            ListTag list = tag.getList("positions", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                positions.add(NbtUtils.readBlockPos(list.getCompound(i)));
            }
        }
    }

    @Override
    protected void write(CompoundTag pTag) {
        savePositions(pTag);
        pTag.putString("warehouse_type", this.getWarehouseType().getSerializedName());
        pTag.put("item_handler", this.warehouseInventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag pTag) {
        readPositionsFromTag(pTag);
        if (pTag.contains("opposing")) {
            CompoundTag tag = pTag.getCompound("opposing");
            this.position1 = NbtUtils.readBlockPos(tag.getCompound("pos1"));
            this.position2 = NbtUtils.readBlockPos(tag.getCompound("pos2"));
        }
        if (!positions.isEmpty()) warehouseInventory = new WarehouseInventory(getStorageSlots());
        this.currentType = Type.valueOf(pTag.getString("warehouse_type"));
        this.warehouseInventory.deserializeNBT(pTag.getCompound("item_handler"));
    }

    @Override
    public void addToUpdateTag(CompoundTag tag) {
        CompoundTag tag1 = new CompoundTag();
        tag1.put("pos1", NbtUtils.writeBlockPos(position1));
        tag1.put("pos2", NbtUtils.writeBlockPos(position2));
        tag.put("opposing", tag1);
    }
}
