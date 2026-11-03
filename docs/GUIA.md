# ❄ Wendigo's Frost — página do mod

**Minecraft Java 1.20.1 · Forge 47.x · versão 0.6.0 (prévia)**

> O inverno não é o seu único inimigo. Um cristal misterioso marca o jogador e, após três dias, um Wendigo pode começar a caçá-lo.

[**⬇️ Baixar a versão 0.6.0**](https://github.com/erickzepixel/wendigo-s-frost/releases/download/v0.6.0-rig-organico/wendigosfrost-0.6.0.jar) · [Página de lançamentos](https://github.com/erickzepixel/wendigo-s-frost/releases) · [Código-fonte](https://github.com/erickzepixel/wendigo-s-frost/tree/rig-organico-v0.6.0)

## Melhorias das relíquias 3D (0.6.0)

- **Chifre do Wendigo:** reconstruído em 42 elementos, com curvatura, pontas ósseas e veios de gelo, corrigindo a textura que antes se misturava com a do bloco de osso.
- **Coração Congelado:** verdadeiro item 3D com 37 elementos, lóbulos assimétricos, camadas de tecido congelado, artérias e fissuras de gelo.
- **Amuleto de Proteção Gélida:** modelo 3D de 56 elementos, com aro metálico, runas e núcleo cristalino.
- **Texturas próprias:** novo atlas `relic_atlas.png` (128×128), gerado durante a compilação, com UVs específicas para cada face e margens para evitar manchas nas bordas.
- **Testes automatizados:** o GitHub Actions confere a geometria, o mapeamento UV e a presença do PNG dentro do JAR.

## Atualização Nightfall 0.6.0

Esta versão adiciona uma batalha em **três fases**, com vida, velocidade e habilidades que mudam ao longo do combate. O Wendigo avisa antes do **Rugido de Pavor** e da **Pisada Glacial**, oferecendo tempo para fugir. Efeitos de neve, batimentos e vinheta na tela dão pistas da distância do monstro.

- **Amuleto de Proteção Gélida:** crie-o com 4 gelos compactados, 2 cristais Frostbloom, 2 fragmentos de prismarinho e 1 Coração do Mar (formato da receita no jogo). O item tem 6 cargas, concede efeitos defensivos e desacelera o Wendigo próximo, com recarga de 30 segundos.
- **Conquistas próprias:** começo da maldição, defesa, derrota do chefe e libertação pelo pedestal.
- **Ritual aprimorado:** partículas, sinos e progresso percentual.
- **/wendigofrost guia:** gera uma cópia atualizada do livro para jogadores antigos.
- **Correção de contagem:** consumir outro cristal não reinicia os três dias da maldição.

## Animações orgânicas — 0.6.0

O Wendigo utiliza agora **a mesma escultura GLB original**, segmentada em 10 grupos com juntas, sem redução de geometria. Caminha em quatro patas, flexiona as pernas, ataca com os braços, ruge e movimenta a cabeça. Ao chegar a 65% da vida, o tronco se ergue de modo interpolado até a postura bípede em 1,8 segundo.

Os ossos são rígidos e movimentam partes independentes do modelo; **não existem pesos suaves**, portanto podem aparecer pequenas costuras entre patas e torso. A animação ainda precisa ser validada visualmente dentro do Minecraft.

## Wendigo — Evolução Glacial (0.6.0)

A primeira fase é **quadrúpede**, com postura curvada, patas anteriores longas e corrida animal. Quando a vida cai para 65%, ele se ergue com animação própria e passa a lutar **bípede**, mantendo a terceira fase de fúria.

O modelo preserva crânio de cervo, galhadas ramificadas, costelas e garras com volumes maiores e menos cubinhos, inspirado na imagem de referência. A textura tem olhos luminosos azuis e detalhes de gelo. A hitbox acompanha a transformação.

### Aba no criativo
Aba **Wendigo's Frost**, com sementes, cristais, coração, chifre, amuleto, pedestal, guia e ovo de invocação.

### Amuleto de Proteção Gélida
Monte na bancada de trabalho:
```text
I C I
P H P
I C I
```
I = Gelo compactado; C = Cristal Frostbloom; P = Fragmento de prismarinho; H = Coração do Mar.

### Drop do chefe
Ao morrer, o Wendigo agora sempre deixa **1 Coração Congelado**, além de 1 Chifre. Isso depende do saque normal de entidades estar habilitado no mundo (regra `doMobLoot`).

## Criaturas e recursos

| Recurso | Descrição |
|---|---|
| **Wendigo** | Chefe gigante hostil, com barra de vida, animação, 260 de vida e 16 de dano base |
| **Frost Bloom** | Planta cultivável com 6 estágios |
| **Cristal Frostbloom** | Consumível que desencadeia o Delírio Gélido e a marca |
| **Sementes** | Item para cultivar Frost Bloom |
| **Chifre e coração congelado** | Componentes do ritual |
| **Pedestal das Geleiras Profundas** | Bloco do ritual de libertação |
| **Manual do Sobrevivente** | Livro in-game com instruções e comandos |

## Ritual instantâneo de invocação do Wendigo — novo em 0.6.0

Não é necessário aguardar os três dias da maldição. Construa este altar na **mesma altura do Pedestal das Geleiras Profundas** (visão superior):

```text
. . F . .
. G . G .
F . P . F
. G . G .
. . F . .
```

**P:** Pedestal das Geleiras Profundas · **F:** Fogueira das almas **acesa** · **G:** Gelo azul · **.:** espaço livre.

1. Coloque 4 fogueiras das almas acesas a **2 blocos** do pedestal: norte, sul, leste e oeste.
2. Coloque 4 gelos azuis nas quatro diagonais a 1 bloco do pedestal.
3. Mantenha uma área externa aberta, com **5 blocos de altura livre** nas proximidades.
4. Segure **1 Cristal Frostbloom**, **agache e clique com o botão direito no pedestal**.
5. O cristal será consumido (exceto no criativo) e um Wendigo hostil surgirá **imediatamente** a 6–12 blocos do altar, com rugido e partículas.

O pedestal tem **2 minutos de recarga** após uma invocação e impede a criação de outro Wendigo se já existir um em um raio de 64 blocos. Nenhum cristal é consumido se a estrutura estiver incompleta, não houver espaço ou o surgimento falhar. Este ritual não aplica nem remove a marca; o ritual de libertação permanece separado (clique normal para inserir os três ingredientes e mão vazia para iniciar a purificação).

## Vigília do Inverno — novidades 0.7.0

### Uma história ao entrar no mundo

Na primeira entrada com o mod, surge uma tela em duas páginas com a origem da maldição e a história dos antigos caçadores. Clique em **Continuar** e **Entrar no mundo**. O mod registra a conclusão para não exibir novamente ao voltar ao mesmo mundo. Use `/wendigofrost historia` para revisitar a história quando quiser.

### Estruturas naturais

O **Santuário de Gelo Ancestral** é encontrado em taigas e regiões nevadas; seus pilares, runas, totens e baús revelam o legado dos caçadores. O **Memorial dos Caçadores** aparece entre árvores de florestas e taigas. O Santuário Congelado subterrâneo existente também foi enriquecido. As estruturas só podem aparecer em **terreno ainda não gerado**.

### Novos blocos e itens

| Nome | Uso |
|---|---|
| Tijolos de Gelo Ancestral | Construção e fabricação de monumentos |
| Pedra dos Sussurros | Clique para ouvir uma das pistas do ritual |
| Lanterna de Almas Gélidas | Iluminação azulada para refúgios e santuários |
| Totem dos Caçadores Perdidos | Bloco decorativo ósseo de ruínas |
| Fragmento do Sussurro | Encontrado em baús das ruínas; ingrediente do Selo |
| Página do Caçador Desaparecido | Clique para ler um trecho da história |
| Selo do Inverno | Visão noturna por 30 s e resistência por 8 s; recarga de 90 s |

O **Selo do Inverno** é fabricado na bancada com dois Fragmentos do Sussurro, um Coração do Mar, uma Ametista e um Gelo Azul:

```text
. A .
F C F
. G .
```

F = Fragmento do Sussurro; C = Coração do Mar; A = Fragmento de Ametista; G = Gelo Azul.

O mod também permite fabricar blocos decorativos com gelo, deepslate, ossos e materiais espirituais.

## Como sobreviver

1. Procure sementes em vegetação de florestas e taigas e cultive a Flor-Gelo.
2. Quando consumir um Cristal Frostbloom, a marca será aplicada e o Delírio Gélido começará.
3. Após **72.000 ticks de jogo** (3 dias por padrão), o Wendigo tentará aparecer em uma área próxima que tenha espaço suficiente.
4. Para remover a marca, reúna **1 Coração Congelado, 1 Chifre de Wendigo e 1 Cristal Frostbloom**.
5. Insira os itens no pedestal, interaja com a mão vazia e mantenha-se dentro de 3 blocos **por 15 segundos sem sofrer dano**.

## Comandos

```mcfunction
/summon wendigosfrost:wendigo ~ ~ ~
/wendigofrost diagnostico
/wendigofrost status
/wendigofrost testmark
/wendigofrost testspawn
/give @p wendigosfrost:guide_book
```

Os comandos de teste exigem privilégios apropriados.

## Instalação

1. Instale **Forge 47.x** para Minecraft Java **1.20.1**.
2. Instale **GeckoLib 4.4.9** e **TerraBlender 3.0.1.7** compatíveis com Forge 1.20.1.
3. Retire versões antigas do Wendigo's Frost da pasta `.minecraft/mods`.
4. Coloque `wendigosfrost-0.6.0.jar` na pasta `mods` e inicie o jogo.
5. Faça backup do seu mundo antes de experimentar uma atualização de teste.

## Modelos e versões

A versão 0.6.0 preserva a malha corrigida que reconstrói do Wendigo, agora com UVs alinhadas a uma textura gerada de 128×128, e simplifica as formas dos itens, da planta e do pedestal para reduzir artefatos. **O GitHub Actions compilou a versão, mas a renderização dentro do jogo ainda não foi validada.**

A página HTML responsiva também está em [`docs/index.html`](index.html). Para que ela seja publicada como site em `erickzepixel.github.io`, é necessário habilitar o GitHub Pages nas configurações do repositório.

---

Relate problemas pelo [GitHub Issues](https://github.com/erickzepixel/wendigo-s-frost/issues).
