package mod.kerzox.brewchemy.common.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class ConnectedModelBlock extends BrewchemyDirectionalBlock{

    public enum StraightLineFormation implements StringRepresentable {
        SINGLE("single"),
        RIGHT_END("right_end"),
        LEFT_END("left_end"),
        MIDDLE("middle");

        private final String name;

        private StraightLineFormation(String p_61311_) {
            this.name = p_61311_;
        }

        public String getSerializedName() {
            return this.name;
        }
    }


    private String blockStateType;

    public static EnumProperty<StraightLineFormation> STRAIGHT_LINE = EnumProperty.create("part", StraightLineFormation.class);

    public ConnectedModelBlock(Properties p_49795_, String type) {
        super(p_49795_);
        this.blockStateType = type;
    }

    @Override
    public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
        switch (blockStateType) {
            case "straight": {
                return getStraightFormation(p_60541_, p_60544_, p_60545_);
            }
            default: return p_60541_;
        }
    }

    private BlockState get2x2Formation(BlockState state, LevelAccessor level, BlockPos pos) {
        return state;
    }

    private @NotNull BlockState getStraightFormation(BlockState state, LevelAccessor level, BlockPos pos) {
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);

        BlockPos rightPos = facing == Direction.EAST ? pos.south() : pos.north();
        BlockPos leftPos = facing == Direction.EAST ? pos.north() : pos.south();

        if (facing.getAxis() == Direction.Axis.Z) {
            rightPos = facing == Direction.SOUTH ? pos.west() : pos.east();
            leftPos = facing == Direction.SOUTH ? pos.east() : pos.west();
        }

        BlockState right = level.getBlockState(rightPos);
        BlockState left = level.getBlockState(leftPos);

        if (right.getBlock() instanceof ConnectedModelBlock && left.getBlock() instanceof ConnectedModelBlock) {
            if (right.getValue(HorizontalDirectionalBlock.FACING).getAxis() == facing.getAxis() && left.getValue(HorizontalDirectionalBlock.FACING).getAxis() == facing.getAxis() && isValidConnection(right) && isValidConnection(left)) {
                return state.setValue(STRAIGHT_LINE, StraightLineFormation.MIDDLE);
            }
        }
        if (right.getBlock() instanceof ConnectedModelBlock) {
            if (right.getValue(HorizontalDirectionalBlock.FACING).getAxis() == facing.getAxis() && isValidConnection(right)) {
                return state.setValue(STRAIGHT_LINE, StraightLineFormation.RIGHT_END);
            }
        }
        if (left.getBlock() instanceof ConnectedModelBlock) {
            // this is a middle piece
            if (left.getValue(HorizontalDirectionalBlock.FACING).getAxis() == facing.getAxis() && isValidConnection(left)) {
                return state.setValue(STRAIGHT_LINE, StraightLineFormation.LEFT_END);
            }
        }
        return state.setValue(STRAIGHT_LINE, StraightLineFormation.SINGLE);
    }

    protected boolean isValidConnection(BlockState state) {
        return true;
    }

    public boolean isStraightLineFormation() {
        return blockStateType.equals("straight");
    }
}
