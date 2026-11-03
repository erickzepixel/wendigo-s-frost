package com.exemplo.wendigosfrost.item;

import com.exemplo.wendigosfrost.client.WendigoHunterArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.function.Consumer;

/** Real geometry for the worn armor: antlers, skull, rib cage and ice talons. */
public final class HunterArmorItem extends ArmorItem {
    public HunterArmorItem(Type type, Item.Properties properties) {
        super(HunterArmorMaterial.VIGILIA, type, properties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "wendigosfrost:textures/models/armor/vigilia_layer_"
            + (slot == EquipmentSlot.LEGS ? "2" : "1") + ".png";
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private HumanoidModel<?> detailedModel;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity wearer, ItemStack stack,
                    EquipmentSlot slot, HumanoidModel<?> original) {
                if (detailedModel == null)
                    detailedModel = WendigoHunterArmorModel.create();
                return detailedModel;
            }
        });
    }
}
