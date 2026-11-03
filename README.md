## 0.7.4 — Rework completo de 18 itens e simplificação visual do boss

- Removidos os **olhos vermelhos da fase 3** e os **espinhos de gelo que cresciam nas costas**, incluindo os antigos VBOs, renderização e estado client-side de crescimento. A terceira fase de combate continua funcionando normalmente; a criatura conserva os olhos glaciares originais.
- **18 itens não-bloco receberam texturas PNG próprias de 256×256**, cada qual com seu arquivo `assets/wendigosfrost/textures/item/rework/<id>.png`, paleta e detalhes rúnicos específicos. Os materiais não são mais todos retirados de um mesmo atlas.
- Modelos 3D específicos foram refeitos para Machado do Degelo, Lança Rúnica, Espada do Coração Partido, Tocha Rúnica, Sinalizador Térmico, Sementes Frostbloom, Cristal Frostbloom, Fragmento do Sussurro, Guia e Página Perdida.
- **Machado do Degelo**: cabo reforçado vertical, cabeça metálica com volume, lâmina crescente larga orientada lateralmente (face esquerda bem definida), lâmina congelada afiada e contrapeso.
- Mantidos e retintados com atlas exclusivo o Coração Congelado (405 peças), Chifre do Wendigo (286), Amuleto, Selo do Inverno e os modelos tridimensionais dos quatro equipamentos de armadura.
- Vida padrão do Wendigo **500 HP**; coração da fase 2, dano, rituais, loot e comandos /locate não foram alterados.
- O Gradle gera os PNGs individuais na compilação (`scripts/rework-items.gradle`), e o CI verifica dimensões, unicidade por hash, caminho de cada atlas e geometria válida.

A atualização foi preparada sobre um branch separado; faça backup do mundo e teste a aparência no Minecraft antes de substituir uma instalação importante. A compilação e validação estática não substituem um teste visual e de FPS.

## 0.7.2 — Caçadores da Vigília: coração exposto, fase final e correção do /locate

**Correção da busca por ruínas:** o JAR 0.7.1 gerava modelos NBT em `data/wendigosfrost/structures/` (plural). O Minecraft Java 1.20.1 procura em **`data/wendigosfrost/structure/` (singular)**. A 0.7.2 corrige isso e testa explicitamente os caminhos dentro do JAR.

Com cheats habilitados, em regiões novas:
```mcfunction
/locate structure wendigosfrost:ancient_ice_shrine
/locate structure wendigosfrost:hunter_cairn
/locate structure wendigosfrost:frozen_sanctuary
```
As estruturas nascem em chunks **ainda não gerados**; o Santuário Congelado aparece no subterrâneo. A atualização não regenera chunks existentes.

**Wendigo (modelo GLB preservado):**
- 1ª fase: quadrúpede.
- 2ª fase: coração espectral exposto e pulsante na região do peito, ponto vulnerável à Lança Rúnica.
- 3ª fase: a aparência glacial original é mantida (sem olhos vermelhos ou espinhos nas costas).
- O Coração Congelado do inventário ganhou um modelo 3D refeito, com 48 partes e atlas de textura próprio.

**Novos equipamentos fabricáveis antes de derrotar o chefe:**
- Armadura da Vigília: elmo, peitoral, calças e botas. Cada peça reduz em 8% o dano do Wendigo (máximo de 32%), além da proteção normal da armadura.
- Lança Rúnica do Caçador: +55% de dano corpo a corpo contra o Wendigo enquanto seu coração estiver exposto; +15% na forma quadrúpede.
- Machado do Degelo: +27% de dano contra o chefe e fogo breve que interrompe habilidades geladas.
- Sinalizador Térmico: item consumível que incendeia Wendigos próximos por 6 segundos, com recarga de 24 segundos.
- Espada do Coração Partido: fabricada com o coração dropado pelo chefe, recebe bônus maior na terceira fase para caçadas futuras.
- Tocha Rúnica da Vigília: utilitário reutilizável, enfraquece e desacelera o Wendigo por 5 segundos, com recarga de 45 segundos.

As imagens dos itens e as texturas da armadura são criadas pelo próprio Gradle, e as receitas usam materiais acessíveis antes da primeira vitória. O combate continua server-side; preserva singleplayer e servidores dedicados.

**Instalação:** remova versões anteriores do mod da pasta `mods`, mantenha Forge 1.20.1, GeckoLib e TerraBlender compatíveis. Faça backup do mundo. Build e teste de empacotamento não substituem a validação visual no Minecraft.

## 0.7.2 — Ruínas localizáveis, sementes naturais e partículas de combate

Todas as três áreas da lore agora são **estruturas vanilla Jigsaw**, não features comuns, então podem ser encontradas com comandos nativos:

```mcfunction
/locate structure wendigosfrost:ancient_ice_shrine
/locate structure wendigosfrost:hunter_cairn
/locate structure wendigosfrost:frozen_sanctuary
```

As estruturas são distribuídas em **novos chunks** em florestas, taigas e regiões geladas (santuário subterrâneo em aproximadamente Y=-16). **Baús de cada estrutura contêm sementes Frostbloom garantidas:** 2–5 no Santuário Ancestral, 1–3 no Memorial dos Caçadores e 3–6 no Santuário Congelado. Fragmentos, páginas e cristais continuam como recompensas adicionais.

O Wendigo ganhou partículas de combate distintas: **riscos de neve e estilhaços no ataque de garras**, **cone de almas e onda sônica no rugido**, **círculo de advertência e anel de estilhaços de gelo que se expande na pisada**. Os efeitos são produzidos no servidor com limites para não aumentar demais o lag.

Os modelos NBT das estruturas são gerados na compilação via Gradle e ficam dentro do JAR, assim como os registros `worldgen/structure`, `structure_set`, `template_pool` e `tags/worldgen/biome`. Os antigos biome modifiers que causariam duplicação das ruínas foram desativados.

**Importante:** o comando `/locate` exige permissão de comandos/cheats. As novas estruturas aparecerão apenas em regiões ainda não geradas. O funcionamento no mundo real ainda requer teste in-game; o CI valida os arquivos, o NBT e a compilação Java.

## 0.7.2 — Vigília do Inverno: expansão de lore

Esta atualização mantém o Wendigo articulado e o combate da 0.6.1, e adiciona uma linha de exploração:

- **Introdução de primeira entrada:** tela em duas páginas, com história do Wendigo, explicação da maldição e dicas. A confirmação é registrada no personagem daquele mundo. Reabra a tela com `/wendigofrost historia` (sem cheats).
- **Duas ruínas procedurais:** Santuário de Gelo Ancestral (taigas/grove/snowy plains) e Memorial dos Caçadores (florestas/taigas), com baús e artefatos. Os novos locais aparecem em **chunks novos**.
- **Quatro blocos originais:** Tijolos de Gelo Ancestral, Pedra dos Sussurros, Lanterna de Almas Gélidas e Totem dos Caçadores Perdidos.
- **Três itens originais:** Fragmento do Sussurro, Página do Caçador Desaparecido e Selo do Inverno. A página revela lore; o selo oferece visão noturna e proteção temporária.
- **Oito texturas PNG autorais 32×32** geradas pelo Gradle durante o build, com desenhos próprios e sem baixar assets de terceiros.
- **Cinco receitas de fabricação** e drops dos blocos; o Fragmento do Sussurro é encontrado nas ruínas.
- O Santuário Congelado subterrâneo existente foi melhorado com novos blocos e páginas perdidas.
- Manual do Sobrevivente ampliado; todos os recursos estão na aba criativa Wendigo's Frost.

**Validação:** testes estáticos, build Forge e integridade do `.jar` via GitHub Actions. A aparência, o equilíbrio das recompensas e a geração de mundo exigem testes dentro do Minecraft. Faça backup antes de atualizar.

# Wendigo's Frost — Minecraft Forge 1.20.1

Código-fonte do mod de terror psicológico **Wendigo's Frost**, versão de desenvolvimento **0.6.0**.

## Guia e página do mod

- **Guia dentro do jogo:** item `wendigofrost:guide_book` em português, concedido no primeiro login. Clique com o botão direito para abrir o manual com páginas sobre a Flor-Gelo, o cristal, a maldição, o ritual e comandos.
- **Site do projeto:** [Página do Wendigo's Frost](docs/index.html). Quando GitHub Pages estiver ativado, o endereço previsto é `https://erickzepixel.github.io/wendigo-s-frost/`.
- **Download das versões:** https://github.com/erickzepixel/wendigo-s-frost/releases
- **Correções 0.6.0:** geometria 3D do Wendigo reconstruída, atlas 128x128 gerado no build; modelos 3D revisados dos itens, Frost Bloom e pedestal.
- **Versão de teste:** as atualizações são publicadas pela branch `rig-organico-v0.6.0`, sem sobrescrever `main`. Ainda é preciso testar as mudanças no jogo.

## Relíquias tridimensionais — 0.6.0

O Chifre do Wendigo foi reconstruído com 42 cubos e UVs dedicadas, evitando a distorção da textura vanilla. O Coração Congelado agora tem 37 elementos 3D e o Amuleto de Proteção Gélida, 56. Todos usam o atlas próprio `relic_atlas.png` de 128px, gerado via Gradle no `processResources`. O CI valida todas as geometrias, os UVs e os arquivos incorporados ao JAR. Ainda são necessários testes in-game.

## Ritual de invocação instantânea (0.6.0)

- Um pedestal com **4 fogueiras das almas acesas** a 2 blocos nas direções cardeais e **4 gelos azuis** nas diagonais de 1 bloco.
- Agache e clique no pedestal com 1 **Cristal Frostbloom**: o Wendigo é invocado na hora, sem aguardar os três dias.
- Verifica espaço de spawn, impede múltiplos chefes num raio de 64 blocos, consome o cristal apenas se a invocação funcionar e persiste o cooldown de 2 minutos no pedestal.
- Efeitos de neve, chamas espirituais e rugido; nova conquista do ritual.
- O procedimento de purificação antigo continua funcionando normalmente.
- Guia e site atualizados, sem mod externo adicional.

## Rig orgânico do modelo original 0.6.0

- Malha do GLB original preservada byte por byte; **70.020 triângulos sem perda**.
- Dez partes articuladas (tronco, cabeça e dois segmentos por pata), com VBOs cacheados na GPU.
- Ciclos de caminhada quadrúpede, corrida, flexão de joelhos e cotovelos, ataques e rugidos.
- Transição de 1,8 segundo ao passar de 65% de vida para a segunda fase bípede.
- Nenhum mod extra além de GeckoLib e TerraBlender.
- Compilação CI e validação de triângulos aprovadas; **testes de renderização no Minecraft ainda pendentes**.
- Limite do rig rígido: podem aparecer pequenas frestas nas junções, pois ainda não há pesos suaves de skinning. Ver [detalhes técnicos](docs/GLB_ORIGINAL.md).

## Evolução Glacial 0.6.0

- Wendigo com geometria remodelada, crânio de cervo, olhos azuis e menos cubos.
- Fase 1 em quatro patas, com animações exclusivas; segunda fase erguida, com transição; terceira fase bípede enfurecida.
- Dimensões de colisão adaptadas às fases.
- Aba criativa própria, com itens, blocos, livro e ovo de invocação.
- Amuleto Gélido fabricável na bancada, com receita existente validada.
- Coração Congelado dropado **sempre** na morte, sem `random_chance` (com `doMobLoot` habilitado).
- Testes de regressão no CI e `jar` publicado em prévia do GitHub.

## Nightfall 0.6.0 — atualização de combate e atmosfera

- Wendigo em **três fases** e animações especiais para **rugido** e **pisada glacial**, com aviso visual e tempo de reação.
- HUD gelado contextual, partículas e paisagem sonora; implementação leve e sem shaders externos.
- Novo amuleto `wendigosfrost:frost_ward`, fabricável, com 6 cargas, cooldown e defesa temporária.
- Quatro conquistas de progressão e ritual com efeitos e barra de progresso.
- `/wendigofrost guia` recupera o livro atualizado; consumir cristais adicionais não reinicia os três dias.
- Configurações `enablePhaseAbilities`, `enableDynamicAtmosphere`, `enableFrostWard`.
- Testes estáticos de JSON/geo e build automático Forge via GitHub Actions; testes in-game ainda necessários.

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

## Compilar com GitHub Actions (recomendado)

1. Coloque o **conteúdo do ZIP**, não o arquivo ZIP em si, na raiz do seu repositório GitHub.
2. Abra **Actions → Compilar Wendigos Frost → Run workflow**.
3. Aguarde a conclusão. Se falhar, o log de **Compilar** mostra o erro, e o JAR não estará pronto.
4. Se o build passar, abra o workflow → **Artifacts → WendigosFrost-JAR**. Descompacte o artifact.
5. Use apenas o JAR principal `wendigosfrost-0.2.0.jar`, nunca o `*-sources.jar`.

## Compilar no Windows

Instale **JDK 17** e **Gradle 8.1.1**, extraia o projeto e execute `COMPILAR_WINDOWS.bat`. A primeira execução requer internet. Veja `build/libs/wendigosfrost-0.2.0.jar` após sucesso.

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
