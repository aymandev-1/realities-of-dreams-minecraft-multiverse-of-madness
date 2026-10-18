package net.realitiesofdreams.server;

import net.minecraft.world.phys.Vec3;
import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.engine.Effect;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

/** Per-player state that only lives while the player is online. */
final class PlayerRuntime {
    // Dialogue
    @Nullable DialogueSession dialogue;
    @Nullable String dialogueId;
    @Nullable UUID speakingNpc;
    int session;
    int lineTicksLeft;

    // Cinematic
    @Nullable String cinematicId;
    int cinematicTicksLeft;

    /** Dialogues and cinematics wait their turn here, in story order. */
    final Deque<Effect> presentation = new ArrayDeque<>();

    // A character walking over to start a conversation.
    @Nullable String approachTrigger;
    @Nullable UUID approachNpc;
    int approachTicks;

    int autoStartTicks = -1;
    int chatterCooldown = 200;
    int idleTicks;
    @Nullable Vec3 lastPos;
    int tick;

    boolean busy() {
        return dialogue != null || cinematicId != null;
    }
}
