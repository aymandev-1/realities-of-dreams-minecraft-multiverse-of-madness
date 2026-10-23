package net.realitiesofdreams.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.story.cinematic.CinematicDef;
import net.realitiesofdreams.story.cinematic.CinematicTimeline;

import java.util.List;

/** Letterbox bars, fades, title cards, captions and spoken subtitles during cinematics. */
public final class CinematicOverlay {
    private CinematicOverlay() {}

    public static void render(GuiGraphics g, DeltaTracker delta) {
        CinematicTimeline tl = CinematicPlayer.timeline();
        if (tl == null) return;
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int w = g.guiWidth(), h = g.guiHeight();
        double t = CinematicPlayer.time(delta.getGameTimeDeltaPartialTick(true));

        if (tl.def().letterbox()) {
            int bar = h / 9;
            g.fill(0, 0, w, bar, 0xFF000000);
            g.fill(0, h - bar, w, h, 0xFF000000);
        }
        for (CinematicDef.Subtitle s : tl.subtitlesAt(t)) {
            float fade = (float) Math.min(1, Math.min((t - s.start()) / 10, (s.start() + s.duration() - t) / 10));
            int alpha = Math.max(8, (int) (fade * 255)) << 24;
            switch (s.style()) {
                case "title" -> drawScaled(g, font, s.text(), w / 2f, h / 2f - 12, 3.0f, alpha | 0xF4E2B0);
                case "caption" -> drawScaled(g, font, s.text(), w / 2f, h * 0.70f, 1.5f, alpha | 0xFFFFFF);
                default -> {
                    String name = s.speaker() == null ? "" : Story.content().characters().displayName(s.speaker());
                    List<FormattedCharSequence> lines = font.split(FormattedText.of(s.text()), Math.min(420, w - 40));
                    int y = h - h / 9 - 14 - lines.size() * 10;
                    if (!name.isEmpty()) g.drawCenteredString(font, name, w / 2, y - 11, alpha | 0xE8B84A);
                    for (int i = 0; i < lines.size(); i++) {
                        g.drawString(font, lines.get(i), (w - font.width(lines.get(i))) / 2, y + i * 10, alpha | 0xFFFFFF, true);
                    }
                }
            }
        }
        if (tl.def().skippable() && t > 20) {
            String hint = "[" + RodKeys.CONTINUE.getTranslatedKeyMessage().getString() + "] skip";
            g.drawString(font, hint, w - font.width(hint) - 8, h - h / 18 - 4, 0x80FFFFFF, false);
        }
        float fade = tl.fadeAlpha(t);
        if (fade > 0.001f) g.fill(0, 0, w, h, ((int) (fade * 255) << 24));
    }

    private static void drawScaled(GuiGraphics g, Font font, String text, float cx, float cy, float scale, int color) {
        g.pose().pushPose();
        g.pose().translate(cx, cy, 0);
        g.pose().scale(scale, scale, 1);
        g.drawString(font, text, -font.width(text) / 2, -4, color, true);
        g.pose().popPose();
    }
}
