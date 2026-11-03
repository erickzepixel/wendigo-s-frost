package com.exemplo.wendigosfrost.event;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import com.exemplo.wendigosfrost.item.HunterArmorItem;
import com.exemplo.wendigosfrost.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Only the Wendigo receives special damage interactions; no global damage boost. */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID)
public final class CombatEquipmentEvents {
    private CombatEquipmentEvents() {}

    @SubscribeEvent
    public static void onDamage(LivingHurtEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel)) return;
        if (event.getEntity() instanceof WendigoEntity boss
                && event.getSource().getEntity() instanceof Player attacker
                && event.getSource().getDirectEntity() == attacker) {
            if (attacker.getMainHandItem().is(ModItems.RUNE_SPEAR.get())) {
                // Early-game weapon remains useful; the exposed chest is a weak point.
                float bonus = boss.getBossPhase() >= 2 ? 1.55f : 1.15f;
                event.setAmount(event.getAmount() * bonus);
            } else if (attacker.getMainHandItem().is(ModItems.THAW_AXE.get())) {
                event.setAmount(event.getAmount() * 1.27f);
                boss.setSecondsOnFire(3); // Fire cancels an icy charged skill.
            }
        }
        if (event.getEntity() instanceof Player victim
                && event.getSource().getEntity() instanceof WendigoEntity) {
            int pieces=0;
            for (var stack : victim.getArmorSlots())
                if (stack.getItem() instanceof HunterArmorItem) pieces++;
            if (pieces > 0)
                event.setAmount(event.getAmount() * (1f - Math.min(.32f,.08f*pieces)));
        }
    }
}
