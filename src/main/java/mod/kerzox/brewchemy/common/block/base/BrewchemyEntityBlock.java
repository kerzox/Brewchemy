package mod.kerzox.brewchemy.common.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class BrewchemyEntityBlock<T extends BlockEntity> extends AbstractBrewchemyEntityBlock  {

    protected RegistryObject<BlockEntityType<T>> type;

    public BrewchemyEntityBlock(RegistryObject<BlockEntityType<T>> type, Properties p_49795_) {
        super(p_49795_);
        this.type = type;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return this.type.get().create(blockPos, blockState);
    }
}
