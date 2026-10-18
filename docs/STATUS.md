# Development Status

Milestone 1: foundation and MC-001 Scene 001. Updated 2026-10-18.

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
| MC-001 universe: its dimension type loads on a real server, and the flat meadow terrain puts the ground exactly where the scene expects it. The test server cannot open custom dimensions, so walking into MC-001 itself is covered by the playtest below | `mc001UniverseIsDefined` game test, `DimensionDataTest` |
| The MC-001 village and Hollow Shrine build correctly (built in the test world, since the test server has no MC-001), clue blocks are in place, the tremor leaves a permanent scar, NPCs are placed per stage without duplicates | `scene001VillageBuilds` game test |
| Scene 001 runs end to end through the real server-side story manager, gives the journal page, and progress survives a save/load of the player data | `scene001PlaysThroughAndSaves` game test |

## Implemented, needs a play test on Windows

These compile and are exercised indirectly, but run only on a game client, which CI cannot open.

- NPC skins rendering on the classic and slim player models.
- Dialogue subtitles with typewriter text, the V key to continue, and the reply picker (mouse or 1-4).
- Objective tracker with distance and a direction arrow.
- Cinematic camera, letterbox, title cards, captions, fades, camera shake and sound cues, with
  player movement and the HUD hidden during cinematics.
- NPCs walking up to the player, companions following and catching up, clue highlights.
- Auto-start on first join, teleport into MC-001, respawn at the last checkpoint.

## Not started

- Scenes after MC-001 Scene 001 (519 remaining main scenes), and sagas 2 to 26.
- Combat encounters, villains and boss fights (companion combat lines exist, but nothing to fight yet).
- Universes other than MC-001 as playable places.
- Multiplayer-specific story handling: progress is per player and works with several players, but
  shared NPCs follow whoever triggered them; co-op scene sync is not designed yet.
- Voice acting, music and custom sound events.
- Story Expansion 1 systems (relationships, Herobrine campaign, TMMA investigations, side
  campaigns). The engine supports flags, triggers and optional dialogue they will build on.

## Open questions for the story owner

- **Scene 001 script.** No story bible, saga outline or scene list was supplied, only Story
  Expansion 1. Scene 001 ("Tremors at Dawn") is a provisional script using the MC-001 cast, and is
  marked provisional in its data file. Send the canon script and it replaces the dialogue without
  code changes.
- **Duplicate skin:** Oblivion Steve and Voidwalker Steve (MC-073) use the identical skin file.
- **MC-002:** both Prehistoric Steve and Witch Alex are labelled MC-002. Is that intended?
- **Witch Alex's skin** came from Planet Minecraft; check its licence before a public release.
- **Unconfirmed roles:** Draxson, Evaulx, Temporal Custodian and MysteryDweller's home universe.

See [CHARACTER_REGISTRY.md](CHARACTER_REGISTRY.md) for the full registry.
