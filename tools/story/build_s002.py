"""Writes the Scene 002 bundle (rod-story/scenes/mc001_s002.json) from the Saga 1 screenplay.

Spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md; the clue inspection lines are
new gameplay text. Locations and the village cast are carried over from Scene 001 so both scenes
share one Firstbrook. Run from the repository root: python tools/story/build_s002.py
"""
import copy
import json

S1 = "src/main/resources/rod-story/scenes/mc001_s001.json"
S2 = "src/main/resources/rod-story/scenes/mc001_s002.json"
INDEX = "src/main/resources/rod-story/index.json"

s1 = json.load(open(S1, encoding="utf8"))
sc1 = s1["scene"]

locs = copy.deepcopy(sc1["locations"])
locs.update({
    "rowan_square": {"pos": [-2, 0, -2], "radius": 2},
    "cracked_wall": {"pos": [5, 0, -7], "radius": 2.5},
    "surveyor_table": {"pos": [3, 0, 3], "radius": 2.5},
    "tower_report": {"pos": [-7, 0, -16], "radius": 2.5},
    "compass_spot": {"pos": [3, 0, 1], "radius": 1.5},
    "bram_patrol": {"pos": [0, 0, -19], "radius": 2},
})

npcs = []
for n in sc1["npcs"]:
    c = n["character"]
    if c == "primordial_steve":
        continue
    n = copy.deepcopy(n)
    n.pop("from_stage", None)
    n.pop("until_stage", None)
    if c == "elder_rowan":
        n.update({"location": "rowan_square", "yaw": 180})
    if c == "bram":
        n = {"character": "bram", "location": "bram_patrol", "yaw": 180, "activity": "patrol",
             "route": ["bram_patrol", "north_road"], "held": "minecraft:iron_sword"}
    if c == "firstbrook_guard_2":
        n = {"character": c, "location": "gate_post", "yaw": 180, "held": "minecraft:iron_sword"}
    npcs.append(n)

scene = {
    "id": "mc001_s002", "title": "The Warning from the North", "universe": "MC-001", "saga": 1, "scene": 2,
    "canon_status": "Saga 1 screenplay, Version 1 (docs/story/SAGA_1_SCREENPLAY.md): spoken dialogue verbatim. "
                    "Clue inspection lines are new gameplay text, not from the screenplay.",
    "dimension": "realitiesofdreams:mc_001", "site": "firstbrook", "anchor": [0, 64, 0],
    "arrival": {"pos": [2, 0, 3], "yaw": 180},
    "next_scene": "mc001_s003",
    "locations": locs, "npcs": npcs,
    "clues": {
        "cracked_wall": {"name": "Cracked wall", "pos": [5, 0, -7], "block": "minecraft:cracked_stone_bricks", "dialogue": "clue_wall"},
        "spinning_compass": {"name": "Surveyor's compass", "pos": [3, 0, 3], "block": "minecraft:cartography_table", "dialogue": "clue_compass"},
        "tower_report": {"name": "Watchtower report", "pos": [-7, 0, -16], "block": "minecraft:lectern", "dialogue": "clue_report"},
    },
    "stages": [
        {"id": "warning", "objective": "Speak with Elder Rowan", "target": "rowan_square", "checkpoint": "square",
         "on_enter": [{"type": "npc_follow", "character": "elara"}, {"type": "npc_approach", "character": "elder_rowan"}],
         "complete": {"type": "dialogue_end", "value": "rowan_warning"}},
        {"id": "inspect", "objective": "Inspect the signs of the disturbance", "target": "cracked_wall", "checkpoint": "square",
         "on_enter": [{"type": "world_change", "id": "mc001_s002_aftermath"},
                      {"type": "hint", "text": "Glowing spots mark things worth a closer look. Press {use} on them to examine.", "ticks": 180}],
         "complete": {"type": "clues", "value": "cracked_wall,spinning_compass,tower_report"}},
        {"id": "bram", "objective": "Hear Bram's report", "target": "square", "checkpoint": "square",
         "on_enter": [{"type": "say", "character": "bram", "text": "Steve! Elder!"}, {"type": "npc_approach", "character": "bram"}],
         "complete": {"type": "dialogue_end", "value": "bram_report"}},
        {"id": "compass", "checkpoint": "compass_spot",
         "on_enter": [{"type": "give_item", "item": "minecraft:compass", "name": "Strange Compass",
                       "lore": ["Its needle will not settle.", "Found on the surveyor's table in Firstbrook."]},
                      {"type": "hold_item", "item": "minecraft:compass"},
                      {"type": "teleport_player", "location": "compass_spot", "yaw": -135},
                      {"type": "start_cinematic", "id": "mc001_s002_compass"}],
         "complete": {"type": "cinematic_end", "value": "mc001_s002_compass"},
         "on_complete": [{"type": "start_dialogue", "id": "heading_north"}]},
        {"id": "heading", "checkpoint": "square",
         "complete": {"type": "dialogue_end", "value": "heading_north"},
         "on_complete": [
             {"type": "title", "title": "Mission Complete", "subtitle": "The Northern Disturbance"},
             {"type": "sound", "sound": "minecraft:ui.toast.challenge_complete", "volume": 0.7},
             {"type": "journal", "id": "northern_disturbance"},
             {"type": "message", "text": "Steve will lead the field survey, with Elara scouting and Orin supplying equipment."},
             {"type": "set_flag", "flag": "mc001.s003.survey_approved"},
             {"type": "add_value", "key": "rel.elara", "amount": "1"},
             {"type": "set_flag", "flag": "mc001.s002.complete"}]},
    ],
    "triggers": [
        {"id": "rowan_warning", "character": "elder_rowan", "dialogue": "rowan_warning", "radius": 40, "talk_radius": 3.5,
         "approach": True, "stages": ["warning"], "priority": 20},
        {"id": "bram_report", "character": "bram", "dialogue": "bram_report", "radius": 48, "talk_radius": 4,
         "approach": True, "stages": ["bram"], "priority": 20},
    ],
    "area_triggers": [],
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


S, R, E, B = "primordial_steve", "elder_rowan", "elara", "bram"
dialogues = {
    "rowan_warning": lines(
        (R, "Steve, I've been looking everywhere for you."), (S, "What's wrong?"),
        (R, "Something happened near the old northern ruins before sunrise."), (E, "The ruins? Nobody's been working there."),
        (R, "Exactly. The ground shook, and the watchtower reported strange lights beneath the stone."), (S, "An earthquake?"),
        (R, "I lived through earthquakes when I was younger. This felt different."), (E, "Different how?"), (R, "Almost deliberate.")),
    "clue_wall": lines(
        (S, "These cracks are fresh. The stones were pushed up from below, not knocked down."),
        (E, "Nothing in Firstbrook is heavy enough to do that.")),
    "clue_compass": lines(
        (S, "The surveyor's compass. The needle won't settle."),
        (E, "It keeps swinging north... then away again.")),
    "clue_report": lines(
        (S, "The night watch's report. 'Two tremors before dawn. Light seen beneath the stones of the northern ridge. Gone within moments.'"),
        (E, "They saw it too. So it wasn't just the ground.")),
    "bram_report": lines(
        (B, "The northern path isn't safe. Loose rocks came down just before dawn."), (S, "Anyone hurt?"),
        (B, "No. I closed the trail before the traders reached it."), (R, "Good. I'd rather lose a morning of trading than a single life."),
        (S, "I'll have a look."), (R, "Not alone. We need to know what we're dealing with.")),
    "heading_north": lines(
        (E, "That's where I was heading."), (S, "Then let's find out what happened."),
        (R, "First, prepare properly. There'll be no heroics today.")),
}

cinematics = {
    "mc001_s002_compass": {
        "duration": 170, "fade_in": 12, "fade_out": 12, "hide_player": False,
        "keyframes": [
            {"t": 0, "pos": [6.0, 2.3, -2.2], "yaw": 34, "pitch": 11, "fov": 50},
            {"t": 44, "pos": [5.4, 2.2, -1.5], "yaw": 35, "pitch": 13, "fov": 44},
            {"t": 46, "pos": [10.5, 30, -88], "yaw": 180, "pitch": 13, "fov": 50},
            {"t": 120, "pos": [10.5, 29.4, -94], "yaw": 180, "pitch": 14, "fov": 40},
            {"t": 170, "pos": [10.5, 29.2, -96], "yaw": 180, "pitch": 14, "fov": 38}],
        "subtitles": [{"t": 8, "duration": 34, "text": "The needle spins.", "style": "caption"}],
        "sounds": [
            {"t": 4, "sound": "minecraft:block.amethyst_block.resonate", "volume": 0.8, "pitch": 0.7},
            {"t": 20, "sound": "minecraft:item.lodestone_compass.lock", "volume": 0.9, "pitch": 0.6},
            {"t": 70, "sound": "minecraft:block.beacon.ambient", "volume": 0.7, "pitch": 0.6},
            {"t": 104, "sound": "minecraft:block.beacon.deactivate", "volume": 0.5, "pitch": 0.7}],
        "shakes": [{"t": 70, "duration": 20, "intensity": 0.05}],
        "cues": [
            {"t": 70, "actions": [{"type": "world_change", "id": "mc001_s002_ruin_light_on"}]},
            {"t": 104, "actions": [{"type": "world_change", "id": "mc001_s002_ruin_light_off"}]}],
    }
}

journal = {"northern_disturbance": {"title": "The Northern Disturbance", "text": (
    "Before sunrise the ground shook near the old northern ruins, and the night watch saw light beneath the stones. "
    "The wall by the square cracked from below, and the surveyor's compass will not settle. Bram closed the northern "
    "path after a rockfall. When I held the compass up I saw it myself: a light under the ridge, there and gone. "
    "Elder Rowan has put me in charge of the field survey. Elara will scout and Orin will see to our equipment. "
    "No heroics, he said.")}}

json.dump({"scene": scene, "dialogues": dialogues, "cinematics": cinematics, "journal": journal},
          open(S2, "w", encoding="utf8"), indent=2, ensure_ascii=False)
sc1["next_scene"] = "mc001_s002"
json.dump(s1, open(S1, "w", encoding="utf8"), indent=2, ensure_ascii=False)
index = json.load(open(INDEX))
index["scenes"] = ["mc001_s001.json", "mc001_s002.json"]
json.dump(index, open(INDEX, "w"), indent=2)
print("wrote", S2)
