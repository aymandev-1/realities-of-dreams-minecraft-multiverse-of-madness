package net.realitiesofdreams.client.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.story.saga.SagaIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * Chapter / Saga Selection: every scene of the sagas, with only what the player has reached
 * selectable. Finished scenes can be replayed; the current scene continues the story. Later scenes
 * stay locked, and scenes not yet released say so.
 */
public class ChapterSelectScreen extends Screen {
    private final Screen parent;
    private final SagaIndex index = SagaIndex.loadFromClasspath(ChapterSelectScreen.class);
    private int saga = 0;
    private int page = 0;

    enum State { DONE, CURRENT, LOCKED, UNRELEASED }

    record Row(SagaIndex.SceneEntry scene, State state) {}

    public ChapterSelectScreen(Screen parent) {
        super(Component.translatable("realitiesofdreams.menu.chapters"));
        this.parent = parent;
    }

    private List<Row> rows() {
        StorySaves.Checkpoint cp = StorySaves.checkpoint();
        List<Row> out = new ArrayList<>();
        boolean reachedCurrent = false;
        for (SagaIndex.SceneEntry s : index.sagas().get(saga).scenes()) {
            boolean released = Story.content().scene(s.id()) != null;
            State st;
            if (cp != null && cp.completed().contains(s.id())) st = State.DONE;
            else if (!reachedCurrent && released && (cp == null ? s.number() == 1 : s.id().equals(cp.sceneId()))) {
                st = State.CURRENT;
                reachedCurrent = true;
            } else st = released ? State.LOCKED : State.UNRELEASED;
            out.add(new Row(s, st));
        }
        return out;
    }

    /** As many scenes per page as fit between the header and the buttons at this GUI size. */
    private int rowsPerPage() {
        return Math.max(3, Math.min(10, (height - 64 - 36) / 23));
    }

    @Override
    protected void init() {
        int ROWS_PER_PAGE = rowsPerPage();
        SagaIndex.Saga s = index.sagas().get(saga);
        int x = Math.max(16, (int) (width * 0.07f));
        int w = Math.min(320, width - x - 16);
        int y = 64;
        List<Row> rows = rows();
        int pages = Math.max(1, (rows.size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        page = Math.min(page, pages - 1);
        long t0 = System.currentTimeMillis();
        int shown = 0;
        for (int i = page * ROWS_PER_PAGE; i < Math.min(rows.size(), (page + 1) * ROWS_PER_PAGE); i++, shown++) {
            Row r = rows.get(i);
            Component label = Component.literal(String.format("%03d  %s", r.scene().number(), r.scene().title()));
            Component sub = Component.translatable("realitiesofdreams.chapters.state." + r.state().name().toLowerCase(java.util.Locale.ROOT));
            MenuButton b = addRenderableWidget(new MenuButton(x, y, w, 22, label, sub, t0 + 35L * shown, () -> choose(r)));
            b.active = r.state() == State.DONE || r.state() == State.CURRENT;
            y += 23;
        }
        if (s.scenes().isEmpty()) {
            MenuButton b = addRenderableWidget(new MenuButton(x, y, w, 22, Component.translatable("realitiesofdreams.chapters.saga_locked"),
                    Component.translatable("realitiesofdreams.chapters.saga_locked.hint"), t0, () -> {}));
            b.active = false;
        }
        int by = height - 28;
        addRenderableWidget(Button.builder(Component.literal("<"), b -> turn(-1)).bounds(x, by, 20, 20).build()).active = saga > 0 || page > 0;
        addRenderableWidget(Button.builder(Component.literal(">"), b -> turn(1)).bounds(x + 24, by, 20, 20).build()).active =
                page < pages - 1 || saga < index.sagas().size() - 1;
        Button back = addRenderableWidget(Button.builder(Component.translatable("gui.back"), b -> onClose()).bounds(x + 52, by, 90, 20).build());
        back.setTooltip(Tooltip.create(Component.translatable("realitiesofdreams.chapters.back")));
    }

    private void turn(int dir) {
        int ROWS_PER_PAGE = rowsPerPage();
        int pages = Math.max(1, (rows().size() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        if (dir > 0) {
            if (page < pages - 1) page++;
            else if (saga < index.sagas().size() - 1) {
                saga++;
                page = 0;
            }
        } else {
            if (page > 0) page--;
            else if (saga > 0) {
                saga--;
                page = 0;
            }
        }
        rebuildWidgets();
    }

    private void choose(Row r) {
        if (r.state() == State.CURRENT) {
            StorySaves.continueStory(this);
            return;
        }
        minecraft.setScreen(new ConfirmScreen(yes -> {
            if (yes) StorySaves.replay(this, r.scene().id());
            else minecraft.setScreen(this);
        }, Component.translatable("realitiesofdreams.chapters.replay.title", r.scene().title()),
                Component.translatable("realitiesofdreams.chapters.replay.body"),
                Component.translatable("realitiesofdreams.chapters.replay.yes"), Component.translatable("gui.cancel")));
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partial) {
        MenuBackground.render(g, width, height, partial, 1.0F, (int) (width * 0.74f), (int) (height * 0.5f), (int) (Math.min(width, height) * 0.8f));
        SagaIndex.Saga s = index.sagas().get(saga);
        int x = Math.max(16, (int) (width * 0.07f));
        g.pose().pushPose();
        g.pose().translate(x, 22, 0);
        g.pose().scale(2f, 2f, 1f);
        g.drawString(font, Component.translatable("realitiesofdreams.chapters.saga", s.number(), s.title()), 0, 0, 0xFFE8B84A, true);
        g.pose().popPose();
        String universe = s.universe().isEmpty() ? "" : s.universe() + "  ·  ";
        String header = universe + Component.translatable("realitiesofdreams.chapters.header").getString();
        float hs = Math.min(1f, (width - x - 8) / (float) font.width(header));
        g.pose().pushPose();
        g.pose().translate(x, 44, 0);
        g.pose().scale(hs, hs, 1f);
        g.drawString(font, header, 0, 0, 0xFFB8B0A0, false);
        g.pose().popPose();
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
