package mod.kerzox.brewchemy.common.block.base;

import mod.kerzox.brewchemy.common.block.BenchSeatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

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

        Direction facing = p_60541_.getValue(HorizontalDirectionalBlock.FACING);

        BlockPos rightPos = facing == Direction.EAST ? p_60545_.south() : p_60545_.north();
        BlockPos leftPos = facing == Direction.EAST ? p_60545_.north() : p_60545_.south();

        if (facing.getAxis() == Direction.Axis.Z) {
            rightPos = facing == Direction.SOUTH ? p_60545_.west() : p_60545_.east();
            leftPos = facing == Direction.SOUTH ? p_60545_.east() : p_60545_.west();
        }

        BlockState right = p_60544_.getBlockState(rightPos);
        BlockState left = p_60544_.getBlockState(leftPos);

        if (right.getBlock() instanceof ConnectedModelBlock && left.getBlock() instanceof ConnectedModelBlock) {
            if (right.getValue(HorizontalDirectionalBlock.FACING).getAxis() == facing.getAxis() && left.getValue(HorizontalDirectionalBlock.FACING).getAxis() == facing.getAxis() && isValidConnection(right) && isValidConnection(left)) {
                return p_60541_.setValue(STRAIGHT_LINE, StraightLineFormation.MIDDLE);
            }
        }
        if (right.getBlock() instanceof ConnectedModelBlock) {
            if (right.getValue(HorizontalDirectionalBlock.FACING).getAxis() == facing.getAxis() && isValidConnection(right)) {
                return p_60541_.setValue(STRAIGHT_LINE, StraightLineFormation.RIGHT_END);
            }
        }
        if (left.getBlock() instanceof ConnectedModelBlock) {
            // this is a middle piece
            if (left.getValue(HorizontalDirectionalBlock.FACING).getAxis() == facing.getAxis() && isValidConnection(left)) {
                return p_60541_.setValue(STRAIGHT_LINE, StraightLineFormation.LEFT_END);
            }
        }
        return p_60541_.setValue(STRAIGHT_LINE, StraightLineFormation.SINGLE);
    }

    protected boolean isValidConnection(BlockState state) {
        return true;
    }

    public boolean isStraightLineFormation() {
        return blockStateType.equals("straight");
    }
}
