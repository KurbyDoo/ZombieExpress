package interface_adapter.controllers;

import application.game_features.update_entity.EntityBehaviourSystem;
import application.ports.PhysicsControlPort;
import domain.player.Player;
import framework.physics.CollisionHandler;
import framework.rendering.EntityMeshSynchronizer;
import java.util.List;

public class GameSimulationController {
    private final WorldSyncController worldSyncController;
    private final CollisionHandler collisionHandler;
    private final EntityBehaviourSystem entityBehaviorSystem;
    private final EntityMeshSynchronizer meshSynchronizer;
    private final EntityCleanupController cleanupController;

    public GameSimulationController(
        WorldSyncController worldSyncController,
        CollisionHandler collisionHandler,
        EntityBehaviourSystem entityBehaviorSystem,
        EntityMeshSynchronizer meshSynchronizer,
        PhysicsControlPort controlPort,
        Player player,
        EntityCleanupController cleanupController
    ) {
        this.worldSyncController = worldSyncController;
        this.collisionHandler = collisionHandler;
        this.entityBehaviorSystem = entityBehaviorSystem;
        this.meshSynchronizer = meshSynchronizer;
        this.cleanupController = cleanupController;
    }

    public void update(float deltaTime) {
        worldSyncController.loadUpdate();
        List<Integer> activeEntities = worldSyncController.getActiveEntities();

        entityBehaviorSystem.updateCache(activeEntities);
        // Run Entity Logic (AI)
        entityBehaviorSystem.update(activeEntities, deltaTime);

        meshSynchronizer.sync();

        // Update physics
        collisionHandler.stepSimulation(deltaTime);

        // unload chunks
        entityBehaviorSystem.unloadCache(activeEntities);
        worldSyncController.unloadUpdate();

        // remove dead entities
        cleanupController.processCleanup();

    }

    public void dispose() {
        worldSyncController.dispose();
        // entityBehaviorSystem.dispose(); // if needed
    }
}
