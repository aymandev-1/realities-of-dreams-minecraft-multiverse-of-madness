package net.realitiesofdreams.story.dialogue;

/** How long a subtitle line stays up: comfortable reading speed, with sensible bounds. */
public final class LineTiming {
    public static final int MIN_TICKS = 50;
    public static final int MAX_TICKS = 260;
    /** Gap after a recording ends before the next line starts. */
    public static final int PAUSE_TICKS = 15;

    private LineTiming() {}

    public static int ticksFor(String text) {
        int t = 30 + (int) Math.round(text.length() * 1.25);
        return Math.max(MIN_TICKS, Math.min(MAX_TICKS, t));
    }

    /** Reading time, or the length of the line's voice recording plus a short pause if that is longer. */
    public static int ticksFor(String speaker, String text) {
        int voice = VoiceDurations.ticks(speaker, text);
        return voice > 0 ? Math.max(ticksFor(text), voice + PAUSE_TICKS) : ticksFor(text);
    }
}
