package net.realitiesofdreams;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.realitiesofdreams.registry.ModAttachments;
import net.realitiesofdreams.registry.ModEntities;
import org.slf4j.Logger;

@Mod(RealitiesOfDreams.MODID)
public class RealitiesOfDreams {
    public static final String MODID = "realitiesofdreams";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RealitiesOfDreams(IEventBus modBus, ModContainer container) {
        ModEntities.ENTITIES.register(modBus);
        ModAttachments.ATTACHMENTS.register(modBus);
        modBus.addListener(ModEntities::registerAttributes);
        container.registerConfig(ModConfig.Type.SERVER, RodConfig.SPEC);
        LOGGER.info("Realities of Dreams loaded {} characters and {} scenes",
                Story.content().characters().size(), Story.content().scenes().size());
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
