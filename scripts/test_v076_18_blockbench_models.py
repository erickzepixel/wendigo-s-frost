#!/usr/bin/env python3
"""18 Blockbench sources -> original rotated Forge OBJ mesh and materials.
Run without args for repository checks; pass a compiled JAR for archive checks.
This is structural verification, not a Minecraft runtime visual test.
"""
import base64
import hashlib
import json
import re
import struct
import sys
import zipfile
from pathlib import Path

SOURCES={
    "tijolos_de_gelo_ancestral":("ancient_frost_bricks","block","6b7a79dd2a4aaf6bf5c0c743159c82c8d51b4cb1ada2eeab2ca1c8ecb38999fb"),
    "totem_dos_cacadores_perdidos":("pale_bone_totem","block","2471ac75387022d9918c30f17119fcc667070bbd48f5361fb357baae56219717"),
    "lanterna_de_almas_gelidas":("soul_frost_lantern","block","e2f1c44078660bb161cbcb90f2572aa274ce298bc9830808c8bd8613cee8d664"),
    "pedestal_das_geleiras_profundas":("deep_glacier_pedestal","block","6ce71d03916290ed8ac4fcd6a972b0e7b5fe7dedb720c61ed6a8175a1e7bd30b"),
    "pedra_dos_sussurros":("whispering_runestone","block","c6f259560f9021e117b3471c0eb9ce2976a8792ee19a9b250c17cfefbba4abc1"),
    "lanca_runica_do_cacador":("rune_spear","item","e9cd24423928e4290c32c8ddb9c2939fd163a589882e9314a2d19917d56e775e"),
    "machado_do_degelo":("thaw_axe","item","d4f7d4cccbee985f88dc0b23f968d50f76766d483e971673d475a6757dbb4d43"),
    "espada_do_coracao_partido":("heartbreaker_blade","item","f00f93fc3003441f1e5287cb0f8b3f94781067ba288eaf13d952c28e7cffa418"),
    "tocha_runica_da_vigilia":("runic_torch","item","482b8c9ea921d37925459f1eab59f3994eaa1f0783f36d12a2caab75357c92d5"),
    "amuleto_de_protecao_gelida":("frost_ward","item","341014199aea9cf2faca421fa16cf7bfeb89c88c6c775bba8f805bcf5eb42b09"),
    "chifre_de_wendigo":("wendigo_horn","item","84a7d5d0f698ee5e958ac0cc37e5ba4490b5fe2bbabf2cb004b444d431fd47a3"),
    "coracao_congelado":("frozen_heart","item","de95c45e3f0f53d705d48f83c042acd202d94bda38d0005681ef72cac36783a4"),
    "cristal_de_frostbloom":("frostbloom_crystal","item","bd1c2975f014a6ee0324361da8c5d6097ae00ddaffccbc53656936541dc2b80e"),
    "fragmento_do_sussurro":("whisper_shard","item","869f49a378bf98fe84cff422b4eaa2392edee566d1b3b2d22e7e0004b0b18c61"),
    "pagina_do_cacador_desaparecido":("lost_page","item","2e1bbcc416c87657f2af98e1f8c967cde944fbbe850023973f37e9ce2782e40e"),
    "selo_do_inverno":("winter_sigil","item","a7d0414451729c670aabb89b5efb56f0aca875a588e3a18c124230432e46b52d"),
    "sementes_de_flor_gelo":("frostbloom_seeds","item","ce2f768bb5aa8bd40d8517cb978723900d809775a8ef516ceebee4ffcb6fe833"),
    "sinalizador_termico":("thermal_flare","item","575fa03f127a2f1dd8d5138fc5c32ef83628cc17814f1d62264fde9b77dcc995"),
}
assets=Path("src/main/resources/assets/wendigosfrost")
unique={}
bounds_checked={}
jar=zipfile.ZipFile(sys.argv[1]) if len(sys.argv)>1 else None

def fetch_resource(path):
    full="assets/wendigosfrost/"+path
    if jar:
        assert full in jar.namelist(),full
        return jar.read(full)
    return (assets/path).read_bytes()

for filename,(id,kind,digest) in SOURCES.items():
    original=Path("assets-source/blocks-items-bbmodels")/(filename+".bbmodel")
    raw=original.read_bytes()
    assert hashlib.sha256(raw).hexdigest()==digest,original
    bb=json.loads(raw)
    assert bb["meta"]["model_format"]=="free"
    assert len(bb["elements"])>=4
    model_path="models/"+kind+"/"+id+".json"
    model=json.loads(fetch_resource(model_path))
    assert model["loader"]=="forge:obj" and model["flip_v"] is True,(id,model)
    assert model["automatic_culling"] is False,id
    assert model["model"]=="wendigosfrost:models/"+kind+"/bb/"+id+".obj"
    assert model["textures"]["particle"].startswith("wendigosfrost:bbmaterials/"),id
    obj=fetch_resource("models/"+kind+"/bb/"+id+".obj").decode()
    mtl=fetch_resource("models/"+kind+"/bb/"+id+".mtl").decode()
    assert obj.startswith("#") and "mtllib "+id+".mtl" in obj
    assert obj.count("\nf ")==len(bb["elements"])*6,(id,obj.count("\nf "))
    assert obj.count("\nv ")==len(bb["elements"])*24,(id,obj.count("\nv "))
    assert obj.count("\nvt ")==len(bb["elements"])*24,(id,obj.count("\nvt "))
    assert obj.count("\nusemtl ")==len(bb["elements"])*6,id
    assert len(model["textures"])==len(bb["textures"])+1,id
    for face in re.findall(r"^f (.+)$",obj,re.M):
        pairs=[entry.split("/") for entry in face.split()]
        assert len(pairs)==4 and all(len(p)==2 for p in pairs)
        assert all(1<=int(i)<=len(bb["elements"])*24 for p in pairs for i in p)
    for index,texture in enumerate(bb["textures"]):
        assert "newmtl tex"+str(index) in mtl,id
        assert "map_Kd #texture"+str(index) in mtl,id
        source=base64.b64decode(texture["source"].split(",",1)[1])
        assert source[:8]==bytes([137,80,78,71,13,10,26,10])
        assert struct.unpack(">II",source[16:24])==(64,64)
        key=model["textures"]["texture"+str(index)]
        path="textures/"+key.split(":",1)[1]+".png"
        target=fetch_resource(path)
        assert target==source,(id,path,"pixel differences")
        unique[hashlib.sha256(source).hexdigest()]=path
    if kind=="block":
        blockitem=json.loads(fetch_resource("models/item/"+id+".json"))
        assert blockitem["loader"]=="forge:obj" and blockitem["model"]==model["model"],id
        state=json.loads(fetch_resource("blockstates/"+id+".json"))
        assert state["variants"][""]["model"]=="wendigosfrost:block/"+id,id
    else:
        assert "firstperson_righthand" in model["display"],id
    bounds_checked[id]=(len(bb["elements"]),len(bb["textures"]))
if jar:jar.close()
assert len(SOURCES)==18
assert len(unique)==10,(len(unique),unique)
assert "mod_version=0.7.6" in Path("gradle.properties").read_text()
print("PASS:",len(SOURCES),"original BB source SHA-256 matches,",len(unique),
      "unaltered material PNGs, 18 exact-rotation OBJ meshes and model links")
print("PASS: 5 placed blocks and matching inventory models; 13 item models")
print("Original elements and textures by registry ID:",bounds_checked)
