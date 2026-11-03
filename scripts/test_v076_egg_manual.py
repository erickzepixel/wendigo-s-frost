#!/usr/bin/env python3
"""Regression for uploaded Blockbench egg/manual and a usable right-click guide.

The original .bbmodel files and their embedded PNGs are source-of-truth.
Assertions verify byte-for-byte provenance, exact source UV/material mappings,
server-safe GUI opening and no change to Minecraft mod version (0.7.6).
"""
import base64
import hashlib
import json
import struct
from pathlib import Path

asset=Path("src/main/resources/assets/wendigosfrost")
java=Path("src/main/java/com/exemplo/wendigosfrost")
pairs={
    "wendigo_spawn_egg":("ovo_de_invocacao_wendigo",
        "b0bb24c7610e9729d5f28b4bc51ee8e41637d3f56a93b7eef3a5c018fcd5e34e",4),
    "guide_book":("manual_do_sobrevivente",
        "a896f360389b39e5b82923cc2fa230ab5a3c165b60419be0fd4fc14c089654a1",5)
}
for item,(name,checksum,count) in pairs.items():
    src=Path("assets-source/item-bbmodels")/(name+".bbmodel")
    raw=src.read_bytes()
    assert hashlib.sha256(raw).hexdigest()==checksum, name
    bb=json.loads(raw)
    model=json.loads((asset/"models/item"/(item+".json")).read_text())
    assert bb["meta"]["model_format"]=="free",item
    assert len(bb["elements"])==len(model["elements"])==5,item
    assert len(bb["textures"])==count
    assert model["parent"]=="minecraft:block/block"
    assert len(model["textures"])==count,item
    assert model["display"]["gui"]["scale"][0]>=1.0
    for index,texture in enumerate(bb["textures"]):
        assert model["textures"]["t"+str(index)] == (
            "wendigosfrost:item/blockbench/"+item+"_"+str(index))
        out=asset/"textures/item/blockbench"/(item+"_"+str(index)+".png")
        expected=base64.b64decode(texture["source"].split(",",1)[1])
        assert out.is_file(),out
        actual=out.read_bytes()
        assert actual==expected, (out,"image pixels changed")
        assert actual[:8]==bytes([137,80,78,71,13,10,26,10])
        assert struct.unpack(">II",actual[16:24])==(64,64)
    for original,converted in zip(bb["elements"],model["elements"]):
        assert all(x<y for x,y in zip(converted["from"],converted["to"]))
        assert all(0<=x<=16 for x in converted["from"]+converted["to"])
        for side,face in original["faces"].items():
            if face["texture"] is None: continue
            assert converted["faces"][side]["uv"] == face["uv"]
            assert converted["faces"][side]["texture"] == "#t"+str(int(face["texture"]))
print("PASS: two exact user .bbmodel sources, 9 original PNGs, original face UVs, 3D item models")
manual=(java/"item/GuideBookItem.java").read_text()
screen=(java/"client/SurvivorManualScreen.java").read_text()
moditems=(java/"registry/ModItems.java").read_text()
assert 'ITEMS.register("guide_book"' in moditems
assert 'ITEMS.register("wendigo_spawn_egg"' in moditems
assert "ForgeSpawnEggItem" in moditems
assert "DistExecutor.unsafeRunWhenOn(Dist.CLIENT" in manual
assert "SurvivorManualScreen.open()" in manual
assert "getCustomRenderer" not in manual
assert "return InteractionResultHolder.sidedSuccess" in manual
assert "public static void open()" in screen
assert "setScreen(new SurvivorManualScreen())" in screen
assert screen.count("new Chapter(")>=13
assert "500 pontos de vida" in screen
for keyword in ("Alt ar", "Ritual", "Frostbloom", "Wendigo", "armadura",
                "locate structure", "render(", "mouseScrolled", "keyPressed"):
    assert keyword.replace(" ","").lower() in screen.replace(" ","").lower(), keyword
for lang in ("pt_br","en_us"):
    loc=json.loads((asset/"lang"/(lang+".json")).read_text())
    assert loc["tooltip.wendigosfrost.guide_open"]
    assert loc["tooltip.wendigosfrost.guide_chapters"]
props=Path("gradle.properties").read_text()
assert "mod_version=0.7.6" in props
assert "mod_version=0.7.7" not in props
print("PASS: right-click interactive 13-chapter guide, scroll/navigation, 500 HP and unchanged 0.7.6 version")
