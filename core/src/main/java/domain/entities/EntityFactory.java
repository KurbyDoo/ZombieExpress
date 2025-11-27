package domain.entities;

import application.use_cases.generate_entity.GenerateEntityStrategy;
import application.use_cases.generate_entity.GenerateEntityInputData;
import data_access.EntityStorage;
import domain.Chunk;
import domain.World;

import java.util.HashMap;
import java.util.Map;

public class EntityFactory {
    private static int idCounter;
    private final Map<EntityType, GenerateEntityStrategy> registry;
    private final EntityStorage storage;

    private EntityFactory(Map<EntityType, GenerateEntityStrategy> registry, EntityStorage storage) {
        idCounter = 0;
        this.registry = registry;
        this.storage = storage;
    }

    public void create(GenerateEntityInputData inputData) {
        // TODO: Should the entity hold id? yes right?
        idCounter++;
        inputData.setId(idCounter);
        Entity e = registry.get(inputData.getType()).execute(inputData);
        storage.setIDEntityPair(e.getID(), e);
    }

    public static class EntityFactoryBuilder {
        private final Map<EntityType, GenerateEntityStrategy> registry = new HashMap<>();
        private final EntityStorage storage;

        public EntityFactoryBuilder(EntityStorage storage) {
            this.storage = storage;
        }

        public EntityFactoryBuilder register(EntityType type, GenerateEntityStrategy strategy) {
            registry.put(type, strategy);
            return this;
        }

        public EntityFactory build() {
            return new EntityFactory(registry, storage);
        }
    }
}
