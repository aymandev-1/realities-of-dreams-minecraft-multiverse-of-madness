"""Writes the Scene 005 bundle (rod-story/scenes/mc001_s005.json) from the Saga 1 screenplay.

"Bram's Volunteers": spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md. Hints and
the outcome message are new gameplay text. The work is real building: BLOCKS stages check the
barricade, the platform planks, the lit route, the supply crates and the cleared rockfall. The
spots must match world/site/NorthRoad.java.
Run from the repository root: python tools/story/build_s005.py
"""
import copy
import json

S4 = "src/main/resources/rod-story/scenes/mc001_s004.json"
S5 = "src/main/resources/rod-story/scenes/mc001_s005.json"
INDEX = "src/main/resources/rod-story/index.json"

s4 = json.load(open(S4, encoding="utf8"))
sc4 = s4["scene"]

locs = copy.deepcopy(sc4["locations"])
locs.update({
    "gate_outside": {"pos": [0, 0, -25], "radius": 3},
    "bram_brief": {"pos": [-1, 0, -31], "radius": 1},
    "vol_1": {"pos": [-2, 0, -28], "radius": 1},
    "vol_2": {"pos": [0, 0, -28], "radius": 1},
    "vol_3": {"pos": [2, 0, -28], "radius": 1},
    "vol_4": {"pos": [-4, 0, -29], "radius": 1},
    "barricade": {"pos": [6, 0, -31], "radius": 3},
    "platform_base": {"pos": [9, 0, -33], "radius": 2},
    "platform_top": {"pos": [9, 5, -36], "radius": 2},
    "route": {"pos": [-1, 0, -33], "radius": 6},
    "supply_spot": {"pos": [12, 0, -36], "radius": 2},
    "rubble": {"pos": [1, 0, -39], "radius": 3},
    "worker_trapped": {"pos": [1, 0, -42], "radius": 1},
    "bram_far": {"pos": [1, 0, -45], "radius": 1},
    "orin_platform": {"pos": [7, 0, -34], "radius": 1},
    "rowan_platform": {"pos": [6, 0, -36], "radius": 1},
})

GROUP = {"bram": ("bram_brief", 0), "firstbrook_villager_2": ("vol_1", 180), "firstbrook_guard_1": ("vol_2", 180),
         "firstbrook_guard_2": ("vol_3", 180), "firstbrook_guard_3": ("vol_4", 180)}
npcs = []
for n in sc4["npcs"]:
    c = n["character"]
    if c in GROUP:
        loc, yaw = GROUP[c]
        n = {"character": c, "location": loc, "yaw": yaw}
        if c.startswith("firstbrook_guard") or c == "bram":
            n["held"] = "minecraft:iron_sword"
    npcs.append(copy.deepcopy(n))
npcs.append({"character": "expedition_worker_1", "location": "worker_trapped", "from_stage": "hazard", "yaw": 180,
             "held": "minecraft:stone_pickaxe"})

BARRICADE = "solid|4,0,-32;6,0,-32;8,0,-32;4,1,-32;6,1,-32;8,1,-32"
PLATFORM = "solid|9,4,-37;10,4,-37;10,4,-36"
LIGHTS = "light|1,2,-29;-5,2,-31;1,2,-34;-4,2,-37"
SUPPLIES = "minecraft:barrel|12,0,-36;12,0,-37"
RUBBLE = "air|" + ";".join(f"{x},{y},-40" for y in (0, 1) for x in range(-1, 4))

scene = {
    "id": "mc001_s005", "title": "Bram's Volunteers", "universe": "MC-001", "saga": 1, "scene": 5,
    "canon_status": "Saga 1 screenplay, Version 1 (docs/story/SAGA_1_SCREENPLAY.md): spoken dialogue verbatim. "
                    "Hints and the outcome message are new gameplay text.",
    "dimension": "realitiesofdreams:mc_001", "site": "firstbrook", "anchor": [0, 64, 0],
    "arrival": {"pos": [0, 0, -18], "yaw": 180},
    "next_scene": "unreleased:mc001_s006",
    "locations": locs, "npcs": npcs, "clues": {},
    "stages": [
        {"id": "arrive", "objective": "Meet Bram at the north gate", "target": "gate_outside", "checkpoint": "gate",
         "on_enter": [{"type": "npc_follow", "character": "elara"}, {"type": "world_change", "id": "mc001_s005_worksite"}],
         "complete": {"type": "reach", "value": "gate_outside"},
         "on_complete": [{"type": "start_cinematic", "id": "mc001_s005_volunteers"}]},
        {"id": "volunteers_cine", "checkpoint": "gate_outside",
         "complete": {"type": "cinematic_end", "value": "mc001_s005_volunteers"},
         "on_complete": [{"type": "start_dialogue", "id": "volunteers"}]},
        {"id": "volunteers_talk", "checkpoint": "gate_outside",
         "complete": {"type": "dialogue_end", "value": "volunteers"},
         "on_complete": [
             {"type": "give_item", "item": "minecraft:oak_planks", "count": "12"},
             {"type": "give_item", "item": "minecraft:torch", "count": "6"},
             {"type": "give_item", "item": "minecraft:barrel", "count": "2", "name": "Supply crate"}]},
        {"id": "barricade", "objective": "Build the barricade between the posts", "target": "barricade", "checkpoint": "gate_outside",
         "on_enter": [{"type": "hint", "ticks": 200, "text": "Green sparkles mark where blocks are needed. Place blocks with {use}."}],
         "complete": {"type": "blocks", "value": BARRICADE}},
        {"id": "platform", "objective": "Repair the watch platform", "target": "platform_base", "checkpoint": "gate_outside",
         "complete": {"type": "blocks", "value": PLATFORM}},
        {"id": "lights", "objective": "Light the safe route", "target": "route", "checkpoint": "gate_outside",
         "on_enter": [{"type": "hint", "ticks": 160, "text": "Put a torch on top of each lantern post."}],
         "complete": {"type": "blocks", "value": LIGHTS}},
        {"id": "supplies", "objective": "Move the supply crates beside the platform", "target": "supply_spot", "checkpoint": "gate_outside",
         "complete": {"type": "blocks", "value": SUPPLIES}},
        {"id": "hazard", "objective": "Clear the rockfall", "target": "rubble", "checkpoint": "gate_outside",
         "on_enter": [{"type": "world_change", "id": "mc001_s005_rockfall"},
                      {"type": "sound", "sound": "minecraft:entity.generic.explode", "volume": 0.5, "pitch": 0.5},
                      {"type": "sound", "sound": "minecraft:block.gravel.fall", "volume": 1.0, "pitch": 0.6},
                      {"type": "shake", "intensity": 0.12, "ticks": 30},
                      {"type": "npc_spawn", "character": "bram", "location": "bram_far"},
                      {"type": "npc_stay", "character": "bram", "location": "bram_far"},
                      {"type": "start_dialogue", "id": "rockfall"}],
         "complete": {"type": "blocks", "value": RUBBLE}},
        {"id": "rescued", "checkpoint": "rubble",
         "on_enter": [{"type": "start_dialogue", "id": "nicely_done"}],
         "complete": {"type": "dialogue_end", "value": "nicely_done"},
         "on_complete": [{"type": "npc_spawn", "character": "orin", "location": "orin_platform"},
                         {"type": "npc_stay", "character": "orin", "location": "orin_platform"},
                         {"type": "npc_spawn", "character": "elder_rowan", "location": "rowan_platform"},
                         {"type": "npc_stay", "character": "elder_rowan", "location": "rowan_platform"}]},
        {"id": "to_platform", "objective": "Climb the watch platform", "target": "platform_top", "checkpoint": "platform_base",
         "complete": {"type": "reach", "value": "platform_top"},
         "on_complete": [{"type": "world_change", "id": "mc001_s005_monument"}]},
        {"id": "spot", "checkpoint": "platform_top",
         "on_enter": [{"type": "start_cinematic", "id": "mc001_s005_spot"}],
         "complete": {"type": "cinematic_end", "value": "mc001_s005_spot"},
         "on_complete": [{"type": "start_dialogue", "id": "there_it_is"}]},
        {"id": "there_talk", "checkpoint": "platform_top",
         "complete": {"type": "dialogue_end", "value": "there_it_is"},
         "on_complete": [{"type": "start_cinematic", "id": "mc001_s005_rise"}]},
        {"id": "rise", "checkpoint": "platform_top",
         "complete": {"type": "cinematic_end", "value": "mc001_s005_rise"},
         "on_complete": [
             {"type": "title", "title": "Mission Complete", "subtitle": "Secure the Northern Road"},
             {"type": "sound", "sound": "minecraft:ui.toast.challenge_complete", "volume": 0.7},
             {"type": "journal", "id": "north_road"},
             {"type": "message", "text": "Act I complete: the north road is secure, and the investigation party can enter the ancient ruins."},
             {"type": "set_flag", "flag": "mc001.north_road.secure"},
             {"type": "set_flag", "flag": "mc001.act1.complete"},
             {"type": "set_flag", "flag": "mc001.s005.complete"}]},
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


S, E, O, R, B = "primordial_steve", "elara", "orin", "elder_rowan", "bram"
VOL, WORKER = "firstbrook_villager_2", "expedition_worker_1"
dialogues = {
    "volunteers": lines(
        (B, "Everyone listen! Nobody goes past the north marker without an escort."), (VOL, "It's just an old ruin."),
        (B, "Then you won't mind waiting until we know it's safe."),
        (B, "Good. I need somebody who knows how to build a proper barricade."), (S, "You could've just asked."),
        (B, "I was hoping you'd volunteer.")),
    "rockfall": lines(
        (WORKER, "Help! I can't get through!"), (S, "Stay back from the rocks!"), (B, "I'll clear the other side!")),
    "nicely_done": lines(
        (B, "Nicely done."), (S, "We're lucky nobody got hurt."), (B, "Luck won't keep us safe forever.")),
    "there_it_is": lines(
        (E, "There it is."), (S, "The ruins?"), (O, "Not exactly. That structure doesn't match any record I've seen."),
        (R, "Then we proceed carefully."), (S, "We go tomorrow.")),
}

cinematics = {
    "mc001_s005_volunteers": {
        "duration": 140, "fade_in": 12, "fade_out": 10, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [5.0, 3.0, -34.0], "yaw": 53, "pitch": 11, "fov": 58},
            {"t": 140, "pos": [4.2, 2.6, -33.2], "yaw": 54, "pitch": 10, "fov": 50}],
        "subtitles": [],
        "sounds": [{"t": 10, "sound": "minecraft:block.wood.place", "volume": 0.6},
                   {"t": 60, "sound": "minecraft:block.anvil.use", "volume": 0.2, "pitch": 0.9}],
    },
    "mc001_s005_spot": {
        "duration": 140, "fade_in": 10, "fade_out": 0, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [9.5, 6.6, -36.5], "yaw": -169.5, "pitch": -17, "fov": 34},
            {"t": 140, "pos": [9.5, 6.6, -36.8], "yaw": -169.6, "pitch": -17, "fov": 24}],
        "subtitles": [{"t": 20, "duration": 80, "text": "Beyond the valley, something old stands against the sky.", "style": "caption"}],
        "sounds": [{"t": 30, "sound": "minecraft:block.beacon.ambient", "volume": 0.5, "pitch": 0.5}],
    },
    "mc001_s005_rise": {
        "duration": 200, "fade_in": 0, "fade_out": 20, "hide_player": True,
        "keyframes": [
            {"t": 0, "pos": [9.5, 8.0, -34.0], "yaw": -170, "pitch": -15, "fov": 50},
            {"t": 200, "pos": [14.0, 55.0, -20.0], "yaw": -171.5, "pitch": -4, "fov": 55}],
        "subtitles": [{"t": 60, "duration": 110, "text": "ACT I COMPLETE", "style": "title"}],
        "sounds": [{"t": 0, "sound": "minecraft:ui.toast.challenge_complete", "volume": 0.3, "pitch": 0.7}],
    },
}

journal = {"north_road": {"title": "Secure the Northern Road", "text": (
    "Bram rounded up volunteers at the north gate and, as usual, volunteered me. We built a barricade, mended the "
    "watch platform, lit the posts along the road and moved the supplies. Then the hillside came down across the "
    "path with a worker caught behind it. We dug him out; nobody was hurt. From the platform I saw it: a structure "
    "on the mountain beyond the valley that Orin says matches no record he has. We go tomorrow, carefully.")}}

json.dump({"scene": scene, "dialogues": dialogues, "cinematics": cinematics, "journal": journal},
          open(S5, "w", encoding="utf8"), indent=2, ensure_ascii=False)
sc4["next_scene"] = "mc001_s005"
json.dump(s4, open(S4, "w", encoding="utf8"), indent=2, ensure_ascii=False)
index = json.load(open(INDEX))
if "mc001_s005.json" not in index["scenes"]:
    index["scenes"].append("mc001_s005.json")
json.dump(index, open(INDEX, "w"), indent=2)
print("wrote", S5)
