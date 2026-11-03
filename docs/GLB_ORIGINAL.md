# Wendigo Glacial original — integração direta GLB (0.5.1)

Esta versão substitui apenas a **representação visual do Wendigo** por uma malha glTF 2.0 **idêntica** à escultura que foi criada anteriormente, contendo 70.020 triângulos e 15 grupos de materiais. Não há reconversão para cubos nem simplificação da geometria.

## Arquivos e fidelidade

- Fonte preservada: `assets-source/wendigo_glacial_exact.glb.xz.b64`, um XZ lossless codificado em Base64.
- Recriação: `python3 scripts/restore_original_wendigo_glb.py`.
- Arquivo reproduzido no JAR: `assets/wendigosfrost/models/gltf/wendigo_glacial_exact.glb`.
- SHA-256 esperado: `e40580c2e51d711878745bed9a9601f8ae6be38f80716c7715c099bc44f6f1d1`.
- Renderer: `src/main/java/com/exemplo/wendigosfrost/client/WendigoRenderer.java`.
- Stream GPU gerado: `src/main/resources/assets/wendigosfrost/models/mesh/wendigo_glacial.wfmesh` (mesmos 70.020 triângulos).

## Renderização nativa, sem instalar biblioteca adicional

O renderer utiliza diretamente **VertexBuffer e shaders de cor do Minecraft**, com os 70.020 triângulos armazenados em um VBO reutilizável. Não há dependência adicional além de GeckoLib e TerraBlender, já presentes nas versões anteriores.

Para gerar um JAR a partir do código-fonte:
```sh
python3 scripts/restore_original_wendigo_glb.py
python3 scripts/bake_exact_mesh.py
gradle build
```

O GLB original continua incluído no JAR. As cores originais dos 15 materiais são reproduzidas por cor de vértice; reflexos PBR, textura normal e iluminação glTF avançada não são reproduzidos.

## Animação: limitação importante

O GLB original continua estático, porém agora o mod gera uma segunda representação com **dez ossos rígidos** que herdam pivôs hierárquicos. O Wendigo movimenta as quatro patas, flexiona articulações, inclina a cabeça e se ergue ao entrar na segunda fase. A mandíbula e as galhadas acompanham a cabeça como partes do mesmo osso, sem animações individuais. Essa solução não modifica os vértices do original e não precisa de novas bibliotecas.

Não é tecnicamente correto prometer que a transição bípede tenha a mesma animação articulada do modelo anterior. Para isso será necessário riggar e animar o GLB no Blender mantendo seus vértices.

## Desempenho

O arquivo tem 70 mil triângulos, portanto pode reduzir o FPS em computadores modestos, particularmente com múltiplos Wendigos visíveis. Faça backup do mundo e teste em uma região aberta antes de adotá-lo permanentemente.

## Testes realizados

O CI valida a cópia integral do GLB (SHA-256), a existência dos dez ossos, a conservação dos 70.020 triângulos, os arquivos do mod e a compilação Java/Forge. O teste visual, as posições das articulações, a orientação e o desempenho ainda exigem execução no Minecraft 1.20.1.
