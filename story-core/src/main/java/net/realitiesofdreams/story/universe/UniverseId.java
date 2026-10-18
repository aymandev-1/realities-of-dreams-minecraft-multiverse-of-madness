package net.realitiesofdreams.story.universe;

import java.util.Locale;
import java.util.Optional;

/**
 * A Minecraft universe identifier such as {@code MC-001}. The multiverse holds universes
 * MC-001 to MC-10000; {@code MC-TBD} marks a character whose universe is not decided yet.
 */
public record UniverseId(int number) {
    public static final int MIN = 1;
    public static final int MAX = 10_000;
    public static final String TBD = "MC-TBD";

    public UniverseId {
        if (number < MIN || number > MAX) throw new IllegalArgumentException("universe out of range: " + number);
    }

    public static Optional<UniverseId> parse(String text) {
        if (text == null) return Optional.empty();
        String t = text.trim().toUpperCase(Locale.ROOT);
        if (!t.startsWith("MC-")) return Optional.empty();
        try {
            int n = Integer.parseInt(t.substring(3));
            return n >= MIN && n <= MAX ? Optional.of(new UniverseId(n)) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /** True for a real universe id or the MC-TBD placeholder. */
    public static boolean isValidReference(String text) {
        return TBD.equalsIgnoreCase(text) || parse(text).isPresent();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "MC-%03d", number);
    }
}
