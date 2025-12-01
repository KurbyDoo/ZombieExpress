/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Encapsulates render radius parameters.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds render radius input.
 * - [PASS] Immutable data class (final fields).
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Unused import: domain.World.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Unused import should be removed.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.render_radius;

import domain.GamePosition;
import domain.World;

public class RenderRadiusManagerInputData {
    private final GamePosition playerPosition;
    private final int renderRadius;

    public RenderRadiusManagerInputData(
        GamePosition playerPosition,
        int radius
    ) {

        this.playerPosition = playerPosition;
        this.renderRadius = radius;
    }

    public GamePosition getPlayerPosition() {
        return playerPosition;
    }

    public int getRenderRadius() {
        return renderRadius;
    }
}
