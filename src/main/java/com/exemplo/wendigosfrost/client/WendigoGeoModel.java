package com.exemplo.wendigosfrost.client;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** GeckoLib 4: malha e animações próprias. */
public final class WendigoGeoModel extends GeoModel<WendigoEntity> {
    @Override public ResourceLocation getModelResource(WendigoEntity animatable) {
        return new ResourceLocation(WendigosFrost.MOD_ID,"geo/wendigo.geo.json");
    }
    @Override public ResourceLocation getTextureResource(WendigoEntity animatable) {
        return new ResourceLocation(WendigosFrost.MOD_ID,"textures/entity/wendigo.png");
    }
    @Override public ResourceLocation getAnimationResource(WendigoEntity animatable) {
        return new ResourceLocation(WendigosFrost.MOD_ID,"animations/wendigo.animation.json");
    }
}
