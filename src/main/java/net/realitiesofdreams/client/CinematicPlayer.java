package net.realitiesofdreams.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.neoforged.neoforge.network.PacketDistributor;
import net.realitiesofdreams.network.CinematicDonePayload;
import net.realitiesofdreams.story.cinematic.CameraPose;
import net.realitiesofdreams.story.cinematic.CinematicDef;
import net.realitiesofdreams.story.cinematic.CinematicTimeline;

import javax.annotation.Nullable;

/**
 * Drives the camera through a cinematic: the view is moved to an invisible camera entity that
 * follows the timeline, while the player's own movement is locked.
 */
public final class CinematicPlayer {
    @Nullable private static CinematicTimeline timeline;
    @Nullable private static Marker camera;
    private static int ax, ay, az;
    private static int tick;

    private CinematicPlayer() {}

    public static boolean active() {
        return timeline != null;
    }

    @Nullable
    static CinematicTimeline timeline() {
        return timeline;
    }

    static double time(float partial) {
        return tick + partial;
    }

    public static void start(CinematicDef def, int anchorX, int anchorY, int anchorZ) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (timeline != null) stop(false);
        timeline = new CinematicTimeline(def);
        ax = anchorX;
        ay = anchorY;
        az = anchorZ;
        tick = 0;
        camera = new Marker(EntityType.MARKER, mc.level);
        place(timeline.sample(0));
        camera.setOldPosAndRot();
        mc.setCameraEntity(camera);
        playSounds(-1, 0);
        if (mc.screen != null && !(mc.screen instanceof DialogueChoiceScreen)) mc.setScreen(null);
    }

    static void tick() {
        if (timeline == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            reset();
            return;
        }
        if (RodKeys.CONTINUE.consumeClick() && timeline.def().skippable()) {
            stop(true);
            return;
        }
        int prev = tick++;
        camera.setOldPosAndRot();
        place(timeline.sample(tick));
        playSounds(prev, tick);
        if (mc.getCameraEntity() != camera) mc.setCameraEntity(camera);
        if (timeline.finished(tick)) stop(true);
    }

    private static void place(CameraPose pose) {
        double[] shake = timeline.shake(tick);
        camera.setPos(ax + pose.x() + shake[0], ay + pose.y() + shake[1], az + pose.z() + shake[2]);
        camera.setYRot(pose.yaw());
        camera.setXRot(pose.pitch());
    }

    private static void playSounds(double from, double to) {
        Minecraft mc = Minecraft.getInstance();
        for (CinematicDef.Subtitle s : timeline.def().subtitles()) {
            if (s.start() > from && s.start() <= to) VoicePlayer.speak(s.speaker(), s.text());
        }
        for (CinematicDef.SoundCue s : timeline.soundsBetween(from, to)) {
            SoundEvent ev = SoundEvent.createVariableRangeEvent(ResourceLocation.parse(s.sound()));
            mc.getSoundManager().play(SimpleSoundInstance.forUI(ev, s.pitch(), s.volume()));
        }
    }

    static void stop(boolean notifyServer) {
        Minecraft mc = Minecraft.getInstance();
        String id = timeline == null ? null : timeline.def().id();
        if (id != null) VoicePlayer.stop();
        timeline = null;
        camera = null;
        if (mc.player != null) mc.setCameraEntity(mc.player);
        if (notifyServer && id != null && mc.getConnection() != null) PacketDistributor.sendToServer(new CinematicDonePayload(id));
    }

    static void reset() {
        VoicePlayer.stop();
        timeline = null;
        camera = null;
    }

    @Nullable
    static Entity camera() {
        return camera;
    }
}
