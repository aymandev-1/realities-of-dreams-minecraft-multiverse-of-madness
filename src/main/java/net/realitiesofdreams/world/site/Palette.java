package net.realitiesofdreams.world.site;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

/**
 * Building materials for one style of Firstbrook house, so homes share a village character without
 * being identical.
 */
public record Palette(String id, Block foundation, Block wall, Block frame, Block trim, Block floor, Block roofStairs, Block roofSlab,
                      Block door, Block window, Block wallSign, Block chimney) {
    public BlockState gable() {
        return wall.defaultBlockState();
    }

    public static final Palette TIMBER = new Palette("timber", Blocks.COBBLESTONE, Blocks.OAK_PLANKS, Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG,
            Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_DOOR, Blocks.GLASS_PANE, Blocks.SPRUCE_WALL_SIGN, Blocks.COBBLESTONE);
    public static final Palette ELDER = new Palette("elder", Blocks.STONE_BRICKS, Blocks.SPRUCE_PLANKS, Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG,
            Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_DOOR, Blocks.GLASS_PANE, Blocks.DARK_OAK_WALL_SIGN, Blocks.STONE_BRICKS);
    public static final Palette BIRCH = new Palette("birch", Blocks.MOSSY_COBBLESTONE, Blocks.BIRCH_PLANKS, Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG,
            Blocks.OAK_PLANKS, Blocks.OAK_STAIRS, Blocks.OAK_SLAB, Blocks.BIRCH_DOOR, Blocks.GLASS_PANE, Blocks.BIRCH_WALL_SIGN, Blocks.COBBLESTONE);
    public static final Palette STONE = new Palette("stone", Blocks.STONE_BRICKS, Blocks.COBBLESTONE, Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_LOG,
            Blocks.SPRUCE_PLANKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.SPRUCE_DOOR, Blocks.GLASS_PANE, Blocks.SPRUCE_WALL_SIGN, Blocks.STONE_BRICKS);
    public static final Palette FARM = new Palette("farm", Blocks.COBBLESTONE, Blocks.SPRUCE_PLANKS, Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG,
            Blocks.OAK_PLANKS, Blocks.OAK_STAIRS, Blocks.OAK_SLAB, Blocks.OAK_DOOR, Blocks.GLASS_PANE, Blocks.OAK_WALL_SIGN, Blocks.COBBLESTONE);
    public static final Palette PLASTER = new Palette("plaster", Blocks.COBBLESTONE, Blocks.WHITE_TERRACOTTA, Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG,
            Blocks.SPRUCE_PLANKS, Blocks.BRICK_STAIRS, Blocks.BRICK_SLAB, Blocks.DARK_OAK_DOOR, Blocks.GLASS_PANE, Blocks.DARK_OAK_WALL_SIGN, Blocks.BRICKS);
    public static final Palette GUARD = new Palette("guard", Blocks.STONE_BRICKS, Blocks.COBBLESTONE, Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG,
            Blocks.SPRUCE_PLANKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICK_SLAB, Blocks.SPRUCE_DOOR, Blocks.GLASS_PANE, Blocks.SPRUCE_WALL_SIGN, Blocks.STONE_BRICKS);

    private static final Map<String, Palette> ALL = Map.of("timber", TIMBER, "elder", ELDER, "birch", BIRCH, "stone", STONE, "farm", FARM,
            "plaster", PLASTER, "guard", GUARD);

    public static Palette of(String id) {
        Palette p = ALL.get(id);
        if (p == null) throw new IllegalArgumentException("Unknown palette " + id);
        return p;
    }
}
