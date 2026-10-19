package net.realitiesofdreams.story.world;

/**
 * The authored shape of MC-001 around Firstbrook, shared by the chunk generator (which carves real
 * terrain to it) and the site builders (which stand buildings on it).
 *
 * Coordinates are relative to the Firstbrook anchor (block 0, 64, 0). A height h means the top solid
 * block is at anchor y - 1 + h, so h = 0 is village ground level. Beyond {@link #AUTHORED_RADIUS} the
 * shape blends into ordinary Minecraft terrain, which keeps going in every direction.
 *
 * Layout: Firstbrook sits on flat ground around the origin. The river runs north to south at x = 32,
 * from a waterfall at the foot of the northern hills down to Southmere lake. The hills rise north of
 * the village wall to the ridge with the old ruin (z = -112), then climb into the mountains at the
 * northern edge of Awakening Valley. Gentle rolling meadow surrounds the rest.
 */
public final class Mc001Terrain {
    /** Inside this distance the terrain is fully authored. */
    public static final double AUTHORED_RADIUS = 210;
    /** By this distance the terrain is fully natural. */
    public static final double NATURAL_RADIUS = 280;
    /** Half-width of the flat village square. */
    public static final int VILLAGE_HALF = 46;
    /** The edge of the hills; the village wall stands just inside it. */
    public static final int HILLS_START_Z = -46;
    public static final int RIDGE_Z = -112;
    public static final int RIVER_X = 32;
    public static final int RIVER_HALF_WIDTH = 2;
    /** The waterfall where the river leaves the hills. */
    public static final int WATERFALL_Z = -57;
    public static final int LAKE_X = 32, LAKE_Z = 112, LAKE_RADIUS = 16;
    /** Water stands level with village ground (the top of the water is at height 0). */
    public static final int WATER_TOP = 0;
    /** Firstbrook's anchor in MC-001 block coordinates (the scenes' "anchor"). */
    public static final int ANCHOR_X = 0, ANCHOR_Y = 64, ANCHOR_Z = 0;

    /**
     * The far capitals (Scenes 013 to 016) stand on authored plateaus of their own out in natural
     * terrain: flat out to {@code radius} at height {@code height}, then blending back into ordinary
     * land over the next {@code blend} blocks. Each has a fixed biome so its grass, sky and weather
     * fit (desert for Sunspire, and so on). Irondeep is underground and needs no plateau.
     */
    public record Region(String id, int x, int z, int radius, int blend, int height, String biome) {
        public double distance(int px, int pz) {
            return Math.hypot(px - x, pz - z);
        }

        /** 1 on the plateau, 0 in natural terrain, smooth between. */
        public double weight(int px, int pz) {
            double d = distance(px, pz);
            if (d <= radius) return 1;
            if (d >= radius + blend) return 0;
            double t = (d - radius) / blend;
            return 1 - t * t * (3 - 2 * t);
        }
    }

    public static final java.util.List<Region> REGIONS = java.util.List.of(
            new Region("sunspire", 520, 420, 100, 70, 4, "minecraft:desert"),
            new Region("emberhold", -560, -60, 100, 70, 8, "minecraft:badlands"),
            new Region("tidewatch", 60, 620, 100, 70, 1, "minecraft:beach"));

    /** The region whose plateau shapes this column, or null. */
    public static Region regionAt(int x, int z) {
        for (Region r : REGIONS) if (r.weight(x, z) > 0) return r;
        return null;
    }

    private Mc001Terrain() {}

    /** The authored height at a column, before blending with natural terrain. */
    public static int height(int x, int z) {
        Region region = regionAt(x, z);
        if (region != null) {
            double out = Math.max(0, region.distance(x, z) - region.radius);
            return (int) Math.round(region.height + roll(x, z) * Math.min(1, out / 20.0));
        }
        double h;
        if (z > HILLS_START_Z) {
            double outside = Math.max(0, Math.max(Math.abs(x) - VILLAGE_HALF, z - VILLAGE_HALF));
            h = roll(x, z) * clamp(outside / 20.0);
        } else if (z >= -140) {
            h = hills(x, z);
            // A rocky shoulder at the foot of the hills for the river to fall from.
            double d = Math.hypot(x - RIVER_X, (z - (WATERFALL_Z - 5)) * 0.8);
            if (z <= WATERFALL_Z && d < 12) h += 7 * (1 - d / 12);
        } else {
            double climb = Math.min(46, (-140 - z) * 0.42);
            h = hills(x, -140) + climb + 6 * Math.sin(x / 17.0) * Math.cos(z / 23.0) * clamp((-140 - z) / 30.0);
        }
        // River channel and its banks, from the waterfall to the lake.
        // River channel, with banks that slope gently up to the meadow (steeper through the hills).
        int rd = Math.abs(x - RIVER_X);
        if (z > WATERFALL_Z && z < LAKE_Z) {
            if (rd <= RIVER_HALF_WIDTH) return -2;
            double slope = z > HILLS_START_Z ? 0.35 : 1.1;
            h = Math.min(h, Math.max(0, (rd - RIVER_HALF_WIDTH - 1) * slope));
        }
        double ld = Math.hypot(x - LAKE_X, z - LAKE_Z);
        if (ld <= LAKE_RADIUS) return ld <= LAKE_RADIUS - 4 ? -4 : -2;
        h = Math.min(h, Math.max(0, (ld - LAKE_RADIUS - 1) * 0.35));
        return (int) Math.round(h);
    }

    /** True where the authored terrain is covered by water up to {@link #WATER_TOP}. */
    public static boolean water(int x, int z) {
        if (z > WATERFALL_Z && z < LAKE_Z && Math.abs(x - RIVER_X) <= RIVER_HALF_WIDTH) return true;
        return Math.hypot(x - LAKE_X, z - LAKE_Z) <= LAKE_RADIUS;
    }

    /** How strongly the authored shape applies here: 1 near Firstbrook, 0 in natural terrain. */
    public static double weight(int x, int z) {
        Region region = regionAt(x, z);
        if (region != null) return region.weight(x, z);
        double d = Math.hypot(x, z);
        if (d <= AUTHORED_RADIUS) return 1;
        if (d >= NATURAL_RADIUS) return 0;
        double t = (d - AUTHORED_RADIUS) / (NATURAL_RADIUS - AUTHORED_RADIUS);
        return 1 - t * t * (3 - 2 * t);
    }

    /** The village core, which must stay solid and free of caves, lava and wild growth. */
    public static boolean protectedCore(int x, int z) {
        return Math.abs(x) <= VILLAGE_HALF + 14 && z <= VILLAGE_HALF + 14 && z >= HILLS_START_Z - 14;
    }

    /** Firstbrook itself, where the site builder owns every block (no wild trees or flowers). */
    public static boolean village(int x, int z) {
        return Math.abs(x) <= VILLAGE_HALF && Math.abs(z) <= VILLAGE_HALF;
    }

    /** Ground height in the northern hills (0 in the village), for standing things on the slopes. */
    public static int hillHeight(int x, int z) {
        return z > HILLS_START_Z ? 0 : Math.max(0, height(x, z));
    }

    private static double hills(int x, int z) {
        if (z > HILLS_START_Z) return 0;
        double rise = Math.min(26, (HILLS_START_Z - z) * 0.36);
        double wave = 3 * Math.sin(x / 9.0) + 2 * Math.cos(z / 7.0) + 1.5 * Math.sin((x + z) / 13.0);
        return Math.max(0, rise + wave * Math.min(1, (HILLS_START_Z - z) / 12.0));
    }

    private static double roll(int x, int z) {
        return 1.6 + 1.6 * Math.sin(x / 23.0) + 1.3 * Math.cos(z / 19.0) + 0.9 * Math.sin((x - 2 * z) / 31.0);
    }

    private static double clamp(double v) {
        return Math.max(0, Math.min(1, v));
    }
}
