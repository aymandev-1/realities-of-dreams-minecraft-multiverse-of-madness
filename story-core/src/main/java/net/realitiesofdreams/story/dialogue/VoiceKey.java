package net.realitiesofdreams.story.dialogue;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Names a spoken line's recording: "v_" + the first 12 hex digits of sha1(speaker + "|" + text).
 * tools/voice/import_voice.py computes the same key, so recordings need no ids in the scene files,
 * and a line whose text changes falls back to subtitles until it is recorded again.
 */
public final class VoiceKey {
    private VoiceKey() {}

    public static String of(String speaker, String text) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-1").digest((speaker + "|" + text).getBytes(StandardCharsets.UTF_8));
            return "v_" + HexFormat.of().formatHex(hash).substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
