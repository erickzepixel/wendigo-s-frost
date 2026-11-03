package com.exemplo.wendigosfrost.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import javax.annotation.Nullable;

import java.util.List;

/**
 * Offline interactive Manual do Sobrevivente.
 *
 * Unlike a WrittenBookItem with pre-generated NBT, this opens directly on
 * right click even for /give, Creative inventory and older saved items.
 * The screen is loaded client-only so dedicated Forge servers remain safe.
 */
public final class GuideBookItem extends Item {
    public GuideBookItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack=player.getItemInHand(hand);
        if (level.isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> com.exemplo.wendigosfrost.client.SurvivorManualScreen.open());
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.wendigosfrost.guide_open"));
        lines.add(Component.translatable("tooltip.wendigosfrost.guide_chapters"));
        super.appendHoverText(stack,level,lines,flag);
    }
}
