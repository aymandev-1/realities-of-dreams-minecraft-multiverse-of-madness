"""Writes the Scene 012 bundle (rod-story/scenes/mc001_s012.json): "The Queen's Summons".

Dialogue is verbatim from docs/story/SAGA_1_SCENES_012_019_REVISED.md (Version 2). Hints, captions,
objectives, item names and the journal are gameplay text. The scene is set in Valebridge, anchored at
MC-001 block (-115, 67, 105); positions must match world/site/Valebridge.java.
Run from the repository root: python tools/story/build_s012.py
"""
from common import *

ARCH, GUARD = "eldervale_official_1", "eldervale_royal_guard_1"

locs = {
    "gate_out": {"pos": [62, 0, 0], "radius": 2},
    "elara_start": {"pos": [63, 0, 2], "radius": 1},
    "bram_start": {"pos": [63, 0, -2], "radius": 1},
    "liora_start": {"pos": [64, 0, 0], "radius": 1},
    "gate": {"pos": [52, 0, 0], "radius": 3},
    "gate_post": {"pos": [50, 0, 2], "radius": 1},
    "street_e1": {"pos": [42, 0, -1], "radius": 1},
    "street_e2": {"pos": [22, 0, 1], "radius": 1},
    "plaza_n": {"pos": [0, 0, -9], "radius": 1},
    "plaza_e": {"pos": [9, 0, 1], "radius": 1},
    "plaza_s": {"pos": [1, 0, 9], "radius": 1},
    "plaza_w": {"pos": [-9, 0, -1], "radius": 1},
    "archive_st": {"pos": [-16, 0, 0], "radius": 1},
    "harbour_st": {"pos": [0, 0, 20], "radius": 1},
    "stall_1": {"pos": [20, 0, 4], "radius": 1},
    "stall_2": {"pos": [34, 0, -4], "radius": 1},
    "bench": {"pos": [6, 0, 6], "radius": 1},
    "archive_door": {"pos": [-24, 0, 0], "radius": 2},
    "archive_desk": {"pos": [-27, 0, 1], "radius": 2},
    "archivist_spot": {"pos": [-26, 0, 4], "radius": 1},
    "messenger_spot": {"pos": [-24, 0, 2], "radius": 1},
    "palace_door": {"pos": [0, 0, -16], "radius": 2},
    "palace_hall": {"pos": [0, 0, -25], "radius": 3},
    "guard_door": {"pos": [3, 0, -20], "radius": 1},
    "queen_seat": {"pos": [0, 0, -39], "radius": 1},
    "adviser_w": {"pos": [-3, 0, -39], "radius": 1},
    "adviser_e": {"pos": [3, 0, -39], "radius": 1},
    "guard_w": {"pos": [-6, 0, -39], "radius": 1},
    "guard_e": {"pos": [6, 0, -39], "radius": 1},
    "elara_hall": {"pos": [-2, 0, -26], "radius": 1},
    "bram_hall": {"pos": [2, 0, -26], "radius": 1},
    "liora_hall": {"pos": [-3, 0, -24], "radius": 1},
    "records": {"pos": [-40, 0, -2], "radius": 4},
}

CITY = ["to_gate", "gates_talk", "to_archive"]

npcs = [
    {"character": E, "location": "elara_start", "yaw": 90},
    {"character": B, "location": "bram_start", "yaw": 90},
    {"character": L, "location": "liora_start", "yaw": 90, "held": "minecraft:bow"},
    {"character": ARCH, "location": "archivist_spot", "yaw": 180},
    {"character": GUARD, "location": "gate_post", "yaw": -90, "held": "minecraft:iron_sword",
     "until_stage": "messenger"},
    {"character": GUARD, "location": "messenger_spot", "yaw": 90, "from_stage": "messenger", "until_stage": "to_palace"},
    {"character": GUARD, "location": "guard_door", "yaw": 180, "from_stage": "to_palace", "held": "minecraft:iron_sword"},
    {"character": AV, "location": "queen_seat", "yaw": 0},
    {"character": "eldervale_official_2", "location": "adviser_w", "yaw": 0},
    {"character": "eldervale_official_3", "location": "adviser_e", "yaw": 0},
    {"character": "eldervale_royal_guard_2", "location": "guard_w", "yaw": 0, "held": "minecraft:iron_sword"},
    {"character": "eldervale_royal_guard_3", "location": "guard_e", "yaw": 0, "held": "minecraft:iron_sword"},
    {"character": "merchant_1", "location": "stall_1", "yaw": 180, "activity": "work"},
    {"character": "merchant_2", "location": "stall_2", "yaw": 0, "activity": "work"},
    {"character": "valebridge_citizen_1", "location": "plaza_n", "activity": "routine", "route": ["plaza_n", "plaza_e", "street_e2"]},
    {"character": "valebridge_citizen_2", "location": "plaza_w", "activity": "routine", "route": ["plaza_w", "archive_st"]},
    {"character": "valebridge_citizen_3", "location": "bench", "yaw": 200},
    {"character": "valebridge_citizen_4", "location": "street_e1", "activity": "routine", "route": ["street_e1", "street_e2"]},
    {"character": "valebridge_citizen_5", "location": "harbour_st", "activity": "routine", "route": ["harbour_st", "plaza_s"]},
    {"character": "valebridge_citizen_6", "location": "plaza_e", "yaw": 90, "activity": "work"},
]

clues = {
    "s12_charter": {"name": "The Charter of the Six Crowns", "pos": [-48, 0, -3], "block": "minecraft:lectern",
                    "dialogue": "s12_charter"},
    "s12_chronicle": {"name": "The Founding Chronicle of Valebridge", "pos": [-40, 0, -3], "block": "minecraft:lectern",
                      "dialogue": "s12_chronicle"},
    "s12_inventory": {"name": "Inventory of the Old Vault", "pos": [-32, 0, -3], "block": "minecraft:lectern",
                      "dialogue": "s12_inventory"},
}


def ambient(id_, who, radius=6):
    return {"id": id_, "character": who, "dialogue": id_, "radius": radius, "talk_radius": radius,
            "approach": False, "stages": CITY, "priority": 1, "ambient": True}


scene = {
    "id": "mc001_s012", "title": "The Queen's Summons", "universe": "MC-001", "saga": 1, "scene": 12,
    "canon_status": ("Saga 1 revised screenplay, Version 2 (docs/story/SAGA_1_SCENES_012_019_REVISED.md): spoken "
                     "dialogue verbatim. Hints, captions, item names and the outcome message are gameplay text."),
    "dimension": DIM, "site": "valebridge", "anchor": [-115, 67, 105],
    "arrival": {"pos": [62, 0, 0], "yaw": 90},
    "next_scene": "mc001_s013",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "to_gate", "objective": "Walk into Valebridge through the east gate", "target": "gate", "checkpoint": "gate_out",
         "on_enter": [{"type": "teleport_player", "location": "gate_out", "yaw": 90},
                      *stay(E, "elara_start"), *stay(B, "bram_start"), *stay(L, "liora_start"), *follow(E, B, L),
                      cinematic("mc001_s012_city")],
         "complete": {"type": "reach", "value": "gate"},
         "on_complete": [dialogue("s12_gates")]},
        {"id": "gates_talk", "checkpoint": "gate",
         "complete": {"type": "dialogue_end", "value": "s12_gates"}},
        {"id": "to_archive", "objective": "Deliver the investigation dossier to the royal archive", "target": "archive_desk",
         "checkpoint": "gate",
         "on_enter": [hint("The royal archive is west of the fountain, along the street from the plaza. "
                           "The city is yours to look round on the way.", 220)],
         "complete": {"type": "reach", "value": "archive_desk"},
         "on_complete": [dialogue("s12_archivist")]},
        {"id": "archivist_talk", "checkpoint": "archive_door",
         "complete": {"type": "dialogue_end", "value": "s12_archivist"}},
        {"id": "messenger", "checkpoint": "archive_door",
         "on_enter": [{"type": "npc_look", "character": GUARD, "location": "archive_desk"}, dialogue("s12_messenger")],
         "complete": {"type": "dialogue_end", "value": "s12_messenger"}},
        {"id": "to_palace", "objective": "Meet Queen Aveline in the royal palace", "target": "palace_hall", "checkpoint": "archive_door",
         "on_enter": [hint("The palace stands north of the plaza, at the end of the guarded street.", 160)],
         "complete": {"type": "reach", "value": "palace_hall"},
         "on_complete": [*stay(E, "elara_hall"), *stay(B, "bram_hall"), *stay(L, "liora_hall"), cinematic("mc001_s012_court")]},
        {"id": "court", "checkpoint": "palace_hall",
         "complete": {"type": "cinematic_end", "value": "mc001_s012_court"},
         "on_complete": [dialogue("s12_council")]},
        {"id": "council_talk", "checkpoint": "palace_hall",
         "complete": {"type": "dialogue_end", "value": "s12_council"},
         "on_complete": [give("minecraft:paper", 1, "Royal writ of Eldervale",
                              "By order of Queen Aveline: the bearer may read the sealed royal records.")]},
        {"id": "records", "objective": "Investigate the sealed royal records in the archive", "target": "records",
         "checkpoint": "archive_door",
         "on_enter": [*follow(E, B, L),
                      hint("Three sealed records lie open on lecterns in the archive hall. Press {use} on each one.", 200)],
         "complete": {"type": "clues", "value": "s12_charter,s12_chronicle,s12_inventory"},
         "on_complete": [dialogue("s12_inheritance")]},
        {"id": "inheritance_talk", "checkpoint": "records",
         "complete": {"type": "dialogue_end", "value": "s12_inheritance"},
         "on_complete": [
             give("minecraft:chiseled_stone_bricks", 1, "Eldervale fragment", "The carved stone from the royal Old Vault.|Not to be moved, until now."),
             give("minecraft:oxidized_chiseled_copper", 1, "Sylvaris fragment", "Liora's rubbing of the grove stone, pressed into copper."),
             *complete("Mission Complete", "The Sixfold Council", "s12_journal",
                       "Queen Aveline has opened Eldervale's records to you. Thane Garrick waits in Irondeep.",
                       ["mc001.eldervale.audience", "mc001.s012.complete"])]},
    ],
    "triggers": [
        ambient("s12_amb_bread", "merchant_1"),
        ambient("s12_amb_shook", "valebridge_citizen_1"),
        ambient("s12_amb_carts", GUARD),
        ambient("s12_amb_caravan", "valebridge_citizen_3"),
    ],
    "area_triggers": [],
    "chatter": [],
}

COUNCIL_CHOICES = [
    ("Cautious: we need to investigate safely.", "c_cautious", "mc001.s012.choice_cautious",
     "We don't understand this discovery. We need to investigate safely."),
    ("Urgent: people may already be in danger.", "c_urgent", "mc001.s012.choice_urgent",
     "People may already be in danger. We cannot afford to wait."),
    ("Cooperative: all six must work together.", "c_cooperative", "mc001.s012.choice_cooperative",
     "All six civilisations need to investigate this together."),
]


def council():
    d = lines(
        (AV, "Primordial Steve of Firstbrook."), (S, "Your Majesty."),
        (AV, "Rise. I've heard about the caravan you protected."), (S, "Anyone would've done the same."),
        (AV, "Not everyone does."), (AV, "Now tell me why six ancient symbols have brought you to my court."),
        (S, "Something beneath the northern ruins is active. The symbols appear in several locations, and the compass reacts to all of them."),
        (AV, "And you believe those symbols correspond to our civilisations?"),
        (L, "We have found matching stonework in Sylvaris."),
        (E, "And evidence of an expedition that vanished while studying it."),
        (AV, "Then the remaining leaders must be informed."))
    last = "l%02d" % (len(d["lines"]) - 1)
    d["lines"][last]["choices"] = [{"text": t, "next": n, "actions": [flag(f)]} for t, n, f, _ in COUNCIL_CHOICES]
    for _, n, _, said in COUNCIL_CHOICES:
        d["lines"][n] = {"speaker": S, "text": said, "next": "p0"}
    d["lines"]["p0"] = {"speaker": AV, "text": "The past belongs to all of us. Whatever was buried beneath these lands may affect all of us too.", "next": "p1"}
    d["lines"]["p1"] = {"speaker": S, "text": "Then we'll find the truth together.", "next": "p2"}
    d["lines"]["p2"] = {"speaker": AV, "text": "Begin with what Eldervale has kept. The sealed royal records are open to you."}
    return d


dialogues = {
    "s12_gates": lines(
        (B, "I've never seen so many people in one place."), (E, "Try not to stare."),
        (B, "I'm looking for the best place to eat."), (S, "Some things never change.")),
    "s12_amb_bread": lines(("merchant_1", "Bread from the river farms! Still warm!"), critical=False),
    "s12_amb_shook": lines(("valebridge_citizen_1", "They say the ground shook as far as the harbour last week."), critical=False),
    "s12_amb_carts": lines((GUARD, "Keep the street clear for the envoys' carts."), critical=False),
    "s12_amb_caravan": lines(("valebridge_citizen_3", "Firstbrook folk? You're the ones who saved that caravan."), critical=False),
    "s12_archivist": lines(
        (ARCH, "Firstbrook's dossier? The court has been waiting for it."),
        (S, "Everything we found is in there. The monument, the camp, the stone in Sylvaris."),
        (ARCH, "Then it belongs beside the oldest records we keep. Her Majesty will read it today.")),
    "s12_messenger": lines(
        (GUARD, "Steve of Firstbrook? Her Majesty asks for you and your companions in the council hall."),
        (S, "Lead the way.")),
    "s12_council": council(),
    "s12_charter": lines(
        (S, "'Six crowns were given the keeping of what came before.' Given by whom?"),
        (E, "It doesn't say. Only that the six kingdoms were made keepers.")),
    "s12_chronicle": lines(
        (L, "Valebridge was raised on older stones. The chronicle calls them the foundations of the first builders."),
        (S, "Builders before Eldervale.")),
    "s12_inventory": lines(
        (B, "'One stone, carved, not to be moved. Five sent away to five keepers.' They catalogued the same symbols."),
        (S, "So the kingdoms didn't make these marks. They were handed them.")),
    "s12_inheritance": lines(
        (L, "Every kingdom tells its history as if it began with itself."),
        (S, "But these records say we inherited something. Knowledge from someone older than all six."),
        (E, "Then the other kingdoms will have pieces of it too."),
        (ARCH, "Her Majesty's orders. Thane Garrick writes that his miners have found a carved mechanism beneath Irondeep. "
               "You are to go to him, with the vault stone."),
        (L, "Take this as well. It's a rubbing of the stone in our grove. It's as close as Sylvaris can come to sending the stone itself."),
        (S, "Then Irondeep is next.")),
}

cinematics = {
    "mc001_s012_city": {
        "duration": 300, "fade_in": 20, "fade_out": 12, "hide_player": True,
        "keyframes": [
            key(0, [80.0, 22.0, -10.0], [0, 6, -30], 62),
            key(110, [40.0, 26.0, 18.0], [0, 4, -20], 60),
            key(210, [6.0, 20.0, 34.0], [0, 2, 0], 58),
            key(300, [44.0, 6.0, 6.0], [54, 2, 0], 55)],
        "subtitles": [{"t": 30, "duration": 120, "text": "Valebridge — Capital of Eldervale", "style": "title"}],
        "sounds": [{"t": 0, "sound": "minecraft:entity.villager.ambient", "volume": 0.4},
                   {"t": 120, "sound": "minecraft:block.bell.use", "volume": 0.5, "pitch": 0.8},
                   {"t": 200, "sound": "minecraft:ambient.underwater.loop.additions", "volume": 0.2}],
    },
    "mc001_s012_court": {
        "duration": 170, "fade_in": 12, "fade_out": 10, "hide_player": True,
        "keyframes": [
            key(0, [0.0, 4.5, -20.0], [0, 2, -44], 64),
            key(170, [0.0, 2.6, -31.0], [0, 1.8, -39], 52)],
        "subtitles": [{"t": 20, "duration": 110, "text": "The council hall of Valebridge", "style": "caption"}],
        "sounds": [{"t": 0, "sound": "minecraft:block.wooden_door.open", "volume": 0.6, "pitch": 0.8}],
        "cues": [{"t": 60, "actions": [{"type": "npc_look", "character": AV, "location": "palace_hall"}]}],
    },
}

journal = {"s12_journal": {"title": "The Sixfold Council", "text": (
    "Valebridge is the biggest city I've ever walked through. We left the dossier at the royal archive and were "
    "summoned straight to Queen Aveline, who heard us out and opened Eldervale's sealed records. They say the six "
    "kingdoms were made keepers of something older than any of them: the city itself stands on the foundations of "
    "earlier builders, and the Old Vault kept one carved stone and sent five more to five other keepers. "
    "The Queen gave us the vault stone, Liora gave us her rubbing of the grove stone, and Thane Garrick of "
    "Stonecrown has found a carved mechanism beneath Irondeep. That's where we go next.")}}

write_scene("mc001_s012", "mc001_s011", scene, dialogues, cinematics, journal)
