package com.exemplo.wendigosfrost.registry;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WendigosFrost.MOD_ID);

    public static final RegistryObject<EntityType<WendigoEntity>> WENDIGO =
            ENTITIES.register("wendigo", () -> EntityType.Builder
                    .of(WendigoEntity::new, MobCategory.MONSTER)
                    .sized(1.95F, 4.5F)
                    .clientTrackingRange(12)
                    .build("wendigosfrost:wendigo"));

    private ModEntities() {}
}
