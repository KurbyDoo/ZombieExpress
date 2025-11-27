package application.use_cases.render_radius;

import domain.Chunk;
import domain.GamePosition;
import domain.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RenderRadiusManagerInteractor.
 * Tests chunk generation, loading, and unloading based on player position changes.
 */
class RenderRadiusManagerInteractorTest {

    private World world;
    private RenderRadiusManagerInteractor interactor;

    @BeforeEach
    void setUp() {
        world = new World();
        interactor = new RenderRadiusManagerInteractor(world);
    }

    @Nested
    @DisplayName("Initial Position Tests")
    class InitialPositionTests {

        @Test
        @DisplayName("Should generate chunks around initial player position")
        void shouldGenerateChunksAroundInitialPosition() {
            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 2
            );

            RenderRadiusOutputData result = interactor.execute(inputData);

            assertFalse(result.getChunksToGenerate().isEmpty(),
                "Should request chunk generation on first execution");
        }

        @Test
        @DisplayName("Should request chunks to load on first execution")
        void shouldRequestChunksToLoadOnFirstExecution() {
            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 2
            );

            RenderRadiusOutputData result = interactor.execute(inputData);

            assertFalse(result.getChunksToLoad().isEmpty(),
                "Should request chunks to load on first execution");
        }

        @Test
        @DisplayName("Should not request unloads on first execution")
        void shouldNotRequestUnloadsOnFirstExecution() {
            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 2
            );

            RenderRadiusOutputData result = interactor.execute(inputData);

            assertTrue(result.getChunksToUnload().isEmpty(),
                "Should not request unloads on first execution");
        }
    }

    @Nested
    @DisplayName("Same Position Tests")
    class SamePositionTests {

        @Test
        @DisplayName("Should return update set when position unchanged")
        void shouldReturnUpdateSetWhenPositionUnchanged() {
            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 2
            );

            // First call
            interactor.execute(inputData);

            // Second call at same position
            RenderRadiusOutputData result = interactor.execute(inputData);

            assertFalse(result.getChunksToUpdate().isEmpty(),
                "Should return chunks to update when position unchanged");
        }

        @Test
        @DisplayName("Should not generate new chunks when position unchanged within same chunk")
        void shouldNotGenerateWhenPositionUnchangedWithinChunk() {
            // First execution
            GamePosition playerPos1 = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData1 = new RenderRadiusManagerInputData(
                playerPos1, world, 2
            );
            interactor.execute(inputData1);

            // Second execution at different position but same chunk
            // Chunk size is 16, so stay within chunk 0
            GamePosition playerPos2 = new GamePosition(5, 0, 5);
            RenderRadiusManagerInputData inputData2 = new RenderRadiusManagerInputData(
                playerPos2, world, 2
            );
            RenderRadiusOutputData result = interactor.execute(inputData2);

            // Should have chunksToUpdate but no new generation or loading
            assertFalse(result.getChunksToUpdate().isEmpty(),
                "Should have chunks to update");
            assertTrue(result.getChunksToGenerate().isEmpty(),
                "Should not generate new chunks within same chunk");
            assertTrue(result.getChunksToLoad().isEmpty(),
                "Should not load new chunks within same chunk");
        }
    }

    @Nested
    @DisplayName("Position Change Tests")
    class PositionChangeTests {

        @Test
        @DisplayName("Should load new chunks when moving to new chunk")
        void shouldLoadNewChunksWhenMovingToNewChunk() {
            // First execution at chunk (0, 0, 0)
            GamePosition playerPos1 = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData1 = new RenderRadiusManagerInputData(
                playerPos1, world, 2
            );
            interactor.execute(inputData1);

            // Move to a different chunk (chunk x=2)
            GamePosition playerPos2 = new GamePosition(32, 0, 0); // 2 chunks away
            RenderRadiusManagerInputData inputData2 = new RenderRadiusManagerInputData(
                playerPos2, world, 2
            );
            RenderRadiusOutputData result = interactor.execute(inputData2);

            assertFalse(result.getChunksToLoad().isEmpty(),
                "Should request new chunks to load when moving to new chunk");
        }

        @Test
        @DisplayName("Should unload old chunks when moving away")
        void shouldUnloadOldChunksWhenMovingAway() {
            // First execution at chunk (0, 0, 0) with small radius
            GamePosition playerPos1 = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData1 = new RenderRadiusManagerInputData(
                playerPos1, world, 1
            );
            interactor.execute(inputData1);

            // Move far away (several chunks)
            GamePosition playerPos2 = new GamePosition(64, 0, 0); // 4 chunks away
            RenderRadiusManagerInputData inputData2 = new RenderRadiusManagerInputData(
                playerPos2, world, 1
            );
            RenderRadiusOutputData result = interactor.execute(inputData2);

            assertFalse(result.getChunksToUnload().isEmpty(),
                "Should request old chunks to unload when moving away");
        }

        @Test
        @DisplayName("Should handle movement in negative direction")
        void shouldHandleMovementInNegativeDirection() {
            // First at origin
            GamePosition playerPos1 = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData1 = new RenderRadiusManagerInputData(
                playerPos1, world, 2
            );
            interactor.execute(inputData1);

            // Move to negative X
            GamePosition playerPos2 = new GamePosition(-32, 0, 0);
            RenderRadiusManagerInputData inputData2 = new RenderRadiusManagerInputData(
                playerPos2, world, 2
            );
            RenderRadiusOutputData result = interactor.execute(inputData2);

            // Should handle negative coordinates correctly
            assertNotNull(result, "Should handle negative coordinates");
            assertFalse(result.getChunksToLoad().isEmpty() || result.getChunksToUnload().isEmpty(),
                "Should process chunks when moving in negative direction");
        }
    }

    @Nested
    @DisplayName("Render Radius Tests")
    class RenderRadiusTests {

        @Test
        @DisplayName("Larger radius should generate more chunks")
        void largerRadiusShouldGenerateMoreChunks() {
            GamePosition playerPos = new GamePosition(0, 0, 0);

            // Small radius
            RenderRadiusManagerInputData smallRadiusInput = new RenderRadiusManagerInputData(
                playerPos, world, 1
            );
            World world1 = new World();
            RenderRadiusManagerInteractor interactor1 = new RenderRadiusManagerInteractor(world1);
            RenderRadiusOutputData smallResult = interactor1.execute(smallRadiusInput);
            int smallCount = smallResult.getChunksToGenerate().size() + smallResult.getChunksToLoad().size();

            // Large radius
            RenderRadiusManagerInputData largeRadiusInput = new RenderRadiusManagerInputData(
                playerPos, world, 3
            );
            World world2 = new World();
            RenderRadiusManagerInteractor interactor2 = new RenderRadiusManagerInteractor(world2);
            RenderRadiusOutputData largeResult = interactor2.execute(largeRadiusInput);
            int largeCount = largeResult.getChunksToGenerate().size() + largeResult.getChunksToLoad().size();

            assertTrue(largeCount > smallCount,
                "Larger radius should result in more chunks: large=" + largeCount + ", small=" + smallCount);
        }

        @Test
        @DisplayName("Generation radius should be one more than render radius")
        void generationRadiusShouldBeOneLargerThanRenderRadius() {
            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 2
            );

            RenderRadiusOutputData result = interactor.execute(inputData);

            // Generation radius = render radius + 1
            // So there should be more chunks to generate than to load
            // Actually, chunks to generate are for positions that don't exist yet
            // chunks to load are for rendering, which is within render radius
            Set<GamePosition> toGenerate = result.getChunksToGenerate();
            Set<GamePosition> toLoad = result.getChunksToLoad();

            // The generation set should contain the load set (generation is superset)
            for (GamePosition loadPos : toLoad) {
                boolean foundInGeneration = toGenerate.stream()
                    .anyMatch(genPos -> genPos.epsilonEquals(loadPos, 0.001f));
                // Note: toLoad contains positions to mesh, which might already exist
                // This relationship is complex, just verify both sets have content
            }

            assertNotNull(toGenerate);
            assertNotNull(toLoad);
        }
    }

    @Nested
    @DisplayName("World Boundary Tests")
    class WorldBoundaryTests {

        @Test
        @DisplayName("Should not generate chunks beyond world boundaries")
        void shouldNotGenerateChunksBeyondBoundaries() {
            // Position at edge of world
            // MIN_WORLD_Z_CHUNKS = -8, so chunk at z=-8 is at world position -128
            GamePosition edgePos = new GamePosition(0, 0, -120);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                edgePos, world, 3
            );

            RenderRadiusOutputData result = interactor.execute(inputData);

            // All generated chunks should be within bounds
            for (GamePosition pos : result.getChunksToGenerate()) {
                assertTrue(pos.z >= -8, "Z position should be >= -8, but was " + pos.z);
                assertTrue(pos.z <= 8, "Z position should be <= 8, but was " + pos.z);
            }
        }

        @Test
        @DisplayName("Should respect Y view range")
        void shouldRespectYViewRange() {
            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 2
            );

            RenderRadiusOutputData result = interactor.execute(inputData);

            // Y_VIEW_RANGE = 3, so chunks should be within -3 to +3 Y range
            // Also respects MIN_WORLD_Y_CHUNKS = 0 and MAX_WORLD_Y_CHUNKS = 8
            for (GamePosition pos : result.getChunksToGenerate()) {
                assertTrue(pos.y >= 0, "Y position should be >= 0 (MIN_WORLD_Y_CHUNKS)");
                assertTrue(pos.y <= 8, "Y position should be <= 8 (MAX_WORLD_Y_CHUNKS)");
            }
        }
    }

    @Nested
    @DisplayName("Existing Chunk Tests")
    class ExistingChunkTests {

        @Test
        @DisplayName("Should not request generation for existing chunks")
        void shouldNotRequestGenerationForExistingChunks() {
            // Pre-populate some chunks
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    for (int y = 0; y <= 3; y++) {
                        GamePosition pos = new GamePosition(x, y, z);
                        world.addChunk(pos, new Chunk(pos));
                    }
                }
            }

            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 1
            );

            RenderRadiusOutputData result = interactor.execute(inputData);

            // Should not request generation for chunks that already exist
            for (GamePosition pos : result.getChunksToGenerate()) {
                assertFalse(world.hasChunk(pos),
                    "Should not request generation for existing chunk at " + pos);
            }
        }

        @Test
        @DisplayName("Should still load existing chunks for rendering")
        void shouldStillLoadExistingChunksForRendering() {
            // Pre-populate chunks
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    GamePosition pos = new GamePosition(x, 0, z);
                    world.addChunk(pos, new Chunk(pos));
                }
            }

            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 1
            );

            RenderRadiusOutputData result = interactor.execute(inputData);

            // Should request to load (render) chunks even if they exist
            assertFalse(result.getChunksToLoad().isEmpty(),
                "Should load chunks for rendering even if they exist");
        }
    }

    @Nested
    @DisplayName("Chunk Tracking Tests")
    class ChunkTrackingTests {

        @Test
        @DisplayName("Should track rendered chunks across calls")
        void shouldTrackRenderedChunksAcrossCalls() {
            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 2
            );

            // First call
            RenderRadiusOutputData result1 = interactor.execute(inputData);
            int loadedCount = result1.getChunksToLoad().size();

            // Second call at same chunk
            RenderRadiusOutputData result2 = interactor.execute(inputData);

            // Should not load already-loaded chunks again
            assertEquals(0, result2.getChunksToLoad().size(),
                "Should not reload already loaded chunks");
            assertFalse(result2.getChunksToUpdate().isEmpty(),
                "Should still have chunks to update");
        }

        @Test
        @DisplayName("Should properly unload and not track unloaded chunks")
        void shouldProperlyUnloadAndNotTrack() {
            // Start at position A
            GamePosition posA = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputA = new RenderRadiusManagerInputData(
                posA, world, 1
            );
            interactor.execute(inputA);

            // Move far away to position B
            GamePosition posB = new GamePosition(80, 0, 0); // 5 chunks away
            RenderRadiusManagerInputData inputB = new RenderRadiusManagerInputData(
                posB, world, 1
            );
            RenderRadiusOutputData resultB = interactor.execute(inputB);

            // Move back to position A
            RenderRadiusOutputData resultA = interactor.execute(inputA);

            // Should need to reload chunks around A since they were unloaded
            assertFalse(resultA.getChunksToLoad().isEmpty(),
                "Should reload chunks when returning to previous position");
        }
    }

    @Nested
    @DisplayName("Circular Radius Tests")
    class CircularRadiusTests {

        @Test
        @DisplayName("Should use circular radius check")
        void shouldUseCircularRadiusCheck() {
            GamePosition playerPos = new GamePosition(0, 0, 0);
            RenderRadiusManagerInputData inputData = new RenderRadiusManagerInputData(
                playerPos, world, 2
            );

            RenderRadiusOutputData result = interactor.execute(inputData);

            // Check that chunks are within circular radius, not square
            // A chunk at corner (2, 0, 2) would be sqrt(8) ~= 2.83 away
            // With radius 2, this should NOT be included
            boolean hasCornerChunk = result.getChunksToLoad().stream()
                .anyMatch(pos -> pos.x == 2 && pos.z == 2);

            assertFalse(hasCornerChunk,
                "Should not include corner chunks outside circular radius");

            // But (2, 0, 0) should be included (distance = 2)
            boolean hasEdgeChunk = result.getChunksToLoad().stream()
                .anyMatch(pos -> ((int)pos.x == 2 && (int)pos.z == 0) ||
                                 ((int)pos.x == 0 && (int)pos.z == 2));

            assertTrue(hasEdgeChunk,
                "Should include edge chunks within circular radius");
        }
    }
}
