package net.realitiesofdreams.client;

import net.minecraft.client.Minecraft;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.network.CinematicPayload;
import net.realitiesofdreams.network.DialogueEndPayload;
import net.realitiesofdreams.network.DialogueLinePayload;
import net.realitiesofdreams.network.HintPayload;
import net.realitiesofdreams.network.ObjectivePayload;
import net.realitiesofdreams.network.ShakePayload;
import net.realitiesofdreams.story.cinematic.CinematicDef;

/** Client side of the story packets. Only referenced from lambdas so servers never load it. */
public final class ClientPayloadHandler {
    private ClientPayloadHandler() {}

    public static void line(DialogueLinePayload p) {
        if (p.ambient()) {
            ClientStory.addAmbient(p);
            return;
        }
        ClientStory.setLine(p);
        Minecraft mc = Minecraft.getInstance();
        if (!p.choices().isEmpty()) {
            mc.setScreen(new DialogueChoiceScreen(p));
        } else if (mc.screen instanceof DialogueChoiceScreen) {
            mc.setScreen(null);
        }
    }

    public static void end(DialogueEndPayload p) {
        if (ClientStory.line != null && ClientStory.line.session() == p.session()) ClientStory.line = null;
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof DialogueChoiceScreen) mc.setScreen(null);
    }

    public static void objective(ObjectivePayload p) {
        ClientStory.objective = p.text().isEmpty() ? null : p;
    }

    public static void hint(HintPayload p) {
        ClientStory.setHint(p);
    }

    public static void shake(ShakePayload p) {
        ClientStory.startShake(p.intensity(), p.ticks());
    }

    public static void cinematic(CinematicPayload p) {
        CinematicDef def = Story.content().cinematic(p.id());
        if (def != null) CinematicPlayer.start(def, p.anchorX(), p.anchorY(), p.anchorZ());
    }
}
