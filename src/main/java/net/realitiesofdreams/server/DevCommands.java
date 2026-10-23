package net.realitiesofdreams.server;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.entity.StoryNpcEntity;
import net.realitiesofdreams.registry.ModAttachments;
import net.realitiesofdreams.story.character.CharacterDef;
import net.realitiesofdreams.story.engine.Effect;
import net.realitiesofdreams.story.engine.ProximityDirector;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import net.realitiesofdreams.world.ModDimensions;
import net.realitiesofdreams.world.SceneWorld;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Development and playtest controls under /rod dev. They need operator permission (in single
 * player: "Allow Cheats" on, or Open to LAN with cheats), so ordinary survival play never sees them.
 *
 * /rod dev restart                 restart Scene 001 from the opening cinematic
 * /rod dev reset                   forget all story progress (fresh start)
 * /rod dev stage &lt;id&gt;              jump to a stage of the current scene
 * /rod dev progress                stage, flags, values, journal and triggers
 * /rod dev tp firstbrook|house     go to the Firstbrook square or Steve's house
 * /rod dev lineup [all]            the 11 Saga 1 characters plus one of each NPC type (or every NPC)
 * /rod dev clear                   remove characters spawned by lineup and /rod spawn
 * /rod dev npcs                    nearby characters: position, activity, who they follow
 * /rod dev npc &lt;character&gt; follow|stay|approach|goto &lt;location&gt;
 * /rod dev talk &lt;dialogue&gt;         play any conversation now
 * /rod dev proximity               which proximity conversations could start here, and why not
 * /rod dev save                    save the world and check the story save round-trips
 */
final class DevCommands {
    static final String DEV_TAG = "rod_dev";

    private DevCommands() {}

    static LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("dev").requires(s -> s.hasPermission(2))
                .then(Commands.literal("restart").executes(c -> {
                    StoryManager.get().restartScene(c.getSource().getPlayerOrException(), StoryManager.FIRST_SCENE);
                    return ok(c, "Restarted Scene 001.");
                }))
                .then(Commands.literal("reset").executes(c -> {
                    StoryManager.get().resetStory(c.getSource().getPlayerOrException());
                    return ok(c, "Story progress reset. Use /rod play to start Scene 001 again.");
                }))
                .then(Commands.literal("stage").then(Commands.argument("id", StringArgumentType.word())
                        .suggests((c, b) -> {
                            SceneDef s = scene(c.getSource().getPlayer());
                            return SharedSuggestionProvider.suggest(s == null ? List.of() : s.stages().stream().map(SceneDef.StageDef::id).toList(), b);
                        })
                        .executes(DevCommands::stage)))
                .then(Commands.literal("progress").executes(DevCommands::progress))
                .then(Commands.literal("tp")
                        .then(Commands.literal("firstbrook").executes(c -> tp(c, new double[] {2, 0, 6}, 180)))
                        .then(Commands.literal("house").executes(c -> tp(c, new double[] {17, 0, 15}, 180))))
                .then(Commands.literal("lineup").executes(c -> lineup(c, false))
                        .then(Commands.literal("all").executes(c -> lineup(c, true))))
                .then(Commands.literal("clear").executes(DevCommands::clear))
                .then(Commands.literal("npcs").executes(DevCommands::npcs))
                .then(Commands.literal("npc").then(Commands.argument("character", StringArgumentType.word())
                        .suggests((c, b) -> SharedSuggestionProvider.suggest(Story.content().characters().all().stream().map(CharacterDef::id), b))
                        .then(Commands.literal("follow").executes(c -> npc(c, "follow", null)))
                        .then(Commands.literal("stay").executes(c -> npc(c, "stay", null)))
                        .then(Commands.literal("approach").executes(c -> npc(c, "approach", null)))
                        .then(Commands.literal("goto").then(Commands.argument("location", StringArgumentType.word())
                                .suggests((c, b) -> {
                                    SceneDef s = scene(c.getSource().getPlayer());
                                    return SharedSuggestionProvider.suggest(s == null ? List.of() : s.locations().keySet(), b);
                                })
                                .executes(c -> npc(c, "move", StringArgumentType.getString(c, "location")))))))
                .then(Commands.literal("talk").then(Commands.argument("dialogue", StringArgumentType.word())
                        .suggests((c, b) -> SharedSuggestionProvider.suggest(Story.content().dialogues().keySet(), b))
                        .executes(DevCommands::talk)))
                .then(Commands.literal("proximity").executes(DevCommands::proximity))
                .then(Commands.literal("save").executes(DevCommands::save));
    }

    private static int ok(CommandContext<CommandSourceStack> c, String text) {
        c.getSource().sendSuccess(() -> Component.literal(text).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static void line(CommandContext<CommandSourceStack> c, Supplier<String> text) {
        c.getSource().sendSuccess(() -> Component.literal(text.get()), false);
    }

    private static SceneDef scene(ServerPlayer p) {
        if (p == null) return null;
        SceneDef active = Story.engine().activeScene(StoryManager.get().state(p));
        return active != null ? active : Story.content().scene(StoryManager.FIRST_SCENE);
    }

    private static int stage(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        String id = StringArgumentType.getString(c, "id");
        if (!StoryManager.get().jumpToStage(p, id)) {
            c.getSource().sendFailure(Component.literal("No scene in progress, or no stage " + id + ". Use /rod play first."));
            return 0;
        }
        return ok(c, "Jumped to stage " + id + ".");
    }

    private static int progress(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        PlayerStoryState s = StoryManager.get().state(p);
        SceneDef scene = Story.engine().activeScene(s);
        line(c, () -> "§6Scene: §f" + (scene == null ? "none" : scene.label() + " " + scene.title()) + "  §6Stage: §f" + s.stage());
        if (scene != null) {
            List<String> stages = new ArrayList<>();
            int at = scene.stageIndex(s.stage());
            for (int i = 0; i < scene.stages().size(); i++) stages.add((i < at ? "§a" : i == at ? "§e" : "§7") + scene.stages().get(i).id());
            line(c, () -> "§6Stages: " + String.join("§7 > ", stages));
        }
        line(c, () -> "§6Completed scenes: §f" + s.completedScenes());
        line(c, () -> "§6Flags: §f" + s.flags());
        line(c, () -> "§6Values: §f" + s.values() + "  §6Journal: §f" + s.journal());
        line(c, () -> "§6Busy: §f" + (StoryManager.get().inDialogue(p) ? "dialogue" : StoryManager.get().cinematic(p) != null ? "cinematic " + StoryManager.get().cinematic(p) : "no"));
        return 1;
    }

    private static int tp(CommandContext<CommandSourceStack> c, double[] rel, float yaw) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        SceneDef scene = Story.content().scene(StoryManager.FIRST_SCENE);
        ServerLevel level = ModDimensions.level(p.server, scene);
        if (level == null) {
            c.getSource().sendFailure(Component.literal("The MC-001 universe is not loaded."));
            return 0;
        }
        SceneWorld.ensureBuilt(level, scene);
        double[] a = ModDimensions.absolute(scene, new SceneDef.Location(rel[0], rel[1], rel[2], 1));
        p.teleportTo(level, a[0], a[1], a[2], yaw, 0);
        return ok(c, "Teleported. (This does not start or change the story.)");
    }

    /** Characters side by side in front of the player, facing them, to check skins and models. */
    private static int lineup(CommandContext<CommandSourceStack> c, boolean all) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        List<CharacterDef> cast = new ArrayList<>();
        Set<String> types = new LinkedHashSet<>();
        for (CharacterDef ch : Story.content().characters().all()) {
            if (!ch.castIn(1) || !ch.usesPlayerModel()) continue;
            if (!ch.generated()) cast.add(ch);
        }
        for (CharacterDef ch : Story.content().characters().all()) {
            if (!ch.castIn(1) || !ch.generated()) continue;
            String type = ch.id().replaceAll("_\\d+$", "");
            if (all || types.add(type)) cast.add(ch);
        }
        Vec3 look = Vec3.directionFromRotation(0, p.getYRot());
        Vec3 side = new Vec3(-look.z, 0, look.x);
        int perRow = 12;
        for (int i = 0; i < cast.size(); i++) {
            int row = i / perRow, col = i % perRow;
            double offset = (col - (Math.min(perRow, cast.size() - row * perRow) - 1) / 2.0) * 1.6;
            Vec3 pos = p.position().add(look.scale(5 + row * 2.5)).add(side.scale(offset));
            Entity e = NpcDirector.spawnAny(p.serverLevel(), cast.get(i), pos.x, p.getY(), pos.z, p.getYRot() + 180);
            e.addTag(DEV_TAG);
            if (e instanceof StoryNpcEntity npc) npc.setHome(npc.blockPosition());
        }
        return ok(c, "Spawned " + cast.size() + " characters. Remove them with /rod dev clear.");
    }

    private static int clear(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        List<Entity> spawned = p.serverLevel().getEntities((Entity) null, p.getBoundingBox().inflate(256), e -> e.getTags().contains(DEV_TAG));
        spawned.forEach(Entity::discard);
        return ok(c, "Removed " + spawned.size() + " test characters.");
    }

    private static int npcs(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        List<StoryNpcEntity> list = p.serverLevel().getEntitiesOfClass(StoryNpcEntity.class, p.getBoundingBox().inflate(64));
        list.sort((a, b) -> Double.compare(a.distanceTo(p), b.distanceTo(p)));
        line(c, () -> "§6" + list.size() + " characters within 64 blocks:");
        for (StoryNpcEntity n : list) {
            String state = n.isSleeping() ? "asleep" : p.getUUID().equals(n.followTarget()) ? "following you"
                    : n.approachTarget() != null ? "walking to you" : n.activity() + (n.getNavigation().isDone() ? "" : ", walking");
            line(c, () -> String.format(" §f%s §7%.0f blocks, %s, home %s", n.characterId(), n.distanceTo(p), state,
                    n.home() == null ? "none" : n.home().toShortString()));
        }
        return list.size();
    }

    private static int npc(CommandContext<CommandSourceStack> c, String kind, String location) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        SceneDef scene = scene(p);
        if (location != null && (scene == null || scene.location(location) == null)) {
            c.getSource().sendFailure(Component.literal("Unknown location " + location));
            return 0;
        }
        NpcDirector.command(p, scene, StoryManager.get().state(p), new Effect.NpcCommand(kind, StringArgumentType.getString(c, "character"), location));
        return ok(c, "Sent " + kind + (location == null ? "" : " " + location) + ". (Only works inside MC-001.)");
    }

    private static int talk(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        String id = StringArgumentType.getString(c, "dialogue");
        if (Story.content().dialogue(id) == null) {
            c.getSource().sendFailure(Component.literal("Unknown dialogue " + id));
            return 0;
        }
        StoryManager.get().startDialogue(p, id, null);
        return 1;
    }

    private static int proximity(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        PlayerStoryState s = StoryManager.get().state(p);
        SceneDef scene = Story.engine().activeScene(s);
        if (scene == null) {
            c.getSource().sendFailure(Component.literal("No scene in progress."));
            return 0;
        }
        for (SceneDef.TriggerDef t : scene.triggers()) {
            StoryNpcEntity npc = p.serverLevel().getEntitiesOfClass(StoryNpcEntity.class, new AABB(p.blockPosition()).inflate(64),
                    n -> n.characterId().equals(t.character())).stream().findFirst().orElse(null);
            String why = s.triggerFired(t.id()) && t.once() ? "already played"
                    : !ProximityDirector.eligible(t, s) ? "not available at this stage (stages " + t.stages() + ", needs " + t.requires() + ")"
                    : npc == null ? "character not nearby"
                    : npc.distanceTo(p) > t.radius() ? String.format("%.0f blocks away; starts within %.0f", npc.distanceTo(p), t.radius())
                    : "ready: walk closer";
            line(c, () -> " §f" + t.id() + " §7(" + t.character() + "): " + why);
        }
        if (scene.triggers().isEmpty()) line(c, () -> "This scene has no proximity conversations.");
        return 1;
    }

    private static int save(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        CompoundTag tag = p.getData(ModAttachments.STORY).serializeNBT(p.registryAccess());
        ModAttachments.StoryData copy = new ModAttachments.StoryData();
        copy.deserializeNBT(p.registryAccess(), tag);
        boolean same = copy.state().toJson().equals(StoryManager.get().state(p).toJson());
        p.server.getPlayerList().saveAll();
        p.server.saveEverything(false, false, false);
        return ok(c, (same ? "Saved. Story data round-trips correctly." : "Saved, but the story data did NOT round-trip!")
                + " To test reloading: Esc > Save and Quit to Title, reopen the world, then /rod dev progress.");
    }
}
