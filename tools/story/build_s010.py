"""Writes the Scene 010 bundle (rod-story/scenes/mc001_s010.json) from the Saga 1 screenplay.

"Liora's Warning": spoken dialogue is verbatim from docs/story/SAGA_1_SCREENPLAY.md. Hints,
captions, item names and the outcome message are new gameplay text. The scene has its own site,
the Sylvaris border outpost, anchored at MC-001 block (150, 66, 40); positions must match
world/site/SylvarisOutpost.java.
Run from the repository root: python tools/story/build_s010.py
"""
from common import *

locs = {
    "path_west": {"pos": [-40, 0, 0], "radius": 2},
    "elara_start": {"pos": [-41, 0, 2], "radius": 1},
    "bram_start": {"pos": [-41, 0, -2], "radius": 1},
    "outpost": {"pos": [-17, 0, 3], "radius": 4},
    "liora_platform": {"pos": [-6, 5, 4], "radius": 1},
    "ranger_platform": {"pos": [0, 5, -3], "radius": 1},
    "ranger_lookout": {"pos": [-6, 12, -1], "radius": 1},
    "ranger_lead": {"pos": [16, 4, 7], "radius": 1},
    "walkway_start": {"pos": [17, 4, 9], "radius": 3},
    "gap": {"pos": [25, 4, 8], "radius": 2},
    "far_platform": {"pos": [36, 4, 8], "radius": 3},
    "grove": {"pos": [37, 0, -10], "radius": 4},
    "ranger_grove": {"pos": [33, 0, -18], "radius": 1},
    "stone_side": {"pos": [36, 0, -11], "radius": 2},
    "liora_stone": {"pos": [38, 0, -13], "radius": 1},
    "tower_foot": {"pos": [44, 0, -32], "radius": 2},
    "liora_tower": {"pos": [44, 0, -28], "radius": 1},
    "tower_top": {"pos": [40, 12, -31], "radius": 2},
    "ranger_tower": {"pos": [41, 12, -30], "radius": 1},
    "stone_glow": {"pos": [36, 1, -14], "radius": 1},
}

npcs = [
    {"character": E, "location": "elara_start", "yaw": -90},
    {"character": B, "location": "bram_start", "yaw": -90, "held": "minecraft:iron_sword"},
    {"character": L, "location": "liora_platform", "yaw": 90, "held": "minecraft:bow"},
    {"character": "sylvaris_ranger_1", "location": "ranger_platform", "yaw": 90, "held": "minecraft:bow"},
    {"character": "sylvaris_ranger_2", "location": "ranger_lookout", "yaw": 90},
    {"character": "sylvaris_ranger_3", "location": "ranger_grove", "yaw": 0, "held": "minecraft:spruce_sapling", "activity": "work"},
    {"character": "sylvaris_ranger_4", "location": "ranger_tower", "yaw": -90},
]

clues = {
    "s10_stone": {"name": "Exposed ancient stone", "pos": [36, 0, -14], "block": "minecraft:chiseled_tuff"},
    "s10_record": {"name": "Old border record", "pos": [40, 12, -31], "block": "minecraft:lectern"},
}

scene = {
    "id": "mc001_s010", "title": "Liora's Warning", "universe": "MC-001", "saga": 1, "scene": 10,
    "canon_status": CANON + " The rangers' route, the walkway and the watchtower record follow the scene's listed objectives.",
    "dimension": DIM, "site": "sylvaris_outpost", "anchor": [150, 66, 40],
    "arrival": {"pos": [-40, 0, 0], "yaw": -90},
    "next_scene": "unreleased:mc001_s011",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "to_outpost", "objective": "Follow the forest path to the Sylvaris outpost", "target": "outpost", "checkpoint": "path_west",
         "on_enter": [{"type": "teleport_player", "location": "path_west", "yaw": -90},
                      *stay(E, "elara_start"), *stay(B, "bram_start"), *follow(E, B),
                      hint("The post at the empty campsite pointed the way to Sylvaris. Follow the path into the forest.", 160)],
         "complete": {"type": "reach", "value": "outpost"},
         "on_complete": [cinematic("mc001_s010_outpost")]},
        {"id": "outpost_view", "checkpoint": "outpost",
         "complete": {"type": "cinematic_end", "value": "mc001_s010_outpost"},
         "on_complete": [dialogue("s10_greeting")]},
        {"id": "greeting_talk", "checkpoint": "outpost",
         "complete": {"type": "dialogue_end", "value": "s10_greeting"},
         "on_complete": [dialogue("s10_humming")]},
        {"id": "humming_talk", "checkpoint": "outpost",
         "complete": {"type": "dialogue_end", "value": "s10_humming"},
         "on_complete": [{"type": "npc_move", "character": "sylvaris_ranger_1", "location": "ranger_lead"}, *follow(L)]},
        {"id": "follow", "objective": "Follow the rangers toward the eastern grove", "target": "walkway_start", "checkpoint": "outpost",
         "on_enter": [hint("Keep to the path and the platforms. Sylvaris guards these trees; nothing here needs cutting.", 200)],
         "complete": {"type": "reach", "value": "walkway_start"}},
        {"id": "walkway", "objective": "Stabilise the fallen walkway", "target": "gap", "checkpoint": "walkway_start",
         "on_enter": [give("minecraft:spruce_planks", 10),
                      hint("The middle of the walkway has fallen into the ravine. Lay planks where the green sparkles are.", 200)],
         "complete": {"type": "blocks", "value": "solid|24,3,8;25,3,8;26,3,8;27,3,8;24,3,9;25,3,9;26,3,9;27,3,9"},
         "on_complete": [sound("minecraft:block.wood.place", 1.0, 0.8), *follow(E, B, L)]},
        {"id": "cross", "objective": "Cross to the eastern grove", "target": "grove", "checkpoint": "walkway_start",
         "complete": {"type": "reach", "value": "grove"},
         "on_complete": [*stay(L, "liora_stone")]},
        {"id": "stone", "objective": "Examine the exposed ancient stone", "target": "stone_side", "checkpoint": "grove",
         "on_enter": [hint("Roots have heaved the ground up round an old stone. Press {use} on it.", 160)],
         "complete": {"type": "clues", "value": "s10_stone"},
         "on_complete": [sound("minecraft:block.amethyst_block.resonate", 0.8, 0.6), *stay(L, "liora_tower")]},
        {"id": "record", "objective": "Help Liora fetch the old border record from the watchtower", "target": "tower_top", "checkpoint": "grove",
         "on_enter": [hint("The watchtower stands south of the grove. Climb the ladder inside and press {use} on the lectern at the top.", 200)],
         "complete": {"type": "clues", "value": "s10_record"},
         "on_complete": [give("minecraft:paper", 1, "Sylvaris border record", "Survey of the eastern grove, many generations old.|It marks the stone with six small carvings.")]},
        {"id": "back_to_stone", "objective": "Bring the record back to the ancient stone", "target": "stone_side", "checkpoint": "tower_foot",
         "on_enter": [*stay(L, "liora_stone")],
         "complete": {"type": "reach", "value": "stone_side"},
         "on_complete": [cinematic("mc001_s010_compare")]},
        {"id": "compare", "checkpoint": "grove",
         "complete": {"type": "cinematic_end", "value": "mc001_s010_compare"},
         "on_complete": [dialogue("s10_six_marks")]},
        {"id": "six_marks_talk", "checkpoint": "grove",
         "complete": {"type": "dialogue_end", "value": "s10_six_marks"},
         "on_complete": [dialogue("s10_warning")]},
        {"id": "warning_talk", "checkpoint": "grove",
         "complete": {"type": "dialogue_end", "value": "s10_warning"},
         "on_complete": [
             give("minecraft:paper", 1, "Liora's warning", "Sealed with the leaf of Sylvaris.|For the elders of Firstbrook and the royal court."),
             {"type": "title", "title": "Act II Complete", "subtitle": "The Forest's Message"},
             sound("minecraft:ui.toast.challenge_complete", 0.7),
             {"type": "journal", "id": "forest_message"},
             {"type": "message", "text": "Liora is coming to the royal court herself. The mystery now reaches beyond Firstbrook."},
             flag("mc001.sylvaris.liora_ally"), flag("mc001.s010.complete")]},
    ],
    "triggers": [], "area_triggers": [], "chatter": [],
}

JOURNAL_ACT = [{"type": "hold_item", "item": "minecraft:written_book"}]

dialogues = {
    "s10_greeting": lines(
        (L, "Travellers from Firstbrook?"), (E, "Liora?"),
        (L, "Elara. I hoped the next time we met would be under better circumstances."),
        (S, "You've been expecting us?"), (L, "We've been expecting someone to follow the tremors.")),
    "s10_humming": lines(
        (L, "The oldest trees have stood here for generations. Their roots reach farther than our records."),
        (S, "And?"), (L, "Yesterday, the ground beneath them started humming."), (B, "Humming?"),
        (L, "A sound without a source. Some of our watchtowers heard it at the same time."),
        (E, "Did you find anything?"),
        (L, "An ancient stone buried beneath the eastern grove. It has the same markings as the drawing you brought.", JOURNAL_ACT),
        (L, "Where did you get that?"), (S, "An abandoned camp."),
        (L, "Then those explorers may have reached farther than we realised.")),
    "s10_six_marks": lines(
        (L, "Six marks. Six civilisations."), (S, "That's what we thought."),
        (L, "Then this is no longer a matter for Firstbrook alone."), (E, "What are you suggesting?"),
        (L, "You need the rulers and representatives together before someone makes a terrible mistake.")),
    "s10_warning": lines(
        (L, "Take this to your elders. Ask them to contact the royal court."), (S, "Will Sylvaris help?"),
        (L, "I'll come myself."), (S, "Then we'll see you there.")),
}

cinematics = {
    "mc001_s010_outpost": {
        "duration": 230, "fade_in": 15, "fade_out": 10, "hide_player": True,
        "keyframes": [
            key(0, [-24.0, 1.6, 8.0], [-4, 14, 0], 62),
            key(120, [-18.0, 3.0, 9.0], [-4, 9, 0], 58),
            key(230, [-12.0, 6.2, 8.5], [-6, 6.6, 4.5], 50)],
        "subtitles": [{"t": 30, "duration": 100, "text": "Sylvaris Border Outpost", "style": "title"}],
        "sounds": [{"t": 0, "sound": "minecraft:block.azalea_leaves.step", "volume": 0.4},
                   {"t": 60, "sound": "minecraft:entity.parrot.ambient", "volume": 0.3, "pitch": 1.2}],
        "cues": [{"t": 90, "actions": [{"type": "npc_look", "character": L, "location": "outpost"}]}],
    },
    "mc001_s010_compare": {
        "duration": 150, "fade_in": 8, "fade_out": 8, "hide_player": False,
        "keyframes": [
            key(0, [33.0, 2.6, -10.5], [36.5, 0.5, -13.5], 55),
            key(150, [34.0, 2.0, -11.5], [36.5, 0.5, -13.5], 42)],
        "subtitles": [{"t": 20, "duration": 110, "text": "Liora holds the drawing beside the stone. The six marks match.", "style": "caption"}],
        "sounds": [{"t": 30, "sound": "minecraft:block.amethyst_block.chime", "volume": 0.8, "pitch": 0.7}],
        "cues": [{"t": 40, "actions": [{"type": "particles", "particle": "minecraft:end_rod", "location": "stone_glow", "count": 14, "spread": 0.4}]}],
    },
}

journal = {"forest_message": {"title": "The Forest's Message", "text": (
    "Liora of Sylvaris was waiting for us. Her rangers have heard the ground humming beneath the oldest trees, "
    "and they found an ancient stone in the eastern grove with the same markings as the expedition's drawing. "
    "We mended a fallen walkway to reach it and fetched an old border record from the watchtower. Six marks: "
    "six civilisations. Liora says the rulers must be brought together, and she is coming to the royal court herself.")}}

write_scene("mc001_s010", "mc001_s009", scene, dialogues, cinematics, journal)
