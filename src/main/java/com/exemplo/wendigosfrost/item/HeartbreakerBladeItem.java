package com.exemplo.wendigosfrost.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

/** Trophy weapon forged from a Wendigo heart for later hunts. */
public final class HeartbreakerBladeItem extends SwordItem {
    public HeartbreakerBladeItem(Item.Properties properties) {
        super(Tiers.DIAMOND, 4, -2.5f, properties);
    }
}
