#!/usr/bin/env python3
"""Generates the mod's original menu art and audio.

Everything here is drawn or synthesised from scratch by this script, so the assets are original
and free of third-party rights. Re-run it after changing it:

    python3 tools/assets/generate_assets.py

Writes into src/main/resources/assets/realitiesofdreams/ (textures/gui/menu, sounds) and the mod
list logo at src/main/resources/realitiesofdreams_logo.png. The menu panorama is not made here: it
is photographed inside the game (see ClientAutotest, `-Drod.panorama=true`).
"""
import math
import os
import subprocess
import sys
import tempfile
import wave

import numpy as np
from PIL import Image, ImageDraw, ImageFilter

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
ASSETS = os.path.join(ROOT, "src", "main", "resources", "assets", "realitiesofdreams")
GUI = os.path.join(ASSETS, "textures", "gui", "menu")
HUD = os.path.join(ASSETS, "textures", "gui", "hud")
SOUNDS = os.path.join(ASSETS, "sounds")
RNG = np.random.default_rng(1001)

# --------------------------------------------------------------------------- pixel font

GLYPHS = {
    "R": ["####.", "#...#", "#...#", "####.", "#.#..", "#..#.", "#...#"],
    "E": ["#####", "#....", "#....", "####.", "#....", "#....", "#####"],
    "A": [".###.", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"],
    "L": ["#....", "#....", "#....", "#....", "#....", "#....", "#####"],
    "I": ["###", ".#.", ".#.", ".#.", ".#.", ".#.", "###"],
    "T": ["#####", "..#..", "..#..", "..#..", "..#..", "..#..", "..#.."],
    "S": [".####", "#....", "#....", ".###.", "....#", "....#", "####."],
    "O": [".###.", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."],
    "F": ["#####", "#....", "#....", "####.", "#....", "#....", "#...."],
    "D": ["####.", "#...#", "#...#", "#...#", "#...#", "#...#", "####."],
    "M": ["#...#", "##.##", "#.#.#", "#.#.#", "#...#", "#...#", "#...#"],
    " ": ["...", "...", "...", "...", "...", "...", "..."],
}


def word_cells(text):
    """Cells (col, row) filled by the text, and its width in cells (one blank column between letters)."""
    cells, x = [], 0
    for i, ch in enumerate(text):
        g = GLYPHS[ch]
        for r, row in enumerate(g):
            for c, v in enumerate(row):
                if v == "#":
                    cells.append((x + c, r))
        x += len(g[0]) + (1 if i < len(text) - 1 else 0)
    return cells, x


def draw_blocky(img, text, ox, oy, cell, top_rgb, bottom_rgb, depth):
    """Minecraft-logo style lettering: textured faces, bevelled cells, dark 3D extrusion and outline."""
    cells, _ = word_cells(text)
    px = img.load()
    filled = set(cells)
    w, h = img.size
    # Outline + extrusion first (behind the faces).
    for k in range(depth, 0, -1):
        shade = int(30 + 40 * (1 - k / depth))
        for (c, r) in cells:
            x0, y0 = ox + c * cell + k, oy + r * cell + k
            for y in range(y0 - 2, y0 + cell + 2):
                for x in range(x0 - 2, x0 + cell + 2):
                    if 0 <= x < w and 0 <= y < h:
                        px[x, y] = (shade // 2 + 10, shade // 3 + 6, shade // 4 + 4, 255)
    for (c, r) in cells:
        x0, y0 = ox + c * cell, oy + r * cell
        for y in range(y0 - 2, y0 + cell + 2):
            for x in range(x0 - 2, x0 + cell + 2):
                if 0 <= x < w and 0 <= y < h and px[x, y][3] < 255:
                    px[x, y] = (18, 12, 8, 255)
    # Faces: vertical gradient, per-pixel grain in 2x2 blocks, bevel on open edges.
    rows = 7
    for (c, r) in cells:
        x0, y0 = ox + c * cell, oy + r * cell
        for y in range(cell):
            t = (r * cell + y) / (rows * cell - 1)
            base = [top_rgb[i] + (bottom_rgb[i] - top_rgb[i]) * t for i in range(3)]
            for x in range(cell):
                grain = 1 + RNG.normal(0, 0.045) if (x // 2 + y // 2) % 1 == 0 else 1
                f = grain
                edge = max(2, cell // 7)
                if y < edge and (c, r - 1) not in filled:
                    f *= 1.18
                if x < edge and (c - 1, r) not in filled:
                    f *= 1.10
                if y >= cell - edge and (c, r + 1) not in filled:
                    f *= 0.72
                if x >= cell - edge and (c + 1, r) not in filled:
                    f *= 0.80
                col = tuple(max(0, min(255, int(v * f))) for v in base)
                px[x0 + x, y0 + y] = col + (255,)


def logo():
    W, H = 1024, 288
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    _, w1 = word_cells("REALITIES")
    _, w2 = word_cells("OF DREAMS")
    c1 = 20
    c2 = 13
    x1 = (W - w1 * c1) // 2
    x2 = (W - w2 * c2) // 2
    draw_blocky(img, "REALITIES", x1, 14, c1, (255, 236, 170), (204, 132, 40), 9)
    draw_blocky(img, "OF DREAMS", x2, 14 + 7 * c1 + 22, c2, (226, 236, 255), (120, 132, 176), 6)
    # A soft golden glow behind the letters.
    alpha = img.split()[3]
    glow = Image.new("RGBA", (W, H), (255, 196, 90, 0))
    glow.putalpha(alpha.filter(ImageFilter.GaussianBlur(14)).point(lambda a: int(a * 0.45)))
    out = Image.alpha_composite(glow, img)
    out.save(os.path.join(GUI, "logo.png"))
    # Mod list logo: the title on a dark band.
    band = Image.new("RGBA", (W, H + 40), (14, 11, 18, 255))
    band.alpha_composite(out, (0, 20))
    band.resize((512, (H + 40) // 2), Image.LANCZOS).save(os.path.join(ROOT, "src", "main", "resources", "realitiesofdreams_logo.png"))


# --------------------------------------------------------------------------- gateway rings

SYMBOLS = {  # the six civilisations' marks, 7x7
    "eldervale": [".#.#.#.", ".#####.", ".#####.", "..###..", "..###..", ".#####.", "......."],   # crown
    "stonecrown": ["...#...", "..###..", ".#####.", "##.#.##", "#######", "#######", "......."],  # mountain
    "sylvaris": ["...#...", "..###..", ".#####.", "#######", "..###..", "...#...", "...#..."],    # tree
    "solkar": ["#..#..#", ".#.#.#.", "..###..", "#######", "..###..", ".#.#.#.", "#..#..#"],      # sun
    "veyrath": ["...#...", "..##...", "..###..", ".####..", ".#####.", "#######", ".#####."],     # flame
    "thalassia": ["..#....", ".#.#..#", "#...##.", ".......", "..#....", ".#.#..#", "#...##."],   # waves
}


def px_round(v):
    return int(math.floor(v + 0.5))


def ring(path, size, r_in, r_out, glyphs, ticks, color, glow):
    """A ring of the ancient gateway: carved stone band, engraved groove and runes, glowing symbol plates."""
    s = 256
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    px = img.load()
    c = (s - 1) / 2
    mid = (r_in + r_out) / 2
    names = list(SYMBOLS)
    for y in range(s):
        for x in range(s):
            d = math.hypot(x - c, y - c)
            if not r_in <= d <= r_out:
                continue
            ang = (math.atan2(y - c, x - c) + math.pi) / (2 * math.pi)
            f = 1.0
            if d < r_in + 2 or d > r_out - 2:
                f = 0.62                                  # chamfered rims
            elif abs(d - mid) < 1.0:
                f = 0.55                                  # engraved centre groove
            seg = ang * ticks
            if abs(seg - round(seg)) < 0.06 and not abs(d - mid) < 1.0:
                f = 0.5                                   # segment joints
            rune = int(ang * ticks * 3) % 5
            if rune in (1, 3) and (abs(d - (mid + (r_out - r_in) / 4)) < 0.8):
                f = 0.7                                   # engraved runes on the outer track
            grain = 1 + RNG.normal(0, 0.035)
            px[x, y] = tuple(max(0, min(255, int(v * f * grain))) for v in color) + (255,)
    for i in range(glyphs):
        ang = -math.pi / 2 + i * 2 * math.pi / glyphs
        mx, my = c + math.cos(ang) * mid, c + math.sin(ang) * mid
        # Dark plate, then the civilisation's mark in glowing pixels (2x scale).
        for gy in range(-9, 9):
            for gx in range(-9, 9):
                x, y = px_round((mx + gx)), px_round((my + gy))
                if abs(gx) + abs(gy) <= 13 and 0 <= x < s and 0 <= y < s:
                    px[x, y] = (40, 34, 30, 255)
        pat = SYMBOLS[names[i % len(names)]]
        for gy, row in enumerate(pat):
            for gx, v in enumerate(row):
                if v != "#":
                    continue
                for oy in range(2):
                    for ox in range(2):
                        x, y = px_round((mx - 7 + gx * 2 + ox)), px_round((my - 7 + gy * 2 + oy))
                        if 0 <= x < s and 0 <= y < s:
                            px[x, y] = glow + (255,)
        # A chevron light just outside the ring.
        cx2, cy2 = c + math.cos(ang) * (r_out + 4), c + math.sin(ang) * (r_out + 4)
        for gy in range(-2, 3):
            for gx in range(-2, 3):
                if abs(gx) + abs(gy) <= 2:
                    x, y = px_round((cx2 + gx)), px_round((cy2 + gy))
                    if 0 <= x < s and 0 <= y < s:
                        px[x, y] = glow + (255,)
    big = img.resize((size, size), Image.NEAREST)
    halo = Image.new("RGBA", (size, size), glow + (0,))
    halo.putalpha(big.split()[3].filter(ImageFilter.GaussianBlur(9)).point(lambda a: int(a * 0.45)))
    Image.alpha_composite(halo, big).save(path)


def vignette():
    s = 256
    y, x = np.mgrid[0:s, 0:s]
    d = np.hypot((x - s / 2) / (s / 2), (y - s / 2) / (s / 2))
    a = np.clip((d - 0.55) / 0.75, 0, 1) ** 1.6 * 235
    arr = np.zeros((s, s, 4), np.uint8)
    arr[..., 3] = a.astype(np.uint8)
    Image.fromarray(arr, "RGBA").save(os.path.join(GUI, "vignette.png"))


def hud_panels():
    """Nine-slice-free HUD frames: dialogue panel and objective panel (drawn stretched)."""
    os.makedirs(HUD, exist_ok=True)
    # Dialogue panel 256x64: dark translucent body, gold top rule, faint corner flourishes.
    w, h = 256, 64
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    for yy in range(h):
        a = int(200 - 40 * (yy / h))
        d.line([(0, yy), (w - 1, yy)], fill=(14, 12, 20, a))
    d.line([(0, 0), (w - 1, 0)], fill=(232, 184, 74, 255))
    d.line([(0, 1), (w - 1, 1)], fill=(150, 112, 40, 200))
    for cx in (2, w - 3):
        for k in range(6):
            d.point((cx + (k if cx < w / 2 else -k), 3 + k), fill=(232, 184, 74, 160 - k * 20))
    img.save(os.path.join(HUD, "dialogue_panel.png"))
    # Quest notification banner 256x32.
    w, h = 256, 32
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    for xx in range(w):
        a = int(210 * (1 - (xx / w) ** 2))
        d.line([(xx, 0), (xx, h - 1)], fill=(14, 12, 20, a))
    d.rectangle([0, 0, 2, h - 1], fill=(232, 184, 74, 255))
    img.save(os.path.join(HUD, "objective_panel.png"))


# --------------------------------------------------------------------------- audio

SR = 44100


def write_ogg(name, samples, stereo=False, quality=4):
    samples = np.asarray(samples, dtype=np.float64)
    peak = np.max(np.abs(samples)) or 1.0
    samples = samples / peak * 0.89
    loud = np.nonzero(np.abs(samples) > 0.002)[0]
    if len(loud):
        samples = samples[: min(len(samples), loud[-1] + 1 + (loud[-1] + 1) % (2 if stereo else 1))]
    pcm = (samples * 32767).astype("<i2")
    with tempfile.TemporaryDirectory() as tmp:
        wav = os.path.join(tmp, "s.wav")
        with wave.open(wav, "wb") as f:
            f.setnchannels(2 if stereo else 1)
            f.setsampwidth(2)
            f.setframerate(SR)
            f.writeframes(pcm.tobytes())
        out = os.path.join(SOUNDS, name + ".ogg")
        os.makedirs(os.path.dirname(out), exist_ok=True)
        subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", wav, "-c:a", "libvorbis", "-q:a", str(quality), out], check=True)


def env(n, attack, release, sustain_level=1.0):
    e = np.ones(n) * sustain_level
    a = min(n, int(attack * SR))
    r = min(n - a, int(release * SR))
    if a:
        e[:a] = np.linspace(0, sustain_level, a) ** 1.5
    if r:
        e[n - r:] *= np.linspace(1, 0, r) ** 2
    return e


def midi(m):
    return 440.0 * 2 ** ((m - 69) / 12)


def lowpass(x, cutoff, rolloff=2.0):
    """Gentle FFT low-pass (whole signal at once)."""
    n = len(x)
    spec = np.fft.rfft(x)
    f = np.fft.rfftfreq(n, 1 / SR)
    spec *= 1 / (1 + (f / cutoff) ** (2 * rolloff))
    return np.fft.irfft(spec, n)


def reverb(x, seconds=3.2, wet=0.35, seed=7):
    """Convolution reverb with a synthetic, slightly diffuse tail."""
    r = np.random.default_rng(seed)
    n = int(seconds * SR)
    t = np.arange(n) / SR
    ir = r.normal(0, 1, n) * np.exp(-t * 6.9 / seconds)
    ir = lowpass(ir, 5200, 1.0)
    ir[0] = 0
    ir /= np.sqrt(np.sum(ir ** 2))
    size = 1 << int(math.ceil(math.log2(len(x) + n)))
    y = np.fft.irfft(np.fft.rfft(x, size) * np.fft.rfft(ir, size), size)[: len(x) + n]
    out = np.zeros(len(x) + n)
    out[: len(x)] += x * (1 - wet)
    out += y * wet * 2.2
    return out


def pad_note(freq, dur, detune=0.004, bright=1.0):
    n = int(dur * SR)
    t = np.arange(n) / SR
    s = np.zeros(n)
    for k, d in enumerate((-detune, 0.0, detune)):
        f = freq * (1 + d)
        # Soft "string pad": a few harmonics with falling weights.
        for h, wgt in ((1, 1.0), (2, 0.42 * bright), (3, 0.2 * bright), (4, 0.09 * bright), (5, 0.05 * bright)):
            s += wgt * np.sin(2 * np.pi * f * h * t + RNG.uniform(0, 6.28))
    vib = 1 + 0.003 * np.sin(2 * np.pi * 4.6 * t)
    return s * vib * env(n, dur * 0.35, dur * 0.4) / 6


def bell(freq, dur, decay=2.2):
    n = int(dur * SR)
    t = np.arange(n) / SR
    s = (np.sin(2 * np.pi * freq * t) + 0.45 * np.sin(2 * np.pi * freq * 2.01 * t) * np.exp(-t * 2)
         + 0.25 * np.sin(2 * np.pi * freq * 3.98 * t) * np.exp(-t * 4) + 0.12 * np.sin(2 * np.pi * freq * 5.4 * t) * np.exp(-t * 7))
    return s * np.exp(-t * decay) * env(n, 0.004, 0.05)


def menu_music():
    """'First Light over Firstbrook': slow D-mixolydian pads, a celesta-like theme, wind and a low drone."""
    bar = 6.4
    chords = [  # (bass, chord tones) in MIDI
        (38, [62, 66, 69, 73, 76]),   # Dmaj9
        (35, [59, 62, 66, 69, 74]),   # Bm9
        (43, [55, 62, 67, 71, 74]),   # G add9
        (45, [57, 62, 64, 69, 72]),   # A7sus4
        (38, [62, 66, 69, 72, 76]),   # D9 (mixolydian colour)
        (36, [60, 64, 67, 71, 74]),   # Cmaj9
        (43, [55, 59, 62, 67, 74]),   # G
        (45, [57, 61, 64, 69, 76]),   # A
    ]
    loops = 2
    total = bar * len(chords) * loops + 6
    n = int(total * SR)
    L = np.zeros(n)
    R = np.zeros(n)

    def add(sig, start, pan=0.0, gain=1.0):
        i = int(start * SR)
        j = min(n, i + len(sig))
        lg, rg = math.cos((pan + 1) * math.pi / 4), math.sin((pan + 1) * math.pi / 4)
        L[i:j] += sig[: j - i] * gain * lg
        R[i:j] += sig[: j - i] * gain * rg

    theme = [  # (beat offset in the 8-bar phrase, MIDI, length) — a rising, questioning figure
        (0, 74, 1.5), (1.5, 76, 0.5), (2, 78, 2), (4, 81, 2), (6, 79, 1), (7, 78, 1),
        (8, 76, 3), (11, 74, 1), (12, 71, 4),
        (16, 74, 1.5), (17.5, 78, 0.5), (18, 81, 2), (20, 83, 2), (22, 81, 1), (23, 79, 1),
        (24, 78, 3), (27, 76, 1), (28, 74, 4),
        (32, 72, 2), (34, 74, 1), (35, 76, 1), (36, 79, 4), (40, 78, 2), (42, 76, 2), (44, 74, 4),
        (48, 71, 2), (50, 73, 2), (52, 76, 4), (56, 78, 3), (59, 76, 1), (60, 74, 4),
    ]
    beat = bar / 4
    for loop in range(loops):
        t0 = loop * bar * len(chords)
        for i, (bass, tones) in enumerate(chords):
            start = t0 + i * bar
            for k, m in enumerate(tones):
                add(pad_note(midi(m), bar * 1.25, bright=0.8), start, pan=-0.6 + 1.2 * k / (len(tones) - 1), gain=0.55)
            add(pad_note(midi(bass), bar * 1.2, detune=0.002, bright=0.5), start, gain=0.7)
            add(pad_note(midi(bass - 12), bar * 1.2, detune=0.001, bright=0.2), start, gain=0.45)
        if loop >= 0:
            for off, m, length in theme:
                st = t0 + off * beat
                if st < total - 4:
                    add(bell(midi(m), length * beat + 2.5), st, pan=0.25 * math.sin(off), gain=0.33 if loop == 0 else 0.4)
                    add(bell(midi(m + 12), length * beat + 1.5, decay=3.5), st + 0.01, pan=-0.3, gain=0.06)
        # Distant hammer-on-anvil chimes, like the blacksmith in the opening.
        for b in range(0, len(chords) * 4, 7):
            add(bell(midi(86), 2.0, decay=5.0), t0 + b * beat + 0.37, pan=0.7, gain=0.05)
    # Wind: filtered noise breathing slowly.
    wind = lowpass(RNG.normal(0, 1, n), 700, 1.5)
    wind *= (0.5 + 0.5 * np.sin(2 * np.pi * np.arange(n) / SR / 11.0)) * 0.05
    L += wind
    R += np.roll(wind, 1800)
    fade = env(n, 3.5, 6.0)
    L, R = reverb(L * fade, 3.8, 0.42, 1), reverb(R * fade, 3.8, 0.42, 2)
    m = min(len(L), len(R))
    write_ogg("music/menu_first_light", np.stack([L[:m], R[:m]], axis=1).reshape(-1), stereo=True, quality=4)


def ui_sounds():
    # Select: a soft wooden tick with a short tonal body.
    n = int(0.16 * SR)
    t = np.arange(n) / SR
    click = RNG.normal(0, 1, n) * np.exp(-t * 90)
    tone = np.sin(2 * np.pi * 880 * t) * np.exp(-t * 32) * 0.7 + np.sin(2 * np.pi * 1320 * t) * np.exp(-t * 45) * 0.3
    write_ogg("ui/menu_select", lowpass(click * 0.35 + tone, 5000))
    # Transition: a breathy whoosh rising then settling.
    n = int(0.9 * SR)
    t = np.arange(n) / SR
    noise = RNG.normal(0, 1, n)
    sweep = np.concatenate([lowpass(noise[i:i + 2205], 300 + 3500 * math.sin(math.pi * i / n)) for i in range(0, n, 2205)])[:n]
    write_ogg("ui/menu_transition", sweep * np.sin(np.pi * t / t[-1]) ** 2)
    # Story begin: a swelling D major chord with a bell on top.
    dur = 3.2
    n = int(dur * SR)
    sig = np.zeros(n)
    for m in (50, 57, 62, 66, 69, 74):
        sig += pad_note(midi(m), dur, bright=0.7)[:n]
    sig *= np.linspace(0.2, 1, n)
    b = bell(midi(86), 2.4)
    sig[int(0.9 * SR):int(0.9 * SR) + len(b)] += b[: n - int(0.9 * SR)] * 0.6
    write_ogg("ui/story_begin", reverb(sig, 2.5, 0.35)[: int((dur + 1.5) * SR)])
    # Quest update: two rising chimes.
    sig = np.zeros(int(1.6 * SR))
    for st, m in ((0.0, 79), (0.14, 86)):
        b = bell(midi(m), 1.3, decay=3.0)
        i = int(st * SR)
        sig[i:i + len(b)] += b[: len(sig) - i]
    write_ogg("ui/quest_update", reverb(sig, 1.6, 0.3)[: int(2.0 * SR)])


def main():
    os.makedirs(GUI, exist_ok=True)
    os.makedirs(SOUNDS, exist_ok=True)
    which = set(sys.argv[1:]) or {"art", "audio"}
    if "art" in which:
        logo()
        ring(os.path.join(GUI, "gateway_ring_outer.png"), 512, 98, 120, 6, 36, (226, 214, 186), (120, 236, 255))
        ring(os.path.join(GUI, "gateway_ring_inner.png"), 512, 92, 110, 0, 24, (206, 192, 166), (120, 236, 255))
        vignette()
        hud_panels()
        print("art written")
    if "audio" in which:
        ui_sounds()
        menu_music()
        print("audio written")


if __name__ == "__main__":
    main()
