package net.realitiesofdreams.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.realitiesofdreams.RealitiesOfDreams;
import net.realitiesofdreams.story.scene.SceneDef;

/** Universes the player can stand in. MC-001 is defined by data/realitiesofdreams/dimension/mc_001.json. */
public final class ModDimensions {
    public static final ResourceKey<Level> MC_001 = ResourceKey.create(Registries.DIMENSION, RealitiesOfDreams.id("mc_001"));

    private ModDimensions() {}

    public static ResourceKey<Level> forScene(SceneDef scene) {
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(scene.dimension()));
    }

    @javax.annotation.Nullable private static ServerLevel testLevel;

    public static ServerLevel level(MinecraftServer server, SceneDef scene) {
        if (testLevel != null) return testLevel;
        return server.getLevel(forScene(scene));
    }

    /**
     * Game tests only: the test server has no custom dimensions, so scenes are staged in the
     * test level instead. Pass null to restore normal behaviour.
     */
    public static void useLevelForTesting(@javax.annotation.Nullable ServerLevel level) {
        testLevel = level;
    }

    public static BlockPos anchor(SceneDef scene) {
        return new BlockPos(scene.anchor()[0], scene.anchor()[1], scene.anchor()[2]);
    }

    public static double[] absolute(SceneDef scene, SceneDef.Location loc) {
        return new double[] {scene.anchor()[0] + loc.x() + 0.5, scene.anchor()[1] + loc.y(), scene.anchor()[2] + loc.z() + 0.5};
    }

    public static BlockPos absoluteBlock(SceneDef scene, double[] rel) {
        return anchor(scene).offset((int) Math.floor(rel[0]), (int) Math.floor(rel[1]), (int) Math.floor(rel[2]));
    }
}
