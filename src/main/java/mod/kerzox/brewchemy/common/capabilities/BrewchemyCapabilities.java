package mod.kerzox.brewchemy.common.capabilities;

import mod.kerzox.brewchemy.common.item.TwineItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BrewchemyCapabilities {

    public static final Capability<TwineItem.PositionSelectionCapability> TWINE_PLACEMENT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});;

    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(TwineItem.PositionSelectionCapability.class);
    }

}
