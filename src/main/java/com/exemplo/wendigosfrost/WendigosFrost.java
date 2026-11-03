package com.exemplo.wendigosfrost;

import com.exemplo.wendigosfrost.registry.ModBlocks;
import com.exemplo.wendigosfrost.registry.ModFeatures;
import com.exemplo.wendigosfrost.registry.ModItems;
import com.exemplo.wendigosfrost.registry.ModEntities;
import com.exemplo.wendigosfrost.registry.ModBlockEntities;
import com.exemplo.wendigosfrost.registry.ModEffects;
import com.exemplo.wendigosfrost.registry.ModCreativeTabs;
import com.exemplo.wendigosfrost.event.MarkEvents;
import com.exemplo.wendigosfrost.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WendigosFrost.MOD_ID)
public class WendigosFrost {
    public static final String MOD_ID = "wendigosfrost";

    public WendigosFrost() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModFeatures.FEATURES.register(bus);
        ModItems.ITEMS.register(bus);
        ModCreativeTabs.TABS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModBlockEntities.TYPES.register(bus);
        ModEffects.EFFECTS.register(bus);
        MarkEvents.registerAttributes(bus);
        com.exemplo.wendigosfrost.network.ModNetwork.register();
        bus.addListener((net.minecraftforge.event.BuildCreativeModeTabContentsEvent e) -> {
            if (e.getTabKey() == net.minecraft.world.item.CreativeModeTabs.INGREDIENTS) {
                e.accept(ModItems.FROSTBLOOM_SEEDS);
                e.accept(ModItems.FROSTBLOOM_CRYSTAL);
                e.accept(ModItems.WENDIGO_HORN);
                e.accept(ModItems.FROZEN_HEART);
                e.accept(ModItems.PEDESTAL_ITEM);
                e.accept(ModItems.GUIDE_BOOK);
                e.accept(ModItems.FROST_WARD);
            }
        });
        ModLoadingContext.get().registerConfig(Type.SERVER, ModConfig.SERVER_SPEC);
    }
}
