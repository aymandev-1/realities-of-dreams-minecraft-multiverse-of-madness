"""Generates the original title-screen art for Realities of Dreams.

Everything here is drawn procedurally (no third-party artwork): a dawn sky over MC-001,
parallax layers of distant mountains with the ancient gateway carved into a peak, Awakening
Valley's forests and river, the Firstbrook skyline, and a pixel-font title logo.

Run from the repository root:  python tools/art/menu_art.py
Writes PNGs to src/main/resources/assets/realitiesofdreams/textures/gui/menu/.
Each scrolling layer is W pixels wide and tiles seamlessly, so the menu can pan forever.
"""
import math
import os
import random

import numpy as np
from PIL import Image

OUT = os.path.join("src", "main", "resources", "assets", "realitiesofdreams", "textures", "gui", "menu")
W, H = 960, 270  # scrolling layers
SW = 480         # static sky


def periodic(x, seed, octaves=((1, 1.0), (2, 0.5), (3, 0.33), (5, 0.2), (8, 0.12), (13, 0.07))):
    """Smooth noise that repeats exactly every W pixels (integer frequencies only)."""
    rnd = random.Random(seed)
    v = np.zeros_like(x, dtype=float)
    for f, a in octaves:
        ph = rnd.random() * math.tau
        v += a * np.sin(x / W * math.tau * f + ph)
    return v


def rgba(hexcol, a=255):
    hexcol = hexcol.lstrip("#")
    return (int(hexcol[0:2], 16), int(hexcol[2:4], 16), int(hexcol[4:6], 16), a)


def lerp(c1, c2, t):
    return tuple(max(0, min(255, int(round(int(c1[i]) + (int(c2[i]) - int(c1[i])) * t)))) for i in range(4))


def new_layer(w=W):
    return np.zeros((H, w, 4), dtype=np.uint8)


def save(arr, name):
    os.makedirs(OUT, exist_ok=True)
    Image.fromarray(arr, "RGBA").save(os.path.join(OUT, name))
    print("wrote", name)


# ------------------------------------------------------------------ sky

def sky():
    a = new_layer(SW)
    stops = [(0.0, rgba("1b1640")), (0.22, rgba("3d2a63")), (0.38, rgba("a4507a")), (0.50, rgba("e98a5a")), (0.62, rgba("f6c477")), (1.0, rgba("fde3a6"))]
    for y in range(H):
        t = y / (H - 1)
        for i in range(len(stops) - 1):
            if stops[i][0] <= t <= stops[i + 1][0]:
                k = (t - stops[i][0]) / (stops[i + 1][0] - stops[i][0])
                # 8 colour bands for a pixel-art dithered gradient
                k = round(k * 8) / 8 if (y % 2 == 0) else math.floor(k * 8) / 8
                a[y, :] = lerp(stops[i][1], stops[i + 1][1], k)
                break
    rnd = random.Random(7)
    for _ in range(140):
        x, y = rnd.randrange(SW), rnd.randrange(int(H * 0.42))
        b = 255 - int(y / (H * 0.42) * 200)
        a[y, x] = (255, 245, 230, max(40, b))
    save(a, "sky.png")


def sun():
    s = 128
    a = np.zeros((s, s, 4), dtype=np.uint8)
    c = s / 2
    for y in range(s):
        for x in range(s):
            d = math.hypot(x + 0.5 - c, y + 0.5 - c) / c
            if d < 0.22:
                a[y, x] = (255, 244, 206, 255)
            elif d < 1.0:
                al = (1 - d) ** 2.2
                al = round(al * 10) / 10  # banded glow, pixel-art style
                a[y, x] = (255, 214, 140, int(al * 200))
    save(a, "sun.png")


def clouds():
    a = new_layer()
    rnd = random.Random(11)
    for _ in range(26):
        cx, cy = rnd.randrange(W), rnd.randrange(30, 150)
        length, thick = rnd.randrange(40, 120), rnd.randrange(5, 12)
        col = (255, 226, 214, 150) if cy > 90 else (232, 196, 220, 120)
        for i in range(-length // 2, length // 2):
            h = int(thick * math.sqrt(max(0, 1 - (2 * i / length) ** 2)))
            for j in range(-h // 2, h // 2 + 1):
                x, y = (cx + i) % W, cy + j
                if 0 <= y < H:
                    shade = col if j <= 0 else (col[0] - 40, col[1] - 50, col[2] - 30, col[3])
                    a[y, x] = shade
    save(a, "clouds.png")


# ------------------------------------------------------------------ mountains

def ridge_layer(name, seed, base, amp, col_top, col_bottom, snow=None, gateway=False, waterfall=False):
    a = new_layer()
    xs = np.arange(W)
    hgt = base + amp * periodic(xs, seed)
    hgt = np.round(hgt).astype(int)
    for x in range(W):
        top = H - hgt[x]
        for y in range(max(0, top), H):
            t = (y - top) / max(1, H - top)
            c = lerp(col_top, col_bottom, min(1, t * 1.4))
            # light from the dawn sun: west-facing slopes brighter
            slope = hgt[(x + 1) % W] - hgt[x - 1]
            if slope > 0 and y - top < 2:
                c = lerp(c, (255, 200, 150, 255), 0.25)
            a[y, x] = c
        if snow is not None and hgt[x] > base + amp * 0.75:
            depth = int((hgt[x] - (base + amp * 0.75)) * 0.8) + 2
            for y in range(max(0, top), min(H, top + depth)):
                a[y, x] = snow
    if gateway:
        # The ancient gateway: a ring with six glyph stones, carved into the tallest peak.
        px = int(np.argmax(hgt))
        cy = H - hgt[px] + 34
        r = 13
        for y in range(cy - r - 3, cy + r + 4):
            for x in range(px - r - 3, px + r + 4):
                d = math.hypot(x - px, y - cy)
                if r - 2.2 <= d <= r + 0.8 and 0 <= y < H:
                    a[y, x % W] = (150, 196, 214, 255)
                elif r - 3.5 <= d < r - 2.2 and 0 <= y < H:
                    a[y, x % W] = (60, 110, 140, 255)
                elif d < r - 3.5 and 0 <= y < H:
                    glow = max(0, 1 - d / (r - 3.5))
                    a[y, x % W] = lerp(tuple(a[y, x % W]), (150, 240, 255, 255), glow * 0.18)
        for k in range(6):
            ang = k * math.tau / 6 - math.pi / 2
            gx, gy = int(px + math.cos(ang) * (r + 4)), int(cy + math.sin(ang) * (r + 4))
            for y in range(gy - 1, gy + 2):
                for x in range(gx - 1, gx + 2):
                    if 0 <= y < H:
                        a[y, x % W] = (255, 226, 150, 255) if (x == gx and y == gy) else (90, 80, 110, 255)
    if waterfall:
        rnd = random.Random(seed)
        for fx in (rnd.randrange(W), rnd.randrange(W)):
            top = H - hgt[fx] + 6
            for y in range(top, H):
                for x in (fx, fx + 1):
                    a[y, x % W] = (200, 236, 255, 230) if (y + x) % 3 else (160, 210, 240, 230)
    save(a, name)


# ------------------------------------------------------------------ valley, forest and river

def tree(a, x, base, h, col, dark):
    for y in range(base - h, base):
        w = int((y - (base - h)) / h * (h * 0.45)) + 1
        for dx in range(-w, w + 1):
            if 0 <= y < H:
                a[y, (x + dx) % W] = dark if dx > w * 0.3 else col
    for y in range(base, min(H, base + 3)):
        a[y, x % W] = (60, 40, 30, 255)


def valley():
    a = new_layer()
    xs = np.arange(W)
    hgt = np.round(70 + 14 * periodic(xs, 21, ((1, 1.0), (2, 0.6), (4, 0.3), (7, 0.15)))).astype(int)
    for x in range(W):
        top = H - hgt[x]
        for y in range(top, H):
            t = (y - top) / max(1, H - top)
            a[y, x] = lerp(rgba("5f8a4a"), rgba("2e4b32"), min(1, t * 1.6))
    # the river winding through the valley
    for y in range(H - 60, H):
        t = (y - (H - 60)) / 60
        cx = int(W * 0.62 + 40 * math.sin(t * 5.0))
        half = int(2 + t * 9)
        for x in range(cx - half, cx + half + 1):
            a[y, x % W] = (140, 196, 214, 255) if (x + y) % 4 else (226, 238, 230, 255)
    rnd = random.Random(5)
    for _ in range(170):
        x = rnd.randrange(W)
        base = H - hgt[x] + rnd.randrange(2, 30)
        tree(a, x, base, rnd.randrange(8, 15), (46, 82, 50, 255), (32, 60, 40, 255))
    save(a, "valley.png")


def village():
    """Firstbrook: timber houses with warm windows, the watchtower, the well and fences."""
    a = new_layer()
    xs = np.arange(W)
    ground = np.round(38 + 4 * periodic(xs, 33, ((1, 1.0), (3, 0.4)))).astype(int)
    for x in range(W):
        for y in range(H - ground[x], H):
            a[y, x] = lerp(rgba("44613a"), rgba("1d2a1e"), (y - (H - ground[x])) / max(1, ground[x]))
    rnd = random.Random(42)
    palettes = [  # wall, roof light, roof dark
        ((120, 88, 62, 255), (92, 54, 44, 255), (70, 40, 34, 255)),
        ((150, 140, 128, 255), (60, 70, 86, 255), (44, 52, 66, 255)),
        ((168, 132, 92, 255), (124, 60, 46, 255), (96, 44, 36, 255)),
        ((110, 74, 50, 255), (74, 84, 52, 255), (56, 64, 40, 255)),
    ]
    frame = (70, 50, 40, 255)
    window = (255, 196, 102, 255)

    def house(x, w, hh, pal):
        wall, roof, roof2 = pal
        g = H - ground[x % W] + 2
        for y in range(g - hh, g):
            for dx in range(w):
                c = frame if dx in (0, w - 1) or y == g - hh else wall
                a[y, (x + dx) % W] = c
        rh = w // 2 + 1
        for i in range(rh):
            y = g - hh - i
            for dx in range(i - 1, w - i + 1):
                a[y, (x + dx) % W] = roof if dx < w / 2 else roof2
        for row in range(g - hh + 3, g - 5, 7):
            for wx in range(3, w - 3, 6):
                for y in (row, row + 1):
                    a[y, (x + wx) % W] = window
                    a[y, (x + wx + 1) % W] = window
        a[g - 4:g, (x + w // 2) % W] = (50, 34, 26, 255)
        if rnd.random() < 0.6:
            cx = x + w - 5
            for y in range(g - hh - rh + 2, g - hh - rh + 7):
                a[y, cx % W] = (90, 86, 84, 255)
            for k in range(10):
                sy = g - hh - rh - k * 2
                if 0 <= sy < H:
                    a[sy, (cx + (k % 3) - 1) % W] = (230, 220, 220, max(0, 160 - k * 16))

    def tower(x):
        g = H - ground[x % W] + 2
        for y in range(g - 50, g):
            for dx in (0, 1, 8, 9):
                a[y, (x + dx) % W] = frame
            if y % 6 == 0:
                for dx in range(10):
                    a[y, (x + dx) % W] = frame
        for y in range(g - 56, g - 50):
            for dx in range(-2, 12):
                a[y, (x + dx) % W] = (70, 40, 34, 255)
        a[g - 53, (x + 4) % W] = window
        a[g - 53, (x + 5) % W] = window

    for start, end in ((90, 430), (610, 820)):
        x = start
        tower_at = rnd.randrange(start + 60, end - 60)
        while x < end:
            if abs(x - tower_at) < 12:
                tower(x)
                x += 18
                continue
            w = rnd.choice((16, 20, 24, 28, 32))
            hh = rnd.choice((11, 13, 15, 20))
            house(x, w, hh, palettes[rnd.randrange(len(palettes))])
            x += w + rnd.randrange(3, 14)
            if rnd.random() < 0.4:
                for fx in range(x - 4, x + 6, 2):
                    a[H - ground[fx % W] - 1:H - ground[fx % W] + 2, fx % W] = (80, 58, 40, 255)
                x += 8
    save(a, "village.png")


def foreground():
    a = new_layer()
    rnd = random.Random(9)
    xs = np.arange(W)
    hgt = np.round(16 + 4 * periodic(xs, 77, ((2, 1.0), (5, 0.5), (11, 0.3)))).astype(int)
    for x in range(W):
        for y in range(H - hgt[x], H):
            a[y, x] = (18, 28, 20, 255)
        for _ in range(2):
            bh = rnd.randrange(3, 12)
            for y in range(H - hgt[x] - bh, H - hgt[x]):
                a[y, x] = (24, 38, 26, 255)
    flowers = [(232, 84, 84, 255), (250, 220, 90, 255), (120, 160, 250, 255), (240, 240, 240, 255)]
    for _ in range(90):
        x = rnd.randrange(W)
        y = H - hgt[x] - rnd.randrange(4, 10)
        a[y, x] = flowers[rnd.randrange(len(flowers))]
    save(a, "foreground.png")


# ------------------------------------------------------------------ title logo (original pixel font)

FONT = {
    "A": ["01110", "10001", "10001", "11111", "10001", "10001", "10001"],
    "D": ["11110", "10001", "10001", "10001", "10001", "10001", "11110"],
    "E": ["11111", "10000", "10000", "11110", "10000", "10000", "11111"],
    "F": ["11111", "10000", "10000", "11110", "10000", "10000", "10000"],
    "I": ["111", "010", "010", "010", "010", "010", "111"],
    "L": ["10000", "10000", "10000", "10000", "10000", "10000", "11111"],
    "M": ["10001", "11011", "10101", "10101", "10001", "10001", "10001"],
    "O": ["01110", "10001", "10001", "10001", "10001", "10001", "01110"],
    "R": ["11110", "10001", "10001", "11110", "10100", "10010", "10001"],
    "S": ["01111", "10000", "10000", "01110", "00001", "00001", "11110"],
    "T": ["11111", "00100", "00100", "00100", "00100", "00100", "00100"],
    " ": ["000", "000", "000", "000", "000", "000", "000"],
}


def logo():
    lines = ["REALITIES", "OF DREAMS"]
    px = 6  # size of one font pixel
    rows = []
    for line in lines:
        cols = []
        for ch in line:
            g = FONT[ch]
            for c in range(len(g[0])):
                cols.append([g[r][c] == "1" for r in range(7)])
            cols.append([False] * 7)
        rows.append(cols[:-1])
    width = max(len(r) for r in rows) * px + 24
    height = (7 * px) * len(rows) + px * 3 * (len(rows) - 1) + 24
    a = np.zeros((height, width, 4), dtype=np.uint8)
    top = 12
    for li, cols in enumerate(rows):
        left = (width - len(cols) * px) // 2
        for ci, col in enumerate(cols):
            for r, on in enumerate(col):
                if not on:
                    continue
                for yy in range(px):
                    for xx in range(px):
                        y = top + r * px + yy
                        x = left + ci * px + xx
                        t = (r * px + yy) / (7 * px)
                        # gold to amber gradient with a bright bevel on the upper-left of each pixel
                        c = lerp(rgba("fff1b8"), rgba("d98a1e"), t)
                        if yy == 0 or xx == 0:
                            c = lerp(c, (255, 255, 240, 255), 0.6)
                        if yy == px - 1 or xx == px - 1:
                            c = lerp(c, (120, 60, 10, 255), 0.5)
                        a[y, x] = c
        top += 7 * px + px * 3
    # dark outline and drop shadow
    alpha = a[:, :, 3] > 0
    out = np.zeros_like(a)
    for dy in range(-3, 6):
        for dx in range(-3, 6):
            if dx * dx + dy * dy > 18 and not (dy > 2 and dx > 2):
                continue
            shifted = np.roll(np.roll(alpha, dy, axis=0), dx, axis=1)
            col = (40, 18, 6, 255) if (dy <= 3 and dx <= 3) else (0, 0, 0, 150)
            out[shifted & (out[:, :, 3] == 0)] = col
    out[alpha] = a[alpha]
    save(out, "logo.png")


def panel():
    """9-slice panel used behind dialogue subtitles and story HUD elements (32x32, 8px border)."""
    s = 32
    a = np.zeros((s, s, 4), dtype=np.uint8)
    for y in range(s):
        for x in range(s):
            edge = min(x, y, s - 1 - x, s - 1 - y)
            if edge == 0:
                a[y, x] = (0, 0, 0, 0)
            elif edge == 1:
                a[y, x] = (196, 146, 64, 255)
            elif edge == 2:
                a[y, x] = (92, 62, 30, 255)
            else:
                a[y, x] = (14, 12, 22, 200)
    for cx, cy in ((3, 3), (s - 4, 3), (3, s - 4), (s - 4, s - 4)):
        a[cy, cx] = (255, 226, 150, 255)
    save(a, "panel.png")


def gateway_icon():
    """64x64 ring of the ancient gateway, used as the menu's saga emblem and loading spinner."""
    s = 64
    a = np.zeros((s, s, 4), dtype=np.uint8)
    c = s / 2 - 0.5
    for y in range(s):
        for x in range(s):
            d = math.hypot(x - c, y - c)
            if 24 <= d <= 28:
                a[y, x] = (110, 210, 230, 255) if d < 27 else (40, 80, 110, 255)
            elif 20 <= d < 24:
                a[y, x] = (70, 66, 88, 255)
    for k in range(6):
        ang = k * math.tau / 6 - math.pi / 2
        gx, gy = int(round(c + math.cos(ang) * 22)), int(round(c + math.sin(ang) * 22))
        for y in range(gy - 2, gy + 3):
            for x in range(gx - 2, gx + 3):
                a[y, x] = (255, 214, 120, 255) if abs(x - gx) + abs(y - gy) <= 1 else (120, 90, 50, 255)
    save(a, "gateway.png")


if __name__ == "__main__":
    sky()
    sun()
    clouds()
    ridge_layer("mountains_far.png", 1, 150, 38, rgba("7a6c9c"), rgba("5a5480"), snow=rgba("efe6f2"), gateway=True)
    ridge_layer("mountains_near.png", 2, 110, 26, rgba("4f6a7a"), rgba("37505a"), waterfall=True)
    valley()
    village()
    foreground()
    logo()
    panel()
    gateway_icon()
