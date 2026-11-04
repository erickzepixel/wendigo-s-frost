# Wendigo's Frost — Minecraft Forge 1.20.1

Código-fonte do mod de terror psicológico **Wendigo's Frost**. O identificador de versão de build está atualizado para **0.7.7**, e os 18 modelos 3D com suas texturas estão integrados aos recursos.

> **Importante:** os arquivos Java deste repositório ainda têm a implementação antiga (iniciada na 0.2.0). O JAR 0.7.7 atualizado nesta conversa foi produzido a partir do JAR compilado 0.7.6, com os recursos visuais e a versão corrigidos. **Compilar este código-fonte no GitHub não reproduz integralmente o JAR 0.7.7**. Veja [VERSAO_0.7.7.md](VERSAO_0.7.7.md).

## Implementado no código

- Frostbloom cultivável com 6 estágios de crescimento, até 4–5 dias no ciclo típico, sementes obtidas de vegetação de florestas e taigas e drops de cristal.
- Cristal consumível com marca persistente ao morrer, troca de dimensões e saída do mundo. Efeito Delírio Gélido por 6 minutos com fome, lentidão, partículas e sons de alucinação, overlay gelado no HUD.
- Wendigo vinculado a um único jogador marcado, despertar depois de 3 dias (72000 ticks), spawn com tentativas a 60–100 blocos em florestas e taigas carregadas, 10 blocos de percepção, caça/ataque/perda de alvo após 15 segundos e vulnerabilidade a fogo.
- Controle de identidade persistente que evita criar novas cópias após descarregamento de chunk; mortes de Wendigos com dono desconectado são registradas em SavedData; novo spawn 120000 ticks depois da morte.
- GeckoLib 4: modelo de criatura com chifres e animações de caminhada/respiração.
- Pedestal com inventário persistente de 3 ingredientes. Ritual de 15s iniciado com mão vazia até 3 blocos; cancela quando o jogador é atingido ou se afasta. Remove a marca, limpa o delírio e despacha a criatura.
- Veios subterrâneos congelados em Y -40 a 40 e santuários raros com pedestal e baú durante a geração de florestas e taigas. **Não existe ainda um bioma independente de Ice Caverns integrado pelo TerraBlender.**
- Configuração Forge no serverconfig para duração, percepção, dano, crescimento e efeitos; traduções em pt-BR/en-US e comandos de teste.

## Limites de validação

**Este é o projeto-fonte atualizado, não um JAR testado.** Neste ambiente a conexão de rede necessária para baixar o ForgeGradle e Gradle está indisponível. O build e um teste no Minecraft/servidor dedicado ainda precisam ser executados para corrigir possíveis incompatibilidades de API e comportamento. Os efeitos de alucinação têm escopo parcial (sons, partículas, overlay), não há fantasma visual independente, jumpscares completos ou bioma Ice Caverns registrado via TerraBlender.

## Compilar com GitHub Actions (código-fonte ainda incompleto)

1. Coloque o **conteúdo do ZIP**, não o arquivo ZIP em si, na raiz do seu repositório GitHub.
2. Abra **Actions → Compilar Wendigos Frost → Run workflow**.
3. Aguarde a conclusão. Se falhar, o log de **Compilar** mostra o erro, e o JAR não estará pronto.
4. Se o build passar, abra o workflow → **Artifacts → WendigosFrost-JAR**. Descompacte o artifact.
5. Use apenas o JAR principal `wendigosfrost-0.7.7.jar`, nunca o `*-sources.jar`.

## Compilar no Windows (código-fonte ainda incompleto)

Instale **JDK 17** e **Gradle 8.1.1**, extraia o projeto e execute `COMPILAR_WINDOWS.bat`. A primeira execução requer internet. Veja `build/libs/wendigosfrost-0.7.7.jar` após sucesso.

## Instalar / testar

- Minecraft Java **1.20.1**, Forge **47.2.0** ou 47.x compatível.
- Coloque na pasta `mods` o JAR do Wendigo's Frost e as bibliotecas **GeckoLib 4.4.9 para Forge 1.20.1** e **TerraBlender 3.0.1.7 para Forge 1.20.1**.
- Faça backup do mundo antes de testar, pois novos blocos de geração de mundo podem afetar o mapa.
- Faça um mundo novo ou vá a chunks ainda não gerados para encontrar santuários/veios de gelo.
- Obtenha itens em criativo ou com `/give @p wendigosfrost:frostbloom_crystal` e `/give @p wendigosfrost:deep_glacier_pedestal`.
- Com cheats habilitados: `/wendigofrost testmark`, `/wendigofrost testspawn` e `/wendigofrost status`.
- O ritual precisa de 1 Coração Congelado, 1 Chifre de Wendigo e 1 Cristal Frostbloom. Clique com cada um no pedestal; com a mão vazia, inicie e permaneça perto por 15 segundos sem tomar dano.

## Observações

Não confundir **código-fonte** (`.zip`) com o mod compilado (`.jar`). Bibliotecas acima exigem versões de Minecraft e loader correspondentes. Servidor e todos os clientes devem ter o mesmo JAR de mod e as bibliotecas necessárias.
