/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Strategy Pattern: Concrete strategy for zombie entity creation.
 * - Factory Method: Creates Zombie instances.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - creates zombie entities.
 * - [PASS] OCP: Implements GenerateEntityStrategy without modification.
 * - [PASS] LSP: Correctly implements GenerateEntityStrategy.
 * - [PASS] ISP: Implements focused interface.
 * - [N/A] DIP: No high-level dependencies.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Clean, minimal implementation.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.generate_entity.zombie;

import application.game_use_cases.generate_entity.GenerateEntityInputData;
import application.game_use_cases.generate_entity.GenerateEntityStrategy;
import domain.entities.Entity;
import domain.entities.Zombie;

public class GenerateZombieStrategy implements GenerateEntityStrategy {
    @Override
    public Entity execute(GenerateEntityInputData inputData) {
        return new Zombie(inputData.getId(), inputData.getPosition(), true);
    }
}
