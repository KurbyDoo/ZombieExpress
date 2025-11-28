package infrastructure.rendering;

import infrastructure.rendering.strategies.GenerateMeshInputData;
import interface_adapter.game.EntityStorage;
import domain.entities.Entity;

public class EntityRenderer {
    private final EntityStorage entityStorage;
    private final MeshFactory meshFactory;
    private final MeshStorage meshStorage;
    public EntityRenderer(EntityStorage entityStorage, MeshFactory meshFactory, MeshStorage meshStorage) {
        this.entityStorage = entityStorage;
        this.meshFactory = meshFactory;
        this.meshStorage = meshStorage;
    }

    public void loadEntity(int id) {
        if (meshStorage.hasMesh(id)) return;
        Entity entity = entityStorage.getEntityByID(id);
        meshFactory.createMesh(new GenerateMeshInputData(entity, id));
    }

    public void unloadEntity(int id) {
        meshStorage.removeMesh(id);
    }
}
