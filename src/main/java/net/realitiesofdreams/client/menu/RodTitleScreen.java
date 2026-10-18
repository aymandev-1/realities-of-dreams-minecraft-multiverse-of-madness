package net.realitiesofdreams.client.menu;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.ModListScreen;
import net.realitiesofdreams.registry.ModSounds;

import javax.annotation.Nullable;

/**
 * The Realities of Dreams main menu. It replaces the vanilla title screen while the mod is
 * installed (the vanilla one stays one click away under "Minecraft Menu", and can be restored for
 * good in the mod's client config).
 */
public class RodTitleScreen extends Screen {
    private static final long FADE_IN_MS = 1400;
    private static final long FADE_OUT_MS = 900;
    private static boolean firstShow = true;

    private final long openedMs = System.currentTimeMillis();
    private final boolean fadeIn;
    @Nullable private Runnable pending;
    private long leavingSinceMs;
    @Nullable private StorySaves.Checkpoint checkpoint;
    private boolean saveExists;
    private int menuX;
    private int logoW = 300;
    private boolean compact;
    private int footerLeftLimit;

    public RodTitleScreen() {
        super(Component.translatable("realitiesofdreams.menu.title"));
        this.fadeIn = firstShow;
        firstShow = false;
    }

    @Override
    protected void init() {
        saveExists = StorySaves.exists();
        checkpoint = saveExists ? StorySaves.checkpoint() : null;
        menuX = Math.max(12, (int) (width * 0.07f));
        compact = height < 300;
        int rowH = compact ? 14 : 18, step = compact ? 15 : 20;
        int buttonW = Math.min(230, Math.max(150, width / 3));
        Component cp = checkpointLine();
        int menuH = 7 * step + (cp == null ? 0 : step / 2 + 2);
        logoW = Math.min(340, Math.min((int) (width * 0.46f), (height - 30 - menuH - logoTop() - 22) * MenuBackground.LOGO_W / MenuBackground.LOGO_H));
        int y = logoBottom() + (compact ? 6 : 12);
        long t0 = System.currentTimeMillis() + (fadeIn ? 700 : 80);
        int i = 0;

        add(new MenuButton(menuX, y, buttonW, rowH, Component.translatable("realitiesofdreams.menu.new_story"), null, t0 + 70L * i++, this::newStory));
        y += step;
        MenuButton cont = add(new MenuButton(menuX, y, buttonW, cp == null ? rowH : rowH + 8, Component.translatable("realitiesofdreams.menu.continue"), cp,
                t0 + 70L * i++, () -> leave(() -> StorySaves.continueStory(this))));
        cont.active = saveExists;
        if (!saveExists) cont.setTooltip(Tooltip.create(Component.translatable("realitiesofdreams.menu.continue.none")));
        y += cp == null ? step : step + step / 2 + 2;
        MenuButton chapters = add(new MenuButton(menuX, y, buttonW, rowH, Component.translatable("realitiesofdreams.menu.chapters"), null, t0 + 70L * i++,
                () -> open(new ChapterSelectScreen(this))));
        chapters.active = saveExists;
        if (!saveExists) chapters.setTooltip(Tooltip.create(Component.translatable("realitiesofdreams.menu.chapters.none")));
        y += step;
        MenuButton mp = add(new MenuButton(menuX, y, buttonW, rowH, Component.translatable("menu.multiplayer"), null, t0 + 70L * i++, () -> {
            Screen s = minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
            minecraft.setScreen(s);
        }));
        if (!minecraft.allowsMultiplayer()) {
            mp.active = false;
            mp.setTooltip(Tooltip.create(Component.translatable("title.multiplayer.disabled")));
        }
        y += step;
        add(new MenuButton(menuX, y, buttonW, rowH, Component.translatable("realitiesofdreams.menu.settings"), null, t0 + 70L * i++,
                () -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        y += step;
        add(new MenuButton(menuX, y, buttonW, rowH, Component.translatable("realitiesofdreams.menu.credits"), null, t0 + 70L * i++,
                () -> open(new RodCreditsScreen(this))));
        y += step;
        add(new MenuButton(menuX, y, buttonW, rowH, Component.translatable("realitiesofdreams.menu.exit"), null, t0 + 70L * i, minecraft::stop));

        // Small corner buttons, as on the vanilla title screen: language, accessibility, mods, vanilla menu.
        int by = height - 24;
        addRenderableWidget(CommonButtons.language(20, b -> minecraft.setScreen(new LanguageSelectScreen(this, minecraft.options, minecraft.getLanguageManager())), true))
                .setPosition(width - 24, by);
        addRenderableWidget(CommonButtons.accessibility(20, b -> minecraft.setScreen(new AccessibilityOptionsScreen(this, minecraft.options)), true))
                .setPosition(width - 48, by);
        int modsW = font.width(Component.translatable("fml.menu.mods")) + 16;
        addRenderableWidget(Button.builder(Component.translatable("fml.menu.mods"), b -> minecraft.setScreen(new ModListScreen(this)))
                .bounds(width - 52 - modsW, by, modsW, 20).build());
        int vanillaW = font.width(Component.translatable("realitiesofdreams.menu.vanilla")) + 16;
        addRenderableWidget(Button.builder(Component.translatable("realitiesofdreams.menu.vanilla"), b -> MenuHooks.openVanillaTitle())
                .bounds(width - 56 - modsW - vanillaW, by, vanillaW, 20).build());
        footerLeftLimit = width - 60 - modsW - vanillaW;
    }

    private void open(Screen s) {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.MENU_TRANSITION.get(), 1.0F, 0.5F));
        minecraft.setScreen(s);
    }

    private MenuButton add(MenuButton b) {
        return addRenderableWidget(b);
    }

    @Nullable
    private Component checkpointLine() {
        if (checkpoint == null) return saveExists ? Component.translatable("realitiesofdreams.menu.continue.saved") : null;
        String objective = checkpoint.objective() == null ? "" : " — " + checkpoint.objective();
        return Component.literal(checkpoint.label() + objective);
    }

    private int logoTop() {
        return Math.max(8, (int) (height * 0.06f));
    }

    private float subtitleScale() {
        return Math.max(0.75f, Math.min(1.4f, logoW / 250f));
    }

    private int logoBottom() {
        return logoTop() + logoW * MenuBackground.LOGO_H / MenuBackground.LOGO_W + (int) (12 * subtitleScale()) + 2;
    }

    private void newStory() {
        if (!saveExists) {
            leave(() -> startNewStory());
            return;
        }
        minecraft.setScreen(new ConfirmScreen(yes -> {
            if (yes) {
                minecraft.setScreen(this);
                leave(this::startNewStory);
            } else {
                minecraft.setScreen(this);
            }
        }, Component.translatable("realitiesofdreams.menu.new_story.confirm.title"),
                Component.translatable("realitiesofdreams.menu.new_story.confirm.body"),
                Component.translatable("realitiesofdreams.menu.new_story.confirm.yes"), Component.translatable("gui.cancel")));
    }

    private void startNewStory() {
        if (!StorySaves.newStory(this)) {
            minecraft.setScreen(new ConfirmScreen(ok -> minecraft.setScreen(new RodTitleScreen()),
                    Component.translatable("realitiesofdreams.menu.new_story.failed.title"),
                    Component.translatable("realitiesofdreams.menu.new_story.failed.body"), Component.translatable("gui.ok"), Component.translatable("gui.back")));
        }
    }

    /** Fades the menu to black, then runs the action (opening the story save). */
    private void leave(Runnable action) {
        if (pending != null) return;
        pending = action;
        leavingSinceMs = System.currentTimeMillis();
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.STORY_BEGIN.get(), 1.0F, 0.9F));
        minecraft.getMusicManager().stopPlaying();
    }

    @Override
    public void tick() {
        if (pending != null && System.currentTimeMillis() - leavingSinceMs >= FADE_OUT_MS) {
            Runnable r = pending;
            pending = null;
            r.run();
        }
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partial) {
        int ringSize = (int) (Math.min(width, height) * 0.95f);
        MenuBackground.render(g, width, height, partial, 1.0F, (int) (width * 0.70f), (int) (height * 0.48f), ringSize);
        float fade = fadeIn ? Mth.clamp((System.currentTimeMillis() - openedMs - 300) / (float) FADE_IN_MS, 0f, 1f) : 1f;
        int lx = menuX - 2, ly = logoTop();
        int lh = MenuBackground.logo(g, lx, ly, logoW, fade);
        float ss = subtitleScale();
        g.pose().pushPose();
        g.pose().translate(lx + 4, ly + lh + 1, 0);
        g.pose().scale(ss, ss, 1f);
        if (fade > 0.03f) g.drawString(font, Component.translatable("realitiesofdreams.menu.subtitle"), 0, 0, (int) (fade * 255) << 24 | 0xE8D9A8, true);
        g.pose().popPose();
        String version = "Realities of Dreams 0.1.0 · Saga 1 · Minecraft " + SharedConstants.getCurrentVersion().getName();
        float vs = Math.min(1f, Math.max(0.5f, (footerLeftLimit - 8) / (float) Math.max(1, font.width(version))));
        g.pose().pushPose();
        g.pose().translate(4, height - 4 - 8 * vs, 0);
        g.pose().scale(vs, vs, 1f);
        g.drawString(font, version, 0, 0, 0x90FFFFFF, false);
        g.pose().popPose();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        super.render(g, mouseX, mouseY, partial);
        long now = System.currentTimeMillis();
        float black = 0;
        if (fadeIn) black = 1 - Mth.clamp((now - openedMs) / (float) FADE_IN_MS, 0f, 1f);
        if (pending != null) black = Math.max(black, Mth.clamp((now - leavingSinceMs) / (float) FADE_OUT_MS, 0f, 1f));
        if (black > 0.004f) {
            g.pose().pushPose();
            g.pose().translate(0, 0, 400);
            g.fill(0, 0, width, height, (int) (black * 255) << 24);
            g.pose().popPose();
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** The vanilla title screen, kept reachable from this menu. */
    static TitleScreen vanilla() {
        return new TitleScreen(false);
    }
}
