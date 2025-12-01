/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Infrastructure/Rendering)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern: Mesh generation strategy.
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package infrastructure.rendering.strategies;

import physics.GameMesh;

public interface GenerateMeshStrategy {
    GameMesh execute(GenerateMeshInputData inputData);
}
