package com.exemplo.wendigosfrost.client;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Não deve ser carregado pelo servidor dedicado. */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.WENDIGO.get(), WendigoRenderer::new);
    }
}
