"""Writes the Scene 003 bundle (rod-story/scenes/mc001_s003.json) from the Saga 1 screenplay.

"Elara's Northern Trail": spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md. Bram's
word at the barrier, the clue remarks and Elara's closing line are new gameplay text. Heights on
the trail come from Mc001Terrain.hillHeight (y = the first air block above the ground).
Run from the repository root: python tools/story/build_s003.py
"""
import copy
import json

S2 = "src/main/resources/rod-story/scenes/mc001_s002.json"
S3 = "src/main/resources/rod-story/scenes/mc001_s003.json"
INDEX = "src/main/resources/rod-story/index.json"

s2 = json.load(open(S2, encoding="utf8"))
sc2 = s2["scene"]

locs = copy.deepcopy(sc2["locations"])
locs.update({
    "trail_gate": {"pos": [0, 0, -25], "radius": 3},
    "bram_gate": {"pos": [2, 0, -25], "radius": 1.5},
    "footprints": {"pos": [2, 6, -60], "radius": 3},
    "elara_crouch": {"pos": [3, 6, -58], "radius": 1},
    "mid_trail": {"pos": [3, 9, -68], "radius": 6},
    "lantern_spot": {"pos": [2, 11, -74], "radius": 2},
    "rock_ledge": {"pos": [9, 18, -76], "radius": 2.5},
    "marker_spot": {"pos": [5, 19, -87], "radius": 2.5},
    "markings": {"pos": [8, 20, -92], "radius": 3},
    "markings_stone": {"pos": [9, 21, -92], "radius": 1.5},
})

npcs = []
for n in sc2["npcs"]:
    c = n["character"]
    n = copy.deepcopy(n)
    if c == "elder_rowan":
        n.update({"location": "rowan_door", "yaw": 90})
    if c == "bram":
        n = {"character": "bram", "location": "bram_gate", "yaw": 0, "held": "minecraft:iron_sword"}
    npcs.append(n)

scene = {
    "id": "mc001_s003", "title": "Elara's Northern Trail", "universe": "MC-001", "saga": 1, "scene": 3,
    "canon_status": "Saga 1 screenplay, Version 1 (docs/story/SAGA_1_SCREENPLAY.md): spoken dialogue verbatim. "
                    "Bram's word at the barrier, clue remarks and Elara's closing line are new gameplay text.",
    "dimension": "realitiesofdreams:mc_001", "site": "firstbrook", "anchor": [0, 64, 0],
    "arrival": {"pos": [0, 0, -18], "yaw": 180},
    "next_scene": "unreleased:mc001_s004",
    "locations": locs, "npcs": npcs,
    "clues": {
        "dropped_lantern": {"name": "Dropped lantern", "pos": [2, 11, -74], "block": "minecraft:lantern", "dialogue": "clue_lantern"},
        "scout_pack": {"name": "Scout's pack", "pos": [10, 18, -77], "block": "minecraft:barrel", "dialogue": "clue_pack"},
        "survey_marker": {"name": "Broken survey marker", "pos": [6, 19, -88], "block": "minecraft:stone_brick_wall", "dialogue": "clue_marker"},
    },
    "stages": [
        {"id": "to_trail", "objective": "Go to the northern trail with Elara", "target": "trail_gate", "checkpoint": "gate",
         "on_enter": [{"type": "npc_follow", "character": "elara"}, {"type": "world_change", "id": "mc001_s003_trail"}],
         "complete": {"type": "reach", "value": "trail_gate"},
         "on_complete": [{"type": "say", "character": "bram",
                          "text": "The trail's closed past the old posts, but the Elder says you two may pass. Mind the loose rock."}]},
        {"id": "follow_trail", "objective": "Follow the trail north", "target": "footprints", "checkpoint": "trail_gate",
         "complete": {"type": "reach", "value": "footprints"}},
        {"id": "tracks", "checkpoint": "footprints",
         "on_enter": [{"type": "start_cinematic", "id": "mc001_s003_tracks"}],
         "complete": {"type": "cinematic_end", "value": "mc001_s003_tracks"},
         "on_complete": [{"type": "start_dialogue", "id": "tracks"}]},
        {"id": "tracks_talk", "checkpoint": "footprints",
         "complete": {"type": "dialogue_end", "value": "tracks"},
         "on_complete": [{"type": "npc_follow", "character": "elara"}]},
        {"id": "investigate", "objective": "Find the missing scout's last position", "target": "rock_ledge", "checkpoint": "footprints",
         "on_enter": [{"type": "hint", "text": "Climb the rocks with {jump}. Press {use} on anything worth a closer look.", "ticks": 160}],
         "complete": {"type": "clues", "value": "dropped_lantern,scout_pack"}},
        {"id": "markers", "objective": "Find the missing trail markers", "target": "marker_spot", "checkpoint": "rock_ledge",
         "complete": {"type": "clues", "value": "survey_marker"}},
        {"id": "markings", "objective": "Examine the strange markings", "target": "markings", "checkpoint": "marker_spot",
         "complete": {"type": "reach", "value": "markings"}},
        {"id": "pulse", "checkpoint": "markings",
         "on_enter": [{"type": "start_cinematic", "id": "mc001_s003_pulse"}],
         "complete": {"type": "cinematic_end", "value": "mc001_s003_pulse"},
         "on_complete": [{"type": "start_dialogue", "id": "reacted"}]},
        {"id": "record", "checkpoint": "markings",
         "complete": {"type": "dialogue_end", "value": "reacted"},
         "on_complete": [
             {"type": "say", "character": "elara",
              "text": "I'll mark this place on the map. Before we go any further, we need proper surveying gear from Orin."},
             {"type": "give_item", "item": "minecraft:map", "name": "Northern Trail Map",
              "lore": ["Elara's markings: the vanished tracks,", "the scout's camp and the strange stone."]},
             {"type": "title", "title": "Mission Complete", "subtitle": "Tracks in the Stone"},
             {"type": "sound", "sound": "minecraft:ui.toast.challenge_complete", "volume": 0.7},
             {"type": "journal", "id": "northern_trail"},
             {"type": "set_flag", "flag": "mc001.s003.northern_trail_map"},
             {"type": "set_flag", "flag": "mc001.s004.orin_workshop"},
             {"type": "add_value", "key": "rel.elara", "amount": "1"},
             {"type": "set_flag", "flag": "mc001.s003.complete"}]},
    ],
    "triggers": [],
    "area_triggers": [
        {"id": "valley_talk", "location": "mid_trail", "stages": ["investigate"], "actions": [{"type": "start_dialogue", "id": "valley"}]},
    ],
    "chatter": [],
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


S, E = "primordial_steve", "elara"
dialogues = {
    "tracks": lines(
        (E, "Look at this."), (S, "A traveller?"), (E, "Maybe. But the tracks stop here."),
        (S, "They could've climbed the ridge."), (E, "Then where are the marks on the rock?"),
        (S, "You think somebody vanished?"), (E, "I think something happened here that neither of us understands.")),
    "valley": lines(
        (E, "I know this valley. I've crossed it since I was a child."),
        (S, "You make it sound like the valley betrayed you."),
        (E, "Maybe that's exactly how it feels.")),
    "clue_lantern": lines(
        (S, "A scout's lantern. Still half full of oil."),
        (E, "Nobody leaves a lantern behind out here. Look at those rocks. Someone climbed them.")),
    "clue_pack": lines(
        (S, "A bedroll, a cold fire and a pack. The scout was here."),
        (E, "Then this is the last place anyone saw them... if anyone did.")),
    "clue_marker": lines(
        (S, "One of the old survey markers. Snapped clean through."),
        (E, "These have stood for a hundred years. And there's another stone further up. Look at it.")),
    "reacted": lines(
        (S, "Did you see that?"), (E, "Yes."), (S, "They reacted to me."),
        (E, "Or to the equipment you're carrying. We don't know yet.")),
}

cinematics = {
    "mc001_s003_tracks": {
        "duration": 120, "fade_in": 12, "fade_out": 10, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [6.8, 9.4, -54.2], "yaw": 141, "pitch": 24, "fov": 55},
            {"t": 120, "pos": [6.0, 9.0, -55.0], "yaw": 142, "pitch": 25, "fov": 46}],
        "subtitles": [],
        "sounds": [{"t": 6, "sound": "minecraft:block.gravel.step", "volume": 0.6},
                   {"t": 30, "sound": "minecraft:block.mud.step", "volume": 0.6}],
        "cues": [{"t": 0, "actions": [{"type": "npc_spawn", "character": "elara", "location": "elara_crouch"},
                                      {"type": "npc_stay", "character": "elara", "location": "elara_crouch"},
                                      {"type": "npc_look", "character": "elara", "location": "footprints"}]}],
    },
    "mc001_s003_pulse": {
        "duration": 110, "fade_in": 8, "fade_out": 10, "hide_player": False,
        "keyframes": [
            {"t": 0, "pos": [5.2, 23.4, -97.4], "yaw": -41, "pitch": 15, "fov": 60},
            {"t": 110, "pos": [5.8, 23.0, -96.6], "yaw": -42, "pitch": 14, "fov": 50}],
        "subtitles": [],
        "sounds": [{"t": 30, "sound": "minecraft:block.amethyst_block.resonate", "volume": 1.0, "pitch": 0.6},
                   {"t": 32, "sound": "minecraft:block.beacon.power_select", "volume": 0.5, "pitch": 1.4},
                   {"t": 64, "sound": "minecraft:block.beacon.deactivate", "volume": 0.4, "pitch": 1.2}],
        "shakes": [{"t": 30, "duration": 12, "intensity": 0.03}],
        "cues": [{"t": 30, "actions": [{"type": "world_change", "id": "mc001_s003_pulse_on"},
                                       {"type": "particles", "particle": "minecraft:end_rod", "location": "markings_stone", "count": 30, "spread": 1}]},
                 {"t": 64, "actions": [{"type": "world_change", "id": "mc001_s003_pulse_off"}]}],
    },
}

journal = {"northern_trail": {"title": "Tracks in the Stone", "text": (
    "Elara found footprints on the northern trail that simply stop. No marks on the rock, no sign of anyone "
    "turning back. Higher up we found a scout's lantern, and on the rocks above it their bedroll, a cold fire and "
    "their pack. One of the old survey markers has been snapped through. Beyond it, a stone carved with shapes I "
    "have never seen pulsed with light as I came near. Elara has marked it all on the map. Next: Orin, and proper "
    "surveying gear.")}}

json.dump({"scene": scene, "dialogues": dialogues, "cinematics": cinematics, "journal": journal},
          open(S3, "w", encoding="utf8"), indent=2, ensure_ascii=False)
sc2["next_scene"] = "mc001_s003"
json.dump(s2, open(S2, "w", encoding="utf8"), indent=2, ensure_ascii=False)
index = json.load(open(INDEX))
if "mc001_s003.json" not in index["scenes"]:
    index["scenes"].append("mc001_s003.json")
json.dump(index, open(INDEX, "w"), indent=2)
print("wrote", S3)
