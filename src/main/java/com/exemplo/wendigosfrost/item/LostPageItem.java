package com.exemplo.wendigosfrost.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

/** A survivor's lost journal, obtained in ancient ruins. */
public final class LostPageItem extends Item {
    public LostPageItem(Properties props) { super(props); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("message.wendigosfrost.page_1"), false);
            player.displayClientMessage(Component.translatable("message.wendigosfrost.page_2"), false);
            player.displayClientMessage(Component.translatable("message.wendigosfrost.page_3"), false);
            level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN,
                SoundSource.PLAYERS, .85f, .9f);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag tooltip) {
        lines.add(Component.translatable("tooltip.wendigosfrost.lost_page").withStyle(ChatFormatting.GRAY));
    }
}
