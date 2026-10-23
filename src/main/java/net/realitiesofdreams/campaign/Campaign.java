package net.realitiesofdreams.campaign;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.RodConfig;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import net.realitiesofdreams.world.ModDimensions;
import net.realitiesofdreams.world.SceneWorld;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * The Realities of Dreams story save. A story world is an ordinary Minecraft save created by the
 * main menu's New Story button (or a dedicated server with {@code storyServer = true}); players
 * who join it go straight into MC-001 and the story, never the overworld.
 *
 * Each story world carries {@value #MARKER} in its folder: it marks the save as a story world and
 * records the checkpoint the main menu shows under Continue Story. Ordinary worlds are never
 * changed.
 */
public final class Campaign {
    public static final String MARKER = "realitiesofdreams_story.json";
    public static final int FORMAT = 1;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** The folder the client's New Story button is creating, so the server recognises it on first start. */
    @Nullable private static volatile String pendingFolder;
    /** Something the menu asked for when it opened the story save, such as "replay:mc001_s001". */
    @Nullable private static volatile String pendingAction;

    private static boolean storyWorld;
    @Nullable private static MinecraftServer server;

    private Campaign() {}

    // ---------------------------------------------------------------- requests from the main menu

    public static void expectNewStoryWorld(String folder) {
        pendingFolder = folder;
    }

    public static void requestOnJoin(@Nullable String action) {
        pendingAction = action;
    }

    @Nullable
    public static String takeJoinAction() {
        String a = pendingAction;
        pendingAction = null;
        return a;
    }

    public static boolean isStoryWorld() {
        return storyWorld;
    }

    // ---------------------------------------------------------------- server lifecycle

    public static void onServerStarted(MinecraftServer s) {
        server = s;
        Path root = s.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize();
        String folder = root.getFileName() == null ? "" : root.getFileName().toString();
        boolean fresh = folder.equals(pendingFolder) && !Files.exists(root.resolve(MARKER));
        storyWorld = Files.exists(root.resolve(MARKER)) || folder.equals(pendingFolder) || RodConfig.storyServer();
        pendingFolder = null;
        if (!storyWorld) return;
        RealitiesOfDreams.LOGGER.info("Realities of Dreams story world: {}", folder);
        // The story world's terrain is authored modded data; don't ask for a backup every time it opens.
        if (s.getWorldData() instanceof PrimaryLevelData pld) pld.withConfirmedWarning(true);
        if (fresh) {
            // Players never live in the story save's overworld, so it needs no loaded spawn area.
            s.getGameRules().getRule(GameRules.RULE_SPAWN_CHUNK_RADIUS).set(0, s);
            s.getGameRules().getRule(GameRules.RULE_DOINSOMNIA).set(false, s);
        }
        // Build Firstbrook now, while the client still shows the loading screen, so the opening
        // never waits on construction.
        SceneDef first = Story.content().scene(net.realitiesofdreams.server.StoryManager.FIRST_SCENE);
        ServerLevel mc001 = s.getLevel(ModDimensions.MC_001);
        if (first != null && mc001 != null) {
            SceneWorld.ensureBuilt(mc001, first);
            pregenerate(s, mc001, ModDimensions.anchor(first));
        }
        if (!Files.exists(root.resolve(MARKER))) writeMarker(root, null, null);
    }

    /** Chunks around Firstbrook generated before the first visit, so the opening shots see a finished valley. */
    private static final int PREGEN_RADIUS = 9;

    private static void pregenerate(MinecraftServer s, ServerLevel level, net.minecraft.core.BlockPos anchor) {
        CampaignData data = CampaignData.get(s);
        if (data.pregenerated()) return;
        long start = System.currentTimeMillis();
        int cx = anchor.getX() >> 4, cz = anchor.getZ() >> 4;
        for (int x = -PREGEN_RADIUS; x <= PREGEN_RADIUS; x++)
            for (int z = -PREGEN_RADIUS; z <= PREGEN_RADIUS; z++) level.getChunk(cx + x, cz + z);
        data.setPregenerated();
        RealitiesOfDreams.LOGGER.info("Generated MC-001 around Firstbrook in {} ms", System.currentTimeMillis() - start);
    }

    public static void onServerStopped() {
        storyWorld = false;
        server = null;
    }

    /**
     * The campaign's lead player: the single-player host, or on a dedicated story server the first
     * player who joined. The lead is the one who appears as Primordial Steve and whose checkpoint
     * the main menu shows.
     */
    public static boolean isLead(ServerPlayer p) {
        if (server == null) return false;
        if (!server.isDedicatedServer()) return server.isSingleplayerOwner(p.getGameProfile());
        CampaignData data = CampaignData.get(server);
        if (data.lead() == null) data.setLead(p.getUUID());
        return p.getUUID().equals(data.lead());
    }

    @Nullable
    public static UUID lead() {
        return server == null || !server.isDedicatedServer() ? null : CampaignData.get(server).lead();
    }

    /** Records the lead player's checkpoint for the main menu. */
    public static void saveProgress(ServerPlayer p, PlayerStoryState state) {
        if (!storyWorld || server == null || !isLead(p)) return;
        writeMarker(server.getWorldPath(LevelResource.ROOT), p, state);
    }

    /** The latest scene (by saga and number) the player has finished. */
    @Nullable
    public static String lastCompleted(PlayerStoryState state) {
        SceneDef best = null;
        for (String id : state.completedScenes()) {
            SceneDef s = Story.content().scene(id);
            if (s != null && (best == null || s.saga() * 1000 + s.sceneNumber() > best.saga() * 1000 + best.sceneNumber())) best = s;
        }
        return best == null ? null : best.id();
    }

    private static void writeMarker(Path root, @Nullable ServerPlayer p, @Nullable PlayerStoryState state) {
        JsonObject o = new JsonObject();
        o.addProperty("format", FORMAT);
        o.addProperty("mod_version", "0.1.0");
        o.addProperty("universe", "MC-001");
        o.addProperty("saga", 1);
        SceneDef scene = state == null ? null : Story.engine().activeScene(state);
        String last = state == null ? null : lastCompleted(state);
        String sceneId = scene != null ? scene.id() : last != null ? last : net.realitiesofdreams.server.StoryManager.FIRST_SCENE;
        SceneDef shown = Story.content().scene(sceneId);
        o.addProperty("scene", sceneId);
        if (shown != null) {
            o.addProperty("scene_number", shown.sceneNumber());
            o.addProperty("scene_title", shown.title());
        }
        if (state != null && scene != null) {
            o.addProperty("stage", state.stage());
            SceneDef.StageDef stage = scene.stage(state.stage());
            if (stage != null && stage.objective() != null) o.addProperty("objective", stage.objective());
        } else if (last != null) {
            o.addProperty("objective", "Scene complete");
        }
        JsonArray done = new JsonArray();
        if (state != null) state.completedScenes().forEach(done::add);
        o.add("completed_scenes", done);
        if (p != null) o.addProperty("player", p.getGameProfile().getName());
        o.addProperty("last_played", System.currentTimeMillis());
        try {
            Path tmp = root.resolve(MARKER + ".tmp");
            Files.writeString(tmp, GSON.toJson(o), StandardCharsets.UTF_8);
            Files.move(tmp, root.resolve(MARKER), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            RealitiesOfDreams.LOGGER.warn("Could not write the story checkpoint file", e);
        }
    }
}
