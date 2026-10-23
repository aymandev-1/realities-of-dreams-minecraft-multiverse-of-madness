"""Writes the Scene 015 bundle (rod-story/scenes/mc001_s015.json): "The Lord of Emberhold".

Dialogue is verbatim from docs/story/SAGA_1_SCENES_012_019_REVISED.md (Version 2). Hints, captions,
objectives, item names and the journal are gameplay text. The scene is set in Emberhold, the seat of
Veyrath, anchored at MC-001 block (-560, 72, -60); positions must match world/site/Emberhold.java.
Run from the repository root: python tools/story/build_s015.py
"""
from common import *

TRUST = "mc001.kaelen.trust"
G1, G2, G3, G4 = "veyrath_guard_1", "veyrath_guard_2", "veyrath_guard_3", "veyrath_guard_4"
C = ["emberhold_citizen_%d" % i for i in range(1, 9)]

locs = {
    # The approach road west of the moat, the causeway and the west gate.
    "road_west": {"pos": [-80, 0, 0], "radius": 2},
    "elara_start": {"pos": [-81, 0, 2], "radius": 1},
    "bram_start": {"pos": [-81, 0, -2], "radius": 1},
    "liora_start": {"pos": [-82, 0, 0], "radius": 1},
    "aveline_start": {"pos": [-76, 0, 0], "radius": 1},
    "garrick_start": {"pos": [-76, 0, 2], "radius": 1},
    "sahir_start": {"pos": [-76, 0, -2], "radius": 1},
    "causeway_mid": {"pos": [-63, 0, 0], "radius": 3},
    "guard_gate_n": {"pos": [-58, 0, -4], "radius": 1},
    "guard_gate_s": {"pos": [-58, 0, 4], "radius": 1},
    "gate": {"pos": [-50, 0, 0], "radius": 3},
    "aveline_gate": {"pos": [-46, 0, 0], "radius": 1},
    "garrick_gate": {"pos": [-46, 0, 2], "radius": 1},
    "sahir_gate": {"pos": [-46, 0, -2], "radius": 1},
    # Forge Street, the Forge Square and the lanes.
    "forge_st_w": {"pos": [-44, 0, 2], "radius": 1},
    "forge_st_e": {"pos": [-8, 0, -2], "radius": 1},
    "square_n": {"pos": [-28, 0, -6], "radius": 1},
    "square_s": {"pos": [-28, 0, 6], "radius": 1},
    "north_st": {"pos": [-28, 0, -20], "radius": 1},
    "north_lane": {"pos": [-42, 0, -28], "radius": 1},
    "south_st": {"pos": [-28, 0, 20], "radius": 1},
    "south_lane": {"pos": [-12, 0, 27], "radius": 1},
    "east_lane": {"pos": [10, 0, -20], "radius": 1},
    "forge_1": {"pos": [-50, 0, 11], "radius": 1},
    "forge_2": {"pos": [-21, 0, -11], "radius": 1},
    "great_forge": {"pos": [30, 0, 26], "radius": 1},
    "market": {"pos": [-40, 0, 11], "radius": 1},
    "citizen_gate": {"pos": [-49, 0, 5], "radius": 1},
    # The Ember Hall: the door on the west, the long council table (x 22..34, z 0) and the throne dais.
    "hall_door": {"pos": [11, 0, 0], "radius": 2},
    "hall_inside": {"pos": [19, 0, 0], "radius": 3},
    "kaelen_throne": {"pos": [42, 1, 0], "radius": 1},
    "kaelen_table": {"pos": [36, 0, 0], "radius": 1},
    "aveline_hall": {"pos": [20, 0, 0], "radius": 1},
    "garrick_hall": {"pos": [25, 0, -2], "radius": 1},
    "sahir_hall": {"pos": [29, 0, -2], "radius": 1},
    "liora_hall": {"pos": [33, 0, -2], "radius": 1},
    "elara_hall": {"pos": [24, 0, 2], "radius": 1},
    "bram_hall": {"pos": [28, 0, 2], "radius": 1},
    "guard_hall_n": {"pos": [37, 0, -7], "radius": 1},
    "guard_hall_s": {"pos": [37, 0, 7], "radius": 1},
    "table": {"pos": [28, 0, 2], "radius": 3},
    # The vault approach north of the hall: the corridor with its three lava channels, the brazier
    # antechamber, the vault door (z -40) and the vault with the Ember Key's pedestal.
    "vault_mouth": {"pos": [30, 0, -9], "radius": 2},
    "kaelen_vault": {"pos": [30, 0, -14], "radius": 1},
    "elara_north": {"pos": [27, 0, -9], "radius": 1},
    "bram_north": {"pos": [33, 0, -9], "radius": 1},
    "liora_north": {"pos": [26, 0, -7], "radius": 1},
    "channels": {"pos": [30, 0, -20], "radius": 4},
    "ante": {"pos": [30, 0, -34], "radius": 3},
    "kaelen_ante": {"pos": [33, 0, -34], "radius": 1},
    "vault": {"pos": [30, 0, -44], "radius": 3},
    "kaelen_pedestal": {"pos": [32, 0, -46], "radius": 1},
    "key_glow": {"pos": [30, 1, -47], "radius": 1},
}

npcs = [
    {"character": E, "location": "elara_start", "yaw": -90},
    {"character": B, "location": "bram_start", "yaw": -90, "held": "minecraft:iron_sword"},
    {"character": L, "location": "liora_start", "yaw": -90, "held": "minecraft:bow"},
    {"character": AV, "location": "aveline_start", "yaw": -90},
    {"character": G, "location": "garrick_start", "yaw": -90},
    {"character": SA, "location": "sahir_start", "yaw": -90},
    {"character": K, "location": "kaelen_throne", "yaw": 90},
    {"character": G1, "location": "guard_hall_n", "yaw": 90, "held": "minecraft:golden_sword"},
    {"character": G2, "location": "guard_hall_s", "yaw": 90, "held": "minecraft:golden_sword"},
    {"character": G3, "location": "guard_gate_n", "yaw": 90, "held": "minecraft:iron_sword"},
    {"character": G4, "location": "guard_gate_s", "yaw": 90, "held": "minecraft:iron_sword"},
    {"character": C[0], "location": "forge_1", "yaw": 0, "activity": "work", "held": "minecraft:iron_ingot"},
    {"character": C[1], "location": "forge_2", "yaw": 180, "activity": "work", "held": "minecraft:iron_pickaxe"},
    {"character": C[2], "location": "forge_st_w", "activity": "routine", "route": ["forge_st_w", "square_n", "forge_st_e"]},
    {"character": C[3], "location": "north_st", "activity": "routine", "route": ["north_st", "north_lane", "square_n"]},
    {"character": C[4], "location": "market", "yaw": 0, "activity": "work", "held": "minecraft:bread"},
    {"character": C[5], "location": "south_st", "activity": "routine", "route": ["south_st", "south_lane", "square_s"]},
    {"character": C[6], "location": "great_forge", "yaw": 180, "activity": "work", "held": "minecraft:iron_axe"},
    {"character": C[7], "location": "citizen_gate", "yaw": 90, "activity": "routine", "route": ["citizen_gate", "east_lane", "square_s"]},
]

# The three pieces of evidence on the council table. Examined first (CLUES), then presented in order
# (SEQUENCE): the presenting ids are different clues at the same places, with the same blocks.
EVIDENCE = [("grove", "Rubbing of the Sylvaris grove stone", [25, 1, 0], "minecraft:chiseled_tuff"),
            ("fragments", "The matching fragments", [28, 1, 0], "minecraft:chiseled_stone_bricks"),
            ("map", "Map of the six landmarks", [31, 1, 0], "minecraft:cartography_table")]
# The three oath braziers in the antechamber, each with its carved word.
BRAZIERS = [("keep", "Brazier carved KEEP", [26, 0, -38]),
            ("apart", "Brazier carved APART", [34, 0, -37]),
            ("never", "Brazier carved NEVER", [26, 0, -35])]

clues = {}
for key_, name, pos, block in EVIDENCE:
    clues["s15_ev_" + key_] = {"name": name, "pos": pos, "block": block}
for key_, name, pos, block in EVIDENCE:
    clues["s15_present_" + key_] = {"name": "Present: " + name[0].lower() + name[1:], "pos": pos, "block": block}
for key_, name, pos in BRAZIERS:
    clues["s15_brazier_" + key_] = {"name": name, "pos": pos, "block": "minecraft:chiseled_polished_blackstone"}
clues["s15_ember_key"] = {"name": "The Ember Key of Veyrath", "pos": [30, 1, -47], "block": "minecraft:chiseled_nether_bricks"}

CITY = ["causeway", "warm_talk", "to_hall"]


def ambient(id_, who, radius=6):
    return {"id": id_, "character": who, "dialogue": id_, "radius": radius, "talk_radius": radius,
            "approach": False, "stages": CITY, "priority": 1, "ambient": True}


def move(who, where):
    return {"type": "npc_move", "character": who, "location": where}


def look(who, where):
    return {"type": "npc_look", "character": who, "location": where}


VAULT_OPEN = [change("mc001_s015_braziers_lit"), change("mc001_s015_vault_open")]

scene = {
    "id": "mc001_s015", "title": "The Lord of Emberhold", "universe": "MC-001", "saga": 1, "scene": 15,
    "canon_status": ("Saga 1 revised screenplay, Version 2 (docs/story/SAGA_1_SCENES_012_019_REVISED.md): spoken "
                     "dialogue verbatim. Hints, captions, item names and the outcome message are gameplay text."),
    "dimension": DIM, "site": "emberhold", "anchor": [-560, 72, -60],
    "arrival": {"pos": [-80, 0, 0], "yaw": -90},
    "next_scene": "mc001_s016",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "arrive", "checkpoint": "road_west",
         "on_enter": [{"type": "teleport_player", "location": "road_west", "yaw": -90},
                      *stay(E, "elara_start"), *stay(B, "bram_start"), *stay(L, "liora_start"),
                      *stay(AV, "aveline_start"), *stay(G, "garrick_start"), *stay(SA, "sahir_start"),
                      *follow(E, B, L), {"type": "set_value", "key": TRUST, "value": 0},
                      cinematic("mc001_s015_arrival")],
         "complete": {"type": "cinematic_end", "value": "mc001_s015_arrival"}},
        {"id": "causeway", "objective": "Cross the causeway over the lava moat", "target": "causeway_mid", "checkpoint": "road_west",
         "on_enter": [move(AV, "aveline_gate"), move(G, "garrick_gate"), move(SA, "sahir_gate"),
                      hint("The causeway is walled on both sides. Keep to it; the moat below is lava.", 160)],
         "complete": {"type": "reach", "value": "causeway_mid"},
         "on_complete": [sound("minecraft:block.lava.pop", 0.6, 0.8), dialogue("s15_warm")]},
        {"id": "warm_talk", "checkpoint": "causeway_mid",
         "complete": {"type": "dialogue_end", "value": "s15_warm"}},
        {"id": "to_hall", "objective": "Go to Lord Kaelen in the Ember Hall", "target": "hall_inside", "checkpoint": "gate",
         "on_enter": [move(AV, "aveline_hall"), move(G, "garrick_hall"), move(SA, "sahir_hall"),
                      hint("The Ember Hall stands at the east end of Forge Street, under the blue and gold banners. "
                           "The city is yours to look round on the way.", 220)],
         "complete": {"type": "reach", "value": "hall_inside"},
         "on_complete": [*stay(E, "elara_hall"), *stay(B, "bram_hall"), *stay(L, "liora_hall"),
                         *stay(AV, "aveline_hall"), *stay(G, "garrick_hall"), *stay(SA, "sahir_hall"),
                         *stay(K, "kaelen_throne"), cinematic("mc001_s015_hall")]},
        {"id": "hall_view", "checkpoint": "hall_inside",
         "complete": {"type": "cinematic_end", "value": "mc001_s015_hall"},
         "on_complete": [dialogue("s15_council")]},
        {"id": "council_talk", "checkpoint": "hall_inside",
         "complete": {"type": "dialogue_end", "value": "s15_council"}},
        {"id": "examine", "objective": "Examine the evidence laid on the council table", "target": "table", "checkpoint": "hall_inside",
         "on_enter": [move(K, "kaelen_table"), look(K, "table"),
                      hint("Three pieces of evidence lie on the long table. Press {use} on each one.", 180)],
         "complete": {"type": "clues", "value": "s15_ev_grove,s15_ev_fragments,s15_ev_map"},
         "on_complete": [sound("minecraft:item.book.page_turn", 0.8)]},
        {"id": "present", "objective": "Present the evidence to the council in the order of its history", "target": "table",
         "checkpoint": "hall_inside",
         "on_enter": [look(K, "table"),
                      hint("Lay the history out in order: first the Sylvaris grove stone, the oldest record; then the "
                           "fragments that match it; last the map that ties all six landmarks together. "
                           "Press {use} on each in turn.", 260)],
         "complete": {"type": "sequence", "value": "s15_present_grove,s15_present_fragments,s15_present_map"},
         "on_wrong": [{"type": "add_value", "key": TRUST, "amount": -1},
                      sound("minecraft:entity.villager.no", 0.7, 0.8), look(K, "hall_inside"),
                      hint("Kaelen's eyes narrow: the story does not hold together in that order. Start again with "
                           "the grove stone record, then the fragments, then the map.", 200)],
         "on_complete": [sound("minecraft:block.amethyst_block.chime", 0.8, 0.7), dialogue("s15_oath")]},
        {"id": "oath_talk", "checkpoint": "hall_inside",
         "complete": {"type": "dialogue_end", "value": "s15_oath"}},
        {"id": "dam", "objective": "Dam the three lava channels across the vault approach", "target": "channels",
         "checkpoint": "vault_mouth",
         "on_enter": [move(K, "kaelen_vault"), *stay(E, "elara_north"), *stay(B, "bram_north"), *stay(L, "liora_north"),
                      give("minecraft:polished_blackstone_bricks", 24, "Dam stone",
                           "Heavy Veyrath stone, cut to fit the vault channels.|Lava will not move it."),
                      hint("Three lava channels cross the passage north of the hall. Place dam stone in each channel "
                           "where the green sparkles are. Work from the edge: don't step into the lava.", 260)],
         "complete": {"type": "blocks", "value": "solid|" + ";".join(
             "%d,-1,%d" % (x, z) for z in (-17, -18, -22, -23, -27, -28) for x in (29, 30, 31))},
         "on_complete": [sound("minecraft:block.lava.extinguish", 0.9, 0.9), move(K, "kaelen_ante"), *follow(E, B, L)]},
        {"id": "braziers", "objective": "Light the oath braziers in the order of the oath", "target": "ante",
         "checkpoint": "channels",
         "on_enter": [hint("Each brazier is carved with a word of Kaelen's oath: keep the Ember Key apart from the door, "
                           "and never carry it in anger. Press {use} on them in the oath's order.", 240)],
         "complete": {"type": "sequence", "value": "s15_brazier_keep,s15_brazier_apart,s15_brazier_never"},
         "on_wrong": [sound("minecraft:block.fire.extinguish", 0.9, 0.9),
                      {"type": "particles", "particle": "minecraft:smoke", "location": "ante", "count": 20, "spread": 1.5},
                      hint("The brazier gutters out and the others go dark. The oath runs: Keep... Apart... Never.", 180)],
         "on_complete": [*VAULT_OPEN, sound("minecraft:block.fire.ambient", 1.0, 0.7), cinematic("mc001_s015_vault")]},
        {"id": "vault_view", "checkpoint": "ante",
         "complete": {"type": "cinematic_end", "value": "mc001_s015_vault"},
         "on_complete": [move(K, "kaelen_pedestal")]},
        {"id": "key", "objective": "Take the Ember Key from its pedestal", "target": "vault", "checkpoint": "ante",
         "on_enter": [*VAULT_OPEN, hint("The vault stands open. Press {use} on the Ember Key.", 160)],
         "complete": {"type": "clues", "value": "s15_ember_key"},
         "on_complete": [sound("minecraft:block.respawn_anchor.charge", 0.7, 0.8),
                         {"type": "particles", "particle": "minecraft:flame", "location": "key_glow", "count": 16, "spread": 0.4},
                         look(K, "vault"), dialogue("s15_closing")]},
        {"id": "closing_talk", "checkpoint": "ante",
         "complete": {"type": "dialogue_end", "value": "s15_closing"},
         "on_complete": [
             give("minecraft:chiseled_nether_bricks", 1, "Veyrath fragment",
                  "The Ember Key of Veyrath. Keep it apart from the door."),
             *complete("Mission Complete", "A Divided Council", "s15_journal",
                       "Veyrath has joined the expedition, cautiously. Lord Kaelen will not let the Ember Key out of his "
                       "sight. Captain Maren waits in Tidewatch.",
                       ["mc001.veyrath.joined", "mc001.s015.complete"])]},
    ],
    "triggers": [
        ambient("s15_amb_channels", C[2]),
        ambient("s15_amb_sheathed", G3),
        ambient("s15_amb_grandmother", C[7]),
        ambient("s15_amb_bellows", C[0]),
        ambient("s15_amb_colours", C[4]),
    ],
    "area_triggers": [],
    "chatter": [],
}

NEG = "mc001.kaelen.approach_"

council = lines(
    (K, "So this is the discovery everyone has been whispering about.", [look(E, "kaelen_throne"), look(B, "kaelen_throne")]),
    (AV, "You were invited to examine the evidence."),
    (K, "I was invited after the others had already begun their investigation.", [look(AV, "kaelen_throne")]),
    (G, "We barely understand what we found.", [look(G, "kaelen_throne")]),
    (K, "And yet you've already carried pieces of it into another kingdom.", [move(K, "kaelen_table")]),
    (K, "I will not allow an ancient Veyrath legacy to be claimed by whoever reached it first.", [look(SA, "kaelen_table")]),
    (S, "Nobody's claiming anything."),
    (K, "Not yet."),
    (L, "The evidence was discovered across several territories. This concerns us all.", [look(L, "kaelen_table")]),
    (K, "That is easy to say when your people already hold part of it.", [look(K, "liora_hall")]),
    (AV, "Enough. We will establish a joint expedition and keep an open record of every discovery.", [look(K, "aveline_hall")]),
    (K, "Then Veyrath expects equal representation."),
    (S, "That's fair."),
    (K, "And who gave you authority to lead this?", [look(K, "hall_inside")]),
    (S, "Nobody. I was looking into something dangerous near my home."),
    (S, "If you know more about it, help us."),
    (K, "Then tell me why Veyrath should open its vault to strangers."))
last = "l%02d" % (len(council["lines"]) - 1)
council["lines"][last]["choices"] = [
    {"text": "The records show all six kingdoms kept a piece. (Evidence)", "next": "ev",
     "actions": [flag(NEG + "evidence"), {"type": "add_value", "key": TRUST, "amount": 1}]},
    {"text": "Veyrath kept its piece longest and best. (Respect)", "next": "re",
     "actions": [flag(NEG + "respect"), {"type": "add_value", "key": TRUST, "amount": 1}]},
    {"text": "Something else may wake it first. (Warning)", "next": "wa",
     "actions": [flag(NEG + "warning")]},
]
council["lines"].update({
    "ev": {"speaker": S, "text": "Because the records show all six kingdoms were given a piece to keep. Yours too.", "next": "ev_k"},
    "ev_k": {"speaker": K, "text": "Records. Scholars' ink. Show me, then."},
    "re": {"speaker": S, "text": "Because Veyrath kept its piece longer and better than anyone. We need what you know, not just the stone.", "next": "re_k"},
    "re_k": {"speaker": K, "text": "Flattery from Firstbrook. Still, it happens to be true."},
    "wa": {"speaker": S, "text": "Because if we don't understand it, something else may wake it first.", "next": "wa_k"},
    "wa_k": {"speaker": K, "text": "You think I haven't lain awake over that? Prove you understand it better than I do."},
})

dialogues = {
    "s15_warm": lines((B, "Is it always this warm?"), (E, "Don't touch the walls.")),
    "s15_amb_channels": lines((C[2], "Mind the channels, outlander. The stone's hotter than it looks."), critical=False),
    "s15_amb_sheathed": lines((G3, "The Lord receives you in the Ember Hall. Weapons stay sheathed."), critical=False),
    "s15_amb_grandmother": lines((C[7], "Eldervale's queen, here? My grandmother won't believe it."), critical=False),
    # Added ambient city lines (not in the screenplay; optional chatter only).
    "s15_amb_bellows": lines((C[0], "The bellows never rest in Emberhold. Neither do we."), critical=False),
    "s15_amb_colours": lines((C[4], "Blue and gold for the Lord's house. Black for the stone that bore us."), critical=False),
    "s15_council": council,
    "s15_oath": lines(
        (K, "Six kingdoms, one pattern. Very well. You've earned the truth.", [look(G, "kaelen_table"), look(L, "kaelen_table")]),
        (K, "My house swore an oath before Emberhold had walls. Keep the Ember Key apart from the door, and never carry it in anger.",
         [look(K, "vault_mouth")]),
        (S, "Door? What door?"),
        (K, "The oath never said. Only that the key and the door must not meet by chance.", [look(K, "table")]),
        (E, "You've guarded it all this time without knowing why?", [look(E, "kaelen_table")]),
        (K, "Veyrath keeps its word. That is the why."),
        (K, "Every kingdom calls Veyrath the dangerous one. Fire, forges, temper. Yet we kept the most dangerous thing of all, and kept it safe.",
         [look(AV, "kaelen_table"), look(K, "vault_mouth")])),
    "s15_closing": lines(
        (K, "Very well. Veyrath will send its experts."),
        (AV, "Then we have an agreement."),
        (K, "An agreement to investigate. Not to surrender our interests."),
        (S, "That's a start."),
        (K, "And the key does not leave my sight. Where it goes, Veyrath goes.", [look(K, "key_glow")]),
        (AV, "Captain Maren sends word from Tidewatch. Her divers have found something at the old coastal watch."),
        (S, "The last piece.")),
}

cinematics = {
    "mc001_s015_arrival": {
        "duration": 280, "fade_in": 20, "fade_out": 12, "hide_player": True,
        "keyframes": [
            key(0, [-96.0, 16.0, -26.0], [-40, 8, 0], 66),
            key(140, [-82.0, 11.0, -10.0], [-30, 9, 0], 60),
            key(280, [-72.0, 4.0, 5.0], [-56, 3, 0], 55)],
        "subtitles": [{"t": 30, "duration": 120, "text": "Emberhold — Seat of Veyrath", "style": "title"},
                      {"t": 160, "duration": 100, "text": "Evening. The delegation crosses the causeway over the lava moat.", "style": "caption"}],
        "sounds": [{"t": 0, "sound": "minecraft:block.lava.ambient", "volume": 0.6},
                   {"t": 70, "sound": "minecraft:block.anvil.use", "volume": 0.3, "pitch": 0.8},
                   {"t": 150, "sound": "minecraft:block.anvil.use", "volume": 0.3, "pitch": 0.9},
                   {"t": 200, "sound": "minecraft:block.fire.ambient", "volume": 0.5}],
    },
    "mc001_s015_hall": {
        "duration": 210, "fade_in": 12, "fade_out": 10, "hide_player": True,
        "keyframes": [
            key(0, [16.0, 5.0, 8.0], [42, 2, 0], 64),
            key(210, [31.0, 2.8, 4.5], [42, 2.2, 0], 50)],
        "subtitles": [{"t": 25, "duration": 120, "text": "The Ember Hall. Lord Kaelen waits on the throne of Veyrath.", "style": "caption"}],
        "sounds": [{"t": 0, "sound": "minecraft:block.fire.ambient", "volume": 0.5},
                   {"t": 90, "sound": "minecraft:block.lava.pop", "volume": 0.4}],
        "cues": [{"t": 60, "actions": [look(K, "hall_inside")]}],
    },
    "mc001_s015_vault": {
        "duration": 190, "fade_in": 8, "fade_out": 10, "hide_player": False,
        "keyframes": [
            key(0, [30.0, 2.6, -33.5], [30, 1.5, -40], 60),
            key(190, [30.0, 2.2, -39.0], [30, 1.0, -47], 50)],
        "subtitles": [{"t": 15, "duration": 130, "text": "The oath fires burn blue, and the vault of Veyrath opens.", "style": "caption"}],
        "sounds": [{"t": 10, "sound": "minecraft:block.fire.ambient", "volume": 0.8, "pitch": 0.7},
                   {"t": 50, "sound": "minecraft:block.piston.contract", "volume": 0.8, "pitch": 0.5},
                   {"t": 120, "sound": "minecraft:block.respawn_anchor.ambient", "volume": 0.6}],
        "cues": [{"t": 110, "actions": [{"type": "particles", "particle": "minecraft:flame", "location": "key_glow",
                                         "count": 14, "spread": 0.4}]}],
    },
}

journal = {"s15_journal": {"title": "A Divided Council", "text": (
    "Emberhold, seat of Veyrath: basalt towers, lava falls and forges that never rest. Lord Kaelen received us in the "
    "Ember Hall, and he did not hide his anger that the others began without him. Queen Aveline promised a joint "
    "expedition with an open record of every discovery, and Veyrath equal representation. We laid out the evidence: "
    "the Sylvaris grove stone, the matching fragments and the map of the six landmarks. Kaelen's house has kept an oath "
    "since before Emberhold had walls: keep the Ember Key apart from the door, and never carry it in anger. We dammed "
    "the lava channels, lit the oath braziers and opened the vault. Veyrath joins us, cautiously, and the key goes "
    "nowhere without Kaelen. Captain Maren's divers have found something at Tidewatch.")}}

write_scene("mc001_s015", "mc001_s014", scene, dialogues, cinematics, journal)
