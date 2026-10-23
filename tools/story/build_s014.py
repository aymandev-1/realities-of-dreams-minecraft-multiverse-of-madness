"""Writes the Scene 014 bundle (rod-story/scenes/mc001_s014.json) from the revised screenplay.

"The Records of Sunspire": spoken dialogue is verbatim from docs/story/SAGA_1_SCENES_012_019_REVISED.md
(the optional city lines marked "added" below are new ambient chatter). Hints, captions, item names,
sign texts and the outcome message are new gameplay text. The scene has its own site, the desert
capital of Solkar, anchored at MC-001 block (520, 68, 420); positions must match world/site/Sunspire.java.
Run from the repository root: python tools/story/build_s014.py
"""
from common import *

CANON_S14 = ("Saga 1 revised screenplay, Scenes 012-019 Version 2 (docs/story/SAGA_1_SCENES_012_019_REVISED.md): "
             "spoken dialogue verbatim. Hints, captions, item names, sign texts and the outcome message are new gameplay text.")

# The great floor map in the House of Records (Sunspire.MAP_*): map cell (dx, dz) is block (dx, -1, dz - 39).
MAP_Z = -39
EMBLEMS = {  # in Sunspire.EMBLEM order: Eldervale, Sylvaris, Stonecrown, Solkar, Veyrath, Thalassia
    "eldervale": (-3, 3), "sylvaris": (4, 1), "stonecrown": (-4, -4),
    "solkar": (10, 8), "veyrath": (-11, -1), "thalassia": (1, 9)}
VALLEY = (0, -2)


def on_map(cell, y=0):
    return [cell[0], y, cell[1] + MAP_Z]


LIGHT_SPOTS = [on_map(EMBLEMS[k]) for k in ("solkar", "veyrath", "thalassia")]

locs = {
    # Outside the west gate, where the party arrives.
    "gate_road": {"pos": [-72, 0, 0], "radius": 2},
    "elara_start": {"pos": [-73, 0, 2], "radius": 1},
    "orin_start": {"pos": [-73, 0, -2], "radius": 1},
    "west_gate": {"pos": [-56, 0, 0], "radius": 4},
    "plaza_west": {"pos": [-17, 0, 0], "radius": 3},
    "records_door": {"pos": [0, 0, -19], "radius": 3},
    # Inside the House of Records.
    "sahir_hall": {"pos": [0, 0, -26], "radius": 1},
    "sahir_meet": {"pos": [0, 0, -24], "radius": 2.5},
    "records_hall": {"pos": [0, 0, -24], "radius": 2},
    "stacks": {"pos": [-14, 0, -43], "radius": 4},
    "sahir_stacks": {"pos": [-14, 0, -38], "radius": 1},
    "glyph_wall": {"pos": [19, 0, -46], "radius": 3},
    "sahir_glyphs": {"pos": [17, 0, -50], "radius": 1},
    "warning_desk": {"pos": [17, 0, -33], "radius": 2},
    "sahir_warning": {"pos": [16, 0, -31], "radius": 1},
    "manuscript_desk": {"pos": [15, 0, -40], "radius": 2},
    "sahir_manuscript": {"pos": [15, 0, -43], "radius": 1},
    "map_view": {"pos": [0, 0, -26], "radius": 3},
    "sahir_map": {"pos": [3, 0, -27], "radius": 1},
    "study": {"pos": [0, 0, -52], "radius": 3},
    "sahir_study": {"pos": [-3, 0, -53], "radius": 1},
    "valley_mark": {"pos": on_map(VALLEY, 0.3), "radius": 1},
    # Scholars.
    "scholar_stacks_a": {"pos": [-14, 0, -49], "radius": 1},
    "scholar_stacks_b": {"pos": [-14, 0, -30], "radius": 1},
    "scholar_map": {"pos": [-8, 0, -48], "radius": 1},
    "scholar_desk": {"pos": [-16, 0, -25], "radius": 1},
    # Citizens about the city.
    "oasis_seller": {"pos": [5, 0, 42], "radius": 1},
    "stall_north": {"pos": [27, 0, -5], "radius": 1},
    "stall_south": {"pos": [41, 0, 5], "radius": 1},
    "plaza_east": {"pos": [12, 0, -6], "radius": 1},
    "market_mid": {"pos": [32, 0, 0], "radius": 1},
    "oasis_west": {"pos": [-12, 0, 30], "radius": 1},
    "avenue_west": {"pos": [-40, 0, 1], "radius": 1},
    "plaza_sw": {"pos": [-12, 0, 5], "radius": 1},
    "portico_side": {"pos": [7, 0, -17], "radius": 1},
    "gate_guard": {"pos": [-52, 0, 4], "radius": 1},
    "oasis_garden": {"pos": [-6, 0, 47], "radius": 1},
    "market": {"pos": [32, 0, 0], "radius": 6},
}

# Particle points along the six lines, from each emblem towards Awakening Valley.
FRACTIONS = [0.25, 0.5, 0.75]
for i, name in enumerate(EMBLEMS):
    ex, ez = EMBLEMS[name]
    locs["emblem_" + name] = {"pos": on_map((ex, ez), 0.3), "radius": 1}
    for k, f in enumerate(FRACTIONS):
        x = round(ex + (VALLEY[0] - ex) * f, 2)
        z = round(ez + (VALLEY[1] - ez) * f, 2)
        locs["line_%s_%d" % (name, k)] = {"pos": on_map((x, z), 0.3), "radius": 1}

SC1, SC2, SC3 = "solkar_scholar_1", "solkar_scholar_2", "solkar_scholar_3"
C = ["sunspire_citizen_%d" % i for i in range(1, 9)]

npcs = [
    {"character": E, "location": "elara_start", "yaw": -90},
    {"character": O, "location": "orin_start", "yaw": -90},
    {"character": SA, "location": "sahir_hall", "yaw": 0, "held": "minecraft:book"},
    {"character": SC1, "location": "scholar_stacks_a", "yaw": 0, "activity": "routine",
     "route": ["scholar_stacks_a", "scholar_stacks_b"], "held": "minecraft:book"},
    {"character": SC2, "location": "scholar_map", "yaw": 180, "activity": "work", "held": "minecraft:writable_book"},
    {"character": SC3, "location": "scholar_desk", "yaw": 90, "activity": "work", "held": "minecraft:feather"},
    {"character": C[0], "location": "oasis_seller", "yaw": 180, "held": "minecraft:glass_bottle"},
    {"character": C[1], "location": "stall_north", "yaw": 0, "activity": "work", "held": "minecraft:melon_slice"},
    {"character": C[2], "location": "stall_south", "yaw": 180, "activity": "work", "held": "minecraft:bread"},
    {"character": C[3], "location": "plaza_east", "yaw": 90, "activity": "routine",
     "route": ["plaza_east", "market_mid", "oasis_west"]},
    {"character": C[4], "location": "avenue_west", "yaw": 90, "activity": "patrol", "route": ["avenue_west", "plaza_sw"]},
    {"character": C[5], "location": "portico_side", "yaw": 0},
    {"character": C[6], "location": "gate_guard", "yaw": -90, "held": "minecraft:iron_sword"},
    {"character": C[7], "location": "oasis_garden", "yaw": 180, "activity": "work", "held": "minecraft:wooden_hoe"},
]

GLYPHS = {  # word: (clue id, z on the east wall x 21, block)
    "open": ("s14_glyph_open", -48, "minecraft:chiseled_tuff"),
    "circle": ("s14_glyph_circle", -42, "minecraft:chiseled_tuff_bricks"),
    "only": ("s14_glyph_only", -45, "minecraft:chiseled_deepslate"),
    "hour": ("s14_glyph_hour", -51, "minecraft:chiseled_polished_blackstone"),
}

clues = {
    "s14_cat_1": {"name": "Catalogue entry: Ashar-Kel, the sun needle of Solkar", "pos": [-17, 0, -51], "block": "minecraft:lectern"},
    "s14_cat_2": {"name": "Catalogue entry: Vey-Orun, the ember gate of Veyrath", "pos": [-17, 0, -43], "block": "minecraft:lectern"},
    "s14_cat_3": {"name": "Catalogue entry: Thal-Mere, the tide stair of Thalassia", "pos": [-17, 0, -35], "block": "minecraft:lectern"},
    "s14_warning": {"name": "Water-stained warning page", "pos": [18, 0, -33], "block": "minecraft:lectern"},
    "s14_manuscript": {"name": "Survey markings and Sahir's manuscript", "pos": [14, 0, -40], "block": "minecraft:lectern"},
    "s14_crate": {"name": "Sealed crate from the Eldervale road", "pos": [3, 0, -54], "block": "minecraft:barrel"},
}
for word, (cid, z, block) in GLYPHS.items():
    clues[cid] = {"name": "Builder glyph: " + word, "pos": [21, 1, z], "block": block}
SEQUENCE = ",".join(GLYPHS[w][0] for w in ("open", "circle", "only", "hour"))

light_value = "light|" + ";".join("%d,%d,%d" % tuple(p) for p in LIGHT_SPOTS)

scene = {
    "id": "mc001_s014", "title": "The Records of Sunspire", "universe": "MC-001", "saga": 1, "scene": 14,
    "canon_status": CANON_S14 + " The catalogue, glyph, map and crate objectives follow the screenplay's listed objectives.",
    "dimension": DIM, "site": "sunspire", "anchor": [520, 68, 420],
    "arrival": {"pos": [-72, 0, 0], "yaw": -90},
    "next_scene": "unreleased:mc001_s015",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "arrive", "checkpoint": "gate_road",
         "on_enter": [{"type": "teleport_player", "location": "gate_road", "yaw": -90},
                      *stay(E, "elara_start"), *stay(O, "orin_start"), *follow(E, O),
                      cinematic("mc001_s014_arrival")],
         "complete": {"type": "cinematic_end", "value": "mc001_s014_arrival"},
         "on_complete": [dialogue("s14_arrival")]},
        {"id": "arrival_talk", "checkpoint": "gate_road",
         "complete": {"type": "dialogue_end", "value": "s14_arrival"}},
        {"id": "to_records", "objective": "Meet Sahir at the House of Records", "target": "sahir_meet", "checkpoint": "west_gate",
         "on_enter": [hint("The House of Records faces the great tower across the plaza. Follow the avenue east from the gate, "
                           "then turn north at the tower.", 220)],
         "complete": {"type": "reach", "value": "sahir_meet"},
         "on_complete": [dialogue("s14_sahir")]},
        {"id": "sahir_talk", "checkpoint": "records_hall",
         "complete": {"type": "dialogue_end", "value": "s14_sahir"},
         "on_complete": [*stay(SA, "sahir_stacks")]},
        {"id": "catalogue", "objective": "Find three catalogue entries in the stacks", "target": "stacks", "checkpoint": "records_hall",
         "on_enter": [hint("The archive stacks fill the west side of the hall. Three catalogue lecterns stand in the aisles; "
                           "press {use} on each. The sign above each one gives the landmark's old name.", 220)],
         "complete": {"type": "clues", "value": "s14_cat_1,s14_cat_2,s14_cat_3"},
         "on_complete": [give("minecraft:paper", 1, "Catalogue notes",
                              "Ashar-Kel: the sun needle of Solkar.|Vey-Orun: the ember gate of Veyrath.|"
                              "Thal-Mere: the tide stair of Thalassia."),
                         dialogue("s14_records")]},
        {"id": "records_talk", "checkpoint": "records_hall",
         "complete": {"type": "dialogue_end", "value": "s14_records"},
         "on_complete": [*stay(SA, "sahir_glyphs")]},
        {"id": "glyphs", "objective": "Decode the Builder inscription", "target": "glyph_wall", "checkpoint": "records_hall",
         "on_enter": [hint("Four Builder glyphs are set in the east wall. The key tablet on the north wall pairs each glyph "
                           "with a word. Press the glyphs in the order that spells the key's sentence.", 240)],
         "on_wrong": [sound("minecraft:block.stone_button.click_off", 1.0, 0.6),
                      sound("minecraft:block.grindstone.use", 0.5, 0.7),
                      hint("The glyphs sink back into the wall. Read the key tablet again: Open the circle only in its hour.", 200)],
         "complete": {"type": "sequence", "value": SEQUENCE},
         "on_complete": [change("mc001_s014_glyphs"), sound("minecraft:block.amethyst_block.resonate", 0.9, 0.7),
                         dialogue("s14_decoded")]},
        {"id": "decoded_talk", "checkpoint": "glyph_wall",
         "complete": {"type": "dialogue_end", "value": "s14_decoded"},
         "on_complete": [*stay(SA, "sahir_warning")]},
        {"id": "warning", "objective": "Read the water-stained warning page", "target": "warning_desk", "checkpoint": "glyph_wall",
         "on_enter": [hint("A page spoiled by an old leak lies on a lectern by the cistern at the east side of the hall. "
                           "Press {use} on it.", 180)],
         "complete": {"type": "clues", "value": "s14_warning"},
         "on_complete": [dialogue("s14_warning_talk")]},
        {"id": "warning_talk", "checkpoint": "warning_desk",
         "complete": {"type": "dialogue_end", "value": "s14_warning_talk"},
         "on_complete": [*stay(SA, "sahir_manuscript")]},
        {"id": "manuscript", "objective": "Compare the survey markings with the manuscript", "target": "manuscript_desk", "checkpoint": "warning_desk",
         "on_enter": [hint("Sahir has laid his manuscript beside the old survey at the east edge of the great map. "
                           "Press {use} on the lectern.", 180)],
         "complete": {"type": "clues", "value": "s14_manuscript"},
         "on_complete": [dialogue("s14_central")]},
        {"id": "central_talk", "checkpoint": "manuscript_desk",
         "complete": {"type": "dialogue_end", "value": "s14_central"},
         "on_complete": [*stay(SA, "sahir_map")]},
        {"id": "map", "objective": "Mark the three catalogued landmarks on the great map", "target": "map_view", "checkpoint": "map_view",
         "on_enter": [give("minecraft:torch", 3, "Archive torch", "For marking the great map."),
                      hint("Three landmarks are already lit. Light the three from the catalogue: the sun of Solkar in the "
                           "south-east, the ember gate of Veyrath in the far west and the tide stair of Thalassia on the "
                           "southern coast. Place a torch on each emblem.", 260)],
         "complete": {"type": "blocks", "value": light_value},
         "on_complete": [sound("minecraft:block.fire.ambient", 1.0, 1.0), cinematic("mc001_s014_alignment")]},
        {"id": "alignment", "checkpoint": "map_view",
         "complete": {"type": "cinematic_end", "value": "mc001_s014_alignment"},
         "on_complete": [change("mc001_s014_map_lines"), dialogue("s14_leads_back")]},
        {"id": "leads_back_talk", "checkpoint": "map_view",
         "complete": {"type": "dialogue_end", "value": "s14_leads_back"},
         "on_complete": [*stay(SA, "sahir_study")]},
        {"id": "crate", "objective": "Open the crate from the Eldervale road in Sahir's study", "target": "study", "checkpoint": "map_view",
         "on_enter": [hint("Sahir's study is behind the great map, at the north end of the hall. The sealed crate the caravan "
                           "saved is inside. Press {use} on it.", 200)],
         "complete": {"type": "clues", "value": "s14_crate"},
         "on_complete": [change("mc001_s014_crate_open"), sound("minecraft:block.barrel.open", 1.0, 0.9),
                         dialogue("s14_crate_talk")]},
        {"id": "crate_talk", "checkpoint": "study",
         "complete": {"type": "dialogue_end", "value": "s14_crate_talk"},
         "on_complete": [
             give("minecraft:chiseled_sandstone", 1, "Solkar fragment",
                  "Solkar's piece, from the sealed crate saved on the Eldervale road."),
             *follow(SA),
             *complete("Mission Complete", "The Sunspire Records", "s14_sunspire_records",
                       "Sahir of Solkar joins you, and Solkar's piece goes with him. The next road leads to Emberhold.",
                       ["mc001.solkar.joined", "mc001.s014.complete"])]},
    ],
    "triggers": [
        {"id": "s14_water_seller", "character": C[0], "dialogue": "s14_city_water", "radius": 7, "talk_radius": 7,
         "approach": False, "ambient": True, "priority": 0},
        {"id": "s14_quiet_stacks", "character": SC1, "dialogue": "s14_city_quiet", "radius": 6, "talk_radius": 6,
         "approach": False, "ambient": True, "priority": 0},
        {"id": "s14_busy_records", "character": C[5], "dialogue": "s14_city_busy", "radius": 7, "talk_radius": 7,
         "approach": False, "ambient": True, "priority": 0},
        {"id": "s14_dates", "character": C[1], "dialogue": "s14_city_dates", "radius": 6, "talk_radius": 6,
         "approach": False, "ambient": True, "priority": 0},
        {"id": "s14_gate_welcome", "character": C[6], "dialogue": "s14_city_gate", "radius": 7, "talk_radius": 7,
         "approach": False, "ambient": True, "priority": 0},
        {"id": "s14_terraces", "character": C[4], "dialogue": "s14_city_terraces", "radius": 6, "talk_radius": 6,
         "approach": False, "ambient": True, "priority": 0},
    ],
    "area_triggers": [
        {"id": "s14_market_hint", "location": "market", "stages": ["to_records"],
         "actions": [hint("The covered market. The House of Records is back the other way: north of the great tower.", 140)]},
    ],
    "chatter": [],
}

dialogues = {
    "s14_arrival": lines(
        (E, "It's so bright."),
        (O, "That tower's been here longer than the city around it, if the stories are right."),
        (S, "Let's find Sahir.")),
    "s14_city_water": lines(("sunspire_citizen_1", "Water from the oasis, a copper a cup!"), critical=False),
    "s14_city_quiet": lines((SC1, "Quiet in the stacks, please. The pages are older than your grandparents."), critical=False),
    "s14_city_busy": lines(("sunspire_citizen_6", "The House of Records hasn't been this busy since the eclipse year."), critical=False),
    # Added ambient lines (not in the screenplay).
    "s14_city_dates": lines(("sunspire_citizen_2", "Dates and figs, sweet as the morning! Try one, traveller."), critical=False),
    "s14_city_gate": lines(("sunspire_citizen_7", "Welcome to Sunspire. Drink before you're thirsty out here."), critical=False),
    "s14_city_terraces": lines(("sunspire_citizen_5", "Mind the terraces at noon. You could bake bread on those roofs."), critical=False),
    "s14_sahir": lines(
        (SA, "I recognised the symbol as soon as the Queen's messenger described it."),
        (S, "You've seen it before?"),
        (SA, "Not with my own eyes. But I know where it's recorded."),
        (SA, "A survey conducted many generations ago mentioned a circle beneath six landmarks."),
        (O, "Six landmarks?"),
        (SA, "Each corresponding to one of the great civilisations.")),
    "s14_records": lines(
        (S, "Why did nobody know about this before?"),
        (SA, "Because the records were incomplete. Some pages were lost, others misunderstood."),
        (E, "Could somebody have hidden them?"),
        (SA, "Possibly. But missing information isn't proof of a conspiracy."),
        (O, "I'm starting to like him."),
        (SA, "Thank you. I think.")),
    "s14_decoded": lines(
        (SA, "'Open the circle only in its hour. What is opened out of its hour will remember the hour it should have kept.'"),
        (O, "Remember? Stone doesn't remember anything."),
        (SA, "The people who wrote this seem to have thought otherwise.")),
    "s14_warning_talk": lines(
        (SA, "This one is worse. 'The deep note holds the world in time. Strike it wrongly, and time will keep two answers.'"),
        (E, "Two answers to what?"),
        (SA, "I wish I knew. The rest of the page is gone."),
        (S, "The deep note... like the humming Liora's rangers heard?"),
        (SA, "Perhaps.")),
    "s14_central": lines(
        (SA, "There. That symbol represents a central structure."),
        (S, "The northern monument?"),
        (SA, "Perhaps. But it appears that the monument is only one part of something much larger.")),
    "s14_leads_back": lines(
        (S, "Everything leads back there."),
        (SA, "Then whatever was hidden beneath the valley was important enough for six civilisations to record.")),
    "s14_crate_talk": lines(
        (SA, "The crate your caravan saved was bound for this house. Solkar's piece."),
        (S, "The raiders were after it."),
        (SA, "My family's oldest book says it was given to us to keep, not to understand. Given by the First Hands, whoever they were."),
        (SA, "Lord Kaelen of Emberhold has refused every letter about this. If Veyrath holds a piece, he won't give it up easily."),
        (S, "Then we'll ask him in person.")),
}

# The six lines light up from the emblems inwards, then meet over Awakening Valley.
line_cues = []
for k in range(len(FRACTIONS)):
    acts = [{"type": "particles", "particle": "minecraft:end_rod", "location": "line_%s_%d" % (n, k), "count": 8, "spread": 0.2}
            for n in EMBLEMS]
    if k == 0:
        acts = [{"type": "particles", "particle": "minecraft:flame", "location": "emblem_" + n, "count": 6, "spread": 0.15}
                for n in EMBLEMS] + acts
    line_cues.append({"t": 40 + 30 * k, "actions": acts})
line_cues.append({"t": 140, "actions": [
    {"type": "particles", "particle": "minecraft:end_rod", "location": "valley_mark", "count": 40, "spread": 0.6},
    change("mc001_s014_map_lines")]})

cinematics = {
    "mc001_s014_arrival": {
        "duration": 320, "fade_in": 20, "fade_out": 12, "hide_player": True,
        "keyframes": [
            key(0, [-85.0, 24.0, 30.0], [0, 40, 0], 62),
            key(110, [-60.0, 32.0, 45.0], [0, 30, 0], 58),
            key(210, [-22.0, 20.0, 50.0], [0, 2, 32], 56),
            key(320, [-66.0, 5.0, 6.0], [0, 22, 0], 60)],
        "subtitles": [{"t": 40, "duration": 110, "text": "Sunspire — City of Solkar", "style": "title"}],
        "sounds": [{"t": 0, "sound": "minecraft:block.sand.step", "volume": 0.4},
                   {"t": 120, "sound": "minecraft:entity.camel.ambient", "volume": 0.3},
                   {"t": 210, "sound": "minecraft:block.water.ambient", "volume": 0.4}],
    },
    "mc001_s014_alignment": {
        "duration": 220, "fade_in": 8, "fade_out": 10, "hide_player": False,
        "keyframes": [
            key(0, [0.0, 8.5, -24.0], [0, -1, -36], 70),
            key(220, [0.5, 8.5, -30.0], [0, -1, -39], 75)],
        "subtitles": [{"t": 30, "duration": 150, "text": "Six lines cross the map and meet near Awakening Valley.", "style": "caption"}],
        "sounds": [{"t": 40, "sound": "minecraft:block.amethyst_block.chime", "volume": 0.8, "pitch": 0.8},
                   {"t": 100, "sound": "minecraft:block.amethyst_block.chime", "volume": 0.8, "pitch": 1.0},
                   {"t": 140, "sound": "minecraft:block.beacon.activate", "volume": 0.6, "pitch": 0.8}],
        "cues": line_cues,
    },
}

journal = {"s14_sunspire_records": {"title": "The Sunspire Records", "text": (
    "Sunspire, the capital of Solkar, stands round a tower older than the city itself. Sahir met us at the House of "
    "Records. An old survey mentions a circle beneath six landmarks, one for each civilisation, and the stacks gave us "
    "the old names of three of them. We decoded a Builder inscription: 'Open the circle only in its hour.' A "
    "water-stained page spoke of a deep note that holds the world in time. The survey's central mark may be the northern "
    "monument, but only as one part of something larger. On the great map six lines met near Awakening Valley. The "
    "sealed crate our caravan saved was bound for Sahir's house: Solkar's piece. Sahir is coming with us. Lord Kaelen of "
    "Emberhold has refused every letter, so we will ask him in person.")}}

write_scene("mc001_s014", "mc001_s013", scene, dialogues, cinematics, journal)
