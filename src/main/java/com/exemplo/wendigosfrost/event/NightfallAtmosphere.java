package com.exemplo.wendigosfrost.event;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.config.ModConfig;
import com.exemplo.wendigosfrost.data.WendigoMark;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ambient cues are server throttled (one check per 100 ticks per player).
 * No phantom entities, camera shake or intrusive jumpscares.
 */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID)
public final class NightfallAtmosphere {
    private NightfallAtmosphere() {}
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        if (player.tickCount % 100 != 0 || !ModConfig.ENABLE_AMBIENT_TERROR.get()) return;
        var wendigos = player.serverLevel().getEntitiesOfClass(WendigoEntity.class,
            player.getBoundingBox().inflate(34));
        if (!wendigos.isEmpty()) {
            WendigoEntity near = wendigos.get(0);
            double distance = player.distanceTo(near);
            if (distance < 22) {
                player.serverLevel().playSound(null, player.blockPosition(),
                    SoundEvents.WARDEN_HEARTBEAT, SoundSource.AMBIENT, .56f,
                    Math.max(.5f, .9f - (float)(22-distance)*.011f));
                player.serverLevel().sendParticles(ParticleTypes.SNOWFLAKE,
                    player.getX(), player.getY()+1.3, player.getZ(), 9, .8, .5, .8, .01);
                if (near.getBossPhase() == 3 && distance < 13)
                    player.displayClientMessage(Component.translatable("message.wendigosfrost.near"), true);
            }
        } else if (WendigoMark.isMarked(player)) {
            // A rare cue for the curse even before the encounter, never more than once / 100 ticks.
            if (player.getRandom().nextInt(8) == 0)
                player.serverLevel().playSound(null, player.blockPosition(),
                    SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT, .25f, .62f);
        }
    }
}