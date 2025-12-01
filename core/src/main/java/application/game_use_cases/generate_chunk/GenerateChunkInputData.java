/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Encapsulates chunk generation use case input.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds chunk generation input.
 * - [PASS] Immutable data class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Fields are private and final.
 * - [WARN] Unused import: domain.World.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Unused import should be removed.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.generate_chunk;

import domain.GamePosition;
import domain.World;

public class GenerateChunkInputData {
    private final GamePosition position;
    private final int worldEndX;
    public GenerateChunkInputData(GamePosition position, int worldEndX) {
        this.position = position;
        this.worldEndX = worldEndX;
    }

    public GamePosition getPosition() { return position; }
    public int getWorldEndX() { return worldEndX; }
}
