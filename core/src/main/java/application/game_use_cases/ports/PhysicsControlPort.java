/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Port Pattern (Clean Architecture): Defines abstraction for physics operations.
 * - Dependency Inversion: Use cases depend on this abstraction.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure port interface - EXCELLENT example of Clean Architecture.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - physics control operations.
 * - [PASS] ISP: Focused interface (3 methods).
 * - [PASS] DIP: High-level modules depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing interface-level Javadoc.
 */
package application.game_use_cases.ports;

import domain.GamePosition;

public interface PhysicsControlPort {
    void setLinearVelocity(int entityId, float x, float y, float z);
    GamePosition getLinearVelocity(int entityId); // Needed to preserve falling speed
    void lookAt(int entityId, GamePosition targetPosition);
}
