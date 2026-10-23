package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Sunspire, the desert capital of Solkar (Scene 014), anchored at MC-001 block (520, 68, 420);
 * everything here is relative to it, with y = 0 the street. The terrain generator makes a flat
 * desert plateau here (top solid block at y -1 out to radius 100), so the city sits on it and its
 * outskirts blend back into natural desert with dunes, rocks and palms out to radius {@link #R}.
 *
 * A square walled city (walls x and z -58..58, two blocks thick). The west gate (x -58..-57,
 * z -3..3) between two gate towers lets the desert road in (the party arrives at (-72, 0, 0)).
 * The west avenue (z -3..3) runs east to the round plaza (radius 15) round the great Sunspire
 * tower at the origin (x and z -4..4, crown of glass and gold up to y 62, with a balcony at y 46).
 * East of the plaza the covered market (x 16..56, z -7..7) stands under a striped canopy. South
 * of the plaza lies the oasis (pool centred (0, 32), radius 8) with palms, its garden plots at
 * x -12..-2, z 49..54 and a camel pen at x 49..55, z 10..30. Furnished flat-roofed houses with
 * rooftop terraces fill the four quarters.
 *
 * North of the plaza is the House of Records (x -21..21, z -56..-22, entrance at x -2..2 on the
 * south side, behind a portico). Inside: the vestibule (z -27..-23, Sahir waits at (0, 0, -26));
 * the archive stacks on the west (bookshelf rows x -20..-15 at z -53, -49 ... -29) with the three
 * catalogue lecterns at (-17, 0, -51), (-17, 0, -43) and (-17, 0, -35); the great floor map
 * (cells x -12..11, z -45..-29 at y -1, frame one block outside) where map cell (dx, dz) is block
 * (dx, -1, dz + {@link #MAP_Z}); the Builder inscription in the east wall (glyphs at x 21, y 1,
 * z -51 hour, -48 open, -45 only, -42 circle); the key tablet in the north wall (x 12..20, z -56);
 * the water-stained warning page at (18, 0, -33) by the cistern; the manuscript lectern at
 * (14, 0, -40); and Sahir's study (x -6..6, z -56..-50, door at (0, 0, -50)) with the sealed crate
 * at (3, 0, -54). Clue blocks are placed by SceneWorld after the build; positions the scene
 * checks are in its clues and its BLOCKS stage (torches on the Solkar, Veyrath and Thalassia
 * emblems at (10, 0, -31), (-11, 0, -40) and (1, 0, -30)), so they must match
 * tools/story/build_s014.py.
 */
public final class Sunspire {
    /** Radius of the site: everything is built inside it, and the dunes fade out at its edge. */
    public static final int R = 92;
    /** The city wall's outer line (the wall is two blocks thick, inside it is 57). */
    public static final int WALL = 58;
    /** Map cell (dx, dz) is block (dx, -1, dz + MAP_Z). */
    public static final int MAP_Z = -39;
    public static final int MAP_X0 = -12, MAP_X1 = 11, MAP_DZ0 = -6, MAP_DZ1 = 10;
    /** Each civilisation's landmark on the map, in the order Eldervale, Sylvaris, Stonecrown, Solkar, Veyrath, Thalassia. */
    public static final int[][] EMBLEM_CELLS = {{-3, 3}, {4, 1}, {-4, -4}, {10, 8}, {-11, -1}, {1, 9}};
    public static final Block[] EMBLEMS = {Blocks.CHISELED_STONE_BRICKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.CHISELED_DEEPSLATE,
            Blocks.CHISELED_SANDSTONE, Blocks.CHISELED_NETHER_BRICKS, Blocks.DARK_PRISMARINE};
    /** Awakening Valley's mark, where the six lines meet. */
    public static final int VALLEY_DX = 0, VALLEY_DZ = -2;
    /** The Builder inscription: glyph z positions on the east wall (x 21, y 1). */
    public static final int GLYPH_X = 21;
    public static final int[] GLYPH_Z = {-51, -48, -45, -42};
    public static final Block[] GLYPHS = {Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.CHISELED_TUFF, Blocks.CHISELED_DEEPSLATE,
            Blocks.CHISELED_TUFF_BRICKS};
    /** The sealed crate in Sahir's study. */
    public static final int CRATE_X = 3, CRATE_Z = -54;

    /** Spots where characters stand, the party arrives or the player places torches: kept clear. */
    private static final int[][] SPOTS = {
            {-72, 0}, {-73, 2}, {-73, -2}, {-56, 0}, {-17, 0}, {0, -19}, {0, -26}, {0, -24}, {-14, -43}, {-14, -38},
            {19, -46}, {17, -50}, {17, -33}, {16, -31}, {15, -40}, {15, -43}, {3, -27}, {0, -52}, {-3, -53},
            {-14, -49}, {-14, -30}, {-8, -48}, {-16, -25}, {5, 42}, {27, -5}, {41, 5}, {12, -6}, {32, 0}, {-12, 30},
            {-40, 1}, {-12, 5}, {7, -17}, {-52, 4}, {-6, 47}};

    private final Builder b;
    private final boolean[][] reserved = new boolean[2 * R + 1][2 * R + 1];

    private Sunspire(ServerLevel level, BlockPos anchor) {
        this.b = new Builder(level, anchor, 1014L);
    }

    private static int hash(int x, int z) {
        int h = x * 73_856_093 ^ z * 19_349_663;
        return (h ^ (h >>> 13)) & 0x7fffffff;
    }

    public static void build(ServerLevel level, BlockPos anchor) {
        Sunspire s = new Sunspire(level, anchor);
        s.land();
        s.outskirts();
        s.walls();
        s.streets();
        s.plaza();
        s.tower();
        s.records();
        s.market();
        s.oasis();
        s.houses();
        s.pen();
        s.lamps();
        s.clearSpots();
    }

    // ------------------------------------------------------------------ helpers

    private void reserve(int x0, int z0, int x1, int z1) {
        for (int x = Math.max(-R, x0); x <= Math.min(R, x1); x++)
            for (int z = Math.max(-R, z0); z <= Math.min(R, z1); z++) reserved[x + R][z + R] = true;
    }

    private boolean reserved(int x, int z) {
        return x < -R || x > R || z < -R || z > R || reserved[x + R][z + R];
    }

    private static int ring(int x, int z) {
        return Math.max(Math.abs(x), Math.abs(z));
    }

    private static BlockState wallBanner(Block banner, Direction facing) {
        return banner.defaultBlockState().setValue(WallBannerBlock.FACING, facing);
    }

    /** A date palm: a jungle-log trunk and drooping fronds. */
    private void palm(int x, int y, int z, int height) {
        for (int i = 0; i < height; i++) b.set(x, y + i, z, Blocks.JUNGLE_LOG);
        int t = y + height;
        BlockState leaf = Builder.leaves(Blocks.JUNGLE_LEAVES);
        b.setIfAir(x, t, z, leaf);
        b.setIfAir(x, t - 1, z, Blocks.JUNGLE_LOG.defaultBlockState());
        for (Direction d : Direction.Plane.HORIZONTAL) {
            int dx = d.getStepX(), dz = d.getStepZ();
            b.setIfAir(x + dx, t, z + dz, leaf);
            b.setIfAir(x + 2 * dx, t, z + 2 * dz, leaf);
            b.setIfAir(x + 3 * dx, t - 1, z + 3 * dz, leaf);
            if (hash(x + dx, z + dz) % 2 == 0) b.setIfAir(x + 3 * dx, t - 2, z + 3 * dz, leaf);
        }
        for (int dx = -1; dx <= 1; dx += 2)
            for (int dz = -1; dz <= 1; dz += 2) {
                b.setIfAir(x + dx, t, z + dz, leaf);
                b.setIfAir(x + 2 * dx, t - 1, z + 2 * dz, leaf);
            }
        b.setIfAir(x, t + 1, z, leaf);
    }

    /** A street lamp: a sandstone post with a lantern. */
    private void lampPost(int x, int z) {
        b.set(x, 0, z, Blocks.SANDSTONE_WALL);
        b.set(x, 1, z, Blocks.SANDSTONE_WALL);
        b.set(x, 2, z, Blocks.LANTERN);
    }

    // ------------------------------------------------------------------ ground

    /** Levels the city and its surroundings: paving inside the walls, sand outside, air above. */
    private void land() {
        for (int x = -R; x <= R; x++)
            for (int z = -R; z <= R; z++) {
                if (Math.hypot(x, z) > R + 0.5) continue;
                int h = hash(x, z);
                b.fill(x, -4, z, x, -2, z, Blocks.SANDSTONE);
                if (ring(x, z) <= 56) {
                    Block top = h % 13 == 0 ? Blocks.SAND : h % 5 == 0 ? Blocks.SANDSTONE : h % 11 == 0 ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE;
                    b.set(x, -1, z, top);
                } else {
                    b.set(x, -1, z, Blocks.SAND);
                }
                b.fill(x, 0, z, x, 30, z, Blocks.AIR);
            }
    }

    private static double clamp(double v) {
        return Math.max(0, Math.min(1, v));
    }

    /** Dune height outside the walls: none at the walls, the road or the site's edge. */
    private static int dune(int x, int z) {
        double r = Math.hypot(x, z);
        double env = clamp((ring(x, z) - 59) / 6.0) * clamp((R - 2 - r) / 10.0);
        if (x < -56) env *= clamp((Math.abs(z) - 3) / 6.0);
        double n = 0.5 + 0.5 * Math.sin(x / 9.0 + 1.3 * Math.cos(z / 13.0)) * Math.cos(z / 11.0 - x / 23.0);
        return (int) Math.round(env * n * 4.0);
    }

    /** Dunes, rocks, dry brush and palms outside the walls, and the desert road to the west gate. */
    private void outskirts() {
        for (int x = -R; x <= R; x++)
            for (int z = -R; z <= R; z++) {
                if (Math.hypot(x, z) > R + 0.5 || ring(x, z) <= WALL) continue;
                int hgt = dune(x, z);
                if (hgt > 0) b.fill(x, 0, z, x, hgt - 1, z, Blocks.SAND);
            }
        // The road from the west.
        for (int x = -R; x <= -59; x++)
            for (int z = -2; z <= 2; z++) {
                if (Math.hypot(x, z) > R + 0.5) continue;
                int h = hash(x, z);
                b.set(x, -1, z, Math.abs(z) == 2 && h % 2 == 0 ? Blocks.SAND : h % 4 == 0 ? Blocks.SMOOTH_SANDSTONE : Blocks.SANDSTONE);
            }
        for (int x = -88; x <= -64; x += 12)
            for (int z : new int[] {-4, 4}) lampPost(x, z);
        // Decoration on the dunes.
        for (int x = -R; x <= R; x++)
            for (int z = -R; z <= R; z++) {
                double r = Math.hypot(x, z);
                if (r > R - 3 || ring(x, z) <= WALL + 3 || (x < -58 && Math.abs(z) <= 4)) continue;
                int h = hash(x, z), y = dune(x, z);
                if (h % 151 == 0) {
                    // A weathered rock.
                    b.set(x, y, z, Blocks.SANDSTONE);
                    b.setIfAir(x + 1, y, z, Blocks.SMOOTH_SANDSTONE.defaultBlockState());
                    b.setIfAir(x, y, z + 1, (h % 3 == 0 ? Blocks.TERRACOTTA : Blocks.SANDSTONE).defaultBlockState());
                    b.setIfAir(x, y + 1, z, Blocks.SANDSTONE_SLAB.defaultBlockState());
                } else if (h % 263 == 0 && r < R - 8) {
                    palm(x, y, z, 5 + h % 3);
                } else if (h % 29 == 0) {
                    b.setIfAir(x, y, z, Blocks.DEAD_BUSH.defaultBlockState());
                } else if (h % 89 == 0 && y == dune(x + 1, z) && y == dune(x - 1, z) && y == dune(x, z + 1) && y == dune(x, z - 1)
                        && b.get(x + 1, y, z).isAir() && b.get(x - 1, y, z).isAir() && b.get(x, y, z + 1).isAir() && b.get(x, y, z - 1).isAir()) {
                    int tall = 1 + h % 3;
                    for (int i = 0; i < tall; i++) b.set(x, y + i, z, Blocks.CACTUS);
                }
            }
        // Palms along the road outside the gate.
        for (int[] p : new int[][] {{-66, 6}, {-70, -6}, {-79, 7}, {-83, -6}, {-63, -9}}) palm(p[0], dune(p[0], p[1]), p[1], 6 + hash(p[0], p[1]) % 2);
    }

    // ------------------------------------------------------------------ walls

    private void walls() {
        for (int x = -WALL; x <= WALL; x++)
            for (int z = -WALL; z <= WALL; z++) {
                int r = ring(x, z);
                if (r < WALL - 1) continue;
                int h = hash(x, z);
                b.fill(x, -3, z, x, -1, z, Blocks.SANDSTONE);
                b.set(x, 0, z, Blocks.CUT_SANDSTONE);
                for (int y = 1; y <= 5; y++) b.set(x, y, z, h % 7 == 0 ? Blocks.SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                b.set(x, 6, z, r == WALL ? Blocks.ORANGE_TERRACOTTA : Blocks.SMOOTH_SANDSTONE);
                b.set(x, 7, z, Blocks.CUT_SANDSTONE);
                if (r == WALL && ((x + z) & 1) == 0) b.set(x, 8, z, Blocks.CUT_SANDSTONE);
                if (r == WALL - 1 && Math.floorMod(x + z, 10) == 0) b.set(x, 8, z, Blocks.LANTERN);
            }
        // Torches on the inner face, so the alleys behind the houses stay lit.
        for (int i = -54; i <= 54; i += 8) {
            wallTorch(-56, i, Direction.EAST);
            wallTorch(56, i, Direction.WEST);
            wallTorch(i, -56, Direction.SOUTH);
            wallTorch(i, 56, Direction.NORTH);
        }
        // The west gate.
        for (int x = -58; x <= -57; x++) {
            for (int z = -3; z <= 3; z++) {
                b.fill(x, 0, z, x, 4, z, Blocks.AIR);
                b.set(x, -1, z, Math.abs(z) == 3 ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
            }
            for (int z = -2; z <= 2; z++) b.set(x, 5, z, Blocks.AIR);
            b.set(x, 5, -3, Builder.stairs(Blocks.SANDSTONE_STAIRS, Direction.SOUTH, true));
            b.set(x, 5, 3, Builder.stairs(Blocks.SANDSTONE_STAIRS, Direction.NORTH, true));
            b.set(x, 6, 0, Blocks.GOLD_BLOCK);
            b.set(x, 6, -1, Blocks.CHISELED_SANDSTONE);
            b.set(x, 6, 1, Blocks.CHISELED_SANDSTONE);
        }
        for (int side : new int[] {-1, 1}) {
            gateTower(-60, side > 0 ? 4 : -9);
            b.set(-61, 7, side * 5, wallBanner(Blocks.BLACK_WALL_BANNER, Direction.WEST));
            b.set(-61, 7, side * 8, wallBanner(Blocks.YELLOW_WALL_BANNER, Direction.WEST));
            b.set(-54, 5, side * 6, wallBanner(Blocks.YELLOW_WALL_BANNER, Direction.EAST));
            lampPost(-62, side * 4);
            lampPost(-54, side * 4);
        }
        for (int cx : new int[] {-WALL, WALL})
            for (int cz : new int[] {-WALL, WALL}) cornerTower(cx, cz);
    }

    private void wallTorch(int x, int z, Direction facing) {
        if (b.get(x, 3, z).isAir()) b.set(x, 3, z, Builder.facing(Blocks.WALL_TORCH, facing));
    }

    /** A gate tower, x0..x0+5 by z0..z0+5. */
    private void gateTower(int x0, int z0) {
        for (int x = x0; x <= x0 + 5; x++)
            for (int z = z0; z <= z0 + 5; z++) {
                boolean edge = x == x0 || x == x0 + 5 || z == z0 || z == z0 + 5;
                b.fill(x, -3, z, x, -1, z, Blocks.SANDSTONE);
                for (int y = 0; y <= 10; y++) {
                    Block s = y == 0 || y == 10 ? Blocks.CUT_SANDSTONE : y == 8 && edge ? Blocks.ORANGE_TERRACOTTA : Blocks.SMOOTH_SANDSTONE;
                    b.set(x, y, z, s);
                }
                if (edge && ((x + z) & 1) == 0) b.set(x, 11, z, Blocks.CUT_SANDSTONE);
            }
        b.set(x0 + 2, 11, z0 + 2, Blocks.LANTERN);
        b.set(x0 + 3, 11, z0 + 3, Blocks.YELLOW_BANNER);
        // Arrow slits.
        b.set(x0, 5, z0 + 2, Blocks.CHISELED_SANDSTONE);
        b.set(x0, 5, z0 + 3, Blocks.CHISELED_SANDSTONE);
    }

    private void cornerTower(int cx, int cz) {
        for (int x = cx - 3; x <= cx + 3; x++)
            for (int z = cz - 3; z <= cz + 3; z++) {
                boolean edge = Math.abs(x - cx) == 3 || Math.abs(z - cz) == 3;
                b.fill(x, -3, z, x, -1, z, Blocks.SANDSTONE);
                for (int y = 0; y <= 10; y++) {
                    Block s = y == 0 || y == 10 ? Blocks.CUT_SANDSTONE : y == 8 && edge ? Blocks.ORANGE_TERRACOTTA : Blocks.SMOOTH_SANDSTONE;
                    b.set(x, y, z, s);
                }
                if (edge && ((x + z) & 1) == 0) b.set(x, 11, z, Blocks.CUT_SANDSTONE);
            }
        b.set(cx, 11, cz, Blocks.GOLD_BLOCK);
        b.set(cx, 12, cz, Blocks.LANTERN);
    }

    // ------------------------------------------------------------------ streets and plaza

    private void avenue(int x0, int z0, int x1, int z1, boolean alongX) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                int across = alongX ? z - z0 : x - x0, width = alongX ? z1 - z0 : x1 - x0, along = alongX ? x : z;
                Block s;
                if (across == 0 || across == width) s = Blocks.CUT_SANDSTONE;
                else if (across == width / 2 && Math.floorMod(along, 6) == 0) s = Blocks.ORANGE_TERRACOTTA;
                else if (across == width / 2 && Math.floorMod(along, 6) == 3) s = Blocks.CHISELED_SANDSTONE;
                else s = ((x + z) & 1) == 0 ? Blocks.SMOOTH_SANDSTONE : Blocks.SANDSTONE;
                b.set(x, -1, z, s);
            }
    }

    private void streets() {
        avenue(-56, -3, -15, 3, true);   // west avenue from the gate
        avenue(15, -3, 56, 3, true);     // market street
        avenue(-3, -21, 3, -15, false);  // to the House of Records
        avenue(-3, 15, 3, 20, false);    // to the oasis
    }

    private void plaza() {
        for (int x = -16; x <= 16; x++)
            for (int z = -16; z <= 16; z++) {
                double d = Math.hypot(x, z);
                if (d > 15.5) continue;
                Block s;
                if (d > 14.5) s = Blocks.CUT_SANDSTONE;
                else if (((int) d) % 4 == 0) s = Blocks.ORANGE_TERRACOTTA;
                else if (((int) (Math.atan2(z, x) * 8 / Math.PI) & 1) == 0) s = Blocks.SMOOTH_SANDSTONE;
                else s = Blocks.SANDSTONE;
                b.set(x, -1, z, s);
            }
        // Palms in planters and benches round the edge.
        for (int[] p : new int[][] {{-10, -10}, {10, -10}, {-10, 10}, {10, 10}}) {
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++) b.set(p[0] + dx, 0, p[1] + dz, Builder.slab(Blocks.CUT_SANDSTONE_SLAB, false));
            b.set(p[0], -1, p[1], Blocks.GRASS_BLOCK);
            b.set(p[0], 0, p[1], Blocks.AIR);
            palm(p[0], 0, p[1], 7);
            reserve(p[0] - 1, p[1] - 1, p[0] + 1, p[1] + 1);
        }
        for (int[] s : new int[][] {{-6, -13, 0}, {6, -13, 0}, {-6, 13, 2}, {6, 13, 2}, {-13, -6, 3}, {13, 6, 1}}) {
            Direction d = Direction.from2DDataValue(s[2]);
            b.set(s[0], 0, s[1], Builder.stairs(Blocks.SMOOTH_SANDSTONE_STAIRS, d, false));
            reserve(s[0], s[1], s[0], s[1]);
        }
    }

    // ------------------------------------------------------------------ the Sunspire

    private void tower() {
        reserve(-7, -7, 7, 7);
        // The stepped plinth.
        for (int x = -6; x <= 6; x++)
            for (int z = -6; z <= 6; z++) {
                int r = ring(x, z);
                if (r >= 5 && Math.abs(x) + Math.abs(z) <= 10) b.set(x, 0, z, Builder.slab(Blocks.SMOOTH_SANDSTONE_SLAB, false));
            }
        // The shaft, 9 x 9 with cut corners, hollow, up to y 44.
        for (int x = -4; x <= 4; x++)
            for (int z = -4; z <= 4; z++) {
                if (Math.abs(x) == 4 && Math.abs(z) == 4) continue;
                int r = ring(x, z);
                b.set(x, -1, z, Blocks.SMOOTH_SANDSTONE);
                for (int y = 0; y <= 44; y++) {
                    if (r < 4) {
                        b.set(x, y, z, Blocks.AIR);
                        continue;
                    }
                    boolean cornerColumn = Math.abs(x) + Math.abs(z) == 7;
                    boolean faceCentre = x == 0 || z == 0;
                    Block s;
                    if (cornerColumn) s = y % 8 == 0 ? Blocks.CHISELED_SANDSTONE : Blocks.CUT_SANDSTONE;
                    else if (y % 8 == 7) s = Blocks.ORANGE_TERRACOTTA;
                    else if (faceCentre && y % 10 == 5) s = Blocks.GLOWSTONE;
                    else if (faceCentre && y % 8 >= 3 && y % 8 <= 4 && y > 4) s = Blocks.GLASS_PANE;
                    else s = Blocks.SMOOTH_SANDSTONE;
                    b.set(x, y, z, s);
                }
            }
        b.door(0, 0, 4, Blocks.ACACIA_DOOR, Direction.SOUTH);
        b.set(0, 2, 4, Blocks.CHISELED_SANDSTONE);
        b.set(-1, 2, 5, Builder.facing(Blocks.WALL_TORCH, Direction.SOUTH));
        b.set(1, 2, 5, Builder.facing(Blocks.WALL_TORCH, Direction.SOUTH));
        b.ladder(0, 0, 45, -3, Direction.SOUTH);
        b.set(2, 0, 2, Blocks.LANTERN);
        b.set(-2, 0, 2, Builder.facing(Blocks.BARREL, Direction.UP));
        // The balcony ring and the crown room floor at y 45.
        for (int x = -5; x <= 5; x++)
            for (int z = -5; z <= 5; z++) {
                int r = ring(x, z);
                if (Math.abs(x) + Math.abs(z) > 9) continue;
                if (r >= 4) {
                    b.set(x, 45, z, Blocks.CUT_SANDSTONE);
                    if (r == 5) b.set(x, 46, z, Blocks.SANDSTONE_WALL);
                } else if (!(x == 0 && z == -3)) {
                    b.set(x, 45, z, Blocks.SMOOTH_SANDSTONE);
                }
            }
        // The crown room: 7 x 7 with open arches on every side.
        for (int x = -3; x <= 3; x++)
            for (int z = -3; z <= 3; z++) {
                int r = ring(x, z);
                for (int y = 46; y <= 50; y++) {
                    if (r < 3) {
                        b.set(x, y, z, Blocks.AIR);
                        continue;
                    }
                    boolean arch = y <= 48 && (Math.abs(x) <= 1 || Math.abs(z) <= 1);
                    b.set(x, y, z, arch ? Blocks.AIR.defaultBlockState() : (y == 50 ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE).defaultBlockState());
                }
                if (r == 3) b.set(x, 51, z, Blocks.GOLD_BLOCK);
            }
        b.set(0, 46, 2, Blocks.LANTERN);
        for (int[] c : new int[][] {{-5, -4}, {5, -4}, {-5, 4}, {5, 4}}) b.set(c[0], 47, c[1], Blocks.LANTERN);
        // The glass and gold dome.
        int[] radius = {3, 3, 2, 2, 1, 0};
        int prev = 3;
        for (int i = 0; i < radius.length; i++) {
            int y = 52 + i, rr = radius[i];
            for (int x = -3; x <= 3; x++)
                for (int z = -3; z <= 3; z++) {
                    int r = ring(x, z);
                    if (r > prev) continue;
                    if (r < rr) {
                        b.set(x, y, z, Blocks.AIR);
                        continue;
                    }
                    boolean rib = Math.abs(x) == Math.abs(z) || rr == 0;
                    b.set(x, y, z, rib ? Blocks.GOLD_BLOCK : (r == rr && i % 2 == 0 ? Blocks.YELLOW_STAINED_GLASS : Blocks.GLASS));
                }
            prev = rr;
        }
        b.set(0, 56, 0, Blocks.GLOWSTONE);
        b.set(0, 52, 0, Blocks.SEA_LANTERN);
        b.fill(0, 58, 0, 0, 61, 0, Blocks.GOLD_BLOCK);
        b.set(0, 62, 0, Blocks.LIGHTNING_ROD);
    }

    // ------------------------------------------------------------------ the House of Records

    private static final int RX0 = -21, RX1 = 21, RZ0 = -56, RZ1 = -22, RTOP = 10;

    private void records() {
        reserve(RX0 - 1, RZ0 - 1, RX1 + 1, -17);
        for (int x = RX0; x <= RX1; x++)
            for (int z = RZ0; z <= RZ1; z++) {
                boolean edge = x == RX0 || x == RX1 || z == RZ0 || z == RZ1;
                boolean corner = (x == RX0 || x == RX1) && (z == RZ0 || z == RZ1);
                b.set(x, -1, z, ((x + z) & 1) == 0 ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                for (int y = 0; y < RTOP; y++) b.set(x, y, z, edge ? recordsWall(x, y, z, corner) : Blocks.AIR.defaultBlockState());
                boolean skylight = x >= -9 && x <= 9 && z >= -44 && z <= -32;
                b.set(x, RTOP, z, skylight ? Blocks.GLASS : Blocks.SMOOTH_SANDSTONE);
                if (edge && ((x + z) & 1) == 0) b.set(x, RTOP + 1, z, Blocks.CUT_SANDSTONE);
                if (corner) b.set(x, RTOP + 1, z, Blocks.GOLD_BLOCK);
            }
        // The entrance and its portico.
        for (int x = -2; x <= 2; x++) b.fill(x, 0, RZ1, x, 4, RZ1, Blocks.AIR);
        b.set(0, 5, RZ1, Blocks.GOLD_BLOCK);
        for (int x = -9; x <= 9; x++)
            for (int z = -21; z <= -18; z++) {
                b.set(x, -1, z, Blocks.CUT_SANDSTONE);
                b.set(x, 7, z, z == -18 ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
            }
        for (int x : new int[] {-8, -4, 4, 8}) {
            b.fill(x, 0, -18, x, 6, -18, Blocks.CUT_SANDSTONE);
            b.set(x, 0, -18, Blocks.CHISELED_SANDSTONE);
            b.set(x, 6, -18, Blocks.CHISELED_SANDSTONE);
            b.set(x, 7, -18, Blocks.GOLD_BLOCK);
            b.set(x, 4, -17, wallBanner(Math.abs(x) == 4 ? Blocks.BLACK_WALL_BANNER : Blocks.YELLOW_WALL_BANNER, Direction.SOUTH));
        }
        for (int x : new int[] {-6, -2, 2, 6}) b.set(x, 6, -20, Builder.hangingLantern());
        b.sign(0, 6, -21, Blocks.ACACIA_WALL_SIGN, Direction.SOUTH, "", "HOUSE OF", "RECORDS", "");
        for (int x = -2; x <= 2; x++) b.set(x, -1, RZ1, Blocks.CHISELED_SANDSTONE);

        stacks();
        floorMap();
        study();
        keyTablet();
        glyphWall();
        warningCorner();
        manuscriptDesk();
        vestibule();
        // Lanterns hang throughout the hall.
        for (int x = -18; x <= 18; x += 6)
            for (int z : new int[] {-53, -47, -41, -35, -29, -25}) {
                if (Math.abs(x) <= 6 && z <= -50) continue; // above the study
                b.set(x, RTOP - 1, z, Builder.hangingLantern());
            }
    }

    private BlockState recordsWall(int x, int y, int z, boolean corner) {
        if (corner) return (y == RTOP - 1 ? Blocks.CHISELED_SANDSTONE : Blocks.CUT_SANDSTONE).defaultBlockState();
        int along = (z == RZ0 || z == RZ1) ? x - RX0 : z - RZ0;
        if (along % 6 == 0) {
            if (y == RTOP - 1) return Blocks.GOLD_BLOCK.defaultBlockState();
            return (y == 0 || y == RTOP - 2 ? Blocks.CHISELED_SANDSTONE : Blocks.CUT_SANDSTONE).defaultBlockState();
        }
        if (y == 0) return Blocks.CUT_SANDSTONE.defaultBlockState();
        if (y == RTOP - 1) return Blocks.ORANGE_TERRACOTTA.defaultBlockState();
        boolean noWindow = z == RZ0 || (x == RX1 && z >= -54 && z <= -38);
        if (!noWindow && along % 6 == 3 && y >= 4 && y <= 6) return Blocks.GLASS_PANE.defaultBlockState();
        return Blocks.SMOOTH_SANDSTONE.defaultBlockState();
    }

    /** The archive stacks: bookshelf rows on the west side, with the three catalogue lecterns. */
    private void stacks() {
        for (int z = -53; z <= -29; z += 4)
            for (int x = -20; x <= -15; x++)
                for (int y = 0; y <= 4; y++)
                    b.set(x, y, z, hash(x * 7 + y, z) % 6 == 0 ? Blocks.CHISELED_BOOKSHELF : Blocks.BOOKSHELF);
        // Each catalogue lectern (placed with the clues) has its entry's sign above it.
        b.sign(-17, 2, -52, Blocks.ACACIA_WALL_SIGN, Direction.SOUTH, "Catalogue XL", "ASHAR-KEL:", "the sun needle", "of Solkar");
        b.sign(-17, 2, -44, Blocks.ACACIA_WALL_SIGN, Direction.SOUTH, "Catalogue XLI", "VEY-ORUN:", "the ember gate", "of Veyrath");
        b.sign(-17, 2, -36, Blocks.ACACIA_WALL_SIGN, Direction.SOUTH, "Catalogue XLII", "THAL-MERE:", "the tide stair", "of Thalassia");
        // Reading stools and spare volumes in the other aisles.
        for (int z : new int[] {-47, -39, -31}) {
            b.set(-19, 0, z, Builder.stairs(Blocks.ACACIA_STAIRS, Direction.WEST, false));
            b.set(-20, 0, z + 1, Builder.facing(Blocks.BARREL, Direction.UP));
        }
        b.sign(-15, 2, -28, Blocks.ACACIA_WALL_SIGN, Direction.SOUTH, "", "THE STACKS", "Quiet, please.", "");
    }

    /** The colour of the map at cell (dx, dz): sea, snowy peaks, mountains, badlands, desert, forest or meadow. */
    private static Block mapLand(int dx, int dz) {
        if (dz >= 10 || (dz == 9 && (dx < -2 || dx > 5))) return Blocks.LIGHT_BLUE_TERRACOTTA;
        if (dz <= -5 && dx <= 4) return Blocks.WHITE_TERRACOTTA;
        if (dz <= -3 && dx <= 2) return Blocks.GRAY_TERRACOTTA;
        if (dx <= -8) return Blocks.RED_TERRACOTTA;
        if (dx >= 6 && dz >= 4) return Blocks.YELLOW_TERRACOTTA;
        if (dx >= 3) return Blocks.GREEN_TERRACOTTA;
        return Blocks.LIME_TERRACOTTA;
    }

    /**
     * The great floor map of the six lands. Three landmarks the House already knew are marked with
     * lanterns; the player lights the other three (Solkar, Veyrath, Thalassia) with torches.
     */
    private void floorMap() {
        for (int dx = MAP_X0 - 1; dx <= MAP_X1 + 1; dx++)
            for (int dz = MAP_DZ0 - 1; dz <= MAP_DZ1 + 1; dz++) {
                boolean frame = dx < MAP_X0 || dx > MAP_X1 || dz < MAP_DZ0 || dz > MAP_DZ1;
                boolean frameCorner = (dx < MAP_X0 || dx > MAP_X1) && (dz < MAP_DZ0 || dz > MAP_DZ1);
                b.set(dx, -1, dz + MAP_Z, frameCorner ? Blocks.GOLD_BLOCK : frame ? Blocks.CUT_SANDSTONE : mapLand(dx, dz));
            }
        for (int i = 0; i < EMBLEM_CELLS.length; i++) b.set(EMBLEM_CELLS[i][0], -1, EMBLEM_CELLS[i][1] + MAP_Z, EMBLEMS[i]);
        b.set(VALLEY_DX, -1, VALLEY_DZ + MAP_Z, Blocks.LODESTONE);
        for (int i = 0; i < 3; i++) b.set(EMBLEM_CELLS[i][0], 0, EMBLEM_CELLS[i][1] + MAP_Z, Blocks.LANTERN);
        // The scholars' survey table at the north edge of the map.
        b.set(-10, 0, -48, Blocks.CARTOGRAPHY_TABLE);
        b.set(-11, 0, -48, Builder.facing(Blocks.BARREL, Direction.UP));
    }

    /** Sahir's study, behind the great map: desk, bed, books, and the crate from the Eldervale road. */
    private void study() {
        for (int x = -6; x <= 6; x++)
            for (int z = -55; z <= -50; z++) {
                boolean wall = x == -6 || x == 6 || z == -50;
                for (int y = 0; y <= 5; y++)
                    b.set(x, y, z, wall ? (y == 0 ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE).defaultBlockState() : Blocks.AIR.defaultBlockState());
                b.set(x, 6, z, Blocks.SMOOTH_SANDSTONE);
                if (!wall) b.set(x, -1, z, Blocks.ACACIA_PLANKS);
            }
        b.door(0, 0, -50, Blocks.ACACIA_DOOR, Direction.SOUTH);
        b.set(-3, 2, -50, Blocks.GLASS_PANE);
        b.set(3, 2, -50, Blocks.GLASS_PANE);
        b.set(0, 3, -49, wallBanner(Blocks.YELLOW_WALL_BANNER, Direction.SOUTH));
        // Carpet in Solkar's colours.
        for (int x = -2; x <= 2; x++)
            for (int z = -54; z <= -52; z++) b.set(x, 0, z, ((x + z) & 1) == 0 ? Blocks.YELLOW_CARPET : Blocks.BLACK_CARPET);
        // Desk, chair and books.
        b.set(-4, 0, -55, Blocks.ACACIA_PLANKS);
        b.set(-3, 0, -55, Blocks.ACACIA_PLANKS);
        b.set(-4, 1, -55, Blocks.CANDLE);
        b.set(-3, 1, -55, Blocks.LANTERN);
        b.set(-3, 0, -54, Builder.stairs(Blocks.ACACIA_STAIRS, Direction.SOUTH, false));
        b.fill(-5, 0, -55, -5, 3, -52, Blocks.BOOKSHELF);
        b.set(-1, 0, -55, Blocks.CARTOGRAPHY_TABLE);
        b.set(1, 0, -55, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(1, 1, -55, Blocks.POTTED_CACTUS);
        b.bed(5, 0, -55, Blocks.YELLOW_BED, Direction.NORTH);
        b.set(5, 0, -51, Blocks.DECORATED_POT);
        b.set(4, 0, -51, Blocks.POTTED_DEAD_BUSH);
        b.set(-5, 0, -51, Blocks.DECORATED_POT);
        b.set(-1, 3, -55, wallBanner(Blocks.BLACK_WALL_BANNER, Direction.SOUTH));
        b.set(1, 3, -55, wallBanner(Blocks.YELLOW_WALL_BANNER, Direction.SOUTH));
        b.sign(3, 2, -55, Blocks.ACACIA_WALL_SIGN, Direction.SOUTH, "Sealed crate", "from the", "Eldervale road.", "For Sahir.");
        b.set(0, 5, -53, Builder.hangingLantern());
        b.set(CRATE_X, 0, CRATE_Z, Builder.facing(Blocks.BARREL, Direction.UP));
    }

    /** The key tablet in the north wall: each Builder glyph above the word it stands for. */
    private void keyTablet() {
        for (int x = 12; x <= 20; x++) for (int y = 1; y <= 4; y++) b.set(x, y, RZ0, Blocks.POLISHED_TUFF);
        // In the key's order: circle, hour, open, only.
        Block[] glyph = {GLYPHS[3], GLYPHS[0], GLYPHS[1], GLYPHS[2]};
        String[] word = {"CIRCLE", "HOUR", "OPEN", "ONLY"};
        for (int i = 0; i < 4; i++) {
            int x = 13 + 2 * i;
            b.set(x, 2, RZ0, glyph[i]);
            b.sign(x, 1, RZ0 + 1, Blocks.ACACIA_WALL_SIGN, Direction.SOUTH, "The glyph", "above means:", word[i], "");
        }
        b.sign(16, 3, RZ0 + 1, Blocks.ACACIA_WALL_SIGN, Direction.SOUTH, "BUILDER KEY", "Open the circle", "only in its", "hour.");
    }

    /** The Builder inscription in the east wall: four glyphs in a panel of tuff. */
    private void glyphWall() {
        for (int z = -53; z <= -40; z++)
            for (int y = 0; y <= 5; y++) {
                boolean frame = y == 0 || y == 5 || z == -53 || z == -40;
                b.set(GLYPH_X, y, z, frame ? Blocks.POLISHED_DEEPSLATE : Blocks.POLISHED_TUFF);
            }
        for (int i = 0; i < GLYPH_Z.length; i++) b.set(GLYPH_X, 1, GLYPH_Z[i], GLYPHS[i]);
        b.sign(GLYPH_X - 1, 4, -46, Blocks.ACACIA_WALL_SIGN, Direction.WEST, "", "BUILDER", "INSCRIPTION", "");
        for (int z = -53; z <= -40; z++) b.set(GLYPH_X - 1, -1, z, Blocks.POLISHED_DEEPSLATE);
    }

    /** The cistern corner, where an old leak spoiled the warning page. */
    private void warningCorner() {
        for (int x = 16; x <= 20; x++)
            for (int z = -35; z <= -31; z++) {
                int h = hash(x, z);
                if (h % 3 == 0) b.set(x, -1, z, Blocks.BROWN_TERRACOTTA);
                else if (h % 3 == 1) b.set(x, -1, z, Blocks.TERRACOTTA);
            }
        b.set(20, 0, -31, Blocks.WATER_CAULDRON);
        b.set(20, 0, -35, Blocks.CAULDRON);
        b.set(20, 0, -33, Blocks.WET_SPONGE);
        b.set(19, 0, -35, Blocks.DECORATED_POT);
        b.set(RX1 - 1, 4, -33, Builder.facing(Blocks.WALL_TORCH, Direction.WEST));
    }

    /** The manuscript lectern (a clue) beside the old survey, at the east edge of the map. */
    private void manuscriptDesk() {
        b.set(14, 0, -42, Blocks.CARTOGRAPHY_TABLE);
        b.set(14, 0, -38, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(14, 1, -38, Blocks.CANDLE);
    }

    /** The vestibule inside the door: the scholars' desks, pots and banners. */
    private void vestibule() {
        b.set(-18, 0, -25, Blocks.ACACIA_PLANKS);
        b.set(-18, 0, -24, Blocks.ACACIA_PLANKS);
        b.set(-18, 1, -24, Blocks.LANTERN);
        b.set(-18, 1, -25, Blocks.CANDLE);
        b.set(-19, 0, -26, Builder.facing(Blocks.BARREL, Direction.UP));
        for (int[] p : new int[][] {{19, -23}, {20, -23}, {20, -24}, {12, -23}, {-12, -23}})
            b.set(p[0], 0, p[1], hash(p[0], p[1]) % 2 == 0 ? Blocks.DECORATED_POT : Blocks.POTTED_CACTUS);
        for (int x : new int[] {-10, 10}) {
            b.set(x, 5, RZ1 - 1, wallBanner(Blocks.BLACK_WALL_BANNER, Direction.NORTH));
            b.set(x + (x < 0 ? -2 : 2), 5, RZ1 - 1, wallBanner(Blocks.YELLOW_WALL_BANNER, Direction.NORTH));
        }
        b.set(16, 0, -24, Builder.stairs(Blocks.SMOOTH_SANDSTONE_STAIRS, Direction.SOUTH, false));
        b.set(17, 0, -24, Builder.stairs(Blocks.SMOOTH_SANDSTONE_STAIRS, Direction.SOUTH, false));
    }

    // ------------------------------------------------------------------ market

    /** The covered market: stalls either side of the street under one striped canopy. */
    private void market() {
        reserve(15, -8, 56, 8);
        for (int x = 16; x <= 56; x++)
            for (int z = -7; z <= 7; z++)
                b.set(x, 5, z, Math.abs(z) == 7 ? Blocks.BLACK_WOOL : Math.floorMod(z, 4) < 2 ? Blocks.YELLOW_WOOL : Blocks.WHITE_WOOL);
        Block[] goods = {Blocks.MELON, Blocks.HAY_BLOCK, Blocks.PUMPKIN, Blocks.DECORATED_POT};
        Block[] cloth = {Blocks.ORANGE_WOOL, Blocks.YELLOW_WOOL, Blocks.RED_WOOL, Blocks.WHITE_WOOL, Blocks.BROWN_WOOL, Blocks.BLACK_WOOL};
        int n = 0;
        for (int x0 = 18; x0 <= 46; x0 += 7)
            for (int side : new int[] {-1, 1}) {
                n++;
                int front = side * 4, back = side * 7;
                for (int x : new int[] {x0, x0 + 5}) {
                    b.fill(x, 0, front, x, 4, front, Blocks.ACACIA_FENCE);
                    b.fill(x, 0, back, x, 4, back, Blocks.ACACIA_FENCE);
                }
                for (int x = x0 + 1; x <= x0 + 4; x++) {
                    int h = hash(x, n);
                    b.set(x, 0, front, x % 2 == 0 ? Builder.facing(Blocks.BARREL, Direction.UP) : goods[h % goods.length].defaultBlockState());
                    b.set(x, 0, back, Builder.facing(Blocks.BARREL, side < 0 ? Direction.SOUTH : Direction.NORTH));
                    b.set(x, 1, back, cloth[(h + n) % cloth.length]);
                    b.set(x, 2, back, cloth[(h + n + 1) % cloth.length]);
                }
                b.set(x0 + 1, 1, front, Blocks.POTTED_CACTUS);
                b.set(x0 + 3, 1, front, Blocks.CANDLE);
                b.set(x0 + 4, 0, side * 6, Blocks.DECORATED_POT);
                b.set(x0 + 2, 4, side * 5, Builder.hangingLantern());
            }
        for (int x = 18; x <= 54; x += 6) b.set(x, 4, 0, Builder.hangingLantern());
        b.fill(15, 0, -5, 15, 3, -5, Blocks.CUT_SANDSTONE);
        b.sign(14, 2, -5, Blocks.ACACIA_WALL_SIGN, Direction.WEST, "", "THE COVERED", "MARKET", "");
    }

    // ------------------------------------------------------------------ oasis

    private void oasis() {
        int cz = 32;
        for (int x = -22; x <= 15; x++)
            for (int z = 21; z <= 56; z++) {
                double d = Math.hypot(x, z - cz);
                int h = hash(x, z);
                if (d <= 8.5) {
                    boolean deep = d < 5;
                    b.set(x, deep ? -6 : -5, z, Blocks.SANDSTONE);
                    b.set(x, deep ? -5 : -4, z, Blocks.SAND);
                    b.fill(x, deep ? -4 : -3, z, x, -1, z, Blocks.WATER);
                    if (h % 13 == 0 && d > 3) b.set(x, 0, z, Blocks.LILY_PAD);
                    reserve(x, z, x, z);
                } else if (d <= 9.5) {
                    b.set(x, -1, z, Blocks.SAND);
                    if (h % 5 == 0) {
                        b.set(x, 0, z, Blocks.SUGAR_CANE);
                        b.set(x, 1, z, Blocks.SUGAR_CANE);
                        reserve(x, z, x, z);
                    }
                } else if (d <= 13) {
                    b.set(x, -1, z, h % 7 == 0 ? Blocks.COARSE_DIRT : h % 5 == 0 ? Blocks.SAND : Blocks.GRASS_BLOCK);
                    if (h % 4 == 0) b.set(x, 0, z, h % 3 == 0 ? Blocks.FERN : Blocks.SHORT_GRASS);
                } else if (h % 3 == 0) {
                    b.set(x, -1, z, Blocks.SAND);
                }
            }
        for (int[] p : new int[][] {{-10, 24}, {10, 24}, {12, 35}, {-12, 36}, {3, 44}, {-5, 20}, {-16, 45}, {14, 47}, {-18, 27}, {8, 53}}) {
            b.set(p[0], -1, p[1], Blocks.SAND);
            b.set(p[0], 0, p[1], Blocks.AIR);
            palm(p[0], 0, p[1], 6 + hash(p[0], p[1]) % 3);
            reserve(p[0], p[1], p[0], p[1]);
        }
        // Benches facing the water, and the water seller's jars.
        for (int[] s : new int[][] {{-9, 41, 2}, {9, 41, 2}, {-13, 30, 3}, {13, 30, 1}}) {
            b.set(s[0], 0, s[1], Builder.stairs(Blocks.ACACIA_STAIRS, Direction.from2DDataValue(s[2]), false));
            reserve(s[0], s[1], s[0], s[1]);
        }
        for (int[] p : new int[][] {{6, 43}, {4, 43}}) {
            b.set(p[0], 0, p[1], Blocks.DECORATED_POT);
            reserve(p[0], p[1], p[0], p[1]);
        }
        b.set(6, 0, 41, Blocks.WATER_CAULDRON);
        reserve(6, 41, 6, 41);
        // The garden plots, watered from a channel.
        reserve(-14, 48, -1, 56);
        for (int x = -12; x <= -2; x++)
            for (int z = 49; z <= 54; z++) {
                b.set(x, -2, z, Blocks.SANDSTONE);
                if (z == 51) {
                    b.set(x, -1, z, Blocks.WATER);
                    continue;
                }
                b.set(x, -1, z, Blocks.FARMLAND);
                Block crop = x % 3 == 0 ? Blocks.CARROTS : x % 3 == 1 ? Blocks.POTATOES : Blocks.WHEAT;
                b.set(x, 0, z, crop.defaultBlockState().setValue(BlockStateProperties.AGE_7, 3 + hash(x, z) % 5));
            }
        for (int x = -13; x <= -1; x++) {
            b.set(x, 0, 48, Blocks.ACACIA_FENCE);
            b.set(x, 0, 55, Blocks.ACACIA_FENCE);
        }
        for (int z = 48; z <= 55; z++) {
            b.set(-13, 0, z, Blocks.ACACIA_FENCE);
            b.set(-1, 0, z, Blocks.ACACIA_FENCE);
        }
        b.gate(-6, 0, 48, Blocks.ACACIA_FENCE_GATE, Direction.NORTH);
        b.set(-13, 1, 48, Blocks.LANTERN);
        b.set(-1, 1, 55, Blocks.LANTERN);
        b.set(-1, 1, 48, Blocks.LANTERN);
        b.set(-13, 1, 55, Blocks.LANTERN);
        b.set(-3, 0, 47, Blocks.COMPOSTER);
    }

    /** A camel pen in the south-east corner, and a stand of date palms. */
    private void pen() {
        reserve(48, 9, 56, 56);
        for (int x = 49; x <= 55; x++) {
            b.set(x, 0, 10, Blocks.ACACIA_FENCE);
            b.set(x, 0, 30, Blocks.ACACIA_FENCE);
        }
        for (int z = 10; z <= 30; z++) {
            b.set(49, 0, z, Blocks.ACACIA_FENCE);
            b.set(55, 0, z, Blocks.ACACIA_FENCE);
            for (int x = 50; x <= 54; x++) b.set(x, -1, z, hash(x, z) % 3 == 0 ? Blocks.COARSE_DIRT : Blocks.SAND);
        }
        b.gate(49, 0, 20, Blocks.ACACIA_FENCE_GATE, Direction.WEST);
        for (int[] c : new int[][] {{49, 10}, {55, 10}, {49, 30}, {55, 30}}) {
            b.set(c[0], 1, c[1], Blocks.ACACIA_FENCE);
            b.set(c[0], 2, c[1], Blocks.LANTERN);
        }
        b.set(54, 0, 12, Blocks.HAY_BLOCK);
        b.set(53, 0, 12, Blocks.HAY_BLOCK);
        b.set(54, 1, 12, Blocks.HAY_BLOCK);
        b.set(54, 0, 28, Blocks.WATER_CAULDRON);
        b.set(53, 0, 28, Blocks.WATER_CAULDRON);
        b.set(51, 0, 20, Blocks.LANTERN);
        b.animal(EntityType.CAMEL, 52, 0, 16);
        b.animal(EntityType.CAMEL, 52, 0, 24);
        for (int[] p : new int[][] {{52, 36}, {51, 44}, {53, 51}}) {
            b.set(p[0], -1, p[1], Blocks.SAND);
            palm(p[0], 0, p[1], 6 + hash(p[0], p[1]) % 3);
        }
        lampPost(52, 40);
        lampPost(52, 48);
    }

    // ------------------------------------------------------------------ houses

    private void houses() {
        int[] westCols = {-55, -44, -33}, eastNorthCols = {25, 36, 47}, eastSouthCols = {17, 28, 39};
        int[] northRows = {-55, -43, -31, -19}, southRows = {9, 21, 33, 45};
        int i = 0;
        for (int z0 : northRows) {
            for (int x0 : westCols) {
                if (x0 == -33 && z0 == -55) {
                    garden(x0, z0);
                    continue;
                }
                house(x0, z0, Direction.SOUTH, i++);
            }
            for (int x0 : eastNorthCols) house(x0, z0, Direction.SOUTH, i++);
        }
        for (int z0 : southRows) {
            for (int x0 : westCols) house(x0, z0, Direction.NORTH, i++);
            for (int x0 : eastSouthCols) house(x0, z0, Direction.NORTH, i++);
        }
    }

    /** The scholars' walled garden by the House of Records. */
    private void garden(int x0, int z0) {
        reserve(x0, z0, x0 + 8, z0 + 8);
        for (int x = x0; x <= x0 + 8; x++)
            for (int z = z0; z <= z0 + 8; z++) {
                boolean edge = x == x0 || x == x0 + 8 || z == z0 || z == z0 + 8;
                b.set(x, -1, z, edge ? Blocks.CUT_SANDSTONE : hash(x, z) % 4 == 0 ? Blocks.COARSE_DIRT : Blocks.GRASS_BLOCK);
                if (edge && !(z == z0 + 8 && Math.abs(x - (x0 + 4)) <= 1)) b.set(x, 0, z, Blocks.SANDSTONE_WALL);
                else if (!edge && hash(x, z) % 5 == 0) b.set(x, 0, z, Blocks.SHORT_GRASS);
            }
        b.set(x0 + 4, -1, z0 + 4, Blocks.WATER);
        for (Direction d : Direction.Plane.HORIZONTAL) b.set(x0 + 4 + d.getStepX(), 0, z0 + 4 + d.getStepZ(), Builder.slab(Blocks.CUT_SANDSTONE_SLAB, false));
        palm(x0 + 2, 0, z0 + 2, 6);
        palm(x0 + 6, 0, z0 + 6, 5);
        b.set(x0 + 6, 0, z0 + 2, Builder.stairs(Blocks.ACACIA_STAIRS, Direction.NORTH, false));
        b.set(x0 + 2, 0, z0 + 6, Blocks.LANTERN);
        b.set(x0 + 4, 1, z0 + 8, Blocks.AIR);
    }

    private static final Block[] TRIMS = {Blocks.ORANGE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.WHITE_TERRACOTTA,
            Blocks.BROWN_TERRACOTTA, Blocks.TERRACOTTA};
    private static final Block[] CARPETS = {Blocks.YELLOW_CARPET, Blocks.ORANGE_CARPET, Blocks.RED_CARPET, Blocks.BROWN_CARPET,
            Blocks.WHITE_CARPET, Blocks.BLACK_CARPET, Blocks.CYAN_CARPET};
    private static final Block[] AWNINGS = {Blocks.YELLOW_WOOL, Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.RED_WOOL};
    private static final DyeColor[] BEDS = {DyeColor.YELLOW, DyeColor.ORANGE, DyeColor.WHITE, DyeColor.RED, DyeColor.BROWN, DyeColor.CYAN};

    /**
     * A flat-roofed house, x0..x0+8 by z0..z0+8, with its door in the middle of the side facing
     * {@code door}: one furnished room, a ladder up to a rooftop terrace with an awning.
     */
    private void house(int x0, int z0, Direction door, int idx) {
        int x1 = x0 + 8, z1 = z0 + 8, wallTop = 4;
        reserve(x0, z0, x1, z1);
        int seed = hash(x0, z0) + idx;
        Block wall = seed % 3 == 1 ? Blocks.SANDSTONE : Blocks.SMOOTH_SANDSTONE;
        Block trim = TRIMS[seed % TRIMS.length];
        Block floor = seed % 2 == 0 ? Blocks.ACACIA_PLANKS : Blocks.SMOOTH_SANDSTONE;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                b.set(x, -1, z, edge ? Blocks.CUT_SANDSTONE : floor);
                int along = (z == z0 || z == z1) ? x - x0 : z - z0;
                for (int y = 0; y <= wallTop; y++) {
                    Block s = Blocks.AIR;
                    if (corner) s = y == wallTop ? Blocks.CHISELED_SANDSTONE : Blocks.CUT_SANDSTONE;
                    else if (edge) {
                        if (y == 0) s = Blocks.CUT_SANDSTONE;
                        else if (y == wallTop) s = trim;
                        else if ((along == 2 || along == 6) && y <= 2) s = Blocks.GLASS_PANE;
                        else s = wall;
                    }
                    b.set(x, y, z, s);
                }
                b.set(x, wallTop + 1, z, edge ? Blocks.CUT_SANDSTONE : Blocks.SMOOTH_SANDSTONE);
                b.set(x, wallTop + 2, z, Blocks.AIR);
                if (edge) b.set(x, wallTop + 2, z, corner ? Blocks.CUT_SANDSTONE.defaultBlockState()
                        : (along % 2 == 0 ? Blocks.SANDSTONE_WALL.defaultBlockState() : Builder.slab(Blocks.SMOOTH_SANDSTONE_SLAB, false)));
            }
        // The door, a torch beside it and a cloth awning over it.
        int dx = (x0 + x1) / 2, dz = door == Direction.SOUTH ? z1 : z0;
        b.door(dx, 0, dz, seed % 2 == 0 ? Blocks.ACACIA_DOOR : Blocks.JUNGLE_DOOR, door);
        int out = door.getStepZ();
        b.set(dx + 1, 2, dz + out, Builder.facing(Blocks.WALL_TORCH, door));
        Block awning = AWNINGS[seed % AWNINGS.length];
        for (int x = dx - 1; x <= dx + 1; x++) b.set(x, 3, dz + out, awning);

        Room room = new Room(b, new int[] {x0, z0, x1, z1}, door, dx, dz);
        furnish(room, seed);
        // The ladder up through the roof in the back corner.
        int lx = room.worldX(room.width - 1, room.depth - 1), lz = room.worldZ(room.width - 1, room.depth - 1);
        b.ladder(lx, 0, wallTop + 1, lz, room.front);
        terrace(room, seed, wallTop + 2);
    }

    private void furnish(Room r, int seed) {
        Block carpet = CARPETS[seed % CARPETS.length];
        Block bed = Builder.bedOf(BEDS[seed % BEDS.length]);
        r.carpet(2, 2, 4, 4, carpet);
        r.bed(0, 6, bed, r.back);
        r.table(5, 3, r.left);
        r.facing(6, 0, 0, Blocks.BARREL, Direction.UP);
        r.set(0, 0, 0, Blocks.DECORATED_POT);
        r.set(0, 0, 1, seed % 2 == 0 ? Blocks.POTTED_CACTUS : Blocks.POTTED_FERN);
        r.set(6, 0, 1, Blocks.FLOWER_POT);
        int kind = seed % 5;
        switch (kind) {
            case 0 -> { // a family home
                r.bed(2, 6, bed, r.back);
                r.facing(6, 0, 2, Blocks.CHEST, r.left);
                r.set(0, 0, 3, Blocks.DECORATED_POT);
            }
            case 1 -> { // a potter
                r.facing(6, 0, 2, Blocks.FURNACE, r.left);
                r.set(0, 0, 3, Blocks.CLAY);
                r.set(0, 0, 4, Blocks.DECORATED_POT);
                r.set(2, 0, 6, Blocks.DECORATED_POT);
                r.set(3, 0, 6, Blocks.DECORATED_POT);
            }
            case 2 -> { // a weaver
                r.facing(6, 0, 2, Blocks.LOOM, r.left);
                r.set(0, 0, 3, Blocks.YELLOW_WOOL);
                r.set(0, 1, 3, Blocks.WHITE_WOOL);
                r.set(0, 0, 4, Blocks.BLACK_WOOL);
                r.set(2, 0, 6, Builder.facing(Blocks.BARREL, Direction.UP));
            }
            case 3 -> { // a copyist from the House of Records
                r.facing(6, 0, 2, Blocks.LECTERN, r.left);
                r.set(0, 0, 3, Blocks.BOOKSHELF);
                r.set(0, 1, 3, Blocks.BOOKSHELF);
                r.set(0, 0, 4, Blocks.BOOKSHELF);
                r.set(2, 0, 6, Blocks.CARTOGRAPHY_TABLE);
            }
            default -> { // a baker
                r.facing(6, 0, 2, Blocks.SMOKER, r.left);
                r.set(0, 0, 3, Blocks.HAY_BLOCK);
                r.set(0, 0, 4, Builder.facing(Blocks.BARREL, Direction.UP));
                r.set(2, 0, 6, Blocks.CRAFTING_TABLE);
            }
        }
        // A lantern hangs from the ceiling in the middle of the room.
        b.set(r.worldX(3, 3), 4, r.worldZ(3, 3), Builder.hangingLantern());
    }

    /** The rooftop terrace: carpets under a cloth awning, plants, a barrel and a lantern. */
    private void terrace(Room r, int seed, int y) {
        Block carpet = CARPETS[(seed + 3) % CARPETS.length];
        Block awning = AWNINGS[(seed + 1) % AWNINGS.length];
        for (int u = 0; u <= 2; u++)
            for (int v = 2; v <= 4; v++) {
                r.set(u, y, v, carpet);
                r.set(u, y + 2, v, awning);
            }
        for (int[] p : new int[][] {{0, 2}, {2, 2}, {0, 4}, {2, 4}}) {
            r.set(p[0], y, p[1], Blocks.ACACIA_FENCE);
            r.set(p[0], y + 1, p[1], Blocks.ACACIA_FENCE);
        }
        if (seed % 2 == 0) b.bed(r.worldX(1, 4), y, r.worldZ(1, 4), Builder.bedOf(BEDS[(seed + 2) % BEDS.length]), r.back);
        r.set(6, y, 0, Blocks.POTTED_CACTUS);
        r.set(0, y, 6, Blocks.POTTED_DEAD_BUSH);
        r.facing(6, y, 2, Blocks.BARREL, Direction.UP);
        r.set(4, y, 6, Blocks.ACACIA_FENCE);
        r.set(4, y + 1, 6, Blocks.LANTERN);
        if (seed % 3 == 0) r.set(5, y, 3, Blocks.DECORATED_POT);
    }

    // ------------------------------------------------------------------ finishing

    /** Lamp posts on a grid wherever the street is open, so no corner of the city is dark. */
    private void lamps() {
        for (int x = -54; x <= 54; x += 12)
            for (int z = -54; z <= 54; z += 12) {
                if (reserved(x, z)) continue;
                boolean open = true;
                for (int dx = -1; dx <= 1 && open; dx++)
                    for (int dz = -1; dz <= 1 && open; dz++)
                        for (int y = 0; y <= 2; y++) if (!b.get(x + dx, y, z + dz).isAir()) open = false;
                if (open) lampPost(x, z);
            }
    }

    /** Where characters stand and torches go, the ground is solid and the air is clear. */
    private void clearSpots() {
        for (int[] s : SPOTS) {
            BlockState below = b.get(s[0], -1, s[1]);
            if (below.isAir() || below.canBeReplaced()) b.set(s[0], -1, s[1], Blocks.SMOOTH_SANDSTONE);
            b.set(s[0], 0, s[1], Blocks.AIR);
            b.set(s[0], 1, s[1], Blocks.AIR);
        }
        for (int i = 3; i < EMBLEM_CELLS.length; i++) b.set(EMBLEM_CELLS[i][0], 0, EMBLEM_CELLS[i][1] + MAP_Z, Blocks.AIR);
    }

    // ------------------------------------------------------------------ world changes

    /** Scene 014: the decoded inscription glows above each glyph. */
    public static void glyphs(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        for (int z : GLYPH_Z) b.set(GLYPH_X, 2, z, Blocks.PEARLESCENT_FROGLIGHT);
    }

    /** Scene 014: six gold lines inlaid across the map from the landmarks to Awakening Valley. */
    public static void mapLines(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        for (int[] e : EMBLEM_CELLS) {
            int x = e[0], z = e[1], tx = VALLEY_DX, tz = VALLEY_DZ;
            int dx = Math.abs(tx - x), dz = -Math.abs(tz - z), sx = x < tx ? 1 : -1, sz = z < tz ? 1 : -1, err = dx + dz;
            while (!(x == tx && z == tz)) {
                int e2 = 2 * err;
                if (e2 >= dz) {
                    err += dz;
                    x += sx;
                }
                if (e2 <= dx) {
                    err += dx;
                    z += sz;
                }
                if (x == tx && z == tz) break;
                boolean emblem = false;
                for (int[] o : EMBLEM_CELLS) if (o[0] == x && o[1] == z) emblem = true;
                if (!emblem) b.set(x, -1, z + MAP_Z, Blocks.GOLD_BLOCK);
            }
        }
        b.set(VALLEY_DX, -1, VALLEY_DZ + MAP_Z, Blocks.GLOWSTONE);
    }

    /** Scene 014: the sealed crate stands open. */
    public static void crateOpen(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        b.set(CRATE_X, 0, CRATE_Z, Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, Direction.UP).setValue(BarrelBlock.OPEN, true));
    }
}
