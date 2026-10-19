package net.realitiesofdreams.story;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.dialogue.LineTiming;
import net.realitiesofdreams.story.dialogue.VoiceDurations;
import net.realitiesofdreams.story.dialogue.VoiceKey;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** The voice recordings in assets/rod_voice: every entry has its file, Scenes 001 to 003 are fully voiced. */
class VoiceTest {
    private static JsonObject read(String path) throws Exception {
        try (var in = VoiceTest.class.getResourceAsStream(path)) {
            assertNotNull(in, path + " missing");
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    @Test
    void keyMatchesTheImportTool() {
        assertEquals("v_54f13f7d4cea", VoiceKey.of("primordial_steve", "Another morning."));
    }

    @Test
    void everySoundHasItsRecording() throws Exception {
        JsonObject sounds = read("/assets/rod_voice/sounds.json");
        assertFalse(sounds.isEmpty());
        for (Map.Entry<String, JsonElement> e : sounds.entrySet()) {
            String name = e.getValue().getAsJsonObject().getAsJsonArray("sounds").get(0).getAsJsonObject().get("name").getAsString();
            String path = "/assets/rod_voice/sounds/" + name.substring("rod_voice:".length()) + ".ogg";
            assertNotNull(VoiceTest.class.getResource(path), e.getKey() + " has no " + path);
            assertTrue(name.endsWith("/" + e.getKey()), e.getKey() + " points at " + name);
        }
    }

    @Test
    void scenesAreFullyVoiced() throws Exception {
        JsonObject sounds = read("/assets/rod_voice/sounds.json");
        JsonObject durations = read("/assets/rod_voice/durations.json");
        for (String scene : new String[] {"mc001_s001", "mc001_s002", "mc001_s003", "mc001_s004", "mc001_s005", "mc001_s006", "mc001_s007"}) {
            List<String[]> spoken = new ArrayList<>();
            collect(read("/rod-story/scenes/" + scene + ".json"), spoken);
            assertTrue(spoken.size() >= 10, scene + ": found " + spoken.size() + " spoken lines");
            for (String[] line : spoken) {
                String key = VoiceKey.of(line[0], line[1]);
                assertTrue(sounds.has(key), scene + ": no recording for " + line[0] + ": " + line[1]);
                assertTrue(durations.has(key), scene + ": no duration for " + key);
            }
        }
    }

    @Test
    void voicedLinesStayUpUntilSpoken() {
        String text = "The night watch's report. 'Two tremors before dawn. Light seen beneath the stones of the northern ridge. Gone within moments.'";
        int voice = VoiceDurations.ticks("primordial_steve", text);
        assertTrue(voice > LineTiming.ticksFor(text), "this recording is slower than reading speed");
        assertEquals(voice + LineTiming.PAUSE_TICKS, LineTiming.ticksFor("primordial_steve", text));
        assertEquals(LineTiming.ticksFor("Unrecorded line."), LineTiming.ticksFor("elara", "Unrecorded line."));
    }

    /** Spoken lines: dialogue lines and cinematic subtitles with a speaker, and stage "say" actions. */
    private static void collect(JsonElement e, List<String[]> out) {
        if (e.isJsonArray()) {
            e.getAsJsonArray().forEach(c -> collect(c, out));
        } else if (e.isJsonObject()) {
            JsonObject o = e.getAsJsonObject();
            String who = o.has("speaker") ? o.get("speaker").getAsString() : o.has("character") ? o.get("character").getAsString() : null;
            if (who != null && o.has("text") && o.get("text").isJsonPrimitive()) out.add(new String[] {who, o.get("text").getAsString()});
            o.entrySet().forEach(c -> collect(c.getValue(), out));
        }
    }
}
