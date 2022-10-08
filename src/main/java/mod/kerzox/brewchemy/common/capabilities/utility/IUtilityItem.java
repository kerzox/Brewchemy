package mod.kerzox.brewchemy.common.capabilities.utility;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IUtilityItem {
   int getUseAmount();
   void add();
   void subtract();
   void set(int amount);
}
