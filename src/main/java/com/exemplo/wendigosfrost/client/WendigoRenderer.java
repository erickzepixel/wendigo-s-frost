package com.exemplo.wendigosfrost.client;

import com.exemplo.wendigosfrost.entity.WendigoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/** Renderização animada pelo GeckoLib 4.4.9. */
public final class WendigoRenderer extends GeoEntityRenderer<WendigoEntity> {
    public WendigoRenderer(EntityRendererProvider.Context context) {
        super(context, new WendigoGeoModel());
        this.shadowRadius = .75f;
    }
}
