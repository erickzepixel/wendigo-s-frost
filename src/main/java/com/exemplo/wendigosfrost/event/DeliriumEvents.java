package com.exemplo.wendigosfrost.event;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.config.ModConfig;
import com.exemplo.wendigosfrost.registry.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Fome, frio e alucinações auditivas sincronizadas pelo servidor. */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID)
public final class DeliriumEvents {
    private DeliriumEvents() {}
    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)
                || !player.hasEffect(ModEffects.FROST_DELIRIUM.get())) return;
        if (player.tickCount % 80 == 0) player.causeFoodExhaustion(0.7f);
        if (player.tickCount % 12 == 0 && ModConfig.ENABLE_FOG.get())
            player.serverLevel().sendParticles(ParticleTypes.SNOWFLAKE,
                    player.getX(), player.getY()+1.1, player.getZ(), 4, 1.0, 0.6, 1.0, 0.01);
        if (ModConfig.ENABLE_HALLUCINATIONS.get() && player.tickCount % 200 == 0 && player.getRandom().nextInt(3)==0)
            player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.AMBIENT_CAVE,
                    SoundSource.AMBIENT, 0.55f, 0.5f + player.getRandom().nextFloat() * 0.35f);
    }
}
