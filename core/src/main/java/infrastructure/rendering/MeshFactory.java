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
import infrastructure.rendering.strategies.GenerateMeshStrategy;
import domain.entities.EntityType;
import physics.GameMesh;

import java.util.HashMap;
import java.util.Map;

public class MeshFactory {
    // TODO: should this class hold storage? should that be the responsibility of the caller
    // The issue is that its tedious for everyone to hold both the mesh storage and factory
    // Maybe we just have one class that holds both and everyone holds that class
    private final Map<EntityType, GenerateMeshStrategy> registry;
    private final MeshStorage storage;

    private MeshFactory(Map<EntityType, GenerateMeshStrategy> registry, MeshStorage storage) {
        this.registry = registry;
        this.storage = storage;
    }

    public void createMesh(GenerateMeshInputData inputData) {
        GameMesh mesh = registry.get(inputData.getEntity().getType()).execute(inputData);
        storage.addMesh(inputData.getId(), mesh);
    }

    public static class MeshFactoryBuilder {
        private final Map<EntityType, GenerateMeshStrategy> registry = new HashMap<>();
        private final MeshStorage storage;

        public MeshFactoryBuilder(MeshStorage storage) {
            this.storage = storage;
        }

        public MeshFactoryBuilder register(EntityType type, GenerateMeshStrategy strategy) {
            registry.put(type, strategy);
            return this;
        }

        public MeshFactory build() {
            return new MeshFactory(registry, storage);
        }
    }
}
