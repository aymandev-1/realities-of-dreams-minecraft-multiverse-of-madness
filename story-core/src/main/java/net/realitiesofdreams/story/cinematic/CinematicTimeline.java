package net.realitiesofdreams.story.cinematic;

import java.util.ArrayList;
import java.util.List;

/**
 * Samples a {@link CinematicDef} at any time: Catmull-Rom camera paths with eased timing,
 * shortest-path rotation, fades, subtitles, camera shake and sound cues.
 */
public final class CinematicTimeline {
    private final CinematicDef def;

    public CinematicTimeline(CinematicDef def) {
        this.def = def;
    }

    public CinematicDef def() {
        return def;
    }

    public boolean finished(double t) {
        return t >= def.duration();
    }

    public CameraPose sample(double t) {
        List<CinematicDef.Keyframe> k = def.keyframes();
        if (t <= k.get(0).t()) return pose(k.get(0));
        CinematicDef.Keyframe last = k.get(k.size() - 1);
        if (t >= last.t()) return pose(last);
        int i = 0;
        while (k.get(i + 1).t() <= t) i++;
        CinematicDef.Keyframe a = k.get(i), b = k.get(i + 1);
        CinematicDef.Keyframe p0 = k.get(Math.max(0, i - 1)), p3 = k.get(Math.min(k.size() - 1, i + 2));
        double u = ease((t - a.t()) / (b.t() - a.t()));
        return new CameraPose(
                catmull(p0.x(), a.x(), b.x(), p3.x(), u),
                catmull(p0.y(), a.y(), b.y(), p3.y(), u),
                catmull(p0.z(), a.z(), b.z(), p3.z(), u),
                lerpAngle(a.yaw(), b.yaw(), u),
                (float) lerp(a.pitch(), b.pitch(), u),
                (float) lerp(a.roll(), b.roll(), u),
                (float) lerp(a.fov(), b.fov(), u));
    }

    /** 0 = clear, 1 = black. Opens on black for hold_black ticks, then fades in. */
    public float fadeAlpha(double t) {
        double alpha = 0;
        if (t < def.holdBlack()) return 1f;
        double in = t - def.holdBlack();
        if (def.fadeIn() > 0 && in < def.fadeIn()) alpha = 1 - in / def.fadeIn();
        double outStart = def.duration() - def.fadeOut();
        if (def.fadeOut() > 0 && t > outStart) alpha = Math.max(alpha, Math.min(1, (t - outStart) / def.fadeOut()));
        for (CinematicDef.Dip d : def.dips()) {
            double half = d.duration() / 2, mid = d.start() + half;
            if (t >= d.start() && t <= d.start() + d.duration()) alpha = Math.max(alpha, 1 - Math.abs(t - mid) / half * 0.999);
        }
        return (float) alpha;
    }

    /** Cues with from &lt; t &lt;= to (cues at t = 0 fire on the first step). */
    public List<CinematicDef.Cue> cuesBetween(double from, double to) {
        List<CinematicDef.Cue> out = new ArrayList<>();
        for (CinematicDef.Cue c : def.cues()) {
            if ((c.t() > from || (from < 0 && c.t() == 0)) && c.t() <= to) out.add(c);
        }
        return out;
    }

    public List<CinematicDef.Subtitle> subtitlesAt(double t) {
        List<CinematicDef.Subtitle> out = new ArrayList<>();
        for (CinematicDef.Subtitle s : def.subtitles()) if (s.activeAt(t)) out.add(s);
        return out;
    }

    /** Camera offset {x, y, z, rollDegrees} for active shakes. */
    public double[] shake(double t) {
        double x = 0, y = 0, z = 0, roll = 0;
        for (CinematicDef.Shake s : def.shakes()) {
            if (t < s.start() || t >= s.start() + s.duration()) continue;
            double life = (t - s.start()) / s.duration();
            double amp = s.intensity() * (1 - life) * (1 - life);
            x += amp * Math.sin(t * 1.9 + 0.3) * 0.6;
            y += amp * Math.sin(t * 2.7 + 1.1) * 0.4;
            z += amp * Math.sin(t * 2.3 + 2.0) * 0.6;
            roll += amp * Math.sin(t * 1.3) * 4;
        }
        return new double[] {x, y, z, roll};
    }

    /** Sound cues with from &lt; t &lt;= to (cues at t = 0 fire on the first step). */
    public List<CinematicDef.SoundCue> soundsBetween(double from, double to) {
        List<CinematicDef.SoundCue> out = new ArrayList<>();
        for (CinematicDef.SoundCue s : def.sounds()) {
            if ((s.t() > from || (from <= 0 && s.t() == 0)) && s.t() <= to) out.add(s);
        }
        return out;
    }

    private static CameraPose pose(CinematicDef.Keyframe k) {
        return new CameraPose(k.x(), k.y(), k.z(), k.yaw(), k.pitch(), k.roll(), k.fov());
    }

    static double ease(double u) {
        return u * u * (3 - 2 * u);
    }

    static double lerp(double a, double b, double u) {
        return a + (b - a) * u;
    }

    static float lerpAngle(float a, float b, double u) {
        double d = ((b - a) % 360 + 540) % 360 - 180;
        return (float) (a + d * u);
    }

    static double catmull(double p0, double p1, double p2, double p3, double u) {
        double u2 = u * u, u3 = u2 * u;
        return 0.5 * ((2 * p1) + (-p0 + p2) * u + (2 * p0 - 5 * p1 + 4 * p2 - p3) * u2 + (-p0 + 3 * p1 - 3 * p2 + p3) * u3);
    }
}
