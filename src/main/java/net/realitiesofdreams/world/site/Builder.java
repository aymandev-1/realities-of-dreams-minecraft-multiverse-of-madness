package net.realitiesofdreams.world.site;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.function.Consumer;

/**
 * Block-placement toolkit for authored sites. Coordinates are relative to the site anchor, and
 * y = 0 is the first block above village ground (ground itself is y = -1).
 */
public class Builder {
    private static final int FLAGS = Block.UPDATE_CLIENTS;

    protected final ServerLevel level;
    protected final BlockPos anchor;
    protected final RandomSource rand;

    public Builder(ServerLevel level, BlockPos anchor, long seed) {
        this.level = level;
        this.anchor = anchor;
        this.rand = RandomSource.create(seed);
    }

    public ServerLevel level() {
        return level;
    }

    public BlockPos at(int x, int y, int z) {
        return anchor.offset(x, y, z);
    }

    // ------------------------------------------------------------------ basics

    public void set(int x, int y, int z, BlockState s) {
        level.setBlock(anchor.offset(x, y, z), s, FLAGS);
    }

    public void set(int x, int y, int z, Block b) {
        set(x, y, z, b.defaultBlockState());
    }

    public BlockState get(int x, int y, int z) {
        return level.getBlockState(anchor.offset(x, y, z));
    }

    public void fill(int x0, int y0, int z0, int x1, int y1, int z1, BlockState s) {
        for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++)
            for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++)
                for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) set(x, y, z, s);
    }

    public void fill(int x0, int y0, int z0, int x1, int y1, int z1, Block b) {
        fill(x0, y0, z0, x1, y1, z1, b.defaultBlockState());
    }

    /** Sets the block only where there is air now (for decoration that must not overwrite buildings). */
    public void setIfAir(int x, int y, int z, BlockState s) {
        if (get(x, y, z).isAir()) set(x, y, z, s);
    }

    // ------------------------------------------------------------------ shaped blocks

    /** The block turned to face d, whether it faces four ways (chests, furnaces) or six (barrels). */
    public static BlockState facing(Block b, Direction d) {
        BlockState s = b.defaultBlockState();
        if (s.hasProperty(HorizontalDirectionalBlock.FACING)) return s.setValue(HorizontalDirectionalBlock.FACING, d);
        if (s.hasProperty(BlockStateProperties.FACING)) return s.setValue(BlockStateProperties.FACING, d);
        return s;
    }

    public static BlockState stairs(Block b, Direction facing, boolean top) {
        return b.defaultBlockState().setValue(StairBlock.FACING, facing).setValue(StairBlock.HALF, top ? Half.TOP : Half.BOTTOM);
    }

    public static BlockState slab(Block b, boolean top) {
        return b.defaultBlockState().setValue(SlabBlock.TYPE, top ? SlabType.TOP : SlabType.BOTTOM);
    }

    public static BlockState log(Block b, Direction.Axis axis) {
        return b.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
    }

    public static BlockState hangingLantern() {
        return Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
    }

    public static BlockState leaves(Block b) {
        return b.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
    }

    public void door(int x, int y, int z, Block door, Direction facing) {
        set(x, y, z, door.defaultBlockState().setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        set(x, y + 1, z, door.defaultBlockState().setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }

    public void gate(int x, int y, int z, Block gate, Direction facing) {
        set(x, y, z, gate.defaultBlockState().setValue(FenceGateBlock.FACING, facing));
    }

    public void trapdoor(int x, int y, int z, Block b, Direction facing, boolean open, boolean top) {
        set(x, y, z, b.defaultBlockState().setValue(TrapDoorBlock.FACING, facing).setValue(TrapDoorBlock.OPEN, open)
                .setValue(TrapDoorBlock.HALF, top ? Half.TOP : Half.BOTTOM));
    }

    public void ladder(int x, int y0, int y1, int z, Direction facing) {
        for (int y = y0; y <= y1; y++) set(x, y, z, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, facing));
    }

    /** A bed with its head at (x, z), lying towards {@code headFacing}. */
    public void bed(int x, int y, int z, Block bed, Direction headFacing) {
        set(x, y, z, bed.defaultBlockState().setValue(BedBlock.FACING, headFacing).setValue(BedBlock.PART, BedPart.HEAD));
        Direction back = headFacing.getOpposite();
        set(x + back.getStepX(), y, z + back.getStepZ(), bed.defaultBlockState().setValue(BedBlock.FACING, headFacing).setValue(BedBlock.PART, BedPart.FOOT));
    }

    /** A lit campfire; on hay it sends up the tall signal-fire smoke that reads from far away. */
    public void campfire(int x, int y, int z, boolean signal) {
        set(x, y, z, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, true).setValue(CampfireBlock.SIGNAL_FIRE, signal));
    }

    public void sign(int x, int y, int z, Block wallSign, Direction facing, String... lines) {
        set(x, y, z, wallSign.defaultBlockState().setValue(WallSignBlock.FACING, facing));
        if (level.getBlockEntity(at(x, y, z)) instanceof SignBlockEntity s) {
            s.updateText(t -> {
                for (int i = 0; i < Math.min(4, lines.length); i++) t = t.setMessage(i, Component.literal(lines[i]));
                return t;
            }, true);
            s.setWaxed(true);
            s.setChanged();
        }
    }

    /** A free-standing sign; rotation 0 faces south, 4 west, 8 north, 12 east. */
    public void standingSign(int x, int y, int z, Block sign, int rotation, String... lines) {
        set(x, y, z, sign.defaultBlockState().setValue(net.minecraft.world.level.block.StandingSignBlock.ROTATION, rotation));
        if (level.getBlockEntity(at(x, y, z)) instanceof SignBlockEntity s) {
            s.updateText(t -> {
                for (int i = 0; i < Math.min(4, lines.length); i++) t = t.setMessage(i, Component.literal(lines[i]));
                return t;
            }, true);
            s.setWaxed(true);
            s.setChanged();
        }
    }

    public void container(int x, int y, int z, Consumer<BaseContainerBlockEntity> fill) {
        BlockEntity be = level.getBlockEntity(at(x, y, z));
        if (be instanceof BaseContainerBlockEntity c) fill.accept(c);
    }

    public static void put(BaseContainerBlockEntity c, int slot, ItemStack stack) {
        if (slot < c.getContainerSize()) c.setItem(slot, stack);
    }

    public void itemFrame(int x, int y, int z, Direction facing, ItemStack item) {
        ItemFrame frame = new ItemFrame(level, at(x, y, z), facing);
        frame.setItem(item, false);
        level.addFreshEntity(frame);
    }

    public ArmorStand armorStand(double x, int y, double z, float yaw, ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        ArmorStand stand = EntityType.ARMOR_STAND.create(level);
        if (stand == null) return null;
        stand.moveTo(anchor.getX() + x, anchor.getY() + y, anchor.getZ() + z, yaw, 0);
        stand.setItemSlot(EquipmentSlot.HEAD, head);
        stand.setItemSlot(EquipmentSlot.CHEST, chest);
        stand.setItemSlot(EquipmentSlot.LEGS, legs);
        stand.setItemSlot(EquipmentSlot.FEET, feet);
        level.addFreshEntity(stand);
        return stand;
    }

    /** A farm animal that stays put (never despawns). */
    public void animal(EntityType<? extends Mob> type, double x, int y, double z) {
        Mob m = type.create(level);
        if (m == null) return;
        m.moveTo(anchor.getX() + x + 0.5, anchor.getY() + y, anchor.getZ() + z + 0.5, rand.nextFloat() * 360, 0);
        m.finalizeSpawn(level, level.getCurrentDifficultyAt(m.blockPosition()), MobSpawnType.STRUCTURE, null);
        m.setPersistenceRequired();
        level.addFreshEntity(m);
    }

    // ------------------------------------------------------------------ larger shapes

    /**
     * A pitched roof over the outer footprint (x0,z0)-(x1,z1) whose walls end at y = wallTop - 1.
     * The ridge runs along the longer side; eaves overhang by one block and the gable ends are
     * closed with {@code gableFill}. Returns the y of the ridge.
     */
    public int gableRoof(int x0, int z0, int x1, int z1, int wallTop, Palette p) {
        boolean alongX = (x1 - x0) >= (z1 - z0);
        int lo = alongX ? z0 - 1 : x0 - 1, hi = alongX ? z1 + 1 : x1 + 1;
        int a0 = alongX ? x0 - 1 : z0 - 1, a1 = alongX ? x1 + 1 : z1 + 1;
        int y = wallTop;
        int i = 0;
        for (; lo + i < hi - i; i++, y++) {
            for (int a = a0; a <= a1; a++) {
                Direction upLo = alongX ? Direction.SOUTH : Direction.EAST;
                Direction upHi = upLo.getOpposite();
                placeRoof(alongX, a, y, lo + i, stairs(p.roofStairs(), upLo, false));
                placeRoof(alongX, a, y, hi - i, stairs(p.roofStairs(), upHi, false));
                // Upside-down stairs under the eaves give the roof a finished edge.
                if (i == 0) {
                    placeRoof(alongX, a, y - 1, lo, stairs(p.roofStairs(), upHi, true));
                    placeRoof(alongX, a, y - 1, hi, stairs(p.roofStairs(), upLo, true));
                }
            }
            // Close the gable ends inside the walls.
            for (int c = lo + i + 1; c <= hi - i - 1; c++) {
                if (c < (alongX ? z0 : x0) || c > (alongX ? z1 : x1)) continue;
                placeRoof(alongX, alongX ? x0 : z0, y, c, p.gable());
                placeRoof(alongX, alongX ? x1 : z1, y, c, p.gable());
            }
        }
        if (lo + i == hi - i) {
            for (int a = a0; a <= a1; a++) placeRoof(alongX, a, y, lo + i, slab(p.roofSlab(), false));
        } else {
            y--;
        }
        return y;
    }

    private void placeRoof(boolean alongX, int a, int y, int c, BlockState s) {
        if (alongX) set(a, y, c, s);
        else set(c, y, a, s);
    }

    /** A stone chimney from the floor up past the roof, with a lit campfire so it smokes. */
    public void chimney(int x, int z, int top, Block stone) {
        fill(x, 0, z, x, top, z, stone);
        campfire(x, top + 1, z, true);
    }

    /** An oak or birch tree with a rounded crown. */
    public void broadleaf(int x, int y, int z, int height, Block logBlock, Block leafBlock) {
        for (int i = 0; i < height; i++) set(x, y + i, z, logBlock);
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                for (int dy = height - 2; dy <= height + 1; dy++) {
                    int r = dy >= height ? 1 : 2;
                    if (Math.abs(dx) > r || Math.abs(dz) > r || (Math.abs(dx) == r && Math.abs(dz) == r && (r == 2 || rand.nextBoolean()))) continue;
                    setIfAir(x + dx, y + dy, z + dz, leaves(leafBlock));
                }
    }

    public void spruce(int x, int y, int z, int height) {
        for (int i = 0; i < height; i++) set(x, y + i, z, Blocks.SPRUCE_LOG);
        for (int dy = 2; dy <= height; dy++) {
            int r = dy >= height - 1 ? 0 : dy % 2 == 0 ? 2 : 1;
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++)
                    if ((dx != 0 || dz != 0) && Math.abs(dx) + Math.abs(dz) <= r + 1) setIfAir(x + dx, y + dy, z + dz, leaves(Blocks.SPRUCE_LEAVES));
        }
        setIfAir(x, y + height, z, leaves(Blocks.SPRUCE_LEAVES));
    }

    public void bush(int x, int y, int z) {
        setIfAir(x, y, z, leaves(rand.nextInt(3) == 0 ? Blocks.FLOWERING_AZALEA_LEAVES : Blocks.AZALEA_LEAVES));
    }

    public static Block bedOf(DyeColor c) {
        return switch (c) {
            case BLUE -> Blocks.BLUE_BED;
            case GREEN -> Blocks.GREEN_BED;
            case YELLOW -> Blocks.YELLOW_BED;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_BED;
            case BROWN -> Blocks.BROWN_BED;
            case CYAN -> Blocks.CYAN_BED;
            case PURPLE -> Blocks.PURPLE_BED;
            case WHITE -> Blocks.WHITE_BED;
            case ORANGE -> Blocks.ORANGE_BED;
            default -> Blocks.RED_BED;
        };
    }
}
