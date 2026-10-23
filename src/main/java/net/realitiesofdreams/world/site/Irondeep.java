package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.WallSide;

import java.util.Set;

/**
 * Irondeep, the underground hold of Stonecrown (Scene 013), anchored at MC-001 block (-160, 24, -170);
 * everything here is relative to it, with y = 0 the standing level of the main cavern floor (the
 * floor blocks are at y = -1). North is -z.
 *
 * The whole box x -72..72, z -72..72, y -30..38 is first made solid (every air, fluid, falling or
 * replaceable block of the natural terrain becomes rock), so no cave, water or lava can break in;
 * then the hold is carved out of it.
 *
 * Main cavern: a rounded square (|x|/62)^4 + (|z|/62)^4 <= 1, ceiling from about y 14 at the walls
 * to y 32 in the middle. The great lift (car x -2..2, z 55..59, arrival (0, 0, 58)) sets visitors
 * down at the south end; its gate (towers x +-5..9, z 43..45) opens on the main avenue (x -4..4),
 * which runs north past the plaza (monument at (0, 0, 26)) and the market terraces (east: ground
 * x 8..19, terrace B x 20..29 at standing y 2, terrace C x 30..39 at y 4, z 12..40, stairs at
 * z 25..27) to the Great Bridge (x -3..3) over the chasm (z about -14..-4, x -6..62, water at its
 * bottom, y -20). The rail line runs along x 46 from the depot (x 42..54, z 22..44) over the rail
 * bridge to the mine in the north-east. The Forge Hall stands at the north end (walls x -15..15,
 * z -58..-30, door at z -30): Garrick's workbench with the fragment on (-6, 1, -48), the
 * reconstruction table at z -48 with its sockets at (4, 1, -48) (Stonecrown inlay, chiseled
 * deepslate), (6, 1, -48) (Eldervale, chiseled stone bricks) and (8, 1, -48) (Sylvaris, oxidized
 * chiseled copper). Workshops (smithy, drafting house, copperworks) stand west of it, the foundry
 * south-west of the bridge, and the residences are carved into the west wall (rock x <= -47,
 * z 8..46; rooms on two tiers, the upper one reached along the ledge at y 6).
 *
 * The Old Deeps: an incline (x -54..-51, rail on x -51) goes down southward from the arch at
 * (-52, 0, -41) to the landing (x -57..-47, z -21..-14, standing y -19). East of it is the flooded
 * works hall (x -45..-23, z -16..5, floor at standing y -22, flooded to y -20) with the ledge
 * (z -20..-19, y -19) and the drainage channel (z -18) along its north side; the top of the
 * channel's wall is broken at (-36..-34, -20, -17). The three Builder counterweights stand along
 * the south wall (cores at (-43, -21, 4) middle, (-38, -21, 4) smallest, (-32, -21, 4) largest)
 * beside the carving that shows their order. The sealed works door is in the east wall
 * (x -22, z -5..-1, y -22..-17); behind it, the Builder works (x -21..-11, z -10..4) with the
 * machine plate at (-16, -21, -3) and the gear housing at (-19, -21, 2).
 * Positions the scene checks are in its BLOCKS stages and clues, so they must match.
 */
public final class Irondeep {
    public static final int X0 = -72, X1 = 72, Z0 = -72, Z1 = 72, Y0 = -30, Y1 = 38;
    public static final int R = 62;
    /** Old Deeps levels: the landing and ledge, and the works hall floor (standing). */
    public static final int LAND = -19, DEEP = -22;
    public static final int HALL_X0 = -45, HALL_X1 = -23, HALL_Z0 = -16, HALL_Z1 = 5;
    public static final int DOOR_X = -22, DOOR_Z0 = -5, DOOR_Z1 = -1, DOOR_Y0 = -22, DOOR_Y1 = -17;

    private static final Set<Block> FLOOR = Set.of(Blocks.STONE, Blocks.ANDESITE, Blocks.TUFF, Blocks.COBBLED_DEEPSLATE,
            Blocks.POLISHED_ANDESITE, Blocks.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE, Blocks.STONE_BRICKS,
            Blocks.CRACKED_STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.DEEPSLATE_BRICKS, Blocks.SPRUCE_PLANKS);

    private final Builder b;
    private final ServerLevel level;

    private Irondeep(ServerLevel level, BlockPos anchor) {
        this.b = new Builder(level, anchor, 1013L);
        this.level = level;
    }

    private static int hash(int x, int z) {
        int h = x * 73_856_093 ^ z * 19_349_663;
        return (h ^ (h >>> 13)) & 0x7fffffff;
    }

    private static int hash3(int x, int y, int z) {
        return hash(x * 31 + y * 7, z * 17 - y * 3);
    }

    public static void build(ServerLevel level, BlockPos anchor) {
        Irondeep d = new Irondeep(level, anchor);
        d.rock();
        d.floor();
        d.streets();
        d.chasm();
        d.lift();
        d.gate();
        d.plaza();
        d.pillars();
        d.market();
        d.depot();
        d.bridges();
        d.forgeHall();
        d.workshops();
        d.foundry();
        d.residences();
        d.mine();
        d.rails();
        d.deepsEntrance();
        d.deeps();
        d.lamps();
        d.carts();
    }

    // ------------------------------------------------------------------ shape

    private static double s(int x, int z) {
        double a = Math.abs(x) / (double) R, c = Math.abs(z) / (double) R;
        return a * a * a * a + c * c * c * c;
    }

    /** The rock the residences are carved into. */
    private static boolean cliff(int x, int z) {
        return x <= -47 && z >= 8 && z <= 46;
    }

    private static boolean inMain(int x, int z) {
        return s(x, z) <= 1 && !cliff(x, z);
    }

    /** First rock block above the main floor. */
    private static int ceil(int x, int z) {
        double sv = s(x, z);
        return 12 + (int) Math.round(20 * Math.sqrt(Math.max(0, 1 - sv)) + 1.5 * Math.sin(x / 7.0) * Math.cos(z / 9.0));
    }

    private static boolean liftShaft(int x, int z) {
        return Math.abs(x) <= 5 && z >= 54 && z <= 61;
    }

    private static double chasmZ(int x) {
        return -9 + Math.sin(x / 11.0);
    }

    private static boolean chasm(int x, int z) {
        if (x < -6 || !inMain(x, z)) return false;
        double hw = Math.min(4 + 0.8 * Math.sin(x / 5.0), (x + 6) / 2.0 + 1);
        return Math.abs(z - chasmZ(x)) <= hw;
    }

    private BlockState rockAt(int x, int y, int z) {
        int ay = b.at(0, y, 0).getY();
        int h = hash3(x, y, z);
        if (ay < 2) return (h % 11 == 0 ? Blocks.TUFF : Blocks.DEEPSLATE).defaultBlockState();
        if (ay < 8) return (h % 2 == 0 ? Blocks.DEEPSLATE : Blocks.STONE).defaultBlockState();
        return (h % 9 == 0 ? Blocks.ANDESITE : h % 13 == 0 ? Blocks.TUFF : Blocks.STONE).defaultBlockState();
    }

    /** Solid rock through the whole box (natural stone is kept), then the cavern and chasm carved out. */
    private void rock() {
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = X0; x <= X1; x++)
            for (int z = Z0; z <= Z1; z++) {
                boolean main = inMain(x, z), gap = chasm(x, z), shaft = liftShaft(x, z);
                int top = main ? ceil(x, z) : 0;
                for (int y = Y0; y <= Y1; y++) {
                    boolean open = (shaft && y >= 0 && y <= 36) || (main && y >= 0 && y < top) || (gap && y >= -20 && y <= -1);
                    BlockState cur = b.get(x, y, z);
                    if (open) {
                        if (!cur.isAir()) b.set(x, y, z, air);
                    } else if (cur.isAir() || !cur.getFluidState().isEmpty() || cur.canBeReplaced() || cur.getBlock() instanceof FallingBlock) {
                        b.set(x, y, z, rockAt(x, y, z));
                    }
                }
            }
    }

    /** The cavern floor: worn natural stone, paved where the streets go. */
    private void floor() {
        for (int x = -R; x <= R; x++)
            for (int z = -R; z <= R; z++) {
                if (!inMain(x, z) || chasm(x, z)) continue;
                int h = hash(x, z) % 10;
                Block f = h < 4 ? Blocks.STONE : h < 6 ? Blocks.ANDESITE : h == 6 ? Blocks.TUFF : h < 9 ? Blocks.COBBLED_DEEPSLATE : Blocks.POLISHED_ANDESITE;
                b.set(x, -1, z, f);
            }
    }

    private void pave(int x0, int z0, int x1, int z1, Block main, Block alt, int altEvery) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                if (!inMain(x, z) || chasm(x, z)) continue;
                b.set(x, -1, z, (altEvery > 0 && Math.floorMod(x + z * 3, altEvery) == 0) ? alt : main);
            }
    }

    private void streets() {
        // The main avenue, lift to Forge Hall, with blackstone kerbs.
        for (int z = -30; z <= 53; z++)
            for (int x = -4; x <= 4; x++) {
                if (chasm(x, z)) continue;
                b.set(x, -1, z, Math.abs(x) == 4 ? Blocks.POLISHED_BLACKSTONE_BRICKS : Math.floorMod(z, 5) == 0 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
            }
        pave(-46, 24, 19, 28, Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, 7);          // cross street
        pave(-50, -26, 44, -22, Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, 9);        // north street
        pave(-45, -21, -41, 46, Blocks.STONE_BRICKS, Blocks.POLISHED_ANDESITE, 5);           // west lane
        pave(-54, -47, -47, -42, Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, 4);        // to the Old Deeps arch
        pave(-49, -46, -47, -22, Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, 8);
        pave(44, -50, 48, 44, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.DEEPSLATE_TILES, 6); // the rail line
        pave(4, 41, 44, 44, Blocks.STONE_BRICKS, Blocks.POLISHED_ANDESITE, 6);               // to the depot
    }

    // ------------------------------------------------------------------ helpers

    private static BlockState wallLine(Block w, boolean alongX) {
        BlockState s = w.defaultBlockState().setValue(BlockStateProperties.UP, false);
        return alongX ? s.setValue(BlockStateProperties.EAST_WALL, WallSide.LOW).setValue(BlockStateProperties.WEST_WALL, WallSide.LOW)
                : s.setValue(BlockStateProperties.NORTH_WALL, WallSide.LOW).setValue(BlockStateProperties.SOUTH_WALL, WallSide.LOW);
    }

    /** Iron bars, fences or panes joined along a line. */
    private static BlockState cross(Block bl, boolean alongX) {
        BlockState s = bl.defaultBlockState();
        return alongX ? s.setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true)
                : s.setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true);
    }

    private static BlockState grindstone() {
        return Blocks.GRINDSTONE.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR);
    }

    private static BlockState waterCauldron() {
        return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
    }

    private static BlockState rail(RailShape shape) {
        return Blocks.RAIL.defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE, shape);
    }

    /** Stonecrown's colours: iron, charcoal and copper. */
    private static Block banner(int i) {
        return switch (Math.floorMod(i, 3)) {
            case 0 -> Blocks.ORANGE_WALL_BANNER;
            case 1 -> Blocks.GRAY_WALL_BANNER;
            default -> Blocks.LIGHT_GRAY_WALL_BANNER;
        };
    }

    /** A blackstone lamp post with a lantern on top. */
    private void post(int x, int y, int z) {
        b.set(x, y, z, Blocks.POLISHED_BLACKSTONE_WALL);
        b.set(x, y + 1, z, Blocks.POLISHED_BLACKSTONE_WALL);
        b.set(x, y + 2, z, Blocks.LANTERN);
    }

    // ------------------------------------------------------------------ the chasm and its bridges

    private void chasm() {
        for (int x = -6; x <= R; x++)
            for (int z = -16; z <= -1; z++)
                if (chasm(x, z)) b.set(x, -20, z, Blocks.WATER);
        // A few glowing veins on the chasm floor, and a ladder up its south wall.
        for (int x = 0; x <= 56; x += 8) {
            int z = (int) Math.round(chasmZ(x));
            if (chasm(x, z)) b.set(x, -21, z, Blocks.OCHRE_FROGLIGHT);
        }
        b.ladder(40, -20, -1, -5, Direction.NORTH);
    }

    private void bridges() {
        // The Great Bridge on the avenue: tiled deck, parapets with lamps, and a central pier.
        for (int z = -16; z <= -2; z++) {
            for (int x = -3; x <= 3; x++) b.set(x, -1, z, x == 0 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
            for (int x : new int[] {-4, 4}) {
                b.set(x, -1, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.set(x, 0, z, wallLine(Blocks.DEEPSLATE_BRICK_WALL, false));
            }
        }
        for (int z : new int[] {-15, -9, -3})
            for (int x : new int[] {-4, 4}) {
                b.set(x, 0, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.set(x, 1, z, Blocks.LANTERN);
            }
        for (int x = -3; x <= 3; x++) {
            for (int y = -20; y <= -2; y++)
                for (int z = -10; z <= -8; z++) b.set(x, y, z, y % 5 == 0 ? Blocks.POLISHED_BLACKSTONE_BRICKS : Blocks.DEEPSLATE_BRICKS);
            b.set(x, -2, -11, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.NORTH, true));
            b.set(x, -2, -7, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.SOUTH, true));
        }
        // The rail bridge (x 45..47), iron-railed, on a blackstone pier.
        for (int z = -16; z <= -2; z++) {
            for (int x = 44; x <= 48; x++) b.set(x, -1, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
            b.set(44, 0, z, cross(Blocks.IRON_BARS, false));
            b.set(48, 0, z, cross(Blocks.IRON_BARS, false));
        }
        for (int x = 45; x <= 47; x++)
            for (int y = -20; y <= -2; y++) for (int z = -10; z <= -8; z++) b.set(x, y, z, Blocks.POLISHED_BLACKSTONE);
        for (int z : new int[] {-16, -2}) {
            b.set(44, 0, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
            b.set(44, 1, z, Blocks.LANTERN);
            b.set(48, 0, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
            b.set(48, 1, z, Blocks.LANTERN);
        }
        // The miners' footbridge (x 22..24), planked and fenced.
        for (int z = -16; z <= -2; z++) {
            for (int x = 22; x <= 24; x++) b.set(x, -1, z, Blocks.SPRUCE_PLANKS);
            b.set(21, -1, z, Blocks.STRIPPED_SPRUCE_LOG);
            b.set(25, -1, z, Blocks.STRIPPED_SPRUCE_LOG);
            b.set(21, 0, z, cross(Blocks.SPRUCE_FENCE, false));
            b.set(25, 0, z, cross(Blocks.SPRUCE_FENCE, false));
        }
        for (int z : new int[] {-16, -9, -2}) {
            b.set(21, 1, z, Blocks.LANTERN);
            b.set(25, 1, z, Blocks.LANTERN);
        }
        for (int y = -19; y <= -2; y++) b.set(23, y, -9, Blocks.CHAIN);
    }

    // ------------------------------------------------------------------ the lift and the gate

    private void lift() {
        // Car floor and the landing round it.
        for (int x = -5; x <= 5; x++)
            for (int z = 54; z <= 61; z++) b.set(x, -1, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int x = -2; x <= 2; x++)
            for (int z = 55; z <= 59; z++) b.set(x, -1, z, (x + z) % 2 == 0 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
        b.set(0, -1, 57, Blocks.OCHRE_FROGLIGHT);
        // Back wall with copper bands, up the whole shaft.
        for (int x = -5; x <= 5; x++)
            for (int z = 60; z <= 61; z++)
                for (int y = 0; y <= 36; y++) b.set(x, y, z, y % 6 == 5 ? Blocks.WAXED_CUT_COPPER : Blocks.POLISHED_BLACKSTONE_BRICKS);
        // Frame posts, barred sides and the barred front above the doors.
        for (int y = 0; y <= 36; y++) {
            for (int x : new int[] {-3, 3}) {
                b.set(x, y, 54, Blocks.POLISHED_BLACKSTONE);
                for (int z = 55; z <= 59; z++) b.set(x, y, z, cross(Blocks.IRON_BARS, false));
            }
            if (y >= 6) for (int x = -2; x <= 2; x++) b.set(x, y, 54, cross(Blocks.IRON_BARS, true));
        }
        for (int x = -3; x <= 3; x++) b.set(x, 5, 54, Math.abs(x) == 3 ? Blocks.WAXED_CUT_COPPER : Blocks.CHISELED_POLISHED_BLACKSTONE);
        b.set(-2, 4, 54, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.EAST, true));
        b.set(2, 4, 54, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.WEST, true));
        // The car's canopy and the chains it hangs from.
        for (int x = -2; x <= 2; x++)
            for (int z = 55; z <= 59; z++)
                if (Math.abs(x) == 2 || z == 55 || z == 59) b.set(x, 5, z, Builder.slab(Blocks.POLISHED_BLACKSTONE_SLAB, true));
        for (int y = 6; y <= 36; y++) {
            b.set(-2, y, 57, Blocks.CHAIN);
            b.set(2, y, 57, Blocks.CHAIN);
        }
        b.set(-2, 0, 59, Blocks.LANTERN);
        b.set(2, 0, 59, Blocks.LANTERN);
        // Counterweights in their bays either side of the car.
        for (int x : new int[] {-5, -4, 4, 5})
            for (int z = 56; z <= 58; z++) {
                for (int y = 9; y <= 11; y++) b.set(x, y, z, y == 10 ? Blocks.WAXED_CUT_COPPER : Blocks.IRON_BLOCK);
                if (z == 57) for (int y = 12; y <= 36; y++) b.set(x, y, z, Blocks.CHAIN);
            }
        // The winding gear east of the lift, where the engineer works.
        b.set(8, 0, 51, Blocks.POLISHED_BLACKSTONE);
        b.set(8, 0, 54, Blocks.POLISHED_BLACKSTONE);
        for (int z = 51; z <= 54; z++) b.set(8, 1, z, Builder.log(Blocks.STRIPPED_SPRUCE_LOG, Direction.Axis.Z));
        b.set(8, 0, 52, grindstone());
        b.set(8, 0, 53, grindstone());
        for (int y = 2; y <= 14; y++) b.set(8, y, 52, Blocks.CHAIN);
        b.set(7, 0, 50, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(7, 1, 50, Blocks.LANTERN);
        b.set(9, 0, 50, Blocks.IRON_BLOCK);
        b.sign(-3, 2, 53, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "THE GREAT LIFT", "of Irondeep", "", "Keep off the rail");
    }

    private void gate() {
        for (int side : new int[] {-1, 1}) {
            for (int ax = 5; ax <= 9; ax++)
                for (int z = 43; z <= 45; z++) {
                    int x = side * ax;
                    for (int y = 0; y <= 9; y++) {
                        boolean corner = (ax == 5 || ax == 9) && (z == 43 || z == 45);
                        b.set(x, y, z, corner ? Blocks.POLISHED_BLACKSTONE : y == 4 ? Blocks.POLISHED_BLACKSTONE_BRICKS : Blocks.DEEPSLATE_BRICKS);
                    }
                    b.set(x, 10, z, (ax + z) % 2 == 0 ? Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState() : Builder.slab(Blocks.DEEPSLATE_TILE_SLAB, false));
                }
            b.set(side * 7, 11, 44, Blocks.LANTERN);
            b.set(side * 7, 10, 44, Blocks.POLISHED_BLACKSTONE);
            for (int ax : new int[] {6, 8}) {
                b.set(side * ax, 7, 46, Builder.facing(banner(ax + side), Direction.SOUTH));
                b.set(side * ax, 7, 42, Builder.facing(banner(ax + side + 1), Direction.NORTH));
            }
            post(side * 6, 0, 47);
            post(side * 6, 0, 41);
        }
        // The arch, with the portcullis drawn up into it.
        for (int x = -4; x <= 4; x++)
            for (int z = 43; z <= 45; z++)
                for (int y = 6; y <= 9; y++) b.set(x, y, z, y == 9 ? Blocks.POLISHED_BLACKSTONE_BRICKS : Blocks.DEEPSLATE_BRICKS);
        for (int z = 43; z <= 45; z++) {
            b.set(-4, 5, z, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.WEST, true));
            b.set(4, 5, z, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.EAST, true));
        }
        for (int x = -3; x <= 3; x++) b.set(x, 5, 44, cross(Blocks.IRON_BARS, true));
        b.set(0, 8, 46, Blocks.CHISELED_POLISHED_BLACKSTONE);
        b.set(0, 8, 42, Blocks.CHISELED_POLISHED_BLACKSTONE);
        for (int x = -4; x <= 4; x += 4) b.set(x, 10, 44, Blocks.LANTERN);
    }

    // ------------------------------------------------------------------ the city

    private void plaza() {
        for (int x = -9; x <= 9; x++)
            for (int z = 17; z <= 35; z++) {
                double d = Math.hypot(x, z - 26);
                if (d > 9.5) continue;
                b.set(x, -1, z, ((int) d) % 3 == 0 ? Blocks.POLISHED_ANDESITE : Blocks.DEEPSLATE_TILES);
            }
        // The Founders' Anvil on its pedestal.
        for (int x = -1; x <= 1; x++)
            for (int z = 25; z <= 27; z++) {
                b.set(x, 0, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.set(x, 1, z, x == 0 && z == 26 ? Blocks.CHISELED_POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS);
            }
        for (int x = -2; x <= 2; x++)
            for (int z = 24; z <= 28; z++)
                if (Math.abs(x) == 2 || Math.abs(z - 26) == 2) b.set(x, 0, z, Builder.slab(Blocks.POLISHED_BLACKSTONE_SLAB, false));
        b.set(0, 2, 26, Builder.facing(Blocks.ANVIL, Direction.EAST));
        for (int x : new int[] {-1, 1}) for (int z : new int[] {25, 27}) b.set(x, 2, z, Blocks.LANTERN);
        b.sign(0, 1, 28, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "THE FOUNDERS'", "ANVIL", "Irondeep was", "struck here");
        for (int x : new int[] {-7, 7}) for (int z : new int[] {20, 32}) post(x, 0, z);
    }

    /** A great pillar from floor to ceiling, with lamps on brackets and Stonecrown's banners. */
    private void pillar(int cx, int cz, int i) {
        int top = ceil(cx, cz) + 2;
        for (int y = -1; y <= top; y++)
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++) {
                    boolean corner = Math.abs(dx) == 1 && Math.abs(dz) == 1;
                    b.set(cx + dx, y, cz + dz, y % 6 == 5 ? Blocks.POLISHED_BLACKSTONE_BRICKS : corner ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_BRICKS);
                }
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                    b.set(cx + dx, 0, cz + dz, Builder.slab(Blocks.POLISHED_BLACKSTONE_SLAB, false));
                    int cap = ceil(cx + dx, cz + dz) - 1;
                    if (cap > 6) b.set(cx + dx, cap, cz + dz, Blocks.POLISHED_BLACKSTONE_BRICKS);
                }
        int k = 0;
        for (Direction d : new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}) {
            int x = cx + 2 * d.getStepX(), z = cz + 2 * d.getStepZ();
            b.set(x, 3, z, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, d.getOpposite(), true));
            b.set(x, 4, z, Blocks.LANTERN);
            b.set(x, 9, z, Builder.facing(banner(i + k++), d));
        }
    }

    private void pillars() {
        int i = 0;
        for (int[] p : new int[][] {{-9, 38}, {9, 38}, {-9, 14}, {9, 14}, {-20, -18}, {20, -18}, {-30, 2}, {30, 2}}) pillar(p[0], p[1], i++);
    }

    /** A market stall facing west: counter, goods, posts and a striped awning. */
    private void stall(int x0, int y, int zc, int kind) {
        for (int z = zc - 1; z <= zc + 1; z++)
            for (int x = x0; x <= x0 + 2; x++) b.set(x, y - 1, z, Blocks.SPRUCE_PLANKS);
        for (int x : new int[] {x0, x0 + 2})
            for (int z : new int[] {zc - 1, zc + 1})
                for (int yy = y; yy <= y + 2; yy++) b.set(x, yy, z, Blocks.SPRUCE_FENCE);
        for (int x = x0 - 1; x <= x0 + 3; x++)
            for (int z = zc - 2; z <= zc + 2; z++) b.set(x, y + 3, z, (z + kind) % 2 == 0 ? Blocks.ORANGE_WOOL : Blocks.GRAY_WOOL);
        b.set(x0 + 1, y + 4, zc, Blocks.LANTERN);
        b.set(x0 + 2, y + 2, zc, Builder.hangingLantern());
        // Counter at the front, goods at the back.
        b.set(x0, y, zc, Builder.slab(Blocks.SPRUCE_SLAB, true));
        switch (kind % 4) {
            case 0 -> {
                b.set(x0 + 2, y, zc, Builder.facing(Blocks.ANVIL, Direction.NORTH));
                b.set(x0, y + 1, zc, Blocks.IRON_BLOCK);
            }
            case 1 -> {
                b.set(x0 + 2, y, zc, Builder.facing(Blocks.BARREL, Direction.UP));
                b.set(x0, y + 1, zc, Blocks.RAW_COPPER_BLOCK);
            }
            case 2 -> {
                b.set(x0 + 2, y, zc, Builder.facing(Blocks.SMOKER, Direction.WEST));
                b.set(x0, y + 1, zc, Blocks.HAY_BLOCK);
            }
            default -> {
                b.set(x0 + 2, y, zc, Builder.facing(Blocks.CHEST, Direction.WEST));
                b.set(x0, y + 1, zc, Blocks.LANTERN);
            }
        }
    }

    private void market() {
        // Terrace B (standing y 2) and terrace C (standing y 4), with paving on top.
        for (int z = 12; z <= 40; z++) {
            for (int x = 20; x <= 29; x++) {
                b.fill(x, 0, z, x, 0, z, Blocks.DEEPSLATE_BRICKS);
                b.set(x, 1, z, x == 20 ? Blocks.POLISHED_BLACKSTONE_BRICKS : (x + z) % 4 == 0 ? Blocks.POLISHED_ANDESITE : Blocks.STONE_BRICKS);
            }
            for (int x = 30; x <= 39; x++) {
                b.fill(x, 0, z, x, 2, z, Blocks.DEEPSLATE_BRICKS);
                b.set(x, 3, z, x == 30 ? Blocks.POLISHED_BLACKSTONE_BRICKS : (x + z) % 3 == 0 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
            }
            boolean steps = z >= 25 && z <= 27;
            if (!steps) {
                b.set(20, 2, z, wallLine(Blocks.DEEPSLATE_BRICK_WALL, false));
                b.set(30, 4, z, wallLine(Blocks.DEEPSLATE_BRICK_WALL, false));
            }
            if (z % 6 == 0 && !steps) {
                b.set(20, 2, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.set(20, 3, z, Blocks.LANTERN);
                b.set(30, 4, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                b.set(30, 5, z, Blocks.LANTERN);
            }
            if (steps) {
                b.set(20, 0, z, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.EAST, false));
                b.set(20, 1, z, Blocks.AIR);
                b.set(21, 1, z, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.EAST, false));
                b.set(30, 2, z, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.EAST, false));
                b.set(30, 3, z, Blocks.AIR);
                b.set(31, 3, z, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.EAST, false));
            }
        }
        for (int x = 21; x <= 29; x++) {
            b.set(x, 2, 12, wallLine(Blocks.DEEPSLATE_BRICK_WALL, true));
            b.set(x, 2, 40, wallLine(Blocks.DEEPSLATE_BRICK_WALL, true));
        }
        for (int x = 31; x <= 39; x++) {
            b.set(x, 4, 12, wallLine(Blocks.DEEPSLATE_BRICK_WALL, true));
            b.set(x, 4, 40, wallLine(Blocks.DEEPSLATE_BRICK_WALL, true));
        }
        // Ground-level stalls, the terrace stalls and the tavern on the top terrace.
        int k = 0;
        for (int zc : new int[] {14, 19, 33, 38}) stall(11, 0, zc, k++);
        stall(23, 2, 16, 2);
        stall(23, 2, 34, 3);
        stall(33, 4, 35, 0);
        for (int x = 32; x <= 38; x++)
            for (int z = 14; z <= 22; z++) {
                b.set(x, 3, z, Blocks.SPRUCE_PLANKS);
                b.set(x, 8, z, Builder.slab(Blocks.SPRUCE_SLAB, false));
            }
        for (int x : new int[] {32, 38})
            for (int z : new int[] {14, 18, 22}) b.fill(x, 4, z, x, 7, z, Blocks.STRIPPED_SPRUCE_LOG);
        for (int z = 15; z <= 21; z++) b.set(38, 4, z, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(38, 5, 15, Builder.facing(Blocks.BARREL, Direction.WEST));
        b.set(38, 5, 21, Builder.facing(Blocks.BARREL, Direction.WEST));
        for (int[] t : new int[][] {{34, 16}, {34, 20}, {36, 18}}) {
            b.set(t[0], 4, t[1], Blocks.DARK_OAK_FENCE);
            b.set(t[0], 5, t[1], Blocks.DARK_OAK_PRESSURE_PLATE);
            b.set(t[0], 4, t[1] - 1, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
            b.set(t[0], 4, t[1] + 1, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH, false));
        }
        for (int[] l : new int[][] {{34, 16}, {34, 20}, {36, 18}}) b.set(l[0], 7, l[1], Builder.hangingLantern());
        b.set(37, 4, 14, Builder.facing(Blocks.SMOKER, Direction.SOUTH));
        b.set(36, 4, 14, Blocks.CRAFTING_TABLE);
        b.sign(32, 6, 23, Blocks.SPRUCE_WALL_SIGN, Direction.SOUTH, "THE ANVIL", "& TANKARD", "", "Miners welcome");
    }

    /** The rail depot by the market: sidings, ore bins and cargo. */
    private void depot() {
        pave(42, 22, 54, 40, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.DEEPSLATE_TILES, 3);
        for (int z = 26; z <= 38; z++) b.set(50, 0, z, rail(RailShape.NORTH_SOUTH));
        b.set(50, 0, 25, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(50, 0, 39, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int[] p : new int[][] {{52, 27}, {53, 27}, {52, 28}, {53, 28}}) b.set(p[0], 0, p[1], Blocks.RAW_IRON_BLOCK);
        b.set(52, 1, 27, Blocks.RAW_IRON_BLOCK);
        for (int[] p : new int[][] {{52, 31}, {53, 31}, {53, 32}}) b.set(p[0], 0, p[1], Blocks.RAW_COPPER_BLOCK);
        for (int[] p : new int[][] {{52, 35}, {53, 35}, {52, 36}}) b.set(p[0], 0, p[1], Blocks.COAL_BLOCK);
        for (int z = 22; z <= 24; z++) {
            b.set(43, 0, z, Builder.facing(Blocks.BARREL, Direction.UP));
            b.set(43, 1, z, Builder.facing(Blocks.BARREL, Direction.UP));
        }
        post(44, 0, 26);
        post(44, 0, 36);
        post(54, 0, 30);
        b.set(49, 0, 40, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(49, 1, 40, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.sign(49, 1, 41, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "ORE DEPOT", "Carts run north", "to the workings", "MIND THE RAILS");
    }

    /** The main rail line along x 46, from the depot over the rail bridge to the mine. */
    private void rails() {
        for (int z = -45; z <= 44; z++) b.set(46, 0, z, rail(RailShape.NORTH_SOUTH));
        b.set(46, 0, 45, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(46, 0, -46, rail(RailShape.SOUTH_WEST));
        for (int x = 30; x <= 45; x++) b.set(x, 0, -46, rail(RailShape.EAST_WEST));
        b.set(29, 0, -46, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(29, 1, -46, Blocks.LANTERN);
    }

    // ------------------------------------------------------------------ the Forge Hall

    private void forgeHall() {
        int x0 = -15, x1 = 15, z0 = -58, z1 = -30;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                b.set(x, -1, z, edge ? Blocks.DEEPSLATE_TILES : Math.abs(x) <= 1 ? Blocks.POLISHED_BLACKSTONE_BRICKS : Blocks.POLISHED_DEEPSLATE);
                for (int y = 0; y <= 11; y++) {
                    if (!edge) {
                        b.set(x, y, z, Blocks.AIR);
                        continue;
                    }
                    Block w = y == 0 ? Blocks.DEEPSLATE_TILES : y == 5 ? Blocks.POLISHED_DEEPSLATE
                            : y == 11 ? (Math.floorMod(x + z, 3) == 0 ? Blocks.CHISELED_POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE)
                            : Blocks.POLISHED_BLACKSTONE_BRICKS;
                    b.set(x, y, z, w);
                }
                b.set(x, 12, z, Builder.slab(Blocks.DEEPSLATE_TILE_SLAB, false));
                if (edge) b.set(x, 12, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                if (edge && Math.floorMod(x + z, 2) == 0) b.set(x, 13, z, Builder.slab(Blocks.POLISHED_BLACKSTONE_SLAB, false));
            }
        // Lamps on the roof so nothing lurks up there.
        for (int x = -12; x <= 12; x += 6)
            for (int z = -54; z <= -34; z += 6) b.set(x, 12, z, Blocks.OCHRE_FROGLIGHT);
        // Buttresses and barred windows.
        for (int z = -54; z <= -36; z += 6) {
            for (int x : new int[] {-16, 16}) b.fill(x, 0, z, x, 12, z, Blocks.DEEPSLATE_BRICKS);
            for (int x : new int[] {-15, 15}) for (int y = 3; y <= 4; y++) b.set(x, y, z + 3, cross(Blocks.IRON_BARS, false));
        }
        // The great door and its arch.
        for (int x = -3; x <= 3; x++) for (int y = 0; y <= 6; y++) b.set(x, y, z1, Blocks.AIR);
        b.set(-3, 6, z1, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.WEST, true));
        b.set(3, 6, z1, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.EAST, true));
        for (int x = -3; x <= 3; x++) b.set(x, 7, z1, x == 0 ? Blocks.CHISELED_POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE);
        for (int x : new int[] {-9, -6, 6, 9}) b.set(x, 8, z1 + 1, Builder.facing(banner(x / 3), Direction.SOUTH));
        post(-5, 0, -28);
        post(-8, 0, -28);
        post(8, 0, -28);
        b.sign(0, 9, z1 + 1, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "THE FORGE HALL", "of the Thanes", "of Stonecrown");
        // Interior columns.
        for (int x : new int[] {-10, 10})
            for (int z : new int[] {-52, -46, -40, -34}) {
                b.fill(x, 0, z, x, 11, z, Blocks.POLISHED_BASALT);
                b.set(x, 0, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
            }
        // The great hearth: a lava bed behind a grille under a stepped hood and chimney.
        for (int x = -5; x <= 5; x++) for (int z = -57; z <= -54; z++) b.set(x, 0, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int x = -2; x <= 2; x++) {
            b.set(x, 0, -56, Blocks.LAVA);
            b.set(x, 0, -55, Blocks.LAVA);
            for (int y = 1; y <= 2; y++) b.set(x, y, -54, cross(Blocks.IRON_BARS, true));
        }
        for (int y = 3; y <= 11; y++) {
            int half = Math.max(2, 5 - (y - 3) / 2);
            int front = y <= 4 ? -53 : -54;
            for (int x = -half; x <= half; x++)
                for (int z = -57; z <= front; z++) b.set(x, y, z, y == 3 ? Blocks.CHISELED_POLISHED_BLACKSTONE : Blocks.POLISHED_BLACKSTONE_BRICKS);
        }
        for (int x = -1; x <= 1; x++) for (int z = -57; z <= -55; z++) b.fill(x, 12, z, x, 18, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.campfire(0, 19, -56, true);
        b.set(-6, 0, -55, Blocks.LAVA_CAULDRON);
        b.set(6, 0, -55, Blocks.LAVA_CAULDRON);
        b.set(-8, 0, -56, Builder.facing(Blocks.BLAST_FURNACE, Direction.SOUTH));
        b.set(8, 0, -56, Builder.facing(Blocks.BLAST_FURNACE, Direction.SOUTH));
        b.set(-3, 0, -51, Builder.facing(Blocks.ANVIL, Direction.EAST));
        b.set(3, 0, -51, Builder.facing(Blocks.CHIPPED_ANVIL, Direction.EAST));
        b.set(-5, 0, -51, grindstone());
        b.set(5, 0, -51, waterCauldron());
        for (int x : new int[] {-13, -12, 12, 13}) {
            b.set(x, 0, -57, Builder.facing(Blocks.CHEST, Direction.SOUTH));
            b.set(x, 1, -57, Builder.facing(Blocks.BARREL, Direction.SOUTH));
        }
        // Storage along the side walls, banners above.
        for (int z = -55; z <= -32; z += 2) {
            b.set(-14, 0, z, Builder.facing(Blocks.BARREL, Direction.EAST));
            b.set(14, 0, z, Builder.facing(Blocks.BARREL, Direction.WEST));
        }
        int i = 0;
        for (int z = -54; z <= -36; z += 6) {
            b.set(-14, 9, z, Builder.facing(banner(i), Direction.EAST));
            b.set(14, 9, z, Builder.facing(banner(i + 1), Direction.WEST));
            i++;
        }
        // Hanging lamps.
        for (int x : new int[] {-6, 6})
            for (int z : new int[] {-50, -42, -34}) {
                b.set(x, 11, z, Blocks.CHAIN);
                b.set(x, 10, z, Builder.hangingLantern());
            }
        // Garrick's workbench (the fragment lies on (-6, 1, -48)).
        b.set(-8, 0, -48, Blocks.POLISHED_BLACKSTONE);
        b.set(-7, 0, -48, Blocks.SMITHING_TABLE);
        b.set(-6, 0, -48, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(-6, 1, -48, Blocks.CHISELED_DEEPSLATE);
        b.set(-5, 0, -48, Blocks.POLISHED_BLACKSTONE);
        b.set(-8, 1, -48, Blocks.LANTERN);
        b.set(-8, 0, -49, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-7, 0, -49, Builder.facing(Blocks.CHEST, Direction.SOUTH));
        b.set(-6, 0, -49, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-5, 0, -49, Blocks.BOOKSHELF);
        b.set(-5, 1, -49, Blocks.LANTERN);
        // The reconstruction table: three sockets over their emblem inlays.
        b.set(3, 0, -48, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.EAST, false));
        b.set(4, 0, -48, Blocks.CHISELED_DEEPSLATE);
        b.set(5, 0, -48, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(6, 0, -48, Blocks.CHISELED_STONE_BRICKS);
        b.set(7, 0, -48, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(8, 0, -48, Blocks.OXIDIZED_CHISELED_COPPER);
        b.set(9, 0, -48, Builder.stairs(Blocks.POLISHED_BLACKSTONE_STAIRS, Direction.WEST, false));
        for (int x = 4; x <= 8; x += 2) b.set(x, 1, -48, Blocks.AIR);
        for (int x = 3; x <= 9; x++) for (int y = 0; y <= 2; y++) b.set(x, y, -49, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(3, 3, -49, Blocks.LANTERN);
        b.set(9, 3, -49, Blocks.LANTERN);
        b.sign(6, 2, -48, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "Match each piece", "to the carving", "beneath it");
        // The engineers' desks.
        b.set(-12, 0, -43, Blocks.CARTOGRAPHY_TABLE);
        b.set(-12, 0, -44, Builder.facing(Blocks.LECTERN, Direction.EAST));
        b.set(-12, 0, -42, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-12, 1, -42, Blocks.LANTERN);
        b.set(12, 0, -38, Blocks.CRAFTING_TABLE);
        b.set(12, 0, -37, Builder.facing(Blocks.STONECUTTER, Direction.WEST));
        b.set(12, 0, -39, Blocks.WAXED_COPPER_BLOCK);
        b.set(12, 1, -39, Blocks.LANTERN);
    }

    // ------------------------------------------------------------------ workshops and foundry

    /** A stone workshop: walls, banded trim, windows, a slab roof and a door. Returns nothing; furnished by the caller. */
    private void workshop(int x0, int z0, int x1, int z1, int h, int doorX, int doorZ, Direction doorFacing) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                b.set(x, -1, z, edge ? Blocks.DEEPSLATE_BRICKS : Blocks.SPRUCE_PLANKS);
                for (int y = 0; y < h; y++) {
                    if (!edge) {
                        b.set(x, y, z, Blocks.AIR);
                        continue;
                    }
                    boolean window = !corner && (y == 1 || y == 2) && Math.floorMod(x + z, 3) == 0;
                    b.set(x, y, z, corner ? Blocks.POLISHED_BLACKSTONE.defaultBlockState()
                            : window ? cross(Blocks.IRON_BARS, z == z0 || z == z1)
                            : y == h - 1 ? Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState() : Blocks.DEEPSLATE_BRICKS.defaultBlockState());
                }
                b.set(x, h, z, edge ? Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState() : Builder.slab(Blocks.DEEPSLATE_TILE_SLAB, false));
            }
        b.set((x0 + x1) / 2, h, (z0 + z1) / 2, Blocks.OCHRE_FROGLIGHT);
        b.door(doorX, 0, doorZ, Blocks.SPRUCE_DOOR, doorFacing);
        b.set((x0 + x1) / 2, h - 1, (z0 + z1) / 2, Builder.hangingLantern());
        b.set((x0 + x1) / 2, -1, (z0 + z1) / 2, Blocks.OCHRE_FROGLIGHT);
    }

    private void workshops() {
        // The smithy.
        workshop(-46, -34, -38, -26, 5, -38, -30, Direction.EAST);
        b.set(-44, 0, -32, Builder.facing(Blocks.ANVIL, Direction.NORTH));
        b.set(-45, 0, -30, Builder.facing(Blocks.BLAST_FURNACE, Direction.EAST));
        b.set(-45, 0, -28, Blocks.LAVA_CAULDRON);
        b.set(-42, 0, -33, grindstone());
        b.set(-40, 0, -33, Blocks.SMITHING_TABLE);
        b.set(-45, 0, -33, Builder.facing(Blocks.CHEST, Direction.EAST));
        b.set(-39, 0, -27, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-39, 1, -27, Blocks.LANTERN);
        b.set(-45, 0, -27, Blocks.IRON_BLOCK);
        b.set(-39, 0, -33, waterCauldron());
        b.chimney(-46, -34, 7, Blocks.DEEPSLATE_BRICKS);
        b.sign(-37, 2, -31, Blocks.DARK_OAK_WALL_SIGN, Direction.EAST, "SMITHY", "Picks mended", "while you wait");
        // The engineers' drafting house.
        workshop(-34, -34, -26, -26, 5, -30, -26, Direction.SOUTH);
        b.set(-33, 0, -33, Blocks.CARTOGRAPHY_TABLE);
        b.set(-32, 0, -33, Blocks.CARTOGRAPHY_TABLE);
        b.set(-28, 0, -33, Builder.facing(Blocks.LECTERN, Direction.SOUTH));
        b.set(-27, 0, -33, Builder.facing(Blocks.LECTERN, Direction.SOUTH));
        for (int z = -31; z <= -28; z++) b.fill(-33, 0, z, -33, 2, z, Blocks.BOOKSHELF);
        b.set(-30, 0, -33, Blocks.POLISHED_BLACKSTONE);
        b.set(-30, 1, -33, grindstone());
        b.set(-27, 0, -29, Blocks.CRAFTING_TABLE);
        b.set(-27, 0, -28, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-27, 1, -28, Blocks.LANTERN);
        b.set(-33, 0, -27, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-33, 1, -27, Blocks.LANTERN);
        b.sign(-31, 2, -25, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "ENGINEERS'", "DRAFTING HOUSE");
        // The copperworks.
        workshop(-46, -50, -38, -42, 5, -42, -42, Direction.SOUTH);
        for (int[] p : new int[][] {{-45, -49}, {-44, -49}, {-45, -48}}) b.set(p[0], 0, p[1], Blocks.WAXED_COPPER_BLOCK);
        b.set(-45, 1, -49, Blocks.WAXED_CUT_COPPER);
        b.set(-39, 0, -49, Builder.facing(Blocks.BLAST_FURNACE, Direction.WEST));
        b.set(-39, 0, -47, waterCauldron());
        b.set(-39, 0, -45, Builder.facing(Blocks.STONECUTTER, Direction.WEST));
        b.set(-45, 0, -44, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-45, 1, -44, Blocks.LANTERN);
        b.set(-39, 0, -43, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-39, 1, -43, Blocks.LANTERN);
        for (int x : new int[] {-45, -42, -39}) b.set(x, 6, -46, Blocks.LIGHTNING_ROD);
        b.sign(-41, 2, -41, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "COPPERWORKS");
    }

    /** The open foundry west of the avenue: blast furnaces round a crucible of lava, under a stone roof. */
    private void foundry() {
        pave(-33, 9, -13, 23, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, 4);
        for (int x : new int[] {-32, -26, -20, -14})
            for (int z : new int[] {10, 16, 22}) b.fill(x, 0, z, x, 6, z, Blocks.POLISHED_BLACKSTONE);
        for (int x = -32; x <= -14; x++)
            for (int z = 10; z <= 22; z++)
                b.set(x, 7, z, (x == -32 || x == -14 || z == 10 || z == 22) ? Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState()
                        : Builder.slab(Blocks.DEEPSLATE_TILE_SLAB, false));
        for (int x = -29; x <= -17; x += 6) b.set(x, 7, 16, Blocks.OCHRE_FROGLIGHT);
        // The crucible: a lava bed sunk into the floor behind a low wall.
        for (int x = -26; x <= -22; x++)
            for (int z = 12; z <= 16; z++) {
                boolean rim = x == -26 || x == -22 || z == 12 || z == 16;
                if (rim) {
                    b.set(x, -1, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                    if (!(x == -26 && z == 16)) b.set(x, 0, z, Blocks.POLISHED_BLACKSTONE_WALL);
                } else {
                    b.set(x, -2, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
                    b.set(x, -1, z, Blocks.LAVA);
                }
            }
        b.set(-31, 0, 11, Builder.facing(Blocks.BLAST_FURNACE, Direction.SOUTH));
        b.set(-29, 0, 11, Builder.facing(Blocks.BLAST_FURNACE, Direction.SOUTH));
        b.set(-31, 0, 13, Builder.facing(Blocks.FURNACE, Direction.EAST));
        b.set(-18, 0, 20, Builder.facing(Blocks.ANVIL, Direction.NORTH));
        b.set(-16, 0, 12, Builder.facing(Blocks.ANVIL, Direction.EAST));
        b.set(-15, 0, 20, Blocks.LAVA_CAULDRON);
        b.set(-31, 0, 21, Blocks.COAL_BLOCK);
        b.set(-30, 0, 21, Blocks.COAL_BLOCK);
        b.set(-31, 1, 21, Blocks.COAL_BLOCK);
        b.set(-17, 0, 11, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-16, 0, 21, waterCauldron());
        for (int x : new int[] {-31, -29}) b.campfire(x, 8, 11, false);
        b.set(-23, 6, 22, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.sign(-23, 6, 23, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "FOUNDRY");
    }

    // ------------------------------------------------------------------ the residences

    private void residences() {
        int[] bands = {9, 18, 27, 36};
        Block[] beds = {Blocks.GRAY_BED, Blocks.ORANGE_BED, Blocks.LIGHT_GRAY_BED, Blocks.BROWN_BED};
        Block[] carpets = {Blocks.ORANGE_CARPET, Blocks.GRAY_CARPET, Blocks.BROWN_CARPET, Blocks.LIGHT_GRAY_CARPET};
        // The ledge (upper walkway) on its columns, with lamps on the railing.
        for (int z = 7; z <= 44; z++) {
            for (int x = -46; x <= -44; x++) b.set(x, 5, z, Blocks.POLISHED_DEEPSLATE);
            b.set(-44, 6, z, wallLine(Blocks.DEEPSLATE_BRICK_WALL, false));
        }
        for (int z = 10; z <= 40; z += 6) {
            b.fill(-44, 0, z, -44, 4, z, Blocks.DEEPSLATE_BRICKS);
            b.set(-44, 6, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
            b.set(-44, 7, z, Blocks.LANTERN);
        }
        b.set(-44, 6, 44, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(-44, 7, 44, Blocks.LANTERN);
        for (int z : new int[] {13, 25, 37}) b.set(-45, 4, z, Builder.hangingLantern());
        // The stair up to the ledge.
        for (int k = 0; k <= 5; k++)
            for (int x = -46; x <= -45; x++) {
                if (k > 0) b.fill(x, 0, 1 + k, x, k - 1, 1 + k, Blocks.DEEPSLATE_BRICKS);
                b.set(x, k, 1 + k, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.SOUTH, false));
            }
        int idx = 0;
        for (int y0 : new int[] {0, 6})
            for (int z0 : bands) room(y0, z0, idx++, beds, carpets);
    }

    /** One home carved into the rock: interior x -55..-48, z z0..z0+5, door at (-47, y0, z0 + 2). */
    private void room(int y0, int z0, int idx, Block[] beds, Block[] carpets) {
        // The dressed front.
        for (int z = z0 - 1; z <= z0 + 6; z++)
            for (int y = y0 - 1; y <= y0 + 4; y++)
                b.set(-47, y, z, (z == z0 - 1 || z == z0 + 6) ? Blocks.POLISHED_DEEPSLATE : y == y0 + 4 ? Blocks.POLISHED_BLACKSTONE_BRICKS : Blocks.DEEPSLATE_BRICKS);
        // Hollow it out.
        for (int x = -55; x <= -48; x++)
            for (int z = z0; z <= z0 + 5; z++) {
                b.set(x, y0 - 1, z, Blocks.SPRUCE_PLANKS);
                for (int y = y0; y <= y0 + 3; y++) b.set(x, y, z, Blocks.AIR);
                b.set(x, y0 + 4, z, (x + z) % 3 == 0 ? Blocks.STRIPPED_SPRUCE_LOG : Blocks.DEEPSLATE_TILES);
            }
        b.door(-47, y0, z0 + 2, Blocks.SPRUCE_DOOR, Direction.EAST);
        b.set(-47, y0 + 2, z0 + 2, Blocks.CHISELED_STONE_BRICKS);
        b.set(-47, y0 + 1, z0 + 4, cross(Blocks.GLASS_PANE, false));
        b.set(-47, y0 + 1, z0, cross(Blocks.GLASS_PANE, false));
        b.set(-46, y0 + 3, z0 + 4, Builder.facing(banner(idx), Direction.EAST));
        // Beds against the back wall.
        Block bed = beds[idx % beds.length];
        b.bed(-55, y0, z0, bed, Direction.WEST);
        if (idx % 2 == 0) b.bed(-55, y0, z0 + 1, bed, Direction.WEST);
        // Lamps on barrels, a hanging lamp, kitchen and storage along the north side.
        b.set(-53, y0, z0, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-53, y0 + 1, z0, Blocks.LANTERN);
        b.set(-55, y0, z0 + 5, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-55, y0 + 1, z0 + 5, Blocks.LANTERN);
        b.set(-48, y0, z0 + 5, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(-48, y0 + 1, z0 + 5, Blocks.LANTERN);
        b.set(-50, y0 + 3, z0 + 2, Builder.hangingLantern());
        b.set(-54, y0, z0 + 5, Builder.facing(idx % 2 == 0 ? Blocks.SMOKER : Blocks.FURNACE, Direction.NORTH));
        b.set(-53, y0, z0 + 5, idx % 3 == 0 ? Blocks.CRAFTING_TABLE : Blocks.STONECUTTER);
        b.set(-52, y0, z0 + 5, Builder.facing(Blocks.CHEST, Direction.NORTH));
        b.set(-51, y0, z0 + 5, Blocks.BOOKSHELF);
        b.set(-51, y0 + 1, z0 + 5, idx % 2 == 0 ? Blocks.BOOKSHELF : Blocks.POTTED_FERN);
        if (idx % 4 == 1) b.set(-50, y0, z0 + 5, Builder.facing(Blocks.ANVIL, Direction.EAST));
        // Table and chairs, carpet, a fern by the door.
        b.set(-51, y0, z0 + 2, Blocks.DARK_OAK_FENCE);
        b.set(-51, y0 + 1, z0 + 2, Blocks.DARK_OAK_PRESSURE_PLATE);
        b.set(-51, y0, z0 + 1, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
        b.set(-51, y0, z0 + 3, Builder.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH, false));
        Block carpet = carpets[idx % carpets.length];
        for (int x = -53; x <= -52; x++) for (int z = z0 + 2; z <= z0 + 3; z++) b.set(x, y0, z, carpet);
        b.set(-48, y0, z0, Blocks.POTTED_FERN);
    }

    // ------------------------------------------------------------------ the mine

    private void mine() {
        // Ore showing in the north and east faces of the workings.
        for (int x = 26; x <= 58; x++) {
            int zw = -18;
            while (zw > -70 && inMain(x, zw)) zw--;
            oreFace(x, zw, true);
            if (x % 6 == 0 && x <= 54) frame(x, zw);
        }
        for (int z = -50; z <= -20; z++) {
            int xw = 40;
            while (xw < 70 && inMain(xw, z)) xw++;
            oreFace(xw, z, false);
        }
        // Ore bins and tools by the line.
        for (int[] p : new int[][] {{51, -34}, {52, -34}, {51, -33}}) b.set(p[0], 0, p[1], Blocks.RAW_IRON_BLOCK);
        b.set(51, 1, -34, Blocks.RAW_IRON_BLOCK);
        b.set(53, 0, -33, Blocks.RAW_COPPER_BLOCK);
        b.set(54, 0, -35, Blocks.COAL_BLOCK);
        b.set(53, 0, -36, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(53, 1, -36, Blocks.LANTERN);
        b.set(40, 0, -40, Builder.facing(Blocks.CHEST, Direction.SOUTH));
        b.set(41, 0, -40, Builder.facing(Blocks.BARREL, Direction.UP));
        b.set(41, 1, -40, Blocks.LANTERN);
        // A deep shaft with its windlass, fenced off.
        for (int x = 34; x <= 36; x++)
            for (int z = -38; z <= -36; z++)
                for (int y = -10; y <= -1; y++) b.set(x, y, z, Blocks.AIR);
        b.set(35, -10, -36, Blocks.LANTERN);
        b.ladder(35, -10, -1, -38, Direction.SOUTH);
        for (int x = 33; x <= 37; x++)
            for (int z = -39; z <= -35; z++)
                if ((x == 33 || x == 37 || z == -39 || z == -35) && !(x == 35 && z == -39)) b.set(x, 0, z, Blocks.SPRUCE_FENCE);
        for (int y = 1; y <= 3; y++) {
            b.set(33, y, -37, Blocks.SPRUCE_LOG);
            b.set(37, y, -37, Blocks.SPRUCE_LOG);
        }
        for (int x = 34; x <= 36; x++) b.set(x, 3, -37, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.X));
        b.set(35, 4, -37, Blocks.LANTERN);
        b.set(35, 2, -37, Blocks.CHAIN);
        b.set(35, 1, -37, Blocks.CHAIN);
        b.sign(32, 2, -37, Blocks.SPRUCE_WALL_SIGN, Direction.WEST, "DEEP SHAFT", "No riding", "the bucket");
    }

    private void oreFace(int x, int z, boolean northFace) {
        for (int y = 0; y <= 8; y++)
            for (int d = 0; d <= 1; d++) {
                int px = northFace ? x : x + d, pz = northFace ? z - d : z;
                int h = hash3(px, y, pz) % 20;
                Block ore = h == 0 || h == 1 ? Blocks.IRON_ORE : h == 2 ? Blocks.DEEPSLATE_IRON_ORE : h == 3 || h == 4 ? Blocks.COPPER_ORE
                        : h == 5 ? Blocks.COAL_ORE : h == 6 ? Blocks.DEEPSLATE_COAL_ORE : null;
                if (ore != null) b.set(px, y, pz, ore);
                else if (d == 0 && h < 12) b.set(px, y, pz, Blocks.STONE);
            }
    }

    /** A timber frame against the north face, with a lamp on it and one hanging beneath. */
    private void frame(int x, int zw) {
        for (int y = 0; y <= 3; y++) {
            b.set(x, y, zw + 1, Blocks.SPRUCE_LOG);
            b.set(x, y, zw + 4, Blocks.SPRUCE_LOG);
        }
        for (int z = zw + 1; z <= zw + 4; z++) b.set(x, 4, z, Builder.log(Blocks.SPRUCE_LOG, Direction.Axis.Z));
        b.set(x, 5, zw + 2, Blocks.LANTERN);
        b.set(x, 3, zw + 3, Builder.hangingLantern());
    }

    // ------------------------------------------------------------------ the Old Deeps

    private BlockState builderWall(int x, int y, int z) {
        int h = hash3(x, y, z);
        if (Math.floorMod(y, 4) == 0) return Blocks.POLISHED_DEEPSLATE.defaultBlockState();
        if (h % 23 == 0) return Blocks.WAXED_OXIDIZED_CUT_COPPER.defaultBlockState();
        return (h % 5 == 0 ? Blocks.POLISHED_TUFF : Blocks.TUFF_BRICKS).defaultBlockState();
    }

    /** The arch over the old incline, its railings and the surface spur. */
    private void deepsEntrance() {
        for (int y = 0; y <= 4; y++) {
            b.set(-55, y, -41, y == 4 ? Blocks.CHISELED_STONE_BRICKS : Blocks.DEEPSLATE_BRICKS);
            b.set(-50, y, -41, y == 4 ? Blocks.CHISELED_STONE_BRICKS : Blocks.DEEPSLATE_BRICKS);
        }
        for (int x = -54; x <= -51; x++) b.set(x, 4, -41, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(-55, 5, -41, Blocks.LANTERN);
        b.set(-50, 5, -41, Blocks.LANTERN);
        b.sign(-53, 4, -42, Blocks.DARK_OAK_WALL_SIGN, Direction.NORTH, "THE OLD DEEPS", "Flooded workings", "Enter only with", "the Thane's leave");
        b.set(-52, 3, -42, Builder.hangingLantern());
        for (int z = -46; z <= -42; z++) b.set(-51, 0, z, rail(RailShape.NORTH_SOUTH));
        b.set(-51, 0, -47, Blocks.POLISHED_BLACKSTONE_BRICKS);
    }

    private void deeps() {
        // Linings: the station (Stonecrown work), then the hall and the works (Builder work).
        for (int x = -58; x <= -46; x++)
            for (int z = -22; z <= -13; z++)
                for (int y = -21; y <= -14; y++) b.set(x, y, z, Blocks.DEEPSLATE_BRICKS);
        for (int x = -46; x <= -22; x++)
            for (int z = -21; z <= 6; z++)
                for (int y = -24; y <= -13; y++) b.set(x, y, z, builderWall(x, y, z));
        for (int x = -21; x <= -10; x++)
            for (int z = -11; z <= 5; z++)
                for (int y = -24; y <= -12; y++) b.set(x, y, z, builderWall(x, y, z));
        // The incline: steps k = 0..18 at z -40 + k, stair blocks at y -1 - k, the rail on x -51.
        for (int k = 0; k <= 18; k++) {
            int z = -40 + k, st = -1 - k;
            for (int x = -55; x <= -50; x++)
                for (int y = st - 1; y <= st + 5; y++) if (y <= -2) b.set(x, y, z, Blocks.DEEPSLATE_BRICKS);
            for (int x = -54; x <= -51; x++) for (int y = st + 1; y <= st + 4; y++) b.set(x, y, z, Blocks.AIR);
            for (int x = -54; x <= -52; x++) b.set(x, st, z, Builder.stairs(Blocks.DEEPSLATE_BRICK_STAIRS, Direction.NORTH, false));
            b.set(-51, st, z, Blocks.POLISHED_DEEPSLATE);
            b.set(-51, st + 1, z, rail(k == 0 ? RailShape.NORTH_SOUTH : RailShape.ASCENDING_NORTH));
            if (k % 4 == 2) b.set(-55, st + 3, z, Blocks.OCHRE_FROGLIGHT);
        }
        for (int z = -40; z <= -36; z++) {
            b.set(-55, 0, z, wallLine(Blocks.DEEPSLATE_BRICK_WALL, false));
            b.set(-50, 0, z, wallLine(Blocks.DEEPSLATE_BRICK_WALL, false));
        }
        for (int x = -55; x <= -50; x++) b.set(x, 0, -35, wallLine(Blocks.DEEPSLATE_BRICK_WALL, true));
        b.set(-55, 1, -36, Blocks.LANTERN);
        b.set(-50, 1, -36, Blocks.LANTERN);
        b.set(-55, 0, -36, Blocks.POLISHED_BLACKSTONE_BRICKS);
        b.set(-50, 0, -36, Blocks.POLISHED_BLACKSTONE_BRICKS);
        // The landing station.
        for (int x = -57; x <= -47; x++)
            for (int z = -21; z <= -14; z++) {
                b.set(x, LAND - 1, z, (x + z) % 4 == 0 ? Blocks.POLISHED_DEEPSLATE : Blocks.DEEPSLATE_TILES);
                for (int y = LAND; y <= -15; y++) b.set(x, y, z, Blocks.AIR);
            }
        b.set(-51, LAND, -21, rail(RailShape.ASCENDING_NORTH));
        for (int z = -20; z <= -16; z++) b.set(-51, LAND, z, rail(RailShape.NORTH_SOUTH));
        b.set(-51, LAND, -15, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int z = -21; z <= -17; z++) {
            b.set(-57, LAND, z, Builder.facing(Blocks.BARREL, Direction.EAST));
            if (z % 2 == 0) b.set(-57, LAND + 1, z, Builder.facing(Blocks.BARREL, Direction.EAST));
        }
        b.set(-57, LAND, -15, Builder.facing(Blocks.CHEST, Direction.EAST));
        b.set(-56, LAND, -14, Blocks.IRON_BLOCK);
        b.set(-55, LAND, -14, grindstone());
        for (int x : new int[] {-56, -52, -48}) {
            b.set(x, -17, -22, Blocks.OCHRE_FROGLIGHT);
            b.set(x, -17, -13, Blocks.OCHRE_FROGLIGHT);
        }
        b.sign(-53, -17, -14, Blocks.DARK_OAK_WALL_SIGN, Direction.NORTH, "OLD DEEPS", "Lower station", "", "Drains: east hall");
        // The works hall: upper volume (ledge level and above) and the pit below it.
        for (int x = -46; x <= -23; x++)
            for (int z = -20; z <= 5; z++)
                for (int y = LAND; y <= -14; y++) {
                    if (x == -46 && z > -19 && (z < -16 || z > -14)) continue;
                    if (x == -46 && z > -14) continue;
                    b.set(x, y, z, Blocks.AIR);
                }
        for (int x = HALL_X0; x <= HALL_X1; x++)
            for (int z = HALL_Z0; z <= HALL_Z1; z++) {
                b.set(x, DEEP - 1, z, Math.floorMod(x + z, 4) == 0 ? Blocks.POLISHED_TUFF : Blocks.TUFF_BRICKS);
                for (int y = DEEP; y <= DEEP + 2; y++) b.set(x, y, z, Blocks.AIR);
            }
        // Ledge, channel and the channel's wall (its top broken at x -36..-34).
        for (int x = -46; x <= -23; x++) {
            b.set(x, LAND - 1, -20, Blocks.POLISHED_DEEPSLATE);
            b.set(x, LAND - 1, -19, Blocks.POLISHED_DEEPSLATE);
        }
        for (int x = -45; x <= -24; x++) b.set(x, -21, -18, Blocks.POLISHED_TUFF);
        for (int x = -45; x <= -23; x++)
            for (int y = DEEP; y <= DEEP + 2; y++) b.set(x, y, -17, y == DEEP + 2 ? Blocks.CHISELED_TUFF : Blocks.TUFF_BRICKS);
        for (int x = -36; x <= -34; x++) b.set(x, -20, -17, Blocks.AIR);
        b.set(-37, -21, -17, Blocks.COBBLED_DEEPSLATE);
        b.set(-33, -21, -17, Blocks.COBBLED_DEEPSLATE);
        // The drain shaft at the channel's east end.
        for (int y = -26; y <= -21; y++) b.set(-23, y, -18, Blocks.AIR);
        b.sign(-24, -18, -20, Blocks.DARK_OAK_WALL_SIGN, Direction.SOUTH, "DRAIN SHAFT");
        b.set(-24, -18, -21, Blocks.POLISHED_DEEPSLATE);
        // Steps down into the hall from the station doorway.
        for (int z = -16; z <= -14; z++) {
            b.set(-45, DEEP, z, Blocks.POLISHED_TUFF);
            b.set(-45, DEEP + 1, z, Builder.stairs(Blocks.POLISHED_DEEPSLATE_STAIRS, Direction.WEST, false));
            b.set(-44, DEEP, z, Builder.stairs(Blocks.POLISHED_DEEPSLATE_STAIRS, Direction.WEST, false));
        }
        // Columns with pale lamps, lamps in the floor and along the ledge wall.
        for (int x : new int[] {-39, -28})
            for (int z : new int[] {-10, -2})
                for (int y = DEEP; y <= -14; y++) b.set(x, y, z, y == -17 ? Blocks.SEA_LANTERN : y == DEEP || y == -14 ? Blocks.CHISELED_TUFF_BRICKS : Blocks.TUFF_BRICKS);
        for (int x : new int[] {-42, -36, -30, -24})
            for (int z : new int[] {-13, -7, -1, 3}) b.set(x, DEEP - 1, z, Blocks.SEA_LANTERN);
        for (int x = -44; x <= -24; x += 4) b.set(x, -18, -21, Blocks.SEA_LANTERN);
        for (int x = -42; x <= -26; x += 8) b.set(x, -13, -6, Blocks.SEA_LANTERN);
        // The three counterweights, out of order: middle, smallest, largest; each on its core.
        weight(-43, 4, 2, 2);
        weight(-38, 4, 1, 1);
        weight(-32, 4, 3, 3);
        // The carving that shows their order: largest, middle, smallest.
        for (int x = -30; x <= -23; x++)
            for (int y = -19; y <= -14; y++) b.set(x, y, 6, Blocks.POLISHED_TUFF);
        for (int x = -29; x <= -27; x++) for (int y = -18; y <= -16; y++) b.set(x, y, 6, Blocks.CHISELED_TUFF_BRICKS);
        for (int x = -26; x <= -25; x++) for (int y = -18; y <= -17; y++) b.set(x, y, 6, Blocks.CHISELED_TUFF_BRICKS);
        b.set(-24, -18, 6, Blocks.CHISELED_TUFF_BRICKS);
        for (int x = -29; x <= -24; x++) b.set(x, -15, 6, Blocks.WAXED_OXIDIZED_CUT_COPPER);
        b.sign(-26, -19, 5, Blocks.DARK_OAK_WALL_SIGN, Direction.NORTH, "A carving:", "the greatest", "weight first, then", "the middle, the least");
        // The sealed works door.
        for (int z = DOOR_Z0 - 1; z <= DOOR_Z1 + 1; z++)
            for (int y = DOOR_Y0; y <= DOOR_Y1 + 1; y++) {
                boolean frame = z == DOOR_Z0 - 1 || z == DOOR_Z1 + 1 || y == DOOR_Y1 + 1;
                b.set(DOOR_X, y, z, frame ? Blocks.CHISELED_TUFF_BRICKS : Blocks.AIR);
            }
        door(true);
        works();
        // Finally the flood: the channel full, and the hall under three blocks of water.
        for (int x = -45; x <= -24; x++) b.set(x, -20, -18, Blocks.WATER);
        for (int x = -36; x <= -34; x++) b.set(x, -20, -17, Blocks.WATER);
        for (int x = HALL_X0; x <= HALL_X1; x++)
            for (int z = HALL_Z0; z <= HALL_Z1; z++)
                for (int y = DEEP; y <= DEEP + 2; y++) if (b.get(x, y, z).isAir()) b.set(x, y, z, Blocks.WATER);
    }

    /** A Builder counterweight hanging over its lodestone core (the core is the scene's clue block). */
    private void weight(int x, int z, int size, int height) {
        b.set(x, DEEP, z, Blocks.POLISHED_TUFF);
        b.set(x, DEEP + 1, z, Blocks.LODESTONE);
        int bottom = DEEP + 2;
        int x0 = size == 3 ? x - 1 : x, z0 = size == 3 ? z - 1 : z;
        for (int dx = 0; dx < size; dx++)
            for (int dz = 0; dz < size; dz++)
                for (int y = bottom; y < bottom + height; y++)
                    b.set(x0 + dx, y, z0 + dz, (y == bottom + height - 1 && size > 1) ? Blocks.CHISELED_TUFF : size == 1 ? Blocks.WAXED_OXIDIZED_COPPER : Blocks.POLISHED_DEEPSLATE);
        for (int y = bottom + height; y <= -14; y++) b.set(x0, y, z0, Blocks.CHAIN);
        if (size > 1) for (int y = bottom + height; y <= -14; y++) b.set(x0 + size - 1, y, z0 + size - 1, Blocks.CHAIN);
        // The gantry posts either side.
        for (int y = DEEP; y <= -14; y++) {
            b.set(x0 - 1, y, z0 + size - 1, Blocks.TUFF_BRICK_WALL);
            b.set(x0 + size, y, z0 + size - 1, Blocks.TUFF_BRICK_WALL);
        }
    }

    /** The works door, closed (Builder stone with copper inlay and a core) or gone. */
    private void door(boolean closed) {
        for (int z = DOOR_Z0; z <= DOOR_Z1; z++)
            for (int y = DOOR_Y0; y <= DOOR_Y1; y++) {
                BlockState s;
                if (!closed) s = Blocks.AIR.defaultBlockState();
                else if (z == -3 && y == -20) s = Blocks.LODESTONE.defaultBlockState();
                else if (z == -3 || y == -20) s = Blocks.WAXED_OXIDIZED_CUT_COPPER.defaultBlockState();
                else s = ((z + y) % 2 == 0 ? Blocks.POLISHED_DEEPSLATE : Blocks.POLISHED_TUFF).defaultBlockState();
                b.set(DOOR_X, y, z, s);
            }
    }

    /** The Builder works behind the door: one great machine, its plate and the gear housing. */
    private void works() {
        for (int x = -21; x <= -11; x++)
            for (int z = -10; z <= 4; z++) {
                b.set(x, DEEP - 1, z, Math.floorMod(x - z, 3) == 0 ? Blocks.TUFF_BRICKS : Blocks.POLISHED_DEEPSLATE);
                for (int y = DEEP; y <= -14; y++) b.set(x, y, z, Blocks.AIR);
                if ((x + 21) % 4 == 1 && (z + 10) % 4 == 1) b.set(x, -13, z, Blocks.SEA_LANTERN);
            }
        for (int[] p : new int[][] {{-19, -8}, {-19, -3}, {-19, 2}, {-12, -9}, {-12, 3}}) b.set(p[0], DEEP - 1, p[1], Blocks.SEA_LANTERN);
        // The machine body (x -15..-12, z -6..0).
        for (int x = -15; x <= -12; x++)
            for (int z = -6; z <= 0; z++)
                for (int y = DEEP; y <= -18; y++) {
                    boolean shell = x == -15 || x == -12 || z == -6 || z == 0 || y == -18;
                    BlockState s = y == DEEP ? Blocks.POLISHED_DEEPSLATE.defaultBlockState()
                            : !shell ? Blocks.LODESTONE.defaultBlockState()
                            : y == -18 ? Blocks.CHISELED_TUFF.defaultBlockState()
                            : (z == -3 && y == -20) ? Blocks.WAXED_OXIDIZED_COPPER_GRATE.defaultBlockState()
                            : (x + z) % 2 == 0 ? Blocks.TUFF_BRICKS.defaultBlockState() : Blocks.POLISHED_TUFF.defaultBlockState();
                    b.set(x, y, z, s);
                }
        b.set(-15, -19, -5, Blocks.LODESTONE);
        b.set(-15, -19, -1, Blocks.LODESTONE);
        b.set(-12, -21, -3, Blocks.SEA_LANTERN);
        b.set(-16, DEEP, -3, Blocks.POLISHED_TUFF);
        b.set(-16, -21, -3, Blocks.CHISELED_DEEPSLATE);
        for (int[] p : new int[][] {{-14, -4}, {-13, -2}, {-14, -1}}) b.set(p[0], -17, p[1], Blocks.LIGHTNING_ROD);
        for (int y = -17; y <= -14; y++) b.set(-13, y, -5, Blocks.CHAIN);
        // Copper pipes from the machine to the walls.
        for (int y = DEEP; y <= -14; y++) b.set(-11, y, -3, Blocks.WAXED_CUT_COPPER);
        for (int z = -9; z <= 3; z++) b.set(-20, -14, z, Blocks.WAXED_CUT_COPPER);
        // The gear housing on its plinth, and gear stock by the walls.
        b.set(-19, DEEP, 2, Blocks.POLISHED_TUFF);
        b.set(-19, -21, 2, Blocks.CHISELED_TUFF_BRICKS);
        b.set(-20, DEEP, -8, grindstone());
        b.set(-21, DEEP, -6, Blocks.POLISHED_TUFF);
        b.set(-21, DEEP + 1, -6, grindstone());
        b.set(-14, DEEP, 3, Blocks.CRAFTER);
        b.set(-13, DEEP, 3, Blocks.CRAFTER);
        b.set(-11, DEEP, 1, Blocks.WAXED_OXIDIZED_COPPER_GRATE);
        b.set(-11, DEEP + 1, 1, Blocks.WAXED_OXIDIZED_COPPER_GRATE);
    }

    // ------------------------------------------------------------------ light and carts

    /** Lamp stones in the floors on a 6-block grid, so every street and room stays lit. */
    private void lamps() {
        for (int x = -R; x <= R; x++)
            for (int z = -R; z <= R; z++) {
                if (Math.floorMod(x, 6) != 0 || Math.floorMod(z, 6) != 2) continue;
                lampAt(x, 0, z);
                lampAt(x, 2, z);
                lampAt(x, 4, z);
            }
    }

    private void lampAt(int x, int y, int z) {
        if (!inMain(x, z) || chasm(x, z)) return;
        if (FLOOR.contains(b.get(x, y - 1, z).getBlock()) && b.get(x, y, z).isAir() && b.get(x, y + 1, z).isAir())
            b.set(x, y - 1, z, Blocks.OCHRE_FROGLIGHT);
    }

    private void cart(EntityType<?> type, double x, int y, double z) {
        Entity e = type.create(level);
        if (e == null) return;
        BlockPos a = b.at(0, 0, 0);
        e.moveTo(a.getX() + x, a.getY() + y, a.getZ() + z, 0, 0);
        level.addFreshEntity(e);
    }

    private void carts() {
        cart(EntityType.CHEST_MINECART, 40.5, 0, -45.5);
        cart(EntityType.CHEST_MINECART, 34.5, 0, -45.5);
        cart(EntityType.MINECART, 46.5, 0, -30.5);
        cart(EntityType.CHEST_MINECART, 50.5, 0, 32.5);
        cart(EntityType.MINECART, -50.5, 0, -45.5);
        cart(EntityType.MINECART, -50.5, LAND, -17.5);
    }

    // ------------------------------------------------------------------ world changes

    /** Scene 013: the mended channel carries the flood away; the works hall stands dry. */
    public static void drain(ServerLevel level, BlockPos anchor) {
        Builder b = new Builder(level, anchor, 1013L);
        for (int y = DEEP + 2; y >= DEEP; y--)
            for (int x = HALL_X0; x <= HALL_X1; x++)
                for (int z = HALL_Z0; z <= HALL_Z1; z++)
                    if (!b.get(x, y, z).getFluidState().isEmpty() && b.get(x, y, z).is(Blocks.WATER)) b.set(x, y, z, Blocks.AIR);
        // A few wet stones where the water stood.
        for (int[] p : new int[][] {{-40, -12}, {-33, -5}, {-27, 1}, {-44, -4}}) {
            if (b.get(p[0], DEEP, p[1]).isAir()) b.set(p[0], DEEP - 1, p[1], Blocks.MOSSY_COBBLESTONE);
        }
    }

    /** Scene 013: the counterweights are set and the sealed works door sinks away. */
    public static void worksOpen(ServerLevel level, BlockPos anchor) {
        drain(level, anchor);
        Irondeep d = new Irondeep(level, anchor);
        d.door(false);
        d.b.set(DOOR_X, DOOR_Y0 - 1, -3, Blocks.WAXED_OXIDIZED_CUT_COPPER);
    }
}
