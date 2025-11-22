package domain.entities;

public interface EntityStorage {

    Entity getEntityByID(Integer id);
    void setIDEntityPair(Integer id, Entity e);
}
