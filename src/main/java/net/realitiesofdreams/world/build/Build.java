package net.realitiesofdreams.world.build;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Block-placement toolkit for authored story sites. Coordinates are relative to the site anchor;
 * y = 0 is the first block above the ground. Every builder records the named points it creates
 * (beds, workstations, doors) so characters can find their homes and workplaces afterwards.
 */
public final class Build {
    private static final int FLAGS = Block.UPDATE_CLIENTS;

    public final ServerLevel level;
    public final BlockPos anchor;
    /** Named points, e.g. "bed:elara" or "work:orin", relative to the anchor. */
    public final Map<String, BlockPos> points = new LinkedHashMap<>();
    /** Story-critical volumes that survival players cannot break (relative to the anchor). */
    public final List<AABB> protectedBoxes = new ArrayList<>();

    public Build(ServerLevel level, BlockPos anchor) {
        this.level = level;
        this.anchor = anchor;
    }

    // ------------------------------------------------------------------ primitives

    public BlockPos abs(int x, int y, int z) {
        return anchor.offset(x, y, z);
    }

    public void set(int x, int y, int z, BlockState s) {
        level.setBlock(anchor.offset(x, y, z), s, FLAGS);
    }

    public void set(int x, int y, int z, Block b) {
        set(x, y, z, b.defaultBlockState());
    }

    public BlockState get(int x, int y, int z) {
        return level.getBlockState(anchor.offset(x, y, z));
    }

    public boolean isAir(int x, int y, int z) {
        return get(x, y, z).isAir();
    }

    public void fill(int x0, int y0, int z0, int x1, int y1, int z1, BlockState s) {
        for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++)
            for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++)
                for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) set(x, y, z, s);
    }

    public void fill(int x0, int y0, int z0, int x1, int y1, int z1, Block b) {
        fill(x0, y0, z0, x1, y1, z1, b.defaultBlockState());
    }

    public void point(String name, int x, int y, int z) {
        points.put(name, new BlockPos(x, y, z));
    }

    public void protect(int x0, int y0, int z0, int x1, int y1, int z1) {
        protectedBoxes.add(new AABB(Math.min(x0, x1), Math.min(y0, y1), Math.min(z0, z1), Math.max(x0, x1) + 1, Math.max(y0, y1) + 1, Math.max(z0, z1) + 1));
    }

    // ------------------------------------------------------------------ shaped blocks

    public static BlockState slab(Block b) {
        return b.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
    }

    public static BlockState topSlab(Block b) {
        return b.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);
    }

    public static BlockState stairs(Block b, Direction facing) {
        return b.defaultBlockState().setValue(StairBlock.FACING, facing);
    }

    public static BlockState upsideDownStairs(Block b, Direction facing) {
        return b.defaultBlockState().setValue(StairBlock.FACING, facing).setValue(StairBlock.HALF, Half.TOP);
    }

    public static BlockState facing(Block b, Direction facing) {
        return b.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing);
    }

    public static BlockState leaves(Block b) {
        return b.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
    }

    public static BlockState hangingLantern() {
        return Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
    }

    public void door(int x, int y, int z, Block door, Direction facing) {
        set(x, y, z, door.defaultBlockState().setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        set(x, y + 1, z, door.defaultBlockState().setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }

    /** A bed whose pillow is at (x, y, z) with the foot one block toward {@code headFacing.getOpposite()}. */
    public void bed(int x, int y, int z, Direction headFacing, Block bed) {
        Direction back = headFacing.getOpposite();
        set(x, y, z, bed.defaultBlockState().setValue(BedBlock.FACING, headFacing).setValue(BedBlock.PART, BedPart.HEAD));
        set(x + back.getStepX(), y, z + back.getStepZ(), bed.defaultBlockState().setValue(BedBlock.FACING, headFacing).setValue(BedBlock.PART, BedPart.FOOT));
    }

    public void trapdoorOpen(int x, int y, int z, Block b, Direction facing) {
        set(x, y, z, b.defaultBlockState().setValue(TrapDoorBlock.FACING, facing).setValue(TrapDoorBlock.OPEN, true));
    }

    /** A container (chest, barrel) filled with the given stacks in order. */
    public void container(int x, int y, int z, BlockState s, ItemStack... items) {
        set(x, y, z, s);
        if (level.getBlockEntity(abs(x, y, z)) instanceof BaseContainerBlockEntity c) {
            for (int i = 0; i < items.length && i < c.getContainerSize(); i++) c.setItem(i, items[i]);
        }
    }

    /** A wall sign on the face of the block behind it, facing {@code facing}. */
    public void sign(int x, int y, int z, Direction facing, String... lines) {
        set(x, y, z, Blocks.SPRUCE_WALL_SIGN.defaultBlockState().setValue(WallSignBlock.FACING, facing));
        if (level.getBlockEntity(abs(x, y, z)) instanceof SignBlockEntity sign) {
            SignText text = new SignText().setColor(DyeColor.BLACK);
            for (int i = 0; i < lines.length && i < 4; i++) text = text.setMessage(i, Component.literal(lines[i]));
            sign.setText(text, true);
            sign.setWaxed(true);
        }
    }

    public void itemFrame(int x, int y, int z, Direction facing, ItemStack item) {
        ItemFrame frame = new ItemFrame(level, abs(x, y, z), facing);
        frame.setItem(item, false);
        level.addFreshEntity(frame);
    }

    public ArmorStand armorStand(int x, int y, int z, float yaw, ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        ArmorStand stand = EntityType.ARMOR_STAND.create(level);
        if (stand == null) return null;
        BlockPos p = abs(x, y, z);
        stand.moveTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, yaw, 0);
        stand.setItemSlot(EquipmentSlot.HEAD, head);
        stand.setItemSlot(EquipmentSlot.CHEST, chest);
        stand.setItemSlot(EquipmentSlot.LEGS, legs);
        stand.setItemSlot(EquipmentSlot.FEET, feet);
        level.addFreshEntity(stand);
        return stand;
    }

    // ------------------------------------------------------------------ roofs

    /**
     * A gable roof of stairs over the footprint (x0,z0)-(x1,z1) with a one-block overhang. The ridge
     * runs along the longer side; the gable ends are filled with {@code gable}.
     */
    public void gableRoof(int x0, int z0, int x1, int z1, int baseY, Block stairs, Block ridgeSlab, Block gable) {
        boolean ridgeAlongX = (x1 - x0) >= (z1 - z0);
        if (ridgeAlongX) {
            int lo = z0 - 1, hi = z1 + 1;
            for (int i = 0; lo + i <= hi - i; i++) {
                int y = baseY + i, a = lo + i, b = hi - i;
                if (a == b) {
                    fill(x0 - 1, y, a, x1 + 1, y, a, slab(ridgeSlab));
                    break;
                }
                fill(x0 - 1, y, a, x1 + 1, y, a, stairs(stairs, Direction.SOUTH));
                fill(x0 - 1, y, b, x1 + 1, y, b, stairs(stairs, Direction.NORTH));
                if (a + 1 <= b - 1) {
                    fill(x0, y, a + 1, x0, y, b - 1, gable);
                    fill(x1, y, a + 1, x1, y, b - 1, gable);
                }
                if (a + 1 == b) {
                    fill(x0 - 1, y + 1, a, x1 + 1, y + 1, b, slab(ridgeSlab));
                    break;
                }
            }
        } else {
            int lo = x0 - 1, hi = x1 + 1;
            for (int i = 0; lo + i <= hi - i; i++) {
                int y = baseY + i, a = lo + i, b = hi - i;
                if (a == b) {
                    fill(a, y, z0 - 1, a, y, z1 + 1, slab(ridgeSlab));
                    break;
                }
                fill(a, y, z0 - 1, a, y, z1 + 1, stairs(stairs, Direction.EAST));
                fill(b, y, z0 - 1, b, y, z1 + 1, stairs(stairs, Direction.WEST));
                if (a + 1 <= b - 1) {
                    fill(a + 1, y, z0, b - 1, y, z0, gable);
                    fill(a + 1, y, z1, b - 1, y, z1, gable);
                }
                if (a + 1 == b) {
                    fill(a, y + 1, z0 - 1, b, y + 1, z1 + 1, slab(ridgeSlab));
                    break;
                }
            }
        }
    }

    // ------------------------------------------------------------------ trees

    public void oak(int x, int y, int z, int height) {
        roundTree(x, y, z, height, Blocks.OAK_LOG, Blocks.OAK_LEAVES);
    }

    public void birch(int x, int y, int z, int height) {
        roundTree(x, y, z, height, Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES);
    }

    private void roundTree(int x, int y, int z, int height, Block log, Block leaf) {
        for (int i = 0; i < height; i++) set(x, y + i, z, log);
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                for (int dy = height - 2; dy <= height + 1; dy++) {
                    int r = dy >= height ? 1 : 2;
                    if (Math.abs(dx) > r || Math.abs(dz) > r || (Math.abs(dx) == r && Math.abs(dz) == r && (r == 2 || dy == height + 1))) continue;
                    if (isAir(x + dx, y + dy, z + dz)) set(x + dx, y + dy, z + dz, leaves(leaf));
                }
    }

    public void spruce(int x, int y, int z, int height) {
        for (int i = 0; i < height; i++) set(x, y + i, z, Blocks.SPRUCE_LOG);
        for (int dy = 2; dy <= height; dy++) {
            int r = dy >= height - 1 ? 0 : dy % 2 == 0 ? 2 : 1;
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++)
                    if ((dx != 0 || dz != 0) && Math.abs(dx) + Math.abs(dz) <= r + 1 && isAir(x + dx, y + dy, z + dz))
                        set(x + dx, y + dy, z + dz, leaves(Blocks.SPRUCE_LEAVES));
        }
        set(x, y + height, z, leaves(Blocks.SPRUCE_LEAVES));
    }

    /** A great old oak with a thick trunk and wide crown (Sylvaris-style landmark tree in miniature). */
    public void ancientOak(int x, int y, int z, int height) {
        for (int i = 0; i < height; i++) fill(x, y + i, z, x + 1, y + i, z + 1, Blocks.DARK_OAK_LOG);
        for (int dx = -4; dx <= 5; dx++)
            for (int dz = -4; dz <= 5; dz++)
                for (int dy = height - 3; dy <= height + 2; dy++) {
                    double cx = dx - 0.5, cz = dz - 0.5, cy = dy - height;
                    if (cx * cx + cz * cz + cy * cy * 2.2 > 22) continue;
                    if (isAir(x + dx, y + dy, z + dz)) set(x + dx, y + dy, z + dz, leaves(Blocks.DARK_OAK_LEAVES));
                }
    }
}
