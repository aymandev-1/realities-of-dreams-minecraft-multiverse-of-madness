package net.realitiesofdreams.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.story.scene.SceneDef;

@EventBusSubscriber(modid = RealitiesOfDreams.MODID)
public final class ServerEvents {
    private ServerEvents() {}

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer p) StoryManager.get().tick(p);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) StoryManager.get().onLogin(p);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) StoryManager.get().onLogout(p);
    }

    /** Dying mid-scene puts the player back at the last checkpoint in the scene's universe. */
    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered() || !(event.getEntity() instanceof ServerPlayer p)) return;
        StoryManager m = StoryManager.get();
        SceneDef scene = Story.engine().activeScene(m.state(p));
        if (scene != null) m.teleportToCheckpoint(p, scene, m.state(p));
    }

    @SubscribeEvent
    public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || !(event.getEntity() instanceof ServerPlayer p)) return;
        if (StoryManager.get().onBlockUse(p, event.getPos())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingIncomingDamageEvent event) {
        LivingEntity e = event.getEntity();
        if (e instanceof ServerPlayer p && event.getSource().getEntity() instanceof LivingEntity) StoryManager.get().onPlayerHurt(p);
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        RodCommands.register(event.getDispatcher());
    }
}
