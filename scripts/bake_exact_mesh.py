#!/usr/bin/env python3
"""Bake exactly the original glTF triangle positions into a GPU-friendly static stream.
No decimation, remeshing, vertex smoothing or re-topology is applied.
The original GLB remains unchanged in the JAR for Blender editing.
"""
from pathlib import Path
import struct, json, math, hashlib

root = Path(__file__).resolve().parents[1]
source = root/"src/main/resources/assets/wendigosfrost/models/gltf/wendigo_glacial_exact.glb"
target = root/"src/main/resources/assets/wendigosfrost/models/mesh/wendigo_glacial.wfmesh"

data=source.read_bytes()
assert hashlib.sha256(data).hexdigest()=="e40580c2e51d711878745bed9a9601f8ae6be38f80716c7715c099bc44f6f1d1"
length,ctype=struct.unpack_from("<I4s",data,12)
assert ctype==b"JSON"
gltf=json.loads(data[20:20+length].decode("utf-8"))
bin_start=20+length
bin_len,bin_type=struct.unpack_from("<I4s",data,bin_start)
assert bin_type == b"BIN" + bytes([0])
payload=data[bin_start+8:bin_start+8+bin_len]
views=gltf["bufferViews"]
accessors=gltf["accessors"]
def accessor(i):
    a=accessors[i]
    view=views[a["bufferView"]]
    assert "sparse" not in a
    width={"SCALAR":1,"VEC3":3}[a["type"]]
    datatype={5125:"I",5123:"H",5126:"f"}[a["componentType"]]
    itemsize=struct.calcsize("<"+datatype)
    stride=view.get("byteStride",width*itemsize)
    offset=view.get("byteOffset",0)+a.get("byteOffset",0)
    fmt="<"+datatype*width
    return [struct.unpack_from(fmt,payload,offset+j*stride) for j in range(a["count"])]

target.parent.mkdir(parents=True,exist_ok=True)
num=0
with target.open("wb") as out:
    out.write(b"WFMS")
    out.write(struct.pack("<I",0))
    for node in gltf["nodes"]:
        if "mesh" not in node:
            continue
        if any(k in node for k in ("matrix","rotation","translation","scale")):
            raise ValueError("Mesh has a node transform: implement transform before baking")
        mesh=gltf["meshes"][node["mesh"]]
        for prim in mesh["primitives"]:
            assert prim.get("mode",4)==4
            pos=accessor(prim["attributes"]["POSITION"])
            indexes=[x[0] for x in accessor(prim["indices"])]
            mat=gltf["materials"][prim["material"]]
            rgba=mat["pbrMetallicRoughness"].get("baseColorFactor",[1,1,1,1])
            color=bytes(round(max(0,min(1,c))*255) for c in rgba)
            for j in range(0,len(indexes),3):
                p0,p1,p2=[pos[indexes[j+k]] for k in (0,1,2)]
                u=[p1[k]-p0[k] for k in range(3)]
                v=[p2[k]-p0[k] for k in range(3)]
                n=[u[1]*v[2]-u[2]*v[1],u[2]*v[0]-u[0]*v[2],u[0]*v[1]-u[1]*v[0]]
                mag=math.sqrt(sum(t*t for t in n)) or 1.
                n=[float(t/mag) for t in n]
                out.write(color)
                out.write(struct.pack("<3f",*n))
                for p in (p0,p1,p2):
                    out.write(struct.pack("<3f",*p))
                num+=1
    out.seek(4)
    out.write(struct.pack("<I",num))
assert num==70020, f"Expected 70,020 triangles, got {num}"
assert target.stat().st_size == 8+num*52
print(f"Exact geometry: {num:,} triangles, {target.stat().st_size:,} bytes")
