# Realities of Dreams: Minecraft Multiverse of Madness

A cinematic story mod for **Minecraft 1.21.1 / NeoForge / Java 21**, set across 10,000 Minecraft
universes (26 sagas, 520 main scenes). This repository holds the game code, the story data and
the character skins.

What works today and what doesn't is tracked in **[docs/STATUS.md](docs/STATUS.md)**.

## Play it (Windows)

1. Install Minecraft 1.21.1 with the NeoForge 21.1.x installer.
2. Download `realitiesofdreams-0.1.0.jar` from the latest **Build and test** run on GitHub
   (Actions tab → run → Artifacts → `realitiesofdreams-mod-jar`), or build it yourself (below).
3. Put the jar in `%APPDATA%\.minecraft\mods` and start the game.
4. The Realities of Dreams main menu appears. Choose **New Story**: the game creates its own story
   save and you wake up in Firstbrook as Primordial Steve. **Continue Story** picks up where you
   left off. Your other worlds are never touched.

Full instructions and a test checklist: [docs/PLAYTEST_WINDOWS.md](docs/PLAYTEST_WINDOWS.md).

Controls: walk up to characters and they talk to you. **V** finishes or skips a line and skips
cinematics. Number keys or the mouse pick replies.

## Build from source

```bat
gradlew.bat build              :: mod jar in build\libs
gradlew.bat runClient          :: launch Minecraft with the mod
gradlew.bat runGameTestServer  :: in-game automated tests on a dedicated server
gradlew.bat :story-core:test   :: story engine unit tests
gradlew.bat runClient -Prod.autotest=true   :: automated playtest in the real client, with screenshots
```

Machines that cannot reach the NeoForge or Mojang servers can still build and test the story
engine with `gradlew -Prod.coreOnly=true :story-core:test`.

## Layout

| Path | What it is |
|---|---|
| `story-core/` | Story engine with no Minecraft dependency: dialogue, scenes, triggers, cinematics, saves. Unit tested. |
| `src/main/java/net/realitiesofdreams/` | The NeoForge mod: main menu and story saves (`client/menu`, `campaign`), NPC entity, story manager, networking, HUD, cinematic camera, MC-001 terrain (`world/gen`), site builders (`world/site`), commands, game tests. |
| `src/main/resources/rod-story/` | Story data: `characters.json`, `sagas.json` (chapter list), one bundle per scene (`scenes/mc001_s001.json`) and site placement registries (`sites/firstbrook.json`). |
| `src/main/resources/assets/realitiesofdreams/` | Textures, menu art, music and sounds, all bundled in the jar (see `docs/ASSETS.md`). |
| `art/skins/source/` | Your original skins, unchanged, in your folder structure. |
| `tools/skins/` | Registry builder and TMMA skin generator (`python3 tools/skins/build_registry.py`). |
| `tools/assets/` | Generator for the original menu art and synthesised audio (`python3 tools/assets/generate_assets.py`). |
| `docs/` | Status report, Windows playtest guide, MC-001 world plan, asset manifest, character registry, story documents. |

## Writing story content

A scene bundle holds the scene (locations, cast, stages, triggers, companion chatter, clues) and
the dialogues and cinematics it uses. Characters start conversations on their own when the
player comes near (`triggers`), walking up first when `approach` is true. Stages advance on
`cinematic_end`, `dialogue_end`, `reach`, `clues` or `flag`. `story-core` tests validate every
reference, so a typo fails the build rather than the game.

## Commands

In story saves the story runs by itself. In ordinary worlds, `/rod play` starts it.

`/rod play`, `/rod status`, `/rod scene restart`, `/rod leave`, `/rod characters [MC-xxx]`,
`/rod universe MC-xxx`, and for operators `/rod spawn <character>`, `/rod scene skip`.
