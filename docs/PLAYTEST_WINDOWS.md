# Playtesting on Windows

What you can play: the **Realities of Dreams main menu**, **New Story** into MC-001, the redesigned
**Firstbrook**, and **Scene 001, "A Morning in Firstbrook"**, start to finish, with **Continue
Story** afterwards. Scenes 002–020 are not implemented yet: after Scene 001 you are free to explore
MC-001, and Chapter Selection shows the later scenes as locked.

You need Minecraft Java **1.21.1** with **NeoForge 21.1.x** (CI builds against 21.1.172).

## Option A: install the built mod jar (no development tools needed)

1. Open the latest green run of **Build and test** for the `claude/gracious-bell-lrzhza` branch:
   <https://github.com/aymandev-1/realities-of-dreams-minecraft-multiverse-of-madness/actions>
2. Under **Artifacts**, download `realitiesofdreams-mod-jar` and unzip it. It contains one file,
   `realitiesofdreams-0.1.0.jar`. That is the whole mod: textures, sounds, music and the world are
   inside it. There is no separate resource pack to install.
3. Install NeoForge 21.1.x for 1.21.1 with the NeoForge installer, or create a NeoForge 1.21.1
   instance in a launcher such as Prism, CurseForge or Modrinth.
4. Copy `realitiesofdreams-0.1.0.jar` into that instance's `mods` folder. For the vanilla launcher
   that is `%APPDATA%\.minecraft\mods`.
5. Start the game. The Realities of Dreams main menu appears instead of the usual title screen.

## Option B: build and run from source

1. Install a **Java 21 JDK** (for example Eclipse Temurin 21) and Git.
2. In PowerShell or Command Prompt:

   ```bat
   git clone https://github.com/aymandev-1/realities-of-dreams-minecraft-multiverse-of-madness.git
   cd realities-of-dreams-minecraft-multiverse-of-madness
   git checkout claude/gracious-bell-lrzhza
   gradlew.bat build
   gradlew.bat runClient
   ```

   The first build downloads Minecraft and NeoForge (a few minutes). The jar is written to
   `build\libs\realitiesofdreams-0.1.0.jar`; `runClient` starts the game with the mod loaded.
3. Optional checks:
   - `gradlew.bat :story-core:test` runs the story engine and world-data unit tests.
   - `gradlew.bat runGameTestServer` runs the in-game tests on a headless server.
   - `gradlew.bat runClient -Prod.autotest=true` plays the whole thing automatically (menu, New Story,
     Scene 001, save and Continue) and saves screenshots to `run\client\screenshots`.

## What to test

### 1. The main menu

- Title, subtitle, the turning panorama of Firstbrook and the faint gateway rings; menu music.
- Move through the menu with the mouse, and with **Tab / arrow keys + Enter**.
- **Continue Story** and **Chapter / Saga Selection** are greyed out until you have a story save.
- **Settings**, **Multiplayer**, the language and accessibility buttons and **Mods** open the usual
  Minecraft screens. **Minecraft Menu** shows the vanilla title screen; its top-left button brings
  you back.
- Resize the window and change GUI Scale: the menu should always fit.

### 2. New Story

Click **New Story**. You don't create a world, pick a game mode, type a command or teleport:

1. A story save named "Realities of Dreams — MC-001" is created next to your other worlds (your
   other worlds are never touched). The first load takes a little longer while Firstbrook is built
   and the valley around it generated.
2. The opening cinematic plays: the valley at sunrise, Firstbrook, Steve asleep at home, Elara at
   the window.
3. You wake as **Primordial Steve** (classic Steve skin, whatever your account skin is). Check
   first person (your arm) and third person (F5).
4. Walk out of the front door. Elara joins you and talks as you walk; you never need to click
   anyone. Follow the lane east past the fields, then to the village square.
5. The camera turns to the northern ridge, the ground trembles, Elara and Steve talk, and Elder
   Rowan comes over. "Mission Complete — A New Morning", rewards, journal entry.

If a story save already exists, New Story asks first and keeps the old save as a dated backup in
your worlds list.

### 3. Explore Firstbrook

After Scene 001, walk around: Steve's house, Elder Rowan's house and the council fire, Elara's home,
Orin's workshop and house, Bram's guard station, the watchtower (climb it), the gate, the
storehouse, the market, the bakery, the Millers' two-storey house, the farmhouse, fields, the barn
and animal pens, the training ground, the woodcutter's camp, the house under construction, the
dock, the river and bridge, and the trail north to the ruin on the ridge and the waterfall.
Villagers walk between home, work and the market, opening doors. Mining, building and crafting work
as normal; only a handful of story blocks (Steve's door and bed, the well, the bridge deck, the gate,
the ruin) can't be broken in survival.

### 4. Save and Continue

Press Esc → **Save and Quit to Title**. The menu now shows **Continue Story** with your scene and
objective. Click it: you are back where you were, still Primordial Steve. Try quitting in the middle
of the walk too: the conversation picks up again when you continue.

## Controls

| Key | Action |
|---|---|
| WASD, Space, mouse | Move, jump, look (your normal Minecraft controls) |
| **V** | Finish the current subtitle line / next line; skip a cinematic |
| 1–4 or mouse | Pick a reply when a conversation offers choices |
| F5 | Change camera (see Primordial Steve) |

## Test commands (operators only)

Story saves are created without cheats. To use these, open the story save to LAN with **Allow
Cheats: ON** (Esc → Open to LAN), or use a creative copy of the save.

| Command | What it does |
|---|---|
| `/rod status` | Show the current scene and stage |
| `/rod dev restart` | Restart Scene 001 from the opening cinematic |
| `/rod dev stage <id>` | Jump to a stage: `opening`, `leave_house`, `explore`, `to_square`, `foreshadow`, `feel_that`, `rowan` |
| `/rod dev progress` | Show the stage, flags, finished dialogues and fired triggers |
| `/rod dev tp firstbrook` / `/rod dev tp house` | Teleport to the square or into Steve's house |
| `/rod dev lineup` / `/rod dev lineup all` | Line up the Saga 1 characters to check skins |
| `/rod dev npcs` | List story characters near you |
| `/rod dev save` | Save the story state now |

In an ordinary (non-story) world the story no longer starts by itself; `/rod play` still starts it.

## Known issues and placeholders

- Only Scene 001 is playable. Scenes 002–020, the monument, Sylvaris, Valebridge and the gateway
  chamber are not built yet.
- MC-001 is fixed at sunrise, so villagers don't sleep at night yet.
- Music and interface sounds are original synthesised placeholders; there is no voice acting
  (subtitles only). See [ASSETS.md](ASSETS.md).
- Firstbrook's villagers, farmers and guards use generated skins (shared outfits, different faces).
- No controller support: Minecraft Java has none built in.
