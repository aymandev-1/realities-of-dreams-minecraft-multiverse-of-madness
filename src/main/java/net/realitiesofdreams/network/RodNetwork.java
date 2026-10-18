package net.realitiesofdreams.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.client.ClientPayloadHandler;
import net.realitiesofdreams.server.StoryManager;

@EventBusSubscriber(modid = RealitiesOfDreams.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class RodNetwork {
    private RodNetwork() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar r = event.registrar("1");
        // Client handlers are only ever invoked on the client; the lambdas keep client classes unloaded on servers.
        r.playToClient(DialogueLinePayload.TYPE, DialogueLinePayload.STREAM_CODEC, (p, ctx) -> ctx.enqueueWork(() -> ClientPayloadHandler.line(p)));
        r.playToClient(DialogueEndPayload.TYPE, DialogueEndPayload.STREAM_CODEC, (p, ctx) -> ctx.enqueueWork(() -> ClientPayloadHandler.end(p)));
        r.playToClient(ObjectivePayload.TYPE, ObjectivePayload.STREAM_CODEC, (p, ctx) -> ctx.enqueueWork(() -> ClientPayloadHandler.objective(p)));
        r.playToClient(CinematicPayload.TYPE, CinematicPayload.STREAM_CODEC, (p, ctx) -> ctx.enqueueWork(() -> ClientPayloadHandler.cinematic(p)));
        r.playToClient(HintPayload.TYPE, HintPayload.STREAM_CODEC, (p, ctx) -> ctx.enqueueWork(() -> ClientPayloadHandler.hint(p)));
        r.playToClient(ShakePayload.TYPE, ShakePayload.STREAM_CODEC, (p, ctx) -> ctx.enqueueWork(() -> ClientPayloadHandler.shake(p)));

        r.playToServer(DialogueChoicePayload.TYPE, DialogueChoicePayload.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> StoryManager.get().onChoice((ServerPlayer) ctx.player(), p.session(), p.choice())));
        r.playToServer(DialogueSkipPayload.TYPE, DialogueSkipPayload.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> StoryManager.get().onSkip((ServerPlayer) ctx.player(), p.session())));
        r.playToServer(CinematicDonePayload.TYPE, CinematicDonePayload.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> StoryManager.get().onCinematicDone((ServerPlayer) ctx.player(), p.id())));
    }
}
