#!/usr/bin/env python3
"""Regression: original lore structures, artifacts, recipes and first-join network UI."""
from pathlib import Path
import json

src=Path("src/main/java/com/exemplo/wendigosfrost")
assets=Path("src/main/resources/assets/wendigosfrost")
data=Path("src/main/resources/data/wendigosfrost")

blocks=["ancient_frost_bricks","whispering_runestone","soul_frost_lantern","pale_bone_totem"]
items=["whisper_shard","lost_page","winter_sigil"]
extra=["soul_frost_lantern_frame"]
for key in blocks:
    assert (assets/f"blockstates/{key}.json").exists(), key
    assert (assets/f"models/block/{key}.json").exists(), key
    assert (assets/f"models/item/{key}.json").exists(), key
    assert (data/f"loot_tables/blocks/{key}.json").exists(), key
for key in items:
    assert (assets/f"models/item/{key}.json").exists(), key
assert (assets/"models/item/winter_sigil.json").exists()
for name in (*blocks,*items,*extra):
    assert name in Path("scripts/lore-textures.gradle").read_text(), "missing texture recipe "+name

for feature in ("ancient_ice_shrine","hunter_cairn"):
    for p in (f"worldgen/configured_feature/{feature}.json",
              f"worldgen/placed_feature/{feature}.json",
              f"forge/biome_modifier/{feature}.json"):
        assert (data/p).exists(), "missing worldgen "+p
    placed=json.loads((data/f"worldgen/placed_feature/{feature}.json").read_text())
    assert placed["placement"][-1]["type"]=="minecraft:biome"
    assert any(e["type"]=="minecraft:heightmap" for e in placed["placement"])
    biome=json.loads((data/f"forge/biome_modifier/{feature}.json").read_text())
    assert biome["step"]=="surface_structures"

for name in (*blocks,"winter_sigil"):
    recipe=json.loads((data/f"recipes/{name}.json").read_text())
    assert recipe["type"]=="minecraft:crafting_shaped"
    assert recipe["result"]["item"]=="wendigosfrost:"+name

tab=(src/"registry/ModCreativeTabs.java").read_text()
for name in ("ANCIENT_FROST_BRICKS_ITEM","WHISPERING_RUNESTONE_ITEM",
             "SOUL_FROST_LANTERN_ITEM","PALE_BONE_TOTEM_ITEM",
             "WHISPER_SHARD","LOST_PAGE","WINTER_SIGIL"):
    assert f"ModItems.{name}.get()" in tab, name
net=(src/"network/ModNetwork.java").read_text()
screen=(src/"client/LoreIntroScreen.java").read_text()
queue=(src/"client/LoreIntroQueue.java").read_text()
login=(src/"event/GuideBookEvents.java").read_text()
assert "READ_KEY" in net and "putBoolean(READ_KEY, true)" in net
assert "CHANNEL.sendToServer(new AcknowledgePacket())" in net
assert "sendIntroduction(player)" in login and "READ_KEY" in login
assert "mc.setScreen(new LoreIntroScreen())" in queue
assert "ModNetwork.acknowledgeIntroduction()" in screen
assert "screen.wendigosfrost.story_1" in screen
assert "new com.exemplo.wendigosfrost.worldgen.LostRuinFeature" in (src/"registry/ModFeatures.java").read_text()
assert 'apply from: \'scripts/lore-textures.gradle\'' in Path("build.gradle").read_text()
for lang in ("pt_br","en_us"):
    loc=json.loads((assets/f"lang/{lang}.json").read_text())
    for key in ("screen.wendigosfrost.title","screen.wendigosfrost.story_1",
                "screen.wendigosfrost.story_2","screen.wendigosfrost.enter",
                "item.wendigosfrost.winter_sigil"):
        assert loc[key], f"{lang}: {key}"
print("PASS: two lore ruin features, four custom blocks, three items, five recipes,")
print("      custom texture generator, persistent one-time intro and translated story.")
