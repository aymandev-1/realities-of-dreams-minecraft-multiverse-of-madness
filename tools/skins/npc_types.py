"""Generated appearances for the unnamed Saga 1 NPCs (villagers, guards, rangers, sailors and so on).

Every NPC type has one shared outfit, painted once from the type's key, so all members of a type
wear pixel-identical clothing. Each individual then gets their own skin tone, face, hair and
identifying features (scar, eyepatch, earring, moustache). check_shared_outfit() proves the outfit
is identical across a type, and build() guarantees no two generated NPCs share a face.

Faction colours are taken from the named leader's skin in the owner's collection: Eldervale from
Queen Aveline (black, silver, white), Stonecrown from Thane Garrick (iron and charcoal), Sylvaris from
Liora (dark leather with teal), Solkar from Sahir (sand robes, black sash), Veyrath from Lord Kaelen
(royal blue with gold) and Thalassia from Captain Maren (black and gold, navy). Firstbrook follows
Bram, Elara and Orin (earthy browns, greens and aprons).
"""
from PIL import Image

import tmma_faces

# Cuboid UV layout of the 64x64 skin: limb -> (x, y, w, h, d) for the base layer and overlay offsets.
def _limbs(slim):
    aw = 3 if slim else 4
    return {
        "head": ((0, 0, 8, 8, 8), (32, 0)),
        "body": ((16, 16, 8, 12, 4), (16, 32)),
        "right_arm": ((40, 16, aw, 12, 4), (40, 32)),
        "left_arm": ((32, 48, aw, 12, 4), (48, 48)),
        "right_leg": ((0, 16, 4, 12, 4), (0, 32)),
        "left_leg": ((16, 48, 4, 12, 4), (0, 48)),
    }


def _faces(x, y, w, h, d):
    """Yields (face, x0, y0, width, height) for the six faces of a cuboid texture."""
    yield "top", x + d, y, w, d
    yield "bottom", x + d + w, y, w, d
    yield "right", x, y + d, d, h
    yield "front", x + d, y + d, w, h
    yield "left", x + d + w, y + d, d, h
    yield "back", x + 2 * d + w, y + d, w, h


def cells(limb, slim, overlay=False):
    """Every pixel of a limb as (face, u, v, w, h, px, py): u across the face, v down it."""
    (x, y, w, h, d), (ox, oy) = _limbs(slim)[limb]
    if overlay:
        x, y = ox, oy
    for face, fx, fy, fw, fh in _faces(x, y, w, h, d):
        for u in range(fw):
            for v in range(fh):
                yield face, u, v, fw, fh, fx + u, fy + v


def shade(c, f):
    return tuple(max(0, min(255, int(round(v * f)))) for v in c[:3]) + (255,)


SKIN = "skin"  # marker colour: filled with each individual's own skin tone

# --- Outfits ------------------------------------------------------------------------------------
# Keys: shirt, sleeves (rows of the arm covered), gloves, trousers, boots, boot_rows, belt, mail,
# coat (+coat_trim, coat_open, coat_rows on the legs), tabard (+emblem, tabard_rows), apron, sash,
# strap, collar, chain, cape, fur, buttons, stripes, pouch, pauldrons, robe.
OUTFITS = {
    "firstbrook_villager": {"shirt": (74, 104, 70), "sleeves": 6, "trousers": (92, 74, 56), "boots": (78, 54, 34),
                            "belt": (60, 42, 28), "collar": (196, 180, 150)},
    "firstbrook_farmer": {"shirt": (212, 198, 164), "sleeves": 5, "trousers": (98, 84, 58), "boots": (82, 58, 36),
                          "belt": (70, 50, 30), "apron": (150, 122, 80), "apron_rows": 4},
    "firstbrook_guard": {"shirt": (150, 154, 160), "mail": True, "sleeves": 12, "gloves": (88, 64, 42),
                         "trousers": (64, 58, 52), "boots": (60, 44, 30), "belt": (58, 40, 26),
                         "tabard": (52, 96, 64), "emblem": (210, 196, 120), "tabard_rows": 4},
    "expedition_worker": {"shirt": (176, 150, 118), "sleeves": 4, "trousers": (84, 72, 58), "boots": (70, 50, 32),
                          "belt": (60, 42, 28), "apron": (120, 86, 54), "apron_rows": 5, "gloves": (110, 82, 52),
                          "pouch": (96, 70, 44)},
    "expedition_researcher": {"shirt": (200, 196, 184), "sleeves": 12, "trousers": (70, 66, 64), "boots": (58, 44, 34),
                              "coat": (118, 98, 72), "coat_trim": (90, 72, 50), "coat_open": True, "coat_rows": 5,
                              "strap": (78, 56, 36), "pouch": (88, 64, 40)},
    "sylvaris_ranger": {"shirt": (70, 54, 40), "sleeves": 12, "gloves": (52, 40, 30), "trousers": (58, 48, 38),
                        "boots": (46, 36, 28), "boot_rows": 5, "belt": (40, 32, 24), "coat": (54, 84, 62),
                        "coat_trim": (62, 132, 138), "coat_rows": 4, "strap": (90, 66, 44), "cape": (46, 74, 54)},
    "eldervale_official": {"shirt": (226, 226, 222), "sleeves": 12, "trousers": (34, 34, 38), "boots": (26, 26, 30),
                           "robe": True, "coat": (36, 36, 40), "coat_trim": (182, 186, 194), "coat_open": True,
                           "coat_rows": 12, "chain": (200, 202, 210)},
    "eldervale_royal_guard": {"shirt": (128, 132, 140), "mail": True, "sleeves": 12, "gloves": (40, 40, 44),
                              "trousers": (40, 40, 44), "boots": (30, 30, 34), "belt": (24, 24, 28),
                              "tabard": (30, 30, 34), "emblem": (214, 216, 222), "tabard_rows": 6,
                              "pauldrons": (168, 172, 180), "cape": (34, 34, 38)},
    "stonecrown_guard": {"shirt": (96, 98, 104), "mail": True, "sleeves": 12, "gloves": (56, 56, 60),
                         "trousers": (52, 52, 56), "boots": (40, 40, 44), "belt": (66, 50, 34),
                         "tabard": (58, 60, 66), "emblem": (186, 120, 60), "tabard_rows": 4,
                         "pauldrons": (120, 122, 128)},
    "stonecrown_engineer": {"shirt": (84, 86, 92), "sleeves": 8, "gloves": (120, 92, 58), "trousers": (60, 60, 64),
                            "boots": (46, 42, 40), "belt": (102, 76, 46), "apron": (112, 82, 52), "apron_rows": 6,
                            "pouch": (150, 112, 50), "collar": (60, 62, 68)},
    "solkar_scholar": {"shirt": (212, 196, 160), "sleeves": 12, "trousers": (196, 180, 146), "boots": (120, 92, 60),
                       "boot_rows": 2, "robe": True, "sash": (34, 32, 32), "collar": (34, 32, 32), "strap": (150, 112, 70)},
    "veyrath_guard": {"shirt": (40, 70, 140), "sleeves": 12, "gloves": (210, 170, 70), "trousers": (30, 44, 86),
                      "boots": (28, 30, 40), "belt": (210, 170, 70), "coat": (36, 64, 132), "coat_trim": (218, 178, 74),
                      "coat_rows": 3, "pauldrons": (176, 180, 188), "buttons": (226, 186, 80), "cape": (60, 150, 160)},
    "thalassian_sailor": {"shirt": (226, 224, 216), "stripes": (40, 52, 92), "sleeves": 6, "trousers": (40, 48, 76),
                          "boots": (30, 28, 30), "belt": (24, 22, 22), "coat": (26, 26, 32), "coat_trim": (206, 166, 72),
                          "coat_open": True, "coat_rows": 0, "buttons": (210, 170, 76)},
    "merchant": {"shirt": (150, 66, 58), "sleeves": 12, "trousers": (88, 70, 52), "boots": (74, 52, 34),
                 "belt": (176, 140, 70), "coat": (128, 92, 50), "coat_trim": (186, 150, 82), "coat_open": True,
                 "coat_rows": 6, "strap": (92, 64, 38), "pouch": (176, 140, 70)},
    "raider": {"shirt": (62, 54, 50), "sleeves": 12, "gloves": (44, 36, 30), "trousers": (54, 48, 44),
               "boots": (40, 32, 26), "belt": (82, 30, 28), "coat": (84, 66, 48), "coat_trim": (66, 50, 36),
               "coat_rows": 3, "strap": (38, 30, 24), "patches": (98, 80, 60)},
    "raider_leader": {"shirt": (52, 44, 40), "sleeves": 12, "gloves": (38, 30, 26), "trousers": (46, 40, 36),
                      "boots": (34, 28, 22), "belt": (140, 34, 30), "coat": (70, 52, 38), "coat_trim": (140, 34, 30),
                      "coat_open": True, "coat_rows": 6, "fur": (150, 130, 104), "pauldrons": (112, 110, 108)},
}

# Headwear per type: a list of options; each individual draws one (None = bare head).
HEADWEAR = {
    "firstbrook_villager": [None, None, "headband"],
    "firstbrook_farmer": ["straw_hat", "straw_hat", None],
    "firstbrook_guard": ["kettle_helm"],
    "expedition_worker": [None, "leather_cap", "headband"],
    "expedition_researcher": [None, None],
    "sylvaris_ranger": ["hood_green"],
    "eldervale_official": [None, None, "coif"],
    "eldervale_royal_guard": ["royal_helm"],
    "stonecrown_guard": ["iron_helm"],
    "stonecrown_engineer": ["goggles_cap", "goggles_cap", "goggles"],
    "solkar_scholar": ["headwrap", "headwrap", None],
    "veyrath_guard": ["veyrath_helm"],
    "thalassian_sailor": ["knit_cap", "bandana", None],
    "merchant": ["felt_hat", None],
    "raider": ["raider_hood", "face_cloth", "raider_hood_cloth"],
    "raider_leader": [None],
}

# Identifying features each type may carry (each individual gets at most one or two).
FEATURES = {
    "firstbrook_villager": ["freckle_band", "moustache", "earring"],
    "firstbrook_farmer": ["moustache", "freckle_band", "scar"],
    "firstbrook_guard": ["scar", "moustache"],
    "expedition_worker": ["scar", "moustache", "soot"],
    "expedition_researcher": ["glasses", "earring"],
    "sylvaris_ranger": ["face_paint", "scar", "earring"],
    "eldervale_official": ["moustache", "glasses", "earring"],
    "eldervale_royal_guard": ["scar", "moustache"],
    "stonecrown_guard": ["scar", "moustache"],
    "stonecrown_engineer": ["soot", "moustache", "scar"],
    "solkar_scholar": ["glasses", "earring", "moustache"],
    "veyrath_guard": ["scar", "earring"],
    "thalassian_sailor": ["eyepatch", "earring", "scar", "moustache"],
    "merchant": ["moustache", "earring", "glasses"],
    "raider": ["scar", "eyepatch", "soot"],
    "raider_leader": ["scar"],
}


def _noise(rng):
    return rng.uniform(0.94, 1.05)


def paint_outfit(type_id, slim):
    """Returns (image, skin_cells) for the shared outfit of a type. Head is left empty."""
    o = OUTFITS[type_id]
    rng = tmma_faces._rng("outfit:" + type_id + (":slim" if slim else ""))
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    skin = {}
    boot_rows = o.get("boot_rows", 3)

    def put(px, py, c):
        if c == SKIN:
            skin[(px, py)] = 1.0
        elif isinstance(c, tuple) and len(c) == 2 and c[0] == SKIN:
            skin[(px, py)] = c[1]
        else:
            skin.pop((px, py), None)
            img.putpixel((px, py), c)

    def cloth(c, face, f=1.0):
        side = 0.92 if face in ("right", "left") else 0.88 if face == "back" else 0.9 if face in ("top", "bottom") else 1.0
        return shade(c, f * side * _noise(rng))

    def mail(c, u, v, face):
        return shade(c, (0.8 + 0.2 * ((u + v) % 2)) * (0.92 if face != "front" else 1.0))

    # Base layer: body.
    for face, u, v, w, h, px, py in cells("body", slim):
        if face in ("top", "bottom"):
            c = cloth(o["shirt"], face)
        elif o.get("belt") and v == 7:
            c = shade(o["belt"], 1.0 if face == "front" else 0.9)
        elif o.get("mail"):
            c = mail(o["shirt"], u, v, face)
        elif o.get("stripes") and v % 2 == 1 and v < 7:
            c = cloth(o["stripes"], face)
        else:
            c = cloth(o["shirt"], face)
        if o.get("collar") and face == "front" and v == 0 and 2 <= u <= 5:
            c = shade(o["collar"], 1.0)
        if o.get("sash") and face in ("front", "back") and v < 7 and (u + v) in (6, 7):
            c = cloth(o["sash"], face)
        if o.get("buttons") and face == "front" and u == 4 and v in (2, 4, 9) and not o.get("coat_open"):
            c = shade(o["buttons"], 1.0)
        if o.get("chain") and face == "front" and ((v == u and v < 4) or (v == 7 - u and v < 4)):
            c = shade(o["chain"], 1.0)
        put(px, py, c)
    # Arms.
    for arm in ("right_arm", "left_arm"):
        for face, u, v, w, h, px, py in cells(arm, slim):
            if face == "top":
                c = mail(o["shirt"], u, v, face) if o.get("mail") else cloth(o["shirt"], face)
            elif face == "bottom":
                c = shade(o["gloves"], 0.9) if o.get("gloves") else (SKIN, 0.92)
            elif o.get("gloves") and v >= 10:
                c = cloth(o["gloves"], face)
            elif v < o["sleeves"]:
                if o.get("mail"):
                    c = mail(o["shirt"], u, v, face)
                elif o.get("stripes") and v % 2 == 1:
                    c = cloth(o["stripes"], face)
                else:
                    c = cloth(o["shirt"], face)
            else:
                c = (SKIN, 1.0 if face == "front" else 0.93)
            put(px, py, c)
    # Legs.
    for leg in ("right_leg", "left_leg"):
        for face, u, v, w, h, px, py in cells(leg, slim):
            if face == "bottom" or (face != "top" and v >= 12 - boot_rows):
                c = cloth(o["boots"], face)
            elif o.get("robe"):
                c = cloth(o["shirt"], face, 0.97)
            else:
                c = cloth(o["trousers"], face)
            put(px, py, c)

    # Overlay layer.
    coat, trim = o.get("coat"), o.get("coat_trim")
    if coat:
        for face, u, v, w, h, px, py in cells("body", slim, overlay=True):
            if face == "bottom":
                continue
            if face == "front" and o.get("coat_open") and 2 <= u <= 5 and face != "top":
                if trim and u in (2, 5):
                    put(px, py, shade(trim, 1.0))
                continue
            edge = trim and ((face == "front" and u in (0, w - 1) and not o.get("coat_open")) or (face != "top" and v == 0))
            c = shade(trim, 1.0) if edge else cloth(coat, face)
            if o.get("belt") and v == 7 and face != "top":
                c = shade(o["belt"], 0.95)
            if o.get("buttons") and o.get("coat_open") and face == "front" and u in (1, 6) and v in (2, 4, 6):
                c = shade(o["buttons"], 1.0)
            put(px, py, c)
        for arm in ("right_arm", "left_arm"):
            for face, u, v, w, h, px, py in cells(arm, slim, overlay=True):
                if face == "bottom" or (face != "top" and v >= min(10, o["sleeves"])):
                    continue
                c = shade(trim, 1.0) if trim and face != "top" and v == min(10, o["sleeves"]) - 1 else cloth(coat, face)
                put(px, py, c)
        rows = o.get("coat_rows", 0)
        for leg in ("right_leg", "left_leg"):
            for face, u, v, w, h, px, py in cells(leg, slim, overlay=True):
                if face in ("top", "bottom") or v >= rows:
                    continue
                if face == "front" and o.get("coat_open") and ((leg == "right_leg" and u == 3) or (leg == "left_leg" and u == 0)):
                    continue
                c = shade(trim, 1.0) if trim and v == rows - 1 else cloth(coat, face)
                put(px, py, c)
    if o.get("tabard"):
        rows = o.get("tabard_rows", 4)
        for face, u, v, w, h, px, py in cells("body", slim, overlay=True):
            if face in ("front", "back") and 2 <= u <= 5:
                c = cloth(o["tabard"], face)
                if face == "front" and o.get("emblem") and ((u in (3, 4) and 2 <= v <= 5) or (v == 3 and u in (2, 5))):
                    c = shade(o["emblem"], 1.0)
                put(px, py, c)
        for leg in ("right_leg", "left_leg"):
            for face, u, v, w, h, px, py in cells(leg, slim, overlay=True):
                inner = (leg == "right_leg" and u >= 2) or (leg == "left_leg" and u <= 1)
                if face in ("front", "back") and inner and v < rows:
                    put(px, py, cloth(o["tabard"], face))
    if o.get("apron"):
        rows = o.get("apron_rows", 4)
        for face, u, v, w, h, px, py in cells("body", slim, overlay=True):
            if face == "front" and 1 <= u <= w - 2 and v >= 1:
                put(px, py, cloth(o["apron"], face))
        for leg in ("right_leg", "left_leg"):
            for face, u, v, w, h, px, py in cells(leg, slim, overlay=True):
                if face == "front" and v < rows:
                    put(px, py, cloth(o["apron"], face))
    if o.get("strap"):
        for face, u, v, w, h, px, py in cells("body", slim, overlay=True):
            if face == "front" and u == v and v < 8:
                put(px, py, shade(o["strap"], 1.0))
            if face == "back" and u == 7 - v and v < 8:
                put(px, py, shade(o["strap"], 0.9))
    if o.get("pouch"):
        for face, u, v, w, h, px, py in cells("right_leg", slim, overlay=True):
            if face in ("front", "right") and v in (0, 1) and u in (1, 2):
                put(px, py, shade(o["pouch"], 1.0 if v == 0 else 0.85))
    if o.get("cape"):
        for face, u, v, w, h, px, py in cells("body", slim, overlay=True):
            if face == "back" or (face == "top" and v == w // 2 - 1 and False):
                put(px, py, cloth(o["cape"], face, 1.0 if v else 1.1))
        for leg in ("right_leg", "left_leg"):
            for face, u, v, w, h, px, py in cells(leg, slim, overlay=True):
                if face == "back" and v < 8:
                    put(px, py, cloth(o["cape"], face))
    if o.get("fur"):
        for face, u, v, w, h, px, py in cells("body", slim, overlay=True):
            if face == "top" or (face != "bottom" and v <= 1):
                put(px, py, shade(o["fur"], rng.uniform(0.8, 1.1)))
    if o.get("pauldrons"):
        for arm in ("right_arm", "left_arm"):
            for face, u, v, w, h, px, py in cells(arm, slim, overlay=True):
                if face == "top" or (face != "bottom" and v <= 2):
                    put(px, py, shade(o["pauldrons"], (1.0 if v != 2 else 0.8) * (0.95 if face != "front" else 1.0)))
    if o.get("patches"):
        for face, u, v, w, h, px, py in cells("body", slim):
            if face == "front" and (u, v) in ((1, 3), (2, 3), (1, 4), (6, 9), (6, 10)):
                put(px, py, shade(o["patches"], 1.0))
        for face, u, v, w, h, px, py in cells("left_leg", slim):
            if face == "front" and (u, v) in ((1, 4), (2, 4), (1, 5)):
                put(px, py, shade(o["patches"], 1.0))
    return img, skin


# --- Heads --------------------------------------------------------------------------------------
def _head(img, face, u, v, c, layer=0):
    for f, fu, fv, w, h, px, py in cells("head", False, overlay=layer == 1):
        if f == face and fu == u and fv == v:
            img.putpixel((px, py), c)
            return


def _head_layer(img, fn, layer=1):
    for f, u, v, w, h, px, py in cells("head", False, overlay=layer == 1):
        c = fn(f, u, v)
        if c is not None:
            img.putpixel((px, py), c)


def _clear_hat(img):
    img.paste(Image.new("RGBA", (32, 16), (0, 0, 0, 0)), (32, 0))


def put_headwear(img, kind, rng):
    """Paints shared headwear on the hat layer (and clears hair volume under it)."""
    if kind is None:
        return ""
    def solid(color, rows, top=True, front_rows=None, f_side=0.92, pattern=None):
        fr = rows if front_rows is None else front_rows
        def fn(face, u, v):
            if face == "top" and top:
                return shade(color, 1.05 * (pattern(u, v) if pattern else 1))
            if face == "bottom":
                return None
            lim = fr if face == "front" else rows
            if v < lim:
                return shade(color, (1.0 if face == "front" else f_side) * (pattern(u, v) if pattern else 1))
            return None
        return fn
    if kind in ("straw_hat", "kettle_helm", "leather_cap", "felt_hat", "knit_cap", "iron_helm", "royal_helm",
                "veyrath_helm", "goggles_cap", "coif", "hood_green", "raider_hood", "raider_hood_cloth", "headwrap", "bandana"):
        _clear_hat(img)
    if kind == "straw_hat":
        weave = lambda u, v: 0.9 + 0.1 * ((u + v) % 2)  # noqa: E731
        _head_layer(img, solid((214, 186, 106), 3, pattern=weave))
        _head_layer(img, lambda f, u, v: shade((150, 70, 50), 1.0) if f != "top" and f != "bottom" and v == 2 else None)
        return "straw hat"
    if kind == "kettle_helm":
        _head_layer(img, solid((170, 174, 182), 2, pattern=lambda u, v: 0.9 + 0.1 * (u % 2)))
        return "kettle helm"
    if kind == "leather_cap":
        _head_layer(img, solid((112, 80, 50), 2))
        return "leather cap"
    if kind == "headband":
        color = rng.choice([(150, 50, 46), (60, 90, 140), (200, 170, 80), (80, 120, 70)])
        _head_layer(img, lambda f, u, v: shade(color, 1.0 if f == "front" else 0.9) if f not in ("top", "bottom") and v == 2 else None)
        return "headband"
    if kind == "felt_hat":
        _head_layer(img, solid((86, 60, 40), 2))
        _head_layer(img, lambda f, u, v: shade((176, 140, 70), 1.0) if f not in ("top", "bottom") and v == 1 else None)
        return "felt hat"
    if kind == "knit_cap":
        _head_layer(img, solid((40, 52, 92), 3, front_rows=2, pattern=lambda u, v: 0.9 + 0.1 * (u % 2)))
        return "knit cap"
    if kind == "bandana":
        _head_layer(img, solid((160, 44, 40), 2, pattern=lambda u, v: 1.15 if (u + v) % 4 == 0 else 1.0))
        return "bandana"
    if kind == "iron_helm":
        _head_layer(img, solid((96, 98, 104), 3, front_rows=2))
        _head_layer(img, lambda f, u, v: shade((186, 120, 60), 1.0) if f == "front" and u in (3, 4) and v < 4 else None)
        return "iron helm"
    if kind == "royal_helm":
        _head_layer(img, solid((176, 180, 190), 3, front_rows=2))
        _head_layer(img, lambda f, u, v: shade((30, 30, 34), 1.0) if f == "top" and u in (3, 4) else None)
        return "royal helm"
    if kind == "veyrath_helm":
        _head_layer(img, solid((176, 180, 188), 3, front_rows=2))
        _head_layer(img, lambda f, u, v: shade((218, 178, 74), 1.0) if (f == "top" and u in (3, 4)) or (f in ("front", "back") and u in (3, 4) and v == 0) else None)
        return "crested helm"
    if kind in ("goggles_cap", "goggles"):
        if kind == "goggles_cap":
            _head_layer(img, solid((70, 70, 76), 2))
        band = (90, 64, 40)
        _head_layer(img, lambda f, u, v: (shade((200, 160, 70), 1.0) if f == "front" and u in (1, 2, 5, 6) else shade(band, 0.9)) if f not in ("top", "bottom") and v == 2 else None)
        return "goggles" + (" and cap" if kind == "goggles_cap" else "")
    if kind == "coif":
        _head_layer(img, solid((34, 34, 38), 2, front_rows=1))
        return "black coif"
    if kind == "hood_green" or kind.startswith("raider_hood"):
        color = (50, 80, 58) if kind == "hood_green" else (52, 46, 42)
        def hood(f, u, v):
            if f == "top":
                return shade(color, 1.0)
            if f == "bottom":
                return None
            if f == "front":
                return shade(color, 0.95) if v < 2 or u in (0, 7) else None
            if f == "back":
                return shade(color, 0.88)
            return shade(color, 0.92) if not (v >= 3 and ((f == "right" and u >= 6) or (f == "left" and u <= 1))) else None
        _head_layer(img, hood)
        if kind == "raider_hood_cloth":
            _head_layer(img, lambda f, u, v: shade((112, 36, 32), 1.0) if f == "front" and v >= 5 and 1 <= u <= 6 else None)
            return "hood and face cloth"
        return "green hood" if kind == "hood_green" else "hood"
    if kind == "face_cloth":
        _head_layer(img, lambda f, u, v: shade((112, 36, 32), 1.0 if f == "front" else 0.9) if f not in ("top", "bottom") and v >= 5 else None)
        return "face cloth"
    if kind == "headwrap":
        _head_layer(img, solid((222, 208, 172), 3, front_rows=2, pattern=lambda u, v: 0.94 + 0.08 * ((u + 2 * v) % 3 == 0)))
        _head_layer(img, lambda f, u, v: shade((34, 32, 32), 1.0) if f not in ("top", "bottom") and v == 2 else None)
        return "headwrap"
    raise ValueError(kind)


def put_feature(img, feature, skin, rng):
    er = 4
    if feature == "scar":
        side = rng.choice((1, 6))
        for dv in (0, 1, 2):
            _head(img, "front", side + (dv if side == 1 else -dv) // 2, er - 1 + dv, shade(skin, 0.72))
        return "scar"
    if feature == "eyepatch":
        _head(img, "front", 1, er, (24, 22, 22, 255), 1); _head(img, "front", 2, er, (24, 22, 22, 255), 1)
        for u in range(8):
            if u not in (1, 2):
                _head(img, "front", u, er - 1 if u > 2 else er, (24, 22, 22, 255), 1)
        for f in ("right", "left", "back"):
            for u in range(8):
                _head(img, f, u, er - 1, (24, 22, 22, 255), 1)
        return "eyepatch"
    if feature == "earring":
        _head(img, "right", 3, 5, (226, 186, 80, 255), 1)
        return "earring"
    if feature == "moustache":
        hair = img.getpixel((24 + 3, 8 + 1))  # back of head: hair colour
        if hair[:3] == shade(skin, 0.93)[:3] or abs(sum(hair[:3]) - sum(skin[:3])) < 40:
            hair = (64, 42, 28)  # bald: a dark brown moustache
        for u in (2, 3, 4, 5):
            _head(img, "front", u, er + 2, shade(hair, 0.85))
        return "moustache"
    if feature == "freckle_band":
        for u in (1, 2, 5, 6):
            _head(img, "front", u, er + 1, shade(skin, 0.8))
        return "freckles"
    if feature == "face_paint":
        for u in (0, 1, 6, 7):
            _head(img, "front", u, er + 1, (62, 132, 138, 255))
        return "teal face paint"
    if feature == "soot":
        for u, v in ((1, er + 2), (2, er + 3), (6, er + 1)):
            _head(img, "front", u, v, shade(skin, 0.6))
        return "soot smudges"
    if feature == "glasses":
        frame = (40, 40, 44, 255)
        for u in (0, 3, 4, 7):
            _head(img, "front", u, er, frame, 1)
        for u in (1, 2, 5, 6):
            _head(img, "front", u, er - 1, frame, 1)
        return "glasses"
    raise ValueError(feature)


def make(type_id, key, slim, seen):
    """One individual of a type. `seen` holds faces already used, so every NPC face is unique."""
    template, skin_cells = paint_outfit(type_id, slim)
    for attempt in range(50):
        k = key if attempt == 0 else f"{key}#{attempt}"
        tone = tmma_faces._rng(k).choice(tmma_faces.SKIN_TONES)  # the tone reface() picks for this key
        img, traits = tmma_faces.reface(template, k, cap_rows=0, keep_hat=False)
        frng = tmma_faces._rng("features:" + k)
        hat = frng.choice(HEADWEAR[type_id])
        feats = frng.sample(FEATURES[type_id], k=min(len(FEATURES[type_id]), frng.choice((1, 1, 2))))
        if "glasses" in feats and hat in ("goggles", "goggles_cap"):
            feats.remove("glasses")
        signature = (tone, traits, hat, tuple(sorted(feats)))
        hair = traits.split(",")[0]
        look = "bald" if hair.startswith("bald") else " ".join(hair.split()[1:-1])  # hair colour, as seen from the front
        face_sig = (tone, hair, tuple(sorted(feats)))
        type_sig = (type_id, tone, look)  # within a type, skin tone and hair colour never both repeat
        if face_sig not in seen and type_sig not in seen:
            seen.update((face_sig, type_sig))
            break
    else:
        raise RuntimeError(f"could not find a unique face for {key}")
    for (px, py), f in skin_cells.items():
        img.putpixel((px, py), shade(tone, f))
    if "glasses" in traits and hat is not None:
        traits = traits.replace(", glasses", "")
    if "glasses" in traits and "glasses" in feats:
        feats.remove("glasses")
    extra = [put_feature(img, f, tone, frng) for f in feats]
    hw = put_headwear(img, hat, frng)
    desc = ", ".join([traits] + extra + ([hw] if hw else []))
    return img, desc, template, skin_cells, signature


def check_shared_outfit(template, skin_cells, img):
    """Every non-head pixel matches the type's outfit, except the individual's own skin."""
    for y in range(16, 64):
        for x in range(64):
            if (x, y) in skin_cells:
                continue
            if template.getpixel((x, y)) != img.getpixel((x, y)):
                raise AssertionError(f"outfit pixel changed at {x},{y}")
