package com.exemplo.wendigosfrost.registry;

import com.exemplo.wendigosfrost.WendigosFrost;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Aba exclusiva do mod no inventario criativo do Forge 1.20.1. */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(ForgeRegistries.CREATIVE_MODE_TABS, WendigosFrost.MOD_ID);

    public static final RegistryObject<CreativeModeTab> WENDIGOS_FROST = TABS.register(
        "wendigos_frost", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.wendigosfrost"))
            .icon(() -> new ItemStack(ModItems.FROSTBLOOM_CRYSTAL.get()))
            .displayItems((params, output) -> {
                output.accept(ModItems.FROSTBLOOM_SEEDS.get());
                output.accept(ModItems.FROSTBLOOM_CRYSTAL.get());
                output.accept(ModItems.WENDIGO_HORN.get());
                output.accept(ModItems.FROZEN_HEART.get());
                output.accept(ModItems.FROST_WARD.get());
                output.accept(ModItems.PEDESTAL_ITEM.get());
                output.accept(ModItems.GUIDE_BOOK.get());
                output.accept(ModItems.WENDIGO_SPAWN_EGG.get());
            })
            .build());

    private ModCreativeTabs() {}
}
