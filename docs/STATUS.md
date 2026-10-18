# Development Status

Milestone: **MC-001 vertical slice** (main menu → New Story → Scene 001 → explore Firstbrook →
Save → Continue). Updated 2026-10-18.

**Playable: Scene 001 only, out of Saga 1's 20 scenes.** How to play it:
[PLAYTEST_WINDOWS.md](PLAYTEST_WINDOWS.md). World layout: [WORLD_MC001.md](WORLD_MC001.md).
Assets and placeholders: [ASSETS.md](ASSETS.md).

## How each feature is verified

| Level | Meaning |
|---|---|
| Designed | Planned and written down, no code |
| Implemented | Code written |
| Compiled | Builds into the mod jar |
| Tested | Covered by an automated unit test (`story-core`) or in-game game test (dedicated server) |
| Verified in-game | Exercised in the real Minecraft client by the automated playtest (`ClientAutotest`, run under a virtual display) with screenshots. Not yet a human on Windows. |
| Blocked | Can't be done yet, and why |

## Vertical slice (the requested playtest deliverable)

| # | Feature | Status | Evidence |
|---|---|---|---|
| 1 | Custom main menu: title, subtitle, New Story, Continue Story, Chapter / Saga Selection, Multiplayer, Settings, Credits, Exit Game; panorama of Firstbrook; gateway-ring motif; music; fades; keyboard navigation; language and accessibility buttons; vanilla menu one click away | Verified in-game | `menu_01_title.png`, `menu_04_chapter_select.png`, `menu_05_credits.png` |
| 2 | New Story creates an isolated story save (`saves/realities_of_dreams_mc001`) and opens it; an existing story save is kept as a dated backup; ordinary saves untouched | Verified in-game | autotest presses New Story and the confirm button; log "Previous story save kept as …_backup_…" |
| 2 | Continue Story opens the story save at its checkpoint; disabled (with a tooltip) when there is none | Verified in-game | `menu_03_continue_available.png`, `continue_01_back_in_firstbrook.png`; progress, journal and appearance checked after reload |
| 3 | Automatic MC-001 entry: no world creation screen, no commands, no teleporting; Firstbrook built and the valley generated behind the loading screen | Verified in-game | autotest asserts the player arrives in `realitiesofdreams:mc_001` and the opening starts by itself |
| 4 | Primordial Steve appearance (classic wide model, collected skin, first-person arm and third person, armour layers unaffected); server-assigned and synced to all players; account skin untouched | Verified in-game | `player_01_first_person_arm.png`, `player_02…05`; autotest checks the skin texture and model |
| 5 | Navigable, recognisable Firstbrook with paths connecting every door | Verified in-game | `tour_*.png` (23 views); game test `firstbrookBuilds` |
| 6 | Steve's furnished house (bed, breakfast chest, gear, workstations, armour stand, trophies) | Tested + verified in-game | game test `firstbrookBuilds`; `tour_19_steve_house_inside.png` |
| 7 | Elara and Rowan with their imported skins | Verified in-game | `skins_*.png` line-ups, Scene 001 shots |
| 8 | Village routines: work, patrols, villagers walking between home, work and market and opening doors | Tested + verified in-game (movement); door use not separately measured | game test `firstbrookCharactersMove`; autotest checks a routine villager moved |
| 9 | Automatic Scene 001 conversations and subtitles (no clicking) | Tested + verified in-game | `scene001PlaysThroughInFirstbrook`, `Scene001PlaythroughTest`, `s001_*.png` |
| 10 | Scene 001 quest and completion (rewards, journal, unlocks) | Tested + verified in-game | same |
| 11 | Save and reload (mid-scene and after completion) | Tested + verified in-game | `Scene001PlaythroughTest` (mid-scene), autotest Save and Quit → Continue |
| 12 | Essential textures, UI assets, music and sounds packaged in the mod jar | Verified in-game | [ASSETS.md](ASSETS.md) lists every file and which are placeholders |

## World

| Feature | Status | Notes |
|---|---|---|
| MC-001 terrain: authored shape over vanilla generation (caves, ores, biomes), blended into natural land | Tested + verified in-game | `TerrainTest`, `DimensionDataTest`; `tour_01`, `tour_02`, `tour_14`–`18` |
| Fixed authored biomes around Firstbrook | Verified in-game | plains / meadow / taiga / forest / sunflower plains |
| River, waterfall, northern hills and trail, ridge ruin, Southmere lake, mountains | Verified in-game | `tour_14`–`18` |
| Firstbrook placement registry and modular builders (houses with pitched roofs, interiors per household) | Tested + verified in-game | `MenuContentTest.firstbrookLayoutIsConsistent` (no overlaps, nobody stands in a wall) |
| Elder Rowan's house and council fire, Elara's home, Orin's workshop and house, Bram's guard station, watchtower, gate, storehouse, market, bakery, two-storey house, farmhouse, fields, barn, animal pens, training ground, woodcutter's camp, construction site, dock | Verified in-game | `tour_*.png` |
| Structure-template override per piece | Implemented, compiled | No templates authored yet, so not exercised |
| Protected story blocks (doors, bed, well, lantern, bridge, gate, ruin) against survival breaking/placing and explosions | Implemented, compiled | Not covered by an automated test yet |
| Chunk-aware NPCs, no permanent loading | Implemented | NPCs only exist in loaded chunks; nothing is force-loaded outside game tests |
| Awakening Valley camps, damaged bridge, abandoned campsite | Designed | [WORLD_MC001.md](WORLD_MC001.md) |
| Ancient Northern Monument | Designed | Scenes 006–007 |
| Sylvaris Forest Outpost | Designed | Scene 010 |
| Valebridge (city, castle, throne room, council, archive, harbour) | Designed | Scenes 012–017 |
| Stonecrown, Solkar, Veyrath, Thalassia presences | Designed | Scenes 013–017 |
| Ancient Gateway Chamber | Designed | Scenes 018–019 |

## Story, characters and engine (carried over, still passing)

| Feature | Status |
|---|---|
| Character registry: 151 entries, 11 named Saga 1 skins pixel-identical to the originals, 55 generated Saga 1 NPCs | Tested |
| Saga 1 casting rules (no later-saga characters, no Empowered form) | Tested |
| Dialogue graphs, proximity triggers, walking conversations, companion chatter, cinematics, checkpoints | Tested + verified in-game |
| Logging out mid-conversation or mid-cinematic cannot soft-lock a scene | Tested |
| Development controls need operator permission | Implemented |

## Not started or blocked

| Feature | Status | Why / next step |
|---|---|---|
| Scenes 002–020 | Not started | Next milestone; Scene 002's opening line already flows from Rowan's arrival |
| NPCs sleeping and waking on a day/night cycle | Blocked | MC-001 is fixed at sunrise; needs the per-scene time-of-day system planned for Scene 008 |
| NPCs reacting to danger, rescuing civilians, combat | Not started | Needs Scene 011's raiders |
| Voice acting | Scene 001 voiced (20 lines); later scenes not yet | Plays on the Voice/Speech volume slider; a line without a recording shows its subtitle only |
| Composed music score, hand-drawn title art | Not started | Original synthesised/generated placeholders ship now |
| Journal and investigation screens | Not started | Steve's journal is a vanilla written book |
| Six-civilisation banners, insignia, ancient inscriptions, gateway effects, custom particles | Not started | Scenes 006+ |
| Controller support | Blocked | Minecraft Java has no built-in controller support |
| Co-op story sync | Partly | Progress is per player and server-authoritative; in a story world only the host (or a dedicated story server's first player) is Primordial Steve; shared NPCs follow whoever triggered them |
| Human playtest on Windows | Waiting | Everything above marked "verified in-game" was played by the automated client, not a person |

## Open questions for the story owner

- **Scene 001 script.** Uses the Saga 1 screenplay's lines verbatim; changes are data edits in
  `rod-story/scenes/mc001_s001.json`.
- **Firstbrook's extra residents.** The Millers, the bakery, the weaver and the Hollow family's new
  house are set dressing invented for the slice; rename or remove freely in `rod-story/sites/firstbrook.json`.
- **Duplicate skin:** Oblivion Steve and Voidwalker Steve (MC-073) use the identical skin file.
- **MC-002:** both Prehistoric Steve and Witch Alex are labelled MC-002. Is that intended?
- **Witch Alex's skin** came from Planet Minecraft; check its licence before a public release.
- **Unconfirmed roles:** Draxson, Evaulx, Temporal Custodian and MysteryDweller's home universe.

See [CHARACTER_REGISTRY.md](CHARACTER_REGISTRY.md) for the full registry.
