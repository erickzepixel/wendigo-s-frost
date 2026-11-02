package com.exemplo.wendigosfrost.entity;

import com.exemplo.wendigosfrost.config.ModConfig;
import com.exemplo.wendigosfrost.data.WendigoMark;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
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
import software.bernie.geckolib.util.GeckoLibUtil;

/** Só caça seu jogador marcado. Estados servidor: espreita, patrulha, caça, ataque, fuga. */
public class WendigoEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    public enum State { STALK, PATROL, CHASE, ATTACK, FLEE }
    private UUID ownerId;
    private State state = State.STALK;
    private int lostTicks = 0;
    private int soundCooldown = 0;
    public WendigoEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        xpReward = 25;
    }
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID id) { ownerId = id; }
    public State getHuntState() { return state; }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100)
                .add(Attributes.ATTACK_DAMAGE, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, state -> state.setAndContinue(
                RawAnimation.begin().thenLoop(state.isMoving() ? "animation.wendigo.walk" : "animation.wendigo.idle"))));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return animationCache; }
    @Override protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        // Não registra NearestAttackableTargetGoal: nunca ataca outros jogadores.
    }
    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide || ownerId == null) return;
        if (soundCooldown > 0) soundCooldown--;
        Player owner = level().getPlayerByUUID(ownerId);
        if (owner != null && !WendigoMark.isMarked(owner)) {
            discard(); // Ritual completado mesmo se o monstro estava fora da área de 128 blocos.
            return;
        }
        if (isOnFire()) {
            state = State.FLEE;
            setTarget(null);
            if (owner != null) getNavigation().moveTo(getX() + (getX()-owner.getX()), getY(), getZ() + (getZ()-owner.getZ()), 1.5);
            return;
        }
        if (owner == null || !owner.isAlive()) {
            state = State.PATROL;
            setTarget(null);
            return;
        }
        double distSq = distanceToSqr(owner);
        double perception = ModConfig.PERCEPTION_RANGE.get();
        if (distSq <= perception * perception && hasLineOfSight(owner)) {
            lostTicks = 0;
            state = distSq <= 7 ? State.ATTACK : State.CHASE;
            setTarget(owner);
            if (soundCooldown <= 0) {
                level().playSound(null, blockPosition(), SoundEvents.WARDEN_NEARBY_CLOSE, SoundSource.HOSTILE, 0.55f, 0.65f);
                soundCooldown = 240;
            }
        } else if (getTarget() != null && ++lostTicks < 300) {
            state = State.CHASE;
            // Persegue por até 15 segundos após perder o contato visual.
        } else {
            setTarget(null);
            state = distSq <= 45 * 45 ? State.STALK : State.PATROL;
            lostTicks = 0;
            // Aproximação silenciosa sem atacar fora do alcance de percepção.
            if (state == State.STALK && tickCount % 100 == 0)
                getNavigation().moveTo(owner.getX(), owner.getY(), owner.getZ(), 0.58);
        }
    }
    @Override public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) ? amount * 1.8f : amount);
    }
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
