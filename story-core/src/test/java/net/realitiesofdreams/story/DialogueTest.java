package net.realitiesofdreams.story;

import net.realitiesofdreams.story.action.ActionDef;
import net.realitiesofdreams.story.dialogue.DialogueSession;
import net.realitiesofdreams.story.dialogue.LineTiming;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DialogueTest {
    final StoryContent content = StoryContent.loadFromClasspath(DialogueTest.class);

    @Test
    void choiceBranchesRejoinAndFireActions() {
        DialogueSession s = new DialogueSession(content.dialogue("rowan_greeting"));
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
        DialogueSession s = new DialogueSession(content.dialogue("elara_meet"));
        s.start();
        s.advance();
        List<ActionDef> actions = s.choose(0);
        assertEquals("npc_follow", actions.get(0).type());
        assertEquals("elara", actions.get(0).arg("character"));
    }

    @Test
    void invalidChoiceIsIgnored() {
        DialogueSession s = new DialogueSession(content.dialogue("elara_meet"));
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
