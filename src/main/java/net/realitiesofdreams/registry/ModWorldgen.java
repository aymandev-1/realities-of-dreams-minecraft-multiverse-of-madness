package net.realitiesofdreams.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.world.gen.Mc001BiomeSource;
import net.realitiesofdreams.world.gen.Mc001ChunkGenerator;

/** World generation types used by data/realitiesofdreams/dimension/mc_001.json. */
public final class ModWorldgen {
    private static final DeferredRegister<MapCodec<? extends ChunkGenerator>> GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, RealitiesOfDreams.MODID);
    private static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, RealitiesOfDreams.MODID);

    static {
        GENERATORS.register("mc001", () -> Mc001ChunkGenerator.CODEC);
        BIOME_SOURCES.register("mc001", () -> Mc001BiomeSource.CODEC);
    }

    private ModWorldgen() {}

    public static void register(IEventBus bus) {
        GENERATORS.register(bus);
        BIOME_SOURCES.register(bus);
    }
}
