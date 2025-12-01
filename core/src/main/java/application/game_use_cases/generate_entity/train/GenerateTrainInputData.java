/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Extends GenerateEntityInputData for train creation.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds train creation input.
 * - [PASS] OCP: Extends base class without modification.
 * - [PASS] LSP: Can substitute for GenerateEntityInputData.
 * - [N/A] ISP: No interfaces.
 * - [N/A] DIP: Pure data class.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.generate_entity.train;

import domain.GamePosition;
import application.game_use_cases.generate_entity.GenerateEntityInputData;
import domain.entities.EntityType;

public class GenerateTrainInputData extends GenerateEntityInputData {
    public GenerateTrainInputData(GamePosition position) {
        super(EntityType.TRAIN, position);
    }
}
