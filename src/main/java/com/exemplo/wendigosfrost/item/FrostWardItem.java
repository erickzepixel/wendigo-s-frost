package com.exemplo.wendigosfrost.item;

import com.exemplo.wendigosfrost.config.ModConfig;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Craftable counterplay tool: 6 charges, limited range and a per-player cooldown.
 * Blocks no projectiles, cannot remove the Frostbloom curse or skip the ritual.
 */
public final class FrostWardItem extends Item {
    public FrostWardItem(Properties properties) { super(properties); }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!ModConfig.ENABLE_FROST_WARD.get() || player.getCooldowns().isOnCooldown(this))
            return InteractionResultHolder.fail(stack);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(player instanceof ServerPlayer server)) return InteractionResultHolder.pass(stack);
        server.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 220, 0));
        server.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 0));
        server.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        server.removeEffect(MobEffects.WEAKNESS);
        // The ward creates an escape window rather than an effortless kill.
        for (WendigoEntity boss : server.serverLevel().getEntitiesOfClass(WendigoEntity.class,
                server.getBoundingBox().inflate(10))) {
            boss.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 90, 1));
        }
        server.serverLevel().sendParticles(ParticleTypes.SNOWFLAKE, server.getX(),
            server.getY()+1, server.getZ(), 70, 1.6, 1.5, 1.6, .07);
        server.serverLevel().playSound(null, server.blockPosition(), SoundEvents.BEACON_ACTIVATE,
            SoundSource.PLAYERS, .95f, 1.5f);
        player.getCooldowns().addCooldown(this, 20 * 30);
        if (!player.getAbilities().instabuild)
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        return InteractionResultHolder.consume(stack);
    }
}