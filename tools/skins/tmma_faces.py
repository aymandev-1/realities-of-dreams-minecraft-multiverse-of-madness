"""Gives TMMA uniform templates new faces and hair without touching the uniform.

Only the head (base layer x0-31,y0-15 and, unless keep_hat, the hat layer x32-63,y0-15)
is redrawn. assert_uniform_unchanged() proves every other pixel is identical.
"""
import hashlib
import random

from PIL import Image

SKIN_TONES = [(255, 220, 177), (241, 194, 150), (224, 172, 125), (198, 134, 90), (160, 104, 66), (122, 78, 48), (92, 58, 36), (236, 188, 160)]
HAIR = {"black": (28, 24, 24), "dark brown": (64, 42, 28), "brown": (104, 70, 42), "auburn": (140, 64, 36),
        "blonde": (214, 178, 102), "ginger": (190, 96, 44), "grey": (150, 150, 150), "white": (226, 226, 222)}
EYES = {"brown": (92, 58, 32), "blue": (60, 110, 200), "green": (60, 140, 80), "grey": (110, 120, 130), "hazel": (130, 100, 50)}
STYLES = ["short", "long", "buzz", "bald", "bun", "swept"]

# Head faces in the base layer: name -> (x, y)
FACES = {"top": (8, 0), "bottom": (16, 0), "right": (0, 8), "front": (8, 8), "left": (16, 8), "back": (24, 8)}
SIDE_FACES = ("right", "front", "left", "back")


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + (255,)


def _rng(key):
    return random.Random(int(hashlib.sha256(key.encode()).hexdigest()[:16], 16))


def reface(template, key, cap_rows=0, keep_hat=False):
    rng = _rng(key)
    img = template.copy()
    skin = rng.choice(SKIN_TONES)
    hair_name, hair = rng.choice(list(HAIR.items()))
    eye_name, eye = rng.choice(list(EYES.items()))
    style = rng.choice(STYLES)
    beard = rng.random() < 0.3 and style != "long"
    glasses = rng.random() < 0.25
    freckles = rng.random() < 0.2 and skin in SKIN_TONES[:2]

    def px(face, x, y, color, layer=0):
        fx, fy = FACES[face]
        if layer == 0 and y < cap_rows and face != "bottom":
            return
        if layer == 0 and face == "top" and cap_rows > 0:
            return
        img.putpixel((fx + x + 32 * layer, fy + y), color)

    def hair_px(x, y, face, f=1.0, layer=0):
        px(face, x, y, shade(hair, f * rng.uniform(0.9, 1.08)), layer)

    # Skin everywhere on the base head, slightly darker on the sides.
    for face in FACES:
        f = {"front": 1.0, "top": 1.0, "bottom": 0.85}.get(face, 0.93)
        for x in range(8):
            for y in range(8):
                px(face, x, y, shade(skin, f))
    if not keep_hat:
        img.paste(Image.new("RGBA", (32, 16), (0, 0, 0, 0)), (32, 0))

    # Face features.
    er = max(4, cap_rows + 1)
    for ex, inner in ((1, 2), (5, 5)):
        px("front", ex, er, (255, 255, 255, 255))
        px("front", ex + 1, er, (255, 255, 255, 255))
        px("front", inner, er, eye + (255,))
    px("front", 3, er + 1, shade(skin, 0.86)); px("front", 4, er + 1, shade(skin, 0.86))
    px("front", 3, er + 2, shade(skin, 0.7)); px("front", 4, er + 2, shade(skin, 0.7))
    if style != "bald":
        for bx in (1, 2, 5, 6):
            px("front", bx, er - 1, shade(hair, 0.9))
    if freckles:
        for fx_ in (1, 2, 5, 6):
            px("front", fx_, er + 1, shade(skin, 0.82))

    # Hair styles.
    if style != "bald":
        top_f = 0.85 if style == "buzz" else 1.0
        for x in range(8):
            for y in range(8):
                hair_px(x, y, "top", top_f)
        fringe = {"short": 1, "long": 1, "buzz": 0, "bun": 1, "swept": 2}[style]
        for x in range(8):
            for y in range(min(fringe + (1 if style != "buzz" else 1), er - 1)):
                if style == "swept" and y == 1 and x > 4:
                    continue
                hair_px(x, y, "front")
        side_depth = {"short": 3, "long": 8, "buzz": 1, "bun": 3, "swept": 3}[style]
        back_depth = {"short": 6, "long": 8, "buzz": 3, "bun": 6, "swept": 6}[style]
        for y in range(side_depth):
            for x in range(8):
                # Long hair below the temples leaves the two columns nearest the face clear.
                if not (style == "long" and y >= 3 and x >= 6):
                    hair_px(x, y, "right", 0.95)
                if not (style == "long" and y >= 3 and x <= 1):
                    hair_px(x, y, "left", 0.95)
        for y in range(back_depth):
            for x in range(8):
                hair_px(x, y, "back", 0.9)
        if style == "long":
            for y in range(er, 8):
                hair_px(0, y, "front"); hair_px(7, y, "front")
        if not keep_hat:
            # A little outer-layer volume on top and around the back.
            for x in range(8):
                for y in range(8):
                    if rng.random() < (0.55 if style in ("long", "short", "swept") else 0.2):
                        hair_px(x, y, "top", 1.05, layer=1)
                for y in range(2 if style != "long" else 6):
                    if rng.random() < 0.5:
                        hair_px(x, y, "back", 1.0, layer=1)
            if style == "bun":
                for x in range(2, 6):
                    for y in range(0, 3):
                        hair_px(x, y, "back", 1.1, layer=1)
    if beard:
        for x in range(8):
            for y in range(er + 2, 8):
                if not (y == er + 2 and x in (3, 4)):
                    hair_px(x, y, "front", 0.9)
        for y in range(er + 1, 8):
            for x in (0, 1, 6, 7):
                hair_px(x, y, "right", 0.85); hair_px(x, y, "left", 0.85)
        for x in range(8):
            for y in range(8):
                hair_px(x, y, "bottom", 0.8)
    else:
        px("front", 3, er + 2, (150, 80, 70, 255)); px("front", 4, er + 2, (150, 80, 70, 255))
    if glasses and not keep_hat:
        frame = (40, 40, 44, 255)
        for x in (0, 3, 4, 7):
            px("front", x, er, frame, layer=1)
        for x in (1, 2, 5, 6):
            px("front", x, er - 1, frame, layer=1)

    traits = f"{style} {hair_name} hair, {eye_name} eyes" + (", beard" if beard else "") + (", glasses" if glasses and not keep_hat else "") + (", freckles" if freckles else "")
    return img, traits


def assert_uniform_unchanged(template, variant):
    for y in range(64):
        for x in range(64):
            if y < 16:
                continue  # head rows (base + hat layer)
            if template.getpixel((x, y)) != variant.getpixel((x, y)):
                raise AssertionError(f"uniform pixel changed at {x},{y}")
