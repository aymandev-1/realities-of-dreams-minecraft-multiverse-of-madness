package net.realitiesofdreams.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.realitiesofdreams.network.DialogueLinePayload;
import net.realitiesofdreams.network.HintPayload;
import net.realitiesofdreams.network.ObjectivePayload;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** What the HUD shows: the current conversation line, passing remarks and the quest tracker. */
public final class ClientStory {
    @Nullable static DialogueLinePayload line;
    static long lineStartMs;
    static boolean lineRevealed;
    @Nullable static ObjectivePayload objective;
    static final List<Ambient> ambient = new ArrayList<>();
    @Nullable static String hint;
    static long hintStartMs, hintEndMs;
    static float shakeIntensity;
    static int shakeTicks, shakeTotal;

    record Ambient(DialogueLinePayload line, long startMs, long endMs) {}

    private ClientStory() {}

    static void setLine(DialogueLinePayload p) {
        line = p;
        lineStartMs = System.currentTimeMillis();
        lineRevealed = false;
    }

    static void addAmbient(DialogueLinePayload p) {
        long now = System.currentTimeMillis();
        ambient.removeIf(a -> a.line.speakerId().equals(p.speakerId()));
        ambient.add(new Ambient(p, now, now + p.ticks() * 50L + 1500));
        while (ambient.size() > 3) ambient.remove(0);
    }

    /** Characters revealed so far by the typewriter effect. */
    static int visibleChars() {
        if (line == null) return 0;
        if (lineRevealed) return line.text().length();
        int n = (int) ((System.currentTimeMillis() - lineStartMs) * 55 / 1000);
        return Math.min(line.text().length(), n);
    }

    static void setHint(HintPayload p) {
        hint = withKeyNames(p.text());
        hintStartMs = System.currentTimeMillis();
        hintEndMs = hintStartMs + p.ticks() * 50L;
    }

    /** Replaces {forward}, {jump}, {inventory}... with the keys the player actually uses. */
    static String withKeyNames(String text) {
        Options o = Minecraft.getInstance().options;
        Object[][] keys = {{"forward", o.keyUp}, {"left", o.keyLeft}, {"back", o.keyDown}, {"right", o.keyRight},
                {"jump", o.keyJump}, {"sneak", o.keyShift}, {"sprint", o.keySprint}, {"inventory", o.keyInventory},
                {"use", o.keyUse}, {"attack", o.keyAttack}, {"continue", RodKeys.CONTINUE}};
        for (Object[] k : keys) {
            text = text.replace("{" + k[0] + "}", "[" + ((KeyMapping) k[1]).getTranslatedKeyMessage().getString() + "]");
        }
        return text;
    }

    static void startShake(float intensity, int ticks) {
        shakeIntensity = intensity;
        shakeTicks = ticks;
        shakeTotal = Math.max(1, ticks);
    }

    static void tickShake() {
        if (shakeTicks > 0) shakeTicks--;
    }

    /** Time the current objective appeared, for the HUD's "new objective" flourish. */
    static long objectiveSinceMs;

    static void objectiveChanged() {
        objectiveSinceMs = System.currentTimeMillis();
        Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                net.realitiesofdreams.registry.ModSounds.QUEST_UPDATE.get(), 1.0F, 0.6F));
    }

    static void reset() {
        line = null;
        objective = null;
        hint = null;
        shakeTicks = 0;
        ambient.clear();
    }
}
