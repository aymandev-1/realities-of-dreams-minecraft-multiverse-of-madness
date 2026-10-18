package net.realitiesofdreams.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.realitiesofdreams.Story;
import net.realitiesofdreams.server.StoryManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * A story character (player-model NPC). It walks up to players when it has something important
 * to say, follows a player as a companion, faces whoever it is talking to and otherwise
 * potters around its home spot.
 */
public class StoryNpcEntity extends PathfinderMob {
    private static final EntityDataAccessor<String> CHARACTER = SynchedEntityData.defineId(StoryNpcEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> TALKING = SynchedEntityData.defineId(StoryNpcEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable private BlockPos home;
    @Nullable private UUID followTarget;
    @Nullable private UUID approachTarget;
    private boolean approachThenStay;
    @Nullable private UUID talkTarget;
    @Nullable private String sceneId;
    private String activity = "idle";
    private final List<BlockPos> route = new ArrayList<>();
    @Nullable private Vec3 lookPos;
    private int lookTicks;

    public StoryNpcEntity(EntityType<? extends StoryNpcEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARACTER, "");
        builder.define(TALKING, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new TalkGoal());
        goalSelector.addGoal(1, new LookAtPointGoal());
        goalSelector.addGoal(2, new ApproachGoal());
        goalSelector.addGoal(3, new FollowGoal());
        goalSelector.addGoal(4, new PatrolGoal());
        goalSelector.addGoal(4, new HomeGoal());
        goalSelector.addGoal(5, new WorkGoal());
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    // --- identity -------------------------------------------------------------------

    public String characterId() {
        return entityData.get(CHARACTER);
    }

    public void setCharacter(String id) {
        entityData.set(CHARACTER, id);
        setCustomName(Component.literal(Story.content().characters().displayName(id)));
        setCustomNameVisible(false);
    }

    public boolean isTalking() {
        return entityData.get(TALKING);
    }

    @Nullable
    public String sceneId() {
        return sceneId;
    }

    public void setSceneId(@Nullable String sceneId) {
        this.sceneId = sceneId;
    }

    // --- story control --------------------------------------------------------------

    public void setHome(BlockPos pos) {
        this.home = pos.immutable();
    }

    @Nullable
    public BlockPos home() {
        return home;
    }

    public void setFollowTarget(@Nullable UUID player) {
        this.followTarget = player;
    }

    @Nullable
    public UUID followTarget() {
        return followTarget;
    }

    public void setApproachTarget(@Nullable UUID player) {
        this.approachTarget = player;
        this.approachThenStay = false;
    }

    /** Walks up to the player once, then stays there (a character coming over at the end of a scene). */
    public void approachAndStay(UUID player) {
        this.approachTarget = player;
        this.approachThenStay = true;
    }

    @Nullable
    public UUID approachTarget() {
        return approachTarget;
    }

    /** "idle", "work" (busy with the held item at home) or "patrol" (walks the route). */
    public void setActivity(String activity, List<BlockPos> route) {
        this.activity = activity == null ? "idle" : activity;
        this.route.clear();
        this.route.addAll(route);
    }

    public String activity() {
        return activity;
    }

    /** Lies down in the bed at pos (an actor asleep at the start of a scene). */
    public void sleepIn(BlockPos pos) {
        getNavigation().stop();
        startSleeping(pos);
    }

    /** Turns to look at a point for a while, e.g. towards the hills a character is worried about. */
    public void lookTowards(Vec3 pos, int ticks) {
        this.lookPos = pos;
        this.lookTicks = ticks;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || isSleeping();
    }

    public void setTalkTarget(@Nullable UUID player) {
        this.talkTarget = player;
        entityData.set(TALKING, player != null);
        if (player != null && followTarget == null) getNavigation().stop(); // a companion keeps walking while talking
    }

    @Nullable
    private Player player(@Nullable UUID id) {
        if (id == null || !(level() instanceof ServerLevel)) return null;
        Player p = level().getPlayerByUUID(id);
        return p != null && p.isAlive() && !p.isSpectator() ? p : null;
    }

    // --- behaviour ------------------------------------------------------------------

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && player instanceof ServerPlayer sp) {
            StoryManager.get().onNpcInteract(sp, this);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Character", characterId());
        if (home != null) tag.putIntArray("Home", new int[] {home.getX(), home.getY(), home.getZ()});
        if (followTarget != null) tag.putUUID("Follow", followTarget);
        if (sceneId != null) tag.putString("Scene", sceneId);
        tag.putString("Activity", activity);
        ListTag r = new ListTag();
        for (BlockPos pos : route) r.add(NbtUtils.writeBlockPos(pos));
        tag.put("Route", r);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Character")) setCharacter(tag.getString("Character"));
        int[] h = tag.getIntArray("Home");
        if (h.length == 3) home = new BlockPos(h[0], h[1], h[2]);
        followTarget = tag.hasUUID("Follow") ? tag.getUUID("Follow") : null;
        sceneId = tag.contains("Scene") ? tag.getString("Scene") : null;
        activity = tag.contains("Activity") ? tag.getString("Activity") : "idle";
        route.clear();
        for (Tag t : tag.getList("Route", Tag.TAG_INT_ARRAY)) {
            if (t instanceof net.minecraft.nbt.IntArrayTag a && a.getAsIntArray().length == 3) {
                int[] v = a.getAsIntArray();
                route.add(new BlockPos(v[0], v[1], v[2]));
            }
        }
    }

    /** Turns towards a point the story asked the character to look at. */
    private class LookAtPointGoal extends Goal {
        LookAtPointGoal() {
            setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return lookPos != null && lookTicks > 0 && talkTarget == null;
        }

        @Override
        public void tick() {
            if (lookPos != null) getLookControl().setLookAt(lookPos.x, lookPos.y, lookPos.z, 20.0F, 20.0F);
            if (--lookTicks <= 0) lookPos = null;
        }
    }

    /** Walks between the points of a route, pausing at each (guards on their rounds). */
    private class PatrolGoal extends Goal {
        private int index;
        private int pause;

        PatrolGoal() {
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return "patrol".equals(activity) && route.size() >= 2 && followTarget == null && approachTarget == null;
        }

        @Override
        public void tick() {
            BlockPos target = route.get(index % route.size());
            Vec3 t = Vec3.atBottomCenterOf(target);
            if (position().distanceTo(t) < 1.6) {
                getNavigation().stop();
                if (pause <= 0) pause = 40 + getRandom().nextInt(60);
                if (--pause <= 0) index = (index + 1) % route.size();
            } else if (getNavigation().isDone()) {
                getNavigation().moveTo(t.x, t.y, t.z, 0.55);
            }
        }

        @Override
        public void stop() {
            getNavigation().stop();
        }
    }

    /** Busy at their spot: swings the tool in hand at the work in front of them. */
    private class WorkGoal extends Goal {
        private int next;

        WorkGoal() {
            setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return "work".equals(activity) && followTarget == null && talkTarget == null && home != null
                    && position().distanceTo(Vec3.atBottomCenterOf(home)) < 2.5;
        }

        @Override
        public void tick() {
            Vec3 ahead = position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.2));
            getLookControl().setLookAt(ahead.x, getY() + 0.2, ahead.z, 10.0F, 40.0F);
            if (--next <= 0) {
                swing(InteractionHand.MAIN_HAND);
                next = 18 + getRandom().nextInt(40);
            }
        }
    }

    /** Stands still and faces the player while speaking (a companion walking alongside keeps walking). */
    private class TalkGoal extends Goal {
        TalkGoal() {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return player(talkTarget) != null && followTarget == null;
        }

        @Override
        public void start() {
            getNavigation().stop();
        }

        @Override
        public void tick() {
            Player p = player(talkTarget);
            if (p != null) getLookControl().setLookAt(p, 30.0F, 30.0F);
        }
    }

    /** Walks up to a player to start an important conversation. */
    private class ApproachGoal extends Goal {
        private int repath;

        ApproachGoal() {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            Player p = player(approachTarget);
            if (p != null && approachThenStay && distanceTo(p) <= 3.0F) {
                approachTarget = null;
                approachThenStay = false;
                home = blockPosition();
                return false;
            }
            return p != null && distanceTo(p) > 2.5F;
        }

        @Override
        public void tick() {
            Player p = player(approachTarget);
            if (p == null) return;
            getLookControl().setLookAt(p, 30.0F, 30.0F);
            if (approachThenStay && distanceTo(p) <= 3.0F) {
                approachTarget = null;
                approachThenStay = false;
                home = blockPosition();
                getNavigation().stop();
                return;
            }
            if (--repath <= 0) {
                repath = 8;
                getNavigation().moveTo(p, 0.75);
            }
        }

        @Override
        public void stop() {
            getNavigation().stop();
        }
    }

    /** Companion behaviour: keeps up with the player, catching up if left far behind. */
    private class FollowGoal extends Goal {
        private int repath;

        FollowGoal() {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            Player p = player(followTarget);
            return p != null && p.level() == level() && distanceTo(p) > 3.5F;
        }

        @Override
        public boolean canContinueToUse() {
            Player p = player(followTarget);
            return p != null && p.level() == level() && distanceTo(p) > 2.2F;
        }

        @Override
        public void tick() {
            Player p = player(followTarget);
            if (p == null) return;
            getLookControl().setLookAt(p, 10.0F, getMaxHeadXRot());
            if (distanceTo(p) > 24) {
                Vec3 behind = p.position().subtract(p.getLookAngle().multiply(2.5, 0, 2.5));
                teleportTo(behind.x, p.getY(), behind.z);
                getNavigation().stop();
                return;
            }
            if (--repath <= 0) {
                repath = 10;
                getNavigation().moveTo(p, distanceTo(p) > 9 ? 1.25 : 0.9);
            }
        }

        @Override
        public void stop() {
            getNavigation().stop();
        }
    }

    /** Returns home when not busy, and occasionally strolls a few steps around it. */
    private class HomeGoal extends Goal {
        @Nullable private Vec3 target;

        HomeGoal() {
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (home == null || followTarget != null || ("patrol".equals(activity) && route.size() >= 2)) return false;
            Vec3 h = Vec3.atBottomCenterOf(home);
            if (position().distanceTo(h) > 2.5) {
                target = h;
                return true;
            }
            if ("idle".equals(activity) && getRandom().nextInt(500) == 0) {
                target = h.add(getRandom().nextInt(5) - 2, 0, getRandom().nextInt(5) - 2);
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            if (target != null) getNavigation().moveTo(target.x, target.y, target.z, 0.6);
        }

        @Override
        public boolean canContinueToUse() {
            return !getNavigation().isDone() && followTarget == null;
        }
    }
}
