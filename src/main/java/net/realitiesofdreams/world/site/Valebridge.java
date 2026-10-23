package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Valebridge, the capital of Eldervale (Scenes 012 and 017), anchored at MC-001 block (-115, 67, 105);
 * everything here is relative to it, with y = 0 the street.
 *
 * <p>A walled city (walls at x -54 and 54 and z -48, with a walkway on their inner side reached by
 * stairs at (19..23, -46), (-24..-20, -46), (-52, -22..-18), (52, -13..-9) and (49..53, 22), and six
 * towers with ladders and guard rooms) open to its harbour on the south. The east gate (x 54,
 * z -2..2, between the gate towers at (54, -6) and (54, 6)) lets the trade road in along the main
 * street (z -3..3), lit by banner arches at x 14, 22, 30, 38, 46 and lamp posts, to a round plaza with
 * a fountain at the origin.
 *
 * <p>Fixed story places (scenes check these, so they must not move): the royal palace
 * (x -16..16, z -46..-18, door x -2..2 at z -18) with the throne dais (x -5..5, z -45..-40), the long
 * council table (x -2..2, z -36..-28) and the Queen's chair at (0, 0, -38); its side rooms are the
 * Queen's study (x -15..-9, z -45..-39), the map room (x 9..15, z -45..-39) and two guard rooms
 * (x -15..-12 and 12..15, z -23..-19). The royal archive (x -52..-22, z -16..12, door on the east side
 * at z -1..1): the research chamber north of the bookshelf wall (z -6, doorway x -38..-36) holds the
 * six-slot reconstruction table round (-41, 1, -11) under its skylight, with clear spots along z -14;
 * the hall holds the catalogue spots along z -3, the archivist's desk (-26/-25, 0, 3), the reading desk
 * (-30/-29, 0, 7) and the great floor map (x -46..-34, z 1..10). The Solkar envoy's pavilion
 * (x 14..22, z 6..13), the harbour quay (z 26..30), pier (x -3..3, z 31..50), crane (x -13..-7, z 28)
 * and the Thalassian ship (x 7..22, z 37..45).
 *
 * <p>City life: thirteen furnished townhouses (some two storeys, with chimneys and fireplaces), the
 * Chapel of the Six Lights (x 20..28, z -18..-9), the Silver Stag inn (x 34..41, z -17..-8), the guard
 * barracks (x 44..52, z -31..-22), the royal granary (x 45..51, z -44..-37), the gate watch house
 * (x 50..53, z 12..21), the Warm Hearth bakery (x 5..11, z 16..24), the Ironbridge forge (x 14..22,
 * z 16..24), an apothecary (x -23..-17, z 16..24), a net loft (x -14..-6, z 16..24), the copyists'
 * house (x -20..-14, z 5..11), market stalls along the main street, a fish market on the harbour yard
 * (z 22..25), wells and gardens at (-17, -10), (-21, -27), (25, 18) and the orchard (x -25..-19,
 * z -45..-36), and fields outside the east wall.
 */
public final class Valebridge {
    public static final int X0 = -56, X1 = 66, Z0 = -50, Z1 = 56;
    /** The reconstruction table's six slots (y 1) and centre, sunwise from the north. */
    public static final int[][] SLOTS = {{-41, -13}, {-39, -12}, {-39, -10}, {-41, -9}, {-43, -10}, {-43, -12}};
    public static final int TABLE_X = -41, TABLE_Z = -11;
    /** Each civilisation's emblem, in slot order: Eldervale, Sylvaris, Stonecrown, Solkar, Veyrath, Thalassia. */
    public static final Block[] EMBLEMS = {Blocks.CHISELED_STONE_BRICKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.CHISELED_DEEPSLATE,
            Blocks.CHISELED_SANDSTONE, Blocks.CHISELED_NETHER_BRICKS, Blocks.DARK_PRISMARINE};

    /** Eldervale's colours: blue, white, black and silver. */
    private static final Block[] ELDER_BANNERS = {Blocks.BLUE_WALL_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.BLACK_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER};
    private static final Block[] ELDER_STANDING = {Blocks.BLUE_BANNER, Blocks.WHITE_BANNER, Blocks.BLACK_BANNER, Blocks.LIGHT_GRAY_BANNER};
    private static final Block[] BEDS = {Blocks.RED_BED, Blocks.BLUE_BED, Blocks.LIGHT_BLUE_BED, Blocks.WHITE_BED, Blocks.GREEN_BED,
            Blocks.YELLOW_BED, Blocks.CYAN_BED, Blocks.BROWN_BED};
    private static final Block[] CARPETS = {Blocks.BLUE_CARPET, Blocks.RED_CARPET, Blocks.LIGHT_GRAY_CARPET, Blocks.BROWN_CARPET,
            Blocks.GREEN_CARPET, Blocks.CYAN_CARPET, Blocks.WHITE_CARPET, Blocks.YELLOW_CARPET};
    private static final Block[] POTS = {Blocks.POTTED_RED_TULIP, Blocks.POTTED_FERN, Blocks.POTTED_BLUE_ORCHID, Blocks.POTTED_OXEYE_DAISY,
            Blocks.POTTED_CORNFLOWER, Blocks.POTTED_AZALEA, Blocks.POTTED_LILY_OF_THE_VALLEY, Blocks.POTTED_DANDELION};
    private static final Block[] FLOWERS = {Blocks.POPPY, Blocks.DANDELION, Blocks.CORNFLOWER, Blocks.OXEYE_DAISY, Blocks.ALLIUM,
            Blocks.AZURE_BLUET, Blocks.LILY_OF_THE_VALLEY, Blocks.BLUE_ORCHID, Blocks.WHITE_TULIP};

    /** The chapel's white stone, deepslate roof and blue glass. */
    private static final Palette CHAPEL = new Palette("chapel", Blocks.STONE_BRICKS, Blocks.CALCITE, Blocks.POLISHED_ANDESITE, Blocks.QUARTZ_PILLAR,
            Blocks.POLISHED_ANDESITE, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DARK_OAK_DOOR, Blocks.BLUE_STAINED_GLASS_PANE,
            Blocks.DARK_OAK_WALL_SIGN, Blocks.STONE_BRICKS);

    private static final int WEAVER = 0, SCRIBE = 1, COOPER = 2, POTTER = 3, TAILOR = 4, FISHER = 5, FAMILY = 6;

    /** A townhouse lot: outer footprint, materials, storeys, the household's trade and the chimney side (B back, L left, R right). */
    private record Lot(int x0, int z0, int x1, int z1, Palette p, int storeys, int trade, char chimney) {}

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
        v.lanes();
        v.plaza();
        v.palace();
        v.archive();
        v.pavilion();
        v.market();
        v.houses();
        v.chapel();
        v.tavern();
        v.barracks();
        v.granary();
        v.bakery();
        v.smithy();
        v.watchHouse();
        v.apothecary();
        v.netLoft();
        v.copyHouse();
        v.gardens();
        v.harbourYard();
        v.outskirts();
        v.lamps();
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
        for (int x = 4; x <= 6; x++) b.set(x, -1, 41, Builder.slab(Blocks.SPRUCE_SLAB, true));
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

    // ------------------------------------------------------------------ walls and towers

    /** City walls with a walkway along their inner side, the east gate and six towers. */
    private void walls() {
        for (int x = -54; x <= 54; x++) wallColumn(x, -48);
        for (int z = -48; z <= 25; z++) {
            wallColumn(-54, z);
            if (Math.abs(z) > 2) wallColumn(54, z);
        }
        // The east gate's arch, its jambs and the lanterns that greet the road.
        for (int z = -3; z <= 3; z++) b.fill(54, 5, z, 54, 6, z, Blocks.STONE_BRICKS);
        b.set(54, 4, -2, Builder.stairs(Blocks.STONE_BRICK_STAIRS, Direction.SOUTH, true));
        b.set(54, 4, 2, Builder.stairs(Blocks.STONE_BRICK_STAIRS, Direction.NORTH, true));
        b.fill(55, 0, -3, 55, 2, -3, Blocks.STONE_BRICK_WALL);
        b.fill(55, 0, 3, 55, 2, 3, Blocks.STONE_BRICK_WALL);
        b.set(55, 3, -3, Blocks.LANTERN);
        b.set(55, 3, 3, Blocks.LANTERN);
        wallBanner(55, 5, 0, Blocks.BLUE_WALL_BANNER, Direction.EAST);
        b.set(54, 7, 0, Blocks.STONE_BRICK_WALL);
        b.set(54, 8, 0, Blocks.LANTERN);
        // The walkway: a ledge one block wide inside the wall, its top level with the wall's (stand at y 5).
        for (int x = -51; x <= 51; x++) ledge(x, -47, Direction.NORTH);
        for (int z = -45; z <= 22; z++) ledge(-53, z, Direction.WEST);
        for (int z = -45; z <= 21; z++) if (z < -8 || z > 8 || Math.abs(z) <= 3) ledge(53, z, Direction.EAST);
        // Towers: corners, and a pair flanking the gate. Each has a door, a guard room, a ladder to the top.
        tower(-54, -48, -52, -47, Direction.EAST, -55, -48, Direction.EAST, Blocks.BLUE_BANNER);
        tower(54, -48, 52, -47, Direction.WEST, 55, -48, Direction.WEST, Blocks.WHITE_BANNER);
        tower(54, -6, 52, -6, Direction.WEST, 55, -6, Direction.WEST, Blocks.BLUE_BANNER);
        tower(54, 6, 52, 6, Direction.WEST, 55, 6, Direction.WEST, Blocks.BLUE_BANNER);
        tower(-54, 25, -52, 24, Direction.EAST, -55, 25, Direction.EAST, Blocks.BLACK_BANNER);
        tower(54, 25, 52, 24, Direction.WEST, 55, 25, Direction.WEST, Blocks.LIGHT_GRAY_BANNER);
        // The walkway runs into each tower's upper room.
        for (int[] g : new int[][] {{-52, -47}, {52, -47}, {-53, -46}, {53, -46}, {-53, 23}, {53, 23}, {53, -8}, {53, -4}, {53, 4}, {53, 8}})
            b.fill(g[0], 5, g[1], g[0], 6, g[1], Blocks.AIR);
        // Banners on the gate towers' faces.
        wallBanner(57, 5, -6, Blocks.BLUE_WALL_BANNER, Direction.EAST);
        wallBanner(57, 5, 6, Blocks.BLUE_WALL_BANNER, Direction.EAST);
        wallBanner(57, 4, -5, Blocks.WHITE_WALL_BANNER, Direction.EAST);
        wallBanner(57, 4, 5, Blocks.WHITE_WALL_BANNER, Direction.EAST);
        // Stairs up to the walkway.
        wallStairs(19, -46, Direction.EAST);
        wallStairs(-20, -46, Direction.WEST);
        wallStairs(-52, -18, Direction.NORTH);
        wallStairs(52, -13, Direction.SOUTH);
        wallStairs(49, 22, Direction.EAST);
        // Lanterns on the battlements light the walkway.
        for (int x = -40; x <= 40; x += 8) b.set(x, 6, -48, Blocks.LANTERN);
        for (int z = -40; z <= 16; z += 8) b.set(-54, 6, z, Blocks.LANTERN);
        for (int z : new int[] {-40, -32, -24, -16, 16}) b.set(54, 6, z, Blocks.LANTERN);
    }

    private void wallColumn(int x, int z) {
        b.fill(x, -2, z, x, 4, z, (hash(x, z) % 7 == 0) ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS);
        if ((x + z) % 2 == 0) b.set(x, 5, z, Blocks.STONE_BRICK_WALL);
    }

    /** One block of the wall walk at (x, 4, z), with a corbel under it now and then; {@code toWall} points at the wall. */
    private void ledge(int x, int z, Direction toWall) {
        b.set(x, 4, z, Blocks.STONE_BRICKS);
        boolean overGate = x == 53 && Math.abs(z) <= 3;
        if (!overGate && hash(x, z) % 4 == 0) b.set(x, 3, z, Builder.stairs(Blocks.STONE_BRICK_STAIRS, toWall, true));
    }

    /** Five steps from the ground up to the wall walk, climbing towards {@code up}. */
    private void wallStairs(int x, int z, Direction up) {
        for (int i = 0; i <= 4; i++) {
            int px = x + up.getStepX() * i, pz = z + up.getStepZ() * i;
            if (i > 0) b.fill(px, 0, pz, px, i - 1, pz, Blocks.STONE_BRICKS);
            b.set(px, i, pz, Builder.stairs(Blocks.STONE_BRICK_STAIRS, up, false));
        }
    }

    /**
     * A 5x5 tower round (cx, cz): a guard room on the ground (door at doorX/doorZ), an upper room at the
     * wall walk's level (floor y 4) and a roof (y 7) with battlements, joined by a ladder.
     */
    private void tower(int cx, int cz, int doorX, int doorZ, Direction doorFacing, int lx, int lz, Direction ladderFacing, Block banner) {
        for (int x = cx - 2; x <= cx + 2; x++)
            for (int z = cz - 2; z <= cz + 2; z++) {
                boolean edge = Math.abs(x - cx) == 2 || Math.abs(z - cz) == 2;
                if (edge) {
                    b.fill(x, -2, z, x, 6, z, hash(x, z) % 9 == 0 ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS);
                } else {
                    b.set(x, -1, z, Blocks.STONE_BRICKS);
                    b.fill(x, 0, z, x, 6, z, Blocks.AIR);
                    b.set(x, 4, z, Blocks.SPRUCE_PLANKS);
                }
                b.set(x, 7, z, Blocks.STONE_BRICKS);
                if (edge && (x + z) % 2 == 0) b.set(x, 8, z, Blocks.STONE_BRICK_WALL);
            }
        b.set(cx, 8, cz, Blocks.STONE_BRICK_WALL);
        b.set(cx, 9, cz, banner);
        b.door(doorX, 0, doorZ, Blocks.SPRUCE_DOOR, doorFacing);
        b.ladder(lx, 0, 7, lz, ladderFacing);
        b.set(cx, 3, cz, Builder.hangingLantern());
        b.set(cx, 6, cz, Builder.hangingLantern());
        b.set(cx, 0, cz + 1, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(cx, 5, cz + 1, Builder.facing(Blocks.CHEST, ladderFacing));
    }

    // ------------------------------------------------------------------ streets

    private void pave(int x0, int z0, int x1, int z1) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                int h = hash(x, z);
                b.set(x, -1, z, h % 9 == 0 ? Blocks.CRACKED_STONE_BRICKS : h % 4 == 0 ? Blocks.POLISHED_ANDESITE : Blocks.STONE_BRICKS);
                b.set(x, 0, z, Blocks.AIR);
            }
    }

    /** A cobbled side lane. */
    private void lane(int x0, int z0, int x1, int z1) {
        for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++)
            for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) {
                laneBlock(x, z);
                b.set(x, 0, z, Blocks.AIR);
            }
    }

    private void laneBlock(int x, int z) {
        int h = hash(x, z) % 10;
        b.set(x, -1, z, h == 0 ? Blocks.MOSSY_COBBLESTONE : h < 4 ? Blocks.COBBLESTONE : h < 7 ? Blocks.ANDESITE : Blocks.POLISHED_ANDESITE);
    }

    private void streets() {
        pave(12, -3, 53, 3);       // main street from the gate
        pave(54, -2, 55, 2);       // through the gate
        pave(-3, -18, 3, -10);     // to the palace
        pave(-22, -2, -10, 2);     // to the archive
        pave(-3, 10, 3, 26);       // down to the harbour
        pave(-18, -17, 18, -14);   // the palace forecourt
        // The road in from the east.
        for (int x = 56; x <= X1; x++)
            for (int z = -2; z <= 2; z++) b.set(x, -1, z, hash(x, z) % 5 == 0 ? Blocks.COARSE_DIRT : Blocks.DIRT_PATH);
    }

    /** The side lanes between the quarters. */
    private void lanes() {
        // North-east quarter.
        lane(17, -34, 52, -32);
        lane(17, -21, 52, -19);
        lane(31, -46, 33, -4);
        lane(17, -46, 18, -14);
        lane(23, -8, 25, -4);      // the chapel's forecourt
        lane(37, -7, 37, -4);      // the inn's step
        lane(47, -9, 47, -4);
        // North-west quarter.
        lane(-18, -46, -17, -14);
        lane(-51, -35, -19, -33);
        lane(-51, -22, -19, -20);
        lane(-41, -46, -39, -36);
        lane(-38, -32, -36, -23);
        lane(-53, -19, -53, 12);   // the alley under the west wall
        // South-west and the copyists' house.
        lane(-53, 13, -4, 15);
        lane(-17, 3, -17, 4);
        // South-east.
        lane(4, 14, 27, 15);
        lane(4, 19, 4, 21);
        lane(23, 9, 51, 11);
        lane(37, 12, 39, 25);
        lane(23, 4, 24, 8);
        lane(36, 4, 37, 8);
        lane(43, 4, 45, 8);
        lane(23, 22, 48, 25);      // the harbour yard
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
        // Lanterns stand on the fountain's rim.
        for (int[] l : new int[][] {{3, 0}, {-3, 0}, {0, 3}, {0, -3}, {2, 2}, {-2, 2}, {2, -2}, {-2, -2}}) b.set(l[0], 1, l[1], Blocks.LANTERN);
        // Benches and trees round the edge.
        for (int[] t : new int[][] {{-10, 7}, {10, -7}, {-9, -8}, {8, 9}}) {
            b.set(t[0], -1, t[1], Blocks.GRASS_BLOCK);
            b.broadleaf(t[0], 0, t[1], 5, Blocks.OAK_LOG, Blocks.OAK_LEAVES);
        }
        for (int[] s : new int[][] {{-7, 10}, {-6, 10}, {6, -10}, {7, -10}}) b.set(s[0], 0, s[1], Builder.stairs(Blocks.SPRUCE_STAIRS, s[1] > 0 ? Direction.SOUTH : Direction.NORTH, false));
    }

    // ------------------------------------------------------------------ the palace

    /** The royal palace: throne at the north end, the council table, side rooms, chandeliers and banners. */
    private void palace() {
        int x0 = -16, x1 = 16, z0 = -46, z1 = -18, top = 9;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                Block floor = Math.abs(x) <= 1 && z > -41 ? Blocks.RED_WOOL : Math.abs(x) == 2 && z > -41 ? Blocks.BLUE_WOOL
                        : (x + z) % 2 == 0 ? Blocks.POLISHED_ANDESITE : Blocks.POLISHED_DIORITE;
                b.set(x, -1, z, floor);
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
        b.fill(-3, 0, z1 + 1, -3, 1, z1 + 1, Blocks.STONE_BRICK_WALL);
        b.fill(3, 0, z1 + 1, 3, 1, z1 + 1, Blocks.STONE_BRICK_WALL);
        b.set(-3, 2, z1 + 1, Blocks.LANTERN);
        b.set(3, 2, z1 + 1, Blocks.LANTERN);
        // Banners of Eldervale along the facade.
        for (int i = 0; i < 4; i++) {
            int x = 5 + i * 3;
            wallBanner(x, 6, z1 + 1, ELDER_BANNERS[i], Direction.SOUTH);
            wallBanner(-x, 6, z1 + 1, ELDER_BANNERS[i], Direction.SOUTH);
        }
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
        // Great banners behind the throne.
        for (int x : new int[] {-3, 3}) wallBanner(x, 6, -45, Blocks.BLUE_WALL_BANNER, Direction.SOUTH);
        wallBanner(-1, 7, -45, Blocks.WHITE_WALL_BANNER, Direction.SOUTH);
        wallBanner(1, 7, -45, Blocks.WHITE_WALL_BANNER, Direction.SOUTH);
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
        // Hanging lanterns, chandeliers over the aisle and the table, and banners on the side walls.
        for (int z = -44; z <= -20; z += 6)
            for (int x : new int[] {-6, 6}) b.set(x, top - 1, z, Builder.hangingLantern());
        chandelier(0, top, -23);
        chandelier(0, top, -32);
        for (int z = -42; z <= -22; z += 8)
            for (int x : new int[] {x0 + 1, x1 - 1}) wallBanner(x, 5, z, Blocks.BLUE_WALL_BANNER, x < 0 ? Direction.EAST : Direction.WEST);
        for (int x : new int[] {x0 + 1, x1 - 1}) {
            Direction d = x < 0 ? Direction.EAST : Direction.WEST;
            wallBanner(x, 5, -30, Blocks.WHITE_WALL_BANNER, d);
            wallBanner(x, 5, -34 + 12, Blocks.BLACK_WALL_BANNER, d);
        }
        palaceStudy();
        palaceMapRoom();
        guardRoom(-1);
        guardRoom(1);
    }

    /** A chandelier hanging from the ceiling block at y = ceiling: two links of chain, four arms, five lanterns. */
    private void chandelier(int cx, int ceiling, int cz) {
        b.set(cx, ceiling - 1, cz, Blocks.CHAIN);
        b.set(cx, ceiling - 2, cz, Blocks.CHAIN);
        b.set(cx, ceiling - 3, cz, Blocks.DARK_OAK_PLANKS);
        b.set(cx, ceiling - 4, cz, Builder.hangingLantern());
        for (Direction d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            int x = cx + d.getStepX(), z = cz + d.getStepZ();
            b.set(x, ceiling - 3, z, Blocks.DARK_OAK_FENCE);
            b.set(x, ceiling - 2, z, litCandles(1));
            b.set(x, ceiling - 4, z, Builder.hangingLantern());
        }
    }

    /** Queen Aveline's study, west of the dais: books, a writing desk and a map of the realm. */
    private void palaceStudy() {
        b.fill(-8, 0, -45, -8, 8, -38, Blocks.POLISHED_ANDESITE);
        b.fill(-15, 0, -38, -8, 8, -38, Blocks.POLISHED_ANDESITE);
        b.door(-8, 0, -41, Blocks.DARK_OAK_DOOR, Direction.EAST);
        for (int x = -14; x <= -9; x++) for (int z = -44; z <= -39; z++) if (x != -10 || z != -42) b.set(x, 0, z, Blocks.BLUE_CARPET);
        b.fill(-15, 0, -45, -11, 2, -45, Blocks.BOOKSHELF);
        b.fill(-15, 0, -44, -15, 2, -42, Blocks.BOOKSHELF);
        b.set(-13, 0, -41, Blocks.DARK_OAK_PLANKS);
        b.set(-12, 0, -41, Blocks.DARK_OAK_PLANKS);
        b.set(-13, 1, -41, litCandles(2));
        b.set(-12, 1, -41, Blocks.POTTED_LILY_OF_THE_VALLEY);
        b.set(-12, 0, -40, Builder.stairs(Blocks.DARK_OAK_STAIRS, Direction.SOUTH, false));
        b.set(-15, 0, -39, Builder.facing(Blocks.CHEST, Direction.EAST));
        b.set(-15, 0, -40, Blocks.CARTOGRAPHY_TABLE);
        b.set(-9, 0, -45, Blocks.POTTED_AZALEA);
        b.set(-9, 0, -39, Blocks.POTTED_FERN);
        b.set(-12, 8, -42, Builder.hangingLantern());
        wallBanner(-9, 5, -43, Blocks.WHITE_WALL_BANNER, Direction.WEST);
    }

    /** The map room east of the dais: the realm's charts in chests, a map table and the six lands' banners. */
    private void palaceMapRoom() {
        b.fill(8, 0, -45, 8, 8, -38, Blocks.POLISHED_ANDESITE);
        b.fill(8, 0, -38, 15, 8, -38, Blocks.POLISHED_ANDESITE);
        b.door(8, 0, -41, Blocks.DARK_OAK_DOOR, Direction.WEST);
        for (int x = 9; x <= 14; x++) for (int z = -44; z <= -39; z++) if (x != 10 || z != -42) b.set(x, 0, z, Blocks.LIGHT_GRAY_CARPET);
        b.set(12, 0, -42, Blocks.CARTOGRAPHY_TABLE);
        b.set(13, 0, -42, Blocks.CARTOGRAPHY_TABLE);
        b.set(12, 0, -41, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH, false));
        for (int x = 11; x <= 15; x++) b.set(x, 0, -45, Builder.facing(x % 2 == 0 ? Blocks.CHEST : Blocks.BARREL, Direction.SOUTH));
        b.fill(15, 0, -44, 15, 1, -42, Blocks.BOOKSHELF);
        b.set(15, 0, -39, Blocks.POTTED_BLUE_ORCHID);
        b.set(12, 8, -41, Builder.hangingLantern());
        Block[] six = {Blocks.BLUE_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.YELLOW_WALL_BANNER,
                Blocks.RED_WALL_BANNER, Blocks.CYAN_WALL_BANNER};
        for (int i = 0; i < six.length; i++) wallBanner(9 + i, 6, -39, six[i], Direction.NORTH);
    }

    /** A guard room inside the palace door, west (side -1) or east (side 1). */
    private void guardRoom(int side) {
        int inner = 11 * side, outer = 15 * side;
        Direction toHall = side < 0 ? Direction.EAST : Direction.WEST, toWall = toHall.getOpposite();
        b.fill(inner, 0, -24, inner, 8, -19, Blocks.POLISHED_ANDESITE);
        b.fill(Math.min(inner, outer), 0, -24, Math.max(inner, outer), 8, -24, Blocks.POLISHED_ANDESITE);
        b.door(inner, 0, -21, Blocks.SPRUCE_DOOR, toHall);
        b.bed(outer, 0, -23, side < 0 ? Blocks.BLUE_BED : Blocks.WHITE_BED, toWall);
        b.set(outer, 0, -21, Builder.facing(Blocks.CHEST, toHall));
        b.set(outer, 0, -20, Builder.facing(Blocks.BARREL, toHall));
        b.set(outer, 0, -19, Blocks.SMITHING_TABLE);
        b.set(12 * side, 0, -19, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH, false));
        b.set(13 * side, 8, -21, Builder.hangingLantern());
        b.armorStand(13 * side + 0.5, 0, -22 + 0.5, side < 0 ? 90 : -90, new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_CHESTPLATE),
                new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_BOOTS));
    }

    // ------------------------------------------------------------------ the archive

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
        // The door on the east side, facing the plaza street, with banners and lamps either side.
        for (int z = -1; z <= 1; z++) b.fill(x1, 0, z, x1, 2, z, Blocks.AIR);
        b.sign(x1 + 1, 3, 0, Blocks.DARK_OAK_WALL_SIGN, Direction.EAST, "", "Royal Archive", "of Eldervale", "");
        wallBanner(x1 + 1, 4, -2, Blocks.BLUE_WALL_BANNER, Direction.EAST);
        wallBanner(x1 + 1, 4, 2, Blocks.BLUE_WALL_BANNER, Direction.EAST);
        lamp(x1 + 2, -3);
        lamp(x1 + 2, 3);
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
        // Map cases in the west alcove, banners above the shelves and chandeliers over the hall.
        for (int z = 1; z <= 10; z++) {
            b.set(-50, 0, z, Builder.facing(z % 3 == 0 ? Blocks.BARREL : Blocks.CHEST, Direction.EAST));
            if (z % 3 == 1) b.set(-50, 1, z, z == 4 ? litCandles(3) : POTS[z % POTS.length].defaultBlockState());
        }
        for (int z : new int[] {3, 8}) wallBanner(x0 + 1, 6, z, z == 3 ? Blocks.BLUE_WALL_BANNER : Blocks.WHITE_WALL_BANNER, Direction.EAST);
        for (int z : new int[] {4, 8}) wallBanner(x1 - 1, 5, z, z == 4 ? Blocks.BLACK_WALL_BANNER : Blocks.BLUE_WALL_BANNER, Direction.WEST);
        chandelier(-40, top, 6);
        chandelier(-29, top, 5);
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

    /** The research chamber: the reconstruction table under a skylight, shelves and the six lands' banners. */
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
        // Work benches and maps round the walls; low shelves under the north windows.
        b.set(-50, 0, -15, Blocks.CARTOGRAPHY_TABLE);
        b.set(-50, 0, -8, Blocks.SMITHING_TABLE);
        b.set(-24, 0, -15, Blocks.CRAFTING_TABLE);
        b.set(-24, 0, -8, Builder.facing(Blocks.BARREL, Direction.UP));
        b.fill(-48, 0, -15, -26, 1, -15, Blocks.BOOKSHELF);
        b.fill(-51, 0, -13, -51, 1, -10, Blocks.BOOKSHELF);
        b.set(-23, 0, -13, Builder.facing(Blocks.CHEST, Direction.WEST));
        b.set(-23, 0, -12, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(-23, 1, -12, Blocks.POTTED_FERN);
        b.set(-23, 0, -10, Builder.facing(Blocks.CHEST, Direction.WEST));
        for (int x = -49; x <= -25; x += 8) {
            b.set(x, 6, -11, Builder.hangingLantern());
            if (x != TABLE_X) b.set(x, 7, -11, Blocks.CHAIN);
        }
        // The six lands' banners on the bookshelf wall, in slot order.
        Block[] six = {Blocks.BLUE_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.YELLOW_WALL_BANNER,
                Blocks.RED_WALL_BANNER, Blocks.CYAN_WALL_BANNER};
        int[] bx = {-50, -47, -44, -32, -29, -26};
        for (int i = 0; i < six.length; i++) wallBanner(bx[i], 5, -7, six[i], Direction.NORTH);
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
        b.set(21, 3, 7, Builder.hangingLantern());
    }

    /** Market stalls along the main street: striped awnings over goods, a few to each side. */
    private void market() {
        int[][] stalls = {{25, 1}, {32, 1}, {39, 1}, {46, 1}, {40, -1}};
        for (int i = 0; i < stalls.length; i++) {
            int x = stalls[i][0], side = stalls[i][1];
            int z0 = side * 5, z1 = side * 7;
            Block stripe = i % 2 == 0 ? Blocks.RED_WOOL : Blocks.BLUE_WOOL;
            for (int dx = 0; dx <= 3; dx++)
                for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) {
                    boolean post = (dx == 0 || dx == 3) && (z == z0 || z == z1);
                    if (post) b.fill(x + dx, 0, z, x + dx, 2, z, Blocks.SPRUCE_FENCE);
                    b.set(x + dx, 3, z, dx % 2 == 0 ? stripe : Blocks.WHITE_WOOL);
                }
            b.set(x + 1, 0, z0, Builder.facing(Blocks.BARREL, Direction.UP));
            b.set(x + 2, 0, z0, Blocks.SPRUCE_PLANKS);
            int zb = z1 - side; // the middle row, behind the counter
            switch (i) {
                case 0 -> { // greengrocer
                    b.set(x + 1, 1, z0, Blocks.MELON);
                    b.set(x + 2, 1, z0, Blocks.PUMPKIN);
                    b.set(x + 1, 0, z1, Blocks.COMPOSTER);
                    b.set(x + 2, 0, z1, Blocks.HAY_BLOCK);
                }
                case 1 -> { // the river farms' bread
                    b.set(x + 2, 1, z0, Blocks.CAKE);
                    b.set(x + 1, 0, z1, Blocks.HAY_BLOCK);
                    b.set(x + 2, 0, z1, Builder.facing(Blocks.BARREL, Direction.UP));
                }
                case 2 -> { // flowers
                    b.set(x + 1, 1, z0, Blocks.POTTED_RED_TULIP);
                    b.set(x + 2, 1, z0, Blocks.POTTED_CORNFLOWER);
                    b.set(x + 1, 0, z1, Blocks.FLOWERING_AZALEA);
                    b.set(x + 2, 0, z1, Blocks.AZALEA);
                }
                case 3 -> { // pots and wares
                    b.set(x + 2, 1, z0, Blocks.DECORATED_POT);
                    b.set(x + 1, 0, z1, Blocks.DECORATED_POT);
                    b.set(x + 2, 0, z1, Builder.facing(Blocks.CHEST, side > 0 ? Direction.NORTH : Direction.SOUTH));
                }
                default -> { // cloth
                    b.set(x + 2, 1, z0, Blocks.WHITE_WOOL);
                    b.set(x + 1, 0, z1, Blocks.LIGHT_BLUE_WOOL);
                    b.set(x + 2, 0, z1, Builder.facing(Blocks.LOOM, side > 0 ? Direction.NORTH : Direction.SOUTH));
                }
            }
            b.set(x + 1, 2, zb, Builder.hangingLantern());
        }
    }

    // ------------------------------------------------------------------ buildings in general

    /** Floor, timber-framed walls (footing, posts, windows, a trim beam on top) and an empty inside. */
    private void shell(int x0, int z0, int x1, int z1, int wallTop, Palette p, int doorX, int doorZ) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                b.set(x, -1, z, edge ? p.foundation() : p.floor());
                if (!edge) b.fill(x, 0, z, x, wallTop - 1, z, Blocks.AIR);
                if (!edge && wallTop == 8) b.set(x, 4, z, p.floor());
            }
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edgeX = x == x0 || x == x1, edgeZ = z == z0 || z == z1;
                if (!edgeX && !edgeZ) continue;
                boolean corner = edgeX && edgeZ;
                int along = edgeZ ? x - x0 : z - z0;
                int len = edgeZ ? x1 - x0 : z1 - z0;
                boolean post = corner || (along % 4 == 0 && along != len);
                for (int y = 0; y < wallTop; y++) {
                    BlockState s;
                    if (post) s = p.frame().defaultBlockState();
                    else if (y == 0) s = p.foundation().defaultBlockState();
                    else if (y == wallTop - 1 || (wallTop == 8 && y == 4)) s = Builder.log(p.trim(), edgeZ ? Direction.Axis.X : Direction.Axis.Z);
                    else s = p.wall().defaultBlockState();
                    b.set(x, y, z, s);
                }
                if (post || along % 4 != 2) continue;
                if (Math.abs(x - doorX) + Math.abs(z - doorZ) <= 1) continue;
                b.fill(x, 1, z, x, 2, z, p.window());
                if (wallTop == 8) b.fill(x, 5, z, x, 6, z, p.window());
            }
    }

    /** A tie beam across the middle under the roof, with a lantern hanging from it. */
    private void beamLantern(int x0, int z0, int x1, int z1, int wallTop, Palette p) {
        int mx = (x0 + x1) / 2, mz = (z0 + z1) / 2;
        if ((x1 - x0) >= (z1 - z0)) b.fill(mx, wallTop, z0, mx, wallTop, z1, Builder.log(p.trim(), Direction.Axis.Z));
        else b.fill(x0, wallTop, mz, x1, wallTop, mz, Builder.log(p.trim(), Direction.Axis.X));
        b.set(mx, wallTop - 1, mz, Builder.hangingLantern());
    }

    /** A lantern on an iron-black bracket beside a door: the bracket at y 3, the lantern hanging under it. */
    private void doorLamp(int x, int z) {
        b.set(x, 3, z, Blocks.DARK_OAK_FENCE);
        b.set(x, 2, z, Builder.hangingLantern());
    }

    private void wallBanner(int x, int y, int z, Block banner, Direction facing) {
        b.set(x, y, z, banner.defaultBlockState().setValue(WallBannerBlock.FACING, facing));
    }

    private static BlockState litCandles(int n) {
        return Blocks.CANDLE.defaultBlockState().setValue(BlockStateProperties.CANDLES, Math.max(1, Math.min(4, n))).setValue(BlockStateProperties.LIT, true);
    }

    private static BlockState lit(BlockState s) {
        return s.setValue(BlockStateProperties.LIT, true);
    }

    private static BlockState waterCauldron() {
        return Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 3);
    }

    /** A lamp post: a stone foot, a fence post and a lantern at y 3. */
    private void lamp(int x, int z) {
        b.set(x, 0, z, Blocks.STONE_BRICK_WALL);
        b.fill(x, 1, z, x, 2, z, Blocks.SPRUCE_FENCE);
        b.set(x, 3, z, Blocks.LANTERN);
    }

    /** A table (fence post with a top) between two chairs, at world coordinates; the chairs sit west and east or north and south. */
    private void table(int x, int z, Block fence, BlockState top, boolean alongX) {
        b.set(x, 0, z, fence);
        b.set(x, 1, z, top);
        Direction a = alongX ? Direction.WEST : Direction.NORTH;
        Direction c = a.getOpposite();
        b.set(x + a.getStepX(), 0, z + a.getStepZ(), Builder.stairs(Blocks.SPRUCE_STAIRS, a, false));
        b.set(x + c.getStepX(), 0, z + c.getStepZ(), Builder.stairs(Blocks.SPRUCE_STAIRS, c, false));
    }

    /** Flowers and bushes round a building's walls, leaving the door and its path free. */
    private void garden(int x0, int z0, int x1, int z1, int frontX, int frontZ) {
        for (int x = x0 - 1; x <= x1 + 1; x++)
            for (int z = z0 - 1; z <= z1 + 1; z++) {
                boolean ring = x == x0 - 1 || x == x1 + 1 || z == z0 - 1 || z == z1 + 1;
                if (!ring || Math.abs(x - frontX) + Math.abs(z - frontZ) <= 2) continue;
                if (!b.get(x, -1, z).is(Blocks.GRASS_BLOCK) || !b.get(x, 0, z).isAir()) continue;
                int h = hash(x * 3 + 1, z * 5 + 2) % 7;
                if (h == 0) b.bush(x, 0, z);
                else if (h <= 2) b.set(x, 0, z, FLOWERS[hash(x, z) % FLOWERS.length]);
            }
    }

    // ------------------------------------------------------------------ townhouses

    /** Townhouses filling the quarters between the streets, each furnished for its household. */
    private void houses() {
        Lot[] lots = {
                new Lot(22, -44, 30, -36, Palette.PLASTER, 1, WEAVER, 'B'),
                new Lot(34, -44, 42, -36, Palette.TIMBER, 2, COOPER, 'B'),
                new Lot(22, -30, 30, -22, Palette.STONE, 2, SCRIBE, 'B'),
                new Lot(34, -30, 42, -22, Palette.ELDER, 1, TAILOR, 'B'),
                new Lot(44, -18, 50, -10, Palette.PLASTER, 1, POTTER, 'R'),
                new Lot(28, 12, 36, 20, Palette.TIMBER, 2, FISHER, 'B'),
                new Lot(40, 12, 48, 20, Palette.STONE, 1, FAMILY, 'B'),
                new Lot(-50, -44, -42, -36, Palette.ELDER, 1, SCRIBE, 'B'),
                new Lot(-38, -44, -26, -36, Palette.PLASTER, 2, FAMILY, 'B'),
                new Lot(-50, 16, -40, 22, Palette.TIMBER, 1, WEAVER, 'B'),
                new Lot(-36, 16, -26, 22, Palette.STONE, 1, POTTER, 'B'),
                new Lot(-48, -32, -40, -24, Palette.ELDER, 2, COOPER, 'L'),
                new Lot(-34, -32, -24, -24, Palette.PLASTER, 1, TAILOR, 'R')};
        for (int i = 0; i < lots.length; i++) house(i, lots[i]);
    }

    private void house(int idx, Lot l) {
        int x0 = l.x0(), z0 = l.z0(), x1 = l.x1(), z1 = l.z1();
        Palette p = l.p();
        boolean two = l.storeys() == 2;
        int wallTop = two ? 8 : 4;
        // The door is on the side facing the nearer street, in the middle.
        int mx = (x0 + x1) / 2;
        boolean south = Math.abs(z1) < Math.abs(z0);
        int dz = south ? z1 : z0, out = south ? 1 : -1;
        Direction door = south ? Direction.SOUTH : Direction.NORTH;
        shell(x0, z0, x1, z1, wallTop, p, mx, dz);
        b.door(mx, 0, dz, p.door(), door);
        laneBlock(mx, dz + out);
        b.set(mx, 0, dz + out, Blocks.AIR);
        int ridge = b.gableRoof(x0, z0, x1, z1, wallTop, p);
        beamLantern(x0, z0, x1, z1, wallTop, p);
        if (two) b.set(mx, 3, (z0 + z1) / 2, Builder.hangingLantern());
        doorLamp(mx + 1, dz + out);
        Room r = new Room(b, new int[] {x0, z0, x1, z1}, door, mx, dz);
        int[] keep = chimney(r, l.chimney(), ridge, p);
        furnish(idx, l, r, keep);
        garden(x0, z0, x1, z1, mx, dz + out);
    }

    /**
     * A chimney outside one wall (back, left or right, in room terms) with a hearth in the wall at its
     * foot. Returns the room cell in front of the hearth, which furniture must leave free.
     */
    private int[] chimney(Room r, char side, int ridge, Palette p) {
        int W = r.width, D = r.depth;
        int cu, cv, ou, ov, ku, kv;
        if (side == 'L') {
            cu = -1; cv = D / 2; ou = -2; ov = cv; ku = 0; kv = cv;
        } else if (side == 'R') {
            cu = W; cv = D / 2; ou = W + 1; ov = cv; ku = W - 1; kv = cv;
        } else {
            cu = W / 2; cv = D; ou = cu; ov = D + 1; ku = cu; kv = D - 1;
        }
        int wx = r.worldX(cu, cv), wz = r.worldZ(cu, cv), ox = r.worldX(ou, ov), oz = r.worldZ(ou, ov);
        b.fill(ox, -1, oz, ox, ridge + 1, oz, p.chimney());
        b.campfire(ox, ridge + 2, oz, false);
        b.campfire(wx, 0, wz, false);
        b.set(wx, 1, wz, p.chimney());
        return new int[] {ku, kv};
    }

    /** Furniture placed in a room's cells (u across, v front to back) at one floor level, never twice in a cell. */
    private final class Interior {
        final Room r;
        final int y, W, D;
        final boolean[][] used;

        Interior(Room r, int y) {
            this.r = r;
            this.y = y;
            this.W = r.width;
            this.D = r.depth;
            this.used = new boolean[W][D];
            for (int u = 0; u < W; u++) for (int v = 0; v < D; v++) used[u][v] = r.doorway(u, v);
        }

        boolean free(int u, int v) {
            return u >= 0 && u < W && v >= 0 && v < D && !used[u][v];
        }

        void reserve(int u, int v) {
            if (u >= 0 && u < W && v >= 0 && v < D) used[u][v] = true;
        }

        boolean put(int u, int v, BlockState s) {
            if (!free(u, v)) return false;
            r.set(u, y, v, s);
            used[u][v] = true;
            return true;
        }

        boolean put(int u, int v, Block s) {
            return put(u, v, s.defaultBlockState());
        }

        void above(int u, int dy, int v, BlockState s) {
            r.set(u, y + dy, v, s);
        }

        boolean bed(int u, int v, Block bed, Direction head) {
            Direction foot = head.getOpposite();
            int fu = u + r.du(foot), fv = v + r.dv(foot);
            if (!free(u, v) || !free(fu, fv)) return false;
            b.bed(r.worldX(u, v), y, r.worldZ(u, v), bed, head);
            reserve(u, v);
            reserve(fu, fv);
            return true;
        }

        void carpet(int u0, int v0, int u1, int v1, Block c) {
            for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) if (free(u, v)) r.set(u, y, v, c.defaultBlockState());
        }

        void table(int u, int v, Block fence, BlockState top) {
            if (!put(u, v, fence)) return;
            above(u, 1, v, top);
            if (free(u - 1, v)) put(u - 1, v, Builder.stairs(Blocks.SPRUCE_STAIRS, r.left, false));
            if (free(u + 1, v)) put(u + 1, v, Builder.stairs(Blocks.SPRUCE_STAIRS, r.right, false));
        }
    }

    /** The household's own things along the right-hand wall. */
    private static BlockState[] trade(int trade, Direction toRoom) {
        return switch (trade) {
            case WEAVER -> new BlockState[] {Builder.facing(Blocks.LOOM, toRoom), Blocks.WHITE_WOOL.defaultBlockState(),
                    Blocks.LIGHT_BLUE_WOOL.defaultBlockState(), Builder.facing(Blocks.LOOM, toRoom)};
            case SCRIBE -> new BlockState[] {Blocks.CARTOGRAPHY_TABLE.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(),
                    Blocks.BOOKSHELF.defaultBlockState(), Builder.facing(Blocks.BARREL, toRoom)};
            case COOPER -> new BlockState[] {Builder.facing(Blocks.BARREL, Direction.UP), Builder.facing(Blocks.BARREL, toRoom),
                    Blocks.FLETCHING_TABLE.defaultBlockState(), Blocks.SMITHING_TABLE.defaultBlockState()};
            case POTTER -> new BlockState[] {Blocks.DECORATED_POT.defaultBlockState(), Blocks.TERRACOTTA.defaultBlockState(),
                    Builder.facing(Blocks.STONECUTTER, toRoom), Blocks.DECORATED_POT.defaultBlockState()};
            case TAILOR -> new BlockState[] {Builder.facing(Blocks.LOOM, toRoom), Builder.facing(Blocks.CHEST, toRoom),
                    Blocks.WHITE_WOOL.defaultBlockState(), Blocks.CARTOGRAPHY_TABLE.defaultBlockState()};
            case FISHER -> new BlockState[] {Builder.facing(Blocks.BARREL, toRoom), Blocks.DRIED_KELP_BLOCK.defaultBlockState(),
                    Builder.facing(Blocks.BARREL, Direction.UP), Blocks.COMPOSTER.defaultBlockState()};
            default -> new BlockState[] {Blocks.COMPOSTER.defaultBlockState(), Builder.facing(Blocks.CHEST, toRoom),
                    Builder.facing(Blocks.BARREL, toRoom)};
        };
    }

    private void furnish(int idx, Lot l, Room r, int[] keep) {
        boolean two = l.storeys() == 2;
        Interior g = new Interior(r, 0);
        int W = g.W, D = g.D, du = r.doorU;
        Direction F = r.front, B = r.back, L = r.left, R = r.right;
        g.reserve(keep[0], keep[1]);
        g.reserve(du, 2);
        if (two) {
            for (int y = 0; y <= 4; y++) r.set(0, y, D - 1, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, F));
            g.reserve(0, D - 1);
            g.reserve(0, D - 2);
            g.reserve(1, D - 1);
        }
        g.carpet(1, 2, W - 2, D - 2, CARPETS[idx % CARPETS.length]);
        Block bed = BEDS[idx % BEDS.length], bed2 = BEDS[(idx + 3) % BEDS.length];
        if (!two) {
            g.bed(0, D - 1, bed, B);
            if (W >= 9 || l.trade() == FAMILY) g.bed(1, D - 1, bed2, B);
            for (int u = 1; u <= 2; u++)
                if (g.put(u, D - 1, Builder.facing(Blocks.BARREL, Direction.UP))) {
                    g.above(u, 1, D - 1, litCandles(1 + idx % 3));
                    break;
                }
            if (idx % 2 == 0) g.above(0, 2, D - 1, Blocks.BLUE_WALL_BANNER.defaultBlockState().setValue(WallBannerBlock.FACING, F));
        }
        // The kitchen in the back corner: a stove, a water cauldron, a crate and the worktable.
        g.put(W - 1, D - 1, lit(Builder.facing(idx % 2 == 0 ? Blocks.SMOKER : Blocks.FURNACE, F)));
        g.put(W - 2, D - 1, waterCauldron());
        if (g.put(W - 1, D - 2, Builder.facing(Blocks.BARREL, L))) g.above(W - 1, 1, D - 2, POTS[idx % POTS.length].defaultBlockState());
        g.put(W - 3, D - 1, Blocks.CRAFTING_TABLE);
        // A chest and bookshelves along the left wall.
        int placed = 0;
        for (int v = D - 3; v >= 2; v--) {
            if (!g.free(0, v)) continue;
            if (placed == 0) g.put(0, v, Builder.facing(Blocks.CHEST, R));
            else {
                g.put(0, v, Blocks.BOOKSHELF);
                g.above(0, 1, v, Blocks.BOOKSHELF.defaultBlockState());
                if (placed == 1) g.above(0, 2, v, POTS[(idx + 5) % POTS.length].defaultBlockState());
            }
            placed++;
        }
        // The trade along the right wall.
        BlockState[] t = trade(l.trade(), L);
        int[][] spots = {{W - 1, 2}, {W - 1, 3}, {W - 1, 4}, {W - 2, 2}};
        int k = 0;
        for (int[] s : spots) if (k < t.length && g.put(s[0], s[1], t[k])) k++;
        // The table with a chair either side.
        int tv = Math.min(D - 2, Math.max(3, D / 2 + 1));
        Block fence = darkWood(l) ? Blocks.DARK_OAK_FENCE : Blocks.SPRUCE_FENCE;
        BlockState top = idx % 3 == 0 ? litCandles(2) : idx % 3 == 1 ? POTS[(idx + 2) % POTS.length].defaultBlockState()
                : Blocks.SPRUCE_PRESSURE_PLATE.defaultBlockState();
        g.table(du, tv, fence, top);
        if (!two) return;
        // Upstairs: the bedrooms.
        Interior up = new Interior(r, 5);
        up.reserve(0, D - 1);
        up.reserve(1, D - 1);
        up.reserve(0, D - 2);
        up.carpet(1, 1, W - 2, D - 2, CARPETS[(idx + 4) % CARPETS.length]);
        up.bed(W - 1, D - 1, bed, B);
        up.bed(W - 3, D - 1, bed2, B);
        if (up.put(W - 2, D - 1, Builder.facing(Blocks.BARREL, Direction.UP))) up.above(W - 2, 1, D - 1, litCandles(2));
        up.put(W - 1, D - 3, Builder.facing(Blocks.CHEST, L));
        if (up.put(0, 2, Blocks.BOOKSHELF)) up.above(0, 1, 2, POTS[(idx + 1) % POTS.length].defaultBlockState());
        up.put(0, 3, Builder.facing(Blocks.CHEST, R));
        up.put(W - 1, 2, l.trade() == WEAVER || l.trade() == TAILOR ? Builder.facing(Blocks.LOOM, L) : Blocks.BOOKSHELF.defaultBlockState());
        up.table(W / 2, 2, fence, litCandles(1));
    }

    private static boolean darkWood(Lot l) {
        return l.p() == Palette.ELDER || l.p() == Palette.PLASTER;
    }

    // ------------------------------------------------------------------ civic buildings

    /** The Chapel of the Six Lights: pews, an altar with candles, blue glass and a bell turret on the ridge. */
    private void chapel() {
        int x0 = 20, z0 = -18, x1 = 28, z1 = -9, top = 7;
        shell(x0, z0, x1, z1, top, CHAPEL, 24, z1);
        // Tall windows.
        for (int z : new int[] {-16, -12}) for (int x : new int[] {x0, x1}) b.fill(x, 3, z, x, 4, z, CHAPEL.window());
        for (int x : new int[] {22, 26}) b.fill(x, 1, z1, x, 4, z1, Blocks.WHITE_STAINED_GLASS_PANE);
        b.door(24, 0, z1, Blocks.DARK_OAK_DOOR, Direction.SOUTH);
        int ridge = b.gableRoof(x0, z0, x1, z1, top, CHAPEL);
        // The aisle, the pews and the altar.
        for (int z = -16; z <= -10; z++) b.set(24, 0, z, Blocks.BLUE_CARPET);
        for (int z : new int[] {-13, -11})
            for (int x : new int[] {21, 22, 23, 25, 26, 27}) b.set(x, 0, z, Builder.stairs(Blocks.DARK_OAK_STAIRS, Direction.SOUTH, false));
        b.fill(23, 0, -17, 25, 0, -17, Blocks.CHISELED_QUARTZ_BLOCK);
        b.set(23, 1, -17, litCandles(3));
        b.set(25, 1, -17, litCandles(3));
        b.set(24, 1, -17, Blocks.POTTED_LILY_OF_THE_VALLEY);
        b.set(24, 0, -15, Builder.facing(Blocks.LECTERN, Direction.SOUTH));
        b.set(21, 0, -17, Blocks.WHITE_BANNER);
        b.set(27, 0, -17, Blocks.BLUE_BANNER);
        wallBanner(22, 4, -17, Blocks.WHITE_WALL_BANNER, Direction.SOUTH);
        wallBanner(26, 4, -17, Blocks.BLUE_WALL_BANNER, Direction.SOUTH);
        b.set(21, 0, -10, Blocks.POTTED_AZALEA);
        b.set(27, 0, -10, Blocks.POTTED_AZALEA);
        // A beam across the nave carries a chandelier and two lanterns.
        b.fill(x0, top, -13, x1, top, -13, Builder.log(CHAPEL.trim(), Direction.Axis.X));
        chandelier(24, top, -13);
        b.set(21, top - 1, -13, Builder.hangingLantern());
        b.set(27, top - 1, -13, Builder.hangingLantern());
        // The bell turret over the front gable.
        b.set(24, ridge, -10, Blocks.STONE_BRICKS);
        b.set(24, ridge + 1, -10, Blocks.BELL);
        for (int[] c : new int[][] {{23, -11}, {25, -11}, {23, -9}, {25, -9}}) b.fill(c[0], ridge, c[1], c[0], ridge + 2, c[1], Blocks.STONE_BRICK_WALL);
        b.fill(23, ridge + 3, -11, 25, ridge + 3, -9, Builder.slab(Blocks.DEEPSLATE_TILE_SLAB, false));
        b.set(24, ridge + 4, -10, Blocks.LIGHTNING_ROD);
        // The front: a sign, banners and a lamp.
        b.sign(21, 2, z1 + 1, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "", "Chapel of", "the Six Lights", "");
        wallBanner(21, 5, z1 + 1, Blocks.BLUE_WALL_BANNER, Direction.SOUTH);
        wallBanner(27, 5, z1 + 1, Blocks.WHITE_WALL_BANNER, Direction.SOUTH);
        doorLamp(25, z1 + 1);
        garden(x0, z0, x1, z1, 24, z1 + 1);
    }

    /** The Silver Stag, inn and tavern: a bar, tables and a hearth below, guest rooms above. */
    private void tavern() {
        int x0 = 34, z0 = -17, x1 = 41, z1 = -8;
        Palette p = Palette.TIMBER;
        shell(x0, z0, x1, z1, 8, p, 37, z1);
        b.door(37, 0, z1, p.door(), Direction.SOUTH);
        int ridge = b.gableRoof(x0, z0, x1, z1, 8, p);
        b.fill(x0, 8, -12, x1, 8, -12, Builder.log(p.trim(), Direction.Axis.X));
        b.set(37, 7, -12, Builder.hangingLantern());
        b.set(37, 3, -12, Builder.hangingLantern());
        b.set(36, 3, -15, Builder.hangingLantern());
        b.ladder(35, 0, 4, -16, Direction.EAST);
        // The hearth in the north wall.
        b.fill(36, -1, z0 - 1, 36, ridge + 1, z0 - 1, Blocks.BRICKS);
        b.campfire(36, ridge + 2, z0 - 1, false);
        b.campfire(36, 0, z0, false);
        b.set(36, 1, z0, Blocks.BRICKS);
        // The bar: a counter, casks behind it and a brewing stand.
        for (int z = -15; z <= -11; z++) b.set(39, 0, z, Blocks.STRIPPED_SPRUCE_WOOD);
        b.set(39, 1, -13, litCandles(3));
        b.set(39, 1, -11, Blocks.POTTED_FERN);
        for (int z = -16; z <= -11; z++) b.set(40, 0, z, Builder.facing(Blocks.BARREL, Direction.WEST));
        for (int z = -16; z <= -14; z++) b.set(40, 1, z, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(40, 1, -12, Blocks.BREWING_STAND);
        // Tables in the taproom.
        table(36, -14, Blocks.SPRUCE_FENCE, litCandles(2), true);
        table(36, -11, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_PRESSURE_PLATE.defaultBlockState(), true);
        for (int z = -16; z <= -9; z++) if (z != -14 && z != -11) b.set(38, 0, z, Blocks.RED_CARPET);
        // Upstairs: four guest beds, chests and a runner.
        b.bed(35, 5, -14, Blocks.RED_BED, Direction.WEST);
        b.bed(35, 5, -11, Blocks.BLUE_BED, Direction.WEST);
        b.bed(40, 5, -15, Blocks.WHITE_BED, Direction.EAST);
        b.bed(40, 5, -11, Blocks.LIGHT_BLUE_BED, Direction.EAST);
        b.set(40, 5, -13, Builder.facing(Blocks.CHEST, Direction.WEST));
        b.set(35, 5, -9, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(35, 6, -9, litCandles(2));
        b.set(40, 5, -9, Blocks.POTTED_RED_TULIP);
        for (int z = -16; z <= -9; z++) for (int x = 37; x <= 38; x++) b.set(x, 5, z, Blocks.RED_CARPET);
        // The front.
        b.sign(35, 2, z1 + 1, p.wallSign(), Direction.SOUTH, "", "The Silver Stag", "Inn & Tavern", "");
        doorLamp(38, z1 + 1);
        b.set(34, 0, z1 + 1, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(35, 0, z1 + 1, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
        b.set(36, 0, z1 + 1, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
    }

    /** The Royal Guard's barracks: bunks, arms and the duty table, under a flat battlemented roof. */
    private void barracks() {
        int x0 = 44, z0 = -31, x1 = 52, z1 = -22;
        Palette p = Palette.GUARD;
        shell(x0, z0, x1, z1, 5, p, 48, z1);
        b.door(48, 0, z1, p.door(), Direction.SOUTH);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                b.set(x, 5, z, Blocks.STONE_BRICKS);
                if (edge && (x + z) % 2 == 0) b.set(x, 6, z, Blocks.STONE_BRICK_WALL);
            }
        for (int[] c : new int[][] {{x0, z0}, {x1, z0}, {x0, z1}, {x1, z1}}) {
            b.set(c[0], 6, c[1], Blocks.STONE_BRICK_WALL);
            b.set(c[0], 7, c[1], Blocks.LANTERN);
        }
        for (int z : new int[] {-30, -28, -26}) {
            b.bed(45, 0, z, Blocks.BLUE_BED, Direction.WEST);
            b.bed(51, 0, z, Blocks.BLUE_BED, Direction.EAST);
        }
        b.set(45, 0, -29, Builder.facing(Blocks.CHEST, Direction.EAST));
        b.set(51, 0, -29, Builder.facing(Blocks.CHEST, Direction.WEST));
        b.set(45, 0, -27, Builder.facing(Blocks.BARREL, Direction.EAST));
        b.set(51, 0, -27, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(45, 0, -24, Blocks.CARTOGRAPHY_TABLE);
        b.set(45, 0, -23, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(45, 1, -23, litCandles(2));
        b.set(51, 0, -24, Blocks.GRINDSTONE);
        b.set(51, 0, -23, Builder.facing(Blocks.CHEST, Direction.WEST));
        table(47, -25, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_PRESSURE_PLATE.defaultBlockState(), false);
        for (int z = -30; z <= -23; z++) if (z != -26 && z != -25 && z != -24) b.set(48, 0, z, Blocks.BLUE_CARPET);
        b.set(48, 4, -26, Builder.hangingLantern());
        b.set(48, 4, -29, Builder.hangingLantern());
        b.armorStand(50.5, 0, -24.5, 90, new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_CHESTPLATE),
                new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_BOOTS));
        b.armorStand(49.5, 0, -30.5, 0, new ItemStack(Items.CHAINMAIL_HELMET), new ItemStack(Items.CHAINMAIL_CHESTPLATE),
                ItemStack.EMPTY, new ItemStack(Items.IRON_BOOTS));
        wallBanner(46, 4, z1 + 1, Blocks.BLUE_WALL_BANNER, Direction.SOUTH);
        wallBanner(50, 4, z1 + 1, Blocks.BLACK_WALL_BANNER, Direction.SOUTH);
        b.sign(45, 2, z1 + 1, p.wallSign(), Direction.SOUTH, "", "Barracks of the", "Royal Guard", "");
        doorLamp(49, z1 + 1);
        // Training dummies by the west wall.
        for (int z : new int[] {-29, -26}) {
            b.set(43, -1, z, Blocks.COARSE_DIRT);
            b.fill(43, 0, z, 43, 1, z, Blocks.SPRUCE_FENCE);
            b.set(43, 2, z, Builder.facing(Blocks.CARVED_PUMPKIN, Direction.WEST));
        }
    }

    /** The royal granary: hay, sacks and casks for the city's stores. */
    private void granary() {
        int x0 = 45, z0 = -44, x1 = 51, z1 = -37;
        Palette p = Palette.FARM;
        shell(x0, z0, x1, z1, 5, p, 48, z1);
        b.door(48, 0, z1, p.door(), Direction.SOUTH);
        b.gableRoof(x0, z0, x1, z1, 5, p);
        beamLantern(x0, z0, x1, z1, 5, p);
        lane(48, z1 + 1, 48, z1 + 2);
        b.fill(46, 0, -43, 47, 1, -42, Blocks.HAY_BLOCK);
        b.fill(49, 0, -43, 50, 2, -43, Blocks.HAY_BLOCK);
        b.set(46, 0, -40, Builder.facing(Blocks.BARREL, Direction.EAST));
        b.set(46, 1, -40, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(46, 0, -39, Builder.facing(Blocks.BARREL, Direction.EAST));
        b.set(46, 0, -38, Blocks.COMPOSTER);
        b.set(50, 0, -40, Builder.facing(Blocks.CHEST, Direction.WEST));
        b.set(50, 0, -39, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.fill(50, 0, -41, 50, 1, -41, Blocks.SCAFFOLDING);
        b.set(48, 0, -42, Blocks.HAY_BLOCK);
        b.sign(46, 2, z1 + 1, p.wallSign(), Direction.SOUTH, "", "Royal Granary", "", "");
        doorLamp(49, z1 + 1);
    }

    /** The Warm Hearth bakery by the harbour street: ovens, flour, a cake on the counter. */
    private void bakery() {
        int x0 = 5, z0 = 16, x1 = 11, z1 = 24;
        Palette p = Palette.PLASTER;
        shell(x0, z0, x1, z1, 5, p, x0, 20);
        b.door(x0, 0, 20, p.door(), Direction.WEST);
        int ridge = b.gableRoof(x0, z0, x1, z1, 5, p);
        b.fill(x0, 5, 20, x1, 5, 20, Builder.log(p.trim(), Direction.Axis.X));
        b.set(8, 4, 20, Builder.hangingLantern());
        // The oven and its chimney on the east wall.
        b.fill(x1 + 1, -1, 21, x1 + 1, ridge + 1, 21, Blocks.BRICKS);
        b.campfire(x1 + 1, ridge + 2, 21, false);
        b.campfire(x1, 0, 21, false);
        b.set(x1, 1, 21, Blocks.BRICKS);
        b.set(10, 0, 18, lit(Builder.facing(Blocks.FURNACE, Direction.WEST)));
        b.set(10, 0, 19, lit(Builder.facing(Blocks.SMOKER, Direction.WEST)));
        b.fill(10, 1, 18, 10, 1, 19, Blocks.BRICKS);
        b.set(10, 0, 22, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(10, 0, 23, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(10, 1, 23, Builder.facing(Blocks.BARREL, Direction.UP));
        // Flour and wheat, the counter with bread for sale, the baker's bed.
        b.set(6, 0, 17, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(7, 0, 17, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(8, 0, 17, Blocks.HAY_BLOCK);
        b.set(9, 0, 17, Blocks.CRAFTING_TABLE);
        b.set(8, 0, 20, Blocks.STRIPPED_SPRUCE_WOOD);
        b.set(8, 0, 21, Blocks.STRIPPED_SPRUCE_WOOD);
        b.set(8, 1, 20, Blocks.CAKE);
        b.set(8, 1, 21, Blocks.POTTED_DANDELION);
        b.bed(6, 0, 23, Blocks.YELLOW_BED, Direction.SOUTH);
        b.set(7, 0, 23, Builder.facing(Blocks.CHEST, Direction.NORTH));
        b.set(8, 0, 23, Blocks.HAY_BLOCK);
        b.sign(x0 - 1, 2, 17, p.wallSign(), Direction.WEST, "", "Warm Hearth", "Bakery", "");
        doorLamp(x0 - 1, 21);
        b.set(x0 - 1, 0, 17, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(x0 - 1, 0, 23, Blocks.HAY_BLOCK);
        garden(x0, z0, x1, z1, x0 - 1, 20);
    }

    /** The Ironbridge forge: an open front, blast furnaces at a hearth, anvil, grindstone and quench. */
    private void smithy() {
        int x0 = 14, z0 = 16, x1 = 22, z1 = 24;
        Palette p = Palette.STONE;
        shell(x0, z0, x1, z1, 5, p, 18, z0);
        b.fill(17, 0, z0, 19, 2, z0, Blocks.AIR);
        int ridge = b.gableRoof(x0, z0, x1, z1, 5, p);
        beamLantern(x0, z0, x1, z1, 5, p);
        b.set(18, 4, 17, Builder.hangingLantern());
        b.fill(16, -1, z1 + 1, 16, ridge + 1, z1 + 1, Blocks.STONE_BRICKS);
        b.campfire(16, ridge + 2, z1 + 1, false);
        b.campfire(16, 0, z1, false);
        b.set(16, 1, z1, Blocks.STONE_BRICKS);
        b.set(15, 0, 23, lit(Builder.facing(Blocks.BLAST_FURNACE, Direction.NORTH)));
        b.set(17, 0, 23, lit(Builder.facing(Blocks.BLAST_FURNACE, Direction.NORTH)));
        b.set(15, 1, 23, Blocks.STONE_BRICKS);
        b.set(17, 1, 23, Blocks.STONE_BRICKS);
        b.set(18, 0, 21, Builder.facing(Blocks.ANVIL, Direction.EAST));
        b.set(20, 0, 20, Blocks.GRINDSTONE);
        b.set(21, 0, 23, Blocks.SMITHING_TABLE);
        b.set(21, 0, 22, Builder.facing(Blocks.CHEST, Direction.WEST));
        b.set(21, 0, 21, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(15, 0, 21, waterCauldron());
        b.set(15, 0, 19, Builder.facing(Blocks.STONECUTTER, Direction.EAST));
        b.set(15, 0, 18, Builder.facing(Blocks.BARREL, Direction.UP));
        b.armorStand(20.5, 0, 18.5, 180, new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_CHESTPLATE), ItemStack.EMPTY, ItemStack.EMPTY);
        b.sign(15, 2, z0 - 1, p.wallSign(), Direction.NORTH, "", "Ironbridge", "Forge", "");
        garden(x0, z0, x1, z1, 18, z0 - 1);
    }

    /** The gate watch house under the east wall: its flat roof is part of the wall walk. */
    private void watchHouse() {
        int x0 = 50, z0 = 12, x1 = 53, z1 = 21;
        Palette p = Palette.GUARD;
        shell(x0, z0, x1, z1, 4, p, 51, z0);
        b.door(51, 0, z0, p.door(), Direction.NORTH);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                b.set(x, 4, z, Blocks.STONE_BRICKS);
                if (x == x0 && (x + z) % 2 == 0) b.set(x, 5, z, Blocks.STONE_BRICK_WALL);
            }
        b.bed(52, 0, 20, Blocks.BLUE_BED, Direction.SOUTH);
        b.bed(52, 0, 17, Blocks.BLUE_BED, Direction.SOUTH);
        b.set(52, 0, 18, Builder.facing(Blocks.CHEST, Direction.WEST));
        b.set(52, 0, 15, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(52, 0, 14, Blocks.CRAFTING_TABLE);
        b.set(52, 0, 13, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(52, 1, 13, litCandles(1));
        b.set(51, 3, 15, Builder.hangingLantern());
        b.set(51, 3, 19, Builder.hangingLantern());
        wallBanner(x0 - 1, 3, 16, Blocks.BLUE_WALL_BANNER, Direction.WEST);
        doorLamp(52, z0 - 1);
    }

    /** The apothecary: brewing stands, a cauldron, herbs in pots. */
    private void apothecary() {
        int x0 = -23, z0 = 16, x1 = -17, z1 = 24;
        Palette p = Palette.BIRCH;
        shell(x0, z0, x1, z1, 5, p, -20, z0);
        b.door(-20, 0, z0, p.door(), Direction.NORTH);
        b.gableRoof(x0, z0, x1, z1, 5, p);
        beamLantern(x0, z0, x1, z1, 5, p);
        for (int x = -21; x <= -19; x++) for (int z = 19; z <= 22; z++) b.set(x, 0, z, Blocks.GREEN_CARPET);
        b.set(-22, 0, 19, Blocks.BREWING_STAND);
        b.set(-22, 0, 20, waterCauldron());
        b.set(-22, 0, 21, Builder.facing(Blocks.BARREL, Direction.EAST));
        b.fill(-22, 0, 22, -22, 1, 23, Blocks.BOOKSHELF);
        b.set(-22, 2, 22, Blocks.POTTED_FERN);
        b.set(-22, 2, 23, Blocks.POTTED_BLUE_ORCHID);
        b.set(-18, 0, 19, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(-18, 1, 19, Blocks.POTTED_FERN);
        b.set(-18, 0, 20, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(-18, 1, 20, Blocks.POTTED_AZALEA);
        b.set(-18, 0, 21, Blocks.COMPOSTER);
        b.bed(-18, 0, 23, Blocks.GREEN_BED, Direction.SOUTH);
        b.set(-20, 0, 21, Blocks.BIRCH_FENCE);
        b.set(-20, 1, 21, Blocks.POTTED_RED_TULIP);
        b.set(-21, 0, 21, Builder.stairs(Blocks.BIRCH_STAIRS, Direction.WEST, false));
        b.set(-21, 0, 23, Builder.facing(Blocks.CHEST, Direction.NORTH));
        b.set(-19, 0, 23, Blocks.CRAFTING_TABLE);
        b.sign(-22, 2, z0 - 1, p.wallSign(), Direction.NORTH, "", "Herbs &", "Remedies", "");
        doorLamp(-19, z0 - 1);
        garden(x0, z0, x1, z1, -20, z0 - 1);
    }

    /** The net loft: the fishers' barrels, kelp, a smoker and a hearth. */
    private void netLoft() {
        int x0 = -14, z0 = 16, x1 = -6, z1 = 24;
        Palette p = Palette.TIMBER;
        shell(x0, z0, x1, z1, 5, p, -10, z0);
        b.door(-10, 0, z0, p.door(), Direction.NORTH);
        int ridge = b.gableRoof(x0, z0, x1, z1, 5, p);
        beamLantern(x0, z0, x1, z1, 5, p);
        b.fill(-12, -1, z1 + 1, -12, ridge + 1, z1 + 1, p.chimney());
        b.campfire(-12, ridge + 2, z1 + 1, false);
        b.campfire(-12, 0, z1, false);
        b.set(-12, 1, z1, p.chimney());
        for (int z = 19; z <= 22; z++) {
            b.set(-13, 0, z, Builder.facing(Blocks.BARREL, Direction.EAST));
            if (z % 2 == 0) b.set(-13, 1, z, Builder.facing(Blocks.BARREL, Direction.UP));
        }
        b.bed(-13, 0, 18, Blocks.CYAN_BED, Direction.WEST);
        b.set(-13, 0, 17, Builder.facing(Blocks.CHEST, Direction.EAST));
        b.set(-7, 0, 23, lit(Builder.facing(Blocks.SMOKER, Direction.NORTH)));
        b.set(-8, 0, 23, Builder.facing(Blocks.BARREL, Direction.NORTH));
        b.fill(-7, 0, 19, -7, 1, 19, Blocks.DRIED_KELP_BLOCK);
        b.set(-7, 0, 20, Blocks.DRIED_KELP_BLOCK);
        b.set(-7, 0, 21, waterCauldron());
        table(-10, 21, Blocks.SPRUCE_FENCE, litCandles(1), true);
        b.sign(-13, 2, z0 - 1, p.wallSign(), Direction.NORTH, "", "Net Loft", "Fresh fish daily", "");
        doorLamp(-9, z0 - 1);
        garden(x0, z0, x1, z1, -10, z0 - 1);
    }

    /** The copyists' house beside the archive: shelves, a writing desk and a bed. */
    private void copyHouse() {
        int x0 = -20, z0 = 5, x1 = -14, z1 = 11;
        Palette p = Palette.ELDER;
        shell(x0, z0, x1, z1, 4, p, -17, z0);
        b.door(-17, 0, z0, p.door(), Direction.NORTH);
        b.gableRoof(x0, z0, x1, z1, 4, p);
        beamLantern(x0, z0, x1, z1, 4, p);
        for (int x = -18; x <= -16; x++) for (int z = 7; z <= 9; z++) b.set(x, 0, z, Blocks.RED_CARPET);
        for (int x = -19; x <= -15; x++) if (x != -17) b.fill(x, 0, 10, x, 1, 10, Blocks.BOOKSHELF);
        b.set(-17, 0, 10, Blocks.CARTOGRAPHY_TABLE);
        b.set(-19, 0, 8, Blocks.DARK_OAK_PLANKS);
        b.set(-19, 1, 8, litCandles(2));
        b.set(-18, 0, 8, Builder.stairs(Blocks.DARK_OAK_STAIRS, Direction.EAST, false));
        b.bed(-15, 0, 7, Blocks.WHITE_BED, Direction.NORTH);
        b.set(-15, 0, 9, Builder.facing(Blocks.CHEST, Direction.WEST));
        b.set(-19, 0, 6, Blocks.POTTED_FERN);
        b.sign(-19, 2, z0 - 1, p.wallSign(), Direction.NORTH, "", "Copyists'", "House", "");
        doorLamp(-16, z0 - 1);
        garden(x0, z0, x1, z1, -17, z0 - 1);
    }

    // ------------------------------------------------------------------ gardens and the yard

    /** A stone well with water, a roof on four posts and a hanging chain. */
    private void well(int cx, int cz) {
        for (int x = cx - 1; x <= cx + 1; x++)
            for (int z = cz - 1; z <= cz + 1; z++) {
                b.set(x, -1, z, Blocks.STONE_BRICKS);
                b.set(x, 0, z, Blocks.STONE_BRICKS);
                b.set(x, 4, z, Builder.slab(Blocks.SPRUCE_SLAB, false));
            }
        b.fill(cx, -4, cz, cx, 0, cz, Blocks.WATER);
        b.set(cx, -5, cz, Blocks.STONE_BRICKS);
        for (int[] c : new int[][] {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}}) b.fill(cx + c[0], 1, cz + c[1], cx + c[0], 3, cz + c[1], Blocks.SPRUCE_FENCE);
        b.set(cx, 3, cz, Blocks.CHAIN);
        b.set(cx, 2, cz, Builder.hangingLantern());
    }

    private void bench(int x, int z, Direction back) {
        b.set(x, 0, z, Builder.stairs(Blocks.SPRUCE_STAIRS, back, false));
    }

    private void tree(int x, int z, boolean birch) {
        b.set(x, -1, z, Blocks.GRASS_BLOCK);
        b.broadleaf(x, 0, z, 5, birch ? Blocks.BIRCH_LOG : Blocks.OAK_LOG, birch ? Blocks.BIRCH_LEAVES : Blocks.OAK_LEAVES);
    }

    private void flowerBed(int x0, int z0, int x1, int z1) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                if (!b.get(x, 0, z).isAir()) continue;
                b.set(x, -1, z, Blocks.GRASS_BLOCK);
                b.set(x, 0, z, FLOWERS[hash(x, z) % FLOWERS.length]);
            }
    }

    /** Wells, gardens, the orchard and the trees about the plaza. */
    private void gardens() {
        // The west garden between the archive and the plaza.
        well(-17, -10);
        tree(-19, -14, false);
        tree(-15, -5, true);
        flowerBed(-20, -7, -19, -5);
        bench(-14, -11, Direction.EAST);
        bench(-14, -10, Direction.EAST);
        // The north well, by the inner lanes of the west quarter.
        well(-21, -27);
        flowerBed(-20, -31, -19, -30);
        // The orchard and beehives below the north wall.
        tree(-23, -42, false);
        tree(-21, -38, false);
        b.set(-25, 0, -40, Builder.facing(Blocks.BEEHIVE, Direction.EAST));
        b.set(-25, 0, -44, Builder.facing(Blocks.BEEHIVE, Direction.EAST));
        flowerBed(-20, -44, -19, -41);
        bench(-24, -37, Direction.SOUTH);
        bench(-23, -37, Direction.SOUTH);
        // The south-east garden and its well.
        well(25, 18);
        tree(26, 13, true);
        tree(24, 21, false);
        flowerBed(27, 16, 27, 20);
        // Trees by the palace and in the plaza's corners, outside the plaza itself.
        tree(12, -12, false);
        tree(-12, -12, false);
        tree(11, 11, true);
        tree(-11, 11, true);
        flowerBed(-16, -13, -14, -13);
        flowerBed(14, -13, 16, -13);
        tree(20, -40, false);
        tree(20, -27, true);
        // Banner poles on the palace forecourt.
        for (int i = 0; i < 4; i++) {
            int x = i < 2 ? -7 - 5 * i : 7 + 5 * (i - 2);
            b.fill(x, 0, -17, x, 3, -17, Blocks.STONE_BRICK_WALL);
            b.set(x, 4, -17, ELDER_STANDING[i]);
        }
        // Benches along the quayside strip of the west quarter.
        for (int x = -48; x <= -28; x += 10) {
            bench(x, 25, Direction.NORTH);
            bench(x + 1, 25, Direction.NORTH);
        }
    }

    /** The fish market and cargo on the harbour yard behind the quay. */
    private void harbourYard() {
        for (int[] s : new int[][] {{28, 23}, {41, 23}}) {
            int x = s[0], z = s[1];
            for (int dx = 0; dx <= 3; dx++)
                for (int dz = 0; dz <= 2; dz++) {
                    boolean post = (dx == 0 || dx == 3) && (dz == 0 || dz == 2);
                    if (post) b.fill(x + dx, 0, z + dz, x + dx, 2, z + dz, Blocks.SPRUCE_FENCE);
                    b.set(x + dx, 3, z + dz, dx % 2 == 0 ? Blocks.CYAN_WOOL : Blocks.WHITE_WOOL);
                }
            b.set(x + 1, 0, z, Builder.facing(Blocks.BARREL, Direction.UP));
            b.set(x + 2, 0, z, waterCauldron());
            b.set(x + 1, 0, z + 2, Blocks.DRIED_KELP_BLOCK);
            b.set(x + 2, 0, z + 2, Builder.facing(Blocks.BARREL, Direction.NORTH));
            b.set(x + 1, 2, z + 1, Builder.hangingLantern());
        }
        for (int[] c : new int[][] {{46, 24}, {47, 24}, {47, 25}, {46, 25}, {24, 25}, {25, 25}, {24, 24}}) b.set(c[0], 0, c[1], Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(47, 1, 25, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(33, 0, 25, Blocks.CHEST);
        b.set(34, 0, 25, Blocks.HAY_BLOCK);
    }

    /** Fields and trees outside the east wall. */
    private void outskirts() {
        for (int[] f : new int[][] {{58, -22, 65, -6, -14}, {58, 6, 65, 22, 14}}) {
            for (int x = f[0]; x <= f[2]; x++)
                for (int z = f[1]; z <= f[3]; z++) {
                    b.set(x, 0, z, Blocks.AIR);
                    if (z == f[4]) {
                        b.set(x, -1, z, Blocks.WATER);
                        continue;
                    }
                    b.set(x, -1, z, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7));
                    Block crop = x <= 60 ? Blocks.WHEAT : x <= 62 ? Blocks.CARROTS : Blocks.POTATOES;
                    b.set(x, 0, z, crop.defaultBlockState().setValue(CropBlock.AGE, 5 + hash(x, z) % 3));
                }
            for (int x = f[0] - 1; x <= f[2] + 1; x++) {
                b.set(x, 0, f[1] - 1, Blocks.SPRUCE_FENCE);
                b.set(x, 0, f[3] + 1, Blocks.SPRUCE_FENCE);
            }
            for (int z = f[1]; z <= f[3]; z++) {
                b.set(f[0] - 1, 0, z, Blocks.SPRUCE_FENCE);
                b.set(f[2] + 1, 0, z, Blocks.SPRUCE_FENCE);
            }
            // A scarecrow in each field.
            int sz = f[4] + 4;
            b.fill(61, 0, sz, 61, 1, sz, Blocks.SPRUCE_FENCE);
            b.set(61, 2, sz, Builder.facing(Blocks.CARVED_PUMPKIN, Direction.WEST));
        }
        b.set(58, 0, -27, Blocks.HAY_BLOCK);
        b.set(59, 0, -27, Blocks.HAY_BLOCK);
        b.set(58, 1, -27, Blocks.HAY_BLOCK);
        for (int[] t : new int[][] {{60, -36}, {64, -44}, {59, -45}, {63, -31}, {62, 33}, {64, 40}, {63, 48}})
            tree(t[0], t[1], (t[0] + t[1]) % 2 == 0);
    }

    // ------------------------------------------------------------------ street lights

    /** A banner arch over the main street: two posts, a beam, a hanging lantern and Eldervale's banners. */
    private void arch(int x, int i) {
        for (int z : new int[] {-4, 4}) b.fill(x, 0, z, x, 5, z, Blocks.STONE_BRICKS);
        b.fill(x, 5, -3, x, 5, 3, Builder.log(Blocks.DARK_OAK_LOG, Direction.Axis.Z));
        b.fill(x, 6, -4, x, 6, 4, Builder.slab(Blocks.STONE_BRICK_SLAB, false));
        b.set(x, 4, 0, Blocks.CHAIN);
        b.set(x, 3, 0, Builder.hangingLantern());
        for (int z : new int[] {-2, 2}) {
            wallBanner(x + 1, 5, z, ELDER_BANNERS[(i + (z > 0 ? 1 : 0)) % 4], Direction.EAST);
            wallBanner(x - 1, 5, z, ELDER_BANNERS[(i + (z > 0 ? 3 : 2)) % 4], Direction.WEST);
        }
    }

    /** Arches and lamp posts along the main street, and lamps along every lane. */
    private void lamps() {
        int[] arches = {14, 22, 30, 38, 46};
        for (int i = 0; i < arches.length; i++) arch(arches[i], i);
        for (int x : new int[] {18, 26, 34, 42, 50}) for (int z : new int[] {-4, 4}) lamp(x, z);
        b.set(53, 3, 0, Builder.hangingLantern());
        // The plaza's edge.
        for (int[] l : new int[][] {{10, -10}, {-10, -10}, {10, 10}, {-10, 10}, {-13, -5}, {-13, 5}, {13, -6}}) lamp(l[0], l[1]);
        // The lanes.
        int[][] lanes = {
                // north-east
                {19, -35}, {28, -31}, {44, -35}, {52, -35}, {19, -22}, {30, -18}, {43, -22}, {52, -18},
                {19, -44}, {19, -28}, {34, -45}, {30, -9}, {9, -14}, {16, -16},
                // north-west
                {-19, -45}, {-19, -37}, {-19, -29}, {-26, -35}, {-34, -35}, {-52, -35}, {-50, -23},
                {-42, -46}, {-23, -19}, {-39, -26}, {-39, -30}, {-9, -14}, {-16, -16},
                // south
                {-24, 15}, {-15, 13}, {-4, 16}, {4, 16}, {13, 15}, {23, 16}, {28, 21},
                {36, 21}, {40, 22}, {27, 8}, {35, 8}, {49, 8}, {-52, 17}, {-4, 23}, {-40, 25}, {-30, 25}, {-20, 25}, {-10, 25}};
        for (int[] l : lanes) lamp(l[0], l[1]);
        // Lanterns on brackets where a lane runs between house walls, and under the walkway over the west alley.
        for (int[] l : new int[][] {{31, -40}, {33, -27}, {-39, -40}, {39, 16}, {42, -12}, {-44, -33}, {-48, 13}, {-38, 13}, {-28, 13}}) doorLamp(l[0], l[1]);
        for (int z = -14; z <= 10; z += 8) b.set(-53, 3, z, Builder.hangingLantern());
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
