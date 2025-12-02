package application.game_features.remove_entity;

import data_access.MockEntityStorage;
import data_access.MockMeshStorage;
import domain.entities.Entity;
import domain.entities.EntityType;
import domain.world.GamePosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RemoveEntityTest {
    private MockEntityStorage entityStorage;
    private MockMeshStorage meshStorage;
    private final List<Integer> pendingRemoval = new ArrayList<>();
    private RemoveEntityInteractor interactor;

    @BeforeEach
    void setup(){
        interactor = new RemoveEntityInteractor();
        entityStorage = new MockEntityStorage();
        meshStorage = new MockMeshStorage();
    }

    @Test
    void removeMarkedEntities(){
        int id1 = 1;
        int id2 = 2;
        Entity e1 =  new Entity(id1, EntityType.ZOMBIE, new GamePosition(0, 0, 0), true);
        Entity e2 = new Entity(id2, EntityType.BULLET, new GamePosition(1,1,0), true);

        e1.markForRemoval();

        meshStorage.addMesh(id1, null);
        meshStorage.addMesh(id2, null);

        entityStorage.setIDEntityPair(id1, e1);
        entityStorage.setIDEntityPair(id2, e2);


        RemoveEntityInputData inputData = new RemoveEntityInputData(entityStorage, meshStorage, pendingRemoval);

        interactor.execute(inputData);

        // Entity 1 should be gone
        assertNull(entityStorage.getEntityByID(id1), "Entity 1 should be removed from EntityStorage");
        assertFalse(meshStorage.hasMesh(id1), "Entity 1 mesh should be removed from MeshStorage");

        // Entity 2 should remain
        assertNotNull(entityStorage.getEntityByID(id2), "Entity 2 should remain in EntityStorage");
        assertTrue(meshStorage.hasMesh(id2), "Entity 2 mesh should remain in MeshStorage");
        pendingRemoval.clear();
    }

}
