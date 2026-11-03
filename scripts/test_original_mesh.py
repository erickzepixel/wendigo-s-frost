#!/usr/bin/env python3
from pathlib import Path
import hashlib
import struct
import json

root=Path("src/main/resources/assets/wendigosfrost/models/gltf/wendigo_glacial_exact.glb")
assert root.is_file(), "Run restore_original_wendigo_glb.py before the build"
glb=root.read_bytes()
assert hashlib.sha256(glb).hexdigest()=="e40580c2e51d711878745bed9a9601f8ae6be38f80716c7715c099bc44f6f1d1"
magic,version,length=struct.unpack_from("<4sII",glb,0)
assert (magic,version,length)==(b"glTF",2,len(glb))
count,typ=struct.unpack_from("<I4s",glb,12)
assert typ==b"JSON"
document=json.loads(glb[20:20+count].decode())
assert len(document.get("meshes",[]))>=1
assert len(document.get("materials",[]))>=15
renderer=Path("src/main/java/com/exemplo/wendigosfrost/client/WendigoRenderer.java").read_text()
assert "buffer.drawWithShader" in renderer
assert 'models/mesh/wendigo_articulado.wfar' in renderer
mesh=Path("src/main/resources/assets/wendigosfrost/models/mesh/wendigo_glacial.wfmesh")
assert mesh.exists() and mesh.read_bytes()[:4]==b"WFMS", "GPU stream not generated"
assert len(mesh.read_bytes()) == 8 + 70020*52, "Unexpected triangle stream size"
print("OK: GLB SHA-256, glTF 2.0, all materials, native 70,020-triangle GPU stream")
