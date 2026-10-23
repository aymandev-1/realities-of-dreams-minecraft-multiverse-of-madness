package net.realitiesofdreams.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.entity.StoryNpcEntity;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, RealitiesOfDreams.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<StoryNpcEntity>> STORY_NPC = ENTITIES.register("story_npc",
            () -> EntityType.Builder.<StoryNpcEntity>of(StoryNpcEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .eyeHeight(1.62F)
                    .clientTrackingRange(10)
                    .build("story_npc"));

    private ModEntities() {}

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(STORY_NPC.get(), StoryNpcEntity.createAttributes().build());
    }
}
