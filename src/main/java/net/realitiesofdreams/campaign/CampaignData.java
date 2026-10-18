package net.realitiesofdreams.campaign;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.UUID;

/** Story-world facts saved with the world (in the overworld's data folder). */
public final class CampaignData extends SavedData {
    private static final String NAME = "realitiesofdreams_campaign";
    @Nullable private UUID lead;
    private boolean pregenerated;

    public static CampaignData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(CampaignData::new, CampaignData::load, null), NAME);
    }

    private static CampaignData load(CompoundTag tag, HolderLookup.Provider provider) {
        CampaignData d = new CampaignData();
        if (tag.hasUUID("Lead")) d.lead = tag.getUUID("Lead");
        d.pregenerated = tag.getBoolean("Pregenerated");
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        if (lead != null) tag.putUUID("Lead", lead);
        tag.putBoolean("Pregenerated", pregenerated);
        return tag;
    }

    @Nullable
    public UUID lead() {
        return lead;
    }

    public boolean pregenerated() {
        return pregenerated;
    }

    public void setPregenerated() {
        pregenerated = true;
        setDirty();
    }

    public void setLead(UUID id) {
        lead = id;
        setDirty();
    }
}
