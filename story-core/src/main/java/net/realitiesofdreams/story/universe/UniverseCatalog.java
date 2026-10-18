package net.realitiesofdreams.story.universe;

import java.util.Map;

/**
 * Deterministic profile for each of the 10,000 universes, so any universe can be generated
 * the same way every time. Canon universes carry their established descriptions.
 */
public final class UniverseCatalog {
    private static final Map<Integer, String> CANON = Map.of(
            1, "Where the Minecraft Multiverse Federation is founded",
            100, "Home of Steve Prime",
            200, "Minecraft: Story Mode (Jesse and Reuben)",
            300, "The Movie universe (Movie Steve)");

    private static final String[] THEMES = {"plains", "forest", "taiga", "desert", "badlands", "jungle", "ocean",
            "mountain", "swamp", "snowy", "mushroom", "cherry grove", "archipelago", "canyon", "savanna", "ruined"};

    private UniverseCatalog() {}

    public record Profile(UniverseId id, long seed, String theme, int stability, String canonNote) {
        public boolean isCanon() {
            return canonNote != null;
        }
    }

    public static Profile profile(UniverseId id) {
        long seed = mix(0x5EEDL * 31 + id.number());
        String theme = THEMES[(int) Long.remainderUnsigned(seed, THEMES.length)];
        int stability = 40 + (int) Long.remainderUnsigned(seed >>> 17, 61);
        return new Profile(id, seed, theme, stability, CANON.get(id.number()));
    }

    private static long mix(long z) {
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
