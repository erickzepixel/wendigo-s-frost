package com.exemplo.wendigosfrost.registry;

import com.exemplo.wendigosfrost.WendigosFrost;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import com.exemplo.wendigosfrost.item.FrostbloomCrystalItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WendigosFrost.MOD_ID);

    public static final RegistryObject<Item> FROSTBLOOM_SEEDS = ITEMS.register(
            "frostbloom_seeds", () -> new ItemNameBlockItem(
                    ModBlocks.FROSTBLOOM_CROP.get(), new Item.Properties()));

    public static final RegistryObject<Item> FROSTBLOOM_CRYSTAL = ITEMS.register(
            "frostbloom_crystal", () -> new FrostbloomCrystalItem(new Item.Properties().food(
                    new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(1).saturationMod(0.1f).alwaysEat().build())));

    public static final RegistryObject<Item> WENDIGO_HORN=ITEMS.register("wendigo_horn",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> FROZEN_HEART=ITEMS.register("frozen_heart",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> PEDESTAL_ITEM=ITEMS.register("deep_glacier_pedestal",
            ()->new BlockItem(ModBlocks.PEDESTAL.get(),new Item.Properties()));
    public static final RegistryObject<Item> GUIDE_BOOK = ITEMS.register("guide_book",
            () -> new com.exemplo.wendigosfrost.item.GuideBookItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FROST_WARD = ITEMS.register("frost_ward",
            () -> new com.exemplo.wendigosfrost.item.FrostWardItem(new Item.Properties().durability(6).rarity(net.minecraft.world.item.Rarity.RARE)));
    private ModItems() {}
}
