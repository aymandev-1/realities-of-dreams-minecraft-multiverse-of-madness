package net.realitiesofdreams.client;

import net.minecraft.client.player.Input;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.network.DialogueSkipPayload;
import net.realitiesofdreams.story.cinematic.CameraPose;
import net.realitiesofdreams.story.cinematic.CinematicTimeline;

@EventBusSubscriber(modid = RealitiesOfDreams.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        ClientStory.tickShake();
        if (CinematicPlayer.active()) {
            CinematicPlayer.tick();
            return;
        }
        while (RodKeys.CONTINUE.consumeClick()) {
            if (ClientStory.line == null || !ClientStory.line.choices().isEmpty()) continue;
            if (ClientStory.visibleChars() < ClientStory.line.text().length()) {
                ClientStory.lineRevealed = true; // first press finishes the line
            } else {
                PacketDistributor.sendToServer(new DialogueSkipPayload(ClientStory.line.session()));
            }
        }
    }

    /** Smooth per-frame camera rotation while a cinematic plays. */
    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        CinematicTimeline tl = CinematicPlayer.timeline();
        if (tl == null) {
            if (ClientStory.shakeTicks > 0) {
                // A tremor during play: a small, fading wobble of the view.
                double t = ClientStory.shakeTicks - event.getPartialTick();
                double amp = ClientStory.shakeIntensity * (t / ClientStory.shakeTotal);
                event.setYaw((float) (event.getYaw() + amp * Math.sin(t * 2.1) * 2.0));
                event.setPitch((float) (event.getPitch() + amp * Math.sin(t * 2.9 + 1) * 1.5));
                event.setRoll((float) (event.getRoll() + amp * Math.sin(t * 1.7) * 3.0));
            }
            return;
        }
        double t = CinematicPlayer.time((float) event.getPartialTick());
        CameraPose pose = tl.sample(t);
        event.setYaw(pose.yaw());
        event.setPitch(pose.pitch());
        event.setRoll(pose.roll() + (float) tl.shake(t)[3]);
    }

    @SubscribeEvent
    public static void onFov(ViewportEvent.ComputeFov event) {
        CinematicTimeline tl = CinematicPlayer.timeline();
        if (tl != null) event.setFOV(tl.sample(CinematicPlayer.time((float) event.getPartialTick())).fov());
    }

    @SubscribeEvent
    public static void onMovement(MovementInputUpdateEvent event) {
        if (!CinematicPlayer.active()) return;
        Input in = event.getInput();
        in.forwardImpulse = 0;
        in.leftImpulse = 0;
        in.up = in.down = in.left = in.right = in.jumping = in.shiftKeyDown = false;
    }

    @SubscribeEvent
    public static void onHand(RenderHandEvent event) {
        if (CinematicPlayer.active()) event.setCanceled(true);
    }

    /** During cinematics only our overlay is drawn: no hotbar, crosshair or chat. */
    @SubscribeEvent
    public static void onGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (CinematicPlayer.active() && !event.getName().equals(ClientSetup.CINEMATIC_LAYER)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        CinematicPlayer.reset();
        ClientStory.reset();
    }
}
