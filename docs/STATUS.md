# Development Status

Milestone: first in-game playtest of MC-001 Scene 001. Updated 2026-10-18.

**Playable: Scene 001 only, out of Saga 1's 20 scenes.** Scenes 002–020 are not implemented; Scene 001
ends by pointing at an unreleased Scene 002. How to play it: [PLAYTEST_WINDOWS.md](PLAYTEST_WINDOWS.md).

Legend: **Verified** = covered by an automated test that passes in CI. **Implemented** = code is
written and compiles, but nobody has played it yet on a real client. **Not started** = nothing yet.

## Verified

| Feature | How it is verified |
|---|---|
| Character registry: all 76 collected skins plus Reuben, 151 entries with 20 generated TMMA skins and 55 generated Saga 1 NPCs | `ContentTest` (story-core) and `storyContentIsValid` game test |
| Identity corrections: Jesse is MC-200, Movie Steve is MC-300, Reuben is Jesse's pig (vanilla pig model) | `ContentTest.ownerIdentityCorrectionsAreApplied` |
| Every player-model character has a 64x64 texture (legacy 64x32 skins converted the way Minecraft does) | `ContentTest.everySkinTextureExists` |
| Generated TMMA skins keep the uniform: every pixel below the head is identical to the template | Assertion in `tools/skins/tmma_faces.py`, run by `build_registry.py` |
| The 11 named Saga 1 skins match your originals pixel for pixel (only hidden base-layer gaps made opaque) | `validate_named` in `build_registry.py`; results table in `docs/CHARACTER_REGISTRY.md` |
| Generated Saga 1 NPCs: each type shares one outfit exactly, and no two share a face | `check_shared_outfit` and the uniqueness check in `tools/skins/npc_types.py` |
| Saga 1 scenes can only use characters cast in Saga 1 (blocks later-saga characters and the Empowered form) | `ContentTest.saga1CastIsTheElevenNamedSkinsPlusGeneratedNpcs`, `ContentTest.aSaga1SceneCannotUseALaterSagaCharacter` |
| Universe IDs MC-001 to MC-10000, with a deterministic profile for every universe | `UniverseTest` |
| Dialogue graphs with branching replies and actions | `DialogueTest` |
| Proximity dialogue: characters start talking when you come near, walk over first if far away, story conversations take priority over greetings, greetings play once | `Scene001PlaythroughTest`, `ProximityDirector` |
| Companion chatter while travelling, never repeated | `Scene001PlaythroughTest` |
| Cinematic timelines: smooth camera paths through every keyframe, short-way rotation, fades, subtitles, shake, sound cues | `CinematicTest` |
| Scene 001 plays from arrival to completion in the story engine, including save, reload and resume mid-scene, and logging out during a cinematic | `Scene001PlaythroughTest` |
| Story NPC entity: carries its character, is named, cannot be hurt, saves its character | `npcCarriesCharacterAndCannotBeHurt` game test |
| MC-001 universe: its dimension type loads on a real server and is fixed at sunrise. The test server cannot open custom dimensions, so tests stage Firstbrook in the test world | `mc001UniverseIsDefined` game test, `DimensionDataTest` |
| Firstbrook builds: Steve's house (bed, chest of food and gear, workstations, armour stand, item frames), Elara's family home, Rowan's house, forge, market, farms, river and bridge, wall, watchtower, northern hills with the ruin; every NPC is placed on solid ground | `firstbrookBuilds` game test |
| Scene 001 plays through on the real server: opening cinematic, Elara following, the walking conversation, the farmer, the square, the ridge cinematic, "Did you feel that?", Elder Rowan walking over, rewards, journal and save | `scene001PlaysThroughInFirstbrook` game test, `Scene001PlaythroughTest` |
| NPCs move: guards patrol, Elara keeps pace with the player while talking | `firstbrookCharactersMove` game test |
| Logging out mid-conversation cannot soft-lock the scene: the conversation replays on return | `Scene001PlaythroughTest.loggingOutMidConversationCannotStickTheScene` |
| Development controls (`/rod dev …`) need operator permission, so survival players never see them | Permission level 2 in `DevCommands` |

## Implemented, needs a play test on Windows

The client side runs only in a real game. CI starts the real client on a virtual screen
(`client-playtest` job) and photographs every Saga 1 character and each beat of Scene 001; the
screenshots are on the `claude/playtest-screenshots` branch. It still needs a human on Windows for:

- How the cinematic camera, fades, subtitles and camera shake feel at full frame rate.
- Typewriter subtitles, the V key to continue, the objective tracker and arrow.
- Sound balance. Placeholders: vanilla sounds (not positioned in the world), birds and lantern sway
  suggested with particles, no voice acting (subtitles only), no custom animations.

## Not started

- Saga 1 Scenes 002–020, and every later saga.
- Combat encounters, villains and boss fights (companion combat lines exist, but nothing to fight yet).
- Universes other than MC-001 as playable places.
- Multiplayer-specific story handling: progress is per player and works with several players, but
  shared NPCs follow whoever triggered them; co-op scene sync is not designed yet.
- Voice acting, music and custom sound events.
- Story Expansion 1 systems (relationships, Herobrine campaign, TMMA investigations, side
  campaigns). The engine supports flags, triggers and optional dialogue they will build on.

## Open questions for the story owner

- **Scene 001 script.** "A Morning in Firstbrook" uses the Saga 1 screenplay's lines. Changes to the
  script are data edits in `rod-story/scenes/mc001_s001.json`, with no code changes.
- **Duplicate skin:** Oblivion Steve and Voidwalker Steve (MC-073) use the identical skin file.
- **MC-002:** both Prehistoric Steve and Witch Alex are labelled MC-002. Is that intended?
- **Witch Alex's skin** came from Planet Minecraft; check its licence before a public release.
- **Unconfirmed roles:** Draxson, Evaulx, Temporal Custodian and MysteryDweller's home universe.

See [CHARACTER_REGISTRY.md](CHARACTER_REGISTRY.md) for the full registry.
