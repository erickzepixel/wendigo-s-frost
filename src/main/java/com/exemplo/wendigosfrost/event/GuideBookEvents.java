package com.exemplo.wendigosfrost.event;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.item.GuideBookItem;
import com.exemplo.wendigosfrost.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Oferece o manual uma unica vez por personagem, sem duplicar a cada login. */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID)
public final class GuideBookEvents {
    private static final String GIVEN_KEY = "WendigosFrostGuideGiven";
    private GuideBookEvents() {}
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.getPersistentData().getBoolean(
                com.exemplo.wendigosfrost.network.ModNetwork.READ_KEY)) {
            com.exemplo.wendigosfrost.network.ModNetwork.sendIntroduction(player);
        }
        if (player.getPersistentData().getBoolean(GIVEN_KEY)) return;
        ItemStack guide = new ItemStack(ModItems.GUIDE_BOOK.get());
        GuideBookItem.prepare(guide);
        if (!player.getInventory().add(guide)) player.drop(guide, false);
        player.getPersistentData().putBoolean(GIVEN_KEY, true);
    }
}