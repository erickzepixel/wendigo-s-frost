package com.exemplo.wendigosfrost.ritual;

import com.exemplo.wendigosfrost.entity.WendigoEntity;
import com.exemplo.wendigosfrost.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Ritual de invocação alternativo à maldição de três dias.
 * Efeito instantâneo quando a estrutura está correta e o jogador ativa o pedestal.
 * Não exige chifre/coração, evitando travar a progressão atrás do próprio boss.
 */
public final class WendigoSummoningRitual {
    private static final int RADIUS = 64;
    private WendigoSummoningRitual() {}

    // Vista superior, mesma altura do pedestal:
    //   . . F . .
    //   . G . G .
    //   F . P . F
    //   . G . G .
    //   . . F . .
    // F = fogueira das almas acesa; G = gelo azul; P = pedestal.
    public static boolean isStructureValid(ServerLevel level, BlockPos center) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos campfire = center.relative(dir, 2);
            var state = level.getBlockState(campfire);
            if (!state.is(Blocks.SOUL_CAMPFIRE) || !state.getValue(CampfireBlock.LIT))
                return false;
        }
        for (int dx : new int[]{-1, 1}) {
            for (int dz : new int[]{-1, 1}) {
                if (!level.getBlockState(center.offset(dx, 0, dz)).is(Blocks.BLUE_ICE))
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns true if a boss was spawned. On failed validation or collision,
     * the offered crystal is never consumed. Does not modify the curse mark.
     */
    public static boolean activate(ServerPlayer player, BlockPos center, ItemStack offering,
                                   long cooldownEnd) {
        ServerLevel level = player.serverLevel();
        if (!isStructureValid(level, center)) {
            player.displayClientMessage(
                Component.translatable("message.wendigosfrost.summon_structure"), false);
            return false;
        }
        if (level.getGameTime() < cooldownEnd) {
            int seconds = (int)Math.ceil((cooldownEnd - level.getGameTime()) / 20.0);
            player.displayClientMessage(
                Component.translatable("message.wendigosfrost.summon_cooldown", seconds), false);
            return false;
        }
        if (!level.getEntitiesOfClass(WendigoEntity.class,
                new net.minecraft.world.phys.AABB(center).inflate(RADIUS)).isEmpty()) {
            player.displayClientMessage(
                Component.translatable("message.wendigosfrost.summon_existing"), false);
            return false;
        }

        WendigoEntity boss = ModEntities.WENDIGO.get().create(level);
        if (boss == null) {
            player.displayClientMessage(
                Component.translatable("message.wendigosfrost.summon_failed"), false);
            return false;
        }
        // Prefer visible nearby terrain, but never materialize the boss inside a
        // block or on an unloaded chunk. Large 1.95 x 4.5 hitbox checked by noCollision.
        boolean found = false;
        final int[][] offsets = {{8,0},{-8,0},{0,8},{0,-8},{6,6},{-6,6},{6,-6},{-6,-6}};
        for (int i = 0; i < 48; i++) {
            int dx = i < offsets.length ? offsets[i][0] : 6 + level.random.nextInt(7);
            int dz = i < offsets.length ? offsets[i][1] : level.random.nextInt(13) - 6;
            if (i >= offsets.length && (i % 2 == 1)) dx = -dx;
            BlockPos target = center.offset(dx, 0, dz);
            if (!level.hasChunkAt(target)) continue;
            BlockPos surface = level.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target);
            if (Math.abs(surface.getY() - center.getY()) > 7) continue;
            if (!level.getBlockState(surface.below()).isFaceSturdy(
                    level, surface.below(), Direction.UP)) continue;
            if (!level.getFluidState(surface).isEmpty()) continue;
            boss.moveTo(surface.getX() + .5, surface.getY(), surface.getZ() + .5,
                level.random.nextFloat() * 360, 0);
            if (!level.noCollision(boss)) continue;
            found = true;
            break;
        }
        if (!found) {
            player.displayClientMessage(
                Component.translatable("message.wendigosfrost.summon_space"), false);
            return false;
        }
        boss.setPersistenceRequired();
        // Ritual bosses are wild and hostile. No owner UUID is set, so this
        // summoning does not override the independent three-day curse mechanic.
        if (!level.addFreshEntity(boss)) {
            player.displayClientMessage(
                Component.translatable("message.wendigosfrost.summon_failed"), false);
            return false;
        }

        if (!player.getAbilities().instabuild) offering.shrink(1);
        level.playSound(null, center, SoundEvents.WARDEN_ROAR,
            SoundSource.HOSTILE, 2f, .6f);
        level.playSound(null, center, SoundEvents.END_PORTAL_SPAWN,
            SoundSource.HOSTILE, 1.3f, .8f);
        for (int i = 0; i < 80; i++) {
            double angle = 2 * Math.PI * i / 80.0;
            double radius = .65 + (i % 5) * .35;
            level.sendParticles(i % 3 == 0 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SNOWFLAKE,
                center.getX() + .5 + radius * Math.cos(angle),
                center.getY() + 1.4 + (i % 9) * .13,
                center.getZ() + .5 + radius * Math.sin(angle),
                1, 0, 0, 0, 0);
        }
        level.sendParticles(ParticleTypes.SOUL, boss.getX(), boss.getY() + 2.0,
            boss.getZ(), 85, 1.3, 1.5, 1.3, .03);
        for (ServerPlayer nearby : level.getPlayers(p -> p.distanceToSqr(
                center.getX() + .5, center.getY() + .5, center.getZ() + .5) <= 50 * 50)) {
            nearby.displayClientMessage(
                Component.translatable("message.wendigosfrost.summon_success"), true);
        }
        var advancement = level.getServer().getAdvancements().getAdvancement(
            new net.minecraft.resources.ResourceLocation("wendigosfrost", "summon_ritual"));
        if (advancement != null)
            player.getAdvancements().award(advancement, "invoked");
        return true;
    }
}
