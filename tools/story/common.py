"""Shared helpers for the scene build scripts (Scenes 010 on).

Each script describes one scene and calls write_scene(), which writes the bundle, points the
previous scene's next_scene at it and lists it in the index.
"""
import json
import math

INDEX = "src/main/resources/rod-story/index.json"
SCENES = "src/main/resources/rod-story/scenes/"
DIM = "realitiesofdreams:mc_001"
CANON = ("Saga 1 screenplay, Version 1 (docs/story/SAGA_1_SCREENPLAY.md): spoken dialogue verbatim. "
         "Hints, captions, item names and the outcome message are new gameplay text.")

S, E, B, O, R = "primordial_steve", "elara", "bram", "orin", "elder_rowan"
L, AV, G, SA, K, M = "liora", "queen_aveline", "thane_garrick", "sahir", "lord_kaelen", "captain_maren"


def stay(who, where):
    return [{"type": "npc_spawn", "character": who, "location": where},
            {"type": "npc_stay", "character": who, "location": where}]


def follow(*who):
    return [{"type": "npc_follow", "character": w} for w in who]


def hint(text, ticks=200):
    return {"type": "hint", "ticks": ticks, "text": text}


def dialogue(id_):
    return {"type": "start_dialogue", "id": id_}


def cinematic(id_):
    return {"type": "start_cinematic", "id": id_}


def change(id_):
    return {"type": "world_change", "id": id_}


def give(item, count=1, name=None, lore=None):
    a = {"type": "give_item", "item": item, "count": count}
    if name:
        a["name"] = name
    if lore:
        a["lore"] = lore
    return a


def sound(s, volume=1.0, pitch=1.0):
    return {"type": "sound", "sound": s, "volume": volume, "pitch": pitch}


def flag(f):
    return {"type": "set_flag", "flag": f}


def complete(title, subtitle, journal, message, flags):
    return [{"type": "title", "title": title, "subtitle": subtitle},
            sound("minecraft:ui.toast.challenge_complete", 0.7),
            {"type": "journal", "id": journal},
            {"type": "message", "text": message},
            *[flag(f) for f in flags]]


def lines(*ls, critical=True):
    """A conversation that runs straight through, line after line."""
    out = {}
    for i, entry in enumerate(ls):
        speaker, text = entry[0], entry[1]
        d = {"speaker": speaker, "text": text}
        if len(entry) > 2:
            d["actions"] = entry[2]
        if i + 1 < len(ls):
            d["next"] = "l%02d" % (i + 1)
        out["l%02d" % i] = d
    return {"critical": critical, "lines": out}


def look(frm, to):
    """Camera yaw and pitch (Minecraft degrees) from one point to another."""
    dx, dy, dz = to[0] - frm[0], to[1] - frm[1], to[2] - frm[2]
    yaw = -math.degrees(math.atan2(dx, dz))
    pitch = -math.degrees(math.atan2(dy, math.hypot(dx, dz)))
    return round(yaw, 1), round(pitch, 1)


def key(t, pos, at, fov=60):
    yaw, pitch = look(pos, at)
    return {"t": t, "pos": list(pos), "yaw": yaw, "pitch": pitch, "fov": fov}


def write_scene(scene_id, previous, scene, dialogues, cinematics, journal):
    path = SCENES + scene_id + ".json"
    json.dump({"scene": scene, "dialogues": dialogues, "cinematics": cinematics, "journal": journal},
              open(path, "w", encoding="utf8"), indent=2, ensure_ascii=False)
    import os
    if previous and os.path.exists(SCENES + previous + ".json"):
        prev_path = SCENES + previous + ".json"
        prev = json.load(open(prev_path, encoding="utf8"))
        prev["scene"]["next_scene"] = scene_id
        json.dump(prev, open(prev_path, "w", encoding="utf8"), indent=2, ensure_ascii=False)
    index = json.load(open(INDEX))
    if scene_id + ".json" not in index["scenes"]:
        index["scenes"].append(scene_id + ".json")
        index["scenes"].sort()
    json.dump(index, open(INDEX, "w"), indent=2)
    print("wrote", path)
