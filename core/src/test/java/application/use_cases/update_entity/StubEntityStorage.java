package application.use_cases.update_entity;

import data_access.EntityStorage;
import domain.entities.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stub implementation of EntityStorage for unit testing.
 */
public class StubEntityStorage implements EntityStorage {

    private final Map<Integer, Entity> entities = new HashMap<>();

    @Override
    public Entity getEntityByID(Integer id) {
        return entities.get(id);
    }

    @Override
    public void setIDEntityPair(Integer id, Entity e) {
        entities.put(id, e);
    }

    @Override
    public void removeEntity(Integer id) {
        entities.remove(id);
    }

    @Override
    public Set<Integer> getAllIds() {
        return new HashSet<>(entities.keySet());
    }

    // Test helper methods

    public void addEntity(Entity entity) {
        entities.put(entity.getID(), entity);
    }

    public void clear() {
        entities.clear();
    }

    public int size() {
        return entities.size();
    }
}
