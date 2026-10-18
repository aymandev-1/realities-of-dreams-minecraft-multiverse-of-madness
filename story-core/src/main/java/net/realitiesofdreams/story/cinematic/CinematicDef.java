package net.realitiesofdreams.story.cinematic;

import com.google.gson.JsonObject;
import net.realitiesofdreams.story.action.ActionDef;
import net.realitiesofdreams.story.json.Json;
import net.realitiesofdreams.story.json.StoryFormatException;

import java.util.List;

/** A scripted camera sequence. Positions are relative to the scene anchor; times are in ticks. */
public record CinematicDef(String id, int duration, List<Keyframe> keyframes, List<Subtitle> subtitles,
                           List<Shake> shakes, List<SoundCue> sounds, int fadeIn, int fadeOut, boolean letterbox,
                           boolean skippable, int holdBlack, List<Dip> dips, List<Cue> cues, boolean hidePlayer) {

    /** A brief dip to black, used to cut the camera to a new place. */
    public record Dip(double start, double duration) {
        static Dip fromJson(JsonObject o) {
            return new Dip(Json.number(o, "t", 0), Json.number(o, "duration", 20));
        }
    }

    /** Story actions the server runs at a moment of the cinematic (an actor wakes, a character walks in). */
    public record Cue(double t, List<ActionDef> actions) {
        static Cue fromJson(JsonObject o) {
            return new Cue(Json.number(o, "t", 0), ActionDef.listFrom(o, "actions"));
        }
    }

    public record Keyframe(double t, double x, double y, double z, float yaw, float pitch, float roll, float fov) {
        static Keyframe fromJson(JsonObject o) {
            double[] p = Json.vec3(o, "pos", "keyframe");
            return new Keyframe(Json.number(o, "t", 0), p[0], p[1], p[2], (float) Json.number(o, "yaw", 0),
                    (float) Json.number(o, "pitch", 0), (float) Json.number(o, "roll", 0), (float) Json.number(o, "fov", 70));
        }
    }

    /** style: "subtitle" (speaker line), "title" (big centred card) or "caption" (location caption). */
    public record Subtitle(double start, double duration, String speaker, String text, String style) {
        static Subtitle fromJson(JsonObject o) {
            return new Subtitle(Json.number(o, "t", 0), Json.number(o, "duration", 60), Json.str(o, "speaker", null),
                    Json.req(o, "text", "subtitle"), Json.str(o, "style", "subtitle"));
        }

        public boolean activeAt(double t) {
            return t >= start && t < start + duration;
        }
    }

    public record Shake(double start, double duration, double intensity) {
        static Shake fromJson(JsonObject o) {
            return new Shake(Json.number(o, "t", 0), Json.number(o, "duration", 20), Json.number(o, "intensity", 0.3));
        }
    }

    public record SoundCue(double t, String sound, float volume, float pitch) {
        static SoundCue fromJson(JsonObject o) {
            return new SoundCue(Json.number(o, "t", 0), Json.req(o, "sound", "sound cue"),
                    (float) Json.number(o, "volume", 1), (float) Json.number(o, "pitch", 1));
        }
    }

    public static CinematicDef fromJson(String id, JsonObject o) {
        List<Keyframe> kf = Json.list(o, "keyframes", Keyframe::fromJson);
        if (kf.size() < 2) throw new StoryFormatException("cinematic " + id + " needs at least two keyframes");
        for (int i = 1; i < kf.size(); i++) {
            if (kf.get(i).t() <= kf.get(i - 1).t()) throw new StoryFormatException("cinematic " + id + ": keyframe times must increase");
        }
        int duration = Json.integer(o, "duration", (int) Math.ceil(kf.get(kf.size() - 1).t()));
        return new CinematicDef(id, duration, kf, Json.list(o, "subtitles", Subtitle::fromJson),
                Json.list(o, "shakes", Shake::fromJson), Json.list(o, "sounds", SoundCue::fromJson),
                Json.integer(o, "fade_in", 20), Json.integer(o, "fade_out", 20), Json.bool(o, "letterbox", true),
                Json.bool(o, "skippable", true), Json.integer(o, "hold_black", 0), Json.list(o, "dips", Dip::fromJson),
                Json.list(o, "cues", Cue::fromJson), Json.bool(o, "hide_player", false));
    }
}
