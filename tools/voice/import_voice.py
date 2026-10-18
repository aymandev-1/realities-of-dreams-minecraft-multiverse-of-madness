#!/usr/bin/env python3
"""Import recorded voice lines into the mod.

Usage: import_voice.py takes.json

takes.json is a list of {"speaker", "text", "direction", "audio"} where "audio" is a local
.mp3/.wav path or an https URL (for example an ElevenLabs download link). Each take is converted
to mono Ogg Vorbis under assets/rod_voice/sounds/<speaker>/<key>.ogg, recorded in lines.json, and
assets/rod_voice/sounds.json is rebuilt from lines.json.

The key must match VoiceKey.of() in story-core: "v_" + the first 12 hex digits of
sha1(speaker + "|" + text). Change a line's text and its old recording stops playing, which is
what we want: the subtitle and the voice can never disagree.
"""
import hashlib
import json
import pathlib
import subprocess
import sys
import tempfile
import urllib.request

ROOT = pathlib.Path(__file__).resolve().parents[2]
ASSETS = ROOT / "src/main/resources/assets/rod_voice"
LINES = pathlib.Path(__file__).resolve().parent / "lines.json"


def key(speaker, text):
    return "v_" + hashlib.sha1(f"{speaker}|{text}".encode("utf-8")).hexdigest()[:12]


def ticks_for(text):
    """Mirror of LineTiming.ticksFor: how long a dialogue line stays up by default."""
    return max(50, min(260, 30 + round(len(text) * 1.25)))


def duration(path):
    out = subprocess.run(["ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "csv=p=0", str(path)],
                         check=True, capture_output=True, text=True).stdout
    return round(float(out.strip()), 2)


def main():
    takes = json.loads(pathlib.Path(sys.argv[1]).read_text(encoding="utf-8"))
    lines = json.loads(LINES.read_text(encoding="utf-8")) if LINES.exists() else []
    by_key = {l["key"]: l for l in lines}
    for t in takes:
        k = key(t["speaker"], t["text"])
        out = ASSETS / "sounds" / t["speaker"] / f"{k}.ogg"
        out.parent.mkdir(parents=True, exist_ok=True)
        with tempfile.TemporaryDirectory() as tmp:
            src = t["audio"]
            if src.startswith("https://"):
                local = pathlib.Path(tmp) / "take.mp3"
                urllib.request.urlretrieve(src, local)
                src = local
            subprocess.run(["ffmpeg", "-loglevel", "error", "-y", "-i", str(src), "-c:a", "libvorbis", "-q:a", "4",
                            "-ac", "1", str(out)], check=True)
        secs = duration(out)
        if secs * 20 > ticks_for(t["text"]):
            print(f"warning: {k} runs {secs}s, longer than its default subtitle time; give the line a duration",
                  file=sys.stderr)
        by_key[k] = {"key": k, "speaker": t["speaker"], "text": t["text"], "direction": t.get("direction", ""),
                     "seconds": secs}
        print(f"{k}  {secs:5.2f}s  {t['speaker']}: {t['text']}")
    lines = sorted(by_key.values(), key=lambda l: (l["speaker"], l["text"]))
    LINES.write_text(json.dumps(lines, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    sounds = {l["key"]: {"sounds": [{"name": f"rod_voice:{l['speaker']}/{l['key']}"}]} for l in lines}
    (ASSETS / "sounds.json").write_text(json.dumps(sounds, indent=2) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
