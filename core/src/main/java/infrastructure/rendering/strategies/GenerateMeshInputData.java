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

import domain.entities.Entity;

public class GenerateMeshInputData {
    private final Entity entity;
    private final int id;
    public GenerateMeshInputData(Entity entity, int id) {
        this.entity = entity;
        this.id = id;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getId() {
        return id;
    }
}
