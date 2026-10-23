package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Tidewatch, the harbour of Thalassia (Scene 016), anchored at MC-001 block (60, 65, 620);
 * everything here is relative to it, with y = 0 the street and the harbour's water surface at
 * y = -2 (vanilla sea level).
 *
 * The town stands on the north side (z -88..10); the harbour basin it carves is everything south
 * of the quay edge (z 11..88 for x &lt;= 51), closed by a breakwater at x -89, x 89 and z 89. East of
 * the town the headland rises to standing height 8 ({@link #ground}); the party arrives on its
 * ridge road (x 71..73, (72, 8, -44)), which turns west at z -2..2 and runs down into Harbour
 * Street (z -3..2). The lighthouse stands on the headland tip round (72, 8, 26).
 *
 * The quay runs z 3..10. The main pier (x -2..2, z 11..39, T-head x -4..4, z 40..44, deck y -1,
 * ladder into the sea at (0, -6..-1, 45)) has the Thalassian ship moored to its west (centre x -9,
 * z 16..40, deck y 0, gangplank at z 28). The west pier (x -40..-37) has a smaller ship to its
 * west (centre x -47, z 15..33, deck y -1, gangplank at z 24). The loading crane on the quay
 * (x 12..16, z 4..11) has a deck at y 3 reached by a ladder at (14, 0..3, 3); its jib runs along
 * x 14 at y 7, z 3..14, missing (14, 7, 9) and (14, 7, 10), and its chain (14, 6, 14), (14, 5, 14)
 * is gone. The fish stalls are at x -12..-4, z 3..6.
 *
 * Maren's chart room (the harbourmaster's house, x -28..-16, z -16..-4, door (-22, 0, -4)) holds
 * the chart table (x -24..-20, z -12..-10, y 0); the fragments lie on it at {@link #SLOTS} (y 1),
 * the Tide Anchor's place being (-20, 1, -10). The tavern is x 16..32, z -20..-4; the net lofts are
 * x -62..-50 and -80..-68, z -16..-4; houses line Main Street (x -2..2) and the lanes.
 *
 * Offshore, a line of buoys runs from the pier end to the half-sunk old coastal watch (x 24..34,
 * z 55..65, seabed y -14) with bubble columns beside it. The watch's lower room (x 26..32, z 57..63,
 * floor y -13) is kept dry by its old door at (29, -12, 56); fallen stones (x 28..30, y -12..-11,
 * z 60) block the alcove holding the Tide Anchor housing at (29, -12, 62).
 */
public final class Tidewatch {
    public static final int X0 = -90, X1 = 90, Z0 = -90, Z1 = 90;
    /** The last row of the quay (x &lt;= 51) and of the headland (x &gt;= 52); the sea begins south of them. */
    public static final int SHORE_Z = 10, CLIFF_Z = 34;
    /** The six fragments' places on the chart table (y 1): Eldervale, Sylvaris, Stonecrown, Solkar, Veyrath, Thalassia. */
    public static final int[][] SLOTS = {{-24, -12}, {-22, -12}, {-20, -12}, {-24, -10}, {-22, -10}, {-20, -10}};
    public static final Block[] FRAGMENTS = {Blocks.CHISELED_STONE_BRICKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.CHISELED_DEEPSLATE,
            Blocks.CHISELED_SANDSTONE, Blocks.CHISELED_NETHER_BRICKS, Blocks.DARK_PRISMARINE};
    /** The buoys from the pier end to the sunken watch, and the bubble columns divers breathe from. */
    public static final int[][] BUOYS = {{4, 47}, {9, 49}, {14, 50}, {19, 51}, {24, 52}};
    public static final int[][] BUBBLES = {{6, 45}, {11, 51}, {16, 48}, {21, 53}, {27, 53}, {31, 53}};

    private static final Palette HARBOUR = new Palette("tidewatch_harbour", Blocks.STONE_BRICKS, Blocks.SPRUCE_PLANKS, Blocks.DARK_OAK_LOG,
            Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DARK_OAK_DOOR,
            Blocks.GLASS_PANE, Blocks.DARK_OAK_WALL_SIGN, Blocks.STONE_BRICKS);
    private static final Palette TIDE = new Palette("tidewatch_tide", Blocks.STONE_BRICKS, Blocks.WHITE_TERRACOTTA, Blocks.DARK_OAK_LOG,
            Blocks.STRIPPED_DARK_OAK_LOG, Blocks.SPRUCE_PLANKS, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_OAK_DOOR,
            Blocks.GLASS_PANE, Blocks.DARK_OAK_WALL_SIGN, Blocks.STONE_BRICKS);
    private static final Palette TAVERN = new Palette("tidewatch_tavern", Blocks.COBBLESTONE, Blocks.DARK_OAK_PLANKS, Blocks.SPRUCE_LOG,
            Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_PLANKS, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_SLAB, Blocks.SPRUCE_DOOR,
            Blocks.GLASS_PANE, Blocks.SPRUCE_WALL_SIGN, Blocks.COBBLESTONE);
    private static final Palette LOFT = new Palette("tidewatch_loft", Blocks.COBBLESTONE, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG,
            Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_DOOR,
            Blocks.GLASS_PANE, Blocks.SPRUCE_WALL_SIGN, Blocks.COBBLESTONE);

    private final Builder b;

    private Tidewatch(ServerLevel level, BlockPos anchor) {
        this.b = new Builder(level, anchor, 1016L);
    }

    private static int hash(int x, int z) {
        int h = x * 73_856_093 ^ z * 19_349_663;
        return (h ^ (h >>> 13)) & 0x7fffffff;
    }

    public static void build(ServerLevel level, BlockPos anchor) {
        Tidewatch t = new Tidewatch(level, anchor);
        t.land();
        t.breakwater();
        t.quay();
        t.streets();
        t.headlandRoad();
        t.lighthouse();
        t.piers();
        t.ship(-9, 16, 40, 4, 0, new int[] {24, 32}, new int[] {14, 12});
        t.ship(-47, 15, 33, 3, -1, new int[] {24}, new int[] {11});
        t.gangplanks();
        t.fishingBoats();
        t.crane();
        t.stall(-12, false);
        t.stall(-7, true);
        t.cargo();
        t.chartRoom();
        t.tavern();
        t.netLoft(-62, -16, -50, -4);
        t.netLoft(-80, -16, -68, -4);
        t.houses();
        t.seabed();
        t.buoys();
        t.sunkenWatch();
        t.outskirts();
    }

    // ------------------------------------------------------------------ terrain

    /** Standing height of the land: 0 in town, rising to 8 along the headland east of it. */
    public static int ground(int x, int z) {
        if (x < 50 || z > CLIFF_Z) return 0;
        double h = Math.min(8, Math.min((x - 50) / 2.5, Math.min((91 - x) / 1.5, (z + 91) / 2.0)));
        return (int) Math.max(0, Math.round(h));
    }

    public static boolean sea(int x, int z) {
        if (x < -88 || x > 88 || z > 88) return false;
        return x <= 51 ? z > SHORE_Z : z > CLIFF_Z;
    }

    /** The seabed block's y: shallow by the quay, twelve blocks of water further out. */
    public static int bed(int x, int z) {
        int shore = x <= 51 ? SHORE_Z : CLIFF_Z;
        return -6 - Math.min(8, (z - shore) / 2);
    }

    private static boolean nearSea(int x, int z) {
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++) if (sea(x + dx, z + dz)) return true;
        return false;
    }

    private void land() {
        for (int x = X0; x <= X1; x++)
            for (int z = Z0; z <= Z1; z++) {
                int h = hash(x, z);
                if (sea(x, z)) {
                    int bed = bed(x, z);
                    b.fill(x, bed - 4, z, x, bed - 1, z, Blocks.STONE);
                    b.set(x, bed, z, h % 4 == 0 ? Blocks.GRAVEL : Blocks.SAND);
                    b.fill(x, bed + 1, z, x, -2, z, Blocks.WATER);
                    b.fill(x, -1, z, x, 14, z, Blocks.AIR);
                    continue;
                }
                int g = ground(x, z);
                boolean shore = nearSea(x, z);
                if (shore) b.fill(x, -22, z, x, -4, z, Blocks.STONE);
                for (int y = -3; y <= g - 2; y++) b.set(x, y, z, shore || y < g - 3 ? Blocks.STONE : Blocks.DIRT);
                Block top;
                if (g > 0 && shore) top = h % 3 == 0 ? Blocks.STONE : Blocks.GRASS_BLOCK;
                else if (g > 0 && h % 17 == 0) top = Blocks.STONE;
                else top = h % 11 == 0 ? Blocks.COARSE_DIRT : h % 29 == 0 ? Blocks.GRAVEL : Blocks.GRASS_BLOCK;
                b.set(x, g - 1, z, top);
                b.fill(x, g, z, x, g + 14, z, Blocks.AIR);
                if (top == Blocks.GRASS_BLOCK && h % 9 == 0) b.set(x, g, z, h % 4 == 0 ? Blocks.FERN : Blocks.SHORT_GRASS);
            }
    }

    /** The breakwater that closes the harbour basin on the west, east and south. */
    private void breakwater() {
        for (int x = -89; x <= 89; x++) mole(x, 89);
        for (int z = SHORE_Z + 1; z <= 88; z++) mole(-89, z);
        for (int z = CLIFF_Z + 1; z <= 88; z++) mole(89, z);
    }

    private void mole(int x, int z) {
        int h = hash(x, z);
        b.fill(x, -22, z, x, -2, z, Blocks.STONE_BRICKS);
        b.set(x, -1, z, h % 5 == 0 ? Blocks.MOSSY_STONE_BRICKS : h % 7 == 0 ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS);
        b.fill(x, 0, z, x, 6, z, Blocks.AIR);
        if ((x + z) % 2 == 0) b.set(x, 0, z, Blocks.STONE_BRICK_WALL);
        if ((x + z) % 16 == 0) {
            b.set(x, 0, z, Blocks.STONE_BRICK_WALL);
            b.set(x, 1, z, Blocks.LANTERN);
        }
    }

    // ------------------------------------------------------------------ waterfront

    private void quay() {
        for (int x = -88; x <= 50; x++)
            for (int z = 3; z <= SHORE_Z; z++) {
                int h = hash(x, z);
                b.set(x, -1, z, z == SHORE_Z ? Blocks.SMOOTH_STONE : h % 9 == 0 ? Blocks.CRACKED_STONE_BRICKS
                        : h % 5 == 0 ? Blocks.POLISHED_ANDESITE : Blocks.STONE_BRICKS);
                b.fill(x, 0, z, x, 6, z, Blocks.AIR);
                if (z == SHORE_Z) b.fill(x, -8, z, x, -2, z, Blocks.STONE_BRICKS);
            }
        // Bollards along the edge and lamps a step back from it.
        for (int x = -86; x <= 48; x += 6) if (openQuay(x)) b.set(x, 0, SHORE_Z, Blocks.POLISHED_BLACKSTONE_WALL);
        for (int x = -85; x <= 45; x += 10) if (openQuay(x)) lamp(x, 0, 9);
        // Ladders down the quay wall for anyone who ends up in the water.
        for (int x : new int[] {-60, -5, 8, 40}) seaLadder(x, -5, 11, Direction.SOUTH);
    }

    /** Quay edge spots clear of the piers and the crane. */
    private static boolean openQuay(int x) {
        return !(x >= -3 && x <= 3) && !(x >= -41 && x <= -36) && !(x >= 11 && x <= 17);
    }

    private void lamp(int x, int y, int z) {
        b.fill(x, y, z, x, y + 2, z, Blocks.DARK_OAK_FENCE);
        b.set(x, y + 3, z, Blocks.LANTERN);
    }

    /** A ladder from {@code y0} up to the deck or quay at y -1, on the face of the block behind it. */
    private void seaLadder(int x, int y0, int z, Direction facing) {
        for (int y = y0; y <= -1; y++)
            b.set(x, y, z, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, facing).setValue(BlockStateProperties.WATERLOGGED, y <= -2));
    }

    private void pave(int x0, int z0, int x1, int z1) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                int h = hash(x, z);
                b.set(x, -1, z, h % 7 == 0 ? Blocks.MOSSY_COBBLESTONE : h % 3 == 0 ? Blocks.COBBLESTONE
                        : h % 5 == 0 ? Blocks.POLISHED_ANDESITE : Blocks.STONE_BRICKS);
                b.fill(x, 0, z, x, 3, z, Blocks.AIR);
            }
    }

    /** A footpath from a door to the street. */
    private void path(int x0, int z0, int x1, int z1) {
        for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++)
            for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) {
                b.set(x, -1, z, hash(x, z) % 4 == 0 ? Blocks.GRAVEL : Blocks.DIRT_PATH);
                b.set(x, 0, z, Blocks.AIR);
            }
    }

    private void streets() {
        pave(-88, -3, 50, 2);      // Harbour Street
        pave(-2, -88, 2, -4);      // Main Street
        pave(-60, -42, 46, -40);   // Rope Walk
        pave(-34, -88, -32, -4);   // West Lane
        pave(34, -88, 36, -4);     // East Lane
        for (int x = -81; x <= 45; x += 12) lamp(x, 0, 2);
        for (int z = -10; z >= -80; z -= 10) lamp(3, 0, z);
        for (int x = -54; x <= 42; x += 12) if (Math.abs(x) > 3 && (x < -35 || x > -31) && (x < 33 || x > 37)) lamp(x, 0, -39);
        for (int z = -14; z >= -84; z -= 14) {
            lamp(-31, 0, z);
            lamp(37, 0, z);
        }
        // A carved anchor stone where Main Street meets the harbour.
        b.set(-4, 0, -4, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(-4, 1, -4, Blocks.GOLD_BLOCK);
        b.set(4, 0, -4, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(4, 1, -4, Blocks.GOLD_BLOCK);
    }

    /** The road over the headland: south along the ridge, then west and down into Harbour Street. */
    private void headlandRoad() {
        java.util.List<int[]> cells = new java.util.ArrayList<>();
        for (int x = 71; x <= 73; x++) for (int z = -88; z <= 2; z++) cells.add(new int[] {x, z});
        for (int x = 50; x <= 70; x++) for (int z = -2; z <= 2; z++) cells.add(new int[] {x, z});
        java.util.Set<Long> road = new java.util.HashSet<>();
        for (int[] c : cells) road.add(((long) c[0] << 32) ^ (c[1] & 0xffffffffL));
        for (int[] c : cells) {
            int g = ground(c[0], c[1]);
            b.set(c[0], g - 1, c[1], hash(c[0], c[1]) % 5 == 0 ? Blocks.COARSE_DIRT : Blocks.DIRT_PATH);
            b.fill(c[0], g, c[1], c[0], g + 3, c[1], Blocks.AIR);
        }
        // Half steps where the road climbs, so it can be walked without jumping.
        for (int[] c : cells) {
            int g = ground(c[0], c[1]);
            for (int[] d : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int nx = c[0] + d[0], nz = c[1] + d[1];
                if (road.contains(((long) nx << 32) ^ (nz & 0xffffffffL)) && ground(nx, nz) == g + 1) {
                    b.set(c[0], g, c[1], Builder.slab(Blocks.COBBLESTONE_SLAB, false));
                    break;
                }
            }
        }
        for (int z = -84; z <= -12; z += 12) lamp(75, ground(75, z), z);
        for (int x = 54; x <= 66; x += 6) lamp(x, ground(x, -3), -3);
        b.standingSign(74, ground(74, 4), 4, Blocks.DARK_OAK_SIGN, 4, "Tidewatch", "Harbour of", "Thalassia", "");
    }

    private void lighthouse() {
        int cx = 72, cz = 26, base = 8;
        for (int dx = -6; dx <= 6; dx++)
            for (int dz = -6; dz <= 6; dz++) {
                double d = Math.hypot(dx, dz);
                int x = cx + dx, z = cz + dz;
                if (d <= 5.5) {
                    b.fill(x, -3, z, x, base - 1, z, Blocks.STONE_BRICKS);
                    b.set(x, base - 1, z, d > 4.5 ? Blocks.POLISHED_ANDESITE : Blocks.STONE_BRICKS);
                    b.fill(x, base, z, x, base + 26, z, Blocks.AIR);
                }
                if (d > 2.5 && d <= 3.65) {
                    for (int y = base; y <= base + 18; y++) {
                        boolean light = (dz == 0 && Math.abs(dx) == 3) && (y - base) % 6 == 2;
                        boolean window = (dx == 0 && dz == -3 || dz == 0 && Math.abs(dx) == 3) && (y - base) % 6 == 5;
                        Block s = light ? Blocks.GLOWSTONE : window ? Blocks.GLASS_PANE
                                : ((y - base) / 3) % 2 == 0 ? Blocks.SMOOTH_QUARTZ : Blocks.POLISHED_BLACKSTONE_BRICKS;
                        b.set(x, y, z, s);
                    }
                } else if (d <= 2.5) {
                    b.fill(x, base, z, x, base + 18, z, Blocks.AIR);
                }
                if (d > 3.65 && d <= 5.2) b.set(x, base + 18, z, Builder.slab(Blocks.STONE_BRICK_SLAB, true));
                if (d <= 5.2) b.set(x, base + 19, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                if (d > 4.3 && d <= 5.2) b.set(x, base + 20, z, Blocks.DARK_OAK_FENCE);
            }
        // The door on the north side, facing the road, and the ladder up the south wall.
        b.door(cx, base, cz - 3, Blocks.DARK_OAK_DOOR, Direction.NORTH);
        b.ladder(cx, base, base + 19, cz + 2, Direction.NORTH);
        b.set(cx - 1, base, cz, Blocks.LANTERN);
        b.set(cx + 1, base, cz - 1, Builder.facing(Blocks.BARREL, Direction.UP));
        // The lantern room: a lamp column inside glass, gold corners and a dark roof.
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++) {
                for (int y = base + 20; y <= base + 22; y++) {
                    Block s = dx == 0 && dz == 0 ? (y == base + 21 ? Blocks.GLOWSTONE : Blocks.SEA_LANTERN)
                            : Math.abs(dx) == 1 && Math.abs(dz) == 1 ? Blocks.GOLD_BLOCK : Blocks.GLASS;
                    b.set(cx + dx, y, cz + dz, s);
                }
            }
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2) continue;
                b.set(cx + dx, base + 23, cz + dz, Builder.slab(Blocks.DARK_PRISMARINE_SLAB, false));
            }
        b.set(cx, base + 23, cz, Blocks.GOLD_BLOCK);
        b.set(cx, base + 24, cz, Blocks.LIGHTNING_ROD);
    }

    private void piers() {
        // The main pier and its T-head.
        for (int x = -4; x <= 4; x++)
            for (int z = 11; z <= 44; z++) {
                if (z < 40 && Math.abs(x) > 2) continue;
                b.set(x, -1, z, Math.abs(x) == 2 && z < 40 ? Builder.log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.Z)
                        : (hash(x, z) % 6 == 0 ? Blocks.DARK_OAK_PLANKS : Blocks.SPRUCE_PLANKS).defaultBlockState());
            }
        for (int z = 12; z <= 44; z += 4)
            for (int x : z >= 40 ? new int[] {-4, -2, 2, 4} : new int[] {-2, 2}) b.fill(x, bed(x, z), z, x, -2, z, Blocks.DARK_OAK_LOG);
        b.fill(0, bed(0, 44), 44, 0, -2, 44, Blocks.DARK_OAK_LOG);
        seaLadder(0, -6, 45, Direction.SOUTH);
        for (int z = 12; z <= 36; z += 6) {
            b.set(-2, 0, z, Blocks.DARK_OAK_FENCE);
            if (z != 18) b.set(2, 0, z, Blocks.DARK_OAK_FENCE);
        }
        b.set(-4, 0, 40, Blocks.DARK_OAK_FENCE);
        b.set(4, 0, 40, Blocks.DARK_OAK_FENCE);
        lampShort(2, 16);
        lampShort(2, 32);
        lampShort(-4, 44);
        lampShort(4, 44);
        // Mooring ropes from the bollards to the ship.
        for (int x = -4; x <= -3; x++) b.set(x, 0, 24, Builder.log(Blocks.CHAIN, Direction.Axis.X));
        for (int x = -5; x <= -3; x++) b.set(x, 0, 36, Builder.log(Blocks.CHAIN, Direction.Axis.X));

        // The west pier.
        for (int x = -40; x <= -37; x++)
            for (int z = 11; z <= 34; z++)
                b.set(x, -1, z, x == -40 || x == -37 ? Builder.log(Blocks.STRIPPED_SPRUCE_LOG, Direction.Axis.Z) : Blocks.SPRUCE_PLANKS.defaultBlockState());
        for (int z = 12; z <= 34; z += 4) for (int x : new int[] {-40, -37}) b.fill(x, bed(x, z), z, x, -2, z, Blocks.SPRUCE_LOG);
        b.fill(-38, bed(-38, 34), 34, -38, -2, 34, Blocks.SPRUCE_LOG);
        seaLadder(-38, -6, 35, Direction.SOUTH);
        for (int z = 14; z <= 32; z += 6) b.set(-40, 0, z, Blocks.DARK_OAK_FENCE);
        lampShort(-37, 18);
        lampShort(-37, 30);
        for (int x = -43; x <= -41; x++) {
            b.set(x, 0, 20, Builder.log(Blocks.CHAIN, Direction.Axis.X));
            b.set(x, 0, 32, Builder.log(Blocks.CHAIN, Direction.Axis.X));
        }

        // A short fishing jetty east of the crane.
        for (int x = 30; x <= 32; x++) for (int z = 11; z <= 22; z++) b.set(x, -1, z, Blocks.SPRUCE_PLANKS);
        for (int z = 12; z <= 22; z += 5) for (int x : new int[] {30, 32}) b.fill(x, bed(x, z), z, x, -2, z, Blocks.SPRUCE_LOG);
        lampShort(32, 22);
        b.set(30, 0, 22, Builder.facing(Blocks.BARREL, Direction.UP));
    }

    /** A two-high lamp on a pier deck. */
    private void lampShort(int x, int z) {
        b.fill(x, 0, z, x, 1, z, Blocks.DARK_OAK_FENCE);
        b.set(x, 2, z, Blocks.LANTERN);
    }

    // ------------------------------------------------------------------ ships

    private static int beam(int z, int z0, int z1, int half) {
        int bow = 6;
        if (z > z1 - bow) return (int) Math.round(half * (z1 - z) / (double) bow);
        if (z <= z0 + 1) return half - 1;
        return half;
    }

    private static boolean inHull(int cx, int z0, int z1, int half, int deckY, int x, int y, int z) {
        if (z < z0 || z > z1 || y < deckY - 5 || y > deckY) return false;
        int w = beam(z, z0, z1, half);
        int wy = y >= deckY - 2 ? w : w - (deckY - 2 - y);
        return wy >= 0 && Math.abs(x - cx) <= wy;
    }

    /**
     * A moored ship with its bow to the south: a dark hull with a navy stripe and a dry hold, a
     * spruce deck, masts with black, navy and white sails, a stern cabin and a gilded bowsprit.
     */
    private void ship(int cx, int z0, int z1, int half, int deckY, int[] masts, int[] heights) {
        for (int z = z0; z <= z1; z++)
            for (int y = deckY - 5; y <= deckY; y++)
                for (int x = cx - half; x <= cx + half; x++) {
                    if (!inHull(cx, z0, z1, half, deckY, x, y, z)) continue;
                    boolean shell = !inHull(cx, z0, z1, half, deckY, x - 1, y, z) || !inHull(cx, z0, z1, half, deckY, x + 1, y, z)
                            || !inHull(cx, z0, z1, half, deckY, x, y - 1, z) || !inHull(cx, z0, z1, half, deckY, x, y, z - 1)
                            || !inHull(cx, z0, z1, half, deckY, x, y, z + 1);
                    Block s;
                    if (shell) s = y == deckY - 1 ? Blocks.BLUE_TERRACOTTA : Blocks.DARK_OAK_PLANKS;
                    else if (y == deckY) s = (x + z) % 5 == 0 ? Blocks.STRIPPED_SPRUCE_LOG : Blocks.SPRUCE_PLANKS;
                    else s = Blocks.AIR;
                    b.set(x, y, z, s);
                }
        // Gunwale.
        for (int z = z0; z <= z1; z++) {
            int w = beam(z, z0, z1, half);
            b.set(cx - w, deckY + 1, z, Blocks.DARK_OAK_FENCE);
            b.set(cx + w, deckY + 1, z, Blocks.DARK_OAK_FENCE);
        }
        for (int x = cx - half + 1; x <= cx + half - 1; x++) b.set(x, deckY + 1, z0, Blocks.DARK_OAK_FENCE);
        // Bowsprit and gilded figurehead.
        b.set(cx, deckY, z1, Blocks.GOLD_BLOCK);
        for (int z = z1; z <= z1 + 3; z++) b.set(cx, deckY + 1, z, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.Z));
        // Masts, yards and sails.
        for (int i = 0; i < masts.length; i++) {
            int mz = masts[i], h = heights[i];
            int lower = deckY + 4, upper = deckY + h - 2;
            for (int x = cx - half - 1; x <= cx + half + 1; x++) {
                b.set(x, lower, mz, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.X));
                b.set(x, upper, mz, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.X));
            }
            for (int y = lower + 1; y < upper; y++)
                for (int x = cx - half; x <= cx + half; x++) {
                    int row = upper - y;
                    Block wool = row == 1 ? Blocks.BLACK_WOOL : row == (upper - lower) / 2 + 1 ? (Math.abs(x - cx) == 1 ? Blocks.YELLOW_WOOL : Blocks.BLUE_WOOL) : Blocks.WHITE_WOOL;
                    b.set(x, y, mz, wool);
                }
            b.fill(cx, deckY + 1, mz, cx, deckY + h, mz, Blocks.SPRUCE_LOG);
            b.set(cx, deckY + h + 1, mz, Blocks.BLACK_BANNER);
        }
        // The stern cabin.
        int c0 = z0 + 2, c1 = z0 + 2 + (half >= 4 ? 3 : 2), cw = half - 1;
        for (int x = cx - cw; x <= cx + cw; x++)
            for (int z = c0; z <= c1; z++) {
                boolean edge = Math.abs(x - cx) == cw || z == c0 || z == c1;
                for (int y = deckY + 1; y <= deckY + 3; y++) {
                    Block s = Blocks.AIR;
                    if (edge) s = y == deckY + 2 && Math.abs(x - cx) == cw && (z - c0) % 2 == 1 ? Blocks.GLASS_PANE : Blocks.DARK_OAK_PLANKS;
                    b.set(x, y, z, s);
                }
                b.set(x, deckY + 4, z, edge ? Blocks.DARK_OAK_PLANKS : Blocks.SPRUCE_PLANKS);
                if (edge) b.set(x, deckY + 5, z, Blocks.DARK_OAK_FENCE);
            }
        b.door(cx, deckY + 1, c1, Blocks.DARK_OAK_DOOR, Direction.SOUTH);
        b.set(cx - cw + 1, deckY + 1, c0 + 1, Blocks.CHEST);
        b.set(cx + cw - 1, deckY + 1, c0 + 1, half >= 4 ? Blocks.CARTOGRAPHY_TABLE.defaultBlockState() : Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(cx, deckY + 3, c0 + 1, Builder.hangingLantern());
        b.set(cx - cw, deckY + 6, c0, Blocks.LANTERN);
        b.set(cx + cw, deckY + 6, c0, Blocks.LANTERN);
        // Deck fittings: a hatch, cargo and lamps.
        int mid = (z0 + z1) / 2;
        b.trapdoor(cx, deckY + 1, mid + 1, Blocks.DARK_OAK_TRAPDOOR, Direction.NORTH, false, false);
        b.set(cx - half + 1, deckY + 1, mid + 2, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(cx + half - 1, deckY + 1, mid + 3, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(cx - half + 1, deckY + 1, mid - 3, Blocks.LANTERN);
        b.set(cx + half - 1, deckY + 1, mid - 3, Blocks.LANTERN);
    }

    /** Gangplanks from the piers onto the two ships. */
    private void gangplanks() {
        b.set(-3, 0, 28, Builder.slab(Blocks.SPRUCE_SLAB, false));
        b.set(-4, 0, 28, Blocks.SPRUCE_PLANKS);
        b.set(-5, 1, 28, Blocks.AIR);
        for (int x = -43; x <= -41; x++) b.set(x, -1, 24, Builder.slab(Blocks.SPRUCE_SLAB, true));
        b.set(-44, 0, 24, Blocks.AIR);
    }

    /** Two rowing boats tied up at the fishing jetty. */
    private void fishingBoats() {
        for (int cx : new int[] {35, 39}) {
            for (int z = 13; z <= 18; z++)
                for (int x = cx - 1; x <= cx + 1; x++) {
                    boolean side = x != cx || z == 13 || z == 18;
                    b.set(x, -2, z, Blocks.OAK_PLANKS);
                    b.set(x, -1, z, side ? Blocks.OAK_PLANKS : Blocks.AIR);
                }
            b.set(cx, -1, 15, Builder.slab(Blocks.OAK_SLAB, false));
        }
        b.set(33, 0, 15, Builder.log(Blocks.CHAIN, Direction.Axis.X));
        b.set(34, -1, 15, Builder.log(Blocks.CHAIN, Direction.Axis.X));
    }

    // ------------------------------------------------------------------ quay works

    /**
     * The loading crane: a timber tower with a deck at y 3 (ladder on its north side), a mast and a
     * jib along x 14 at y 7. The jib's broken stretch (14, 7, 9..10) and its chain (14, 6..5, 14)
     * are what the harbour work restores.
     */
    private void crane() {
        for (int[] l : new int[][] {{12, 4}, {16, 4}, {12, 8}, {16, 8}}) b.fill(l[0], 0, l[1], l[0], 2, l[1], Blocks.DARK_OAK_LOG);
        for (int x : new int[] {12, 16}) b.fill(x, bed(x, 11), 11, x, 2, 11, Blocks.DARK_OAK_LOG);
        b.fill(14, 0, 4, 14, 2, 4, Blocks.DARK_OAK_LOG);
        for (int x = 12; x <= 16; x++)
            for (int z = 4; z <= 11; z++) {
                b.set(x, 3, z, (x + z) % 4 == 0 ? Blocks.DARK_OAK_PLANKS : Blocks.SPRUCE_PLANKS);
                boolean edge = x == 12 || x == 16 || z == 4 || z == 11;
                if (edge && !(x == 14 && z == 4)) b.set(x, 4, z, Blocks.DARK_OAK_FENCE);
            }
        b.ladder(14, 0, 3, 3, Direction.NORTH);
        b.fill(14, 4, 6, 14, 6, 6, Blocks.SPRUCE_LOG);
        for (int z = 3; z <= 14; z++) if (z != 9 && z != 10) b.set(14, 7, z, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.Z));
        b.set(14, 6, 3, Blocks.COBBLESTONE);          // counterweight
        b.set(14, 6, 7, Blocks.DARK_OAK_FENCE);       // brace
        b.set(16, 5, 4, Blocks.LANTERN);
        b.set(12, 5, 11, Blocks.LANTERN);
        // The snapped beam and the old chain, fallen on the quay.
        b.set(19, 0, 3, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.X));
        b.set(20, 0, 3, Builder.log(Blocks.STRIPPED_SPRUCE_LOG, Direction.Axis.X));
        b.set(13, 0, 9, Builder.log(Blocks.CHAIN, Direction.Axis.X));
        b.sign(12, 1, 3, Blocks.DARK_OAK_WALL_SIGN, Direction.NORTH, "Harbour crane", "", "Out of service", "");
    }

    /** Scene 016: with the beam and chain back, the crane lifts its first load again. */
    public static void craneFixed(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        b.set(14, 7, 9, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.Z));
        b.set(14, 7, 10, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.Z));
        b.set(14, 6, 14, Blocks.CHAIN);
        b.set(14, 5, 14, Blocks.CHAIN);
        b.set(14, 4, 14, Builder.facing(Blocks.BARREL, Direction.UP));
        b.sign(12, 1, 3, Blocks.DARK_OAK_WALL_SIGN, Direction.NORTH, "Harbour crane", "", "Working", "");
    }

    /** A fish stall: posts, a striped awning, the catch on ice and crates behind. */
    private void stall(int x0, boolean salmon) {
        int z0 = 3, z1 = 6;
        for (int[] p : new int[][] {{x0, z0}, {x0 + 3, z0}, {x0, z1}, {x0 + 3, z1}}) b.fill(p[0], 0, p[1], p[0], 2, p[1], Blocks.SPRUCE_FENCE);
        for (int x = x0; x <= x0 + 3; x++) for (int z = z0; z <= z1; z++) b.set(x, 3, z, ((x + z) & 1) == 0 ? Blocks.BLUE_WOOL : Blocks.WHITE_WOOL);
        b.set(x0 + 1, 0, z0, Blocks.PACKED_ICE);
        b.set(x0 + 2, 0, z0, Blocks.PACKED_ICE);
        b.itemFrame(x0 + 1, 1, z0, Direction.UP, new ItemStack(salmon ? Items.SALMON : Items.COD));
        b.itemFrame(x0 + 2, 1, z0, Direction.UP, new ItemStack(salmon ? Items.TROPICAL_FISH : Items.COD));
        b.set(x0 + 1, 0, z1, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(x0 + 2, 0, z1, Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
        b.sign(x0, 1, z0 - 1, Blocks.SPRUCE_WALL_SIGN, Direction.NORTH, "", salmon ? "Salmon" : "Fresh cod", "caught this", "morning");
    }

    private void crate(int x, int y, int z) {
        int h = hash(x * 3 + y, z);
        b.set(x, y, z, h % 3 == 0 ? Blocks.SPRUCE_PLANKS.defaultBlockState() : h % 3 == 1 ? Builder.facing(Blocks.BARREL, Direction.UP)
                : Blocks.BROWN_WOOL.defaultBlockState());
    }

    /** Cargo waiting on the quay and the piers; the three damaged barrels are placed with the scene's clues. */
    private void cargo() {
        int[][] stacks = {{22, 0, 4}, {23, 0, 4}, {24, 0, 4}, {22, 1, 4}, {23, 0, 5}, {-20, 0, 4}, {-19, 0, 4}, {-18, 0, 4}, {-19, 1, 4},
                {-20, 0, 5}, {-34, 0, 4}, {-33, 0, 4}, {-34, 1, 4}, {-33, 0, 5}, {40, 0, 5}, {41, 0, 5}, {42, 0, 5}, {41, 1, 5}, {42, 0, 6},
                {1, 0, 33}, {1, 1, 33}, {-1, 0, 34}, {-38, 0, 28}, {-39, 0, 28}, {-60, 0, 5}, {-59, 0, 5}, {-60, 1, 5}};
        for (int[] c : stacks) crate(c[0], c[1], c[2]);
        b.set(-21, 0, 4, Blocks.HAY_BLOCK);
        b.set(43, 0, 5, Blocks.HAY_BLOCK);
        // Signs of damage round the barrels the tremors knocked over: split lids and spilled staves.
        b.set(8, 0, 7, Builder.slab(Blocks.SPRUCE_SLAB, false));
        b.trapdoor(6, 0, 8, Blocks.SPRUCE_TRAPDOOR, Direction.NORTH, false, false);
        b.trapdoor(1, 0, 23, Blocks.SPRUCE_TRAPDOOR, Direction.NORTH, false, false);
        b.set(22, 0, 8, Builder.slab(Blocks.SPRUCE_SLAB, false));
        b.trapdoor(21, 0, 9, Blocks.SPRUCE_TRAPDOOR, Direction.NORTH, false, false);
    }

    // ------------------------------------------------------------------ buildings

    /**
     * Walls, floor and roof of a building: stone footing, framed corners and posts, windows at
     * y 1..2. Returns the roof's ridge height.
     */
    private int shell(int x0, int z0, int x1, int z1, int top, Palette p, Block floorA, Block floorB) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean ex = x == x0 || x == x1, ez = z == z0 || z == z1;
                boolean edge = ex || ez, corner = ex && ez;
                b.set(x, -1, z, edge ? p.foundation() : (x + z) % 2 == 0 ? floorA : floorB);
                for (int y = 0; y < top; y++) {
                    Block s = Blocks.AIR;
                    if (corner) s = p.frame();
                    else if (edge) {
                        int along = ex ? z - z0 : x - x0;
                        boolean post = along % 4 == 0;
                        boolean window = !post && (y == 1 || y == 2) && along % 4 == 2;
                        s = post ? p.frame() : y == 0 ? p.foundation() : window ? p.window() : p.wall();
                    }
                    b.set(x, y, z, s);
                }
            }
        return b.gableRoof(x0, z0, x1, z1, top, p);
    }

    /** A beam under the roof along the building's long axis, with lanterns hanging from it. */
    private void beam(int x0, int z0, int x1, int z1, int top) {
        int mx = (x0 + x1) / 2, mz = (z0 + z1) / 2;
        if (x1 - x0 >= z1 - z0) {
            for (int x = x0 + 1; x < x1; x++) b.set(x, top, mz, Builder.log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.X));
            for (int x = x0 + 2; x < x1 - 1; x += 4) b.set(x, top - 1, mz, Builder.hangingLantern());
        } else {
            for (int z = z0 + 1; z < z1; z++) b.set(mx, top, z, Builder.log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.Z));
            for (int z = z0 + 2; z < z1 - 1; z += 4) b.set(mx, top - 1, z, Builder.hangingLantern());
        }
    }

    private void wallBanner(int x, int y, int z, Block banner, Direction facing) {
        b.set(x, y, z, banner.defaultBlockState().setValue(WallBannerBlock.FACING, facing));
    }

    /**
     * Maren's chart room, the harbourmaster's house. The chart table (x -24..-20, z -12..-10) is
     * where the fragments are laid ({@link #SLOTS}); Maren stands by its north-east corner.
     */
    private void chartRoom() {
        int x0 = -28, z0 = -16, x1 = -16, z1 = -4, top = 5;
        shell(x0, z0, x1, z1, top, HARBOUR, Blocks.DARK_OAK_PLANKS, Blocks.SPRUCE_PLANKS);
        b.door(-22, 0, z1, Blocks.DARK_OAK_DOOR, Direction.SOUTH);
        b.sign(-24, 2, z1 + 1, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "Harbourmaster", "of Tidewatch", "", "Capt. Maren");
        beam(x0, z0, x1, z1, top);
        // The navy runner from the door to the table.
        for (int z = -9; z <= -5; z++) b.set(-22, 0, z, Blocks.BLUE_CARPET);
        // The chart table, with maps and Maren's compass laid on it.
        for (int x = -24; x <= -20; x++)
            for (int z = -12; z <= -10; z++) b.set(x, 0, z, z == -11 && x == -22 ? Blocks.CARTOGRAPHY_TABLE : Blocks.DARK_OAK_PLANKS);
        b.itemFrame(-23, 1, -11, Direction.UP, new ItemStack(Items.MAP));
        b.itemFrame(-22, 1, -11, Direction.UP, new ItemStack(Items.COMPASS));
        b.itemFrame(-21, 1, -11, Direction.UP, new ItemStack(Items.MAP));
        b.set(-25, 0, -11, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
        b.set(-22, 0, -13, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
        // Shelves of charts and logbooks along the north wall, the log lectern between them.
        for (int x = -27; x <= -17; x++) {
            if (x >= -24 && x <= -20) continue;
            b.fill(x, 0, -15, x, 2, -15, Blocks.BOOKSHELF);
        }
        b.set(-22, 0, -15, Builder.facing(Blocks.LECTERN, Direction.SOUTH));
        b.set(-24, 0, -15, Builder.facing(Blocks.BARREL, Direction.SOUTH));
        b.set(-20, 0, -15, Builder.facing(Blocks.BARREL, Direction.SOUTH));
        b.itemFrame(-23, 2, -15, Direction.SOUTH, new ItemStack(Items.MAP));
        b.itemFrame(-21, 2, -15, Direction.SOUTH, new ItemStack(Items.MAP));
        // Maren's desk and stores on the west side.
        b.set(-27, 0, -12, Blocks.DARK_OAK_PLANKS);
        b.set(-27, 1, -12, Blocks.CANDLE);
        b.set(-26, 0, -12, Builder.stairs(Blocks.DARK_OAK_STAIRS, Direction.EAST, false));
        b.set(-27, 0, -8, Builder.facing(Blocks.CHEST, Direction.EAST));
        b.set(-27, 0, -7, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-27, 1, -7, Blocks.POTTED_FERN);
        b.set(-27, 0, -6, Builder.facing(Blocks.BARREL, Direction.EAST));
        // A bunk in the south-east corner and the spyglass by the harbour window.
        b.bed(-17, 0, -6, Blocks.BLUE_BED, Direction.NORTH);
        b.itemFrame(-17, 2, -8, Direction.WEST, new ItemStack(Items.SPYGLASS));
        // Thalassia's colours: black, gold and navy.
        wallBanner(-27, 2, -12, Blocks.BLACK_WALL_BANNER, Direction.EAST);
        wallBanner(-27, 2, -8, Blocks.BLUE_WALL_BANNER, Direction.EAST);
        wallBanner(-17, 2, -12, Blocks.YELLOW_WALL_BANNER, Direction.WEST);
        lampShort(-19, -3);
    }

    /** Scene 016: the party lays out the five fragments they carry on Maren's chart table. */
    public static void fragmentsLaid(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        for (int i = 0; i < 5; i++) b.set(SLOTS[i][0], 1, SLOTS[i][1], FRAGMENTS[i]);
    }

    /** The tavern on Harbour Street: bar, fireplace, tables and the barrels behind the counter. */
    private void tavern() {
        int x0 = 16, z0 = -20, x1 = 32, z1 = -4, top = 5;
        shell(x0, z0, x1, z1, top, TAVERN, Blocks.SPRUCE_PLANKS, Blocks.DARK_OAK_PLANKS);
        b.door(24, 0, z1, Blocks.SPRUCE_DOOR, Direction.SOUTH);
        b.sign(26, 2, z1 + 1, Blocks.SPRUCE_WALL_SIGN, Direction.SOUTH, "The Anchor", "and Gull", "", "Tavern");
        lampShort(22, -3);
        for (int x = x0 + 1; x < x1; x++) {
            b.set(x, top, -12, Builder.log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.X));
            b.set(x, top, -8, Builder.log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.X));
        }
        for (int x = 19; x <= 29; x += 5) {
            b.set(x, top - 1, -12, Builder.hangingLantern());
            b.set(x, top - 1, -8, Builder.hangingLantern());
        }
        // The bar, with the barrels and the brewing stand behind it.
        for (int x = 19; x <= 28; x++) b.set(x, 0, -15, Blocks.DARK_OAK_PLANKS);
        b.set(20, 1, -15, Blocks.CANDLE);
        b.set(27, 1, -15, Blocks.LANTERN);
        for (int x = 18; x <= 30; x++) {
            if (x == 24) {
                b.set(x, 0, -19, Blocks.BREWING_STAND);
                continue;
            }
            b.set(x, 0, -19, Builder.facing(Blocks.BARREL, Direction.SOUTH));
            if (x % 2 == 0) b.set(x, 1, -19, Builder.facing(Blocks.BARREL, Direction.SOUTH));
        }
        // The fireplace on the west wall.
        b.set(17, 0, -11, Blocks.STONE_BRICKS);
        b.set(17, 0, -9, Blocks.STONE_BRICKS);
        b.fill(17, 1, -11, 17, 4, -9, Blocks.STONE_BRICKS);
        b.campfire(17, 0, -10, false);
        // Tables and stools.
        for (int[] t : new int[][] {{21, -9}, {27, -9}, {21, -12}, {27, -12}}) {
            b.set(t[0], 0, t[1], Blocks.SPRUCE_FENCE);
            b.set(t[0], 1, t[1], Blocks.SPRUCE_PRESSURE_PLATE);
            b.set(t[0] - 1, 0, t[1], Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
            b.set(t[0] + 1, 0, t[1], Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
        }
        b.set(31, 0, -5, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(31, 0, -6, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(31, 1, -5, Blocks.POTTED_RED_TULIP);
        wallBanner(31, 2, -12, Blocks.BLACK_WALL_BANNER, Direction.WEST);
        b.itemFrame(31, 2, -9, Direction.WEST, new ItemStack(Items.FISHING_ROD));
    }

    /**
     * A net loft: an open-fronted store on the ground floor and the loft above it (floor y 4),
     * where the nets hang to dry, with a hoist over the loading door.
     */
    private void netLoft(int x0, int z0, int x1, int z1) {
        int top = 8, mx = (x0 + x1) / 2, mz = (z0 + z1) / 2;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean ex = x == x0 || x == x1, ez = z == z0 || z == z1;
                boolean edge = ex || ez, corner = ex && ez;
                b.set(x, -1, z, edge ? Blocks.COBBLESTONE : Blocks.SPRUCE_PLANKS);
                for (int y = 0; y < top; y++) {
                    Block s = Blocks.AIR;
                    boolean post = corner || (edge && ((ex ? z - z0 : x - x0) % 4 == 0));
                    if (y == 4 && !edge) s = Blocks.SPRUCE_PLANKS;
                    else if (post) s = Blocks.SPRUCE_LOG;
                    else if (edge && z == z1 && y < 4) s = y == 3 ? Blocks.STRIPPED_SPRUCE_LOG : Blocks.AIR; // the open front
                    else if (edge) s = (y == 6 && (x + z) % 3 == 0) ? Blocks.GLASS_PANE : y == 0 ? Blocks.COBBLESTONE : Blocks.SPRUCE_PLANKS;
                    b.set(x, y, z, s);
                }
            }
        b.gableRoof(x0, z0, x1, z1, top, LOFT);
        // Ladder up through the loft floor.
        b.set(x0 + 1, 4, z0 + 1, Blocks.AIR);
        b.ladder(x0 + 1, 0, 4, z0 + 1, Direction.EAST);
        // The loading door and the hoist above it.
        b.fill(mx, 5, z1, mx, 6, z1, Blocks.AIR);
        b.set(mx, 7, z1 + 1, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.Z));
        b.set(mx, 7, z1 + 2, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.Z));
        b.set(mx, 6, z1 + 2, Blocks.CHAIN);
        b.set(mx, 5, z1 + 2, Blocks.CHAIN);
        // Ground floor: barrels, chests, rods and a mending bench.
        for (int x = x0 + 3; x <= x1 - 1; x++) b.set(x, 0, z0 + 1, Builder.facing(Blocks.BARREL, Direction.SOUTH));
        b.set(x0 + 3, 1, z0 + 1, Builder.facing(Blocks.BARREL, Direction.SOUTH));
        b.set(x1 - 1, 0, mz, Builder.facing(Blocks.CHEST, Direction.WEST));
        b.set(x1 - 1, 0, mz + 1, Blocks.CRAFTING_TABLE);
        b.set(x0 + 1, 0, mz + 2, Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
        b.set(x0 + 1, 0, mz + 3, Blocks.WHITE_WOOL);
        b.itemFrame(x0 + 1, 2, mz, Direction.EAST, new ItemStack(Items.FISHING_ROD));
        b.set(mx, 3, mz, Builder.hangingLantern());
        b.set(x0 + 3, 3, z1 - 2, Builder.hangingLantern());
        // The loft: nets hung under the roof, rope and wool bales.
        for (int x = x0 + 2; x <= x1 - 2; x++)
            for (int z = z0 + 2; z <= z1 - 2; z += 3) if ((x + z) % 2 == 0) b.set(x, 7, z, Blocks.COBWEB);
        b.set(x1 - 1, 5, z0 + 1, Blocks.WHITE_WOOL);
        b.set(x1 - 2, 5, z0 + 1, Blocks.BROWN_WOOL);
        b.set(x1 - 1, 6, z0 + 1, Blocks.WHITE_WOOL);
        b.set(mx, 5, mz, Blocks.LANTERN);
        b.set(x1 - 1, 5, z1 - 1, Blocks.LANTERN);
    }

    /** Townhouses along Main Street and the lanes: {x0, z0, x1, z1, door side (0 E, 1 W), far end of the front path}. */
    private void houses() {
        int[][] lots = {
                {-13, -36, -5, -26, 0, -3}, {5, -36, 13, -26, 1, 3}, {-13, -58, -5, -46, 0, -3}, {5, -58, 13, -46, 1, 3},
                {-13, -78, -5, -66, 0, -3}, {5, -78, 13, -66, 1, 3}, {-30, -36, -20, -26, 1, -31}, {-30, -58, -20, -48, 1, -31},
                {20, -36, 30, -26, 0, 33}, {20, -58, 30, -48, 0, 33}, {-46, -36, -38, -28, 0, -35}};
        Palette[] palettes = {TIDE, Palette.TIMBER, Palette.STONE, HARBOUR, Palette.ELDER};
        for (int i = 0; i < lots.length; i++) house(lots[i], palettes[i % palettes.length], i);
    }

    private void house(int[] lot, Palette p, int i) {
        int x0 = lot[0], z0 = lot[1], x1 = lot[2], z1 = lot[3], top = 4;
        boolean east = lot[4] == 0;
        int mz = (z0 + z1) / 2;
        int ridge = shell(x0, z0, x1, z1, top, p, p.floor(), p.floor());
        int dx = east ? x1 : x0;
        Direction out = east ? Direction.EAST : Direction.WEST;
        b.door(dx, 0, mz, p.door(), out);
        int step = east ? 1 : -1;
        path(dx + step, mz, lot[5], mz);
        b.fill(dx + step, 0, mz + 2, dx + step, 1, mz + 2, Blocks.DARK_OAK_FENCE);
        b.set(dx + step, 2, mz + 2, Blocks.LANTERN);
        // Flowers by the front wall.
        for (int z = z0 + 1; z < z1; z++) {
            if (Math.abs(z - mz) <= 2) continue;
            int h = hash(dx + step, z);
            if (h % 3 == 0) b.set(dx + step, 0, z, h % 2 == 0 ? Blocks.CORNFLOWER : Blocks.POPPY);
        }
        beam(x0, z0, x1, z1, top);
        if (i % 3 == 0) b.chimney(east ? x0 : x1, z0 + 2, ridge + 1, Blocks.STONE_BRICKS);
        furnish(new Room(b, new int[] {x0, z0, x1, z1}, out, dx, mz), i);
    }

    private void furnish(Room r, int i) {
        Block[] carpets = {Blocks.BLUE_CARPET, Blocks.BLACK_CARPET, Blocks.LIGHT_BLUE_CARPET, Blocks.CYAN_CARPET};
        Block[] beds = {Blocks.BLUE_BED, Blocks.CYAN_BED, Blocks.WHITE_BED, Blocks.LIGHT_BLUE_BED};
        int w = r.width, d = r.depth;
        r.carpet(2, 2, w - 3, d - 3, carpets[i % carpets.length]);
        r.bed(0, d - 1, beds[i % beds.length], r.back);
        r.facing(1, 0, d - 1, Blocks.BARREL, Direction.UP);
        r.set(1, 1, d - 1, Blocks.POTTED_FERN);
        r.facing(w - 1, 0, d - 1, Blocks.CHEST, r.front);
        r.facing(w - 1, 0, d - 2, i % 2 == 0 ? Blocks.SMOKER : Blocks.FURNACE, r.left);
        r.set(w - 1, 0, d - 3, Blocks.CRAFTING_TABLE);
        r.table(w / 2, d / 2, r.left);
        r.set(0, 0, 2, Blocks.BOOKSHELF);
        r.set(0, 0, 3, i % 2 == 0 ? Blocks.BOOKSHELF : Blocks.LOOM);
        r.set(w - 1, 0, 2, Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
        r.set(0, 0, d - 3, Builder.facing(Blocks.BARREL, r.right));
    }

    // ------------------------------------------------------------------ the sea

    /** Seagrass, sea pickles and sea lanterns across the harbour floor. */
    private void seabed() {
        for (int x = -88; x <= 88; x++)
            for (int z = 11; z <= 88; z++) {
                if (!sea(x, z)) continue;
                if (x >= 20 && x <= 38 && z >= 46 && z <= 70) continue; // the watch keeps its own floor
                int bed = bed(x, z), h = hash(x, z);
                if (!b.get(x, bed + 1, z).is(Blocks.WATER)) continue;
                if (h % 53 == 0) b.set(x, bed, z, Blocks.SEA_LANTERN);
                else if (h % 5 == 0) b.set(x, bed + 1, z, Blocks.SEAGRASS);
                else if (h % 37 == 0) b.set(x, bed + 1, z, pickles(1 + h % 4));
            }
    }

    private static BlockState pickles(int n) {
        return Blocks.SEA_PICKLE.defaultBlockState().setValue(BlockStateProperties.PICKLES, n).setValue(BlockStateProperties.WATERLOGGED, true);
    }

    /** The buoy line out to the watch, lit underwater, and the bubble columns divers breathe from. */
    private void buoys() {
        for (int i = 0; i < BUOYS.length; i++) {
            int x = BUOYS[i][0], z = BUOYS[i][1], bed = bed(x, z);
            b.set(x, bed, z, Blocks.SEA_LANTERN);
            for (int y = bed + 1; y <= -3; y++) b.set(x, y, z, Blocks.CHAIN.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true));
            b.set(x, -2, z, i % 2 == 0 ? Blocks.YELLOW_WOOL : Blocks.BLACK_WOOL);
            b.set(x, -1, z, Blocks.DARK_OAK_FENCE);
            b.set(x, 0, z, Blocks.LANTERN);
        }
        for (int[] c : BUBBLES) {
            int x = c[0], z = c[1], bed = bed(x, z);
            b.set(x, bed, z, Blocks.SOUL_SAND);
            b.set(x + 1, bed, z, Blocks.SEA_LANTERN);
            b.set(x - 1, bed + 1, z, pickles(4));
            for (int y = bed + 1; y <= -2; y++) b.set(x, y, z, Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(BlockStateProperties.DRAG, false));
        }
    }

    private Block watchStone(int x, int y, int z) {
        int h = hash(x * 31 + y, z);
        return h % 5 == 0 ? Blocks.MOSSY_STONE_BRICKS : h % 7 == 0 ? Blocks.CRACKED_STONE_BRICKS : h % 11 == 0 ? Blocks.PRISMARINE_BRICKS : Blocks.STONE_BRICKS;
    }

    /**
     * The old coastal watch, half sunk: a broken tower standing out of the water, highest on its
     * south-west side. Its lower room is still dry behind its door; the fallen stones block the
     * alcove where the Tide Anchor housing is set.
     */
    private void sunkenWatch() {
        int x0 = 24, x1 = 34, z0 = 55, z1 = 65;
        for (int x = x0 - 1; x <= x1 + 1; x++) for (int z = z0 - 1; z <= z1 + 1; z++) b.fill(x, -18, z, x, -14, z, Blocks.STONE_BRICKS);
        // The lower room's shell is solid so the sea stays out; the room is hollowed from it.
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++)
                for (int y = -13; y <= -9; y++) b.set(x, y, z, watchStone(x, y, z));
        b.fill(26, -12, 57, 32, -10, 63, Blocks.AIR);
        for (int x = 26; x <= 32; x++)
            for (int z = 57; z <= 63; z++) b.set(x, -13, z, x == 26 || x == 32 || z == 57 || z == 63 ? Blocks.STONE_BRICKS : Blocks.PRISMARINE_BRICKS);
        // The way in: a passage through the north wall, and the old door that still keeps the water out.
        b.set(29, -12, 55, Blocks.WATER);
        b.set(29, -11, 55, Blocks.WATER);
        b.door(29, -12, 56, Blocks.DARK_OAK_DOOR, Direction.NORTH);
        b.set(28, -11, 55, Blocks.SEA_LANTERN);
        b.set(30, -11, 55, Blocks.SEA_LANTERN);
        b.set(29, -10, 55, Blocks.CHISELED_STONE_BRICKS);
        // The front room: light through the ceiling, old stores and rubble.
        for (int x : new int[] {27, 29, 31}) {
            b.set(x, -9, 58, Blocks.SEA_LANTERN);
            b.set(x, -9, 62, Blocks.SEA_LANTERN);
        }
        b.set(32, -12, 57, Builder.facing(Blocks.CHEST, Direction.WEST));
        b.set(26, -12, 57, Builder.facing(Blocks.BARREL, Direction.EAST));
        b.set(26, -12, 59, Builder.slab(Blocks.COBBLESTONE_SLAB, false));
        b.set(32, -12, 59, Blocks.MOSSY_COBBLESTONE);
        b.set(31, -12, 59, Builder.slab(Blocks.STONE_BRICK_SLAB, false));
        b.set(26, -11, 58, Blocks.LANTERN.defaultBlockState());
        b.set(26, -12, 58, Blocks.STONE_BRICKS);
        // The wall across the room, and the stones fallen into its archway (x 28..30, y -12..-11).
        for (int x = 26; x <= 32; x++) for (int y = -12; y <= -10; y++) b.set(x, y, 60, Blocks.STONE_BRICKS);
        Block[] fallen = {Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.CRACKED_STONE_BRICKS};
        for (int x = 28; x <= 30; x++) for (int y = -12; y <= -11; y++) b.set(x, y, 60, fallen[(x + y + 40) % 3]);
        b.set(29, -10, 60, Blocks.CHISELED_STONE_BRICKS);
        // The alcove and the housing (the housing block itself is placed with the scene's clues).
        b.set(29, -13, 62, Blocks.GOLD_BLOCK);
        for (int y = -12; y <= -10; y++) {
            b.set(28, y, 63, Blocks.PRISMARINE_BRICKS);
            b.set(30, y, 63, Blocks.PRISMARINE_BRICKS);
            b.set(29, y, 63, y == -11 ? Blocks.GOLD_BLOCK : Blocks.DARK_PRISMARINE);
        }
        b.set(28, -12, 62, Blocks.PRISMARINE_WALL);
        b.set(30, -12, 62, Blocks.PRISMARINE_WALL);
        b.set(26, -12, 63, Blocks.LANTERN);
        b.set(32, -12, 63, Blocks.LANTERN);
        // The broken tower above, flooded to the sea's level.
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                int h = hash(x, z);
                if (edge) {
                    int top = Math.min(7, -6 + ((z - z0) + (x1 - x)) * 3 / 5 + h % 3);
                    for (int y = -8; y <= top; y++) {
                        if (hash(x * 7 + y, z) % 9 == 0 && y < top - 1) continue;
                        b.set(x, y, z, watchStone(x, y, z));
                    }
                    if (top >= 0 && (x + z) % 2 == 0) b.set(x, top + 1, z, Blocks.STONE_BRICK_WALL);
                } else if (h % 6 == 0) {
                    b.set(x, -8, z, h % 4 == 0 ? Blocks.COBBLESTONE.defaultBlockState() : Builder.slab(Blocks.STONE_BRICK_SLAB, false));
                } else if (h % 13 == 0) {
                    b.set(x, -8, z, pickles(3));
                }
            }
        // A ledge just above the water inside the south-west corner, where a diver can rest.
        b.fill(25, -8, 64, 25, -2, 64, Blocks.STONE_BRICKS);
        b.fill(25, -1, 62, 27, -1, 64, Blocks.STONE_BRICKS);
        b.set(25, 0, 64, Blocks.LANTERN);
        // Fallen masonry round the foot of the tower, and a toppled column lying to the east.
        for (int x = 18; x <= 40; x++)
            for (int z = 48; z <= 72; z++) {
                if (x >= x0 && x <= x1 && z >= z0 && z <= z1) continue;
                if (x >= 26 && x <= 32 && z <= 54) continue; // keep the way to the door clear
                int h = hash(x + 5, z * 3);
                if (h % 9 == 0 && b.get(x, -13, z).is(Blocks.WATER)) b.set(x, -13, z, watchStone(x, -13, z));
                else if (h % 31 == 0 && b.get(x, -13, z).is(Blocks.WATER)) b.set(x, -13, z, Blocks.SEA_LANTERN);
            }
        for (int i = 0; i <= 6; i++) b.set(35 + i, -13, 58 + i / 3, watchStone(35 + i, -13, 58));
    }

    /** Scene 016: with the Tide Anchor lifted out, the light over its housing goes dark. */
    public static void anchorTaken(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        b.set(29, -9, 62, Blocks.PRISMARINE_BRICKS);
        b.set(29, -11, 63, Blocks.DARK_PRISMARINE);
    }

    // ------------------------------------------------------------------ edges

    /** Low field walls on the town's north and west edges, and spruce on the outskirts. */
    private void outskirts() {
        for (int x = -88; x <= 68; x++) {
            if (Math.abs(x) <= 3 || (x >= -35 && x <= -31) || (x >= 33 && x <= 37)) continue;
            b.set(x, ground(x, -88), -88, hash(x, -88) % 3 == 0 ? Blocks.MOSSY_COBBLESTONE_WALL : Blocks.COBBLESTONE_WALL);
        }
        for (int z = -87; z <= -4; z++) b.set(-88, 0, z, hash(-88, z) % 3 == 0 ? Blocks.MOSSY_COBBLESTONE_WALL : Blocks.COBBLESTONE_WALL);
        for (int x = X0; x <= X1; x++)
            for (int z = Z0; z <= Z1; z++) {
                boolean fringe = (z <= -82 && z >= -87 && x <= 68) || (x >= -87 && x <= -83 && z <= -20) || (x >= 79 && x <= 86 && z >= -80 && z <= 18);
                if (!fringe) continue;
                if (Math.abs(x) <= 6 || (x >= -38 && x <= -28) || (x >= 30 && x <= 40)) continue;
                int h = hash(x * 5, z * 7);
                int g = ground(x, z);
                if (h % 41 != 0 || !b.get(x, g - 1, z).is(Blocks.GRASS_BLOCK)) continue;
                b.spruce(x, g, z, 6 + h % 4);
            }
    }
}
