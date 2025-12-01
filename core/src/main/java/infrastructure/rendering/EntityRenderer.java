/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Infrastructure/Rendering)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package infrastructure.rendering;

import infrastructure.rendering.strategies.GenerateMeshInputData;
import domain.repositories.EntityStorage;
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
