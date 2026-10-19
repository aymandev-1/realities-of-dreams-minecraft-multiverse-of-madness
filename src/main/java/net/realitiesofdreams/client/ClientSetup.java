package net.realitiesofdreams.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.registry.ModEntities;

@EventBusSubscriber(modid = RealitiesOfDreams.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    public static final net.minecraft.resources.ResourceLocation CINEMATIC_LAYER = RealitiesOfDreams.id("cinematic");
    public static final net.minecraft.resources.ResourceLocation DIALOGUE_LAYER = RealitiesOfDreams.id("dialogue");
    public static final net.minecraft.resources.ResourceLocation OBJECTIVE_LAYER = RealitiesOfDreams.id("objective");

    private ClientSetup() {}

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.STORY_NPC.get(), StoryNpcRenderer::new);
    }

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent event) {
        event.register(RodKeys.CONTINUE);
        event.register(RodKeys.HELP);
    }

    @SubscribeEvent
    public static void layers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(OBJECTIVE_LAYER, StoryHud::renderObjective);
        event.registerAboveAll(DIALOGUE_LAYER, StoryHud::renderDialogue);
        event.registerAboveAll(CINEMATIC_LAYER, CinematicOverlay::render);
    }
}
