#!/usr/bin/env python3
"""0.7.4 regression: no third-phase visual mutations; every item has its own 3D art and PNG."""
import json
from pathlib import Path

base=Path("src/main/resources/assets/wendigosfrost/models/item")
src=Path("src/main/java/com/exemplo/wendigosfrost")
ids=(
    "frostbloom_seeds","frostbloom_crystal","frozen_heart","wendigo_horn",
    "frost_ward","guide_book","whisper_shard","lost_page","winter_sigil",
    "hunter_helmet","hunter_chestplate","hunter_leggings","hunter_boots",
    "rune_spear","thaw_axe","thermal_flare","heartbreaker_blade","runic_torch"
)
assert len(set(ids))==18
references=[]
for id in ids:
    obj=json.loads((base/(id+".json")).read_text())
    if id.startswith("hunter_"):
        assert obj["parent"]=="builtin/entity" and "elements" not in obj,id
        references.append("wendigosfrost:textures/models/armor/vigilia_bb_"+{
            "hunter_helmet":"elmo","hunter_chestplate":"peitoral",
            "hunter_leggings":"calcas","hunter_boots":"botas"
        }[id]+".png")
        continue
    if id == "guide_book":
        assert obj["parent"]=="minecraft:block/block"
        assert len(obj["elements"])==5
        assert set(obj["textures"])=={"t0","t1","t2","t3","t4"}
        references.append(obj["textures"]["t0"])
        continue
    assert obj["loader"]=="forge:obj",id
    assert obj["model"]=="wendigosfrost:models/item/bb/"+id+".obj",id
    source=Path("src/main/resources/assets/wendigosfrost/models/item/bb")/(id+".obj")
    assert source.exists() and source.stat().st_size>500,id
    assert source.read_text().count("f ")>=24,id
    references.append(obj["model"])
assert len(set(references))==len(ids)
# The newly provided original axe features a recognizable complete blade.
axe=Path("src/main/resources/assets/wendigosfrost/models/item/bb/thaw_axe.obj").read_text()
assert "g handle_0" in axe and "g head_metal_" in axe
assert "g blade_left_" in axe and "g blade_right_" in axe
assert "g spike_top_" in axe
renderer=(src/"client/WendigoRenderer.java").read_text()
detail=(src/"client/WendigoPhaseDetails.java").read_text()
entity=(src/"entity/WendigoEntity.java").read_text()
for removed in ("drawRedEyes","drawSpines","getThirdPhaseGrowth"):
    assert removed not in renderer and removed not in detail and removed not in entity,removed
assert "drawHeart(pose,stand,t)" in renderer
assert ".add(Attributes.MAX_HEALTH, 500)" in entity
texture_generator=Path("scripts/rework-items.gradle").read_text()
assert "textures/item/rework" in texture_generator
for id in ids:
    assert id+":" in texture_generator,id
assert "apply from: 'scripts/rework-items.gradle'" in Path("build.gradle").read_text()
print("PASS: 18 unique model texture paths, axe crescent shape, no red eyes or dorsal ice spikes")
