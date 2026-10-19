package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Scene 010, the Sylvaris border outpost, anchored at MC-001 block (150, 66, 40); everything here
 * is relative to it, with y = 0 the forest floor.
 *
 * A clearing of forest (x and z -45..45) with enormous dark oaks. The main platform (floor y 4,
 * x -9..2, z -5..6) stands round the great tree at x -4..-3, z 0..1, reached by a stair on its
 * west side. East of it a ravine with a stream (x 22..29) is crossed by an elevated walkway (floor
 * y 3, z 8..9) between two platforms; its middle (x 24..27) has fallen and the player rebuilds it.
 * Beyond lies the eastern grove with the exposed ancient stone (36, 0, -14) and a ranger
 * watchtower (x 38..42, z -34..-30, door on the east side, lookout floor standing height y 12). Positions the scene checks are in its
 * BLOCKS stage and clues, so they must match.
 */
public final class SylvarisOutpost {
    public static final int X0 = -45, X1 = 45, Z0 = -45, Z1 = 45;
    /** The ravine under the walkway. */
    public static final int RAVINE_W = 22, RAVINE_E = 29;
    /** The fallen stretch of walkway the player rebuilds (floor y 3, z 8..9). */
    public static final int GAP_W = 24, GAP_E = 27;

    private final Builder b;

    private SylvarisOutpost(ServerLevel level, BlockPos anchor) {
        this.b = new Builder(level, anchor, 1010L);
    }

    private static int hash(int x, int z) {
        int h = x * 73_856_093 ^ z * 19_349_663;
        return (h ^ (h >>> 13)) & 0x7fffffff;
    }

    public static void build(ServerLevel level, BlockPos anchor) {
        SylvarisOutpost s = new SylvarisOutpost(level, anchor);
        s.land();
        s.greatTrees();
        s.mainPlatform();
        s.walkway();
        s.grove();
        s.watchtower();
        s.forest();
    }

    private boolean ravine(int x) {
        return x >= RAVINE_W && x <= RAVINE_E;
    }

    private void land() {
        for (int x = X0; x <= X1; x++)
            for (int z = Z0; z <= Z1; z++) {
                int h = hash(x, z);
                if (ravine(x)) {
                    boolean bank = x == RAVINE_W || x == RAVINE_E;
                    int floor = bank ? -2 : -5;
                    b.fill(x, -16, z, x, floor - 1, z, Blocks.STONE);
                    b.set(x, floor, z, bank ? Blocks.MOSSY_COBBLESTONE : h % 3 == 0 ? Blocks.GRAVEL : Blocks.MUD);
                    for (int y = floor + 1; y <= 26; y++) b.set(x, y, z, y <= -3 ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState());
                    if (bank && h % 4 == 0) b.set(x, floor + 1, z, Blocks.FERN);
                    continue;
                }
                b.fill(x, -16, z, x, -4, z, Blocks.STONE);
                b.fill(x, -3, z, x, -2, z, Blocks.DIRT);
                Block top = h % 9 == 0 ? Blocks.PODZOL : h % 13 == 0 ? Blocks.MOSS_BLOCK : h % 17 == 0 ? Blocks.ROOTED_DIRT : Blocks.GRASS_BLOCK;
                b.set(x, -1, z, top);
                for (int y = 0; y <= 26; y++) b.set(x, y, z, Blocks.AIR);
                if (h % 7 == 0) b.set(x, 0, z, h % 3 == 0 ? Blocks.FERN : Blocks.SHORT_GRASS);
                else if (h % 61 == 0) b.set(x, 0, z, Blocks.LILY_OF_THE_VALLEY);
                else if (h % 67 == 0) b.set(x, 0, z, Blocks.BROWN_MUSHROOM);
            }
        // The ravine is closed at both ends so the stream stays put.
        for (int x = RAVINE_W; x <= RAVINE_E; x++)
            for (int y = -16; y <= -2; y++) {
                b.set(x, y, Z0 - 1, Blocks.STONE);
                b.set(x, y, Z1 + 1, Blocks.STONE);
            }
        // The ranger path: in from the west edge, round the south of the main platform to the walkway
        // stair, and on the far side of the ravine from the walkway stair down to the grove and tower.
        for (int x = X0; x <= -15; x++) for (int z = -1; z <= 1; z++) path(x, z);
        for (int x = -17; x <= -15; x++) for (int z = -1; z <= 10; z++) path(x, z);
        for (int x = -15; x <= 9; x++) for (int z = 8; z <= 10; z++) path(x, z);
        for (int x = 43; x <= 44; x++) for (int z = -32; z <= 9; z++) path(x, z);
        for (int x = 38; x <= 42; x++) for (int z = -12; z <= -11; z++) path(x, z);
    }

    private void path(int x, int z) {
        if (hash(x, z) % 6 != 0) b.set(x, -1, z, Blocks.DIRT_PATH);
        b.set(x, 0, z, Blocks.AIR);
    }

    /** An enormous dark oak: a 2x2 trunk with roots spreading at its foot and a wide crown. */
    private void greatTree(int x, int z, int height, int crown) {
        for (int y = -1; y < height; y++)
            for (int dx = 0; dx <= 1; dx++)
                for (int dz = 0; dz <= 1; dz++) b.set(x + dx, y, z + dz, Blocks.DARK_OAK_LOG);
        // Buttress roots.
        for (int[] r : new int[][] {{-1, 0}, {2, 1}, {0, -1}, {1, 2}, {-1, 1}, {2, 0}}) {
            b.set(x + r[0], 0, z + r[1], Blocks.DARK_OAK_WOOD);
            if (hash(x + r[0], z + r[1]) % 2 == 0) b.set(x + r[0], 1, z + r[1], Blocks.DARK_OAK_WOOD);
            b.set(x + r[0] * 2 - (r[0] > 0 ? 1 : 0), -1, z + r[1] * 2 - (r[1] > 0 ? 1 : 0), Blocks.ROOTED_DIRT);
        }
        // Branches and the crown.
        int top = height;
        for (int dx = -crown; dx <= crown + 1; dx++)
            for (int dz = -crown; dz <= crown + 1; dz++)
                for (int dy = -4; dy <= 3; dy++) {
                    double cx = dx - 0.5, cz = dz - 0.5;
                    double r = (cx * cx + cz * cz) / (double) (crown * crown) + (dy * dy) / 16.0;
                    if (r > 1 || (r > 0.8 && hash(x + dx, z + dz + dy) % 3 == 0)) continue;
                    BlockState leaf = Builder.leaves(hash(x + dx, top + dy) % 11 == 0 ? Blocks.FLOWERING_AZALEA_LEAVES : Blocks.DARK_OAK_LEAVES);
                    b.setIfAir(x + dx, top + dy, z + dz, leaf);
                }
        for (Direction d : Direction.Plane.HORIZONTAL) {
            for (int i = 1; i <= crown - 1; i++) {
                int bx = x + (d.getStepX() > 0 ? 1 : 0) + d.getStepX() * i, bz = z + (d.getStepZ() > 0 ? 1 : 0) + d.getStepZ() * i;
                b.set(bx, top - 3 + i / 3, bz, Builder.log(Blocks.DARK_OAK_LOG, d.getAxis()));
            }
        }
        // Vines and glow lichen hanging from the crown.
        for (int i = 0; i < 10; i++) {
            int vx = x - crown + hash(x, i) % (crown * 2 + 2), vz = z - crown + hash(i, z) % (crown * 2 + 2);
            for (int y = top - 5; y >= top - 5 - hash(vx, vz) % 4; y--)
                if (b.get(vx, y, vz).isAir() && !b.get(vx, y + 1, vz).isAir()) b.set(vx, y, vz, Blocks.CAVE_VINES_PLANT);
        }
    }

    private void greatTrees() {
        greatTree(-4, 0, 22, 7);
        greatTree(16, 13, 19, 6);
        greatTree(36, 15, 18, 6);
        greatTree(31, -27, 20, 6);
        greatTree(-30, 24, 18, 6);
        greatTree(-28, -26, 21, 7);
        greatTree(4, -32, 17, 6);
        greatTree(-38, 7, 16, 5);
    }

    /** A plank floor at floor height with a fence railing round its edge (gaps where stairs arrive). */
    private void platform(int x0, int z0, int x1, int z1, int floor, int... gapsX) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                if (b.get(x, floor, z).is(Blocks.DARK_OAK_LOG)) continue;
                b.set(x, floor, z, (x + z) % 5 == 0 ? Blocks.STRIPPED_SPRUCE_WOOD : Blocks.SPRUCE_PLANKS);
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                if (edge) b.set(x, floor + 1, z, Blocks.SPRUCE_FENCE);
            }
        for (int g : gapsX) for (int z = z0; z <= z1; z++) if (b.get(g, floor + 1, z).is(Blocks.SPRUCE_FENCE)) b.set(g, floor + 1, z, Blocks.AIR);
        // Posts down to the forest floor at the corners.
        for (int[] c : new int[][] {{x0, z0}, {x0, z1}, {x1, z0}, {x1, z1}}) b.fill(c[0], 0, c[1], c[0], floor - 1, c[1], Blocks.SPRUCE_LOG);
        b.set(x0, floor + 2, z0, Blocks.LANTERN);
        b.set(x1, floor + 2, z1, Blocks.LANTERN);
    }

    /** A straight stair of {@code steps} going up eastward from (x, 0, z), two wide. */
    private void stairEast(int x, int z, int steps) {
        for (int k = 0; k < steps; k++)
            for (int dz = 0; dz <= 1; dz++) {
                b.fill(x + k, -1, z + dz, x + k, k - 1, z + dz, Blocks.SPRUCE_PLANKS);
                b.set(x + k, k, z + dz, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
            }
    }

    private void stairWest(int x, int z, int steps) {
        for (int k = 0; k < steps; k++)
            for (int dz = 0; dz <= 1; dz++) {
                b.fill(x - k, -1, z + dz, x - k, k - 1, z + dz, Blocks.SPRUCE_PLANKS);
                b.set(x - k, k, z + dz, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
            }
    }

    private void mainPlatform() {
        platform(-9, -5, 2, 6, 4);
        stairEast(-14, 4, 5);
        b.set(-9, 5, 4, Blocks.AIR);
        b.set(-9, 5, 5, Blocks.AIR);
        // A ranger's post: banners of Sylvaris green, a map table and a bench.
        b.set(1, 5, -4, Blocks.CARTOGRAPHY_TABLE);
        b.set(-1, 5, -4, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-8, 5, -4, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH, false));
        b.set(-7, 5, -4, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH, false));
        for (int[] p : new int[][] {{-9, -5}, {2, -5}, {2, 6}}) b.set(p[0], 6, p[1], Blocks.GREEN_BANNER);
        // A lower lookout higher up the trunk, reached by a ladder (scenery).
        platform(-7, -3, 0, 3, 11);
        b.ladder(-2, 5, 10, 2, Direction.SOUTH);
        b.set(-2, 11, 2, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH));
        // Campfire and bedrolls under the platform: the rangers live here.
        b.campfire(-12, 0, -8, false);
        b.set(-13, 0, -10, Blocks.GREEN_CARPET);
        b.set(-11, 0, -10, Blocks.GREEN_CARPET);
        b.set(-10, 0, -7, Builder.facing(Blocks.BARREL, Direction.UP));
    }

    /** Platforms either side of the ravine and the walkway between them, its middle fallen. */
    private void walkway() {
        platform(14, 6, 18, 11, 3, 14, 18);
        stairEast(10, 8, 4);
        platform(34, 6, 38, 11, 3, 34, 38);
        stairWest(42, 8, 4);
        for (int x = 19; x <= 33; x++)
            for (int z = 8; z <= 9; z++) {
                if (x >= GAP_W && x <= GAP_E) continue;
                b.set(x, 3, z, Blocks.SPRUCE_PLANKS);
                b.set(x, 4, z == 8 ? 7 : 10, Blocks.SPRUCE_FENCE);
            }
        // Rope-hung supports where the walkway leaves the platforms.
        for (int x : new int[] {21, 30}) for (int z : new int[] {7, 10}) b.fill(x, 4, z, x, 6, z, Blocks.SPRUCE_FENCE);
        // The broken planks hang down into the ravine, the rest lie in the water.
        b.set(GAP_W, 2, 8, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, true));
        b.set(GAP_E, 2, 9, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, true));
        b.set(25, -4, 8, Blocks.SPRUCE_PLANKS);
        b.set(26, -4, 10, Blocks.SPRUCE_SLAB);
        b.set(24, -4, 7, Blocks.SPRUCE_FENCE);
    }

    /** The eastern grove: roots heaved up round the ancient stone, broken ground about it. */
    private void grove() {
        for (int dx = -3; dx <= 3; dx++)
            for (int dz = -3; dz <= 3; dz++) {
                int x = 36 + dx, z = -14 + dz;
                double d = Math.hypot(dx, dz);
                if (d > 3.5) continue;
                b.set(x, -1, z, d < 1.5 ? Blocks.MUD : hash(x, z) % 2 == 0 ? Blocks.ROOTED_DIRT : Blocks.COARSE_DIRT);
                b.set(x, 0, z, Blocks.AIR);
                if (d >= 2 && hash(z, x) % 3 == 0) b.set(x, 0, z, Blocks.MANGROVE_ROOTS);
            }
        // The stone itself is the scene's clue block (36, 0, -14); old masonry shows round it.
        b.set(36, -1, -14, Blocks.CHISELED_STONE_BRICKS);
        b.set(35, 0, -15, Blocks.MOSSY_STONE_BRICK_SLAB);
        b.set(37, 0, -13, Blocks.CRACKED_STONE_BRICKS);
        b.set(37, -1, -15, Blocks.MOSSY_STONE_BRICKS);
        b.set(35, -1, -13, Blocks.MOSSY_STONE_BRICKS);
        // Ranger lanterns on posts mark the dig.
        for (int[] p : new int[][] {{32, -18}, {40, -10}}) {
            b.fill(p[0], 0, p[1], p[0], 1, p[1], Blocks.SPRUCE_FENCE);
            b.set(p[0], 2, p[1], Blocks.LANTERN);
        }
    }

    /** The watchtower east of the grove: log corners, a ladder inside, a roofed lookout on top. */
    private void watchtower() {
        int x0 = 38, z0 = -34, x1 = 42, z1 = -30, top = 11;
        for (int y = 0; y < top; y++)
            for (int x = x0; x <= x1; x++)
                for (int z = z0; z <= z1; z++) {
                    boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                    boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                    if (corner) b.set(x, y, z, Blocks.SPRUCE_LOG);
                    else if (edge && (y % 4 == 3)) b.set(x, y, z, Blocks.STRIPPED_SPRUCE_LOG);
                    else if (edge && y > 2 && hash(x + y, z) % 4 == 0) b.set(x, y, z, Blocks.SPRUCE_PLANKS);
                }
        // The way in on the east side, by the path, and the ladder up the inside of the north wall.
        b.set(x1, 0, -32, Blocks.AIR);
        b.set(x1, 1, -32, Blocks.AIR);
        b.ladder(40, 0, top, -33, Direction.SOUTH);
        for (int x = x0 - 1; x <= x1 + 1; x++)
            for (int z = z0 - 1; z <= z1 + 1; z++) {
                if (x == 40 && z == -33) continue;
                b.set(x, top, z, Blocks.SPRUCE_PLANKS);
                boolean edge = x == x0 - 1 || x == x1 + 1 || z == z0 - 1 || z == z1 + 1;
                if (edge) b.set(x, top + 1, z, Blocks.SPRUCE_FENCE);
                b.set(x, top + 4, z, Blocks.DARK_OAK_SLAB);
            }
        for (int[] c : new int[][] {{x0 - 1, z0 - 1}, {x0 - 1, z1 + 1}, {x1 + 1, z0 - 1}, {x1 + 1, z1 + 1}})
            b.fill(c[0], top + 1, c[1], c[0], top + 3, c[1], Blocks.SPRUCE_FENCE);
        // The record shelf: a lectern (the scene's clue block at 40, 12, -31) and a lantern.
        b.set(42, 12, -34, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(40, top + 3, -30, Builder.hangingLantern());
        b.set(38, 12, -30, Blocks.GREEN_BANNER);
    }

    /** Ordinary forest round the great trees, kept off the paths, platforms and the grove. */
    private void forest() {
        for (int x = X0 + 2; x <= X1 - 2; x += 3)
            for (int z = Z0 + 2; z <= Z1 - 2; z += 3) {
                int h = hash(x, z);
                if (h % 5 != 0) continue;
                int tx = x + h % 2, tz = z + (h >> 3) % 2;
                if (ravine(tx) || ravine(tx + 1) || ravine(tx - 1)) continue;
                if (Math.abs(tz) <= 3 && tx < -12) continue; // the entrance path
                if (tx >= -18 && tx <= 44 && tz >= -8 && tz <= 13) continue; // platforms, stairs, walkway
                if (tx >= 30 && tx <= 44 && tz >= -36 && tz <= -6) continue; // grove and tower
                if (!b.get(tx, 0, tz).isAir() && !b.get(tx, 0, tz).canBeReplaced()) continue;
                if (!b.get(tx, 1, tz).isAir()) continue;
                switch (h % 4) {
                    case 0 -> b.spruce(tx, 0, tz, 7 + h % 4);
                    case 1 -> b.broadleaf(tx, 0, tz, 6, Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES);
                    default -> b.broadleaf(tx, 0, tz, 5 + h % 2, Blocks.OAK_LOG, Blocks.OAK_LEAVES);
                }
            }
        for (int i = 0; i < 40; i++) {
            int x = X0 + hash(i, 7) % (X1 - X0), z = Z0 + hash(7, i) % (Z1 - Z0);
            if (ravine(x) || (Math.abs(z) <= 2 && x < -12)) continue;
            if (b.get(x, 0, z).isAir() || b.get(x, 0, z).canBeReplaced()) b.bush(x, 0, z);
        }
    }
}
