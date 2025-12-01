/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Entity Pattern: Concrete entity representing a zombie enemy.
 * - Inheritance: Extends Entity base class.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages zombie-specific data (speed, health).
 * - [PASS] OCP: Extends Entity without modifying it.
 * - [PASS] LSP: Can substitute for Entity in any context.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No dependencies to invert.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Field 'Health' should be 'health' (line 8) - Java fields use camelCase.
 * - [WARN] Field 'Health' is unused (no getter/setter for health).
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Field name 'Health' violates naming convention (should be 'health').
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [PASS] Single-line getter follows common style.
 */
package domain.entities;

import domain.GamePosition;

public class Zombie extends Entity {
    // Store raw info of the zombies
    private float speed = 2f;
    private float Health = 100f;

    public Zombie(Integer id, GamePosition position, boolean isVisible) {
        super(id, EntityType.ZOMBIE, position, isVisible);  // pass data to Entity
    }

    public float getSpeed() { return speed; }
}
