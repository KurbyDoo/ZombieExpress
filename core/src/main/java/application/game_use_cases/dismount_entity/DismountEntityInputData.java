/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Encapsulates dismount use case input.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds dismount input data.
 * - [N/A] Other principles not applicable for empty DTO.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Unused import: domain.entities.Rideable.
 * - [WARN] Empty class - consider removing if not needed.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Unused import should be removed.
 * - [WARN] Empty class body.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.dismount_entity;

import domain.entities.Rideable;

public class DismountEntityInputData {
}
