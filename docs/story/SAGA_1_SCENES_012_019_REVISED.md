# Saga 1, Scenes 012–019: revised screenplay (Version 2)

This replaces Scenes 012–019 of `SAGA_1_SCREENPLAY.md` (Version 1). It follows the story owner's
"Scenes 12–19 complete overhaul" brief of 2026-10-19. Scenes 001–011 and 020 are unchanged.

Every spoken line is marked:
- **(S)** kept word for word from Version 1.
- **(A)** a Version 1 line adapted to the new setting. The original speaker or wording is given in brackets.
- **(N)** a new line written for Version 2. The required Scene 019 lines from the brief are marked **(R)**.

Game data is built from this document by `tools/story/build_s012.py` … `build_s019.py`. Hints, captions,
objectives, item names and journal entries are gameplay text and are not marked.

## Lore corrections in Version 2

| # | Version 1 | Version 2 (official canon) |
|---|---|---|
| L1 | Scenes 012–017 all take place in Valebridge; Garrick, Sahir, Kaelen and Maren come to the Queen. | Each civilisation is visited in its own capital: Irondeep (013), Sunspire (014), Emberhold (015) and Tidewatch (016). Scene 017 brings everyone back to Valebridge. |
| L2 | The six civilisations are the builders of the symbols. Who made the gateway is never addressed. | The **Ancient Builders** built the First Gateway, using technology developed from studying Prime Resonance. The six civilisations only inherited and kept fragments of the Builders' knowledge. Scene 012 reveals the inheritance and Scene 017 names the Builders. |
| L3 | Prime Resonance is not mentioned. | Only hinted at: Sunspire's records speak of "the deep note" that "holds the world in time" (014), and Scene 018's machines hum with it. It is never named or explained. |
| L4 | Scene 019: the portal flares, Steve stabilises three pylons and Elara pulls Steve away. | Scene 019: the gateway destabilises, the bridge collapses, **Elara is trapped** and **Steve rescues her**. The gateway's hidden preservation system reacts. A brief distortion shows two near-identical chambers overlapping. The gateway closes before any travel happens. |
| L5 | Architects are not revealed. | Unchanged. The Builders are named, but Architect Aurelian, the surviving Builders and the Last Workshop are never mentioned. |
| L6 | Fragments: four physical pieces, with continuity defaults. | All six are physical and accounted for. Eldervale's piece is in the royal vault (012). Liora brings a rubbing of the Sylvaris grove stone (012). Stonecrown's piece is the Builder machine plate (013). Solkar's piece is the sealed crate from the caravan in 011 (014). Veyrath's is the Ember Key (015). Thalassia's is the Tide Anchor (016). |
| L7 | Steve does not travel through the gateway. | Unchanged. Steve never enters another universe in Saga 1. |

**Not revealed in Saga 1:** what the divergence means, any alternate timeline or alternate future, any later
identity of anyone present, or the preservation system's purpose. No HUD label, journal entry, item text,
subtitle, hint or debug screen may hint at them. The divergence is stored only as story state (see Scene 019).

## Where each scene happens

| Scene | Site id | Anchor (MC-001 block) | Notes |
|---|---|---|---|
| 012 | `valebridge` | (−115, 67, 105) | Existing city. Houses get furnished interiors and residents. |
| 013 | `irondeep` | (−160, 24, −170) | Underground city in a sealed cavern beneath the north-western mountains. |
| 014 | `sunspire` | (520, 68, 420) | Desert capital on the `sunspire` terrain plateau (desert biome). |
| 015 | `emberhold` | (−560, 72, −60) | Volcanic capital on the `emberhold` plateau (badlands biome). |
| 016 | `tidewatch` | (60, 65, 620) | Coastal town on the `tidewatch` plateau (beach biome), with its own harbour sea. |
| 017 | `valebridge` | (−115, 67, 105) | Royal archive research chamber. |
| 018–019 | `gateway_chamber` | (40, 108, −200) | Beneath the Ancient Northern Monument. |

Travel between capitals is a fade-and-arrive: the first stage of each scene teleports the party to the site
and opens with an establishing cinematic.

---

## Scene 012 — The Queen's Summons

Valebridge • Afternoon • Quest: The Sixfold Council
Cast: Steve, Elara, Bram, Liora, Queen Aveline, the Archivist (Eldervale adviser), royal guards, citizens, merchants.

**Arrival.** The party comes up the trade road to Valebridge's east gate. Establishing cinematic over the walls,
the plaza fountain, the palace and the harbour: "Valebridge — Capital of Eldervale".
Walking through the gate (proximity):

- BRAM: "I've never seen so many people in one place." (S)
- ELARA: "Try not to stare." (S)
- BRAM: "I'm looking for the best place to eat." (S)
- STEVE: "Some things never change." (S)

**City (optional, proximity).** Citizens and merchants speak as Steve passes:
- MERCHANT: "Bread from the river farms! Still warm!" (N)
- CITIZEN: "They say the ground shook as far as the harbour last week." (N)
- ROYAL GUARD: "Keep the street clear for the envoys' carts." (N)
- CITIZEN: "Firstbrook folk? You're the ones who saved that caravan." (N)

**Objective: deliver the investigation dossier to the royal archive.** The Archivist waits at the desk inside the door.
- ARCHIVIST: "Firstbrook's dossier? The court has been waiting for it." (N)
- STEVE: "Everything we found is in there. The monument, the camp, the stone in Sylvaris." (N)
- ARCHIVIST: "Then it belongs beside the oldest records we keep. Her Majesty will read it today." (N)

A royal guard comes to fetch them:
- ROYAL GUARD: "Steve of Firstbrook? Her Majesty asks for you and your companions in the council hall." (N)
- STEVE: "Lead the way." (N)

**Objective: meet Queen Aveline at the royal palace.** Council cinematic, then:
- QUEEN AVELINE: "Primordial Steve of Firstbrook." (S)
- STEVE: "Your Majesty." (S)
- QUEEN AVELINE: "Rise. I've heard about the caravan you protected." (S)
- STEVE: "Anyone would've done the same." (S)
- QUEEN AVELINE: "Not everyone does." (S)
- QUEEN AVELINE: "Now tell me why six ancient symbols have brought you to my court." (S)
- STEVE: "Something beneath the northern ruins is active. The symbols appear in several locations, and the compass reacts to all of them." (S)
- QUEEN AVELINE: "And you believe those symbols correspond to our civilisations?" (S)
- LIORA: "We have found matching stonework in Sylvaris." (S)
- ELARA: "And evidence of an expedition that vanished while studying it." (S)
- QUEEN AVELINE: "Then the remaining leaders must be informed." (S)
- *Choice: how Steve frames his concern. The choice sets a flag and changes nothing else.*
  - Cautious → STEVE: "We don't understand this discovery. We need to investigate safely." (S)
  - Urgent → STEVE: "People may already be in danger. We cannot afford to wait." (S)
  - Cooperative → STEVE: "All six civilisations need to investigate this together." (S)
- QUEEN AVELINE: "The past belongs to all of us. Whatever was buried beneath these lands may affect all of us too." (S)
- STEVE: "Then we'll find the truth together." (S)
- QUEEN AVELINE: "Begin with what Eldervale has kept. The sealed royal records are open to you." (N)

**Objective: investigate the sealed royal records.** Three lecterns in the archive hall. Each one starts a short exchange:
- *The Charter of the Six Crowns*
  - STEVE: "'Six crowns were given the keeping of what came before.' Given by whom?" (N)
  - ELARA: "It doesn't say. Only that the six kingdoms were made keepers." (N)
- *The Founding Chronicle of Valebridge*
  - LIORA: "Valebridge was raised on older stones. The chronicle calls them the foundations of the first builders." (N)
  - STEVE: "Builders before Eldervale." (N)
- *Inventory of the Old Vault*
  - BRAM: "'One stone, carved, not to be moved. Five sent away to five keepers.' They catalogued the same symbols." (N)
  - STEVE: "So the kingdoms didn't make these marks. They were handed them." (N)

**Inheritance:**
- LIORA: "Every kingdom tells its history as if it began with itself." (N)
- STEVE: "But these records say we inherited something. Knowledge from someone older than all six." (N)
- ELARA: "Then the other kingdoms will have pieces of it too." (N)
- ARCHIVIST: "Her Majesty's orders. Thane Garrick writes that his miners have found a carved mechanism beneath Irondeep. You are to go to him, with the vault stone." (N)
- LIORA: "Take this as well. It's a rubbing of the stone in our grove. It's as close as Sylvaris can come to sending the stone itself." (N)
- STEVE: "Then Irondeep is next." (N)

Outcome: Steve receives the royal writ, Eldervale's fragment (from the vault) and Liora's rubbing (Sylvaris's fragment).
Journal: *The Sixfold Council.*

## Scene 013 — The Stone of Irondeep

Irondeep, beneath the mountains of Stonecrown • Morning • Quest: The Engraved Stone
Cast: Steve, Elara, Bram, Orin, Thane Garrick, Stonecrown guards and engineers, Irondeep miners.

**Arrival.** The great lift sets the party down in Irondeep. Establishing cinematic: a vast lit cavern of terraces,
stone bridges, forge glow and mine rails: "Irondeep — Hold of Stonecrown".
At the lift gate:
- STONECROWN GUARD: "A writ from Eldervale? The Thane's expecting you. Forge Hall, past the market terraces." (N)
- ORIN: "Took you long enough. I came by the mountain road with Firstbrook's records." (N)
- STEVE: "Orin! You came all this way?" (N)
- ORIN: "Somebody has to stop you touching the wrong thing." (N)

City (optional, proximity): miners and smiths at work.
- MINER: "Mind the rails! Ore carts don't stop for visitors." (N)
- MINER: "The deep shafts have been groaning since the tremors." (N)
- ENGINEER: "Lift's running on the old counterweights again. Don't lean on the rail." (N)

**Objective: meet Thane Garrick in the Forge Hall.**
- THANE GARRICK: "I was told you found something worth dragging me away from my forge." (A — V1: "from Irondeep")
- STEVE: "Queen Aveline sent us. She says your miners found something." (N)
- GARRICK: "My miners discovered this beneath the oldest foundations of Irondeep." (S)
- ORIN: "That carving..." (S)
- STEVE: "It's the same symbol." (S)
- GARRICK: "Exactly. Which is why I want to know how you found another." (S)
- GARRICK: "Our deepest halls are older than the current kingdom. There are tunnels nobody remembers building." (S)
- STEVE: "Could they connect to the northern monument?" (S)
- GARRICK: "Not through any passage my miners know." (S)
- ORIN: "The markings are consistent, though. Same spacing. Same geometry." (S)
- GARRICK: "Then somebody put considerable effort into making them identical." (S)

**Objective: investigate the ancient mechanical fragment** on Garrick's workbench.
- ORIN: "This isn't decoration. Look at the teeth on that edge. It's part of a machine." (N)
- GARRICK: "Stonecrown makes the finest gears in the six kingdoms. We never made this." (N)

**Objective: reconstruct the carvings.** On the reconstruction table, place the Eldervale stone, Liora's rubbing and the Irondeep fragment into the sockets whose inlays match their carvings. Incompatible pieces don't fit.
- GARRICK: "That pattern resembles something I once saw in our oldest mine." (S)
- STEVE: "What was it?" (S)
- GARRICK: "A chamber sealed long before my grandfather's time." (S)
- ELARA: "Can you open it?" (A — V1 speaker: Queen Aveline)
- GARRICK: "Perhaps. But I'd rather discover why it was sealed first." (S)
- GARRICK: "The Old Deeps flooded generations ago when the drains failed. If you want that chamber, we drain it first." (N)
- STEVE: "Then we drain it." (N)

**Engineering puzzle in the Old Deeps:**
1. Follow the mine rail down to the Old Deeps.
2. Patch the broken drainage channel wall so the water runs into the drain shaft. The flooded works hall empties.
3. Reset the Builder counterweights in the order the wall carving shows: largest, middle, smallest. A wrong order lets them drop back harmlessly.
4. The sealed works door grinds open (cinematic).

**Objective: recover the evidence** inside the works: the Builder machine plate and a gear housing.
- ORIN: "These machines weren't built by Stonecrown." (N)
- GARRICK: "No. My ancestors found them and swore to guard them. We built Irondeep around something older." (N)
- STEVE: "The same builders who left the monument." (N)
- GARRICK: "Then Stonecrown stands with you. My engineers, my miners, whatever you need." (N)
- GARRICK: "Solkar's scholars have asked about that symbol for years. Sahir of Sunspire would trade his library for a look at this plate." (N)

Outcome: Stonecrown joins. Steve receives the Builder machine plate (Stonecrown's fragment). Journal: *The Engraved Stone.*

## Scene 014 — The Records of Sunspire

Sunspire, capital of Solkar • Afternoon • Quest: The Sunspire Records
Cast: Steve, Elara, Orin, Sahir, Solkar scholars, Sunspire citizens.

**Arrival.** Cinematic over sandstone terraces, the great Sunspire tower, palm-shaded oasis and market awnings: "Sunspire — City of Solkar".
- ELARA: "It's so bright." (N)
- ORIN: "That tower's been here longer than the city around it, if the stories are right." (N)
- STEVE: "Let's find Sahir." (N)

City (optional, proximity): Sunspire citizens and scholars.
- CITIZEN: "Water from the oasis, a copper a cup!" (N)
- SCHOLAR: "Quiet in the stacks, please. The pages are older than your grandparents." (N)
- CITIZEN: "The House of Records hasn't been this busy since the eclipse year." (N)

**Objective: meet Sahir at the House of Records.**
- SAHIR: "I recognised the symbol as soon as the Queen's messenger described it." (S)
- STEVE: "You've seen it before?" (S)
- SAHIR: "Not with my own eyes. But I know where it's recorded." (S)
- SAHIR: "A survey conducted many generations ago mentioned a circle beneath six landmarks." (S)
- ORIN: "Six landmarks?" (S)
- SAHIR: "Each corresponding to one of the great civilisations." (S)

**Objective: find three catalogue entries in the stacks.** Their names give the old word for each landmark.
- STEVE: "Why did nobody know about this before?" (S)
- SAHIR: "Because the records were incomplete. Some pages were lost, others misunderstood." (S)
- ELARA: "Could somebody have hidden them?" (S)
- SAHIR: "Possibly. But missing information isn't proof of a conspiracy." (S)
- ORIN: "I'm starting to like him." (S)
- SAHIR: "Thank you. I think." (S)

**Objective: decode the Builder inscription.** The key tablet pairs each Builder glyph with a word. Press the four inscription glyphs in the order that spells the sentence on the key. A wrong glyph clears the wall.
- SAHIR: "'Open the circle only in its hour. What is opened out of its hour will remember the hour it should have kept.'" (N)
- ORIN: "Remember? Stone doesn't remember anything." (N)
- SAHIR: "The people who wrote this seem to have thought otherwise." (N)

**Objective: read the water-stained warning page.**
- SAHIR: "This one is worse. 'The deep note holds the world in time. Strike it wrongly, and time will keep two answers.'" (N)
- ELARA: "Two answers to what?" (N)
- SAHIR: "I wish I knew. The rest of the page is gone." (N)
- STEVE: "The deep note... like the humming Liora's rangers heard?" (N)
- SAHIR: "Perhaps." (N)

**Objective: compare the survey markings with the manuscript.**
- SAHIR: "There. That symbol represents a central structure." (S)
- STEVE: "The northern monument?" (S)
- SAHIR: "Perhaps. But it appears that the monument is only one part of something much larger." (S)

**Objective: mark the three catalogued landmarks on the great map** by lighting them. Cinematic: six lines connect across the map and meet near Awakening Valley.
- STEVE: "Everything leads back there." (S)
- SAHIR: "Then whatever was hidden beneath the valley was important enough for six civilisations to record." (S)

**Objective: open the crate from the Eldervale road** in Sahir's study. It is the sealed crate from Scene 011.
- SAHIR: "The crate your caravan saved was bound for this house. Solkar's piece." (N)
- STEVE: "The raiders were after it." (N)
- SAHIR: "My family's oldest book says it was given to us to keep, not to understand. Given by the First Hands, whoever they were." (N)
- SAHIR: "Lord Kaelen of Emberhold has refused every letter about this. If Veyrath holds a piece, he won't give it up easily." (N)
- STEVE: "Then we'll ask him in person." (N)

Outcome: Sahir joins. Steve receives Solkar's fragment. Journal: *The Sunspire Records.*

## Scene 015 — The Lord of Emberhold

Emberhold, seat of Veyrath • Evening • Quest: A Divided Council
Cast: Steve, Elara, Bram, Liora, Sahir, Thane Garrick, Queen Aveline, Lord Kaelen, Veyrath honour guards, Emberhold citizens.
Kaelen: proud and sharp-tongued. He is fiercely loyal to an oath his house has kept for generations, and he is tired of
Veyrath being treated as the "dangerous" kingdom. He is not a villain. He distrusts the others because they began
without him, and he fears what the thing his house guards might do in careless hands.

**Arrival.** The delegation crosses the causeway over the lava moat. Cinematic over basalt towers, lava falls and forge chimneys: "Emberhold — Seat of Veyrath".
- BRAM: "Is it always this warm?" (N)
- ELARA: "Don't touch the walls." (N)

City (optional, proximity):
- CITIZEN: "Mind the channels, outlander. The stone's hotter than it looks." (N)
- VEYRATH GUARD: "The Lord receives you in the Ember Hall. Weapons stay sheathed." (N)
- CITIZEN: "Eldervale's queen, here? My grandmother won't believe it." (N)

**Objective: enter the Ember Hall.**
- LORD KAELEN: "So this is the discovery everyone has been whispering about." (S)
- QUEEN AVELINE: "You were invited to examine the evidence." (S)
- KAELEN: "I was invited after the others had already begun their investigation." (S)
- GARRICK: "We barely understand what we found." (S)
- KAELEN: "And yet you've already carried pieces of it into another kingdom." (S)
- KAELEN: "I will not allow an ancient Veyrath legacy to be claimed by whoever reached it first." (S)
- STEVE: "Nobody's claiming anything." (S)
- KAELEN: "Not yet." (S)
- LIORA: "The evidence was discovered across several territories. This concerns us all." (S)
- KAELEN: "That is easy to say when your people already hold part of it." (S)
- QUEEN AVELINE: "Enough. We will establish a joint expedition and keep an open record of every discovery." (S)
- KAELEN: "Then Veyrath expects equal representation." (S)
- STEVE: "That's fair." (S)
- KAELEN: "And who gave you authority to lead this?" (S)
- STEVE: "Nobody. I was looking into something dangerous near my home." (S)
- STEVE: "If you know more about it, help us." (S)

**Negotiation (choice).**
- KAELEN: "Then tell me why Veyrath should open its vault to strangers." (N)
  - Evidence → STEVE: "Because the records show all six kingdoms were given a piece to keep. Yours too." (N)
    - KAELEN: "Records. Scholars' ink. Show me, then." (N)
  - Respect → STEVE: "Because Veyrath kept its piece longer and better than anyone. We need what you know, not just the stone." (N)
    - KAELEN: "Flattery from Firstbrook. Still, it happens to be true." (N)
  - Warning → STEVE: "Because if we don't understand it, something else may wake it first." (N)
    - KAELEN: "You think I haven't lain awake over that? Prove you understand it better than I do." (N)

**Objective: present the evidence to the council** (examine three pieces on the hall table, then present them in the order that makes the shared history clear). A wrong order sets Kaelen's trust lower and lets Steve start again.
- KAELEN: "Six kingdoms, one pattern. Very well. You've earned the truth." (N)
- KAELEN: "My house swore an oath before Emberhold had walls. Keep the Ember Key apart from the door, and never carry it in anger." (N)
- STEVE: "Door? What door?" (N)
- KAELEN: "The oath never said. Only that the key and the door must not meet by chance." (N)
- ELARA: "You've guarded it all this time without knowing why?" (N)
- KAELEN: "Veyrath keeps its word. That is the why." (N)
- KAELEN: "Every kingdom calls Veyrath the dangerous one. Fire, forges, temper. Yet we kept the most dangerous thing of all, and kept it safe." (N)

**Trial of the Vault.**
1. Dam the three lava channels across the vault approach (place blocks where the channels cross the path).
2. Light the three oath braziers in the order of the oath: *Keep*, *Apart*, *Never*. A wrong brazier gutters out and the order resets.
3. The vault opens (cinematic). Take the Ember Key from its pedestal.

**Closing.**
- KAELEN: "Very well. Veyrath will send its experts." (S)
- QUEEN AVELINE: "Then we have an agreement." (S)
- KAELEN: "An agreement to investigate. Not to surrender our interests." (S)
- STEVE: "That's a start." (S)
- KAELEN: "And the key does not leave my sight. Where it goes, Veyrath goes." (N)
- QUEEN AVELINE: "Captain Maren sends word from Tidewatch. Her divers have found something at the old coastal watch." (N)
- STEVE: "The last piece." (N)

Outcome: Veyrath joins, still cautious (`mc001.kaelen.trust` records how the negotiation went). Steve carries the Ember Key (Veyrath's fragment). Journal: *A Divided Council.*

## Scene 016 — The Fragment from Tidewatch

Tidewatch, harbour of Thalassia • Morning • Quest: The Sixth Fragment
Cast: Steve, Elara, Orin, Sahir, Garrick, Captain Maren, Thalassian sailors, Tidewatch dockhands.

**Arrival.** The party comes over the headland. Cinematic over the lighthouse, the busy docks, moored ships and the ruined coastal watch half sunk offshore: "Tidewatch — Harbour of Thalassia".

City (optional, proximity):
- DOCKHAND: "Watch your feet! Ropes everywhere this morning." (N)
- SAILOR: "Tide's turning. If the Captain wants the divers out, it'll be soon." (N)
- DOCKHAND: "Fresh catch! Cod, salmon, and one very angry crab." (N)

**Objective: meet Captain Maren on the docks.**
- CAPTAIN MAREN: "I'd have met you on the road, but the sea wasn't cooperative." (A — V1: "I apologise for arriving late. The sea wasn't cooperative.")
- ORIN: "Have you got the fragment?" (A — V1: "Did you bring the fragment?")
- MAREN: "Found it. Then the tremors brought half the old watch down on top of it." (N)
- MAREN: "We found it beneath an old coastal watch structure." (S)
- SAHIR: "Then the same builders may have worked across all six territories." (S)

**Harbour work.** Secure three damaged cargo barrels. Then repair the loading crane: replace its broken beam and rehang its chain.
- MAREN: "I heard you helped a caravan outside Eldervale." (S)
- STEVE: "We were in the right place." (S)
- MAREN: "The right place is usually where someone chooses to stand." (S)
- STEVE: "You sound like Rowan." (S)
- MAREN: "Then Rowan sounds sensible." (S)

**Recovery dive.**
- MAREN: "Follow the buoys out and keep to the bubble lines. Tidewatch divers have breathed off them for three hundred years." (N)
1. Swim out along the buoy line to the sunken watch. Columns of bubbles rising from the seabed give air.
2. Clear the fallen stones inside the watch's lower room.
3. Take the Tide Anchor from its housing, then swim back to the pier.

- MAREN: "The Tide Anchor. My grandfather swore it kept this coast from shaking." (N)
- ORIN: "Maybe it did. Look how it's made. It's meant to hold something steady." (N)

**Objective: lay the Tide Anchor beside the other fragments** on Maren's chart table. Cinematic: the fragments briefly glow together, the compass needle stops, and a line of light points toward Awakening Valley.
- MAREN: "Well. That's new." (S)
- ORIN: "I think we've just found our next destination." (S)
- MAREN: "I'll carry everyone to Valebridge myself. Every kingdom should be in one room for this." (N)

Outcome: Thalassia joins. Steve carries the Tide Anchor (Thalassia's fragment), the last of the six. Journal: *The Sixth Fragment.*

## Scene 017 — The Truth in the Fragments

Valebridge research chamber • Afternoon • Quest: Six Histories, One Mystery
Cast: Steve, Aveline, Garrick, Sahir, Kaelen, Maren, Liora, Orin, Elara, Bram.

**Opening.** The six delegations gather around the reconstruction table (cinematic).

**Objective: assemble the six fragments** in the table's sockets, each on its matching inlay.
- ORIN: "That's everything." (S — V1 Scene 016)
- SAHIR: "Not quite." (S — V1 Scene 016)
- ELARA: "The centre is missing." (S — V1 Scene 016)
- GARRICK: "A seventh stone?" (S — V1 Scene 016)
- STEVE: "Or something that was never meant to be removed." (S — V1 Scene 016)

**Six interpretations.**
- GARRICK: "Stonecrown records describe it as a foundation." (S)
- SAHIR: "Solkar's scholars called it a boundary." (S)
- MAREN: "Our maps identify it as a navigation marker." (S)
- LIORA: "Sylvaris preserved it as a warning." (S)
- KAELEN: "Veyrath's oldest accounts refer to a sealed inheritance." (S)
- STEVE: "You're all describing different parts of the same thing." (S)

**Objective: review the six records** (one lectern per kingdom). Then Orin opens the skylight over the diagram (cinematic):
- ORIN: "They're not contradicting one another." (S)
- SAHIR: "The foundation supports the structure." (S)
- MAREN: "The marker shows where it is." (S)
- LIORA: "The warning explains why it should remain undisturbed." (S)
- STEVE: "And the boundary?" (S)
- SAHIR: "That part we still don't understand." (S)

**Objective: read the writing revealed by the light** in the grooves of the assembled diagram.
- SAHIR: "There's writing in the grooves, finer than any chisel we own." (N)
- SAHIR: "'We who built before the crowns leave this to six keepers. Keep it apart. Keep it whole. Do not open the door.'" (N)
- GARRICK: "Before the crowns. Before any of us." (N)
- STEVE: "The Ancient Builders. They made the gateway. Our kingdoms only inherited the pieces." (N)
- KAELEN: "And swore oaths they never understood." (N)
- KAELEN: "I owe this council an admission." (S)
- AVELINE: "We're listening." (S)
- KAELEN: "I was prepared to treat this discovery as another dispute between kingdoms." (S)
- STEVE: "And now?" (S)
- KAELEN: "Now I believe there's something far more important beneath the valley." (S)
- LIORA: "Then perhaps we can investigate it together." (S)
- KAELEN: "We can try." (S)

**Objective: decode the route on the great floor map.** The diagram's spiral reads: *begin where the six lines meet, climb to the old stone, go down at its eastern foot.* Mark the three points in that order. A wrong point clears the route.

**Planning cinematic.**
- STEVE: "Here. The central intersection." (S)
- GARRICK: "My engineers can help secure the excavation." (S)
- MAREN: "I'll organise transport." (S)
- SAHIR: "I'll bring the translated records." (S)
- LIORA: "Sylvaris will send scouts." (S)
- AVELINE: "And Eldervale will provide supplies." (S)
- BRAM: "Sounds like we're going somewhere." (S)
- ELARA: "Somewhere nobody's been for a very long time." (S)

**Objective: prepare the expedition.** Collect torches and timber from the quartermaster's supply chest.

Outcome: Act III complete. The joint expedition is assembled. Journal: *Six Histories, One Mystery.*

## Scene 018 — Beneath the Ancient Stone

Awakening Valley excavation, the Ancient Northern Monument • Dawn • Quest: The Sealed Chamber
Cast: Steve, Elara, Bram, Orin, Liora, Sahir, Garrick, Maren, Kaelen; expedition workers, researchers, engineers, rangers, sailors, Veyrath guards.

**Opening cinematic** through the busy camp around the monument.
- BRAM: "I've never seen so many people working together." (S)
- ELARA: "Let's hope they stay that way." (S)
- ORIN: "They will if nobody touches the wrong thing." (S)
- STEVE: "You keep saying that." (S)
- ORIN: "And you keep reaching toward mysterious glowing stones." (S)
- STEVE: "Fair point." (S)
- GARRICK: "My engineers found an entrance beneath the eastern foundation." (S)
- SAHIR: "Does it match the plans?" (S)
- GARRICK: "Too closely for coincidence." (S)

**Phase 1: secure the excavation.** Repair the two broken supports, light the descent, and clear the loose rubble without breaking the carved evidence. Then rebuild the collapsed steps and escort the first research team down. During the descent (proximity):
- ELARA: "The air feels different down here." (S)
- STEVE: "Colder?" (S)
- ELARA: "Still. Like nobody's disturbed it in centuries." (S)
- BRAM: "Let's keep it that way." (S)
- ORIN: "We came here specifically to disturb it." (S)
- BRAM: "You know what I mean." (S)

**Phase 2: the Builder machine hall.**
1. *Restore the conduits.* Three empty sockets in the hall's power channels take copper blocks from Garrick's engineers. When all three are fitted, the channels wake and hum.
   - GARRICK: "Copper channels, older than any mine of mine. And they're humming." (N)
   - SAHIR: "The deep note. The Sunspire pages spoke of it." (N)
2. *The Hall of Still Clocks.* Investigate three Builder devices.
   - *A clock that does not move:* ORIN: "This mechanism has been turning for centuries and it hasn't moved an inch." (N) SAHIR: "Or it only moves when time does something it shouldn't." (N)
   - *Twin circles carved side by side:* ELARA: "Two circles. Exactly the same." (N) SAHIR: "A door and its echo, perhaps." (N)
   - *A warning carving:* GARRICK: "Another warning. Whoever built this was very worried about something." (N)
3. *The sealed door.*
   - ORIN: "Six symbols. Six records." (S)
   - SAHIR: "The carvings tell us how to align them." (S)
   - STEVE: "And what's behind the door?" (S)
   - ORIN: "That's what we're here to learn." (S)
   Read the door's carving, then press the six lock stones in the order it shows (sunwise from Eldervale). A wrong lock resets the mechanism harmlessly.

**Door cinematic.**
- BRAM: "Everybody stand back!" (S)
- ELARA: "Steve!" (S)
- ORIN: "It worked." (S)
- SAHIR: "After all these generations..." (S)

**Phase 3: explore the chamber.** Survey the gateway's supports and the chamber carvings.
- STEVE: "What is that?" (S)
- ORIN: "I haven't the faintest idea." (S)
- SAHIR: "The records called this place a boundary." (S)
- ELARA: "Between what?" (S)
- SAHIR: "That's the part they never explained." (S)
- GARRICK: "This chamber has survived longer than any structure I've ever seen." (S)
- STEVE: "Then let's make sure it survives today." (S)

**Conclusion.** Steve finds the compass pedestal on the landing. The compass points straight at the gateway.
- ORIN: "Don't activate anything yet." (S)
- STEVE: "I wasn't planning to." (S)
- ELARA: "Steve..." (S)
Cut to black as a locking mechanism moves somewhere inside the structure. Continues straight into Scene 019.

## Scene 019 — The Door That Should Not Open

The First Gateway chamber • Continuous • Quest: An Impossible Door
Cast: Steve, Elara, Orin, Bram, Sahir, Garrick, Liora, Kaelen, Maren (no lines), expedition personnel.

**Layout.** The door opens onto a landing on the chamber's south side. A deep chasm rings the gateway's dais, and a single
stone bridge crosses it. Orin's observation ledge is on the west wall. Broken pillars and hanging stone rings stand
in the chasm, and the gateway stands upright on the dais.

**1. The expedition enters.** Researchers spread out. Elara crosses the bridge to study the console behind the gateway.

**2. Investigate the machinery.** Examine the landing pylon, the bridge-head console and the gateway's base from the bridge.
- ORIN: "These channels feed the ring. Every one of them." (N)
- GARRICK: "The bridge was built to carry weight. Something heavy was moved across it once." (N)
- SAHIR: "Same glyphs as the Sunspire wall. 'In its hour.'" (N)

**3. The gateway activates.**
- ORIN: "Nobody touch anything." (S)
- BRAM: "Nobody is touching anything!" (S)
- SAHIR: "The structure is responding to something." (S)
- STEVE: "The compass?" (S)
- ORIN: "I don't think the compass is causing this." (S)
A luminous surface forms inside the ring.
- STEVE: "What... is that?" (S)
- ORIN: "I don't know." (S)
- SAHIR: "None of the records described anything like this." (S)
- ELARA: "Steve, we have to leave." (S)
- STEVE: "I can see something." (S) — *for a moment, a shape too indistinct to name*
- ELARA: "Steve!" (S)

**4–6. Destabilisation.** A pulse throws dust from the dome and the bridge's middle falls into the chasm. Elara is cut off on the dais, and falling stone pins her in the alcove behind the gateway.
- ELARA: "Steve! The bridge is collapsing!" (R)
- ORIN: "The gateway's overloading! We need to evacuate!" (R)
- STEVE: "Elara, I'm coming!" (R)
- ELARA: "No! You'll get trapped too!" (R)
- STEVE: "I'm not leaving you!" (R)
- BRAM: "Steve! Get them out!" (S — V1 had Bram bracing a beam)
- BRAM: "Come on, everyone! Move!" (A — V1 speaker: Elara)
- LIORA: "The upper passage is clear!" (S)
- GARRICK: "My engineers will hold the supports!" (S)

**7–8. Reach Elara.** The player crosses the chasm along the east ledge, the broken pillar tops and the hanging stone rings, while
pulses shake the chamber and debris falls. A fall drops into the chasm's catch pool and Steve climbs back to the landing.
- ORIN: "Keep to the stone! The rings will hold you!" (N, while crossing)

**9. The rescue.** Clear the fallen stone pinning Elara.
- STEVE: "Take my hand!" (N)
- ELARA: "The floor's going!" (N)
- STEVE: "I've got you." (N)
Then rebuild the bridge's gap so Elara can cross back. She follows Steve over.

**10–13. Divergence and closing.** As they reach the landing, the gateway shows a horizon of unfamiliar stars.
- STEVE: "Those stars..." (S)
- SAHIR: "What about them?" (S)
- STEVE: "I've never seen them before." (S)
The gateway's hidden mechanism wakes. For a heartbeat the whole chamber doubles: two near-identical chambers overlap and slide apart, with a bridge both whole and broken. Then everything settles. No one remarks on it. The surface collapses inward and vanishes, and only a faint blue glow stays in the keystone.
- ELARA: "Steve? Are you alright?" (R)
- STEVE: "Yeah... I think so." (R)

**14. Escape.** Lead everyone up the stair to the surface.

**Aftermath** at the dig entrance:
- BRAM: "Everyone still alive?" (S)
- ORIN: "I think so." (S)
- STEVE: "What did we just see?" (S)
- SAHIR: "Something our ancestors may have understood." (S)
- GARRICK: "Then why did they seal it?" (S)
- KAELEN: "Perhaps because they knew exactly what would happen if it opened." (S)
- STEVE: "Or because they were afraid of something they couldn't explain." (S)
- LIORA: "Either way, we cannot pretend this didn't happen." (S)

(V1's "ELARA: Steve?" / "STEVE: I'm fine." are replaced by the required lines in the chamber.)

**Ending cinematic.** The camera returns to the silent gateway. The keystone gives one last, almost inaudible pulse. Deep beneath the chamber, another mechanism briefly answers. Cut to black. → Scene 020.

**Story state recorded (internal; never shown to the player):**
- World event `mc001_first_divergence`, recorded once per world.
- Flags `mc001_first_divergence`, `elara_rescued`, `gateway_temporal_branch_recorded` and `first_gateway_closed`.
- Value `primordial_steve_multiversal_travel_unlocked` = 0.
All of these are idempotent. Replaying the scene, reloading, dying or rejoining never sets them twice or rolls them back.
