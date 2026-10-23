package net.realitiesofdreams.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import net.realitiesofdreams.network.DialogueChoicePayload;
import net.realitiesofdreams.network.DialogueLinePayload;
import org.lwjgl.glfw.GLFW;

/** Reply picker. The world stays visible; keys 1-4 also choose. */
public class DialogueChoiceScreen extends Screen {
    private final DialogueLinePayload line;
    private boolean sent;

    public DialogueChoiceScreen(DialogueLinePayload line) {
        super(Component.literal(line.speakerName()));
        this.line = line;
    }

    @Override
    protected void init() {
        int w = Math.min(320, width - 40);
        int x = (width - w) / 2;
        int y = height - 60 - line.choices().size() * 24;
        for (int i = 0; i < line.choices().size(); i++) {
            int index = i;
            addRenderableWidget(Button.builder(Component.literal((i + 1) + ".  " + line.choices().get(i)), b -> choose(index))
                    .bounds(x, y + i * 24, w, 20).build());
        }
    }

    private void choose(int index) {
        if (sent) return;
        sent = true;
        PacketDistributor.sendToServer(new DialogueChoicePayload(line.session(), index));
        ClientStory.lineRevealed = true;
        onClose();
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        int i = key - GLFW.GLFW_KEY_1;
        if (i >= 0 && i < line.choices().size()) {
            choose(i);
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partial) {
        // No blur or dimming: the speaker stays visible in the world.
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
