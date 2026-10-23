package net.realitiesofdreams.story;

import net.realitiesofdreams.story.universe.UniverseCatalog;
import net.realitiesofdreams.story.universe.UniverseId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniverseTest {
    @Test
    void parsesAndFormatsIds() {
        assertEquals(1, UniverseId.parse("MC-001").orElseThrow().number());
        assertEquals("MC-001", UniverseId.parse("mc-1").orElseThrow().toString());
        assertEquals("MC-1323", UniverseId.parse("MC-1323").orElseThrow().toString());
        assertEquals("MC-10000", UniverseId.parse("MC-10000").orElseThrow().toString());
        assertTrue(UniverseId.parse("MC-10001").isEmpty());
        assertTrue(UniverseId.parse("MC-000").isEmpty());
        assertTrue(UniverseId.parse("MC-TBD").isEmpty());
        assertTrue(UniverseId.isValidReference("MC-TBD"));
    }

    @Test
    void profilesAreDeterministicAndCoverAllUniverses() {
        for (int n = UniverseId.MIN; n <= UniverseId.MAX; n++) {
            UniverseCatalog.Profile a = UniverseCatalog.profile(new UniverseId(n));
            assertEquals(a, UniverseCatalog.profile(new UniverseId(n)));
            assertTrue(a.stability() >= 40 && a.stability() <= 100);
        }
        assertTrue(UniverseCatalog.profile(new UniverseId(200)).canonNote().contains("Story Mode"));
        assertTrue(UniverseCatalog.profile(new UniverseId(300)).canonNote().contains("Movie"));
    }
}
