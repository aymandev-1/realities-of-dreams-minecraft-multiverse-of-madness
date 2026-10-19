package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Scenes 008 and 009 in eastern Awakening Valley, below the monument's mountainside, anchored at
 * MC-001 block (90, 96, -150); everything here is relative to it, with y = 0 the valley floor.
 *
 * The site is cut as a level stretch of valley (x -30..95, z -30..30) with a river gorge running
 * north to south through it (water at x 7..15). An ancient stone bridge crosses at z = 0; its
 * middle span (x 10..12) falls in Scene 008 and the player fills the gap. The east bank holds the
 * camp ground, three old survey markers, berry bushes, the overlook hill and, beyond it, the
 * expedition's abandoned campsite (x 68..92) with a cliff edge on its east side (Scene 009).
 * Positions the scenes check are listed in their BLOCKS stages and clues, so they must match.
 */
public final class ValleyCrossing {
    private static final int FLAGS = Block.UPDATE_CLIENTS;
    public static final int X0 = -30, X1 = 95, Z0 = -30, Z1 = 30;
    /** The gorge: lips at x 6 and 16, water between. */
    public static final int GORGE_W = 6, GORGE_E = 16;

    private final ServerLevel level;
    private final BlockPos anchor;

    private ValleyCrossing(ServerLevel level, BlockPos anchor) {
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

    /** Standing height of the ground (0 on the valley floor). */
    private static int ground(int x, int z) {
        // The overlook: a stepped hill, five blocks up, north-east of the camp ground.
        int d = Math.max(Math.abs(x - 42), Math.abs(z + 14));
        if (d <= 6) return Math.min(5, 6 - d);
        // The cliff on the campsite's east side.
        if (x >= 92 && z >= -18 && z <= -6) return -12;
        return 0;
    }

    public static void build(ServerLevel level, BlockPos anchor) {
        ValleyCrossing v = new ValleyCrossing(level, anchor);
        v.land();
        v.bridge();
        v.eastBank();
        v.campsite();
        v.trees();
    }

    private void land() {
        for (int x = X0; x <= X1; x++)
            for (int z = Z0; z <= Z1; z++) {
                int h = hash(x, z);
                if (x > GORGE_W && x < GORGE_E) {
                    for (int y = -16; y <= -10; y++) set(x, y, z, Blocks.STONE);
                    set(x, -9, z, h % 3 == 0 ? Blocks.SAND : Blocks.GRAVEL);
                    for (int y = -8; y <= -6; y++) set(x, y, z, Blocks.WATER);
                    for (int y = -5; y <= 22; y++) set(x, y, z, Blocks.AIR);
                    continue;
                }
                int g = ground(x, z);
                boolean lip = x == GORGE_W || x == GORGE_E;
                for (int y = -16; y < g - 3; y++) set(x, y, z, Blocks.STONE);
                for (int y = Math.max(-16, g - 3); y < g - 1; y++) set(x, y, z, lip ? Blocks.STONE : Blocks.DIRT);
                BlockState top = lip ? (h % 3 == 0 ? Blocks.MOSSY_COBBLESTONE : Blocks.STONE).defaultBlockState()
                        : (h % 13 == 0 ? Blocks.COARSE_DIRT : Blocks.GRASS_BLOCK).defaultBlockState();
                set(x, g - 1, z, top);
                for (int y = g; y <= 22; y++) set(x, y, z, Blocks.AIR);
                if (!lip && g == 0 && h % 17 == 0) set(x, 0, z, h % 2 == 0 ? Blocks.SHORT_GRASS : Blocks.DANDELION);
                else if (!lip && g == 0 && h % 23 == 0) set(x, 0, z, Blocks.SUNFLOWER);
            }
        // The gorge is closed at both ends so the river stays in the site.
        for (int x = GORGE_W; x <= GORGE_E; x++)
            for (int y = -16; y <= -1; y++) {
                set(x, y, Z0 - 1, Blocks.STONE);
                set(x, y, Z1 + 1, Blocks.STONE);
            }
        // Gorge walls are rough stone all the way down.
        for (int z = Z0; z <= Z1; z++)
            for (int y = -9; y <= -2; y++) {
                set(GORGE_W, y, z, hash(z, y) % 4 == 0 ? Blocks.ANDESITE : Blocks.STONE);
                set(GORGE_E, y, z, hash(y, z) % 4 == 0 ? Blocks.ANDESITE : Blocks.STONE);
            }
    }

    /** The ancient bridge across the gorge at z = 0, still whole. */
    private void bridge() {
        for (int x = GORGE_W - 1; x <= GORGE_E + 1; x++)
            for (int z = -1; z <= 1; z++) set(x, -1, z, hash(x, z) % 4 == 0 ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS);
        for (int x = GORGE_W; x <= GORGE_E; x++)
            for (int z : new int[] {-2, 2})
                if (hash(x, z) % 5 != 0) set(x, 0, z, hash(z, x) % 3 == 0 ? Blocks.MOSSY_STONE_BRICK_WALL : Blocks.STONE_BRICK_WALL);
        for (int px : new int[] {9, 13})
            for (int z = -1; z <= 1; z++)
                for (int y = -9; y <= -2; y++) set(px, y, z, y < -5 && hash(px, y) % 3 == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS);
        // The supply crate on the bridge's west end is the scene's clue block (9, 0, 1).
    }

    /** Scene 008: the middle span gives way under loose stone, leaving the crate on the edge of the west stub. */
    public static void collapse(ServerLevel level, BlockPos anchor) {
        ValleyCrossing v = new ValleyCrossing(level, anchor);
        for (int x = 10; x <= 12; x++)
            for (int z = -2; z <= 2; z++) {
                v.set(x, -1, z, Blocks.AIR);
                v.set(x, 0, z, Blocks.AIR);
                if (Math.abs(z) <= 1) v.set(x, -8, z, hash(x, z) % 2 == 0 ? Blocks.COBBLESTONE : Blocks.STONE_BRICKS);
            }
        // The west stub cracks and tilts toward the gap, the crate right on its edge.
        v.set(9, -1, 1, Blocks.CRACKED_STONE_BRICKS);
        v.set(9, -1, 2, Blocks.AIR);
    }

    /** Scene 008: the crate is safe in Steve's hands. */
    public static void crateSaved(ServerLevel level, BlockPos anchor) {
        new ValleyCrossing(level, anchor).set(9, 0, 1, Blocks.AIR);
    }

    /** Camp ground, old survey markers and berry bushes on the east bank. */
    private void eastBank() {
        // Log seats round the cleared camp ground (the campfire goes at 26, 0, 6).
        for (int[] s : new int[][] {{24, 4}, {28, 8}, {24, 8}}) set(s[0], 0, s[1], Blocks.STRIPPED_OAK_LOG);
        for (int x = 24; x <= 28; x++) for (int z = 4; z <= 8; z++) set(x, -1, z, Blocks.COARSE_DIRT);
        // Old survey markers: weathered posts with rubble round their feet (the posts' tops are clues).
        for (int[] m : new int[][] {{30, -22}, {52, 18}, {58, -10}}) {
            set(m[0], 0, m[1], Blocks.MOSSY_STONE_BRICKS);
            set(m[0] + 1, 0, m[1], Blocks.COBBLESTONE_SLAB);
            set(m[0], 0, m[1] - 1, Blocks.MOSSY_COBBLESTONE_SLAB);
        }
        BlockState ripe = Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3);
        for (int[] b : new int[][] {{33, 12}, {35, 13}, {37, 12}, {34, 15}, {36, 16}, {38, 15}}) set(b[0], 0, b[1], ripe);
        // A worn stone at the overlook's top.
        set(42, 5, -16, Blocks.MOSSY_STONE_BRICK_WALL);
    }

    /** Scene 009: the expedition's campsite, left in a hurry. */
    private void campsite() {
        for (int[] t : new int[][] {{72, -8}, {82, -10}, {74, 8}}) tent(t[0], t[1]);
        set(80, 0, -2, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, false));
        set(70, 0, 12, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, false));
        // The table the lantern rolls across.
        for (int x = 77; x <= 79; x++) set(x, 0, 2, Blocks.SPRUCE_SLAB.defaultBlockState()
                .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, net.minecraft.world.level.block.state.properties.SlabType.TOP));
        set(77, 1, 2, Blocks.LANTERN);
        // Scattered equipment.
        set(83, 0, 3, Blocks.BARREL);
        set(84, 0, 4, Blocks.COMPOSTER);
        set(71, 0, -2, Blocks.CRAFTING_TABLE);
        for (int[] c : new int[][] {{75, -1}, {86, -4}, {79, 9}}) set(c[0], 0, c[1], Blocks.WHITE_CARPET);
        set(87, 0, -3, Blocks.COBBLESTONE_SLAB);
        // Tracks heading east toward the trees.
        for (int x = 80; x <= 91; x++) if (x % 2 == 0) set(x, -1, 10 + (x - 80) / 4, Blocks.PODZOL);
        // The perimeter post with the woodland emblem (its top is the clue block).
        set(66, 0, -12, Blocks.MOSSY_STONE_BRICKS);
        set(66, 1, -12, Blocks.MOSSY_STONE_BRICKS);
        set(65, 0, -12, Blocks.FERN);
    }

    private void tent(int x, int z) {
        for (int dz = 0; dz <= 3; dz++) {
            set(x, 0, z + dz, Blocks.WHITE_WOOL);
            set(x + 4, 0, z + dz, Blocks.WHITE_WOOL);
            set(x + 1, 1, z + dz, Blocks.WHITE_WOOL);
            set(x + 3, 1, z + dz, Blocks.WHITE_WOOL);
            set(x + 2, 2, z + dz, Blocks.WHITE_WOOL);
        }
        set(x + 2, 0, z + 1, Blocks.WHITE_CARPET);
        set(x + 1, 0, z, Blocks.OAK_FENCE);
        set(x + 3, 0, z, Blocks.OAK_FENCE);
    }

    /** Scene 009: the tremor rolls the camp lantern along the table. */
    public static void lanternRolls(ServerLevel level, BlockPos anchor) {
        ValleyCrossing v = new ValleyCrossing(level, anchor);
        v.set(77, 1, 2, Blocks.AIR);
        v.set(78, 1, 2, Blocks.LANTERN);
    }

    private void trees() {
        int[][] oaks = {{-24, -20}, {-14, 22}, {-26, 8}, {28, 24}, {60, 24}, {92, 12}, {93, 18}, {90, 22}, {-6, -24}};
        for (int[] t : oaks) tree(t[0], t[1], hash(t[0], t[1]) % 2 == 0);
    }

    private void tree(int x, int z, boolean spruce) {
        Block log = spruce ? Blocks.SPRUCE_LOG : Blocks.OAK_LOG, leaves = spruce ? Blocks.SPRUCE_LEAVES : Blocks.OAK_LEAVES;
        int g = ground(x, z);
        for (int y = 0; y < 5; y++) set(x, g + y, z, log);
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                for (int y = 3; y <= 6; y++) {
                    int r = y >= 5 ? 1 : 2;
                    if (Math.abs(dx) > r || Math.abs(dz) > r || (dx == 0 && dz == 0 && y < 5)) continue;
                    if (Math.abs(dx) == 2 && Math.abs(dz) == 2) continue;
                    set(x + dx, g + y, z + dz, leaves.defaultBlockState()
                            .setValue(net.minecraft.world.level.block.LeavesBlock.PERSISTENT, true));
                }
        set(x, g + 5, z, leaves.defaultBlockState().setValue(net.minecraft.world.level.block.LeavesBlock.PERSISTENT, true));
        set(x, g + 6, z, leaves.defaultBlockState().setValue(net.minecraft.world.level.block.LeavesBlock.PERSISTENT, true));
    }
}
