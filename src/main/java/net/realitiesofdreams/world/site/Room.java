package net.realitiesofdreams.world.site;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The inside of a house, in coordinates relative to its front door, so one furnishing plan works
 * whichever way the house faces. u runs across the room (0 = left as you walk in), v runs from the
 * front wall (0) to the back wall (depth - 1).
 */
public final class Room {
    private final Builder b;
    private final int x0, z0, x1, z1;
    /** F points out of the front door; R is to the right as you walk in... and so on. */
    public final Direction front, back, left, right;
    public final int width, depth, doorU;

    public Room(Builder b, int[] box, Direction door, int doorX, int doorZ) {
        this.b = b;
        this.x0 = box[0];
        this.z0 = box[1];
        this.x1 = box[2];
        this.z1 = box[3];
        this.front = door;
        this.back = door.getOpposite();
        this.left = door.getClockWise();
        this.right = door.getCounterClockWise();
        boolean ns = door.getAxis() == Direction.Axis.Z;
        this.width = ns ? x1 - x0 - 1 : z1 - z0 - 1;
        this.depth = ns ? z1 - z0 - 1 : x1 - x0 - 1;
        this.doorU = u(doorX, doorZ);
    }

    public int worldX(int u, int v) {
        return switch (front) {
            case NORTH -> x1 - 1 - u;
            case SOUTH -> x0 + 1 + u;
            case WEST -> x0 + 1 + v;
            default -> x1 - 1 - v;
        };
    }

    public int worldZ(int u, int v) {
        return switch (front) {
            case NORTH -> z0 + 1 + v;
            case SOUTH -> z1 - 1 - v;
            case WEST -> z0 + 1 + u;
            default -> z1 - 1 - u;
        };
    }

    private int u(int wx, int wz) {
        return switch (front) {
            case NORTH -> x1 - 1 - wx;
            case SOUTH -> wx - x0 - 1;
            case WEST -> wz - z0 - 1;
            default -> z1 - 1 - wz;
        };
    }

    /** True for the two cells just inside the door, which furniture must leave free. */
    public boolean doorway(int u, int v) {
        return v <= 1 && Math.abs(u - doorU) <= 1;
    }

    public void set(int u, int y, int v, BlockState s) {
        if (u < 0 || u >= width || v < 0 || v >= depth || doorway(u, v)) return;
        b.set(worldX(u, v), y, worldZ(u, v), s);
    }

    public void set(int u, int y, int v, Block block) {
        set(u, y, v, block.defaultBlockState());
    }

    /** A block that faces a room direction (furnace, barrel front, lectern...). */
    public void facing(int u, int y, int v, Block block, Direction dir) {
        set(u, y, v, Builder.facing(block, dir));
    }

    public void bed(int u, int v, Block bed, Direction headTowards) {
        Direction foot = headTowards.getOpposite();
        int fu = u + du(foot), fv = v + dv(foot);
        if (doorway(u, v) || doorway(fu, fv)) return;
        b.bed(worldX(u, v), 0, worldZ(u, v), bed, headTowards);
    }

    /** Converts a world direction into a step along u. */
    public int du(Direction d) {
        if (d == right) return 1;
        if (d == left) return -1;
        return 0;
    }

    public int dv(Direction d) {
        if (d == back) return 1;
        if (d == front) return -1;
        return 0;
    }

    /** A small table (fence post with a pressure-plate top) with a chair on one side. */
    public void table(int u, int v, Direction chairSide) {
        set(u, 0, v, Blocks.OAK_FENCE);
        set(u, 1, v, Blocks.OAK_PRESSURE_PLATE);
        int cu = u + du(chairSide), cv = v + dv(chairSide);
        set(cu, 0, cv, Builder.stairs(Blocks.SPRUCE_STAIRS, chairSide, false));
    }

    public void carpet(int u0, int v0, int u1, int v1, Block carpet) {
        for (int u = u0; u <= u1; u++) for (int v = v0; v <= v1; v++) set(u, 0, v, carpet);
    }
}
