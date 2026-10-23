package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Ancient Northern Monument (Scene 006), on the mountainside north of Awakening Valley at
 * MC-001 block (40, 108, -200), which is the scene's anchor. Everything here is relative to it,
 * with y = 0 the courtyard floor.
 *
 * The weathered outcrop is cut into a level courtyard (radius 26) reached by an old causeway from
 * the south. A broken ring wall and eight ring pillars surround a raised terrace (x and z -8..8,
 * top at y 4) whose south stair has collapsed: the four steps the player rebuilds are the scene's
 * "entrance" BLOCKS stage. On the terrace stand two carved steles, a third panel in the face of
 * the upper tier (z -8..-5, top at y 7), the six-structure carving hidden behind fallen earth, and
 * the split central pillar whose carved lines light up. Clue blocks are placed by the scene.
 */
public final class NorthernMonument {
    private static final int FLAGS = Block.UPDATE_CLIENTS;
    /** Where the monument stands, in MC-001 block coordinates (the scene's anchor). */
    public static final int X = 40, Y = 108, Z = -200;
    /** The courtyard's radius and the causeway's southern end. */
    public static final int COURT_RADIUS = 26, CAUSEWAY_END = 46;
    /** The six-structure carving and the earth in front of it the player digs away. */
    public static final int CARVING_Z = -5, COVER_Z = -4;

    private final ServerLevel level;
    private final BlockPos anchor;

    private NorthernMonument(ServerLevel level, BlockPos anchor) {
        this.level = level;
        this.anchor = anchor;
    }

    private void set(int x, int y, int z, BlockState s) {
        level.setBlock(anchor.offset(x, y, z), s, FLAGS);
    }

    private void set(int x, int y, int z, Block b) {
        set(x, y, z, b.defaultBlockState());
    }

    private static int hash(int x, int z) {
        int h = x * 73_856_093 ^ z * 19_349_663;
        return (h ^ (h >>> 13)) & 0x7fffffff;
    }

    /** Builds the whole site, replacing the silhouette Scene 005 showed from the watch platform. */
    public static void build(ServerLevel level, BlockPos anchor) {
        NorthernMonument m = new NorthernMonument(level, anchor);
        m.ground();
        m.ringWall();
        m.ringPillars();
        m.terrace();
        m.upperTier();
        m.carving();
        m.centralPillar();
        m.steles();
        m.camp();
    }

    /** The courtyard and causeway: solid ground beneath, open air above. */
    private void ground() {
        for (int x = -COURT_RADIUS - 1; x <= COURT_RADIUS + 1; x++)
            for (int z = -COURT_RADIUS - 1; z <= CAUSEWAY_END; z++) {
                double d = Math.hypot(x, z);
                boolean court = d <= COURT_RADIUS;
                boolean causeway = !court && Math.abs(x) <= 2 && z > 0;
                if (!court && !causeway) continue;
                int h = hash(x, z);
                for (int y = causeway ? -16 : -12; y <= -2; y++) set(x, y, z, Blocks.STONE);
                BlockState top;
                if (causeway) top = (h % 5 == 0 ? Blocks.CRACKED_STONE_BRICKS : h % 3 == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS).defaultBlockState();
                else if (d <= 17) top = (h % 7 == 0 ? Blocks.MOSSY_COBBLESTONE : h % 4 == 0 ? Blocks.CRACKED_STONE_BRICKS
                        : h % 3 == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS).defaultBlockState();
                else top = (h % 6 == 0 ? Blocks.COARSE_DIRT : h % 9 == 0 ? Blocks.MOSS_BLOCK : Blocks.GRASS_BLOCK).defaultBlockState();
                set(x, -1, z, top);
                int clear = d <= 18 ? 44 : 18;
                for (int y = 0; y <= clear; y++) set(x, y, z, Blocks.AIR);
                if (causeway && Math.abs(x) == 2 && h % 4 != 0) set(x, 0, z, h % 3 == 0 ? Blocks.MOSSY_STONE_BRICK_WALL : Blocks.STONE_BRICK_WALL);
                else if (!causeway && d > 18 && h % 11 == 0) set(x, 0, z, Blocks.SHORT_GRASS);
            }
    }

    /** A broken ring wall round the courtyard, open to the south where the causeway arrives. */
    private void ringWall() {
        for (int x = -17; x <= 17; x++)
            for (int z = -17; z <= 17; z++) {
                double d = Math.hypot(x, z);
                if (d < 15.5 || d >= 16.5 || (z > 0 && Math.abs(x) <= 3)) continue;
                int h = hash(x, z);
                if (h % 9 == 0) continue; // fallen away
                int height = 1 + h % 4;
                for (int y = 0; y < height; y++) {
                    Block b = y == height - 1 && h % 2 == 0 ? Blocks.MOSSY_STONE_BRICKS
                            : (h + y) % 5 == 0 ? Blocks.CRACKED_DEEPSLATE_BRICKS : Blocks.DEEPSLATE_BRICKS;
                    set(x, y, z, b);
                }
                if (h % 3 == 0) set(x, height, z, Blocks.MOSS_CARPET);
            }
    }

    /** Eight pillars, some still standing tall, some broken off. */
    private void ringPillars() {
        int[] heights = {34, 22, 30, 12, 36, 18, 28, 26};
        for (int i = 0; i < heights.length; i++) {
            double a = Math.PI / 8 + i * Math.PI * 2 / heights.length;
            int px = (int) Math.round(Math.cos(a) * 12), pz = (int) Math.round(Math.sin(a) * 12);
            if (px < 0) px--;
            if (pz < 0) pz--;
            for (int y = 0; y < heights[i]; y++)
                for (int dx = 0; dx <= 1; dx++)
                    for (int dz = 0; dz <= 1; dz++)
                        set(px + dx, y, pz + dz, y % 5 == 4 ? Blocks.DEEPSLATE_BRICKS : y < 3 && hash(px + dx, y) % 3 == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.POLISHED_DEEPSLATE);
            // Fallen pieces of the broken ones lie in the courtyard.
            if (heights[i] < 20) for (int k = 1; k <= 3; k++) set(px + (int) Math.signum(px) * k, 0, pz, k == 3 ? Blocks.COBBLED_DEEPSLATE_SLAB : Blocks.POLISHED_DEEPSLATE);
        }
    }

    /** The raised terrace, its collapsed south stair and the rubble that fell from it. */
    private void terrace() {
        for (int x = -8; x <= 8; x++)
            for (int z = -8; z <= 8; z++)
                for (int y = 0; y <= 3; y++) {
                    boolean edge = Math.abs(x) == 8 || Math.abs(z) == 8;
                    int h = hash(x * 3 + y, z);
                    Block b;
                    if (y == 3) b = h % 6 == 0 ? Blocks.MOSSY_STONE_BRICKS : h % 5 == 0 ? Blocks.CRACKED_DEEPSLATE_TILES : Blocks.POLISHED_DEEPSLATE;
                    else if (edge) b = h % 4 == 0 ? Blocks.CRACKED_DEEPSLATE_BRICKS : Blocks.DEEPSLATE_BRICKS;
                    else b = Blocks.COBBLED_DEEPSLATE;
                    set(x, y, z, b);
                }
        // The south stair (x 0, z 12 up to 9) has collapsed into rubble beside where it stood.
        set(-2, 0, 9, Blocks.CRACKED_DEEPSLATE_BRICKS);
        set(2, 0, 9, Blocks.DEEPSLATE_BRICKS);
        set(2, 1, 9, Blocks.COBBLED_DEEPSLATE_WALL);
        set(-1, 0, 10, Blocks.COBBLED_DEEPSLATE_SLAB);
        set(1, 0, 12, Blocks.COBBLED_DEEPSLATE_SLAB);
        set(-3, 0, 12, Blocks.COBBLED_DEEPSLATE);
        set(3, 0, 11, Blocks.COBBLED_DEEPSLATE_SLAB);
        set(-2, 0, 13, Blocks.COBBLESTONE_SLAB);
    }

    /** The upper tier along the terrace's north side, reached by a stair on its west end. */
    private void upperTier() {
        for (int x = -8; x <= 8; x++)
            for (int z = -8; z <= -5; z++)
                for (int y = 4; y <= 6; y++) {
                    int h = hash(x, z * 7 + y);
                    set(x, y, z, y == 6 ? (h % 5 == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.POLISHED_DEEPSLATE)
                            : h % 4 == 0 ? Blocks.CRACKED_DEEPSLATE_BRICKS : Blocks.DEEPSLATE_BRICKS);
                }
        BlockState step = Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
        set(-7, 4, -3, step);
        set(-7, 4, -4, Blocks.DEEPSLATE_BRICKS);
        set(-7, 5, -4, step);
        // A few broken battlements on the upper tier.
        for (int x : new int[] {-8, -4, 3, 8}) set(x, 7, -8, Blocks.DEEPSLATE_BRICK_WALL);
    }

    /**
     * The carving of six structures round a circle, in the upper tier's face, with the earth that
     * slid down over it (x -2..2, y 4..6 at z -4): the player digs that away to uncover it.
     */
    private void carving() {
        for (int x = -2; x <= 2; x++) for (int y = 4; y <= 6; y++) set(x, y, CARVING_Z, Blocks.POLISHED_BLACKSTONE);
        for (int[] s : new int[][] {{-2, 5}, {-1, 6}, {-1, 4}, {1, 6}, {1, 4}, {2, 5}}) set(s[0], s[1], CARVING_Z, Blocks.CHISELED_STONE_BRICKS);
        set(0, 5, CARVING_Z, Blocks.CHISELED_POLISHED_BLACKSTONE);
        for (int x = -2; x <= 2; x++)
            for (int y = 4; y <= 6; y++)
                set(x, y, COVER_Z, (hash(x, y) % 3 == 0 ? Blocks.ROOTED_DIRT : y == 6 ? Blocks.MOSS_BLOCK : Blocks.COARSE_DIRT));
    }

    /** The split central pillar: carved lines up its corners, open down the middle above y 7. */
    private void centralPillar() {
        for (int y = 4; y <= 19; y++)
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++) {
                    boolean split = x == 0 && y >= 7 && y <= 19;
                    boolean broken = x == 1 && y > 16;
                    if (split || broken) {
                        set(x, y, z, Blocks.AIR);
                        continue;
                    }
                    boolean corner = Math.abs(x) == 1 && Math.abs(z) == 1;
                    set(x, y, z, corner && y >= 5 ? Blocks.CHISELED_DEEPSLATE : Blocks.POLISHED_DEEPSLATE);
                }
        // The patterns the split laid bare, on the inside faces.
        for (int y = 8; y <= 18; y += 2) {
            set(-1, y, 0, Blocks.CHISELED_POLISHED_BLACKSTONE);
            if (y <= 16) set(1, y, 0, Blocks.CHISELED_POLISHED_BLACKSTONE);
        }
        // Chips of stone fallen from the crack.
        set(2, 4, 2, Blocks.COBBLED_DEEPSLATE_SLAB);
        set(-2, 4, 1, Blocks.POLISHED_DEEPSLATE_SLAB);
    }

    /** The pillar's carved lines lit by the light that runs through them, or dark again. */
    public static void glow(ServerLevel level, BlockPos anchor, boolean on) {
        NorthernMonument m = new NorthernMonument(level, anchor);
        BlockState line = (on ? Blocks.SEA_LANTERN : Blocks.CHISELED_DEEPSLATE).defaultBlockState();
        for (int y = 5; y <= 19; y++)
            for (int x : new int[] {-1, 1})
                for (int z : new int[] {-1, 1}) if (!(x == 1 && y > 16)) m.set(x, y, z, line);
    }

    /** Two steles on the terrace; their carved panels are clue blocks the scene places on top. */
    private void steles() {
        for (int x : new int[] {-6, 6}) {
            set(x, 4, 3, Blocks.DEEPSLATE_BRICKS);
            set(x, 6, 3, Blocks.POLISHED_DEEPSLATE_SLAB);
        }
    }

    /**
     * Scene 007: Orin's field table with the three compasses he gave up on, lying in frames on the
     * table, and the stone pedestal. The survey stakes are the scene's clue blocks (lodestones).
     */
    public static void survey(ServerLevel level, BlockPos anchor) {
        NorthernMonument m = new NorthernMonument(level, anchor);
        m.set(7, 0, 19, Blocks.SPRUCE_PLANKS);
        m.set(7, 0, 20, Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH));
        for (int x = 5; x <= 7; x++) {
            BlockPos at = anchor.offset(x, 1, 19);
            level.setBlock(at, Blocks.AIR.defaultBlockState(), FLAGS);
            net.minecraft.world.entity.decoration.ItemFrame frame = new net.minecraft.world.entity.decoration.ItemFrame(level, at, Direction.UP);
            frame.setItem(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COMPASS), false);
            level.addFreshEntity(frame);
        }
        // The pedestal: a plinth of old stone in the courtyard, its top the clue block.
        for (int[] c : new int[][] {{-1, 22}, {1, 22}, {-1, 24}, {1, 24}}) m.set(c[0], -1, c[1], Blocks.POLISHED_ANDESITE);
        m.set(0, -1, 23, Blocks.POLISHED_ANDESITE);
    }

    /** Orin's survey gear and Bram's lookout post in the courtyard. */
    private void camp() {
        set(5, 0, 19, Blocks.BARREL);
        set(6, 0, 19, Blocks.CARTOGRAPHY_TABLE);
        set(4, 0, 20, Blocks.CHEST);
        set(-6, 0, 18, Blocks.CAMPFIRE.defaultBlockState().setValue(net.minecraft.world.level.block.CampfireBlock.LIT, false));
        set(-9, 0, 17, Blocks.SPRUCE_FENCE);
        set(-9, 1, 17, Blocks.LANTERN);
    }
}
