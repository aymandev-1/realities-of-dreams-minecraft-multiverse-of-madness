package net.realitiesofdreams.story;

import net.realitiesofdreams.story.world.Mc001Terrain;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** The authored MC-001 landscape that the chunk generator and the site builders share. */
class TerrainTest {
    @Test
    void firstbrookStandsOnFlatGround() {
        for (int x = -44; x <= 44; x++)
            for (int z = -44; z <= 44; z++) {
                if (Mc001Terrain.water(x, z)) continue;
                assertEquals(0, Mc001Terrain.height(x, z), "village ground at " + x + "," + z);
            }
    }

    @Test
    void theRiverRunsFromTheWaterfallToTheLake() {
        for (int z = Mc001Terrain.WATERFALL_Z + 1; z < Mc001Terrain.LAKE_Z; z++) {
            assertTrue(Mc001Terrain.water(Mc001Terrain.RIVER_X, z), "river missing at z=" + z);
            assertTrue(Mc001Terrain.height(Mc001Terrain.RIVER_X, z) < Mc001Terrain.WATER_TOP, "river bed must be below the water");
        }
        assertTrue(Mc001Terrain.water(Mc001Terrain.LAKE_X, Mc001Terrain.LAKE_Z));
        assertTrue(Mc001Terrain.height(Mc001Terrain.RIVER_X, Mc001Terrain.WATERFALL_Z - 2) >= 2, "no cliff for the waterfall to fall from");
    }

    @Test
    void theRidgeRisesNorthOfTheVillage() {
        int ridge = Mc001Terrain.hillHeight(10, Mc001Terrain.RIDGE_Z);
        assertTrue(ridge > 15 && ridge < 40, "ridge height " + ridge);
        assertEquals(ridge, Mc001Terrain.height(10, Mc001Terrain.RIDGE_Z), "the cinematics' ridge and the generator agree");
        assertTrue(Mc001Terrain.height(0, -200) > ridge, "mountains beyond the ridge");
    }

    @Test
    void banksSlopeInsteadOfCliffs() {
        // Outside the village no step between neighbouring columns near the river or lake is over 2 blocks.
        for (int z = 50; z < 140; z++)
            for (int x = 10; x < 60; x++) {
                int a = Mc001Terrain.height(x, z), b = Mc001Terrain.height(x + 1, z);
                if (Mc001Terrain.water(x, z) || Mc001Terrain.water(x + 1, z)) continue;
                assertTrue(Math.abs(a - b) <= 2, "cliff at " + x + "," + z + ": " + a + " vs " + b);
            }
    }

    @Test
    void authoredTerrainBlendsIntoNaturalTerrain() {
        assertEquals(1.0, Mc001Terrain.weight(0, 0));
        assertEquals(1.0, Mc001Terrain.weight(0, (int) Mc001Terrain.AUTHORED_RADIUS));
        assertEquals(0.0, Mc001Terrain.weight((int) Mc001Terrain.NATURAL_RADIUS + 1, 0));
        double mid = Mc001Terrain.weight((int) ((Mc001Terrain.AUTHORED_RADIUS + Mc001Terrain.NATURAL_RADIUS) / 2), 0);
        assertTrue(mid > 0.2 && mid < 0.8);
        assertTrue(Mc001Terrain.protectedCore(0, 0) && Mc001Terrain.protectedCore(40, -40) && !Mc001Terrain.protectedCore(0, -150));
    }
}
