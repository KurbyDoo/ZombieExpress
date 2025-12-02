package application.game_features.update_world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import application.game_features.generate_chunk.GenerateChunkInputBoundary;
import application.game_features.generate_chunk.GenerateChunkInputData;
import application.game_features.generate_chunk.GenerateChunkOutputData;
import application.game_features.populate_chunk.PopulateChunkInputBoundary;
import application.game_features.populate_chunk.PopulateChunkInputData;
import application.game_features.render_radius.RenderRadiusManagerInputBoundary;
import application.game_features.render_radius.RenderRadiusManagerInputData;
import application.game_features.render_radius.RenderRadiusOutputData;
import domain.player.Player;
import domain.world.Chunk;
import domain.world.GamePosition;
import domain.world.World;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Unit test for UpdateWorldInteractor.
 */
class UpdateWorldInteractorTest {
    private RenderRadiusManagerInputBoundary renderRadiusManager;
    private GenerateChunkInputBoundary chunkGenerator;
    private PopulateChunkInputBoundary chunkPopulator;
    private World world;
    private Player player;

    private UpdateWorldInteractor interactor;

    @BeforeEach
    void setUp() {
        renderRadiusManager = mock(RenderRadiusManagerInputBoundary.class);
        chunkGenerator = mock(GenerateChunkInputBoundary.class);
        chunkPopulator = mock(PopulateChunkInputBoundary.class);
        world = mock(World.class);
        player = mock(Player.class);

        interactor = new UpdateWorldInteractor(
            renderRadiusManager,
            chunkGenerator,
            chunkPopulator,
            world,
            player
        );
    }

    @Nested
    @DisplayName("Main Execution Flow")
    class ExecutionTests {

        @Test
        @DisplayName("Execute should handle generation, loading, and unloading correctly")
        void shouldHandleFullLifecycle() {
            int renderRadius = 5;
            GamePosition playerPos = mock(GamePosition.class);
            UpdateWorldInputData inputData = new UpdateWorldInputData(renderRadius);

            when(player.getPosition()).thenReturn(playerPos);

            GamePosition posToGenerate = mock(GamePosition.class);
            GamePosition posToLoad = mock(GamePosition.class);
            GamePosition posToUnload = mock(GamePosition.class);
            GamePosition posToUpdate = mock(GamePosition.class);

            Set<GamePosition> generateList = Collections.singleton(posToGenerate);
            Set<GamePosition> loadList = Collections.singleton(posToLoad);
            Set<GamePosition> unloadList = Collections.singleton(posToUnload);
            Set<GamePosition> updateList = Collections.singleton(posToUpdate);

            RenderRadiusOutputData mockRadiusOutput = mock(RenderRadiusOutputData.class);
            when(mockRadiusOutput.getChunksToGenerate()).thenReturn(generateList);
            when(mockRadiusOutput.getChunksToLoad()).thenReturn(loadList);
            when(mockRadiusOutput.getChunksToUnload()).thenReturn(unloadList);
            when(mockRadiusOutput.getChunksToUpdate()).thenReturn(updateList);

            when(renderRadiusManager.execute(any(RenderRadiusManagerInputData.class)))
                .thenReturn(mockRadiusOutput);

            List<Integer> activeEntities = Arrays.asList(1, 2, 3);
            when(world.getEntitiesInChunks(updateList)).thenReturn(activeEntities);

            int worldDepth = 10;
            when(world.getWorldDepthChunks()).thenReturn(worldDepth);

            Chunk generatedChunk = mock(Chunk.class);
            GenerateChunkOutputData genOutput = new GenerateChunkOutputData(generatedChunk);
            when(chunkGenerator.execute(any(GenerateChunkInputData.class))).thenReturn(genOutput);

            Chunk chunkToLoad = mock(Chunk.class);
            Chunk chunkToUnload = mock(Chunk.class);
            when(world.getChunk(posToLoad)).thenReturn(chunkToLoad);
            when(world.getChunk(posToUnload)).thenReturn(chunkToUnload);

            UpdateWorldOutputData result = interactor.execute(inputData);

            verify(player).getPosition();
            verify(renderRadiusManager).execute(any(RenderRadiusManagerInputData.class));
            assertEquals(activeEntities, result.getActiveEntities());

            // Should call generator
            verify(chunkGenerator).execute(any(GenerateChunkInputData.class));
            // Should add chunk to world
            verify(world).addChunk(posToGenerate, generatedChunk);
            // Should populate chunk
            verify(chunkPopulator).execute(any(PopulateChunkInputData.class));

            verify(world).getChunk(posToLoad);
            assertNotNull(result.getChunksToLoad());
            assertEquals(chunkToLoad, result.getChunksToLoad().get(posToLoad));

            verify(world).getChunk(posToUnload);
            assertNotNull(result.getChunksToUnload());
            assertEquals(chunkToUnload, result.getChunksToUnload().get(posToUnload));
        }

        @Test
        @DisplayName("Execute should succeed even when no chunks need action")
        void shouldHandleEmptyLists() {
            GamePosition playerPos = mock(GamePosition.class);
            when(player.getPosition()).thenReturn(playerPos);

            // Return empty lists for all radius actions
            RenderRadiusOutputData mockRadiusOutput = mock(RenderRadiusOutputData.class);
            when(mockRadiusOutput.getChunksToGenerate()).thenReturn(Collections.emptySet());
            when(mockRadiusOutput.getChunksToLoad()).thenReturn(Collections.emptySet());
            when(mockRadiusOutput.getChunksToUnload()).thenReturn(Collections.emptySet());
            when(mockRadiusOutput.getChunksToUpdate()).thenReturn(Collections.emptySet());

            when(renderRadiusManager.execute(any(RenderRadiusManagerInputData.class)))
                .thenReturn(mockRadiusOutput);

            when(world.getEntitiesInChunks(any())).thenReturn(Collections.emptyList());

            UpdateWorldOutputData result = interactor.execute(new UpdateWorldInputData(5));

            assertEquals(0, result.getChunksToLoad().size());
            assertEquals(0, result.getChunksToUnload().size());
            assertEquals(0, result.getActiveEntities().size());

            // Verify generator was never called because list was empty
            verify(chunkGenerator, org.mockito.Mockito.never()).execute(any());
        }
    }
}
