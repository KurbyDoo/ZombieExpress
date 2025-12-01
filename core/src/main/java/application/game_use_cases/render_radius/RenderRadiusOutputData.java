/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Data Pattern: Encapsulates render radius calculation results.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds render output data.
 * - [WARN] Not immutable - Sets are initialized empty and populated externally.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.render_radius;

import domain.GamePosition;

import java.util.HashSet;
import java.util.Set;

public class RenderRadiusOutputData {
    private final Set<GamePosition> chunksToGenerate;
    private final Set<GamePosition> chunksToLoad;
    private final Set<GamePosition> chunksToUnload;
    private final Set<GamePosition> chunksToUpdate;
    public RenderRadiusOutputData() {
        chunksToGenerate = new HashSet<>();
        chunksToLoad = new HashSet<>();
        chunksToUnload = new HashSet<>();
        chunksToUpdate = new HashSet<>();
    }

    public Set<GamePosition> getChunksToGenerate() {
        return chunksToGenerate;
    }

    public Set<GamePosition> getChunksToLoad() {
        return chunksToLoad;
    }

    public Set<GamePosition> getChunksToUnload() {
        return chunksToUnload;
    }

    public Set<GamePosition> getChunksToUpdate() {
        return chunksToUpdate;
    }
}
