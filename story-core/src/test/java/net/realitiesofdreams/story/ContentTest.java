package net.realitiesofdreams.story;

import net.realitiesofdreams.story.character.CharacterDef;
import net.realitiesofdreams.story.character.CharacterRegistry;
import net.realitiesofdreams.story.scene.SceneValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContentTest {
    static StoryContent content;

    @BeforeAll
    static void load() {
        content = StoryContent.loadFromClasspath(ContentTest.class);
    }

    @Test
    void storyContentHasNoBrokenReferences() {
        List<String> errors = SceneValidator.validate(content);
        assertTrue(errors.isEmpty(), () -> String.join("\n", errors));
    }

    @Test
    void ownerIdentityCorrectionsAreApplied() {
        CharacterRegistry r = content.characters();
        assertEquals("MC-200", r.require("jesse").universe());
        assertEquals("MC-300", r.require("movie_steve").universe());
        CharacterDef reuben = r.require("reuben");
        assertEquals("MC-200", reuben.universe());
        assertEquals("minecraft:pig", reuben.entity());
        assertTrue(reuben.role().contains("Jesse's pig"));
    }

    @Test
    void everySkinTextureExists() {
        Path assets = Path.of("..", "src", "main", "resources", "assets");
        for (CharacterDef c : content.characters().all()) {
            if (!c.usesPlayerModel()) continue;
            String[] parts = c.texture().split(":", 2);
            Path tex = assets.resolve(parts[0]).resolve(parts[1]);
            assertTrue(Files.isRegularFile(tex), "missing texture for " + c.id() + ": " + tex);
            assertNotNull(c.model(), c.id());
        }
    }

    @Test
    void registryCoversAllCollectedSkinsAndGeneratedTmma() {
        // 76 collected files, minus one byte-identical TMMA Crew duplicate, plus Reuben (no skin).
        long collected = content.characters().all().stream().filter(c -> !c.generated()).count();
        assertEquals(76, collected);
        long tmma = content.characters().all().stream().filter(c -> c.generated() && "TMMA".equals(c.faction())).count();
        assertEquals(20, tmma);
        long saga1Npcs = content.characters().all().stream().filter(c -> c.generated() && "MC-001 Saga 1 NPCs".equals(c.group())).count();
        assertEquals(95, saga1Npcs);
    }

    @Test
    void saga1CastIsTheElevenNamedSkinsPlusGeneratedNpcs() {
        List<String> named = List.of("primordial_steve", "elara", "elder_rowan", "orin", "bram", "liora", "queen_aveline",
                "thane_garrick", "sahir", "lord_kaelen", "captain_maren");
        List<String> cast = content.characters().all().stream().filter(c -> c.castIn(1) && !c.generated()).map(CharacterDef::id).toList();
        assertEquals(new java.util.TreeSet<>(named), new java.util.TreeSet<>(cast));
        // Saga 1 uses Primordial Steve's normal classic appearance; the Empowered form is not cast.
        assertFalse(content.characters().require("primordial_steve_empowered").castIn(1));
        assertEquals("Steve", content.characters().displayName("primordial_steve"));
        assertTrue(content.characters().all().stream().filter(CharacterDef::generated).filter(c -> c.castIn(1))
                .allMatch(c -> "MC-001".equals(c.universe())));
    }

    @Test
    void mc001CastIsPresent() {
        assertTrue(content.characters().inUniverse("MC-001").size() >= 12);
    }

    @Test
    void aSaga1SceneCannotUseALaterSagaCharacter() {
        String bundle = """
                {"scene": {"id": "cast_probe", "title": "Probe", "universe": "MC-001", "saga": 1, "scene": 99,
                  "anchor": [0, 64, 0], "arrival": {"pos": [0, 0, 0]}, "locations": {"here": {"pos": [0, 0, 0]}},
                  "npcs": [{"character": "steve_prime", "location": "here"},
                           {"character": "primordial_steve_empowered", "location": "here"},
                           {"character": "elara", "location": "here"}],
                  "stages": [{"id": "a", "complete": {"type": "reach", "value": "here"}}]}}
                """;
        StoryContent probe = StoryContent.load(path -> switch (path) {
            case "index.json" -> new java.io.StringReader("{\"scenes\": [\"probe.json\"]}");
            case "scenes/probe.json" -> new java.io.StringReader(bundle);
            default -> new java.io.InputStreamReader(ContentTest.class.getResourceAsStream(StoryContent.ROOT + path), java.nio.charset.StandardCharsets.UTF_8);
        });
        List<String> errors = SceneValidator.validate(probe);
        assertTrue(errors.contains("scene cast_probe: steve_prime is not cast in Saga 1"), errors::toString);
        assertTrue(errors.contains("scene cast_probe: primordial_steve_empowered is not cast in Saga 1"), errors::toString);
        assertFalse(errors.stream().anyMatch(e -> e.contains("elara")), errors::toString);
    }
}
