package net.realitiesofdreams.gametest;

import net.minecraft.core.BlockPos;
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
import net.realitiesofdreams.network.ObjectivePayload;
import net.realitiesofdreams.registry.ModAttachments;
import net.realitiesofdreams.registry.ModEntities;
import net.realitiesofdreams.server.NpcDirector;
import net.realitiesofdreams.server.StoryManager;
import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.engine.StoryEvent;
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
        helper.assertTrue(Story.content().characters().size() >= 96, "character registry incomplete");
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

    @GameTest(template = "empty")
    public static void mc001UniverseExists(GameTestHelper helper) {
        ServerLevel mc001 = helper.getLevel().getServer().getLevel(ModDimensions.MC_001);
        helper.assertTrue(mc001 != null, "MC-001 dimension missing");
        helper.assertTrue(mc001.getBlockState(new BlockPos(200, 63, 200)).is(Blocks.GRASS_BLOCK), "MC-001 ground is not grass at y=63");
        helper.assertTrue(mc001.getBlockState(new BlockPos(200, 64, 200)).isAir(), "MC-001 is not open above ground");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void scene001VillageBuilds(GameTestHelper helper) {
        SceneDef scene = Story.content().scene("mc001_s001");
        ServerLevel level = ModDimensions.level(helper.getLevel().getServer(), scene);
        SceneWorld.ensureBuilt(level, scene);
        for (SceneDef.ClueDef clue : scene.clues().values()) {
            BlockPos pos = ModDimensions.absoluteBlock(scene, clue.pos());
            String expected = clue.block().substring(clue.block().indexOf(':') + 1);
            helper.assertTrue(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).getPath().equals(expected),
                    "clue " + clue.id() + " block missing at " + pos);
        }
        BlockPos arrival = ModDimensions.absoluteBlock(scene, new double[] {0, 0, 26});
        helper.assertTrue(level.getBlockState(arrival).isAir() && level.getBlockState(arrival.above()).isAir(), "arrival blocked");
        helper.assertTrue(!level.getBlockState(arrival.below()).isAir(), "arrival has no floor");

        SceneWorld.applyChange(level, scene, SceneWorld.RIFT_SCAR);
        helper.assertTrue(level.getBlockState(ModDimensions.absoluteBlock(scene, new double[] {2, 0, -48})).is(Blocks.CRYING_OBSIDIAN), "rift scar not applied");

        PlayerStoryState state = new PlayerStoryState();
        state.setScene(scene.id(), "find_rowan");
        NpcDirector.reconcile(level, scene, state);
        List<StoryNpcEntity> npcs = level.getEntitiesOfClass(StoryNpcEntity.class, new net.minecraft.world.phys.AABB(ModDimensions.anchor(scene)).inflate(64));
        helper.assertTrue(npcs.stream().anyMatch(n -> n.characterId().equals("elder_rowan")), "Elder Rowan not placed");
        helper.assertTrue(npcs.stream().noneMatch(n -> n.characterId().equals("captain_maren")), "Captain Maren appeared too early");
        int before = npcs.size();
        NpcDirector.reconcile(level, scene, state);
        helper.assertTrue(level.getEntitiesOfClass(StoryNpcEntity.class, new net.minecraft.world.phys.AABB(ModDimensions.anchor(scene)).inflate(64)).size() == before,
                "reconcile duplicated NPCs");
        helper.succeed();
    }

    /** Plays Scene 001 start to finish through the real StoryManager, then checks the save data. */
    @GameTest(template = "empty", timeoutTicks = 400)
    public static void scene001PlaysThroughAndSaves(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ModAttachments.story(player).setIntroOffered(true);
        List<CustomPacketPayload> sent = new ArrayList<>();
        StoryManager m = new StoryManager((p, payload) -> sent.add(payload));

        m.startScene(player, "mc001_s001", false);
        helper.assertTrue(sent.stream().anyMatch(p -> p instanceof CinematicPayload c && c.id().equals("mc001_s001_opening")), "opening cinematic not sent");
        m.onCinematicDone(player, "mc001_s001_opening");
        expectStage(helper, m, player, "find_rowan");
        helper.assertTrue(sent.stream().anyMatch(p -> p instanceof ObjectivePayload o && o.text().contains("Elder Rowan") && o.hasTarget()), "objective not sent");

        talk(m, player, "rowan_greeting");
        expectStage(helper, m, player, "meet_elara");
        talk(m, player, "elara_meet");
        expectStage(helper, m, player, "to_shrine");

        m.handleEvent(player, new StoryEvent.Reached("shrine"));
        expectStage(helper, m, player, "tremor");
        m.onCinematicDone(player, "mc001_s001_tremor");
        expectStage(helper, m, player, "examine");

        for (String clue : List.of("rift_stone", "journal_page", "footprints")) {
            m.onClue(player, clue);
            finishDialogues(m, player);
        }
        expectStage(helper, m, player, "report");
        boolean hasPage = false;
        for (ItemStack s : player.getInventory().items) hasPage |= s.is(Items.PAPER) && s.getHoverName().getString().equals("Torn Journal Page");
        helper.assertTrue(hasPage, "journal page not given");

        talk(m, player, "rowan_report");
        expectStage(helper, m, player, "closing");
        helper.assertTrue(sent.stream().anyMatch(p -> p instanceof DialogueLinePayload l && l.speakerId().equals("captain_maren")), "Captain Maren never spoke");
        m.onCinematicDone(player, "mc001_s001_closing");

        PlayerStoryState state = ModAttachments.story(player);
        helper.assertTrue(state.isSceneComplete("mc001_s001") && state.activeScene() == null, "scene not completed");

        // Save and load the attachment the way the game does when the player logs out and back in.
        CompoundTag saved = player.getData(ModAttachments.STORY).serializeNBT(helper.getLevel().registryAccess());
        ModAttachments.StoryData reloaded = new ModAttachments.StoryData();
        reloaded.deserializeNBT(helper.getLevel().registryAccess(), saved);
        helper.assertTrue(reloaded.state().isSceneComplete("mc001_s001") && reloaded.state().hasFlag("mc001.s001.complete"), "progress lost on save/load");

        helper.getLevel().getServer().getPlayerList().remove(player);
        helper.succeed();
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
