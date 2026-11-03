package com.exemplo.wendigosfrost.item;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item;

/** Heavy melee tool for damaging the ice spikes and staggering the boss. */
public final class ThawAxeItem extends AxeItem {
    public ThawAxeItem(Item.Properties props) {
        super(Tiers.IRON, 5.5f, -3.15f, props);
    }
}
