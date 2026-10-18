package net.realitiesofdreams.client.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.Music;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.client.RodClientConfig;
import net.realitiesofdreams.registry.ModSounds;

/** Puts the Realities of Dreams main menu in place of the vanilla title screen, with its own music. */
@EventBusSubscriber(modid = RealitiesOfDreams.MODID, value = Dist.CLIENT)
public final class MenuHooks {
    private static Music menuMusic;
    /** The player chose the vanilla menu; keep showing it until they join a world or come back. */
    private static boolean vanillaChosen;

    private MenuHooks() {}

    static void openVanillaTitle() {
        vanillaChosen = true;
        Minecraft.getInstance().setScreen(RodTitleScreen.vanilla());
    }

    static boolean enabled() {
        return RodClientConfig.customMainMenu() && !vanillaChosen;
    }

    @SubscribeEvent
    public static void onOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen && enabled()) event.setNewScreen(new RodTitleScreen());
    }

    /** The vanilla title screen gets a way back to the story menu. */
    @SubscribeEvent
    public static void onInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof TitleScreen) || !RodClientConfig.customMainMenu()) return;
        event.addListener(Button.builder(Component.translatable("realitiesofdreams.menu.back_to_story"), b -> {
            vanillaChosen = false;
            Minecraft.getInstance().setScreen(new RodTitleScreen());
        }).bounds(4, 4, 130, 20).build());
    }

    @SubscribeEvent
    public static void onJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        vanillaChosen = false;
    }

    @SubscribeEvent
    public static void onMusic(SelectMusicEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null || !enabled()) return;
        if (menuMusic == null) menuMusic = new Music(ModSounds.MUSIC_MENU, 40, 400, true);
        event.setMusic(menuMusic);
    }
}
