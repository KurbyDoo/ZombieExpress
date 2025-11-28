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
        Chunk chunk = outputData.getChunk();
        assertNotNull(chunk, "Chunk should not be null");
    }

}
