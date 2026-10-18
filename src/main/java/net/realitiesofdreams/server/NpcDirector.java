package net.realitiesofdreams.server;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.network.chat.Component;
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

/** Places scene characters in the world and carries out NPC commands from the story. */
public final class NpcDirector {
    private NpcDirector() {}

    /** Makes sure every character the current stage needs is standing in the world. */
    public static void reconcile(ServerLevel level, SceneDef scene, PlayerStoryState state) {
        int stage = scene.stageIndex(state.stage());
        for (SceneDef.NpcPlacement n : scene.npcs()) {
            int from = n.fromStage() == null ? 0 : scene.stageIndex(n.fromStage());
            int until = n.untilStage() == null ? Integer.MAX_VALUE : scene.stageIndex(n.untilStage());
            if (stage >= from && stage < until) ensure(level, scene, n.character(), n.location(), n.yaw());
        }
    }

    @Nullable
    static StoryNpcEntity find(ServerLevel level, SceneDef scene, String character) {
        java.util.UUID id = SceneWorldData.get(level).npc(scene.id() + "/" + character);
        return id != null && level.getEntity(id) instanceof StoryNpcEntity npc && npc.isAlive() ? npc : null;
    }

    static StoryNpcEntity ensure(ServerLevel level, SceneDef scene, String character, String location, float yaw) {
        StoryNpcEntity existing = find(level, scene, character);
        if (existing != null) return existing;
        double[] a = ModDimensions.absolute(scene, scene.location(location));
        BlockPos home = BlockPos.containing(a[0], a[1], a[2]);
        if (SceneWorldData.get(level).npc(scene.id() + "/" + character) != null && !level.isLoaded(home)) {
            return null; // it exists but its chunk is not loaded; don't duplicate it
        }
        StoryNpcEntity npc = spawn(level, character, a[0], a[1], a[2], yaw);
        npc.setHome(home);
        npc.setSceneId(scene.id());
        SceneWorldData.get(level).setNpc(scene.id() + "/" + character, npc.getUUID());
        return npc;
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

    public static void command(ServerPlayer p, @Nullable SceneDef scene, Effect.NpcCommand cmd) {
        if (scene == null) return;
        ServerLevel level = ModDimensions.level(p.server, scene);
        if (level == null || p.level() != level) return; // NPCs only exist in the scene's universe
        StoryNpcEntity npc = find(level, scene, cmd.character());
        switch (cmd.kind()) {
            case "spawn" -> ensure(level, scene, cmd.character(), cmd.location(), 0);
            case "follow" -> {
                if (npc == null) npc = spawn(level, cmd.character(), p.getX() + 1, p.getY(), p.getZ() + 1, p.getYRot());
                npc.setFollowTarget(p.getUUID());
            }
            case "stay" -> {
                if (npc != null) {
                    npc.setFollowTarget(null);
                    if (cmd.location() != null) {
                        double[] a = ModDimensions.absolute(scene, scene.location(cmd.location()));
                        npc.setHome(BlockPos.containing(a[0], a[1], a[2]));
                    }
                }
            }
            case "move" -> {
                if (npc != null && cmd.location() != null) {
                    double[] a = ModDimensions.absolute(scene, scene.location(cmd.location()));
                    npc.setHome(BlockPos.containing(a[0], a[1], a[2]));
                }
            }
            case "despawn" -> {
                if (npc != null) npc.discard();
            }
            default -> RealitiesOfDreams.LOGGER.warn("Unknown NPC command {}", cmd.kind());
        }
        if (Story.content().characters().get(cmd.character()).isEmpty()) {
            RealitiesOfDreams.LOGGER.warn("NPC command for unknown character {}", cmd.character());
        }
    }

    /** Companions stop following a player who leaves or finishes the scene; they walk back home. */
    public static void releaseAll(ServerPlayer p) {
        for (ServerLevel level : p.server.getAllLevels()) {
            for (Entity e : level.getAllEntities()) {
                if (e instanceof StoryNpcEntity npc && p.getUUID().equals(npc.followTarget())) {
                    npc.setFollowTarget(null);
                    npc.setApproachTarget(null);
                }
            }
        }
    }
}
