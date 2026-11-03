package com.exemplo.wendigosfrost.item;

import com.exemplo.wendigosfrost.entity.WendigoEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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

/** A reusable hunter lantern that creates a short escape window. */
public final class RunicTorchItem extends Item {
    public RunicTorchItem(Item.Properties props) { super(props); }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack=player.getItemInHand(hand);
        if(player.getCooldowns().isOnCooldown(this))
            return InteractionResultHolder.fail(stack);
        if(level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, player.getX(),
                player.getY()+1.0,player.getZ(),70,1.0,.8,1.0,.05);
            server.sendParticles(ParticleTypes.END_ROD, player.getX(),player.getY()+1.0,
                player.getZ(),27,.9,.9,.9,.023);
            server.playSound(null,player.blockPosition(),SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS,.9f,.7f);
            for(WendigoEntity boss:server.getEntitiesOfClass(WendigoEntity.class,
                    player.getBoundingBox().inflate(10),e->e.isAlive())) {
                if(boss.distanceToSqr(player)<=100) {
                    boss.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20*5,2));
                    boss.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,20*5,0));
                    boss.getNavigation().stop();
                }
            }
            player.getCooldowns().addCooldown(this,20*45);
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
}
