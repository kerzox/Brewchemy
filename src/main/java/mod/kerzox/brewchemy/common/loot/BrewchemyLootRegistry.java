package mod.kerzox.brewchemy.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.kerzox.brewchemy.common.loot.modifer.GrassDropModifiers;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static mod.kerzox.brewchemy.Brewchemy.MODID;

public class BrewchemyLootRegistry {

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    private static final RegistryObject<Codec<GrassDropModifiers>> GRASS_DROP = GLM.register("grass_drops", GrassDropModifiers.CODEC);

    public static void init(IEventBus bus) {
        GLM.register(bus);
    }

}
