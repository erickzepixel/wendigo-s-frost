package com.exemplo.wendigosfrost.client;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.registry.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Bordas geladas pulsantes sem shaders ou dependências visuais externas. */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID, value = Dist.CLIENT)
public final class DeliriumOverlay {
    private DeliriumOverlay() {}
    @SubscribeEvent
    public static void onHud(RenderGuiEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !mc.player.hasEffect(ModEffects.FROST_DELIRIUM.get())) return;
        GuiGraphics gui = e.getGuiGraphics();
        int w = gui.guiWidth(), h = gui.guiHeight();
        double pulse = (Math.sin(mc.player.tickCount * .08) + 1.0) * .5;
        int alpha = 26 + (int)(24 * pulse);
        int tint = (alpha << 24) | 0x66CBEF;
        int edge = Math.max(9, Math.min(w,h)/24);
        gui.fill(0, 0, w, edge, tint);
        gui.fill(0, h-edge, w, h, tint);
        gui.fill(0, 0, edge, h, tint);
        gui.fill(w-edge, 0, w, h, tint);
    }
}
