package net.realitiesofdreams.server;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.phys.Vec3;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.RodConfig;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.entity.StoryNpcEntity;
import net.realitiesofdreams.network.CinematicPayload;
import net.realitiesofdreams.network.DialogueEndPayload;
import net.realitiesofdreams.network.DialogueLinePayload;
import net.realitiesofdreams.network.HintPayload;
import net.realitiesofdreams.network.ShakePayload;
import net.realitiesofdreams.network.ObjectivePayload;
import net.realitiesofdreams.registry.ModAttachments;
import net.realitiesofdreams.story.StoryContent;
import net.realitiesofdreams.story.action.ActionDef;
import net.realitiesofdreams.story.cinematic.CinematicDef;
import net.realitiesofdreams.story.dialogue.DialogueDef;
import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.engine.CompanionChatter;
import net.realitiesofdreams.story.engine.Effect;
import net.realitiesofdreams.story.engine.ProximityDirector;
import net.realitiesofdreams.story.engine.SceneEngine;
import net.realitiesofdreams.story.engine.StoryEvent;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.state.PlayerStoryState;
import net.realitiesofdreams.world.ModDimensions;
import net.realitiesofdreams.world.SceneWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Runs the story for every online player: starts scenes, plays dialogue line by line, lets
 * characters walk up and talk, runs cinematics, tracks objectives and saves progress.
 */
public final class StoryManager {
    public static final String FIRST_SCENE = "mc001_s001";
    private static final double TALK_CANCEL_DISTANCE = 16;
    private static StoryManager instance = new StoryManager(StoryMessenger.NETWORK);

    private final StoryMessenger messenger;
    private final Map<UUID, PlayerRuntime> runtimes = new HashMap<>();
    private final Random random = new Random();

    public StoryManager(StoryMessenger messenger) {
        this.messenger = messenger;
    }

    public static StoryManager get() {
        return instance;
    }

    private SceneEngine engine() {
        return Story.engine();
    }

    PlayerRuntime runtime(ServerPlayer p) {
        return runtimes.computeIfAbsent(p.getUUID(), k -> new PlayerRuntime());
    }

    public PlayerStoryState state(ServerPlayer p) {
        return ModAttachments.story(p);
    }

    public boolean inDialogue(ServerPlayer p) {
        return runtime(p).dialogue != null;
    }

    public int dialogueSession(ServerPlayer p) {
        return runtime(p).session;
    }

    @Nullable
    public DialogueSession dialogue(ServerPlayer p) {
        return runtime(p).dialogue;
    }

    @Nullable
    public String cinematic(ServerPlayer p) {
        return runtime(p).cinematicId;
    }

    // ---------------------------------------------------------------- lifecycle

    public void onLogin(ServerPlayer p) {
        PlayerRuntime rt = runtime(p);
        PlayerStoryState state = state(p);
        SceneDef scene = engine().activeScene(state);
        if (scene != null) {
            ServerLevel level = ModDimensions.level(p.server, scene);
            if (level != null) {
                SceneWorld.ensureBuilt(level, scene);
                NpcDirector.reconcile(level, scene, state);
            }
            perform(p, engine().resume(state));
        } else if (!state.introOffered() && RodConfig.autoStart()) {
            rt.autoStartTicks = 60;
        }
    }

    public void onLogout(ServerPlayer p) {
        NpcDirector.releaseAll(p);
        runtimes.remove(p.getUUID());
    }

    /** Starts (or resumes) the story and takes the player to the scene. */
    public void play(ServerPlayer p) {
        PlayerStoryState state = state(p);
        state.setIntroOffered(true);
        SceneDef active = engine().activeScene(state);
        if (active != null) {
            teleportToCheckpoint(p, active, state);
            perform(p, engine().resume(state));
            return;
        }
        if (state.isSceneComplete(FIRST_SCENE)) {
            p.sendSystemMessage(Component.literal("Scene 001 is complete. Scene 002 arrives in a future update. Use /rod scene restart to replay.").withStyle(ChatFormatting.GOLD));
            return;
        }
        startScene(p, FIRST_SCENE, true);
    }

    public void startScene(ServerPlayer p, String sceneId, boolean teleport) {
        SceneDef scene = Story.content().scene(sceneId);
        PlayerStoryState state = state(p);
        state.setIntroOffered(true);
        ServerLevel level = ModDimensions.level(p.server, scene);
        if (level != null) SceneWorld.ensureBuilt(level, scene);
        if (teleport && level != null) {
            double[] a = ModDimensions.absolute(scene, scene.arrival());
            p.teleportTo(level, a[0], a[1], a[2], scene.arrivalYaw(), 0);
        }
        List<Effect> effects = engine().startScene(state, sceneId);
        if (level != null) NpcDirector.arrange(level, scene, state);
        runtime(p).lastStage = state.stage();
        perform(p, effects);
    }

    public void restartScene(ServerPlayer p, String sceneId) {
        PlayerRuntime rt = runtime(p);
        stopPresentation(p, rt);
        NpcDirector.releaseAll(p);
        PlayerStoryState state = state(p);
        state.resetScene(sceneId, List.of(sceneId.replace("_s", ".s") + ".", SceneEngine.FOLLOW_FLAG));
        startScene(p, sceneId, true);
    }

    private void stopPresentation(ServerPlayer p, PlayerRuntime rt) {
        if (rt.dialogue != null) {
            clearSpeaker(p, rt);
            messenger.send(p, new DialogueEndPayload(rt.session));
        }
        rt.dialogue = null;
        rt.dialogueId = null;
        if (rt.cinematicId != null) p.removeEffect(MobEffects.INVISIBILITY);
        rt.cinematicId = null;
        rt.presentation.clear();
        rt.approachTrigger = null;
    }

    public void teleportToCheckpoint(ServerPlayer p, SceneDef scene, PlayerStoryState state) {
        ServerLevel level = ModDimensions.level(p.server, scene);
        if (level == null) return;
        SceneDef.Location loc = scene.arrival();
        int idx = scene.stageIndex(state.stage());
        for (int i = Math.max(0, idx); i >= 0; i--) {
            String cp = scene.stages().get(i).checkpoint();
            if (cp != null) {
                loc = scene.location(cp);
                break;
            }
        }
        double[] a = ModDimensions.absolute(scene, loc);
        p.teleportTo(level, a[0], a[1], a[2], p.getYRot(), 0);
    }

    // ---------------------------------------------------------------- per tick

    public void tick(ServerPlayer p) {
        PlayerRuntime rt = runtime(p);
        rt.tick++;
        if (rt.autoStartTicks > 0 && --rt.autoStartTicks == 0) {
            rt.autoStartTicks = -1;
            if (!state(p).introOffered()) play(p);
        }
        tickDialogue(p, rt);
        if (rt.cinematicId != null) tickCinematicCues(p, rt);
        if (rt.cinematicId != null && --rt.cinematicTicksLeft <= 0) {
            RealitiesOfDreams.LOGGER.warn("Cinematic {} timed out for {}", rt.cinematicId, p.getGameProfile().getName());
            finishCinematic(p, rt.cinematicId);
        }
        if (!rt.busy()) drainPresentation(p, rt);

        PlayerStoryState state = state(p);
        SceneDef scene = engine().activeScene(state);
        if (scene == null || !p.level().dimension().equals(ModDimensions.forScene(scene))) return;

        Vec3 pos = p.position();
        boolean moving = rt.lastPos != null && pos.distanceToSqr(rt.lastPos) > 0.004;
        rt.idleTicks = moving ? 0 : rt.idleTicks + 1;
        rt.lastPos = pos;
        if (rt.chatterCooldown > 0) rt.chatterCooldown--;

        if (rt.tick % 5 == 0 && !rt.busy()) tickProximity(p, rt, scene, state);
        if (rt.tick % 10 == 0 && !rt.busy()) tickReach(p, scene, state);
        if (rt.tick % 10 == 5 && rt.cinematicId == null) {
            perform(p, engine().checkAreas(state, loc -> loc != null && distance(p, scene, loc) <= loc.radius()));
        }
        if (rt.tick % 20 == 0) {
            if (!rt.busy()) tickChatter(p, rt, scene, state, moving);
            highlightClues(p, scene, state);
        }
    }

    private void tickProximity(ServerPlayer p, PlayerRuntime rt, SceneDef scene, PlayerStoryState state) {
        ServerLevel level = p.serverLevel();
        if (rt.approachTrigger != null) {
            SceneDef.TriggerDef trigger = scene.triggers().stream().filter(t -> t.id().equals(rt.approachTrigger)).findFirst().orElse(null);
            StoryNpcEntity npc = rt.approachNpc == null ? null : (level.getEntity(rt.approachNpc) instanceof StoryNpcEntity n ? n : null);
            rt.approachTicks--;
            if (trigger == null || npc == null || !ProximityDirector.eligible(trigger, state) || npc.distanceTo(p) > trigger.radius() + 8) {
                if (npc != null) npc.setApproachTarget(null);
                rt.approachTrigger = null;
                return;
            }
            if (npc.distanceTo(p) <= trigger.talkRadius() + 0.5 || rt.approachTicks <= 0) {
                npc.setApproachTarget(null);
                rt.approachTrigger = null;
                fireTrigger(p, trigger, npc);
            }
            return;
        }
        List<StoryNpcEntity> npcs = level.getEntitiesOfClass(StoryNpcEntity.class, p.getBoundingBox().inflate(16));
        List<ProximityDirector.NearbyCharacter> nearby = new ArrayList<>();
        for (StoryNpcEntity n : npcs) nearby.add(new ProximityDirector.NearbyCharacter(n.characterId(), n.distanceTo(p), n.hasLineOfSight(p)));
        Optional<ProximityDirector.Decision> decision = ProximityDirector.choose(scene, state, nearby);
        if (decision.isEmpty()) return;
        ProximityDirector.Decision d = decision.get();
        StoryNpcEntity npc = npcs.stream()
                .filter(n -> n.characterId().equals(d.npc().characterId()))
                .min((a, b) -> Double.compare(a.distanceTo(p), b.distanceTo(p))).orElse(null);
        if (npc == null) return;
        if (d.approach()) {
            rt.approachTrigger = d.trigger().id();
            rt.approachNpc = npc.getUUID();
            rt.approachTicks = 40; // checked every 5 ticks: about ten seconds
            npc.setApproachTarget(p.getUUID());
        } else {
            fireTrigger(p, d.trigger(), npc);
        }
    }

    private void fireTrigger(ServerPlayer p, SceneDef.TriggerDef trigger, StoryNpcEntity npc) {
        state(p).markTriggerFired(trigger.id());
        startDialogue(p, trigger.dialogue(), npc);
    }

    private void tickReach(ServerPlayer p, SceneDef scene, PlayerStoryState state) {
        SceneDef.StageDef stage = engine().activeStage(state);
        if (stage == null || stage.completion() != SceneDef.CompletionType.REACH) return;
        SceneDef.Location loc = scene.location(stage.completionValue());
        if (loc != null && distance(p, scene, loc) <= loc.radius()) {
            perform(p, engine().handle(state, new StoryEvent.Reached(stage.completionValue())));
        }
    }

    private void tickChatter(ServerPlayer p, PlayerRuntime rt, SceneDef scene, PlayerStoryState state, boolean moving) {
        SceneDef.StageDef stage = engine().activeStage(state);
        if (stage == null || !stage.chatter() || rt.chatterCooldown > 0) return;
        Set<String> present = companionsNear(p);
        if (present.isEmpty()) return;
        Optional<SceneDef.ChatterDef> line = Optional.empty();
        for (Map.Entry<String, SceneDef.Location> e : scene.locations().entrySet()) {
            if (distance(p, scene, e.getValue()) <= e.getValue().radius() * 1.5) {
                line = CompanionChatter.pick(scene, state, "near:" + e.getKey(), present, random);
                if (line.isPresent()) break;
            }
        }
        if (line.isEmpty() && moving) line = CompanionChatter.pick(scene, state, "moving", present, random);
        if (line.isEmpty() && rt.idleTicks > 200) line = CompanionChatter.pick(scene, state, "idle", present, random);
        line.ifPresent(c -> {
            ambient(p, c.character(), c.text());
            rt.chatterCooldown = RodConfig.chatterCooldownTicks();
        });
    }

    /** A companion reacts when the player is hurt in a fight. */
    public void onPlayerHurt(ServerPlayer p) {
        PlayerStoryState state = state(p);
        SceneDef scene = engine().activeScene(state);
        if (scene == null || runtime(p).busy()) return;
        CompanionChatter.pick(scene, state, "combat", companionsNear(p), random).ifPresent(c -> ambient(p, c.character(), c.text()));
    }

    private Set<String> companionsNear(ServerPlayer p) {
        Set<String> out = new HashSet<>();
        for (StoryNpcEntity n : p.serverLevel().getEntitiesOfClass(StoryNpcEntity.class, p.getBoundingBox().inflate(12))) {
            if (p.getUUID().equals(n.followTarget())) out.add(n.characterId());
        }
        return out;
    }

    private void highlightClues(ServerPlayer p, SceneDef scene, PlayerStoryState state) {
        SceneDef.StageDef stage = engine().activeStage(state);
        if (stage == null || stage.completion() != SceneDef.CompletionType.CLUES) return;
        for (String id : SceneEngine.stageClues(stage)) {
            if (state.cluesFound().contains(id)) continue;
            BlockPos pos = ModDimensions.absoluteBlock(scene, scene.clues().get(id).pos());
            p.serverLevel().sendParticles(p, ParticleTypes.END_ROD, false, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 3, 0.25, 0.3, 0.25, 0.01);
        }
    }

    private static double distance(ServerPlayer p, SceneDef scene, SceneDef.Location loc) {
        double[] a = ModDimensions.absolute(scene, loc);
        return p.position().distanceTo(new Vec3(a[0], a[1], a[2]));
    }

    // ---------------------------------------------------------------- world interaction

    /** Right-clicking a clue block. Returns true if it was a clue (and the click is consumed). */
    public boolean onBlockUse(ServerPlayer p, BlockPos clicked) {
        PlayerStoryState state = state(p);
        SceneDef scene = engine().activeScene(state);
        SceneDef.StageDef stage = engine().activeStage(state);
        if (scene == null || stage == null || stage.completion() != SceneDef.CompletionType.CLUES) return false;
        if (!p.level().dimension().equals(ModDimensions.forScene(scene))) return false;
        for (String id : SceneEngine.stageClues(stage)) {
            BlockPos pos = ModDimensions.absoluteBlock(scene, scene.clues().get(id).pos());
            if (pos.distSqr(clicked) <= 2.25) {
                if (!state.cluesFound().contains(id) && !runtime(p).busy()) onClue(p, id);
                return true;
            }
        }
        return false;
    }

    public void onClue(ServerPlayer p, String clueId) {
        perform(p, engine().handle(state(p), new StoryEvent.ClueExamined(clueId)));
    }

    /** Right-clicking a character also starts whatever they have to say. */
    public void onNpcInteract(ServerPlayer p, StoryNpcEntity npc) {
        PlayerRuntime rt = runtime(p);
        if (rt.busy()) return;
        PlayerStoryState state = state(p);
        SceneDef scene = engine().activeScene(state);
        Optional<ProximityDirector.Decision> d = ProximityDirector.choose(scene, state,
                List.of(new ProximityDirector.NearbyCharacter(npc.characterId(), 0, true)));
        if (d.isPresent()) {
            fireTrigger(p, d.get().trigger(), npc);
        } else {
            ambient(p, npc.characterId(), idleLine(npc.characterId()));
        }
    }

    private static String idleLine(String character) {
        String[] lines = {"Stay safe out there.", "Hm? Oh, I was miles away.", "Strange times, friend.", "The Elder will know what to do."};
        return lines[Math.floorMod(character.hashCode(), lines.length)];
    }

    public void handleEvent(ServerPlayer p, StoryEvent event) {
        perform(p, engine().handle(state(p), event));
    }

    // ---------------------------------------------------------------- dialogue

    public void startDialogue(ServerPlayer p, String dialogueId, @Nullable StoryNpcEntity npc) {
        PlayerRuntime rt = runtime(p);
        DialogueDef def = Story.content().dialogue(dialogueId);
        if (def == null) {
            RealitiesOfDreams.LOGGER.error("Unknown dialogue {}", dialogueId);
            return;
        }
        if (rt.busy()) {
            rt.presentation.add(new Effect.StartDialogue(dialogueId));
            return;
        }
        rt.dialogue = new DialogueSession(def);
        rt.dialogueId = dialogueId;
        rt.session++;
        List<ActionDef> actions = rt.dialogue.start();
        showLine(p, rt, npc);
        applyDialogueActions(p, actions);
    }

    private void showLine(ServerPlayer p, PlayerRuntime rt, @Nullable StoryNpcEntity preferred) {
        DialogueDef.Node node = rt.dialogue.current();
        clearSpeaker(p, rt);
        StoryNpcEntity speaker = preferred != null && preferred.characterId().equals(node.speaker()) ? preferred : findNpc(p, node.speaker());
        if (speaker != null) {
            speaker.setTalkTarget(p.getUUID());
            rt.speakingNpc = speaker.getUUID();
        }
        List<String> choices = node.choices().stream().map(DialogueDef.Choice::text).toList();
        rt.lineTicksLeft = node.hasChoices() ? -1 : node.displayTicks();
        messenger.send(p, new DialogueLinePayload(rt.session, node.speaker(), Story.content().characters().displayName(node.speaker()),
                node.text(), node.displayTicks(), choices, false));
    }

    private void tickDialogue(ServerPlayer p, PlayerRuntime rt) {
        if (rt.dialogue == null) return;
        if (!rt.dialogue.def().critical() && rt.speakingNpc != null && p.level() instanceof ServerLevel level
                && level.getEntity(rt.speakingNpc) instanceof StoryNpcEntity npc && npc.distanceTo(p) > TALK_CANCEL_DISTANCE) {
            // The player walked off mid-greeting: the character simply stops talking.
            endDialogue(p, rt);
            return;
        }
        if (rt.lineTicksLeft > 0 && --rt.lineTicksLeft == 0) advance(p, rt);
    }

    public void onSkip(ServerPlayer p, int session) {
        PlayerRuntime rt = runtime(p);
        if (rt.dialogue != null && rt.session == session && !rt.dialogue.awaitingChoice()) advance(p, rt);
    }

    public void onChoice(ServerPlayer p, int session, int choice) {
        PlayerRuntime rt = runtime(p);
        if (rt.dialogue == null || rt.session != session || !rt.dialogue.awaitingChoice()) return;
        List<ActionDef> actions = rt.dialogue.choose(choice);
        if (rt.dialogue.awaitingChoice()) return; // out-of-range choice
        continueDialogue(p, rt, actions);
    }

    private void advance(ServerPlayer p, PlayerRuntime rt) {
        continueDialogue(p, rt, rt.dialogue.advance());
    }

    private void continueDialogue(ServerPlayer p, PlayerRuntime rt, List<ActionDef> actions) {
        if (rt.dialogue.finished()) {
            applyDialogueActions(p, actions);
            endDialogue(p, rt);
        } else {
            showLine(p, rt, null);
            applyDialogueActions(p, actions);
        }
    }

    private void applyDialogueActions(ServerPlayer p, List<ActionDef> actions) {
        if (!actions.isEmpty()) perform(p, engine().applyActions(state(p), actions));
    }

    private void endDialogue(ServerPlayer p, PlayerRuntime rt) {
        String id = rt.dialogueId;
        clearSpeaker(p, rt);
        messenger.send(p, new DialogueEndPayload(rt.session));
        rt.dialogue = null;
        rt.dialogueId = null;
        rt.chatterCooldown = Math.max(rt.chatterCooldown, 160);
        if (id != null) perform(p, engine().handle(state(p), new StoryEvent.DialogueFinished(id)));
        drainPresentation(p, rt);
    }

    private void clearSpeaker(ServerPlayer p, PlayerRuntime rt) {
        if (rt.speakingNpc != null && p.level() instanceof ServerLevel level && level.getEntity(rt.speakingNpc) instanceof StoryNpcEntity npc) {
            npc.setTalkTarget(null);
        }
        rt.speakingNpc = null;
    }

    /** A line said in passing; never blocks the player or the story. */
    public void ambient(ServerPlayer p, String character, String text) {
        messenger.send(p, new DialogueLinePayload(-1, character, Story.content().characters().displayName(character), text,
                net.realitiesofdreams.story.dialogue.LineTiming.ticksFor(text), List.of(), true));
    }

    @Nullable
    private StoryNpcEntity findNpc(ServerPlayer p, String character) {
        return p.serverLevel().getEntitiesOfClass(StoryNpcEntity.class, p.getBoundingBox().inflate(24),
                        n -> n.characterId().equals(character))
                .stream().min((a, b) -> Double.compare(a.distanceTo(p), b.distanceTo(p))).orElse(null);
    }

    // ---------------------------------------------------------------- cinematics

    private void startCinematic(ServerPlayer p, PlayerRuntime rt, String id) {
        CinematicDef def = Story.content().cinematic(id);
        SceneDef scene = engine().activeScene(state(p));
        if (def == null || scene == null) {
            perform(p, engine().handle(state(p), new StoryEvent.CinematicFinished(id)));
            return;
        }
        rt.cinematicId = id;
        rt.cinematicTicksLeft = def.duration() + 100;
        rt.cinematicTick = -1;
        if (def.hidePlayer()) p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, def.duration() + 120, 0, false, false));
        int[] a = scene.anchor();
        messenger.send(p, new CinematicPayload(id, a[0], a[1], a[2]));
    }

    public void onCinematicDone(ServerPlayer p, String id) {
        if (id.equals(runtime(p).cinematicId)) finishCinematic(p, id);
    }

    /** Runs the cinematic's timed story actions (an actor waking, a character walking past). */
    private void tickCinematicCues(ServerPlayer p, PlayerRuntime rt) {
        CinematicDef def = Story.content().cinematic(rt.cinematicId);
        if (def == null) return;
        int prev = rt.cinematicTick++;
        runCues(p, def, prev, rt.cinematicTick);
    }

    private void runCues(ServerPlayer p, CinematicDef def, double from, double to) {
        for (CinematicDef.Cue cue : new net.realitiesofdreams.story.cinematic.CinematicTimeline(def).cuesBetween(from, to)) {
            perform(p, engine().applyActions(state(p), cue.actions()));
        }
    }

    private void finishCinematic(ServerPlayer p, String id) {
        PlayerRuntime rt = runtime(p);
        CinematicDef def = Story.content().cinematic(id);
        if (def != null) {
            // Skipped or cut short: the world still ends up where the cinematic would have left it.
            runCues(p, def, rt.cinematicTick, Double.MAX_VALUE);
            if (def.hidePlayer()) p.removeEffect(MobEffects.INVISIBILITY);
        }
        rt.cinematicId = null;
        perform(p, engine().handle(state(p), new StoryEvent.CinematicFinished(id)));
        drainPresentation(p, rt);
    }

    private void drainPresentation(ServerPlayer p, PlayerRuntime rt) {
        while (!rt.busy() && !rt.presentation.isEmpty()) {
            Effect e = rt.presentation.poll();
            if (e instanceof Effect.StartDialogue d) startDialogue(p, d.dialogueId(), null);
            else if (e instanceof Effect.StartCinematic c) startCinematic(p, rt, c.cinematicId());
        }
    }

    // ---------------------------------------------------------------- effects

    public void perform(ServerPlayer p, List<Effect> effects) {
        PlayerRuntime rt = runtime(p);
        PlayerStoryState state = state(p);
        for (Effect e : effects) {
            switch (e) {
                case Effect.ShowObjective o -> {
                    SceneDef scene = engine().activeScene(state);
                    double[] t = o.target() == null || scene == null ? null : ModDimensions.absolute(scene, o.target());
                    messenger.send(p, new ObjectivePayload(o.sceneLabel(), o.sceneTitle(), o.text(), t != null,
                            t == null ? 0 : t[0], t == null ? 0 : t[1], t == null ? 0 : t[2], o.progress() == null ? "" : o.progress()));
                }
                case Effect.ClearObjective c -> messenger.send(p, ObjectivePayload.clear());
                case Effect.StartDialogue d -> {
                    if (rt.busy()) rt.presentation.add(d);
                    else startDialogue(p, d.dialogueId(), null);
                }
                case Effect.StartCinematic c -> {
                    if (rt.busy()) rt.presentation.add(c);
                    else startCinematic(p, rt, c.cinematicId());
                }
                case Effect.NpcCommand n -> NpcDirector.command(p, engine().activeScene(state), state, n);
                case Effect.Hint h -> messenger.send(p, new HintPayload(h.text(), h.ticks()));
                case Effect.Shake sh -> messenger.send(p, new ShakePayload(sh.intensity(), sh.ticks()));
                case Effect.Say say -> ambient(p, say.character(), say.text());
                case Effect.TeleportPlayer tp -> {
                    SceneDef scene = engine().activeScene(state);
                    ServerLevel level = scene == null ? null : ModDimensions.level(p.server, scene);
                    if (level != null && p.level() == level) {
                        double[] a = ModDimensions.absolute(scene, scene.location(tp.location()));
                        p.teleportTo(level, a[0], a[1], a[2], tp.yaw(), 0);
                    }
                }
                case Effect.Journal j -> updateJournal(p, state, j.entryId());
                case Effect.Particles pa -> {
                    SceneDef scene = engine().activeScene(state);
                    if (scene != null && p.level() == ModDimensions.level(p.server, scene)
                            && BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(pa.particle())) instanceof SimpleParticleType type) {
                        double[] a = ModDimensions.absolute(scene, scene.location(pa.location()));
                        p.serverLevel().sendParticles(type, a[0], a[1], a[2], pa.count(), pa.spread(), pa.spread() * 0.5, pa.spread(), 0.02);
                    }
                }
                case Effect.ShowTitle t -> {
                    p.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 25));
                    p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(t.subtitle()).withStyle(ChatFormatting.GRAY)));
                    p.connection.send(new ClientboundSetTitleTextPacket(Component.literal(t.title()).withStyle(ChatFormatting.GOLD)));
                }
                case Effect.GiveItem g -> giveItem(p, g);
                case Effect.PlaySound s -> p.playNotifySound(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(s.sound())),
                        SoundSource.MASTER, s.volume(), s.pitch());
                case Effect.WorldChange w -> {
                    SceneDef scene = engine().activeScene(state);
                    ServerLevel level = scene == null ? null : ModDimensions.level(p.server, scene);
                    if (level != null && p.level() == level) SceneWorld.applyChange(level, scene, w.change());
                }
                case Effect.Message m -> p.sendSystemMessage(Component.literal(m.text()));
                case Effect.SceneCompleted s -> {
                    if (s.nextScene() != null && Story.content().scene(s.nextScene()) != null) {
                        // The story carries straight on: the next scene starts where the player stands.
                        rt.pendingScene = s.nextScene();
                    } else {
                        NpcDirector.releaseFollowers(p);
                        p.sendSystemMessage(Component.literal(s.nextScene() != null && s.nextScene().startsWith("unreleased:")
                                ? "Your progress is saved. The next scene arrives in a future update."
                                : "Scene complete. Progress saved.").withStyle(ChatFormatting.GOLD));
                    }
                }
                case Effect.Checkpoint c -> { /* checkpoints are derived from the saved stage */ }
            }
        }
        SceneDef scene = engine().activeScene(state);
        if (scene != null && !java.util.Objects.equals(rt.lastStage, state.stage())) {
            rt.lastStage = state.stage();
            ServerLevel level = ModDimensions.level(p.server, scene);
            if (level != null && p.level() == level) NpcDirector.reconcile(level, scene, state);
        }
        if (rt.pendingScene != null && state.activeScene() == null) {
            String next = rt.pendingScene;
            rt.pendingScene = null;
            startScene(p, next, false);
        }
    }

    /** Adds the entry to Steve's journal: a written book that updates itself as the story goes on. */
    private static void updateJournal(ServerPlayer p, PlayerStoryState state, String entryId) {
        StoryContent content = Story.content();
        List<Filterable<Component>> pages = new ArrayList<>();
        for (String id : state.journal()) {
            StoryContent.JournalEntry e = content.journal(id);
            if (e == null) continue;
            pages.add(Filterable.passThrough(Component.empty()
                    .append(Component.literal(e.title()).withStyle(Style.EMPTY.withBold(true)))
                    .append("\n\n").append(Component.literal(e.text()))));
        }
        WrittenBookContent book = new WrittenBookContent(Filterable.passThrough("Steve's Journal"), "Steve", 0, pages, true);
        ItemStack journal = null;
        for (ItemStack s : p.getInventory().items) {
            CustomData data = s.get(DataComponents.CUSTOM_DATA);
            if (s.is(Items.WRITTEN_BOOK) && data != null && data.copyTag().getBoolean("rod_journal")) journal = s;
        }
        if (journal == null) {
            journal = new ItemStack(Items.WRITTEN_BOOK);
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("rod_journal", true);
            journal.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            journal.set(DataComponents.WRITTEN_BOOK_CONTENT, book);
            if (!p.getInventory().add(journal)) p.drop(journal, false);
        } else {
            journal.set(DataComponents.WRITTEN_BOOK_CONTENT, book);
        }
        StoryContent.JournalEntry e = content.journal(entryId);
        p.sendSystemMessage(Component.literal("Journal updated: " + (e == null ? entryId : e.title())).withStyle(ChatFormatting.YELLOW));
    }

    private static void giveItem(ServerPlayer p, Effect.GiveItem g) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(g.item()));
        ItemStack stack = new ItemStack(item, g.count());
        if (g.name() != null) stack.set(DataComponents.CUSTOM_NAME, Component.literal(g.name()).withStyle(ChatFormatting.LIGHT_PURPLE));
        if (!g.lore().isEmpty()) {
            stack.set(DataComponents.LORE, new ItemLore(g.lore().stream()
                    .map(l -> (Component) Component.literal(l).withStyle(ChatFormatting.GRAY)).toList()));
        }
        Component name = stack.getHoverName();
        if (!p.getInventory().add(stack)) p.drop(stack, false);
        p.sendSystemMessage(Component.literal("Received: ").append(name));
    }

    /** Test hook. */
    public static void setInstanceForTesting(StoryManager manager) {
        instance = manager;
    }
}
