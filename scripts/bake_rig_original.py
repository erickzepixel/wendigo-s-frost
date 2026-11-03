#!/usr/bin/env python3
"""Bake original GLB triangles to 10 rigid articulated components without changing triangles.
Input identical 70,020-triangle GLB. All stdlib. No triangle loss or decimation.
"""
from pathlib import Path
import struct,json,hashlib,math,collections

SOURCE=Path('src/main/resources/assets/wendigosfrost/models/gltf/wendigo_glacial_exact.glb')
DEST=Path('src/main/resources/assets/wendigosfrost/models/mesh/wendigo_articulado.wfar')
EXPECTED='e40580c2e51d711878745bed9a9601f8ae6be38f80716c7715c099bc44f6f1d1'
data=SOURCE.read_bytes(); assert hashlib.sha256(data).hexdigest()==EXPECTED
json_len,jtype=struct.unpack_from('<I4s',data,12); assert jtype==b'JSON'
doc=json.loads(data[20:20+json_len]); bin_off=20+json_len
bin_len,btype=struct.unpack_from('<I4s',data,bin_off); assert btype==b'BIN'+bytes([0]); raw=data[bin_off+8:bin_off+8+bin_len]

views=doc['bufferViews']; acc=doc['accessors']
def decode(i):
 a=acc[i]; v=views[a['bufferView']]; count=a['count']; width={'SCALAR':1,'VEC2':2,'VEC3':3}[a['type']]
 fmt={5125:'I',5123:'H',5126:'f'}[a['componentType']]; size=struct.calcsize('<'+fmt)
 step=v.get('byteStride',size*width); start=v.get('byteOffset',0)+a.get('byteOffset',0)
 return [struct.unpack_from('<'+fmt*width,raw,start+j*step) for j in range(count)]

def target(mat, c, minimum, maximum):
 x,y,z=c
 # Bone and horn components of head, plus jaw and eyes. Keep spine/ribs on torso.
 if (mat in ('eye','eye_core','blood') or
     (z>4.55 and x>-.1) or (x>2.15 and z>1.85) or
     (mat in ('bone','bone_dark','bone_light','frost','ice','glacier') and x>1.7 and z>3.5)):
  return 1
 if abs(y)>.65 and x>.85 and z<3.6 and mat not in ('bone','bone_dark','bone_light'):
  return (2 if y>0 else 4) if z>1.42 else (3 if y>0 else 5)
 if abs(y)>.52 and x<-1.95 and z<3.0 and mat not in ('bone','bone_dark','bone_light'):
  return (6 if y>0 else 8) if z>1.25 else (7 if y>0 else 9)
 return 0

batches=[[] for _ in range(10)]
components=collections.Counter(); totals=collections.Counter(); examples=collections.defaultdict(list)
for node in doc['nodes']:
 if 'mesh' not in node: continue
 assert not any(k in node for k in ('matrix','translation','rotation','scale'))
 mesh=doc['meshes'][node['mesh']]
 for primitive in mesh['primitives']:
  assert primitive.get('mode',4)==4
  ps=decode(primitive['attributes']['POSITION']); ix=[e[0] for e in decode(primitive['indices'])]
  material=doc['materials'][primitive['material']]; name=material['name']
  rgba=material['pbrMetallicRoughness'].get('baseColorFactor',[1,1,1,1]); rgba=bytes(round(max(0,min(1,v))*255) for v in rgba)
  n=len(ps); parent=list(range(n))
  def find(x):
   while x!=parent[x]:parent[x]=parent[parent[x]];x=parent[x]
   return x
  for i in range(0,len(ix),3):
   a,b,c=ix[i:i+3]; ra,rb,rc=find(a),find(b),find(c)
   parent[rb]=ra;parent[rc]=ra
  chunks=collections.defaultdict(list)
  for i in range(0,len(ix),3):
   a,b,c=ix[i:i+3];chunks[find(a)].append((a,b,c))
  for tris in chunks.values():
   vertices=set(k for face in tris for k in face)
   c=tuple(sum(ps[v][axis] for v in vertices)/len(vertices) for axis in range(3))
   low=tuple(min(ps[v][axis] for v in vertices) for axis in range(3))
   high=tuple(max(ps[v][axis] for v in vertices) for axis in range(3))
   slot=target(name,c,low,high); components[slot]+=1;totals[slot]+=len(tris)
   if len(examples[slot])<12:examples[slot].append((name,len(tris),tuple(round(x,2) for x in c)))
   for a,b,d in tris:
    p0,p1,p2=ps[a],ps[b],ps[d]
    v=[p1[j]-p0[j] for j in range(3)];w=[p2[j]-p0[j] for j in range(3)]
    norm=(v[1]*w[2]-v[2]*w[1],v[2]*w[0]-v[0]*w[2],v[0]*w[1]-v[1]*w[0]);length=math.sqrt(sum(q*q for q in norm)) or 1
    tri=rgba+struct.pack('<3f',*(q/length for q in norm))+struct.pack('<9f',*p0,*p1,*p2)
    assert len(tri)==52;batches[slot].append(tri)
assert sum(map(len,batches))==70020
with DEST.open('wb') as f:
 f.write(b'WFAR'+struct.pack('<II',1,10))
 for slot,items in enumerate(batches):
  f.write(struct.pack('<II',slot,len(items)))
  f.write(b''.join(items))
print('triangles',sum(totals.values()),'bytes',DEST.stat().st_size)
for k in range(10): print(k,'components',components[k], 'tris',totals[k], 'examples',examples[k][:3])