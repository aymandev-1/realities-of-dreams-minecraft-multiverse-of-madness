"""Builds the character registry, normalised textures and generated TMMA skins.

Usage (from the repo root):  python3 tools/skins/build_registry.py
Inputs:  art/skins/source/**  (the owner's original skins, never modified)
         tools/skins/character_seed.py
Outputs: src/main/resources/assets/realitiesofdreams/textures/entity/character/*.png
         src/main/resources/rod-story/characters.json
         docs/CHARACTER_REGISTRY.md, docs/images/*.png
"""
import json
import os
import sys

from PIL import Image, ImageDraw

sys.path.insert(0, os.path.dirname(__file__))
import character_seed as seed  # noqa: E402
import tmma_faces  # noqa: E402
import npc_types  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
SRC = os.path.join(ROOT, "art", "skins", "source")
TEX = os.path.join(ROOT, "src", "main", "resources", "assets", "realitiesofdreams", "textures", "entity", "character")
REG = os.path.join(ROOT, "src", "main", "resources", "rod-story", "characters.json")
DOC = os.path.join(ROOT, "docs", "CHARACTER_REGISTRY.md")
IMG = os.path.join(ROOT, "docs", "images")


def copy_rect(img, sx, sy, dx, dy, w, h, mirror_x):
    region = img.crop((sx, sy, sx + w, sy + h))
    if mirror_x:
        region = region.transpose(Image.FLIP_LEFT_RIGHT)
    img.paste(region, (sx + dx, sy + dy))


def legacy_to_modern(old):
    """Same remapping Minecraft applies to 64x32 skins (HttpTexture.processLegacySkin)."""
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    img.paste(old, (0, 0))
    for args in [(4, 16, 16, 32, 4, 4), (8, 16, 16, 32, 4, 4), (0, 20, 24, 32, 4, 12), (4, 20, 16, 32, 4, 12),
                 (8, 20, 8, 32, 4, 12), (12, 20, 16, 32, 4, 12), (44, 16, -8, 32, 4, 4), (48, 16, -8, 32, 4, 4),
                 (40, 20, 0, 32, 4, 12), (44, 20, -8, 32, 4, 12), (48, 20, -16, 32, 4, 12), (52, 20, -8, 32, 4, 12)]:
        copy_rect(img, *args, True)
    return img


def is_slim(img):
    return all(img.getpixel((x, y))[3] == 0 for x in (54, 55) for y in range(20, 32))


def set_opaque(img, box):
    x0, y0, x1, y1 = box
    for x in range(x0, x1):
        for y in range(y0, y1):
            r, g, b, a = img.getpixel((x, y))
            img.putpixel((x, y), (r, g, b, 255))


def normalise(path):
    raw = Image.open(path).convert("RGBA")
    legacy = raw.size == (64, 32)
    img = legacy_to_modern(raw) if legacy else raw.copy()
    if img.size != (64, 64):
        raise ValueError(f"{path}: unsupported skin size {raw.size}")
    slim = (not legacy) and is_slim(img)
    if legacy:
        # Legacy skins with a fully opaque hat layer render it as transparent (Notch hack).
        hat = img.crop((32, 0, 64, 16))
        if all(p[3] == 255 for p in hat.get_flattened_data()):
            img.paste(Image.new("RGBA", (32, 16), (0, 0, 0, 0)), (32, 0))
    for box in ((0, 0, 32, 16), (0, 16, 64, 32), (16, 48, 48, 64)):
        set_opaque(img, box)
    return img, slim, legacy


def front_preview(img, slim):
    c = Image.new("RGBA", (16, 32), (0, 0, 0, 0))

    def p(box, dst):
        c.alpha_composite(img.crop(box), dst)

    aw = 3 if slim else 4
    p((8, 8, 16, 16), (4, 0)); p((40, 8, 48, 16), (4, 0))
    p((20, 20, 28, 32), (4, 8)); p((20, 36, 28, 48), (4, 8))
    p((44, 20, 44 + aw, 32), (4 - aw, 8)); p((44, 36, 44 + aw, 48), (4 - aw, 8))
    p((36, 52, 36 + aw, 64), (12, 8)); p((52, 52, 52 + aw, 64), (12, 8))
    p((4, 20, 8, 32), (4, 20)); p((4, 36, 8, 48), (4, 20))
    p((20, 52, 24, 64), (8, 20)); p((4, 52, 8, 64), (8, 20))
    return c


def contact_sheet(entries, out, cols=10):
    scale, cw, ch = 6, 16 * 6 + 24, 32 * 6 + 34
    rows = (len(entries) + cols - 1) // cols
    sheet = Image.new("RGB", (cols * cw, rows * ch), (40, 42, 48))
    d = ImageDraw.Draw(sheet)
    for i, (label, img, slim) in enumerate(entries):
        x, y = (i % cols) * cw, (i // cols) * ch
        fr = front_preview(img, slim).resize((16 * scale, 32 * scale), Image.NEAREST)
        sheet.paste(fr, (x + 12, y + 4), fr)
        d.text((x + 3, y + 32 * scale + 8), label[:19], fill=(235, 235, 235))
    sheet.save(out)


def main():
    os.makedirs(TEX, exist_ok=True)
    os.makedirs(IMG, exist_ok=True)
    os.makedirs(os.path.dirname(REG), exist_ok=True)
    registry, sheet, textures = [], [], {}
    for cid, name, universe, group, faction, role, source, notes in seed.CHARACTERS:
        entry = {"id": cid, "name": name, "universe": universe, "group": group, "faction": faction,
                 "role": role, "notes": notes, "generated": False}
        if source is None:
            entry.update({"entity": "minecraft:pig", "texture": None, "model": None, "source": None})
        else:
            img, slim, legacy = normalise(os.path.join(SRC, source))
            img.save(os.path.join(TEX, cid + ".png"))
            textures[cid] = img
            entry.update({"entity": "realitiesofdreams:story_npc", "texture": f"realitiesofdreams:textures/entity/character/{cid}.png",
                          "model": "slim" if slim else "wide", "source": "art/skins/source/" + source, "legacy_source": legacy})
            sheet.append((name, img, slim))
        if cid in seed.SPEAKER_NAMES:
            entry["speaker_name"] = seed.SPEAKER_NAMES[cid]
        if cid in seed.SAGA1_CAST:
            entry["faction"], scenes = seed.SAGA1_CAST[cid]
            entry.update({"sagas": [1], "scenes": {"1": scenes}})
        else:
            entry["sagas"] = []  # not cast in a written saga yet, so no scene may use them
        registry.append(entry)

    variants = []
    for template_id, cfg in seed.TMMA_TEMPLATES.items():
        base = next(e for e in registry if e["id"] == template_id)
        for n in range(1, seed.VARIANTS_PER_TEMPLATE + 1):
            vid = f"{template_id}_v{n}"
            img, traits = tmma_faces.reface(textures[template_id], f"{vid}", **cfg)
            tmma_faces.assert_uniform_unchanged(textures[template_id], img)
            img.save(os.path.join(TEX, vid + ".png"))
            entry = dict(base)
            entry.update({"id": vid, "name": f"{base['name']} #{n}", "generated": True, "template": template_id,
                          "texture": f"realitiesofdreams:textures/entity/character/{vid}.png",
                          "notes": "Generated from the " + base["name"] + " uniform: " + traits + ". Uniform pixels are identical to the template.",
                          "source": None})
            registry.append(entry)
            variants.append((entry["name"], img, base["model"] == "slim"))

    folk, seen = [], set()
    for type_id, display, speaker, faction, count, scenes, role in seed.NPC_TYPES:
        for n in range(1, count + 1):
            cid = f"{type_id}_{n}"
            slim = n % 3 == 0  # every third member has the slim build, for body variety
            img, traits, template, skin_cells, _ = npc_types.make(type_id, cid, slim, seen)
            npc_types.check_shared_outfit(template, skin_cells, img)
            img.save(os.path.join(TEX, cid + ".png"))
            registry.append({"id": cid, "name": f"{display} #{n}", "speaker_name": speaker, "universe": "MC-001",
                             "group": "MC-001 Saga 1 NPCs", "faction": faction, "role": role, "npc_type": type_id,
                             "notes": "Generated NPC appearance: " + traits + ". Outfit shared by every " + display + ".",
                             "generated": True, "entity": "realitiesofdreams:story_npc",
                             "texture": f"realitiesofdreams:textures/entity/character/{cid}.png",
                             "model": "slim" if slim else "wide", "source": None, "sagas": [1], "scenes": {"1": scenes}})
            folk.append((f"{speaker} {n}", img, slim))

    with open(REG, "w", encoding="utf-8") as f:
        json.dump({"schema": 1, "characters": registry}, f, indent=2, ensure_ascii=False)
        f.write("\n")
    contact_sheet(sheet, os.path.join(IMG, "character_sheet.png"))
    contact_sheet(variants, os.path.join(IMG, "tmma_generated_sheet.png"))
    contact_sheet(folk, os.path.join(IMG, "saga1_npc_sheet.png"), cols=11)
    write_doc(registry)
    print(f"{len(registry)} registry entries ({len(variants)} generated TMMA variants, {len(folk)} generated Saga 1 NPCs)")


def validate_named(cid, source):
    """Checks one of the owner's skins: size, model, base-layer holes, overlay use, and that the game
    texture keeps every pixel of the original (only base-layer alpha is forced opaque, as Minecraft does)."""
    raw = Image.open(os.path.join(SRC, source))
    fmt = f"{raw.format} {raw.mode} {raw.size[0]}x{raw.size[1]}"
    orig = raw.convert("RGBA")
    tex = Image.open(os.path.join(TEX, cid + ".png")).convert("RGBA")
    slim = is_slim(orig)
    base = [(px, py) for limb in ("head", "body", "right_arm", "left_arm", "right_leg", "left_leg")
            for *_, px, py in npc_types.cells(limb, slim)]
    over = [(px, py) for limb in ("head", "body", "right_arm", "left_arm", "right_leg", "left_leg")
            for *_, px, py in npc_types.cells(limb, slim, overlay=True)]
    holes = sum(1 for p in base if orig.getpixel(p)[3] < 255)
    changed = sum(1 for p in base if orig.getpixel(p)[:3] != tex.getpixel(p)[:3]) + sum(1 for p in over if orig.getpixel(p) != tex.getpixel(p))
    overlay = sum(1 for p in over if orig.getpixel(p)[3] > 0) / len(over)
    return fmt, "slim" if slim else "wide", holes, overlay, changed


def saga1_cast_section(registry):
    lines = ["", "## Saga 1 cast (MC-001)", "",
             "Saga 1 scenes may only use characters cast in Saga 1. The story validator rejects any other character, "
             "so later-saga characters and Primordial Steve's Empowered form cannot appear in the opening story.", "",
             "Your 11 named skins are imported from `art/skins/source/` (byte-identical copies of the collection, never modified). "
             "Each was checked: the game texture keeps every original pixel, and only fully hidden base-layer gaps are made opaque, as Minecraft does for player skins.", "",
             "| Character | Faction | Saga 1 scenes | File | Model | Base-layer gaps | Overlay used | Pixels changed |",
             "|---|---|---|---|---|---|---|---|"]
    for e in registry:
        if e["id"] in seed.SAGA1_CAST:
            fmt, model, holes, overlay, changed = validate_named(e["id"], e["source"].split("source/", 1)[1])
            scenes = e["scenes"]["1"]
            sc = "001–020" if scenes == list(range(1, 21)) else ", ".join(f"{n:03d}" for n in scenes)
            gaps = "none" if holes == 0 else f"{holes} (body underside, rendered black as in vanilla)"
            lines.append(f"| {e['name']} | {e['faction']} | {sc} | {fmt} | {model} | {gaps} | {overlay:.0%} | {changed} |")
    lines += ["", "Primordial Steve keeps his normal classic Steve skin throughout Saga 1. `primordial_steve_empowered` is registered but not cast in any saga yet."]
    return lines


def saga1_npc_section():
    lines = ["", "Every member of a type wears the same outfit, pixel for pixel (checked when the textures are built). "
             "Faces, skin tones, hair, builds and identifying features differ, and no two generated NPCs share a face. "
             "Faction colours follow each civilisation's named leader in your collection.", "",
             "| Type | Speaker | Faction | Universe | Count | Saga 1 scenes | Headwear options |", "|---|---|---|---|---|---|---|"]
    for type_id, display, speaker, faction, count, scenes, role in seed.NPC_TYPES:
        hats = ", ".join(sorted({h.replace("_", " ") if h else "bare head" for h in npc_types.HEADWEAR[type_id]}))
        lines.append(f"| {display} (`{type_id}_N`) | {speaker} | {faction} | MC-001 | {count} | {', '.join(f'{n:03d}' for n in scenes)} | {hats} |")
    lines += ["", "![Generated Saga 1 NPCs](images/saga1_npc_sheet.png)"]
    return lines


def write_doc(registry):
    lines = ["# Character Registry", "",
             "Generated by `tools/skins/build_registry.py` from `tools/skins/character_seed.py`. Edit the seed, not this file.", "",
             "The game loads the same data from `src/main/resources/rod-story/characters.json`.", "",
             "![Collected skins](images/character_sheet.png)", "",
             "## Identity corrections applied", "",
             "- **Jesse** belongs to **MC-200** (Minecraft: Story Mode).",
             "- **Movie Steve** belongs to **MC-300**.",
             "- **Reuben** is **Jesse's pig** (MC-200). He uses the vanilla pig model, not a player skin.", "",
             ] + saga1_cast_section(registry) + ["", "## Open questions for the story owner", ""]
    for e in registry:
        if not e["generated"] and any(k in e["notes"] for k in ("IDENTICAL", "confirm", "not stated", "temporary", "licence")):
            lines.append(f"- **{e['name']}** (`{e['id']}`): {e['notes']}")
    groups = {}
    for e in registry:
        key = e["group"] if "Saga 1 NPCs" in e["group"] else "Generated TMMA personnel" if e["generated"] else e["group"]
        groups.setdefault(key, []).append(e)
    for g, items in groups.items():
        lines += ["", f"## {g} ({len(items)})", "", "| ID | Name | Universe | Faction | Model | Notes |", "|---|---|---|---|---|---|"]
        for e in items:
            notes = " ".join(x for x in (e["role"], e["notes"]) if x).replace("|", "/")
            lines.append(f"| `{e['id']}` | {e['name']} | {e['universe'] or '—'} | {e['faction']} | {e['model'] or 'pig'} | {notes} |")
        if g.startswith("Generated"):
            lines += ["", "![Generated TMMA skins](images/tmma_generated_sheet.png)"]
        if "Saga 1 NPCs" in g:
            lines += saga1_npc_section()
    lines.append("")
    with open(DOC, "w", encoding="utf-8") as f:
        f.write("\n".join(lines))


if __name__ == "__main__":
    main()
