/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Strategy Context Pattern: Manages and delegates to EntityBehaviour strategies.
 * - Factory Pattern: EntityBehaviourSystemFactory builds the system.
 * - Registry Pattern: Maps EntityType to behaviors.
 *
 * ORCHESTRATOR: EntityBehaviourSystem orchestrates entity behavior updates,
 * delegating to specific EntityBehaviour implementations based on entity type.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Manages entity behavior execution.
 * - [PASS] OCP: New entity types can be registered without modification.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces implemented.
 * - [PASS] DIP: Depends on EntityBehaviour abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Builder pattern correctly implemented.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 * - [PASS] Good use of inner factory class.
 */
package application.game_use_cases.update_entity;

import domain.GamePosition;
import domain.repositories.EntityStorage;
import domain.Chunk;
import domain.World;
import domain.entities.Entity;
import domain.entities.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityBehaviourSystem {
    private final EntityStorage storage;
    private final World world;

    // Map Enum types to specific strategies
    private final Map<EntityType, EntityBehaviour> behaviors;

    private Map<Integer, GamePosition> entityToPosition;

    public EntityBehaviourSystem(
        Map<EntityType, EntityBehaviour> behaviors, EntityStorage storage, World world
    ) {
        this.storage = storage;
        this.world = world;
        this.behaviors = behaviors;

        entityToPosition = new HashMap<>();
    }

    public void update(List<Integer> activeEntities, float deltaTime) {
        // Create context once per frame
        for (Integer entityID : activeEntities) {
            Entity entity = storage.getEntityByID(entityID);
            EntityBehaviour strategy = behaviors.get(entity.getType());

            if (strategy != null) {
                strategy.execute(new EntityBehaviourInputData(entity, deltaTime));
            }
        }
    }

    public void updateCache(List<Integer> activeEntities) {
        for (Integer entityID : activeEntities) {
            Entity entity = storage.getEntityByID(entityID);
            entityToPosition.put(entityID, entity.getPosition().cpy());
        }
    }

    public void unloadCache(List<Integer> activeEntities) {
        for (Integer entityID : activeEntities) {
            GamePosition previousPos = entityToPosition.get(entityID);
            Chunk previousChunk = world.getChunkFromWorldPos(previousPos);

            GamePosition newPos = storage.getEntityByID(entityID).getPosition();
            Chunk newChunk = world.getChunkFromWorldPos(newPos);

            if (previousChunk != newChunk) {
                previousChunk.getEntityIds().remove(entityID);
                if (newChunk == null) continue;
                newChunk.getEntityIds().add(entityID);
            }
        }
    }

    public static class EntityBehaviourSystemFactory {
        private final EntityStorage storage;
        private final World world;

        private final Map<EntityType, EntityBehaviour> behaviours;

        public EntityBehaviourSystemFactory(EntityStorage storage, World world) {
            this.storage = storage;
            this.world = world;

            behaviours = new HashMap<>();
        }


        public EntityBehaviourSystemFactory register(EntityType type, EntityBehaviour behaviour) {
            behaviours.put(type, behaviour);
            return this;
        }

        public EntityBehaviourSystem build() {
            return new EntityBehaviourSystem(behaviours, storage, world);
        }
    }
}
