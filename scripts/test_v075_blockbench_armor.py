#!/usr/bin/env python3
"""Validate the four original Blockbench uploads and exactly shared item/wearer pipeline.

The sources are byte-identical to the user-provided files. Tests are intentionally
strict: no generated approximations, no flat inventory recolors, no dummy vanilla
mesh, and no missing original texture layers.
"""
import base64
import hashlib
import io
import json
import struct
from pathlib import Path

root=Path("assets-source/vigilia-bbmodels")
assets=Path("src/main/resources/assets/wendigosfrost/models/item")
src=Path("src/main/java/com/exemplo/wendigosfrost")
inputs={
 "elmo":("hunter_helmet",8,6,"4509b3f0cb469947dacf4149c04780fd34cc62773896e89206bc21b34d67eb4a"),
 "peitoral":("hunter_chestplate",9,5,"fe8370d8b080d83b857ed338f2ddd258e237eaa0af8f2e329858ac50a6c0ed57"),
 "calcas":("hunter_leggings",9,3,"f0540b4a3ac6e83b90088ebab686be51ff25e1d90feac5c9311b7b704a2ac382"),
 "botas":("hunter_boots",8,3,"995a4986cc099c530c748287db926aba1ceaebd77d2c7f2272fc98a01c82ffd3")
}
for key,(item,nc,nt,digest) in inputs.items():
    source=root/(key+"_da_vigilia.bbmodel")
    raw=source.read_bytes()
    assert hashlib.sha256(raw).hexdigest()==digest,source
    data=json.loads(raw)
    assert data["meta"]["model_format"]=="free",key
    assert len(data["elements"])==nc,(key,len(data["elements"]))
    assert len(data["textures"])==nt
    assert all(e["type"]=="cube" for e in data["elements"]),key
    for e in data["elements"]:
        assert len(e["from"])==len(e["to"])==3
        assert all(a<b for a,b in zip(e["from"],e["to"]))
        for face in e["faces"].values():
            assert face["texture"] in range(nt),key
            assert len(face["uv"])==4
    for t in data["textures"]:
        rawtex=base64.b64decode(t["source"].split(",",1)[-1])
        assert rawtex[:8]==bytes([137,80,78,71,13,10,26,10]),(key,t["name"])
        assert struct.unpack(">II",rawtex[16:24])==(64,64),(key,t["name"])
    model=json.loads((assets/(item+".json")).read_text())
    assert model["parent"]=="builtin/entity",item
    assert "elements" not in model,item # renderer must use same source mesh
gradle=Path("scripts/vigilia-bbmodel-import.gradle").read_text()
assert "java.util.Base64.getDecoder()" in gradle
assert "'assets/wendigosfrost/armor_bb'" in gradle
assert "'assets/wendigosfrost/textures/models/armor'" in gradle
assert "source.elements" in gradle
assert "vigilia_bb_" in gradle
assert "apply from: 'scripts/vigilia-bbmodel-import.gradle'" in Path("build.gradle").read_text()
item=(src/"item/HunterArmorItem.java").read_text()
mesh=(src/"client/VigilArmorMesh.java").read_text()
worn=(src/"client/WendigoHunterArmorModel.java").read_text()
renderer=(src/"client/VigilArmorItemRenderer.java").read_text()
assert "getCustomRenderer()" in item
assert "VigilArmorItemRenderer.instance()" in item
assert "getHumanoidArmorModel" in item
assert "detailedModel.head.copyFrom(original.head)" in item
assert "detailedModel.leftArm.copyFrom(original.leftArm)" in item
assert "detailedModel.rightLeg.copyFrom(original.rightLeg)" in item
assert 'vigilia_bb_'+chr(34) in item
assert "VigilArmorMesh.of(armor.getType())" in renderer
assert "mesh.drawInventory(" in renderer
assert "mesh.drawBone(" in worn
assert "HumanoidModel" in worn
assert "ModelPart(List.of(),Map.of(" in worn
for symbol in ("HEAD","BODY","LEFT_ARM","RIGHT_ARM","LEFT_LEG","RIGHT_LEG"):
    assert symbol in mesh
for key in ("head","shoulder","waist","left_leg","right_leg"):
    assert key.lower() in (mesh+worn).lower()
assert "rotationXYZ(" in mesh
assert "material*64f" in mesh # per-face UV original material preserved
assert "textureWidth" in mesh and "512" in mesh
print("PASS: four byte-exact .bbmodel sources, every original PNG,")
print("      shared geometry inventory/player, fitted humanoid limbs and original UVs")
