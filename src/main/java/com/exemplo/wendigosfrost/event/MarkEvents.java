package com.exemplo.wendigosfrost.event;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.config.ModConfig;
import com.exemplo.wendigosfrost.data.WendigoMark;
import com.exemplo.wendigosfrost.data.PendingWendigoDeaths;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import com.exemplo.wendigosfrost.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID)
public final class MarkEvents {
    private MarkEvents() {}
    public static void registerAttributes(IEventBus bus) {
        bus.addListener((EntityAttributeCreationEvent e) -> e.put(ModEntities.WENDIGO.get(), WendigoEntity.createAttributes().build()));
    }
    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer player)
                || player.tickCount % 40 != 0 || !WendigoMark.isMarked(player)) return;
        if (PendingWendigoDeaths.get(player.getServer()).take(player.getUUID()))
            WendigoMark.recordDeath(player, 120000L);
        long elapsed = WendigoMark.elapsed(player);
        if (elapsed < ModConfig.DAYS_TO_AWAKEN.get() * 24000L) {
            if (elapsed >= 48000 && player.tickCount % 1200 < 40)
                player.displayClientMessage(Component.translatable("message.wendigosfrost.warning"), true);
            return;
        }
        CompoundTag tag = WendigoMark.tag(player);
        if (WendigoMark.hasWendigo(player)) {
            // Identidade persistente evita duplicatas ao descarregar chunks ou trocar de dimensão.
            // Libera apenas após o evento de morte e o período de renascimento.
            return;
        }
        if (WendigoMark.now(player) < tag.getLong("nextSpawn")) return;
        ServerLevel level = player.serverLevel();
        for (int i = 0; i < 48; i++) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2;
            int radius = 18 + player.getRandom().nextInt(25);
            BlockPos pos = player.blockPosition().offset((int)(Math.cos(angle) * radius), 0, (int)(Math.sin(angle) * radius));
            if (!level.hasChunkAt(pos)) continue;
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
            if (!level.getFluidState(surface).isEmpty() || !level.getFluidState(surface.below()).isEmpty()) continue;
            boolean clear = true;
            for (int dy = 0; dy < 5; dy++) {
                if (!level.getBlockState(surface.above(dy)).isAir()) { clear = false; break; }
            }
            if (!clear) continue;
            WendigoEntity w = ModEntities.WENDIGO.get().create(level);
            if (w == null) return;
            w.moveTo(surface.getX()+0.5, surface.getY(), surface.getZ()+0.5, player.getRandom().nextFloat()*360, 0);
            w.setOwnerId(player.getUUID());
            w.setPersistenceRequired();
            if (level.addFreshEntity(w)) {
                WendigoMark.setWendigo(player, w.getUUID());
                player.displayClientMessage(Component.translatable("message.wendigosfrost.awakened"), true);
                level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 0.8f, 0.65f);
                level.sendParticles(ParticleTypes.SNOWFLAKE, player.getX(), player.getY()+1, player.getZ(), 35, 2, 1, 2, .03);
                return;
            }
        }
    }
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (!(e.getEntity() instanceof WendigoEntity w) || w.getOwnerId() == null || e.getEntity().level().isClientSide) return;
        var server = e.getEntity().getServer();
        if (server == null) return;
        ServerPlayer owner = server.getPlayerList().getPlayer(w.getOwnerId());
        if (owner != null && WendigoMark.isMarked(owner)) WendigoMark.recordDeath(owner, 120000L);
        else PendingWendigoDeaths.get(server).add(w.getOwnerId());
    }
    @SubscribeEvent
    public static void clonePlayer(net.minecraftforge.event.entity.player.PlayerEvent.Clone e) {
        if (!e.isWasDeath()) return;
        e.getOriginal().reviveCaps();
        if (e.getOriginal().getPersistentData().contains(WendigoMark.KEY))
            e.getEntity().getPersistentData().put(WendigoMark.KEY,
                e.getOriginal().getPersistentData().getCompound(WendigoMark.KEY).copy());
        e.getOriginal().invalidateCaps();
    }
}
