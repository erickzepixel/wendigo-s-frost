#!/usr/bin/env python3
"""Static regression checks; CI compilation follows. Runtime combat still needs game testing."""
import json
from pathlib import Path
base=Path("src/main/resources")
src=Path("src/main/java/com/exemplo/wendigosfrost")
geo=json.loads((base/"assets/wendigosfrost/geo/wendigo.geo.json").read_text())
anim=json.loads((base/"assets/wendigosfrost/animations/wendigo.animation.json").read_text())
loot=json.loads((base/"data/wendigosfrost/loot_tables/entities/wendigo.json").read_text())
recipe=json.loads((base/"data/wendigosfrost/recipes/frost_ward.json").read_text())
entity=(src/"entity/WendigoEntity.java").read_text()
creative=(src/"registry/ModCreativeTabs.java").read_text()
main=(src/"WendigosFrost.java").read_text()
bones=geo["minecraft:geometry"][0]["bones"]
assert len(bones)>=7
assert sum(len(b.get("cubes",[])) for b in bones)<=120, "Too many tiny cubes: optimize silhouette"
for bn in ("body","head","arm_left","arm_right","leg_left","leg_right"):
    assert any(b["name"]==bn for b in bones), bn
for clip in ("quad_idle","quad_walk","quad_roar","rise","idle","walk","attack","roar","stomp"):
    assert "animation.wendigo."+clip in anim["animations"], clip
assert "isQuadruped()" in entity and "EntityDimensions.scalable(2.75F, 2.85F)" in entity
assert "entityData.set(RISE_UNTIL" in entity and "refreshDimensions()" in entity
assert "ModCreativeTabs.TABS.register(bus)" in main
assert "Registries.CREATIVE_MODE_TAB" in creative
for key in ("FROSTBLOOM_SEEDS","FROSTBLOOM_CRYSTAL","WENDIGO_HORN","FROZEN_HEART",
            "FROST_WARD","PEDESTAL_ITEM","GUIDE_BOOK","WENDIGO_SPAWN_EGG"):
    assert "ModItems."+key+".get()" in creative, key
hearts=[p for p in loot["pools"] if any(e.get("name")=="wendigosfrost:frozen_heart" for e in p.get("entries",[]))]
assert hearts, "No heart reward"
assert all(not any(c.get("condition") in ("minecraft:random_chance","minecraft:killed_by_player") for c in p.get("conditions",[])) for p in hearts)
assert recipe["type"]=="minecraft:crafting_shaped"
assert recipe["result"]["item"]=="wendigosfrost:frost_ward"
assert recipe["pattern"]==["ICI","PHP","ICI"]
print("OK: biome-independent dual forms, nine animations, custom creative tab, guaranteed heart and ward recipe")
