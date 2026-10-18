package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Scene 004 in Orin's workshop (footprint x 8..14, z -11..-5): a workbench with a survey table
 * beside it, a chest of survey supplies, Elara's sketch of the markings and, for the cinematic,
 * the compass laid next to it.
 */
public final class OrinWorkshop {
    private static final int FLAGS = Block.UPDATE_CLIENTS;
    /** Relative positions inside the workshop. */
    public static final BlockPos WORKBENCH = new BlockPos(12, 0, -7), SURVEY_TABLE = new BlockPos(12, 0, -6), SUPPLIES = new BlockPos(10, 0, -10);

    private OrinWorkshop() {}

    /** The workbench, the survey table and the supply chest. */
    public static void supplies(ServerLevel level, BlockPos anchor) {
        level.setBlock(anchor.offset(WORKBENCH), Blocks.CRAFTING_TABLE.defaultBlockState(), FLAGS);
        level.setBlock(anchor.offset(SURVEY_TABLE), Blocks.SPRUCE_PLANKS.defaultBlockState(), FLAGS);
        BlockPos chest = anchor.offset(SUPPLIES);
        level.setBlock(chest, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH), FLAGS);
        if (level.getBlockEntity(chest) instanceof ChestBlockEntity c) {
            ItemStack[] items = {
                    new ItemStack(Items.IRON_INGOT, 4), new ItemStack(Items.STICK, 6), new ItemStack(Items.STRING, 4),
                    new ItemStack(Items.SLIME_BALL, 1), new ItemStack(Items.BONE_MEAL, 3), new ItemStack(Items.COAL, 4),
                    new ItemStack(Items.REDSTONE, 1)};
            for (int i = 0; i < items.length; i++) c.setItem(i, items[i]);
        }
    }

    /** Elara lays her sketch of the markings on the survey table. */
    public static void sketch(ServerLevel level, BlockPos anchor) {
        ItemStack sketch = new ItemStack(Items.PAPER);
        sketch.set(DataComponents.CUSTOM_NAME, Component.literal("Elara's sketch"));
        sketch.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("Markings copied from the carved stone"),
                Component.literal("on the northern trail."))));
        frame(level, anchor.offset(SURVEY_TABLE).above(), sketch);
    }

    /** Orin lays the compass beside the sketch for the cinematic. */
    public static void compass(ServerLevel level, BlockPos anchor) {
        frame(level, anchor.offset(WORKBENCH).above(), new ItemStack(Items.COMPASS));
    }

    private static void frame(ServerLevel level, BlockPos at, ItemStack item) {
        if (!level.getEntitiesOfClass(ItemFrame.class, new AABB(at)).isEmpty()) return;
        ItemFrame frame = new ItemFrame(level, at, Direction.UP);
        frame.setItem(item, false);
        level.addFreshEntity(frame);
    }
}
