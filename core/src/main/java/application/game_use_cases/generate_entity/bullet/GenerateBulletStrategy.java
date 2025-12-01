/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern: Concrete strategy for bullet entity creation.
 * - Factory Method: Creates Bullet instances.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - creates bullet entities.
 * - [PASS] OCP: Implements GenerateEntityStrategy without modification.
 * - [PASS] LSP: Correctly implements GenerateEntityStrategy.
 * - [PASS] ISP: Implements focused interface.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Unused imports (GeneratePickupInputData, PickupEntity).
 * - [WARN] Direct cast (line 12) could throw ClassCastException.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Unused imports should be removed.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.generate_entity.bullet;

import application.game_use_cases.generate_entity.GenerateEntityInputData;
import application.game_use_cases.generate_entity.GenerateEntityStrategy;
import application.game_use_cases.generate_entity.pickup.GeneratePickupInputData;
import domain.entities.Bullet;
import domain.entities.Entity;
import domain.entities.PickupEntity;

public class GenerateBulletStrategy implements GenerateEntityStrategy {
    @Override
    public Entity execute(GenerateEntityInputData genericInput) {
        GenerateBulletInputData input = (GenerateBulletInputData) genericInput;

        return new Bullet(
            input.getId(),
            input.getPosition(),
            input.getDirection(),
            true
        );
    }
}
