package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Scene 011, the Eldervale trade road, anchored at MC-001 block (100, 66, 135); everything here is
 * relative to it, with y = 0 the road.
 *
 * The road runs west to east along z = 0 (x -50..50) through open meadow, over a little stone
 * bridge across a stream at x -32..-28. A rocky ridge rises north of it (z -22..-9), where two of
 * the raiders come down; the merchants' wagon stands on the road at x -4..2 with a second cart
 * behind it. The sealed crate the scene examines is a barrel by the lead wagon's tail (-5, 0, 3).
 */
public final class EasternRoad {
    public static final int X0 = -50, X1 = 50, Z0 = -22, Z1 = 22;
    public static final int STREAM_W = -32, STREAM_E = -28;

    private final Builder b;

    private EasternRoad(ServerLevel level, BlockPos anchor) {
        this.b = new Builder(level, anchor, 1011L);
    }

    private static int hash(int x, int z) {
        int h = x * 73_856_093 ^ z * 19_349_663;
        return (h ^ (h >>> 13)) & 0x7fffffff;
    }

    /** Standing height of the ground: the ridge north of the road climbs to eight blocks. */
    public static int ground(int x, int z) {
        if (z >= -8) return 0;
        double rise = Math.min(8, (-8 - z) * 0.65 + 1.5 * Math.sin(x / 7.0));
        return (int) Math.max(0, Math.round(rise));
    }

    public static void build(ServerLevel level, BlockPos anchor) {
        EasternRoad r = new EasternRoad(level, anchor);
        r.land();
        r.bridge();
        r.wagons();
        r.roadside();
    }

    private boolean stream(int x) {
        return x >= STREAM_W && x <= STREAM_E;
    }

    private void land() {
        for (int x = X0; x <= X1; x++)
            for (int z = Z0; z <= Z1; z++) {
                int h = hash(x, z);
                if (stream(x)) {
                    boolean bank = x == STREAM_W || x == STREAM_E;
                    b.fill(x, -16, z, x, -5, z, Blocks.STONE);
                    b.set(x, -4, z, h % 3 == 0 ? Blocks.GRAVEL : Blocks.SAND);
                    for (int y = -3; y <= 24; y++) b.set(x, y, z, y <= -2 && !bank ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState());
                    if (bank) {
                        b.fill(x, -3, z, x, -2, z, Blocks.DIRT);
                        b.set(x, -2, z, Blocks.GRASS_BLOCK);
                    }
                    continue;
                }
                int g = ground(x, z);
                boolean rock = g >= 3 && h % 3 != 0;
                b.fill(x, -16, z, x, g - 4, z, Blocks.STONE);
                b.fill(x, g - 3, z, x, g - 2, z, rock ? Blocks.STONE : Blocks.DIRT);
                Block top = rock ? (h % 4 == 0 ? Blocks.ANDESITE : h % 5 == 0 ? Blocks.COBBLESTONE : Blocks.STONE)
                        : h % 15 == 0 ? Blocks.COARSE_DIRT : Blocks.GRASS_BLOCK;
                b.set(x, g - 1, z, top);
                for (int y = g; y <= 24; y++) b.set(x, y, z, Blocks.AIR);
                boolean road = Math.abs(z) <= 1;
                if (road) {
                    b.set(x, -1, z, h % 7 == 0 ? Blocks.COARSE_DIRT : h % 5 == 0 ? Blocks.GRAVEL : Blocks.DIRT_PATH);
                } else if (!rock && h % 9 == 0) {
                    b.set(x, g, z, h % 4 == 0 ? Blocks.POPPY : h % 3 == 0 ? Blocks.CORNFLOWER : Blocks.SHORT_GRASS);
                }
            }
        for (int x = STREAM_W; x <= STREAM_E; x++)
            for (int y = -16; y <= -2; y++) {
                b.set(x, y, Z0 - 1, Blocks.STONE);
                b.set(x, y, Z1 + 1, Blocks.STONE);
            }
        // Boulders on the ridge for the raiders to hide behind.
        for (int[] r : new int[][] {{-14, -15}, {4, -13}, {18, -16}, {30, -12}, {-30, -18}}) {
            int g = ground(r[0], r[1]);
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++)
                    for (int dy = 0; dy <= 2; dy++)
                        if (Math.abs(dx) + Math.abs(dz) + dy <= 2) b.set(r[0] + dx, g + dy, r[1] + dz, hash(dx + r[0], dz + dy) % 3 == 0 ? Blocks.MOSSY_COBBLESTONE : Blocks.ANDESITE);
        }
    }

    /** The little stone bridge carrying the road over the stream. */
    private void bridge() {
        for (int x = STREAM_W - 1; x <= STREAM_E + 1; x++)
            for (int z = -2; z <= 2; z++) {
                b.set(x, -1, z, Math.abs(z) == 2 ? Blocks.STONE_BRICKS : Blocks.COBBLESTONE);
                if (Math.abs(z) == 2) b.set(x, 0, z, Blocks.COBBLESTONE_WALL);
            }
        for (int z = -1; z <= 1; z++) b.fill(-30, -4, z, -30, -2, z, Blocks.STONE_BRICKS);
    }

    /** The merchants' covered wagon and a second cart, loaded with goods. */
    private void wagons() {
        wagon(-4, 2, 2);
        wagon(-14, -12, 2);
        // Cargo on and around the wagon (the sealed crate by its tail is the scene's clue block).
        b.set(-3, 1, 3, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-1, 1, 3, Blocks.CHEST);
        b.set(0, 1, 2, Blocks.HAY_BLOCK);
        b.set(-13, 1, 3, Blocks.HAY_BLOCK);
        b.set(-11, 1, 2, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-11, 1, 4, Blocks.COMPOSTER);
        // Hitch posts in front of the lead wagon.
        b.set(3, 0, 2, Blocks.DARK_OAK_FENCE);
        b.set(3, 0, 4, Blocks.DARK_OAK_FENCE);
    }

    /** A wagon bed on four wheels with a cloth cover, x0..x1 along the road, z 2..4. */
    private void wagon(int x0, int x1, int z0) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z0 + 2; z++) b.set(x, 0, z, Builder.slab(Blocks.SPRUCE_SLAB, true));
        for (int x : new int[] {x0, x1})
            for (int z : new int[] {z0 - 1, z0 + 3}) b.set(x, 0, z, Builder.facing(Blocks.DARK_OAK_TRAPDOOR, z < z0 ? Direction.NORTH : Direction.SOUTH)
                    .setValue(net.minecraft.world.level.block.TrapDoorBlock.OPEN, true));
        for (int x = x0 + 1; x <= x1 - 1; x += 2)
            for (int z = z0; z <= z0 + 2; z++) {
                boolean side = z == z0 || z == z0 + 2;
                b.set(x, side ? 2 : 3, z, Blocks.WHITE_WOOL);
                if (side) b.set(x, 1, z, Blocks.SPRUCE_FENCE);
            }
    }

    /** Fence lines, a milestone, a few trees and a farm track off the road. */
    private void roadside() {
        for (int x = -46; x <= 46; x++) {
            if (stream(x) || stream(x - 1) || stream(x + 1)) continue;
            if (hash(x, 9) % 4 != 0) b.set(x, 0, 6, Blocks.OAK_FENCE);
        }
        b.set(20, 0, -3, Blocks.STONE_BRICK_WALL);
        b.set(20, 1, -3, Blocks.CHISELED_STONE_BRICKS);
        b.standingSign(21, 0, -3, Blocks.OAK_SIGN, 0, "Valebridge", "East", "<- Sylvaris");
        for (int[] t : new int[][] {{-40, 12}, {-20, 16}, {14, 14}, {36, 11}, {44, 18}, {-6, 18}})
            b.broadleaf(t[0], 0, t[1], 5 + hash(t[0], t[1]) % 2, Blocks.OAK_LOG, Blocks.OAK_LEAVES);
        for (int[] t : new int[][] {{-24, -20}, {26, -21}}) b.spruce(t[0], ground(t[0], t[1]), t[1], 8);
        for (int x = 24; x <= 30; x++) for (int z = 8; z <= 14; z++) b.set(x, -1, z, x % 2 == 0 ? Blocks.FARMLAND : Blocks.COARSE_DIRT);
        for (int x = 24; x <= 30; x += 2) for (int z = 8; z <= 14; z++) b.set(x, 0, z, Blocks.WHEAT);
    }
}
