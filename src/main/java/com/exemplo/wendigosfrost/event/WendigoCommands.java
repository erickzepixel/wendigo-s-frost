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
            .then(Commands.literal("historia").executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayerOrException();
                com.exemplo.wendigosfrost.network.ModNetwork.sendIntroduction(player);
                return 1;
            }))
            .then(Commands.literal("guia").executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                var stack = new net.minecraft.world.item.ItemStack(
                    com.exemplo.wendigosfrost.registry.ModItems.GUIDE_BOOK.get());
                if (!p.getInventory().add(stack)) p.drop(stack, false);
                ctx.getSource().sendSuccess(() -> Component.literal("Manual do Sobrevivente recebido!"), false);
                return 1;
            }))
            .then(Commands.literal("altar").executes(ctx -> {
                ctx.getSource().sendSuccess(() -> Component.literal(
                    "Altar: pedestal ao centro, 4 Fogueiras das Almas ACESAS a 2 blocos nos pontos cardeais, " +
                    "4 blocos de Gelo Azul nas diagonais adjacentes. Agache e clique no pedestal " +
                    "segurando 1 Cristal Frostbloom para invocar o Wendigo imediatamente."), false);
                return 1;
            }))
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
            .then(Commands.literal("diagnostico").requires(src -> src.hasPermission(2)).executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                int nearby = p.serverLevel().getEntitiesOfClass(WendigoEntity.class,
                        p.getBoundingBox().inflate(80)).size();
                ctx.getSource().sendSuccess(() -> Component.literal(
                    "Wendigo registrado: wendigosfrost:wendigo | Dificuldade: "
                    + p.serverLevel().getDifficulty() + " | Wendigos num raio de 80 blocos: "
                    + nearby + " | Marca Frostbloom: " + WendigoMark.isMarked(p)), false);
                return 1;
            }))
            .then(Commands.literal("testspawn").requires(src -> src.hasPermission(2)).executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                boolean vincular = WendigoMark.isMarked(p) && !WendigoMark.hasWendigo(p);
                WendigoEntity w = ModEntities.WENDIGO.get().create(p.serverLevel());
                if (w == null) {
                    ctx.getSource().sendFailure(Component.literal("Falha ao criar entidade Wendigo. Verifique os logs."));
                    return 0;
                }
                w.moveTo(p.getX()+4, p.getY()+0.2, p.getZ()+4, 0, 0);
                w.setPersistenceRequired();
                if (vincular) w.setOwnerId(p.getUUID());
                if (!p.serverLevel().addFreshEntity(w)) {
                    ctx.getSource().sendFailure(Component.literal("Falha ao adicionar Wendigo ao mundo. Verifique os logs."));
                    return 0;
                }
                if (vincular) WendigoMark.setWendigo(p, w.getUUID());
                ctx.getSource().sendSuccess(() -> Component.literal(
                    "Wendigo criado a 4 blocos! ID: " + w.getUUID()
                    + " | Vinculado a marca: " + vincular), true);
                return 1;
            })));
    }
}
