package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Scenes 018 and 019: the excavation beneath the Ancient Northern Monument and the gateway chamber
 * it opens. Shares the monument's anchor, MC-001 block (40, 108, -200), with y = 0 the courtyard
 * floor; the monument itself is built first (NorthernMonument).
 *
 * The expedition camp fills the east of the courtyard. Its dig (x 19..21) starts at z 12 and the
 * stair goes down northward, one block per step: step k has its stair block at (x, -1 - k, 12 - k),
 * k = 0..39, inside a deepslate-lined tunnel with timber supports every eight steps. At the bottom
 * a corridor (x 14..26, z -34..-28, standing y -39) leads to the great round door (plane z -35) with
 * six lock stones in front of it. Behind the door lies the chamber (centre x 20, z -50, radius 14):
 * the upright gateway ring (plane z -52, centre y -31), its keystone at the top (20, -24, -52), the
 * compass pedestal south of it, an observation platform on the west side and three pylons.
 * Positions the scenes check are in their BLOCKS stages and clues, so they must match.
 */
public final class GatewayChamber {
    public static final int STEPS = 40, DIG_Z = 12;
    public static final int FLOOR = -40;
    public static final int CX = 20, CZ = -50, RADIUS = 14;
    public static final int RING_Z = -52, RING_Y = -31, RING_R = 7;
    /** The steps that collapsed and the supports that broke (Scene 018 rebuilds them). */
    public static final int GAP_FROM = 27, GAP_TO = 29;
    public static final int[] BROKEN = {16, 24};

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
        g.corridor();
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
        for (int x = 4; x <= 36; x++)
            for (int z = -67; z <= -26; z++)
                for (int y = FLOOR - 3; y <= -19; y++) b.set(x, y, z, wall(x, y, z));
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
        // The entrance frame at the top of the dig.
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
        // The collapsed steps, with a drop beneath them.
        for (int k = GAP_FROM; k <= GAP_TO; k++) {
            int z = DIG_Z - k, floor = -1 - k;
            for (int x = 19; x <= 21; x++) for (int y = floor - 3; y <= floor; y++) b.set(x, y, z, Blocks.AIR);
        }
    }

    private void corridor() {
        for (int x = 14; x <= 26; x++)
            for (int z = -34; z <= -28; z++) {
                b.set(x, FLOOR, z, (x + z) % 3 == 0 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
                for (int y = FLOOR + 1; y <= FLOOR + 6; y++) b.set(x, y, z, Blocks.AIR);
            }
        // The last step lands in the corridor.
        for (int x = 19; x <= 21; x++) b.set(x, FLOOR, -27, Blocks.DEEPSLATE_TILES);
        // The six lock stones before the door, out of order; their order is carved on the door.
        Block[] locks = {Blocks.CHISELED_SANDSTONE, Blocks.CHISELED_STONE_BRICKS, Blocks.DARK_PRISMARINE,
                Blocks.OXIDIZED_CHISELED_COPPER, Blocks.CHISELED_NETHER_BRICKS, Blocks.CHISELED_DEEPSLATE};
        for (int i = 0; i < locks.length; i++) {
            b.set(15 + i * 2, FLOOR, -32, Blocks.POLISHED_BLACKSTONE);
            b.set(15 + i * 2, FLOOR + 1, -32, locks[i]);
        }
        // The round door, sealed.
        door(true);
        for (int x : new int[] {15, 25}) b.set(x, FLOOR + 3, -29, Blocks.SOUL_LANTERN);
    }

    /** The great round door in the plane z = -35: closed, or open (gone into the floor). */
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
                if (y <= FLOOR) continue;
                b.set(x, y, -35, s);
            }
        if (closed) b.set(CX, FLOOR + 3, -35, Blocks.CHISELED_POLISHED_BLACKSTONE); // the carving of the six emblems (a clue)
    }

    /** The chamber: a round hall with carved channels, suspended rings, the platform and pylons. */
    private void chamber() {
        for (int x = CX - RADIUS; x <= CX + RADIUS; x++)
            for (int z = CZ - RADIUS; z <= CZ + RADIUS; z++) {
                double d = Math.hypot(x - CX, z - CZ);
                if (d > RADIUS + 0.5) continue;
                b.set(x, FLOOR, z, d < 3 ? Blocks.POLISHED_DEEPSLATE : ((int) d) % 4 == 0 ? Blocks.CHISELED_TUFF_BRICKS : Blocks.TUFF_BRICKS);
                int height = (int) Math.round(18 * Math.sqrt(Math.max(0, 1 - (d / (RADIUS + 1.5)) * (d / (RADIUS + 1.5)))));
                for (int y = FLOOR + 1; y <= FLOOR + Math.max(6, height); y++) b.set(x, y, z, Blocks.AIR);
                // Carved channels in the floor, radiating from the gateway (they light up in Scene 019).
                if (d > 3 && d < RADIUS - 1 && (Math.abs(x - CX) == 0 || Math.abs(z - RING_Z) == 0)) b.set(x, FLOOR, z, Blocks.POLISHED_BLACKSTONE);
            }
        // The way in from the door.
        for (int x = CX - 5; x <= CX + 5; x++)
            for (int z = -36; z <= -34; z++)
                for (int y = FLOOR + 1; y <= FLOOR + 10; y++) if (Math.hypot(x - CX, y - (FLOOR + 6)) <= 5.6 && z != -35) b.set(x, y, z, Blocks.AIR);
        // Geometric carvings round the wall.
        for (int i = 0; i < 24; i++) {
            double a = i * Math.PI / 12;
            int x = CX + (int) Math.round(Math.cos(a) * (RADIUS + 1)), z = CZ + (int) Math.round(Math.sin(a) * (RADIUS + 1));
            b.set(x, FLOOR + 3, z, i % 2 == 0 ? Blocks.CHISELED_DEEPSLATE : Blocks.CHISELED_TUFF);
        }
        // Suspended stone rings, hanging on chains from the dome.
        for (int[] c : new int[][] {{CX - 7, CZ + 5}, {CX + 7, CZ + 5}}) {
            for (int i = 0; i < 12; i++) {
                double a = i * Math.PI / 6;
                b.set(c[0] + (int) Math.round(Math.cos(a) * 2), FLOOR + 9, c[1] + (int) Math.round(Math.sin(a) * 2), Blocks.POLISHED_DEEPSLATE_SLAB);
            }
            for (int y = FLOOR + 10; y <= FLOOR + 14; y++) b.set(c[0], y, c[1], Blocks.CHAIN);
        }
        // The observation platform on the west side, with the control lectern and the stabiliser.
        for (int x = 8; x <= 12; x++)
            for (int z = -54; z <= -46; z++) {
                b.fill(x, FLOOR + 1, z, x, FLOOR + 3, z, Blocks.POLISHED_DEEPSLATE);
                if (x == 12 || z == -54 || z == -46) b.set(x, FLOOR + 4, z, Blocks.DEEPSLATE_TILE_WALL);
            }
        for (int i = 0; i < 3; i++) {
            b.set(13 + i, FLOOR + 3 - i, -48, Builder.stairs(Blocks.POLISHED_DEEPSLATE_STAIRS, Direction.WEST, false));
            b.set(13 + i, FLOOR + 3 - i, -47, Builder.stairs(Blocks.POLISHED_DEEPSLATE_STAIRS, Direction.WEST, false));
            for (int y = FLOOR + 1; y < FLOOR + 3 - i; y++) {
                b.set(13 + i, y, -48, Blocks.POLISHED_DEEPSLATE);
                b.set(13 + i, y, -47, Blocks.POLISHED_DEEPSLATE);
            }
        }
        b.set(12, FLOOR + 4, -48, Blocks.AIR);
        b.set(12, FLOOR + 4, -47, Blocks.AIR);
        // Three pylons round the gateway (their copper hearts are Scene 019's controls).
        for (int[] p : new int[][] {{12, -61}, {28, -61}, {30, -44}}) {
            b.fill(p[0], FLOOR + 1, p[1], p[0], FLOOR + 4, p[1], Blocks.POLISHED_DEEPSLATE);
            b.set(p[0], FLOOR + 5, p[1], Blocks.LIGHTNING_ROD);
        }
        // The compass pedestal south of the gateway, like the one in the courtyard above.
        b.set(CX, FLOOR + 1, -44, Blocks.POLISHED_DEEPSLATE);
        b.set(CX, FLOOR + 1, -43, Builder.stairs(Blocks.POLISHED_DEEPSLATE_STAIRS, Direction.NORTH, false));
        // Faint pale light in a few channels: the chamber is not quite dark.
        for (int[] l : new int[][] {{CX, -40}, {CX - 10, RING_Z}, {CX + 10, RING_Z}}) b.set(l[0], FLOOR, l[1], Blocks.PEARLESCENT_FROGLIGHT);
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
        // Rope coils and a winch frame by the dig.
        b.set(23, 0, 12, Blocks.COMPOSTER);
        b.set(17, 0, 13, Blocks.SCAFFOLDING);
        b.set(17, 1, 13, Blocks.SCAFFOLDING);
    }

    // --- story changes ------------------------------------------------------------------

    /** Scene 018: the six locks align and the door sinks away. */
    public static void openDoor(ServerLevel level, BlockPos anchor) {
        new GatewayChamber(level, anchor).door(false);
    }

    /** Scene 019: the outer ring turns (its carved stones shift round) and the channels glow. */
    public static void ringsTurn(ServerLevel level, BlockPos anchor) {
        GatewayChamber g = new GatewayChamber(level, anchor);
        for (int x = CX - RING_R; x <= CX + RING_R; x++)
            for (int y = RING_Y - RING_R; y <= RING_Y + RING_R; y++) {
                double d = Math.hypot(x - CX, y - RING_Y);
                if (y <= FLOOR || d <= RING_R - 1.5 || d > RING_R - 0.5) continue;
                g.b.set(x, y, RING_Z, (Math.floorMod(x + y, 3) == 1 ? Blocks.CHISELED_TUFF : Blocks.POLISHED_TUFF).defaultBlockState());
            }
        for (int x = CX - RADIUS; x <= CX + RADIUS; x++)
            for (int z = CZ - RADIUS; z <= CZ + RADIUS; z++)
                if (g.b.get(x, FLOOR, z).is(Blocks.POLISHED_BLACKSTONE)) g.b.set(x, FLOOR, z, Blocks.PEARLESCENT_FROGLIGHT);
    }

    /** Scene 019: a stone support falls across the far side of the chamber, trapping a researcher. */
    public static void collapse(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        for (int[] d : new int[][] {{29, -58}, {30, -58}, {31, -58}, {29, -57}, {32, -57}}) {
            b.set(d[0], FLOOR + 1, d[1], Blocks.COBBLED_DEEPSLATE);
            b.set(d[0], FLOOR + 2, d[1], (d[0] + d[1]) % 2 == 0 ? Blocks.COBBLED_DEEPSLATE : Blocks.AIR);
        }
        b.set(30, FLOOR + 3, -58, Builder.log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.X));
    }

    /** Scene 019: the fallen support is shifted and the trapped researcher is free. */
    public static void cleared(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 0);
        for (int x = 29; x <= 32; x++)
            for (int z = -58; z <= -57; z++)
                for (int y = FLOOR + 1; y <= FLOOR + 3; y++) b.set(x, y, z, Blocks.AIR);
        b.set(33, FLOOR + 1, -56, Blocks.COBBLED_DEEPSLATE);
        b.set(33, FLOOR + 1, -55, Blocks.COBBLED_DEEPSLATE_SLAB);
    }

    /** Scene 019: the luminous surface inside the ring, pale (on), filled with strange stars, or gone. */
    public static void surface(ServerLevel level, BlockPos anchor, String state) {
        Builder b = new Builder(level, anchor, 0);
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
