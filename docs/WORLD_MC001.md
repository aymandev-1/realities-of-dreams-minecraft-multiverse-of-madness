# MC-001 world plan

How MC-001, the First World, is put together: the terrain, the coordinate plan, Firstbrook's
placement registry and the rules for blocks the story protects. What is built today and what
is only planned is marked on every line.

## How the world is made

| Layer | What it does | Code / data |
|---|---|---|
| Terrain | Normal Minecraft terrain (caves, ores, biomes, mountains), reshaped while chunks generate to an authored layout around Firstbrook and blended back into natural terrain from 210 to 280 blocks out. No vanilla villages or outposts. Firstbrook's core has no caves, lava or wild trees. | `world/gen/Mc001ChunkGenerator`, `story-core/.../world/Mc001Terrain`, `data/realitiesofdreams/dimension/mc_001.json` |
| Biomes | Fixed biomes inside the authored area, so the village looks the same whatever the seed: plains (Firstbrook), meadow (northern hills), taiga (mountains), forest (east woodland), sunflower plains (valley). Normal overworld biomes beyond. | `world/gen/Mc001BiomeSource` |
| Sites | Authored places, built once per world from a placement registry. Each piece is recorded as built, so an interrupted build resumes. Any piece can be swapped for a saved structure template. | `world/SceneWorld`, `world/site/*`, `rod-story/sites/firstbrook.json` |
| Story protection | Only the blocks the story needs (Steve's door and bed, the well, the square lantern, the eastern bridge, Rowan's door, the north gate, the ridge ruin) are protected from survival players and explosions. Everything else can be mined and built on. Creative players can edit anything. Can be switched off with `protectStorySites`. | `ServerEvents`, `"protected"` in the site file |

The story save always uses seed 1001, so every new story generates the same land beyond the
authored area too. When the story save starts, Firstbrook is built on the server while the client
still shows the loading screen (about a second), so the game never freezes mid-play.

## Coordinate plan

All coordinates are relative to the Firstbrook anchor, block (0, 64, 0) in MC-001. Village ground
is y = 63 (the anchor stands on it). North is −z.

| Area | Where | Status |
|---|---|---|
| Firstbrook | x −44..44, z −44..44, flat | Built |
| North wall and gate | z = −22, gate at x −2..2 | Built |
| River | x 30..34, from the waterfall (z −57) to Southmere lake (32, 112) | Built (terrain + bridge, dock) |
| Northern hills and trail | z −46..−140, rising to the ridge | Built (terrain, trail, spruces) |
| The ruin on the ridge | (4..16, ~27, −112) | Built (Scene 001 foreshadowing); Scene 002 investigation not built |
| Mountains at the valley's northern edge | z < −150 | Terrain only |
| East woodland | x > 40 | Natural forest |
| Southmere lake | centre (32, 112), radius 16 | Terrain only |
| Ancient Northern Monument (Scenes 006–007) | planned around (40, —, −320), beyond the ridge | Not started |
| Expedition camps, damaged bridge, abandoned campsite (Scenes 008–009) | planned along the northern trail, z −150..−300 | Not started |
| Sylvaris Forest Outpost (Scene 010) | planned east, around (+450, —, −150), in a forest region | Not started |
| Valebridge, Eldervale's capital (Scenes 012–017) | planned south, around (0, —, +700), on the coast | Not started |
| Stonecrown, Solkar, Veyrath, Thalassia presences (Scenes 013–017) | planned as quarters of Valebridge first, then their own regions | Not started |
| Ancient Gateway Chamber (Scenes 018–019) | planned underground beneath the monument, y ≈ 20 | Not started |

Each planned site gets its own `rod-story/sites/<site>.json` and builder (or structure
templates), placed by the scenes that use it, so MC-001 grows scene by scene instead of being
generated all at once.

## Firstbrook's placement registry

`rod-story/sites/firstbrook.json` lists every piece in build order with its footprint
(`box: [x0, z0, x1, z1]`) and settings. Built today:

| Piece | Footprint | Notes |
|---|---|---|
| Steve's House | 12..20, 14..21 | Bed under the north window, chest of breakfast and gear, crafting table, furnace, armour stand, trophies, carrot patch, chimney |
| Elder Rowan's House | −16..−8, −6..1 | Bookshelves, lectern with the village records, cartography table, his bed |
| Council Fire (meeting area) | −27..−19, −5..1 | Open pavilion around a fire, benches, the ledger, Eldervale banners |
| Elara's Home | 22..28, 15..21 | Ranger's things: fletching and cartography tables, green bed |
| Orin's Workshop | 8..14, −11..−5 | Forge row with smoking chimney, anvil, grindstone, quench cauldron, tool racks, armour on a stand |
| Orin's House | 20..26, −12..−6 | Stone house next to the workshop |
| Guard Station (Bram) | 5..12, −21..−15 | Bunks, arms chest, patrol log on a lectern, map table, armour rack, practice target, banner |
| North Watchtower | −12..−8, −21..−17 | Stone base, ladder to a roofed lookout with a bell |
| North wall and gate | z = −22 | Palisade, stone gate posts, lanterns, banner |
| Village Storehouse | −26..−18, −21..−16 | Barrels, chests, hay, produce |
| Market | −14..−4, 6..9 | Three awninged stalls and a trader's cart |
| The Bakery | −14..−8, 15..21 | Smokers, furnace, cake, flour barrels |
| The Millers' House | −5..3, 15..22 | Two storeys: kitchen below, bedrooms above |
| Weaver's Cottage | −28..−21, −14..−8 | Looms and wool |
| Farmhouse | −2..6, 25..32 | Farmers' beds, produce barrels |
| Fields | 8..15 × 3..9, 20..27 × 3..9, 10..22 × 26..34 | Wheat, carrots, potatoes, beetroot, pumpkins, melons, scarecrow |
| Barn | −40..−31, 23..35 | Hay loft, stalls, two horses |
| Animal pens | −28..−15, 24..37 | Cows, sheep, pigs, chickens and coop, troughs, gates |
| Training Ground | −42..−31, −20..−6 | Archery targets, practice dummies, practice gear chest |
| Woodcutter's Camp | −44..−32, −3..20 | Grove of oak and birch, log piles, chopping block |
| House under construction | 37..43, 15..22 | Half-built frame, scaffolding, timber |
| Fishing dock | 31..38, 27..30 | Pier, rowing boat, fish barrel |
| Square and well | −6..6, −6..6 | Well, the swaying lantern, benches, notice board |
| Paths, lantern posts, trees, gardens, signposts | — | Lanes connect every door |
| Hills, trail, ruin, waterfall | north of the wall | Trail with old lantern posts up to the ridge |

## Saving a building as a structure template

1. In a creative copy of the story save, build or edit the piece.
2. Save it with a structure block named `realitiesofdreams:firstbrook/<piece id>`, with the
   structure's corner at the piece box's (x0, 62, z0).
3. Copy the `.nbt` from the save's `generated/realitiesofdreams/structures/firstbrook/` into
   `src/main/resources/data/realitiesofdreams/structure/firstbrook/`.
4. New story saves place the template instead of running that piece's builder.
