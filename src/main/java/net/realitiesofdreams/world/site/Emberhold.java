package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;

/**
 * Emberhold, the volcanic seat of Veyrath (Scene 015), anchored at MC-001 block (-560, 72, -60) on the
 * terrain's "emberhold" plateau; everything here is relative to it, with y = 0 the street.
 *
 * A square walled city (walls at Chebyshev distance 55..56 from the anchor) inside a lava moat (lava
 * at y -2, Chebyshev 62..65, rails on both rims). The approach road comes in from the west along z 0
 * (the party arrives at (-80, 0, 0)) and crosses the moat on a walled causeway (x -66..-61, walkway
 * z -2..2) to the west gate (x -56..-55, z -2..2). Forge Street runs east from the gate along z -4..4,
 * with a lava channel under glass down its middle (z 0), through the Forge Square (x -35..-21, z -8..8)
 * to the Ember Hall. Two open smithies stand on Forge Street (x -53..-46, z 9..16 and x -24..-17,
 * z -16..-9); houses with furnished interiors fill the quarters between Forge Street, the north and
 * south streets (x -30..-26), the lanes (z -29..-27 and 26..28) and the east lane (x 8..13).
 *
 * The Ember Hall (x 14..46, z -12..12, door on the west at z -2..2): the long council table (top
 * slabs, x 22..34, z 0) carries the three evidence clues at (25, 1, 0), (28, 1, 0) and (31, 1, 0);
 * the throne dais is x 39..45, z -4..4 (standing height 1), the throne at (43, 1, 0) with Kaelen's spot
 * in front of it (42, 1, 0); lava falls behind glass flank the throne at (46, 1..8, -3) and (46, 1..8, 3).
 *
 * The vault approach leaves the hall's north wall at x 29..31: a corridor (floor y -1) north to
 * z -32, crossed by three lava channels at y -1 (rows z -17/-18, -22/-23, -27/-28) that the player
 * dams at x 29..31; the channels run on under iron bars into sealed alcoves. The antechamber
 * (x 25..35, z -39..-33) holds the three oath braziers (carved pedestal at y 0, unlit soul campfire at
 * y 1): KEEP (26, 0, -38), APART (34, 0, -37), NEVER (26, 0, -35). The vault door fills x 29..31,
 * y 0..3 at z -40; the vault (x 25..35, z -50..-41) has the Ember Key's pedestal at (30, 0, -47), the
 * key at (30, 1, -47). South of the hall is the Great Forge (x 16..46, z 18..44). Positions the scene
 * checks are in its BLOCKS stage and clues, so they must match.
 */
public final class Emberhold {
    static final int R = 90;
    static final int WALL_IN = 55, WALL_OUT = 56, MOAT_IN = 61, MOAT_OUT = 66;
    public static final int HALL_X0 = 14, HALL_X1 = 46, HALL_Z0 = -12, HALL_Z1 = 12, HALL_TOP = 10;
    /** Rows of the vault corridor the lava channels cross (y -1, dammed at x 29..31). */
    public static final int[] CHANNEL_ROWS = {-17, -18, -22, -23, -27, -28};
    /** The oath braziers: KEEP, APART, NEVER (pedestal at y 0, fire bowl at y 1). */
    public static final int[][] BRAZIERS = {{26, -38}, {34, -37}, {26, -35}};
    static final String[] WORDS = {"KEEP", "APART", "NEVER"};
    public static final int DOOR_Z = -40;

    private static final Palette EMBER = new Palette("ember", Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BASALT,
            Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.CRIMSON_PLANKS, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICK_SLAB, Blocks.CRIMSON_DOOR,
            Blocks.ORANGE_STAINED_GLASS, Blocks.CRIMSON_WALL_SIGN, Blocks.NETHER_BRICKS);
    private static final Palette BASALT = new Palette("basalt", Blocks.POLISHED_BLACKSTONE, Blocks.SMOOTH_BASALT, Blocks.BASALT,
            Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB,
            Blocks.WARPED_DOOR, Blocks.CYAN_STAINED_GLASS, Blocks.CRIMSON_WALL_SIGN, Blocks.POLISHED_BLACKSTONE_BRICKS);
    private static final Palette NETHER = new Palette("nether", Blocks.BLACKSTONE, Blocks.NETHER_BRICKS, Blocks.POLISHED_BASALT,
            Blocks.RED_NETHER_BRICKS, Blocks.CRIMSON_PLANKS, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE_SLAB, Blocks.CRIMSON_DOOR,
            Blocks.ORANGE_STAINED_GLASS, Blocks.CRIMSON_WALL_SIGN, Blocks.BLACKSTONE);
    private static final Palette DEEP = new Palette("deep", Blocks.COBBLED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.POLISHED_BASALT,
            Blocks.CHISELED_DEEPSLATE, Blocks.WARPED_PLANKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.WARPED_DOOR,
            Blocks.CYAN_STAINED_GLASS, Blocks.CRIMSON_WALL_SIGN, Blocks.DEEPSLATE_BRICKS);
    private static final Palette[] PALETTES = {EMBER, BASALT, NETHER, DEEP};
    private static final Block[] BEDS = {Blocks.BLUE_BED, Blocks.CYAN_BED, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.GRAY_BED, Blocks.ORANGE_BED};
    private static final Block[] CARPETS = {Blocks.BLUE_CARPET, Blocks.RED_CARPET, Blocks.CYAN_CARPET, Blocks.BLACK_CARPET};

    private final Builder b;

    private Emberhold(ServerLevel level, BlockPos anchor) {
        this.b = new Builder(level, anchor, 1015L);
    }

    private static int hash(int x, int z) {
        int h = x * 73_856_093 ^ z * 19_349_663;
        return (h ^ (h >>> 13)) & 0x7fffffff;
    }

    private static int cheb(int x, int z) {
        return Math.max(Math.abs(x), Math.abs(z));
    }

    public static void build(ServerLevel level, BlockPos anchor) {
        Emberhold e = new Emberhold(level, anchor);
        e.land();
        e.moat();
        e.approach();
        e.walls();
        e.streets();
        e.forgeSquare();
        e.market();
        e.smithy(-53, 9, -46, 16, Direction.NORTH, "Forge of the", "West Gate");
        e.smithy(-24, -16, -17, -9, Direction.SOUTH, "Bellows", "and Blade");
        e.houses();
        e.smelterYard();
        e.ashGarden();
        e.storehouse();
        e.trainingYard();
        e.barracks();
        e.greatForge();
        e.stockpiles();
        e.hall();
        e.vault();
        e.lights();
    }

    // ------------------------------------------------------------------ helpers

    private static BlockState lit(Block furnace, Direction facing) {
        return Builder.facing(furnace, facing).setValue(BlockStateProperties.LIT, true);
    }

    private static BlockState banner(Block wallBanner, Direction facing) {
        return wallBanner.defaultBlockState().setValue(WallBannerBlock.FACING, facing);
    }

    /** A wall block joined to its neighbours along one axis (a railing), with or without a post. */
    private static BlockState rail(Block wall, boolean alongX, boolean post) {
        BlockState s = wall.defaultBlockState().setValue(WallBlock.UP, post);
        return alongX ? s.setValue(WallBlock.EAST_WALL, WallSide.LOW).setValue(WallBlock.WEST_WALL, WallSide.LOW)
                : s.setValue(WallBlock.NORTH_WALL, WallSide.LOW).setValue(WallBlock.SOUTH_WALL, WallSide.LOW);
    }

    /** Iron bars joined north-south or east-west. */
    private static BlockState bars(boolean alongZ) {
        BlockState s = Blocks.IRON_BARS.defaultBlockState();
        return alongZ ? s.setValue(CrossCollisionBlock.NORTH, true).setValue(CrossCollisionBlock.SOUTH, true)
                : s.setValue(CrossCollisionBlock.EAST, true).setValue(CrossCollisionBlock.WEST, true);
    }

    private static BlockState candle() {
        return Blocks.CANDLE.defaultBlockState().setValue(BlockStateProperties.LIT, true);
    }

    private static BlockState soulCampfire(boolean lit) {
        return Blocks.SOUL_CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, lit).setValue(CampfireBlock.SIGNAL_FIRE, false);
    }

    private void lampPost(int x, int z, boolean soul) {
        b.fill(x, 0, z, x, 1, z, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        b.set(x, 2, z, soul ? Blocks.SOUL_LANTERN : Blocks.LANTERN);
    }

    /**
     * A lava fall behind glass. The glass column is (gx, y0..y1, gz); the lava falls in a one-block
     * shaft just behind it (one step towards {@code out}), sealed on every other side, from a source
     * at y1 + 1. Flowing lava can only go down the shaft and pool at its sealed foot.
     */
    private void lavaFall(int gx, int gz, Direction out, int y0, int y1) {
        int sx = gx + out.getStepX(), sz = gz + out.getStepZ();
        Direction side = out.getClockWise();
        for (int y = y0 - 1; y <= y1 + 2; y++) {
            b.set(sx + out.getStepX(), y, sz + out.getStepZ(), Blocks.BLACKSTONE);
            b.set(sx + side.getStepX(), y, sz + side.getStepZ(), Blocks.BLACKSTONE);
            b.set(sx - side.getStepX(), y, sz - side.getStepZ(), Blocks.BLACKSTONE);
        }
        b.set(sx, y0 - 1, sz, Blocks.MAGMA_BLOCK);
        b.set(gx, y0 - 1, gz, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int y = y0; y <= y1; y++) {
            b.set(gx, y, gz, Blocks.GLASS);
            b.set(sx, y, sz, Blocks.AIR);
        }
        b.set(gx, y1 + 1, gz, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(sx, y1 + 2, sz, Blocks.BLACKSTONE);
        b.set(sx, y1 + 1, sz, Blocks.LAVA);
    }

    private void pave(int x0, int z0, int x1, int z1) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                int h = hash(x, z);
                b.set(x, -1, z, h % 11 == 0 ? Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS : h % 5 == 0 ? Blocks.POLISHED_BLACKSTONE
                        : h % 7 == 0 ? Blocks.SMOOTH_BASALT : Blocks.POLISHED_BLACKSTONE_BRICKS);
            }
    }

    /** A flat roof whose top layer is bottom slabs, so nothing spawns on it. */
    private void flatRoof(int x0, int z0, int x1, int z1, int y, Block roof, Block slab) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                b.set(x, y, z, roof);
                b.set(x, y + 1, z, Builder.slab(slab, false));
            }
    }

    /** Hollow building shell: walls y 0..top-1, floor at y -1, flat roof at y top. */
    private void shell(int x0, int z0, int x1, int z1, int top, Block wall, Block corner, Block floor) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean cor = (x == x0 || x == x1) && (z == z0 || z == z1);
                b.set(x, -1, z, floor);
                for (int y = 0; y < top; y++) b.set(x, y, z, cor ? corner : edge ? wall : Blocks.AIR);
            }
        flatRoof(x0, z0, x1, z1, top, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
    }

    // ------------------------------------------------------------------ land

    private static BlockState cityGround(int h) {
        return (h % 9 == 0 ? Blocks.BASALT : h % 7 == 0 ? Blocks.SMOOTH_BASALT : h % 13 == 0 ? Blocks.TUFF : Blocks.BLACKSTONE).defaultBlockState();
    }

    private static BlockState edgeGround(double t, int h) {
        Block g;
        if (t < 0.25) g = h % 3 == 0 ? Blocks.SMOOTH_BASALT : h % 3 == 1 ? Blocks.BASALT : Blocks.BLACKSTONE;
        else if (t < 0.55) g = h % 4 == 0 ? Blocks.BASALT : h % 4 == 1 ? Blocks.TUFF : h % 4 == 2 ? Blocks.BLACKSTONE : Blocks.COARSE_DIRT;
        else if (t < 0.8) g = h % 3 == 0 ? Blocks.BLACKSTONE : h % 3 == 1 ? Blocks.TERRACOTTA : Blocks.RED_SAND;
        else g = h % 4 == 0 ? Blocks.ORANGE_TERRACOTTA : h % 4 == 1 ? Blocks.TERRACOTTA : Blocks.RED_SAND;
        return g.defaultBlockState();
    }

    /**
     * The city ground, and round it a band where the volcanic stone gives way to the badlands' red
     * earth. Columns beyond the plateau's flat top (98 blocks out) are left as the terrain made them.
     */
    private void land() {
        for (int x = -R; x <= R; x++)
            for (int z = -R; z <= R; z++) {
                double r = Math.hypot(x, z);
                if (r > 98) continue;
                int c = cheb(x, z), h = hash(x, z);
                if (c <= MOAT_OUT) {
                    b.fill(x, -6, z, x, -2, z, h % 5 == 0 ? Blocks.BASALT : Blocks.BLACKSTONE);
                    b.set(x, -1, z, cityGround(h));
                    b.fill(x, 0, z, x, c <= WALL_OUT ? 26 : 12, z, Blocks.AIR);
                } else {
                    double t = (r - 70) / 26.0 + ((h % 100) - 50) / 160.0;
                    b.fill(x, -4, z, x, -2, z, t < 0.5 ? Blocks.BLACKSTONE : Blocks.TERRACOTTA);
                    b.set(x, -1, z, edgeGround(t, h));
                    b.fill(x, 0, z, x, 8, z, Blocks.AIR);
                }
            }
        // Basalt spires and loose stone scattered through the edge band.
        for (int x = -R; x <= R; x++)
            for (int z = -R; z <= R; z++) {
                double r = Math.hypot(x, z);
                if (r < 70 || r > 95 || cheb(x, z) <= MOAT_OUT + 1 || (x < 0 && Math.abs(z) <= 4)) continue;
                int h = hash(x * 3 + 7, z * 5 - 11);
                if (h % 41 != 0) continue;
                int tall = 1 + h % 4 + (r < 82 ? 1 : 0);
                b.fill(x, 0, z, x, tall - 1, z, Blocks.BASALT);
                for (Direction d : Direction.Plane.HORIZONTAL) {
                    if (((h >> (d.get2DDataValue() + 3)) & 1) == 0) {
                        b.set(x + d.getStepX(), 0, z + d.getStepZ(), ((h >> 8) & 1) == 0 ? Blocks.BLACKSTONE : Blocks.SMOOTH_BASALT);
                    }
                }
            }
    }

    /** The lava moat round the walls (lava at y -2, one deep), its railed rims and the causeway. */
    private void moat() {
        for (int x = -MOAT_OUT; x <= MOAT_OUT; x++)
            for (int z = -MOAT_OUT; z <= MOAT_OUT; z++) {
                int c = cheb(x, z);
                if (c < MOAT_IN) continue;
                if (x <= -MOAT_IN && Math.abs(z) <= 3) {
                    b.fill(x, -5, z, x, -2, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                    b.set(x, -1, z, Math.abs(z) == 3 ? Blocks.POLISHED_BLACKSTONE_BRICKS
                            : Math.floorMod(x + z, 3) == 0 ? Blocks.CHISELED_POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE);
                    continue;
                }
                if (c == MOAT_IN || c == MOAT_OUT) {
                    b.fill(x, -5, z, x, -2, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                    b.set(x, -1, z, Blocks.POLISHED_BLACKSTONE);
                } else {
                    b.fill(x, -5, z, x, -3, z, Blocks.BASALT);
                    b.set(x, -2, z, Blocks.LAVA);
                    b.set(x, -1, z, Blocks.AIR);
                }
            }
        ringRail(MOAT_IN);
        ringRail(MOAT_OUT);
        // The causeway's own walls, with lanterns on their posts.
        for (int x = -MOAT_OUT; x <= -MOAT_IN; x++)
            for (int z : new int[] {-3, 3}) {
                boolean post = x == -MOAT_OUT || x == -MOAT_IN || x == -64;
                b.set(x, 0, z, rail(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, true, post));
                if (post) b.set(x, 1, z, Blocks.SOUL_LANTERN);
            }
    }

    private void ringRail(int c) {
        for (int i = -c; i <= c; i++) {
            railCell(i, -c, true);
            railCell(i, c, true);
            railCell(-c, i, false);
            railCell(c, i, false);
        }
    }

    private void railCell(int x, int z, boolean alongX) {
        if (x < 0 && Math.abs(z) <= 3 && Math.abs(x) >= MOAT_IN) return; // the causeway
        boolean post = Math.floorMod(alongX ? x : z, 8) == 0;
        b.set(x, 0, z, rail(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, alongX, post));
        if (post) b.set(x, 1, z, Blocks.LANTERN);
    }

    /** The road in from the west and the paved apron in front of the gate. */
    private void approach() {
        for (int x = -97; x <= -MOAT_OUT - 1; x++)
            for (int z = -2; z <= 2; z++) {
                int h = hash(x, z);
                b.set(x, -1, z, Math.abs(z) == 2 ? Blocks.BLACKSTONE : h % 6 == 0 ? Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS
                        : h % 3 == 0 ? Blocks.POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.fill(x, 0, z, x, 3, z, Blocks.AIR);
            }
        for (int x = -92; x <= -70; x += 8) {
            lampPost(x, -3, false);
            lampPost(x, 3, false);
        }
        // Waymarks with Veyrath's colours, well clear of the moat.
        for (int z : new int[] {-4, 4}) {
            b.fill(-74, 0, z, -74, 2, z, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
            b.set(-74, 3, z, Blocks.BLUE_BANNER);
        }
        pave(-60, -2, -57, 2);
    }

    // ------------------------------------------------------------------ walls

    private void walls() {
        for (int x = -WALL_OUT; x <= WALL_OUT; x++)
            for (int z = -WALL_OUT; z <= WALL_OUT; z++) {
                int c = cheb(x, z);
                if (c != WALL_IN && c != WALL_OUT) continue;
                int along = Math.abs(x) == c ? z : x;
                int h = hash(x, z);
                Block block = c == WALL_OUT && Math.floorMod(along, 8) == 0 ? Blocks.POLISHED_BASALT
                        : h % 9 == 0 ? Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS : h % 13 == 0 ? Blocks.BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS;
                b.fill(x, -1, z, x, 7, z, block);
                if (c == WALL_OUT && Math.floorMod(along, 8) == 4) b.set(x, 4, z, Blocks.MAGMA_BLOCK);
                b.set(x, 8, z, c == WALL_OUT && Math.floorMod(along, 2) == 0 ? Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState()
                        : Builder.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
            }
        // The west gate.
        for (int x = -WALL_OUT; x <= -WALL_IN; x++)
            for (int z = -2; z <= 2; z++) {
                b.fill(x, 0, z, x, 4, z, Blocks.AIR);
                b.set(x, -1, z, Blocks.POLISHED_BLACKSTONE);
            }
        b.set(-WALL_OUT, 5, 0, Blocks.GILDED_BLACKSTONE);
        b.set(-WALL_OUT, 5, -1, Blocks.CHISELED_POLISHED_BLACKSTONE);
        b.set(-WALL_OUT, 5, 1, Blocks.CHISELED_POLISHED_BLACKSTONE);
        b.set(-WALL_OUT - 1, 6, 0, banner(Blocks.CYAN_WALL_BANNER, Direction.WEST));
        for (int z : new int[] {-4, 4}) {
            b.set(-WALL_OUT - 1, 5, z, banner(Blocks.BLUE_WALL_BANNER, Direction.WEST));
            b.set(-WALL_IN + 1, 5, z, banner(Blocks.BLUE_WALL_BANNER, Direction.EAST));
        }
        for (int z : new int[] {-3, 3}) {
            b.fill(-WALL_OUT - 1, 0, z, -WALL_OUT - 1, 2, z, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
            b.set(-WALL_OUT - 1, 3, z, Blocks.SOUL_LANTERN);
        }
        tower(-59, 5, -54, 9);
        tower(-59, -9, -54, -5);
        for (int sx : new int[] {-1, 1})
            for (int sz : new int[] {-1, 1}) tower(sx * 56 - 3, sz * 56 - 3, sx * 56 + 3, sz * 56 + 3);
    }

    /** A solid basalt-and-blackstone tower with merlons, a fire on top and a royal banner. */
    private void tower(int x0, int z0, int x1, int z1) {
        int top = 12;
        int cx = (x0 + x1) / 2, cz = (z0 + z1) / 2;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                b.fill(x, -1, z, x, top - 1, z, corner ? Blocks.POLISHED_BASALT : Blocks.POLISHED_BLACKSTONE_BRICKS);
                if (edge && !corner && (x + z) % 3 == 0) b.set(x, 7, z, Blocks.ORANGE_STAINED_GLASS);
                b.set(x, top, z, edge ? (Math.floorMod(x + z, 2) == 0 ? Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState()
                        : Builder.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false)) : Builder.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
            }
        b.set(cx, top, cz, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.campfire(cx, top + 1, cz, false);
        b.set(cx + 1, top, cz, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        b.set(cx + 1, top + 1, cz, Blocks.BLUE_BANNER);
    }

    // ------------------------------------------------------------------ streets

    private void streets() {
        pave(-54, -4, 13, 4);      // Forge Street
        pave(-30, -52, -26, -5);   // north street
        pave(-30, 5, -26, 52);     // south street
        pave(-54, -29, 7, -27);    // north lane
        pave(-54, 26, 15, 28);     // south lane
        pave(8, -52, 13, 52);      // east lane and the hall forecourt
        pave(13, 13, 54, 17);      // between the hall and the Great Forge
        pave(47, -19, 54, 12);     // east of the hall
        pave(37, -19, 46, -13);    // the courtyard before the barracks
        // The lava channel under glass down the middle of Forge Street.
        for (int x = -52; x <= 10; x++) {
            b.set(x, -3, 0, Blocks.BASALT);
            b.set(x, -2, 0, Blocks.LAVA);
            b.set(x, -1, 0, Blocks.GLASS);
            b.set(x, -2, -1, Blocks.BASALT);
            b.set(x, -2, 1, Blocks.BASALT);
        }
        for (int x = -48; x <= 8; x += 8) {
            lampPost(x, -4, false);
            lampPost(x, 4, false);
        }
        for (int z = -48; z <= 48; z += 8) {
            if (Math.abs(z) <= 8) continue;
            lampPost(-31, z, false);
            lampPost(7, z, z % 16 == 0);
        }
    }

    /** The Forge Square: a widening of Forge Street round the Great Anvil. */
    private void forgeSquare() {
        pave(-35, -8, -21, 8);
        for (int x = -35; x <= -21; x++) {
            b.set(x, -1, -8, Blocks.CHISELED_POLISHED_BLACKSTONE);
            b.set(x, -1, 8, Blocks.CHISELED_POLISHED_BLACKSTONE);
        }
        // The plinth over the channel, the anvil of the first forge, and soul lanterns at its corners.
        for (int x = -29; x <= -27; x++)
            for (int z = -1; z <= 1; z++) {
                b.set(x, -1, z, Blocks.POLISHED_BLACKSTONE);
                b.set(x, 0, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                if (Math.abs(x + 28) == 1 && Math.abs(z) == 1) b.set(x, 1, z, Blocks.SOUL_LANTERN);
            }
        b.set(-28, 1, 0, Blocks.CHISELED_POLISHED_BLACKSTONE);
        b.set(-28, 2, 0, Builder.facing(Blocks.ANVIL, Direction.EAST));
        b.set(-28, 1, -1, Blocks.GOLD_BLOCK);
        // Four fire columns.
        for (int x : new int[] {-32, -24})
            for (int z : new int[] {-4, 4}) {
                b.fill(x, 0, z, x, 3, z, Blocks.POLISHED_BASALT);
                b.campfire(x, 4, z, false);
                b.set(x + (x < -28 ? -1 : 1), 2, z, banner(Blocks.BLUE_WALL_BANNER, x < -28 ? Direction.WEST : Direction.EAST));
            }
        b.sign(-28, 0, -2, Blocks.CRIMSON_WALL_SIGN, Direction.NORTH, "THE FIRST ANVIL", "Struck before", "Emberhold had", "walls");
    }

    /** Market stalls on the south side of Forge Street, beside the west smithy. */
    private void market() {
        for (int[] s : new int[][] {{-44, -42}, {-39, -37}}) {
            for (int x = s[0]; x <= s[1]; x++)
                for (int z = 10; z <= 12; z++) {
                    boolean post = (x == s[0] || x == s[1]) && (z == 10 || z == 12);
                    if (post) b.fill(x, 0, z, x, 2, z, Blocks.CRIMSON_FENCE);
                    b.set(x, 3, z, Math.floorMod(x, 2) == 0 ? Blocks.BLUE_WOOL : Blocks.BLACK_WOOL);
                }
            b.set(s[0] + 1, 0, 10, Builder.slab(Blocks.CRIMSON_SLAB, true));
            b.set(s[0] + 1, 0, 12, Builder.facing(Blocks.BARREL, Direction.UP));
        }
        b.set(-43, 1, 10, Blocks.DECORATED_POT);
        b.set(-38, 1, 10, Blocks.DECORATED_POT);
        b.set(-37, 0, 11, Builder.facing(Blocks.BARREL, Direction.UP));
        b.armorStand(-41.5, 0, 13.5, 180, new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_CHESTPLATE), ItemStack.EMPTY, ItemStack.EMPTY);
        b.armorStand(-35.5, 0, 13.5, 180, new ItemStack(Items.GOLDEN_HELMET), new ItemStack(Items.CHAINMAIL_CHESTPLATE), ItemStack.EMPTY, ItemStack.EMPTY);
    }

    /**
     * An open-fronted smithy: lit blast furnaces and a smoker along the back, a magma hearth set in the
     * back wall with a smoking chimney over it, anvils, a grindstone, a quench cauldron and tools.
     */
    private void smithy(int x0, int z0, int x1, int z1, Direction open, String sign1, String sign2) {
        int top = 5;
        int s = open == Direction.NORTH ? 1 : -1;       // towards the back
        int front = open == Direction.NORTH ? z0 : z1, backWall = open == Direction.NORTH ? z1 : z0;
        int zb = backWall - s, zm = backWall - 3 * s;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                b.set(x, -1, z, hash(x, z) % 4 == 0 ? Blocks.BLACKSTONE : Blocks.POLISHED_BLACKSTONE);
                for (int y = 0; y < top; y++) {
                    Block w = Blocks.AIR;
                    if (corner) w = Blocks.POLISHED_BASALT;
                    else if (edge && z != front) w = y == 0 ? Blocks.BLACKSTONE : Blocks.NETHER_BRICKS;
                    else if (z == front && y == top - 1) w = Blocks.POLISHED_BLACKSTONE_BRICKS;
                    b.set(x, y, z, w);
                }
            }
        flatRoof(x0, z0, x1, z1, top, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_SLAB);
        b.set(x0 + 1, 0, zb, lit(Blocks.BLAST_FURNACE, open));
        b.set(x0 + 2, 0, zb, lit(Blocks.BLAST_FURNACE, open));
        b.set(x0 + 4, 0, zb, lit(Blocks.SMOKER, open));
        b.set(x0 + 5, 0, zb, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(x0 + 6, 0, zb, Blocks.SMITHING_TABLE);
        // The hearth in the back wall and its chimney.
        b.set(x0 + 3, 0, backWall, Blocks.MAGMA_BLOCK);
        b.fill(x0 + 3, 1, backWall, x0 + 3, top + 4, backWall, Blocks.NETHER_BRICKS);
        b.campfire(x0 + 3, top + 5, backWall, true);
        b.set(x0 + 2, 0, zm, Builder.facing(Blocks.ANVIL, Direction.EAST));
        b.set(x0 + 5, 0, zm, Builder.facing(Blocks.CHIPPED_ANVIL, Direction.EAST));
        b.set(x0 + 1, 0, zm, Blocks.GRINDSTONE.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR));
        b.set(x1 - 1, 0, zm, Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
        b.itemFrame(x0 + 1, 2, zm, Direction.EAST, new ItemStack(Items.IRON_PICKAXE));
        b.itemFrame(x0 + 1, 2, zm + s, Direction.EAST, new ItemStack(Items.IRON_SWORD));
        b.itemFrame(x1 - 1, 2, zm, Direction.WEST, new ItemStack(Items.IRON_AXE));
        b.set((x0 + x1) / 2, top - 2, (z0 + z1) / 2, Builder.hangingLantern());
        b.sign((x0 + x1) / 2, top - 1, front - s, Blocks.CRIMSON_WALL_SIGN, open, "", sign1, sign2, "");    }

    // ------------------------------------------------------------------ houses

    private void houses() {
        int[][] lots = {
                // x0, z0, x1, z1, door: 0 north, 1 south, 2 west
                {-53, -17, -45, -9, 1}, {-42, -17, -33, -9, 1}, {-53, -26, -45, -19, 0}, {-42, -26, -33, -19, 0},
                {-14, -17, -6, -9, 1}, {-3, -17, 5, -9, 1}, {-24, -26, -16, -19, 0}, {-13, -26, -5, -19, 0}, {-2, -26, 6, -19, 0},
                {-53, -40, -44, -31, 1}, {-42, -40, -33, -31, 1}, {-24, -40, -15, -31, 1}, {-12, -40, -3, -31, 1}, {0, -40, 7, -31, 1},
                {-53, 18, -45, 25, 1}, {-42, 18, -33, 25, 1},
                {-24, 9, -16, 16, 0}, {-13, 9, -5, 16, 0}, {-2, 9, 6, 16, 0},
                {-24, 18, -16, 25, 1}, {-13, 18, -5, 25, 1}, {-2, 18, 6, 25, 1},
                {-53, 31, -44, 40, 0}, {-42, 31, -33, 40, 0}, {-24, 31, -15, 40, 0}, {-12, 31, -3, 40, 0},
                {14, -40, 22, -31, 2}, {14, -26, 22, -17, 2}};
        Direction[] doors = {Direction.NORTH, Direction.SOUTH, Direction.WEST};
        for (int i = 0; i < lots.length; i++) {
            int[] l = lots[i];
            house(l[0], l[1], l[2], l[3], doors[l[4]], i);
        }
    }

    private void house(int x0, int z0, int x1, int z1, Direction door, int variant) {
        Palette p = PALETTES[variant % PALETTES.length];
        int wallTop = 4 + (variant % 3 == 0 ? 1 : 0);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                b.set(x, -1, z, edge ? p.foundation() : p.floor());
                for (int y = 0; y < wallTop; y++) {
                    Block s = Blocks.AIR;
                    if (corner) s = p.frame();
                    else if (edge) s = y == 0 ? p.foundation() : (y == 2 && Math.floorMod(x + z, 3) == 0) ? p.window() : p.wall();
                    b.set(x, y, z, s);
                }
            }
        int mx = (x0 + x1) / 2, mz = (z0 + z1) / 2;
        int dx = door == Direction.WEST ? x0 : door == Direction.EAST ? x1 : mx;
        int dz = door == Direction.NORTH ? z0 : door == Direction.SOUTH ? z1 : mz;
        b.door(dx, 0, dz, p.door(), door);
        int ox = dx + door.getStepX(), oz = dz + door.getStepZ();
        b.set(ox, 3, oz, Builder.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, door.getOpposite(), true));
        b.set(ox, 2, oz, Builder.hangingLantern());
        int ridge = b.gableRoof(x0, z0, x1, z1, wallTop, p);
        // A chimney through the back wall, smoking gently.
        Direction back = door.getOpposite();
        int cx = back.getAxis() == Direction.Axis.Z ? x0 + 2 : (back == Direction.WEST ? x0 : x1);
        int cz = back.getAxis() == Direction.Axis.Z ? (back == Direction.NORTH ? z0 : z1) : z0 + 2;
        b.fill(cx, 0, cz, cx, ridge + 1, cz, p.chimney());
        b.campfire(cx, ridge + 2, cz, false);
        Room r = new Room(b, new int[] {x0, z0, x1, z1}, door, dx, dz);
        furnish(r, variant, wallTop);
    }

    /** Beds, storage, a cooking smoker, a table with chairs, a rug, a lamp and something of the owner's trade. */
    private void furnish(Room r, int v, int wallTop) {
        int w = r.width, d = r.depth;
        Block carpet = CARPETS[v % CARPETS.length];
        for (int u = 1; u <= w - 2; u++)
            for (int vv = 2; vv <= d - 2; vv++) r.set(u, 0, vv, carpet);
        Block bed = BEDS[v % BEDS.length];
        r.bed(0, d - 1, bed, r.back);
        if (v % 2 == 0 && w >= 6) r.bed(2, d - 1, BEDS[(v + 3) % BEDS.length], r.back);
        r.facing(0, 0, d - 3, Blocks.CHEST, r.right);
        r.set(w - 1, 0, d - 1, Builder.facing(Blocks.BARREL, Direction.UP));
        r.set(w - 1, 1, d - 1, v % 2 == 0 ? Blocks.POTTED_CRIMSON_FUNGUS : Blocks.POTTED_WARPED_FUNGUS);
        r.set(w - 1, 0, d - 2, lit(Blocks.SMOKER, r.left));
        Block trade = switch (v % 5) {
            case 0 -> Blocks.CRAFTING_TABLE;
            case 1 -> Blocks.SMITHING_TABLE;
            case 2 -> Blocks.CARTOGRAPHY_TABLE;
            case 3 -> Blocks.STONECUTTER;
            default -> Blocks.LOOM;
        };
        r.set(w - 1, 0, d - 3, trade);
        int tu = w / 2 + 1, tv = Math.max(2, d / 2);
        r.set(tu, 0, tv, Blocks.CRIMSON_FENCE);
        r.set(tu, 1, tv, v % 3 == 0 ? candle() : Blocks.CRIMSON_PRESSURE_PLATE.defaultBlockState());
        r.set(tu - 1, 0, tv, Builder.stairs(Blocks.CRIMSON_STAIRS, r.left, false));
        r.set(tu + 1, 0, tv, Builder.stairs(Blocks.CRIMSON_STAIRS, r.right, false));
        if (v % 4 == 2) {
            r.set(1, 0, d - 1, Blocks.BOOKSHELF);
            r.facing(0, 0, d - 4, Blocks.LECTERN, r.right);
        }
        if (v % 4 == 1) r.set(0, 0, 2, Builder.facing(Blocks.ANVIL, r.front));
        r.set(w / 2, wallTop - 1, Math.max(2, d / 2), Builder.hangingLantern());
    }

    // ------------------------------------------------------------------ quarters

    /** North-west: the smelters' yard, its back wall carrying three lava falls behind glass. */
    private void smelterYard() {
        pave(-53, -51, -32, -43);
        for (int x = -53; x <= -32; x++) b.fill(x, -1, -52, x, 8, -52, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int x = -53; x <= -32; x++) b.fill(x, -1, -54, x, 9, -53, Blocks.BLACKSTONE);
        for (int x : new int[] {-49, -42, -36}) lavaFall(x, -52, Direction.NORTH, 1, 7);
        for (int x : new int[] {-52, -45, -39, -33}) {
            b.set(x, 0, -51, lit(Blocks.BLAST_FURNACE, Direction.SOUTH));
            b.set(x, 1, -51, lit(Blocks.BLAST_FURNACE, Direction.SOUTH));
            b.fill(x, 2, -51, x, 10, -51, Blocks.NETHER_BRICKS);
            b.campfire(x, 11, -51, true);
        }
        for (int[] p : new int[][] {{-50, -46}, {-47, -45}, {-44, -47}, {-38, -46}, {-35, -45}}) {
            b.set(p[0], 0, p[1], (p[0] + p[1]) % 2 == 0 ? Blocks.COAL_BLOCK : Blocks.RAW_IRON_BLOCK);
        }
        b.set(-47, 1, -45, Blocks.COAL_BLOCK);
        b.set(-40, 0, -44, Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
        b.set(-41, 0, -44, Builder.facing(Blocks.ANVIL, Direction.NORTH));
        b.fill(-42, 0, -43, -42, 1, -43, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        b.set(-42, 2, -43, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.sign(-42, 2, -42, Blocks.CRIMSON_WALL_SIGN, Direction.SOUTH, "", "SMELTERS'", "YARD", "");
    }

    /** North-east: the ash garden, fungus beds round basalt spires, with benches. */
    private void ashGarden() {
        for (int x = -24; x <= 6; x++)
            for (int z = -52; z <= -43; z++) {
                int h = hash(x, z);
                boolean path = z == -47 || x == -9;
                b.set(x, -1, z, path ? Blocks.POLISHED_BLACKSTONE_BRICKS : h % 2 == 0 ? Blocks.CRIMSON_NYLIUM : Blocks.WARPED_NYLIUM);
                if (path) continue;
                if (h % 7 == 0) b.set(x, 0, z, h % 2 == 0 ? Blocks.CRIMSON_FUNGUS : Blocks.WARPED_FUNGUS);
                else if (h % 5 == 0) b.set(x, 0, z, h % 2 == 0 ? Blocks.CRIMSON_ROOTS : Blocks.WARPED_ROOTS);
                else if (h % 11 == 0) b.set(x, 0, z, Blocks.NETHER_SPROUTS);
            }
        for (int[] s : new int[][] {{-20, -50, 4}, {-15, -44, 2}, {0, -50, 3}, {4, -44, 5}}) {
            b.fill(s[0], 0, s[1], s[0], s[2] - 1, s[1], Blocks.BASALT);
            b.set(s[0], s[2], s[1], Blocks.SHROOMLIGHT);
        }
        for (int x : new int[] {-12, -6}) {
            b.set(x, 0, -48, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.NORTH, false));
            b.set(x, 0, -46, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.SOUTH, false));
        }
        b.fill(-9, 0, -50, -9, 3, -50, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        b.set(-9, 4, -50, Blocks.CYAN_BANNER);
    }

    /** South-west: the city storehouse, rows of barrels and chests and the keeper's ledger. */
    private void storehouse() {
        int x0 = -53, z0 = 43, x1 = -33, z1 = 52, top = 5;
        shell(x0, z0, x1, z1, top, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BASALT, Blocks.POLISHED_BLACKSTONE);
        b.door(-43, 0, z0, Blocks.CRIMSON_DOOR, Direction.NORTH);
        for (int x = x0 + 2; x <= x1 - 2; x += 3) b.set(x, 2, z0, Blocks.ORANGE_STAINED_GLASS);
        for (int x = x0 + 2; x <= x1 - 2; x++) {
            if (x >= -45 && x <= -41) continue;
            b.set(x, 0, z1 - 1, Builder.facing(Blocks.BARREL, Direction.UP));
            b.set(x, 1, z1 - 1, Builder.facing(Blocks.BARREL, Direction.UP));
            if (x % 2 == 0) b.set(x, 0, z1 - 4, Builder.facing(Blocks.CHEST, Direction.NORTH));
        }
        b.set(-43, 0, z1 - 1, Builder.facing(Blocks.LECTERN, Direction.NORTH));
        b.set(x0 + 1, 0, z0 + 2, Builder.facing(Blocks.BARREL, Direction.EAST));
        for (int x = x0 + 3; x <= x1 - 3; x += 5) b.set(x, top - 1, (z0 + z1) / 2, Builder.hangingLantern());
        b.sign(-43, 3, z0 - 1, Blocks.CRIMSON_WALL_SIGN, Direction.NORTH, "", "STOREHOUSE", "OF EMBERHOLD", "");
    }

    /** South-east: the honour guard's training yard, with dummies, targets and a weapon rack. */
    private void trainingYard() {
        for (int x = -24; x <= 6; x++)
            for (int z = 43; z <= 52; z++) {
                boolean edge = x == -24 || x == 6 || z == 43 || z == 52;
                b.set(x, -1, z, hash(x, z) % 3 == 0 ? Blocks.GRAVEL : Blocks.COARSE_DIRT);
                if (edge && !(z == 43 && x >= -11 && x <= -7)) b.set(x, 0, z, rail(Blocks.BLACKSTONE_WALL, z == 43 || z == 52, (x + z) % 4 == 0));
            }
        for (int x = -24; x <= 6; x++) {
            b.set(x, 0, 43, rail(Blocks.BLACKSTONE_WALL, true, x % 4 == 0));
        }
        b.fill(-11, 0, 43, -7, 0, 43, Blocks.AIR);
        for (int x = -20; x <= 2; x += 6) b.armorStand(x + 0.5, 0, 47.5, 0, new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_CHESTPLATE),
                new ItemStack(Items.IRON_LEGGINGS), ItemStack.EMPTY);
        for (int x : new int[] {-18, -4}) {
            b.set(x, 0, 51, Blocks.HAY_BLOCK);
            b.set(x, 1, 51, Blocks.TARGET);
        }
        b.set(-11, 0, 51, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(-10, 0, 51, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.itemFrame(-11, 1, 50, Direction.NORTH, new ItemStack(Items.IRON_SWORD));
        b.itemFrame(-10, 1, 50, Direction.NORTH, new ItemStack(Items.SHIELD));
        b.set(-11, 1, 51, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(-10, 1, 51, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int x : new int[] {-14, -2}) b.set(x, 0, 44, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.NORTH, false));
    }

    /** The honour guard's barracks north-east of the hall: bunks, armour, a mess table and banners. */
    private void barracks() {
        int x0 = 39, z0 = -40, x1 = 52, z1 = -20, top = 6;
        shell(x0, z0, x1, z1, top, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BASALT, Blocks.POLISHED_BLACKSTONE);
        b.door(45, 0, z1, Blocks.WARPED_DOOR, Direction.SOUTH);
        b.door(46, 0, z1, Blocks.WARPED_DOOR, Direction.SOUTH);
        for (int z = z0 + 2; z <= z1 - 2; z += 3) {
            b.set(x0, 2, z, Blocks.CYAN_STAINED_GLASS);
            b.set(x1, 2, z, Blocks.CYAN_STAINED_GLASS);
        }
        for (int z = z0 + 1; z <= z1 - 4; z += 3) {
            b.bed(x0 + 1, 0, z, Blocks.BLUE_BED, Direction.WEST);
            b.bed(x1 - 1, 0, z, Blocks.BLUE_BED, Direction.EAST);
            b.set(x0 + 1, 0, z + 1, Builder.facing(Blocks.CHEST, Direction.EAST));
            b.set(x1 - 1, 0, z + 1, Builder.facing(Blocks.CHEST, Direction.WEST));
        }
        // The mess table down the middle.
        for (int z = z0 + 4; z <= z0 + 12; z++) {
            b.set(45, 0, z, Builder.slab(Blocks.POLISHED_BLACKSTONE_SLAB, true));
            b.set(46, 0, z, Builder.slab(Blocks.POLISHED_BLACKSTONE_SLAB, true));
            if (z % 2 == 0) {
                b.set(44, 0, z, Builder.stairs(Blocks.CRIMSON_STAIRS, Direction.WEST, false));
                b.set(47, 0, z, Builder.stairs(Blocks.CRIMSON_STAIRS, Direction.EAST, false));
            }
        }
        b.set(45, 1, z0 + 6, candle());
        b.set(46, 1, z0 + 10, candle());
        b.armorStand(42.5, 0, z0 + 1.5, 0, new ItemStack(Items.GOLDEN_HELMET), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_BOOTS));
        b.armorStand(49.5, 0, z0 + 1.5, 0, new ItemStack(Items.GOLDEN_HELMET), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_BOOTS));
        b.itemFrame(45, 2, z0 + 1, Direction.SOUTH, new ItemStack(Items.GOLDEN_SWORD));
        b.itemFrame(46, 2, z0 + 1, Direction.SOUTH, new ItemStack(Items.SHIELD));
        b.set(45, 4, z0 + 1, banner(Blocks.BLUE_WALL_BANNER, Direction.SOUTH));
        b.set(46, 4, z0 + 1, banner(Blocks.CYAN_WALL_BANNER, Direction.SOUTH));
        for (int z = z0 + 3; z <= z1 - 3; z += 6) b.set(45, top - 1, z, Builder.hangingLantern());
        for (int z = z0 + 3; z <= z1 - 3; z += 6) b.set(48, top - 1, z, Builder.hangingLantern());
        b.set(44, 3, z1 + 1, banner(Blocks.BLUE_WALL_BANNER, Direction.SOUTH));
        b.set(47, 3, z1 + 1, banner(Blocks.BLUE_WALL_BANNER, Direction.SOUTH));
    }

    /**
     * The Great Forge south of the hall: rows of anvils, lit blast furnaces under smoking chimneys,
     * a central hearth, and three lava falls behind glass in its back wall.
     */
    private void greatForge() {
        int x0 = 16, z0 = 18, x1 = 46, z1 = 44, top = 9;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                b.set(x, -1, z, hash(x, z) % 5 == 0 ? Blocks.POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS);
                for (int y = 0; y < top; y++) {
                    Block w = Blocks.AIR;
                    if (corner || (edge && Math.floorMod(x + z, 6) == 0)) w = Blocks.POLISHED_BASALT;
                    else if (edge) w = (y == 5 || y == 6) && Math.floorMod(x + z, 3) == 0 ? Blocks.ORANGE_STAINED_GLASS
                            : y == 0 ? Blocks.BLACKSTONE : Blocks.NETHER_BRICKS;
                    b.set(x, y, z, w);
                }
            }
        flatRoof(x0, z0, x1, z1, top, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_SLAB);
        // Doorways from the north and the west.
        for (int x = 28; x <= 32; x++) b.fill(x, 0, z0, x, 4, z0, Blocks.AIR);
        for (int z = 25; z <= 29; z++) b.fill(x0, 0, z, x0, 4, z, Blocks.AIR);
        b.sign(30, 5, z0 - 1, Blocks.CRIMSON_WALL_SIGN, Direction.NORTH, "", "THE GREAT", "FORGE", "");
        b.set(27, 5, z0 - 1, banner(Blocks.BLUE_WALL_BANNER, Direction.NORTH));
        b.set(33, 5, z0 - 1, banner(Blocks.CYAN_WALL_BANNER, Direction.NORTH));
        // Lava falls in the back wall.
        for (int x : new int[] {22, 30, 38}) lavaFall(x, z1, Direction.SOUTH, 1, 7);
        // Blast furnaces along the east wall under three chimneys.
        for (int z = z0 + 2; z <= z1 - 2; z += 2) b.set(x1 - 1, 0, z, lit(Blocks.BLAST_FURNACE, Direction.WEST));
        for (int z : new int[] {24, 30, 38}) {
            b.fill(x1 - 1, 1, z, x1 - 1, top + 4, z, Blocks.NETHER_BRICKS);
            b.campfire(x1 - 1, top + 5, z, true);
        }
        // Smokers and smithing tables along the west wall.
        for (int z = z0 + 2; z <= z1 - 2; z += 3) {
            if (z >= 24 && z <= 30) continue;
            b.set(x0 + 1, 0, z, z % 2 == 0 ? lit(Blocks.SMOKER, Direction.EAST) : Blocks.SMITHING_TABLE.defaultBlockState());
        }
        // The central hearth: a raised nether-brick forge with its fire, under a hood.
        for (int x = 29; x <= 33; x++)
            for (int z = 31; z <= 35; z++) {
                boolean rim = x == 29 || x == 33 || z == 31 || z == 35;
                b.set(x, 0, z, rim ? Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState() : Blocks.NETHER_BRICKS.defaultBlockState());
            }
        b.set(31, 1, 33, Blocks.MAGMA_BLOCK);
        b.campfire(31, 2, 33, true);
        for (int x = 30; x <= 32; x++)
            for (int z = 32; z <= 34; z++) if (x != 31 || z != 33) b.set(x, 6, z, Blocks.NETHER_BRICKS);
        b.fill(31, 7, 33, 31, top + 3, 33, Blocks.NETHER_BRICKS);
        b.set(31, 6, 33, Blocks.AIR);
        // Anvils, grindstones and quench cauldrons in rows.
        for (int x : new int[] {21, 25, 36, 40})
            for (int z : new int[] {23, 29, 37}) {
                b.set(x, 0, z, Builder.facing(hash(x, z) % 3 == 0 ? Blocks.CHIPPED_ANVIL : Blocks.ANVIL, Direction.NORTH));
                b.set(x + 1, 0, z + 1, (x + z) % 2 == 0 ? Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 3)
                        : Blocks.GRINDSTONE.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR));
            }
        b.itemFrame(20, 2, z0 + 1, Direction.SOUTH, new ItemStack(Items.IRON_PICKAXE));
        b.itemFrame(22, 2, z0 + 1, Direction.SOUTH, new ItemStack(Items.IRON_SWORD));
        b.itemFrame(38, 2, z0 + 1, Direction.SOUTH, new ItemStack(Items.IRON_AXE));
        for (int x = 20; x <= 42; x += 6)
            for (int z = 22; z <= 40; z += 6) if (!(x >= 29 && x <= 33 && z >= 31 && z <= 35)) b.set(x, top - 1, z, Builder.hangingLantern());
    }

    /** Coal, ore and barrels stacked in the yards behind the Great Forge and east of it. */
    private void stockpiles() {
        for (int x = 18; x <= 44; x += 4) {
            int h = hash(x, 49);
            b.set(x, 0, 49, h % 2 == 0 ? Blocks.COAL_BLOCK : Blocks.RAW_IRON_BLOCK);
            if (h % 3 == 0) b.set(x, 1, 49, Blocks.COAL_BLOCK);
            b.set(x + 1, 0, 50, Builder.facing(Blocks.BARREL, Direction.UP));
        }
        for (int z = 22; z <= 50; z += 5) {
            b.set(51, 0, z, Builder.facing(Blocks.BARREL, Direction.UP));
            b.set(52, 0, z, hash(52, z) % 2 == 0 ? Blocks.RAW_IRON_BLOCK : Blocks.COAL_BLOCK);
            b.set(52, 1, z, Builder.facing(Blocks.BARREL, Direction.UP));
        }
    }

    // ------------------------------------------------------------------ the Ember Hall

    private void hall() {
        int x0 = HALL_X0, x1 = HALL_X1, z0 = HALL_Z0, z1 = HALL_Z1, top = HALL_TOP;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                boolean pilaster = edge && Math.floorMod((x == x0 || x == x1) ? z : x, 4) == 0;
                b.set(x, -1, z, Math.floorMod(x + z, 2) == 0 ? Blocks.POLISHED_BLACKSTONE_BRICKS : Blocks.POLISHED_BLACKSTONE);
                for (int y = 0; y < top; y++) {
                    Block w = Blocks.AIR;
                    if (corner || pilaster) w = y == 6 && !corner ? Blocks.MAGMA_BLOCK : Blocks.POLISHED_BASALT;
                    else if (edge) {
                        boolean window = z == z1 && y >= 4 && y <= 6 && Math.floorMod(x, 4) == 2;
                        w = window ? Blocks.ORANGE_STAINED_GLASS : y == 8 ? Blocks.NETHER_BRICKS : Blocks.POLISHED_BLACKSTONE_BRICKS;
                    }
                    b.set(x, y, z, w);
                }
                b.set(x, top, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.set(x, top + 1, z, edge && Math.floorMod(x + z, 2) == 0 ? Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState()
                        : Builder.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
            }
        // A blue runner up the middle to the table.
        for (int x = x0 + 1; x <= 21; x++) {
            b.set(x, -1, 0, Blocks.LAPIS_BLOCK);
            b.set(x, -1, -1, Blocks.CHISELED_POLISHED_BLACKSTONE);
            b.set(x, -1, 1, Blocks.CHISELED_POLISHED_BLACKSTONE);
        }
        // The great door on the west, with the royal colours and one gold keystone.
        for (int z = -2; z <= 2; z++) b.fill(x0, 0, z, x0, 4, z, Blocks.AIR);
        b.set(x0, 5, 0, Blocks.GOLD_BLOCK);
        b.set(x0, 5, -1, Blocks.CHISELED_POLISHED_BLACKSTONE);
        b.set(x0, 5, 1, Blocks.CHISELED_POLISHED_BLACKSTONE);
        for (int z : new int[] {-4, 4}) {
            b.set(x0 - 1, 6, z, banner(Blocks.BLUE_WALL_BANNER, Direction.WEST));
            b.fill(x0 - 2, 0, z, x0 - 2, 1, z, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
            b.set(x0 - 2, 2, z, Blocks.SOUL_LANTERN);
        }
        b.set(x0 - 1, 7, 0, banner(Blocks.CYAN_WALL_BANNER, Direction.WEST));
        b.sign(x0 - 1, 4, -3, Blocks.CRIMSON_WALL_SIGN, Direction.WEST, "", "THE EMBER HALL", "of Veyrath", "");
        // Corner towers rising over the roof.
        for (int[] t : new int[][] {{x0, z0}, {x0, z1}, {x1, z0}, {x1, z1}}) {
            b.fill(t[0], top, t[1], t[0], top + 4, t[1], Blocks.POLISHED_BASALT);
            b.campfire(t[0], top + 5, t[1], false);
        }
        // Pillars down the hall, with banners between them.
        for (int x : new int[] {17, 24, 31, 38})
            for (int z : new int[] {-8, 8}) b.fill(x, 0, z, x, top - 1, z, Blocks.POLISHED_BASALT);
        for (int x : new int[] {20, 28, 35}) {
            b.set(x, 5, z0 + 1, banner(Blocks.BLUE_WALL_BANNER, Direction.SOUTH));
            b.set(x, 5, z1 - 1, banner(Blocks.CYAN_WALL_BANNER, Direction.NORTH));
        }
        // Soul lanterns on chains from the ceiling.
        for (int x : new int[] {19, 27, 35})
            for (int z : new int[] {-5, 5}) {
                b.set(x, top - 1, z, Blocks.CHAIN);
                b.set(x, top - 2, z, Blocks.SOUL_LANTERN.defaultBlockState().setValue(BlockStateProperties.HANGING, true));
            }
        // The long council table: top slabs on x 22..34 at z 0, with chairs on both sides.
        for (int x = 22; x <= 34; x++) b.set(x, 0, 0, Builder.slab(Blocks.POLISHED_BLACKSTONE_SLAB, true));
        for (int x : new int[] {23, 26, 29, 32}) {
            b.set(x, 0, -1, Builder.stairs(Blocks.CRIMSON_STAIRS, Direction.NORTH, false));
            b.set(x, 0, 1, Builder.stairs(Blocks.CRIMSON_STAIRS, Direction.SOUTH, false));
        }
        b.set(23, 1, 0, candle());
        b.set(34, 1, 0, candle());
        b.set(x0 + 2, 0, -9, Builder.facing(Blocks.LECTERN, Direction.EAST));
        // The dais and the throne of Veyrath, flanked by lava falls behind glass.
        for (int x = 39; x <= 45; x++)
            for (int z = -4; z <= 4; z++) b.set(x, 0, z, Math.abs(z) == 4 || x == 39 ? Blocks.CHISELED_POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int z = -4; z <= 4; z++) b.set(38, 0, z, Builder.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.EAST, false));
        b.set(43, 1, 0, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.EAST, false));
        b.set(43, 1, -1, Builder.slab(Blocks.POLISHED_BLACKSTONE_SLAB, false));
        b.set(43, 1, 1, Builder.slab(Blocks.POLISHED_BLACKSTONE_SLAB, false));
        b.set(44, 1, 0, Blocks.CHISELED_POLISHED_BLACKSTONE);
        b.set(44, 2, 0, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(44, 3, 0, Blocks.GOLD_BLOCK);
        b.set(44, 4, 0, Blocks.GILDED_BLACKSTONE);
        b.set(45, 6, 0, banner(Blocks.BLUE_WALL_BANNER, Direction.WEST));
        for (int z : new int[] {-6, 6}) b.set(45, 5, z, banner(Blocks.CYAN_WALL_BANNER, Direction.WEST));
        for (int z : new int[] {-3, 3}) b.set(40, 1, z, Blocks.SOUL_LANTERN);
        for (int z : new int[] {-3, 3}) lavaFall(x1, z, Direction.EAST, 1, 8);
        // The way to the vault, through the north wall behind the table.
        for (int x = 29; x <= 31; x++) b.fill(x, 0, z0, x, 3, z0, Blocks.AIR);
        b.set(30, 4, z0, Blocks.CHISELED_POLISHED_BLACKSTONE);
        b.set(28, 2, z0 + 1, banner(Blocks.BLUE_WALL_BANNER, Direction.SOUTH));
        b.set(32, 2, z0 + 1, banner(Blocks.BLUE_WALL_BANNER, Direction.SOUTH));
    }

    // ------------------------------------------------------------------ the vault

    private void vault() {
        int x0 = 24, x1 = 36, z0 = -51, z1 = -13;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                int h = hash(x, z);
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                b.fill(x, -3, z, x, 6, z, h % 10 == 0 ? Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS : Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.set(x, 7, z, edge && Math.floorMod(x + z, 2) == 0 ? Blocks.POLISHED_BLACKSTONE_BRICK_WALL.defaultBlockState()
                        : Builder.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, false));
            }
        // The corridor (floor y -1, air y 0..4) from the hall to the antechamber.
        for (int z = -32; z <= z1; z++)
            for (int x = 29; x <= 31; x++) {
                b.set(x, -1, z, Math.floorMod(z, 5) == 0 ? Blocks.CHISELED_POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE);
                b.fill(x, 0, z, x, 4, z, Blocks.AIR);
            }
        for (int z = -15; z >= -30; z -= 5) b.set(30, 4, z, Builder.hangingLantern());
        // The three lava channels, one deep, running on under iron bars into sealed alcoves.
        for (int z : CHANNEL_ROWS) {
            for (int x = 26; x <= 34; x++) b.set(x, -1, z, Blocks.LAVA);
            for (int x : new int[] {26, 27, 33, 34}) b.fill(x, 0, z, x, 1, z, Blocks.AIR);
            for (int y = 0; y <= 1; y++) {
                b.set(28, y, z, bars(true));
                b.set(32, y, z, bars(true));
            }
        }
        // The antechamber with the oath braziers.
        for (int x = 25; x <= 35; x++)
            for (int z = -39; z <= -33; z++) {
                boolean border = x == 25 || x == 35 || z == -39 || z == -33;
                b.set(x, -1, z, border ? Blocks.GILDED_BLACKSTONE : Math.floorMod(x + z, 2) == 0 ? Blocks.POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.fill(x, 0, z, x, 5, z, Blocks.AIR);
            }
        for (int i = 0; i < BRAZIERS.length; i++) {
            int bx = BRAZIERS[i][0], bz = BRAZIERS[i][1];
            b.set(bx, 0, bz, Blocks.CHISELED_POLISHED_BLACKSTONE);
            b.set(bx, 1, bz, soulCampfire(false));
            Direction face = bx < 30 ? Direction.EAST : Direction.WEST;
            b.sign(bx + face.getStepX(), 0, bz, Blocks.CRIMSON_WALL_SIGN, face, "", WORDS[i], "", "");
        }
        for (int x : new int[] {28, 32}) b.set(x, 5, -36, Builder.hangingLantern());
        b.set(35, 3, -35, banner(Blocks.BLUE_WALL_BANNER, Direction.WEST));
        b.set(25, 3, -36, banner(Blocks.BLUE_WALL_BANNER, Direction.EAST));
        b.sign(30, 4, -39, Blocks.CRIMSON_WALL_SIGN, Direction.SOUTH, "Keep it apart", "from the door.", "Never carry it", "in anger.");
        // The vault door and its frame.
        for (int y = 0; y <= 4; y++) {
            b.set(28, y, DOOR_Z, Blocks.POLISHED_BASALT);
            b.set(32, y, DOOR_Z, Blocks.POLISHED_BASALT);
        }
        for (int x = 29; x <= 31; x++) b.set(x, 4, DOOR_Z, Blocks.GILDED_BLACKSTONE);
        closeDoor(b);
        // The vault and the Ember Key's pedestal.
        for (int x = 25; x <= 35; x++)
            for (int z = -50; z <= -41; z++) {
                int dx = x - 30, dz = z + 47;
                b.set(x, -1, z, Math.max(Math.abs(dx), Math.abs(dz)) == 2 ? Blocks.GILDED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.fill(x, 0, z, x, 5, z, Blocks.AIR);
            }
        b.set(30, 0, -47, Blocks.GILDED_BLACKSTONE);
        b.set(30, 1, -47, Blocks.CHISELED_NETHER_BRICKS);
        for (int x : new int[] {28, 32}) {
            b.set(x, 0, -48, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
            b.set(x, 1, -48, Blocks.SOUL_LANTERN);
        }
        for (int x : new int[] {29, 31}) b.set(x, 3, -50, banner(Blocks.BLUE_WALL_BANNER, Direction.SOUTH));
        b.set(30, 4, -50, banner(Blocks.CYAN_WALL_BANNER, Direction.SOUTH));
        for (int x : new int[] {26, 34}) lavaFall(x, z0, Direction.NORTH, 1, 4);
        for (int x : new int[] {27, 33}) b.set(x, 5, -45, Builder.hangingLantern());
    }

    private static void closeDoor(Builder b) {
        for (int x = 29; x <= 31; x++)
            for (int y = 0; y <= 3; y++) {
                Block s = x == 30 && y == 2 ? Blocks.CHISELED_POLISHED_BLACKSTONE
                        : y == 3 ? Blocks.GILDED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS;
                b.set(x, y, DOOR_Z, s);
            }
    }

    /** Scene 015: the three oath braziers flare up with blue fire. */
    public static void braziersLit(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        for (int[] p : BRAZIERS) b.set(p[0], 1, p[1], soulCampfire(true));
    }

    /** Scene 015: the vault door opens (its stones withdraw into the floor and walls). */
    public static void vaultOpen(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        for (int x = 29; x <= 31; x++)
            for (int y = 0; y <= 3; y++) b.set(x, y, DOOR_Z, Blocks.AIR);
        b.set(30, -1, DOOR_Z, Blocks.GILDED_BLACKSTONE);
        for (int[] p : BRAZIERS) b.set(p[0], 1, p[1], soulCampfire(true));
    }

    // ------------------------------------------------------------------ lighting

    /**
     * Ember lights set flush in the ground wherever the street is open to the sky, on a grid close
     * enough that no outdoor block in the city is left dark.
     */
    private void lights() {
        for (int x = -86; x <= 86; x++)
            for (int z = -86; z <= 86; z++) {
                int c = cheb(x, z);
                boolean city = c <= WALL_IN - 1 || (c >= WALL_OUT + 1 && c < MOAT_IN);
                int step = city ? 6 : 10;
                if (Math.floorMod(x, step) != 3 || Math.floorMod(z, step) != 3) continue;
                if (!city && (c <= MOAT_OUT + 1 || Math.hypot(x, z) > 92)) continue;
                BlockState ground = b.get(x, -1, z);
                if (ground.isAir() || !ground.getFluidState().isEmpty() || ground.is(Blocks.GLASS) || ground.is(Blocks.MAGMA_BLOCK)) continue;
                boolean open = true;
                for (int y = 0; y <= 12 && open; y++) open = b.get(x, y, z).isAir();
                if (open && b.get(x, -2, z).getFluidState().isEmpty()) b.set(x, -1, z, Blocks.SHROOMLIGHT);
            }
    }
}
