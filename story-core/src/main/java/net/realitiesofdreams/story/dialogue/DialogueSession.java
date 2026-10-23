package net.realitiesofdreams.story.dialogue;

import net.realitiesofdreams.story.action.ActionDef;

import java.util.ArrayList;
import java.util.List;

/** Runtime walk through one {@link DialogueDef}. Returns the actions each step fires. */
public final class DialogueSession {
    private final DialogueDef def;
    private String current;
    private boolean finished;

    public DialogueSession(DialogueDef def) {
        this.def = def;
    }

    public DialogueDef def() {
        return def;
    }

    /** Enters the first line; returns its actions. */
    public List<ActionDef> start() {
        current = def.start();
        return new ArrayList<>(def.node(current).actions());
    }

    public DialogueDef.Node current() {
        return finished ? null : def.node(current);
    }

    public boolean finished() {
        return finished;
    }

    public boolean awaitingChoice() {
        return !finished && current().hasChoices();
    }

    /** Moves past a line without choices. Does nothing while a choice is pending. */
    public List<ActionDef> advance() {
        if (finished || awaitingChoice()) return List.of();
        return goTo(current().next(), List.of());
    }

    public List<ActionDef> choose(int index) {
        if (!awaitingChoice()) return List.of();
        List<DialogueDef.Choice> choices = current().choices();
        if (index < 0 || index >= choices.size()) return List.of();
        DialogueDef.Choice c = choices.get(index);
        return goTo(c.next(), c.actions());
    }

    private List<ActionDef> goTo(String next, List<ActionDef> carried) {
        List<ActionDef> out = new ArrayList<>(carried);
        if (next == null) {
            finished = true;
        } else {
            current = next;
            out.addAll(def.node(current).actions());
        }
        return out;
    }
}
