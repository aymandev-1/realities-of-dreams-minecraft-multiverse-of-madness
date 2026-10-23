package net.realitiesofdreams.story;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.saga.SagaIndex;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Data behind the main menu and Firstbrook's placement registry. */
class MenuContentTest {
    private static final StoryContent CONTENT = StoryContent.loadFromClasspath(MenuContentTest.class);

    @Test
    void sagaOneListsItsTwentyScenesInOrder() {
        SagaIndex index = SagaIndex.loadFromClasspath(MenuContentTest.class);
        SagaIndex.Saga s1 = index.sagas().get(0);
        assertEquals(1, s1.number());
        assertEquals("MC-001", s1.universe());
        assertEquals(20, s1.scenes().size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i + 1, s1.scenes().get(i).number());
            assertEquals(String.format("mc001_s%03d", i + 1), s1.scenes().get(i).id());
        }
        // Every released scene's title matches the chapter list.
        for (SagaIndex.SceneEntry e : s1.scenes()) {
            var scene = CONTENT.scene(e.id());
            if (scene != null) assertEquals(e.title(), scene.title(), e.id());
        }
        assertNotNull(CONTENT.scene(e(s1, 1)), "Scene 001 must be playable");
    }

    private static String e(SagaIndex.Saga s, int n) {
        return s.scenes().get(n - 1).id();
    }

    @Test
    void firstbrookLayoutIsConsistent() throws Exception {
        JsonObject o;
        try (var in = MenuContentTest.class.getResourceAsStream("/rod-story/sites/firstbrook.json")) {
            assertNotNull(in);
            o = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
        Set<String> ids = new HashSet<>();
        java.util.List<int[]> buildings = new java.util.ArrayList<>();
        for (JsonElement e : o.getAsJsonArray("pieces")) {
            JsonObject p = e.getAsJsonObject();
            assertTrue(ids.add(p.get("id").getAsString()), "duplicate piece " + p.get("id"));
            if (!p.has("box")) continue;
            var b = p.getAsJsonArray("box");
            int[] box = {b.get(0).getAsInt(), b.get(1).getAsInt(), b.get(2).getAsInt(), b.get(3).getAsInt()};
            assertTrue(box[0] <= box[2] && box[1] <= box[3], p.get("id") + " box is inverted");
            String type = p.get("type").getAsString();
            if (Set.of("house", "steve_house", "workshop", "guard_station", "watchtower", "storehouse", "barn").contains(type)) buildings.add(box);
        }
        // Buildings never overlap one another.
        for (int i = 0; i < buildings.size(); i++)
            for (int j = i + 1; j < buildings.size(); j++) {
                int[] a = buildings.get(i), b = buildings.get(j);
                boolean overlap = a[0] <= b[2] && b[0] <= a[2] && a[1] <= b[3] && b[1] <= a[3];
                assertFalse(overlap, "buildings overlap: " + java.util.Arrays.toString(a) + " and " + java.util.Arrays.toString(b));
            }
        // Every Scene 001 character stands outside every building wall (inside is fine only for interiors).
        var s001 = CONTENT.scene("mc001_s001");
        for (var n : s001.npcs()) {
            var loc = s001.location(n.location());
            for (int[] b : buildings) {
                boolean onWall = (loc.x() == b[0] || loc.x() == b[2]) && loc.z() >= b[1] && loc.z() <= b[3]
                        || (loc.z() == b[1] || loc.z() == b[3]) && loc.x() >= b[0] && loc.x() <= b[2];
                assertFalse(onWall, n.character() + " at " + n.location() + " stands in a wall");
            }
        }
        assertTrue(o.getAsJsonArray("protected").size() >= 5);
    }
}
