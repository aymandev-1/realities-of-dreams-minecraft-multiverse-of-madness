package net.realitiesofdreams.world;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.story.scene.SceneDef;
import net.realitiesofdreams.story.world.Mc001Terrain;
import net.realitiesofdreams.world.site.FirstbrookBuilder;
import net.realitiesofdreams.world.site.SiteLayout;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Builds each scene's site once per world from its placement registry
 * (rod-story/sites/&lt;site&gt;.json), and answers which blocks the story protects.
 */
public final class SceneWorld {
    private static final Map<String, SiteLayout> LAYOUTS = new HashMap<>();

    private SceneWorld() {}

    @Nullable
    public static SiteLayout layout(String site) {
        return LAYOUTS.computeIfAbsent(site, s -> {
            try {
                return SiteLayout.load(s);
            } catch (RuntimeException e) {
                RealitiesOfDreams.LOGGER.warn("No layout for site {}: {}", s, e.getMessage());
                return null;
            }
        });
    }

    /**
     * Builds the scene's site once per world. Scenes that share a site (Firstbrook hosts Scenes
     * 001 to 005) share one build; the northern monument (Scene 006 on) has its own. Each
     * Firstbrook piece is recorded as it finishes, so a build interrupted by a crash resumes
     * where it stopped.
     */
    public static void ensureBuilt(ServerLevel level, SceneDef scene) {
        SceneWorldData data = SceneWorldData.get(level);
        String key = "built:site:" + scene.site();
        if (data.isDone(key)) {
            // A scene played out of order (chapter select) at an already-built monument still gets its clue blocks.
            if (scene.site().equals("northern_monument") && !data.isDone("clues:" + scene.id())) {
                placeClues(level, scene);
                data.markDone("clues:" + scene.id());
            }
            return;
        }
        if (scene.site().equals("valley_crossing")) {
            net.realitiesofdreams.world.site.ValleyCrossing.build(level, ModDimensions.anchor(scene));
            // Scenes 008 and 009 share the valley; each one's clue blocks are part of the place.
            for (SceneDef s : net.realitiesofdreams.Story.content().scenes()) if (s.site().equals(scene.site())) placeClues(level, s);
            data.markDone(key);
            RealitiesOfDreams.LOGGER.info("Built site {}", scene.site());
            return;
        }
        if (scene.site().equals("northern_monument")) {
            net.realitiesofdreams.world.site.NorthernMonument.build(level, ModDimensions.anchor(scene));
            placeClues(level, scene);
            data.markDone("clues:" + scene.id());
            data.markDone(key);
            RealitiesOfDreams.LOGGER.info("Built site {}", scene.site());
            return;
        }
        SiteLayout layout = layout(scene.site());
        if (layout == null || !scene.site().equals("firstbrook")) {
            RealitiesOfDreams.LOGGER.warn("No builder for site {} (scene {})", scene.site(), scene.id());
            data.markDone(key);
            return;
        }
        long start = System.currentTimeMillis();
        BlockPos anchor = ModDimensions.anchor(scene);
        FirstbrookBuilder builder = new FirstbrookBuilder(level, anchor);
        for (SiteLayout.Piece piece : layout.pieces()) {
            String pieceKey = "built:piece:" + scene.site() + "/" + piece.id();
            if (data.isDone(pieceKey)) continue;
            long t = System.currentTimeMillis();
            if (!placeTemplate(level, anchor, scene.site(), piece)) builder.build(piece);
            data.markDone(pieceKey);
            RealitiesOfDreams.LOGGER.debug("Built {} in {} ms", piece.id(), System.currentTimeMillis() - t);
        }
        data.markDone(key);
        RealitiesOfDreams.LOGGER.info("Built site {} ({} pieces) in {} ms", scene.site(), layout.pieces().size(), System.currentTimeMillis() - start);
    }

    /**
     * A piece can be authored in-game and saved with a structure block as
     * data/realitiesofdreams/structure/&lt;site&gt;/&lt;piece&gt;.nbt; it then replaces the built-in builder,
     * placed with its corner at the piece box's (x0, -1, z0).
     */
    private static boolean placeTemplate(ServerLevel level, BlockPos anchor, String site, SiteLayout.Piece piece) {
        if (piece.box() == null) return false;
        ResourceLocation id = RealitiesOfDreams.id(site + "/" + piece.id());
        Optional<StructureTemplate> template = level.getStructureManager().get(id);
        if (template.isEmpty()) return false;
        BlockPos at = anchor.offset(piece.box()[0], -1, piece.box()[1]);
        template.get().placeInWorld(level, at, at, new StructurePlaceSettings().setRotation(Rotation.NONE), level.getRandom(), 2);
        RealitiesOfDreams.LOGGER.info("Placed structure template {} for {}", id, piece.id());
        return true;
    }

    /** True if the block at this position is part of the story the site protects (doors, the well...). */
    public static boolean isProtected(ServerLevel level, SceneDef scene, BlockPos pos) {
        SiteLayout layout = layout(scene.site());
        if (layout == null || !SceneWorldData.get(level).isDone("built:site:" + scene.site())) return false;
        BlockPos rel = pos.subtract(ModDimensions.anchor(scene));
        for (SiteLayout.Region r : layout.protectedRegions()) if (r.contains(rel.getX(), rel.getY(), rel.getZ())) return true;
        return false;
    }

    /** Permanent changes to the world caused by story events, each applied once per world. */
    public static void applyChange(ServerLevel level, SceneDef scene, String change) {
        SceneWorldData data = SceneWorldData.get(level);
        if (data.isDone("change:" + change)) {
            refreshDetails(level);
            return;
        }
        BlockPos a = ModDimensions.anchor(scene);
        switch (change) {
            case "mc001_s002_aftermath" -> aftermath(level, scene, a);
            case "mc001_s002_ruin_light_on" -> ruinLight(level, a, true);
            case "mc001_s002_ruin_light_off" -> ruinLight(level, a, false);
            case "mc001_s003_trail" -> {
                net.realitiesofdreams.world.site.NorthernTrail.build(level, a);
                placeClues(level, scene);
                SceneDef.ClueDef pack = scene.clues().get("scout_pack");
                if (pack != null) net.realitiesofdreams.world.site.NorthernTrail.fillPack(level, a, ModDimensions.absoluteBlock(scene, pack.pos()));
            }
            case "mc001_s003_pulse_on" -> net.realitiesofdreams.world.site.NorthernTrail.pulse(level, a, true);
            case "mc001_s003_pulse_off" -> net.realitiesofdreams.world.site.NorthernTrail.pulse(level, a, false);
            case "mc001_s004_supplies" -> net.realitiesofdreams.world.site.OrinWorkshop.supplies(level, a);
            case "mc001_s004_sketch" -> net.realitiesofdreams.world.site.OrinWorkshop.sketch(level, a);
            case "mc001_s004_compass" -> net.realitiesofdreams.world.site.OrinWorkshop.compass(level, a);
            case "mc001_s005_worksite" -> net.realitiesofdreams.world.site.NorthRoad.worksite(level, a);
            case "mc001_s005_rockfall" -> net.realitiesofdreams.world.site.NorthRoad.rockfall(level, a);
            case "mc001_s005_monument" -> {
                // Once the monument has been built for Scene 006, the distant silhouette must not cover it.
                if (!data.isDone("built:site:northern_monument")) net.realitiesofdreams.world.site.NorthRoad.monumentSilhouette(level, a);
            }
            case "mc001_s006_glow_on" -> net.realitiesofdreams.world.site.NorthernMonument.glow(level, a, true);
            case "mc001_s006_glow_off" -> net.realitiesofdreams.world.site.NorthernMonument.glow(level, a, false);
            case "mc001_s008_collapse" -> net.realitiesofdreams.world.site.ValleyCrossing.collapse(level, a);
            case "mc001_s008_crate_saved" -> net.realitiesofdreams.world.site.ValleyCrossing.crateSaved(level, a);
            case "mc001_s009_lantern" -> net.realitiesofdreams.world.site.ValleyCrossing.lanternRolls(level, a);
            case "mc001_s007_survey" -> {
                net.realitiesofdreams.world.site.NorthernMonument.survey(level, a);
                placeClues(level, scene);
            }
            default -> RealitiesOfDreams.LOGGER.warn("Unknown world change {}", change);
        }
        data.markDone("change:" + change);
    }

    private static final int FLAGS = net.minecraft.world.level.block.Block.UPDATE_CLIENTS;

    /**
     * Scene 002: what the pre-dawn tremor left behind. A garden wall by the square pushed up and
     * cracked from below, the surveyor's table with the compass, and the night watch's report at
     * the watchtower. Each clue's block is placed where the scene says it is.
     */
    private static void aftermath(ServerLevel level, SceneDef scene, BlockPos a) {
        net.minecraft.world.level.block.state.BlockState bricks = net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState();
        net.minecraft.world.level.block.state.BlockState cracked = net.minecraft.world.level.block.Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        // The damaged wall, x 4..7 at z -7: heaved stones, a split course and rubble at its foot.
        for (int x = 4; x <= 7; x++) {
            level.setBlock(a.offset(x, -1, -7), net.minecraft.world.level.block.Blocks.COARSE_DIRT.defaultBlockState(), FLAGS);
            level.setBlock(a.offset(x, 0, -7), x == 5 ? cracked : bricks, FLAGS);
            if (x != 6) level.setBlock(a.offset(x, 1, -7), x == 4 ? cracked : net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), FLAGS);
        }
        level.setBlock(a.offset(7, 1, -7), net.minecraft.world.level.block.Blocks.STONE_BRICK_SLAB.defaultBlockState(), FLAGS);
        for (int[] r : new int[][] {{4, -6}, {6, -6}, {5, -8}, {7, -6}}) {
            if (level.getBlockState(a.offset(r[0], 0, r[1])).canBeReplaced())
                level.setBlock(a.offset(r[0], 0, r[1]), (r[0] % 2 == 0 ? net.minecraft.world.level.block.Blocks.COBBLESTONE_SLAB
                        : net.minecraft.world.level.block.Blocks.STONE_BUTTON).defaultBlockState(), FLAGS);
            level.setBlock(a.offset(r[0], -1, r[1]), net.minecraft.world.level.block.Blocks.GRAVEL.defaultBlockState(), FLAGS);
        }
        placeClues(level, scene);
        reportBook(level, scene);
    }

    /** Places every clue's block where the scene says it is. */
    private static void placeClues(ServerLevel level, SceneDef scene) {
        for (SceneDef.ClueDef clue : scene.clues().values()) {
            BlockPos at = ModDimensions.absoluteBlock(scene, clue.pos());
            var block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.parse(clue.block()));
            var state = block.defaultBlockState();
            if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) {
                state = state.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING, net.minecraft.core.Direction.SOUTH);
            }
            level.setBlock(at, state, FLAGS);
        }
    }

    /**
     * Details added to the world after a save was first made: run whenever a story world is entered.
     * Safe to repeat. Puts the night watch's report on its lectern (added after Scene 002 shipped).
     */
    public static void refreshDetails(ServerLevel level) {
        SceneDef s2 = net.realitiesofdreams.Story.content().scene("mc001_s002");
        if (s2 != null && SceneWorldData.get(level).isDone("change:mc001_s002_aftermath")) reportBook(level, s2);
    }

    /** The night watch's written report, readable on the lectern by the watchtower. */
    private static void reportBook(ServerLevel level, SceneDef scene) {
        SceneDef.ClueDef clue = scene.clues().get("tower_report");
        if (clue == null) return;
        BlockPos at = ModDimensions.absoluteBlock(scene, clue.pos());
        var state = level.getBlockState(at);
        if (!(state.getBlock() instanceof net.minecraft.world.level.block.LecternBlock)
                || state.getValue(net.minecraft.world.level.block.LecternBlock.HAS_BOOK)) return;
        java.util.List<net.minecraft.server.network.Filterable<net.minecraft.network.chat.Component>> pages = new java.util.ArrayList<>();
        for (String page : new String[] {
                "NIGHT WATCH REPORT\nNorth Watchtower\n\nFirst tremor two hours before dawn. Short, but the tower swayed.",
                "Second tremor soon after, stronger. Lantern on the square knocked loose.\n\nThen a light beneath the stones of the northern ridge, under the old ruin.",
                "Pale, like moonlight on water, but there was no moon on the ridge. Gone within moments.\n\nNo sound with it.",
                "Rockfall heard on the northern path near dawn. Captain Bram informed.\n\nSigned,\nthe night watch"}) {
            pages.add(net.minecraft.server.network.Filterable.passThrough(net.minecraft.network.chat.Component.literal(page)));
        }
        net.minecraft.world.item.ItemStack book = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WRITTEN_BOOK);
        book.set(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT, new net.minecraft.world.item.component.WrittenBookContent(
                net.minecraft.server.network.Filterable.passThrough("Night Watch Report"), "Firstbrook Watch", 0, pages, true));
        net.minecraft.world.level.block.LecternBlock.tryPlaceBook(null, level, at, state, book);
    }

    /** Scene 002's light beneath the ruin on the ridge: on for a moment during the compass cinematic, then gone. */
    private static void ruinLight(ServerLevel level, BlockPos a, boolean on) {
        int top = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, a.getX() + 10, a.getZ() - 111);
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = new BlockPos(a.getX() + 10 + dx, top - 1, a.getZ() - 111 + dz);
                level.setBlock(p, on ? net.minecraft.world.level.block.Blocks.SEA_LANTERN.defaultBlockState()
                        : (dx + dz) % 2 == 0 ? net.minecraft.world.level.block.Blocks.CRACKED_STONE_BRICKS.defaultBlockState()
                        : net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE.defaultBlockState(), FLAGS);
            }
        if (on) level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD, a.getX() + 10.5, top + 0.5, a.getZ() - 110.5, 40, 1.5, 1, 1.5, 0.05);
    }

    public static int hillHeight(int x, int z) {
        return Mc001Terrain.hillHeight(x, z);
    }
}
