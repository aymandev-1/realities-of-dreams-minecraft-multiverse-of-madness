package net.realitiesofdreams.story.dialogue;

import com.google.gson.JsonObject;
import net.realitiesofdreams.story.action.ActionDef;
import net.realitiesofdreams.story.json.Json;
import net.realitiesofdreams.story.json.StoryFormatException;

import java.util.List;
import java.util.Map;

/**
 * A conversation: a graph of lines. A line without choices flows to {@code next} (or ends);
 * a line with choices waits for the player.
 */
public record DialogueDef(String id, String start, boolean critical, Map<String, Node> nodes) {

    public record Choice(String text, String next, List<ActionDef> actions) {
        static Choice fromJson(JsonObject o) {
            return new Choice(Json.req(o, "text", "choice"), Json.str(o, "next", null), ActionDef.listFrom(o, "actions"));
        }
    }

    public record Node(String id, String speaker, String text, String next, List<Choice> choices,
                       List<ActionDef> actions, int durationTicks) {
        static Node fromJson(String id, JsonObject o) {
            return new Node(id, Json.req(o, "speaker", "line " + id), Json.req(o, "text", "line " + id),
                    Json.str(o, "next", null), Json.list(o, "choices", Choice::fromJson),
                    ActionDef.listFrom(o, "actions"), Json.integer(o, "duration", 0));
        }

        public boolean hasChoices() {
            return !choices.isEmpty();
        }

        public int displayTicks() {
            return durationTicks > 0 ? durationTicks : LineTiming.ticksFor(speaker, text);
        }
    }

    public static DialogueDef fromJson(String id, JsonObject o) {
        Map<String, Node> nodes = Json.map(o, "lines", Node::fromJson);
        if (nodes.isEmpty()) throw new StoryFormatException("dialogue " + id + " has no lines");
        String start = Json.str(o, "start", nodes.keySet().iterator().next());
        return new DialogueDef(id, start, Json.bool(o, "critical", false), nodes);
    }

    public Node node(String nodeId) {
        Node n = nodes.get(nodeId);
        if (n == null) throw new StoryFormatException("dialogue " + id + ": unknown line " + nodeId);
        return n;
    }
}
