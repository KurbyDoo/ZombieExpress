/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Input Data Pattern: Encapsulates mount entity use case input.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure data structure.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds mount input data.
 * - [PASS] Fields are private and final (immutable).
 * - [N/A] Other principles not heavily applicable.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Proper getter method.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.mount_entity;

import domain.entities.Rideable;

public class MountEntityInputData {
    private final Rideable ride;

    public MountEntityInputData(Rideable ride) {
        this.ride = ride;
    }

    public Rideable getRide() {
        return ride;
    }
}
