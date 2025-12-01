/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern: Concrete strategy for pickup entity creation.
 * - Factory Method: Creates PickupEntity instances.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - creates pickup entities.
 * - [PASS] OCP: Implements GenerateEntityStrategy without modification.
 * - [PASS] LSP: Correctly implements GenerateEntityStrategy.
 * - [PASS] ISP: Implements focused interface.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Direct cast (line 13) could throw ClassCastException.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.generate_entity.pickup;

import application.game_use_cases.generate_entity.GenerateEntityStrategy;
import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.entities.Entity;
import domain.entities.PickupEntity;

public class GeneratePickupStrategy implements GenerateEntityStrategy {

    @Override
    public Entity execute(GenerateEntityInputData genericInput) {
        GeneratePickupInputData input = (GeneratePickupInputData) genericInput;

        return new PickupEntity(
            input.getId(),
            input.getItem(),
            input.getPosition(),
            true
        );
    }
}
