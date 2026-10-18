package net.realitiesofdreams.story;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The game-test server only creates vanilla dimensions, so the MC-001 dimension files are checked
 * here: the scene layout assumes the grass surface is at y=63 (anchor y=64 stands on it).
 */
class DimensionDataTest {
    private static JsonObject read(String path) throws Exception {
        try (var in = DimensionDataTest.class.getResourceAsStream(path)) {
            assertNotNull(in, path + " missing");
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    @Test
    void mc001GroundIsAtSceneAnchor() throws Exception {
        JsonObject type = read("/data/realitiesofdreams/dimension_type/mc_001.json");
        JsonObject dim = read("/data/realitiesofdreams/dimension/mc_001.json");
        assertEquals("realitiesofdreams:mc_001", dim.get("type").getAsString());

        int minY = type.get("min_y").getAsInt();
        int height = 0;
        String top = null;
        for (var layer : dim.getAsJsonObject("generator").getAsJsonObject("settings").getAsJsonArray("layers")) {
            height += layer.getAsJsonObject().get("height").getAsInt();
            top = layer.getAsJsonObject().get("block").getAsString();
        }
        assertEquals("minecraft:grass_block", top);
        assertEquals(63, minY + height - 1, "grass surface y");
        assertTrue(type.get("height").getAsInt() >= height);
    }
}
