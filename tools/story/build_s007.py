"""Writes the Scene 007 bundle (rod-story/scenes/mc001_s007.json) from the Saga 1 screenplay.

"The Compass That Points Nowhere": spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md.
Hints, captions and the outcome message are new gameplay text. The scene shares the monument site
with Scene 006 (anchor 40, 108, -200); Orin's field table is the camp in the courtyard. Positions
must match world/site/NorthernMonument.java (camp and survey).
Run from the repository root: python tools/story/build_s007.py
"""
import json
import math

S6 = "src/main/resources/rod-story/scenes/mc001_s006.json"
S7 = "src/main/resources/rod-story/scenes/mc001_s007.json"
INDEX = "src/main/resources/rod-story/index.json"

s6 = json.load(open(S6, encoding="utf8"))
locs = {k: v for k, v in s6["scene"]["locations"].items() if k in ("trail_start", "courtyard", "stair_top", "terrace")}
locs.update({
    "field_table": {"pos": [6, 0, 21], "radius": 2},
    "table_top": {"pos": [6, 1, 19], "radius": 1},
    "orin_table": {"pos": [4, 0, 21], "radius": 1},
    "elara_table": {"pos": [8, 0, 21], "radius": 1},
    "pedestal": {"pos": [0, 0, 23], "radius": 2},
    "pedestal_side": {"pos": [1, 0, 22], "radius": 1},
    "pillar_mid": {"pos": [0, 8, 0], "radius": 1},
    "elara_survey": {"pos": [-9, 0, 4], "radius": 1},
})
# The six points of the shape that hangs above the compass, and its centre.
for i in range(6):
    a = math.radians(90 + 60 * i)
    locs[f"star_{i + 1}"] = {"pos": [round(0.75 * math.cos(a), 3), 1.4, round(23 + 0.75 * math.sin(a), 3)], "radius": 1}
locs["star_centre"] = {"pos": [0, 1.4, 23], "radius": 1}

npcs = [
    {"character": "orin", "location": "orin_table", "yaw": 0, "held": "minecraft:compass"},
    {"character": "elara", "location": "elara_table", "yaw": 90},
]

clues = {
    "survey_1": {"name": "Survey stake", "pos": [-13, 0, 8], "block": "minecraft:lodestone"},
    "survey_2": {"name": "Survey stake", "pos": [13, 0, -3], "block": "minecraft:lodestone"},
    "survey_3": {"name": "Survey stake", "pos": [0, 7, -7], "block": "minecraft:lodestone"},
    "survey_map": {"name": "Orin's survey map", "pos": [6, 0, 19], "block": "minecraft:cartography_table"},
    "pedestal": {"name": "Stone pedestal", "pos": [0, 0, 23], "block": "minecraft:chiseled_stone_bricks"},
}


def stay(who, where):
    return [{"type": "npc_spawn", "character": who, "location": where},
            {"type": "npc_stay", "character": who, "location": where}]


star = [{"type": "particles", "particle": "minecraft:end_rod", "location": f"star_{i + 1}", "count": 3, "spread": 0.02}
        for i in range(6)] + [{"type": "particles", "particle": "minecraft:enchant", "location": "star_centre", "count": 12, "spread": 0.3}]

scene = {
    "id": "mc001_s007", "title": "The Compass That Points Nowhere", "universe": "MC-001", "saga": 1, "scene": 7,
    "canon_status": "Saga 1 screenplay, Version 1 (docs/story/SAGA_1_SCREENPLAY.md): spoken dialogue verbatim. "
                    "Hints, captions and the outcome message are new gameplay text. Played at Orin's field table "
                    "in the monument courtyard.",
    "dimension": "realitiesofdreams:mc_001", "site": "northern_monument", "anchor": [40, 108, -200],
    "arrival": {"pos": [0, 0, 20], "yaw": 180},
    "next_scene": "unreleased:mc001_s008",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "to_table", "objective": "Go back to Orin's field table", "target": "field_table", "checkpoint": "courtyard",
         "on_enter": [{"type": "world_change", "id": "mc001_s007_survey"}, *stay("orin", "orin_table"), *stay("elara", "elara_table")],
         "complete": {"type": "reach", "value": "field_table"},
         "on_complete": [{"type": "start_dialogue", "id": "three_compasses"}]},
        {"id": "table_talk", "checkpoint": "field_table",
         "complete": {"type": "dialogue_end", "value": "three_compasses"},
         "on_complete": [{"type": "start_cinematic", "id": "mc001_s007_needles"}]},
        {"id": "needles", "checkpoint": "field_table",
         "complete": {"type": "cinematic_end", "value": "mc001_s007_needles"},
         "on_complete": [{"type": "start_dialogue", "id": "buried"}]},
        {"id": "buried_talk", "checkpoint": "field_table",
         "complete": {"type": "dialogue_end", "value": "buried"},
         "on_complete": [{"type": "give_item", "item": "minecraft:compass", "count": 1, "name": "Anomaly compass"},
                         {"type": "npc_move", "character": "elara", "location": "elara_survey"}]},
        {"id": "readings", "objective": "Take a reading at each of Orin's survey stakes", "target": "courtyard", "checkpoint": "field_table",
         "on_enter": [{"type": "hint", "ticks": 200, "text": "Orin's anomaly compass reads the signal. Press {use} on each lodestone survey stake."}],
         "complete": {"type": "clues", "value": "survey_1,survey_2,survey_3"},
         "on_complete": [{"type": "sound", "sound": "minecraft:block.respawn_anchor.deplete", "volume": 0.6, "pitch": 0.6},
                         {"type": "message", "text": "The needle tips over and points straight down."},
                         {"type": "npc_spawn", "character": "orin", "location": "orin_table"},
                         {"type": "npc_spawn", "character": "elara", "location": "elara_table"},
                         {"type": "start_dialogue", "id": "below"}]},
        {"id": "below_talk", "checkpoint": "field_table",
         "complete": {"type": "dialogue_end", "value": "below"}},
        {"id": "compare", "objective": "Compare the three readings on Orin's survey map", "target": "field_table", "checkpoint": "field_table",
         "complete": {"type": "clues", "value": "survey_map"}},
        {"id": "pedestal", "objective": "Set the compass on the stone pedestal", "target": "pedestal", "checkpoint": "field_table",
         "on_enter": [*stay("orin", "pedestal_side"), {"type": "hint", "ticks": 140, "text": "Press {use} on the pedestal."}],
         "complete": {"type": "clues", "value": "pedestal"},
         "on_complete": [{"type": "start_cinematic", "id": "mc001_s007_projection"}]},
        {"id": "projection", "checkpoint": "field_table",
         "complete": {"type": "cinematic_end", "value": "mc001_s007_projection"},
         "on_complete": [{"type": "start_dialogue", "id": "six_points"}]},
        {"id": "six_talk", "checkpoint": "field_table",
         "complete": {"type": "dialogue_end", "value": "six_points"},
         "on_complete": [
             {"type": "title", "title": "Mission Complete", "subtitle": "A Direction Without North"},
             {"type": "sound", "sound": "minecraft:ui.toast.challenge_complete", "volume": 0.7},
             {"type": "journal", "id": "anomaly_compass"},
             {"type": "message", "text": "The three readings line up on a route through Awakening Valley, toward a second ancient survey site. Unlocked: the anomaly compass."},
             {"type": "set_flag", "flag": "mc001.anomaly_compass"},
             {"type": "set_flag", "flag": "mc001.s007.complete"}]},
    ],
    "triggers": [], "area_triggers": [], "chatter": [],
}


def lines(*ls):
    ids = "abcdefghijklmnop"
    out = {}
    for i, (speaker, text) in enumerate(ls):
        d = {"speaker": speaker, "text": text}
        if i + 1 < len(ls):
            d["next"] = ids[i + 1]
        out[ids[i]] = d
    return {"critical": True, "lines": out}


S, E, O = "primordial_steve", "elara", "orin"
dialogues = {
    "three_compasses": lines(
        (O, "I've tried three compasses. Every single one is wrong."), (S, "Maybe they're all broken."),
        (O, "All three? At exactly the same time?"), (E, "Orin, what are they pointing toward?")),
    "buried": lines((O, "Whatever is buried beneath that structure.")),
    "below": lines(
        (S, "The signal isn't ahead of us."), (O, "Then where?"), (S, "Below."), (E, "Below the monument?"),
        (S, "Farther than that, I think.")),
    "six_points": lines(
        (O, "Did you both see that?"), (E, "Six points."), (S, "Like the monument carving."),
        (O, "We're going to need information from outside Firstbrook."),
        (E, "Maybe the other kingdoms have seen these symbols.")),
}

cinematics = {
    "mc001_s007_needles": {
        "duration": 150, "fade_in": 8, "fade_out": 10, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [6.5, 3.4, 21.6], "yaw": 180, "pitch": 62, "fov": 50},
            {"t": 60, "pos": [6.5, 3.4, 21.6], "yaw": 180, "pitch": 58, "fov": 46},
            {"t": 150, "pos": [6.0, 2.6, 22.5], "yaw": 163, "pitch": -12, "fov": 50}],
        "subtitles": [{"t": 20, "duration": 90, "text": "Every needle turns toward the monument.", "style": "caption"}],
        "sounds": [{"t": 15, "sound": "minecraft:block.amethyst_block.chime", "volume": 0.6, "pitch": 0.6},
                   {"t": 60, "sound": "minecraft:block.beacon.ambient", "volume": 0.4, "pitch": 0.5}],
        "cues": [{"t": 70, "actions": [{"type": "particles", "particle": "minecraft:end_rod", "location": "pillar_mid", "count": 20, "spread": 1}]}],
    },
    "mc001_s007_projection": {
        "duration": 170, "fade_in": 8, "fade_out": 0, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [2.5, 2.2, 26.0], "yaw": 140, "pitch": 10, "fov": 55},
            {"t": 170, "pos": [2.0, 2.0, 25.3], "yaw": 140, "pitch": 6, "fov": 45}],
        "subtitles": [],
        "sounds": [{"t": 0, "sound": "minecraft:item.lodestone_compass.lock", "volume": 0.8},
                   {"t": 5, "sound": "minecraft:block.grindstone.use", "volume": 0.3, "pitch": 1.8},
                   {"t": 60, "sound": "minecraft:block.amethyst_block.resonate", "volume": 0.9, "pitch": 0.7},
                   {"t": 120, "sound": "minecraft:block.beacon.deactivate", "volume": 0.6, "pitch": 1.2}],
        "cues": [{"t": 60, "actions": star}, {"t": 72, "actions": star}, {"t": 84, "actions": star}, {"t": 96, "actions": star},
                 {"t": 100, "actions": [{"type": "npc_look", "character": "orin", "location": "star_centre"}]},
                 {"t": 108, "actions": star}],
    },
}

journal = {"anomaly_compass": {"title": "A Direction Without North", "text": (
    "Three of Orin's compasses, all wrong the same way: every needle turned to the monument. He gave me one he has "
    "been altering, and at each survey stake it read differently, until it tipped over and pointed down. The signal "
    "is below the monument, and farther. On the pedestal the needle spun, stopped, and a six-pointed shape hung "
    "above it for a moment, like the carving. We need to know whether the other kingdoms have seen these symbols. "
    "The readings point through Awakening Valley.")}}

json.dump({"scene": scene, "dialogues": dialogues, "cinematics": cinematics, "journal": journal},
          open(S7, "w", encoding="utf8"), indent=2, ensure_ascii=False)
s6["scene"]["next_scene"] = "mc001_s007"
json.dump(s6, open(S6, "w", encoding="utf8"), indent=2, ensure_ascii=False)
index = json.load(open(INDEX))
if "mc001_s007.json" not in index["scenes"]:
    index["scenes"].append("mc001_s007.json")
json.dump(index, open(INDEX, "w"), indent=2)
print("wrote", S7)
