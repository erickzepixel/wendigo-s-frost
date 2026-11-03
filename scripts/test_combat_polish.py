#!/usr/bin/env python3
"""Static checks for 0.6.1 combat polish. Minecraft gameplay still needs a real client test."""
from pathlib import Path
import json

java=Path("src/main/java/com/exemplo/wendigosfrost")
resources=Path("src/main/resources/assets/wendigosfrost")
entity=(java/"entity/WendigoEntity.java").read_text(encoding="utf-8")
render=(java/"client/WendigoRenderer.java").read_text(encoding="utf-8")
ward=(java/"item/FrostWardItem.java").read_text(encoding="utf-8")
shock=(java/"combat/FrostShockwave.java").read_text(encoding="utf-8")
config=(java/"config/ModConfig.java").read_text(encoding="utf-8")
build=Path("build.gradle").read_text(encoding="utf-8")
model=json.loads((resources/"models/item/frost_ward.json").read_text(encoding="utf-8"))
assert "-86f*stand" in render, "torso no longer rears fully upright"
assert "86f*stand" in render and "headYaw" in render, "head does not compensate upright pose"
assert "stand * 83f" in render, "arms do not hang down in biped phase"
assert "entity.hurtTime > 0" in render and "setShaderColor(1f, flicker" in render, "red hit flash missing"
assert "getSwipeBlend(partialTick)" in render and "SWIPE_COUNTER" in entity, "melee animation is unsynced"
assert "getAttackCue()==3" in render and "getAttackCue()==4" in render, "boss impact poses missing"
assert "FrostShockwave.release(server, this" in entity, "boss does not use shared frost wave"
assert "FrostShockwave.release(server.serverLevel(), server" in ward, "amulet does not use the same attack"
assert "level.sendParticles" in shock and "ParticleTypes.SNOWFLAKE" in shock, "shockwave VFX missing"
assert 'baseMeleeDamage", 5.5' in config
assert "Math.min(ModConfig.WENDIGO_DAMAGE.get(), 7.0)" in entity
assert ".add(Attributes.ARMOR, 4)" in entity
assert model["loader"]=="forge:obj" and model["model"]=="wendigosfrost:models/item/bb/frost_ward.obj"
assert (resources/"models/item/bb/frost_ward.obj").is_file()
assert "frost_ward_atlas.png" in build and "256, 256" in build
assert "public boolean isFoil(" in ward
for lang in ("pt_br","en_us"):
    obj=json.loads((resources/"lang"/(lang+".json")).read_text(encoding="utf-8"))
    assert "message.wendigosfrost.ward_wave" in obj
    assert "tooltip.wendigosfrost.frost_ward" in obj
print("PASS: upright forward-looking biped, hit flash, melee + abilities, balanced damage, textured ability amulet")
