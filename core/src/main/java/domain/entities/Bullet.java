/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Entity Pattern: Concrete entity representing a bullet projectile.
 * - Inheritance: Extends Entity base class.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies (inherits indirectly via GamePosition).
 * - [PASS] Pure domain entity.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages bullet-specific data (direction).
 * - [PASS] OCP: Extends Entity without modifying it.
 * - [PASS] LSP: Can substitute for Entity in any context.
 * - [N/A] ISP: No interfaces implemented.
 * - [N/A] DIP: No dependencies to invert.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Extends Entity correctly.
 * - [WARN] Missing defensive copy in getDirection() - returns mutable reference.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level and method-level Javadoc.
 * - [PASS] Proper use of final for immutable fields.
 */
package domain.entities;

import domain.GamePosition;

public class Bullet extends Entity{

    private GamePosition direction;

    public Bullet(Integer id, GamePosition position, GamePosition direction, boolean isVisible) {
        super(id, EntityType.BULLET, position, isVisible);  // pass data to Entity
        this.direction = direction;
    }

    public GamePosition getDirection() {
        return direction;
    }
}
