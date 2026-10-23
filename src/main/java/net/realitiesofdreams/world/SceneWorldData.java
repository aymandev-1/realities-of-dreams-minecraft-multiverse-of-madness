package net.realitiesofdreams.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Per-universe record of what has been built, which world changes happened, and which NPC is which. */
public final class SceneWorldData extends SavedData {
    private static final String NAME = "realitiesofdreams_scenes";
    private final Set<String> done = new HashSet<>();
    private final Map<String, UUID> npcs = new HashMap<>();

    public static SceneWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(SceneWorldData::new, SceneWorldData::load, null), NAME);
    }

    private static SceneWorldData load(CompoundTag tag, HolderLookup.Provider provider) {
        SceneWorldData d = new SceneWorldData();
        for (Tag t : tag.getList("done", Tag.TAG_STRING)) d.done.add(t.getAsString());
        CompoundTag n = tag.getCompound("npcs");
        for (String k : n.getAllKeys()) d.npcs.put(k, n.getUUID(k));
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        for (String s : done) list.add(StringTag.valueOf(s));
        tag.put("done", list);
        CompoundTag n = new CompoundTag();
        npcs.forEach(n::putUUID);
        tag.put("npcs", n);
        return tag;
    }

    public boolean isDone(String key) {
        return done.contains(key);
    }

    public void markDone(String key) {
        if (done.add(key)) setDirty();
    }

    public UUID npc(String key) {
        return npcs.get(key);
    }

    public void setNpc(String key, UUID id) {
        npcs.put(key, id);
        setDirty();
    }
}
