# ❄ Wendigo's Frost — página do mod

**Minecraft Java 1.20.1 · Forge 47.x · versão 0.3.2 (prévia)**

> O inverno não é o seu único inimigo. Um cristal misterioso marca o jogador e, após três dias, um Wendigo pode começar a caçá-lo.

[**⬇️ Baixar a versão 0.3.2**](https://github.com/erickzepixel/wendigo-s-frost/releases/download/v0.3.2-modelos-guia/wendigosfrost-0.3.2.jar) · [Página de lançamentos](https://github.com/erickzepixel/wendigo-s-frost/releases) · [Código-fonte](https://github.com/erickzepixel/wendigo-s-frost/tree/fix-modelos-guia-v0.3.2)

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
4. Coloque `wendigosfrost-0.3.2.jar` na pasta `mods` e inicie o jogo.
5. Faça backup do seu mundo antes de experimentar uma atualização de teste.

## Modelos e versões

A versão 0.3.2 reconstrói a malha do Wendigo, agora com UVs alinhadas a uma textura gerada de 128×128, e simplifica as formas dos itens, da planta e do pedestal para reduzir artefatos. **O GitHub Actions compilou a versão, mas a renderização dentro do jogo ainda não foi validada.**

A página HTML responsiva também está em [`docs/index.html`](index.html). Para que ela seja publicada como site em `erickzepixel.github.io`, é necessário habilitar o GitHub Pages nas configurações do repositório.

---

Relate problemas pelo [GitHub Issues](https://github.com/erickzepixel/wendigo-s-frost/issues).
