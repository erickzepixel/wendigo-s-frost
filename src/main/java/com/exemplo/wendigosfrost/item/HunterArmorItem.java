package com.exemplo.wendigosfrost.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

/** Special set bonus comes from CombatEquipmentEvents, not client-only rendering. */
public final class HunterArmorItem extends ArmorItem {
    public HunterArmorItem(Type type, Item.Properties properties) {
        super(HunterArmorMaterial.VIGILIA, type, properties);
    }
}
