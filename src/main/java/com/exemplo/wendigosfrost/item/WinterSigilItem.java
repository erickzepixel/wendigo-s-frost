package com.exemplo.wendigosfrost.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

/** Mid-game exploration tool crafted from relics. */
public final class WinterSigilItem extends Item {
    public WinterSigilItem(Properties props) { super(props); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        if (player instanceof ServerPlayer server) {
            server.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20*30, 0));
            server.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20*8, 0));
            server.serverLevel().sendParticles(ParticleTypes.SNOWFLAKE,
                server.getX(), server.getY()+1, server.getZ(), 45, .7, .8, .7, .025);
            server.serverLevel().playSound(null, server.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS, .8f, .65f);
            server.displayClientMessage(Component.translatable("message.wendigosfrost.sigil"), true);
            server.getCooldowns().addCooldown(this, 20*90);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.wendigosfrost.winter_sigil").withStyle(ChatFormatting.AQUA));
    }
}
