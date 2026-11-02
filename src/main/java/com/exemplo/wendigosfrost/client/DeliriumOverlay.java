package com.exemplo.wendigosfrost.client;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import com.exemplo.wendigosfrost.registry.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Lightweight immersive overlay; no fullscreen shader or costly per-frame entity scan.
 * Screen effect is purely cosmetic; all combat remains server-controlled.
 */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID, value = Dist.CLIENT)
public final class DeliriumOverlay {
    private static long lastScanTick = Long.MIN_VALUE;
    private static double nearestBoss = 999;
    private static int lastDimensionId = 0;
    private DeliriumOverlay() {}

    @SubscribeEvent
    public static void onHud(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;
        GuiGraphics gui = event.getGuiGraphics();
        int w = gui.guiWidth(), h = gui.guiHeight();
        long now = mc.player.tickCount;
        int dimensionId = mc.level.dimension().location().hashCode();
        if (lastDimensionId != dimensionId || now < lastScanTick || now - lastScanTick >= 15) {
            lastScanTick = now;
            lastDimensionId = dimensionId;
            nearestBoss = mc.level.getEntitiesOfClass(WendigoEntity.class,
                mc.player.getBoundingBox().inflate(40)).stream()
                .mapToDouble(b -> b.distanceTo(mc.player)).min().orElse(999);
        }
        boolean delirium = mc.player.hasEffect(ModEffects.FROST_DELIRIUM.get());
        boolean threatened = nearestBoss < 30;
        if (!delirium && !threatened) return;

        double pulse = .5 + .5 * Math.sin(now * (threatened ? .13 : .052));
        int intensity = delirium ? 16 + (int)(12*pulse) : 8 + (int)(10*pulse);
        if (threatened) intensity += (int)(24 * (1 - nearestBoss/30));
        int tint = (Math.min(66, intensity) << 24) | (threatened ? 0x5875AB : 0x5FC6E8);
        int edge = Math.max(8, Math.min(w, h)/32);
        for (int i=0; i<4; ++i) {
            int a = Math.max(3, (intensity - i*5));
            int c = (a << 24) | (threatened ? 0x355A91 : 0x6CCAE8);
            int e = edge * (4-i) / 4;
            gui.fill(0, 0, w, e, c);
            gui.fill(0, h-e, w, h, c);
            gui.fill(0, 0, e, h, c);
            gui.fill(w-e, 0, w, h, c);
        }
        if (threatened) {
            String danger = nearestBoss < 12 ? "O FRIO ESTÁ AQUI" : "VOCÊ NÃO ESTÁ SOZINHO";
            int color = nearestBoss < 12 ? 0xffb1e6 : 0x8fd3e6;
            gui.drawCenteredString(mc.font, danger, w/2, h-58, color);
        } else if (delirium && (now / 40) % 8 == 0) {
            gui.drawCenteredString(mc.font, Component.translatable("message.wendigosfrost.overlay"),
                w/2, h-58, 0x88d9f1);
        }
    }
}