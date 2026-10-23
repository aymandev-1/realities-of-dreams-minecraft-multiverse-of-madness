package net.realitiesofdreams.story;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.world.Mc001Terrain;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The game-test server only creates vanilla dimensions, so the MC-001 dimension files are checked
 * here: MC-001 uses the mod's own generator and biome source, and the scenes' anchor matches the
 * authored terrain (village ground at y = 63, the anchor y = 64 standing on it).
 */
class DimensionDataTest {
    private static JsonObject read(String path) throws Exception {
        try (var in = DimensionDataTest.class.getResourceAsStream(path)) {
            assertNotNull(in, path + " missing");
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    @Test
    void mc001UsesTheAuthoredGenerator() throws Exception {
        JsonObject type = read("/data/realitiesofdreams/dimension_type/mc_001.json");
        JsonObject dim = read("/data/realitiesofdreams/dimension/mc_001.json");
        assertEquals("realitiesofdreams:mc_001", dim.get("type").getAsString());
        JsonObject gen = dim.getAsJsonObject("generator");
        assertEquals("realitiesofdreams:mc001", gen.get("type").getAsString());
        assertEquals("minecraft:overworld", gen.get("settings").getAsString(), "MC-001 keeps normal Minecraft terrain, caves and ores");
        JsonObject biomes = gen.getAsJsonObject("biome_source");
        assertEquals("realitiesofdreams:mc001", biomes.get("type").getAsString());
        for (String k : new String[] {"wilds", "village", "hills", "mountains", "woodland", "valley"}) assertTrue(biomes.has(k), "biome " + k);
        assertTrue(type.get("min_y").getAsInt() <= Mc001Terrain.ANCHOR_Y - 64 && type.get("height").getAsInt() >= 384);
    }

    @Test
    void scenesAreAnchoredOnTheAuthoredTerrain() throws Exception {
        JsonObject s001 = read("/rod-story/scenes/mc001_s001.json").getAsJsonObject("scene");
        var anchor = s001.getAsJsonArray("anchor");
        assertEquals(Mc001Terrain.ANCHOR_X, anchor.get(0).getAsInt());
        assertEquals(Mc001Terrain.ANCHOR_Y, anchor.get(1).getAsInt());
        assertEquals(Mc001Terrain.ANCHOR_Z, anchor.get(2).getAsInt());
    }
}
