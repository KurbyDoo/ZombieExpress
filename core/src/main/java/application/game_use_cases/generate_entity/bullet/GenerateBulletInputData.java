/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Extends GenerateEntityInputData for bullet creation.
 * - Specialization: Adds bullet-specific direction data.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds bullet creation input.
 * - [PASS] OCP: Extends base class without modification.
 * - [PASS] LSP: Can substitute for GenerateEntityInputData.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: Pure data class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Fields are private and final.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.generate_entity.bullet;

import domain.GamePosition;
import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;

public class GenerateBulletInputData extends GenerateEntityInputData {

    private final GamePosition direction;
    public GenerateBulletInputData(GamePosition position, GamePosition direction) {
        super(EntityType.BULLET, position);
        this.direction = direction;
    }

    public GamePosition getDirection() {
        return direction;
    }
}
