package net.realitiesofdreams.world.firstbrook;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.realitiesofdreams.world.build.Build;
import net.realitiesofdreams.world.build.Room;

import java.util.List;

/** The homes of Firstbrook: Steve's house and a family of cottage styles with furnished interiors. */
final class Homes {
    private Homes() {}

    /** Wall, roof and trim materials for one architectural style. */
    enum Style {
        TIMBER(Blocks.BIRCH_PLANKS, Blocks.BIRCH_PLANKS, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_STAIRS,
                Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_DOOR, Blocks.SPRUCE_PLANKS, false),
        STONE(Blocks.COBBLESTONE, Blocks.SPRUCE_PLANKS, Blocks.STONE_BRICKS, Blocks.SPRUCE_LOG, Blocks.SPRUCE_STAIRS,
                Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_PLANKS, true),
        LOG(Blocks.SPRUCE_LOG, Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.DARK_OAK_STAIRS,
                Blocks.DARK_OAK_SLAB, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_DOOR, Blocks.OAK_PLANKS, false),
        BRICK(Blocks.BRICKS, Blocks.BRICKS, Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_LOG, Blocks.DEEPSLATE_TILE_STAIRS,
                Blocks.DEEPSLATE_TILE_SLAB, Blocks.BRICKS, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_PLANKS, true),
        COTTAGE(Blocks.OAK_PLANKS, Blocks.OAK_PLANKS, Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_STAIRS,
                Blocks.SPRUCE_SLAB, Blocks.OAK_PLANKS, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_PLANKS, false),
        ELDER(Blocks.STONE_BRICKS, Blocks.OAK_PLANKS, Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_STAIRS,
                Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_PLANKS, true);

        final Block lower, upper, corner, beam, roof, ridge, gable, door, floor;
        final boolean chimney;

        Style(Block lower, Block upper, Block corner, Block beam, Block roof, Block ridge, Block gable, Block door, Block floor, boolean chimney) {
            this.lower = lower;
            this.upper = upper;
            this.corner = corner;
            this.beam = beam;
            this.roof = roof;
            this.ridge = ridge;
            this.gable = gable;
            this.door = door;
            this.floor = floor;
            this.chimney = chimney;
        }
    }

    private static final Block[] BEDS = {Blocks.BLUE_BED, Blocks.GREEN_BED, Blocks.YELLOW_BED, Blocks.CYAN_BED, Blocks.PURPLE_BED, Blocks.ORANGE_BED};

    /**
     * A furnished home. (x0,z0)-(x1,z1) is the outer footprint, the door is in the middle of the
     * wall on {@code doorSide}, and one bed is made for each owner (named points "bed:owner").
     */
    static Room cottage(Build b, String id, int x0, int z0, int x1, int z1, Direction doorSide, Style s, List<String> owners) {
        shell(b, x0, z0, x1, z1, doorSide, s);
        Room r = new Room(b, x0, z0, x1, z1, doorSide);
        int w = r.width, d = r.depth;

        // Beds against the back wall, one per resident.
        int[] bedCols = {0, w - 1, 2, w - 3};
        for (int i = 0; i < owners.size() && i < bedCols.length; i++) {
            BlockPos pillow = r.bed(bedCols[i], BEDS[Math.floorMod(id.hashCode() + i, BEDS.length)]);
            b.point("bed:" + owners.get(i), pillow.getX(), 0, pillow.getZ());
            // The resident stands beside the bed when getting up.
        }
        // Kitchen corner by the door: stove, larder barrel, crafting table.
        r.set(0, 0, 0, Build.facing(Blocks.SMOKER, r.across()).setValue(AbstractFurnaceBlock.LIT, false));
        r.set(0, 0, 1, Blocks.BARREL);
        r.set(0, 1, 1, Blocks.POTTED_RED_TULIP);
        if (d >= 5) r.set(0, 0, 2, Blocks.CRAFTING_TABLE);
        // Storage chest on the other side of the door.
        BlockPos chest = r.rel(w - 1, 0, 0);
        b.container(chest.getX(), 0, chest.getZ(), Build.facing(Blocks.CHEST, r.across().getOpposite()).setValue(ChestBlock.FACING, r.across().getOpposite()),
                new ItemStack(Items.BREAD, 4), new ItemStack(Items.APPLE, 3), new ItemStack(Items.WHEAT, 8), new ItemStack(Items.TORCH, 6),
                new ItemStack(Items.OAK_LOG, 6));
        // Table with two chairs in the middle of the room.
        int tu = w / 2, tv = Math.max(2, d / 2);
        if (tv >= d - 2) tv = d - 3;
        if (tv >= 1 && w >= 5) {
            r.set(tu, 0, tv, Blocks.OAK_FENCE);
            r.set(tu, 1, tv, Blocks.OAK_PRESSURE_PLATE);
            r.set(tu - 1, 0, tv, Build.stairs(Blocks.OAK_STAIRS, r.across().getOpposite()));
            r.set(tu + 1, 0, tv, Build.stairs(Blocks.OAK_STAIRS, r.across()));
        }
        // Personal touches: bookshelf or loom, flower pot, rug.
        if (w >= 6 && d >= 6) {
            r.set(w - 1, 0, d - 3, s == Style.TIMBER ? Blocks.LOOM : Blocks.BOOKSHELF);
            r.set(w - 1, 1, d - 3, Blocks.POTTED_AZURE_BLUET);
        }
        Block rug = switch (s) {
            case TIMBER -> Blocks.LIGHT_BLUE_CARPET;
            case STONE -> Blocks.BROWN_CARPET;
            case LOG -> Blocks.RED_CARPET;
            case BRICK -> Blocks.GRAY_CARPET;
            default -> Blocks.GREEN_CARPET;
        };
        for (int u = 1; u < w - 1; u++) {
            BlockPos p = r.rel(u, 0, 1);
            if (b.isAir(p.getX(), 0, p.getZ()) && u != r.doorU()) b.set(p.getX(), 0, p.getZ(), rug);
        }
        BlockPos lamp = r.rel(w / 2, 3, d / 2);
        b.set(lamp.getX(), 3, lamp.getZ(), Build.hangingLantern());
        BlockPos work = r.rel(Math.min(1, w - 1), 0, 1);
        b.point("home:" + id, work.getX(), 0, work.getZ());
        return r;
    }

    /** Walls, windows, floor, ceiling, gable roof, door, porch light and (for some styles) a smoking chimney. */
    static void shell(Build b, int x0, int z0, int x1, int z1, Direction doorSide, Style s) {
        b.fill(x0, -1, z0, x1, -1, z1, s.floor);
        b.fill(x0 + 1, 0, z0 + 1, x1 - 1, 3, z1 - 1, Blocks.AIR);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean ex = x == x0 || x == x1, ez = z == z0 || z == z1;
                if (!ex && !ez) continue;
                boolean corner = ex && ez;
                for (int y = 0; y <= 3; y++) b.set(x, y, z, corner ? s.corner : y == 0 ? s.lower : s.upper);
                b.set(x, 4, z, s.beam);
                // windows on every wall, every other block, kept clear of the corners
                int along = ex ? z - z0 : x - x0;
                int fromCorner = ex ? Math.min(z - z0, z1 - z) : Math.min(x - x0, x1 - x);
                if (!corner && fromCorner >= 2 && along % 2 == 0) {
                    b.set(x, 1, z, Blocks.GLASS_PANE);
                    b.set(x, 2, z, Blocks.GLASS_PANE);
                }
            }
        b.fill(x0 + 1, 4, z0 + 1, x1 - 1, 4, z1 - 1, s.floor); // ceiling
        b.gableRoof(x0, z0, x1, z1, 5, s.roof, s.ridge, s.gable);

        int cx = (x0 + x1) / 2, cz = (z0 + z1) / 2;
        int dx = switch (doorSide) { case EAST -> x1; case WEST -> x0; default -> cx; };
        int dz = switch (doorSide) { case NORTH -> z0; case SOUTH -> z1; default -> cz; };
        b.door(dx, 0, dz, s.door, doorSide);
        b.set(dx, 2, dz, s.upper == Blocks.SPRUCE_LOG ? Blocks.STRIPPED_SPRUCE_LOG : s.upper);
        int ox = dx + doorSide.getStepX(), oz = dz + doorSide.getStepZ();
        b.set(ox, -1, oz, Blocks.DIRT_PATH);
        Direction side = doorSide.getClockWise();
        int tx = dx + side.getStepX(), tz = dz + side.getStepZ();
        b.set(tx + doorSide.getStepX(), 2, tz + doorSide.getStepZ(), Blocks.WALL_TORCH.defaultBlockState()
                .setValue(net.minecraft.world.level.block.WallTorchBlock.FACING, doorSide));
        // flower boxes under the front windows
        for (int k = -3; k <= 3; k++) {
            if (Math.abs(k) < 2) continue;
            int fx = dx + side.getStepX() * k + doorSide.getStepX(), fz = dz + side.getStepZ() * k + doorSide.getStepZ();
            if (fx < Math.min(x0, x1) - 1 || fx > Math.max(x0, x1) + 1 || fz < Math.min(z0, z1) - 1 || fz > Math.max(z0, z1) + 1) continue;
            if (Math.abs(k) == 2 && b.get(fx - doorSide.getStepX(), 1, fz - doorSide.getStepZ()).is(Blocks.GLASS_PANE)) {
                b.trapdoorOpen(fx, 0, fz, Blocks.SPRUCE_TRAPDOOR, doorSide);
            }
        }
        if (s.chimney) {
            Direction back = doorSide.getOpposite();
            int chx = (back.getAxis() == Direction.Axis.X ? (back == Direction.EAST ? x1 + 1 : x0 - 1) : x0 + 1);
            int chz = (back.getAxis() == Direction.Axis.Z ? (back == Direction.SOUTH ? z1 + 1 : z0 - 1) : z0 + 1);
            int top = 5 + (Math.min(x1 - x0, z1 - z0) + 2) / 2 + 1;
            b.fill(chx, 0, chz, chx, top, chz, Blocks.COBBLESTONE);
            b.set(chx, top + 1, chz, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, true).setValue(CampfireBlock.SIGNAL_FIRE, false));
        }
    }

    // ------------------------------------------------------------------ Steve's house

    /**
     * Steve's house: a modest adventurer's home with a bed under the north window, crafting table,
     * furnace, storage, armour stand with everyday leather armour, and trophies from ordinary
     * adventures. Footprint x 12..20, z 14..21; the door faces the lane. The opening cinematic
     * films this room, so the positions of the bed, door, stand and windows are fixed.
     */
    static void steveHouse(Build b) {
        int x0 = 12, z0 = 14, x1 = 20, z1 = 21;
        b.fill(x0, -1, z0, x1, -1, z1, Blocks.SPRUCE_PLANKS);
        b.fill(x0 + 1, 0, z0 + 1, x1 - 1, 3, z1 - 1, Blocks.AIR);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean ex = x == x0 || x == x1, ez = z == z0 || z == z1;
                if (!ex && !ez) continue;
                boolean corner = ex && ez;
                for (int y = 0; y <= 3; y++) b.set(x, y, z, corner ? Blocks.SPRUCE_LOG : y == 0 ? Blocks.COBBLESTONE : Blocks.OAK_PLANKS);
            }
        b.fill(x0, 4, z0, x1, 4, z1, Blocks.STRIPPED_SPRUCE_LOG);
        b.fill(x0 + 1, 4, z0 + 1, x1 - 1, 4, z1 - 1, Blocks.SPRUCE_PLANKS);
        b.gableRoof(x0, z0, x1, z1, 5, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_SLAB, Blocks.OAK_PLANKS);
        for (int[] w : new int[][] {{13, 14}, {14, 14}, {18, 14}, {19, 14}, {20, 16}, {20, 17}, {12, 17}, {12, 18}, {18, 21}}) {
            b.set(w[0], 1, w[1], Blocks.GLASS_PANE);
        }
        b.door(16, 0, z0, Blocks.SPRUCE_DOOR, Direction.NORTH);
        b.set(16, -1, 13, Blocks.DIRT_PATH);
        // Porch posts with lanterns either side of the path.
        for (int px : new int[] {14, 18}) {
            b.fill(px, 0, 13, px, 1, 13, Blocks.SPRUCE_FENCE);
            b.set(px, 2, 13, Blocks.LANTERN);
        }
        b.sign(15, 2, 13, Direction.NORTH, "", "Steve", "", "");

        // Bed under the north window, where the morning light falls.
        b.bed(19, 0, 15, Direction.NORTH, Blocks.RED_BED);
        b.set(13, 0, 15, Blocks.CRAFTING_TABLE);
        b.set(13, 0, 16, Build.facing(Blocks.FURNACE, Direction.EAST));
        b.set(13, 0, 17, Blocks.CARTOGRAPHY_TABLE);
        b.container(13, 0, 19, Build.facing(Blocks.CHEST, Direction.EAST),
                new ItemStack(Items.BREAD, 3), new ItemStack(Items.APPLE, 2), new ItemStack(Items.COOKED_SALMON, 1),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.WOODEN_SWORD),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(Items.OAK_PLANKS, 16), new ItemStack(Items.COBBLESTONE, 16), new ItemStack(Items.COAL, 4));
        b.container(13, 0, 20, Blocks.BARREL.defaultBlockState(), new ItemStack(Items.WHEAT, 12), new ItemStack(Items.STICK, 8));
        b.set(14, 0, 20, Blocks.BARREL);
        b.set(13, 1, 20, Blocks.POTTED_DANDELION);
        b.set(14, 1, 20, Blocks.CANDLE.defaultBlockState().setValue(net.minecraft.world.level.block.CandleBlock.LIT, false));
        for (int x = 15; x <= 17; x++) for (int z = 17; z <= 18; z++) b.set(x, 0, z, Blocks.BROWN_CARPET);
        b.set(16, 3, 17, Build.hangingLantern());
        b.set(19, 0, 18, Build.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH));
        b.set(18, 0, 20, Blocks.BOOKSHELF);
        b.set(18, 1, 20, Blocks.FLOWER_POT);
        b.set(19, 3, 15, Build.hangingLantern());

        b.armorStand(19, 0, 20, 90, ItemStack.EMPTY, new ItemStack(Items.LEATHER_CHESTPLATE), ItemStack.EMPTY, new ItemStack(Items.LEATHER_BOOTS));
        // Trophies from ordinary adventures on the south wall.
        b.itemFrame(15, 1, 20, Direction.NORTH, new ItemStack(Items.IRON_SWORD));
        b.itemFrame(16, 2, 20, Direction.NORTH, new ItemStack(Items.FISHING_ROD));
        b.itemFrame(17, 1, 20, Direction.NORTH, new ItemStack(Items.BOW));
        b.point("bed:primordial_steve", 19, 0, 15);

        // Kitchen garden behind the house.
        int gz0 = 23, gz1 = 27;
        for (int x = 12; x <= 20; x++)
            for (int z = gz0; z <= gz1; z++) {
                boolean edge = x == 12 || x == 20 || z == gz0 || z == gz1;
                if (edge) {
                    b.set(x, 0, z, (x == 12 && z == 25) ? Blocks.SPRUCE_FENCE_GATE.defaultBlockState()
                            .setValue(net.minecraft.world.level.block.FenceGateBlock.FACING, Direction.EAST) : Blocks.SPRUCE_FENCE.defaultBlockState());
                    continue;
                }
                if (x == 16) {
                    b.set(x, -1, z, Blocks.WATER);
                    continue;
                }
                b.set(x, -1, z, Blocks.FARMLAND.defaultBlockState().setValue(net.minecraft.world.level.block.FarmBlock.MOISTURE, 7));
                Block crop = x < 16 ? Blocks.POTATOES : Blocks.BEETROOTS;
                b.set(x, 0, z, crop == Blocks.BEETROOTS
                        ? crop.defaultBlockState().setValue(net.minecraft.world.level.block.BeetrootBlock.AGE, 2 + Math.floorMod(x + z, 2))
                        : crop.defaultBlockState().setValue(net.minecraft.world.level.block.CropBlock.AGE, 4 + Math.floorMod(x * z, 4)));
            }
        b.set(11, 0, 24, Blocks.COMPOSTER);
        b.set(11, 0, 26, Blocks.BEE_NEST);
        b.set(16, 0, 22, Blocks.WATER_CAULDRON.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3));
    }

    /**
     * Elder Rowan's house: the village's oldest home, stone-founded, lined with books and maps.
     * Footprint x -16..-8, z -6..1, door on the east facing the square.
     */
    static void rowanHouse(Build b) {
        Room r = cottage(b, "rowan_house", -16, -6, -8, 1, Direction.EAST, Style.ELDER, List.of("elder_rowan"));
        // Library wall: bookshelves along the back, a lectern and a map table.
        for (int u = 2; u < r.width - 1; u++) {
            BlockPos p = r.rel(u, 0, r.depth - 1);
            b.fill(p.getX(), 0, p.getZ(), p.getX(), 2, p.getZ(), Blocks.BOOKSHELF);
        }
        BlockPos lec = r.rel(r.width - 2, 0, r.depth - 3);
        b.set(lec.getX(), 0, lec.getZ(), Blocks.LECTERN.defaultBlockState().setValue(LecternBlock.FACING, r.inward().getOpposite()));
        BlockPos map = r.rel(1, 0, 3);
        b.set(map.getX(), 0, map.getZ(), Blocks.CARTOGRAPHY_TABLE);
        b.set(map.getX(), 1, map.getZ(), Blocks.POTTED_FERN);
        b.point("work:elder_rowan", lec.getX(), 0, lec.getZ() + 1);
        b.protect(-16, 0, -6, -8, 9, 1);
    }

    /** A half-built house on the west side: frame, scaffolding and stacked timber (a construction space). */
    static void constructionSite(Build b, int x0, int z0, int x1, int z1) {
        b.fill(x0, -1, z0, x1, -1, z1, Blocks.COBBLESTONE);
        for (int[] c : new int[][] {{x0, z0}, {x1, z0}, {x0, z1}, {x1, z1}}) b.fill(c[0], 0, c[1], c[0], 3, c[1], Blocks.STRIPPED_OAK_LOG);
        b.fill(x0, 4, z0, x1, 4, z0, Blocks.STRIPPED_OAK_LOG);
        b.fill(x0, 4, z0, x0, 4, z1, Blocks.STRIPPED_OAK_LOG);
        b.fill(x0 + 1, 0, z0, x1 - 1, 1, z0, Blocks.OAK_PLANKS);
        b.fill(x0, 0, z0 + 1, x0, 0, z1 - 1, Blocks.OAK_PLANKS);
        for (int y = 0; y <= 4; y++) b.set(x1 + 1, y, z1 + 1, Blocks.SCAFFOLDING);
        b.fill(x1 + 1, 0, z1 - 1, x1 + 2, 1, z1 - 1, Blocks.OAK_PLANKS);
        b.fill(x1 + 1, 0, z1 - 2, x1 + 1, 0, z1 - 2, Blocks.OAK_LOG);
        b.set(x1 + 2, 0, z1 - 2, Blocks.CRAFTING_TABLE);
        b.container(x0 + 2, 0, z1, Build.facing(Blocks.CHEST, Direction.NORTH),
                new ItemStack(Items.OAK_PLANKS, 32), new ItemStack(Items.OAK_LOG, 16), new ItemStack(Items.COBBLESTONE, 32),
                new ItemStack(Items.GLASS_PANE, 8), new ItemStack(Items.STONE_AXE), new ItemStack(Items.LADDER, 8));
        b.sign(x0 + 3, 1, z1 + 1, Direction.SOUTH, "New home for", "the Harlow family", "-- volunteers", "welcome! --");
        b.point("work:construction", x0 + 3, 0, z1 + 2);
    }
}
