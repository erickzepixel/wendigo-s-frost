package com.exemplo.wendigosfrost.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/** Guia autossuficiente: funciona mesmo sem acesso a internet ou bibliotecas de wiki. */
public class GuideBookItem extends WrittenBookItem {
    private static final String[] PAGES = {
        "WENDIGO'S FROST\n\nGUIA DO SOBREVIVENTE\n\nO frio chama por algo antigo. Cultive a Flor-Gelo e estude seus cristais antes de despertar a criatura.",
        "1. FLOR-GELO\n\nEncontre sementes em vegetacao de florestas e taigas. Plante-as em terra cultivada. A Flor-Gelo possui seis estagios e demora alguns dias para crescer.",
        "2. CRISTAL\n\nColha a Flor-Gelo para obter cristais. Comer um Cristal Frostbloom causa Delirio Gelido e marca o jogador. O efeito e a marca nao sao a mesma coisa.",
        "3. A MALDICAO\n\nA marca e persistente. Apos 3 dias de jogo (72.000 ticks por padrao), um Wendigo pode surgir perto de voce. Ele e hostil e tem uma barra de chefe.",
        "4. WENDIGO\n\nO Wendigo possui 260 pontos de vida, e perigoso em combate e e vulneravel ao fogo. Fique alerta: ele pode perseguir jogadores proximo de seu esconderijo.",
        "5. RITUAL\n\nEncontre ou fabrique o Pedestal das Geleiras Profundas. Coloque nele: 1 Coracao Congelado, 1 Chifre de Wendigo e 1 Cristal Frostbloom.",
        "6. LIBERTACAO\n\nCom os tres ingredientes no pedestal, use a mao vazia para comecar. Permaneça a ate 3 blocos por 15 segundos e evite receber dano. Isso remove a marca.",
        "7. COMANDOS\n\n/summon wendigosfrost:wendigo\n\n/wendigofrost status\n/wendigofrost testmark\n/wendigofrost testspawn\n/wendigofrost diagnostico",
        "8. PROTECAO GELIDA\\n\\nCrie o Amuleto de Protecao Gelida. Ele concede defesa, remove fraqueza e lentidao e atrasa o Wendigo por alguns segundos. Tem 6 cargas e recarga de 30 segundos.",
        "9. BATALHA EM 3 FASES\\n\\nFase 1: perseguicao e rugido. Fase 2: o Wendigo acelera e usa pisada de gelo. Fase 3: furia final. Ao ouvir o grito, fuja da area marcada por particulas.",
        "10. NUNCA IGNORE O SOM\\n\\nBatimentos e neve surgem quando o Wendigo esta perto. O fogo ainda e uma fraqueza importante, interrompendo suas habilidades especiais.",
        "11. COMANDOS\\n\\n/wendigofrost status\\n/wendigofrost testmark\\n/wendigofrost testspawn\\n/wendigofrost diagnostico\\n/summon wendigosfrost:wendigo",
        "12. INFORMACOES\n\nRequer Minecraft Java 1.20.1, Forge 47.x, GeckoLib 4 e TerraBlender. Atualizacoes e instrucao: github.com/erickzepixel/wendigo-s-frost"
    };
    public GuideBookItem(Item.Properties props) {
        super(props);
    }
    public static void prepare(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("pages", 9) && tag.contains("title", 8)) return;
        tag.putString("title", "Wendigo's Frost - Guia");
        tag.putString("author", "Wendigo's Frost");
        tag.putBoolean("resolved", false);
        ListTag pages = new ListTag();
        for (String page : PAGES) {
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(page))));
        }
        tag.put("pages", pages);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        prepare(player.getItemInHand(hand));
        return super.use(level, player, hand);
    }
}