package com.exemplo.wendigosfrost.registry;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.worldgen.FrozenSanctuaryFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, WendigosFrost.MOD_ID);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SANCTUARY = FEATURES.register(
            "frozen_sanctuary", () -> new FrozenSanctuaryFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ANCIENT_SHRINE = FEATURES.register(
            "ancient_ice_shrine", () -> new com.exemplo.wendigosfrost.worldgen.LostRuinFeature(
                NoneFeatureConfiguration.CODEC, true));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> HUNTER_CAIRN = FEATURES.register(
            "hunter_cairn", () -> new com.exemplo.wendigosfrost.worldgen.LostRuinFeature(
                NoneFeatureConfiguration.CODEC, false));

    private ModFeatures() {}
}
