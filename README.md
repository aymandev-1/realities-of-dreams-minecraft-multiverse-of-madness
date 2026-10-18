# Realities of Dreams: Minecraft Multiverse of Madness

A cinematic story mod for **Minecraft 1.21.1 / NeoForge / Java 21**, set across 10,000 Minecraft
universes (26 sagas, 520 main scenes). This repository holds the game code, the story data and
the character skins.

What works today and what doesn't is tracked in **[docs/STATUS.md](docs/STATUS.md)**.

## Play it (Windows)

1. Install Minecraft 1.21.1 with the NeoForge 21.1.x installer.
2. Download `realitiesofdreams-0.1.0.jar` from the latest **Build and test** run on GitHub
   (Actions tab → run → Artifacts → `realitiesofdreams-mod-jar`), or build it yourself (below).
3. Put the jar in `%APPDATA%\.minecraft\mods`, start a new single-player world.
4. Scene 001 starts by itself a few seconds after you join. You can also type `/rod play`.

Controls: walk up to characters and they talk to you. **V** finishes or skips a line and skips
cinematics. Number keys or the mouse pick replies.

## Build from source

```bat
gradlew.bat build              :: mod jar in build\libs
gradlew.bat runClient          :: launch Minecraft with the mod
gradlew.bat runGameTestServer  :: in-game automated tests on a dedicated server
gradlew.bat :story-core:test   :: story engine unit tests
```

Machines that cannot reach the NeoForge or Mojang servers can still build and test the story
engine with `gradlew -Prod.coreOnly=true :story-core:test`.

## Layout

| Path | What it is |
|---|---|
| `story-core/` | Story engine with no Minecraft dependency: dialogue, scenes, triggers, cinematics, saves. Unit tested. |
| `src/main/java/net/realitiesofdreams/` | The NeoForge mod: NPC entity, story manager, networking, HUD, cinematic camera, world builder, commands, game tests. |
| `src/main/resources/rod-story/` | Story data: `characters.json` and one bundle per scene (`scenes/mc001_s001.json`). |
| `art/skins/source/` | Your original skins, unchanged, in your folder structure. |
| `tools/skins/` | Registry builder and TMMA skin generator (`python3 tools/skins/build_registry.py`). |
| `docs/` | Character registry, status report, story documents. |

## Writing story content

A scene bundle holds the scene (locations, cast, stages, triggers, companion chatter, clues) and
the dialogues and cinematics it uses. Characters start conversations on their own when the
player comes near (`triggers`), walking up first when `approach` is true. Stages advance on
`cinematic_end`, `dialogue_end`, `reach`, `clues` or `flag`. `story-core` tests validate every
reference, so a typo fails the build rather than the game.

## Commands

`/rod play`, `/rod status`, `/rod scene restart`, `/rod leave`, `/rod characters [MC-xxx]`,
`/rod universe MC-xxx`, and for operators `/rod spawn <character>`, `/rod scene skip`.
