/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Interface Pattern: Defines contract for rideable entities.
 * - Strategy Pattern: Allows different rideable implementations (Train, etc.).
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Focused interface for rideable entities.
 * - [PASS] OCP: New rideable types can implement this interface.
 * - [PASS] LSP: Implementers must fulfill the contract.
 * - [PASS] ISP: Interface is minimal and focused (3 methods).
 * - [PASS] DIP: High-level modules depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [PASS] Method signatures are clear.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing interface-level and method-level Javadoc.
 * - [PASS] Clean, minimal interface design.
 */
package domain.entities;

import domain.GamePosition;

public interface Rideable {
    int getSpeed();
    GamePosition getRideOffset();
    GamePosition getPosition();
}
