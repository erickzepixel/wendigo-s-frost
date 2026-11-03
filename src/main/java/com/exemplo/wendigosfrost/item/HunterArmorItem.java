package com.exemplo.wendigosfrost.item;

import com.exemplo.wendigosfrost.client.VigilArmorItemRenderer;
import com.exemplo.wendigosfrost.client.WendigoHunterArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.function.Consumer;

/**
 * The four uploaded BB models are the only source for both inventory and
 * worn armor graphics; original faces and PNG pixels are never redrawn.
 */
public final class HunterArmorItem extends ArmorItem {
    private final Type piece;

    public HunterArmorItem(Type type, Item.Properties properties) {
        super(HunterArmorMaterial.VIGILIA, type, properties);
        this.piece=type;
    }

    private String sourceName() {
        return switch(piece) {
            case HELMET -> "elmo";
            case CHESTPLATE -> "peitoral";
            case LEGGINGS -> "calcas";
            case BOOTS -> "botas";
        };
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity,
                                  EquipmentSlot slot, String layer) {
        return "wendigosfrost:textures/models/armor/vigilia_bb_"+sourceName()+".png";
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private WendigoHunterArmorModel detailedModel;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity wearer,
                    ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if(detailedModel==null)
                    detailedModel=WendigoHunterArmorModel.create(piece);
                // Preserve vanilla player pose: arm swing, head look, leg
                // stride, swimming, sneaking and the ArmorLayer adjustments.
                // Copy animation bone transforms without wildcard generic casts.
                // Forge also copies model properties when it wraps this model.
                detailedModel.head.copyFrom(original.head);
                detailedModel.body.copyFrom(original.body);
                detailedModel.leftArm.copyFrom(original.leftArm);
                detailedModel.rightArm.copyFrom(original.rightArm);
                detailedModel.leftLeg.copyFrom(original.leftLeg);
                detailedModel.rightLeg.copyFrom(original.rightLeg);
                detailedModel.crouching=original.crouching;
                detailedModel.riding=original.riding;
                detailedModel.young=original.young;
                return detailedModel;
            }

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return VigilArmorItemRenderer.instance();
            }
        });
    }
}
