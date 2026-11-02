package com.exemplo.wendigosfrost.item;

import com.exemplo.wendigosfrost.data.WendigoMark;
import com.exemplo.wendigosfrost.config.ModConfig;
import com.exemplo.wendigosfrost.registry.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Consumível inicial: alimenta e grava/reinicia a marca no servidor. */
public class FrostbloomCrystalItem extends Item {
    public FrostbloomCrystalItem(Properties properties) { super(properties); }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            WendigoMark.apply(player);
            int duration = ModConfig.DELIRIUM_SECONDS.get() * 20;
            player.addEffect(new MobEffectInstance(ModEffects.FROST_DELIRIUM.get(), duration));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 0));
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.wendigosfrost.marked"), true);
        }
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return super.use(level, player, hand);
    }
}
