"""Writes the Scene 017 bundle (rod-story/scenes/mc001_s017.json): "The Truth in the Fragments".

Dialogue is verbatim from docs/story/SAGA_1_SCENES_012_019_REVISED.md (Version 2). Hints, captions,
objectives, item names and the journal are gameplay text. Set in Valebridge's royal archive research
chamber, anchored at MC-001 block (-115, 67, 105); positions must match world/site/Valebridge.java
(SLOTS, TABLE_X/Z, the floor map and the record lecterns along z = -14).
Run from the repository root: python tools/story/build_s017.py
"""
from common import *

SLOTS = [(-41, -13), (-39, -12), (-39, -10), (-41, -9), (-43, -10), (-43, -12)]
EMBLEMS = ["minecraft:chiseled_stone_bricks", "minecraft:oxidized_chiseled_copper", "minecraft:chiseled_deepslate",
           "minecraft:chiseled_sandstone", "minecraft:chiseled_nether_bricks", "minecraft:dark_prismarine"]
FRAGMENTS = [("Eldervale fragment", "The carved stone from the royal Old Vault."),
             ("Sylvaris fragment", "Liora's rubbing of the grove stone, pressed into copper."),
             ("Stonecrown fragment", "The Builder machine plate from beneath Irondeep."),
             ("Solkar fragment", "Solkar's piece, from the sealed crate saved on the Eldervale road."),
             ("Veyrath fragment", "The Ember Key of Veyrath. Keep it apart from the door."),
             ("Thalassia fragment", "The Tide Anchor of Tidewatch. Made to hold something steady.")]

locs = {
    "hall_in": {"pos": [-27, 0, -1], "radius": 2},
    "elara_in": {"pos": [-27, 0, 1], "radius": 1},
    "bram_in": {"pos": [-27, 0, -3], "radius": 1},
    "chamber": {"pos": [-37, 0, -9], "radius": 3},
    "table_side": {"pos": [-41, 0, -15], "radius": 3},
    "queen": {"pos": [-41, 0, -15], "radius": 1},
    "garrick": {"pos": [-45, 0, -13], "radius": 1},
    "sahir": {"pos": [-45, 0, -9], "radius": 1},
    "kaelen": {"pos": [-37, 0, -13], "radius": 1},
    "maren": {"pos": [-37, 0, -10], "radius": 1},
    "liora": {"pos": [-44, 0, -15], "radius": 1},
    "orin": {"pos": [-38, 0, -15], "radius": 1},
    "elara": {"pos": [-43, 0, -7], "radius": 1},
    "bram": {"pos": [-39, 0, -7], "radius": 1},
    "kaelen_guard": {"pos": [-33, 0, -12], "radius": 1},
    "royal_guard": {"pos": [-36, 0, -7], "radius": 1},
    "table_light": {"pos": [-41, 1.2, -11], "radius": 1},
    "map": {"pos": [-40, 0, 5], "radius": 4},
    "quartermaster": {"pos": [-25, 0, -12], "radius": 2},
    "qm_spot": {"pos": [-25, 0, -14], "radius": 1},
    "orin_map": {"pos": [-36, 0, 3], "radius": 1},
    "sahir_map": {"pos": [-44, 0, 3], "radius": 1},
    "queen_map": {"pos": [-40, 0, 0], "radius": 1},
}

npcs = [
    {"character": E, "location": "elara_in", "yaw": 90},
    {"character": B, "location": "bram_in", "yaw": 90},
    {"character": AV, "location": "queen", "yaw": 0},
    {"character": G, "location": "garrick", "yaw": -90},
    {"character": SA, "location": "sahir", "yaw": -90},
    {"character": K, "location": "kaelen", "yaw": 90},
    {"character": M, "location": "maren", "yaw": 90},
    {"character": L, "location": "liora", "yaw": 0},
    {"character": O, "location": "orin", "yaw": 0},
    {"character": "veyrath_guard_1", "location": "kaelen_guard", "yaw": 90, "held": "minecraft:iron_sword"},
    {"character": "eldervale_royal_guard_1", "location": "royal_guard", "yaw": 180, "held": "minecraft:iron_sword"},
    {"character": "eldervale_official_1", "location": "qm_spot", "yaw": 90, "activity": "work"},
]

RECORDS = [("eldervale", -48, "Eldervale's record: 'the first stone, not to be moved'"),
           ("stonecrown", -46, "Stonecrown's record: 'the foundation'"),
           ("sylvaris", -36, "Sylvaris's record: 'the warning'"),
           ("solkar", -34, "Solkar's record: 'the boundary'"),
           ("veyrath", -30, "Veyrath's record: 'the sealed inheritance'"),
           ("thalassia", -28, "Thalassia's record: 'the navigation marker'")]

clues = {f"s17_rec_{k}": {"name": n, "pos": [x, 0, -14], "block": "minecraft:lectern"} for k, x, n in RECORDS}
clues["s17_writing"] = {"name": "Writing in the grooves of the diagram", "pos": [-41, 0, -11], "block": "minecraft:polished_blackstone"}
clues["s17_map_valley"] = {"name": "Map: where the six lines meet", "pos": [-40, 0, 4], "block": "minecraft:red_candle"}
clues["s17_map_stone"] = {"name": "Map: the old stone in the north", "pos": [-40, 0, 2], "block": "minecraft:red_candle"}
clues["s17_map_east"] = {"name": "Map: the old stone's eastern foot", "pos": [-38, 0, 2], "block": "minecraft:red_candle"}
clues["s17_map_west"] = {"name": "Map: the old stone's western foot", "pos": [-42, 0, 2], "block": "minecraft:red_candle"}

table_spots = " + ".join(f"{EMBLEMS[i]}|{x},1,{z}" for i, (x, z) in enumerate(SLOTS))

scene = {
    "id": "mc001_s017", "title": "The Truth in the Fragments", "universe": "MC-001", "saga": 1, "scene": 17,
    "canon_status": ("Saga 1 revised screenplay, Version 2 (docs/story/SAGA_1_SCENES_012_019_REVISED.md): spoken "
                     "dialogue verbatim. Hints, captions, item names and the outcome message are gameplay text."),
    "dimension": DIM, "site": "valebridge", "anchor": [-115, 67, 105],
    "arrival": {"pos": [-27, 0, -1], "yaw": 90},
    "next_scene": "mc001_s018",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "gather", "objective": "Join the council in the archive's research chamber", "target": "table_side", "checkpoint": "hall_in",
         "on_enter": [{"type": "teleport_player", "location": "hall_in", "yaw": 90},
                      *stay(E, "elara_in"), *stay(B, "bram_in"), *follow(E, B),
                      hint("Maren's ship brought everyone back to Valebridge. The six delegations are waiting round "
                           "the reconstruction table, through the doorway in the shelves.", 220)],
         "complete": {"type": "reach", "value": "table_side"},
         "on_complete": [*stay(E, "elara"), *stay(B, "bram"), cinematic("mc001_s017_council")]},
        {"id": "council_view", "checkpoint": "table_side",
         "complete": {"type": "cinematic_end", "value": "mc001_s017_council"}},
        {"id": "assemble", "objective": "Set all six fragments into the reconstruction table", "target": "table_side", "checkpoint": "table_side",
         "on_enter": [*[give(EMBLEMS[i], 1, FRAGMENTS[i][0], FRAGMENTS[i][1]) for i in range(6)],
                      hint("Each socket's inlay shows which civilisation's fragment belongs on it. "
                           "Place all six fragments on their matching inlays.", 240)],
         "complete": {"type": "blocks", "value": table_spots},
         "on_complete": [sound("minecraft:block.end_portal_frame.fill", 0.8, 0.7), dialogue("s17_centre")]},
        {"id": "centre_talk", "checkpoint": "table_side",
         "complete": {"type": "dialogue_end", "value": "s17_centre"},
         "on_complete": [dialogue("s17_interpretations")]},
        {"id": "interpretations_talk", "checkpoint": "table_side",
         "complete": {"type": "dialogue_end", "value": "s17_interpretations"}},
        {"id": "records", "objective": "Review the six civilisations' records", "target": "table_side", "checkpoint": "table_side",
         "on_enter": [hint("Each delegation has laid its oldest record on a lectern along the north wall. Read all six.", 200)],
         "complete": {"type": "clues", "value": ",".join(f"s17_rec_{k}" for k, _, _ in RECORDS)},
         "on_complete": [cinematic("mc001_s017_light")]},
        {"id": "light", "checkpoint": "table_side",
         "complete": {"type": "cinematic_end", "value": "mc001_s017_light"},
         "on_complete": [dialogue("s17_light")]},
        {"id": "light_talk", "checkpoint": "table_side",
         "complete": {"type": "dialogue_end", "value": "s17_light"}},
        {"id": "writing", "objective": "Read the writing the light has revealed", "target": "table_side", "checkpoint": "table_side",
         "on_enter": [hint("Under the skylight, fine lines show in the grooves at the centre of the diagram. Press {use} on the centre.", 200)],
         "complete": {"type": "clues", "value": "s17_writing"},
         "on_complete": [dialogue("s17_builders")]},
        {"id": "builders_talk", "checkpoint": "table_side",
         "complete": {"type": "dialogue_end", "value": "s17_builders"},
         "on_complete": [{"type": "npc_move", "character": O, "location": "orin_map"},
                         {"type": "npc_move", "character": SA, "location": "sahir_map"},
                         {"type": "npc_move", "character": AV, "location": "queen_map"}]},
        {"id": "route", "objective": "Decode the route on the great floor map", "target": "map", "checkpoint": "table_side",
         "on_enter": [hint("The spiral reads: begin where the six lines meet, climb to the old stone, go down at its "
                           "eastern foot. Mark those three points on the floor map in that order.", 260)],
         "complete": {"type": "sequence", "value": "s17_map_valley,s17_map_stone,s17_map_east"},
         "on_wrong": [hint("That doesn't follow the spiral. The route clears; begin again where the six lines meet.", 160),
                      sound("minecraft:block.candle.extinguish", 0.8)],
         "on_complete": [cinematic("mc001_s017_plan")]},
        {"id": "plan", "checkpoint": "map",
         "complete": {"type": "cinematic_end", "value": "mc001_s017_plan"},
         "on_complete": [dialogue("s17_plan")]},
        {"id": "plan_talk", "checkpoint": "map",
         "complete": {"type": "dialogue_end", "value": "s17_plan"},
         "on_complete": [change("mc001_s017_supplies")]},
        {"id": "supplies", "objective": "Collect torches and timber from the quartermaster's chest", "target": "quartermaster",
         "checkpoint": "map",
         "on_enter": [hint("The quartermaster's supply chest is by the east wall of the research chamber. "
                           "Take 16 torches and 16 spruce planks.", 220)],
         "complete": {"type": "items", "value": "minecraft:torch*16,minecraft:spruce_planks*16"},
         "on_complete": [
             {"type": "title", "title": "Act III Complete", "subtitle": "Six Histories, One Mystery"},
             sound("minecraft:ui.toast.challenge_complete", 0.7),
             {"type": "journal", "id": "s17_journal"},
             {"type": "message", "text": "The joint expedition is assembled. At dawn it sets out for the monument."},
             flag("mc001.expedition.assembled"), flag("mc001.s017.complete")]},
    ],
    "triggers": [], "area_triggers": [], "chatter": [],
}

dialogues = {
    "s17_centre": lines(
        (O, "That's everything."), (SA, "Not quite."), (E, "The centre is missing."),
        (G, "A seventh stone?"), (S, "Or something that was never meant to be removed.")),
    "s17_interpretations": lines(
        (G, "Stonecrown records describe it as a foundation."), (SA, "Solkar's scholars called it a boundary."),
        (M, "Our maps identify it as a navigation marker."), (L, "Sylvaris preserved it as a warning."),
        (K, "Veyrath's oldest accounts refer to a sealed inheritance."),
        (S, "You're all describing different parts of the same thing.")),
    "s17_light": lines(
        (O, "They're not contradicting one another."), (SA, "The foundation supports the structure."),
        (M, "The marker shows where it is."), (L, "The warning explains why it should remain undisturbed."),
        (S, "And the boundary?"), (SA, "That part we still don't understand.")),
    "s17_builders": lines(
        (SA, "There's writing in the grooves, finer than any chisel we own."),
        (SA, "'We who built before the crowns leave this to six keepers. Keep it apart. Keep it whole. Do not open the door.'"),
        (G, "Before the crowns. Before any of us."),
        (S, "The Ancient Builders. They made the gateway. Our kingdoms only inherited the pieces."),
        (K, "And swore oaths they never understood."),
        (K, "I owe this council an admission."), (AV, "We're listening."),
        (K, "I was prepared to treat this discovery as another dispute between kingdoms."), (S, "And now?"),
        (K, "Now I believe there's something far more important beneath the valley."),
        (L, "Then perhaps we can investigate it together."), (K, "We can try.")),
    "s17_plan": lines(
        (S, "Here. The central intersection."), (G, "My engineers can help secure the excavation."),
        (M, "I'll organise transport."), (SA, "I'll bring the translated records."),
        (L, "Sylvaris will send scouts."), (AV, "And Eldervale will provide supplies."),
        (B, "Sounds like we're going somewhere."), (E, "Somewhere nobody's been for a very long time.")),
}

cinematics = {
    "mc001_s017_council": {
        "duration": 180, "fade_in": 12, "fade_out": 10, "hide_player": True,
        "keyframes": [
            key(0, [-33.0, 4.5, -5.0], [-41, 1, -11], 62),
            key(180, [-47.0, 4.0, -6.0], [-41, 1, -11], 55)],
        "subtitles": [{"t": 20, "duration": 120, "text": "Six kingdoms, one table", "style": "caption"}],
        "sounds": [{"t": 0, "sound": "minecraft:block.wooden_door.close", "volume": 0.5, "pitch": 0.8}],
    },
    "mc001_s017_light": {
        "duration": 170, "fade_in": 8, "fade_out": 8, "hide_player": False,
        "keyframes": [
            key(0, [-41.0, 6.2, -16.0], [-41, 1, -11], 55),
            key(170, [-41.0, 4.2, -14.0], [-41, 1, -11], 42)],
        "subtitles": [{"t": 15, "duration": 120, "text": "Orin opens the skylight and sets a lamp above the diagram.", "style": "caption"}],
        "sounds": [{"t": 20, "sound": "minecraft:block.trapdoor.open", "volume": 0.7},
                   {"t": 60, "sound": "minecraft:block.amethyst_block.chime", "volume": 0.8, "pitch": 0.8}],
        "cues": [{"t": 25, "actions": [change("mc001_s017_skylight")]},
                 {"t": 70, "actions": [{"type": "particles", "particle": "minecraft:end_rod", "location": "table_light", "count": 18, "spread": 1.2}]}],
    },
    "mc001_s017_plan": {
        "duration": 200, "fade_in": 10, "fade_out": 10, "hide_player": True,
        "keyframes": [
            key(0, [-40.0, 7.0, 10.0], [-40, 0, 4], 60),
            key(200, [-40.0, 4.0, 7.5], [-39, 0, 2], 48)],
        "subtitles": [{"t": 20, "duration": 140, "text": "The route ends beneath the monument's eastern foundation.", "style": "caption"}],
        "sounds": [{"t": 30, "sound": "minecraft:item.book.page_turn", "volume": 0.8}],
        "cues": [{"t": 50, "actions": [{"type": "npc_look", "character": AV, "location": "map"}]}],
    },
}

journal = {"s17_journal": {"title": "Six Histories, One Mystery", "text": (
    "All six kingdoms came to Valebridge, and all six fragments fit the table, except for an empty space in the "
    "middle. Each kingdom remembered a different part: a foundation, a boundary, a marker, a warning, a sealed "
    "inheritance. Under Orin's light, writing showed in the grooves: the people who built before the crowns left "
    "it to six keepers and told them never to open the door. The Ancient Builders made the gateway; we only "
    "inherited the pieces. The spiral gives the way in, beneath the monument's eastern foundation. At dawn the "
    "joint expedition sets out.")}}

write_scene("mc001_s017", "mc001_s016", scene, dialogues, cinematics, journal)
