"""Writes the Scene 004 bundle (rod-story/scenes/mc001_s004.json) from the Saga 1 screenplay.

"The Craftsman's Warning": spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md. The
survey-kit hint and the outcome message are new gameplay text. The kit is real crafting: the stage
completes when the player carries every item (an ITEMS stage).
Run from the repository root: python tools/story/build_s004.py
"""
import copy
import json

S1 = "src/main/resources/rod-story/scenes/mc001_s001.json"
S3 = "src/main/resources/rod-story/scenes/mc001_s003.json"
S4 = "src/main/resources/rod-story/scenes/mc001_s004.json"
INDEX = "src/main/resources/rod-story/index.json"

s1 = json.load(open(S1, encoding="utf8"))["scene"]
s3 = json.load(open(S3, encoding="utf8"))
sc3 = s3["scene"]

locs = copy.deepcopy(sc3["locations"])
locs.update({
    "workshop_inside": {"pos": [11, 0, -6], "radius": 3},
    "workbench_front": {"pos": [11, 0, -7], "radius": 2},
    "orin_table": {"pos": [11, 0, -8], "radius": 1},
    "survey_table": {"pos": [12, 1, -6], "radius": 1},
    "workshop_front": {"pos": [11, 0, -3], "radius": 2},
})

npcs = []
for n in sc3["npcs"]:
    c = n["character"]
    n = copy.deepcopy(n)
    if c == "bram":
        n = copy.deepcopy(next(p for p in s1["npcs"] if p["character"] == "bram"))
    npcs.append(n)

KIT = "minecraft:compass,minecraft:lead,minecraft:white_dye,minecraft:torch*8,minecraft:iron_pickaxe"

scene = {
    "id": "mc001_s004", "title": "The Craftsman's Warning", "universe": "MC-001", "saga": 1, "scene": 4,
    "canon_status": "Saga 1 screenplay, Version 1 (docs/story/SAGA_1_SCREENPLAY.md): spoken dialogue verbatim. "
                    "The survey-kit hint and the outcome message are new gameplay text.",
    "dimension": "realitiesofdreams:mc_001", "site": "firstbrook", "anchor": [0, 64, 0],
    "arrival": {"pos": [11, 0, -2], "yaw": 180},
    "next_scene": "mc001_s005",
    "locations": locs, "npcs": npcs, "clues": {},
    "stages": [
        {"id": "to_workshop", "objective": "Take the news to Orin's workshop", "target": "workshop_inside", "checkpoint": "workshop_front",
         "on_enter": [{"type": "npc_follow", "character": "elara"}],
         "complete": {"type": "reach", "value": "workshop_inside"},
         "on_complete": [{"type": "start_dialogue", "id": "orin_markings"}]},
        {"id": "markings_talk", "checkpoint": "workshop_inside",
         "complete": {"type": "dialogue_end", "value": "orin_markings"}},
        {"id": "kit", "objective": "Assemble the survey kit", "target": "workbench_front", "checkpoint": "workshop_inside",
         "on_enter": [{"type": "world_change", "id": "mc001_s004_supplies"},
                      {"type": "hint", "ticks": 260,
                       "text": "Survey kit: a compass, rope (a lead), chalk (white dye), 8 torches and an iron pickaxe. "
                               "Supplies are in the chest; craft them at the workbench. The pickaxe hangs on the workshop's back wall."}],
         "complete": {"type": "items", "value": KIT}},
        {"id": "compass", "checkpoint": "workshop_inside",
         "on_enter": [{"type": "start_cinematic", "id": "mc001_s004_compass"}],
         "complete": {"type": "cinematic_end", "value": "mc001_s004_compass"},
         "on_complete": [{"type": "start_dialogue", "id": "not_normal"}]},
        {"id": "kit_done", "checkpoint": "workshop_inside",
         "complete": {"type": "dialogue_end", "value": "not_normal"},
         "on_complete": [
             {"type": "title", "title": "Mission Complete", "subtitle": "Tools for the Unknown"},
             {"type": "sound", "sound": "minecraft:ui.toast.challenge_complete", "volume": 0.7},
             {"type": "journal", "id": "survey_kit"},
             {"type": "message", "text": "Orin joins the investigation as technical adviser. His workshop is now your equipment and repair station."},
             {"type": "set_flag", "flag": "mc001.orin.adviser"},
             {"type": "set_flag", "flag": "mc001.unlock.repair_station"},
             {"type": "set_flag", "flag": "mc001.s004.complete"}]},
    ],
    "triggers": [],
    "area_triggers": [
        {"id": "pickaxe_warning", "location": "workbench_front", "stages": ["kit"], "actions": [{"type": "start_dialogue", "id": "orin_pickaxe"}]},
    ],
    "chatter": [],
}


def lines(*ls):
    ids = "abcdefghijklmnop"
    out = {}
    for i, entry in enumerate(ls):
        speaker, text = entry[0], entry[1]
        d = {"speaker": speaker, "text": text}
        if len(entry) > 2:
            d["actions"] = entry[2]
        if i + 1 < len(ls):
            d["next"] = ids[i + 1]
        out[ids[i]] = d
    return {"critical": True, "lines": out}


S, E, O = "primordial_steve", "elara", "orin"
dialogues = {
    "orin_markings": lines(
        (O, "Before you ask, no, I haven't worked out what happened."), (S, "We haven't asked yet."),
        (O, "Your faces asked for you."), (E, "We found markings near the northern path."),
        (O, "Markings?", [{"type": "world_change", "id": "mc001_s004_sketch"}]),
        (O, "Where did you find that?"), (S, "On a rock near the ridge."), (O, "That's not a natural crack.")),
    "orin_pickaxe": lines(
        (O, "You can break most stone with a good pickaxe."), (S, "That's usually the idea."), (O, "This time, don't."),
        (E, "He's serious, Steve."), (O, "Very serious. I want measurements before somebody destroys the evidence.")),
    "not_normal": lines(
        (S, "That's not normal."), (O, "Glad we've established that.")),
}

cinematics = {
    "mc001_s004_compass": {
        "duration": 120, "fade_in": 10, "fade_out": 10, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [9.6, 2.3, -4.4], "yaw": -123, "pitch": 18, "fov": 55},
            {"t": 120, "pos": [10.4, 2.0, -5.0], "yaw": -126, "pitch": 22, "fov": 42}],
        "subtitles": [{"t": 10, "duration": 40, "text": "The needle spins toward the paper.", "style": "caption"}],
        "sounds": [
            {"t": 12, "sound": "minecraft:item.lodestone_compass.lock", "volume": 1.0, "pitch": 0.7},
            {"t": 50, "sound": "minecraft:block.anvil.land", "volume": 0.25, "pitch": 0.5},
            {"t": 52, "sound": "minecraft:block.amethyst_block.resonate", "volume": 0.9, "pitch": 0.5},
            {"t": 70, "sound": "minecraft:block.chain.step", "volume": 0.8, "pitch": 0.6}],
        "shakes": [{"t": 50, "duration": 30, "intensity": 0.04}],
        "cues": [{"t": 0, "actions": [{"type": "world_change", "id": "mc001_s004_compass"},
                                      {"type": "npc_spawn", "character": "orin", "location": "orin_table"},
                                      {"type": "npc_look", "character": "orin", "location": "survey_table"}]}],
    }
}

journal = {"survey_kit": {"title": "Tools for the Unknown", "text": (
    "Orin knew something was wrong before we said a word. When Elara showed him her sketch of the markings he put "
    "his hammer down: not a natural crack, he said. We put together a survey kit: compass, rope, chalk, torches and "
    "a pickaxe I'm told not to use on anything that matters. Then Orin held the compass near the sketch and the "
    "needle swung round to the paper while the whole workshop hummed. Orin is with us now, as our adviser, and his "
    "workshop is where we'll mend and supply.")}}

json.dump({"scene": scene, "dialogues": dialogues, "cinematics": cinematics, "journal": journal},
          open(S4, "w", encoding="utf8"), indent=2, ensure_ascii=False)
sc3["next_scene"] = "mc001_s004"
json.dump(s3, open(S3, "w", encoding="utf8"), indent=2, ensure_ascii=False)
index = json.load(open(INDEX))
if "mc001_s004.json" not in index["scenes"]:
    index["scenes"].append("mc001_s004.json")
json.dump(index, open(INDEX, "w"), indent=2)
print("wrote", S4)
