package data_access;

import domain.entities.Entity;
import application.gateways.EntityStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MockEntityStorage implements EntityStorage {
    Map<Integer, Entity> entities = new HashMap<>();
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

    }

    @Override
    public Set<Integer> getAllIds() {
        return entities.keySet();
    }
}
