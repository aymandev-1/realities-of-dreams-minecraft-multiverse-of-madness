package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Valebridge, the capital of Eldervale (Scenes 012 to 017), anchored at MC-001 block (-115, 67, 105);
 * everything here is relative to it, with y = 0 the street.
 *
 * A walled city (walls at x -54 and 54 and z -48) open to its harbour on the south. The east gate
 * (x 54, z -2..2) lets the trade road in along the main street to a round plaza with a fountain at
 * the origin. North of the plaza stands the royal palace (x -16..16, z -46..-18): the throne at its
 * north end and the long council table (x -2..2, z -36..-28) in the middle. West is the royal
 * archive (x -52..-22, z -16..12, door on the east side): its north room is the research chamber
 * with the six-slot reconstruction table round (-41, 1, -11); its hall holds the catalogue lecterns
 * and the great floor map (x -46..-34, z 1..10). The Solkar envoy's pavilion is south-east of the
 * plaza, and south of the city lies the harbour: a quay (z 26..30), a pier (x -3..3) and the
 * Thalassian ship. Positions the scenes check are in their BLOCKS stages and clues, so they must match.
 */
public final class Valebridge {
    public static final int X0 = -56, X1 = 66, Z0 = -50, Z1 = 56;
    /** The reconstruction table's six slots (y 1) and centre, sunwise from the north. */
    public static final int[][] SLOTS = {{-41, -13}, {-39, -12}, {-39, -10}, {-41, -9}, {-43, -10}, {-43, -12}};
    public static final int TABLE_X = -41, TABLE_Z = -11;
    /** Each civilisation's emblem, in slot order: Eldervale, Sylvaris, Stonecrown, Solkar, Veyrath, Thalassia. */
    public static final Block[] EMBLEMS = {Blocks.CHISELED_STONE_BRICKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.CHISELED_DEEPSLATE,
            Blocks.CHISELED_SANDSTONE, Blocks.CHISELED_NETHER_BRICKS, Blocks.DARK_PRISMARINE};

    private final Builder b;

    private Valebridge(ServerLevel level, BlockPos anchor) {
        this.b = new Builder(level, anchor, 1012L);
    }

    private static int hash(int x, int z) {
        int h = x * 73_856_093 ^ z * 19_349_663;
        return (h ^ (h >>> 13)) & 0x7fffffff;
    }

    public static void build(ServerLevel level, BlockPos anchor) {
        Valebridge v = new Valebridge(level, anchor);
        v.land();
        v.harbour();
        v.walls();
        v.streets();
        v.plaza();
        v.palace();
        v.archive();
        v.pavilion();
        v.market();
        v.houses();
        v.ship();
    }

    private static boolean water(int x, int z) {
        return z >= 31 && x >= -50 && x <= 60;
    }

    private void land() {
        for (int x = X0; x <= X1; x++)
            for (int z = Z0; z <= Z1; z++) {
                int h = hash(x, z);
                if (water(x, z)) {
                    b.fill(x, -16, z, x, -9, z, Blocks.STONE);
                    b.set(x, -8, z, h % 3 == 0 ? Blocks.GRAVEL : Blocks.SAND);
                    b.fill(x, -7, z, x, -2, z, Blocks.WATER);
                    b.fill(x, -1, z, x, 30, z, Blocks.AIR);
                    continue;
                }
                b.fill(x, -16, z, x, -4, z, Blocks.STONE);
                b.fill(x, -3, z, x, -2, z, Blocks.DIRT);
                b.set(x, -1, z, h % 11 == 0 ? Blocks.COARSE_DIRT : Blocks.GRASS_BLOCK);
                b.fill(x, 0, z, x, 30, z, Blocks.AIR);
                if (h % 13 == 0) b.set(x, 0, z, Blocks.SHORT_GRASS);
            }
        // The harbour basin is closed in so the sea stays in the site.
        for (int x = -51; x <= 61; x++) b.fill(x, -16, Z1 + 1, x, -1, Z1 + 1, Blocks.STONE);
        for (int z = 31; z <= Z1 + 1; z++) {
            b.fill(-51, -16, z, -51, -1, z, Blocks.STONE_BRICKS);
            b.fill(61, -16, z, 61, -1, z, Blocks.STONE_BRICKS);
        }
    }

    /** The quay along the city's south side and the pier out into the harbour. */
    private void harbour() {
        for (int x = -54; x <= 60; x++)
            for (int z = 26; z <= 30; z++) {
                b.fill(x, -8, z, x, -2, z, Blocks.STONE_BRICKS);
                b.set(x, -1, z, z == 30 ? Blocks.SMOOTH_STONE : hash(x, z) % 6 == 0 ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS);
            }
        for (int x = -50; x <= 60; x += 8) b.set(x, 0, 30, Blocks.STONE_BRICK_WALL);
        // The pier, on log posts.
        for (int x = -3; x <= 3; x++)
            for (int z = 31; z <= 50; z++) {
                b.set(x, -1, z, (x + z) % 4 == 0 ? Blocks.STRIPPED_SPRUCE_WOOD : Blocks.SPRUCE_PLANKS);
                if (Math.abs(x) == 3 && z % 2 == 0) b.set(x, 0, z, Blocks.SPRUCE_FENCE);
            }
        for (int z = 32; z <= 50; z += 6)
            for (int x : new int[] {-3, 3}) b.fill(x, -8, z, x, -2, z, Blocks.SPRUCE_LOG);
        b.set(-3, 1, 50, Blocks.LANTERN);
        b.set(3, 1, 50, Blocks.LANTERN);
        b.set(3, 0, 50, Blocks.SPRUCE_FENCE);
        b.set(-3, 0, 50, Blocks.SPRUCE_FENCE);
        // The gangplank to the ship.
        for (int x = 4; x <= 6; x++) b.set(x, -1, 41, Blocks.SPRUCE_SLAB.defaultBlockState()
                .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, net.minecraft.world.level.block.state.properties.SlabType.TOP));
        b.set(3, 0, 41, Blocks.AIR);
        // Cargo waiting on the pier and the quay; three damaged crates are the harbour clues.
        for (int[] c : new int[][] {{-1, 34}, {1, 34}, {-1, 35}, {-6, 27}, {-5, 27}, {8, 27}, {9, 27}, {9, 28}})
            b.set(c[0], 0, c[1], Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(2, 0, 49, Blocks.CHEST);
        // The loading crane on the quay: two posts, a beam and a chain. Its broken beam
        // (x -10..-9) and hanging chain (x -8, y 4..5) are what the player repairs.
        b.fill(-12, 0, 28, -12, 6, 28, Blocks.SPRUCE_LOG);
        b.fill(-7, 0, 28, -7, 3, 28, Blocks.SPRUCE_LOG);
        b.set(-11, 6, 28, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.X));
        b.set(-8, 6, 28, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.X));
        b.ladder(-13, 0, 6, 28, Direction.WEST);
        b.set(-10, 0, 29, Blocks.SPRUCE_LOG);
        b.set(-9, 0, 29, Blocks.CHAIN);
        b.set(-8, 0, 27, Builder.facing(Blocks.BARREL, Direction.UP));
    }

    /** City walls with walkway battlements, the east gate and corner towers. */
    private void walls() {
        for (int x = -54; x <= 54; x++) wallColumn(x, -48);
        for (int z = -48; z <= 25; z++) {
            wallColumn(-54, z);
            if (Math.abs(z) > 2) wallColumn(54, z);
        }
        // The east gate's arch.
        for (int z = -3; z <= 3; z++) b.fill(54, 5, z, 54, 6, z, Blocks.STONE_BRICKS);
        b.set(54, 4, -2, Builder.stairs(Blocks.STONE_BRICK_STAIRS, Direction.SOUTH, true));
        b.set(54, 4, 2, Builder.stairs(Blocks.STONE_BRICK_STAIRS, Direction.NORTH, true));
        b.set(55, 3, -3, Blocks.LANTERN);
        b.set(55, 3, 3, Blocks.LANTERN);
        b.fill(55, 0, -3, 55, 2, -3, Blocks.STONE_BRICK_WALL);
        b.fill(55, 0, 3, 55, 2, 3, Blocks.STONE_BRICK_WALL);
        b.set(55, 5, 0, Blocks.BLUE_WALL_BANNER.defaultBlockState().setValue(net.minecraft.world.level.block.WallBannerBlock.FACING, Direction.EAST));
        for (int[] t : new int[][] {{-54, -48}, {54, -48}, {54, -4}, {54, 4}, {-54, 25}, {54, 25}}) tower(t[0], t[1]);
        // The road in from the east.
        for (int x = 55; x <= X1; x++)
            for (int z = -2; z <= 2; z++) b.set(x, -1, z, hash(x, z) % 5 == 0 ? Blocks.COARSE_DIRT : Blocks.DIRT_PATH);
    }

    private void wallColumn(int x, int z) {
        b.fill(x, -2, z, x, 4, z, (hash(x, z) % 7 == 0) ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS);
        if ((x + z) % 2 == 0) b.set(x, 5, z, Blocks.STONE_BRICK_WALL);
    }

    private void tower(int cx, int cz) {
        for (int x = cx - 2; x <= cx + 2; x++)
            for (int z = cz - 2; z <= cz + 2; z++) {
                boolean edge = Math.abs(x - cx) == 2 || Math.abs(z - cz) == 2;
                b.fill(x, -2, z, x, 7, z, edge ? Blocks.STONE_BRICKS.defaultBlockState() : Blocks.AIR.defaultBlockState());
                b.set(x, 7, z, Blocks.STONE_BRICKS);
                if (edge && (x + z) % 2 == 0) b.set(x, 8, z, Blocks.STONE_BRICK_WALL);
            }
        b.set(cx, 9, cz, Blocks.BLUE_BANNER);
        b.set(cx, 8, cz, Blocks.STONE_BRICK_WALL);
    }

    private void pave(int x0, int z0, int x1, int z1) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                int h = hash(x, z);
                b.set(x, -1, z, h % 9 == 0 ? Blocks.CRACKED_STONE_BRICKS : h % 4 == 0 ? Blocks.POLISHED_ANDESITE : Blocks.STONE_BRICKS);
                b.set(x, 0, z, Blocks.AIR);
            }
    }

    private void streets() {
        pave(12, -3, 55, 3);       // main street from the gate
        pave(-3, -18, 3, -10);     // to the palace
        pave(-22, -2, -10, 2);     // to the archive
        pave(-3, 10, 3, 26);       // down to the harbour
        // Street lamps.
        for (int x = 16; x <= 50; x += 8) {
            for (int z : new int[] {-4, 4}) {
                b.fill(x, 0, z, x, 2, z, Blocks.SPRUCE_FENCE);
                b.set(x, 3, z, Blocks.LANTERN);
            }
        }
    }

    /** The round plaza and its fountain. */
    private void plaza() {
        for (int x = -13; x <= 13; x++)
            for (int z = -13; z <= 13; z++) {
                double d = Math.hypot(x, z);
                if (d > 13.5) continue;
                b.set(x, -1, z, d < 4.5 ? Blocks.SMOOTH_STONE : ((int) d) % 3 == 0 ? Blocks.POLISHED_ANDESITE : Blocks.STONE_BRICKS);
                b.set(x, 0, z, Blocks.AIR);
            }
        for (int x = -3; x <= 3; x++)
            for (int z = -3; z <= 3; z++) {
                double d = Math.hypot(x, z);
                if (d > 3.3) continue;
                b.set(x, -1, z, Blocks.STONE_BRICKS);
                b.set(x, 0, z, d > 2.3 ? Blocks.STONE_BRICK_WALL.defaultBlockState() : Blocks.WATER.defaultBlockState());
            }
        b.fill(0, 0, 0, 0, 2, 0, Blocks.STONE_BRICK_WALL);
        b.set(0, 3, 0, Blocks.WATER);
        // Benches and trees round the edge.
        for (int[] t : new int[][] {{-10, 7}, {10, -7}, {-9, -8}, {8, 9}}) {
            b.set(t[0], -1, t[1], Blocks.GRASS_BLOCK);
            b.broadleaf(t[0], 0, t[1], 5, Blocks.OAK_LOG, Blocks.OAK_LEAVES);
        }
        for (int[] s : new int[][] {{-7, 10}, {-6, 10}, {6, -10}, {7, -10}}) b.set(s[0], 0, s[1], Builder.stairs(Blocks.SPRUCE_STAIRS, s[1] > 0 ? Direction.SOUTH : Direction.NORTH, false));
    }

    /** The royal palace: throne at the north end, the council table, pillars, lanterns and banners. */
    private void palace() {
        int x0 = -16, x1 = 16, z0 = -46, z1 = -18, top = 9;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                b.set(x, -1, z, Math.abs(x) <= 1 && z > -41 ? Blocks.RED_WOOL : (x + z) % 2 == 0 ? Blocks.POLISHED_ANDESITE : Blocks.POLISHED_DIORITE);
                for (int y = 0; y < top; y++) {
                    BlockState s = Blocks.AIR.defaultBlockState();
                    if (edge) {
                        boolean window = y >= 3 && y <= 6 && (x - x0) % 4 == 2 && z != z1 || (z - z0) % 4 == 2 && (x == x0 || x == x1) && y >= 3 && y <= 6;
                        s = window ? Blocks.GLASS_PANE.defaultBlockState() : (y == 0 ? Blocks.STONE_BRICKS : Blocks.SMOOTH_STONE).defaultBlockState();
                        if ((x == x0 || x == x1) && (z - z0) % 8 == 0) s = Blocks.QUARTZ_PILLAR.defaultBlockState();
                    }
                    b.set(x, y, z, s);
                }
                b.set(x, top, z, edge ? Blocks.STONE_BRICKS : Blocks.SMOOTH_STONE);
                if (edge && (x + z) % 2 == 0) b.set(x, top + 1, z, Blocks.STONE_BRICK_WALL);
            }
        // The great doorway on the south side.
        for (int x = -2; x <= 2; x++) b.fill(x, 0, z1, x, 4, z1, Blocks.AIR);
        b.set(-3, 2, z1 + 1, Blocks.LANTERN);
        b.set(3, 2, z1 + 1, Blocks.LANTERN);
        b.fill(-3, 0, z1 + 1, -3, 1, z1 + 1, Blocks.STONE_BRICK_WALL);
        b.fill(3, 0, z1 + 1, 3, 1, z1 + 1, Blocks.STONE_BRICK_WALL);
        // Pillars down the hall.
        for (int z = -42; z <= -22; z += 8)
            for (int x : new int[] {-10, 10}) b.fill(x, 0, z, x, top - 1, z, Blocks.QUARTZ_PILLAR);
        // The dais and throne.
        b.fill(-5, 0, -45, 5, 0, -41, Blocks.POLISHED_DEEPSLATE);
        for (int x = -5; x <= 5; x++) b.set(x, 0, -40, Builder.stairs(Blocks.POLISHED_DEEPSLATE_STAIRS, Direction.NORTH, false));
        b.set(0, 1, -44, Builder.stairs(Blocks.DARK_OAK_STAIRS, Direction.NORTH, false));
        b.fill(0, 1, -45, 0, 3, -45, Blocks.GOLD_BLOCK);
        b.set(-1, 1, -44, Blocks.DARK_OAK_TRAPDOOR);
        b.set(1, 1, -44, Blocks.DARK_OAK_TRAPDOOR);
        for (int x : new int[] {-4, 4}) {
            b.set(x, 1, -45, Blocks.BLUE_BANNER);
            b.set(x, 1, -42, Blocks.LANTERN);
        }
        // The long council table, with a chair at its head for the Queen.
        b.fill(-2, 0, -36, 2, 0, -28, Blocks.DARK_OAK_PLANKS);
        for (int x = -2; x <= 2; x++) for (int z = -36; z <= -28; z++) if ((x + z) % 3 == 0) b.set(x, 1, z, Blocks.AIR);
        b.set(0, 0, -38, Builder.stairs(Blocks.DARK_OAK_STAIRS, Direction.NORTH, false));
        for (int z = -35; z <= -29; z += 2) {
            b.set(-4, 0, z, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
            b.set(4, 0, z, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
        }
        b.set(-1, 1, -31, Blocks.CANDLE);
        b.set(1, 1, -33, Blocks.CANDLE);
        // Hanging lanterns and banners.
        for (int z = -44; z <= -20; z += 6)
            for (int x : new int[] {-6, 6}) b.set(x, top - 1, z, Builder.hangingLantern());
        for (int z = -42; z <= -22; z += 8)
            for (int x : new int[] {x0 + 1, x1 - 1}) b.set(x, 5, z, Blocks.BLUE_WALL_BANNER.defaultBlockState()
                    .setValue(net.minecraft.world.level.block.WallBannerBlock.FACING, x < 0 ? Direction.EAST : Direction.WEST));
    }

    /** The royal archive: catalogue hall with the floor map, and the research chamber behind it. */
    private void archive() {
        int x0 = -52, x1 = -22, z0 = -16, z1 = 12, top = 8;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                b.set(x, -1, z, (x + z) % 2 == 0 ? Blocks.SPRUCE_PLANKS : Blocks.DARK_OAK_PLANKS);
                for (int y = 0; y < top; y++) {
                    BlockState s = Blocks.AIR.defaultBlockState();
                    if (edge) {
                        boolean window = y >= 2 && y <= 4 && ((z - z0) % 5 == 2 && (x == x0 || x == x1) || (x - x0) % 5 == 2 && (z == z0 || z == z1));
                        s = window ? Blocks.GLASS_PANE.defaultBlockState()
                                : ((x == x0 || x == x1) && (z == z0 || z == z1) ? Blocks.STRIPPED_DARK_OAK_LOG : y == 0 ? Blocks.STONE_BRICKS : Blocks.WHITE_TERRACOTTA).defaultBlockState();
                    }
                    b.set(x, y, z, s);
                }
                b.set(x, top, z, Blocks.DARK_OAK_PLANKS);
                if (edge) b.set(x, top + 1, z, Blocks.DARK_OAK_SLAB);
            }
        // The door on the east side, facing the plaza street.
        for (int z = -1; z <= 1; z++) b.fill(x1, 0, z, x1, 2, z, Blocks.AIR);
        b.sign(x1 + 1, 3, 0, Blocks.DARK_OAK_WALL_SIGN, Direction.EAST, "", "Royal Archive", "of Eldervale", "");
        // Wall between the hall and the research chamber, with a doorway.
        for (int x = x0 + 1; x < x1; x++) {
            b.fill(x, x >= -38 && x <= -36 ? 3 : 0, -6, x, top - 1, -6, Blocks.BOOKSHELF);
        }
        // Hall: shelves along the walls, the catalogue lecterns and reading desks.
        for (int z = -5; z <= 11; z++) b.fill(x0 + 1, 0, z, x0 + 1, 4, z, Blocks.BOOKSHELF);
        for (int x = x0 + 2; x <= x1 - 2; x++) if (x < -26 || x > -24) b.fill(x, 0, z1 - 1, x, 3, z1 - 1, Blocks.BOOKSHELF);
        for (int x = -50; x <= -24; x++) if (x % 8 != 0 && (x < -39 || x > -35)) b.fill(x, 0, -5, x, 2, -5, Blocks.BOOKSHELF);
        for (int x = -49; x <= -25; x += 6) b.set(x, top - 1, 0, Builder.hangingLantern());
        // The archivist's desk by the door.
        b.set(-26, 0, 3, Blocks.DARK_OAK_PLANKS);
        b.set(-25, 0, 3, Blocks.DARK_OAK_PLANKS);
        b.set(-25, 1, 3, Blocks.CANDLE);
        // The reading desk where Sahir lays out his manuscript (the lectern by it is a clue).
        b.set(-29, 0, 7, Blocks.DARK_OAK_PLANKS);
        b.set(-30, 0, 7, Blocks.DARK_OAK_PLANKS);
        b.set(-30, 1, 7, Blocks.CANDLE);
        floorMap();
        researchChamber();
    }

    /**
     * The great floor map of the six lands, inlaid in the hall floor (x -46..-34, z 1..10, at y -1):
     * meadow and forest, mountains in the north-west, desert to the south-east, the sea along the
     * south. Each civilisation's emblem marks its landmark, and Awakening Valley's mark is in the middle.
     */
    private void floorMap() {
        for (int x = -46; x <= -34; x++)
            for (int z = 1; z <= 10; z++) {
                Block land;
                if (z >= 9) land = Blocks.LIGHT_BLUE_TERRACOTTA;
                else if (x <= -43 && z <= 4) land = Blocks.GRAY_TERRACOTTA;
                else if (x >= -37 && z >= 6) land = Blocks.YELLOW_TERRACOTTA;
                else if (x >= -37) land = Blocks.GREEN_TERRACOTTA;
                else land = Blocks.LIME_TERRACOTTA;
                b.set(x, -1, z, land);
            }
        for (int x = -47; x <= -33; x++) {
            b.set(x, -1, 0, Blocks.STRIPPED_DARK_OAK_LOG);
            b.set(x, -1, 11, Blocks.STRIPPED_DARK_OAK_LOG);
        }
        // Landmarks: Eldervale (Valebridge), Sylvaris, Stonecrown (Irondeep), Solkar (Sunspire), Veyrath, Thalassia (Tidewatch).
        int[][] marks = {{-44, 8}, {-35, 4}, {-45, 2}, {-35, 8}, {-46, 6}, {-40, 9}};
        for (int i = 0; i < marks.length; i++) b.set(marks[i][0], -1, marks[i][1], EMBLEMS[i]);
        b.set(-40, -1, 4, Blocks.LODESTONE);
    }

    /** The research chamber: the reconstruction table under a skylight, and six record lecterns. */
    private void researchChamber() {
        b.fill(TABLE_X - 2, 0, TABLE_Z - 2, TABLE_X + 2, 0, TABLE_Z + 2, Blocks.POLISHED_DEEPSLATE);
        // Each slot's inlay carries the emblem its fragment must match.
        for (int i = 0; i < SLOTS.length; i++) b.set(SLOTS[i][0], 0, SLOTS[i][1], EMBLEMS[i]);
        b.set(TABLE_X, 0, TABLE_Z, Blocks.POLISHED_BLACKSTONE);
        for (int x = TABLE_X - 2; x <= TABLE_X + 2; x++)
            for (int z = TABLE_Z - 2; z <= TABLE_Z + 2; z++)
                if (Math.abs(x - TABLE_X) == 2 && Math.abs(z - TABLE_Z) == 2) b.set(x, 1, z, Blocks.CANDLE);
        b.set(TABLE_X, 7, TABLE_Z, Blocks.GLASS);
        b.set(TABLE_X, 8, TABLE_Z, Blocks.GLASS);
        // Work benches and maps round the walls.
        b.set(-50, 0, -15, Blocks.CARTOGRAPHY_TABLE);
        b.set(-50, 0, -8, Blocks.SMITHING_TABLE);
        b.set(-24, 0, -15, Blocks.CRAFTING_TABLE);
        b.set(-24, 0, -8, Builder.facing(Blocks.BARREL, Direction.UP));
        for (int x = -49; x <= -25; x += 8) b.set(x, 6, -11, Builder.hangingLantern());
    }

    /** The Solkar envoy's pavilion: sandstone floor, a striped tent and cushions round a map table. */
    private void pavilion() {
        int x0 = 14, x1 = 22, z0 = 6, z1 = 13;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                b.set(x, -1, z, Blocks.SMOOTH_SANDSTONE);
                b.fill(x, 0, z, x, 5, z, Blocks.AIR);
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                if (corner) b.fill(x, 0, z, x, 3, z, Blocks.STRIPPED_ACACIA_LOG);
                int roof = 4 + Math.min(Math.min(x - x0, x1 - x), 2) / 2;
                b.set(x, roof, z, (x + z) % 2 == 0 ? Blocks.ORANGE_WOOL : Blocks.YELLOW_WOOL);
            }
        b.set(18, 0, 10, Blocks.CARTOGRAPHY_TABLE);
        for (int[] c : new int[][] {{16, 9}, {20, 11}, {16, 11}}) b.set(c[0], 0, c[1], Blocks.ORANGE_CARPET);
        b.set(21, 0, 12, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(15, 0, 12, Blocks.DECORATED_POT);
        b.set(21, 3, 7, Blocks.LANTERN);
    }

    /** Market stalls along the main street. */
    private void market() {
        for (int x = 18; x <= 46; x += 7)
            for (int side : new int[] {-1, 1}) {
                int z0 = side * 5, z1 = side * 7;
                for (int dx = 0; dx <= 3; dx++)
                    for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) {
                        boolean post = (dx == 0 || dx == 3) && (z == z0 || z == z1);
                        if (post) b.fill(x + dx, 0, z, x + dx, 2, z, Blocks.SPRUCE_FENCE);
                        b.set(x + dx, 3, z, ((x / 7 + side) & 1) == 0 ? Blocks.RED_WOOL : Blocks.WHITE_WOOL);
                    }
                b.set(x + 1, 0, z0, Builder.facing(Blocks.BARREL, Direction.UP));
                b.set(x + 2, 0, z0, ((x / 7) & 1) == 0 ? Blocks.MELON : Blocks.PUMPKIN);
            }
    }

    /** Townhouses filling the quarters between the streets. */
    private void houses() {
        int[][] lots = {
                {22, -44, 30, -36}, {34, -44, 42, -36}, {22, -30, 30, -22}, {34, -30, 42, -22}, {44, -18, 50, -10},
                {28, 12, 36, 20}, {40, 12, 48, 20}, {-50, -44, -42, -36}, {-38, -44, -26, -36}, {-50, 16, -40, 22},
                {-36, 16, -26, 22}, {-48, -32, -40, -24}, {-34, -32, -24, -24}};
        Palette[] palettes = {Palette.PLASTER, Palette.TIMBER, Palette.STONE, Palette.ELDER};
        for (int i = 0; i < lots.length; i++) house(lots[i], palettes[i % palettes.length]);
    }

    private void house(int[] lot, Palette p) {
        int x0 = lot[0], z0 = lot[1], x1 = lot[2], z1 = lot[3], wallTop = 4;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                b.set(x, -1, z, p.foundation());
                for (int y = 0; y < wallTop; y++) {
                    Block s = Blocks.AIR;
                    if (corner) s = p.frame();
                    else if (edge) s = y == 0 ? p.foundation() : (y == 2 && (x + z) % 3 == 0) ? p.window() : p.wall();
                    b.set(x, y, z, s);
                }
            }
        // A door on the side facing the nearest street.
        int mx = (x0 + x1) / 2, mz = (z0 + z1) / 2;
        boolean south = Math.abs(z1) < Math.abs(z0);
        int dz = south ? z1 : z0;
        b.door(mx, 0, dz, p.door(), south ? Direction.SOUTH : Direction.NORTH);
        b.set(mx, 2, dz + (south ? 1 : -1), Blocks.LANTERN);
        b.gableRoof(x0, z0, x1, z1, wallTop, p);
        b.set(mx, 3, mz, Builder.hangingLantern());
    }

    /** The Thalassian ship moored east of the pier. */
    private void ship() {
        int x0 = 7, x1 = 22, z0 = 37, z1 = 45, cz = 41;
        for (int x = x0; x <= x1; x++) {
            // A pointed bow at the east end.
            int half = x > x1 - 4 ? Math.max(0, 4 - (x - (x1 - 4)) - 1) : 4;
            for (int z = cz - half; z <= cz + half; z++) {
                boolean side = Math.abs(z - cz) == half;
                for (int y = -5; y <= -2; y++) {
                    b.set(x, y, z, side || y == -5 ? Blocks.DARK_OAK_PLANKS : Blocks.AIR);
                }
                b.set(x, -1, z, side ? Blocks.DARK_OAK_PLANKS : Blocks.SPRUCE_PLANKS);
                if (side) b.set(x, 0, z, Blocks.DARK_OAK_FENCE);
            }
        }
        b.set(x0, 0, cz, Blocks.AIR); // where the gangplank comes aboard
        for (int z = z0; z <= z1; z++) if (Math.abs(z - cz) == 4) b.set(x0, 0, z, Blocks.DARK_OAK_FENCE);
        // Mast and sail.
        b.fill(14, 0, cz, 14, 13, cz, Blocks.SPRUCE_LOG);
        for (int z = cz - 3; z <= cz + 3; z++) b.fill(14, 5, z, 14, 11, z, Blocks.WHITE_WOOL);
        b.set(14, 5, cz, Blocks.SPRUCE_LOG);
        b.fill(14, 12, cz - 4, 14, 12, cz + 4, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.Z));
        b.set(14, 14, cz, Blocks.CYAN_BANNER);
        // Cargo and the stern cabin.
        b.fill(8, 0, cz - 2, 10, 2, cz + 2, Blocks.DARK_OAK_PLANKS);
        b.fill(9, 0, cz - 1, 9, 1, cz + 1, Blocks.AIR);
        b.set(18, 0, cz - 2, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(18, 0, cz + 2, Blocks.CHEST);
        b.set(12, 0, cz + 3, Blocks.LANTERN);
    }

    /** Scene 016: the Solkar and Veyrath fragments laid on the table beside the first three. */
    public static void tableFragments(ServerLevel level, BlockPos anchor, int upTo) {
        Builder b = new Builder(level, anchor, 0);
        for (int i = 0; i < upTo; i++) b.set(SLOTS[i][0], 1, SLOTS[i][1], EMBLEMS[i]);
    }

    /** Scene 017: Orin opens the skylight over the diagram and sets a lamp beneath it. */
    public static void skylight(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        b.set(TABLE_X, 6, TABLE_Z, Blocks.LANTERN.defaultBlockState().setValue(net.minecraft.world.level.block.LanternBlock.HANGING, true));
        b.set(TABLE_X, 8, TABLE_Z, Blocks.AIR);
        b.set(TABLE_X, 7, TABLE_Z, Blocks.SEA_LANTERN);
    }
}
