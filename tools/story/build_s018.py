"""Writes the Scene 018 bundle (rod-story/scenes/mc001_s018.json): "Beneath the Ancient Stone".

Dialogue is verbatim from docs/story/SAGA_1_SCENES_012_019_REVISED.md (Version 2). Hints, captions,
objectives, item names and the journal are gameplay text. Set at the excavation beneath the Ancient
Northern Monument, anchored at MC-001 block (40, 108, -200) with y = 0 the courtyard floor; every
position must match world/site/GatewayChamber.java (the stair: step k stands at (x, -k, 12 - k); the
machine hall at y -40; the lock stones on z -32; the door in the plane z -35; the chamber beyond).
Run from the repository root: python tools/story/build_s018.py
"""
from common import *

CAMP_WORKERS = ["expedition_worker_1", "expedition_worker_2", "expedition_worker_3", "expedition_worker_4"]
RESEARCHERS = ["expedition_researcher_1", "expedition_researcher_2", "expedition_researcher_3"]


def step(k, x=20):
    """Where the player stands on stair step k."""
    return [x, -k, 12 - k]


locs = {
    # The camp in the courtyard.
    "camp": {"pos": [14, 0, 8], "radius": 2},
    "elara_camp": {"pos": [16, 0, 6], "radius": 1}, "bram_camp": {"pos": [12, 0, 6], "radius": 1},
    "orin_camp": {"pos": [16, 0, 12], "radius": 1}, "garrick_camp": {"pos": [22, 0, 9], "radius": 1},
    "sahir_camp": {"pos": [13, 0, 11], "radius": 1}, "liora_camp": {"pos": [10, 0, 13], "radius": 1},
    "kaelen_camp": {"pos": [26, 0, 4], "radius": 1}, "maren_camp": {"pos": [24, 0, 4], "radius": 1},
    "kaelen_guard": {"pos": [27, 0, 6], "radius": 1}, "kaelen_guard_2": {"pos": [27, 0, 2], "radius": 1},
    "w1a": {"pos": [18, 0, 16], "radius": 1}, "w1b": {"pos": [22, 0, 11], "radius": 1},
    "w2": {"pos": [23, 0, 13], "radius": 1}, "w3a": {"pos": [10, 0, 4], "radius": 1}, "w3b": {"pos": [20, 0, 4], "radius": 1},
    "w4": {"pos": [11, 0, 15], "radius": 1},
    "eng1": {"pos": [23, 0, 14], "radius": 1}, "eng2": {"pos": [16, 0, 14], "radius": 1},
    "ranger_a": {"pos": [4, 0, 18], "radius": 1}, "ranger_b": {"pos": [-6, 0, 20], "radius": 1},
    "sailor_a": {"pos": [6, 0, 10], "radius": 1}, "sailor_b": {"pos": [6, 0, 2], "radius": 1},
    "res1": {"pos": [11, 0, 9], "radius": 1}, "res2": {"pos": [12, 0, 10], "radius": 1}, "res3": {"pos": [18, 0, 9], "radius": 1},
    # The stair.
    "dig_top": {"pos": [20, 0, 14], "radius": 2},
    "upper_stair": {"pos": step(9), "radius": 2},
    "gap_edge": {"pos": step(25), "radius": 2},
    "team_gather": {"pos": step(22), "radius": 1},
    "lower_stair": {"pos": step(33), "radius": 2},
    # The machine hall.
    "hall_entry": {"pos": [20, -39, -29], "radius": 2},
    "hall_centre": {"pos": [20, -39, -30], "radius": 5},
    "elara_hall": {"pos": [18, -39, -29], "radius": 1}, "bram_hall": {"pos": [22, -39, -29], "radius": 1},
    "orin_hall": {"pos": [12, -39, -31], "radius": 1}, "garrick_hall": {"pos": [26, -39, -30], "radius": 1},
    "sahir_hall": {"pos": [14, -39, -29], "radius": 1}, "liora_hall": {"pos": [28, -39, -29], "radius": 1},
    "kaelen_hall": {"pos": [30, -39, -33], "radius": 1},
    "res1_hall": {"pos": [10, -39, -33], "radius": 1}, "res2_hall": {"pos": [31, -39, -28], "radius": 1},
    "res3_hall": {"pos": [16, -39, -33], "radius": 1},
    "door_front": {"pos": [18, -39, -34], "radius": 2},
    # The chamber.
    "landing": {"pos": [20, -39, -37], "radius": 2},
    "dais": {"pos": [20, -39, -48], "radius": 3},
    "elara_land": {"pos": [19, -39, -38], "radius": 1}, "bram_land": {"pos": [23, -39, -36], "radius": 1},
    "orin_land": {"pos": [14, -39, -38], "radius": 1}, "sahir_land": {"pos": [25, -39, -37], "radius": 1},
    "garrick_land": {"pos": [27, -39, -38], "radius": 1}, "liora_land": {"pos": [11, -39, -37], "radius": 1},
    "kaelen_land": {"pos": [29, -39, -37], "radius": 1},
    "pedestal": {"pos": [17, -39, -37], "radius": 1},
    "gateway": {"pos": [20, -31, -52], "radius": 2},
}

npcs = [
    {"character": M, "location": "maren_camp", "yaw": 180},
    {"character": "veyrath_guard_1", "location": "kaelen_guard", "yaw": 180, "held": "minecraft:iron_sword"},
    {"character": "veyrath_guard_2", "location": "kaelen_guard_2", "yaw": 180, "held": "minecraft:iron_sword"},
    {"character": "expedition_worker_1", "location": "w1a", "activity": "routine", "route": ["w1a", "w1b"], "held": "minecraft:barrel"},
    {"character": "expedition_worker_2", "location": "w2", "activity": "work", "yaw": 0},
    {"character": "expedition_worker_3", "location": "w3a", "activity": "routine", "route": ["w3a", "w3b"]},
    {"character": "expedition_worker_4", "location": "w4", "activity": "work", "yaw": 90},
    {"character": "stonecrown_engineer_2", "location": "eng2", "activity": "work", "yaw": 180, "held": "minecraft:iron_pickaxe"},
    {"character": "stonecrown_engineer_1", "location": "eng1", "activity": "work", "yaw": 180, "until_stage": "conduits"},
    {"character": "stonecrown_engineer_1", "location": "eng1", "from_stage": "conduits", "activity": "work", "yaw": 180},
    {"character": "sylvaris_ranger_1", "location": "ranger_a", "activity": "patrol", "route": ["ranger_a", "ranger_b"], "held": "minecraft:bow"},
    {"character": "thalassian_sailor_1", "location": "sailor_a", "activity": "routine", "route": ["sailor_a", "sailor_b"]},
    *[{"character": r, "location": f"res{i + 1}", "yaw": 90, "until_stage": "escort"} for i, r in enumerate(RESEARCHERS)],
]

LOCKS = [("eldervale", 17, "minecraft:chiseled_stone_bricks", "The Eldervale lock stone"),
         ("sylvaris", 21, "minecraft:oxidized_chiseled_copper", "The Sylvaris lock stone"),
         ("stonecrown", 25, "minecraft:chiseled_deepslate", "The Stonecrown lock stone"),
         ("solkar", 15, "minecraft:chiseled_sandstone", "The Solkar lock stone"),
         ("veyrath", 23, "minecraft:chiseled_nether_bricks", "The Veyrath lock stone"),
         ("thalassia", 19, "minecraft:dark_prismarine", "The Thalassia lock stone")]

clues = {
    "s18_clock": {"name": "A clock that does not move", "pos": [10, -39, -29], "block": "minecraft:chiseled_copper", "dialogue": "s18_clock"},
    "s18_twins": {"name": "Twin circles carved side by side", "pos": [6, -37, -31], "block": "minecraft:chiseled_tuff", "dialogue": "s18_twins"},
    "s18_warning": {"name": "A warning carving", "pos": [34, -37, -31], "block": "minecraft:chiseled_deepslate", "dialogue": "s18_warning"},
    "s18_door_carving": {"name": "The six emblems on the door", "pos": [20, -37, -35], "block": "minecraft:chiseled_polished_blackstone", "dialogue": "s18_carving"},
    "s18_plinth": {"name": "The gateway's western plinth", "pos": [17, -39, -51], "block": "minecraft:polished_deepslate", "dialogue": "s18_plinth"},
    "s18_bridge_grooves": {"name": "Grooves worn into the bridge", "pos": [20, -40, -42], "block": "minecraft:polished_deepslate", "dialogue": "s18_grooves"},
    "s18_pedestal": {"name": "The compass pedestal", "pos": [17, -39, -37], "block": "minecraft:chiseled_polished_blackstone"},
}
for civ, x, block, name in LOCKS:
    clues[f"s18_lock_{civ}"] = {"name": name, "pos": [x, -39, -32], "block": block}

SUPPORTS = [[x, -13, -4] for x in (19, 20, 21)] + [[x, -21, -12] for x in (19, 20, 21)]
LIGHTS = [[21, -5, 6], [21, -11, 0], [21, -19, -8]]
RUBBLE = [[20, -10, 2], [21, -11, 1], [19, -12, 0]]
STEPS = [[x, -1 - k, 12 - k] for k in (27, 28, 29) for x in (19, 20, 21)]
SOCKETS = [[8, -38, -30], [32, -38, -30], [20, -38, -33]]


def spots(mode, ps):
    return mode + "|" + ";".join(f"{p[0]},{p[1]},{p[2]}" for p in ps)


CAST_HALL = [(O, "orin_hall"), (G, "garrick_hall"), (SA, "sahir_hall"), (L, "liora_hall"), (K, "kaelen_hall"),
             (E, "elara_hall"), (B, "bram_hall"),
             *[(r, f"res{i + 1}_hall") for i, r in enumerate(RESEARCHERS)]]
CAST_LANDING = [(O, "orin_land"), (G, "garrick_land"), (SA, "sahir_land"), (L, "liora_land"), (K, "kaelen_land"),
                (E, "elara_land"), (B, "bram_land")]

scene = {
    "id": "mc001_s018", "title": "Beneath the Ancient Stone", "universe": "MC-001", "saga": 1, "scene": 18,
    "canon_status": ("Saga 1 revised screenplay, Version 2 (docs/story/SAGA_1_SCENES_012_019_REVISED.md): spoken "
                     "dialogue verbatim. Hints, captions, item names and the outcome message are gameplay text."),
    "dimension": DIM, "site": "gateway_chamber", "anchor": [40, 108, -200],
    "arrival": {"pos": [14, 0, 8], "yaw": -90},
    "next_scene": "mc001_s019",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "dawn", "checkpoint": "camp",
         "on_enter": [{"type": "teleport_player", "location": "camp", "yaw": -90},
                      *stay(E, "elara_camp"), *stay(B, "bram_camp"), *stay(O, "orin_camp"), *stay(G, "garrick_camp"),
                      *stay(SA, "sahir_camp"), *stay(L, "liora_camp"), *stay(K, "kaelen_camp"),
                      cinematic("mc001_s018_camp")],
         "complete": {"type": "cinematic_end", "value": "mc001_s018_camp"},
         "on_complete": [dialogue("s18_camp")]},
        {"id": "camp_talk", "checkpoint": "camp",
         "complete": {"type": "dialogue_end", "value": "s18_camp"}},
        {"id": "secure", "objective": "Secure the excavation: repair both broken supports, light the stair and clear the loose rubble",
         "target": "dig_top", "checkpoint": "dig_top",
         "on_enter": [give("minecraft:spruce_planks", 24, "Expedition timber", "Garrick's engineers cut it to fit the old supports."),
                      give("minecraft:torch", 8),
                      give("minecraft:iron_pickaxe", 1, "Garrick's pick", "Stonecrown steel. Mind the carvings."),
                      *follow(E, B),
                      hint("Green sparkles mark the work. Fit timber where the two support beams fell, set torches on the "
                           "marked spots along the east wall, and break the loose rubble on the steps. Leave the carved "
                           "stones in the wall alone: they are evidence.", 300)],
         "complete": {"type": "blocks", "value": " + ".join([spots("solid", SUPPORTS), spots("light", LIGHTS), spots("air", RUBBLE)])},
         "on_complete": [sound("minecraft:block.wood.place", 0.8, 0.8),
                         {"type": "say", "character": G, "text": "The supports will hold. Mind the steps further down."}]},
        {"id": "gap", "objective": "Rebuild the collapsed steps", "target": "gap_edge", "checkpoint": "upper_stair",
         "on_enter": [hint("Three steps have fallen away below the second support. Fill all nine marked spots with timber "
                           "so the research team can get down.", 220)],
         "complete": {"type": "blocks", "value": spots("solid", STEPS)},
         "on_complete": [sound("minecraft:block.wood.place", 0.8, 0.9)]},
        {"id": "escort", "objective": "Lead the research team down to the bottom of the stair", "target": "hall_entry",
         "checkpoint": "gap_edge",
         "on_enter": [*[a for r in RESEARCHERS for a in stay(r, "team_gather")], *stay(O, "team_gather"),
                      *follow(O, *RESEARCHERS),
                      hint("Orin and the first research team have come down behind you. Lead them to the foot of the stair.", 200)],
         "complete": {"type": "reach", "value": "hall_entry"},
         "on_complete": [*[a for who, where in CAST_HALL for a in stay(who, where)]]},
        {"id": "conduits", "objective": "Fit the three copper hearts into the empty conduit sockets", "target": "hall_centre",
         "checkpoint": "hall_entry",
         "on_enter": [give("minecraft:copper_block", 3, "Conduit heart", "Copper cut by Garrick's engineers to the Builders' measure."),
                      hint("The copper channels in the floor run to three empty sockets on pedestals: one at each end of the "
                           "hall and one before the door. Place a conduit heart in each.", 240)],
         "complete": {"type": "blocks", "value": spots("minecraft:copper_block", SOCKETS)},
         "on_complete": [change("mc001_s018_conduits"), sound("minecraft:block.beacon.activate", 0.9, 0.6),
                         {"type": "shake", "intensity": 0.05, "ticks": 30}, dialogue("s18_conduits")]},
        {"id": "conduits_talk", "checkpoint": "hall_entry",
         "complete": {"type": "dialogue_end", "value": "s18_conduits"}},
        {"id": "clocks", "objective": "Investigate the three Builder devices in the Hall of Still Clocks", "target": "hall_centre",
         "checkpoint": "hall_entry",
         "on_enter": [hint("Look for a clock that does not move, and the carvings in the hall's two end walls.", 200)],
         "complete": {"type": "clues", "value": "s18_clock,s18_twins,s18_warning"},
         "on_complete": [dialogue("s18_symbols")]},
        {"id": "symbols_talk", "checkpoint": "hall_entry",
         "complete": {"type": "dialogue_end", "value": "s18_symbols"}},
        {"id": "carving", "objective": "Read the carving on the sealed door", "target": "door_front", "checkpoint": "hall_entry",
         "on_enter": [hint("The six emblems are carved in a ring at the centre of the great door.", 160)],
         "complete": {"type": "clues", "value": "s18_door_carving"}},
        {"id": "locks", "objective": "Press the six lock stones in the order the door shows", "target": "door_front",
         "checkpoint": "hall_entry",
         "on_enter": [hint("Start with Eldervale's stone and go sunwise round the door: Eldervale, Sylvaris, Stonecrown, "
                           "Solkar, Veyrath, Thalassia. Each lock stone is the same stone as that kingdom's fragment.", 280)],
         "complete": {"type": "sequence", "value": ",".join(f"s18_lock_{c}" for c in
                                                            ["eldervale", "sylvaris", "stonecrown", "solkar", "veyrath", "thalassia"])},
         "on_wrong": [hint("The stones grind back into place. Begin again with Eldervale's.", 160),
                      sound("minecraft:block.grindstone.use", 0.7, 0.6)],
         "on_complete": [cinematic("mc001_s018_door")]},
        {"id": "door", "checkpoint": "hall_entry",
         "complete": {"type": "cinematic_end", "value": "mc001_s018_door"},
         "on_complete": [dialogue("s18_door")]},
        {"id": "door_talk", "checkpoint": "hall_entry",
         "complete": {"type": "dialogue_end", "value": "s18_door"},
         "on_complete": [*[{"type": "npc_move", "character": who, "location": where} for who, where in CAST_LANDING]]},
        {"id": "enter", "objective": "Go through the door", "target": "landing", "checkpoint": "hall_entry",
         "complete": {"type": "reach", "value": "landing"},
         "on_complete": [dialogue("s18_chamber")]},
        {"id": "chamber_talk", "checkpoint": "landing",
         "complete": {"type": "dialogue_end", "value": "s18_chamber"}},
        {"id": "survey", "objective": "Survey the gateway's supports and the old bridge", "target": "dais", "checkpoint": "landing",
         "on_enter": [hint("Cross the bridge to the dais. Examine the grooves in the bridge and the plinth under the "
                           "gateway's west side.", 200)],
         "complete": {"type": "clues", "value": "s18_bridge_grooves,s18_plinth"},
         "on_complete": [dialogue("s18_survive")]},
        {"id": "survive_talk", "checkpoint": "landing",
         "complete": {"type": "dialogue_end", "value": "s18_survive"}},
        {"id": "pedestal", "objective": "Examine the pedestal on the landing", "target": "pedestal", "checkpoint": "landing",
         "on_enter": [hint("Back on the landing, a pedestal like the one in the courtyard above stands west of the door.", 180),
                      {"type": "hold_item", "item": "minecraft:compass"}],
         "complete": {"type": "clues", "value": "s18_pedestal"},
         "on_complete": [dialogue("s18_compass")]},
        {"id": "compass_talk", "checkpoint": "landing",
         "complete": {"type": "dialogue_end", "value": "s18_compass"},
         "on_complete": [cinematic("mc001_s018_end")]},
        {"id": "end", "checkpoint": "landing",
         "complete": {"type": "cinematic_end", "value": "mc001_s018_end"},
         "on_complete": complete("Quest Complete", "The Sealed Chamber", "s18_journal",
                                 "The door beneath the monument is open.",
                                 ["mc001.gateway.chamber_opened", "mc001.s018.complete"])},
    ],
    "triggers": [
        {"id": "s18_amb_rope", "character": "expedition_worker_2", "dialogue": "s18_amb_rope", "radius": 5, "talk_radius": 5,
         "approach": False, "stages": ["camp_talk", "secure"], "priority": 1, "ambient": True},
        {"id": "s18_amb_ranger", "character": "sylvaris_ranger_1", "dialogue": "s18_amb_ranger", "radius": 5, "talk_radius": 5,
         "approach": False, "stages": ["camp_talk", "secure"], "priority": 1, "ambient": True},
        {"id": "s18_amb_eng", "character": "stonecrown_engineer_2", "dialogue": "s18_amb_eng", "radius": 5, "talk_radius": 5,
         "approach": False, "stages": ["secure", "gap"], "priority": 1, "ambient": True},
    ],
    "area_triggers": [
        {"id": "s18_descent_talk", "location": "lower_stair", "stages": ["escort"], "actions": [dialogue("s18_descent")]},
    ],
    "chatter": [],
}

dialogues = {
    "s18_camp": lines(
        (B, "I've never seen so many people working together."), (E, "Let's hope they stay that way."),
        (O, "They will if nobody touches the wrong thing."), (S, "You keep saying that."),
        (O, "And you keep reaching toward mysterious glowing stones."), (S, "Fair point."),
        (G, "My engineers found an entrance beneath the eastern foundation."), (SA, "Does it match the plans?"),
        (G, "Too closely for coincidence.")),
    "s18_descent": lines(
        (E, "The air feels different down here."), (S, "Colder?"),
        (E, "Still. Like nobody's disturbed it in centuries."), (B, "Let's keep it that way."),
        (O, "We came here specifically to disturb it."), (B, "You know what I mean."), critical=False),
    "s18_conduits": lines(
        (G, "Copper channels, older than any mine of mine. And they're humming."),
        (SA, "The deep note. The Sunspire pages spoke of it.")),
    "s18_clock": lines(
        (O, "This mechanism has been turning for centuries and it hasn't moved an inch."),
        (SA, "Or it only moves when time does something it shouldn't.")),
    "s18_twins": lines((E, "Two circles. Exactly the same."), (SA, "A door and its echo, perhaps.")),
    "s18_warning": lines((G, "Another warning. Whoever built this was very worried about something.")),
    "s18_symbols": lines(
        (O, "Six symbols. Six records."), (SA, "The carvings tell us how to align them."),
        (S, "And what's behind the door?"), (O, "That's what we're here to learn.")),
    "s18_carving": lines(
        (SA, "Eldervale's tower at the crown. Then sunwise: the leaf of Sylvaris, the anvil of Stonecrown, "
             "the sun of Solkar, the flame of Veyrath, the wave of Thalassia.")),
    "s18_door": lines(
        (B, "Everybody stand back!"), (E, "Steve!"), (O, "It worked."), (SA, "After all these generations...")),
    "s18_chamber": lines(
        (S, "What is that?"), (O, "I haven't the faintest idea."),
        (SA, "The records called this place a boundary."), (E, "Between what?"),
        (SA, "That's the part they never explained.")),
    "s18_grooves": lines((G, "Worn smooth. Something heavy crossed this bridge, many times.")),
    "s18_plinth": lines((SA, "The same six emblems, carved at its foot.")),
    "s18_survive": lines(
        (G, "This chamber has survived longer than any structure I've ever seen."),
        (S, "Then let's make sure it survives today.")),
    "s18_compass": lines(
        (O, "Don't activate anything yet."), (S, "I wasn't planning to."), (E, "Steve...")),
    "s18_amb_rope": lines(("expedition_worker_2", "Rope's for going down slowly. Not for going down fast."), critical=False),
    "s18_amb_ranger": lines(("sylvaris_ranger_1", "Liora wants the treeline watched. Nobody comes up that causeway unseen."), critical=False),
    "s18_amb_eng": lines(("stonecrown_engineer_2", "Every beam you fit is one less we have to carry back up."), critical=False),
}

cinematics = {
    "mc001_s018_camp": {
        "duration": 220, "fade_in": 20, "fade_out": 12, "hide_player": True,
        "keyframes": [key(0, [-4.0, 9.0, 26.0], [16, 0, 10], 66),
                      key(120, [6.0, 5.0, 20.0], [20, 0, 12], 60),
                      key(220, [24.0, 3.5, 18.0], [20, -2, 11], 55)],
        "subtitles": [{"t": 10, "duration": 110, "text": "Dawn. The joint expedition's camp, beneath the Ancient Northern Monument", "style": "caption"},
                      {"t": 130, "duration": 80, "text": "Quest: The Sealed Chamber", "style": "title"}],
        "sounds": [{"t": 0, "sound": "minecraft:block.campfire.crackle", "volume": 0.8},
                   {"t": 60, "sound": "minecraft:block.anvil.use", "volume": 0.3, "pitch": 1.4},
                   {"t": 140, "sound": "minecraft:block.wood.hit", "volume": 0.5}],
    },
    "mc001_s018_door": {
        "duration": 200, "fade_in": 8, "fade_out": 10, "hide_player": False,
        "keyframes": [key(0, [20.0, -37.5, -27.0], [20, -34, -35], 62),
                      key(200, [20.0, -37.0, -30.0], [20, -34, -40], 70)],
        "shakes": [{"t": 30, "duration": 60, "intensity": 0.35}, {"t": 120, "duration": 30, "intensity": 0.2}],
        "sounds": [{"t": 10, "sound": "minecraft:block.end_portal_frame.fill", "volume": 1.0, "pitch": 0.6},
                   {"t": 30, "sound": "minecraft:block.piston.contract", "volume": 1.0, "pitch": 0.5},
                   {"t": 60, "sound": "minecraft:block.grindstone.use", "volume": 1.0, "pitch": 0.4},
                   {"t": 100, "sound": "minecraft:block.iron_door.open", "volume": 1.0, "pitch": 0.5}],
        "subtitles": [{"t": 20, "duration": 120, "text": "The six locks turn together, and the great door sinks into the floor.", "style": "caption"}],
        "cues": [{"t": 95, "actions": [change("mc001_s018_door_open"),
                                        {"type": "particles", "particle": "minecraft:cloud", "location": "door_front", "count": 30, "spread": 2.5}]}],
    },
    "mc001_s018_end": {
        "duration": 180, "fade_in": 10, "fade_out": 30, "hide_player": True, "hold_black": 30,
        "keyframes": [key(0, [20.0, -36.0, -40.0], [20, -31, -52], 60),
                      key(150, [20.0, -33.0, -45.0], [20, -31, -52], 48)],
        "dips": [{"t": 150, "duration": 30}],
        "sounds": [{"t": 40, "sound": "minecraft:block.amethyst_block.resonate", "volume": 0.6, "pitch": 0.5},
                   {"t": 130, "sound": "minecraft:block.piston.extend", "volume": 0.9, "pitch": 0.4},
                   {"t": 150, "sound": "minecraft:block.iron_trapdoor.close", "volume": 0.9, "pitch": 0.5}],
        "subtitles": [{"t": 120, "duration": 50, "text": "Somewhere inside the structure, something locks into place.", "style": "caption"}],
    },
}

journal = {"s18_journal": {"title": "The Sealed Chamber", "text": (
    "The kingdoms dug beneath the monument together. We shored up the old supports, rebuilt the fallen steps and "
    "took the first research team down. At the bottom was a hall the Builders left: copper channels that hummed "
    "when we gave them their hearts, a clock that has never moved, two circles carved exactly alike, and another "
    "warning. The door's carving gave the order of the six lock stones, and the door sank away. Behind it is a "
    "chamber with a great stone ring standing over a chasm. The compass points straight at it. Orin says nobody "
    "touches anything.")}}

write_scene("mc001_s018", "mc001_s017", scene, dialogues, cinematics, journal)
