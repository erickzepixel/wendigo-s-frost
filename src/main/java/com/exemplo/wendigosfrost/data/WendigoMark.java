package com.exemplo.wendigosfrost.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import java.util.UUID;

/** A marca segue o jogador entre mortes e dimensões. Contagem por tempo do mundo principal. */
public final class WendigoMark {
    public static final String KEY = "WendigosFrostMark";
    private static final String START = "startGameTime";
    private static final String ACTIVE = "active";
    private WendigoMark() {}

    private static long gameTime(ServerPlayer p) {
        return p.getServer().overworld().getGameTime();
    }

    public static void apply(ServerPlayer player) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(ACTIVE, true);
        tag.putLong(START, gameTime(player));
        player.getPersistentData().put(KEY, tag);
    }
    public static CompoundTag tag(Player player) {
        return player.getPersistentData().getCompound(KEY);
    }
    public static boolean isMarked(Player player) {
        return tag(player).getBoolean(ACTIVE);
    }
    public static long elapsed(ServerPlayer player) {
        return isMarked(player) ? Math.max(0, gameTime(player) - tag(player).getLong(START)) : 0;
    }
    public static long now(ServerPlayer player) { return gameTime(player); }
    public static void setWendigo(ServerPlayer player, UUID uuid) {
        CompoundTag tag = tag(player);
        tag.putUUID("wendigoId", uuid);
        player.getPersistentData().put(KEY, tag);
    }
    public static boolean hasWendigo(Player player) { return tag(player).hasUUID("wendigoId"); }
    public static UUID wendigo(Player player) { return hasWendigo(player) ? tag(player).getUUID("wendigoId") : null; }
    public static void recordDeath(ServerPlayer player, long cooldownTicks) {
        CompoundTag t = tag(player);
        t.remove("wendigoId");
        t.putLong("nextSpawn", now(player) + cooldownTicks);
        player.getPersistentData().put(KEY, t);
    }
    public static void clear(Player player) {
        player.getPersistentData().remove(KEY);
    }
}
