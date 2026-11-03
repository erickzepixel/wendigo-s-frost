#!/usr/bin/env python3
"""Validate actual generated NBT templates and locatable datapack entries in release JAR."""
import gzip
import json
from pathlib import Path
import sys
import zipfile

jar=Path(sys.argv[1] if len(sys.argv)>1 else "build/libs/wendigosfrost-0.7.2.jar")
names=("ancient_ice_shrine","hunter_cairn","frozen_sanctuary")
with zipfile.ZipFile(jar) as z:
    included=set(z.namelist())
    for name in names:
        asset="data/wendigosfrost/structure/"+name+".nbt"
        assert asset in included, "Missing Jigsaw template: "+asset
        raw=z.read(asset)
        assert raw[:2]==bytes([31,139]), "Template must be GZip NBT: "+name
        nbt=gzip.decompress(raw)
        assert nbt[:3]==bytes([10,0,0]), "NBT root must be Compound: "+name
        assert b"palette" in nbt and b"blocks" in nbt and b"entities" in nbt
        assert b"minecraft:chest" in nbt, "Missing chest in "+name
        assert b"LootTable" in nbt and ("wendigosfrost:chests/"+name).encode() in nbt
        assert len(nbt)>3000, "Suspiciously short structure template "+name
        for entry in ("worldgen/structure/"+name+".json",
                      "worldgen/structure_set/"+name+".json",
                      "worldgen/template_pool/"+name+".json",
                      "tags/worldgen/biome/has_structure/"+name+".json",
                      "loot_tables/chests/"+name+".json"):
            assert "data/wendigosfrost/"+entry in included, entry
        loot=json.loads(z.read("data/wendigosfrost/loot_tables/chests/"+name+".json"))
        assert loot["pools"][0]["entries"][0]["name"]=="wendigosfrost:frostbloom_seeds"
        print(name, "template",len(raw),"bytes; loot guaranteed; /locate registry included")
print("PASS: three valid GZIP/NBT templates with loot, registry keys and structure sets in JAR")
