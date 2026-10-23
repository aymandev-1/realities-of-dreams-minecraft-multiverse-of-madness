"""Writes the Scene 009 bundle (rod-story/scenes/mc001_s009.json) from the Saga 1 screenplay.

"The Empty Campsite": spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md. Hints,
captions and the outcome message are new gameplay text. The campsite is the east end of the
valley site Scene 008 uses (anchor 90, 96, -150); positions must match world/site/ValleyCrossing.java.
Run from the repository root: python tools/story/build_s009.py
"""
import json

S8 = "src/main/resources/rod-story/scenes/mc001_s008.json"
S9 = "src/main/resources/rod-story/scenes/mc001_s009.json"
INDEX = "src/main/resources/rod-story/index.json"

locs = {
    "overlook_foot": {"pos": [35, 0, -14], "radius": 2},
    "camp_edge": {"pos": [68, 0, 0], "radius": 4},
    "elara_camp": {"pos": [72, 0, -2], "radius": 1},
    "bram_camp": {"pos": [70, 0, 3], "radius": 1},
    "campsite": {"pos": [80, 0, 0], "radius": 8},
    "table": {"pos": [78, 1, 2], "radius": 1},
    "elara_journal": {"pos": [75, 0, 5], "radius": 1},
    "bram_journal": {"pos": [73, 0, 6], "radius": 1},
    "post": {"pos": [66, 0, -10], "radius": 2},
    "elara_post": {"pos": [67, 0, -10], "radius": 1},
}

npcs = [
    {"character": "elara", "location": "elara_camp", "yaw": -90},
    {"character": "bram", "location": "bram_camp", "yaw": -90, "held": "minecraft:iron_sword"},
]

clues = {
    "journal": {"name": "Torn expedition journal", "pos": [76, 0, 6], "block": "minecraft:lectern", "dialogue": "journal_talk"},
    "lantern": {"name": "Damaged lantern", "pos": [90, 0, -12], "block": "minecraft:lantern"},
    "erased_map": {"name": "Map with a section erased", "pos": [82, 0, -6], "block": "minecraft:cartography_table"},
    "tracks": {"name": "Tracks toward the forest", "pos": [90, -1, 12], "block": "minecraft:podzol"},
    "supply_chest": {"name": "Untouched supply chest", "pos": [72, 0, 8], "block": "minecraft:chest"},
    "sylvaris_post": {"name": "Weathered stone post", "pos": [66, 2, -12], "block": "minecraft:chiseled_stone_bricks"},
}


def stay(who, where):
    return [{"type": "npc_spawn", "character": who, "location": where},
            {"type": "npc_stay", "character": who, "location": where}]


scene = {
    "id": "mc001_s009", "title": "The Empty Campsite", "universe": "MC-001", "saga": 1, "scene": 9,
    "canon_status": "Saga 1 screenplay, Version 1 (docs/story/SAGA_1_SCREENPLAY.md): spoken dialogue verbatim. "
                    "Hints, captions and the outcome message are new gameplay text. MC-001 keeps a fixed time of day, "
                    "so the scene's sunset is not shown.",
    "dimension": "realitiesofdreams:mc_001", "site": "valley_crossing", "anchor": [90, 96, -150],
    "arrival": {"pos": [35, 0, -14], "yaw": -90},
    "next_scene": "unreleased:mc001_s010",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "to_campsite", "objective": "Go down to the campsite", "target": "camp_edge", "checkpoint": "overlook_foot",
         "on_enter": [{"type": "npc_follow", "character": "elara"}, {"type": "npc_follow", "character": "bram"}],
         "complete": {"type": "reach", "value": "camp_edge"},
         "on_complete": [*stay("elara", "elara_camp"), *stay("bram", "bram_camp"), {"type": "start_dialogue", "id": "nobody_here"}]},
        {"id": "arrive_talk", "checkpoint": "camp_edge",
         "complete": {"type": "dialogue_end", "value": "nobody_here"}},
        {"id": "investigate", "objective": "Work out what happened to the expedition", "target": "campsite", "checkpoint": "camp_edge",
         "on_enter": [{"type": "hint", "ticks": 200, "text": "Press {use} on anything the expedition left behind: the journal, the lantern by the cliff, the map, the tracks and the supplies."}],
         "complete": {"type": "clues", "value": "journal,lantern,erased_map,tracks,supply_chest"},
         "on_complete": [{"type": "start_cinematic", "id": "mc001_s009_tremor"}]},
        {"id": "tremor", "checkpoint": "camp_edge",
         "complete": {"type": "cinematic_end", "value": "mc001_s009_tremor"},
         "on_complete": [{"type": "start_dialogue", "id": "signal"}]},
        {"id": "signal_talk", "checkpoint": "camp_edge",
         "complete": {"type": "dialogue_end", "value": "signal"}},
        {"id": "to_post", "objective": "Follow the anomaly compass to the edge of the camp", "target": "post", "checkpoint": "camp_edge",
         "on_enter": [*stay("elara", "elara_post"), {"type": "hint", "ticks": 160, "text": "The compass leads to a stone post at the camp's edge. Press {use} on its carving."}],
         "complete": {"type": "clues", "value": "sylvaris_post"},
         "on_complete": [{"type": "start_dialogue", "id": "sylvaris"}]},
        {"id": "sylvaris_talk", "checkpoint": "camp_edge",
         "complete": {"type": "dialogue_end", "value": "sylvaris"},
         "on_complete": [
             {"type": "title", "title": "Mission Complete", "subtitle": "The Missing Expedition"},
             {"type": "sound", "sound": "minecraft:ui.toast.challenge_complete", "volume": 0.7},
             {"type": "journal", "id": "missing_expedition"},
             {"type": "message", "text": "The expedition's fate is still unknown. Someone in Sylvaris knows about this place."},
             {"type": "set_flag", "flag": "mc001.missing_explorers.open"},
             {"type": "set_flag", "flag": "mc001.s009.complete"}]},
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


S, E, B = "primordial_steve", "elara", "bram"
dialogues = {
    "nobody_here": lines(
        (B, "Nobody here."), (E, "The fire's cold, but these supplies haven't been here long."),
        (S, "They left in a hurry."), (B, "Or somebody made them leave.")),
    "journal_talk": lines(
        (E, "These people were mapping the same markings we found."), (S, "How do you know?"),
        (E, "Look at this drawing."), (B, "Who sent them?"), (S, "The last page is missing."),
        (E, "Maybe they carried it with them.")),
    "signal": lines(
        (B, "There it is again."), (S, "The same signal."), (E, "And it's getting stronger.")),
    "sylvaris": lines(
        (E, "Sylvaris."), (S, "The forest civilisation?"), (E, "Someone from Sylvaris knows about this site.")),
}

cinematics = {
    "mc001_s009_tremor": {
        "duration": 130, "fade_in": 8, "fade_out": 8, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [75.5, 2.6, 5.5], "yaw": -145, "pitch": 22, "fov": 55},
            {"t": 130, "pos": [76.0, 2.4, 5.0], "yaw": -142, "pitch": 24, "fov": 45}],
        "subtitles": [],
        "sounds": [{"t": 10, "sound": "minecraft:ambient.basalt_deltas.additions", "volume": 0.8, "pitch": 0.5},
                   {"t": 45, "sound": "minecraft:block.lantern.place", "volume": 0.5, "pitch": 0.7},
                   {"t": 80, "sound": "minecraft:item.lodestone_compass.lock", "volume": 0.8}],
        "cues": [{"t": 15, "actions": [{"type": "shake", "intensity": 0.06, "ticks": 50}]},
                 {"t": 45, "actions": [{"type": "world_change", "id": "mc001_s009_lantern"}]}],
    },
}

journal = {"missing_expedition": {"title": "The Missing Expedition", "text": (
    "Tents, cold fires and supplies that haven't been out here long, and nobody. Their journal shows they were "
    "mapping the same markings we found, with a sketch of the six-pointed shape; the last page is gone. A lantern "
    "lay smashed by the cliff, part of their map has been rubbed out, and tracks lead toward the trees. Then the "
    "ground hummed, the lantern rolled along the table with no wind, and the compass woke. It led to a stone post "
    "carved with a woodland emblem. Elara says it is Sylvaris.")}}

json.dump({"scene": scene, "dialogues": dialogues, "cinematics": cinematics, "journal": journal},
          open(S9, "w", encoding="utf8"), indent=2, ensure_ascii=False)
s8 = json.load(open(S8, encoding="utf8"))
s8["scene"]["next_scene"] = "mc001_s009"
json.dump(s8, open(S8, "w", encoding="utf8"), indent=2, ensure_ascii=False)
index = json.load(open(INDEX))
if "mc001_s009.json" not in index["scenes"]:
    index["scenes"].append("mc001_s009.json")
json.dump(index, open(INDEX, "w"), indent=2)
print("wrote", S9)
