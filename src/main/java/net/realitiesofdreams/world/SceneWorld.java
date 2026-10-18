package net.realitiesofdreams.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.story.scene.SceneDef;

/**
 * Builds scene locations block by block, so the mod needs no pre-made structure files.
 * Coordinates are relative to the scene anchor; y = 0 is the first block above the ground.
 */
public final class SceneWorld {
    private static final int FLAGS = Block.UPDATE_CLIENTS;

    private final ServerLevel level;
    private final BlockPos anchor;

    private SceneWorld(ServerLevel level, BlockPos anchor) {
        this.level = level;
        this.anchor = anchor;
    }

    /**
     * Builds the scene's site once per world. Scenes that share a site (Firstbrook hosts Scenes
     * 001 to 005) share one build.
     */
    public static void ensureBuilt(ServerLevel level, SceneDef scene) {
        SceneWorldData data = SceneWorldData.get(level);
        String key = "built:site:" + scene.site();
        if (data.isDone(key)) return;
        long start = System.currentTimeMillis();
        SceneWorld w = new SceneWorld(level, ModDimensions.anchor(scene));
        if (scene.site().equals("firstbrook")) w.buildFirstbrook();
        else RealitiesOfDreams.LOGGER.warn("No builder for site {} (scene {})", scene.site(), scene.id());
        data.markDone(key);
        RealitiesOfDreams.LOGGER.info("Built site {} in {} ms", scene.site(), System.currentTimeMillis() - start);
    }

    /** Permanent changes to the world caused by story events. None are defined for the current scenes. */
    public static void applyChange(ServerLevel level, SceneDef scene, String change) {
        SceneWorldData data = SceneWorldData.get(level);
        if (data.isDone("change:" + change)) return;
        RealitiesOfDreams.LOGGER.warn("Unknown world change {}", change);
        data.markDone("change:" + change);
    }

    // ------------------------------------------------------------------ helpers

    private void set(int x, int y, int z, BlockState s) {
        level.setBlock(anchor.offset(x, y, z), s, FLAGS);
    }

    private void set(int x, int y, int z, Block b) {
        set(x, y, z, b.defaultBlockState());
    }

    private BlockState get(int x, int y, int z) {
        return level.getBlockState(anchor.offset(x, y, z));
    }

    private void fill(int x0, int y0, int z0, int x1, int y1, int z1, BlockState s) {
        for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++)
            for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++)
                for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) set(x, y, z, s);
    }

    private void fill(int x0, int y0, int z0, int x1, int y1, int z1, Block b) {
        fill(x0, y0, z0, x1, y1, z1, b.defaultBlockState());
    }

    private void door(int x, int z, Direction facing) {
        set(x, 0, z, Blocks.SPRUCE_DOOR.defaultBlockState().setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        set(x, 1, z, Blocks.SPRUCE_DOOR.defaultBlockState().setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }

    private BlockState slab(Block b) {
        return b.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
    }

    // ------------------------------------------------------------------ Firstbrook (MC-001)

    /**
     * Firstbrook, laid out for Scenes 001 to 005: Steve's house on the east lane, the square and
     * well, the farm, Orin's workshop, Elder Rowan's house, the market, the north wall and gate with
     * a watchtower, the newly repaired eastern bridge over the river, and the northern hills with
     * the ruin on the ridge.
     */
    private void buildFirstbrook() {
        RandomSource rand = RandomSource.create(1L);
        // Clear the village volume and lay fresh ground, so the builder also works outside a flat world.
        fill(-44, 0, -44, 44, 10, 44, Blocks.AIR);
        fill(-44, -1, -44, 44, -1, 44, Blocks.GRASS_BLOCK);
        fill(-44, -3, -44, 44, -2, 44, Blocks.DIRT);

        northernHills(rand);
        river();

        // Paths: the east lane past Steve's house to the bridge, up to the square, and north to the gate.
        for (int x = 3; x <= 36; x++) for (int z = 11; z <= 12; z++) if (x < 29 || x > 35) set(x, -1, z, Blocks.DIRT_PATH);
        for (int z = 4; z <= 12; z++) for (int x = 3; x <= 4; x++) set(x, -1, z, Blocks.DIRT_PATH);
        set(16, -1, 13, Blocks.DIRT_PATH);
        for (int z = -6; z >= -22; z--) for (int x = -1; x <= 1; x++) set(x, -1, z, Blocks.DIRT_PATH);
        for (int x = -6; x >= -7; x--) for (int z = -3; z <= -2; z++) set(x, -1, z, Blocks.DIRT_PATH);

        // Village square with the well and a hanging lantern on a post.
        for (int x = -6; x <= 6; x++)
            for (int z = -6; z <= 6; z++)
                if (x * x + z * z <= 30) set(x, -1, z, rand.nextInt(4) == 0 ? Blocks.MOSSY_COBBLESTONE : Blocks.COBBLESTONE);
        well();
        fill(4, 0, -4, 4, 2, -4, Blocks.OAK_FENCE);
        set(4, 3, -4, Blocks.OAK_FENCE);
        set(3, 3, -4, Blocks.OAK_FENCE);
        set(3, 2, -4, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        set(-4, 0, 4, Blocks.SPRUCE_STAIRS);

        steveHouse();
        house(-16, -6, -8, 1, Direction.EAST, true);     // Elder Rowan's house
        house(-14, 15, -8, 21, Direction.NORTH, false);   // south-west cottage
        house(20, -12, 26, -6, Direction.WEST, false);    // cottage beside the workshop
        house(-28, -14, -21, -8, Direction.EAST, false);  // west cottage
        house(22, 15, 28, 21, Direction.NORTH, false);    // Elara's family home, further down the lane
        forge(8, -11);                                    // Orin's workshop
        market(-14, 6);
        farm(8, 3, 15, 9, rand);
        farm(20, 3, 27, 9, rand);
        northWall();
        watchtower(-9, -20);
        trail(rand);

        // Trees, hay and flowers.
        int[][] trees = {{-24, 18}, {-30, -2}, {-34, 26}, {-20, 30}, {12, 30}, {30, 30}, {-36, -30}, {24, -30}, {14, -34},
                {-16, -34}, {40, 2}, {40, -20}, {38, 24}, {-40, 10}, {6, 24}, {-4, 28}};
        for (int[] t : trees) tree(t[0], t[1], 5 + Math.floorMod(t[0] * 31 + t[1], 2));
        for (int[] h : new int[][] {{17, 4}, {17, 5}, {18, 4}, {6, 8}}) set(h[0], 0, h[1], Blocks.HAY_BLOCK);
        Block[] flowers = {Blocks.POPPY, Blocks.DANDELION, Blocks.CORNFLOWER, Blocks.OXEYE_DAISY, Blocks.SHORT_GRASS, Blocks.SHORT_GRASS};
        for (int i = 0; i < 520; i++) {
            int x = rand.nextInt(88) - 44, z = rand.nextInt(88) - 44;
            if (get(x, -1, z).is(Blocks.GRASS_BLOCK) && get(x, 0, z).isAir()) set(x, 0, z, flowers[rand.nextInt(flowers.length)]);
        }
    }

    /**
     * Steve's modest house: crafting table, furnace, armour stand, tools, stored resources and
     * trophies from ordinary adventures. Outer footprint x 12..20, z 14..21; the door faces the lane.
     */
    private void steveHouse() {
        int x0 = 12, z0 = 14, x1 = 20, z1 = 21;
        fill(x0, -1, z0, x1, -1, z1, Blocks.SPRUCE_PLANKS);
        fill(x0 + 1, 0, z0 + 1, x1 - 1, 3, z1 - 1, Blocks.AIR);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edgeX = x == x0 || x == x1, edgeZ = z == z0 || z == z1;
                if (!edgeX && !edgeZ) continue;
                boolean corner = edgeX && edgeZ;
                for (int y = 0; y <= 3; y++) set(x, y, z, corner ? Blocks.SPRUCE_LOG : y == 3 ? Blocks.STRIPPED_SPRUCE_LOG : Blocks.OAK_PLANKS);
            }
        for (int[] w : new int[][] {{13, 14}, {14, 14}, {18, 14}, {19, 14}, {20, 16}, {20, 17}, {12, 17}, {12, 18}, {18, 21}}) set(w[0], 1, w[1], Blocks.GLASS_PANE);
        for (int i = 0; i < 3; i++) {
            fill(x0 - 1 + i, 4 + i, z0 - 1 + i, x1 + 1 - i, 4 + i, z1 + 1 - i, i == 2 ? Blocks.SPRUCE_PLANKS.defaultBlockState() : slab(Blocks.SPRUCE_SLAB));
            if (i < 2) fill(x0 + i, 4 + i, z0 + i, x1 - i, 4 + i, z1 - i, Blocks.SPRUCE_PLANKS);
        }
        door(16, z0, Direction.NORTH);
        set(17, 2, z0 - 1, Blocks.LANTERN);

        // Bed under the north window, where the morning light falls.
        set(19, 0, 15, Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH).setValue(BedBlock.PART, BedPart.HEAD));
        set(19, 0, 16, Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH).setValue(BedBlock.PART, BedPart.FOOT));
        set(13, 0, 15, Blocks.CRAFTING_TABLE);
        set(13, 0, 16, Blocks.FURNACE.defaultBlockState().setValue(net.minecraft.world.level.block.AbstractFurnaceBlock.FACING, Direction.EAST));
        set(13, 0, 19, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.EAST));
        set(13, 0, 20, Blocks.BARREL);
        set(14, 0, 20, Blocks.BARREL);
        set(13, 1, 20, Blocks.POTTED_DANDELION);
        for (int x = 15; x <= 17; x++) for (int z = 17; z <= 18; z++) set(x, 0, z, Blocks.BROWN_CARPET);
        set(16, 3, 17, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        set(19, 0, 18, Blocks.SPRUCE_STAIRS);

        // Breakfast and basic gear in the storage chest.
        if (level.getBlockEntity(anchor.offset(13, 0, 19)) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(Items.BREAD, 3));
            chest.setItem(1, new ItemStack(Items.APPLE, 2));
            chest.setItem(2, new ItemStack(Items.COOKED_SALMON, 1));
            chest.setItem(9, new ItemStack(Items.LEATHER_HELMET));
            chest.setItem(10, new ItemStack(Items.LEATHER_LEGGINGS));
            chest.setItem(11, new ItemStack(Items.WOODEN_SWORD));
            chest.setItem(18, new ItemStack(Items.OAK_PLANKS, 16));
            chest.setItem(19, new ItemStack(Items.COBBLESTONE, 16));
            chest.setItem(20, new ItemStack(Items.COAL, 4));
        }
        if (level.getBlockEntity(anchor.offset(13, 0, 20)) instanceof BarrelBlockEntity barrel) {
            barrel.setItem(0, new ItemStack(Items.WHEAT, 12));
            barrel.setItem(1, new ItemStack(Items.STICK, 8));
        }

        // Armour stand with his everyday leather armour.
        ArmorStand stand = EntityType.ARMOR_STAND.create(level);
        if (stand != null) {
            BlockPos p = anchor.offset(19, 0, 20);
            stand.moveTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, 90, 0);
            stand.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
            stand.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
            level.addFreshEntity(stand);
        }
        // Trophies from ordinary adventures on the south wall.
        itemFrame(15, 1, 20, new ItemStack(Items.IRON_SWORD));
        itemFrame(16, 2, 20, new ItemStack(Items.FISHING_ROD));
        itemFrame(17, 1, 20, new ItemStack(Items.BOW));
    }

    private void itemFrame(int x, int y, int z, ItemStack item) {
        ItemFrame frame = new ItemFrame(level, anchor.offset(x, y, z), Direction.NORTH);
        frame.setItem(item, false);
        level.addFreshEntity(frame);
    }

    private void farm(int x0, int z0, int x1, int z1, RandomSource rand) {
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
                Block crop = (x / 3) % 3 == 0 ? Blocks.CARROTS : Blocks.WHEAT;
                BlockState s = crop.defaultBlockState();
                if (crop == Blocks.WHEAT) s = s.setValue(CropBlock.AGE, 4 + rand.nextInt(4));
                else s = s.setValue(CropBlock.AGE, 3 + rand.nextInt(5));
                set(x, 0, z, s);
            }
        set(x0 - 1, 0, z1 + 1, Blocks.COMPOSTER);
    }

    private void watchtower(int x0, int z0) {
        for (int[] c : new int[][] {{0, 0}, {2, 0}, {0, 2}, {2, 2}}) fill(x0 + c[0], 0, z0 + c[1], x0 + c[0], 6, z0 + c[1], Blocks.SPRUCE_LOG);
        fill(x0 + 1, 0, z0 + 1, x0 + 1, 6, z0 + 1, Blocks.STRIPPED_SPRUCE_LOG);
        fill(x0, 7, z0, x0 + 2, 7, z0 + 2, Blocks.SPRUCE_PLANKS);
        for (int x = x0; x <= x0 + 2; x++) for (int z = z0; z <= z0 + 2; z++)
            if (x != x0 + 1 || z != z0 + 1) set(x, 8, z, Blocks.SPRUCE_FENCE);
        fill(x0 + 1, 0, z0 + 2, x0 + 1, 7, z0 + 2, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH));
        set(x0 + 1, 8, z0 + 2, Blocks.AIR);
        set(x0 + 1, 8, z0 + 1, Blocks.LANTERN);
    }

    /** The river east of the village and the eastern bridge Steve repaired yesterday. */
    private void river() {
        for (int x = 30; x <= 34; x++)
            for (int z = -43; z <= 43; z++) {
                set(x, -1, z, Blocks.WATER);
                set(x, -2, z, Blocks.WATER);
                set(x, -3, z, Blocks.SAND);
            }
        for (int x = 29; x <= 35; x++) {
            set(x, -1, -44, Blocks.SAND);
            set(x, -1, 44, Blocks.SAND);
        }
        for (int z = -44; z <= 44; z++) {
            set(29, -1, z, Blocks.SAND);
            set(35, -1, z, Blocks.SAND);
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
        fill(36, 0, 14, 37, 0, 14, Blocks.OAK_PLANKS); // spare timber left from the repair
        set(36, 1, 14, Blocks.OAK_SLAB);
    }

    /**
     * The northern hills rise beyond the village wall toward a ridge; an old ruin stands on the
     * ridge, visible from the square.
     */
    private void northernHills(RandomSource rand) {
        for (int x = -64; x <= 64; x++)
            for (int z = -46; z >= -140; z--) {
                int h = hillHeight(x, z);
                if (h <= 0) continue;
                for (int y = 0; y < h; y++) set(x, y, z, y < h - 3 ? Blocks.STONE : Blocks.DIRT);
                set(x, h - 1, z, Blocks.GRASS_BLOCK);
                if (rand.nextInt(60) == 0 && h > 4 && Math.abs(x - 10) > 6) spruce(x, h, z, 6 + rand.nextInt(3));
            }
        // The ruin on the ridge: broken pillars and a fallen lintel.
        int rz = -112, base = hillHeight(10, rz);
        int[][] pillars = {{4, 7}, {8, 9}, {12, 5}, {16, 8}, {6, 3}, {14, 2}};
        for (int[] pl : pillars) {
            int px = pl[0], top = base + pl[1];
            for (int y = hillHeight(px, rz); y < top; y++) set(px, y, rz, (y % 3 == 0) ? Blocks.CRACKED_STONE_BRICKS : Blocks.MOSSY_STONE_BRICKS);
        }
        fill(7, base + 9, rz, 9, base + 9, rz, Blocks.STONE_BRICK_SLAB.defaultBlockState());
        fill(10, hillHeight(10, rz + 2), rz + 2, 12, hillHeight(10, rz + 2), rz + 2, Blocks.MOSSY_COBBLESTONE.defaultBlockState());
    }

    public static int hillHeight(int x, int z) {
        if (z > -46) return 0;
        double rise = Math.min(26, (-46 - z) * 0.36);
        double wave = 3 * Math.sin(x / 9.0) + 2 * Math.cos(z / 7.0) + 1.5 * Math.sin((x + z) / 13.0);
        return (int) Math.max(0, Math.round(rise + wave * Math.min(1, (-46 - z) / 12.0)));
    }

    private void spruce(int x, int y, int z, int height) {
        for (int i = 0; i < height; i++) set(x, y + i, z, Blocks.SPRUCE_LOG);
        for (int dy = 2; dy <= height; dy++) {
            int r = dy >= height - 1 ? 0 : dy % 2 == 0 ? 2 : 1;
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++)
                    if ((dx != 0 || dz != 0) && Math.abs(dx) + Math.abs(dz) <= r + 1 && get(x + dx, y + dy, z + dz).isAir())
                        set(x + dx, y + dy, z + dz, Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(net.minecraft.world.level.block.LeavesBlock.PERSISTENT, true));
        }
        set(x, y + height, z, Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(net.minecraft.world.level.block.LeavesBlock.PERSISTENT, true));
    }

    private void well() {
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
        fill(-1, 3, -1, 1, 3, 1, slab(Blocks.SPRUCE_SLAB));
        set(0, 2, 0, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
    }

    /** A timber cottage; (x0,z0)-(x1,z1) is the outer footprint. */
    private void house(int x0, int z0, int x1, int z1, Direction doorSide, boolean elder) {
        fill(x0, -1, z0, x1, -1, z1, Blocks.SPRUCE_PLANKS);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edgeX = x == x0 || x == x1, edgeZ = z == z0 || z == z1;
                if (!edgeX && !edgeZ) continue;
                Block wall = edgeX && edgeZ ? Blocks.SPRUCE_LOG : Blocks.OAK_PLANKS;
                for (int y = 0; y <= 3; y++) set(x, y, z, y == 3 && !(edgeX && edgeZ) ? Blocks.STRIPPED_SPRUCE_LOG : wall);
                if (!(edgeX && edgeZ) && (x + z) % 3 == 0) set(x, 1, z, Blocks.GLASS_PANE);
            }
        // Stepped roof.
        for (int i = 0; i < 3; i++) {
            fill(x0 - 1 + i, 4 + i, z0 - 1 + i, x1 + 1 - i, 4 + i, z1 + 1 - i, i == 2 ? Blocks.SPRUCE_PLANKS.defaultBlockState() : slab(Blocks.SPRUCE_SLAB));
            if (i < 2) fill(x0 + i, 4 + i, z0 + i, x1 - i, 4 + i, z1 - i, Blocks.SPRUCE_PLANKS);
        }
        int cx = (x0 + x1) / 2, cz = (z0 + z1) / 2;
        switch (doorSide) {
            case EAST -> { door(x1, cz, Direction.EAST); set(x1 + 1, 2, cz - 1, Blocks.LANTERN); set(x1 + 1, -1, cz, Blocks.DIRT_PATH); }
            case WEST -> { door(x0, cz, Direction.WEST); set(x0 - 1, 2, cz + 1, Blocks.LANTERN); }
            case NORTH -> { door(cx, z0, Direction.NORTH); set(cx + 1, 2, z0 - 1, Blocks.LANTERN); }
            default -> door(cx, z1, Direction.SOUTH);
        }
        // Furnishings.
        set(x0 + 1, 0, z0 + 1, Blocks.CRAFTING_TABLE);
        set(x1 - 1, 0, z0 + 1, Blocks.BARREL);
        set(x0 + 1, 2, z0 + 1, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        if (elder) {
            fill(x0 + 1, 0, z1 - 1, x0 + 1, 2, z1 - 1, Blocks.BOOKSHELF);
            fill(x0 + 2, 0, z1 - 1, x0 + 2, 1, z1 - 1, Blocks.BOOKSHELF);
            set(x0 + 3, 0, z1 - 1, Blocks.LECTERN.defaultBlockState().setValue(LecternBlock.FACING, Direction.NORTH));
            set(x0 + 1, 0, z0 + 3, Blocks.POTTED_FERN);
        }
    }

    private void forge(int x0, int z0) {
        fill(x0, -1, z0, x0 + 6, -1, z0 + 6, Blocks.STONE_BRICKS);
        for (int[] c : new int[][] {{0, 0}, {6, 0}, {0, 6}, {6, 6}}) fill(x0 + c[0], 0, z0 + c[1], x0 + c[0], 3, z0 + c[1], Blocks.STRIPPED_OAK_LOG);
        fill(x0, 4, z0, x0 + 6, 4, z0 + 6, slab(Blocks.COBBLESTONE_SLAB));
        fill(x0 + 5, 0, z0 + 1, x0 + 5, 0, z0 + 5, Blocks.COBBLESTONE);
        set(x0 + 5, 1, z0 + 2, Blocks.BLAST_FURNACE.defaultBlockState().setValue(net.minecraft.world.level.block.AbstractFurnaceBlock.FACING, Direction.WEST));
        set(x0 + 5, 1, z0 + 3, Blocks.FURNACE.defaultBlockState().setValue(net.minecraft.world.level.block.AbstractFurnaceBlock.FACING, Direction.WEST));
        set(x0 + 5, 1, z0 + 4, Blocks.SMOKER.defaultBlockState().setValue(net.minecraft.world.level.block.AbstractFurnaceBlock.FACING, Direction.WEST));
        set(x0 + 3, 0, z0 + 2, Blocks.ANVIL);
        set(x0 + 2, 0, z0 + 5, Blocks.SMITHING_TABLE);
        set(x0 + 1, 0, z0 + 1, Blocks.GRINDSTONE);
        set(x0 + 3, 3, z0 + 3, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
    }

    private void market(int x0, int z0) {
        Block[] roofs = {Blocks.RED_WOOL, Blocks.YELLOW_WOOL, Blocks.LIGHT_BLUE_WOOL};
        for (int s = 0; s < 3; s++) {
            int sx = x0 + s * 4;
            for (int[] c : new int[][] {{0, 0}, {2, 0}, {0, 2}, {2, 2}}) fill(sx + c[0], 0, z0 + c[1], sx + c[0], 2, z0 + c[1], Blocks.OAK_FENCE);
            fill(sx, 3, z0, sx + 2, 3, z0 + 2, roofs[s]);
            set(sx + 1, 0, z0, Blocks.BARREL);
        }
        set(x0 + 1, 0, z0 + 3, Blocks.HAY_BLOCK);
        set(x0 + 5, 0, z0 + 3, Blocks.COMPOSTER);
        set(x0 + 9, 0, z0 + 3, Blocks.PUMPKIN);
        set(x0 + 10, 0, z0 + 3, Blocks.MELON);
    }

    private void northWall() {
        for (int x = -30; x <= 30; x++) {
            if (Math.abs(x) <= 2) continue;
            fill(x, 0, -22, x, Math.abs(x) == 3 ? 4 : 2 + (Math.floorMod(x, 3) == 0 ? 1 : 0), -22, Blocks.SPRUCE_LOG);
        }
        fill(-3, 5, -22, 3, 5, -22, Blocks.SPRUCE_PLANKS);
        set(-2, 4, -22, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        set(2, 4, -22, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        set(4, 0, -20, Blocks.BARREL);
        set(5, 0, -20, Blocks.CHEST);
    }

    private void trail(RandomSource rand) {
        Block[] surface = {Blocks.DIRT_PATH, Blocks.COARSE_DIRT, Blocks.GRAVEL, Blocks.DIRT_PATH};
        for (int z = -23; z >= -44; z--) {
            int cx = (int) Math.round(Math.sin(z / 4.0) * 2);
            for (int x = cx - 1; x <= cx + 1; x++) set(x, -1, z, surface[rand.nextInt(surface.length)]);
            if (z % 6 == 0) {
                int px = cx + (z % 12 == 0 ? 3 : -3);
                fill(px, 0, z, px, 1, z, Blocks.OAK_FENCE); // old lantern posts, long unlit
            }
        }
    }

    private void tree(int x, int z, int height) {
        for (int y = 0; y < height; y++) set(x, y, z, Blocks.OAK_LOG);
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                for (int dy = height - 2; dy <= height + 1; dy++) {
                    int r = dy >= height ? 1 : 2;
                    if (Math.abs(dx) > r || Math.abs(dz) > r || (Math.abs(dx) == r && Math.abs(dz) == r && r == 2)) continue;
                    if (get(x + dx, dy, z + dz).isAir()) set(x + dx, dy, z + dz, Blocks.OAK_LEAVES.defaultBlockState()
                            .setValue(net.minecraft.world.level.block.LeavesBlock.PERSISTENT, true));
                }
    }

}
