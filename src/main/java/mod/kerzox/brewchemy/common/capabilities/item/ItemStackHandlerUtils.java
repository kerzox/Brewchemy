package mod.kerzox.brewchemy.common.capabilities.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemStackHandlerUtils {

    public static void insertAndModifyStack(IItemHandler insertTo, ItemStack stack) {
        if (stack == null) stack = ItemStack.EMPTY;
        ItemStack ret = ItemHandlerHelper.insertItem(insertTo, stack.copy(), false);
        if (ret.isEmpty()) {
            stack.shrink(stack.getCount());
        } else {
            int extracted = stack.getCount() - ret.getCount();
            stack.shrink(extracted);
        }
    }

    public static void insertAndModifyStack(IItemHandler insertTo, ItemStack stack, int maxInsert) {
        ItemStack ret = ItemHandlerHelper.insertItem(insertTo, stack.copyWithCount(maxInsert), false);
        if (ret.isEmpty()) {
            stack.shrink(maxInsert);
        } else {
            int extracted = ret.getCount() - maxInsert;
            stack.shrink(extracted);
        }
    }


}
