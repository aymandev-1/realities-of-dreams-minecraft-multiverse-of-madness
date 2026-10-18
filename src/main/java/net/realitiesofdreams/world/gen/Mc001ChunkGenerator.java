package net.realitiesofdreams.world.gen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.realitiesofdreams.story.world.Mc001Terrain;

import java.util.concurrent.CompletableFuture;

/**
 * MC-001's terrain: ordinary Minecraft terrain (caves, ores, biomes, mountains, rivers) reshaped to
 * the authored layout around Firstbrook ({@link Mc001Terrain}) and blended back into the natural
 * landscape further out. The reshaping happens while chunks generate, off the main thread.
 *
 * Firstbrook's core stays solid, with no caves, lava or wild trees, so the village always stands on
 * firm ground. Vanilla structures (villages, outposts) are not generated in MC-001: its settlements
 * are the authored ones.
 */
public class Mc001ChunkGenerator extends NoiseBasedChunkGenerator {
    public static final MapCodec<Mc001ChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings)
    ).apply(i, Mc001ChunkGenerator::new));

    public Mc001ChunkGenerator(BiomeSource biomes, Holder<NoiseGeneratorSettings> settings) {
        super(biomes, settings);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState random, StructureManager structures, ChunkAccess chunk) {
        return super.fillFromNoise(blender, random, structures, chunk).thenApply(c -> {
            shape(c);
            return c;
        });
    }

    private void shape(ChunkAccess chunk) {
        ChunkPos cp = chunk.getPos();
        BlockState stone = generatorSettings().value().defaultBlock();
        BlockState water = generatorSettings().value().defaultFluid();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState(), dirt = Blocks.DIRT.defaultBlockState();
        BlockState sand = Blocks.SAND.defaultBlockState(), gravel = Blocks.GRAVEL.defaultBlockState();
        int minY = chunk.getMinBuildHeight(), maxY = chunk.getMaxBuildHeight() - 1;
        int waterTop = Mc001Terrain.ANCHOR_Y - 1 + Mc001Terrain.WATER_TOP;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int wx = cp.getMinBlockX() + lx, wz = cp.getMinBlockZ() + lz;
                int x = wx - Mc001Terrain.ANCHOR_X, z = wz - Mc001Terrain.ANCHOR_Z;
                double w = Mc001Terrain.weight(x, z);
                if (w <= 0) continue;
                int natural = maxY;
                while (natural > minY) {
                    BlockState s = chunk.getBlockState(pos.set(wx, natural, wz));
                    if (!s.isAir() && s.getFluidState().isEmpty()) break;
                    natural--;
                }
                int authored = Mc001Terrain.ANCHOR_Y - 1 + Mc001Terrain.height(x, z);
                int top = (int) Math.round(natural + (authored - natural) * w);
                for (int y = natural + 1; y <= top; y++) chunk.setBlockState(pos.set(wx, y, wz), stone, false);
                for (int y = top + 1; y <= natural; y++) chunk.setBlockState(pos.set(wx, y, wz), air, false);
                boolean wet = w >= 0.999 && Mc001Terrain.water(x, z);
                if (top != natural && !wet) {
                    // Vanilla surface rules only dress ground near the natural surface, so reshaped
                    // ground (raised or cut down) gets its grass and soil here. Under water it gets sand.
                    boolean flooded = !chunk.getBlockState(pos.set(wx, top + 1, wz)).getFluidState().isEmpty();
                    chunk.setBlockState(pos.set(wx, top, wz), flooded ? sand : grass, false);
                    for (int y = top - 1; y >= top - 3; y--) chunk.setBlockState(pos.set(wx, y, wz), flooded ? sand : dirt, false);
                }
                if (wet) {
                    for (int y = top; y >= top - 2; y--) chunk.setBlockState(pos.set(wx, y, wz), y == top ? sand : gravel, false);
                    for (int y = top + 1; y <= waterTop; y++) chunk.setBlockState(pos.set(wx, y, wz), water, false);
                } else if (w >= 0.999 && top >= waterTop - 1) {
                    // Dry land in the authored area: no stray aquifer water left standing above it.
                    for (int y = top + 1; y <= waterTop + 1; y++) {
                        if (!chunk.getBlockState(pos.set(wx, y, wz)).getFluidState().isEmpty()) chunk.setBlockState(pos, air, false);
                    }
                }
                if (Mc001Terrain.protectedCore(x, z)) {
                    for (int y = Mc001Terrain.ANCHOR_Y - 24; y <= top - 3; y++) {
                        BlockState s = chunk.getBlockState(pos.set(wx, y, wz));
                        if (s.isAir() || !s.getFluidState().isEmpty()) chunk.setBlockState(pos, stone, false);
                    }
                }
            }
        }
    }

    private static boolean overlaps(ChunkPos cp, java.util.function.BiPredicate<Integer, Integer> area) {
        int x0 = cp.getMinBlockX() - Mc001Terrain.ANCHOR_X, z0 = cp.getMinBlockZ() - Mc001Terrain.ANCHOR_Z;
        for (int dx = 0; dx <= 15; dx += 15) for (int dz = 0; dz <= 15; dz += 15) if (area.test(x0 + dx, z0 + dz)) return true;
        return false;
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState random, BiomeManager biomes, StructureManager structures,
                             ChunkAccess chunk, GenerationStep.Carving step) {
        if (!overlaps(chunk.getPos(), Mc001Terrain::protectedCore)) super.applyCarvers(region, seed, random, biomes, structures, chunk, step);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structures) {
        if (!overlaps(chunk.getPos(), Mc001Terrain::village)) super.applyBiomeDecoration(level, chunk, structures);
    }

    @Override
    public void createStructures(RegistryAccess registries, ChunkGeneratorStructureState state, StructureManager structures,
                                 ChunkAccess chunk, StructureTemplateManager templates) {
        // MC-001's settlements are authored; no vanilla villages, outposts or monuments.
    }
}
