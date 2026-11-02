# Wendigo's Frost — Minecraft Forge 1.20.1

Código-fonte do mod de terror psicológico **Wendigo's Frost**, versão de desenvolvimento **0.4.1**.

## Guia e página do mod

- **Guia dentro do jogo:** item `wendigofrost:guide_book` em português, concedido no primeiro login. Clique com o botão direito para abrir o manual com páginas sobre a Flor-Gelo, o cristal, a maldição, o ritual e comandos.
- **Site do projeto:** [Página do Wendigo's Frost](docs/index.html). Quando GitHub Pages estiver ativado, o endereço previsto é `https://erickzepixel.github.io/wendigo-s-frost/`.
- **Download das versões:** https://github.com/erickzepixel/wendigo-s-frost/releases
- **Correções 0.4.1:** geometria 3D do Wendigo reconstruída, atlas 128x128 gerado no build; modelos 3D revisados dos itens, Frost Bloom e pedestal.
- **Versão de teste:** as atualizações são publicadas pela branch `fix-reliquias-3d-v0.4.1`, sem sobrescrever `main`. Ainda é preciso testar as mudanças no jogo.

## Relíquias tridimensionais — 0.4.1

O Chifre do Wendigo foi reconstruído com 42 cubos e UVs dedicadas, evitando a distorção da textura vanilla. O Coração Congelado agora tem 37 elementos 3D e o Amuleto de Proteção Gélida, 56. Todos usam o atlas próprio `relic_atlas.png` de 128px, gerado via Gradle no `processResources`. O CI valida todas as geometrias, os UVs e os arquivos incorporados ao JAR. Ainda são necessários testes in-game.

## Nightfall 0.4.1 — atualização de combate e atmosfera

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
