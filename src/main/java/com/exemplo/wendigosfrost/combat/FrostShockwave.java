package com.exemplo.wendigosfrost.combat;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;

/** One shared Frost Shockwave for both the Wendigo and its Frost Ward artefact. */
public final class FrostShockwave {
    private FrostShockwave() {}

    public static int release(ServerLevel level, LivingEntity caster, double radius,
                              float damage, boolean affectPlayers) {
        level.playSound(null, caster.blockPosition(), SoundEvents.GENERIC_EXPLODE,
            caster instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1.0f, .75f);

        // Dense ring at ground height, plus central burst. Visible even with minimal particle settings.
        for (int i = 0; i < 64; i++) {
            double theta = i * Math.PI * 2.0 / 64.0;
            double r = radius * (.88 + .12 * (i % 3) / 2.0);
            level.sendParticles(ParticleTypes.SNOWFLAKE,
                caster.getX() + Math.cos(theta) * r, caster.getY() + .22,
                caster.getZ() + Math.sin(theta) * r, 2, .12, .24, .12, .03);
            if (i % 2 == 0)
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    caster.getX() + Math.cos(theta) * r * .58, caster.getY() + .55,
                    caster.getZ() + Math.sin(theta) * r * .58, 1, .07, .19, .07, .009);
        }
        level.sendParticles(ParticleTypes.CLOUD, caster.getX(), caster.getY() + .35,
            caster.getZ(), 70, 1.1, .25, 1.1, .13);
        level.sendParticles(ParticleTypes.SNOWFLAKE, caster.getX(), caster.getY() + 1.1,
            caster.getZ(), 90, 2.0, 1.0, 2.0, .16);
        int affected = 0;
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class,
                caster.getBoundingBox().inflate(radius),
                e -> e != caster && e.isAlive() && (affectPlayers ? e instanceof Player : e instanceof Monster))) {
            if (victim.distanceToSqr(caster) > radius * radius) continue;
            if (damage > 0) victim.hurt(caster instanceof net.minecraft.world.entity.Mob mob
                ? caster.damageSources().mobAttack(mob)
                : caster.damageSources().playerAttack((Player) caster), damage);
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 65, 0));
            victim.knockback(1.05, caster.getX() - victim.getX(),
                caster.getZ() - victim.getZ());
            affected++;
        }
        return affected;
    }
}
