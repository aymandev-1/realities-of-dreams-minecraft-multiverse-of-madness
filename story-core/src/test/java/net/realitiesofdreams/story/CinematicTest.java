package net.realitiesofdreams.story;

import net.realitiesofdreams.story.cinematic.CameraPose;
import net.realitiesofdreams.story.cinematic.CinematicDef;
import net.realitiesofdreams.story.cinematic.CinematicTimeline;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CinematicTest {
    final StoryContent content = StoryContent.loadFromClasspath(CinematicTest.class);
    final CinematicDef opening = content.cinematic("mc001_s001_opening");

    @Test
    void pathPassesThroughEveryKeyframe() {
        CinematicTimeline tl = new CinematicTimeline(opening);
        for (CinematicDef.Keyframe k : opening.keyframes()) {
            CameraPose p = tl.sample(k.t());
            assertEquals(k.x(), p.x(), 1e-6);
            assertEquals(k.y(), p.y(), 1e-6);
            assertEquals(k.z(), p.z(), 1e-6);
            assertEquals(0, Math.floorMod(Math.round(k.yaw() - p.yaw()), 360));
        }
    }

    @Test
    void yawTakesTheShortWayRound() {
        CinematicTimeline tl = new CinematicTimeline(opening);
        // The camera turns from the bed (about -139 degrees) to the window (about -148): it must not spin the long way.
        float a = tl.sample(500).yaw(), b = tl.sample(560).yaw();
        for (double t = 500; t <= 560; t += 5) {
            float y = tl.sample(t).yaw();
            float d = Math.abs(Math.floorMod(Math.round(y - a + 180), 360) - 180) + Math.abs(Math.floorMod(Math.round(y - b + 180), 360) - 180);
            assertTrue(d <= Math.abs(Math.floorMod(Math.round(b - a + 180), 360) - 180) + 2, "yaw " + y + " at t=" + t);
        }
    }

    @Test
    void cameraMovesSmoothlyExceptTheCutIntoTheHouse() {
        CinematicTimeline tl = new CinematicTimeline(opening);
        CameraPose prev = tl.sample(0);
        for (double t = 0.5; t <= opening.duration(); t += 0.5) {
            CameraPose p = tl.sample(t);
            double step = Math.sqrt(Math.pow(p.x() - prev.x(), 2) + Math.pow(p.y() - prev.y(), 2) + Math.pow(p.z() - prev.z(), 2));
            if (step >= 0.6) {
                // The only jump is the cut through the wall, and the screen is black when it happens.
                assertTrue(t > 399 && t <= 401, "jump of " + step + " at t=" + t);
                assertTrue(tl.fadeAlpha(t) > 0.9f, "cut visible at t=" + t);
            }
            prev = p;
        }
    }

    @Test
    void openingStartsBlackAndEndsAtSteveEyes() {
        CinematicTimeline tl = new CinematicTimeline(opening);
        assertEquals(1f, tl.fadeAlpha(0), 1e-6);
        assertEquals(1f, tl.fadeAlpha(59), 1e-6);
        assertEquals(0f, tl.fadeAlpha(100), 1e-6);
        assertEquals(1f, tl.fadeAlpha(400), 1e-6);
        assertEquals(0f, tl.fadeAlpha(600), 1e-6);
        CameraPose end = tl.sample(opening.duration());
        assertEquals(18.5, end.x(), 1e-6);
        assertEquals(15.5, end.z(), 1e-6);
        assertEquals("primordial_steve", tl.subtitlesAt(500).get(0).speaker());
        assertEquals("Another morning.", tl.subtitlesAt(500).get(0).text());
        assertEquals("elara", tl.subtitlesAt(600).get(0).speaker());
        assertTrue(opening.hidePlayer());
        assertEquals(1, tl.cuesBetween(-1, 0).size(), "the actor is put to bed on the first tick");
        assertEquals(5, tl.soundsBetween(0, 50).size());
        assertTrue(tl.finished(opening.duration()));
    }

    @Test
    void ridgeShotShakesAndRumbles() {
        CinematicTimeline tl = new CinematicTimeline(content.cinematic("mc001_s001_ridge"));
        assertNotEquals(0, tl.shake(30)[1]);
        assertEquals(0, tl.shake(100)[0]);
        assertEquals(3, tl.soundsBetween(0, 60).size());
        assertEquals(2, tl.cuesBetween(0, 60).size());
    }
}
