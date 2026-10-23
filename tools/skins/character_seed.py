"""Hand-maintained identity table for every collected skin.

Run build_registry.py after editing; it writes the registry JSON used by the mod,
normalised 64x64 textures and docs/CHARACTER_REGISTRY.md.
Only facts stated by the project owner or the story documents go in `role`.
"""

A = "Ancient figures, TMMA and Federation"
V = "Main Villains"
S = "Major Steves & Crossovers"

# id, name, universe, group, faction, role, source (relative to art/skins/source), notes
CHARACTERS = [
    # --- MC-001 -----------------------------------------------------------
    ("primordial_steve", "Primordial Steve", "MC-001", "MC-001", "MC-001", "His ultimate role is preserved for Saga 24.", "MC-001/Primordial Steve (MC-001)/cbe20ed58814c5e1.png", ""),
    ("primordial_steve_empowered", "Primordial Steve (Empowered)", "MC-001", "MC-001", "MC-001", "Empowered form of Primordial Steve.", "MC-001/Primordial Steve - Empowered MC-001/defd58707bfee808.png", "Alternate form; shares identity with primordial_steve."),
    ("elder_rowan", "Elder Rowan", "MC-001", "MC-001", "MC-001", "", "MC-001/Elder Rowan(MC-001)/be741cde6ea03fb4.png", ""),
    ("elara", "Elara", "MC-001", "MC-001", "MC-001", "", "MC-001/Elara (MC-001)/0015cf5b9969b142.png", ""),
    ("bram", "Bram", "MC-001", "MC-001", "MC-001", "", "MC-001/Bram (MC-001)/754f91e0a4931dff.png", ""),
    ("captain_maren", "Captain Maren", "MC-001", "MC-001", "MC-001", "", "MC-001/Captain Maren (MC-001)/d12e8e32f857fcaa.png", ""),
    ("liora", "Liora", "MC-001", "MC-001", "MC-001", "", "MC-001/Liora (MC-001)/a88e92c1ed49d91f.png", ""),
    ("lord_kaelen", "Lord Kaelen", "MC-001", "MC-001", "MC-001", "", "MC-001/Lord Kaelen (MC-001)/93aa1dcf1dc874c6.png", ""),
    ("orin", "Orin", "MC-001", "MC-001", "MC-001", "", "MC-001/Orin (MC-001)/0c2348c046c49671.png", ""),
    ("queen_aveline", "Queen Aveline", "MC-001", "MC-001", "MC-001", "", "MC-001/Queen Aveline (MC-001)/queen.png", ""),
    ("sahir", "Sahir", "MC-001", "MC-001", "MC-001", "", "MC-001/Sahir (MC-001)/34b05a641066240d.png", ""),
    ("thane_garrick", "Thane Garrick", "MC-001", "MC-001", "MC-001", "", "MC-001/Thane Garrick (MC-001)/135896cfc2935928.png", ""),
    # --- MC-100 -----------------------------------------------------------
    ("steve_prime", "Steve Prime", "MC-100", "MC-100", "MC-100", "", "MC-100/Steve Prime (MC-100)/46dec770857edc6d.png", ""),
    ("alex_mc100", "Alex", "MC-100", "MC-100", "MC-100", "", "MC-100/Alex (MC-100)/2ec53a7c141b6b29.png", ""),
    ("arthur", "Arthur", "MC-100", "MC-100", "MC-100", "", "MC-100/Arthur (MC-100)/bc16af750e271683.png", ""),
    ("betty", "Betty", "MC-100", "MC-100", "MC-100", "", "MC-100/Betty (MC-100)/a1c611001ee708ed.png", ""),
    ("griff", "Griff", "MC-100", "MC-100", "MC-100", "", "MC-100/Griff (MC-100)/a464d54ff01c34e0.png", ""),
    ("monclair", "Monclair", "MC-100", "MC-100", "MC-100", "", "MC-100/Monclair (MC-100)/84738f7a56eacebf.png", ""),
    # --- Major Steves & crossovers -----------------------------------------
    ("prehistoric_steve", "Prehistoric Steve", "MC-002", "Major Steves & Crossovers", "Steve variant", "", S + "/Prehistoric Steve (MC-002)/43a430df7b7c0df4.png", ""),
    ("witch_alex", "Witch Alex", "MC-002", "Major Steves & Crossovers", "Alex variant", "", S + "/Witch Alex (MC-002)/Witch-Alex-on-planetminecraft-com.png", "Shares MC-002 with Prehistoric Steve; confirm this is intended. Skin file came from Planet Minecraft, so check its licence before release."),
    ("kingdomless_steve", "Kingdomless Steve", "MC-003", "Major Steves & Crossovers", "Steve variant", "", S + "/Kingdomless Steve MC-003/bfa6a134d451466f.png", ""),
    ("wanderer_steve", "Wanderer Steve", "MC-004", "Major Steves & Crossovers", "Steve variant", "", S + "/Wanderer Steve MC-004/401b1047808d4281.png", "Two skins supplied; the second is registered as wanderer_steve_alt."),
    ("wanderer_steve_alt", "Wanderer Steve (Alternate)", "MC-004", "Major Steves & Crossovers", "Steve variant", "", S + "/Wanderer Steve MC-004/a412bf05142eb060.png", "Alternate outfit of wanderer_steve."),
    ("plague_doctor_steve", "Plague Doctor Steve", "MC-005", "Major Steves & Crossovers", "Steve variant", "", S + "/Plague Doctor Steve MC-005/7d6fc62ba4dd1fd3.png", ""),
    ("redstone_overlord_steve", "Redstone Overlord Steve", "MC-006", "Major Steves & Crossovers", "Steve variant", "", S + "/Redstone Overlord Steve MC-006/311318bd7dbfc3cd.png", ""),
    ("end_king_steve", "End King Steve", "MC-007", "Major Steves & Crossovers", "Steve variant", "", S + "/End King Steve MC-007/9543d3db262b9c60.png", ""),
    ("nether_emperor_steve", "Nether Emperor Steve", "MC-008", "Major Steves & Crossovers", "Steve variant", "", S + "/Nether Emperor Steve MC-008/288706fb6fff2485.png", ""),
    ("time_lost_steve", "Time-Lost Steve", "MC-009", "Major Steves & Crossovers", "Steve variant", "", S + "/Time-Lost Steve MC-009/mrclock.png", ""),
    ("glitchwalker_steve", "Glitchwalker Steve", "MC-010", "Major Steves & Crossovers", "Steve variant", "", S + "/Glitchwalker steve MC-010/4ae6b6ca0efb59b7.png", "Hero variant; not the villain Glitch Steve."),
    ("corrupted_guardian_steve", "Corrupted Guardian Steve", "MC-013", "Major Steves & Crossovers", "Steve variant", "", S + "/Corrupte Guardian Steve MC-013/04811539c92e572e.png", "Folder spelled 'Corrupte'; name normalised."),
    ("ancient_builder_steve", "Ancient Builder Steve", "MC-014", "Major Steves & Crossovers", "Steve variant", "Has an independent story arc (Story Expansion 1).", S + "/Acient Builder Steve MC-014/dbe354b5b5f46c5e.png", "Folder spelled 'Acient'; name normalised."),
    ("cosmic_steve", "Cosmic Steve", "MC-015", "Major Steves & Crossovers", "Steve variant", "Has an independent story arc (Story Expansion 1).", S + "/Cosmic Steve (MC-015)/781708912c3ced6a.png", ""),
    ("survivor_steve", "Survivor Steve", "MC-016", "Major Steves & Crossovers", "Steve variant", "", S + "/Survivor Steve Mc-016/2fc001e4492aebe4.png", ""),
    ("herald_steve", "Herald Steve", "MC-017", "Major Steves & Crossovers", "Steve variant", "", S + "/Herald Steve MC-017/c101bd5ecdd8091d.png", ""),
    ("ironbound_steve", "Ironbound Steve", "MC-018", "Major Steves & Crossovers", "Steve variant", "", S + "/IronBound Steve MC-018/5fd193f91ecc3e2e.png", ""),
    ("exiled_prince_steve", "Exiled Prince Steve", "MC-019", "Major Steves & Crossovers", "Steve variant", "", S + "/Exiled Prince Steve MC-019/b4fe85907d9c9a4d.png", ""),
    ("warden_steve", "Warden Steve", "MC-058", "Major Steves & Crossovers", "Steve variant", "Has an independent story arc (Story Expansion 1).", S + "/Wardem Steve MC-058/18b7fbd018a5d83a.png", "Folder spelled 'Wardem'; name normalised. Two skins supplied; the second is warden_steve_alt."),
    ("warden_steve_alt", "Warden Steve (Alternate)", "MC-058", "Major Steves & Crossovers", "Steve variant", "", S + "/Wardem Steve MC-058/91329339d410a06f.png", "Alternate look of warden_steve."),
    ("voidwalker_steve", "Voidwalker Steve", "MC-073", "Major Steves & Crossovers", "Steve variant", "", S + "/Voidwalker Steve MC-073/8d93fed9f4c46709.png", "IDENTICAL skin file to Oblivion Steve. One of them needs its own skin."),
    ("chrono_steve", "Chrono Steve", "MC-101", "Major Steves & Crossovers", "Steve variant", "", S + "/Chrono Steve (MC-101)/e763c1d597ac357a.png", ""),
    ("jesse", "Jesse", "MC-200", "Major Steves & Crossovers", "Minecraft: Story Mode", "Protagonist of Minecraft: Story Mode. Reuben is Jesse's pig.", S + "/Jesse Story Mode MC-200/1a8a173e20fb491d.png", "Identity corrected by owner: Jesse belongs to MC-200."),
    ("reuben", "Reuben", "MC-200", "Major Steves & Crossovers", "Minecraft: Story Mode", "Jesse's pig.", None, "Identity corrected by owner: Reuben is Jesse's pig, not a human character. Uses the vanilla pig model; no skin needed."),
    ("movie_steve", "Movie Steve", "MC-300", "Major Steves & Crossovers", "Movie universe", "", S + "/Movie Steve (MC-300)/aba97be291f29ae0.png", "Identity corrected by owner: Movie Steve belongs to MC-300."),
    ("professor_steve", "Professor Steve", "MC-1323", "Major Steves & Crossovers", "Steve variant", "", S + "/Professor Steve (MC-1323)/da2e61ba815d2854.png", ""),
    ("aeon_steve", "Aeon Steve", "MC-TBD", "Major Steves & Crossovers", "Steve variant", "", S + "/Aeon Steve (Temp)/ff888782a4d0bec9.png", "Folder marked '(Temp)': name and universe are temporary."),
    ("mystery_dweller", "MysteryDweller", None, "MysteryDweller", "Forgotten Steves", "The first Minecraft inter-universal traveller (Story Expansion 1).", "MysteryDweller/acaf0b6e753b3609.png", "Home universe not stated in the documents I have."),
    # --- Main villains ----------------------------------------------------
    ("herobrine", "Herobrine", None, "Main Villains", "Villain", "Leads a strategic campaign with consequences across multiple universes (Story Expansion 1).", V + "/Herobrine/5ec3677ccc95b332.png", ""),
    ("entity_303", "Entity 303", None, "Main Villains", "Villain", "", V + "/Entity 303/09f5855f298cf918.png", ""),
    ("null", "Null", None, "Main Villains", "Villain", "", V + "/Null/038b147ff9a8a1e6.png", ""),
    ("glitch_steve", "Glitch Steve", None, "Main Villains", "Villain", "", V + "/GLitch Steve/61af210cee8cf65a.png", "Villain; not the hero Glitchwalker Steve."),
    ("virus_steve", "Virus Steve", None, "Main Villains", "Villain", "", V + "/Virus Steve/e9e97cb4c3eec4dc.png", ""),
    ("assassin_steve", "Assassin Steve", "MC-TBD", "Main Villains", "Villain", "", V + "/Assassin Steve MC-TBD/bed66d5c6b1a25fc.png", ""),
    ("corrupted_steve", "Corrupted Steve", "MC-TBD", "Main Villains", "Villain", "", V + "/Corrupted Steve MC-TBD/7a642cdc6e9a2891.png", ""),
    ("dark_samurai_steve", "Dark Samurai Steve", "MC-TBD", "Main Villains", "Villain", "", V + "/Dark Samurai Steve MC-TBD/fa98c2af0e6fac96.png", ""),
    ("oblivion_steve", "Oblivion Steve", "MC-TBD", "Main Villains", "Villain", "", V + "/Oblivion Steve MC-TBD/8d93fed9f4c46709.png", "IDENTICAL skin file to Voidwalker Steve (MC-073). One of them needs its own skin."),
    ("shadow_steve", "Shadow Steve", "MC-TBD", "Main Villains", "Villain", "", V + "/Shadow Steve MC-TBD/1d429c21f37676a2.png", ""),
    ("sovereign_steve", "Sovereign Steve", "MC-TBD", "Main Villains", "Villain", "", V + "/Sovereign Steve MC-TBD/5bcb6eb8f8bb8bf9.png", ""),
    # --- Ancient figures, TMMA and Federation -----------------------------
    ("architect_representative", "Architect Representative", None, A, "Architects", "Speaks for the ancient Architects.", A + "/Architect Representative/96a888c483e6c40c.png", ""),
    ("notch", "Notch", None, A, "Ancient figures", "Fictional in-story figure; the Notch/Jeb revelation is preserved for Saga 24.", A + "/Notch/5d5eb6d84b57ea29.png", "Legacy 64x32 skin; converted to 64x64."),
    ("jeb", "Jeb", None, A, "Ancient figures", "Fictional in-story figure; the Notch/Jeb revelation is preserved for Saga 24.", A + "/Jeb/747aed816e687730.png", "Legacy 64x32 skin; converted to 64x64."),
    ("draxson", "Draxson", None, A, "Unconfirmed", "", A + "/Draxson/976b1b9485fcf95c.png", "Role and faction not stated in the documents I have."),
    ("evaulx", "Evaulx", None, A, "Unconfirmed", "", A + "/Evaulx/4de36baabf970dbd.png", "Role and faction not stated in the documents I have."),
    ("temporal_custodian", "Temporal Custodian", None, A, "Unconfirmed", "", A + "/Temporal Custodian/27ed993dd530d9e6.png", ""),
    ("federation_engineer", "Federation Engineer", None, A, "Minecraft Multiverse Federation", "The Federation is founded on MC-001 and stays separate from the TMMA.", A + "/Federation Engineer/265703c130110b02.png", ""),
    ("tmma_commander", "TMMA Commander", None, A, "TMMA", "", A + "/TMMA Commander/3033f7160d226376.png", ""),
    ("tmma_agent", "TMMA Agent", None, A, "TMMA", "", A + "/TMMA Agents/cffd94eb8cc74d2a.png", "Helmeted uniform."),
    ("tmma_field_agent", "TMMA Field Agent", None, A, "TMMA", "", A + "/TMMA Field Agent/543f56fb2b80fc9a.png", "Helmeted uniform."),
    ("tmma_guard", "TMMA Guard", None, A, "TMMA", "", A + "/TMMA Guards/732831a749398817.png", "Helmeted uniform."),
    ("tmma_delta_force", "TMMA Special Delta Force", None, A, "TMMA", "", A + "/TMMA Special Delta Force/d58810bae6811d75.png", "Helmeted uniform."),
    ("tmma_delta_force_b", "TMMA Special Delta Force (B)", None, A, "TMMA", "", A + "/TMMA Special Delta Force/e134fecee03ba534.png", "Second Delta Force skin."),
    ("tmma_scientist", "TMMA Scientist", None, A, "TMMA", "", A + "/TMMA Scientist/dff172374365adf7.png", ""),
    ("tmma_customer_service", "TMMA Customer Service", None, A, "TMMA", "", A + "/TMMA Customer Service/cb8f9c69fe707a90.png", "Legacy 64x32 skin; converted to 64x64."),
    ("tmma_crew_grey", "TMMA Crew (Grey)", None, A, "TMMA", "", A + "/TMMA Crew/3d4225ce3b7d940b.png", ""),
    ("tmma_crew_officer", "TMMA Crew (Officer)", None, A, "TMMA", "", A + "/TMMA Crew/aed99d488ea2fd26.png", ""),
    ("tmma_crew_purple", "TMMA Crew (Purple)", None, A, "TMMA", "", A + "/TMMA Crew/e91571f16e92cd96.png", "'e91571f16e92cd96 (1).png' is a byte-identical duplicate and was skipped."),
    ("tmma_crew_white", "TMMA Crew (White)", None, A, "TMMA", "", A + "/TMMA Crew/f773604c0b092059.png", ""),
]

# Uniform templates the TMMA variant generator may re-face. Helmeted uniforms are
# excluded because their faces are part of the uniform. cap_rows keeps the top rows
# of the head (cap/hood brim) untouched; keep_hat preserves the outer head layer.
TMMA_TEMPLATES = {
    "tmma_crew_grey": {"cap_rows": 3, "keep_hat": True},
    "tmma_crew_officer": {"cap_rows": 0, "keep_hat": False},
    "tmma_crew_purple": {"cap_rows": 0, "keep_hat": False},
    "tmma_crew_white": {"cap_rows": 0, "keep_hat": False},
    "tmma_scientist": {"cap_rows": 0, "keep_hat": False},
}
VARIANTS_PER_TEMPLATE = 4

# Names shown on subtitles where the registry name is not how people address the character.
SPEAKER_NAMES = {
    "primordial_steve": "Steve",  # Saga 1 subtitles read STEVE; his title is not used in everyday speech.
}

# Unnamed Saga 1 NPC types (tools/skins/npc_types.py paints them). Every member of a type shares one
# outfit; faces, hair and identifying features differ. Scenes are Saga 1 scene numbers from the screenplay.
# type id, display name, speaker name, faction, count, scenes, role
NPC_TYPES = [
    ("firstbrook_villager", "Firstbrook Villager", "Villager", "Firstbrook (Eldervale)", 6, [1, 5, 20],
     "Residents of Firstbrook; Scene 005's volunteers are drawn from them."),
    ("firstbrook_farmer", "Firstbrook Farmer", "Farmer", "Firstbrook (Eldervale)", 3, [1, 20], "Farmers working Firstbrook's fields."),
    ("firstbrook_guard", "Firstbrook Guard", "Guard", "Firstbrook (Eldervale)", 3, [1, 5, 20], "Village guards who patrol the paths and the north gate."),
    ("expedition_worker", "Expedition Worker", "Worker", "Eldervale", 4, [5, 18, 19, 20],
     "Road and excavation workers; Eldervale's supply crews at the monument."),
    ("expedition_researcher", "Expedition Researcher", "Researcher", "Eldervale", 3, [18, 19, 20], "Researchers inspecting the sealed chamber."),
    ("sylvaris_ranger", "Sylvaris Ranger", "Ranger", "Sylvaris", 4, [10, 18, 19], "Forest rangers who serve with Liora."),
    ("eldervale_official", "Eldervale Official", "Adviser", "Eldervale", 3, [12, 13, 14, 15, 17], "Queen Aveline's advisers at the Valebridge council."),
    ("eldervale_royal_guard", "Eldervale Royal Guard", "Royal Guard", "Eldervale", 3, [12, 13, 14, 15, 17, 20], "The Queen's guard in Valebridge."),
    ("stonecrown_guard", "Stonecrown Guard", "Stonecrown Guard", "Stonecrown", 2, [13], "Thane Garrick's escorts carrying the Irondeep stone."),
    ("stonecrown_engineer", "Stonecrown Engineer", "Engineer", "Stonecrown", 4, [18, 19, 20], "Engineers who build the excavation supports."),
    ("solkar_scholar", "Solkar Scholar", "Scholar", "Solkar", 3, [14, 18, 19], "Scholars of Sunspire who travel with Sahir."),
    ("veyrath_guard", "Veyrath Honour Guard", "Veyrath Guard", "Veyrath", 4, [15, 17, 18, 19], "Lord Kaelen's armed honour guard from Emberhold."),
    ("thalassian_sailor", "Thalassian Sailor", "Sailor", "Thalassia", 4, [16, 18, 19], "Captain Maren's harbour crew and rope specialists."),
    ("merchant", "Travelling Merchant", "Merchant", "Independent traders", 4, [11, 12], "The eastern road caravan and Valebridge's market."),
    ("raider", "Raider", "Raider", "Eastern road raiders", 4, [11], "Human raiders who attack the caravan."),
    ("raider_leader", "Raider Leader", "Raider Leader", "Eastern road raiders", 1, [11], "Leader of the eastern road raiders."),
    ("valebridge_citizen", "Valebridge Citizen", "Citizen", "Eldervale", 8, [12, 17], "Residents of Valebridge, Eldervale's capital."),
    ("irondeep_miner", "Irondeep Miner", "Miner", "Stonecrown", 8, [13], "Miners, smiths and residents of Irondeep."),
    ("sunspire_citizen", "Sunspire Citizen", "Citizen", "Solkar", 8, [14], "Residents, traders and copyists of Sunspire."),
    ("emberhold_citizen", "Emberhold Citizen", "Citizen", "Veyrath", 8, [15], "Forge workers and residents of Emberhold."),
    ("tidewatch_dockhand", "Tidewatch Dockhand", "Dockhand", "Thalassia", 8, [16], "Dock workers, divers and fishers of Tidewatch."),
]

# Factions and Saga 1 scene appearances of the named MC-001 cast (from each scene's cast list).
SAGA1_CAST = {
    "primordial_steve": ("Firstbrook (Eldervale)", list(range(1, 21))),
    "elara": ("Firstbrook (Eldervale)", list(range(1, 21))),
    "elder_rowan": ("Firstbrook (Eldervale)", [1, 2, 5, 20]),
    "orin": ("Firstbrook (Eldervale)", [1, 4, 5, 6, 7, 13, 14, 16, 17, 18, 19, 20]),
    "bram": ("Firstbrook (Eldervale)", [1, 2, 3, 5, 6, 8, 9, 10, 11, 12, 17, 18, 19, 20]),
    "liora": ("Sylvaris", [10, 11, 12, 15, 17, 18, 19, 20]),
    "queen_aveline": ("Eldervale", [12, 13, 14, 15, 17, 20]),
    "thane_garrick": ("Stonecrown", [13, 15, 16, 17, 18, 19, 20]),
    "sahir": ("Solkar", [14, 15, 16, 17, 18, 19, 20]),
    "lord_kaelen": ("Veyrath", [15, 17, 18, 19, 20]),
    "captain_maren": ("Thalassia", [16, 17, 18, 19, 20]),
}
