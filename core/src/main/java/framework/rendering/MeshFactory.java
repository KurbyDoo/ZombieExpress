package framework.rendering;

import domain.entities.EntityType;
import framework.physics.GameMesh;
import framework.rendering.strategies.GenerateMeshInputData;
import framework.rendering.strategies.GenerateMeshStrategy;
import java.util.HashMap;
import java.util.Map;

public class MeshFactory {
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
