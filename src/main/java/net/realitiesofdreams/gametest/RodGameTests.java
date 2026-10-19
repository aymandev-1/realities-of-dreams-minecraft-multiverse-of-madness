package net.realitiesofdreams.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.entity.StoryNpcEntity;
import net.realitiesofdreams.network.CinematicPayload;
import net.realitiesofdreams.network.DialogueLinePayload;
import net.realitiesofdreams.network.HintPayload;
import net.realitiesofdreams.registry.ModAttachments;
import net.realitiesofdreams.registry.ModEntities;
import net.realitiesofdreams.server.NpcDirector;
import net.realitiesofdreams.server.StoryManager;
import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.scene.SceneValidator;
import net.realitiesofdreams.story.state.PlayerStoryState;
import net.realitiesofdreams.world.ModDimensions;
import net.realitiesofdreams.world.SceneWorld;

import java.util.ArrayList;
import java.util.List;

/** In-game tests, run on a real dedicated server by `gradlew runGameTestServer` (and in CI). */
@GameTestHolder(RealitiesOfDreams.MODID)
@PrefixGameTestTemplate(false)
public final class RodGameTests {
    private RodGameTests() {}

    @GameTest(template = "empty")
    public static void storyContentIsValid(GameTestHelper helper) {
        List<String> errors = SceneValidator.validate(Story.content());
        helper.assertTrue(errors.isEmpty(), String.join("; ", errors));
        helper.assertTrue(Story.content().characters().size() >= 151, "character registry incomplete");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void npcCarriesCharacterAndCannotBeHurt(GameTestHelper helper) {
        StoryNpcEntity npc = helper.spawn(ModEntities.STORY_NPC.get(), 1, 2, 1);
        npc.setCharacter("elara");
        helper.assertTrue("elara".equals(npc.characterId()), "character id not stored");
        helper.assertTrue(npc.getCustomName() != null && npc.getCustomName().getString().equals("Elara"), "name not set");
        npc.hurt(helper.getLevel().damageSources().generic(), 100);
        helper.assertTrue(npc.isAlive() && npc.getHealth() == npc.getMaxHealth(), "story NPC took damage");
        CompoundTag tag = new CompoundTag();
        npc.saveWithoutId(tag);
        helper.assertTrue("elara".equals(tag.getString("Character")), "character not saved");
        npc.discard();
        helper.succeed();
    }

    /**
     * The game-test server only creates the vanilla dimensions, so this checks that the MC-001
     * dimension definition loads from the mod's data, and checks the terrain when the level exists
     * (it always does in a normal world).
     */
    @GameTest(template = "empty")
    public static void mc001UniverseIsDefined(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        var access = server.registryAccess();
        helper.assertTrue(access.registryOrThrow(Registries.DIMENSION_TYPE).containsKey(RealitiesOfDreams.id("mc_001")), "MC-001 dimension type not loaded");
        ServerLevel mc001 = server.getLevel(ModDimensions.MC_001);
        if (mc001 != null) {
            helper.assertTrue(mc001.getBlockState(new BlockPos(-40, 63, 40)).is(Blocks.GRASS_BLOCK), "Firstbrook ground is not grass at y=63");
            helper.assertTrue(mc001.getBlockState(new BlockPos(-40, 64, 40)).isAir(), "Firstbrook is not open above ground");
        }
        helper.succeed();
    }

    // ------------------------------------------------------------------ Scene 001 in a real world

    private static SceneDef scene001() {
        return Story.content().scene("mc001_s001");
    }

    /**
     * Stages Firstbrook in the test level (the test server has no MC-001 dimension) and keeps its
     * chunks loaded and ticking so the characters move.
     */
    private static ServerLevel stageFirstbrook(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ModDimensions.useLevelForTesting(level);
        for (int cx = -4; cx <= 3; cx++) for (int cz = -9; cz <= 3; cz++) level.setChunkForced(cx, cz, true);
        SceneWorld.ensureBuilt(level, scene001());
        return level;
    }

    private static void unstage(ServerLevel level) {
        for (int cx = -4; cx <= 3; cx++) for (int cz = -9; cz <= 3; cz++) level.setChunkForced(cx, cz, false);
        ModDimensions.useLevelForTesting(null);
    }

    private static BlockPos rel(int x, int y, int z) {
        return ModDimensions.anchor(scene001()).offset(x, y, z);
    }

    @GameTest(template = "empty", batch = "firstbrook_build", timeoutTicks = 600)
    public static void firstbrookBuilds(GameTestHelper helper) {
        ServerLevel level = stageFirstbrook(helper);
        try {
            helper.assertTrue(level.getBlockState(rel(19, 0, 15)).is(Blocks.RED_BED), "Steve's bed missing");
            helper.assertTrue(level.getBlockState(rel(16, 0, 14)).is(Blocks.SPRUCE_DOOR), "Steve's front door missing");
            helper.assertTrue(level.getBlockState(rel(13, 0, 15)).is(Blocks.CRAFTING_TABLE), "crafting table missing");
            helper.assertTrue(level.getBlockState(rel(13, 0, 16)).is(Blocks.FURNACE), "furnace missing");
            helper.assertTrue(level.getBlockEntity(rel(13, 0, 19)) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest
                    && chest.getItem(0).is(Items.BREAD), "breakfast not in the storage chest");
            helper.assertTrue(level.getBlockState(rel(0, -1, 0)).is(Blocks.WATER), "village well missing");
            helper.assertTrue(level.getBlockState(rel(10, -1, 4)).is(Blocks.FARMLAND), "farm missing");
            helper.assertTrue(level.getBlockState(rel(32, -1, 0)).is(Blocks.WATER), "river missing");
            helper.assertTrue(level.getBlockState(rel(32, -1, 11)).is(Blocks.SPRUCE_PLANKS) || level.getBlockState(rel(32, -1, 11)).is(Blocks.STRIPPED_SPRUCE_LOG), "eastern bridge missing");
            helper.assertTrue(level.getBlockState(rel(10, 0, -9)).is(Blocks.ANVIL) || level.getBlockState(rel(11, 0, -9)).is(Blocks.ANVIL), "Orin's anvil missing");
            int ridge = SceneWorld.hillHeight(10, -112);
            helper.assertTrue(ridge > 15, "northern ridge too low: " + ridge);
            helper.assertTrue(!level.getBlockState(rel(8, ridge + 2, -112)).isAir(), "ruin on the ridge missing");
            var standBox = new net.minecraft.world.phys.AABB(rel(12, 0, 14)).expandTowards(9, 4, 8);
            helper.assertTrue(level.getEntitiesOfClass(net.minecraft.world.entity.decoration.ArmorStand.class, standBox).stream()
                    .anyMatch(a -> a.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).is(Items.LEATHER_CHESTPLATE)), "armour stand missing");
            helper.assertTrue(level.getEntitiesOfClass(net.minecraft.world.entity.decoration.ItemFrame.class, standBox).size() == 3, "trophy frames missing");
            // Every placed character's spot is open ground they can stand on.
            for (SceneDef.NpcPlacement n : scene001().npcs()) {
                BlockPos at = ModDimensions.absoluteBlock(scene001(), new double[] {scene001().location(n.location()).x(), 0, scene001().location(n.location()).z()});
                helper.assertTrue(level.getBlockState(at).getCollisionShape(level, at).isEmpty() && level.getBlockState(at.above()).isAir(),
                        n.character() + " is placed inside a block at " + n.location());
                helper.assertTrue(!level.getBlockState(at.below()).getCollisionShape(level, at.below()).isEmpty() || level.getBlockState(at.below()).is(Blocks.FARMLAND)
                        || level.getBlockState(at.below()).is(Blocks.DIRT_PATH), n.character() + " has no floor at " + n.location());
            }
            helper.succeed();
        } finally {
            unstage(level);
        }
    }

    /** The few blocks the story depends on are protected; the rest of Firstbrook is ordinary Minecraft. */
    @GameTest(template = "empty", batch = "firstbrook_build", timeoutTicks = 600)
    public static void storyBlocksAreProtected(GameTestHelper helper) {
        ServerLevel level = stageFirstbrook(helper);
        try {
            SceneDef s = scene001();
            helper.assertTrue(SceneWorld.isProtected(level, s, rel(16, 0, 14)), "Steve's front door is not protected");
            helper.assertTrue(SceneWorld.isProtected(level, s, rel(19, 0, 15)), "Steve's bed is not protected");
            helper.assertTrue(SceneWorld.isProtected(level, s, rel(0, -1, 0)), "the well is not protected");
            helper.assertTrue(SceneWorld.isProtected(level, s, rel(32, -1, 11)), "the eastern bridge is not protected");
            helper.assertTrue(!SceneWorld.isProtected(level, s, rel(13, 0, 15)), "Steve's crafting table should be usable and breakable");
            helper.assertTrue(!SceneWorld.isProtected(level, s, rel(-30, -1, 30)), "ordinary ground must stay minable");
            helper.assertTrue(!SceneWorld.isProtected(level, s, rel(12, 1, 30)), "open air must stay buildable");
            // Every house has a door and a roof; the watchtower has a ladder to its lookout.
            helper.assertTrue(level.getBlockState(rel(-8, 0, -3)).getBlock() instanceof net.minecraft.world.level.block.DoorBlock, "Elder Rowan's door missing");
            helper.assertTrue(level.getBlockState(rel(5, 0, -18)).getBlock() instanceof net.minecraft.world.level.block.DoorBlock, "guard station door missing");
            helper.assertTrue(level.getBlockState(rel(-10, 5, -20)).is(Blocks.LADDER), "watchtower ladder missing");
            helper.assertTrue(level.getBlockState(rel(-10, 10, -18)).is(Blocks.SPRUCE_PLANKS), "watchtower lookout missing");
            helper.assertTrue(level.getBlockState(rel(16, 6, 17)).isAir() && !level.getBlockState(rel(16, 8, 17)).isAir(), "Steve's house has no pitched roof");
            helper.succeed();
        } finally {
            unstage(level);
        }
    }

    /** Plays Scene 001 start to finish through the real StoryManager with the characters in the world. */
    @GameTest(template = "empty", batch = "firstbrook_play", timeoutTicks = 1200)
    public static void scene001PlaysThroughInFirstbrook(GameTestHelper helper) {
        ServerLevel level = stageFirstbrook(helper);
        StoryManager previous = StoryManager.get();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        List<CustomPacketPayload> sent = new ArrayList<>();
        StoryManager m = new StoryManager((p, payload) -> sent.add(payload));
        StoryManager.setInstanceForTesting(m);
        try {
            ModAttachments.story(player).setIntroOffered(true);
            m.startScene(player, "mc001_s001", true);
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof CinematicPayload c && c.id().equals("mc001_s001_opening")), "opening cinematic not sent");
            helper.assertTrue(player.hasEffect(net.minecraft.world.effect.MobEffects.INVISIBILITY), "player visible during the opening");
            m.tick(player);
            StoryNpcEntity actor = NpcDirector.find(level, "primordial_steve");
            helper.assertTrue(actor != null && actor.isSleeping(), "Steve is not asleep in his bed at the start");
            helper.assertTrue(NpcDirector.find(level, "elara") != null, "Elara not in Firstbrook");
            helper.assertTrue(NpcDirector.find(level, "firstbrook_guard_1") != null, "village guards missing");

            m.onCinematicDone(player, "mc001_s001_opening");
            expectStage(helper, m, player, "leave_house");
            helper.assertTrue(NpcDirector.find(level, "primordial_steve") == null, "the sleeping actor was not replaced by the player");
            helper.assertTrue(!player.hasEffect(net.minecraft.world.effect.MobEffects.INVISIBILITY), "player still invisible");
            helper.assertTrue(player.position().distanceTo(net.minecraft.world.phys.Vec3.atBottomCenterOf(rel(18, 0, 15))) < 0.6, "player not standing where Steve woke: " + player.position());
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof HintPayload), "movement hint not shown");

            // Walk out of the front door: Elara joins and the walking conversation starts by itself.
            teleport(player, level, 16, 12);
            tickUntil(m, player, () -> !"leave_house".equals(m.state(player).stage()), 40);
            expectStage(helper, m, player, "explore");
            StoryNpcEntity elara = NpcDirector.find(level, "elara");
            helper.assertTrue(elara != null && player.getUUID().equals(elara.followTarget()), "Elara is not walking with Steve");
            helper.assertTrue(m.inDialogue(player) && sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.speakerId().equals("elara")
                    && l.text().equals("You repaired the eastern bridge yesterday?")), "walking conversation did not start");
            finishDialogues(m, player);
            helper.assertTrue(m.state(player).hasFlag("mc001.s001.walk1"), "walking conversation did not finish");

            // Passing the farmer: he calls out without being clicked.
            teleport(player, level, 13, 12);
            tickUntil(m, player, () -> m.inDialogue(player), 60);
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().equals("Morning, Steve!")), "the farmer did not greet Steve");
            finishDialogues(m, player);
            expectStage(helper, m, player, "to_square");

            teleport(player, level, 2, 3);
            tickUntil(m, player, () -> m.cinematic(player) != null, 40);
            helper.assertTrue("mc001_s001_ridge".equals(m.cinematic(player)), "ridge cinematic did not play");
            m.onCinematicDone(player, "mc001_s001_ridge");
            helper.assertTrue(m.inDialogue(player), "\"Did you feel that?\" did not start");
            finishDialogues(m, player);

            PlayerStoryState state = ModAttachments.story(player);
            helper.assertTrue(state.isSceneComplete("mc001_s001"), "scene not completed");
            helper.assertTrue("mc001_s002".equals(state.activeScene()), "Scene 002 did not follow on: " + state.activeScene());
            StoryNpcEntity rowan = NpcDirector.find(level, "elder_rowan");
            helper.assertTrue(rowan != null && player.getUUID().equals(rowan.approachTarget()), "Elder Rowan is not coming over");
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().equals("Steve! There you are.")), "Rowan did not call out");
            boolean journal = false, pickaxe = false;
            for (ItemStack s : player.getInventory().items) {
                journal |= s.is(Items.WRITTEN_BOOK);
                pickaxe |= s.is(Items.STONE_PICKAXE);
            }
            helper.assertTrue(journal, "Firstbrook journal entry not given");
            helper.assertTrue(pickaxe, "starter tools not given");
            helper.assertTrue(state.hasFlag("mc001.firstbrook.explore_unlocked") && state.hasValue("rel.elara"), "persistent consequences missing");

            // Save and load the attachment the way the game does when the player logs out and back in.
            CompoundTag saved = player.getData(ModAttachments.STORY).serializeNBT(level.registryAccess());
            ModAttachments.StoryData reloaded = new ModAttachments.StoryData();
            reloaded.deserializeNBT(level.registryAccess(), saved);
            helper.assertTrue(reloaded.state().isSceneComplete("mc001_s001") && reloaded.state().hasFlag("mc001.s001.complete")
                    && reloaded.state().journal().contains("firstbrook_morning"), "progress lost on save/load");

            // ---- Scene 002: Rowan's warning starts by itself when he reaches Steve.
            tickUntil(m, player, () -> m.inDialogue(player), 400);
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().equals("Steve, I've been looking everywhere for you.")),
                    "Rowan's warning did not start");
            finishDialogues(m, player);
            expectStage(helper, m, player, "inspect");
            SceneDef s2 = Story.content().scene("mc001_s002");
            for (SceneDef.ClueDef clue : s2.clues().values()) {
                BlockPos at = ModDimensions.absoluteBlock(s2, clue.pos());
                helper.assertTrue(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(level.getBlockState(at).getBlock()).toString().equals(clue.block()),
                        clue.id() + " block not placed at " + at);
                // Examine it the way a player does: right-click the block.
                m.onBlockUse(player, at);
                helper.assertTrue(ModAttachments.story(player).cluesFound().contains(clue.id()), clue.id() + " did not respond to a right-click");
                if (clue.id().equals("tower_report")) {
                    helper.assertTrue(level.getBlockState(at).getValue(net.minecraft.world.level.block.LecternBlock.HAS_BOOK), "the night watch's report is not on the lectern");
                }
                finishDialogues(m, player);
            }
            expectStage(helper, m, player, "bram");
            tickUntil(m, player, () -> m.inDialogue(player), 400);
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().equals("The northern path isn't safe. Loose rocks came down just before dawn.")),
                    "Bram's report did not start");
            finishDialogues(m, player);
            helper.assertTrue("mc001_s002_compass".equals(m.cinematic(player)), "compass cinematic did not play");
            m.onCinematicDone(player, "mc001_s002_compass");
            finishDialogues(m, player);
            PlayerStoryState s2state = ModAttachments.story(player);
            helper.assertTrue(s2state.isSceneComplete("mc001_s002"), "Scene 002 not completed");
            helper.assertTrue("mc001_s003".equals(s2state.activeScene()), "Scene 003 did not follow on: " + s2state.activeScene());
            helper.assertTrue(s2state.hasFlag("mc001.s003.survey_approved") && s2state.journal().contains("northern_disturbance"), "Scene 002 outcome missing");
            boolean compass = false;
            for (ItemStack st : player.getInventory().items) compass |= st.is(Items.COMPASS) && st.getHoverName().getString().equals("Strange Compass");
            helper.assertTrue(compass, "the Strange Compass was not given");
            helper.succeed();
        } finally {
            NpcDirector.releaseAll(player);
            StoryManager.setInstanceForTesting(previous);
            level.getServer().getPlayerList().remove(player);
            unstage(level);
        }
    }

    /** Plays Scene 003 on the northern trail: barrier, tracks, the climb to the scout's camp, the marker and the pulse. */
    @GameTest(template = "empty", batch = "firstbrook_trail", timeoutTicks = 1200)
    public static void scene003PlaysOnTheNorthernTrail(GameTestHelper helper) {
        ServerLevel level = stageFirstbrook(helper);
        for (int cx = -1; cx <= 1; cx++) for (int cz = -7; cz <= -2; cz++) level.setChunkForced(cx, cz, true);
        StoryManager previous = StoryManager.get();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        List<CustomPacketPayload> sent = new ArrayList<>();
        StoryManager m = new StoryManager((p, payload) -> sent.add(payload));
        StoryManager.setInstanceForTesting(m);
        try {
            ModAttachments.story(player).setIntroOffered(true);
            m.restartScene(player, "mc001_s003");
            expectStage(helper, m, player, "to_trail");
            SceneDef s3 = Story.content().scene("mc001_s003");
            for (SceneDef.ClueDef clue : s3.clues().values()) {
                BlockPos at = ModDimensions.absoluteBlock(s3, clue.pos());
                helper.assertTrue(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(level.getBlockState(at).getBlock()).toString().equals(clue.block()),
                        clue.id() + " block not placed at " + at + ": " + level.getBlockState(at));
            }
            helper.assertTrue(level.getBlockState(rel(9, 17, -76)).isSolid() && level.getBlockState(rel(9, 18, -76)).isAir(), "the outcrop to climb is missing");
            helper.assertTrue(level.getBlockState(rel(9, 21, -92)).is(Blocks.CHISELED_TUFF), "the carved stone is missing");

            walk(m, player, level, s3, "trail_gate");
            expectStage(helper, m, player, "follow_trail");
            walk(m, player, level, s3, "footprints");
            helper.assertTrue("mc001_s003_tracks".equals(m.cinematic(player)), "tracks cinematic did not play");
            m.onCinematicDone(player, "mc001_s003_tracks");
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().equals("Look at this.")), "Elara did not speak at the tracks");
            finishDialogues(m, player);
            expectStage(helper, m, player, "investigate");
            walk(m, player, level, s3, "mid_trail");
            helper.assertTrue(m.inDialogue(player), "the valley conversation did not start");
            finishDialogues(m, player);
            for (String id : List.of("dropped_lantern", "scout_pack", "survey_marker")) {
                m.onBlockUse(player, ModDimensions.absoluteBlock(s3, s3.clues().get(id).pos()));
                helper.assertTrue(ModAttachments.story(player).cluesFound().contains(id), id + " did not respond to a right-click");
                finishDialogues(m, player);
            }
            expectStage(helper, m, player, "markings");
            walk(m, player, level, s3, "markings");
            helper.assertTrue("mc001_s003_pulse".equals(m.cinematic(player)), "pulse cinematic did not play");
            m.onCinematicDone(player, "mc001_s003_pulse");
            finishDialogues(m, player);
            PlayerStoryState st = ModAttachments.story(player);
            helper.assertTrue(st.isSceneComplete("mc001_s003") && st.journal().contains("northern_trail"), "Scene 003 not completed");
            helper.assertTrue(level.getBlockState(rel(9, 21, -92)).is(Blocks.CHISELED_TUFF), "the markings did not go dark again");
            boolean map = false;
            for (ItemStack it : player.getInventory().items) map |= it.is(Items.MAP);
            helper.assertTrue(map, "the Northern Trail map was not given");
            helper.succeed();
        } finally {
            NpcDirector.releaseAll(player);
            StoryManager.setInstanceForTesting(previous);
            level.getServer().getPlayerList().remove(player);
            for (int cx = -1; cx <= 1; cx++) for (int cz = -7; cz <= -2; cz++) level.setChunkForced(cx, cz, false);
            unstage(level);
        }
    }

    /** Plays Scene 004 in Orin's workshop: the conversation, the sketch, crafting the survey kit, the compass cinematic. */
    @GameTest(template = "empty", batch = "firstbrook_workshop", timeoutTicks = 1200)
    public static void scene004BuildsTheSurveyKit(GameTestHelper helper) {
        ServerLevel level = stageFirstbrook(helper);
        StoryManager previous = StoryManager.get();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        List<CustomPacketPayload> sent = new ArrayList<>();
        StoryManager m = new StoryManager((p, payload) -> sent.add(payload));
        StoryManager.setInstanceForTesting(m);
        try {
            ModAttachments.story(player).setIntroOffered(true);
            m.restartScene(player, "mc001_s004");
            SceneDef s4 = Story.content().scene("mc001_s004");
            walk(m, player, level, s4, "workshop_inside");
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().equals("Before you ask, no, I haven't worked out what happened.")),
                    "Orin did not speak when Steve came in");
            finishDialogues(m, player);
            helper.assertTrue(!level.getEntitiesOfClass(net.minecraft.world.entity.decoration.ItemFrame.class,
                    new net.minecraft.world.phys.AABB(rel(12, 1, -6))).isEmpty(), "Elara's sketch is not on the survey table");
            expectStage(helper, m, player, "kit");
            helper.assertTrue(level.getBlockState(rel(12, 0, -7)).is(Blocks.CRAFTING_TABLE), "workbench missing");
            helper.assertTrue(level.getBlockEntity(rel(10, 0, -10)) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity c
                    && c.countItem(Items.SLIME_BALL) == 1 && c.countItem(Items.IRON_INGOT) >= 3, "survey supplies missing");
            // Carry the kit the way a player who has crafted it would; the story counts it.
            player.getInventory().clearContent();
            for (ItemStack it : List.of(new ItemStack(Items.COMPASS), new ItemStack(Items.LEAD), new ItemStack(Items.WHITE_DYE),
                    new ItemStack(Items.TORCH, 4))) player.getInventory().add(it);
            for (int i = 0; i < 40; i++) m.tick(player);
            expectStage(helper, m, player, "kit");
            helper.assertTrue(ModAttachments.story(player).value(net.realitiesofdreams.story.engine.SceneEngine.ITEMS_PROGRESS) == 3, "kit progress not counted");
            // Standing by the workbench, Orin gives his warning about the pickaxe.
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().equals("This time, don't.")) || m.inDialogue(player),
                    "Orin's pickaxe warning did not play at the workbench");
            finishDialogues(m, player);
            player.getInventory().add(new ItemStack(Items.TORCH, 4));
            player.getInventory().add(new ItemStack(Items.IRON_PICKAXE));
            for (int i = 0; i < 40 && m.cinematic(player) == null; i++) m.tick(player);
            helper.assertTrue("mc001_s004_compass".equals(m.cinematic(player)), "compass cinematic did not play once the kit was complete");
            m.onCinematicDone(player, "mc001_s004_compass");
            finishDialogues(m, player);
            PlayerStoryState st = ModAttachments.story(player);
            helper.assertTrue(st.isSceneComplete("mc001_s004") && st.hasFlag("mc001.orin.adviser"), "Scene 004 not completed");
            helper.succeed();
        } finally {
            NpcDirector.releaseAll(player);
            StoryManager.setInstanceForTesting(previous);
            level.getServer().getPlayerList().remove(player);
            unstage(level);
        }
    }

    /** Plays Scene 005 on the north road: every job is checked against the blocks actually in the world. */
    @GameTest(template = "empty", batch = "firstbrook_road", timeoutTicks = 1600)
    public static void scene005SecuresTheNorthRoad(GameTestHelper helper) {
        ServerLevel level = stageFirstbrook(helper);
        StoryManager previous = StoryManager.get();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        List<CustomPacketPayload> sent = new ArrayList<>();
        StoryManager m = new StoryManager((p, payload) -> sent.add(payload));
        StoryManager.setInstanceForTesting(m);
        try {
            ModAttachments.story(player).setIntroOffered(true);
            m.restartScene(player, "mc001_s005");
            SceneDef s5 = Story.content().scene("mc001_s005");
            helper.assertTrue(level.getBlockState(rel(3, 0, -32)).is(Blocks.STRIPPED_SPRUCE_LOG), "barricade posts missing");
            helper.assertTrue(level.getBlockState(rel(9, 4, -37)).isAir() && level.getBlockState(rel(8, 4, -37)).is(Blocks.SPRUCE_PLANKS),
                    "the watch platform is not damaged the way the scene expects");
            walk(m, player, level, s5, "gate_outside");
            helper.assertTrue("mc001_s005_volunteers".equals(m.cinematic(player)), "the volunteers cinematic did not play");
            m.onCinematicDone(player, "mc001_s005_volunteers");
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().startsWith("Everyone listen!")), "the volunteers did not talk");
            finishDialogues(m, player);
            int planks = 0;
            for (ItemStack it : player.getInventory().items) if (it.is(Items.OAK_PLANKS)) planks += it.getCount();
            helper.assertTrue(planks == 12, "Bram did not hand over the planks (" + planks + ")");

            Object[][] jobs = {{"barricade", Blocks.OAK_PLANKS}, {"platform", Blocks.SPRUCE_PLANKS}, {"lights", Blocks.TORCH}, {"supplies", Blocks.BARREL}};
            for (Object[] job : jobs) {
                String stage = (String) job[0];
                expectStage(helper, m, player, stage);
                List<int[]> spots = net.realitiesofdreams.story.engine.SceneEngine.stageBlocks(s5.stage(stage)).spots();
                for (int i = 0; i < spots.size(); i++) {
                    if (i == spots.size() - 1) {
                        tickUntil(m, player, () -> false, 20);
                        expectStage(helper, m, player, stage);
                        helper.assertTrue(ModAttachments.story(player).value(net.realitiesofdreams.story.engine.SceneEngine.BLOCKS_PROGRESS) == spots.size() - 1,
                                stage + " progress not counted");
                    }
                    int[] s = spots.get(i);
                    level.setBlock(rel(s[0], s[1], s[2]), ((net.minecraft.world.level.block.Block) job[1]).defaultBlockState(), 3);
                }
                tickUntil(m, player, () -> !stage.equals(m.state(player).stage()), 40);
            }

            expectStage(helper, m, player, "hazard");
            helper.assertTrue(level.getBlockState(rel(1, 0, -40)).isSolid(), "the rockfall did not block the road");
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().equals("Help! I can't get through!")), "the trapped worker did not call out");
            finishDialogues(m, player);
            for (int[] s : net.realitiesofdreams.story.engine.SceneEngine.stageBlocks(s5.stage("hazard")).spots())
                level.setBlock(rel(s[0], s[1], s[2]), Blocks.AIR.defaultBlockState(), 3);
            tickUntil(m, player, () -> m.inDialogue(player), 40);
            helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.text().equals("Nicely done.")), "Bram did not thank Steve");
            finishDialogues(m, player);

            expectStage(helper, m, player, "to_platform");
            walk(m, player, level, s5, "platform_top");
            helper.assertTrue("mc001_s005_spot".equals(m.cinematic(player)), "the monument was not spotted from the platform");
            m.onCinematicDone(player, "mc001_s005_spot");
            finishDialogues(m, player);
            helper.assertTrue("mc001_s005_rise".equals(m.cinematic(player)), "the closing cinematic did not play");
            m.onCinematicDone(player, "mc001_s005_rise");
            PlayerStoryState st = ModAttachments.story(player);
            helper.assertTrue(st.isSceneComplete("mc001_s005") && st.hasFlag("mc001.act1.complete") && st.journal().contains("north_road"), "Scene 005 not completed");
            helper.succeed();
        } finally {
            NpcDirector.releaseAll(player);
            StoryManager.setInstanceForTesting(previous);
            level.getServer().getPlayerList().remove(player);
            unstage(level);
        }
    }

    /** Stands the player on a scene location and lets the story notice. */
    private static void walk(StoryManager m, ServerPlayer p, ServerLevel level, SceneDef scene, String location) {
        double[] a = ModDimensions.absolute(scene, scene.location(location));
        p.teleportTo(level, a[0], a[1], a[2], 180, 0);
        String stage = m.state(p).stage();
        for (int i = 0; i < 40 && stage.equals(m.state(p).stage()) && !m.inDialogue(p) && m.cinematic(p) == null; i++) m.tick(p);
    }

    /** Villagers work and patrol, and Elara keeps up with Steve. */
    @GameTest(template = "empty", batch = "firstbrook_npcs", timeoutTicks = 600)
    public static void firstbrookCharactersMove(GameTestHelper helper) {
        ServerLevel level = stageFirstbrook(helper);
        StoryManager previous = StoryManager.get();
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        StoryManager m = new StoryManager((p, payload) -> {});
        StoryManager.setInstanceForTesting(m);
        ModAttachments.story(player).setIntroOffered(true);
        m.restartScene(player, "mc001_s001");
        m.onCinematicDone(player, "mc001_s001_opening");
        teleport(player, level, 16, 12);
        tickUntil(m, player, () -> !"leave_house".equals(m.state(player).stage()), 40);
        StoryNpcEntity guard = NpcDirector.find(level, "firstbrook_guard_1");
        StoryNpcEntity elara = NpcDirector.find(level, "elara");
        helper.assertTrue(guard != null && elara != null, "characters missing");
        // Earlier tests in this level may have left the guard anywhere on his round: start him at its first point.
        BlockPos laneWest = rel(6, 0, 11);
        guard.teleportTo(laneWest.getX() + 0.5, laneWest.getY(), laneWest.getZ() + 0.5);
        net.minecraft.world.phys.Vec3 guardStart = guard.position();
        teleport(player, level, 24, 12);
        helper.runAfterDelay(300, () -> {
            try {
                helper.assertTrue(guard.position().distanceTo(guardStart) > 2, "the patrolling guard did not move: at " + guard.position() + " start " + guardStart + " activity " + guard.activity() + " follow " + guard.followTarget() + " approach " + guard.approachTarget() + " alive " + guard.isAlive() + " sleeping " + guard.isSleeping() + " stage " + m.state(player).stage());
                helper.assertTrue(elara.distanceTo(player) < 6, "Elara did not keep up with Steve (" + elara.distanceTo(player) + " blocks)");
                helper.assertTrue(!elara.isSleeping() && elara.isAlive(), "Elara is not active");
                helper.succeed();
            } finally {
                NpcDirector.releaseAll(player);
                StoryManager.setInstanceForTesting(previous);
                level.getServer().getPlayerList().remove(player);
                unstage(level);
            }
        });
    }

    private static void teleport(ServerPlayer p, ServerLevel level, int x, int z) {
        BlockPos b = rel(x, 0, z);
        p.teleportTo(level, b.getX() + 0.5, b.getY(), b.getZ() + 0.5, 180, 0);
    }

    private static void tickUntil(StoryManager m, ServerPlayer p, java.util.function.BooleanSupplier done, int maxTicks) {
        for (int i = 0; i < maxTicks && !done.getAsBoolean(); i++) m.tick(p);
    }

    private static void expectStage(GameTestHelper helper, StoryManager m, ServerPlayer p, String stage) {
        String actual = m.state(p).stage();
        helper.assertTrue(stage.equals(actual), "expected stage " + stage + " but was " + actual);
    }

    private static void talk(StoryManager m, ServerPlayer p, String dialogue) {
        m.startDialogue(p, dialogue, null);
        finishDialogues(m, p);
    }

    /** Reads through every queued conversation, always taking the first reply. */
    private static void finishDialogues(StoryManager m, ServerPlayer p) {
        for (int guard = 0; guard < 200 && m.inDialogue(p); guard++) {
            DialogueSession s = m.dialogue(p);
            if (s.awaitingChoice()) m.onChoice(p, m.dialogueSession(p), 0);
            else m.onSkip(p, m.dialogueSession(p));
        }
    }
}
