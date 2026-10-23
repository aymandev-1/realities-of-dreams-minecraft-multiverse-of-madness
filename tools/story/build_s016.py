"""Writes the Scene 016 bundle (rod-story/scenes/mc001_s016.json) from the revised screenplay.

"The Fragment from Tidewatch": spoken dialogue is verbatim from
docs/story/SAGA_1_SCENES_012_019_REVISED.md (Scene 016). Hints, captions, item names and the outcome
message are new gameplay text. The scene has its own site, Tidewatch, anchored at MC-001 block
(60, 65, 620); positions must match world/site/Tidewatch.java (y = 0 is the street, the harbour's
water surface is y = -2).

The party arrives over the headland, meets Captain Maren on the main pier, secures three damaged
cargo barrels, mends the loading crane, then dives along the buoy line to the half-sunk old watch,
clears the fallen stones in its dry lower room and takes the Tide Anchor. Back on the pier, the
anchor is laid on Maren's chart table beside the other five fragments. Dying or drowning during
the dive puts the player back on the pier.
Run from the repository root: python tools/story/build_s016.py
"""
import math

from common import *

DH = "tidewatch_dockhand_%d"
SAIL = "thalassian_sailor_%d"


def ground(x, z):
    """Standing height on the headland (Tidewatch.ground); Java's Math.round is floor(v + 0.5)."""
    if x < 50 or z > 34:
        return 0
    h = min(8, (x - 50) / 2.5, (91 - x) / 1.5, (z + 91) / 2.0)
    return max(0, int(math.floor(h + 0.5)))


def hl(x, z):
    return [x, ground(x, z), z]


SLOTS = [(-24, -12), (-22, -12), (-20, -12), (-24, -10), (-22, -10), (-20, -10)]
ANCHOR_SLOT = SLOTS[5]

locs = {
    # Headland and arrival.
    "headland_road": {"pos": hl(72, -44), "radius": 2},
    "elara_start": {"pos": hl(71, -46), "radius": 1},
    "orin_start": {"pos": hl(73, -46), "radius": 1},
    "sahir_start": {"pos": hl(70, -47), "radius": 1},
    "garrick_start": {"pos": hl(74, -47), "radius": 1},
    # The waterfront.
    "quay": {"pos": [0, 0, 6], "radius": 3},
    "maren_meet": {"pos": [0, 0, 11], "radius": 4},
    "maren_dock": {"pos": [0, 0, 14], "radius": 1},
    "cargo_area": {"pos": [6, 0, 9], "radius": 4},
    "crane_deck": {"pos": [14, 4, 9], "radius": 2},
    "pier": {"pos": [0, 0, 38], "radius": 2},
    "pier_end": {"pos": [0, 0, 41], "radius": 5},
    "maren_pier": {"pos": [0, 0, 40], "radius": 1},
    "pier_elara": {"pos": [-1, 0, 41], "radius": 1},
    "pier_orin": {"pos": [1, 0, 41], "radius": 1},
    "pier_sahir": {"pos": [-3, 0, 43], "radius": 1},
    "pier_garrick": {"pos": [3, 0, 43], "radius": 1},
    # The dive.
    "watch_door": {"pos": [29, -12, 54], "radius": 3},
    "watch_room": {"pos": [29, -12, 58], "radius": 2},
    "stones": {"pos": [29, -12, 59], "radius": 2},
    "housing_side": {"pos": [29, -12, 61], "radius": 1},
    # Maren's chart room.
    "chart_door": {"pos": [-22, 0, -2], "radius": 2},
    "chart_room": {"pos": [-21, 0, -8], "radius": 3},
    "maren_chart": {"pos": [-18, 0, -12], "radius": 1},
    # Townsfolk stations.
    "quay_ropes": {"pos": [4, 0, 8], "radius": 1},
    "quay_east": {"pos": [28, 0, 7], "radius": 1},
    "crane_hand": {"pos": [18, 0, 5], "radius": 1},
    "stall_cod": {"pos": [-11, 0, 5], "radius": 1},
    "stall_salmon": {"pos": [-6, 0, 5], "radius": 1},
    "gangplank": {"pos": [-1, 0, 29], "radius": 1},
    "deck_a": {"pos": [-8, 1, 30], "radius": 1},
    "bow_a": {"pos": [-9, 1, 36], "radius": 1},
    "deck_b": {"pos": [-46, 0, 20], "radius": 1},
    "loft": {"pos": [-56, 0, -8], "radius": 1},
    "loft_front": {"pos": [-56, 0, -1], "radius": 1},
    "quay_west": {"pos": [-30, 0, 6], "radius": 1},
    "pier2_head": {"pos": [-38, 0, 13], "radius": 1},
    "tavern_table": {"pos": [21, 0, -7], "radius": 1},
    "tavern_door": {"pos": [24, 0, -1], "radius": 1},
    "main_street": {"pos": [0, 0, -30], "radius": 1},
    "harbour_east": {"pos": [36, 0, 0], "radius": 1},
    "harbour_west": {"pos": [-40, 0, 0], "radius": 1},
}
# The fragments on the chart table glow, and a line of light runs north toward Awakening Valley.
for i, (x, z) in enumerate(SLOTS):
    locs["frag_%d" % (i + 1)] = {"pos": [x, 1, z], "radius": 1}
for i, (x, y, z) in enumerate([(-22, 2, -13), (-22, 3, -15), (-22, 4, -19), (-22, 5, -24), (-21, 6, -30), (-21, 7, -37), (-21, 8, -45)]):
    locs["light_%d" % (i + 1)] = {"pos": [x, y, z], "radius": 1}

DIVE = ["swim", "clear", "take", "swim_back"]

npcs = [
    {"character": E, "location": "elara_start", "yaw": 0},
    {"character": O, "location": "orin_start", "yaw": 0},
    {"character": SA, "location": "sahir_start", "yaw": 0},
    {"character": G, "location": "garrick_start", "yaw": 0, "held": "minecraft:iron_pickaxe"},
    {"character": M, "location": "maren_dock", "yaw": 180, "until_stage": "lay"},
    {"character": M, "location": "maren_chart", "yaw": 90, "from_stage": "lay"},
    {"character": DH % 1, "location": "crane_hand", "yaw": 90, "activity": "work", "held": "minecraft:chain"},
    {"character": DH % 2, "location": "quay_ropes", "yaw": 180, "activity": "routine", "route": ["quay_ropes", "quay_east", "cargo_area"]},
    {"character": DH % 3, "location": "loft", "yaw": 0, "activity": "work", "held": "minecraft:string"},
    {"character": DH % 4, "location": "quay_west", "activity": "routine", "route": ["quay_west", "loft_front", "pier2_head"]},
    {"character": DH % 5, "location": "stall_cod", "yaw": 180, "activity": "work", "held": "minecraft:cod"},
    {"character": DH % 6, "location": "stall_salmon", "yaw": 180, "activity": "work", "held": "minecraft:salmon"},
    {"character": DH % 7, "location": "tavern_table", "activity": "routine", "route": ["tavern_table", "tavern_door", "quay"]},
    {"character": DH % 8, "location": "main_street", "activity": "routine", "route": ["main_street", "harbour_east", "harbour_west"]},
    {"character": SAIL % 1, "location": "gangplank", "yaw": 90, "activity": "work", "held": "minecraft:lead"},
    {"character": SAIL % 2, "location": "deck_a", "yaw": 0, "activity": "work", "held": "minecraft:string"},
    {"character": SAIL % 3, "location": "deck_b", "yaw": 90, "activity": "work", "held": "minecraft:fishing_rod"},
    {"character": SAIL % 4, "location": "bow_a", "yaw": 0, "held": "minecraft:spyglass"},
]

clues = {
    "s16_barrel_1": {"name": "Damaged cargo barrel", "pos": [7, 0, 7], "block": "minecraft:barrel"},
    "s16_barrel_2": {"name": "Damaged cargo barrel", "pos": [1, 0, 22], "block": "minecraft:barrel"},
    "s16_barrel_3": {"name": "Damaged cargo barrel", "pos": [21, 0, 8], "block": "minecraft:barrel"},
    "s16_housing": {"name": "Tide Anchor housing", "pos": [29, -12, 62], "block": "minecraft:dark_prismarine"},
}

CRANE = "minecraft:spruce_log|14,7,9;14,7,10 + minecraft:chain|14,6,14;14,5,14"
STONES = "air|" + ";".join("%d,%d,60" % (x, y) for y in (-12, -11) for x in (28, 29, 30))
LAY = "minecraft:dark_prismarine|%d,1,%d" % ANCHOR_SLOT
CREW = [E, O, SA, G]
AMBIENT_STAGES = ["to_docks", "cargo", "crane"]

scene = {
    "id": "mc001_s016", "title": "The Fragment from Tidewatch", "universe": "MC-001", "saga": 1, "scene": 16,
    "canon_status": ("Saga 1 revised screenplay, Version 2 (docs/story/SAGA_1_SCENES_012_019_REVISED.md): spoken dialogue "
                     "verbatim. Hints, captions, item names and the outcome message are new gameplay text. The harbour work and "
                     "the dive follow the scene's listed objectives."),
    "dimension": DIM, "site": "tidewatch", "anchor": [60, 65, 620],
    "arrival": {"pos": hl(72, -44), "yaw": 0},
    "next_scene": "unreleased:mc001_s017",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "arrive", "checkpoint": "headland_road",
         "on_enter": [{"type": "teleport_player", "location": "headland_road", "yaw": 0},
                      *stay(E, "elara_start"), *stay(O, "orin_start"), *stay(SA, "sahir_start"), *stay(G, "garrick_start"),
                      *follow(*CREW), cinematic("mc001_s016_arrival")],
         "complete": {"type": "cinematic_end", "value": "mc001_s016_arrival"}},
        {"id": "to_docks", "objective": "Meet Captain Maren on the docks", "target": "maren_meet", "checkpoint": "headland_road",
         "on_enter": [hint("Follow the road down off the headland into Tidewatch. Captain Maren is waiting at the head of the main pier.", 200)],
         "complete": {"type": "reach", "value": "maren_meet"},
         "on_complete": [dialogue("s16_maren")]},
        {"id": "maren_talk", "checkpoint": "quay",
         "complete": {"type": "dialogue_end", "value": "s16_maren"}},
        {"id": "cargo", "objective": "Secure the three damaged cargo barrels", "target": "cargo_area", "checkpoint": "quay",
         "on_enter": [hint("The tremors knocked cargo loose along the quay and the pier. Find the three split barrels and press {use} on each to lash it down.", 220)],
         "complete": {"type": "clues", "value": "s16_barrel_1,s16_barrel_2,s16_barrel_3"},
         "on_complete": [sound("minecraft:block.wood.place", 0.8, 0.9)]},
        {"id": "crane", "objective": "Repair the loading crane", "target": "crane_deck", "checkpoint": "quay",
         "on_enter": [give("minecraft:spruce_log", 4, "Crane beam timber"), give("minecraft:chain", 3, "Crane chain"),
                      hint("Climb the ladder on the crane's north side to its deck. Set two logs into the gap in the jib, then hang two chain links under its end, where the green sparkles are.", 260)],
         "complete": {"type": "blocks", "value": CRANE},
         "on_complete": [change("mc001_s016_crane_fixed"), sound("minecraft:block.chain.place", 1.0, 0.8),
                         {"type": "message", "text": "The crane swings again. The dockhands cheer and start loading."},
                         dialogue("s16_caravan")]},
        {"id": "caravan_talk", "checkpoint": "quay",
         "complete": {"type": "dialogue_end", "value": "s16_caravan"},
         "on_complete": [dialogue("s16_dive")]},
        {"id": "dive_talk", "checkpoint": "quay",
         "complete": {"type": "dialogue_end", "value": "s16_dive"}},
        {"id": "swim", "objective": "Follow the buoys out to the sunken watch", "target": "watch_door", "checkpoint": "pier",
         "on_enter": [*stay(M, "maren_pier"), *stay(E, "pier_elara"), *stay(O, "pier_orin"), *stay(SA, "pier_sahir"), *stay(G, "pier_garrick"),
                      give("minecraft:turtle_helmet", 1, "Tidewatch diving helmet", "Lent by the Tidewatch divers.|A few more breaths under water."),
                      hint("Wear the diving helmet ({inventory}). Climb down the ladder at the pier's end and swim out along the buoys. "
                           "The door of the old watch is on the seabed below the last buoy; the bubble columns beside it give you air.", 300)],
         "complete": {"type": "reach", "value": "watch_room"},
         "on_complete": [sound("minecraft:block.wooden_door.close", 0.8, 0.8)]},
        {"id": "clear", "objective": "Clear the fallen stones", "target": "stones", "checkpoint": "pier",
         "on_enter": [give("minecraft:iron_pickaxe", 1, "Diver's pick", "Tidewatch diving gear."),
                      hint("The old door still keeps the sea out of this room. Break the fallen stones in the archway with {attack}.", 200)],
         "complete": {"type": "blocks", "value": STONES},
         "on_complete": [sound("minecraft:block.stone.break", 1.0, 0.7)]},
        {"id": "take", "objective": "Take the Tide Anchor from its housing", "target": "housing_side", "checkpoint": "pier",
         "on_enter": [hint("Press {use} on the dark housing in the alcove.", 160)],
         "complete": {"type": "clues", "value": "s16_housing"},
         "on_complete": [change("mc001_s016_anchor_taken"), sound("minecraft:block.conduit.deactivate", 0.8, 0.9),
                         {"type": "shake", "intensity": 0.03, "ticks": 12},
                         {"type": "message", "text": "You lift the Tide Anchor out of its housing. It is heavier than it looks."}]},
        {"id": "swim_back", "objective": "Swim back to the pier", "target": "pier_end", "checkpoint": "pier",
         "on_enter": [hint("Out through the door and back along the buoys. The ladder at the pier's end takes you up.", 180)],
         "complete": {"type": "reach", "value": "pier_end"},
         "on_complete": [dialogue("s16_anchor")]},
        {"id": "anchor_talk", "checkpoint": "pier",
         "complete": {"type": "dialogue_end", "value": "s16_anchor"}},
        {"id": "lay", "objective": "Lay the Tide Anchor beside the other fragments", "target": "chart_room", "checkpoint": "chart_door",
         "on_enter": [*stay(M, "maren_chart"), *follow(*CREW), change("mc001_s016_fragments"),
                      give("minecraft:dark_prismarine", 1, "Tide Anchor", "Raised from the old coastal watch.|Lay it on Maren's chart table beside the other fragments."),
                      hint("Maren's chart room is on Harbour Street, west of the main pier. The other five fragments are laid out on her chart table: place the Tide Anchor in the last space.", 240)],
         "complete": {"type": "blocks", "value": LAY},
         "on_complete": [cinematic("mc001_s016_glow")]},
        {"id": "glow", "checkpoint": "chart_door",
         "complete": {"type": "cinematic_end", "value": "mc001_s016_glow"},
         "on_complete": [dialogue("s16_new")]},
        {"id": "new_talk", "checkpoint": "chart_door",
         "complete": {"type": "dialogue_end", "value": "s16_new"},
         "on_complete": [
             give("minecraft:dark_prismarine", 1, "Thalassia fragment", "The Tide Anchor of Tidewatch. Made to hold something steady."),
             *complete("Mission Complete", "The Sixth Fragment", "s16_sixth_fragment",
                       "Thalassia joins. All six fragments are accounted for, and Captain Maren will carry everyone to Valebridge.",
                       ["mc001.thalassia.joined", "mc001.s016.complete"])]},
    ],
    "triggers": [
        {"id": "s16_amb_ropes", "character": DH % 2, "dialogue": "s16_ropes", "radius": 7, "talk_radius": 5, "approach": False,
         "stages": AMBIENT_STAGES, "ambient": True},
        {"id": "s16_amb_tide", "character": SAIL % 1, "dialogue": "s16_tide", "radius": 7, "talk_radius": 5, "approach": False,
         "stages": AMBIENT_STAGES, "ambient": True},
        {"id": "s16_amb_catch", "character": DH % 5, "dialogue": "s16_catch", "radius": 7, "talk_radius": 5, "approach": False,
         "stages": AMBIENT_STAGES, "ambient": True},
        {"id": "s16_amb_nets", "character": DH % 3, "dialogue": "s16_nets", "radius": 6, "talk_radius": 4, "approach": False,
         "stages": AMBIENT_STAGES, "ambient": True},
        {"id": "s16_amb_waiting", "character": SAIL % 3, "dialogue": "s16_waiting", "radius": 6, "talk_radius": 4, "approach": False,
         "stages": AMBIENT_STAGES, "ambient": True},
        {"id": "s16_amb_watch", "character": DH % 7, "dialogue": "s16_old_watch", "radius": 6, "talk_radius": 4, "approach": False,
         "stages": AMBIENT_STAGES, "ambient": True},
    ],
    "area_triggers": [
        {"id": "s16_ladder_hint", "location": "pier_end", "stages": ["swim"],
         "actions": [hint("The ladder goes down into the sea at the very end of the pier.", 120)]},
        {"id": "s16_door_hint", "location": "watch_door", "stages": ["swim"],
         "actions": [hint("Open the old door with {use} and swim through. The room behind it is dry.", 140)]},
    ],
    "chatter": [],
}

dialogues = {
    "s16_maren": lines(
        (M, "I'd have met you on the road, but the sea wasn't cooperative."),
        (O, "Have you got the fragment?"),
        (M, "Found it. Then the tremors brought half the old watch down on top of it."),
        (M, "We found it beneath an old coastal watch structure."),
        (SA, "Then the same builders may have worked across all six territories.")),
    "s16_caravan": lines(
        (M, "I heard you helped a caravan outside Eldervale."),
        (S, "We were in the right place."),
        (M, "The right place is usually where someone chooses to stand."),
        (S, "You sound like Rowan."),
        (M, "Then Rowan sounds sensible.")),
    "s16_dive": lines(
        (M, "Follow the buoys out and keep to the bubble lines. Tidewatch divers have breathed off them for three hundred years.")),
    "s16_anchor": lines(
        (M, "The Tide Anchor. My grandfather swore it kept this coast from shaking."),
        (O, "Maybe it did. Look how it's made. It's meant to hold something steady.")),
    "s16_new": lines(
        (M, "Well. That's new."),
        (O, "I think we've just found our next destination."),
        (M, "I'll carry everyone to Valebridge myself. Every kingdom should be in one room for this.")),
    # Optional harbour chatter (the first three are the screenplay's; the last three are added ambient lines).
    "s16_ropes": lines((DH % 2, "Watch your feet! Ropes everywhere this morning."), critical=False),
    "s16_tide": lines((SAIL % 1, "Tide's turning. If the Captain wants the divers out, it'll be soon."), critical=False),
    "s16_catch": lines((DH % 5, "Fresh catch! Cod, salmon, and one very angry crab."), critical=False),
    "s16_nets": lines((DH % 3, "Mending nets all morning. The tremors shook half of them off the hooks."), critical=False),
    "s16_waiting": lines((SAIL % 3, "We sail when the Captain says. Not a moment before."), critical=False),
    "s16_old_watch": lines((DH % 7, "Heard the old watch go in the night. Sounded like the sea itself coughing."), critical=False),
}

cinematics = {
    "mc001_s016_arrival": {
        "duration": 320, "fade_in": 20, "fade_out": 12, "hide_player": True,
        "keyframes": [
            key(0, [72.0, 14.0, -30.0], [72, 26, 26], 62),
            key(120, [56.0, 20.0, -10.0], [0, 1, 22], 60),
            key(230, [24.0, 16.0, 20.0], [29, -1, 60], 56),
            key(320, [14.0, 12.0, 30.0], [29, -2, 60], 50)],
        "subtitles": [{"t": 30, "duration": 110, "text": "Tidewatch — Harbour of Thalassia", "style": "title"},
                      {"t": 200, "duration": 100, "text": "Offshore, the old coastal watch stands half sunk in the sea.", "style": "caption"}],
        "sounds": [{"t": 0, "sound": "minecraft:entity.fishing_bobber.splash", "volume": 0.4},
                   {"t": 60, "sound": "minecraft:block.bell.use", "volume": 0.5, "pitch": 0.8},
                   {"t": 200, "sound": "minecraft:entity.dolphin.ambient_water", "volume": 0.4}],
    },
    "mc001_s016_glow": {
        "duration": 240, "fade_in": 8, "fade_out": 10, "hide_player": False,
        "keyframes": [
            key(0, [-17.5, 3.0, -7.0], [-22, 1, -11], 58),
            key(120, [-18.5, 2.6, -8.0], [-22, 1, -11], 48),
            key(240, [-22.0, 3.2, -6.5], [-22, 5, -24], 55)],
        "subtitles": [{"t": 20, "duration": 80, "text": "The six fragments glow together.", "style": "caption"},
                      {"t": 100, "duration": 70, "text": "The compass needle stops.", "style": "caption"},
                      {"t": 170, "duration": 70, "text": "A line of light points north, toward Awakening Valley.", "style": "caption"}],
        "sounds": [{"t": 15, "sound": "minecraft:block.amethyst_block.chime", "volume": 0.9, "pitch": 0.7},
                   {"t": 100, "sound": "minecraft:block.beacon.power_select", "volume": 0.6, "pitch": 1.2},
                   {"t": 165, "sound": "minecraft:block.beacon.activate", "volume": 0.7, "pitch": 0.8}],
        "cues": [
            {"t": 20, "actions": [{"type": "particles", "particle": "minecraft:end_rod", "location": "frag_%d" % (i + 1), "count": 10, "spread": 0.3}
                                  for i in range(6)]},
            {"t": 60, "actions": [{"type": "particles", "particle": "minecraft:glow", "location": "frag_%d" % (i + 1), "count": 8, "spread": 0.4}
                                  for i in range(6)]},
            {"t": 165, "actions": [{"type": "particles", "particle": "minecraft:end_rod", "location": "light_%d" % (i + 1), "count": 14, "spread": 0.25}
                                   for i in range(7)]},
            {"t": 200, "actions": [{"type": "particles", "particle": "minecraft:end_rod", "location": "light_%d" % (i + 1), "count": 10, "spread": 0.2}
                                   for i in range(7)]},
        ],
    },
}

journal = {"s16_sixth_fragment": {"title": "The Sixth Fragment", "text": (
    "Tidewatch, the harbour of Thalassia. Captain Maren's divers had found Thalassia's fragment beneath the old coastal watch, "
    "and then the tremors brought half the watch down on top of it. We helped on the docks first: three split cargo barrels "
    "lashed down and the loading crane mended. Then I swam out along the buoys, breathing at the bubble lines, cleared the fallen "
    "stones in the watch's lower room and lifted out the Tide Anchor. Orin says it was made to hold something steady. On Maren's "
    "chart table the six fragments glowed together, the compass needle stopped, and a line of light pointed north toward Awakening "
    "Valley. Thalassia joins us, and Maren will carry everyone to Valebridge.")}}

write_scene("mc001_s016", "mc001_s015", scene, dialogues, cinematics, journal)
