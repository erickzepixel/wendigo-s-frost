package com.exemplo.wendigosfrost.item;

import com.exemplo.wendigosfrost.combat.FrostShockwave;
import com.exemplo.wendigosfrost.config.ModConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Frost Ward can RELEASE the Wendigo's Frost Shockwave, not just grant buffs.
 * Six charges, 30 second cooldown: modest AoE damage, knockback and chill.
 * It protects the owner and never applies the permanent Frostbloom curse.
 */
public final class FrostWardItem extends Item {
    public FrostWardItem(Properties properties) { super(properties); }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // enchanted glimmer over the new icy rune texture
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!ModConfig.ENABLE_FROST_WARD.get() || player.getCooldowns().isOnCooldown(this))
            return InteractionResultHolder.fail(stack);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(player instanceof ServerPlayer server)) return InteractionResultHolder.pass(stack);

        // The very same shockwave visual and rules as Wendigo's second phase.
        // Player version hits monsters only, has smaller range and damage.
        int enemies = FrostShockwave.release(server.serverLevel(), server, 6.0, 4.0f, false);
        server.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 140, 0));
        server.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 120, 0));
        server.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        server.removeEffect(MobEffects.WEAKNESS);
        server.serverLevel().sendParticles(ParticleTypes.END_ROD, server.getX(), server.getY() + 1.25,
            server.getZ(), 38, 1.0, .85, 1.0, .05);
        server.serverLevel().playSound(null, server.blockPosition(), SoundEvents.BEACON_ACTIVATE,
            SoundSource.PLAYERS, .9f, 1.45f);

        player.getCooldowns().addCooldown(this, 20 * 30);
        if (!player.getAbilities().instabuild)
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        server.displayClientMessage(Component.translatable("message.wendigosfrost.ward_wave", enemies), true);
        return InteractionResultHolder.consume(stack);
    }
}
