#!/usr/bin/env python3
"""Static datapack checks for Forge 1.20.1 locatable structures and guaranteed seed loot."""
from pathlib import Path
import json
ROOT=Path("src/main/resources/data/wendigosfrost")
ASSET=Path("src/main/java/com/exemplo/wendigosfrost")
names=("ancient_ice_shrine","hunter_cairn","frozen_sanctuary")
ids=set()
for name in names:
    s=json.loads((ROOT/"worldgen/structure"/f"{name}.json").read_text())
    sp=json.loads((ROOT/"worldgen/structure_set"/f"{name}.json").read_text())
    pool=json.loads((ROOT/"worldgen/template_pool"/f"{name}.json").read_text())
    biomes=json.loads((ROOT/"tags/worldgen/biome/has_structure"/f"{name}.json").read_text())
    loot=json.loads((ROOT/"loot_tables/chests"/f"{name}.json").read_text())
    assert s["type"]=="minecraft:jigsaw" and s["size"]==0, name
    assert s["biomes"]=="#wendigosfrost:has_structure/"+name, name
    assert s["start_pool"]=="wendigosfrost:"+name, name
    assert s["step"] in ("surface_structures","underground_structures")
    assert sp["placement"]["type"]=="minecraft:random_spread"
    assert sp["placement"]["separation"] < sp["placement"]["spacing"]
    assert sp["structures"]==[{"structure":"wendigosfrost:"+name,"weight":1}]
    assert pool["fallback"]=="minecraft:empty", name
    assert len(pool["elements"])==1 and pool["elements"][0]["element"]["location"]=="wendigosfrost:"+name
    assert pool["elements"][0]["element"]["element_type"]=="minecraft:single_pool_element"
    assert len(biomes["values"])>=4, name
    assert not (ROOT/"forge/biome_modifier"/f"{name}.json").exists(), "Duplicate feature for "+name
    assert loot["type"]=="minecraft:chest"
    pool0=loot["pools"][0]
    assert pool0["rolls"]==1 and "conditions" not in pool0
    assert len(pool0["entries"])==1
    seed=pool0["entries"][0]
    assert seed["type"]=="minecraft:item" and seed["name"]=="wendigosfrost:frostbloom_seeds"
    count=seed["functions"][0]
    assert count["function"]=="minecraft:set_count"
    assert count["count"]["min"]>=1 and count["count"]["max"]>=count["count"]["min"]
    ids.add(name)
baker=Path("scripts/structure-templates.gradle").read_text()
for name in ids:
    assert "makeTemplate('"+name+"'" in baker, "Missing baked template "+name
    assert "wendigosfrost:chests/"+name in baker
    assert "makeTemplate" in baker
build=Path("build.gradle").read_text()
assert "apply from: 'scripts/structure-templates.gradle'" in build
entity=(ASSET/"entity/WendigoEntity.java").read_text()
vfx=(ASSET/"combat/WendigoAttackParticles.java").read_text()
for function in ("telegraph","claw","roar","aftershock"):
    assert "WendigoAttackParticles."+function+"(" in entity
    assert "static void "+function+"(" in vfx
assert "ParticleTypes.SONIC_BOOM" in vfx
assert "ParticleTypes.SWEEP_ATTACK" in vfx
assert "ParticleTypes.BLOCK" in vfx
print("PASS: three locatable structure registry paths, unique biome placement,")
print("      original loot chest seeds guaranteed and four distinct boss VFX routines")
