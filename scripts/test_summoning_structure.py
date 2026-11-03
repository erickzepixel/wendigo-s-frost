#!/usr/bin/env python3
"""Static integration regression checks for the alternate summon. Runtime game testing is still required."""
from pathlib import Path
import json

java=Path("src/main/java/com/exemplo/wendigosfrost")
ritual=(java/"ritual/WendigoSummoningRitual.java").read_text(encoding="utf-8")
block=(java/"block/DeepGlacierPedestal.java").read_text(encoding="utf-8")
block_entity=(java/"blockentity/PedestalBlockEntity.java").read_text(encoding="utf-8")
event=(java/"event/SummoningInteractEvents.java").read_text(encoding="utf-8")
book=(java/"client/SurvivorManualScreen.java").read_text(encoding="utf-8")
required={
    "4 lit cardinal campfires": "Blocks.SOUL_CAMPFIRE" in ritual and "CampfireBlock.LIT" in ritual and "center.relative(dir, 2)" in ritual,
    "4 diagonal blue ice blocks": "Blocks.BLUE_ICE" in ritual and "for (int dx : new int[]{-1, 1})" in ritual and "for (int dz : new int[]{-1, 1})" in ritual,
    "safe spawn collision": "level.noCollision(boss)" in ritual and "level.hasChunkAt(target)" in ritual,
    "no crystal consumption on failure": ritual.index("if (!found)") < ritual.index("offering.shrink(1)"),
    "2 minute cooldown saved": 'tag.putLong("SummonCooldownUntil"' in block_entity and 'tag.getLong("SummonCooldownUntil")' in block_entity and "2400L" in block_entity,
    "separate cleansing ritual": "be.start(sp)" in block and "be.insert(stack)" in block,
    "sneak right click event": "event.getEntity().isShiftKeyDown()" in event and "event.setCanceled(true)" in event,
    "survival guide ritual diagram": "Fogueiras" in book and "GELO AZUL" in book and "pedestal no centro" in book,
    "no link to marked player": "boss.setOwnerId" not in ritual,
    "summoning advancement": Path("src/main/resources/data/wendigosfrost/advancements/summon_ritual.json").exists()
}
for lang in ("pt_br","en_us"):
    data=json.loads(Path(f"src/main/resources/assets/wendigosfrost/lang/{lang}.json").read_text(encoding="utf-8"))
    required[f"{lang} translations"] = all("message.wendigosfrost.summon_"+term in data
        for term in ("structure","cooldown","existing","failed","space","success","busy"))
for check,passed in required.items():
    print(("PASS" if passed else "FAIL")+": "+check)
assert all(required.values()), "Summoning ritual integration regression found"
