package net.realitiesofdreams.world.gen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.realitiesofdreams.story.world.Mc001Terrain;

import java.util.stream.Stream;

/**
 * MC-001's biomes: fixed, authored biomes for Firstbrook and Awakening Valley (so the village always
 * has the same grass, trees and weather whatever the world seed), and the normal overworld biomes
 * beyond them.
 */
public class Mc001BiomeSource extends BiomeSource {
    public static final MapCodec<Mc001BiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiomeSource.CODEC.fieldOf("wilds").forGetter(s -> s.wilds),
            Biome.CODEC.fieldOf("village").forGetter(s -> s.village),
            Biome.CODEC.fieldOf("hills").forGetter(s -> s.hills),
            Biome.CODEC.fieldOf("mountains").forGetter(s -> s.mountains),
            Biome.CODEC.fieldOf("woodland").forGetter(s -> s.woodland),
            Biome.CODEC.fieldOf("valley").forGetter(s -> s.valley),
            com.mojang.serialization.Codec.unboundedMap(com.mojang.serialization.Codec.STRING, Biome.CODEC)
                    .optionalFieldOf("regions", java.util.Map.of()).forGetter(s -> s.regions)
    ).apply(i, Mc001BiomeSource::new));

    private final BiomeSource wilds;
    private final Holder<Biome> village, hills, mountains, woodland, valley;
    /** The far capitals' biomes, by region id (Mc001Terrain.REGIONS); missing ones fall back to the wilds. */
    private final java.util.Map<String, Holder<Biome>> regions;

    public Mc001BiomeSource(BiomeSource wilds, Holder<Biome> village, Holder<Biome> hills, Holder<Biome> mountains,
                            Holder<Biome> woodland, Holder<Biome> valley, java.util.Map<String, Holder<Biome>> regions) {
        this.regions = regions;
        this.wilds = wilds;
        this.village = village;
        this.hills = hills;
        this.mountains = mountains;
        this.woodland = woodland;
        this.valley = valley;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(wilds.possibleBiomes().stream(), Stream.concat(Stream.of(village, hills, mountains, woodland, valley), regions.values().stream())).distinct();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int qx, int qy, int qz, Climate.Sampler sampler) {
        int x = QuartPos.toBlock(qx) - Mc001Terrain.ANCHOR_X, z = QuartPos.toBlock(qz) - Mc001Terrain.ANCHOR_Z;
        for (Mc001Terrain.Region r : Mc001Terrain.REGIONS) {
            Holder<Biome> b = regions.get(r.id());
            if (b != null && r.distance(x, z) <= r.radius() + r.blend() / 2.0) return b;
        }
        double d = Math.hypot(x, z);
        if (d > Mc001Terrain.AUTHORED_RADIUS + 20) return wilds.getNoiseBiome(qx, qy, qz, sampler);
        if (z > Mc001Terrain.HILLS_START_Z && Math.abs(x) <= 60 && z <= 64) return village;
        if (z <= -150) return mountains;
        if (z <= Mc001Terrain.HILLS_START_Z) return hills;
        if (x > 40 && z < 100) return woodland;
        return valley;
    }
}
