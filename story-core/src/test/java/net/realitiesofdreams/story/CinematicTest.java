package net.realitiesofdreams.story;

import net.realitiesofdreams.story.cinematic.CameraPose;
import net.realitiesofdreams.story.cinematic.CinematicDef;
import net.realitiesofdreams.story.cinematic.CinematicTimeline;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CinematicTest {
    final StoryContent content = StoryContent.loadFromClasspath(CinematicTest.class);

    @Test
    void pathPassesThroughEveryKeyframe() {
        CinematicDef def = content.cinematic("mc001_s001_opening");
        CinematicTimeline tl = new CinematicTimeline(def);
        for (CinematicDef.Keyframe k : def.keyframes()) {
            CameraPose p = tl.sample(k.t());
            assertEquals(k.x(), p.x(), 1e-6);
            assertEquals(k.y(), p.y(), 1e-6);
            assertEquals(k.z(), p.z(), 1e-6);
            assertEquals(0, Math.floorMod(Math.round(k.yaw() - p.yaw()), 360));
        }
    }

    @Test
    void yawTakesTheShortWayRound() {
        CinematicTimeline tl = new CinematicTimeline(content.cinematic("mc001_s001_opening"));
        // Between keyframes at 150.9 and 191.9 degrees the camera must stay between them.
        float mid = tl.sample(130).yaw();
        assertTrue(mid > 150 && mid < 192, "yaw " + mid);
    }

    @Test
    void cameraMovesSmoothly() {
        CinematicTimeline tl = new CinematicTimeline(content.cinematic("mc001_s001_opening"));
        CameraPose prev = tl.sample(0);
        for (double t = 0.5; t <= 260; t += 0.5) {
            CameraPose p = tl.sample(t);
            double step = Math.sqrt(Math.pow(p.x() - prev.x(), 2) + Math.pow(p.y() - prev.y(), 2) + Math.pow(p.z() - prev.z(), 2));
            assertTrue(step < 0.6, "jump of " + step + " at t=" + t);
            prev = p;
        }
    }

    @Test
    void fadesSubtitlesShakeAndSounds() {
        CinematicTimeline tl = new CinematicTimeline(content.cinematic("mc001_s001_tremor"));
        assertEquals(1f, tl.fadeAlpha(0), 1e-6);
        assertEquals(0f, tl.fadeAlpha(60), 1e-6);
        assertEquals(1f, tl.fadeAlpha(140), 1e-6);
        assertEquals("elara", tl.subtitlesAt(30).get(0).speaker());
        assertTrue(tl.subtitlesAt(5).isEmpty());
        assertNotEquals(0, tl.shake(20)[1]);
        assertEquals(0, tl.shake(75)[0]);
        assertEquals(2, tl.soundsBetween(0, 20).size());
        assertTrue(tl.finished(140));
    }
}
