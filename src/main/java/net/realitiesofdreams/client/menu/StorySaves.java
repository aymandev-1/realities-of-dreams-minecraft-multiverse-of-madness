package net.realitiesofdreams.client.menu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.campaign.Campaign;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The single Realities of Dreams story save, kept in its own world folder next to the player's
 * ordinary saves. New Story creates it, Continue Story opens it. Ordinary worlds are never opened,
 * changed or deleted; a story save being replaced is renamed to a dated backup, not deleted.
 */
public final class StorySaves {
    public static final String FOLDER = "realities_of_dreams_mc001";
    /** Every new story generates the same MC-001 beyond Firstbrook. */
    public static final long SEED = 1_001L;

    private StorySaves() {}

    /** What the main menu shows about the story save: where the player is up to. */
    public record Checkpoint(String sceneId, int sceneNumber, String sceneTitle, @Nullable String objective, Set<String> completed, long lastPlayed) {
        public String label() {
            return String.format("Scene %03d · %s", sceneNumber, sceneTitle);
        }
    }

    private static LevelStorageSource source() {
        return Minecraft.getInstance().getLevelSource();
    }

    private static Path folder() {
        return source().getBaseDir().resolve(FOLDER);
    }

    public static boolean exists() {
        return Files.isRegularFile(folder().resolve("level.dat"));
    }

    @Nullable
    public static Checkpoint checkpoint() {
        Path marker = folder().resolve(Campaign.MARKER);
        if (!exists() || !Files.isRegularFile(marker)) return null;
        try {
            JsonObject o = JsonParser.parseString(Files.readString(marker, StandardCharsets.UTF_8)).getAsJsonObject();
            Set<String> done = new LinkedHashSet<>();
            if (o.has("completed_scenes")) for (JsonElement e : o.getAsJsonArray("completed_scenes")) done.add(e.getAsString());
            return new Checkpoint(o.get("scene").getAsString(), o.has("scene_number") ? o.get("scene_number").getAsInt() : 1,
                    o.has("scene_title") ? o.get("scene_title").getAsString() : "", o.has("objective") ? o.get("objective").getAsString() : null,
                    done, o.has("last_played") ? o.get("last_played").getAsLong() : 0);
        } catch (IOException | RuntimeException e) {
            RealitiesOfDreams.LOGGER.warn("Unreadable story checkpoint {}", marker, e);
            return null;
        }
    }

    /** Opens the story save at its last checkpoint. */
    public static void continueStory(Screen parent) {
        Campaign.requestOnJoin(null);
        Minecraft.getInstance().createWorldOpenFlows().openWorld(FOLDER, () -> Minecraft.getInstance().setScreen(parent));
    }

    /** Opens the story save and replays a scene the player has already reached. */
    public static void replay(Screen parent, String sceneId) {
        Campaign.requestOnJoin("replay:" + sceneId);
        Minecraft.getInstance().createWorldOpenFlows().openWorld(FOLDER, () -> Minecraft.getInstance().setScreen(parent));
    }

    /**
     * Starts a brand-new story save. An existing one is first renamed to a dated backup.
     * Returns false (and changes nothing) if the old save could not be moved aside.
     */
    public static boolean newStory(Screen parent) {
        Minecraft mc = Minecraft.getInstance();
        if (Files.exists(folder()) && backupExisting() == null) return false;
        GameRules rules = new GameRules();
        rules.getRule(GameRules.RULE_SPAWN_CHUNK_RADIUS).set(0, null);
        LevelSettings settings = new LevelSettings(Component.translatable("realitiesofdreams.save.name").getString(), GameType.SURVIVAL, false,
                Difficulty.NORMAL, false, rules, WorldDataConfiguration.DEFAULT);
        Campaign.expectNewStoryWorld(FOLDER);
        Campaign.requestOnJoin(null);
        // The overworld is only a formality in a story save (players live in MC-001), so it is a
        // quick-to-generate flat world.
        mc.createWorldOpenFlows().createFreshLevel(FOLDER, settings, new WorldOptions(SEED, false, false),
                access -> access.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions(),
                parent);
        return true;
    }

    /** Renames the current story save to a dated backup folder. Returns the backup folder name. */
    @Nullable
    static String backupExisting() {
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String backup = FOLDER + "_backup_" + stamp;
        try {
            Files.move(folder(), source().getBaseDir().resolve(backup));
            try (LevelStorageSource.LevelStorageAccess access = source().validateAndCreateAccess(backup)) {
                access.renameLevel(Component.translatable("realitiesofdreams.save.backup_name", stamp.replace('_', ' ')).getString());
            } catch (Exception e) {
                RealitiesOfDreams.LOGGER.warn("Backed up the story save to {} but could not rename it", backup, e);
            }
            RealitiesOfDreams.LOGGER.info("Previous story save kept as {}", backup);
            return backup;
        } catch (IOException e) {
            RealitiesOfDreams.LOGGER.error("Could not back up the story save; New Story cancelled", e);
            return null;
        }
    }
}
