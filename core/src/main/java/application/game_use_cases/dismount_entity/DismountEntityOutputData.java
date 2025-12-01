/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Output Data Pattern: Encapsulates dismount use case output.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds dismount output data.
 * - [N/A] Other principles not heavily applicable for DTOs.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Fields are private and final.
 * - [PASS] Boolean getter uses 'is' prefix.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.dismount_entity;

public class DismountEntityOutputData {
    private final boolean dismountSuccess;

    public DismountEntityOutputData(boolean dismountSuccess) {
        this.dismountSuccess = dismountSuccess;
    }

    public boolean isDismountSuccess() {
        return dismountSuccess;
    }
}
