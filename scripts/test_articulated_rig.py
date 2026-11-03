#!/usr/bin/env python3
"""Regression tests for lossless original mesh partitioned into animated joints."""
from pathlib import Path
import hashlib, struct

base=Path("src/main/resources/assets/wendigosfrost/models")
glb=(base/"gltf/wendigo_glacial_exact.glb").read_bytes()
rig=(base/"mesh/wendigo_articulado.wfar").read_bytes()
assert hashlib.sha256(glb).hexdigest()=="e40580c2e51d711878745bed9a9601f8ae6be38f80716c7715c099bc44f6f1d1"
assert rig[:4]==b"WFAR"
version,bones=struct.unpack_from("<II",rig,4)
assert version==1 and bones==10
offset=12;sumtri=0;counts=[]
for bone in range(bones):
    idx,number=struct.unpack_from("<II",rig,offset)
    assert idx==bone and number>100, (idx,number)
    offset+=8+number*52
    counts.append(number)
    sumtri+=number
assert offset==len(rig) and sumtri==70020, (offset,len(rig),sumtri)
src=Path("src/main/java/com/exemplo/wendigosfrost/client/WendigoRenderer.java").read_text()
entity=Path("src/main/java/com/exemplo/wendigosfrost/entity/WendigoEntity.java").read_text()
assert "loadBones()" in src and "VertexBuffer[]" in src
assert src.count("drawBone(")>=7
assert "hinge(pose" in src and "frontLeg(pose" in src and "hindLeg(pose" in src
assert "getStandBlend(partialTick)" in src and "getAttackAnim(partialTick)" in src
assert "getStandBlend(float partialTick)" in entity
print("OK: original GLB identical; 70,020 triangles conserved across 10 articulated bones")
print("Anatomical triangle distribution:",counts)
