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
     * 001 to 005) share one build. Each piece is recorded as it finishes, so a build interrupted
     * by a crash resumes where it stopped.
     */
    public static void ensureBuilt(ServerLevel level, SceneDef scene) {
        SceneWorldData data = SceneWorldData.get(level);
        String key = "built:site:" + scene.site();
        if (data.isDone(key)) return;
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

    /** Permanent changes to the world caused by story events. None are defined for the current scenes. */
    public static void applyChange(ServerLevel level, SceneDef scene, String change) {
        SceneWorldData data = SceneWorldData.get(level);
        if (data.isDone("change:" + change)) return;
        RealitiesOfDreams.LOGGER.warn("Unknown world change {}", change);
        data.markDone("change:" + change);
    }

    public static int hillHeight(int x, int z) {
        return Mc001Terrain.hillHeight(x, z);
    }
}
