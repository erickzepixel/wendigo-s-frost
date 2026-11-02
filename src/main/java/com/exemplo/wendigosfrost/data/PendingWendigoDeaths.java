package com.exemplo.wendigosfrost.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Mortes de Wendigos com dono offline aguardam o próximo login do jogador. */
public final class PendingWendigoDeaths extends SavedData {
    private final Set<UUID> owners = new HashSet<>();
    public PendingWendigoDeaths() {}
    public static PendingWendigoDeaths load(CompoundTag tag) {
        PendingWendigoDeaths data = new PendingWendigoDeaths();
        ListTag entries = tag.getList("Owners", Tag.TAG_COMPOUND);
        for (int i=0; i<entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            if (entry.hasUUID("Id")) data.owners.add(entry.getUUID("Id"));
        }
        return data;
    }
    public static PendingWendigoDeaths get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(PendingWendigoDeaths::load,
                PendingWendigoDeaths::new, "wendigosfrost_pending_deaths");
    }
    public void add(UUID id) { if (owners.add(id)) setDirty(); }
    public boolean take(UUID id) {
        boolean found = owners.remove(id);
        if (found) setDirty();
        return found;
    }
    @Override public CompoundTag save(CompoundTag tag) {
        ListTag entries = new ListTag();
        for (UUID id : owners) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Id", id);
            entries.add(entry);
        }
        tag.put("Owners", entries);
        return tag;
    }
}
