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
    assert obj["textures"]["layer0"]=="wendigosfrost:item/"+id, id
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
assert len(heart["elements"]) >= 40
assert heart["textures"]["atlas"]=="wendigosfrost:item/frozen_heart_atlas"
detail=(java/"client/WendigoPhaseDetails.java").read_text()
renderer=(java/"client/WendigoRenderer.java").read_text()
entity=(java/"entity/WendigoEntity.java").read_text()
assert "drawHeart(pose,stand,t)" in renderer
assert "drawSpines(pose,entity.getThirdPhaseGrowth(partialTick))" in renderer
assert "drawRedEyes(pose,t)" in renderer
assert "getThirdPhaseGrowth(float partialTick)" in entity
assert "redEyes=finish(b)" in detail and "spikes=finish(b)" in detail
assert "getBossPhase() >= 3" in renderer
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
print("OK: 9 new items and recipes, armor set, heart weak point, red eyes,")
print("    growing ice spines, dedicated textures and corrected singular structure path")
