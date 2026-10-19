"""Writes the Scene 011 bundle (rod-story/scenes/mc001_s011.json) from the Saga 1 screenplay.

"The Raiders of the Eastern Road": spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md.
Hints, captions, item names and the outcome message are new gameplay text. The scene has its own
site on the Eldervale trade road, anchored at MC-001 block (100, 66, 135); positions must match
world/site/EasternRoad.java. The fight runs in the screenplay's three phases: the front assault,
the flanking attempt from the ridge, and the leader. Raiders give up and run once beaten.
Run from the repository root: python tools/story/build_s011.py
"""
import math

from common import *


def ridge(x, z):
    """Standing height on the ridge north of the road (EasternRoad.ground)."""
    if z >= -8:
        return 0
    rise = min(8, (-8 - z) * 0.65 + 1.5 * math.sin(x / 7.0))
    return int(max(0, round(rise)))


locs = {
    "road_west": {"pos": [-40, 0, 0], "radius": 2},
    "elara_start": {"pos": [-41, 0, 2], "radius": 1},
    "bram_start": {"pos": [-41, 0, -2], "radius": 1},
    "liora_start": {"pos": [-42, 0, 0], "radius": 1},
    "caravan": {"pos": [-7, 0, 0], "radius": 4},
    "merchant_wagon": {"pos": [-6, 0, 1], "radius": 1},
    "merchant_cart": {"pos": [-16, 0, 1], "radius": 1},
    "merchant_side": {"pos": [-9, 0, -2], "radius": 1},
    "merchant_shelter": {"pos": [-18, 0, 4], "radius": 1},
    "leader_block": {"pos": [10, 0, 0], "radius": 1},
    "raider_1_block": {"pos": [12, 0, -2], "radius": 1},
    "raider_2_block": {"pos": [12, 0, 2], "radius": 1},
    "leader_back": {"pos": [24, 0, 0], "radius": 1},
    "ridge_1": {"pos": [4, ridge(4, -16), -16], "radius": 1},
    "ridge_2": {"pos": [9, ridge(9, -17), -17], "radius": 1},
    "front": {"pos": [6, 0, 0], "radius": 6},
    "ridge_foot": {"pos": [6, 0, -8], "radius": 5},
    "crate_side": {"pos": [-6, 0, 3], "radius": 2},
}

npcs = [
    {"character": E, "location": "elara_start", "yaw": -90, "held": "minecraft:iron_sword"},
    {"character": B, "location": "bram_start", "yaw": -90, "held": "minecraft:iron_sword"},
    {"character": L, "location": "liora_start", "yaw": -90, "held": "minecraft:bow"},
    {"character": "merchant_1", "location": "merchant_wagon", "yaw": -90},
    {"character": "merchant_2", "location": "merchant_cart", "yaw": -90},
    {"character": "merchant_3", "location": "merchant_side", "yaw": 90, "until_stage": "front"},
    {"character": "merchant_3", "location": "merchant_shelter", "yaw": 90, "from_stage": "front"},
    {"character": "raider_leader_1", "location": "leader_block", "yaw": 90, "held": "minecraft:iron_axe",
     "from_stage": "standoff_talk", "until_stage": "leader"},
    {"character": "raider_leader_1", "location": "leader_back", "yaw": 90, "held": "minecraft:iron_axe",
     "from_stage": "leader", "activity": "hostile"},
    {"character": "raider_1", "location": "raider_1_block", "yaw": 90, "held": "minecraft:wooden_sword",
     "from_stage": "standoff_talk", "until_stage": "front"},
    {"character": "raider_1", "location": "raider_1_block", "yaw": 90, "held": "minecraft:wooden_sword",
     "from_stage": "front", "activity": "hostile"},
    {"character": "raider_2", "location": "raider_2_block", "yaw": 90, "held": "minecraft:stone_axe",
     "from_stage": "standoff_talk", "until_stage": "front"},
    {"character": "raider_2", "location": "raider_2_block", "yaw": 90, "held": "minecraft:stone_axe",
     "from_stage": "front", "activity": "hostile"},
    {"character": "raider_3", "location": "ridge_1", "yaw": 0, "held": "minecraft:wooden_axe",
     "from_stage": "flank", "activity": "hostile"},
    {"character": "raider_4", "location": "ridge_2", "yaw": 0, "held": "minecraft:wooden_sword",
     "from_stage": "flank", "activity": "hostile"},
]

clues = {
    "s11_crate": {"name": "Sealed crate with a faded emblem", "pos": [-5, 0, 3], "block": "minecraft:barrel"},
}

FIGHT_HINT = ("Fight with {attack}; your pickaxe will do. Raiders give up and run once they have taken enough. "
              "Bram, Elara and Liora fight beside you. If you fall, you wake at the caravan.")

scene = {
    "id": "mc001_s011", "title": "The Raiders of the Eastern Road", "universe": "MC-001", "saga": 1, "scene": 11,
    "canon_status": CANON + " The fight follows the screenplay's three phases; raiders retreat when beaten.",
    "dimension": DIM, "site": "eastern_road", "anchor": [100, 66, 135],
    "arrival": {"pos": [-40, 0, 0], "yaw": -90},
    "next_scene": "unreleased:mc001_s012",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "to_caravan", "objective": "Catch up with the merchant caravan on the road to Eldervale", "target": "caravan", "checkpoint": "road_west",
         "on_enter": [{"type": "teleport_player", "location": "road_west", "yaw": -90},
                      *stay(E, "elara_start"), *stay(B, "bram_start"), *stay(L, "liora_start"), *follow(E, B, L),
                      cinematic("mc001_s011_road")],
         "complete": {"type": "reach", "value": "caravan"},
         "on_complete": [dialogue("s11_strange_roads")]},
        {"id": "road_talk", "checkpoint": "caravan",
         "complete": {"type": "dialogue_end", "value": "s11_strange_roads"}},
        {"id": "standoff_talk", "checkpoint": "caravan",
         "on_enter": [sound("minecraft:entity.pillager.celebrate", 0.8, 0.8), dialogue("s11_standoff")],
         "complete": {"type": "dialogue_end", "value": "s11_standoff"},
         "on_complete": [give("minecraft:shield", 1, "Caravan shield", "Raise it to turn aside a blow.")]},
        {"id": "front", "objective": "Drive the raiders away from the wagon", "target": "front", "checkpoint": "caravan",
         "on_enter": [{"type": "npc_hostile", "character": "raider_1"}, {"type": "npc_hostile", "character": "raider_2"},
                      *follow(E, B, L), dialogue("s11_wagon"), hint(FIGHT_HINT, 260)],
         "complete": {"type": "defeated", "value": "raider_1,raider_2"}},
        {"id": "flank", "objective": "Stop the raiders coming down from the ridge", "target": "ridge_foot", "checkpoint": "caravan",
         "on_enter": [dialogue("s11_ridge"), {"type": "shake", "intensity": 0.03, "ticks": 10}],
         "complete": {"type": "defeated", "value": "raider_3,raider_4"}},
        {"id": "leader", "objective": "Face the raider leader", "target": "front", "checkpoint": "caravan",
         "on_enter": [{"type": "npc_hostile", "character": "raider_leader_1"}, dialogue("s11_hold_road")],
         "complete": {"type": "defeated", "value": "raider_leader_1"},
         "on_complete": [dialogue("s11_retreat")]},
        {"id": "retreat_talk", "checkpoint": "caravan",
         "complete": {"type": "dialogue_end", "value": "s11_retreat"},
         "on_complete": [dialogue("s11_after")]},
        {"id": "after_talk", "checkpoint": "caravan",
         "complete": {"type": "dialogue_end", "value": "s11_after"}},
        {"id": "crate", "objective": "Look at the sealed crate the raiders were after", "target": "crate_side", "checkpoint": "caravan",
         "on_enter": [hint("One crate in the caravan is sealed and marked. Press {use} on it.", 160)],
         "complete": {"type": "clues", "value": "s11_crate"},
         "on_complete": [
             give("minecraft:bread", 6), give("minecraft:emerald", 3),
             *complete("Mission Complete", "Defend the Caravan", "caravan",
                       "The merchant says the crate came from a ruined trading post and was bound for Eldervale. "
                       "Word of the caravan will reach the royal court ahead of you.",
                       ["mc001.caravan.saved", "mc001.s011.complete"])]},
    ],
    "triggers": [], "area_triggers": [],
    "chatter": [
        {"id": "s11_hurt_bram", "character": B, "text": "Steve! Keep them away from the wagon!", "stages": ["front"], "context": "combat"},
    ],
}

dialogues = {
    "s11_strange_roads": lines(
        ("merchant_1", "You came from Sylvaris?"), (L, "The eastern outpost."),
        ("merchant_1", "Then you may know why the roads have been so strange lately."), (S, "Strange how?"),
        ("merchant_1", "People disappearing. Animals refusing to cross certain bridges. And raiders taking advantage of it."),
        (B, "Speaking of raiders...")),
    "s11_standoff": lines(
        ("raider_leader_1", "Leave the supplies and walk away!"), (B, "Wrong caravan."),
        (S, "Nobody needs to get hurt. Let the travellers pass."),
        ("raider_leader_1", "You've got a lot of confidence for someone holding a pickaxe."),
        (S, "It's a very good pickaxe.")),
    "s11_wagon": lines((B, "Steve! Keep them away from the wagon!"), critical=False),
    "s11_ridge": lines((E, "Two coming from the ridge!"), (S, "I've got them!"), critical=False),
    "s11_hold_road": lines((L, "The merchants are moving! Hold the road!"), critical=False),
    "s11_retreat": lines(("raider_leader_1", "This road isn't worth dying for!")),
    "s11_after": lines(
        (B, "Not bad."), (S, "You sound surprised."), (B, "I'm always surprised when your plans work."),
        (L, "Everyone's safe."), (S, "Then let's get them home.")),
}

cinematics = {
    "mc001_s011_road": {
        "duration": 200, "fade_in": 15, "fade_out": 10, "hide_player": True,
        "keyframes": [
            key(0, [-30.0, 9.0, 14.0], [-6, 1, 2], 60),
            key(200, [-18.0, 4.0, 7.0], [-4, 1, 2], 55)],
        "subtitles": [{"t": 25, "duration": 100, "text": "The Eldervale trade road", "style": "title"}],
        "sounds": [{"t": 0, "sound": "minecraft:entity.horse.step_wood", "volume": 0.4},
                   {"t": 80, "sound": "minecraft:entity.horse.ambient", "volume": 0.3}],
    },
}

journal = {"caravan": {"title": "Defend the Caravan", "text": (
    "On the road to Eldervale we fell in with a merchant caravan. The merchants say people have been disappearing, "
    "animals will not cross certain bridges, and raiders are taking advantage. Then the raiders came: two at the front, "
    "two down off the ridge, and their leader last of all. We held the road and he ran. One crate in the caravan is "
    "sealed with a faded six-point emblem; it came from a ruined trading post and was bound for Eldervale.")}}

write_scene("mc001_s011", "mc001_s010", scene, dialogues, cinematics, journal)
