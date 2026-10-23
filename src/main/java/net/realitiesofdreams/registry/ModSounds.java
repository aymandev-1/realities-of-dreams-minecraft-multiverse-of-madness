package net.realitiesofdreams.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.realitiesofdreams.RealitiesOfDreams;

/** Original sounds shipped in assets/realitiesofdreams/sounds (see docs/ASSETS.md). */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, RealitiesOfDreams.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_MENU = register("music.menu");
    public static final DeferredHolder<SoundEvent, SoundEvent> MENU_SELECT = register("ui.menu_select");
    public static final DeferredHolder<SoundEvent, SoundEvent> MENU_TRANSITION = register("ui.menu_transition");
    public static final DeferredHolder<SoundEvent, SoundEvent> STORY_BEGIN = register("ui.story_begin");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUEST_UPDATE = register("ui.quest_update");

    private ModSounds() {}

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(RealitiesOfDreams.id(name)));
    }
}
