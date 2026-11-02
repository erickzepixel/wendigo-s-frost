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

# Regression tests for the custom relic atlas models.
for item in ("wendigo_horn", "frozen_heart", "frost_ward"):
    file = root / "assets/wendigosfrost/models/item" / f"{item}.json"
    if not file.exists():
        errors.append(f"missing item model {item}")
        continue
    model = json.loads(file.read_text(encoding="utf-8"))
    if model.get("textures", {}).get("atlas") != "wendigosfrost:item/relic_atlas":
        errors.append(f"{item}: wrong texture atlas reference")
    cubes = model.get("elements", [])
    if len(cubes) < 30:
        errors.append(f"{item}: expected a detailed true-3D model")
    for idx, cube in enumerate(cubes):
        f = cube.get("from", [])
        t = cube.get("to", [])
        if len(f) != 3 or len(t) != 3 or any(not 0 <= a < b <= 16 for a,b in zip(f,t)):
            errors.append(f"{item}: invalid cube #{idx}")
            continue
        if t[2]-f[2] < 0.1:
            errors.append(f"{item}: flat geometry #{idx}")
        for face_name in ("north", "south", "east", "west", "up", "down"):
            data = cube.get("faces", {}).get(face_name, {})
            uv = data.get("uv")
            if data.get("texture") != "#atlas" or not isinstance(uv, list) or len(uv)!=4:
                errors.append(f"{item}: missing UV/atlas in cube #{idx} {face_name}")
                continue
            if not (0 <= uv[0] < uv[2] <= 16 and 0 <= uv[1] < uv[3] <= 16):
                errors.append(f"{item}: UV out of bounds #{idx}")
            # UVs must remain strictly inside one material tile (4x4 model UV units).
            if int(uv[0] // 4) != int((uv[2]-0.0001) // 4) or int(uv[1] // 4) != int((uv[3]-0.0001) // 4):
                errors.append(f"{item}: atlas tile bleeding in cube #{idx}")

if errors:
    print("\n".join(errors))
    sys.exit(1)
print(f"OK: {len(files)} resource JSON files, boss skeleton and new progression files")
