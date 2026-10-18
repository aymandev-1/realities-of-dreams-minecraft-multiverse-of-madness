package net.realitiesofdreams.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.entity.StoryNpcEntity;
import net.realitiesofdreams.registry.ModAttachments;
import net.realitiesofdreams.server.NpcDirector;
import net.realitiesofdreams.story.character.CharacterDef;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import net.realitiesofdreams.world.ModDimensions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Automated client playtest, for development only. It runs only when the JVM is started with
 * -Drod.autotest=true (`gradlew runClient -Prod.autotest=true`), and does nothing otherwise.
 *
 * It creates a creative test world, photographs every Saga 1 character from the front and back,
 * then plays Scene 001 from the opening cinematic to "Mission Complete", taking screenshots at each
 * story beat and logging the story state, and finally quits. Screenshots go to run/client/screenshots
 * and the log to run/client/autotest.log.
 */
@EventBusSubscriber(modid = RealitiesOfDreams.MODID, value = Dist.CLIENT)
public final class ClientAutotest {
    private static final boolean ENABLED = Boolean.getBoolean("rod.autotest");
    private static final int GLOBAL_TIMEOUT = 20 * 60 * 15;
    private static final String TAG = "rod_autotest";

    private record Step(String label, int delay, BooleanSupplier until, int timeout, Runnable action) {}

    private static final Deque<Step> steps = new ArrayDeque<>();
    private static boolean worldRequested, started;
    private static int wait, waited, ticks;
    private static final List<String> failures = new ArrayList<>();

    private ClientAutotest() {}

    @SubscribeEvent
    public static void onScreen(ScreenEvent.Init.Post event) {
        if (!ENABLED || worldRequested || !(event.getScreen() instanceof TitleScreen)) return;
        worldRequested = true;
        log("Creating test world");
        Minecraft mc = Minecraft.getInstance();
        LevelSettings settings = new LevelSettings("rod-autotest-" + System.currentTimeMillis(), GameType.CREATIVE, false,
                Difficulty.PEACEFUL, true, new GameRules(), WorldDataConfiguration.DEFAULT);
        mc.createWorldOpenFlows().createFreshLevel(settings.levelName(), settings, new WorldOptions(1L, false, false),
                access -> access.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions(),
                event.getScreen());
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        if (!ENABLED) return;
        Minecraft mc = Minecraft.getInstance();
        if (!started) {
            if (mc.player == null || mc.getSingleplayerServer() == null) return;
            started = true;
            script();
            log("World joined; running " + steps.size() + " steps");
        }
        if (++ticks > GLOBAL_TIMEOUT) {
            fail("global timeout");
            finish();
            return;
        }
        if (steps.isEmpty()) return;
        Step s = steps.peek();
        if (wait < s.delay()) {
            wait++;
            return;
        }
        if (s.until() != null && !s.until().getAsBoolean()) {
            if (++waited < s.timeout()) return;
            fail("timed out waiting: " + s.label());
        }
        steps.poll();
        wait = 0;
        waited = 0;
        log("step: " + s.label());
        try {
            s.action().run();
        } catch (RuntimeException e) {
            fail(s.label() + ": " + e);
        }
    }

    // ------------------------------------------------------------------ the script

    private static void script() {
        Minecraft mc = Minecraft.getInstance();
        SceneDef scene = Story.content().scene("mc001_s001");
        step("enter Firstbrook", 60, () -> command("rod dev tp firstbrook"));
        step("let the village load", 200, () -> mc.options.hideGui = true);

        // Character skins: the 11 named Saga 1 characters, then one of each generated NPC type.
        List<String> named = new ArrayList<>();
        List<String> types = new ArrayList<>();
        Set<String> seenTypes = new LinkedHashSet<>();
        for (CharacterDef c : Story.content().characters().all()) {
            if (!c.castIn(1) || !c.usesPlayerModel()) continue;
            if (!c.generated()) named.add(c.id());
            else if (seenTypes.add(c.id().replaceAll("_\\d+$", ""))) types.add(c.id());
        }
        List<List<String>> groups = new ArrayList<>();
        for (int i = 0; i < named.size(); i += 4) groups.add(named.subList(i, Math.min(named.size(), i + 4)));
        for (int i = 0; i < types.size(); i += 4) groups.add(types.subList(i, Math.min(types.size(), i + 4)));
        for (int g = 0; g < groups.size(); g++) {
            List<String> group = groups.get(g);
            String name = String.format("skins_%02d_%s", g + 1, String.join("-", group));
            step("line up " + group, 0, () -> onServer(sp -> lineUp(sp, scene, group)));
            step("front " + group, 40, () -> shot(name + "_front"));
            step("turn around", 0, () -> onServer(sp -> turn(sp, 0)));
            step("back " + group, 20, () -> shot(name + "_back"));
        }
        step("clear the line-up", 0, () -> onServer(sp -> clear(sp)));

        // Scene 001, played as a player would: no clicking on characters.
        step("start Scene 001", 10, () -> {
            mc.options.hideGui = false;
            command("rod play");
        });
        step("opening: valley", 150, () -> shot("s001_01_opening_valley"));
        step("opening: village", 160, () -> shot("s001_02_opening_village"));
        step("opening: Steve asleep", 160, () -> shot("s001_03_opening_asleep"));
        step("opening: awake", 100, () -> shot("s001_04_opening_another_morning"));
        step("opening: window", 110, () -> shot("s001_05_opening_window"));
        step("opening: Elara", 120, () -> shot("s001_06_opening_elara"));
        step("player in control", 0, () -> "leave_house".equals(stage()), 900, () -> {
            logState();
            shot("s001_07_in_control");
        });
        step("look round the house", 30, () -> onServer(sp -> face(sp, 135, 15)));
        step("house interior", 20, () -> shot("s001_08_house_interior"));
        step("leave the house", 0, () -> onServer(sp -> moveTo(sp, scene, 16, 12, 90)));
        step("Elara joins", 0, () -> "explore".equals(stage()), 100, () -> mc.options.setCameraType(CameraType.THIRD_PERSON_BACK));
        step("walking conversation", 60, () -> shot("s001_09_elara_joins"));
        for (int x = 15; x >= 9; x--) {
            int fx = x;
            step("walk west to x=" + x, 30, () -> onServer(sp -> moveTo(sp, scene, fx, 12, 90)));
        }
        step("walking with Elara", 10, () -> shot("s001_10_walk_and_talk"));
        step("farmer greets Steve", 0, () -> hasFlag("mc001.s001.walk1"), 800, () -> shot("s001_11_walk_finished"));
        step("farmer", 0, () -> "to_square".equals(stage()), 600, () -> shot("s001_12_farmer_done"));
        for (int z = 11; z >= 4; z--) {
            int fz = z;
            step("walk to the square z=" + z, 25, () -> onServer(sp -> moveTo(sp, scene, 4, fz, 180)));
        }
        step("arrive in the square", 0, () -> onServer(sp -> moveTo(sp, scene, 2, 3, 180)));
        step("ridge cinematic", 60, () -> shot("s001_13_ridge"));
        step("did you feel that", 0, () -> "feel_that".equals(stage()) || isComplete(), 400, () -> shot("s001_14_feel_that"));
        step("Rowan arrives", 0, ClientAutotest::isComplete, 1200, () -> {
            onServer(sp -> face(sp, 90, 5));
            shot("s001_15_mission_complete");
        });
        step("Rowan comes over", 80, () -> shot("s001_16_rowan_approaches"));
        step("Rowan arrived", 120, () -> shot("s001_17_rowan_arrived"));
        step("save", 0, () -> command("rod dev save"));
        step("final state", 40, () -> {
            logState();
            command("rod dev npcs");
        });
        step("finish", 40, ClientAutotest::finish);
    }

    private static void step(String label, int delay, Runnable action) {
        steps.add(new Step(label, delay, null, 0, action));
    }

    private static void step(String label, int delay, BooleanSupplier until, int timeout, Runnable action) {
        steps.add(new Step(label, delay, until, timeout, action));
    }

    // ------------------------------------------------------------------ helpers

    private static void command(String cmd) {
        log("command: /" + cmd);
        Minecraft.getInstance().player.connection.sendCommand(cmd);
    }

    private static void onServer(Consumer<ServerPlayer> task) {
        Minecraft mc = Minecraft.getInstance();
        MinecraftServer server = mc.getSingleplayerServer();
        java.util.UUID id = mc.player.getUUID();
        server.execute(() -> {
            ServerPlayer sp = server.getPlayerList().getPlayer(id);
            if (sp != null) task.accept(sp);
        });
    }

    private static ServerPlayer serverPlayer() {
        Minecraft mc = Minecraft.getInstance();
        return mc.getSingleplayerServer() == null || mc.player == null ? null : mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());
    }

    private static String stage() {
        ServerPlayer sp = serverPlayer();
        return sp == null ? null : ModAttachments.story(sp).stage();
    }

    private static boolean hasFlag(String flag) {
        ServerPlayer sp = serverPlayer();
        return sp != null && ModAttachments.story(sp).hasFlag(flag);
    }

    private static boolean isComplete() {
        ServerPlayer sp = serverPlayer();
        return sp != null && ModAttachments.story(sp).isSceneComplete("mc001_s001");
    }

    private static void lineUp(ServerPlayer sp, SceneDef scene, List<String> ids) {
        clear(sp);
        ServerLevel level = ModDimensions.level(sp.server, scene);
        if (level == null) {
            fail("MC-001 level missing");
            return;
        }
        double[] origin = ModDimensions.absolute(scene, new SceneDef.Location(0, 0, 38, 1));
        for (int i = 0; i < ids.size(); i++) {
            double x = origin[0] + (i - (ids.size() - 1) / 2.0) * 1.5;
            StoryNpcEntity npc = NpcDirector.spawn(level, ids.get(i), x, origin[1], origin[2], 180);
            npc.setNoAi(true);
            npc.addTag(TAG);
        }
        double[] cam = ModDimensions.absolute(scene, new SceneDef.Location(0, 0, 34, 1));
        sp.teleportTo(level, cam[0], cam[1], cam[2] - 0.3, 0, 8);
    }

    private static void turn(ServerPlayer sp, float yaw) {
        for (Entity e : sp.serverLevel().getEntities((Entity) null, sp.getBoundingBox().inflate(32), e -> e.getTags().contains(TAG))) {
            e.setYRot(yaw);
            e.setYBodyRot(yaw);
            e.setYHeadRot(yaw);
        }
    }

    private static void clear(ServerPlayer sp) {
        sp.serverLevel().getEntities((Entity) null, sp.getBoundingBox().inflate(64), e -> e.getTags().contains(TAG)).forEach(Entity::discard);
    }

    private static void moveTo(ServerPlayer sp, SceneDef scene, int x, int z, float yaw) {
        double[] a = ModDimensions.absolute(scene, new SceneDef.Location(x, 0, z, 1));
        sp.teleportTo(sp.serverLevel(), a[0], a[1], a[2], yaw, 10);
    }

    private static void face(ServerPlayer sp, float yaw, float pitch) {
        sp.teleportTo(sp.serverLevel(), sp.getX(), sp.getY(), sp.getZ(), yaw, pitch);
    }

    private static void shot(String name) {
        Minecraft mc = Minecraft.getInstance();
        Screenshot.grab(mc.gameDirectory, name + ".png", mc.getMainRenderTarget(), msg -> log("screenshot " + name));
    }

    private static void logState() {
        ServerPlayer sp = serverPlayer();
        if (sp == null) return;
        PlayerStoryState s = ModAttachments.story(sp);
        log("state: " + s.toJson());
        List<String> npcs = new ArrayList<>();
        for (StoryNpcEntity n : sp.serverLevel().getEntitiesOfClass(StoryNpcEntity.class, sp.getBoundingBox().inflate(80))) {
            npcs.add(String.format("%s@%.1f,%.1f,%.1f%s%s", n.characterId(), n.getX(), n.getY(), n.getZ(),
                    n.isSleeping() ? " asleep" : "", sp.getUUID().equals(n.followTarget()) ? " following" : ""));
        }
        log("characters: " + npcs);
    }

    private static void fail(String why) {
        failures.add(why);
        log("FAIL: " + why);
    }

    private static void finish() {
        log(failures.isEmpty() ? "RESULT: PASS" : "RESULT: FAIL " + failures);
        steps.clear();
        Minecraft.getInstance().stop();
    }

    private static void log(String text) {
        RealitiesOfDreams.LOGGER.info("[autotest] {}", text);
        try {
            Files.writeString(Path.of("autotest.log"), text + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
            // logging only
        }
    }
}
