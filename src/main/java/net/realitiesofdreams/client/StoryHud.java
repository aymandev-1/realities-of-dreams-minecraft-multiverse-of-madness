package net.realitiesofdreams.client;

import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.realitiesofdreams.network.DialogueLinePayload;
import net.realitiesofdreams.network.ObjectivePayload;

import java.util.List;

/** Conversation subtitles, passing remarks and the objective tracker with a direction arrow. */
public final class StoryHud {
    private static final int GOLD = 0xFFE8B84A;

    private StoryHud() {}

    public static void renderDialogue(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || CinematicPlayer.active()) return;
        Font font = mc.font;
        int width = g.guiWidth(), height = g.guiHeight();

        DialogueLinePayload line = ClientStory.line;
        if (line != null) {
            int boxW = Math.min(360, width - 40);
            String shown = line.text().substring(0, ClientStory.visibleChars());
            List<FormattedCharSequence> lines = font.split(FormattedText.of(shown.isEmpty() ? " " : shown), boxW - 16);
            int boxH = 22 + Math.max(2, font.split(FormattedText.of(line.text()), boxW - 16).size()) * 10;
            int bottom = mc.screen instanceof DialogueChoiceScreen ? height - 66 - line.choices().size() * 24 : height - 62;
            int x = (width - boxW) / 2, y = bottom - boxH;
            g.fill(x, y, x + boxW, bottom, 0xB0101018);
            g.fill(x, y, x + boxW, y + 1, GOLD);
            g.drawString(font, line.speakerName(), x + 8, y + 6, GOLD, true);
            for (int i = 0; i < lines.size(); i++) g.drawString(font, lines.get(i), x + 8, y + 18 + i * 10, 0xFFF2F2F2, true);
            if (line.choices().isEmpty()) {
                String hint = "[" + RodKeys.CONTINUE.getTranslatedKeyMessage().getString() + "] continue";
                g.drawString(font, hint, x + boxW - font.width(hint) - 6, y + 6, 0xFF8A8A8A, false);
            }
        }

        // Remarks made in passing, stacked on the left.
        long now = System.currentTimeMillis();
        ClientStory.ambient.removeIf(a -> now > a.endMs());
        int ay = height / 2 + 10;
        for (ClientStory.Ambient a : ClientStory.ambient) {
            float alpha = Mth.clamp((a.endMs() - now) / 600f, 0f, 1f) * Mth.clamp((now - a.startMs()) / 250f, 0f, 1f);
            int al = (int) (alpha * 255);
            if (al < 8) continue;
            List<FormattedCharSequence> lines = font.split(FormattedText.of(a.line().text()), 200);
            int h = 14 + lines.size() * 10;
            g.fill(8, ay, 216, ay + h, (al * 0x90 / 255) << 24 | 0x101018);
            g.drawString(font, a.line().speakerName(), 13, ay + 3, (al << 24) | 0xE8B84A, false);
            for (int i = 0; i < lines.size(); i++) g.drawString(font, lines.get(i), 13, ay + 13 + i * 10, (al << 24) | 0xE6E6E6, false);
            ay += h + 4;
        }
    }

    public static void renderObjective(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        ObjectivePayload o = ClientStory.objective;
        if (o == null || mc.options.hideGui || CinematicPlayer.active() || mc.player == null) return;
        Font font = mc.font;
        String header = o.label() + "  ·  " + o.title();
        String text = o.text() + (o.progress().isEmpty() ? "" : "  (" + o.progress() + ")");
        int w = Math.max(font.width(header), font.width(text) + 14) + 16;
        int x = 8, y = 8;
        g.fill(x, y, x + w, y + 30, 0x90101018);
        g.fill(x, y, x + 2, y + 30, 0xFFE8B84A);
        g.drawString(font, header, x + 8, y + 5, 0xFFA8A8B0, false);
        g.drawString(font, "◆ " + text, x + 8, y + 17, 0xFFFFFFFF, true);
        if (!o.hasTarget()) return;

        Vec3 target = new Vec3(o.x(), o.y(), o.z());
        Vec3 pos = mc.player.position();
        double dist = Math.sqrt(Math.pow(target.x - pos.x, 2) + Math.pow(target.z - pos.z, 2));
        String d = dist < 4 ? "here" : Math.round(dist) + "m";
        int cx = x + w + 18, cy = y + 15;
        g.fill(cx - 13, y, cx + 13 + font.width(d) + 4, y + 30, 0x90101018);
        if (dist >= 4) {
            double bearing = Math.toDegrees(Math.atan2(-(target.x - pos.x), target.z - pos.z));
            float rel = (float) Mth.wrapDegrees(bearing - mc.player.getYRot());
            g.pose().pushPose();
            g.pose().translate(cx, cy, 0);
            g.pose().mulPose(Axis.ZP.rotationDegrees(rel));
            g.drawString(font, "▲", -font.width("▲") / 2, -4, 0xFFE8B84A, false);
            g.pose().popPose();
        }
        g.drawString(font, d, cx + 10, cy - 4, 0xFFFFFFFF, true);
    }
}
