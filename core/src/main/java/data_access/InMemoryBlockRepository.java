/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Data Access)
 *
 * DESIGN PATTERNS:
 * - Repository Pattern: Implements BlockRepository interface.
 * - Flyweight Factory Pattern: Caches and reuses Block instances.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] Implements domain interface (BlockRepository).
 * - [PASS] No LibGDX dependencies - pure data storage.
 * - [PASS] Correct layer for concrete repository implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - block storage.
 * - [PASS] OCP: Can add new blocks without modification.
 * - [PASS] LSP: Correctly implements BlockRepository.
 * - [PASS] DIP: Implements domain abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Uses Java 8 Optional correctly.
 * - [NOTE] TODO comment suggests future JSON configuration.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] TODO comment should be addressed.
 * - [PASS] Generally well-structured.
 */
package data_access;

import domain.repositories.BlockRepository;
import domain.Block;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Adapter class following the flyweight factory pattern
public class InMemoryBlockRepository implements BlockRepository {
    private final Map<Short, Block> blocksById = new HashMap<>();
    private final Map<String, Block> blocksByName = new HashMap<>();

    // TODO: We will update this repository to read from json
    public InMemoryBlockRepository() {
        register(new Block((short) 0, "AIR", false, true));
        register(new Block((short) 1, "GRASS", true, false));
        register(new Block((short) 2, "DIRT", true, false));
        register(new Block((short) 3, "STONE", true, false));
    }

    private void register(Block block) {
        blocksById.put(block.getId(), block);
        blocksByName.put(block.getName(), block);
    }

    @Override
    public Optional<Block> findById(short id) {
        return Optional.ofNullable(blocksById.get(id));
    }

    @Override
    public Optional<Block> findByName(String name) {
        return Optional.ofNullable(blocksByName.get(name.toUpperCase()));
    }

    @Override
    public Collection<Block> findAll() {
        return blocksById.values();
    }
}
