package mod.kerzox.brewchemy.common.capabilities;

import mod.kerzox.brewchemy.common.capabilities.utility.IUtilityItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class BrewchemyCapabilities {

    public static Capability<IUtilityItem> UTILITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});



}
