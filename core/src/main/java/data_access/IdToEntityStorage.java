/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Data Access)
 *
 * DESIGN PATTERNS:
 * - Repository Pattern: Implements EntityStorage interface.
 * - In-Memory Storage: Uses HashMap for entity storage.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] Implements domain interface (EntityStorage).
 * - [PASS] Correct layer for concrete storage implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - entity storage.
 * - [PASS] LSP: Correctly implements EntityStorage.
 * - [PASS] DIP: Implements domain abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Extra blank line after package declaration.
 * - [MINOR] Missing class-level Javadoc.
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package data_access;


import domain.entities.Entity;
import domain.entities.Train;
import domain.repositories.EntityStorage;
import domain.Chunk;
import domain.GamePosition;
import domain.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IdToEntityStorage implements EntityStorage {
    private final Map<Integer, Entity> storage = new HashMap<>();
    private final World world;

    public IdToEntityStorage(World world) {
        this.world = world;
    }

    public Train getTrain() {
        for (Entity e : storage.values()) {
            if (e instanceof Train) {
                return (Train) e;
            }
        }
        return null;
    }

    @Override
    public Entity getEntityByID(Integer id) {
        return storage.get(id);
    }

    @Override
    public void setIDEntityPair(Integer id, Entity e) {
        storage.put(id, e);
        GamePosition position = e.getPosition();
        Chunk chunk = world.getChunkFromWorldPos(position);
        chunk.addEntity(id);
    }

    @Override
    public void removeEntity(Integer id) {
        Entity entity = storage.get(id);
        if (entity == null) return;
        GamePosition position = entity.getPosition();
        Chunk chunk = world.getChunkFromWorldPos(position);
        storage.remove(id);
        chunk.removeEntity(id);
    }

    @Override
    public Set<Integer> getAllIds() {
        return storage.keySet();
    }
}
