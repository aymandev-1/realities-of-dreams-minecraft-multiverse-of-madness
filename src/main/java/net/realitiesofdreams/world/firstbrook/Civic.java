package net.realitiesofdreams.world.firstbrook;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.realitiesofdreams.world.build.Build;

/** Firstbrook's shared places: the square and well, market, storehouse, meeting pavilion, north gate and watchtower. */
final class Civic {
    private Civic() {}

    /** The village square with the well, lantern post, notice board and benches. Centred on (0, 0). */
    static void square(Build b, RandomSource rand) {
        for (int x = -6; x <= 6; x++)
            for (int z = -6; z <= 6; z++)
                if (x * x + z * z <= 30) b.set(x, -1, z, rand.nextInt(4) == 0 ? Blocks.MOSSY_COBBLESTONE : rand.nextInt(5) == 0 ? Blocks.ANDESITE : Blocks.COBBLESTONE);
        // The well.
        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) {
                    b.fill(0, -6, 0, 0, -1, 0, Blocks.WATER);
                    b.set(0, -7, 0, Blocks.STONE);
                } else {
                    b.set(x, 0, z, Blocks.COBBLESTONE_WALL);
                }
            }
        for (int[] c : new int[][] {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}) b.fill(c[0], 1, c[1], c[0], 2, c[1], Blocks.OAK_FENCE);
        b.fill(-1, 3, -1, 1, 3, 1, Build.slab(Blocks.SPRUCE_SLAB));
        b.set(0, 2, 0, Build.hangingLantern());
        b.protect(-1, -6, -1, 1, 3, 1);

        // Lantern post on the north-east edge (the lantern that sways when the tremor comes).
        b.fill(4, 0, -4, 4, 2, -4, Blocks.OAK_FENCE);
        b.set(4, 3, -4, Blocks.OAK_FENCE);
        b.set(3, 3, -4, Blocks.OAK_FENCE);
        b.set(3, 2, -4, Build.hangingLantern());

        // Benches facing the well.
        b.set(-4, 0, 4, Build.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH));
        b.set(-5, 0, 3, Build.stairs(Blocks.SPRUCE_STAIRS, Direction.WEST));
        b.set(5, 0, 2, Build.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST));
        b.set(5, 0, 1, Build.stairs(Blocks.SPRUCE_STAIRS, Direction.EAST));

        // Notice board on the north-west edge of the square.
        b.fill(-5, 0, -5, -5, 2, -5, Blocks.SPRUCE_FENCE);
        b.fill(-3, 0, -5, -3, 2, -5, Blocks.SPRUCE_FENCE);
        b.fill(-5, 3, -5, -3, 3, -5, Build.slab(Blocks.SPRUCE_SLAB));
        b.fill(-4, 1, -5, -4, 2, -5, Blocks.SPRUCE_PLANKS);
        b.sign(-4, 2, -4, Direction.SOUTH, "NOTICE", "Volunteers needed", "at the west build", "-- Elder Rowan");
        b.sign(-4, 1, -4, Direction.SOUTH, "Market day", "every morning", "Fresh bread!", "");

        // Flower planters at the square's entrances.
        for (int[] p : new int[][] {{6, 3}, {6, -3}, {-6, 1}}) {
            b.set(p[0], 0, p[1], Blocks.COARSE_DIRT);
            b.set(p[0], 1, p[1], Blocks.POPPY);
        }
    }

    /** Three market stalls with goods, south-west of the square; vendors stand on their south side. */
    static void market(Build b, int x0, int z0) {
        Block[] roofs = {Blocks.RED_WOOL, Blocks.YELLOW_WOOL, Blocks.LIGHT_BLUE_WOOL};
        for (int s = 0; s < 3; s++) {
            int sx = x0 + s * 4;
            for (int[] c : new int[][] {{0, 0}, {2, 0}, {0, 2}, {2, 2}}) b.fill(sx + c[0], 0, z0 + c[1], sx + c[0], 2, z0 + c[1], Blocks.OAK_FENCE);
            b.fill(sx, 3, z0, sx + 2, 3, z0 + 2, roofs[s]);
            b.set(sx - 1, 3, z0 + 1, Build.slab(Blocks.SPRUCE_SLAB));
            b.set(sx + 3, 3, z0 + 1, Build.slab(Blocks.SPRUCE_SLAB));
            b.set(sx + 1, 2, z0 + 1, Build.hangingLantern());
            b.set(sx + 1, 0, z0, Blocks.BARREL);
        }
        // Goods on display.
        b.set(x0 + 1, 0, z0 + 1, Blocks.HAY_BLOCK);
        b.set(x0 + 1, 1, z0, Blocks.CAKE);
        b.set(x0 + 5, 0, z0 + 1, Blocks.COMPOSTER);
        b.set(x0 + 5, 1, z0, Blocks.POTTED_CORNFLOWER);
        b.set(x0 + 9, 0, z0 + 1, Blocks.PUMPKIN);
        b.set(x0 + 9, 1, z0, Blocks.MELON);
        b.set(x0 + 1, 0, z0 + 3, Blocks.HAY_BLOCK);
        b.set(x0 + 5, 0, z0 + 3, Blocks.COMPOSTER);
        b.set(x0 + 9, 0, z0 + 3, Blocks.PUMPKIN);
        b.set(x0 + 10, 0, z0 + 3, Blocks.MELON);
        b.sign(x0 + 4, 1, z0 - 1, Direction.NORTH, "", "Firstbrook", "Market", "");
    }

    /** The village storehouse: barrels and chests of grain, timber and tools for everyone. */
    static void storehouse(Build b, int x0, int z0, int x1, int z1, Direction door) {
        Homes.shell(b, x0, z0, x1, z1, door, Homes.Style.STONE);
        for (int x = x0 + 1; x <= x1 - 1; x++) {
            b.set(x, 0, z0 + 1, Blocks.BARREL);
            b.set(x, 1, z0 + 1, Blocks.BARREL);
            b.set(x, 0, z1 - 1, Blocks.HAY_BLOCK);
        }
        b.container(x1 - 1, 0, z0 + 2, Build.facing(Blocks.CHEST, Direction.WEST), new ItemStack(Items.WHEAT, 32), new ItemStack(Items.CARROT, 16),
                new ItemStack(Items.POTATO, 16), new ItemStack(Items.BREAD, 8));
        b.container(x0 + 1, 0, z0 + 2, Build.facing(Blocks.CHEST, Direction.EAST), new ItemStack(Items.OAK_PLANKS, 48), new ItemStack(Items.COBBLESTONE, 48),
                new ItemStack(Items.TORCH, 16), new ItemStack(Items.STONE_SHOVEL), new ItemStack(Items.STONE_AXE));
        b.set((x0 + x1) / 2, 3, (z0 + z1) / 2, Build.hangingLantern());
        b.sign((x0 + x1) / 2, 3, z0 - 1, Direction.NORTH, "", "Storehouse", "", "");
    }

    /** Elder Rowan's meeting pavilion: a roofed gathering place with benches, a map table and the village bell. */
    static void pavilion(Build b, int x0, int z0, int x1, int z1) {
        b.fill(x0, -1, z0, x1, -1, z1, Blocks.STONE_BRICKS);
        for (int[] c : new int[][] {{x0, z0}, {x1, z0}, {x0, z1}, {x1, z1}}) b.fill(c[0], 0, c[1], c[0], 3, c[1], Blocks.STRIPPED_SPRUCE_LOG);
        b.fill(x0, 4, z0, x1, 4, z1, Blocks.SPRUCE_PLANKS);
        b.gableRoof(x0, z0, x1, z1, 5, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
        int cx = (x0 + x1) / 2, cz = (z0 + z1) / 2;
        b.set(cx, 0, cz, Blocks.CARTOGRAPHY_TABLE);
        for (int x = x0 + 1; x <= x1 - 1; x++) {
            b.set(x, 0, z0 + 1, Build.stairs(Blocks.SPRUCE_STAIRS, Direction.NORTH));
            b.set(x, 0, z1 - 1, Build.stairs(Blocks.SPRUCE_STAIRS, Direction.SOUTH));
        }
        b.set(x0 + 1, 3, cz, Blocks.BELL.defaultBlockState().setValue(BellBlock.ATTACHMENT, BellAttachType.CEILING));
        b.set(cx, 3, cz, Build.hangingLantern());
        b.set(x1 - 1, 3, cz, Build.hangingLantern());
        b.sign(x1, 2, z1 + 1, Direction.SOUTH, "Meeting Hall", "All voices are", "heard here.", "");
        b.point("work:elder_rowan_hall", cx, 0, cz + 1);
    }

    /** The palisade along the north edge with its gate, gatehouse lanterns and a guard booth. */
    static void northWall(Build b) {
        for (int x = -30; x <= 30; x++) {
            if (Math.abs(x) <= 2) continue;
            b.fill(x, 0, -22, x, Math.abs(x) == 3 ? 5 : 2 + (Math.floorMod(x, 3) == 0 ? 1 : 0), -22, Blocks.SPRUCE_LOG);
        }
        b.fill(-3, 6, -22, 3, 6, -22, Blocks.SPRUCE_PLANKS);
        b.fill(-3, 7, -22, 3, 7, -22, Build.slab(Blocks.SPRUCE_SLAB));
        b.set(-2, 5, -22, Build.hangingLantern());
        b.set(2, 5, -22, Build.hangingLantern());
        b.set(4, 0, -20, Blocks.BARREL);
        b.set(5, 0, -20, Blocks.CHEST);
        b.sign(3, 3, -21, Direction.SOUTH, "North Gate", "Awakening Valley", "beyond. Travel", "in pairs.");
        b.protect(-3, 0, -22, 3, 7, -22);
    }

    /** A timber watchtower with a ladder, lookout platform and a small roof. */
    static void watchtower(Build b, int x0, int z0) {
        for (int[] c : new int[][] {{0, 0}, {2, 0}, {0, 2}, {2, 2}}) b.fill(x0 + c[0], 0, z0 + c[1], x0 + c[0], 9, z0 + c[1], Blocks.SPRUCE_LOG);
        b.fill(x0 + 1, 0, z0 + 1, x0 + 1, 6, z0 + 1, Blocks.STRIPPED_SPRUCE_LOG);
        b.fill(x0, 7, z0, x0 + 2, 7, z0 + 2, Blocks.SPRUCE_PLANKS);
        for (int x = x0; x <= x0 + 2; x++)
            for (int z = z0; z <= z0 + 2; z++)
                if ((x != x0 + 1 || z != z0 + 1) && (x == x0 + 1 || z == z0 + 1)) b.set(x, 8, z, Blocks.SPRUCE_FENCE);
        b.fill(x0 + 1, 0, z0 + 3, x0 + 1, 7, z0 + 3, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH));
        b.fill(x0 + 1, 0, z0 + 2, x0 + 1, 7, z0 + 2, Blocks.SPRUCE_PLANKS);
        b.set(x0 + 1, 7, z0 + 2, Blocks.AIR);
        b.set(x0 + 1, 8, z0 + 2, Blocks.AIR);
        b.fill(x0 - 1, 10, z0 - 1, x0 + 3, 10, z0 + 3, Build.slab(Blocks.SPRUCE_SLAB));
        b.fill(x0, 10, z0, x0 + 2, 10, z0 + 2, Blocks.SPRUCE_PLANKS);
        b.set(x0 + 1, 11, z0 + 1, Build.slab(Blocks.SPRUCE_SLAB));
        b.set(x0 + 1, 9, z0 + 1, Build.hangingLantern());
    }

    /** Signposts welcoming travellers at the bridge and the north trail. */
    static void signposts(Build b) {
        b.fill(37, 0, 10, 37, 1, 10, Blocks.SPRUCE_FENCE);
        b.sign(38, 1, 10, Direction.EAST, "Welcome to", "FIRSTBROOK", "Eldervale", "");
        b.fill(-3, 0, -24, -3, 1, -24, Blocks.SPRUCE_FENCE);
        b.sign(-3, 1, -25, Direction.NORTH, "Awakening", "Valley", "^ North", "");
    }
}
