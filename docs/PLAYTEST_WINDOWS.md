# Playtesting Scene 001 on Windows

Only **Scene 001, "A Morning in Firstbrook"**, is playable. Scenes 002–020 are not implemented yet:
when Scene 001 ends, the game says the next scene is unreleased and leaves you in Firstbrook.

You need Minecraft Java **1.21.1** with **NeoForge 21.1.x** (CI builds against 21.1.172).

## Option A: install the built mod jar (no development tools needed)

1. Open the latest green run of **Build and test** on the `claude/project-thread-0cmccg` branch:
   <https://github.com/aymandev-1/realities-of-dreams-minecraft-multiverse-of-madness/actions>
2. Under **Artifacts**, download `realitiesofdreams-mod-jar` and unzip it. It contains one file,
   `realitiesofdreams-0.1.0.jar`. That is the playable mod. The build makes no sources jar, so you
   can't pick the wrong one.
3. Install NeoForge 21.1.x for 1.21.1 with the NeoForge installer, or create a NeoForge 1.21.1
   instance in a launcher such as Prism, CurseForge or Modrinth.
4. Copy `realitiesofdreams-0.1.0.jar` into that instance's `mods` folder. For the vanilla launcher
   that is `%APPDATA%\.minecraft\mods`.
5. Start the game, create a **new world** with **Allow Cheats: ON** (the `/rod dev` commands need
   it), and type `/rod play`.

## Option B: build and run from source

1. Install a **Java 21 JDK** (for example Eclipse Temurin 21) and Git.
2. In PowerShell or Command Prompt:

   ```bat
   git clone https://github.com/aymandev-1/realities-of-dreams-minecraft-multiverse-of-madness.git
   cd realities-of-dreams-minecraft-multiverse-of-madness
   git checkout claude/project-thread-0cmccg
   gradlew.bat build
   ```

   The first build downloads Minecraft and NeoForge, which takes a few minutes. The mod jar is
   written to `build\libs\realitiesofdreams-0.1.0.jar`.
3. Run a development client that already has the mod loaded:

   ```bat
   gradlew.bat runClient
   ```

   The `client` run configuration is defined in `build.gradle` (`neoForge { runs { client { … } } }`)
   and uses `run\client` as its game folder. Create a world with cheats on, then type `/rod play`.
4. Optional checks:
   - `gradlew.bat runGameTestServer` runs the in-game tests on a headless server.
   - `gradlew.bat runClient -Prod.autotest=true` replays the automated playtest and saves
     screenshots to `run\client\screenshots`.

## Playing Scene 001

Type `/rod play`. You don't need to right-click anyone: every conversation starts when you walk
near the character.

1. A sunrise cinematic plays over Firstbrook, then cuts to Steve waking in his house while Elara
   calls through the window.
2. Steve's chest holds food and starter gear. Walk out of the front door.
3. Elara joins you and talks as she walks beside you. Follow the lane east, over the bridge, to the
   farms.
4. A farmer speaks when you get near.
5. Walk to the village square (the well). The camera turns to the northern hills and the ground
   shakes, then Elara asks if you felt that.
6. Elder Rowan walks over to you. The scene completes, you get your rewards and the game saves.

Subtitles stand in for voice acting. Sounds are vanilla placeholders.

## Test commands (operators only)

None of these do anything unless you are an operator (cheats on). Survival players never see them.

| Command | What it does |
|---|---|
| `/rod play` | Start or continue the story |
| `/rod status` | Show the current scene and stage |
| `/rod leave` | Go back to the overworld, keeping your progress |
| `/rod dev restart` | Restart Scene 001 from the opening cinematic |
| `/rod dev reset` | Erase all story progress |
| `/rod dev stage <id>` | Jump to a stage: `opening`, `leave_house`, `explore`, `to_square`, `foreshadow`, `feel_that`, `rowan` |
| `/rod dev progress` | Show the stage, flags, finished dialogues and fired triggers |
| `/rod dev tp firstbrook` / `/rod dev tp house` | Teleport to the village square or into Steve's house |
| `/rod dev lineup` / `/rod dev lineup all` | Line up the 11 named characters and one NPC of each type (`all`: every Saga 1 NPC) to check skins |
| `/rod dev clear` | Remove characters spawned by test commands |
| `/rod dev npcs` | List story characters near you |
| `/rod dev npc <character> follow\|stay\|approach\|goto <location>` | Move a character, for example `/rod dev npc elara follow` |
| `/rod dev talk <dialogue>` | Play a dialogue, for example `walk_bridge` |
| `/rod dev proximity` | Explain why each nearby talk trigger will or won't fire |
| `/rod dev save` | Save the story state now, so you can test reloading |

**Testing save and reload:** in the middle of the walk, type `/rod dev save`, quit to the title
screen, reload the world and type `/rod play`. The conversation you were in starts again and the
scene can still be finished.

## Known issues and placeholders

- Only Scene 001 exists. There is nothing to play after Rowan arrives.
- Scene 001 dialogue is provisional until the canon script is supplied.
- No voice acting (subtitles only), no custom animations, and vanilla sounds as placeholders.
  Birds and lantern sway are suggested with particles.
- Faction NPC skins are generated (shared uniforms, different faces). The 11 named characters use
  your original skins unchanged.
- Time in Firstbrook is fixed at sunrise.
