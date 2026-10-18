# Asset manifest

Every asset the mod ships, where it came from, and what is still a placeholder. All assets are
inside the mod jar (`src/main/resources/assets/realitiesofdreams/`), so players never download a
separate resource pack. There is no separate resource pack: nothing needs one yet.

Status: **Final** = intended for release. **Original placeholder** = made for this mod, free to
ship, but meant to be replaced by hand-made work. **Not started** = nothing exists yet.

## Interface

| Asset | File | Source | Status |
|---|---|---|---|
| Title graphic "REALITIES OF DREAMS" | `textures/gui/menu/logo.png` | Drawn by `tools/assets/generate_assets.py` (blocky pixel lettering, 3D extrusion, glow) | Original placeholder |
| Mod list logo | `realitiesofdreams_logo.png` (jar root) | Same script | Original placeholder |
| Ancient gateway rings (menu motif) | `textures/gui/menu/gateway_ring_outer.png`, `gateway_ring_inner.png` | Same script; the six civilisation marks are 7x7 pixel glyphs | Original placeholder |
| Menu vignette | `textures/gui/menu/vignette.png` | Same script | Final |
| Menu panorama of Firstbrook at sunrise | `textures/gui/title/background/panorama_0..5.png` | Photographed in the game by the automated playtest (`-Prod.panorama=true`) | Final for Saga 1 (re-shoot when Firstbrook changes) |
| Dialogue subtitle panel | `textures/gui/hud/dialogue_panel.png` | Same script | Original placeholder |
| Quest tracker panel | `textures/gui/hud/objective_panel.png` | Same script | Original placeholder |
| Journal and investigation screens | — | Steve's journal is a vanilla written book | Not started |
| Cinematic transitions | — | Drawn in code (letterbox, fades, title cards) | Final (code) |

## Audio

| Sound event | File | Source | Status |
|---|---|---|---|
| `realitiesofdreams:music.menu` "First Light over Firstbrook" (1:46) | `sounds/music/menu_first_light.ogg` | Synthesised by the script: D-mixolydian pads, bell theme, wind, convolution reverb | Original placeholder (replace with a composed score) |
| `ui.menu_select` | `sounds/ui/menu_select.ogg` | Synthesised | Original placeholder |
| `ui.menu_transition` | `sounds/ui/menu_transition.ogg` | Synthesised | Original placeholder |
| `ui.story_begin` | `sounds/ui/story_begin.ogg` | Synthesised | Original placeholder |
| `ui.quest_update` | `sounds/ui/quest_update.ogg` | Synthesised | Original placeholder |
| Ambient story sounds (water, birds, hammer) | — | Vanilla sound events played by cinematics | Placeholder (vanilla) |
| Voice acting | — | Every line has a speaker id and subtitle; no recordings | Not started |

No audio or artwork from other games is used. Vanilla Minecraft sounds are referenced by id, not
copied.

## Characters

| Asset | Source | Status |
|---|---|---|
| 11 named Saga 1 skins | The story owner's collected skins, unchanged (see `docs/CHARACTER_REGISTRY.md`) | Final |
| Primordial Steve as the player | The collected classic Steve skin, applied by a client-side render override | Final |
| 55 Saga 1 NPC skins (villagers, farmers, guards, merchants, faction members, raiders) | Generated from shared uniforms by `tools/skins/` | Original placeholder |
| Primordial Steve — Empowered | Collected skin, not used in Saga 1 | Final (locked until a later saga) |

## World

| Asset | Source | Status |
|---|---|---|
| MC-001 terrain | `Mc001ChunkGenerator` + `Mc001Terrain` (authored shape over vanilla terrain) | Implemented |
| Firstbrook buildings | Code builders driven by `rod-story/sites/firstbrook.json`; any piece can be replaced by a structure template | Implemented |
| Structure templates (`data/realitiesofdreams/structure/firstbrook/*.nbt`) | None yet; the loader is ready | Not started |
| Six-civilisation banners and insignia | Firstbrook uses plain blue Eldervale banners | Not started |
| Unique blocks and props, ancient inscriptions, gateway effects, custom particles | — | Not started (Scenes 006+) |

To regenerate the drawn and synthesised assets: `python3 tools/assets/generate_assets.py`
(needs Python 3 with Pillow and numpy, and ffmpeg with libvorbis).
