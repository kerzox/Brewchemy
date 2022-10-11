package mod.kerzox.brewchemy.common.capabilities.utility;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;

@AutoRegisterCapability
public interface IUtilityItem {
   Capability<?> cycleModes(boolean reverse);
}
