package com.exemplo.wendigosfrost.entity;

import com.exemplo.wendigosfrost.config.ModConfig;
import com.exemplo.wendigosfrost.data.WendigoMark;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Nightfall: three-phase encounter, visible charge-up, no block griefing.
 * All damage is server authoritative; client only renders synced cues/animations.
 */
public class WendigoEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Integer> PHASE =
        SynchedEntityData.defineId(WendigoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CUE =
        SynchedEntityData.defineId(WendigoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RISE_UNTIL =
        SynchedEntityData.defineId(WendigoEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossBar = new ServerBossEvent(
        Component.translatable("entity.wendigosfrost.wendigo"),
        BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10);
    private UUID ownerId;
    private int skillCooldown = 100;
    private int chargeTicks = 0;
    private int chargingSkill = 0;
    private int ambientCooldown = 0;
    private boolean configuredAttributes = false;
    // Client-only render phase interpolation, measured in the entity's own tick time.
    // The legacy RISE_UNTIL server field is preserved for combat/attack safety.
    private int clientRiseStartTick = -10000;
    public float getStandBlend(float partialTick) {
        if (getBossPhase() <= 1) return 0f;
        if (clientRiseStartTick < 0) return 1f; // Loaded mid-fight.
        float progress = Math.max(0f, Math.min(1f,
            (tickCount + partialTick - clientRiseStartTick) / 36f));
        return progress * progress * (3f - 2f * progress);
    }


    public WendigoEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 65;
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(PHASE, 1);
        entityData.define(CUE, 0);
        entityData.define(RISE_UNTIL, 0);
    }
    public int getBossPhase() { return entityData.get(PHASE); }
    public boolean isQuadruped() { return getBossPhase() == 1; }
    private boolean isRising() { return getBossPhase() >= 2 && tickCount < entityData.get(RISE_UNTIL); }
    @Override public EntityDimensions getDimensions(Pose pose) {
        return isQuadruped() ? EntityDimensions.scalable(2.75F, 2.85F) : super.getDimensions(pose);
    }
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (PHASE.equals(key)) {
            refreshDimensions();
            if (level().isClientSide && getBossPhase() >= 2 && clientRiseStartTick < 0)
                clientRiseStartTick = tickCount;
        }
    }
    public int getAttackCue() { return entityData.get(CUE); }
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID owner) { ownerId = owner; }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 260)
            .add(Attributes.ATTACK_DAMAGE, 16)
            .add(Attributes.ARMOR, 8)
            .add(Attributes.MOVEMENT_SPEED, 0.34)
            .add(Attributes.FOLLOW_RANGE, 48)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.88);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 7, anim -> {
            // First form stalks on all fours. The second form stands upright
            // after a short, dedicated rise-up sequence synchronized to the server.
            if (isRising())
                return anim.setAndContinue(RawAnimation.begin().thenPlay("animation.wendigo.rise"));
            if (getAttackCue() == 1)
                return anim.setAndContinue(RawAnimation.begin().thenLoop(isQuadruped()
                    ? "animation.wendigo.quad_roar" : "animation.wendigo.roar"));
            if (getAttackCue() == 2)
                return anim.setAndContinue(RawAnimation.begin().thenLoop("animation.wendigo.stomp"));
            boolean moving = anim.isMoving();
            String clip = isQuadruped()
                ? (moving ? "animation.wendigo.quad_walk" : "animation.wendigo.quad_idle")
                : (moving ? "animation.wendigo.walk" : "animation.wendigo.idle");
            return anim.setAndContinue(RawAnimation.begin().thenLoop(clip));
        }));
        controllers.add(new AnimationController<>(this, "attack", 0, anim -> {
            if (!isRising() && this.swinging && getAttackCue() == 0)
                return anim.setAndContinue(RawAnimation.begin().thenPlay("animation.wendigo.attack"));
            return PlayState.STOP;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animationCache; }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.18, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, .65));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossBar.addPlayer(player);
    }
    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        bossBar.removePlayer(player);
        super.stopSeenByPlayer(player);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel server)) return;
        // Read config only after world/registry initialization, and preserve health percentage on reload.
        if (!configuredAttributes) {
            configuredAttributes = true;
            float hp = getHealth() / Math.max(1, getMaxHealth());
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(ModConfig.WENDIGO_HEALTH.get());
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ModConfig.WENDIGO_DAMAGE.get());
            setHealth(Math.max(1f, Math.min(getMaxHealth(), hp * getMaxHealth())));
        }
        if (ownerId != null) {
            Player owner = level().getPlayerByUUID(ownerId);
            if (owner != null && !WendigoMark.isMarked(owner)) {
                discard();
                return;
            }
        }

        float fraction = getHealth() / Math.max(1, getMaxHealth());
        int nextPhase = fraction > .65f ? 1 : fraction > .30f ? 2 : 3;
        if (getBossPhase() != nextPhase) {
            if (getBossPhase() == 1 && nextPhase >= 2)
                entityData.set(RISE_UNTIL, tickCount + 36);
            entityData.set(PHASE, nextPhase);
            bossBar.setColor(nextPhase == 1 ? BossEvent.BossBarColor.BLUE :
                nextPhase == 2 ? BossEvent.BossBarColor.PURPLE : BossEvent.BossBarColor.RED);
            server.playSound(null, blockPosition(), SoundEvents.WARDEN_ROAR,
                SoundSource.HOSTILE, 1.7f, .55f + nextPhase * .1f);
            server.sendParticles(ParticleTypes.SOUL, getX(), getY() + 2.5, getZ(),
                55, 1.4, 1.5, 1.4, .04);
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.31 + nextPhase * .035);
            for (ServerPlayer p : server.getPlayers(q -> q.distanceToSqr(this) < 45 * 45)) {
                p.displayClientMessage(Component.translatable("message.wendigosfrost.phase" + nextPhase), true);
            }
        }
        bossBar.setProgress(Math.max(0f, Math.min(1f, fraction)));
        if (isRising()) {
            getNavigation().stop();
            setDeltaMovement(0, getDeltaMovement().y, 0);
            return;
        }

        if (ambientCooldown-- <= 0) {
            ambientCooldown = 150 + getRandom().nextInt(110);
            server.playSound(null, blockPosition(), SoundEvents.WARDEN_HEARTBEAT,
                SoundSource.HOSTILE, .8f, .64f);
            server.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 2, getZ(),
                14, 1.2, 1.5, 1.2, .025);
        }

        if (isOnFire()) { // fire remains a deliberate counterplay tool
            if (chargeTicks > 0) cancelSkill();
            return;
        }
        if (!ModConfig.ENABLE_BOSS_SKILLS.get()) return;
        if (chargeTicks > 0) {
            getNavigation().stop();
            setDeltaMovement(getDeltaMovement().multiply(.38, 1, .38));
            if (chargeTicks % 5 == 0) {
                double r = chargingSkill == 1 ? 10 : 6;
                for (int i = 0; i < 20; i++) {
                    double a = i * Math.PI * 2 / 20;
                    server.sendParticles(chargingSkill == 1 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SNOWFLAKE,
                        getX() + Math.cos(a)*r, getY() + .22, getZ() + Math.sin(a)*r,
                        1, 0, 0, 0, 0);
                }
            }
            if (--chargeTicks == 0) {
                if (chargingSkill == 1) castRoar(server);
                else castStomp(server);
                cancelSkill();
            }
            return;
        }
        if (skillCooldown > 0) skillCooldown--;
        if (skillCooldown == 0 && getTarget() instanceof Player target &&
            target.isAlive() && distanceToSqr(target) < 17 * 17) {
            chargingSkill = getBossPhase() >= 2 && distanceToSqr(target) < 9 * 9 ? 2 : 1;
            chargeTicks = 34; // player can retreat/dodge before any AoE damage
            entityData.set(CUE, chargingSkill);
            skillCooldown = Math.max(96, 240 - getBossPhase() * 42);
            server.playSound(null, blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.HOSTILE, 1.0f, .72f);
            for (ServerPlayer p : server.getPlayers(q -> q.distanceToSqr(this) < 28 * 28)) {
                p.displayClientMessage(Component.translatable(chargingSkill == 1 ?
                    "message.wendigosfrost.roar_warning" : "message.wendigosfrost.stomp_warning"), true);
            }
        }
    }

    private void cancelSkill() {
        chargeTicks = 0;
        chargingSkill = 0;
        entityData.set(CUE, 0);
    }

    private void castRoar(ServerLevel server) {
        server.playSound(null, blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 1.5f, .7f);
        server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 2, getZ(),
            125, 5, 1.5, 5, .05);
        for (ServerPlayer player : server.getPlayers(p -> p.isAlive() && p.distanceToSqr(this) < 10 * 10)) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 110, getBossPhase() == 3 ? 1 : 0));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 95, 0));
        }
    }

    private void castStomp(ServerLevel server) {
        server.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.15f, .66f);
        server.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + .6, getZ(),
            150, 5.2, .7, 5.2, .08);
        for (ServerPlayer player : server.getPlayers(p -> p.isAlive() && p.distanceToSqr(this) < 6 * 6)) {
            player.hurt(damageSources().mobAttack(this), 5 + 2 * getBossPhase());
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 75, 1));
            player.knockback(1.15, this.getX() - player.getX(), this.getZ() - player.getZ());
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity victim) {
        if (isRising()) return false;
        boolean hit = super.doHurtTarget(victim);
        if (hit && victim instanceof Player player && getBossPhase() >= 2) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 42, 0));
        }
        return hit;
    }
    @Override public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, source.is(DamageTypeTags.IS_FIRE) ? amount * 1.8f : amount);
    }
    @Override protected boolean shouldDespawnInPeaceful() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerId != null) tag.putUUID("MarkedOwner", ownerId);
        tag.putInt("BossPhase", getBossPhase());
        tag.putInt("SkillCooldown", skillCooldown);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("MarkedOwner")) ownerId = tag.getUUID("MarkedOwner");
        skillCooldown = tag.contains("SkillCooldown") ? tag.getInt("SkillCooldown") : 100;
        entityData.set(PHASE, tag.contains("BossPhase") ? tag.getInt("BossPhase") : 1);
        entityData.set(RISE_UNTIL, 0);
        refreshDimensions();
        cancelSkill(); // Never resume a half-finished attack after a server restart.
    }
}
