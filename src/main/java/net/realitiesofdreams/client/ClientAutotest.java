package net.realitiesofdreams.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.campaign.Campaign;
import net.realitiesofdreams.client.menu.ChapterSelectScreen;
import net.realitiesofdreams.client.menu.RodCreditsScreen;
import net.realitiesofdreams.client.menu.RodTitleScreen;
import net.realitiesofdreams.client.menu.StorySaves;
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
 * It plays the game the way a new player would: the Realities of Dreams main menu, New Story,
 * the automatic arrival in MC-001 and Scene 001 from the opening cinematic to "Mission Complete"
 * without clicking any character; then it checks Primordial Steve's appearance, photographs the
 * Saga 1 cast and a tour of Firstbrook, saves and quits to the menu, and loads the story again
 * with Continue Story. With -Drod.panorama=true it also photographs the menu panorama.
 * Screenshots go to run/autotest/screenshots and the log to run/autotest/autotest.log.
 */
@EventBusSubscriber(modid = RealitiesOfDreams.MODID, value = Dist.CLIENT)
public final class ClientAutotest {
    private static final boolean ENABLED = Boolean.getBoolean("rod.autotest");
    private static final boolean PANORAMA = Boolean.getBoolean("rod.panorama");
    private static final int GLOBAL_TIMEOUT = 20 * 60 * 25;
    private static final String TAG = "rod_autotest";

    private record Step(String label, int delay, BooleanSupplier until, int timeout, Runnable action) {}

    private static final Deque<Step> steps = new ArrayDeque<>();
    private static boolean started;
    private static int wait, waited, ticks;
    private static final List<String> failures = new ArrayList<>();
    private static net.minecraft.world.phys.Vec3 routineStart;

    private ClientAutotest() {}

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        if (!ENABLED) return;
        // Make it obvious this window is not a game to play: the test moves the player around.
        if (ticks % 20 == 0) Minecraft.getInstance().getWindow().setTitle("AUTOMATED TEST - do not play (Realities of Dreams)");
        if (!started) {
            started = true;
            script();
            log("Autotest started; " + steps.size() + " steps");
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
            RealitiesOfDreams.LOGGER.error("[autotest] step failed", e);
        }
    }

    // ------------------------------------------------------------------ the script

    private static void script() {
        Minecraft mc = Minecraft.getInstance();
        SceneDef scene = Story.content().scene("mc001_s001");

        // The main menu replaces the vanilla title screen.
        step("main menu", 0, () -> mc.screen instanceof RodTitleScreen, 20 * 60 * 5, () -> log("Realities of Dreams main menu is showing"));
        step("menu faded in", 70, () -> shot("menu_01_title"));
        step("press New Story", 10, () -> press(Component.translatable("realitiesofdreams.menu.new_story")));
        // A save from an earlier run is kept as a backup when a new story begins.
        step("confirm if asked", 30, () -> {
            if (mc.screen instanceof ConfirmScreen) {
                shot("menu_02_new_story_confirm");
                press(Component.translatable("realitiesofdreams.menu.new_story.confirm.yes"));
            }
        });
        step("story save created and joined", 0, () -> serverPlayer() != null && mc.player != null, 20 * 60 * 6, () -> {
            ServerPlayer sp = serverPlayer();
            check(Campaign.isStoryWorld(), "the New Story save is not recognised as a story world");
            check(sp.level().dimension().equals(ModDimensions.MC_001), "player did not arrive in MC-001 but in " + sp.level().dimension().location());
            check(StorySaves.exists(), "story save folder missing");
        });

        // Scene 001, played as a player would: no commands, no clicking on characters.
        step("opening cinematic starts by itself", 0, CinematicPlayer::active, 900, () -> log("opening cinematic started"));
        step("opening: valley", 150, () -> shot("s001_01_opening_valley"));
        step("opening: village", 160, () -> shot("s001_02_opening_village"));
        step("opening: Steve asleep", 130, () -> shot("s001_03_opening_asleep"));
        step("opening: awake", 70, () -> shot("s001_04_opening_another_morning"));
        step("opening: window", 100, () -> shot("s001_05_opening_window"));
        step("opening: Elara", 120, () -> shot("s001_06_opening_elara"));
        step("player in control", 0, () -> "leave_house".equals(stage()), 900, () -> {
            logState();
            shot("s001_07_in_control");
            routineStart = npcPos("firstbrook_villager_3");
            log("villager_3 starts at " + routineStart);
        });
        step("Primordial Steve appearance", 20, () -> {
            check(ClientAppearance.overridden(mc.player.getUUID()), "player is not drawn as Primordial Steve");
            String tex = mc.player.getSkin().texture().toString();
            check(tex.equals("realitiesofdreams:textures/entity/character/primordial_steve.png"), "player skin is " + tex);
            check(mc.player.getSkin().model() == net.minecraft.client.resources.PlayerSkin.Model.WIDE, "Primordial Steve must use the classic (wide) model");
            log("player skin: " + tex + " model " + mc.player.getSkin().model());
        });
        step("first-person arm", 0, () -> onServer(sp -> face(sp, 120, 35)));
        step("first-person shot", 20, () -> shot("player_01_first_person_arm"));
        step("third person (front)", 0, () -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
        step("primordial steve front", 25, () -> shot("player_02_primordial_steve_front"));
        step("third person (back)", 0, () -> mc.options.setCameraType(CameraType.THIRD_PERSON_BACK));
        step("primordial steve back", 25, () -> shot("player_03_primordial_steve_back"));
        step("look round the house", 0, () -> {
            mc.options.setCameraType(CameraType.FIRST_PERSON);
            onServer(sp -> face(sp, 135, 15));
        });
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
        step("did you feel that", 0, () -> "feel_that".equals(stage()) || isComplete(), 400, ClientAutotest::logState);
        step("feel what?", 40, () -> shot("s001_14_feel_that"));
        step("Rowan arrives", 0, ClientAutotest::isComplete, 1200, () -> {
            onServer(sp -> face(sp, 90, 5));
            shot("s001_15_mission_complete");
        });
        step("Rowan comes over", 80, () -> shot("s001_16_rowan_approaches"));
        step("Rowan arrived", 120, () -> {
            shot("s001_17_rowan_arrived");
            StorySaves.Checkpoint cp = StorySaves.checkpoint();
            check(cp != null && cp.completed().contains("mc001_s001"), "story checkpoint file does not record Scene 001 as complete: " + cp);
        });

        // Scene 002 follows straight on: Rowan's warning, three clues, Bram, the compass cinematic.
        SceneDef s2 = Story.content().scene("mc001_s002");
        step("Scene 002 starts by itself", 0, () -> "mc001_s002".equals(activeScene()) && busy(), 20 * 30, () -> shot("s002_01_rowan_warning"));
        step("Rowan's warning", 120, () -> shot("s002_02_almost_deliberate"));
        step("warning finished", 0, () -> "inspect".equals(stage()), 20 * 120, ClientAutotest::logState);
        for (String clueId : List.of("cracked_wall", "spinning_compass", "tower_report")) {
            SceneDef.ClueDef clue = s2.clues().get(clueId);
            int cx = (int) clue.pos()[0], cz = (int) clue.pos()[2];
            step("walk to " + clueId, 0, () -> !busy(), 20 * 60, () -> onServer(sp -> moveTo(sp, s2, cx, cz + 2, 180)));
            step("look at " + clueId, 15, () -> onServer(sp -> face(sp, 180, 40)));
            step("photograph " + clueId, 15, () -> shot("s002_clue_" + clueId));
            step("examine " + clueId, 0, () -> useBlock(s2, clue.pos()));
            step(clueId + " found", 0, () -> clueFound(clueId), 100, () -> {});
            step(clueId + " remark", 30, () -> shot("s002_clue_" + clueId + "_remark"));
        }
        step("Bram arrives", 0, () -> "bram".equals(stage()) || "compass".equals(stage()), 20 * 60, () -> {});
        step("Bram's report", 0, ClientAutotest::busy, 20 * 30, () -> shot("s002_03_bram_report"));
        step("compass cinematic", 0, () -> "compass".equals(stage()), 20 * 90, () -> {});
        step("the needle spins", 25, () -> shot("s002_04_needle_spins"));
        step("light beneath the ruin", 55, () -> shot("s002_05_light_beneath_ruin"));
        step("Scene 002 complete", 0, () -> { ServerPlayer sp = serverPlayer(); return sp != null && ModAttachments.story(sp).isSceneComplete("mc001_s002"); },
                20 * 90, () -> {
                    shot("s002_06_mission_complete");
                    logState();
                    ServerPlayer sp = serverPlayer();
                    boolean compass = false;
                    for (net.minecraft.world.item.ItemStack st : sp.getInventory().items) compass |= st.is(net.minecraft.world.item.Items.COMPASS);
                    check(compass, "the Strange Compass was not given");
                    check(ModAttachments.story(sp).journal().contains("northern_disturbance"), "Scene 002 journal entry missing");
                });

        // Scene 003 follows on: up the northern trail with Elara.
        SceneDef s3 = Story.content().scene("mc001_s003");
        step("Scene 003 starts", 0, () -> "mc001_s003".equals(activeScene()) && !busy(), 20 * 60, () -> goTo(s3, "trail_gate", 180, 5));
        step("Bram's barrier", 30, () -> shot("s003_01_barrier"));
        step("follow the trail", 0, () -> "follow_trail".equals(stage()), 200, () -> goTo(s3, "footprints", 180, 20));
        step("tracks cinematic", 0, () -> "tracks".equals(stage()), 200, () -> {});
        step("Elara at the tracks", 50, () -> shot("s003_02_tracks"));
        step("tracks conversation", 0, () -> "investigate".equals(stage()), 20 * 90, () -> shot("s003_03_tracks_talk_done"));
        step("up the trail", 0, () -> goTo(s3, "mid_trail", 180, 5));
        step("walking conversation", 0, ClientAutotest::busy, 200, () -> shot("s003_04_valley_talk"));
        Object[][] trailClues = {{"dropped_lantern", 2, 11, -72, 180f, 45f}, {"scout_pack", 9, 18, -77, -90f, 35f}, {"survey_marker", 6, 19, -86, 180f, 40f}};
        for (Object[] c : trailClues) {
            String clueId = (String) c[0];
            SceneDef.ClueDef clue = s3.clues().get(clueId);
            step("walk to " + clueId, 0, () -> !busy(), 20 * 60, () -> onServer(sp -> {
                double[] p = ModDimensions.absolute(s3, new SceneDef.Location((int) c[1], (int) c[2], (int) c[3], 1));
                sp.teleportTo(sp.serverLevel(), p[0], p[1], p[2], (float) c[4], (float) c[5]);
            }));
            step("photograph " + clueId, 20, () -> shot("s003_clue_" + clueId));
            step("examine " + clueId, 0, () -> useBlock(s3, clue.pos()));
            step(clueId + " found", 0, () -> clueFound(clueId), 100, () -> {});
            step(clueId + " remark", 30, () -> shot("s003_clue_" + clueId + "_remark"));
        }
        step("to the carved stone", 0, () -> "markings".equals(stage()) && !busy(), 20 * 60, () -> goTo(s3, "markings", -90, 10));
        step("pulse cinematic", 0, () -> "pulse".equals(stage()), 200, () -> {});
        step("the markings pulse", 40, () -> shot("s003_05_pulse"));
        step("Scene 003 complete", 0, () -> { ServerPlayer sp = serverPlayer(); return sp != null && ModAttachments.story(sp).isSceneComplete("mc001_s003"); },
                20 * 90, () -> {
                    shot("s003_06_mission_complete");
                    logState();
                    check(ModAttachments.story(serverPlayer()).journal().contains("northern_trail"), "Scene 003 journal entry missing");
                });

        // Scene 004 follows on: Orin's workshop and the survey kit.
        SceneDef s4 = Story.content().scene("mc001_s004");
        step("Scene 004 starts", 0, () -> "mc001_s004".equals(activeScene()) && !busy(), 20 * 60, () -> goTo(s4, "workshop_front", 180, 5));
        step("walk into the workshop", 20, () -> goTo(s4, "workshop_inside", 180, 10));
        step("Orin speaks", 0, ClientAutotest::busy, 200, () -> shot("s004_01_orin"));
        step("the sketch", 0, () -> "kit".equals(stage()) && !busy(), 20 * 90, () -> onServer(sp -> face(sp, -110, 35)));
        step("sketch on the table", 15, () -> shot("s004_02_sketch_and_supplies"));
        step("at the workbench", 0, () -> goTo(s4, "workbench_front", -90, 30));
        step("Orin's warning", 0, ClientAutotest::busy, 200, () -> shot("s004_03_pickaxe_warning"));
        // The kit is made by crafting; here the result of that crafting is put in the inventory.
        step("craft the survey kit", 0, () -> !busy(), 20 * 60, () -> onServer(sp -> {
            for (net.minecraft.world.item.ItemStack it : List.of(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COMPASS),
                    new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.LEAD), new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WHITE_DYE),
                    new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.TORCH, 8), new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE))) {
                sp.getInventory().add(it);
            }
        }));
        step("compass cinematic", 0, () -> "compass".equals(stage()), 200, () -> {});
        step("the needle spins to the paper", 50, () -> shot("s004_04_compass_cinematic"));
        step("Scene 004 complete", 0, () -> { ServerPlayer sp = serverPlayer(); return sp != null && ModAttachments.story(sp).isSceneComplete("mc001_s004"); },
                20 * 90, () -> {
                    shot("s004_05_mission_complete");
                    logState();
                    check(ModAttachments.story(serverPlayer()).journal().contains("survey_kit"), "Scene 004 journal entry missing");
                });

        // Scene 005 follows on: Bram's volunteers secure the north road. The building is done by placing
        // and breaking the blocks the stages check, the way a player would.
        SceneDef s5 = Story.content().scene("mc001_s005");
        step("Scene 005 starts", 0, () -> "mc001_s005".equals(activeScene()) && !busy(), 20 * 60, () -> goTo(s5, "gate_outside", 180, 10));
        step("volunteers cinematic", 0, ClientAutotest::busy, 200, () -> {});
        step("Bram gathers volunteers", 60, () -> shot("s005_01_volunteers"));
        step("volunteers talk", 0, () -> "barricade".equals(stage()) && !busy(), 20 * 90, () -> onServer(sp -> moveTo(sp, s5, 6, -28, 180)));
        step("the barricade gaps", 20, () -> shot("s005_02_barricade_gaps"));
        Object[][] jobs = {{"barricade", net.minecraft.world.level.block.Blocks.OAK_PLANKS, "platform"},
                {"platform", net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS, "lights"},
                {"lights", net.minecraft.world.level.block.Blocks.TORCH, "supplies"},
                {"supplies", net.minecraft.world.level.block.Blocks.BARREL, "hazard"}};
        for (Object[] job : jobs) {
            String stage = (String) job[0], next = (String) job[2];
            step("build: " + stage, 0, () -> stage.equals(stage()) && !busy(), 20 * 30,
                    () -> fillSpots(s5, stage, ((net.minecraft.world.level.block.Block) job[1]).defaultBlockState()));
            step(stage + " done", 0, () -> next.equals(stage()), 100, () -> {});
        }
        step("the rockfall", 0, ClientAutotest::busy, 100, () -> onServer(sp -> moveTo(sp, s5, 1, -36, 180)));
        step("rocks across the road", 30, () -> shot("s005_03_rockfall"));
        step("clear the rubble", 0, () -> "hazard".equals(stage()) && !busy(), 20 * 60,
                () -> fillSpots(s5, "hazard", net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()));
        step("worker rescued", 0, () -> "to_platform".equals(stage()) && !busy(), 20 * 60, () -> shot("s005_04_rescued"));
        step("the finished work site", 0, () -> onServer(sp -> moveTo(sp, s5, 3, -26, -150)));
        step("work site photo", 30, () -> shot("s005_05_worksite_done"));
        step("climb the platform", 0, () -> goTo(s5, "platform_top", 180, 0));
        step("spot cinematic", 0, () -> "spot".equals(stage()), 200, () -> {});
        step("the monument on the horizon", 80, () -> shot("s005_06_monument_spotted"));
        step("there it is", 0, () -> "rise".equals(stage()), 20 * 90, () -> {});
        step("the camera rises", 90, () -> shot("s005_07_act1_complete"));
        step("Scene 005 complete", 0, () -> { ServerPlayer sp = serverPlayer(); return sp != null && ModAttachments.story(sp).isSceneComplete("mc001_s005"); },
                20 * 90, () -> {
                    shot("s005_08_mission_complete");
                    logState();
                    check(ModAttachments.story(serverPlayer()).journal().contains("north_road"), "Scene 005 journal entry missing");
                });

        // Scene 006 follows on: the next morning the party climbs to the monument (its own site, at the
        // anchor 40, 108, -200). The stair is rebuilt and the earth dug away block by block, as in Scene 005.
        SceneDef s6 = Story.content().scene("mc001_s006");
        step("Scene 006 starts", 0, () -> "mc001_s006".equals(activeScene()), 20 * 60, () -> {});
        step("the approach", 80, () -> shot("s006_01_approach"));
        step("up the causeway", 0, () -> "to_courtyard".equals(stage()) && !busy(), 20 * 60, () -> goTo(s6, "courtyard", 180, -12));
        step("Bram at the monument", 0, ClientAutotest::busy, 200, () -> shot("s006_02_courtyard"));
        step("the collapsed stair", 0, () -> "entrance".equals(stage()) && !busy(), 20 * 90, () -> goTo(s6, "courtyard", 180, 12));
        step("stair gap photo", 20, () -> shot("s006_03_collapsed_stair"));
        step("build the stair", 0, () -> fillSpots(s6, "entrance", net.minecraft.world.level.block.Blocks.DEEPSLATE_BRICKS.defaultBlockState()));
        step("stair built", 0, () -> "panels".equals(stage()), 100, () -> goTo(s6, "stair_top", 180, 10));
        step("the terrace", 20, () -> shot("s006_04_terrace"));
        Object[][] panels = {{"panel_west", -6, 4, 5, 180f, 35f}, {"panel_east", 6, 4, 5, 180f, 35f},
                {"panel_north", 5, 4, -3, 180f, -5f}, {"fracture", 0, 4, 3, 180f, 30f}};
        for (Object[] c : panels) {
            String clueId = (String) c[0];
            SceneDef.ClueDef clue = s6.clues().get(clueId);
            step("walk to " + clueId, 0, () -> !busy(), 20 * 60, () -> onServer(sp -> {
                double[] p = ModDimensions.absolute(s6, new SceneDef.Location((int) c[1], (int) c[2], (int) c[3], 1));
                sp.teleportTo(sp.serverLevel(), p[0], p[1], p[2], (float) c[4], (float) c[5]);
            }));
            step("photograph " + clueId, 15, () -> shot("s006_clue_" + clueId));
            step("examine " + clueId, 0, () -> useBlock(s6, clue.pos()));
            step(clueId + " recorded", 0, () -> clueFound(clueId), 100, () -> {});
        }
        step("the fallen earth", 0, () -> "uncover".equals(stage()) && !busy(), 20 * 30, () -> goTo(s6, "carving", 180, 10));
        step("earth photo", 20, () -> shot("s006_05_fallen_earth"));
        step("dig it away", 0, () -> fillSpots(s6, "uncover", net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()));
        step("six towers", 0, ClientAutotest::busy, 100, () -> {});
        step("the carving uncovered", 30, () -> shot("s006_06_six_structures"));
        step("pillar cinematic", 0, () -> "light".equals(stage()), 20 * 90, () -> {});
        step("the pillar lights", 70, () -> shot("s006_07_pillar_light"));
        step("Scene 006 complete", 0, () -> { ServerPlayer sp = serverPlayer(); return sp != null && ModAttachments.story(sp).isSceneComplete("mc001_s006"); },
                20 * 90, () -> {
                    shot("s006_08_mission_complete");
                    logState();
                    check(ModAttachments.story(serverPlayer()).journal().contains("monument"), "Scene 006 journal entry missing");
                });

        // Primordial Steve in daylight, from the front and behind.
        step("appearance outdoors", 0, () -> onServer(sp -> moveTo(sp, scene, -3, 1, 0)));
        step("third person front outdoors", 10, () -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
        step("primordial steve in the square (front)", 30, () -> shot("player_04_square_front"));
        step("third person back outdoors", 0, () -> mc.options.setCameraType(CameraType.THIRD_PERSON_BACK));
        step("primordial steve in the square (back)", 30, () -> {
            shot("player_05_square_back");
            mc.options.setCameraType(CameraType.FIRST_PERSON);
            net.minecraft.world.phys.Vec3 now = npcPos("firstbrook_villager_3");
            log("villager_3 is now at " + now);
            check(routineStart != null && now != null && now.distanceTo(routineStart) > 3, "a villager on a daily routine did not move");
        });

        // A tour of Firstbrook and the valley around it, photographed from a spectator camera.
        step("tour: spectator", 0, () -> {
            mc.options.setCameraType(CameraType.FIRST_PERSON);
            mc.options.hideGui = true;
            onServer(sp -> sp.setGameMode(GameType.SPECTATOR));
        });
        for (Shot t : TOUR) {
            step("tour: go to " + t.name, 0, () -> onServer(sp -> place(sp, scene, t)));
            step("tour: " + t.name, 90, () -> shot("tour_" + t.name));
        }
        if (PANORAMA) {
            step("panorama: position", 0, () -> onServer(sp -> place(sp, scene, PANORAMA_SPOT)));
            step("panorama: capture", 120, () -> {
                Component c = mc.grabPanoramixScreenshot(mc.gameDirectory, 1024, 1024);
                log("panorama: " + c.getString());
            });
        }

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
        step("clear the line-up", 0, () -> onServer(sp -> {
            clear(sp);
            sp.setGameMode(GameType.SURVIVAL);
            moveTo(sp, scene, 2, 3, 180);
        }));
        step("final state", 40, () -> {
            mc.options.hideGui = false;
            logState();
        });

        // Save and Quit, exactly as the pause menu does it, then Continue Story from the main menu.
        step("save and quit", 20, ClientAutotest::saveAndQuit);
        step("back at the main menu", 0, () -> mc.screen instanceof RodTitleScreen && mc.level == null, 20 * 60 * 2, () -> {});
        step("continue is offered", 60, () -> {
            StorySaves.Checkpoint cp = StorySaves.checkpoint();
            check(cp != null, "no checkpoint for Continue Story");
            log("checkpoint: " + cp);
            shot("menu_03_continue_available");
        });
        step("press Continue Story", 0, () -> press(Component.translatable("realitiesofdreams.menu.continue")));
        step("story save reloaded", 0, () -> serverPlayer() != null && mc.player != null && mc.screen == null, 20 * 60 * 4, () -> {
            ServerPlayer sp = serverPlayer();
            PlayerStoryState s = ModAttachments.story(sp);
            check(s.isSceneComplete("mc001_s001"), "Scene 001 progress was not kept");
            check(s.journal().contains("firstbrook_morning"), "journal entry not kept");
            check(sp.level().dimension().equals(ModDimensions.MC_001), "Continue did not return the player to MC-001");
            logState();
        });
        step("continued in Firstbrook", 60, () -> {
            check(ClientAppearance.overridden(mc.player.getUUID()), "Primordial Steve appearance not restored after reload");
            shot("continue_01_back_in_firstbrook");
        });
        step("quit again", 20, ClientAutotest::saveAndQuit);
        step("main menu again", 0, () -> mc.screen instanceof RodTitleScreen && mc.level == null, 20 * 60 * 2, () -> {});
        step("chapter select", 40, () -> mc.setScreen(new ChapterSelectScreen(mc.screen)));
        step("chapter select shown", 40, () -> shot("menu_04_chapter_select"));
        step("credits", 0, () -> mc.setScreen(new RodCreditsScreen(new RodTitleScreen())));
        step("credits rolling", 120, () -> shot("menu_05_credits"));
        step("finish", 20, ClientAutotest::finish);
    }

    /** A camera position relative to Firstbrook's anchor. */
    private record Shot(String name, double x, double y, double z, float yaw, float pitch) {}

    private static final Shot PANORAMA_SPOT = new Shot("panorama", 6.5, 9, 4.5, 200, 0);

    private static final List<Shot> TOUR = List.of(
            new Shot("01_firstbrook_from_south", 4, 40, 84, 180, 24),
            new Shot("02_firstbrook_from_east", 78, 34, 6, 90, 22),
            new Shot("03_village_square", 8, 6, 8, 135, 18),
            new Shot("04_steve_house", 9, 4, 6, 315, 10),
            new Shot("05_lane_and_bridge", 22, 4, 7, 300, 8),
            new Shot("06_elder_rowans_house", -3, 5, 6, 129, 12),
            new Shot("07_orins_workshop", 16, 4, 3, 156, 10),
            new Shot("08_bram_guard_station", 0, 4, -11, 210, 10),
            new Shot("09_north_wall_from_the_trail", -4, 7, -36, 10, 10),
            new Shot("10_market", -2, 5, 12, 122, 14),
            new Shot("11_barn_and_pens", -6, 10, 44, 124, 20),
            new Shot("12_training_ground", -36, 12, 2, 180, 35),
            new Shot("13_riverbank_and_dock", 27, 4, 34, 229, 10),
            new Shot("14_northern_hills_ridge", 8, 20, -60, 180, 8),
            new Shot("15_ruin_on_the_ridge", 0, 34, -100, 200, 16),
            new Shot("16_waterfall", 32, 6, -40, 180, 4),
            new Shot("17_southmere_lake", 32, 14, 80, 0, 18),
            new Shot("18_awakening_valley_north", 10, 60, -150, 180, 12),
            new Shot("19_steve_house_inside", 14.5, 1, 20.3, 200, 15),
            new Shot("20_orin_workshop_inside", 10.5, 1, -6, 200, 12),
            new Shot("21_council_fire", -16, 4, 6, 120, 15),
            new Shot("22_millers_and_bakery", -6, 5, 9, 200, 12),
            new Shot("23_firstbrook_aerial", 0, 70, 30, 180, 55));

    private static void place(ServerPlayer sp, SceneDef scene, Shot t) {
        net.minecraft.core.BlockPos a = ModDimensions.anchor(scene);
        sp.teleportTo(sp.serverLevel(), a.getX() + t.x, a.getY() + t.y, a.getZ() + t.z, t.yaw, t.pitch);
    }

    private static void step(String label, int delay, Runnable action) {
        steps.add(new Step(label, delay, null, 0, action));
    }

    private static void step(String label, int delay, BooleanSupplier until, int timeout, Runnable action) {
        steps.add(new Step(label, delay, until, timeout, action));
    }

    // ------------------------------------------------------------------ helpers

    /** Presses the button on the current screen whose label matches, as a click would. */
    private static void press(Component label) {
        Screen screen = Minecraft.getInstance().screen;
        String want = label.getString();
        if (screen != null) {
            for (GuiEventListener l : screen.children()) {
                if (l instanceof AbstractWidget w && w.getMessage().getString().equals(want)) {
                    check(w.active, "button \"" + want + "\" is disabled");
                    w.onClick(w.getX() + 2, w.getY() + 2);
                    log("pressed \"" + want + "\" on " + screen.getClass().getSimpleName());
                    return;
                }
            }
        }
        fail("no button \"" + want + "\" on " + (screen == null ? "no screen" : screen.getClass().getSimpleName()));
    }

    private static void saveAndQuit() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        log("save and quit");
        mc.level.disconnect();
        mc.disconnect(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
        mc.setScreen(new net.minecraft.client.gui.screens.TitleScreen()); // as the pause menu does; our menu takes its place
    }

    private static void check(boolean ok, String why) {
        if (!ok) fail(why);
    }

    private static void onServer(Consumer<ServerPlayer> task) {
        Minecraft mc = Minecraft.getInstance();
        MinecraftServer server = mc.getSingleplayerServer();
        if (server == null || mc.player == null) return;
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

    private static String activeScene() {
        ServerPlayer sp = serverPlayer();
        return sp == null ? null : ModAttachments.story(sp).activeScene();
    }

    private static boolean busy() {
        ServerPlayer sp = serverPlayer();
        return sp != null && (net.realitiesofdreams.server.StoryManager.get().inDialogue(sp) || net.realitiesofdreams.server.StoryManager.get().cinematic(sp) != null);
    }

    private static void goTo(SceneDef scene, String location, float yaw, float pitch) {
        onServer(sp -> {
            double[] p = ModDimensions.absolute(scene, scene.location(location));
            sp.teleportTo(sp.serverLevel(), p[0], p[1], p[2], yaw, pitch);
        });
    }

    private static boolean clueFound(String id) {
        ServerPlayer sp = serverPlayer();
        return sp != null && ModAttachments.story(sp).cluesFound().contains(id);
    }

    /** Right-clicks a block the way the player does, through the client's interaction path. */
    private static void useBlock(SceneDef scene, double[] rel) {
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.core.BlockPos pos = ModDimensions.absoluteBlock(scene, rel);
        mc.gameMode.useItemOn(mc.player, net.minecraft.world.InteractionHand.MAIN_HAND,
                new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos), net.minecraft.core.Direction.UP, pos, false));
    }

    private static String stage() {
        ServerPlayer sp = serverPlayer();
        return sp == null ? null : ModAttachments.story(sp).stage();
    }

    private static boolean hasFlag(String flag) {
        ServerPlayer sp = serverPlayer();
        return sp != null && ModAttachments.story(sp).hasFlag(flag);
    }

    private static net.minecraft.world.phys.Vec3 npcPos(String character) {
        ServerPlayer sp = serverPlayer();
        if (sp == null) return null;
        StoryNpcEntity n = NpcDirector.find(sp.serverLevel(), character);
        return n == null ? null : n.position();
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

    /** Sets every spot a BLOCKS stage checks, as a player placing (or breaking) those blocks would. */
    private static void fillSpots(SceneDef scene, String stage, net.minecraft.world.level.block.state.BlockState block) {
        onServer(sp -> {
            net.minecraft.core.BlockPos anchor = ModDimensions.anchor(scene);
            for (int[] s : net.realitiesofdreams.story.engine.SceneEngine.stageBlocks(scene.stage(stage)).spots())
                sp.serverLevel().setBlock(anchor.offset(s[0], s[1], s[2]), block, 3);
        });
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
        Minecraft mc = Minecraft.getInstance();
        log(String.format("player: %.1f,%.1f,%.1f in %s yaw %.0f pitch %.0f, camera %s", sp.getX(), sp.getY(), sp.getZ(), sp.level().dimension().location(),
                sp.getYRot(), sp.getXRot(),
                mc.getCameraEntity() == mc.player ? "player (" + mc.options.getCameraType() + ")" : String.valueOf(mc.getCameraEntity())));
        long voices = mc.getSoundManager().getAvailableSounds().stream().filter(id -> id.getNamespace().equals(VoicePlayer.NAMESPACE)).count();
        log("voice: " + voices + " recordings loaded, speaking now: " + VoicePlayer.speaking());
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
