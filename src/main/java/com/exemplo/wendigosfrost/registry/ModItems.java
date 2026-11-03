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
    public static final RegistryObject<Item> WENDIGO_SPAWN_EGG = ITEMS.register("wendigo_spawn_egg",
        () -> new net.minecraftforge.common.ForgeSpawnEggItem(
            ModEntities.WENDIGO, 0x1A2532, 0x91E6FF, new Item.Properties()));


    public static final RegistryObject<Item> ANCIENT_FROST_BRICKS_ITEM = ITEMS.register(
            "ancient_frost_bricks", () -> new BlockItem(ModBlocks.ANCIENT_FROST_BRICKS.get(),new Item.Properties()));
    public static final RegistryObject<Item> WHISPERING_RUNESTONE_ITEM = ITEMS.register(
            "whispering_runestone", () -> new BlockItem(ModBlocks.WHISPERING_RUNESTONE.get(),new Item.Properties()));
    public static final RegistryObject<Item> SOUL_FROST_LANTERN_ITEM = ITEMS.register(
            "soul_frost_lantern", () -> new BlockItem(ModBlocks.SOUL_FROST_LANTERN.get(),new Item.Properties()));
    public static final RegistryObject<Item> PALE_BONE_TOTEM_ITEM = ITEMS.register(
            "pale_bone_totem", () -> new BlockItem(ModBlocks.PALE_BONE_TOTEM.get(),new Item.Properties()));

    public static final RegistryObject<Item> WHISPER_SHARD = ITEMS.register(
            "whisper_shard", () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    public static final RegistryObject<Item> LOST_PAGE = ITEMS.register(
            "lost_page", () -> new com.exemplo.wendigosfrost.item.LostPageItem(
                new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    public static final RegistryObject<Item> WINTER_SIGIL = ITEMS.register(
            "winter_sigil", () -> new com.exemplo.wendigosfrost.item.WinterSigilItem(
                new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE)));

    // New hunter equipment: craftable before the boss is defeated.
    public static final RegistryObject<Item> HUNTER_HELMET = ITEMS.register("hunter_helmet",
        () -> new com.exemplo.wendigosfrost.item.HunterArmorItem(
            net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> HUNTER_CHESTPLATE = ITEMS.register("hunter_chestplate",
        () -> new com.exemplo.wendigosfrost.item.HunterArmorItem(
            net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> HUNTER_LEGGINGS = ITEMS.register("hunter_leggings",
        () -> new com.exemplo.wendigosfrost.item.HunterArmorItem(
            net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> HUNTER_BOOTS = ITEMS.register("hunter_boots",
        () -> new com.exemplo.wendigosfrost.item.HunterArmorItem(
            net.minecraft.world.item.ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<Item> RUNE_SPEAR = ITEMS.register("rune_spear",
        () -> new com.exemplo.wendigosfrost.item.RuneSpearItem(
            new Item.Properties().durability(380).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THAW_AXE = ITEMS.register("thaw_axe",
        () -> new com.exemplo.wendigosfrost.item.ThawAxeItem(
            new Item.Properties().durability(280).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    public static final RegistryObject<Item> THERMAL_FLARE = ITEMS.register("thermal_flare",
        () -> new com.exemplo.wendigosfrost.item.ThermalFlareItem(
            new Item.Properties().stacksTo(16).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));

    private ModItems() {}
}
