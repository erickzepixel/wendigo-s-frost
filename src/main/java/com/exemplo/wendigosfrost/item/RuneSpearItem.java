package com.exemplo.wendigosfrost.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item;

/** A low-cost hunter spear. Bonus against the exposed heart is server-side. */
public final class RuneSpearItem extends SwordItem {
    public RuneSpearItem(Item.Properties props) {
        super(Tiers.IRON, 4, -2.5f, props);
    }
}
