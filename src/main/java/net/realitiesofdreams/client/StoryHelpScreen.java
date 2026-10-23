package net.realitiesofdreams.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import net.realitiesofdreams.network.ObjectivePayload;
import net.realitiesofdreams.network.StoryHelpPayload;

import java.util.List;

/**
 * Story help, opened with [H] or from the pause menu: what to do now, the controls, and ways out
 * for a player who is stuck (a hint, skipping the current step, going back to the checkpoint).
 */
public class StoryHelpScreen extends Screen {
    private static final List<String> CONTROLS = List.of(
            "Move with {forward} {left} {back} {right}, jump with {jump}",
            "{continue} finishes a line of dialogue or skips a cutscene",
            "{use} examines sparkling objects and places blocks",
            "Green sparkles: build or clear here. White sparkles: something to examine",
            "The gold arrow beside the objective points where to go",
            "Your journal is the written book in your inventory");
    private boolean confirmSkip;

    public StoryHelpScreen() {
        super(Component.literal("Story help"));
    }

    @Override
    protected void init() {
        int w = 220, x = (width - w) / 2, y = top() + 34;
        boolean objective = hasObjective();
        Button hint = addRenderableWidget(Button.builder(Component.literal("Give me a hint"), b -> send("hint")).bounds(x, y, w, 20).build());
        Button skip = addRenderableWidget(Button.builder(Component.literal("Skip this step"), b -> {
            if (!confirmSkip) {
                confirmSkip = true;
                b.setMessage(Component.literal("Click again to skip this step").withStyle(ChatFormatting.GOLD));
                return;
            }
            send("skip");
        }).bounds(x, y + 24, w, 20).build());
        Button checkpoint = addRenderableWidget(Button.builder(Component.literal("Back to the last checkpoint"), b -> send("checkpoint"))
                .bounds(x, y + 48, w, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose()).bounds(x, y + 72, w, 20).build());
        hint.active = objective;
        skip.active = objective;
        checkpoint.active = objective;
    }

    /** Title, objective, four buttons and the controls fit in about 200 pixels; centre that block. */
    private int top() {
        return Math.max(6, (height - 200) / 2);
    }

    private static boolean hasObjective() {
        ObjectivePayload o = ClientStory.objective;
        return o != null && !o.text().isEmpty();
    }

    private void send(String action) {
        PacketDistributor.sendToServer(new StoryHelpPayload(action));
        onClose();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        int cx = width / 2, top = top();
        g.drawCenteredString(font, title.copy().withStyle(ChatFormatting.BOLD), cx, top, 0xFFE8B84A);
        ObjectivePayload o = ClientStory.objective;
        String now = hasObjective() ? o.label() + "  ·  " + o.text() : "No story objective right now.";
        g.drawCenteredString(font, now, cx, top + 16, 0xFFFFFFFF);
        int y = top + 136;
        for (String line : CONTROLS) {
            g.drawCenteredString(font, ClientStory.withKeyNames(line), cx, y, 0xFFB8B8C0);
            y += 10;
        }
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (RodKeys.HELP.matches(key, scan)) {
            onClose();
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }
}
