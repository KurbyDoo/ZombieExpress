package application.game_use_cases.generate_chunk;

import application.game_use_cases.ports.BlockRepository;
import data_access.InMemoryBlockRepository;
import domain.Chunk;
import domain.GamePosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenerateChunkInteractorTest {
    private BlockRepository blockRepository;
    private GenerateChunkInteractor interactor;

    @BeforeEach
    void setUp() {
        blockRepository = new InMemoryBlockRepository();
        interactor = new GenerateChunkInteractor(blockRepository);
    }

    @Test
    @DisplayName("Chunk successfully generates")
    void shouldSuccessfullyGenerate() {
        GamePosition pos = new GamePosition(0, 0, 0);
        GenerateChunkOutputData outputData = interactor.execute(
            new GenerateChunkInputData(pos, 100)
        );
        interactor.execute(new GenerateChunkInputData(new GamePosition(0, 1, 0), 100));
        Chunk chunk = outputData.getChunk();
        assertNotNull(chunk, "Chunk should not be null");
        assertEquals(0, chunk.getMaxBlockHeight(), "Should be flat at the origin");
        assertEquals(0, chunk.getMinBlockHeight(), "Should be flat at the origin");
    }

    @Test
    @DisplayName("Chunk successfully generates")
    void shouldGenerateValley() {
        GenerateChunkOutputData backOutput = interactor.execute(
            new GenerateChunkInputData(new GamePosition(6, 0, 0), 32)
        );
        Chunk backChunk = backOutput.getChunk();
        GenerateChunkOutputData frontOutput = interactor.execute(
            new GenerateChunkInputData(new GamePosition(-6, 0, 0), 32)
        );
        Chunk frontChunk = frontOutput.getChunk();
        GenerateChunkOutputData leftOutput = interactor.execute(
            new GenerateChunkInputData(new GamePosition(0, 0, -4), 32)
        );
        Chunk leftChunk = leftOutput.getChunk();
        GenerateChunkOutputData rightOutput = interactor.execute(
            new GenerateChunkInputData(new GamePosition(0, 0, 4), 32)
        );
        Chunk rightChunk = rightOutput.getChunk();

        // Chunks should be part of the valley
        assertTrue(leftChunk.getMaxBlockHeight() > 0, "Left chunk should be elevated");
        assertTrue(rightChunk.getMaxBlockHeight() > 0, "Right chunk should be elevated");
        assertTrue(frontChunk.getMaxBlockHeight() > 0, "Front chunk should be elevated");
        assertTrue(backChunk.getMaxBlockHeight() > 0, "Back chunk should be elevated");
    }

    @Test
    void shouldGenerateAir() {
        GenerateChunkOutputData output = interactor.execute(
            new GenerateChunkInputData(new GamePosition(0, 2, 0), 32)
        );
        Chunk chunk = output.getChunk();

        // Chunks should all air
        assertTrue(chunk.getMaxBlockHeight() < chunk.getChunkWorldY(), "Back chunk should be elevated");
    }

    @Test
    void shouldGenerateStone() {
        GenerateChunkOutputData output = interactor.execute(
            new GenerateChunkInputData(new GamePosition(0, 0, 18), 32)
        );
        Chunk chunk = output.getChunk();

        // Chunks should all air
        boolean foundStone = false;
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    foundStone |= chunk.getBlock(x, y, z) == 3;
                }
            }
        }
        assertTrue(foundStone, "Stone should exist");
    }

}
