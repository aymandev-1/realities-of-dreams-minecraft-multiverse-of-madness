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
        long generated = content.characters().all().stream().filter(CharacterDef::generated).count();
        assertEquals(20, generated);
        assertTrue(content.characters().all().stream().filter(CharacterDef::generated).allMatch(c -> "TMMA".equals(c.faction())));
    }

    @Test
    void mc001CastIsPresent() {
        assertTrue(content.characters().inUniverse("MC-001").size() >= 12);
    }
}
