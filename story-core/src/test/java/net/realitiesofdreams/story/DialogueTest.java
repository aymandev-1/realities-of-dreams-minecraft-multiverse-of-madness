package net.realitiesofdreams.story;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.realitiesofdreams.story.action.ActionDef;
import net.realitiesofdreams.story.dialogue.DialogueDef;
import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.dialogue.LineTiming;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DialogueTest {
    /** Branching test dialogues (the engine supports choices; Scene 001 itself has none). */
    static final JsonObject DIALOGUES = JsonParser.parseString("""
            {"rowan_greeting": {"critical": true, "lines": {"a": {"speaker": "elder_rowan", "text": "There you are. I saw you coming up the south road.", "next": "b"}, "b": {"speaker": "elder_rowan", "text": "You felt it too, didn't you? The ground, just before dawn.", "choices": [{"text": "Felt what?", "next": "c1"}, {"text": "The tremor? I thought I'd dreamed it.", "next": "c2"}]}, "c1": {"speaker": "elder_rowan", "text": "A shiver through the earth, deep and old. Most slept through it. The animals didn't.", "next": "d"}, "c2": {"speaker": "elder_rowan", "text": "If it was a dream, half the village shared it. Bram swears his anvil sang.", "next": "d"}, "d": {"speaker": "elder_rowan", "text": "Tremors don't wander this valley without a reason.", "next": "e"}, "e": {"speaker": "elder_rowan", "text": "Elara keeps watch at the north gate. Go to her. She knows the old trail.", "actions": [{"type": "set_flag", "flag": "mc001.s001.met_rowan"}]}}}, "elara_meet": {"critical": true, "lines": {"a": {"speaker": "elara", "text": "The Elder sent you? Good. I've been staring at that hill since the shaking stopped.", "next": "b"}, "b": {"speaker": "elara", "text": "There's an old shrine up the trail. Nobody goes there anymore. But last night I'd swear I saw a light.", "choices": [{"text": "Let's go and look.", "next": "c1", "actions": [{"type": "npc_follow", "character": "elara"}]}, {"text": "What kind of light?", "next": "c2", "actions": [{"type": "npc_follow", "character": "elara"}]}]}, "c1": {"speaker": "elara", "text": "That's what I hoped you'd say. Stay close, the trail's overgrown."}, "c2": {"speaker": "elara", "text": "Purple. Like the edge of a dream, if dreams could burn. Come on, I'd rather see it with someone."}}}}
            """).getAsJsonObject();

    static DialogueDef dialogue(String id) {
        return DialogueDef.fromJson(id, DIALOGUES.getAsJsonObject(id));
    }

    @Test
    void choiceBranchesRejoinAndFireActions() {
        DialogueSession s = new DialogueSession(dialogue("rowan_greeting"));
        assertTrue(s.start().isEmpty());
        assertEquals("elder_rowan", s.current().speaker());
        s.advance();
        assertTrue(s.awaitingChoice());
        assertTrue(s.advance().isEmpty(), "advance must not skip a choice");
        s.choose(1);
        assertTrue(s.current().text().contains("Bram"));
        s.advance();
        List<ActionDef> actions = s.advance();
        assertEquals("set_flag", actions.get(0).type());
        s.advance();
        assertTrue(s.finished());
    }

    @Test
    void choiceActionsAreReturned() {
        DialogueSession s = new DialogueSession(dialogue("elara_meet"));
        s.start();
        s.advance();
        List<ActionDef> actions = s.choose(0);
        assertEquals("npc_follow", actions.get(0).type());
        assertEquals("elara", actions.get(0).arg("character"));
    }

    @Test
    void invalidChoiceIsIgnored() {
        DialogueSession s = new DialogueSession(dialogue("elara_meet"));
        s.start();
        s.advance();
        assertTrue(s.choose(7).isEmpty());
        assertTrue(s.awaitingChoice());
    }

    @Test
    void lineTimingIsReadable() {
        assertEquals(LineTiming.MIN_TICKS, LineTiming.ticksFor("Hi"));
        int longLine = LineTiming.ticksFor("x".repeat(120));
        assertTrue(longLine > 150 && longLine <= LineTiming.MAX_TICKS);
    }
}
