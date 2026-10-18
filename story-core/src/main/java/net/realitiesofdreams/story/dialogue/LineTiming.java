package net.realitiesofdreams.story.dialogue;

/** How long a subtitle line stays up: comfortable reading speed, with sensible bounds. */
public final class LineTiming {
    public static final int MIN_TICKS = 50;
    public static final int MAX_TICKS = 260;

    private LineTiming() {}

    public static int ticksFor(String text) {
        int t = 30 + (int) Math.round(text.length() * 1.25);
        return Math.max(MIN_TICKS, Math.min(MAX_TICKS, t));
    }
}
