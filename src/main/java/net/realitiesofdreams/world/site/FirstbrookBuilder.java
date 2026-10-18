package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.realitiesofdreams.story.world.Mc001Terrain;
import net.realitiesofdreams.world.gen.Mc001ChunkGenerator;

/**
 * Firstbrook, MC-001's first village, built piece by piece from rod-story/sites/firstbrook.json.
 * Each piece type has a builder here; the layout file decides where each piece goes.
 */
public final class FirstbrookBuilder extends Builder {
    public FirstbrookBuilder(ServerLevel level, BlockPos anchor) {
        super(level, anchor, 1L);
    }

    public void build(SiteLayout.Piece p) {
        switch (p.type()) {
            case "terrain" -> terrain();
            case "ground" -> ground();
            case "river" -> river();
            case "square" -> square();
            case "paths" -> paths();
            case "steve_house" -> steveHouse();
            case "house" -> house(p);
            case "meeting_area" -> meetingArea(p.box());
            case "workshop" -> workshop(p.box());
            case "guard_station" -> guardStation(p);
            case "watchtower" -> watchtower(p.box());
            case "north_wall" -> northWall();
            case "storehouse" -> storehouse(p);
            case "market" -> market(p.box());
            case "farm" -> farm(p.box(), p.option("crops", "grain"));
            case "barn" -> barn(p.box());
            case "pens" -> pens(p.box());
            case "training_ground" -> trainingGround(p.box());
            case "woodcutter" -> woodcutter(p.box());
            case "construction" -> construction(p.box());
            case "dock" -> dock(p.box());
            case "trees" -> trees();
            case "hills" -> hills();
            case "waterfall" -> waterfall();
            case "signs" -> signs();
            default -> throw new IllegalArgumentException("Unknown Firstbrook piece type " + p.type());
        }
    }

    // ------------------------------------------------------------------ land

    /**
     * MC-001's generator already shapes the land. Anywhere else (the game-test world, or an old
     * flat MC-001), the hills, river channel and lake are raised here instead.
     */
    private void terrain() {
        if (level.getChunkSource().getGenerator() instanceof Mc001ChunkGenerator) return;
        for (int x = -64; x <= 63; x++) {
            for (int z = -140; z <= 44; z++) {
                int h = Mc001Terrain.height(x, z);
                if (h > 0) {
                    for (int y = 0; y < h; y++) set(x, y, z, y < h - 3 ? Blocks.STONE : Blocks.DIRT);
                    set(x, h - 1, z, Blocks.GRASS_BLOCK);
                } else if (h < 0) {
                    for (int y = -1; y >= h; y--) set(x, y, z, Mc001Terrain.water(x, z) ? Blocks.WATER : Blocks.AIR);
                    set(x, h - 1, z, Blocks.SAND);
                }
            }
        }
    }

    private void ground() {
        fill(-44, 0, -44, 44, 14, 44, Blocks.AIR);
        for (int x = -44; x <= 44; x++)
            for (int z = -44; z <= 44; z++) {
                if (Mc001Terrain.water(x, z)) continue;
                set(x, -1, z, Blocks.GRASS_BLOCK);
                set(x, -2, z, Blocks.DIRT);
                set(x, -3, z, Blocks.DIRT);
            }
    }

    /** The river east of the village, its reed-lined banks and the eastern bridge Steve repaired yesterday. */
    private void river() {
        for (int x = 30; x <= 34; x++)
            for (int z = -44; z <= 44; z++) {
                set(x, -1, z, Blocks.WATER);
                set(x, -2, z, Blocks.WATER);
                set(x, -3, z, Blocks.SAND);
            }
        for (int z = -44; z <= 44; z++) {
            set(29, -1, z, Blocks.SAND);
            set(35, -1, z, Blocks.SAND);
            if (rand.nextInt(5) == 0 && Math.abs(z - 11) > 3 && Math.abs(z - 28) > 3) {
                int bank = rand.nextBoolean() ? 29 : 35;
                set(bank, 0, z, Blocks.SUGAR_CANE);
                if (rand.nextBoolean()) set(bank, 1, z, Blocks.SUGAR_CANE);
            }
            if (rand.nextInt(9) == 0 && Math.abs(z - 11) > 2) set(30 + rand.nextInt(5), 0, z, Blocks.LILY_PAD);
        }
        for (int x = 29; x <= 35; x++) {
            for (int z = 11; z <= 12; z++) set(x, -1, z, (x + z) % 3 == 0 ? Blocks.STRIPPED_SPRUCE_LOG : Blocks.SPRUCE_PLANKS);
            set(x, 0, 10, Blocks.SPRUCE_FENCE);
            set(x, 0, 13, Blocks.SPRUCE_FENCE);
        }
        for (int z : new int[] {10, 13}) {
            set(29, 1, z, Blocks.SPRUCE_FENCE);
            set(29, 2, z, Blocks.LANTERN);
            set(35, 1, z, Blocks.SPRUCE_FENCE);
            set(35, 2, z, Blocks.LANTERN);
        }
        // Posts under the deck, and timber left over from the repair.
        for (int x : new int[] {29, 35}) for (int z : new int[] {11, 12}) set(x, -2, z, Blocks.SPRUCE_LOG);
        fill(36, 0, 14, 37, 0, 14, Blocks.OAK_PLANKS);
        set(36, 1, 14, Blocks.OAK_SLAB);
    }

    private void square() {
        for (int x = -6; x <= 6; x++)
            for (int z = -6; z <= 6; z++)
                if (x * x + z * z <= 30) set(x, -1, z, rand.nextInt(4) == 0 ? Blocks.MOSSY_COBBLESTONE : rand.nextInt(5) == 0 ? Blocks.ANDESITE : Blocks.COBBLESTONE);
        // The well.
        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) {
                    set(0, -1, 0, Blocks.WATER);
                    set(0, -2, 0, Blocks.WATER);
                } else {
                    set(x, 0, z, Blocks.COBBLESTONE_WALL);
                }
            }
        for (int[] c : new int[][] {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}) fill(c[0], 1, c[1], c[0], 2, c[1], Blocks.OAK_FENCE);
        fill(-1, 3, -1, 1, 3, 1, slab(Blocks.SPRUCE_SLAB, false));
        set(0, 2, 0, hangingLantern());
        // The lantern on its post that sways in the foreshadowing cinematic.
        fill(4, 0, -4, 4, 2, -4, Blocks.OAK_FENCE);
        set(4, 3, -4, Blocks.OAK_FENCE);
        set(3, 3, -4, Blocks.OAK_FENCE);
        set(3, 2, -4, hangingLantern());
        // Benches facing the well, flower tubs and the notice board.
        set(-4, 0, 4, stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH, false));
        set(-5, 0, 2, stairs(Blocks.SPRUCE_STAIRS, Direction.WEST, false));
        set(5, 0, 2, stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
        set(-3, 0, -5, stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH, false));
        for (int[] f : new int[][] {{-5, -1}, {5, -1}, {-2, 5}}) {
            set(f[0], 0, f[1], Blocks.COMPOSTER);
            set(f[0], 1, f[1], rand.nextBoolean() ? Blocks.POPPY : Blocks.CORNFLOWER);
        }
        fill(-6, 0, -3, -6, 2, -3, Blocks.SPRUCE_LOG);
        fill(-6, 0, -1, -6, 2, -1, Blocks.SPRUCE_LOG);
        fill(-6, 1, -2, -6, 2, -2, Blocks.SPRUCE_PLANKS);
        set(-6, 3, -3, slab(Blocks.SPRUCE_SLAB, false));
        set(-6, 3, -2, slab(Blocks.SPRUCE_SLAB, false));
        set(-6, 3, -1, slab(Blocks.SPRUCE_SLAB, false));
        sign(-5, 2, -2, Blocks.SPRUCE_WALL_SIGN, Direction.EAST, "FIRSTBROOK", "Notices", "", "Market day: today");
        sign(-5, 1, -2, Blocks.SPRUCE_WALL_SIGN, Direction.EAST, "Northern trail", "CLOSED past", "the old ruins", "- Bram");
    }

    private void path(int x0, int z0, int x1, int z1) {
        for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++)
            for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) {
                BlockState below = get(x, -1, z);
                if (!below.is(Blocks.GRASS_BLOCK) && !below.is(Blocks.DIRT)) continue;
                int r = rand.nextInt(10);
                set(x, -1, z, r < 7 ? Blocks.DIRT_PATH.defaultBlockState() : r < 9 ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.GRAVEL.defaultBlockState());
                if (!get(x, 0, z).isAir() && get(x, 0, z).canBeReplaced()) set(x, 0, z, Blocks.AIR);
            }
    }

    private void lanternPost(int x, int z) {
        fill(x, 0, z, x, 1, z, Blocks.SPRUCE_FENCE);
        set(x, 2, z, Blocks.LANTERN);
    }

    private void paths() {
        path(3, 11, 28, 12);    // east lane to the bridge
        path(36, 11, 36, 12);
        path(3, 4, 4, 10);      // up to the square
        path(16, 13, 16, 13);   // Steve's door
        path(-20, 11, 2, 12);   // west lane past the market
        path(-11, 13, -11, 14); // bakery door
        path(-1, 13, -1, 14);   // the Millers' door
        path(5, 13, 6, 24);     // down to the farmhouse
        path(2, 23, 4, 24);
        path(-1, -6, 1, -21);   // north road to the gate
        path(-7, -3, -6, -2);   // Elder Rowan's door
        path(2, -18, 4, -18);   // guard station
        path(-22, -15, -2, -15); // storehouse
        path(-30, -7, -2, -7);  // west to the training ground
        path(-30, -13, -29, -8);
        path(2, -4, 19, -4);    // past Orin's workshop
        path(18, -9, 19, -5);   // Orin's door
        path(-21, 13, -20, 22); // south to the pens and barn
        path(-30, 21, -22, 22);
        path(-30, 23, -29, 36);
        path(36, -20, 36, 10);  // the east bank
        path(36, 13, 36, 40);
        path(-23, -4, -21, -1); // council fire
        for (int[] l : new int[][] {{8, 13}, {24, 13}, {-6, 13}, {-16, 13}, {2, -12}, {-2, -18}, {7, 24}, {37, 9}, {-19, 20}}) lanternPost(l[0], l[1]);
    }

    // ------------------------------------------------------------------ houses

    private static Direction side(String s) {
        return switch (s) {
            case "north" -> Direction.NORTH;
            case "east" -> Direction.EAST;
            case "west" -> Direction.WEST;
            default -> Direction.SOUTH;
        };
    }

    /**
     * A house on its footprint: stone footing, timber frame and posts, windows, a pitched roof,
     * optional chimney and upper floor, a lantern by the door, a name sign and a furnished interior.
     */
    private void house(SiteLayout.Piece p) {
        int[] b = p.box();
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        Palette pal = Palette.of(p.option("palette", "timber"));
        int storeys = p.option("storeys", 1);
        int wallH = p.option("wall_height", storeys == 2 ? 8 : 4);
        Direction door = side(p.option("door", "south"));
        // Floor, then clear the inside.
        fill(x0, -1, z0, x1, -1, z1, pal.foundation());
        fill(x0 + 1, -1, z0 + 1, x1 - 1, -1, z1 - 1, pal.floor());
        fill(x0 + 1, 0, z0 + 1, x1 - 1, wallH + 8, z1 - 1, Blocks.AIR);
        int dx = door == Direction.EAST ? x1 : door == Direction.WEST ? x0 : Math.floorDiv(x0 + x1, 2);
        int dz = door == Direction.SOUTH ? z1 : door == Direction.NORTH ? z0 : Math.floorDiv(z0 + z1, 2);
        walls(x0, z0, x1, z1, wallH, pal, dx, dz);
        if (storeys == 2) {
            fill(x0 + 1, 4, z0 + 1, x1 - 1, 4, z1 - 1, pal.floor());
            fill(x0, 4, z0, x1, 4, z1, log(pal.trim(), (x1 - x0) >= (z1 - z0) ? Direction.Axis.X : Direction.Axis.Z));
            fill(x0 + 1, 4, z0 + 1, x1 - 1, 4, z1 - 1, pal.floor());
        }
        door(dx, 0, dz, pal.door(), door);
        // Lantern on a post and a name sign beside the door.
        Direction along = door.getClockWise();
        int ox = dx + door.getStepX(), oz = dz + door.getStepZ();
        lanternPost(ox + along.getStepX() * 2, oz + along.getStepZ() * 2);
        if (!p.name().isEmpty()) sign(dx - along.getStepX() * 2 + door.getStepX(), 1, dz - along.getStepZ() * 2 + door.getStepZ(), pal.wallSign(), door, "", p.name());
        set(ox, -1, oz, Blocks.DIRT_PATH);
        int ridge = gableRoof(x0, z0, x1, z1, wallH, pal);
        // A tie beam across the middle carries the hanging lanterns.
        boolean alongX = (x1 - x0) >= (z1 - z0);
        int beamY = storeys == 2 ? wallH : wallH;
        if (alongX) {
            int bx = Math.floorDiv(x0 + x1, 2);
            fill(bx, beamY, z0 + 1, bx, beamY, z1 - 1, log(pal.trim(), Direction.Axis.Z));
            set(bx, beamY - 1, Math.floorDiv(z0 + z1, 2), hangingLantern());
        } else {
            int bz = Math.floorDiv(z0 + z1, 2);
            fill(x0 + 1, beamY, bz, x1 - 1, beamY, bz, log(pal.trim(), Direction.Axis.X));
            set(Math.floorDiv(x0 + x1, 2), beamY - 1, bz, hangingLantern());
        }
        if (storeys == 2) set(Math.floorDiv(x0 + x1, 2) + (alongX ? 2 : 0), 3, Math.floorDiv(z0 + z1, 2) + (alongX ? 0 : 2), hangingLantern());
        if (p.flag("chimney")) {
            Direction back = door.getOpposite();
            int cx = back == Direction.EAST ? x1 + 1 : back == Direction.WEST ? x0 - 1 : Math.floorDiv(x0 + x1, 2) + 2;
            int cz = back == Direction.SOUTH ? z1 + 1 : back == Direction.NORTH ? z0 - 1 : Math.floorDiv(z0 + z1, 2) + 2;
            set(cx, -1, cz, pal.chimney());
            chimney(cx, cz, ridge + 1, pal.chimney());
        }
        Room room = new Room(this, b, door, dx, dz);
        furnish(room, p.option("interior", "home"), storeys, pal);
        // Bushes and flowers along the walls outside.
        for (int x = x0 - 1; x <= x1 + 1; x++) for (int z = z0 - 1; z <= z1 + 1; z++) {
            boolean ring = x == x0 - 1 || x == x1 + 1 || z == z0 - 1 || z == z1 + 1;
            if (!ring || Math.abs(x - ox) + Math.abs(z - oz) <= 3 || !get(x, -1, z).is(Blocks.GRASS_BLOCK) || !get(x, 0, z).isAir()) continue;
            int r = rand.nextInt(9);
            if (r == 0) bush(x, 0, z);
            else if (r == 1) set(x, 0, z, rand.nextBoolean() ? Blocks.ROSE_BUSH.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,
                    net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER) : Blocks.ALLIUM.defaultBlockState());
            if (r == 1 && get(x, 0, z).is(Blocks.ROSE_BUSH)) set(x, 1, z, Blocks.ROSE_BUSH.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,
                    net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER));
        }
    }

    /** Timber-framed walls: footing course, posts at corners and every few blocks, windows, a trim beam on top. */
    private void walls(int x0, int z0, int x1, int z1, int wallH, Palette pal, int doorX, int doorZ) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edgeX = x == x0 || x == x1, edgeZ = z == z0 || z == z1;
                if (!edgeX && !edgeZ) continue;
                boolean corner = edgeX && edgeZ;
                int along = edgeZ ? x - x0 : z - z0;
                int len = edgeZ ? x1 - x0 : z1 - z0;
                boolean post = corner || (along % 4 == 0 && along != len);
                for (int y = 0; y < wallH; y++) {
                    BlockState s;
                    if (post) s = pal.frame().defaultBlockState();
                    else if (y == 0) s = pal.foundation().defaultBlockState();
                    else if (y == wallH - 1 || (wallH == 8 && y == 4)) s = log(pal.trim(), edgeZ ? Direction.Axis.X : Direction.Axis.Z);
                    else s = pal.wall().defaultBlockState();
                    set(x, y, z, s);
                }
                if (post || corner) continue;
                boolean nearDoor = Math.abs(x - doorX) + Math.abs(z - doorZ) <= 2; // keeps the wall solid where the name sign hangs
                if (!nearDoor && along % 2 == 0) {
                    set(x, 1, z, pal.window());
                    if (wallH >= 5) set(x, 2, z, pal.window());
                    if (wallH == 8) {
                        set(x, 5, z, pal.window());
                        set(x, 6, z, pal.window());
                    }
                }
            }
    }

    private void furnish(Room r, String interior, int storeys, Palette pal) {
        Direction B = r.back, F = r.front, L = r.left, R = r.right;
        int W = r.width, D = r.depth;
        switch (interior) {
            case "elder" -> {
                for (int u = 1; u < W - 1; u++) {
                    r.set(u, 0, D - 1, Blocks.BOOKSHELF);
                    r.set(u, 1, D - 1, Blocks.BOOKSHELF);
                }
                r.set(W / 2, 0, D - 2, Blocks.LECTERN.defaultBlockState().setValue(LecternBlock.FACING, F).setValue(LecternBlock.HAS_BOOK, true));
                r.bed(0, D - 1, Blocks.RED_BED, L);
                r.facing(0, 0, D - 3 < 2 ? 2 : D - 3, Blocks.CHEST, R);
                r.set(W - 1, 0, D - 1, Blocks.CARTOGRAPHY_TABLE);
                r.table(W - 1, 2, F);
                r.set(W - 1, 1, D - 1, Blocks.POTTED_FERN);
                r.carpet(1, 2, W - 2, D - 3, Blocks.RED_CARPET);
                r.set(W - 1, 0, 1, Blocks.FLOWER_POT);
            }
            case "scout" -> { // Elara: a ranger's things
                r.bed(0, D - 1, Blocks.GREEN_BED, L);
                r.set(1, 0, D - 1, Blocks.FLETCHING_TABLE);
                r.set(W - 1, 0, D - 1, Blocks.CARTOGRAPHY_TABLE);
                r.facing(W - 1, 0, D - 2, Blocks.CHEST, L);
                r.set(W - 1, 1, D - 1, Blocks.POTTED_BLUE_ORCHID);
                r.set(1, 1, D - 1, Blocks.POTTED_OXEYE_DAISY);
                r.facing(W - 1, 0, 2, Blocks.BARREL, L);
                r.carpet(1, 2, W - 2, D - 2, Blocks.GREEN_CARPET);
            }
            case "baker" -> {
                r.facing(0, 0, D - 1, Blocks.SMOKER, B.getOpposite());
                r.facing(1, 0, D - 1, Blocks.SMOKER, B.getOpposite());
                r.facing(2, 0, D - 1, Blocks.FURNACE, B.getOpposite());
                r.set(W - 1, 0, D - 1, Blocks.HAY_BLOCK);
                r.set(W - 1, 1, D - 1, Blocks.HAY_BLOCK);
                r.facing(W - 1, 0, D - 2, Blocks.BARREL, L);
                r.set(W - 1, 0, 2, Blocks.CRAFTING_TABLE);
                r.table(1, 2, R);
                r.set(1, 1, 2, Blocks.CAKE);
                r.bed(0, 1 + 1, Blocks.YELLOW_BED, B);
            }
            case "smith_home" -> { // Orin's house
                r.bed(0, D - 1, Blocks.BROWN_BED, L);
                r.facing(1, 0, D - 1, Blocks.CHEST, F);
                r.set(W - 1, 0, D - 1, Blocks.SMITHING_TABLE);
                r.set(W - 1, 0, D - 2, Blocks.BOOKSHELF);
                r.set(W - 1, 1, D - 2, Blocks.BOOKSHELF);
                r.table(W - 2, 2, F);
                r.facing(0, 0, 2, Blocks.GRINDSTONE, R);
                r.carpet(1, 2, W - 3, D - 3, Blocks.BROWN_CARPET);
            }
            case "weaver" -> {
                r.facing(0, 0, D - 1, Blocks.LOOM, F);
                r.facing(1, 0, D - 1, Blocks.LOOM, F);
                Block[] wool = {Blocks.WHITE_WOOL, Blocks.YELLOW_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.RED_WOOL};
                for (int i = 0; i < 3; i++) r.set(W - 1, 0, D - 1 - i, wool[i]);
                r.set(W - 1, 1, D - 1, wool[3]);
                r.bed(0, 2, Blocks.LIGHT_BLUE_BED, B);
                r.facing(W - 1, 0, 1 + 1, Blocks.CHEST, L);
                r.carpet(1, 2, W - 2, D - 2, Blocks.YELLOW_CARPET);
            }
            case "family" -> {
                // Ground floor: kitchen and table. Upper floor: beds.
                r.facing(0, 0, D - 1, Blocks.SMOKER, F);
                r.facing(1, 0, D - 1, Blocks.BARREL, F);
                r.set(2, 0, D - 1, Blocks.CRAFTING_TABLE);
                r.table(W / 2, D / 2, L);
                r.set(W / 2 + 1, 0, D / 2, stairs(Blocks.SPRUCE_STAIRS, L, false));
                // Ladder up through the floor at the back right.
                int lu = W - 1, lv = D - 1;
                for (int y = 0; y <= 4; y++) r.set(lu, y, lv, Blocks.LADDER.defaultBlockState().setValue(net.minecraft.world.level.block.LadderBlock.FACING, F));
                for (int u = 0; u < W - 1; u++) {
                    if (u % 3 == 0) {
                        int wx = r.worldX(u, D - 1), wz = r.worldZ(u, D - 1);
                        bed(wx, 5, wz, u == 0 ? Blocks.BLUE_BED : Blocks.WHITE_BED, F.getOpposite());
                    }
                }
                int cx = r.worldX(W - 1, 1), cz = r.worldZ(W - 1, 1);
                set(cx, 5, cz, facing(Blocks.CHEST, L));
                int bx = r.worldX(0, 1), bz = r.worldZ(0, 1);
                set(bx, 5, bz, Blocks.BOOKSHELF);
            }
            case "farmhouse" -> {
                for (int u = 0; u < W; u += 2) r.bed(u, D - 1, u == 2 ? Blocks.ORANGE_BED : Blocks.BROWN_BED, B);
                r.set(0, 0, D - 3, Blocks.COMPOSTER);
                r.facing(W - 1, 0, 2, Blocks.BARREL, L);
                r.facing(W - 1, 1, 2, Blocks.BARREL, L);
                r.facing(W - 1, 0, 3, Blocks.BARREL, L);
                r.set(0, 0, 2, Blocks.CRAFTING_TABLE);
                r.table(W / 2, D / 2 - 1, F);
                r.set(0, 1, 2, Blocks.POTTED_DANDELION);
            }
            case "guard" -> {
                // Bunks along the back wall, an arms rack, a map table and the patrol log.
                for (int u = 0; u < W; u += 2) r.bed(u, D - 1, Blocks.BLUE_BED, B);
                r.set(W - 1, 0, 2, Blocks.CARTOGRAPHY_TABLE);
                r.set(W - 1, 0, 3, Blocks.LECTERN.defaultBlockState().setValue(LecternBlock.FACING, L).setValue(LecternBlock.HAS_BOOK, true));
                r.facing(0, 0, 2, Blocks.CHEST, R);
                r.facing(0, 0, 3, Blocks.BARREL, R);
                r.set(0, 1, 3, Blocks.TARGET);
            }
            case "storehouse" -> {
                for (int u = 0; u < W; u++) {
                    if (r.doorway(u, 0) || r.doorway(u, 1)) continue;
                    r.facing(u, 0, D - 1, Blocks.BARREL, F);
                    r.facing(u, 1, D - 1, u % 2 == 0 ? Blocks.BARREL : Blocks.CHEST, F);
                    if (u % 3 == 0) r.set(u, 2, D - 1, Blocks.HAY_BLOCK);
                }
                for (int v = 1; v < D - 1; v++) {
                    r.set(0, 0, v, v % 2 == 0 ? Blocks.HAY_BLOCK : Blocks.BARREL);
                    r.set(W - 1, 0, v, Blocks.SCAFFOLDING);
                    r.set(W - 1, 1, v, v % 2 == 0 ? Blocks.PUMPKIN : Blocks.MELON);
                }
            }
            default -> { // an ordinary home
                r.bed(0, D - 1, Blocks.RED_BED, L);
                r.facing(1, 0, D - 1, Blocks.CHEST, F);
                r.set(W - 1, 0, D - 1, Blocks.CRAFTING_TABLE);
                r.facing(W - 1, 0, D - 2, Blocks.FURNACE, L);
                r.table(W / 2, D / 2, F);
                r.set(W - 1, 0, 1 + 1, Blocks.BARREL);
            }
        }
    }

    // ------------------------------------------------------------------ Steve's house

    /**
     * Steve's modest house: crafting table, furnace, armour stand, tools, stored resources and
     * trophies from ordinary adventures. Outer footprint x 12..20, z 14..21; the door faces the lane.
     * The furnishings stay exactly where Scene 001's opening cinematic expects them.
     */
    private void steveHouse() {
        int x0 = 12, z0 = 14, x1 = 20, z1 = 21;
        Palette pal = Palette.TIMBER;
        fill(x0, -1, z0, x1, -1, z1, Blocks.COBBLESTONE);
        fill(x0 + 1, -1, z0 + 1, x1 - 1, -1, z1 - 1, Blocks.SPRUCE_PLANKS);
        fill(x0 + 1, 0, z0 + 1, x1 - 1, 12, z1 - 1, Blocks.AIR);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edgeX = x == x0 || x == x1, edgeZ = z == z0 || z == z1;
                if (!edgeX && !edgeZ) continue;
                boolean corner = edgeX && edgeZ;
                for (int y = 0; y <= 3; y++) {
                    BlockState s = corner ? Blocks.SPRUCE_LOG.defaultBlockState() : y == 3 ? log(Blocks.STRIPPED_SPRUCE_LOG, edgeZ ? Direction.Axis.X : Direction.Axis.Z)
                            : y == 0 ? Blocks.COBBLESTONE.defaultBlockState() : Blocks.OAK_PLANKS.defaultBlockState();
                    set(x, y, z, s);
                }
            }
        for (int[] w : new int[][] {{13, 14}, {14, 14}, {18, 14}, {19, 14}, {20, 16}, {20, 17}, {12, 17}, {12, 18}, {18, 21}}) set(w[0], 1, w[1], Blocks.GLASS_PANE);
        door(16, 0, z0, Blocks.SPRUCE_DOOR, Direction.NORTH);
        set(16, -1, z0 - 1, Blocks.DIRT_PATH);
        int ridge = gableRoof(x0, z0, x1, z1, 4, pal);
        fill(x0 + 1, 4, 17, x1 - 1, 4, 17, log(Blocks.STRIPPED_SPRUCE_LOG, Direction.Axis.X));
        lanternPost(18, z0 - 1);
        sign(15, 1, z0 - 1, Blocks.SPRUCE_WALL_SIGN, Direction.NORTH, "", "Steve");
        set(x0 - 1, -1, 19, Blocks.COBBLESTONE);
        chimney(x0 - 1, 19, ridge + 1, Blocks.COBBLESTONE);

        // Bed under the north window, where the morning light falls.
        bed(19, 0, 15, Blocks.RED_BED, Direction.NORTH);
        set(13, 0, 15, Blocks.CRAFTING_TABLE);
        set(13, 0, 16, Blocks.FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.FACING, Direction.EAST));
        set(13, 0, 19, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.EAST));
        set(13, 0, 20, Blocks.BARREL);
        set(14, 0, 20, Blocks.BARREL);
        set(13, 1, 20, Blocks.POTTED_DANDELION);
        for (int x = 15; x <= 17; x++) for (int z = 17; z <= 18; z++) set(x, 0, z, Blocks.BROWN_CARPET);
        set(16, 3, 17, hangingLantern());
        set(19, 0, 18, Blocks.SPRUCE_STAIRS);
        set(19, 0, 19, Blocks.OAK_FENCE);
        set(19, 1, 19, Blocks.OAK_PRESSURE_PLATE);
        set(13, 1, 15, Blocks.FLOWER_POT);

        // Breakfast and basic gear in the storage chest.
        container(13, 0, 19, c -> {
            put(c, 0, new ItemStack(Items.BREAD, 3));
            put(c, 1, new ItemStack(Items.APPLE, 2));
            put(c, 2, new ItemStack(Items.COOKED_SALMON, 1));
            put(c, 9, new ItemStack(Items.LEATHER_HELMET));
            put(c, 10, new ItemStack(Items.LEATHER_LEGGINGS));
            put(c, 11, new ItemStack(Items.WOODEN_SWORD));
            put(c, 18, new ItemStack(Items.OAK_PLANKS, 16));
            put(c, 19, new ItemStack(Items.COBBLESTONE, 16));
            put(c, 20, new ItemStack(Items.COAL, 4));
        });
        container(13, 0, 20, c -> {
            put(c, 0, new ItemStack(Items.WHEAT, 12));
            put(c, 1, new ItemStack(Items.STICK, 8));
        });
        container(14, 0, 20, c -> {
            put(c, 0, new ItemStack(Items.OAK_LOG, 8));
            put(c, 1, new ItemStack(Items.TORCH, 6));
        });
        // Armour stand with his everyday leather armour.
        armorStand(19.5, 0, 20.5, 90, ItemStack.EMPTY, new ItemStack(Items.LEATHER_CHESTPLATE), ItemStack.EMPTY, new ItemStack(Items.LEATHER_BOOTS));
        // Trophies from ordinary adventures on the south wall.
        itemFrame(15, 1, 20, Direction.NORTH, new ItemStack(Items.IRON_SWORD));
        itemFrame(16, 2, 20, Direction.NORTH, new ItemStack(Items.FISHING_ROD));
        itemFrame(17, 1, 20, Direction.NORTH, new ItemStack(Items.BOW));
        // His vegetable patch by the door.
        for (int x = 18; x <= 20; x++) {
            set(x, -1, 23, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7));
            set(x, 0, 23, Blocks.CARROTS.defaultBlockState().setValue(CropBlock.AGE, 5 + rand.nextInt(3)));
        }
        set(17, -1, 23, Blocks.WATER);
    }

    /**
     * The council fire beside Elder Rowan's house, where Firstbrook meets: an open timber pavilion
     * around a fire, benches, the village ledger on a lectern and Eldervale's blue banners.
     */
    private void meetingArea(int[] b) {
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        int cx = Math.floorDiv(x0 + x1, 2), cz = Math.floorDiv(z0 + z1, 2);
        for (int x = x0; x <= x1; x++) for (int z = z0; z <= z1; z++) set(x, -1, z, rand.nextInt(4) == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS);
        for (int[] c : new int[][] {{x0, z0}, {x1, z0}, {x0, z1}, {x1, z1}}) fill(c[0], 0, c[1], c[0], 3, c[1], Blocks.DARK_OAK_LOG);
        for (int x = x0; x <= x1; x++) {
            set(x, 4, z0, log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.X));
            set(x, 4, z1, log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.X));
        }
        for (int z = z0; z <= z1; z++) {
            set(x0, 4, z, log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.Z));
            set(x1, 4, z, log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.Z));
        }
        gableRoof(x0, z0, x1, z1, 5, Palette.ELDER);
        // Leave the roof open over the fire so the smoke escapes.
        for (int y = 5; y <= 9; y++) set(cx, y, cz, Blocks.AIR);
        campfire(cx, 0, cz, false);
        for (int dx = -2; dx <= 2; dx++) {
            if (dx == 0) continue;
            set(cx + dx, 0, cz - 2, stairs(Blocks.DARK_OAK_STAIRS, Direction.NORTH, false));
            set(cx + dx, 0, cz + 2, stairs(Blocks.DARK_OAK_STAIRS, Direction.SOUTH, false));
        }
        set(cx - 3, 0, cz, Blocks.LECTERN.defaultBlockState().setValue(LecternBlock.FACING, Direction.EAST).setValue(LecternBlock.HAS_BOOK, true));
        set(x0 + 1, 3, z0, Blocks.BLUE_WALL_BANNER.defaultBlockState().setValue(net.minecraft.world.level.block.WallBannerBlock.FACING, Direction.EAST));
        set(x1 - 1, 3, z1, Blocks.BLUE_WALL_BANNER.defaultBlockState().setValue(net.minecraft.world.level.block.WallBannerBlock.FACING, Direction.WEST));
        set(cx + 2, 3, cz, hangingLantern());
        set(cx - 2, 3, cz, hangingLantern());
        fill(cx - 2, 4, cz, cx + 2, 4, cz, log(Blocks.STRIPPED_DARK_OAK_LOG, Direction.Axis.X));
        set(cx, 4, cz, Blocks.AIR);
        standingSign(x1 + 1, 0, z1 + 1, Blocks.DARK_OAK_SIGN, 0, "", "Council Fire", "All are welcome");
    }

    // ------------------------------------------------------------------ workplaces

    /** Orin's workshop: open to the square, with forge, anvil, tool racks and a smoking chimney. */
    private void workshop(int[] b) {
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        fill(x0, -1, z0, x1, -1, z1, Blocks.STONE_BRICKS);
        fill(x0 + 1, -1, z0 + 1, x1 - 1, -1, z1 - 1, Blocks.POLISHED_ANDESITE);
        fill(x0, 0, z0, x1, 10, z1, Blocks.AIR);
        // Back and side walls; the south side stays open between two posts.
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edgeX = x == x0 || x == x1, edgeZ = z == z0;
                boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                if (!(edgeX || edgeZ) && !corner) continue;
                for (int y = 0; y <= 3; y++) {
                    BlockState s = corner ? Blocks.STRIPPED_OAK_LOG.defaultBlockState() : z == z1 ? Blocks.AIR.defaultBlockState()
                            : y <= 1 ? Blocks.STONE_BRICKS.defaultBlockState() : y == 3 ? log(Blocks.STRIPPED_SPRUCE_LOG, edgeZ ? Direction.Axis.X : Direction.Axis.Z)
                            : Blocks.SPRUCE_PLANKS.defaultBlockState();
                    set(x, y, z, s);
                }
            }
        for (int x = x0 + 1; x < x1; x++) set(x, 3, z1, log(Blocks.STRIPPED_SPRUCE_LOG, Direction.Axis.X));
        set(x0 + 2, 2, z0, Blocks.GLASS_PANE);
        set(x0 + 4, 2, z0, Blocks.GLASS_PANE);
        set(x0, 2, z0 + 3, Blocks.GLASS_PANE);
        int ridge = gableRoof(x0, z0, x1, z1, 4, Palette.STONE);
        fill(x0 + 1, 4, z0 + 3, x1 - 1, 4, z0 + 3, log(Blocks.STRIPPED_SPRUCE_LOG, Direction.Axis.X));
        // Forge row against the east wall, under the chimney.
        fill(x0 + 5, 0, z0 + 1, x0 + 5, 0, z0 + 5, Blocks.COBBLESTONE);
        set(x0 + 5, 1, z0 + 2, Blocks.BLAST_FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.FACING, Direction.WEST).setValue(AbstractFurnaceBlock.LIT, true));
        set(x0 + 5, 1, z0 + 3, Blocks.FURNACE.defaultBlockState().setValue(AbstractFurnaceBlock.FACING, Direction.WEST));
        set(x0 + 5, 1, z0 + 4, Blocks.SMOKER.defaultBlockState().setValue(AbstractFurnaceBlock.FACING, Direction.WEST));
        set(x1, -1, z0 + 2, Blocks.STONE_BRICKS);
        chimney(x1, z0 + 2, ridge + 1, Blocks.STONE_BRICKS);
        set(x0 + 3, 0, z0 + 2, Blocks.ANVIL);
        set(x0 + 2, 0, z0 + 5, Blocks.SMITHING_TABLE);
        set(x0 + 1, 0, z0 + 1, Blocks.GRINDSTONE);
        set(x0 + 1, 0, z0 + 3, Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
        set(x0 + 1, 0, z0 + 4, facing(Blocks.CHEST, Direction.EAST));
        set(x0 + 3, 0, z0 + 1, facing(Blocks.BARREL, Direction.SOUTH));
        set(x0 + 4, 0, z0 + 1, Blocks.STONECUTTER);
        set(x0 + 3, 3, z0 + 3, hangingLantern());
        container(x0 + 1, 0, z0 + 4, c -> {
            put(c, 0, new ItemStack(Items.IRON_INGOT, 6));
            put(c, 1, new ItemStack(Items.COAL, 10));
            put(c, 2, new ItemStack(Items.IRON_NUGGET, 12));
        });
        // Tools hung on the back wall.
        itemFrame(x0 + 2, 1, z0 + 1, Direction.SOUTH, new ItemStack(Items.IRON_PICKAXE));
        itemFrame(x0 + 4, 1, z0 + 1, Direction.SOUTH, new ItemStack(Items.IRON_AXE));
        itemFrame(x0 + 1, 2, z0 + 2, Direction.EAST, new ItemStack(Items.SHEARS));
        armorStand(x0 + 2.5, 0, z0 + 3.5, 160, new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_CHESTPLATE), ItemStack.EMPTY, ItemStack.EMPTY);
        sign(x0 - 1, 1, z1, Blocks.SPRUCE_WALL_SIGN, Direction.WEST, "", "Orin's", "Workshop");
    }

    /** Bram's guard station by the north gate: bunks, arms, the patrol log and the duty board. */
    private void guardStation(SiteLayout.Piece p) {
        house(p);
        int[] b = p.box();
        ArmorStand rack = armorStand(b[0] + 2.5, 0, b[1] - 0.0 + 1.5, 180, new ItemStack(Items.IRON_HELMET), new ItemStack(Items.CHAINMAIL_CHESTPLATE),
                new ItemStack(Items.CHAINMAIL_LEGGINGS), new ItemStack(Items.IRON_BOOTS));
        if (rack != null) rack.setShowArms(true);
        set(b[0] - 1, 0, b[1] + 1, Blocks.HAY_BLOCK);
        set(b[0] - 1, 1, b[1] + 1, Blocks.TARGET);
        set(b[0] - 1, 2, b[3] - 1, Blocks.BLUE_WALL_BANNER.defaultBlockState().setValue(net.minecraft.world.level.block.WallBannerBlock.FACING, Direction.WEST));
        container(b[0] + 1, 0, b[3] - 3, c -> {
            put(c, 0, new ItemStack(Items.ARROW, 32));
            put(c, 1, new ItemStack(Items.BREAD, 6));
            put(c, 2, new ItemStack(Items.TORCH, 16));
        });
    }

    /** The north watchtower: stone base, timber shaft with arrow slits, a roofed lookout platform. */
    private void watchtower(int[] b) {
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        fill(x0, 0, z0, x1, 15, z1, Blocks.AIR);
        fill(x0, -1, z0, x1, -1, z1, Blocks.STONE_BRICKS);
        for (int y = 0; y <= 9; y++)
            for (int x = x0; x <= x1; x++)
                for (int z = z0; z <= z1; z++) {
                    boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                    if (!edge) continue;
                    boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
                    BlockState s = corner ? Blocks.SPRUCE_LOG.defaultBlockState() : y <= 2 ? Blocks.STONE_BRICKS.defaultBlockState()
                            : (y == 5 || y == 8) && (x == (x0 + x1) / 2 || z == (z0 + z1) / 2) ? Blocks.AIR.defaultBlockState() : Blocks.SPRUCE_PLANKS.defaultBlockState();
                    set(x, y, z, s);
                }
        int mx = Math.floorDiv(x0 + x1, 2);
        door(mx, 0, z1, Blocks.SPRUCE_DOOR, Direction.SOUTH);
        ladder(mx, 0, 10, z0 + 1, Direction.SOUTH);
        fill(x0 - 1, 10, z0 - 1, x1 + 1, 10, z1 + 1, Blocks.SPRUCE_PLANKS);
        set(mx, 10, z0 + 1, Blocks.LADDER.defaultBlockState().setValue(net.minecraft.world.level.block.LadderBlock.FACING, Direction.SOUTH));
        for (int x = x0 - 1; x <= x1 + 1; x++)
            for (int z = z0 - 1; z <= z1 + 1; z++) {
                boolean edge = x == x0 - 1 || x == x1 + 1 || z == z0 - 1 || z == z1 + 1;
                boolean corner = (x == x0 - 1 || x == x1 + 1) && (z == z0 - 1 || z == z1 + 1);
                if (corner) fill(x, 11, z, x, 12, z, Blocks.SPRUCE_LOG);
                else if (edge) set(x, 11, z, Blocks.SPRUCE_FENCE);
            }
        gableRoof(x0 - 1, z0 - 1, x1 + 1, z1 + 1, 13, Palette.GUARD);
        set(mx, 12, Math.floorDiv(z0 + z1, 2), hangingLantern());
        set(mx + 1, 11, z1, Blocks.BELL.defaultBlockState());
    }

    private void northWall() {
        for (int x = -30; x <= 28; x++) {
            if (Math.abs(x) <= 2) continue;
            int h = Math.abs(x) == 3 ? 5 : 2 + (Math.floorMod(x, 3) == 0 ? 1 : 0);
            fill(x, 0, -22, x, h, -22, Math.abs(x) == 3 ? Blocks.STONE_BRICKS : Blocks.SPRUCE_LOG);
            if (Math.abs(x) != 3) set(x, h + 1, -22, Blocks.SPRUCE_FENCE);
        }
        fill(-3, 6, -22, 3, 6, -22, log(Blocks.STRIPPED_SPRUCE_LOG, Direction.Axis.X));
        set(0, 7, -22, Blocks.BLUE_BANNER.defaultBlockState());
        set(-2, 5, -22, hangingLantern());
        set(2, 5, -22, hangingLantern());
        // Corner posts.
        for (int x : new int[] {-30, 28}) fill(x, 0, -22, x, 4, -22, Blocks.STONE_BRICKS);
    }

    private void storehouse(SiteLayout.Piece p) {
        house(p);
        int[] b = p.box();
        // Crates and a cart outside.
        set(b[2] + 1, 0, b[3] - 1, Blocks.BARREL);
        set(b[2] + 1, 1, b[3] - 1, Blocks.BARREL);
        set(b[2] + 1, 0, b[3] - 2, Blocks.HAY_BLOCK);
    }

    private void market(int[] b) {
        int x0 = b[0], z0 = b[1];
        Block[] roofs = {Blocks.RED_WOOL, Blocks.YELLOW_WOOL, Blocks.LIGHT_BLUE_WOOL};
        Block[][] goods = {{Blocks.HAY_BLOCK, Blocks.PUMPKIN}, {Blocks.MELON, Blocks.BARREL}, {Blocks.CARVED_PUMPKIN, Blocks.BEEHIVE}};
        for (int s = 0; s < 3; s++) {
            int sx = x0 + s * 4;
            for (int[] c : new int[][] {{0, 0}, {2, 0}, {0, 2}, {2, 2}}) fill(sx + c[0], 0, z0 + c[1], sx + c[0], 2, z0 + c[1], Blocks.OAK_FENCE);
            fill(sx, 3, z0, sx + 2, 3, z0 + 2, roofs[s]);
            set(sx + 1, 0, z0, facing(Blocks.BARREL, Direction.SOUTH));
            set(sx + 1, 0, z0 + 2, slab(Blocks.SPRUCE_SLAB, true));
            set(sx + 1, 1, z0 + 2, goods[s][0]);
            set(sx + 1, 2, z0, hangingLantern());
        }
        set(x0 + 1, 0, z0 + 3, Blocks.HAY_BLOCK);
        set(x0 + 5, 0, z0 + 3, Blocks.COMPOSTER);
        set(x0 + 9, 0, z0 + 3, Blocks.PUMPKIN);
        set(x0 + 10, 0, z0 + 3, Blocks.MELON);
        // A trader's cart at the end of the row.
        int cx = x0 + 12;
        fill(cx, 0, z0, cx + 1, 0, z0 + 2, slab(Blocks.SPRUCE_SLAB, true));
        set(cx, 1, z0, Blocks.BARREL);
        set(cx + 1, 1, z0 + 1, Blocks.HAY_BLOCK);
        set(cx - 1, 0, z0, Blocks.OAK_FENCE);
        standingSign(x0 - 1, 0, z0 + 1, Blocks.OAK_SIGN, 4, "", "Market");
    }

    private void farm(int[] b, String crops) {
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        Block[] veg = {Blocks.POTATOES, Blocks.BEETROOTS, Blocks.CARROTS, Blocks.WHEAT};
        for (int x = x0 - 1; x <= x1 + 1; x++)
            for (int z = z0 - 1; z <= z1 + 1; z++) {
                boolean edge = x < x0 || x > x1 || z < z0 || z > z1;
                if (edge) {
                    if (get(x, -1, z).is(Blocks.GRASS_BLOCK)) set(x, -1, z, Blocks.DIRT_PATH);
                    continue;
                }
                if (z == (z0 + z1) / 2) {
                    set(x, -1, z, Blocks.WATER);
                    continue;
                }
                set(x, -1, z, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7));
                Block crop = crops.equals("vegetables") ? veg[Math.floorMod(x / 3, veg.length)] : (x / 3) % 3 == 0 ? Blocks.CARROTS : Blocks.WHEAT;
                BlockState s = crop.defaultBlockState();
                if (crop == Blocks.BEETROOTS) s = s.setValue(BlockStateProperties.AGE_3, 2 + rand.nextInt(2));
                else s = s.setValue(CropBlock.AGE, 3 + rand.nextInt(5));
                set(x, 0, z, s);
            }
        set(x0 - 1, 0, z1 + 1, Blocks.COMPOSTER);
        if (crops.equals("vegetables")) {
            for (int z = z0; z <= z1; z += 2) set(x1 + 2, 0, z, rand.nextBoolean() ? Blocks.PUMPKIN : Blocks.MELON);
            ArmorStand scarecrow = armorStand((x0 + x1) / 2.0 + 0.5, 0, z0 - 0.5, 180, new ItemStack(Blocks.CARVED_PUMPKIN), new ItemStack(Items.LEATHER_CHESTPLATE),
                    ItemStack.EMPTY, ItemStack.EMPTY);
            if (scarecrow != null) scarecrow.setShowArms(true);
        }
    }

    /** The barn: a tall timber hall with stalls, hay stacks and a loft. */
    private void barn(int[] b) {
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        Palette pal = Palette.FARM;
        fill(x0, -1, z0, x1, -1, z1, Blocks.COBBLESTONE);
        fill(x0 + 1, -1, z0 + 1, x1 - 1, -1, z1 - 1, Blocks.COARSE_DIRT);
        fill(x0 + 1, 0, z0 + 1, x1 - 1, 14, z1 - 1, Blocks.AIR);
        int midZ = Math.floorDiv(z0 + z1, 2);
        walls(x0, z0, x1, z1, 6, pal, x1, midZ);
        // Wide doorway facing the pens.
        fill(x1, 0, midZ - 1, x1, 3, midZ + 1, Blocks.AIR);
        int ridge = gableRoof(x0, z0, x1, z1, 6, pal);
        // Loft over the back half with a ladder.
        fill(x0 + 1, 3, z0 + 1, x0 + 4, 3, z1 - 1, Blocks.SPRUCE_SLAB.defaultBlockState().setValue(net.minecraft.world.level.block.SlabBlock.TYPE,
                net.minecraft.world.level.block.state.properties.SlabType.TOP));
        ladder(x0 + 5, 0, 3, z0 + 1, Direction.SOUTH);
        set(x0 + 5, 3, z0 + 1, Blocks.AIR);
        ladder(x0 + 5, 0, 3, z0 + 1, Direction.SOUTH);
        for (int z = z0 + 2; z <= z1 - 2; z += 3) {
            set(x0 + 1, 4, z, Blocks.HAY_BLOCK);
            set(x0 + 2, 4, z, Blocks.HAY_BLOCK);
            set(x0 + 1, 5, z, Blocks.HAY_BLOCK);
        }
        // Stalls along the front wall.
        for (int z = z0 + 1; z <= z1 - 1; z++) if ((z - z0) % 3 == 0 && Math.abs(z - midZ) > 1) fill(x1 - 3, 0, z, x1 - 1, 0, z, Blocks.OAK_FENCE);
        set(x1 - 1, 0, z0 + 1, Blocks.HAY_BLOCK);
        set(x1 - 1, 0, z1 - 1, Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
        for (int z = z0 + 3; z <= z1 - 3; z += 4) set(Math.floorDiv(x0 + x1, 2), 5, z, hangingLantern());
        fill(x0 + 1, 6, z0 + 1, x1 - 1, 6, z0 + 1, log(Blocks.STRIPPED_OAK_LOG, Direction.Axis.X));
        for (int z = z0 + 3; z <= z1 - 3; z += 4) fill(x0 + 1, 6, z, x1 - 1, 6, z, log(Blocks.STRIPPED_OAK_LOG, Direction.Axis.X));
        animal(EntityType.HORSE, x1 - 2, 0, z0 + 2);
        animal(EntityType.HORSE, x1 - 2, 0, z1 - 3);
        sign(x1 + 1, 1, midZ + 2, Blocks.OAK_WALL_SIGN, Direction.EAST, "", "Barn");
    }

    /** Fenced pens for cattle and sheep, pigs, and a chicken run with its coop. */
    private void pens(int[] b) {
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        int split = z0 + 8, mid = Math.floorDiv(x0 + x1, 2);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                boolean inner = z == split || (z > split && x == mid);
                if (edge || inner) set(x, 0, z, Blocks.OAK_FENCE);
                else if (rand.nextInt(6) == 0) set(x, -1, z, Blocks.COARSE_DIRT);
            }
        gate(mid - 4, 0, z0, Blocks.OAK_FENCE_GATE, Direction.NORTH);
        gate(x0, 0, z0 + 4, Blocks.OAK_FENCE_GATE, Direction.WEST);
        gate(mid - 3, 0, split, Blocks.OAK_FENCE_GATE, Direction.NORTH);
        gate(mid + 3, 0, split, Blocks.OAK_FENCE_GATE, Direction.NORTH);
        // Water troughs and hay.
        fill(x1 - 3, -1, z0 + 2, x1 - 1, -1, z0 + 2, Blocks.WATER);
        set(x0 + 2, 0, z0 + 2, Blocks.HAY_BLOCK);
        set(x0 + 3, 0, z1 - 2, Blocks.HAY_BLOCK);
        set(mid - 2, -1, z1 - 2, Blocks.WATER);
        // The chicken coop.
        int cx0 = mid + 2, cz0 = z1 - 4;
        fill(cx0, 0, cz0, cx0 + 3, 0, cz0 + 2, Blocks.SPRUCE_PLANKS);
        fill(cx0, 1, cz0, cx0 + 3, 1, cz0 + 2, Blocks.SPRUCE_FENCE);
        fill(cx0, 2, cz0, cx0 + 3, 2, cz0 + 2, slab(Blocks.SPRUCE_SLAB, false));
        set(cx0, 1, cz0 + 1, Blocks.AIR);
        set(cx0 + 1, 1, cz0 + 1, Blocks.HAY_BLOCK);
        for (int i = 0; i < 3; i++) animal(EntityType.COW, x0 + 3 + i * 3, 0, z0 + 3 + i % 2);
        for (int i = 0; i < 3; i++) animal(EntityType.SHEEP, x0 + 4 + i * 3, 0, z0 + 6 - i % 2);
        for (int i = 0; i < 3; i++) animal(EntityType.PIG, x0 + 2 + i * 2, 0, split + 2 + i);
        for (int i = 0; i < 4; i++) animal(EntityType.CHICKEN, mid + 1 + i, 0, split + 2 + i % 2);
        standingSign(x0 + 1, 0, z0 - 1, Blocks.OAK_SIGN, 8, "", "Animal pens", "Close the gates!");
    }

    /** Bram's training ground: a fenced yard with practice dummies and an archery range. */
    private void trainingGround(int[] b) {
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                if (edge) set(x, 0, z, Blocks.SPRUCE_FENCE);
                else set(x, -1, z, rand.nextInt(3) == 0 ? Blocks.COARSE_DIRT : rand.nextInt(4) == 0 ? Blocks.GRAVEL : Blocks.DIRT_PATH);
            }
        int gz = Math.floorDiv(z0 + z1, 2);
        gate(x1, 0, gz, Blocks.SPRUCE_FENCE_GATE, Direction.EAST);
        // Archery targets along the north fence.
        for (int x = x0 + 2; x <= x1 - 2; x += 3) {
            set(x, 0, z0 + 1, Blocks.HAY_BLOCK);
            set(x, 1, z0 + 1, Blocks.TARGET);
        }
        // Practice dummies.
        for (int i = 0; i < 3; i++) {
            double dx = x0 + 3.5 + i * 3, dz = z1 - 3.5;
            set((int) dx, -1, (int) dz, Blocks.HAY_BLOCK);
            ArmorStand dummy = armorStand(dx, 0, dz, 0, new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.LEATHER_CHESTPLATE), ItemStack.EMPTY, ItemStack.EMPTY);
            if (dummy != null) dummy.setShowArms(true);
        }
        // Weapon racks and a bench.
        set(x1 - 1, 0, z0 + 1, facing(Blocks.CHEST, Direction.WEST));
        container(x1 - 1, 0, z0 + 1, c -> {
            put(c, 0, new ItemStack(Items.WOODEN_SWORD));
            put(c, 1, new ItemStack(Items.WOODEN_SWORD));
            put(c, 2, new ItemStack(Items.BOW));
            put(c, 3, new ItemStack(Items.ARROW, 16));
            put(c, 4, new ItemStack(Items.SHIELD));
        });
        set(x1 - 1, 0, z0 + 2, stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
        set(x1 - 1, 0, z0 + 3, stairs(Blocks.SPRUCE_STAIRS, Direction.EAST, false));
        lanternPost(x1 - 1, z1 - 1);
        standingSign(x1 + 1, 0, gz + 2, Blocks.SPRUCE_SIGN, 12, "Training Ground", "Drills at dawn", "- Bram");
    }

    /** A grove west of the village with the woodcutter's clearing. */
    private void woodcutter(int[] b) {
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        int cx = Math.floorDiv(x0 + x1, 2), cz = Math.floorDiv(z0 + z1, 2);
        int placed = 0;
        for (int i = 0; i < 80 && placed < 16; i++) {
            int x = x0 + rand.nextInt(x1 - x0 + 1), z = z0 + rand.nextInt(z1 - z0 + 1);
            if (Math.abs(x - cx) < 4 && Math.abs(z - cz) < 4) continue;
            if (!get(x, -1, z).is(Blocks.GRASS_BLOCK) || !get(x, 0, z).isAir() || !get(x + 1, 0, z).isAir() || !get(x - 1, 0, z).isAir()) continue;
            if (rand.nextInt(3) == 0) broadleaf(x, 0, z, 5 + rand.nextInt(2), Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES);
            else broadleaf(x, 0, z, 4 + rand.nextInt(3), Blocks.OAK_LOG, Blocks.OAK_LEAVES);
            placed++;
        }
        // The clearing: log piles, a chopping block, a sawbench and a lean-to.
        for (int i = 0; i < 3; i++) fill(cx - 3, i, cz - 2, cx - 3, i, cz, log(Blocks.OAK_LOG, Direction.Axis.Z));
        fill(cx - 2, 0, cz - 2, cx - 2, 1, cz, log(Blocks.OAK_LOG, Direction.Axis.Z));
        set(cx, 0, cz, Blocks.STRIPPED_OAK_LOG);
        set(cx + 1, 0, cz + 1, Blocks.STONECUTTER);
        campfire(cx + 2, 0, cz - 2, false);
        for (int[] c : new int[][] {{1, 2}, {3, 2}}) fill(cx + c[0], 0, cz + c[1], cx + c[0], 1, cz + c[1], Blocks.OAK_FENCE);
        fill(cx + 1, 2, cz + 2, cx + 3, 2, cz + 3, slab(Blocks.OAK_SLAB, false));
        set(cx + 2, 0, cz + 3, facing(Blocks.BARREL, Direction.NORTH));
        for (int x = cx - 4; x <= cx + 4; x++) for (int z = cz - 4; z <= cz + 4; z++) if (rand.nextInt(4) == 0 && get(x, -1, z).is(Blocks.GRASS_BLOCK)) set(x, -1, z, Blocks.COARSE_DIRT);
    }

    /** A new house going up on the east bank: footing, half-built frame, scaffolding and timber. */
    private void construction(int[] b) {
        int x0 = b[0], z0 = b[1], x1 = b[2], z1 = b[3];
        fill(x0, -1, z0, x1, -1, z1, Blocks.COBBLESTONE);
        fill(x0 + 1, -1, z0 + 1, x1 - 1, -1, z1 - 3, Blocks.SPRUCE_PLANKS);
        for (int[] c : new int[][] {{x0, z0}, {x1, z0}, {x0, z1}, {x1, z1}}) fill(c[0], 0, c[1], c[0], 3, c[1], Blocks.SPRUCE_LOG);
        fill(x0, 0, z0 + 1, x0, 2, z1 - 1, Blocks.OAK_PLANKS);
        fill(x0 + 1, 0, z0, x1 - 1, 1, z0, Blocks.OAK_PLANKS);
        fill(x0, 3, z0, x1, 3, z0, log(Blocks.STRIPPED_SPRUCE_LOG, Direction.Axis.X));
        for (int z = z0; z <= z1; z += 2) fill(x1 + 1, 0, z, x1 + 1, 3, z, Blocks.SCAFFOLDING);
        for (int i = 0; i < 2; i++) fill(x0 + 2, i, z1 - 1, x0 + 4, i, z1 - 1, log(Blocks.SPRUCE_LOG, Direction.Axis.X));
        set(x0 + 2, 0, z1 - 2, Blocks.OAK_PLANKS);
        set(x0 + 2, 1, z1 - 2, Blocks.OAK_PLANKS);
        set(x0 + 3, 0, z1 - 2, Blocks.CRAFTING_TABLE);
        set(x0 + 4, 0, z1 - 2, facing(Blocks.BARREL, Direction.WEST));
        sign(x0 - 1, 1, z0 + 2, Blocks.SPRUCE_WALL_SIGN, Direction.WEST, "New home for", "the Hollow family", "Builders wanted!", "- Bram");
    }

    private void dock(int[] b) {
        int z = b[1] + 1;
        for (int x = 31; x <= 35; x++) {
            set(x, -1, z, Blocks.SPRUCE_PLANKS);
            set(x, -1, z + 1, Blocks.SPRUCE_PLANKS);
        }
        for (int x : new int[] {31, 33}) {
            set(x, -2, z - 1, Blocks.SPRUCE_LOG);
            set(x, -1, z - 1, Blocks.SPRUCE_LOG);
            set(x, 0, z - 1, Blocks.SPRUCE_FENCE);
        }
        set(31, 0, z, Blocks.SPRUCE_FENCE);
        set(31, 1, z, Blocks.LANTERN);
        set(36, 0, z + 1, facing(Blocks.BARREL, Direction.WEST));
        container(36, 0, z + 1, c -> {
            put(c, 0, new ItemStack(Items.COD, 4));
            put(c, 1, new ItemStack(Items.FISHING_ROD));
        });
        Boat boat = EntityType.BOAT.create(level);
        if (boat != null) {
            BlockPos p = at(32, -1, z + 3);
            boat.setVariant(Boat.Type.OAK);
            boat.moveTo(p.getX() + 0.5, p.getY() + 0.6, p.getZ() + 0.5, 90, 0);
            level.addFreshEntity(boat);
        }
    }

    // ------------------------------------------------------------------ nature

    private void trees() {
        int[][] oaks = {{-24, 18}, {-36, -30}, {24, -30}, {14, -34}, {-16, -34}, {40, 2}, {40, -20}, {38, 30}, {-40, 10},
                {-8, 28}, {28, 38}, {-34, 42}, {10, 40}, {42, -36}, {-2, 40}, {20, 24}, {-20, -40}, {40, 42}};
        for (int[] t : oaks) if (get(t[0], 0, t[1]).isAir() && get(t[0], -1, t[1]).is(Blocks.GRASS_BLOCK))
            broadleaf(t[0], 0, t[1], 5 + Math.floorMod(t[0] * 31 + t[1], 2), Blocks.OAK_LOG, Blocks.OAK_LEAVES);
        int[][] birches = {{42, -10}, {38, -2}, {41, 24}, {-12, 38}, {16, 42}};
        for (int[] t : birches) if (get(t[0], 0, t[1]).isAir() && get(t[0], -1, t[1]).is(Blocks.GRASS_BLOCK))
            broadleaf(t[0], 0, t[1], 6, Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES);
        Block[] flowers = {Blocks.POPPY, Blocks.DANDELION, Blocks.CORNFLOWER, Blocks.OXEYE_DAISY, Blocks.AZURE_BLUET, Blocks.SHORT_GRASS, Blocks.SHORT_GRASS, Blocks.SHORT_GRASS};
        for (int i = 0; i < 700; i++) {
            int x = rand.nextInt(89) - 44, z = rand.nextInt(89) - 44;
            if (get(x, -1, z).is(Blocks.GRASS_BLOCK) && get(x, 0, z).isAir()) set(x, 0, z, flowers[rand.nextInt(flowers.length)]);
        }
    }

    /**
     * The northern hills beyond the wall: the old trail with its unlit lantern posts, spruces, and
     * the ruin on the ridge, visible from the square.
     */
    private void hills() {
        Block[] surface = {Blocks.DIRT_PATH, Blocks.COARSE_DIRT, Blocks.GRAVEL, Blocks.DIRT_PATH};
        for (int z = -23; z >= -104; z--) {
            int cx = (int) Math.round(Math.sin(z / 4.0) * 2) + (z < -46 ? (int) Math.round((-46 - z) * 0.1) : 0);
            for (int x = cx - 1; x <= cx + 1; x++) {
                int h = Mc001Terrain.hillHeight(x, z);
                set(x, h - 1, z, surface[rand.nextInt(surface.length)]);
                if (!get(x, h, z).isAir() && get(x, h, z).canBeReplaced()) set(x, h, z, Blocks.AIR);
            }
            if (z % 6 == 0) {
                int px = cx + (z % 12 == 0 ? 3 : -3);
                int h = Mc001Terrain.hillHeight(px, z);
                fill(px, h, z, px, h + 1, z, Blocks.OAK_FENCE); // old lantern posts, long unlit
            }
        }
        for (int i = 0; i < 70; i++) {
            int x = rand.nextInt(129) - 64, z = -47 - rand.nextInt(20);
            int h = Mc001Terrain.hillHeight(x, z);
            if (h > 3 && Math.abs(x) > 6 && Math.abs(x - Mc001Terrain.RIVER_X) > 5 && get(x, h, z).isAir() && get(x, h - 1, z).is(Blocks.GRASS_BLOCK))
                spruce(x, h, z, 6 + rand.nextInt(3));
        }
        // The ruin on the ridge: broken pillars and a fallen lintel.
        int rz = Mc001Terrain.RIDGE_Z, base = Mc001Terrain.hillHeight(10, rz);
        int[][] pillars = {{4, 7}, {8, 9}, {12, 5}, {16, 8}, {6, 3}, {14, 2}};
        for (int[] pl : pillars) {
            int px = pl[0], top = base + pl[1];
            for (int y = Mc001Terrain.hillHeight(px, rz) - 1; y < top; y++) set(px, y, rz, (y % 3 == 0) ? Blocks.CRACKED_STONE_BRICKS : Blocks.MOSSY_STONE_BRICKS);
        }
        fill(7, base + 9, rz, 9, base + 9, rz, Blocks.STONE_BRICK_SLAB.defaultBlockState());
        fill(10, Mc001Terrain.hillHeight(10, rz + 2), rz + 2, 12, Mc001Terrain.hillHeight(10, rz + 2), rz + 2, Blocks.MOSSY_COBBLESTONE.defaultBlockState());
    }

    /** A spring pool at the foot of the hills that spills over the cliff into the river. */
    private void waterfall() {
        int z0 = Mc001Terrain.WATERFALL_Z - 1;
        int top = 0;
        for (int x = 29; x <= 35; x++) for (int z = z0 - 4; z <= z0; z++) top = Math.max(top, Mc001Terrain.height(x, z));
        if (top < 2) return;
        for (int x = 29; x <= 35; x++)
            for (int z = z0 - 4; z <= z0; z++) {
                boolean rim = x == 29 || x == 35 || z == z0 - 4;
                for (int y = Mc001Terrain.height(x, z); y <= top; y++) set(x, y - 1, z, Blocks.MOSSY_COBBLESTONE);
                if (rim) set(x, top, z, rand.nextBoolean() ? Blocks.MOSSY_COBBLESTONE : Blocks.COBBLESTONE);
                else {
                    set(x, top - 1, z, Blocks.WATER);
                    set(x, top, z, Blocks.AIR);
                }
            }
        // Let the water fall: an opening in the pool's front at the river channel.
        for (int x = 31; x <= 33; x++) for (int y = 0; y <= top; y++) if (!get(x, y, z0 + 1).isAir() && !get(x, y, z0 + 1).is(Blocks.WATER)) set(x, y, z0 + 1, Blocks.AIR);
        for (int x = 30; x <= 34; x++) {
            set(x, -1, z0 + 1, Blocks.WATER);
            set(x, -2, z0 + 1, Blocks.WATER);
            set(x, -3, z0 + 1, Blocks.SAND);
        }
    }

    /** Signposts at the village entrances. */
    private void signs() {
        standingSign(-3, 0, -24, Blocks.SPRUCE_SIGN, 8, "", "FIRSTBROOK", "Eldervale");
        standingSign(38, 0, 9, Blocks.SPRUCE_SIGN, 12, "", "FIRSTBROOK", "<- Village");
        standingSign(2, 0, 7, Blocks.SPRUCE_SIGN, 4, "<- Market", "Farms ->", "North gate ^");
    }
}
