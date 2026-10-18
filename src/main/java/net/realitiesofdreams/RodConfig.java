package net.realitiesofdreams;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class RodConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue AUTO_START_STORY;
    public static final ModConfigSpec.BooleanValue STORY_SERVER;
    public static final ModConfigSpec.BooleanValue PRIMORDIAL_APPEARANCE;
    public static final ModConfigSpec.BooleanValue PROTECT_STORY_SITES;
    public static final ModConfigSpec.IntValue CHATTER_COOLDOWN_SECONDS;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        AUTO_START_STORY = b.comment("Also start MC-001 Scene 001 automatically in ordinary worlds the first time a player joins.",
                        "Story saves made with the main menu's New Story button always start the story; ordinary worlds only",
                        "start it with /rod play unless this is true.")
                .define("autoStartInOrdinaryWorlds", false);
        STORY_SERVER = b.comment("Treat this whole server as a Realities of Dreams story world (for co-op story servers):",
                        "players arrive in MC-001 and the story starts by itself.")
                .define("storyServer", false);
        PRIMORDIAL_APPEARANCE = b.comment("Players appear as Primordial Steve while playing the MC-001 campaign. Only changes how the game",
                        "draws them; account skins are never touched.")
                .define("primordialSteveAppearance", true);
        PROTECT_STORY_SITES = b.comment("Protect the few blocks the story depends on (doors, the well, story props) from being broken.",
                        "Everything else in MC-001 can be mined and built on as normal.")
                .define("protectStorySites", true);
        CHATTER_COOLDOWN_SECONDS = b.comment("Minimum seconds between unprompted companion lines.")
                .defineInRange("chatterCooldownSeconds", 22, 5, 600);
        SPEC = b.build();
    }

    private RodConfig() {}

    static boolean loaded() {
        return SPEC.isLoaded();
    }

    public static boolean autoStart() {
        return SPEC.isLoaded() && AUTO_START_STORY.get();
    }

    public static boolean storyServer() {
        return SPEC.isLoaded() && STORY_SERVER.get();
    }

    public static boolean primordialAppearance() {
        return !SPEC.isLoaded() || PRIMORDIAL_APPEARANCE.get();
    }

    public static boolean protectStorySites() {
        return !SPEC.isLoaded() || PROTECT_STORY_SITES.get();
    }

    public static int chatterCooldownTicks() {
        return 20 * (SPEC.isLoaded() ? CHATTER_COOLDOWN_SECONDS.get() : 22);
    }
}
