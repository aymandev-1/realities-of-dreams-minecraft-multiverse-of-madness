package net.realitiesofdreams.registry;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.story.state.PlayerStoryState;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RealitiesOfDreams.MODID);

    /** The player's story progress; saved with the player and kept through death. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<StoryData>> STORY = ATTACHMENTS.register("story",
            () -> AttachmentType.serializable(() -> new StoryData()).copyOnDeath().build());

    private ModAttachments() {}

    public static PlayerStoryState story(Player player) {
        return player.getData(STORY).state();
    }

    public static final class StoryData implements INBTSerializable<CompoundTag> {
        private PlayerStoryState state = new PlayerStoryState();

        public PlayerStoryState state() {
            return state;
        }

        public void replace(PlayerStoryState state) {
            this.state = state;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = new CompoundTag();
            tag.putString("json", state.toJson());
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
            state = PlayerStoryState.fromJson(tag.getString("json"));
        }
    }
}
