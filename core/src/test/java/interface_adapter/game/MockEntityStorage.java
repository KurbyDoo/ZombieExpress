package interface_adapter.game;

import domain.entities.Entity;

import java.util.Map;
import java.util.Set;

public class MockEntityStorage implements EntityStorage {
    Map<Integer, Entity> entities;

    @Override
    public Entity getEntityByID(Integer id) {
        return entities.getOrDefault(id, null);
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
        return entities.keySet();
    }
}
