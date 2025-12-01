/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Base class for entity generation input.
 * - Template Method: Extended by specific entity input data classes.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure for factory input.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds entity creation input.
 * - [PASS] OCP: Extended by specific input data classes.
 * - [PASS] LSP: Subclasses add specific data.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: Pure data class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Unused import: domain.Chunk (line 4).
 * - [WARN] Field 'id' is mutable but other fields are final - inconsistent.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Unused import should be removed.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.generate_entity;

import domain.GamePosition;
import domain.Chunk;
import domain.entities.EntityType;

public class GenerateEntityInputData {
    private int id;
    private final EntityType type;
    private final GamePosition position;

    public GenerateEntityInputData(EntityType type, GamePosition position) {
        this.type = type;
        this.position = position;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public EntityType getType() {
        return type;
    }

    public GamePosition getPosition() {
        return position;
    }
}
