#!/usr/bin/env python3
"""Static regression for 0.7.2. Build and in-game verification are separate."""
from pathlib import Path
import json

java=Path("src/main/java/com/exemplo/wendigosfrost")
assets=Path("src/main/resources/assets/wendigosfrost")
data=Path("src/main/resources/data/wendigosfrost")
ids=("hunter_helmet","hunter_chestplate","hunter_leggings","hunter_boots",
     "rune_spear","thaw_axe","thermal_flare","heartbreaker_blade","runic_torch")
reg=(java/"registry/ModItems.java").read_text()
creative=(java/"registry/ModCreativeTabs.java").read_text()
for id in ids:
    obj=json.loads((assets/"models/item"/(id+".json")).read_text())
    if id.startswith("hunter_"):
        assert obj["parent"]=="builtin/entity" and "elements" not in obj,id
    else:
        assert obj["loader"]=="forge:obj",id
        assert obj["model"]=="wendigosfrost:models/item/bb/"+id+".obj",id
        assert (assets/"models/item/bb"/(id+".obj")).is_file(),id
    rec=json.loads((data/"recipes"/(id+".json")).read_text())
    assert rec["type"]=="minecraft:crafting_shaped", id
    assert rec["result"]["item"]=="wendigosfrost:"+id, id
    assert set("".join(rec["pattern"])).difference({" "})==set(rec["key"]), id
    assert f'"{id}"' in reg, id
    assert id.upper()+".get()" in creative, id

for javafile in ("item/HunterArmorMaterial.java","item/HunterArmorItem.java",
                 "item/RuneSpearItem.java","item/ThawAxeItem.java",
                 "item/ThermalFlareItem.java","item/HeartbreakerBladeItem.java",
                 "item/RunicTorchItem.java","event/CombatEquipmentEvents.java",
                 "client/WendigoPhaseDetails.java"):
    assert (java/javafile).exists(),javafile
heart=json.loads((assets/"models/item/frozen_heart.json").read_text())
assert heart["loader"]=="forge:obj"
assert heart["model"]=="wendigosfrost:models/item/bb/frozen_heart.obj"
detail=(java/"client/WendigoPhaseDetails.java").read_text()
renderer=(java/"client/WendigoRenderer.java").read_text()
entity=(java/"entity/WendigoEntity.java").read_text()
assert "drawHeart(pose,stand,t)" in renderer
assert "drawRedEyes(" not in renderer
assert "drawSpines(" not in renderer
assert "getThirdPhaseGrowth" not in entity
assert "redEyes=" not in detail and "spikes=" not in detail
combat=(java/"event/CombatEquipmentEvents.java").read_text()
assert "boss.getBossPhase() >= 2" in combat
assert "HunterArmorItem" in combat and "1f - Math.min(.32f,.08f*pieces)" in combat
assert "ModItems.RUNE_SPEAR.get()" in combat and "ModItems.THAW_AXE.get()" in combat
thermal=(java/"item/ThermalFlareItem.java").read_text()
assert "boss.setSecondsOnFire(6)" in thermal
generator=Path("scripts/structure-templates.gradle").read_text()
assert "'data/wendigosfrost/structure'" in generator
assert "'data/wendigosfrost/structures'" not in generator
assert "apply from: 'scripts/hunter-gear-textures.gradle'" in Path("build.gradle").read_text()
tex=Path("scripts/hunter-gear-textures.gradle").read_text()
assert "frozen_heart_atlas.png" in tex and "vigilia_layer_" in tex
for id in ids: assert id in tex
for lang in ("pt_br","en_us"):
    loc=json.loads((assets/"lang"/(lang+".json")).read_text())
    for id in ids:
        assert loc.get("item.wendigosfrost."+id), (lang,id)
print("OK: 9 new items and recipes, armor set, heart weak point, normal ice eyes,")
print("    no phase-3 ice spines, unique item textures and locatable structures")
