package com.exemplo.wendigosfrost.entity;

import com.exemplo.wendigosfrost.config.ModConfig;
import com.exemplo.wendigosfrost.data.WendigoMark;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import java.util.UUID;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/** Boss hostil: caça jogadores próximos, preservando a ligação com a marca original. */
public class WendigoEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    public enum State { STALK, PATROL, CHASE, ATTACK, FLEE }
    private UUID ownerId;
    private State state = State.STALK;
    private int lostTicks = 0;
    private int soundCooldown = 0;
    private final ServerBossEvent bossBar = new ServerBossEvent(
            Component.translatable("entity.wendigosfrost.wendigo"),
            BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
    public WendigoEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        xpReward = 25;
        // Garante que o boss invocado manualmente não desapareça por distância.
        setPersistenceRequired();
    }
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID id) { ownerId = id; }
    public State getHuntState() { return state; }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 260)
                .add(Attributes.ATTACK_DAMAGE, 16)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.88);
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, anim -> anim.setAndContinue(
                RawAnimation.begin().thenLoop(anim.isMoving() ? "animation.wendigo.walk" : "animation.wendigo.idle"))));
        controllers.add(new AnimationController<>(this, "attack", 0, anim -> {
            if (this.swinging) return anim.setAndContinue(RawAnimation.begin().thenPlay("animation.wendigo.attack"));
            return PlayState.STOP;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animationCache; }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
    @Override public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossBar.addPlayer(player);
    }
    @Override public void stopSeenByPlayer(ServerPlayer player) {
        bossBar.removePlayer(player);
        super.stopSeenByPlayer(player);
    }
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        bossBar.setProgress(Math.max(0.0f, Math.min(1.0f, getHealth() / getMaxHealth())));
        if (soundCooldown > 0) soundCooldown--;
        if (ownerId != null) {
            Player owner = level().getPlayerByUUID(ownerId);
            if (owner != null && !WendigoMark.isMarked(owner)) {
                discard(); // A cerimônia retirou a marca.
                return;
            }
        }
        if (isOnFire()) {
            state = State.FLEE;
            setTarget(null);
            return;
        }
        var target = getTarget();
        if (target != null && target.isAlive()) {
            state = distanceToSqr(target) <= 20.25 ? State.ATTACK : State.CHASE;
            if (soundCooldown == 0) {
                level().playSound(null, blockPosition(), SoundEvents.WARDEN_NEARBY_CLOSE, SoundSource.HOSTILE, 0.55f, 0.65f);
                soundCooldown = 240;
            }
        } else {
            state = State.PATROL;
        }
    }
    @Override public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) ? amount * 1.8f : amount);
    }
    // Boss invocado por comando ou ritual continua no mundo até ser derrotado ou dispensado.
    @Override protected boolean shouldDespawnInPeaceful() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerId != null) tag.putUUID("MarkedOwner", ownerId);
        tag.putString("HuntState", state.name());
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("MarkedOwner")) ownerId = tag.getUUID("MarkedOwner");
        try { state = State.valueOf(tag.getString("HuntState")); }
        catch (IllegalArgumentException ignored) { state = State.STALK; }
    }
}
