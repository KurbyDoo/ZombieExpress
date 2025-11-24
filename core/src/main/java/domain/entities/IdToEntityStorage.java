package domain.entities;


import data_access.EntityStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IdToEntityStorage implements EntityStorage {
    private Map<Integer, Entity> storage = new HashMap<>();

    @Override
    public Entity getEntityByID(Integer id) {
        return storage.get(id);
    }

    @Override
    public void setIDEntityPair(Integer id, Entity e) {
        storage.put(id, e);
    }

    @Override
    public void removeEntity(Integer id) {
        storage.remove(id);
    }

    @Override
    public Set<Integer> getAllIds() {
        return storage.keySet();
    }
}
