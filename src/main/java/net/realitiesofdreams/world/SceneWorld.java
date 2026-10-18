package net.realitiesofdreams.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
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
    public static final String RIFT_SCAR = "mc001_rift_scar";
    private static final int FLAGS = Block.UPDATE_CLIENTS;

    private final ServerLevel level;
    private final BlockPos anchor;

    private SceneWorld(ServerLevel level, BlockPos anchor) {
        this.level = level;
        this.anchor = anchor;
    }

    public static void ensureBuilt(ServerLevel level, SceneDef scene) {
        SceneWorldData data = SceneWorldData.get(level);
        String key = "built:" + scene.id();
        if (data.isDone(key)) return;
        long start = System.currentTimeMillis();
        SceneWorld w = new SceneWorld(level, ModDimensions.anchor(scene));
        if (scene.id().equals("mc001_s001")) w.buildMc001Village(scene);
        else RealitiesOfDreams.LOGGER.warn("No builder for scene {}", scene.id());
        data.markDone(key);
        RealitiesOfDreams.LOGGER.info("Built scene {} in {} ms", scene.id(), System.currentTimeMillis() - start);
    }

    /** Permanent changes to the world caused by story events. */
    public static void applyChange(ServerLevel level, SceneDef scene, String change) {
        SceneWorldData data = SceneWorldData.get(level);
        if (data.isDone("change:" + change)) return;
        SceneWorld w = new SceneWorld(level, ModDimensions.anchor(scene));
        if (RIFT_SCAR.equals(change)) w.riftScar();
        else RealitiesOfDreams.LOGGER.warn("Unknown world change {}", change);
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

    // ------------------------------------------------------------------ MC-001 village

    private void buildMc001Village(SceneDef scene) {
        RandomSource rand = RandomSource.create(1L);
        // Clear the build volume and lay fresh grass, so the builder also works outside a flat world.
        fill(-40, 0, -66, 40, 14, 42, Blocks.AIR);
        fill(-40, -1, -66, 40, -1, 42, Blocks.GRASS_BLOCK);
        fill(-40, -3, -66, 40, -2, 42, Blocks.DIRT);

        // Roads: south road through the square to the north gate, and an east-west lane.
        for (int z = 40; z >= -22; z--) for (int x = -1; x <= 1; x++) set(x, -1, z, Blocks.DIRT_PATH);
        for (int x = -24; x <= 24; x++) for (int z = -1; z <= 1; z++) set(x, -1, z, Blocks.DIRT_PATH);

        // Village square with a well.
        for (int x = -6; x <= 6; x++)
            for (int z = -6; z <= 6; z++)
                if (x * x + z * z <= 30) set(x, -1, z, rand.nextInt(4) == 0 ? Blocks.MOSSY_COBBLESTONE : Blocks.COBBLESTONE);
        well();

        house(-15, -6, -7, 2, Direction.EAST, true);   // Elder Rowan's house
        house(7, -13, 12, -7, Direction.WEST, false);  // neighbour's cottage
        house(-14, 15, -8, 21, Direction.NORTH, false); // southern cottage
        forge(8, 4);
        market(-14, 8);
        northWall();
        trail(rand);
        shrine();

        // Trees and flowers around the edges.
        int[][] trees = {{-24, 18}, {-28, -6}, {22, 16}, {26, -10}, {-20, -30}, {18, -32}, {-14, -44}, {13, -58}, {-30, 30}, {30, 32}};
        for (int[] t : trees) tree(t[0], t[1], 5 + Math.floorMod(t[0] * 31 + t[1], 2));
        Block[] flowers = {Blocks.POPPY, Blocks.DANDELION, Blocks.CORNFLOWER, Blocks.OXEYE_DAISY, Blocks.SHORT_GRASS, Blocks.SHORT_GRASS};
        for (int i = 0; i < 420; i++) {
            int x = rand.nextInt(80) - 40, z = rand.nextInt(108) - 66;
            if (get(x, -1, z).is(Blocks.GRASS_BLOCK) && get(x, 0, z).isAir()) set(x, 0, z, flowers[rand.nextInt(flowers.length)]);
        }
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

    private void shrine() {
        int cz = -50;
        // Mound: one block high with a half-step rim.
        for (int x = -9; x <= 9; x++)
            for (int z = cz - 9; z <= cz + 9; z++) {
                double d = Math.sqrt(x * x + (z - cz) * (z - cz));
                if (d <= 7.5) set(x, 0, z, Blocks.GRASS_BLOCK);
                else if (d <= 8.5) set(x, 0, z, slab(Blocks.MOSSY_COBBLESTONE_SLAB));
            }
        fill(-1, 0, cz - 1, 1, 0, cz + 1, Blocks.CHISELED_STONE_BRICKS);
        set(0, 1, cz, Blocks.LODESTONE);
        // Ring of ruined pillars.
        int[] heights = {4, 2, 3, 1, 4, 2, 3, 2};
        for (int i = 0; i < 8; i++) {
            double a = Math.PI * 2 * i / 8;
            int px = (int) Math.round(Math.cos(a) * 6), pz = cz + (int) Math.round(Math.sin(a) * 6);
            for (int y = 1; y <= heights[i]; y++) set(px, y, pz, (y + i) % 3 == 0 ? Blocks.CRACKED_STONE_BRICKS : Blocks.MOSSY_STONE_BRICKS);
            if (i % 3 == 0) set(px, heights[i] + 1, pz, Blocks.SMALL_AMETHYST_BUD);
        }
        set(3, 1, -47, Blocks.LECTERN.defaultBlockState().setValue(LecternBlock.FACING, Direction.NORTH));
        // Footprints walking out from the stone and stopping mid-stride.
        for (int[] f : new int[][] {{-2, -50}, {-3, -51}, {-4, -51}, {-5, -52}}) set(f[0], 0, f[1], Blocks.SOUL_SOIL);
        set(5, 1, -53, Blocks.MOSSY_COBBLESTONE);
        set(-5, 1, -47, Blocks.MOSSY_COBBLESTONE_SLAB);
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

    /** The tremor leaves a lasting scar: cracked ground, glowing crystal and fallen stones. */
    private void riftScar() {
        int cz = -50;
        for (int[] d : new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}) {
            for (int i = 1; i <= 4; i++) set(d[0] * i, 0, cz + d[1] * i, i % 2 == 0 ? Blocks.CRYING_OBSIDIAN : Blocks.OBSIDIAN);
        }
        set(1, 1, cz, Blocks.AMETHYST_CLUSTER);
        set(-1, 1, cz, Blocks.AMETHYST_CLUSTER);
        set(0, 1, cz + 1, Blocks.LARGE_AMETHYST_BUD);
        set(0, 2, cz, Blocks.AIR);
        // Two pillars lose their tops.
        fill(6, 3, cz, 6, 5, cz, Blocks.AIR);
        set(7, 1, cz + 1, Blocks.MOSSY_COBBLESTONE);
        set(-4, 2, cz - 4, Blocks.AIR);
        set(-5, 1, cz - 5, Blocks.CRACKED_STONE_BRICKS);
    }
}
