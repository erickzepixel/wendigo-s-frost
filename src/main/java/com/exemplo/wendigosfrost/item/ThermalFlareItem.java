package com.exemplo.wendigosfrost.item;

import com.exemplo.wendigosfrost.entity.WendigoEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Short-range flame pulse; creates a fair window to interrupt an icy attack. */
public final class ThermalFlareItem extends Item {
    public ThermalFlareItem(Properties props) { super(props); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this))
            return InteractionResultHolder.fail(stack);
        if (!(level instanceof ServerLevel server))
            return InteractionResultHolder.sidedSuccess(stack, true);

        server.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY()+1.1, player.getZ(),
            105, 1.5, 1.0, 1.5, .08);
        server.sendParticles(ParticleTypes.LAVA, player.getX(), player.getY()+.6, player.getZ(),
            22, 1.5, .5, 1.5, .03);
        server.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE,
            SoundSource.PLAYERS, 1f, .72f);
        for (WendigoEntity boss : server.getEntitiesOfClass(WendigoEntity.class,
                player.getBoundingBox().inflate(11), e -> e.isAlive())) {
            if (boss.distanceToSqr(player) <= 121) boss.setSecondsOnFire(6);
        }
        player.getCooldowns().addCooldown(this, 20*24);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResultHolder.consume(stack);
    }
}
