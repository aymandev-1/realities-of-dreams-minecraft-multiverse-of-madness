package net.realitiesofdreams;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class RodConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue AUTO_START_STORY;
    public static final ModConfigSpec.IntValue CHATTER_COOLDOWN_SECONDS;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        AUTO_START_STORY = b.comment("Start MC-001 Scene 001 automatically the first time a player joins.")
                .define("autoStartStory", true);
        CHATTER_COOLDOWN_SECONDS = b.comment("Minimum seconds between unprompted companion lines.")
                .defineInRange("chatterCooldownSeconds", 22, 5, 600);
        SPEC = b.build();
    }

    private RodConfig() {}

    static boolean loaded() {
        return SPEC.isLoaded();
    }

    public static boolean autoStart() {
        return !SPEC.isLoaded() || AUTO_START_STORY.get();
    }

    public static int chatterCooldownTicks() {
        return 20 * (SPEC.isLoaded() ? CHATTER_COOLDOWN_SECONDS.get() : 22);
    }
}
