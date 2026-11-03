#!/usr/bin/env python3
"""Fast static checks for 1.20.1 resources (not a game runtime test)."""
from pathlib import Path
import json
import sys

root = Path("src/main/resources")
errors = []
files = list(root.rglob("*.json"))
for path in files:
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except Exception as exc:
        errors.append(f"{path}: invalid JSON: {exc}")
        continue
    if "/models/" in str(path).replace("\\", "/"):
        for cube in data.get("elements", []):
            for axis, low, high in zip("xyz", cube.get("from", []), cube.get("to", [])):
                if not isinstance(low, (int, float)) or not isinstance(high, (int, float)) or low >= high or low < -16 or high > 32:
                    errors.append(f"{path}: invalid {axis} cube {low}..{high}")
    if "/advancements/" in str(path).replace("\\", "/") and "criteria" not in data:
        errors.append(f"{path}: advancement missing criteria")
geo = root / "assets/wendigosfrost/geo/wendigo.geo.json"
animations = root / "assets/wendigosfrost/animations/wendigo.animation.json"
if not geo.is_file() or not animations.is_file():
    errors.append("Missing boss model or animation")
else:
    g = json.loads(geo.read_text(encoding="utf-8"))["minecraft:geometry"][0]
    a = json.loads(animations.read_text(encoding="utf-8"))["animations"]
    names = {bone["name"] for bone in g["bones"]}
    for essential in ["body", "head", "arm_left", "arm_right", "leg_left", "leg_right"]:
        if essential not in names:
            errors.append("Missing animated bone " + essential)
    for essential in ["idle", "walk", "attack", "roar", "stomp"]:
        if "animation.wendigo." + essential not in a:
            errors.append("Missing animation " + essential)
for required in [
    "assets/wendigosfrost/models/item/frost_ward.json",
    "assets/wendigosfrost/models/item/guide_book.json",
    "data/wendigosfrost/recipes/frost_ward.json",
    "data/wendigosfrost/advancements/freedom.json"
]:
    if not (root / required).exists():
        errors.append("Missing resource " + required)

# Relics now use the actual uploaded Blockbench geometry through Forge OBJ.
for item in ("wendigo_horn","frozen_heart","frost_ward"):
    file=root/"assets/wendigosfrost/models/item"/(item+".json")
    if not file.exists():
        errors.append("Missing Blockbench relic model "+item)
        continue
    model=json.loads(file.read_text(encoding="utf-8"))
    expected="wendigosfrost:models/item/bb/"+item+".obj"
    if model.get("loader")!="forge:obj" or model.get("model")!=expected:
        errors.append(item+": expected original Forge OBJ mesh")
    for extension in ("obj","mtl"):
        if not (root/"assets/wendigosfrost/models/item/bb"/(item+"."+extension)).is_file():
            errors.append(item+": missing BB artifact ."+extension)

if errors:
    print("\n".join(errors))
    sys.exit(1)
print(f"OK: {len(files)} resource JSON files, boss skeleton and new progression files")
