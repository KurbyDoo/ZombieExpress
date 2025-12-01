/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Repository Pattern: Defines contract for entity persistence/retrieval.
 * - Port Pattern (Clean Architecture): Abstraction for entity data access.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain interface defining a port.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines entity data access contract.
 * - [PASS] OCP: Implementations can be added without modification.
 * - [PASS] LSP: All implementations must fulfill this contract.
 * - [PASS] ISP: Interface is focused (4 methods).
 * - [PASS] DIP: High-level modules depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [PASS] Method names follow camelCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing interface-level and method-level Javadoc.
 * - [PASS] Clean interface design.
 */
package domain.repositories;

import domain.entities.Entity;

import java.util.Set;

public interface EntityStorage {
    Entity getEntityByID(Integer id);
    void setIDEntityPair(Integer id, Entity e);
    void removeEntity(Integer id);
    Set<Integer> getAllIds();
}
