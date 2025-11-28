package application.use_cases.update_entity;

import domain.GamePosition;
import application.use_cases.ports.PhysicsControlPort;
import data_access.EntityStorage;
import domain.Chunk;
import domain.World;
import domain.entities.Entity;
import domain.entities.EntityType;
import domain.player.Player;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityBehaviourSystem {
    private final PhysicsControlPort physicsControl;
    private final Player player;
    private final EntityStorage storage;
    private final World world;

    // Map Enum types to specific strategies
    private final Map<EntityType, EntityBehaviour> behaviors = new EnumMap<>(EntityType.class);

    private Map<Integer, GamePosition> entityToPosition;

    public EntityBehaviourSystem(PhysicsControlPort physicsControl, Player player, EntityStorage storage, World world) {
        this.physicsControl = physicsControl;
        this.player = player;
        this.storage = storage;
        this.world = world;

        entityToPosition = new HashMap<>();

        initializeBehaviors();
    }

    // TODO: Convert to registry factory pattern
    private void initializeBehaviors() {
        // Register strategies here
        behaviors.put(EntityType.ZOMBIE, new ZombieBehaviour());
        behaviors.put(EntityType.BULLET, new BulletBehaviour());
        behaviors.put(EntityType.TRAIN, new TrainBehaviour());
        // Add more as needed
    }

    public void update(List<Integer> activeEntities, float deltaTime) {
        // Create context once per frame
        BehaviourContext context = new BehaviourContext(physicsControl, player, deltaTime);

        for (Integer entityID : activeEntities) {
            Entity entity = storage.getEntityByID(entityID);
            EntityBehaviour strategy = behaviors.get(entity.getType());

            // TODO: this is a good behaviour to unit test
            if (strategy != null) {
                // Update chunk if it moved
                strategy.update(entity, context);
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
}
