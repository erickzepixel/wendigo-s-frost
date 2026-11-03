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
    assert obj.get("parent")=="minecraft:block/block",id
    path=obj["textures"]["atlas"]
    assert path=="wendigosfrost:item/rework/"+id,(id,path)
    references.append(path)
    elements=obj["elements"]
    assert len(elements)>=50,(id,len(elements))
    for box in elements:
        assert all(0<=a<b<=16 for a,b in zip(box["from"],box["to"])),id
        for face in box["faces"].values():
            assert face["texture"]=="#atlas",id
            u,v,ur,vr=face["uv"]
            assert 0<=u<ur<=16 and 0<=v<vr<=16,id
assert len(set(references))==len(ids)
axe=json.loads((base/"thaw_axe.json").read_text())
parts=axe["elements"]
# Visual-semantic check, not merely triangle count: distinct wide head and
# vertical shaft, broad sharpened edge to the left and hammer poll to the right.
handle=[p for p in parts if p["from"][1]<5 and 7<p["from"][0]<9]
blade=[p for p in parts if p["from"][0]<4 and p["from"][1]>8.5]
poll=[p for p in parts if p["to"][0]>11 and p["from"][1]>10]
assert len(handle)>=10,len(handle)
assert len(blade)>=20,len(blade)
assert len(poll)>=5,len(poll)
assert min(p["from"][0] for p in blade)<2.2
assert max(p["to"][0] for p in parts)>11
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
