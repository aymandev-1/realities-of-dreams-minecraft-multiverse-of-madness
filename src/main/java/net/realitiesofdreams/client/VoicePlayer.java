package net.realitiesofdreams.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.realitiesofdreams.story.dialogue.VoiceKey;

import javax.annotation.Nullable;

/**
 * Plays the recorded voice for a spoken line, if there is one. Recordings live in the
 * {@code rod_voice} resource namespace under {@link VoiceKey}, so a line whose text changes simply
 * falls back to subtitles. Voices use the Voice/Speech volume slider and only one plays at a time.
 */
public final class VoicePlayer {
    public static final String NAMESPACE = "rod_voice";

    @Nullable private static SoundInstance current;

    private VoicePlayer() {}

    /** Starts the line's recording, replacing whatever was speaking. Lines without one stay silent. */
    static void speak(@Nullable String speaker, String text) {
        if (speaker == null || speaker.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NAMESPACE, VoiceKey.of(speaker, text));
        if (mc.getSoundManager().getSoundEvent(id) == null) return;
        stop();
        current = new SimpleSoundInstance(id, SoundSource.VOICE, 1.0F, 1.0F, RandomSource.create(), false, 0,
                SoundInstance.Attenuation.NONE, 0, 0, 0, true);
        mc.getSoundManager().play(current);
    }

    static void stop() {
        if (current != null) Minecraft.getInstance().getSoundManager().stop(current);
        current = null;
    }

    static boolean speaking() {
        return current != null && Minecraft.getInstance().getSoundManager().isActive(current);
    }
}
