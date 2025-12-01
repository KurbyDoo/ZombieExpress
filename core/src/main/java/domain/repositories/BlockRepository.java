/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Domain (Level 1 - Enterprise Business Rules)
 *
 * DESIGN PATTERNS:
 * - Repository Pattern: Defines contract for block persistence/retrieval.
 * - Port Pattern (Clean Architecture): Abstraction for data access.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure domain interface defining a port.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - defines block data access contract.
 * - [PASS] OCP: Implementations can be added without modification.
 * - [PASS] LSP: All implementations must fulfill this contract.
 * - [PASS] ISP: Interface is focused and minimal (3 methods).
 * - [PASS] DIP: High-level modules depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [PASS] Uses Java 8 Optional for nullable returns.
 * - [MINOR] Missing Javadoc documentation.
 * - [NOTE] TODO comment suggests design uncertainty.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing interface-level Javadoc.
 * - [WARN] TODO comment on line 7 should be addressed.
 */
package domain.repositories;

import domain.Block;
import java.util.Collection;
import java.util.Optional;

// TODO: Is this class needed?
public interface BlockRepository {
    Optional<Block> findById(short id);
    Optional<Block> findByName(String name);
    Collection<Block> findAll();
}
