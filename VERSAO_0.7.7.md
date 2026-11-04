# Wendigo's Frost 0.7.7

## Versão atualizada

- A versão de mod no arquivo `gradle.properties` foi alterada de `0.2.0` para **`0.7.7`**. O `mods.toml` do projeto usa o campo `${mod_version}`, portanto o build passa a declarar 0.7.7.
- 18 modelos Blockbench (13 itens e 5 blocos) e 10 texturas PNG estão no repositório, com coordenadas UV preservadas. Consulte `MODELOS_E_TEXTURAS_0.7.6.md` e `modelos-blockbench/`.
- O **JAR de jogo 0.7.7**, feito a partir do JAR compilado 0.7.6 com as texturas corrigidas, tem o campo `version="0.7.7"` em `META-INF/mods.toml`.

## Diferença entre fonte e JAR

**O GitHub ainda não contém todo o código Java da versão compilada 0.7.7.** A fonte Java foi iniciada na versão 0.2.0 e não foi migrada na íntegra. **Compilar a branch main não cria o mesmo mod que o arquivo JAR 0.7.7 corrigido.**

Ainda falta publicar o JAR binário na página GitHub Releases; por ora a versão jogável compilada está disponível como arquivo anexado à conversa que a gerou. O JAR foi validado estruturalmente, mas não foi testado em um cliente Minecraft.

Compatibilidade alvo: Minecraft Java 1.20.1, Forge 47.x, GeckoLib 4.4.9, TerraBlender 3.0.1.7.
