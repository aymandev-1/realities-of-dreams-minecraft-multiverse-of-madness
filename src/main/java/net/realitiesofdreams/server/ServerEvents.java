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
import net.realitiesofdreams.RodConfig;
import net.realitiesofdreams.world.ModDimensions;
import net.realitiesofdreams.world.SceneWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.realitiesofdreams.campaign.Appearance;
import net.realitiesofdreams.campaign.Campaign;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.realitiesofdreams.story.scene.SceneDef;

@EventBusSubscriber(modid = RealitiesOfDreams.MODID)
public final class ServerEvents {
    private ServerEvents() {}

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        Campaign.onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        Campaign.onServerStopped();
        Appearance.reset();
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        StoryManager.get().tick(p);
        if (p.tickCount % 20 == 0) Appearance.refresh(p);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        StoryManager.get().onLogin(p);
        Appearance.onLogin(p);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        StoryManager.get().onLogout(p);
        Appearance.onLogout(p);
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) Appearance.refresh(p);
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

    /** Survival players can't break or build over the few blocks the story depends on. */
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer p && storyBlock(p, event.getPos())) {
            event.setCanceled(true);
            p.displayClientMessage(Component.translatable("realitiesofdreams.protected"), true);
        }
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer p && storyBlock(p, event.getPos())) {
            event.setCanceled(true);
            p.displayClientMessage(Component.translatable("realitiesofdreams.protected"), true);
        }
    }

    /** Creepers and TNT don't blow up story blocks either. */
    @SubscribeEvent
    public static void onExplode(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !RodConfig.protectStorySites()) return;
        for (SceneDef scene : sitesIn(level)) event.getAffectedBlocks().removeIf(pos -> SceneWorld.isProtected(level, scene, pos));
    }

    private static boolean storyBlock(ServerPlayer p, BlockPos pos) {
        if (!RodConfig.protectStorySites() || p.isCreative()) return false;
        for (SceneDef scene : sitesIn(p.serverLevel())) if (SceneWorld.isProtected(p.serverLevel(), scene, pos)) return true;
        return false;
    }

    /** One scene per site whose world is this level (scenes sharing a site share its protected blocks). */
    private static java.util.List<SceneDef> sitesIn(ServerLevel level) {
        java.util.Map<String, SceneDef> bySite = new java.util.LinkedHashMap<>();
        for (SceneDef s : Story.content().scenes()) {
            if (ModDimensions.level(level.getServer(), s) == level) bySite.putIfAbsent(s.site(), s);
        }
        return java.util.List.copyOf(bySite.values());
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        RodCommands.register(event.getDispatcher());
    }
}
