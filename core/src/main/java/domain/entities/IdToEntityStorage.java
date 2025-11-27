package domain.entities;


import com.badlogic.gdx.math.Vector3;
import data_access.EntityStorage;
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
