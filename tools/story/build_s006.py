"""Writes the Scene 006 bundle (rod-story/scenes/mc001_s006.json) from the Saga 1 screenplay.

"The Fractured Monument": spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md. Hints,
captions and the outcome message are new gameplay text. The scene has its own site, the monument
on the northern mountainside, anchored at MC-001 block (40, 108, -200); every position is relative
to it and must match world/site/NorthernMonument.java.
Run from the repository root: python tools/story/build_s006.py
"""
import json

S5 = "src/main/resources/rod-story/scenes/mc001_s005.json"
S6 = "src/main/resources/rod-story/scenes/mc001_s006.json"
INDEX = "src/main/resources/rod-story/index.json"

locs = {
    "trail_start": {"pos": [0, 0, 42], "radius": 2},
    "bram_trail": {"pos": [1, 0, 38], "radius": 1},
    "elara_trail": {"pos": [-1, 0, 39], "radius": 1},
    "orin_trail": {"pos": [0, 0, 36], "radius": 1},
    "courtyard": {"pos": [0, 0, 20], "radius": 4},
    "bram_court": {"pos": [4, 0, 16], "radius": 1},
    "elara_court": {"pos": [-3, 0, 16], "radius": 1},
    "orin_court": {"pos": [1, 0, 15], "radius": 1},
    "entrance": {"pos": [0, 0, 11], "radius": 3},
    "stair_top": {"pos": [0, 4, 7], "radius": 2},
    "terrace": {"pos": [0, 4, 4], "radius": 5},
    "pillar_front": {"pos": [0, 4, 2], "radius": 2},
    "pillar_mid": {"pos": [0, 8, 0], "radius": 1},
    "carving": {"pos": [0, 4, -3], "radius": 2},
    "elara_upper": {"pos": [-4, 7, -6], "radius": 1},
    "elara_near": {"pos": [-2, 4, 3], "radius": 1},
    "orin_pillar": {"pos": [3, 4, 3], "radius": 1},
    "bram_watch": {"pos": [9, 0, 11], "radius": 1},
}

npcs = [
    {"character": "elara", "location": "elara_court", "yaw": 180},
    {"character": "bram", "location": "bram_court", "yaw": 180, "held": "minecraft:iron_sword"},
    {"character": "orin", "location": "orin_court", "yaw": 180, "held": "minecraft:spyglass"},
]

clues = {
    "panel_west": {"name": "Carved panel", "pos": [-6, 5, 3], "block": "minecraft:chiseled_deepslate"},
    "panel_east": {"name": "Carved panel", "pos": [6, 5, 3], "block": "minecraft:chiseled_deepslate"},
    "panel_north": {"name": "Carved panel", "pos": [5, 6, -5], "block": "minecraft:chiseled_deepslate"},
    "fracture": {"name": "Fracture in the central pillar", "pos": [0, 6, 1], "block": "minecraft:cracked_deepslate_tiles"},
}

STAIRS = "solid|0,0,12;0,1,11;0,2,10;0,3,9"
EARTH = "air|" + ";".join(f"{x},{y},-4" for y in (4, 5, 6) for x in range(-2, 3))


def stay(who, where):
    return [{"type": "npc_spawn", "character": who, "location": where},
            {"type": "npc_stay", "character": who, "location": where}]


scene = {
    "id": "mc001_s006", "title": "The Fractured Monument", "universe": "MC-001", "saga": 1, "scene": 6,
    "canon_status": "Saga 1 screenplay, Version 1 (docs/story/SAGA_1_SCREENPLAY.md): spoken dialogue verbatim. "
                    "Hints, captions and the outcome message are new gameplay text.",
    "dimension": "realitiesofdreams:mc_001", "site": "northern_monument", "anchor": [40, 108, -200],
    "arrival": {"pos": [0, 0, 42], "yaw": 180},
    "next_scene": "unreleased:mc001_s007",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "arrive", "checkpoint": "trail_start",
         "on_enter": [{"type": "teleport_player", "location": "trail_start", "yaw": 180},
                      *stay("bram", "bram_trail"), *stay("elara", "elara_trail"), *stay("orin", "orin_trail"),
                      {"type": "start_cinematic", "id": "mc001_s006_approach"}],
         "complete": {"type": "cinematic_end", "value": "mc001_s006_approach"}},
        {"id": "to_courtyard", "objective": "Follow the others up to the monument", "target": "courtyard", "checkpoint": "trail_start",
         "complete": {"type": "reach", "value": "courtyard"},
         "on_complete": [{"type": "start_dialogue", "id": "approach"}]},
        {"id": "approach_talk", "checkpoint": "courtyard",
         "complete": {"type": "dialogue_end", "value": "approach"},
         "on_complete": [{"type": "give_item", "item": "minecraft:deepslate_bricks", "count": 6}]},
        {"id": "entrance", "objective": "Build a stable staircase up to the terrace", "target": "entrance", "checkpoint": "courtyard",
         "on_enter": [{"type": "hint", "ticks": 200, "text": "The old stair has collapsed. Green sparkles mark the steps: place blocks there with {use}, and leave the monument itself whole."}],
         "complete": {"type": "blocks", "value": STAIRS},
         "on_complete": [*stay("elara", "elara_upper"), *stay("orin", "orin_pillar"), *stay("bram", "bram_watch")]},
        {"id": "panels", "objective": "Record the three carved panels", "target": "terrace", "checkpoint": "stair_top",
         "on_enter": [{"type": "hint", "ticks": 160, "text": "Press {use} on each carved panel to copy its symbols."}],
         "complete": {"type": "clues", "value": "panel_west,panel_east,panel_north"},
         "on_complete": [{"type": "give_item", "item": "minecraft:spyglass", "count": 1, "name": "Orin's measuring glass"}]},
        {"id": "fracture", "objective": "Measure the fracture in the central pillar", "target": "pillar_front", "checkpoint": "stair_top",
         "on_enter": [{"type": "hint", "ticks": 160, "text": "Orin lent you his measuring glass. Press {use} on the crack at the foot of the pillar."}],
         "complete": {"type": "clues", "value": "fracture"}},
        {"id": "uncover", "objective": "Dig away the fallen earth below the upper terrace", "target": "carving", "checkpoint": "stair_top",
         "on_enter": [{"type": "hint", "ticks": 160, "text": "There is carving behind that earth. Dig it away by hand."}],
         "complete": {"type": "blocks", "value": EARTH},
         "on_complete": [{"type": "sound", "sound": "minecraft:block.rooted_dirt.break", "volume": 0.8},
                         {"type": "start_dialogue", "id": "six_towers"}]},
        {"id": "six_talk", "checkpoint": "stair_top",
         "complete": {"type": "dialogue_end", "value": "six_towers"},
         "on_complete": [{"type": "start_cinematic", "id": "mc001_s006_light"}]},
        {"id": "light", "checkpoint": "stair_top",
         "complete": {"type": "cinematic_end", "value": "mc001_s006_light"},
         "on_complete": [{"type": "start_dialogue", "id": "move_away"}]},
        {"id": "move_away_talk", "checkpoint": "stair_top",
         "complete": {"type": "dialogue_end", "value": "move_away"},
         "on_complete": [{"type": "world_change", "id": "mc001_s006_glow_off"},
                         {"type": "sound", "sound": "minecraft:block.beacon.deactivate", "volume": 0.7, "pitch": 0.8},
                         {"type": "start_dialogue", "id": "didnt_touch"}]},
        {"id": "after_talk", "checkpoint": "stair_top",
         "complete": {"type": "dialogue_end", "value": "didnt_touch"},
         "on_complete": [
             {"type": "title", "title": "Mission Complete", "subtitle": "The Stone That Remembers"},
             {"type": "sound", "sound": "minecraft:ui.toast.challenge_complete", "volume": 0.7},
             {"type": "journal", "id": "monument"},
             {"type": "message", "text": "The monument's markings are recorded, and Orin has bagged a fallen stone fragment to study. New discovery: six connected civilisation symbols."},
             {"type": "set_flag", "flag": "mc001.monument.recorded"},
             {"type": "set_flag", "flag": "mc001.s006.complete"}]},
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


S, E, O, B = "primordial_steve", "elara", "orin", "bram"
dialogues = {
    "approach": lines(
        (B, "That's bigger than it looked from the watchtower."), (E, "The outer walls weren't visible yesterday."),
        (O, "They've been buried for generations. The disturbance must've exposed them."),
        (S, "Then the ground didn't just shake. Something underneath moved."), (O, "That's what worries me.")),
    "six_towers": lines(
        (S, "Six towers?"), (O, "Possibly. Or six settlements."),
        (E, "Look at their shapes. That one resembles the towers of Eldervale."),
        (B, "And that one looks like the old Stonecrown fortress."), (S, "Why would somebody carve all six here?"),
        (O, "I don't know. But somebody wanted them remembered together.")),
    "move_away": lines((E, "Steve! Move away!")),
    "didnt_touch": lines(
        (S, "I didn't touch anything."), (O, "I saw. It started before your hand reached the stone."),
        (B, "Then we're not staying here after dark.")),
}

cinematics = {
    "mc001_s006_approach": {
        "duration": 220, "fade_in": 20, "fade_out": 12, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [0.5, 5.0, 52.0], "yaw": 180, "pitch": -8, "fov": 50},
            {"t": 220, "pos": [0.5, 3.5, 33.0], "yaw": 180, "pitch": -14, "fov": 62}],
        "subtitles": [{"t": 30, "duration": 100, "text": "The Ancient Northern Monument", "style": "title"}],
        "sounds": [{"t": 20, "sound": "minecraft:ambient.basalt_deltas.mood", "volume": 0.4, "pitch": 0.7}],
        "cues": [{"t": 10, "actions": [{"type": "npc_move", "character": "bram", "location": "bram_court"},
                                       {"type": "npc_move", "character": "elara", "location": "elara_court"},
                                       {"type": "npc_move", "character": "orin", "location": "orin_court"}]}],
    },
    "mc001_s006_light": {
        "duration": 160, "fade_in": 10, "fade_out": 0, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [3.5, 5.8, 6.5], "yaw": 152, "pitch": -20, "fov": 58},
            {"t": 160, "pos": [3.0, 5.6, 5.6], "yaw": 152, "pitch": -26, "fov": 52}],
        "subtitles": [],
        "sounds": [{"t": 40, "sound": "minecraft:block.beacon.activate", "volume": 0.8, "pitch": 0.6},
                   {"t": 44, "sound": "minecraft:block.amethyst_block.resonate", "volume": 0.8, "pitch": 0.5}],
        "cues": [{"t": 0, "actions": [{"type": "npc_spawn", "character": "primordial_steve", "location": "pillar_front"},
                                      {"type": "npc_look", "character": "primordial_steve", "location": "pillar_mid"}]},
                 {"t": 40, "actions": [{"type": "world_change", "id": "mc001_s006_glow_on"},
                                       {"type": "particles", "particle": "minecraft:end_rod", "location": "pillar_mid", "count": 30, "spread": 1},
                                       {"type": "shake", "intensity": 0.08, "ticks": 60}]},
                 {"t": 80, "actions": [{"type": "npc_move", "character": "elara", "location": "elara_near"}]},
                 {"t": 160, "actions": [{"type": "npc_despawn", "character": "primordial_steve"}]}],
    },
}

journal = {"monument": {"title": "The Stone That Remembers", "text": (
    "The monument is far bigger than it looked from the watchtower; the tremors have uncovered walls that were "
    "buried for generations. We rebuilt the fallen stair, copied three carved panels and measured the split in the "
    "central pillar. Behind a slump of earth: six structures carved around a circle. Elara knows one as the towers "
    "of Eldervale, Bram another as the old Stonecrown fortress. When I leaned close, light ran through the pillar's "
    "carved lines before my hand ever reached it. Orin took a fallen fragment back to study.")}}

json.dump({"scene": scene, "dialogues": dialogues, "cinematics": cinematics, "journal": journal},
          open(S6, "w", encoding="utf8"), indent=2, ensure_ascii=False)
s5 = json.load(open(S5, encoding="utf8"))
s5["scene"]["next_scene"] = "mc001_s006"
json.dump(s5, open(S5, "w", encoding="utf8"), indent=2, ensure_ascii=False)
index = json.load(open(INDEX))
if "mc001_s006.json" not in index["scenes"]:
    index["scenes"].append("mc001_s006.json")
json.dump(index, open(INDEX, "w"), indent=2)
print("wrote", S6)
