package application.use_cases.update_entity;

import application.use_cases.ports.PhysicsControlPort;
import data_access.EntityStorage;
import domain.entities.Entity;
import domain.entities.EntityType;
import domain.player.Player;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EntityBehaviourSystem {
    private final PhysicsControlPort physicsControl;
    private final Player player;
    private final EntityStorage storage;

    // Map Enum types to specific strategies
    private final Map<EntityType, EntityBehaviour> behaviors = new EnumMap<>(EntityType.class);

    public EntityBehaviourSystem(PhysicsControlPort physicsControl, Player player, EntityStorage storage) {
        this.physicsControl = physicsControl;
        this.player = player;
        this.storage = storage;

        initializeBehaviors();
    }

    private void initializeBehaviors() {
        // Register strategies here
        behaviors.put(EntityType.ZOMBIE, new ZombieBehaviour());
        behaviors.put(EntityType.BULLET, new BulletBehaviour());
        // Add more as needed
    }

    public void update(List<Integer> activeEntities, float deltaTime) {
        // Create context once per frame
        BehaviourContext context = new BehaviourContext(physicsControl, player, deltaTime);

        for (Integer entityID : activeEntities) {
            Entity entity = storage.getEntityByID(entityID);
            EntityBehaviour strategy = behaviors.get(entity.getType());

            if (strategy != null) {
                strategy.update(entity, context);
            }
        }
    }
}
