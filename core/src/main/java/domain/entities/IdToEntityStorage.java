package domain.entities;


import java.util.HashMap;
import java.util.Map;

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
}
