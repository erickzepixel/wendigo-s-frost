package com.exemplo.wendigosfrost.event;

import com.exemplo.wendigosfrost.WendigosFrost;
import com.exemplo.wendigosfrost.data.WendigoMark;
import com.exemplo.wendigosfrost.entity.WendigoEntity;
import com.exemplo.wendigosfrost.registry.ModEntities;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Ferramentas administrativas de teste. A remoção real da marca continua exclusiva do ritual. */
@Mod.EventBusSubscriber(modid = WendigosFrost.MOD_ID)
public final class WendigoCommands {
    private WendigoCommands() {}
    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("wendigofrost")
            .then(Commands.literal("status").executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                boolean active = WendigoMark.isMarked(p);
                ctx.getSource().sendSuccess(() -> Component.literal(active
                    ? "Marca ativa. Ticks desde o cristal: " + WendigoMark.elapsed(p)
                    : "Você não está marcado."), false);
                return 1;
            }))
            .then(Commands.literal("testmark").requires(src -> src.hasPermission(2)).executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                WendigoMark.apply(p);
                ctx.getSource().sendSuccess(() -> Component.literal("Marca aplicada para teste."), true);
                return 1;
            }))
            .then(Commands.literal("testspawn").requires(src -> src.hasPermission(2)).executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                if (!WendigoMark.isMarked(p) || WendigoMark.hasWendigo(p)) return 0;
                WendigoEntity w = ModEntities.WENDIGO.get().create(p.serverLevel());
                if (w == null) return 0;
                w.moveTo(p.getX()+8, p.getY(), p.getZ()+8, 0, 0);
                w.setOwnerId(p.getUUID());
                w.setPersistenceRequired();
                if (!p.serverLevel().addFreshEntity(w)) return 0;
                WendigoMark.setWendigo(p, w.getUUID());
                ctx.getSource().sendSuccess(() -> Component.literal("Wendigo de teste criado."), true);
                return 1;
            })));
    }
}
