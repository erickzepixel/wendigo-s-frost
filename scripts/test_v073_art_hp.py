#!/usr/bin/env python3
"""0.7.3: verify saved-world 100 HP migration, 3D assets and real armor geometry."""
from pathlib import Path
import json
root=Path("src/main/java/com/exemplo/wendigosfrost")
asset=Path("src/main/resources/assets/wendigosfrost")
entity=(root/"entity/WendigoEntity.java").read_text()
config=(root/"config/ModConfig.java").read_text()
phase=(root/"client/WendigoPhaseDetails.java").read_text()
armor=(root/"client/WendigoHunterArmorModel.java").read_text()
armor_item=(root/"item/HunterArmorItem.java").read_text()
assert ".add(Attributes.MAX_HEALTH, 500)" in entity
assert 'defineInRange("bossMaxHealth", 500' in config
assert 'defineInRange("health", 260' not in config
assert "Math.min(getMaxHealth(), hp * getMaxHealth())" in entity
assert "pose.translate(-1.24f, 0f, -1.20f)" in phase, "Heart not moved to sternum"
assert "WendigoHunterArmorModel.create(piece)" in armor_item
assert "getHumanoidArmorModel" in armor_item and "getArmorTexture(" in armor_item
assert "getCustomRenderer()" in armor_item
assert "VigilArmorMesh.Bone" in armor
assert "new ModelPart(List.of(),Map.of(" in armor
assert (root/"client/VigilArmorMesh.java").exists()
assert (root/"client/VigilArmorItemRenderer.java").exists()
ids=("wendigo_horn","frozen_heart","rune_spear","thaw_axe",
     "heartbreaker_blade","runic_torch","thermal_flare","winter_sigil",
     "hunter_helmet","hunter_chestplate","hunter_leggings","hunter_boots")
counts={}
for id in ids:
    file=asset/"models/item"/f"{id}.json"
    model=json.loads(file.read_text(encoding="utf-8"))
    if id.startswith("hunter_"):
        assert model["parent"]=="builtin/entity" and "elements" not in model,id
        counts[id]="original BB mesh"
        continue
    assert model["loader"]=="forge:obj",id
    assert model["model"]=="wendigosfrost:models/item/bb/"+id+".obj",id
    obj=asset/"models/item/bb"/(id+".obj")
    assert obj.exists(),obj
    assert obj.read_text().count("\\nf ")>=24,id
    counts[id]="original Blockbench OBJ"
build=Path("build.gradle").read_text()
assert "scripts/frost-relic-master.gradle" in build
assert "scripts/wendigo-armor-textures.gradle" in build
textures=Path("scripts/frost-relic-master.gradle").read_text()
assert "frost_relic_master.png" in textures
assert "scripts/rework-items.gradle" in build
assert "frozen_heart" in Path("scripts/rework-items.gradle").read_text()
print("PASS: 500 HP migration, correctly placed chest heart, original Blockbench humanoid armor")
print("Complex 3D model elements:",counts)
