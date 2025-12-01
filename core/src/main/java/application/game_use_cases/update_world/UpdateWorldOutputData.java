/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Data Pattern: Encapsulates world update results.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds update output data.
 * - [PASS] Immutable data class (final fields).
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.update_world;

import domain.Chunk;
import domain.GamePosition;

import java.util.List;
import java.util.Map;

public class UpdateWorldOutputData {
    private final Map<GamePosition, Chunk> chunksToLoad;
    private final Map<GamePosition, Chunk> chunksToUnload;
    private final List<Integer> activeEntities;

    public UpdateWorldOutputData(
        Map<GamePosition, Chunk> toLoad,
        Map<GamePosition, Chunk> toUnload,
        List<Integer> activeEntities
    ) {
        chunksToLoad = toLoad;
        chunksToUnload = toUnload;
        this.activeEntities = activeEntities;
    }

    public Map<GamePosition, Chunk> getChunksToLoad() {
        return chunksToLoad;
    }

    public Map<GamePosition, Chunk> getChunksToUnload() {
        return chunksToUnload;
    }

    public List<Integer> getActiveEntities() {
        return activeEntities;
    }
}
