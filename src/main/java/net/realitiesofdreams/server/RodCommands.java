package net.realitiesofdreams.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.story.character.CharacterDef;
import net.realitiesofdreams.story.engine.SceneEngine;
import net.realitiesofdreams.story.engine.StoryEvent;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import net.realitiesofdreams.story.universe.UniverseCatalog;
import net.realitiesofdreams.story.universe.UniverseId;

import java.util.List;
import java.util.Optional;

/**
 * /rod play                 start or resume the story
 * /rod status               where you are in the story
 * /rod scene restart        replay Scene 001 from the start
 * /rod scene skip           (op) complete the current objective, for testing
 * /rod leave                return to the overworld (progress is kept)
 * /rod characters [MC-xxx]  list registry characters
 * /rod spawn &lt;character&gt;    (op) spawn a character where you stand
 * /rod universe &lt;MC-xxx&gt;    show a universe profile
 * /rod dev ...              (op) playtest controls, see {@link DevCommands}
 */
public final class RodCommands {
    private RodCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("rod")
                .then(Commands.literal("play").executes(c -> {
                    StoryManager.get().play(c.getSource().getPlayerOrException());
                    return 1;
                }))
                .then(Commands.literal("status").executes(RodCommands::status))
                .then(Commands.literal("leave").executes(c -> {
                    ServerPlayer p = c.getSource().getPlayerOrException();
                    ServerLevel over = p.server.overworld();
                    var spawn = over.getSharedSpawnPos();
                    p.teleportTo(over, spawn.getX() + 0.5, over.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, spawn.getX(), spawn.getZ()), spawn.getZ() + 0.5, p.getYRot(), 0);
                    c.getSource().sendSuccess(() -> Component.literal("Your story progress is saved. Use /rod play to return."), false);
                    return 1;
                }))
                .then(Commands.literal("scene")
                        .then(Commands.literal("restart")
                                .executes(c -> restart(c, null))
                                .then(Commands.argument("scene", StringArgumentType.word())
                                        .suggests((c, b) -> SharedSuggestionProvider.suggest(Story.content().scenes().stream().map(SceneDef::id), b))
                                        .executes(c -> restart(c, StringArgumentType.getString(c, "scene")))))
                        .then(Commands.literal("skip").requires(s -> s.hasPermission(2)).executes(RodCommands::skip)))
                .then(Commands.literal("characters")
                        .executes(c -> characters(c, null))
                        .then(Commands.argument("universe", StringArgumentType.string()).executes(c -> characters(c, StringArgumentType.getString(c, "universe")))))
                .then(Commands.literal("spawn").requires(s -> s.hasPermission(2))
                        .then(Commands.argument("character", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(Story.content().characters().all().stream().map(CharacterDef::id), b))
                                .executes(RodCommands::spawn)))
                .then(Commands.literal("universe")
                        .then(Commands.argument("id", StringArgumentType.string()).executes(RodCommands::universe)))
                .then(DevCommands.node()));
    }

    /** Replays a scene: the named one, else the scene in progress, else the last one finished. */
    private static int restart(CommandContext<CommandSourceStack> c, String sceneId) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        PlayerStoryState s = StoryManager.get().state(p);
        if (sceneId == null) {
            SceneDef active = Story.engine().activeScene(s);
            sceneId = active != null ? active.id() : null;
            if (sceneId == null) for (String id : s.completedScenes()) sceneId = id;
            if (sceneId == null) sceneId = StoryManager.FIRST_SCENE;
        }
        if (Story.content().scene(sceneId) == null) {
            c.getSource().sendFailure(Component.literal("Unknown scene " + sceneId));
            return 0;
        }
        StoryManager.get().restartScene(p, sceneId);
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        PlayerStoryState s = StoryManager.get().state(p);
        SceneDef scene = Story.engine().activeScene(s);
        if (scene == null) {
            c.getSource().sendSuccess(() -> Component.literal("No scene in progress. Completed: " + s.completedScenes()), false);
        } else {
            SceneDef.StageDef stage = Story.engine().activeStage(s);
            c.getSource().sendSuccess(() -> Component.literal(scene.label() + " " + scene.title() + " | stage " + stage.id()
                    + (stage.objective().isEmpty() ? "" : " | " + stage.objective())).withStyle(ChatFormatting.GOLD), false);
        }
        return 1;
    }

    /** Completes the current stage the way the help menu's skip does, so each stage can be tested in isolation. */
    private static int skip(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        if (!StoryManager.get().skipStage(p)) {
            c.getSource().sendFailure(Component.literal("No objective to skip right now."));
            return 0;
        }
        return 1;
    }

    private static int characters(CommandContext<CommandSourceStack> c, String universe) {
        List<CharacterDef> list = Story.content().characters().all().stream()
                .filter(ch -> universe == null || universe.equalsIgnoreCase(String.valueOf(ch.universe()))).toList();
        c.getSource().sendSuccess(() -> Component.literal(list.size() + " characters" + (universe == null ? "" : " in " + universe) + ":").withStyle(ChatFormatting.GOLD), false);
        for (CharacterDef ch : list) {
            c.getSource().sendSuccess(() -> Component.literal(" " + ch.name() + " (" + (ch.universe() == null ? "—" : ch.universe()) + ") ")
                    .append(Component.literal(ch.id()).withStyle(ChatFormatting.DARK_GRAY)), false);
        }
        return list.size();
    }

    private static int spawn(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = c.getSource().getPlayerOrException();
        String id = StringArgumentType.getString(c, "character");
        Optional<CharacterDef> ch = Story.content().characters().get(id);
        if (ch.isEmpty()) {
            c.getSource().sendFailure(Component.literal("Unknown character " + id));
            return 0;
        }
        NpcDirector.spawnAny(p.serverLevel(), ch.get(), p.getX(), p.getY(), p.getZ(), p.getYRot() + 180).addTag(DevCommands.DEV_TAG);
        c.getSource().sendSuccess(() -> Component.literal("Spawned " + ch.get().name()), true);
        return 1;
    }

    private static int universe(CommandContext<CommandSourceStack> c) {
        String text = StringArgumentType.getString(c, "id");
        Optional<UniverseId> id = UniverseId.parse(text);
        if (id.isEmpty()) {
            c.getSource().sendFailure(Component.literal("Universes run from MC-001 to MC-10000."));
            return 0;
        }
        UniverseCatalog.Profile pr = UniverseCatalog.profile(id.get());
        long cast = Story.content().characters().inUniverse(id.get().toString()).size();
        c.getSource().sendSuccess(() -> Component.literal(id.get() + ": " + (pr.isCanon() ? pr.canonNote() : "uncharted " + pr.theme() + " world")
                + " | stability " + pr.stability() + "% | " + cast + " known characters").withStyle(ChatFormatting.AQUA), false);
        return 1;
    }
}
