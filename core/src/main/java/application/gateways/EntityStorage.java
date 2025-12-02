package application.gateways;

import domain.entities.Entity;
import java.util.Set;

public interface EntityStorage {
    Entity getEntityByID(Integer id);

    void setIDEntityPair(Integer id, Entity e);

    void removeEntity(Integer id);

    Set<Integer> getAllIds();
}
