package com.exemplo.wendigosfrost.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;

/** Armor that starts a little weaker than diamond and specializes against the Wendigo. */
public enum HunterArmorMaterial implements ArmorMaterial {
    VIGILIA;

    private static final int[] DURABILITY = {13, 15, 16, 11};
    private static final int[] DEFENSE = {2, 5, 6, 2};
    private static final int MULTIPLIER = 20;

    @Override public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.ordinal()] * MULTIPLIER;
    }
    @Override public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE[type.ordinal()];
    }
    @Override public int getEnchantmentValue() { return 12; }
    @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_IRON; }
    @Override public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.PACKED_ICE, Items.IRON_INGOT);
    }
    @Override public String getName() { return "wendigosfrost:vigilia"; }
    @Override public float getToughness() { return 1.0f; }
    @Override public float getKnockbackResistance() { return .025f; }
}
