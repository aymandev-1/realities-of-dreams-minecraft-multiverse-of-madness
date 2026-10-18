package net.realitiesofdreams.world.build;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The interior of a building seen from its front door, so furniture layouts work whichever way
 * the building faces. {@code v} counts rows inward from the door wall (v = 0 is the row just
 * inside the door) and {@code u} counts columns across the room along {@link #across()}.
 */
public final class Room {
    private final Build b;
    private final Direction inward;
    private final Direction across;
    private final int ox, oz;
    public final int width, depth;

    /** (x0,z0)-(x1,z1) is the outer wall footprint; the door is in the wall on {@code doorSide}. */
    public Room(Build b, int x0, int z0, int x1, int z1, Direction doorSide) {
        this.b = b;
        this.inward = doorSide.getOpposite();
        this.across = inward.getCounterClockWise();
        int ix0 = x0 + 1, ix1 = x1 - 1, iz0 = z0 + 1, iz1 = z1 - 1;
        // origin: interior corner on the door wall, at the low end of the "across" axis
        int startX = switch (doorSide) {
            case EAST -> ix1;
            case WEST -> ix0;
            default -> across.getStepX() > 0 ? ix0 : ix1;
        };
        int startZ = switch (doorSide) {
            case NORTH -> iz0;
            case SOUTH -> iz1;
            default -> across.getStepZ() > 0 ? iz0 : iz1;
        };
        this.ox = startX;
        this.oz = startZ;
        boolean acrossX = across.getAxis() == Direction.Axis.X;
        this.width = acrossX ? ix1 - ix0 + 1 : iz1 - iz0 + 1;
        this.depth = acrossX ? iz1 - iz0 + 1 : ix1 - ix0 + 1;
    }

    public Direction inward() {
        return inward;
    }

    public Direction across() {
        return across;
    }

    public int x(int u, int v) {
        return ox + across.getStepX() * u + inward.getStepX() * v;
    }

    public int z(int u, int v) {
        return oz + across.getStepZ() * u + inward.getStepZ() * v;
    }

    public BlockPos rel(int u, int y, int v) {
        return new BlockPos(x(u, v), y, z(u, v));
    }

    public void set(int u, int y, int v, BlockState s) {
        b.set(x(u, v), y, z(u, v), s);
    }

    public void set(int u, int y, int v, Block block) {
        b.set(x(u, v), y, z(u, v), block);
    }

    /** The door's position along the door wall. */
    public int doorU() {
        return width / 2;
    }

    /** A bed against the back wall at column u, pillow toward the back wall. Returns the pillow position. */
    public BlockPos bed(int u, Block bed) {
        int v = depth - 1;
        b.bed(x(u, v), 0, z(u, v), inward, bed);
        return rel(u, 0, v);
    }
}
