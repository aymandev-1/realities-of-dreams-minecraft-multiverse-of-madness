package net.realitiesofdreams.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class RodKeys {
    public static final String CATEGORY = "key.categories.realitiesofdreams";
    /** Finishes the current line, skips to the next one, or skips a cinematic. */
    public static final KeyMapping CONTINUE = new KeyMapping("key.realitiesofdreams.continue", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);

    private RodKeys() {}
}
