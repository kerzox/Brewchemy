package mod.kerzox.brewchemy.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.kerzox.brewchemy.Brewchemy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.Brightness;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jline.utils.Log;

import java.util.HashMap;
import java.util.Map;

public class BrewingKettleHeating {

    public static final HashMap<ResourceLocation, BrewingKettleHeating> HEAT_SOURCES = new HashMap<>();

    public static final Codec<BrewingKettleHeating> KETTLE_HEATING_CODEC = RecordCodecBuilder.create(instance -> // Given an instance
            instance.group( // Define the fields within the instance
                    ResourceLocation.CODEC.fieldOf("source").forGetter(BrewingKettleHeating::source),
                    Codec.INT.optionalFieldOf("heat", 0).forGetter(BrewingKettleHeating::heat)
            ).apply(instance, BrewingKettleHeating::new) // Define how to create the object
    );

    private ResourceLocation source;
    private int heat;

    public BrewingKettleHeating(ResourceLocation source, int heat) {
        this.source = source;
        this.heat = heat;
    }

    public ResourceLocation source() {
        return this.source;
    }

    public int heat() {
        return this.heat;
    }

    public static int getHeat(ResourceLocation blockKey) {
        return HEAT_SOURCES.values().stream()
                .filter(brewingKettleHeating -> brewingKettleHeating.source().equals(blockKey))
                .mapToInt(BrewingKettleHeating::heat)
                .findFirst()
                .orElse(0);
    }

    public static int getHeat(Block blockKey) {
        return getHeat(ForgeRegistries.BLOCKS.getKey(blockKey));
    }

    public static class ReloadListener extends SimpleJsonResourceReloadListener {

        public static ReloadListener INSTANCE = new ReloadListener(new GsonBuilder().setPrettyPrinting().setLenient().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create(), "brewchemy/kettle_heating");

        public ReloadListener(Gson gson, String dir) {
            super(gson, dir);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> json, ResourceManager manager, ProfilerFiller profiler) {
            DynamicOps<JsonElement> ops = JsonOps.INSTANCE;
            for (Map.Entry<ResourceLocation, JsonElement> entry : json.entrySet()) {
                ResourceLocation location = entry.getKey();
                BrewingKettleHeating.KETTLE_HEATING_CODEC.parse(ops, entry.getValue())
                        .resultOrPartial(errorMsg -> Log.warn("Could not decode kettle heat source with json id {} - error: {}", location, errorMsg))
                        .ifPresent(s -> getHeatSources().put(location, s));
            }
        }

        public HashMap<ResourceLocation, BrewingKettleHeating> getHeatSources() {
            return BrewingKettleHeating.HEAT_SOURCES;
        }
    }

}
