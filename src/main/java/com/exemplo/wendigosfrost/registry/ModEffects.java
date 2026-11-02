package com.exemplo.wendigosfrost.registry;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.effect.FrostDeliriumEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WendigosFrost.MOD_ID);
    public static final RegistryObject<MobEffect> FROST_DELIRIUM =
            EFFECTS.register("frost_delirium", FrostDeliriumEffect::new);
    private ModEffects() {}
}
