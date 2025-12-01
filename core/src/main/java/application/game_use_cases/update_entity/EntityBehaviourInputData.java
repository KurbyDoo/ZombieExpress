/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Encapsulates entity behavior input.
 * - Data Transfer Object (DTO).
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds behavior input data.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: Pure data class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Fields are private and final (immutable).
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.update_entity;

import domain.entities.Entity;

public class EntityBehaviourInputData {
    private final Entity entity;
    private final float deltaTime;

    public EntityBehaviourInputData(Entity entity, float deltaTime) {
        this.entity = entity;
        this.deltaTime = deltaTime;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getDeltaTime() {
        return deltaTime;
    }
}
