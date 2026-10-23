"""Writes the Scene 008 bundle (rod-story/scenes/mc001_s008.json) from the Saga 1 screenplay.

"Across Awakening Valley": spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md. Hints,
captions and the outcome message are new gameplay text. The scene has its own site in eastern
Awakening Valley, anchored at MC-001 block (90, 96, -150), shared with Scene 009; positions must
match world/site/ValleyCrossing.java.
Run from the repository root: python tools/story/build_s008.py
"""
import json

S7 = "src/main/resources/rod-story/scenes/mc001_s007.json"
S8 = "src/main/resources/rod-story/scenes/mc001_s008.json"
INDEX = "src/main/resources/rod-story/index.json"

locs = {
    "trail_start": {"pos": [-20, 0, 0], "radius": 2},
    "elara_edge": {"pos": [-19, 0, -2], "radius": 1},
    "bram_edge": {"pos": [-21, 0, 2], "radius": 1},
    "bridge_west": {"pos": [3, 0, 0], "radius": 2},
    "bram_rope": {"pos": [7, 0, -1], "radius": 1},
    "elara_lookout": {"pos": [4, 0, -4], "radius": 1},
    "gap": {"pos": [11, 0, 0], "radius": 2},
    "east_bank": {"pos": [21, 0, 0], "radius": 3},
    "camp": {"pos": [26, 0, 6], "radius": 3},
    "camp_side": {"pos": [26, 0, 3], "radius": 2},
    "markers": {"pos": [44, 0, 0], "radius": 6},
    "berries": {"pos": [35, 0, 14], "radius": 3},
    "overlook_top": {"pos": [42, 5, -14], "radius": 2},
    "overlook_foot": {"pos": [35, 0, -14], "radius": 2},
    "campsite_far": {"pos": [78, 1, 0], "radius": 1},
}

npcs = [
    {"character": "elara", "location": "elara_edge", "yaw": -90},
    {"character": "bram", "location": "bram_edge", "yaw": -90, "held": "minecraft:iron_sword"},
]

clues = {
    "crate": {"name": "Supply crate", "pos": [9, 0, 1], "block": "minecraft:barrel"},
    "marker_1": {"name": "Old survey marker", "pos": [30, 1, -22], "block": "minecraft:chiseled_stone_bricks"},
    "marker_2": {"name": "Old survey marker", "pos": [52, 1, 18], "block": "minecraft:chiseled_stone_bricks"},
    "marker_3": {"name": "Old survey marker", "pos": [58, 1, -10], "block": "minecraft:chiseled_stone_bricks"},
}


def stay(who, where):
    return [{"type": "npc_spawn", "character": who, "location": where},
            {"type": "npc_stay", "character": who, "location": where}]


def follow(*who):
    return [{"type": "npc_follow", "character": w} for w in who]


scene = {
    "id": "mc001_s008", "title": "Across Awakening Valley", "universe": "MC-001", "saga": 1, "scene": 8,
    "canon_status": "Saga 1 screenplay, Version 1 (docs/story/SAGA_1_SCREENPLAY.md): spoken dialogue verbatim. "
                    "Hints, captions and the outcome message are new gameplay text. MC-001 keeps a fixed time of day, "
                    "so the closing cinematic's sunset is not shown.",
    "dimension": "realitiesofdreams:mc_001", "site": "valley_crossing", "anchor": [90, 96, -150],
    "arrival": {"pos": [-20, 0, 0], "yaw": -90},
    "next_scene": "unreleased:mc001_s009",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "arrive", "checkpoint": "trail_start",
         "on_enter": [{"type": "teleport_player", "location": "trail_start", "yaw": -90},
                      *stay("elara", "elara_edge"), *stay("bram", "bram_edge"),
                      {"type": "start_cinematic", "id": "mc001_s008_valley"}],
         "complete": {"type": "cinematic_end", "value": "mc001_s008_valley"},
         "on_complete": [{"type": "start_dialogue", "id": "valley_edge"}]},
        {"id": "edge_talk", "checkpoint": "trail_start",
         "complete": {"type": "dialogue_end", "value": "valley_edge"},
         "on_complete": [{"type": "give_item", "item": "minecraft:oak_planks", "count": 8},
                         {"type": "give_item", "item": "minecraft:campfire", "count": 1},
                         *follow("elara", "bram")]},
        {"id": "to_bridge", "objective": "Head down to the old bridge", "target": "bridge_west", "checkpoint": "trail_start",
         "complete": {"type": "reach", "value": "bridge_west"},
         "on_complete": [{"type": "world_change", "id": "mc001_s008_collapse"},
                         {"type": "sound", "sound": "minecraft:block.stone.break", "volume": 1.0, "pitch": 0.5},
                         {"type": "sound", "sound": "minecraft:entity.generic.splash", "volume": 1.0, "pitch": 0.7},
                         {"type": "shake", "intensity": 0.1, "ticks": 25},
                         {"type": "particles", "particle": "minecraft:poof", "location": "gap", "count": 30, "spread": 1.5},
                         *stay("bram", "bram_rope"), *stay("elara", "elara_lookout")]},
        {"id": "rescue", "objective": "Save the supply crate before it slides into the river", "target": "bridge_west", "checkpoint": "trail_start",
         "on_enter": [{"type": "hint", "ticks": 160, "text": "The middle of the bridge gave way. Press {use} on the crate on the broken edge before it goes over."}],
         "complete": {"type": "clues", "value": "crate"},
         "on_complete": [{"type": "world_change", "id": "mc001_s008_crate_saved"},
                         {"type": "give_item", "item": "minecraft:barrel", "count": 1, "name": "Supply crate"}]},
        {"id": "bridge", "objective": "Bridge the gap so the party can cross", "target": "gap", "checkpoint": "trail_start",
         "on_enter": [{"type": "hint", "ticks": 200, "text": "Elara has picked out a route: lay planks across the middle of the bridge where the sparkles are."}],
         "complete": {"type": "blocks", "value": "solid|10,-1,0;11,-1,0;12,-1,0"},
         "on_complete": [*follow("elara", "bram")]},
        {"id": "cross", "objective": "Cross to the east bank", "target": "east_bank", "checkpoint": "bridge_west",
         "complete": {"type": "reach", "value": "east_bank"},
         "on_complete": [{"type": "start_dialogue", "id": "beyond"}]},
        {"id": "camp", "objective": "Set up a field camp", "target": "camp", "checkpoint": "east_bank",
         "on_enter": [{"type": "hint", "ticks": 160, "text": "Place the campfire on the cleared ground between the log seats."}],
         "complete": {"type": "blocks", "value": "minecraft:campfire|26,0,6"},
         "on_complete": [{"type": "sound", "sound": "minecraft:block.campfire.crackle", "volume": 1.0},
                         {"type": "message", "text": "Field camp set up. It is your checkpoint in the valley."}]},
        {"id": "markers", "objective": "Find three old survey markers with the anomaly compass", "target": "markers", "checkpoint": "camp",
         "on_enter": [{"type": "hint", "ticks": 200, "text": "Hold the anomaly compass and follow the sparkles. Press {use} on each marker you find."}],
         "complete": {"type": "clues", "value": "marker_1,marker_2,marker_3"},
         "on_complete": [{"type": "start_dialogue", "id": "tunnel"}]},
        {"id": "tunnel_talk", "checkpoint": "camp",
         "complete": {"type": "dialogue_end", "value": "tunnel"}},
        {"id": "provisions", "objective": "Gather berries for the return journey", "target": "berries", "checkpoint": "camp",
         "on_enter": [{"type": "hint", "ticks": 140, "text": "Press {use} on the berry bushes to pick them."}],
         "complete": {"type": "items", "value": "minecraft:sweet_berries*6"}},
        {"id": "to_overlook", "objective": "Climb to the eastern overlook", "target": "overlook_top", "checkpoint": "camp",
         "complete": {"type": "reach", "value": "overlook_top"},
         "on_complete": [{"type": "start_cinematic", "id": "mc001_s008_needle"}]},
        {"id": "needle", "checkpoint": "overlook_foot",
         "complete": {"type": "cinematic_end", "value": "mc001_s008_needle"},
         "on_complete": [{"type": "start_dialogue", "id": "someones_been_here"}]},
        {"id": "been_here_talk", "checkpoint": "overlook_foot",
         "complete": {"type": "dialogue_end", "value": "someones_been_here"},
         "on_complete": [
             {"type": "title", "title": "Mission Complete", "subtitle": "Into the Valley"},
             {"type": "sound", "sound": "minecraft:ui.toast.challenge_complete", "volume": 0.7},
             {"type": "journal", "id": "valley"},
             {"type": "message", "text": "The expedition camp stands on the east bank, and the compass points to a deserted campsite beyond the overlook."},
             {"type": "set_flag", "flag": "mc001.valley.camp"},
             {"type": "set_flag", "flag": "mc001.s008.complete"}]},
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
    "valley_edge": lines(
        (E, "Beautiful, isn't it?"), (B, "Beautiful until you have to cross it carrying supplies."),
        (S, "We could build a bridge."), (B, "Of course that's your answer."), (S, "It's a good answer.")),
    "beyond": lines(
        (E, "Have you ever wondered what's beyond the kingdoms?"), (S, "Mountains. Oceans. Probably more mountains."),
        (E, "I'm serious."), (S, "So am I."),
        (E, "There are maps older than anyone in Firstbrook. Some have places nobody remembers."),
        (S, "Then somebody should go see if they're still there."), (E, "I thought you'd say that.")),
    "tunnel": lines(
        (B, "Rowan said you were always getting into trouble as a child."), (S, "He exaggerates."),
        (B, "He said you tried building a tunnel beneath the village."), (S, "It would've worked."),
        (B, "You flooded three houses."), (S, "I said it would've worked.")),
    "someones_been_here": lines(
        (E, "Someone's been here."), (B, "Recently?"), (S, "Let's find out.")),
}

cinematics = {
    "mc001_s008_valley": {
        "duration": 260, "fade_in": 20, "fade_out": 12, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [11.5, -4.0, 26.0], "yaw": 180, "pitch": -2, "fov": 60},
            {"t": 150, "pos": [6.0, 24.0, 34.0], "yaw": 168, "pitch": 32, "fov": 66},
            {"t": 260, "pos": [-15.0, 3.0, 5.0], "yaw": 125, "pitch": 6, "fov": 55}],
        "subtitles": [{"t": 40, "duration": 100, "text": "Awakening Valley", "style": "title"}],
        "sounds": [{"t": 0, "sound": "minecraft:block.water.ambient", "volume": 0.6},
                   {"t": 120, "sound": "minecraft:entity.parrot.ambient", "volume": 0.2, "pitch": 0.8}],
    },
    "mc001_s008_needle": {
        "duration": 150, "fade_in": 8, "fade_out": 10, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [42.5, 7.0, -13.5], "yaw": -70, "pitch": 6, "fov": 55},
            {"t": 150, "pos": [43.0, 7.0, -13.5], "yaw": -71, "pitch": 4, "fov": 30}],
        "subtitles": [{"t": 20, "duration": 100, "text": "The needle swings toward a deserted campsite.", "style": "caption"}],
        "sounds": [{"t": 10, "sound": "minecraft:item.lodestone_compass.lock", "volume": 0.8, "pitch": 0.8}],
        "cues": [{"t": 40, "actions": [{"type": "particles", "particle": "minecraft:end_rod", "location": "campsite_far", "count": 20, "spread": 1}]}],
    },
}

journal = {"valley": {"title": "Into the Valley", "text": (
    "Awakening Valley from the edge: a river deep in its gorge and one old bridge across it. Bram complained about "
    "the supplies; I said we could build a bridge. Half of the old one fell as we reached it and nearly took our "
    "crate with it, so we did build some. We made camp on the east bank, found three of the old survey markers "
    "with Orin's compass and picked berries for the way back. From the overlook the needle swung toward a "
    "campsite further east. Somebody has been out here.")}}

json.dump({"scene": scene, "dialogues": dialogues, "cinematics": cinematics, "journal": journal},
          open(S8, "w", encoding="utf8"), indent=2, ensure_ascii=False)
s7 = json.load(open(S7, encoding="utf8"))
s7["scene"]["next_scene"] = "mc001_s008"
json.dump(s7, open(S7, "w", encoding="utf8"), indent=2, ensure_ascii=False)
index = json.load(open(INDEX))
if "mc001_s008.json" not in index["scenes"]:
    index["scenes"].append("mc001_s008.json")
json.dump(index, open(INDEX, "w"), indent=2)
print("wrote", S8)
