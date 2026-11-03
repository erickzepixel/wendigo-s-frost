package com.exemplo.wendigosfrost.client;

import com.exemplo.wendigosfrost.WendigosFrost;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Wait for a usable client world and close any loading screen before showing lore. */
@Mod.EventBusSubscriber(modid=WendigosFrost.MOD_ID, value=Dist.CLIENT)
public final class LoreIntroQueue {
    private static boolean pending;
    private static int ticks;
    private LoreIntroQueue() {}

    public static void request() {
        pending = true;
        ticks = 0;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !pending) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.screen != null) return;
        if (++ticks < 12) return;
        pending = false;
        mc.setScreen(new LoreIntroScreen());
    }
}
