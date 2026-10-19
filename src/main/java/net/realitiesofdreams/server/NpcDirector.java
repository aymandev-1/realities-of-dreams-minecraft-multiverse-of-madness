package net.realitiesofdreams.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.entity.StoryNpcEntity;
import net.realitiesofdreams.registry.ModEntities;
import net.realitiesofdreams.story.character.CharacterDef;
import net.realitiesofdreams.story.engine.Effect;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import net.realitiesofdreams.world.ModDimensions;
import net.realitiesofdreams.world.SceneWorldData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Places scene characters in the world and carries out NPC commands from the story.
 * Each character exists once per universe, so Elara in Scene 001 is the same Elara in Scene 002.
 */
public final class NpcDirector {
    private NpcDirector() {}

    private static String key(String character) {
        return "npc/" + character;
    }

    /**
     * Makes sure every character the current stage needs is in the world, and removes characters
     * whose part in this scene is over (or has not started yet).
     */
    public static void reconcile(ServerLevel level, SceneDef scene, PlayerStoryState state) {
        for (String character : scene.npcs().stream().map(SceneDef.NpcPlacement::character).distinct().toList()) {
            // An enemy beaten in this scene has run off; it does not come back.
            if (state.hasFlag(net.realitiesofdreams.story.engine.SceneEngine.defeatedFlag(scene, character))) continue;
            SceneDef.NpcPlacement placement = placement(scene, state, character);
            StoryNpcEntity npc = find(level, character);
            if (placement != null) {
                if (npc == null) ensure(level, scene, placement);
            } else if (npc != null && npc.followTarget() == null) {
                npc.discard();
            }
        }
    }

    /**
     * Called when a scene starts: characters already in the world take up their places for the new
     * scene (walking there, or appearing there if they are far away).
     */
    public static void arrange(ServerLevel level, SceneDef scene, PlayerStoryState state) {
        for (SceneDef.NpcPlacement n : scene.npcs()) {
            if (!scene.presentDuring(n, state.stage())) continue;
            StoryNpcEntity npc = find(level, n.character());
            if (npc == null || npc.followTarget() != null) continue;
            if (npc.isSleeping()) npc.stopSleeping();
            double[] a = ModDimensions.absolute(scene, scene.location(n.location()));
            if (npc.position().distanceTo(new Vec3(a[0], a[1], a[2])) > 32) npc.teleportTo(a[0], a[1], a[2]);
            apply(scene, npc, n);
        }
        reconcile(level, scene, state);
    }

    /** The placement of a character that applies during the player's current stage, if any. */
    @Nullable
    static SceneDef.NpcPlacement placement(SceneDef scene, PlayerStoryState state, String character) {
        for (SceneDef.NpcPlacement n : scene.placementsFor(character)) if (scene.presentDuring(n, state.stage())) return n;
        return null;
    }

    @Nullable
    public static StoryNpcEntity find(ServerLevel level, String character) {
        UUID id = SceneWorldData.get(level).npc(key(character));
        return id != null && level.getEntity(id) instanceof StoryNpcEntity npc && npc.isAlive() ? npc : null;
    }

    @Nullable
    static StoryNpcEntity ensure(ServerLevel level, SceneDef scene, SceneDef.NpcPlacement n) {
        StoryNpcEntity existing = find(level, n.character());
        if (existing != null) return existing;
        double[] a = ModDimensions.absolute(scene, scene.location(n.location()));
        BlockPos home = BlockPos.containing(a[0], a[1], a[2]);
        if (SceneWorldData.get(level).npc(key(n.character())) != null && !level.isLoaded(home)) {
            return null; // it exists but its chunk is not loaded; don't duplicate it
        }
        StoryNpcEntity npc = spawn(level, n.character(), a[0], a[1], a[2], n.yaw());
        apply(scene, npc, n);
        SceneWorldData.get(level).setNpc(key(n.character()), npc.getUUID());
        return npc;
    }

    /** Gives a character their spot, activity and held item for this scene. */
    private static void apply(SceneDef scene, StoryNpcEntity npc, SceneDef.NpcPlacement n) {
        npc.setSceneId(scene.id());
        double[] a = ModDimensions.absolute(scene, scene.location(n.location()));
        npc.setHome(BlockPos.containing(a[0], a[1], a[2]));
        List<BlockPos> route = new ArrayList<>();
        for (String r : n.route()) {
            double[] p = ModDimensions.absolute(scene, scene.location(r));
            route.add(BlockPos.containing(p[0], p[1], p[2]));
        }
        npc.setActivity(n.activity(), route);
        ItemStack held = n.held() == null ? ItemStack.EMPTY : new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(n.held())));
        npc.setItemSlot(EquipmentSlot.MAINHAND, held);
        npc.setDropChance(EquipmentSlot.MAINHAND, 0);
    }

    public static StoryNpcEntity spawn(ServerLevel level, String character, double x, double y, double z, float yaw) {
        StoryNpcEntity npc = ModEntities.STORY_NPC.get().create(level);
        npc.setCharacter(character);
        npc.moveTo(x, y, z, yaw, 0);
        npc.setYHeadRot(yaw);
        npc.setYBodyRot(yaw);
        npc.setHome(BlockPos.containing(x, y, z));
        level.addFreshEntity(npc);
        return npc;
    }

    /** Spawns any registry character at a position: player-model NPC, or a named pig for Reuben. */
    public static Entity spawnAny(ServerLevel level, CharacterDef c, double x, double y, double z, float yaw) {
        if (c.usesPlayerModel()) return spawn(level, c.id(), x, y, z, yaw);
        Pig pig = EntityType.PIG.create(level);
        pig.moveTo(x, y, z, yaw, 0);
        pig.setCustomName(Component.literal(c.name()));
        pig.setPersistenceRequired();
        pig.finalizeSpawn(level, level.getCurrentDifficultyAt(pig.blockPosition()), MobSpawnType.COMMAND, null);
        level.addFreshEntity(pig);
        return pig;
    }

    public static void command(ServerPlayer p, @Nullable SceneDef scene, PlayerStoryState state, Effect.NpcCommand cmd) {
        if (scene == null) return;
        ServerLevel level = ModDimensions.level(p.server, scene);
        if (level == null || p.level() != level) return; // NPCs only exist in the scene's universe
        if (Story.content().characters().get(cmd.character()).isEmpty()) {
            RealitiesOfDreams.LOGGER.warn("NPC command for unknown character {}", cmd.character());
            return;
        }
        StoryNpcEntity npc = find(level, cmd.character());
        if (npc == null && !cmd.kind().equals("despawn")) {
            SceneDef.NpcPlacement n = placement(scene, state, cmd.character());
            if (n == null && !scene.placementsFor(cmd.character()).isEmpty()) n = scene.placementsFor(cmd.character()).get(0);
            if (n != null) npc = ensure(level, scene, n);
        }
        double[] at = cmd.location() == null ? null : ModDimensions.absolute(scene, scene.location(cmd.location()));
        switch (cmd.kind()) {
            case "spawn" -> {
                if (npc == null) {
                    double[] pos = at != null ? at : new double[] {p.getX() + 1, p.getY(), p.getZ() + 1};
                    npc = spawn(level, cmd.character(), pos[0], pos[1], pos[2], 0);
                    SceneWorldData.get(level).setNpc(key(cmd.character()), npc.getUUID());
                } else if (at != null) {
                    npc.teleportTo(at[0], at[1], at[2]);
                }
                if (at != null) npc.setHome(BlockPos.containing(at[0], at[1], at[2]));
                npc.setSceneId(scene.id());
            }
            case "follow" -> {
                if (npc == null) {
                    npc = spawn(level, cmd.character(), p.getX() + 1, p.getY(), p.getZ() + 1, p.getYRot());
                    SceneWorldData.get(level).setNpc(key(cmd.character()), npc.getUUID());
                }
                if (npc.isSleeping()) npc.stopSleeping();
                npc.setFollowTarget(p.getUUID());
            }
            case "stay" -> {
                if (npc != null) {
                    npc.setFollowTarget(null);
                    if (at != null) npc.setHome(BlockPos.containing(at[0], at[1], at[2]));
                }
            }
            case "move" -> {
                if (npc != null && at != null) npc.setHome(BlockPos.containing(at[0], at[1], at[2]));
            }
            case "despawn" -> {
                if (npc != null) npc.discard();
            }
            case "sleep" -> {
                if (npc != null && at != null) npc.sleepIn(BlockPos.containing(at[0], at[1], at[2]));
            }
            case "wake" -> {
                if (npc != null && npc.isSleeping()) npc.stopSleeping();
            }
            case "approach" -> {
                if (npc != null) npc.approachAndStay(p.getUUID());
            }
            case "hostile" -> {
                if (npc != null && !npc.hasYielded()) {
                    npc.setFollowTarget(null);
                    npc.setActivity("hostile", List.of());
                }
            }
            case "look" -> {
                if (npc != null && at != null) npc.lookTowards(new Vec3(at[0], at[1] + 1.5, at[2]), 80);
            }
            default -> RealitiesOfDreams.LOGGER.warn("Unknown NPC command {}", cmd.kind());
        }
    }

    /** Companions stop following a player whose scene ends without another scene to carry on into. */
    public static void releaseFollowers(ServerPlayer p) {
        for (ServerLevel level : p.server.getAllLevels()) {
            for (Entity e : level.getAllEntities()) {
                if (e instanceof StoryNpcEntity npc && p.getUUID().equals(npc.followTarget())) npc.setFollowTarget(null);
            }
        }
    }

    /** Companions stop following a player who leaves; they walk back home. */
    public static void releaseAll(ServerPlayer p) {
        for (ServerLevel level : p.server.getAllLevels()) {
            for (Entity e : level.getAllEntities()) {
                if (e instanceof StoryNpcEntity npc && (p.getUUID().equals(npc.followTarget()) || p.getUUID().equals(npc.approachTarget()))) {
                    npc.setFollowTarget(null);
                    npc.setApproachTarget(null);
                }
            }
        }
    }
}
