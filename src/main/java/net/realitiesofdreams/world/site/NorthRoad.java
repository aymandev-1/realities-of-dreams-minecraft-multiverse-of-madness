package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Scene 005 on the northern road outside the gate (flat ground, y = 0): the half-built barricade,
 * the damaged watch platform, the unlit lantern posts, the supply spot, the rockfall gully, and the
 * Ancient Northern Monument's silhouette on the mountainside beyond the valley (around x 40, z -200).
 * The gaps the player fills are listed in the scene's BLOCKS stages, so they must match this layout.
 */
public final class NorthRoad {
    private static final int FLAGS = Block.UPDATE_CLIENTS;

    private final ServerLevel level;
    private final BlockPos anchor;

    private NorthRoad(ServerLevel level, BlockPos anchor) {
        this.level = level;
        this.anchor = anchor;
    }

    private void set(int x, int y, int z, BlockState s) {
        level.setBlock(anchor.offset(x, y, z), s, FLAGS);
    }

    private void set(int x, int y, int z, Block b) {
        set(x, y, z, b.defaultBlockState());
    }

    /** The work site: barricade posts, the platform with planks missing, lantern posts and the supply spot. */
    public static void worksite(ServerLevel level, BlockPos anchor) {
        NorthRoad r = new NorthRoad(level, anchor);
        // Bram's old barrier across the trail comes down: the road opens with an escort.
        for (int x = -4; x <= 2; x++) r.set(x, 0, -27, Blocks.AIR);
        r.set(-3, 1, -27, Blocks.AIR);
        // Barricade along the east side of the road: posts at x 3, 5, 7, 9; the gaps between are the player's.
        for (int x = 3; x <= 9; x += 2) for (int y = 0; y <= 2; y++) r.set(x, y, -32, Blocks.STRIPPED_SPRUCE_LOG);
        for (int x = 3; x <= 9; x++) r.set(x, -1, -32, Blocks.COARSE_DIRT);
        // The watch platform (x 8..11, z -38..-35), deck at y 4 with three planks missing.
        for (int[] c : new int[][] {{8, -38}, {11, -38}, {8, -35}, {11, -35}}) for (int y = 0; y <= 3; y++) r.set(c[0], y, c[1], Blocks.SPRUCE_LOG);
        for (int y = 0; y <= 3; y++) r.set(9, y, -35, Blocks.SPRUCE_LOG);
        for (int x = 8; x <= 11; x++)
            for (int z = -38; z <= -35; z++) {
                boolean missing = (x == 9 && z == -37) || (x == 10 && z == -37) || (x == 10 && z == -36);
                r.set(x, 4, z, missing ? Blocks.AIR.defaultBlockState() : Blocks.SPRUCE_PLANKS.defaultBlockState());
                boolean edge = x == 8 || x == 11 || z == -38 || z == -35;
                if (edge && !(x == 9 && z == -35)) r.set(x, 5, z, Blocks.SPRUCE_FENCE);
            }
        for (int y = 0; y <= 4; y++) r.set(9, y, -34, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH));
        r.set(10, 0, -33, Blocks.OAK_PLANKS);
        r.set(10, 1, -33, Blocks.OAK_SLAB);
        // Lantern posts along the safe route, unlit.
        for (int[] p : new int[][] {{1, -29}, {-5, -31}, {1, -34}, {-4, -37}}) for (int y = 0; y <= 1; y++) r.set(p[0], y, p[1], Blocks.SPRUCE_FENCE);
        // Supply spot beside the platform.
        r.set(12, -1, -36, Blocks.SPRUCE_PLANKS);
        r.set(12, -1, -37, Blocks.SPRUCE_PLANKS);
        r.set(13, 0, -36, Blocks.SPRUCE_FENCE);
        r.set(13, 1, -36, Blocks.LANTERN);
    }

    /** Falling stone fills the road at z -40 between the walls of a narrow cutting. */
    public static void rockfall(ServerLevel level, BlockPos anchor) {
        NorthRoad r = new NorthRoad(level, anchor);
        for (int z = -43; z <= -38; z++)
            for (int y = 0; y <= 2; y++) {
                r.set(-2, y, z, (y + z) % 3 == 0 ? Blocks.ANDESITE : Blocks.STONE);
                r.set(4, y, z, (y + z) % 3 == 0 ? Blocks.STONE : Blocks.ANDESITE);
            }
        for (int x = -1; x <= 3; x++)
            for (int y = 0; y <= 1; y++) r.set(x, y, -40, (x + y) % 2 == 0 ? Blocks.COBBLESTONE : Blocks.STONE);
        for (int x : new int[] {-1, 2}) r.set(x, 0, -39, Blocks.COBBLESTONE_SLAB);
    }

    /**
     * The Ancient Northern Monument seen from afar: a broken ring of tall pillars round a stepped
     * core on the mountainside, built on whatever ground is there. Scene 006 will explore it.
     */
    public static void monumentSilhouette(ServerLevel level, BlockPos anchor) {
        int cx = anchor.getX() + 40, cz = anchor.getZ() - 200;
        int base = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, cx, cz);
        BlockState stone = Blocks.POLISHED_DEEPSLATE.defaultBlockState(), dark = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        // Stepped core.
        for (int step = 0; step < 6; step++) {
            int r = 7 - step;
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++)
                    for (int y = 0; y < 4; y++) level.setBlock(new BlockPos(cx + dx, base + step * 4 + y - 1, cz + dz), (y == 3 ? dark : stone), FLAGS);
        }
        // Broken ring of pillars, some taller than the core.
        int[] heights = {34, 22, 30, 12, 36, 18, 28, 26};
        for (int i = 0; i < heights.length; i++) {
            double a = i * Math.PI * 2 / heights.length;
            int px = cx + (int) Math.round(Math.cos(a) * 13), pz = cz + (int) Math.round(Math.sin(a) * 13);
            int ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, px, pz);
            for (int y = ground - 2; y < ground + heights[i]; y++)
                for (int dx = 0; dx <= 1; dx++)
                    for (int dz = 0; dz <= 1; dz++) level.setBlock(new BlockPos(px + dx, y, pz + dz), (y % 5 == 0 ? dark : stone), FLAGS);
        }
        // A faint light at the top of the core: something is still awake inside.
        level.setBlock(new BlockPos(cx, base + 23, cz), Blocks.SEA_LANTERN.defaultBlockState(), FLAGS);
    }
}
