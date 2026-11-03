package com.exemplo.wendigosfrost.combat;

import com.exemplo.wendigosfrost.entity.WendigoEntity;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Gameplay-readable server-authoritative VFX for the Wendigo's three attacks.
 * All loops have a hard particle budget. The effects are cosmetics: damage
 * happens only in existing server combat methods and cannot be spoofed by clients.
 */
public final class WendigoAttackParticles {
    private static final BlockParticleOption ICE_SHARD =
        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.PACKED_ICE.defaultBlockState());
    private WendigoAttackParticles() {}

    /** A readable charging boundary: smaller cyan pulses before the impact. */
    public static void telegraph(ServerLevel world, WendigoEntity boss,
                                 int ability, int remainingTicks) {
        double radius = ability == 1 ? 8.0 : 5.5;
        int count = 28;
        double spin = remainingTicks * .06;
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2 * i / count + spin;
            double x = boss.getX() + Math.cos(angle) * radius;
            double z = boss.getZ() + Math.sin(angle) * radius;
            world.sendParticles(ability == 1 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SNOWFLAKE,
                x, boss.getY() + .22, z, 1, 0, .035, 0, .01);
        }
        // Rising ice means a stomp; rising souls mean a roar.
        world.sendParticles(ability == 1 ? ParticleTypes.SOUL : ParticleTypes.END_ROD,
            boss.getX(), boss.getY() + (boss.isQuadruped() ? 1.1 : 2.6),
            boss.getZ(), 6, .65, .75, .65, .025);
    }

    /** Three curved frost scratch trails in front of the beast. */
    public static void claw(ServerLevel world, WendigoEntity boss, Entity target) {
        Vec3 toTarget = target.position().subtract(boss.position());
        double len = Math.max(.001, Math.hypot(toTarget.x, toTarget.z));
        double rightX = -toTarget.z / len, rightZ = toTarget.x / len;
        double frontX = toTarget.x / len, frontZ = toTarget.z / len;
        double originX = target.getX() - frontX * .3;
        double originZ = target.getZ() - frontZ * .3;
        double top = target.getY() + Math.min(2.3, target.getBbHeight() * .87);
        for (int stroke = -1; stroke <= 1; stroke++) {
            for (int step = 0; step < 9; step++) {
                double t = step / 8.0;
                double across = (t - .5) * 2.15 + stroke * .36;
                double y = top - t * 1.35 - Math.sin(t * Math.PI) * .12;
                world.sendParticles(ParticleTypes.SNOWFLAKE,
                    originX + rightX * across, y, originZ + rightZ * across,
                    1, .025, .025, .025, .006);
            }
        }
        world.sendParticles(ParticleTypes.SWEEP_ATTACK,
            target.getX(), target.getY() + target.getBbHeight() * .57,
            target.getZ(), 2, .25, .3, .25, .01);
        world.sendParticles(ICE_SHARD,
            target.getX(), target.getY() + .6, target.getZ(),
            13, .38, .55, .38, .07);
    }

    /** A visible wave of frozen mist and soul fire radiating from the roar. */
    public static void roar(ServerLevel world, WendigoEntity boss) {
        double originY = boss.getY() + (boss.isQuadruped() ? 1.65 : 3.10);
        Vec3 forward = boss.getLookAngle();
        double base = Math.atan2(forward.z, forward.x);
        for (int ring = 1; ring <= 4; ring++) {
            double distance = ring * 2.25;
            for (int i = -8; i <= 8; i++) {
                double angle = base + i * .105;
                double x = boss.getX() + distance * Math.cos(angle);
                double z = boss.getZ() + distance * Math.sin(angle);
                double y = originY + (ring - 2) * .11;
                world.sendParticles(i % 3 == 0 ? ParticleTypes.SOUL_FIRE_FLAME
                    : ParticleTypes.SNOWFLAKE, x, y, z, 2, .08, .28, .08, .045);
            }
        }
        for (int i=1;i<=3;i++) {
            world.sendParticles(ParticleTypes.SONIC_BOOM,
                boss.getX() + forward.x * i * 1.5,
                originY,
                boss.getZ() + forward.z * i * 1.5,
                1, 0, 0, 0, 0);
        }
        world.sendParticles(ParticleTypes.CLOUD,
            boss.getX(), originY, boss.getZ(), 45, 1.2, .55, 1.2, .08);
    }

    /** Spawned along the shockwave for a short-lived expanding ring of ice fragments. */
    public static void aftershock(ServerLevel world, WendigoEntity boss, int ticksRemaining) {
        if (ticksRemaining % 2 != 0) return;
        double radius = 5.5 * (12 - ticksRemaining + 1) / 12.0;
        for (int i=0;i<24;i++) {
            double angle = (i * Math.PI * 2 / 24.0) + boss.tickCount * .10;
            double x = boss.getX() + Math.cos(angle) * radius;
            double z = boss.getZ() + Math.sin(angle) * radius;
            world.sendParticles(i%3==0 ? ParticleTypes.SNOWFLAKE : ICE_SHARD,
                x, boss.getY() + .16, z, 1, .04, .12, .04, .025);
        }
    }
}
