package net.realitiesofdreams.client;

import net.minecraft.client.player.Input;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.network.DialogueSkipPayload;
import net.realitiesofdreams.story.cinematic.CameraPose;
import net.realitiesofdreams.story.cinematic.CinematicTimeline;

@EventBusSubscriber(modid = RealitiesOfDreams.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    private static boolean readySent;
    private static int readyWait;

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        ClientStory.tickShake();
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (!readySent && mc.player != null && mc.getConnection() != null && !(mc.screen instanceof net.minecraft.client.gui.screens.ReceivingLevelScreen)
                && !(mc.screen instanceof net.minecraft.client.gui.screens.LevelLoadingScreen)) {
            // Give the world a moment to appear around the player before the story goes on.
            readyWait++;
            int view = mc.options.getEffectiveRenderDistance();
            boolean chunksIn = mc.level.getChunkSource().getLoadedChunksCount() >= 0.7 * (2 * view + 1) * (2 * view + 1);
            if ((readyWait > 10 && chunksIn && mc.levelRenderer.hasRenderedAllSections()) || readyWait > 400) {
                readySent = true;
                PacketDistributor.sendToServer(new net.realitiesofdreams.network.ClientReadyPayload());
            }
        }
        if (CinematicPlayer.active()) {
            CinematicPlayer.tick();
            return;
        }
        while (RodKeys.HELP.consumeClick()) {
            if (mc.screen == null && mc.player != null) mc.setScreen(new StoryHelpScreen());
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

    /** The cinematic camera sees Steve from outside, so his own name tag must not float above him. */
    @SubscribeEvent
    public static void onNameTag(RenderNameTagEvent event) {
        if (CinematicPlayer.active() && event.getEntity() == net.minecraft.client.Minecraft.getInstance().player) {
            event.setCanRender(TriState.FALSE);
        }
    }

    /** During cinematics only our overlay is drawn: no hotbar, crosshair or chat. */
    @SubscribeEvent
    public static void onGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (CinematicPlayer.active() && !event.getName().equals(ClientSetup.CINEMATIC_LAYER)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        readySent = false;
        readyWait = 0;
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        CinematicPlayer.reset();
        ClientStory.reset();
        ClientAppearance.reset();
        readySent = false;
    }

    /** The pause menu gets a "Story help" button while a story objective is showing. */
    @SubscribeEvent
    public static void onScreenInit(net.neoforged.neoforge.client.event.ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof net.minecraft.client.gui.screens.PauseScreen pause) || !pause.showsPauseMenu()) return;
        if (ClientStory.objective == null || ClientStory.objective.text().isEmpty()) return;
        event.addListener(net.minecraft.client.gui.components.Button.builder(net.minecraft.network.chat.Component.literal("Story help"),
                b -> net.minecraft.client.Minecraft.getInstance().setScreen(new StoryHelpScreen())).bounds(8, 8, 90, 20).build());
    }
}
