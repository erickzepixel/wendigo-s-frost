# Wendigo Glacial original — integração direta GLB (0.5.1)

Esta versão substitui apenas a **representação visual do Wendigo** por uma malha glTF 2.0 **idêntica** à escultura que foi criada anteriormente, contendo 70.020 triângulos e 15 grupos de materiais. Não há reconversão para cubos nem simplificação da geometria.

## Arquivos e fidelidade

- Fonte preservada: `assets-source/wendigo_glacial_exact.glb.xz.b64`, um XZ lossless codificado em Base64.
- Recriação: `python3 scripts/restore_original_wendigo_glb.py`.
- Arquivo reproduzido no JAR: `assets/wendigosfrost/models/gltf/wendigo_glacial_exact.glb`.
- SHA-256 esperado: `e40580c2e51d711878745bed9a9601f8ae6be38f80716c7715c099bc44f6f1d1`.
- Renderer: `src/main/java/com/exemplo/wendigosfrost/client/WendigoRenderer.java`.

## Dependência adicional obrigatória

O antigo renderizador GeckoLib não entende GLB de alta densidade. Para renderizar a malha intacta, instale no cliente o **glTF Renderer API 1.0.0-beta.1** para Minecraft Forge 1.20.1, além do Forge, GeckoLib e TerraBlender já exigidos. Download oficial do provedor: https://www.curseforge.com/minecraft/mc-mods/gltf-renderer-api/files/all

O projeto compila contra a API pública do próprio desenvolvedor, sem incorporar nem redistribuir o código proprietário dessa biblioteca.

## Animação: limitação importante

O GLB original é **estático**, sem ossos, pesos de skinning ou clipes de animação. Por isso, o novo visual preserva a geometria exata, mas só permite o movimento da entidade e pequenas alterações de pose de corpo inteiro. Os golpes e a transição de quadrúpede para bípede continuam existindo na lógica do boss; entretanto, **o modelo original não faz movimentos independentes de patas, mandíbula ou galhadas**.

Não é tecnicamente correto prometer que a transição bípede tenha a mesma animação articulada do modelo anterior. Para isso será necessário riggar e animar o GLB no Blender mantendo seus vértices.

## Desempenho

O arquivo tem 70 mil triângulos, portanto pode reduzir o FPS em computadores modestos, particularmente com múltiplos Wendigos visíveis. Faça backup do mundo e teste em uma região aberta antes de adotá-lo permanentemente.

## Testes realizados

O CI valida o hash exato do GLB antes e depois do build, os materiais e a compilação Java/Forge. O teste final de orientação, sombras, iluminação e desempenho exige uma execução real do Minecraft 1.20.1.
