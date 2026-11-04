# Modelos e texturas Blockbench (recursos 0.7.6)

Os 18 modelos editáveis ficam em `modelos-blockbench/`. Foram exportados para `src/main/resources/assets/wendigosfrost/models/` e apontam para as 10 texturas PNG em `src/main/resources/assets/wendigosfrost/textures/blockbench/modelos/`.

**Correção:** todas as faces usam as coordenadas UV originais do Blockbench, convertidas de 64 × 64 pixels para as coordenadas 0–16 esperadas pelo formato JSON do Minecraft. Modelos e texturas possuem referências consistentes. Alguns ângulos de rotação foram ajustados aos valores aceitos pelo jogo.

| Modelo | ID no jogo | Categoria |
|---|---|---|
| tijolos_de_gelo_ancestral | `ancient_frost_bricks` | Bloco |
| totem_dos_cacadores_perdidos | `pale_bone_totem` | Bloco |
| lanterna_de_almas_gelidas | `soul_frost_lantern` | Bloco |
| pedestal_das_geleiras_profundas | `deep_glacier_pedestal` | Bloco |
| pedra_dos_sussurros | `whispering_runestone` | Bloco |
| chifre_de_wendigo | `wendigo_horn` | Item |
| coracao_congelado | `frozen_heart` | Item |
| cristal_de_frostbloom | `frostbloom_crystal` | Item |
| fragmento_do_sussurro | `whisper_shard` | Item |
| pagina_do_cacador_desaparecido | `lost_page` | Item |
| selo_do_inverno | `winter_sigil` | Item |
| sementes_de_flor_gelo | `frostbloom_seeds` | Item |
| sinalizador_termico | `thermal_flare` | Item |
| tocha_runica_da_vigilia | `runic_torch` | Item |
| amuleto_de_protecao_gelida | `frost_ward` | Item |
| lanca_runica_do_cacador | `rune_spear` | Item |
| machado_do_degelo | `thaw_axe` | Item |
| espada_do_coracao_partido | `heartbreaker_blade` | Item |

**Aviso sobre versão:** estes recursos foram extraídos dos modelos referentes à 0.7.6, mas este repositório contém código Java antigo 0.2.0. A compilação a partir da branch `main` ainda não produz a versão jogável 0.7.6, porque o código Java correspondente não foi migrado. O JAR corrigido deve ser distribuído separadamente.

Teste local realizado sobre o JAR corrigido: ZIP válido; 18 modelos com UV ajustadas; 10 PNG legíveis; zero referências ausentes. O jogo não foi executado para inspeção visual.
