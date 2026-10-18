package net.realitiesofdreams.client.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.realitiesofdreams.RealitiesOfDreams;

/**
 * The menus' moving backdrop: a slowly turning panorama of Firstbrook at sunrise (photographed in
 * the game itself), darkened on the menu side, with the ancient gateway's rings turning faintly
 * behind the title.
 */
public final class MenuBackground {
    public static final ResourceLocation PANORAMA = RealitiesOfDreams.id("textures/gui/title/background/panorama");
    public static final ResourceLocation RING_OUTER = RealitiesOfDreams.id("textures/gui/menu/gateway_ring_outer.png");
    public static final ResourceLocation RING_INNER = RealitiesOfDreams.id("textures/gui/menu/gateway_ring_inner.png");
    public static final ResourceLocation VIGNETTE = RealitiesOfDreams.id("textures/gui/menu/vignette.png");
    public static final ResourceLocation LOGO = RealitiesOfDreams.id("textures/gui/menu/logo.png");
    /** logo.png is this many pixels across and down. */
    public static final int LOGO_W = 1024, LOGO_H = 288;

    private static final PanoramaRenderer RENDERER = new PanoramaRenderer(new CubeMap(PANORAMA));
    private static final PanoramaRenderer VANILLA = new PanoramaRenderer(new CubeMap(ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama")));
    private static Boolean hasPanorama;

    private MenuBackground() {}

    /**
     * @param shade how dark the left (menu) side is, 0..1
     * @param ringX where the gateway rings are centred, or -1 for none
     */
    public static void render(GuiGraphics g, int width, int height, float partial, float shade, int ringX, int ringY, int ringSize) {
        if (hasPanorama == null) {
            hasPanorama = Minecraft.getInstance().getResourceManager().getResource(PANORAMA.withSuffix("_0.png")).isPresent();
        }
        if (hasPanorama) RENDERER.render(g, width, height, 1.0F, partial);
        else VANILLA.render(g, width, height, 1.0F, partial);
        RenderSystem.enableBlend();
        // Darken the menu side so the text stays readable over any part of the panorama.
        int steps = 24, edge = (int) (width * 0.62f);
        for (int i = 0; i < steps; i++) {
            int x0 = edge * i / steps, x1 = edge * (i + 1) / steps;
            float t = 1 - i / (float) steps;
            g.fill(x0, 0, x1, height, ((int) (shade * 200 * t * t) << 24) | 0x07060A);
        }
        g.fillGradient(0, height - height / 4, width, height, 0x00000000, (int) (shade * 150) << 24);
        if (ringX >= 0) rings(g, ringX, ringY, ringSize);
        blend();
        g.setColor(1, 1, 1, 0.55F);
        g.blit(VIGNETTE, 0, 0, width, height, 0, 0, 256, 256, 256, 256);
        g.setColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    private static void rings(GuiGraphics g, int cx, int cy, int size) {
        double t = (System.currentTimeMillis() % 3_600_000L) / 1000.0;
        float speed = Minecraft.getInstance().options.panoramaSpeed().get().floatValue();
        g.setColor(1, 1, 1, 0.15F);
        drawRotated(g, RING_OUTER, cx, cy, size, (float) (t * 2.0 * speed));
        g.setColor(1, 1, 1, 0.19F);
        drawRotated(g, RING_INNER, cx, cy, (int) (size * 0.72f), (float) (-t * 3.5 * speed));
        g.setColor(1, 1, 1, 1);
    }

    /** GuiGraphics.fill switches blending off again, so textures with transparency re-enable it. */
    private static void blend() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private static void drawRotated(GuiGraphics g, ResourceLocation tex, int cx, int cy, int size, float degrees) {
        blend();
        g.pose().pushPose();
        g.pose().translate(cx, cy, 0);
        g.pose().mulPose(Axis.ZP.rotationDegrees(degrees));
        g.blit(tex, -size / 2, -size / 2, size, size, 0, 0, 512, 512, 512, 512);
        g.pose().popPose();
    }

    /** Draws the title graphic with its top-left corner at (x, y), the given width wide. Returns its height. */
    public static int logo(GuiGraphics g, int x, int y, int width, float alpha) {
        int h = width * LOGO_H / LOGO_W;
        blend();
        g.setColor(1, 1, 1, alpha);
        g.blit(LOGO, x, y, width, h, 0, 0, LOGO_W, LOGO_H, LOGO_W, LOGO_H);
        g.setColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        return h;
    }
}
