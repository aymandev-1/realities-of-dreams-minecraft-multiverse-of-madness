# Saga 1 continuity check

Checked: the full Saga 1 screenplay (Scenes 001–020, Version 1, `SAGA_1_SCREENPLAY.md`) against
`STORY_EXPANSION_1.md`, the character registry and the game as built so far.

The Master Story Bible is not in the project or the repository, so it could not be checked.
Everything below is measured against the documents that are here.

Each open point has the default the game uses until the story owner decides otherwise.

## 1. Conflicts with what was already built

| # | Conflict | Resolution |
|---|---|---|
| 1.1 | The earlier provisional Scene 001, "Tremors at Dawn", was written before any script existed. It has a hill shrine, a rift tremor, an "M.D." journal page hinting at MysteryDweller, Captain Maren in Firstbrook, and Liora and Sahir living in the village. The screenplay contradicts all of it. Steve learns nothing of MysteryDweller in Saga 1. Maren first appears at Valebridge harbour in Scene 016. Liora belongs to Sylvaris and Sahir to Solkar. | **Fixed.** Scene 001 is rebuilt from the screenplay and the provisional version is retired. It stays in git history only. |
| 1.2 | MC-001 was locked to mid-morning. Saga 1 needs early morning (001), sunset (008, 009, 020) and a starry night (020). Minecraft shares the time of day between the Overworld and custom worlds. | Scene 001 now runs in early morning. A per-scene time-of-day system is needed before Scene 008. |
| 1.3 | The village was an unnamed placeholder. | It is now Firstbrook, laid out for Scenes 001–005: Steve's house, the square, Orin's workshop, the north gate and the watchtower. |

## 2. Against canon (Story Expansion 1)

There are no hard conflicts.

- **Federation founded on MC-001.** Scene 020's joint council of six civilisations makes a natural seed for it. The screenplay does not name it, and the game will not.
- **The Forgotten Steves missing-explorer mystery.** This fits Scene 009's missing expedition and the side quest "The Missing Explorers", as long as their fate stays unresolved, as the screenplay says.
- **MysteryDweller as first traveller, TMMA, Architects, Root Command Block, Notch/Jeb.** None of these is revealed. The Scene 019 silhouette stays unidentified.
- **Phobia-safe design.** The screenplay already asks for no horror creatures at the abandoned camp. The raiders in Scene 011 are human.

## 3. Gaps inside the screenplay (decisions needed)

| # | Gap | Default used until decided |
|---|---|---|
| 3.1 | "Primordial Steve" is used as an in-world title. Queen Aveline says "Primordial Steve of Firstbrook" in Scene 012. Is "Primordial" a name people in MC-001 know, given that his ultimate role is reserved for Saga 24? | Everyone else calls him Steve, and on-screen subtitles say STEVE. The Queen's line is kept as written. |
| 3.2 | Scene 016 says all six emblems are now represented, but only four physical fragments are accounted for: the monument piece (006), the Irondeep stone (013), the Tidewatch stone (016) and the Sylvaris grove stone, which stays buried (010). Solkar brings records and Veyrath brings nothing. The emblem crate from the caravan (011) is never mentioned again. | The monument piece stands for Eldervale. Liora brings a rubbing of the grove stone. Sahir identifies the caravan crate as Solkar's piece in 014. Kaelen's honour guard carries Veyrath's piece in 015. No new dialogue is added. |
| 3.3 | Is Firstbrook part of Eldervale? It is not one of the six civilisations, yet Steve reports to Eldervale's queen. | Firstbrook is an Eldervale village. |
| 3.4 | Orin is at Valebridge in Scene 013, but only Steve, Elara, Bram and Liora travelled there in 011–012. | Orin arrives off-screen with Firstbrook's records. No new dialogue is added. |
| 3.5 | Scene 003 sends Steve to the "last known position of a missing scout", but no scout has been mentioned before. | The scout belongs to the expedition later found abandoned in Scene 009. No new dialogue is added. |
| 3.6 | Scene 007 is set at both the monument and Orin's Firstbrook workshop. | It is set at Orin's field table at the monument. |
| 3.7 | Small cast mismatches. Bram is listed for Scene 003 but has no part. Scene 002 says Orin will supply equipment before he hears of the markings in 004. Maren is dropped from Scene 019's cast although it continues straight from 018. | Bram stays at the gate in 003. Scene 002's line is kept as written. Maren is present without lines in 019. |

## 4. Assets the screenplay needs that do not exist yet

- **Generic NPCs.** No skins existed for the unnamed cast. 55 generated appearances now cover 16 NPC types (Firstbrook villagers, farmers and guards, expedition workers and researchers, Sylvaris rangers, Eldervale officials and royal guards, Stonecrown guards and engineers, Solkar scholars, Veyrath honour guards, Thalassian sailors, merchants, raiders and the raider leader). They are generated art, not hand-made skins; see `docs/CHARACTER_REGISTRY.md`.
- **Voice recordings, soundtrack cues and custom animations.** None exist. Every line has a speaker ID and subtitle, and has an empty voice slot for later. Vanilla sounds stand in for ambience.
- **Locations.** Awakening Valley, the monument, the Sylvaris outpost, Valebridge, the harbour and the gateway chamber are not built yet. They are built scene by scene.
- **Saga 2.** Not started until its screenplay is provided.
