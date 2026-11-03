package com.exemplo.wendigosfrost.network;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.client.LoreIntroQueue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

/** Server-authoritative, per-world first-join introduction with acknowledgement. */
public final class ModNetwork {
    private static final String PROTOCOL="2";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(WendigosFrost.MOD_ID, "lore"),
        () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    public static final String READ_KEY = "WendigosFrostLoreIntroRead";
    private ModNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0, IntroPacket.class,
            (packet, buf) -> {}, buf -> new IntroPacket(), ModNetwork::receiveIntro);
        CHANNEL.registerMessage(1, AcknowledgePacket.class,
            (packet, buf) -> {}, buf -> new AcknowledgePacket(), ModNetwork::receiveAck);
    }

    public static void sendIntroduction(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new IntroPacket());
    }
    public static void acknowledgeIntroduction() {
        CHANNEL.sendToServer(new AcknowledgePacket());
    }
    private static void receiveIntro(IntroPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
            Dist.CLIENT, () -> () -> LoreIntroQueue.request()));
        context.get().setPacketHandled(true);
    }
    private static void receiveAck(AcknowledgePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) player.getPersistentData().putBoolean(READ_KEY, true);
        });
        context.get().setPacketHandled(true);
    }
    public static final class IntroPacket {}
    public static final class AcknowledgePacket {}
}
