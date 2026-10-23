"""Writes the Scene 013 bundle (rod-story/scenes/mc001_s013.json) from the revised screenplay.

"The Stone of Irondeep": spoken dialogue is verbatim from docs/story/SAGA_1_SCENES_012_019_REVISED.md
(the (S)/(A)/(N) markers dropped). Hints, captions, item names and the outcome message are gameplay
text. Two optional city lines are added to the screenplay's three (see AMBIENT below). The scene
has its own site, the underground capital of Irondeep, anchored at MC-001 block (-160, 24, -170)
with y = 0 the main cavern floor; positions must match world/site/Irondeep.java.
Run from the repository root: python tools/story/build_s013.py
"""
from common import *

CANON = ("Saga 1 revised screenplay, Version 2 (docs/story/SAGA_1_SCENES_012_019_REVISED.md): spoken dialogue "
         "verbatim. Hints, captions, item names and the outcome message are new gameplay text.")

GUARD, GUARD2 = "stonecrown_guard_1", "stonecrown_guard_2"
ENG = ["stonecrown_engineer_%d" % i for i in range(1, 5)]
MIN = ["irondeep_miner_%d" % i for i in range(1, 9)]

# Old Deeps levels (Irondeep.java): the landing and ledge stand at y -19, the works hall floor at -22.
LAND, DEEP = -19, -22

locs = {
    # The lift and its gate (south end of the cavern).
    "lift_car": {"pos": [0, 0, 58], "radius": 2},
    "elara_start": {"pos": [-2, 0, 57], "radius": 1},
    "bram_start": {"pos": [2, 0, 57], "radius": 1},
    "lift_gate": {"pos": [0, 0, 49], "radius": 4},
    "orin_gate": {"pos": [1, 0, 50], "radius": 1},
    "guard_gate": {"pos": [-4, 0, 47], "radius": 1},
    "lift_machine": {"pos": [6, 0, 51], "radius": 1},
    # The city.
    "plaza": {"pos": [0, 0, 31], "radius": 4},
    "market_stall": {"pos": [12, 0, 19], "radius": 1},
    "market_steps": {"pos": [9, 0, 26], "radius": 1},
    "foundry": {"pos": [-20, 0, 14], "radius": 1},
    "residence": {"pos": [-45, 0, 20], "radius": 1},
    "ledge": {"pos": [-45, 6, 30], "radius": 1},
    "depot": {"pos": [48, 0, 30], "radius": 1},
    "mine_cart": {"pos": [42, 0, -44], "radius": 1},
    "mine_face_east": {"pos": [50, 0, -50], "radius": 1},
    "mine_face_west": {"pos": [36, 0, -52], "radius": 1},
    "smithy": {"pos": [-42, 0, -30], "radius": 1},
    "drafting": {"pos": [-30, 0, -30], "radius": 1},
    "great_bridge": {"pos": [0, 0, -9], "radius": 3},
    # The Forge Hall (north).
    "forge_hall": {"pos": [0, 0, -36], "radius": 4},
    "forge_door": {"pos": [5, 0, -28], "radius": 1},
    "garrick_forge": {"pos": [0, 0, -48], "radius": 1},
    "engineer_hall_west": {"pos": [-10, 0, -43], "radius": 1},
    "engineer_hall_east": {"pos": [10, 0, -38], "radius": 1},
    "workbench_front": {"pos": [-6, 0, -46], "radius": 2},
    "table_front": {"pos": [6, 0, -46], "radius": 2},
    "socket_middle": {"pos": [6, 1, -48], "radius": 1},
    # The Old Deeps.
    "deeps_entrance": {"pos": [-52, 0, -43], "radius": 3},
    "deeps_landing": {"pos": [-52, LAND, -17], "radius": 3},
    "engineer_landing": {"pos": [-49, LAND, -15], "radius": 1},
    "breach_side": {"pos": [-35, LAND, -19], "radius": 2},
    "hall_floor": {"pos": [-35, DEEP, -6], "radius": 3},
    "weights": {"pos": [-38, DEEP, 1], "radius": 3},
    "door_front": {"pos": [-26, DEEP, -3], "radius": 2},
    "works_inside": {"pos": [-17, DEEP, -3], "radius": 3},
    "works_door_glow": {"pos": [-22, -20, -3], "radius": 1},
}

npcs = [
    {"character": E, "location": "elara_start", "yaw": 180},
    {"character": B, "location": "bram_start", "yaw": 180, "held": "minecraft:iron_sword"},
    {"character": O, "location": "orin_gate", "yaw": 0},
    {"character": G, "location": "garrick_forge", "yaw": 0, "held": "minecraft:iron_pickaxe"},
    {"character": GUARD, "location": "guard_gate", "yaw": 0, "held": "minecraft:iron_axe"},
    {"character": GUARD2, "location": "forge_door", "yaw": 0, "held": "minecraft:iron_axe"},
    # Engineers: the lift, the Forge Hall and the drafting workshop (who goes down to the Old Deeps).
    {"character": ENG[0], "location": "lift_machine", "yaw": 90, "activity": "work", "held": "minecraft:iron_ingot"},
    {"character": ENG[1], "location": "engineer_hall_west", "yaw": 90, "activity": "work", "held": "minecraft:compass"},
    {"character": ENG[2], "location": "engineer_hall_east", "yaw": -90, "activity": "work", "held": "minecraft:copper_ingot"},
    {"character": ENG[3], "location": "drafting", "yaw": 0, "activity": "work", "held": "minecraft:paper", "until_stage": "to_deeps"},
    {"character": ENG[3], "location": "engineer_landing", "yaw": 90, "held": "minecraft:lantern", "from_stage": "to_deeps"},
    # Miners and smiths going about their day.
    {"character": MIN[0], "location": "mine_face_east", "yaw": 180, "activity": "work", "held": "minecraft:iron_pickaxe"},
    {"character": MIN[1], "location": "mine_face_west", "yaw": 180, "activity": "work", "held": "minecraft:stone_pickaxe"},
    {"character": MIN[2], "location": "mine_cart", "yaw": 0, "activity": "routine", "route": ["mine_cart", "depot"],
     "held": "minecraft:raw_iron"},
    {"character": MIN[3], "location": "residence", "yaw": -90, "activity": "routine", "route": ["residence", "market_steps", "plaza"]},
    {"character": MIN[4], "location": "ledge", "yaw": -90},
    {"character": MIN[5], "location": "market_stall", "yaw": 90, "activity": "work", "held": "minecraft:iron_pickaxe"},
    {"character": MIN[6], "location": "foundry", "yaw": 0, "activity": "work", "held": "minecraft:iron_ingot"},
    {"character": MIN[7], "location": "smithy", "yaw": 90, "activity": "work", "held": "minecraft:iron_axe"},
]

clues = {
    "s13_fragment": {"name": "Ancient mechanical fragment", "pos": [-6, 1, -48], "block": "minecraft:chiseled_deepslate"},
    # The three Builder counterweights, stood in the hall out of order: middle, smallest, largest.
    "s13_weight_middle": {"name": "Middle counterweight", "pos": [-43, -21, 4], "block": "minecraft:lodestone"},
    "s13_weight_small": {"name": "Smallest counterweight", "pos": [-38, -21, 4], "block": "minecraft:lodestone"},
    "s13_weight_large": {"name": "Largest counterweight", "pos": [-32, -21, 4], "block": "minecraft:lodestone"},
    "s13_plate": {"name": "Builder machine plate", "pos": [-16, -21, -3], "block": "minecraft:chiseled_deepslate"},
    "s13_gear_housing": {"name": "Builder gear housing", "pos": [-19, -21, 2], "block": "minecraft:heavy_core"},
}

# Reconstruction table sockets (y 1) above their emblem inlays: Stonecrown x 4, Eldervale x 6, Sylvaris x 8.
SOCKETS = ("minecraft:chiseled_deepslate|4,1,-48 + minecraft:chiseled_stone_bricks|6,1,-48 + "
           "minecraft:oxidized_chiseled_copper|8,1,-48")
# The broken top of the drainage channel's wall.
BREACH = "solid|-36,-20,-17;-35,-20,-17;-34,-20,-17"

FRAGMENTS = [
    give("minecraft:chiseled_stone_bricks", 1, "Eldervale fragment", "The carved stone from the royal vault of Eldervale."),
    give("minecraft:oxidized_chiseled_copper", 1, "Sylvaris fragment", "Liora's rubbing of the Sylvaris grove stone."),
    give("minecraft:chiseled_deepslate", 1, "Stonecrown fragment", "The piece Garrick's miners found beneath Irondeep."),
]

CITY = ["to_forge", "to_deeps"]

scene = {
    "id": "mc001_s013", "title": "The Stone of Irondeep", "universe": "MC-001", "saga": 1, "scene": 13,
    "canon_status": CANON + " The Old Deeps puzzle follows the screenplay's four engineering steps.",
    "dimension": DIM, "site": "irondeep", "anchor": [-160, 24, -170],
    "arrival": {"pos": [0, 0, 58], "yaw": 180},
    "next_scene": "unreleased:mc001_s014",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "arrive", "checkpoint": "lift_car",
         "on_enter": [{"type": "teleport_player", "location": "lift_car", "yaw": 180},
                      *stay(E, "elara_start"), *stay(B, "bram_start"), *follow(E, B),
                      cinematic("mc001_s013_arrival")],
         "complete": {"type": "cinematic_end", "value": "mc001_s013_arrival"},
         "on_complete": [dialogue("s13_gate")]},
        {"id": "gate_talk", "checkpoint": "lift_gate",
         "complete": {"type": "dialogue_end", "value": "s13_gate"},
         "on_complete": [*follow(E, B, O)]},
        {"id": "to_forge", "objective": "Meet Thane Garrick in the Forge Hall", "target": "forge_hall", "checkpoint": "lift_gate",
         "on_enter": [hint("The main avenue runs north from the lift, past the market terraces and over the Great Bridge. "
                           "The Forge Hall stands at the far end.", 220)],
         "complete": {"type": "reach", "value": "forge_hall"},
         "on_complete": [dialogue("s13_garrick")]},
        {"id": "garrick_talk", "checkpoint": "forge_hall",
         "complete": {"type": "dialogue_end", "value": "s13_garrick"}},
        {"id": "fragment", "objective": "Investigate the ancient mechanical fragment", "target": "workbench_front", "checkpoint": "forge_hall",
         "on_enter": [hint("The fragment lies on Garrick's workbench, west of the great forge. Press {use} on it.", 180)],
         "complete": {"type": "clues", "value": "s13_fragment"},
         "on_complete": [dialogue("s13_machine")]},
        {"id": "machine_talk", "checkpoint": "forge_hall",
         "complete": {"type": "dialogue_end", "value": "s13_machine"}},
        {"id": "reconstruct", "objective": "Reconstruct the carvings on the reconstruction table", "target": "table_front", "checkpoint": "forge_hall",
         "on_enter": [*FRAGMENTS,
                      hint("Set each fragment into the socket whose inlay matches its carving. A piece that doesn't match won't hold; "
                           "break it out and try another socket.", 240)],
         "complete": {"type": "blocks", "value": SOCKETS},
         "on_complete": [sound("minecraft:block.amethyst_block.resonate", 0.9, 0.7),
                         {"type": "particles", "particle": "minecraft:end_rod", "location": "socket_middle", "count": 16, "spread": 1.2},
                         dialogue("s13_sealed")]},
        {"id": "sealed_talk", "checkpoint": "forge_hall",
         "complete": {"type": "dialogue_end", "value": "s13_sealed"},
         "on_complete": [*follow(G, E, B, O)]},
        {"id": "to_deeps", "objective": "Follow the mine rail down to the Old Deeps", "target": "deeps_landing", "checkpoint": "forge_hall",
         "on_enter": [hint("The way down is in the north-west of the hold, past the workshops: follow the rail into the old incline.", 220)],
         "complete": {"type": "reach", "value": "deeps_landing"},
         "on_complete": [sound("minecraft:ambient.cave", 0.6, 0.8)]},
        {"id": "patch", "objective": "Patch the broken drainage channel wall", "target": "breach_side", "checkpoint": "deeps_landing",
         "on_enter": [give("minecraft:polished_deepslate", 6, "Dressed deepslate", "Stonecrown masonry for the channel wall."),
                      hint("The channel along the ledge has broken through into the works hall. Stand on the ledge and fill the gap "
                           "in its wall where the green sparkles are.", 240)],
         "complete": {"type": "blocks", "value": BREACH},
         "on_complete": [sound("minecraft:block.stone.place", 1.0, 0.8), cinematic("mc001_s013_drain")]},
        {"id": "drain_view", "checkpoint": "deeps_landing",
         "complete": {"type": "cinematic_end", "value": "mc001_s013_drain"},
         "on_complete": [change("mc001_s013_drain")]},
        {"id": "counterweights", "objective": "Reset the Builder counterweights in the order the wall carving shows", "target": "weights",
         "checkpoint": "hall_floor",
         "on_enter": [hint("Three counterweights stand along the south wall, and a carving beside them shows their order. "
                           "Press {use} on each weight's core in turn.", 240)],
         "complete": {"type": "sequence", "value": "s13_weight_large,s13_weight_middle,s13_weight_small"},
         "on_wrong": [sound("minecraft:block.anvil.land", 0.5, 0.6), {"type": "shake", "intensity": 0.02, "ticks": 8},
                      hint("The weights drop back into their cradles. The carving reads largest, then middle, then smallest.", 180)],
         "on_complete": [cinematic("mc001_s013_works_open")]},
        {"id": "door_open", "checkpoint": "hall_floor",
         "complete": {"type": "cinematic_end", "value": "mc001_s013_works_open"},
         "on_complete": [change("mc001_s013_works_open")]},
        {"id": "evidence", "objective": "Recover the evidence inside the sealed works", "target": "works_inside", "checkpoint": "door_front",
         "on_enter": [hint("Look over the machine: its carved plate and the gear housing beside it. Press {use} on each.", 200)],
         "complete": {"type": "clues", "value": "s13_plate,s13_gear_housing"},
         "on_complete": [dialogue("s13_older")]},
        {"id": "closing_talk", "checkpoint": "door_front",
         "complete": {"type": "dialogue_end", "value": "s13_older"},
         "on_complete": [
             give("minecraft:chiseled_deepslate", 1, "Stonecrown fragment", "The Builder machine plate from beneath Irondeep."),
             *complete("Mission Complete", "The Engraved Stone", "engraved_stone",
                       "Thane Garrick has pledged Stonecrown's engineers and miners. The machine plate is Stonecrown's piece "
                       "of the puzzle, and Sahir of Sunspire may know what its symbol means.",
                       ["mc001.stonecrown.joined", "mc001.s013.complete"])]},
    ],
    "triggers": [
        {"id": "s13_city_lift", "character": ENG[0], "dialogue": "s13_city_lift", "radius": 6, "talk_radius": 4,
         "approach": False, "stages": CITY, "ambient": True},
        {"id": "s13_city_rails", "character": MIN[2], "dialogue": "s13_city_rails", "radius": 7, "talk_radius": 4,
         "approach": False, "stages": CITY, "ambient": True},
        {"id": "s13_city_shafts", "character": MIN[5], "dialogue": "s13_city_shafts", "radius": 6, "talk_radius": 4,
         "approach": False, "stages": CITY, "ambient": True},
        {"id": "s13_city_bridges", "character": MIN[3], "dialogue": "s13_city_bridges", "radius": 6, "talk_radius": 4,
         "approach": False, "stages": CITY, "ambient": True},
        {"id": "s13_city_forge", "character": MIN[6], "dialogue": "s13_city_forge", "radius": 6, "talk_radius": 4,
         "approach": False, "stages": CITY, "ambient": True},
    ],
    "area_triggers": [], "chatter": [],
}

dialogues = {
    "s13_gate": lines(
        (GUARD, "A writ from Eldervale? The Thane's expecting you. Forge Hall, past the market terraces."),
        (O, "Took you long enough. I came by the mountain road with Firstbrook's records."),
        (S, "Orin! You came all this way?"),
        (O, "Somebody has to stop you touching the wrong thing.")),
    "s13_garrick": lines(
        (G, "I was told you found something worth dragging me away from my forge."),
        (S, "Queen Aveline sent us. She says your miners found something."),
        (G, "My miners discovered this beneath the oldest foundations of Irondeep.",
         [{"type": "npc_look", "character": G, "location": "workbench_front"}]),
        (O, "That carving..."), (S, "It's the same symbol."),
        (G, "Exactly. Which is why I want to know how you found another."),
        (G, "Our deepest halls are older than the current kingdom. There are tunnels nobody remembers building."),
        (S, "Could they connect to the northern monument?"),
        (G, "Not through any passage my miners know."),
        (O, "The markings are consistent, though. Same spacing. Same geometry."),
        (G, "Then somebody put considerable effort into making them identical.")),
    "s13_machine": lines(
        (O, "This isn't decoration. Look at the teeth on that edge. It's part of a machine."),
        (G, "Stonecrown makes the finest gears in the six kingdoms. We never made this.")),
    "s13_sealed": lines(
        (G, "That pattern resembles something I once saw in our oldest mine."), (S, "What was it?"),
        (G, "A chamber sealed long before my grandfather's time."), (E, "Can you open it?"),
        (G, "Perhaps. But I'd rather discover why it was sealed first."),
        (G, "The Old Deeps flooded generations ago when the drains failed. If you want that chamber, we drain it first."),
        (S, "Then we drain it.")),
    "s13_older": lines(
        (O, "These machines weren't built by Stonecrown."),
        (G, "No. My ancestors found them and swore to guard them. We built Irondeep around something older."),
        (S, "The same builders who left the monument."),
        (G, "Then Stonecrown stands with you. My engineers, my miners, whatever you need."),
        (G, "Solkar's scholars have asked about that symbol for years. Sahir of Sunspire would trade his library for a look at this plate.")),
    # Optional city lines (ambient): the first three are the screenplay's.
    "s13_city_rails": lines((MIN[2], "Mind the rails! Ore carts don't stop for visitors."), critical=False),
    "s13_city_shafts": lines((MIN[5], "The deep shafts have been groaning since the tremors."), critical=False),
    "s13_city_lift": lines((ENG[0], "Lift's running on the old counterweights again. Don't lean on the rail."), critical=False),
    # Added ambient lines (not in the screenplay; non-critical city chatter).
    "s13_city_bridges": lines((MIN[3], "Every bridge in the hold is checked at each shift change. Walk easy."), critical=False),
    "s13_city_forge": lines((MIN[6], "Keep clear of the forge mouths. The heat carries further than you think."), critical=False),
}

cinematics = {
    "mc001_s013_arrival": {
        "duration": 280, "fade_in": 20, "fade_out": 12, "hide_player": True,
        "keyframes": [
            key(0, [0.5, 14.0, 58.5], [0.5, 3, 30], 64),
            key(120, [16.0, 22.0, 34.0], [0, 4, -20], 66),
            key(200, [-12.0, 16.0, 6.0], [0, 6, -40], 60),
            key(280, [4.5, 5.0, 53.5], [0.5, 1.5, 48.5], 55)],
        "subtitles": [{"t": 40, "duration": 110, "text": "Irondeep — Hold of Stonecrown", "style": "title"}],
        "sounds": [{"t": 0, "sound": "minecraft:block.chain.place", "volume": 0.6, "pitch": 0.6},
                   {"t": 10, "sound": "minecraft:block.piston.contract", "volume": 0.5, "pitch": 0.5},
                   {"t": 70, "sound": "minecraft:block.anvil.use", "volume": 0.3, "pitch": 0.8},
                   {"t": 130, "sound": "minecraft:block.lava.ambient", "volume": 0.5},
                   {"t": 190, "sound": "minecraft:entity.minecart.riding", "volume": 0.25}],
        "cues": [{"t": 230, "actions": [{"type": "npc_look", "character": O, "location": "lift_car"},
                                        {"type": "npc_look", "character": GUARD, "location": "lift_car"}]}],
    },
    "mc001_s013_drain": {
        "duration": 150, "fade_in": 8, "fade_out": 8, "hide_player": False,
        "keyframes": [
            key(0, [-41.0, -16.0, -13.0], [-34, -21, -2], 62),
            key(150, [-40.0, -15.0, -11.0], [-30, -22, 0], 58)],
        "subtitles": [{"t": 15, "duration": 120, "text": "The channel holds. The flood runs away down the drain shaft.", "style": "caption"}],
        "sounds": [{"t": 10, "sound": "minecraft:block.water.ambient", "volume": 1.0, "pitch": 0.7},
                   {"t": 30, "sound": "minecraft:item.bucket.empty", "volume": 0.8, "pitch": 0.6},
                   {"t": 60, "sound": "minecraft:block.water.ambient", "volume": 0.6, "pitch": 0.5}],
        "cues": [{"t": 30, "actions": [change("mc001_s013_drain")]}],
    },
    "mc001_s013_works_open": {
        "duration": 220, "fade_in": 8, "fade_out": 10, "hide_player": False,
        "keyframes": [
            key(0, [-35.0, -19.0, -3.5], [-22, -19.5, -2.5], 60),
            key(220, [-29.0, -20.0, -3.0], [-18, -20, -2.5], 54)],
        "subtitles": [{"t": 20, "duration": 150, "text": "The three weights lock into place. Behind the wall, old gears begin to turn.",
                       "style": "caption"}],
        "sounds": [{"t": 15, "sound": "minecraft:block.chain.place", "volume": 0.8, "pitch": 0.5},
                   {"t": 50, "sound": "minecraft:block.piston.extend", "volume": 1.0, "pitch": 0.5},
                   {"t": 60, "sound": "minecraft:block.grindstone.use", "volume": 0.8, "pitch": 0.5},
                   {"t": 110, "sound": "minecraft:block.beacon.ambient", "volume": 0.6, "pitch": 0.7}],
        "cues": [{"t": 60, "actions": [change("mc001_s013_works_open"), {"type": "shake", "intensity": 0.04, "ticks": 30},
                                       {"type": "particles", "particle": "minecraft:cloud", "location": "works_door_glow",
                                        "count": 30, "spread": 2.0}]}],
    },
}

journal = {"engraved_stone": {"title": "The Engraved Stone", "text": (
    "The great lift set us down in Irondeep, Stonecrown's hold beneath the mountains. Orin was waiting at the gate with "
    "Firstbrook's records. Thane Garrick showed us what his miners found under the oldest foundations: a fragment with the "
    "same symbol, and teeth like part of a machine. Set beside the Eldervale stone and Liora's rubbing, the carvings matched, "
    "and Garrick remembered a chamber sealed long before his grandfather's time. We followed the old incline into the "
    "flooded Old Deeps, mended the drainage channel, and reset the counterweights in the order the carving showed: largest, "
    "middle, smallest. Behind the sealed door stood machines Stonecrown never built. Garrick's ancestors swore to guard "
    "them. Stonecrown stands with us now, and the machine plate is Stonecrown's piece of the puzzle.")}}

write_scene("mc001_s013", "mc001_s012", scene, dialogues, cinematics, journal)
