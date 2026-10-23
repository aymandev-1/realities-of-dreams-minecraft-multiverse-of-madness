package net.realitiesofdreams.client.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

/** Slowly scrolling credits over the menu backdrop. Scroll with the mouse wheel or arrow keys. */
public class RodCreditsScreen extends Screen {
    private final Screen parent;
    private final long openedMs = System.currentTimeMillis();
    private double manualOffset;
    private final List<Line> lines = new ArrayList<>();

    record Line(FormattedCharSequence text, int color, float scale) {}

    public RodCreditsScreen(Screen parent) {
        super(Component.translatable("realitiesofdreams.menu.credits"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        lines.clear();
        int wrap = Math.min(360, width - 40);
        for (int i = 1; ; i++) {
            String key = "realitiesofdreams.credits." + i;
            Component c = Component.translatable(key);
            if (c.getString().equals(key)) break;
            String raw = c.getString();
            boolean heading = raw.startsWith("#");
            String text = heading ? raw.substring(1).trim() : raw;
            if (text.isEmpty()) {
                lines.add(new Line(FormattedCharSequence.EMPTY, 0, 1f));
                continue;
            }
            for (FormattedCharSequence seq : font.split(FormattedText.of(text), heading ? wrap / 2 : wrap)) {
                lines.add(new Line(seq, heading ? 0xFFE8B84A : 0xFFE6E2D8, heading ? 1.6f : 1f));
            }
        }
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose()).bounds(width / 2 - 50, height - 26, 100, 20).build());
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partial) {
        MenuBackground.render(g, width, height, partial, 0.7F, width / 2, height / 2, (int) (Math.min(width, height) * 1.1f));
        g.fill(0, 0, width, height, 0x80000000);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        super.render(g, mouseX, mouseY, partial);
        double t = (System.currentTimeMillis() - openedMs) / 1000.0;
        int top = 8, bottom = height - 34;
        int logoW = Math.min(300, width - 40);
        int logoH = logoW * MenuBackground.LOGO_H / MenuBackground.LOGO_W;
        double total = logoH + 16 + (height - 30);
        for (Line l : lines) total += (int) (10 * l.scale()) + 2;
        double scrolled = ((t * 14 - manualOffset) % total + total) % total; // the credits loop
        double y = height - 30 - scrolled;
        if (y + logoH > top && y < bottom) MenuBackground.logo(g, (width - logoW) / 2, (int) y, logoW, 1f);
        y += logoH + 16;
        g.enableScissor(0, top, width, bottom);
        for (Line l : lines) {
            int h = (int) (10 * l.scale()) + 2;
            if (y + h > top && y < bottom && l.text() != FormattedCharSequence.EMPTY) {
                g.pose().pushPose();
                g.pose().translate(width / 2f, (float) y, 0);
                g.pose().scale(l.scale(), l.scale(), 1);
                g.drawString(font, l.text(), -font.width(l.text()) / 2, 0, l.color(), true);
                g.pose().popPose();
            }
            y += h;
        }
        g.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dx, double dy) {
        manualOffset += dy * 18;
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_UP) manualOffset += 18;
        else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN) manualOffset -= 18;
        else return super.keyPressed(key, scan, mods);
        return true;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
