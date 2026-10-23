package net.realitiesofdreams.story.dialogue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * How long each voice recording runs, from assets/rod_voice/durations.json (written by
 * tools/voice/import_voice.py). The server reads it so a voiced line stays up until it has been
 * spoken, even when the recording is slower than reading speed.
 */
public final class VoiceDurations {
    private static Map<String, Double> seconds;

    private VoiceDurations() {}

    /** Ticks the line's recording runs, or 0 when it has none. */
    public static int ticks(String speaker, String text) {
        Double s = load().get(VoiceKey.of(speaker, text));
        return s == null ? 0 : (int) Math.ceil(s * 20);
    }

    private static synchronized Map<String, Double> load() {
        if (seconds == null) {
            seconds = new HashMap<>();
            try (InputStream in = VoiceDurations.class.getResourceAsStream("/assets/rod_voice/durations.json")) {
                if (in != null) {
                    JsonObject o = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
                    for (Map.Entry<String, JsonElement> e : o.entrySet()) seconds.put(e.getKey(), e.getValue().getAsDouble());
                }
            } catch (Exception e) {
                // No timings: lines fall back to reading speed.
            }
        }
        return seconds;
    }
}
