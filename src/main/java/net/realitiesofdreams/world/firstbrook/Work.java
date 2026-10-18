package net.realitiesofdreams.world.firstbrook;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.realitiesofdreams.world.build.Build;

import java.util.List;

/** Where Firstbrook works: Orin's forge and workshop, Bram's guard station, the training yard, farms, barn and pens. */
final class Work {
    private Work() {}

    /** Orin's open-air forge (x0..x0+6, z0..z0+6) with furnaces, anvil, grindstone and smithing table. */
    static void forge(Build b, int x0, int z0) {
        b.fill(x0, -1, z0, x0 + 6, -1, z0 + 6, Blocks.STONE_BRICKS);
        for (int[] c : new int[][] {{0, 0}, {6, 0}, {0, 6}, {6, 6}}) b.fill(x0 + c[0], 0, z0 + c[1], x0 + c[0], 3, z0 + c[1], Blocks.STRIPPED_OAK_LOG);
        b.fill(x0, 4, z0, x0 + 6, 4, z0 + 6, Build.slab(Blocks.COBBLESTONE_SLAB));
        b.fill(x0 + 5, 0, z0 + 1, x0 + 5, 0, z0 + 5, Blocks.COBBLESTONE);
        b.set(x0 + 5, 1, z0 + 2, Build.facing(Blocks.BLAST_FURNACE, Direction.WEST).setValue(AbstractFurnaceBlock.LIT, true));
        b.set(x0 + 5, 1, z0 + 3, Build.facing(Blocks.FURNACE, Direction.WEST).setValue(AbstractFurnaceBlock.LIT, true));
        b.set(x0 + 5, 1, z0 + 4, Build.facing(Blocks.SMOKER, Direction.WEST));
        b.fill(x0 + 6, 0, z0 + 3, x0 + 6, 6, z0 + 3, Blocks.BRICKS); // forge chimney
        b.set(x0 + 6, 7, z0 + 3, Blocks.CAMPFIRE.defaultBlockState().setValue(net.minecraft.world.level.block.CampfireBlock.SIGNAL_FIRE, false));
        b.set(x0 + 3, 0, z0 + 2, Blocks.ANVIL);
        b.set(x0 + 2, 0, z0 + 5, Blocks.SMITHING_TABLE);
        b.set(x0 + 1, 0, z0 + 1, Blocks.GRINDSTONE);
        b.set(x0 + 1, 0, z0 + 5, Blocks.WATER_CAULDRON.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3));
        b.set(x0 + 3, 3, z0 + 3, Build.hangingLantern());
        b.point("work:orin", x0 + 3, 0, z0 + 3);
    }

    /** Orin's enclosed workshop behind the forge, where he also sleeps. */
    static void workshop(Build b, int x0, int z0, int x1, int z1) {
        Homes.shell(b, x0, z0, x1, z1, Direction.SOUTH, Homes.Style.BRICK);
        b.bed(x0 + 1, 0, z0 + 1, Direction.NORTH, Blocks.ORANGE_BED);
        b.point("bed:orin", x0 + 1, 0, z0 + 1);
        b.set(x0 + 2, 0, z0 + 1, Blocks.STONECUTTER);
        b.set(x0 + 3, 0, z0 + 1, Blocks.FLETCHING_TABLE);
        b.set(x1 - 1, 0, z0 + 1, Blocks.CHIPPED_ANVIL);
        b.container(x1 - 1, 0, z0 + 2, Build.facing(Blocks.CHEST, Direction.WEST), new ItemStack(Items.IRON_INGOT, 12), new ItemStack(Items.COAL, 24),
                new ItemStack(Items.IRON_NUGGET, 20), new ItemStack(Items.CHAIN, 4), new ItemStack(Items.SHEARS));
        b.container(x1 - 1, 0, z0 + 3, Blocks.BARREL.defaultBlockState(), new ItemStack(Items.STICK, 32), new ItemStack(Items.LEATHER, 6));
        b.set(x0 + 1, 0, z1 - 1, Blocks.CRAFTING_TABLE);
        b.set((x0 + x1) / 2, 3, (z0 + z1) / 2, Build.hangingLantern());
        b.itemFrame(x0 + 3, 2, z0 + 1, Direction.SOUTH, new ItemStack(Items.IRON_PICKAXE));
        b.itemFrame(x0 + 4, 2, z0 + 1, Direction.SOUTH, new ItemStack(Items.IRON_AXE));
        b.sign((x0 + x1) / 2 + 2, 2, z1 + 1, Direction.SOUTH, "ORIN'S", "Smithy & Workshop", "Repairs done", "while you wait");
    }

    /** Bram's guard station east of the bridge: bunks for the watch, weapons, and an armour stand. */
    static void guardStation(Build b, int x0, int z0, int x1, int z1) {
        Homes.cottage(b, "guard_station", x0, z0, x1, z1, Direction.SOUTH, Homes.Style.STONE,
                List.of("bram", "firstbrook_guard_1", "firstbrook_guard_2", "firstbrook_guard_3"));
        b.armorStand(x0 + 1, 0, z1 + 2, 180, new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_BOOTS));
        b.container(x1, 0, z1 + 2, Build.facing(Blocks.CHEST, Direction.SOUTH), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.SHIELD),
                new ItemStack(Items.BOW), new ItemStack(Items.ARROW, 16), new ItemStack(Items.BREAD, 4));
        b.sign((x0 + x1) / 2 + 2, 2, z1 + 1, Direction.SOUTH, "Firstbrook Watch", "Captain: Bram", "", "Report trouble here");
        b.set((x0 + x1) / 2 - 2, 0, z1 + 2, Blocks.BELL.defaultBlockState().setValue(net.minecraft.world.level.block.BellBlock.ATTACHMENT,
                net.minecraft.world.level.block.state.properties.BellAttachType.FLOOR));
    }

    /** A fenced training yard with straw dummies and archery targets. */
    static void trainingYard(Build b, int x0, int z0, int x1, int z1) {
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
                b.set(x, -1, z, (x + z) % 3 == 0 ? Blocks.COARSE_DIRT : Blocks.DIRT_PATH);
                if (edge) b.set(x, 0, z, Blocks.OAK_FENCE);
            }
        b.set(x0, 0, (z0 + z1) / 2, Blocks.OAK_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.WEST));
        for (int i = 0; i < 3; i++) {
            int dx = x0 + 2 + i * 3, dz = z0 + 2;
            b.set(dx, 0, dz, Blocks.OAK_FENCE);
            b.set(dx, 1, dz, Blocks.HAY_BLOCK);
            b.set(dx, 2, dz, Build.facing(Blocks.CARVED_PUMPKIN, Direction.SOUTH));
        }
        for (int i = 0; i < 3; i++) {
            int tx = x0 + 2 + i * 3;
            b.set(tx, 0, z1 - 1, Blocks.HAY_BLOCK);
            b.set(tx, 1, z1 - 1, Blocks.TARGET);
        }
        b.container(x1 - 1, 0, z0 + 1, Build.facing(Blocks.CHEST, Direction.WEST), new ItemStack(Items.WOODEN_SWORD, 1), new ItemStack(Items.WOODEN_SWORD, 1),
                new ItemStack(Items.BOW), new ItemStack(Items.ARROW, 24));
        b.point("work:training", (x0 + x1) / 2, 0, (z0 + z1) / 2);
    }

    /** A crop field with an irrigation channel down the middle. */
    static void farm(Build b, int x0, int z0, int x1, int z1, RandomSource rand) {
        for (int x = x0 - 1; x <= x1 + 1; x++)
            for (int z = z0 - 1; z <= z1 + 1; z++) {
                boolean edge = x < x0 || x > x1 || z < z0 || z > z1;
                if (edge) {
                    if (b.get(x, -1, z).is(Blocks.GRASS_BLOCK)) b.set(x, -1, z, Blocks.DIRT_PATH);
                    continue;
                }
                if (z == (z0 + z1) / 2) {
                    b.set(x, -1, z, Blocks.WATER);
                    continue;
                }
                b.set(x, -1, z, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7));
                Block crop = (x / 3) % 3 == 0 ? Blocks.CARROTS : Blocks.WHEAT;
                BlockState s = crop.defaultBlockState();
                s = crop == Blocks.WHEAT ? s.setValue(CropBlock.AGE, 4 + rand.nextInt(4)) : s.setValue(CropBlock.AGE, 3 + rand.nextInt(5));
                b.set(x, 0, z, s);
            }
        b.set(x0 - 1, 0, z1 + 1, Blocks.COMPOSTER);
    }

    /** The big barn: tall timber walls, hay loft, stalls, and the farm's tools. Door faces north. */
    static void barn(Build b, int x0, int z0, int x1, int z1) {
        b.fill(x0, -1, z0, x1, -1, z1, Blocks.COARSE_DIRT);
        b.fill(x0 + 1, 0, z0 + 1, x1 - 1, 6, z1 - 1, Blocks.AIR);
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean ex = x == x0 || x == x1, ez = z == z0 || z == z1;
                if (!ex && !ez) continue;
                boolean corner = ex && ez;
                for (int y = 0; y <= 5; y++) b.set(x, y, z, corner ? Blocks.STRIPPED_SPRUCE_LOG : (y == 0 ? Blocks.COBBLESTONE : Blocks.RED_TERRACOTTA));
                b.set(x, 6, z, Blocks.STRIPPED_SPRUCE_LOG);
            }
        b.gableRoof(x0, z0, x1, z1, 7, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_SLAB, Blocks.RED_TERRACOTTA);
        // Big double opening in the north wall.
        int cx = (x0 + x1) / 2;
        b.fill(cx - 1, 0, z0, cx + 1, 3, z0, Blocks.AIR);
        b.fill(cx - 2, 4, z0, cx + 2, 4, z0, Blocks.STRIPPED_SPRUCE_LOG);
        b.set(cx - 2, 2, z0 - 1, Blocks.WALL_TORCH.defaultBlockState().setValue(net.minecraft.world.level.block.WallTorchBlock.FACING, Direction.NORTH));
        b.set(cx + 2, 2, z0 - 1, Blocks.WALL_TORCH.defaultBlockState().setValue(net.minecraft.world.level.block.WallTorchBlock.FACING, Direction.NORTH));
        // Hay loft along the back with a ladder.
        b.fill(x0 + 1, 4, z1 - 3, x1 - 1, 4, z1 - 1, Blocks.SPRUCE_PLANKS);
        b.fill(x0 + 1, 5, z1 - 2, x1 - 1, 5, z1 - 1, Blocks.HAY_BLOCK);
        b.fill(x0 + 1, 0, z1 - 4, x0 + 1, 4, z1 - 4, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.NORTH));
        b.fill(x0 + 1, 0, z1 - 3, x0 + 1, 3, z1 - 3, Blocks.SPRUCE_PLANKS);
        // Stalls along the sides.
        for (int z = z0 + 2; z <= z1 - 5; z += 2) {
            b.fill(x0 + 1, 0, z, x0 + 2, 0, z, Blocks.SPRUCE_FENCE);
            b.fill(x1 - 2, 0, z, x1 - 1, 0, z, Blocks.SPRUCE_FENCE);
        }
        b.set(x0 + 1, 0, z0 + 1, Blocks.HAY_BLOCK);
        b.set(x1 - 1, 0, z0 + 1, Blocks.COMPOSTER);
        b.container(x1 - 1, 0, z1 - 4, Build.facing(Blocks.CHEST, Direction.WEST), new ItemStack(Items.WHEAT_SEEDS, 24), new ItemStack(Items.BONE_MEAL, 12),
                new ItemStack(Items.IRON_HOE), new ItemStack(Items.BUCKET), new ItemStack(Items.LEAD, 2));
        b.set(cx, 3, (z0 + z1) / 2, Build.hangingLantern());
        b.set(cx, 3, z0 + 2, Build.hangingLantern());
        animals(b, EntityType.COW, cx, z0 + 3, 2);
        b.point("work:barn", cx, 0, z0 + 2);
    }

    /** Fenced pens for sheep, pigs and a chicken coop. (x0..x1, z0..z1) is the outer fence. */
    static void pens(Build b, int x0, int z0, int x1, int z1) {
        int mid = (x0 + x1) / 2;
        for (int x = x0; x <= x1; x++)
            for (int z = z0; z <= z1; z++) {
                boolean edge = x == x0 || x == x1 || z == z0 || z == z1 || x == mid;
                if (edge) b.set(x, 0, z, Blocks.OAK_FENCE);
            }
        b.set((x0 + mid) / 2, 0, z0, Blocks.OAK_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.NORTH));
        b.set((mid + x1) / 2, 0, z0, Blocks.OAK_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.NORTH));
        b.set(x0 + 1, 0, z1 - 1, Blocks.HAY_BLOCK);
        b.set(x1 - 1, 0, z1 - 1, Blocks.WATER_CAULDRON.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3));
        animals(b, EntityType.SHEEP, (x0 + mid) / 2, (z0 + z1) / 2, 3);
        animals(b, EntityType.PIG, (mid + x1) / 2, (z0 + z1) / 2, 2);
        // Chicken coop on the far side.
        int cx = x1 + 2, cz = z0;
        for (int x = cx; x <= cx + 4; x++)
            for (int z = cz; z <= cz + 4; z++)
                if (x == cx || x == cx + 4 || z == cz || z == cz + 4) b.set(x, 0, z, Blocks.SPRUCE_FENCE);
        b.fill(cx + 3, 0, cz + 3, cx + 3, 0, cz + 3, Blocks.HAY_BLOCK);
        b.fill(cx, 1, cz, cx + 4, 1, cz + 4, Build.slab(Blocks.SPRUCE_SLAB));
        b.fill(cx + 1, 1, cz + 1, cx + 3, 1, cz + 3, Blocks.AIR);
        animals(b, EntityType.CHICKEN, cx + 2, cz + 2, 4);
    }

    private static void animals(Build b, EntityType<? extends Mob> type, int x, int z, int count) {
        for (int i = 0; i < count; i++) {
            Mob m = type.create(b.level);
            if (m == null) continue;
            BlockPos p = b.abs(x + (i % 2), 0, z + i / 2);
            m.moveTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, i * 70, 0);
            m.setPersistenceRequired();
            b.level.addFreshEntity((Entity) m);
        }
    }
}
