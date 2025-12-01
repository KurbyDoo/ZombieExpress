/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Test
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package application.game_use_cases.render_radius;

import domain.GamePosition;
import domain.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RenderRadiusManagerInteractorTest {
    World world;
    RenderRadiusManagerInteractor interactor;
    @BeforeEach
    void setUp() {
        world = new World();
        interactor = new RenderRadiusManagerInteractor(world);
    }

    @Test
    @DisplayName("Generate chunks for empty world")
    void emptyLoadChunks() {
        GamePosition playerPos = new GamePosition(0,0, 0);
        RenderRadiusOutputData output =  interactor.execute(
            new RenderRadiusManagerInputData(playerPos, 2)
        );
        assertFalse(output.getChunksToGenerate().isEmpty(), "Generated chunks should be non-empty");
        assertTrue(output.getChunksToGenerate().containsAll(output.getChunksToLoad()),
            "Chunks to load should a subset of generated chunks");
        assertTrue(output.getChunksToUpdate().isEmpty(), "Chunks to update should be empty");
    }

    @Test
    @DisplayName("Crossing chunk boundary has no load/unload")
    void chunksToLoadShouldBeNonEmpty() {
        GamePosition playerPos = new GamePosition(0,0, 0);
        RenderRadiusOutputData output =  interactor.execute(
            new RenderRadiusManagerInputData(playerPos, 2)
        );

        // Update again without moving
        RenderRadiusOutputData output2 =  interactor.execute(
            new RenderRadiusManagerInputData(playerPos, 2)
        );

        assertTrue(output2.getChunksToLoad().isEmpty(), "Chunks to load should be empty");
        assertTrue(output2.getChunksToUnload().isEmpty(), "Chunks to unload should be empty");
        assertTrue(output2.getChunksToGenerate().isEmpty(), "Chunks to generate should be empty");
        assertFalse(output2.getChunksToUpdate().isEmpty(), "Chunks to update should be non-empty");
        assertTrue(output.getChunksToLoad().containsAll(output2.getChunksToUpdate()) &&
                output2.getChunksToUpdate().containsAll(output.getChunksToLoad()),
            "Chunks to update should be the same as previous");
    }

    @Test
    @DisplayName("Moving away unloads chunks")
    void chunksToUnloadIsNonEmpty() {
        GamePosition playerPos = new GamePosition(0,0, 0);
        RenderRadiusOutputData output =  interactor.execute(
            new RenderRadiusManagerInputData(playerPos, 2)
        );

        // Move to a new chunk
        playerPos = new GamePosition(16,0, 0);
        RenderRadiusOutputData output2 =  interactor.execute(
            new RenderRadiusManagerInputData(playerPos, 2)
        );
        assertFalse(output2.getChunksToUnload().isEmpty(), "Chunks to unload should be non-empty");
    }
}
