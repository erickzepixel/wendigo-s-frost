package com.exemplo.wendigosfrost.event;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.blockentity.PedestalBlockEntity;
import com.exemplo.wendigosfrost.registry.ModBlocks;
import com.exemplo.wendigosfrost.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Catch sneak + crystal before the usual food interaction.
 * This also allows the summoning ritual to work consistently when
 * Minecraft would normally bypass the pedestal's Block#use while sneaking.
 */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID)
public final class SummoningInteractEvents {
    private SummoningInteractEvents() {}

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!event.getEntity().isShiftKeyDown()) return;
        if (!event.getItemStack().is(ModItems.FROSTBLOOM_CRYSTAL.get())) return;
        if (!event.getLevel().getBlockState(event.getPos()).is(ModBlocks.PEDESTAL.get())) return;

        if (!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer player
                && event.getLevel().getBlockEntity(event.getPos()) instanceof PedestalBlockEntity pedestal) {
            pedestal.summonInstantly(player, event.getItemStack());
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
