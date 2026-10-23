"""Writes the Scene 019 bundle (rod-story/scenes/mc001_s019.json): "The Door That Should Not Open".

Dialogue is verbatim from docs/story/SAGA_1_SCENES_012_019_REVISED.md (Version 2), including the six
lines the story owner requires. Hints, captions, objectives, item names and the journal are gameplay
text. Set in the First Gateway chamber beneath the Ancient Northern Monument (anchor MC-001
(40, 108, -200), y = 0 the courtyard); every position must match world/site/GatewayChamber.java.

The divergence is story state only. It is recorded with the flags mc001_first_divergence,
elara_rescued, gateway_temporal_branch_recorded and first_gateway_closed, and the value
primordial_steve_multiversal_travel_unlocked = 0. None of these flags carries the scene prefix,
so replaying the scene never clears them, and setting them again changes nothing. Nothing the
player reads (hints, captions, objectives, journal, titles) names or explains the divergence.
Run from the repository root: python tools/story/build_s019.py
"""
from common import *

RESEARCHERS = ["expedition_researcher_1", "expedition_researcher_2", "expedition_researcher_3"]

locs = {
    "landing": {"pos": [20, -39, -37], "radius": 2},
    "landing_wide": {"pos": [20, -39, -37], "radius": 4},
    "elara_land": {"pos": [19, -39, -38], "radius": 1}, "bram_land": {"pos": [23, -39, -36], "radius": 1},
    "orin_land": {"pos": [14, -39, -38], "radius": 1}, "sahir_land": {"pos": [25, -39, -37], "radius": 1},
    "garrick_land": {"pos": [27, -39, -38], "radius": 1}, "liora_land": {"pos": [11, -39, -37], "radius": 1},
    "kaelen_land": {"pos": [29, -39, -37], "radius": 1},
    "res1_land": {"pos": [16, -39, -36], "radius": 1}, "res2_land": {"pos": [26, -39, -36], "radius": 1},
    "res3_land": {"pos": [12, -39, -36], "radius": 1},
    "elara_console": {"pos": [20, -39, -64], "radius": 1},
    "dais": {"pos": [20, -39, -48], "radius": 3},
    "dais_north": {"pos": [22, -39, -57], "radius": 3},
    "alcove_mouth": {"pos": [20, -39, -60], "radius": 2},
    "bridge_head": {"pos": [20, -39, -45], "radius": 2},
    "stone_2": {"pos": [28, -39, -44], "radius": 2},
    "stone_4": {"pos": [31, -39, -50], "radius": 2},
    "stone_6": {"pos": [28, -39, -56], "radius": 2},
    "pool": {"pos": [20, -54, -42], "radius": 14},
    "hall_entry": {"pos": [20, -39, -29], "radius": 2},
    "mid_stair": {"pos": [20, -20, -8], "radius": 2},
    "gateway": {"pos": [20, -31, -52], "radius": 2},
    "dig_top": {"pos": [20, 0, 14], "radius": 3},
    "elara_top": {"pos": [18, 0, 15], "radius": 1}, "bram_top": {"pos": [22, 0, 16], "radius": 1},
    "orin_top": {"pos": [16, 0, 17], "radius": 1}, "sahir_top": {"pos": [24, 0, 17], "radius": 1},
    "garrick_top": {"pos": [25, 0, 15], "radius": 1}, "kaelen_top": {"pos": [14, 0, 15], "radius": 1},
    "liora_top": {"pos": [19, 0, 18], "radius": 1}, "maren_top": {"pos": [21, 0, 19], "radius": 1},
}

LANDING = [(O, "orin_land"), (G, "garrick_land"), (SA, "sahir_land"), (L, "liora_land"), (K, "kaelen_land"),
           (B, "bram_land"), *[(r, f"res{i + 1}_land") for i, r in enumerate(RESEARCHERS)]]
SURFACE = [(B, "bram_top"), (O, "orin_top"), (SA, "sahir_top"), (G, "garrick_top"), (K, "kaelen_top"), (L, "liora_top"), (M, "maren_top")]

# The scene opens on the landing. Everyone is placed for the whole scene; the evacuation and the
# escape move them with actions.
npcs = [{"character": who, "location": where, "yaw": 180} for who, where in LANDING]
npcs.append({"character": E, "location": "elara_land", "yaw": 180})

clues = {
    "s19_pylon": {"name": "The landing pylon's copper heart", "pos": [13, -38, -37], "block": "minecraft:copper_bulb", "dialogue": "s19_pylon"},
    "s19_console": {"name": "The console at the bridge head", "pos": [22, -39, -38], "block": "minecraft:lodestone", "dialogue": "s19_console"},
    "s19_base": {"name": "Glyphs on the gateway's eastern plinth", "pos": [23, -39, -51], "block": "minecraft:polished_deepslate", "dialogue": "s19_base"},
}

RUBBLE = [[x, y, -61] for y in (-39, -38) for x in (19, 20, 21)]
BRIDGE = [[20, -40, z] for z in (-43, -42, -41, -40)]


def spots(mode, ps):
    return mode + "|" + ";".join(f"{p[0]},{p[1]},{p[2]}" for p in ps)


def say(who, text):
    return {"type": "say", "character": who, "text": text}


def debris(n, who=None, text=None):
    acts = [change(f"mc001_s019_debris_{n}"), {"type": "shake", "intensity": 0.18, "ticks": 25},
            sound("minecraft:block.deepslate.break", 1.0, 0.6)]
    if who:
        acts.append(say(who, text))
    return acts


STORY_STATE = [flag("mc001_first_divergence"), flag("elara_rescued"), flag("gateway_temporal_branch_recorded"),
               flag("first_gateway_closed"),
               {"type": "set_value", "key": "primordial_steve_multiversal_travel_unlocked", "value": 0}]

scene = {
    "id": "mc001_s019", "title": "The Door That Should Not Open", "universe": "MC-001", "saga": 1, "scene": 19,
    "canon_status": ("Saga 1 revised screenplay, Version 2 (docs/story/SAGA_1_SCENES_012_019_REVISED.md): spoken "
                     "dialogue verbatim, including the owner's required lines. Hints, captions, item names and the "
                     "outcome message are gameplay text."),
    "dimension": DIM, "site": "gateway_chamber", "anchor": [40, 108, -200],
    "arrival": {"pos": [20, -39, -37], "yaw": 180},
    "next_scene": "unreleased:mc001_s020",
    "locations": locs, "npcs": npcs, "clues": clues,
    "stages": [
        {"id": "enter", "checkpoint": "landing",
         "on_enter": [{"type": "teleport_player", "location": "landing", "yaw": 180}, cinematic("mc001_s019_enter")],
         "complete": {"type": "cinematic_end", "value": "mc001_s019_enter"}},
        {"id": "investigate", "objective": "Examine the machinery: the landing pylon, the bridge-head console and the gateway's base",
         "target": "landing", "checkpoint": "landing",
         "on_enter": [{"type": "npc_move", "character": E, "location": "elara_console"},
                      hint("Researchers are spreading out. Elara has crossed to the console in the alcove behind the "
                           "gateway. Examine the pylon west of the door, the console at the bridge head, and the "
                           "glyphs on the gateway's eastern plinth across the bridge.", 280)],
         "complete": {"type": "clues", "value": "s19_pylon,s19_console,s19_base"},
         "on_complete": [cinematic("mc001_s019_rings")]},
        {"id": "rings", "checkpoint": "landing",
         "complete": {"type": "cinematic_end", "value": "mc001_s019_rings"},
         "on_complete": [dialogue("s19_rings")]},
        {"id": "rings_talk", "checkpoint": "landing",
         "complete": {"type": "dialogue_end", "value": "s19_rings"},
         "on_complete": [cinematic("mc001_s019_surface")]},
        {"id": "surface", "checkpoint": "landing",
         "complete": {"type": "cinematic_end", "value": "mc001_s019_surface"},
         "on_complete": [dialogue("s19_surface")]},
        {"id": "surface_talk", "checkpoint": "landing",
         "complete": {"type": "dialogue_end", "value": "s19_surface"},
         "on_complete": [cinematic("mc001_s019_collapse")]},
        {"id": "collapse", "checkpoint": "landing",
         "complete": {"type": "cinematic_end", "value": "mc001_s019_collapse"},
         "on_complete": [dialogue("s19_collapse")]},
        {"id": "collapse_talk", "checkpoint": "landing",
         "complete": {"type": "dialogue_end", "value": "s19_collapse"},
         "on_complete": [*[{"type": "npc_move", "character": who, "location": "hall_entry"}
                           for who in [SA, L, K, *RESEARCHERS]]]},
        {"id": "traverse", "objective": "Reach Elara: cross the chasm on the stepping stones", "target": "dais_north",
         "checkpoint": "landing",
         "on_enter": [hint("The bridge is gone. Stepping stones curve round the east side of the chasm, starting from the "
                           "landing's east end. Jump from stone to stone to the dais. If you fall, the pool will catch "
                           "you: climb the ladder at the landing's west end.", 300),
                      {"type": "shake", "intensity": 0.1, "ticks": 40}],
         "complete": {"type": "reach", "value": "dais_north"},
         "on_complete": [sound("minecraft:block.deepslate.step", 1.0, 0.8)]},
        {"id": "clear", "objective": "Clear the fallen stone trapping Elara", "target": "alcove_mouth", "checkpoint": "dais_north",
         "on_enter": [give("minecraft:iron_pickaxe", 1, "Garrick's pick", "Stonecrown steel."),
                      say(E, "Steve! I'm here, behind the stones!"),
                      hint("Break the six fallen stones across the alcove's mouth.", 200)],
         "complete": {"type": "blocks", "value": spots("air", RUBBLE)},
         "on_complete": [dialogue("s19_rescue")]},
        {"id": "rescue_talk", "checkpoint": "dais_north",
         "complete": {"type": "dialogue_end", "value": "s19_rescue"},
         "on_complete": [*follow(E)]},
        {"id": "bridge", "objective": "Rebuild the bridge so Elara can cross back", "target": "bridge_head", "checkpoint": "dais_north",
         "on_enter": [give("minecraft:spruce_planks", 8, "Expedition timber"),
                      hint("Lay a line of timber across the gap, from the stub of the bridge at the dais back to the landing. "
                           "Four marked spots.", 220)],
         "complete": {"type": "blocks", "value": spots("solid", BRIDGE)},
         "on_complete": [sound("minecraft:block.wood.place", 1.0, 0.9)]},
        {"id": "cross", "objective": "Get Elara back to the landing", "target": "landing", "checkpoint": "bridge_head",
         "complete": {"type": "reach", "value": "landing"},
         "on_complete": [cinematic("mc001_s019_stars")]},
        {"id": "stars", "checkpoint": "landing",
         "complete": {"type": "cinematic_end", "value": "mc001_s019_stars"},
         "on_complete": [dialogue("s19_stars")]},
        {"id": "stars_talk", "checkpoint": "landing",
         "complete": {"type": "dialogue_end", "value": "s19_stars"},
         "on_complete": [cinematic("mc001_s019_closing")]},
        {"id": "closing", "checkpoint": "landing",
         "complete": {"type": "cinematic_end", "value": "mc001_s019_closing"},
         "on_complete": [*STORY_STATE, dialogue("s19_after")]},
        {"id": "after_talk", "checkpoint": "landing",
         "complete": {"type": "dialogue_end", "value": "s19_after"},
         "on_complete": [*[a for who, where in SURFACE for a in stay(who, where)],
                         *[{"type": "npc_despawn", "character": r} for r in RESEARCHERS], *follow(E)]},
        {"id": "escape", "objective": "Get everyone out: climb the stair to the surface", "target": "dig_top", "checkpoint": "landing",
         "on_enter": [hint("The others have gone ahead. Take Elara up the stair to the camp.", 160)],
         "complete": {"type": "reach", "value": "dig_top"},
         "on_complete": [*stay(E, "elara_top"), dialogue("s19_aftermath")]},
        {"id": "aftermath", "checkpoint": "dig_top",
         "complete": {"type": "dialogue_end", "value": "s19_aftermath"},
         "on_complete": [cinematic("mc001_s019_end")]},
        {"id": "end", "checkpoint": "dig_top",
         "complete": {"type": "cinematic_end", "value": "mc001_s019_end"},
         "on_complete": [*STORY_STATE,
                         *complete("Quest Complete", "An Impossible Door", "s19_journal",
                                   "The gateway beneath the monument has closed. Everyone made it out.",
                                   ["mc001.s019.complete"])]},
    ],
    "triggers": [],
    "area_triggers": [
        {"id": "s19_debris_1", "location": "stone_2", "stages": ["traverse"],
         "actions": debris(1, O, "Keep to the stone! The rings will hold you!")},
        {"id": "s19_debris_2", "location": "stone_4", "stages": ["traverse"], "actions": debris(2)},
        {"id": "s19_debris_3", "location": "stone_6", "stages": ["traverse"], "actions": debris(3, B, "Steve! Get them out!")},
        {"id": "s19_fell", "location": "pool", "stages": ["traverse"],
         "actions": [hint("You've fallen into the pool. Swim to the ladder at the landing's west end and climb back up.", 200)]},
        {"id": "s19_stair_tremor", "location": "mid_stair", "stages": ["escape"],
         "actions": [{"type": "shake", "intensity": 0.08, "ticks": 30}, sound("minecraft:block.gravel.fall", 0.8, 0.6)]},
    ],
    "chatter": [],
}

dialogues = {
    "s19_pylon": lines((O, "These channels feed the ring. Every one of them.")),
    "s19_console": lines((G, "The bridge was built to carry weight. Something heavy was moved across it once.")),
    "s19_base": lines((SA, "Same glyphs as the Sunspire wall. 'In its hour.'")),
    "s19_rings": lines(
        (O, "Nobody touch anything."), (B, "Nobody is touching anything!"),
        (SA, "The structure is responding to something."), (S, "The compass?"),
        (O, "I don't think the compass is causing this.")),
    "s19_surface": lines(
        (S, "What... is that?"), (O, "I don't know."),
        (SA, "None of the records described anything like this."), (E, "Steve, we have to leave."),
        (S, "I can see something."), (E, "Steve!")),
    "s19_collapse": lines(
        (E, "Steve! The bridge is collapsing!"), (O, "The gateway's overloading! We need to evacuate!"),
        (S, "Elara, I'm coming!"), (E, "No! You'll get trapped too!"), (S, "I'm not leaving you!"),
        (B, "Come on, everyone! Move!"), (L, "The upper passage is clear!"),
        (G, "My engineers will hold the supports!")),
    "s19_rescue": lines((S, "Take my hand!"), (E, "The floor's going!"), (S, "I've got you.")),
    "s19_stars": lines((S, "Those stars..."), (SA, "What about them?"), (S, "I've never seen them before.")),
    "s19_after": lines((E, "Steve? Are you alright?"), (S, "Yeah... I think so.")),
    "s19_aftermath": lines(
        (B, "Everyone still alive?"), (O, "I think so."), (S, "What did we just see?"),
        (SA, "Something our ancestors may have understood."), (G, "Then why did they seal it?"),
        (K, "Perhaps because they knew exactly what would happen if it opened."),
        (S, "Or because they were afraid of something they couldn't explain."),
        (L, "Either way, we cannot pretend this didn't happen.")),
}

cinematics = {
    "mc001_s019_enter": {
        "duration": 200, "fade_in": 15, "fade_out": 10, "hide_player": True,
        "keyframes": [key(0, [20.0, -37.0, -34.0], [20, -31, -52], 66),
                      key(110, [12.0, -34.0, -40.0], [20, -32, -52], 60),
                      key(200, [28.0, -33.0, -42.0], [20, -32, -52], 56)],
        "subtitles": [{"t": 10, "duration": 100, "text": "The First Gateway chamber", "style": "caption"},
                      {"t": 115, "duration": 80, "text": "Quest: An Impossible Door", "style": "title"}],
        "sounds": [{"t": 0, "sound": "minecraft:ambient.cave", "volume": 0.6, "pitch": 0.8},
                   {"t": 60, "sound": "minecraft:block.beacon.ambient", "volume": 0.5, "pitch": 0.5}],
    },
    "mc001_s019_rings": {
        "duration": 180, "fade_in": 8, "fade_out": 8, "hide_player": False,
        "keyframes": [key(0, [20.0, -36.5, -38.0], [20, -31, -52], 62),
                      key(180, [20.0, -35.0, -41.0], [20, -31, -52], 52)],
        "shakes": [{"t": 40, "duration": 60, "intensity": 0.12}],
        "sounds": [{"t": 20, "sound": "minecraft:block.grindstone.use", "volume": 1.0, "pitch": 0.4},
                   {"t": 50, "sound": "minecraft:block.beacon.activate", "volume": 1.0, "pitch": 0.5},
                   {"t": 120, "sound": "minecraft:block.amethyst_block.resonate", "volume": 1.0, "pitch": 0.6}],
        "subtitles": [{"t": 30, "duration": 120, "text": "The inner ring begins to turn. Light runs along the channels.", "style": "caption"}],
        "cues": [{"t": 2, "actions": [{"type": "teleport_player", "location": "landing", "yaw": 180}]},
                 {"t": 50, "actions": [change("mc001_s019_rings")]}],
    },
    "mc001_s019_surface": {
        "duration": 160, "fade_in": 6, "fade_out": 8, "hide_player": False,
        "keyframes": [key(0, [20.0, -35.5, -41.0], [20, -31, -52], 55),
                      key(160, [20.0, -33.5, -46.0], [20, -31, -52], 44)],
        "sounds": [{"t": 15, "sound": "minecraft:block.portal.trigger", "volume": 0.5, "pitch": 1.6},
                   {"t": 30, "sound": "minecraft:block.end_portal.spawn", "volume": 0.6, "pitch": 1.2}],
        "subtitles": [{"t": 30, "duration": 110, "text": "A luminous surface forms inside the ring.", "style": "caption"}],
        "cues": [{"t": 30, "actions": [change("mc001_s019_portal_on"),
                                        {"type": "particles", "particle": "minecraft:end_rod", "location": "gateway", "count": 40, "spread": 4}]}],
    },
    "mc001_s019_collapse": {
        "duration": 150, "fade_in": 4, "fade_out": 8, "hide_player": False,
        "keyframes": [key(0, [20.0, -36.0, -38.5], [20, -40, -42], 64),
                      key(80, [17.0, -35.0, -39.0], [20, -38, -50], 66),
                      key(150, [21.0, -35.5, -39.0], [20, -38, -62], 58)],
        "shakes": [{"t": 0, "duration": 70, "intensity": 0.45}, {"t": 90, "duration": 40, "intensity": 0.25}],
        "sounds": [{"t": 0, "sound": "minecraft:entity.generic.explode", "volume": 0.7, "pitch": 0.5},
                   {"t": 20, "sound": "minecraft:block.deepslate.break", "volume": 1.0, "pitch": 0.5},
                   {"t": 40, "sound": "minecraft:entity.generic.splash", "volume": 1.0, "pitch": 0.6},
                   {"t": 95, "sound": "minecraft:block.stone.break", "volume": 1.0, "pitch": 0.5}],
        "subtitles": [{"t": 10, "duration": 70, "text": "A pulse from the gateway. The bridge's middle falls into the chasm.", "style": "caption"},
                      {"t": 90, "duration": 55, "text": "Stone comes down across the alcove where Elara is working.", "style": "caption"}],
        "cues": [{"t": 15, "actions": [change("mc001_s019_bridge_falls"),
                                        {"type": "particles", "particle": "minecraft:cloud", "location": "bridge_head", "count": 30, "spread": 2.5}]}],
    },
    "mc001_s019_stars": {
        "duration": 150, "fade_in": 6, "fade_out": 6, "hide_player": False,
        "keyframes": [key(0, [20.0, -36.0, -40.0], [20, -31, -52], 50),
                      key(150, [20.0, -33.0, -46.0], [20, -31, -52], 40)],
        "sounds": [{"t": 10, "sound": "minecraft:block.beacon.ambient", "volume": 1.0, "pitch": 0.6},
                   {"t": 40, "sound": "minecraft:block.amethyst_block.chime", "volume": 0.8, "pitch": 0.5}],
        "subtitles": [{"t": 25, "duration": 110, "text": "In the ring: a horizon of unfamiliar stars.", "style": "caption"}],
        "cues": [{"t": 20, "actions": [change("mc001_s019_portal_stars")]}],
    },
    # The divergence: for a heartbeat the chamber doubles, the bridge both whole and broken, then all
    # of it settles and the gateway closes. No caption says what happened.
    "mc001_s019_closing": {
        "duration": 200, "fade_in": 4, "fade_out": 12, "hide_player": True,
        "keyframes": [key(0, [20.0, -35.5, -40.0], [20, -38, -46], 60),
                      {**key(50, [20.3, -35.4, -40.2], [20, -38, -46], 66), "roll": 2.5},
                      {**key(80, [19.7, -35.6, -40.0], [20, -38, -46], 58), "roll": -2.0},
                      key(130, [20.0, -35.0, -41.0], [20, -32, -52], 52),
                      key(200, [20.0, -34.0, -43.0], [20, -31, -52], 46)],
        "effects": [{"t": 40, "duration": 14, "shader": "realitiesofdreams:shaders/post/divergence.json"},
                    {"t": 60, "duration": 22, "shader": "realitiesofdreams:shaders/post/divergence.json"}],
        "shakes": [{"t": 38, "duration": 46, "intensity": 0.2}, {"t": 140, "duration": 30, "intensity": 0.3}],
        "sounds": [{"t": 30, "sound": "minecraft:block.respawn_anchor.charge", "volume": 0.9, "pitch": 0.5},
                   {"t": 40, "sound": "minecraft:block.amethyst_block.resonate", "volume": 1.0, "pitch": 0.5},
                   {"t": 60, "sound": "minecraft:block.amethyst_block.resonate", "volume": 1.0, "pitch": 0.53},
                   {"t": 140, "sound": "minecraft:block.end_portal_frame.fill", "volume": 1.0, "pitch": 0.5},
                   {"t": 150, "sound": "minecraft:block.beacon.deactivate", "volume": 1.0, "pitch": 0.6}],
        "subtitles": [{"t": 140, "duration": 55, "text": "The surface collapses inward and is gone.", "style": "caption"}],
        "cues": [{"t": 42, "actions": [change("mc001_s019_echo_1")]},
                 {"t": 52, "actions": [change("mc001_s019_echo_2")]},
                 {"t": 64, "actions": [change("mc001_s019_echo_3")]},
                 {"t": 80, "actions": [change("mc001_s019_echo_4")]},
                 {"t": 85, "actions": [change("mc001_first_divergence")]},
                 {"t": 145, "actions": [change("mc001_s019_portal_off"),
                                         {"type": "particles", "particle": "minecraft:reverse_portal", "location": "gateway", "count": 60, "spread": 3}]}],
    },
    "mc001_s019_end": {
        "duration": 200, "fade_in": 20, "fade_out": 30, "hide_player": True, "hold_black": 40,
        "keyframes": [key(0, [20.0, -36.0, -40.0], [20, -24, -52], 55),
                      key(170, [20.0, -30.0, -46.0], [20, -24, -52], 40)],
        "dips": [{"t": 170, "duration": 30}],
        "sounds": [{"t": 60, "sound": "minecraft:block.beacon.ambient", "volume": 0.4, "pitch": 0.5},
                   {"t": 120, "sound": "minecraft:block.piston.contract", "volume": 0.5, "pitch": 0.3}],
        "subtitles": [{"t": 50, "duration": 60, "text": "The keystone pulses once, almost too faint to hear.", "style": "caption"},
                      {"t": 115, "duration": 50, "text": "Deep beneath the chamber, something answers.", "style": "caption"}],
        "cues": [{"t": 60, "actions": [{"type": "particles", "particle": "minecraft:glow", "location": "gateway", "count": 8, "spread": 0.5}]}],
    },
}

journal = {"s19_journal": {"title": "The Door That Should Not Open", "text": (
    "The gateway woke while we were all in the chamber. The rings turned, and a bright surface filled the ring. "
    "Then the bridge fell and stone came down over Elara in the alcove behind it. I crossed on the stepping "
    "stones, dug her out and laid timber where the bridge had been. When we reached the landing the ring was "
    "full of stars none of us had ever seen. Then it closed, and only a faint blue light stayed in the keystone. "
    "Everyone made it out. Nobody can say what we saw.")}}

write_scene("mc001_s019", "mc001_s018", scene, dialogues, cinematics, journal)
