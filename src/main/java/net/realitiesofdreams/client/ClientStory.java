package net.realitiesofdreams.client;

import net.realitiesofdreams.network.DialogueLinePayload;
import net.realitiesofdreams.network.ObjectivePayload;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** What the HUD shows: the current conversation line, passing remarks and the quest tracker. */
public final class ClientStory {
    @Nullable static DialogueLinePayload line;
    static long lineStartMs;
    static boolean lineRevealed;
    @Nullable static ObjectivePayload objective;
    static final List<Ambient> ambient = new ArrayList<>();

    record Ambient(DialogueLinePayload line, long startMs, long endMs) {}

    private ClientStory() {}

    static void setLine(DialogueLinePayload p) {
        line = p;
        lineStartMs = System.currentTimeMillis();
        lineRevealed = false;
    }

    static void addAmbient(DialogueLinePayload p) {
        long now = System.currentTimeMillis();
        ambient.removeIf(a -> a.line.speakerId().equals(p.speakerId()));
        ambient.add(new Ambient(p, now, now + p.ticks() * 50L + 1500));
        while (ambient.size() > 3) ambient.remove(0);
    }

    /** Characters revealed so far by the typewriter effect. */
    static int visibleChars() {
        if (line == null) return 0;
        if (lineRevealed) return line.text().length();
        int n = (int) ((System.currentTimeMillis() - lineStartMs) * 55 / 1000);
        return Math.min(line.text().length(), n);
    }

    static void reset() {
        line = null;
        objective = null;
        ambient.clear();
    }
}
