package net.realitiesofdreams.client;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Client-only settings (config/realitiesofdreams-client.toml). */
public final class RodClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue CUSTOM_MAIN_MENU;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        CUSTOM_MAIN_MENU = b.comment("Show the Realities of Dreams main menu instead of the vanilla title screen.",
                        "The vanilla menu is always one click away under \"Minecraft Menu\".")
                .define("customMainMenu", true);
        SPEC = b.build();
    }

    private RodClientConfig() {}

    public static boolean customMainMenu() {
        return !SPEC.isLoaded() || CUSTOM_MAIN_MENU.get();
    }
}
