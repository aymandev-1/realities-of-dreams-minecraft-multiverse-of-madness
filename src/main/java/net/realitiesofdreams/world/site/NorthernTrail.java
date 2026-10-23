package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.realitiesofdreams.story.world.Mc001Terrain;

/**
 * Scene 003's places on the northern trail, built when the scene starts: Bram's barrier, the
 * footprints that stop, the scout's lantern, the rocks to climb with the scout's camp on top, a
 * broken survey marker and the carved stone that pulses. Coordinates are relative to the MC-001
 * anchor and sit on the authored hills ({@link Mc001Terrain#hillHeight}, the first air block).
 */
public final class NorthernTrail {
    private static final int FLAGS = Block.UPDATE_CLIENTS;

    private final ServerLevel level;
    private final BlockPos anchor;

    private NorthernTrail(ServerLevel level, BlockPos anchor) {
        this.level = level;
        this.anchor = anchor;
    }

    private void set(int x, int y, int z, BlockState s) {
        level.setBlock(anchor.offset(x, y, z), s, FLAGS);
    }

    private void set(int x, int y, int z, Block b) {
        set(x, y, z, b.defaultBlockState());
    }

    private static int ground(int x, int z) {
        return Mc001Terrain.hillHeight(x, z);
    }

    /** Everything except the clue blocks, which the scene places itself. */
    public static void build(ServerLevel level, BlockPos anchor) {
        NorthernTrail t = new NorthernTrail(level, anchor);
        t.barrier();
        t.footprints();
        t.outcrop();
        t.marker();
        t.carvedStone();
    }

    /** Bram's barrier across the trail just outside the gate, with its gate left open for the survey. */
    private void barrier() {
        for (int x = -4; x <= 2; x++) set(x, 0, -27, x == -1
                ? Blocks.SPRUCE_FENCE_GATE.defaultBlockState().setValue(FenceGateBlock.FACING, Direction.NORTH).setValue(FenceGateBlock.OPEN, true)
                : Blocks.SPRUCE_FENCE.defaultBlockState());
        set(-3, 1, -27, Blocks.SPRUCE_SIGN.defaultBlockState().setValue(StandingSignBlock.ROTATION, 8));
        if (level.getBlockEntity(anchor.offset(-3, 1, -27)) instanceof SignBlockEntity sign) {
            sign.setText(new SignText().setMessage(0, Component.literal("TRAIL CLOSED")).setMessage(1, Component.literal("Rockfall ahead"))
                    .setMessage(3, Component.literal("-- Bram")), true);
            sign.setWaxed(true);
        }
    }

    /** One traveller's boot prints beside the path, ending abruptly at z -60. */
    private void footprints() {
        int[][] steps = {{3, -52}, {2, -54}, {3, -56}, {2, -58}, {2, -60}};
        for (int[] s : steps) set(s[0], ground(s[0], s[1]) - 1, s[1], Blocks.MUD);
    }

    /** A rocky outcrop to climb (one-block steps from the trail) with the missing scout's camp on top. */
    private void outcrop() {
        for (int x = 4; x <= 10; x++)
            for (int z = -78; z <= -74; z++) {
                int top = Math.min(17, 13 + (x - 4));
                for (int y = ground(x, z) - 1; y <= top; y++) {
                    Block b = (x * 7 + y * 3 + z) % 5 == 0 ? Blocks.ANDESITE : (x + y + z) % 7 == 0 ? Blocks.COBBLESTONE : Blocks.STONE;
                    set(x, y, z, y == top && (x + z) % 3 == 0 ? Blocks.MOSSY_COBBLESTONE : b);
                }
                for (int y = top + 1; y <= top + 3; y++) set(x, y, z, Blocks.AIR);
            }
        // The scout's camp: bedroll, cold fire, pack (the pack is a clue block, placed by the scene).
        set(8, 18, -77, Blocks.WHITE_CARPET);
        set(8, 18, -78, Blocks.WHITE_CARPET);
        set(9, 18, -75, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, false));
        set(10, 18, -75, Blocks.OAK_LOG);
    }

    /** Fills the scout's pack once the scene has placed it. */
    public static void fillPack(ServerLevel level, BlockPos anchor, BlockPos pack) {
        if (level.getBlockEntity(pack) instanceof BarrelBlockEntity barrel && barrel.isEmpty()) {
            ItemStack notes = new ItemStack(Items.PAPER, 3);
            notes.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("Scout's survey notes"));
            barrel.setItem(0, notes);
            barrel.setItem(1, new ItemStack(Items.BREAD, 2));
            barrel.setItem(2, new ItemStack(Items.SPYGLASS));
            barrel.setItem(3, new ItemStack(Items.TORCH, 5));
        }
    }

    /** One of the old survey posts, snapped off; the standing stump is the clue block. */
    private void marker() {
        set(6, ground(6, -88) - 1, -88, Blocks.MOSSY_COBBLESTONE);
        set(7, ground(7, -88), -88, Blocks.STONE_BRICK_SLAB);
        set(7, ground(7, -89), -89, Blocks.STONE_BRICK_WALL);
    }

    /** The carved stone beside the trail: tuff with geometric markings on the side facing the path. */
    private void carvedStone() {
        for (int x = 9; x <= 10; x++)
            for (int z = -93; z <= -92; z++) {
                int g = ground(x, z);
                for (int y = g - 1; y <= g + 1; y++) set(x, y, z, x == 9 && y >= g ? Blocks.CHISELED_TUFF : Blocks.TUFF);
            }
    }

    /** The markings light up for a moment (Scene 003's pulse), then go dark again. */
    public static void pulse(ServerLevel level, BlockPos anchor, boolean on) {
        NorthernTrail t = new NorthernTrail(level, anchor);
        for (int z = -93; z <= -92; z++) {
            int g = ground(9, z);
            for (int y = g; y <= g + 1; y++) t.set(9, y, z, on ? Blocks.SEA_LANTERN : Blocks.CHISELED_TUFF);
        }
    }
}
