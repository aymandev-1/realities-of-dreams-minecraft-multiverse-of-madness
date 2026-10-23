package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Scenes 018 and 019: the excavation beneath the Ancient Northern Monument and the Ancient
 * Builders' gateway chamber it opens. Shares the monument's anchor, MC-001 block (40, 108, -200),
 * with y = 0 the courtyard floor; the monument itself is built first (NorthernMonument).
 *
 * The expedition camp fills the east of the courtyard. Its dig (x 19..21) starts at z 12 and the
 * stair goes down northward, one block per step: step k has its stair block at (x, -1 - k, 12 - k),
 * k = 0..39, inside a deepslate-lined tunnel with timber supports every eight steps.
 *
 * At the bottom is the Builders' machine hall (x 7..33, z -34..-28, floor y -40): copper channels in
 * the floor, three empty conduit sockets on pedestals, the still clock, the twin-circle and warning
 * carvings in the end walls, and six lock stones before the great round door (plane z -35).
 *
 * Behind the door: a landing (x 10..30, z -39..-36) on the lip of a chasm that rings the gateway's
 * dais (centre x 20, z -52, radius 7). A stone bridge (x 19..21, z -44..-40) crosses the chasm; a
 * line of stepping stones curves round its east side; a walled alcove with the Builders' console runs
 * north from the dais (x 18..22, z -67..-59). Anyone who falls lands in a deep pool and climbs out by
 * the ladder at the landing's west end. The gateway is an upright double ring in the plane z -52.
 *
 * Positions the scenes check (BLOCKS stages, clues, sequences) are in tools/story/build_s018.py and
 * build_s019.py and must match these.
 */
public final class GatewayChamber {
    public static final int STEPS = 40, DIG_Z = 12;
    public static final int FLOOR = -40;
    public static final int CX = 20, CZ = -52, RADIUS = 16;
    public static final double DAIS = 7.5;
    public static final int RING_Z = -52, RING_Y = -31, RING_R = 7;
    /** The flooded bottom of the chasm: water from POOL_BOTTOM + 1 to POOL_TOP. */
    public static final int POOL_TOP = -53, POOL_BOTTOM = -56;
    /** The steps that collapsed and the supports that broke (Scene 018 rebuilds them). */
    public static final int GAP_FROM = 27, GAP_TO = 29;
    public static final int[] BROKEN = {16, 24};
    /** The stepping stones across the chasm, {x0, z0, x1, z1}, from the landing round to the dais. */
    public static final int[][] PLATFORMS = {{25, -42, 26, -41}, {28, -45, 29, -44}, {30, -48, 31, -47},
            {31, -51, 32, -50}, {30, -54, 31, -53}, {28, -57, 29, -56}, {26, -60, 27, -59}};
    private static final boolean[] HANGING = {false, false, true, false, true, false, true};

    private final Builder b;

    private GatewayChamber(ServerLevel level, BlockPos anchor) {
        this.b = new Builder(level, anchor, 1018L);
    }

    private static int hash(int x, int z) {
        int h = x * 73_856_093 ^ z * 19_349_663;
        return (h ^ (h >>> 13)) & 0x7fffffff;
    }

    public static void build(ServerLevel level, BlockPos anchor) {
        GatewayChamber g = new GatewayChamber(level, anchor);
        g.shell();
        g.descent();
        g.machineHall();
        g.chamber();
        g.gateway();
        g.camp();
    }

    private BlockState wall(int x, int y, int z) {
        int h = hash(x * 7 + y, z);
        return (h % 9 == 0 ? Blocks.CRACKED_DEEPSLATE_BRICKS : h % 5 == 0 ? Blocks.DEEPSLATE_TILES : Blocks.DEEPSLATE_BRICKS).defaultBlockState();
    }

    /** Solid deepslate round everything below, so no cave or lava can break in. */
    private void shell() {
        for (int k = 0; k < STEPS; k++) {
            int z = DIG_Z - k, floor = -1 - k;
            for (int x = 17; x <= 23; x++)
                for (int y = floor - 4; y <= floor + 5; y++)
                    if (y <= -2) b.set(x, y, z, wall(x, y, z));
        }
        for (int x = 2; x <= 38; x++)
            for (int z = -71; z <= -26; z++)
                for (int y = POOL_BOTTOM - 3; y <= -19; y++) b.set(x, y, z, wall(x, y, z));
    }

    /** The stair down, its supports, wall carvings and the ropes of the first dig. */
    private void descent() {
        for (int k = 0; k < STEPS; k++) {
            int z = DIG_Z - k, floor = -1 - k;
            for (int x = 19; x <= 21; x++) {
                for (int y = floor + 1; y <= floor + 4; y++) b.set(x, y, z, Blocks.AIR);
                b.set(x, floor, z, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.SOUTH, false));
            }
            // A timber support every eight steps: posts in the walls, a beam under the ceiling.
            if (k % 8 == 0 && k > 0) {
                for (int y = floor + 1; y <= floor + 4; y++) {
                    b.set(18, y, z, Blocks.SPRUCE_LOG);
                    b.set(22, y, z, Blocks.SPRUCE_LOG);
                }
                for (int x = 19; x <= 21; x++) b.set(x, floor + 4, z, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.X));
            }
            // Old carvings in the walls: these are evidence and must not be broken.
            if (k % 6 == 3) b.set(22, floor + 2, z, Blocks.CHISELED_DEEPSLATE);
        }
        for (int y = 0; y <= 3; y++) {
            b.set(18, y, DIG_Z + 1, Blocks.SPRUCE_LOG);
            b.set(22, y, DIG_Z + 1, Blocks.SPRUCE_LOG);
        }
        for (int x = 18; x <= 22; x++) b.set(x, 4, DIG_Z + 1, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.X));
        b.set(20, 3, DIG_Z + 1, Builder.hangingLantern());
        // The two supports the tremors broke: their beams have fallen onto the steps.
        for (int k : BROKEN) {
            int z = DIG_Z - k, floor = -1 - k;
            for (int x = 19; x <= 21; x++) b.set(x, floor + 4, z, Blocks.AIR);
            b.set(19, floor + 1, z + 1, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.Z));
        }
        // Loose rubble on the steps (Scene 018 clears it).
        b.set(20, -10, 2, Blocks.GRAVEL);
        b.set(21, -11, 1, Blocks.COBBLED_DEEPSLATE);
        b.set(19, -12, 0, Blocks.GRAVEL);
        // The collapsed steps, with a shallow drop beneath them (a fall there is climbable).
        for (int k = GAP_FROM; k <= GAP_TO; k++) {
            int z = DIG_Z - k, floor = -1 - k;
            for (int x = 19; x <= 21; x++) for (int y = floor - 1; y <= floor; y++) b.set(x, y, z, Blocks.AIR);
        }
    }

    /** The Builders' machine hall at the foot of the stair, before the great door. */
    private void machineHall() {
        for (int x = 7; x <= 33; x++)
            for (int z = -34; z <= -28; z++) {
                b.set(x, FLOOR, z, (x + z) % 3 == 0 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
                for (int y = FLOOR + 1; y <= FLOOR + 6; y++) b.set(x, y, z, Blocks.AIR);
            }
        for (int x = 19; x <= 21; x++) b.set(x, FLOOR, -27, Blocks.DEEPSLATE_TILES);
        // Copper channels in the floor: east-west through the hall, and north to the door.
        for (int x = 8; x <= 32; x++) b.set(x, FLOOR, -31, Blocks.WAXED_OXIDIZED_COPPER);
        for (int z = -34; z <= -28; z++) b.set(CX, FLOOR, z, Blocks.WAXED_OXIDIZED_COPPER);
        // Three conduit sockets, empty: the pedestals stand, the copper hearts are missing.
        for (int[] s : CONDUITS) {
            b.set(s[0], FLOOR + 1, s[1], Blocks.CHISELED_TUFF_BRICKS);
            b.set(s[0], FLOOR + 2, s[1], Blocks.AIR);
        }
        // The still clock: a dial of copper whose hand never moved.
        b.set(10, FLOOR + 1, -29, Blocks.CHISELED_COPPER);
        b.set(10, FLOOR + 2, -29, Blocks.LIGHTNING_ROD);
        // The two carvings in the end walls: twin circles (west) and the warning (east).
        b.set(6, FLOOR + 3, -31, Blocks.CHISELED_TUFF);
        b.set(6, FLOOR + 4, -31, Blocks.CHISELED_TUFF);
        b.set(34, FLOOR + 3, -31, Blocks.CHISELED_DEEPSLATE);
        // Six lock stones before the door, in a row the Builders never meant as their order.
        Block[] locks = {Blocks.CHISELED_SANDSTONE, Blocks.CHISELED_STONE_BRICKS, Blocks.DARK_PRISMARINE,
                Blocks.OXIDIZED_CHISELED_COPPER, Blocks.CHISELED_NETHER_BRICKS, Blocks.CHISELED_DEEPSLATE};
        for (int i = 0; i < locks.length; i++) {
            b.set(15 + i * 2, FLOOR, -32, Blocks.POLISHED_BLACKSTONE);
            b.set(15 + i * 2, FLOOR + 1, -32, locks[i]);
        }
        door(true);
        for (int x : new int[] {8, 15, 25, 32}) b.set(x, FLOOR + 5, -28, Builder.hangingLantern());
        for (int x : new int[] {8, 32}) b.set(x, FLOOR + 5, -34, Blocks.SOUL_LANTERN);
    }

    /** The three conduit sockets, {x, z}; their hearts sit at y FLOOR + 2. */
    public static final int[][] CONDUITS = {{8, -30}, {32, -30}, {20, -33}};

    /** The great round door in the plane z = -35: closed, or open (sunk into the floor). */
    private void door(boolean closed) {
        int cy = FLOOR + 6;
        for (int x = CX - 7; x <= CX + 7; x++)
            for (int y = FLOOR + 1; y <= FLOOR + 13; y++) {
                double d = Math.hypot(x - CX, y - cy);
                if (d > 6.6) continue;
                BlockState s;
                if (d > 5.6) s = Blocks.CHISELED_DEEPSLATE.defaultBlockState();
                else if (!closed) s = Blocks.AIR.defaultBlockState();
                else s = ((int) (d * 2) % 3 == 0 ? Blocks.POLISHED_BLACKSTONE_BRICKS : Blocks.POLISHED_DEEPSLATE).defaultBlockState();
                b.set(x, y, -35, s);
            }
        if (closed) b.set(CX, FLOOR + 3, -35, Blocks.CHISELED_POLISHED_BLACKSTONE); // the carving of the six emblems (a clue)
    }

    private static boolean inPlatform(int x, int z) {
        for (int[] p : PLATFORMS) if (x >= p[0] && x <= p[2] && z >= p[1] && z <= p[3]) return true;
        return false;
    }

    private static boolean inAlcove(int x, int z) {
        return x >= 18 && x <= 22 && z >= -67 && z <= -59;
    }

    private static boolean inBridge(int x, int z) {
        return x >= 19 && x <= 21 && z >= -44 && z <= -40;
    }

    /** True for the open chasm (not the landing, dais, bridge, stepping stones or alcove). */
    public static boolean isChasm(int x, int z) {
        return isBelowOpen(x, z) && !inBridge(x, z);
    }

    /** True where the chasm is open beneath the floor: the chasm itself and under the bridge. */
    private static boolean isBelowOpen(int x, int z) {
        double d = Math.hypot(x - CX, z - CZ);
        if (d > RADIUS + 0.5 || z >= -39 || d <= DAIS) return false;
        return !inPlatform(x, z) && !inAlcove(x, z);
    }

    /** The chamber: landing, chasm and pool, bridge, stepping stones, dais, alcove and dome. */
    private void chamber() {
        for (int x = CX - RADIUS - 1; x <= CX + RADIUS + 1; x++)
            for (int z = CZ - RADIUS - 1; z <= -36; z++) {
                double d = Math.hypot(x - CX, z - CZ);
                boolean landing = x >= 10 && x <= 30 && z >= -39;
                if (d > RADIUS + 0.5 && !landing) continue;
                int height = landing && d > RADIUS ? 7 : (int) Math.round(18 * Math.sqrt(Math.max(0, 1 - (d / (RADIUS + 1.5)) * (d / (RADIUS + 1.5)))));
                for (int y = FLOOR + 1; y <= FLOOR + Math.max(7, height); y++) b.set(x, y, z, Blocks.AIR);
                if (isBelowOpen(x, z)) {
                    for (int y = POOL_BOTTOM + 1; y <= FLOOR; y++) b.set(x, y, z, y <= POOL_TOP ? Blocks.WATER : Blocks.AIR);
                } else if (d <= DAIS) {
                    b.set(x, FLOOR, z, d < 3 ? Blocks.POLISHED_DEEPSLATE : Math.abs(x - CX) == 0 || z == RING_Z ? Blocks.POLISHED_BLACKSTONE : ((int) d) % 3 == 0 ? Blocks.CHISELED_TUFF_BRICKS : Blocks.TUFF_BRICKS);
                } else {
                    b.set(x, FLOOR, z, (x + z) % 4 == 0 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
                }
            }
        // The way in from the door.
        for (int x = CX - 5; x <= CX + 5; x++)
            for (int y = FLOOR + 1; y <= FLOOR + 11; y++)
                if (Math.hypot(x - CX, y - (FLOOR + 6)) <= 5.6) b.set(x, y, -36, Blocks.AIR);
        // Geometric carvings round the wall.
        for (int i = 0; i < 24; i++) {
            double a = i * Math.PI / 12;
            int x = CX + (int) Math.round(Math.cos(a) * (RADIUS + 1)), z = CZ + (int) Math.round(Math.sin(a) * (RADIUS + 1));
            if (z < -39) b.set(x, FLOOR + 4, z, i % 2 == 0 ? Blocks.CHISELED_DEEPSLATE : Blocks.CHISELED_TUFF);
        }
        bridge();
        // The stepping stones: some stand on pillars from the pool, some hang on chains from the dome.
        for (int i = 0; i < PLATFORMS.length; i++) {
            int[] p = PLATFORMS[i];
            for (int x = p[0]; x <= p[2]; x++)
                for (int z = p[1]; z <= p[3]; z++) {
                    b.set(x, FLOOR, z, Blocks.POLISHED_DEEPSLATE);
                    if (!HANGING[i]) for (int y = POOL_BOTTOM + 1; y < FLOOR; y++) b.set(x, y, z, Blocks.DEEPSLATE_BRICKS);
                    else b.set(x, FLOOR - 1, z, Builder.slab(Blocks.POLISHED_DEEPSLATE_SLAB, true));
                }
            if (HANGING[i]) for (int y = FLOOR + 5; y <= FLOOR + 12; y++) {
                b.set(p[0], y, p[1], Blocks.CHAIN);
                b.set(p[2], y, p[3], Blocks.CHAIN);
            }
        }
        // The alcove: a walled causeway north of the dais to the Builders' console.
        for (int z = -67; z <= -59; z++) {
            for (int x = 18; x <= 22; x++) {
                b.set(x, FLOOR, z, Blocks.POLISHED_DEEPSLATE);
                for (int y = POOL_BOTTOM + 1; y < FLOOR; y++) b.set(x, y, z, Blocks.DEEPSLATE_BRICKS);
            }
            if (z <= -60) for (int y = FLOOR + 1; y <= FLOOR + 3; y++) {
                b.set(17, y, z, Blocks.POLISHED_DEEPSLATE);
                b.set(23, y, z, Blocks.POLISHED_DEEPSLATE);
            }
        }
        b.set(20, FLOOR + 1, -66, Blocks.POLISHED_DEEPSLATE);
        b.set(20, FLOOR + 2, -66, Blocks.LODESTONE);
        b.set(18, FLOOR + 3, -66, Blocks.SOUL_LANTERN);
        b.set(22, FLOOR + 3, -66, Blocks.SOUL_LANTERN);
        // The ladder out of the pool, at the west end of the landing.
        b.ladder(11, POOL_TOP, FLOOR, -40, Direction.NORTH);
        // On the landing: the compass pedestal, a pylon with a copper heart, and a Builder console.
        b.set(17, FLOOR + 1, -37, Blocks.CHISELED_POLISHED_BLACKSTONE);
        b.set(13, FLOOR + 1, -37, Blocks.POLISHED_DEEPSLATE);
        b.set(13, FLOOR + 2, -37, Blocks.COPPER_BULB);
        b.set(13, FLOOR + 3, -37, Blocks.LIGHTNING_ROD);
        b.set(22, FLOOR + 1, -38, Blocks.LODESTONE);
        // Faint pale light: the chamber is not quite dark.
        for (int x : new int[] {10, 30}) b.set(x, FLOOR + 1, -36, Blocks.SOUL_LANTERN);
        b.set(CX, FLOOR, -46, Blocks.PEARLESCENT_FROGLIGHT);
    }

    /** The stone bridge over the chasm, from the landing to the dais. */
    private void bridge() {
        for (int x = 19; x <= 21; x++)
            for (int z = -44; z <= -40; z++) {
                b.set(x, FLOOR, z, x == 20 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
                b.set(x, FLOOR - 1, z, Builder.slab(Blocks.DEEPSLATE_BRICK_SLAB, true));
            }
    }

    /** The gateway: an upright double ring in the plane z = -52, its keystone at the top. */
    private void gateway() {
        for (int x = CX - RING_R - 1; x <= CX + RING_R + 1; x++)
            for (int y = RING_Y - RING_R - 1; y <= RING_Y + RING_R + 1; y++) {
                double d = Math.hypot(x - CX, y - RING_Y);
                if (y <= FLOOR) continue;
                if (d > RING_R - 0.5 && d <= RING_R + 0.5) b.set(x, y, RING_Z, ringBlock(x, y, 0));
                else if (d > RING_R - 1.5 && d <= RING_R - 0.5) b.set(x, y, RING_Z, ringBlock(x, y, 1));
            }
        b.set(CX, RING_Y + RING_R, RING_Z, Blocks.CHISELED_POLISHED_BLACKSTONE); // the keystone
        // Feet: the ring stands on two plinths.
        for (int x : new int[] {CX - 3, CX + 3}) b.fill(x, FLOOR + 1, RING_Z - 1, x, FLOOR + 1, RING_Z + 1, Blocks.POLISHED_DEEPSLATE);
    }

    private static BlockState ringBlock(int x, int y, int layer) {
        if (layer == 0) return (hash(x, y) % 4 == 0 ? Blocks.CHISELED_DEEPSLATE : Blocks.DEEPSLATE_TILES).defaultBlockState();
        return (Math.floorMod(x + y, 3) == 0 ? Blocks.CHISELED_TUFF : Blocks.POLISHED_TUFF).defaultBlockState();
    }

    /** The expedition's camp in the east of the courtyard: tents, supplies, ropes and lanterns. */
    private void camp() {
        for (int[] t : new int[][] {{12, 16}, {8, 20}}) {
            for (int dz = 0; dz <= 3; dz++) {
                b.set(t[0], 0, t[1] + dz, Blocks.WHITE_WOOL);
                b.set(t[0] + 4, 0, t[1] + dz, Blocks.WHITE_WOOL);
                b.set(t[0] + 1, 1, t[1] + dz, Blocks.WHITE_WOOL);
                b.set(t[0] + 3, 1, t[1] + dz, Blocks.WHITE_WOOL);
                b.set(t[0] + 2, 2, t[1] + dz, Blocks.WHITE_WOOL);
            }
        }
        for (int[] c : new int[][] {{16, 15}, {17, 15}, {16, 16}, {24, 14}, {24, 15}}) b.set(c[0], 0, c[1], Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(24, 0, 16, Blocks.CHEST);
        b.set(15, 0, 9, Blocks.CRAFTING_TABLE);
        b.set(25, 0, 10, Blocks.STONECUTTER);
        b.set(15, 0, 11, Blocks.CARTOGRAPHY_TABLE);
        for (int[] p : new int[][] {{17, 10}, {23, 10}, {14, 18}}) {
            b.fill(p[0], 0, p[1], p[0], 1, p[1], Blocks.SPRUCE_FENCE);
            b.set(p[0], 2, p[1], Blocks.LANTERN);
        }
        b.campfire(19, 0, 19, false);
        b.set(23, 0, 12, Blocks.COMPOSTER);
        b.set(17, 0, 13, Blocks.SCAFFOLDING);
        b.set(17, 1, 13, Blocks.SCAFFOLDING);
    }

    // --- story changes ------------------------------------------------------------------

    /** Applies one of the Scene 018/019 world changes; returns false if the id is not one of them. */
    public static boolean change(ServerLevel level, BlockPos anchor, String id) {
        GatewayChamber g = new GatewayChamber(level, anchor);
        Builder b = g.b;
        switch (id) {
            case "mc001_s018_conduits" -> {
                // The copper hearts are in: the channels wake, light running along them.
                for (int x = 8; x <= 32; x++) b.set(x, FLOOR, -31, x % 3 == 2 ? Blocks.PEARLESCENT_FROGLIGHT : Blocks.WAXED_COPPER_BLOCK);
                for (int z = -34; z <= -28; z++) b.set(CX, FLOOR, z, z % 3 == 0 ? Blocks.PEARLESCENT_FROGLIGHT : Blocks.WAXED_COPPER_BLOCK);
            }
            case "mc001_s018_door_open" -> g.door(false);
            case "mc001_s019_rings" -> {
                // The inner ring turns (its carved stones shift round) and the dais channels glow.
                for (int x = CX - RING_R; x <= CX + RING_R; x++)
                    for (int y = RING_Y - RING_R; y <= RING_Y + RING_R; y++) {
                        double d = Math.hypot(x - CX, y - RING_Y);
                        if (y <= FLOOR || d <= RING_R - 1.5 || d > RING_R - 0.5) continue;
                        b.set(x, y, RING_Z, (Math.floorMod(x + y, 3) == 1 ? Blocks.CHISELED_TUFF : Blocks.POLISHED_TUFF).defaultBlockState());
                    }
                for (int x = CX - RADIUS; x <= CX + RADIUS; x++)
                    for (int z = CZ - RADIUS; z <= CZ + RADIUS; z++)
                        if (b.get(x, FLOOR, z).is(Blocks.POLISHED_BLACKSTONE)) b.set(x, FLOOR, z, Blocks.PEARLESCENT_FROGLIGHT);
            }
            case "mc001_s019_portal_on" -> g.surface("on");
            case "mc001_s019_portal_stars" -> g.surface("stars");
            case "mc001_s019_portal_off" -> g.surface("off");
            case "mc001_s019_bridge_falls" -> {
                // The bridge's middle drops into the chasm, and the alcove's ceiling stones come down
                // across its mouth, shutting Elara in behind them.
                for (int x = 19; x <= 21; x++)
                    for (int z = -43; z <= -40; z++) {
                        b.set(x, FLOOR, z, Blocks.AIR);
                        b.set(x, FLOOR - 1, z, Blocks.AIR);
                    }
                g.resetPool();
                for (int x = 19; x <= 21; x++) b.set(x, FLOOR + 1, -61, Blocks.COBBLED_DEEPSLATE);
                for (int x = 19; x <= 21; x++) b.set(x, FLOOR + 2, -61, x == 20 ? Blocks.COBBLED_DEEPSLATE : Blocks.COBBLED_DEEPSLATE_WALL);
                fall(level, anchor, 20, FLOOR + 8, -42, Blocks.DEEPSLATE_TILES);
                fall(level, anchor, 19, FLOOR + 9, -41, Blocks.COBBLED_DEEPSLATE);
            }
            case "mc001_s019_debris_1" -> {
                fall(level, anchor, 27, FLOOR + 12, -45, Blocks.COBBLED_DEEPSLATE);
                fall(level, anchor, 26, FLOOR + 13, -48, Blocks.GRAVEL);
            }
            case "mc001_s019_debris_2" -> {
                fall(level, anchor, 34, FLOOR + 10, -49, Blocks.COBBLED_DEEPSLATE);
                fall(level, anchor, 28, FLOOR + 12, -52, Blocks.GRAVEL);
            }
            case "mc001_s019_debris_3" -> {
                fall(level, anchor, 32, FLOOR + 10, -56, Blocks.COBBLED_DEEPSLATE);
                fall(level, anchor, 25, FLOOR + 12, -61, Blocks.GRAVEL);
            }
            // The divergence: for a moment the bridge is whole again, then gone, then whole, then
            // as the player rebuilt it. Nothing explains it.
            case "mc001_s019_echo_1", "mc001_s019_echo_3" -> {
                for (int x = 19; x <= 21; x++) for (int z = -43; z <= -40; z++) b.set(x, FLOOR, z, x == 20 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
            }
            case "mc001_s019_echo_2" -> {
                for (int x = 19; x <= 21; x++) for (int z = -43; z <= -40; z++) b.set(x, FLOOR, z, Blocks.AIR);
            }
            case "mc001_s019_echo_4" -> {
                g.resetPool();
                for (int x = 19; x <= 21; x++) for (int z = -43; z <= -40; z++) b.set(x, FLOOR, z, x == 20 ? Blocks.SPRUCE_PLANKS : Blocks.AIR);
            }
            case "mc001_first_divergence" -> {
                // Recorded as story state only; the world itself shows nothing.
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    /** Clears anything that fell into the pool, so the chasm is the same on every visit. */
    private void resetPool() {
        for (int x = CX - RADIUS; x <= CX + RADIUS; x++)
            for (int z = CZ - RADIUS; z <= -40; z++)
                if (isBelowOpen(x, z)) for (int y = POOL_BOTTOM + 1; y <= FLOOR - 2; y++) b.set(x, y, z, y <= POOL_TOP ? Blocks.WATER : Blocks.AIR);
        b.ladder(11, POOL_TOP, FLOOR, -40, Direction.NORTH);
    }

    /** A falling block that hurts whoever it lands on, released from (x, y, z) if that is open air. */
    private static void fall(ServerLevel level, BlockPos anchor, int x, int y, int z, Block block) {
        BlockPos pos = anchor.offset(x, y, z);
        if (!level.getBlockState(pos).isAir()) return;
        FallingBlockEntity e = FallingBlockEntity.fall(level, pos, block.defaultBlockState());
        e.setHurtsEntities(1.0f, 4);
        e.disableDrop();
    }

    /** The luminous surface inside the ring, pale (on), filled with strange stars, or gone. */
    private void surface(String state) {
        for (int x = CX - RING_R; x <= CX + RING_R; x++)
            for (int y = RING_Y - RING_R; y <= RING_Y + RING_R; y++) {
                double d = Math.hypot(x - CX, y - RING_Y);
                if (d > RING_R - 1.5 || y <= FLOOR) continue;
                BlockState s = switch (state) {
                    case "on" -> Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
                    case "stars" -> (hash(x, y) % 9 == 0 ? Blocks.WHITE_STAINED_GLASS : Blocks.BLACK_STAINED_GLASS).defaultBlockState();
                    default -> Blocks.AIR.defaultBlockState();
                };
                b.set(x, y, RING_Z, s);
            }
        if (state.equals("off")) b.set(CX, RING_Y + RING_R, RING_Z, Blocks.SEA_LANTERN); // a faint blue glow stays in the keystone
    }
}
