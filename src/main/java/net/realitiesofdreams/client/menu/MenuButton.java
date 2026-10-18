package net.realitiesofdreams.client.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.realitiesofdreams.registry.ModSounds;

import javax.annotation.Nullable;

/**
 * A main-menu entry: plain text that lights up gold, with a marker bar, when hovered or focused by
 * keyboard. It is a normal Minecraft button underneath, so Tab/arrow-key navigation, Enter, the
 * narrator and GUI scaling all work as usual.
 */
public class MenuButton extends AbstractButton {
    static final int GOLD = 0xE8B84A;
    private final Runnable action;
    @Nullable private Component subtitle;
    private final long appearAtMs;
    private float hover;

    public MenuButton(int x, int y, int width, int height, Component label, @Nullable Component subtitle, long appearAtMs, Runnable action) {
        super(x, y, width, height, label);
        this.action = action;
        this.subtitle = subtitle;
        this.appearAtMs = appearAtMs;
    }

    public void setSubtitle(@Nullable Component subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public void onPress() {
        action.run();
    }

    @Override
    public void playDownSound(SoundManager sounds) {
        sounds.play(SimpleSoundInstance.forUI(ModSounds.MENU_SELECT.get(), 1.0F, 0.9F));
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
        Font font = Minecraft.getInstance().font;
        float appear = Mth.clamp((System.currentTimeMillis() - appearAtMs) / 450f, 0f, 1f);
        float ease = 1 - (1 - appear) * (1 - appear) * (1 - appear);
        float a = this.alpha * ease;
        if (a <= 0.02f) return;
        float target = active && isHoveredOrFocused() ? 1f : 0f;
        hover += (target - hover) * Math.min(1f, partial * 0.5f + 0.18f);
        int slide = (int) ((1 - ease) * -24);
        int x = getX() + slide, y = getY();

        int band = (int) (a * hover * 150);
        if (band > 2) {
            fillHorizontalGradient(g, x, y, x + width, y + height, (band << 24) | 0x1A1408, 0x001A1408);
            g.fill(x, y, x + 2, y + height, ((int) (a * hover * 255) << 24) | GOLD);
        }
        int textColor = !active ? 0x7A7A80 : lerpColor(0xF2EEE4, GOLD, hover);
        int alphaBits = Math.max(8, (int) (a * 255)) << 24;
        int tx = x + 10 + (int) (hover * 5);
        int ty = subtitle == null ? y + (height - 8) / 2 : y + 4;
        g.drawString(font, getMessage(), tx, ty, alphaBits | textColor, true);
        if (subtitle != null) {
            g.pose().pushPose();
            g.pose().translate(tx, ty + 11, 0);
            g.pose().scale(0.75f, 0.75f, 1f);
            g.drawString(font, subtitle, 0, 0, alphaBits | (active ? 0xB8B0A0 : 0x6A6A70), false);
            g.pose().popPose();
        }
    }

    private static void fillHorizontalGradient(GuiGraphics g, int x0, int y0, int x1, int y1, int left, int right) {
        int steps = 16;
        for (int i = 0; i < steps; i++) {
            int a = x0 + (x1 - x0) * i / steps, b = x0 + (x1 - x0) * (i + 1) / steps;
            g.fill(a, y0, b, y1, lerpArgb(left, right, i / (float) (steps - 1)));
        }
    }

    static int lerpColor(int from, int to, float t) {
        int r = (int) Mth.lerp(t, from >> 16 & 255, to >> 16 & 255);
        int gr = (int) Mth.lerp(t, from >> 8 & 255, to >> 8 & 255);
        int b = (int) Mth.lerp(t, from & 255, to & 255);
        return r << 16 | gr << 8 | b;
    }

    private static int lerpArgb(int from, int to, float t) {
        int a = (int) Mth.lerp(t, from >>> 24, to >>> 24);
        return a << 24 | lerpColor(from, to, t);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        defaultButtonNarrationText(out);
        if (subtitle != null) out.add(NarratedElementType.HINT, subtitle);
    }
}
