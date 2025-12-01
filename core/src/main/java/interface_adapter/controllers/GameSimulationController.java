package interface_adapter.controllers;

import application.game_features.ports.PhysicsControlPort;
import application.game_features.update_entity.EntityBehaviourSystem;
import domain.player.Player;
import framework.rendering.EntityMeshSynchronizer;
import framework.physics.CollisionHandler;

import java.util.List;

public class GameSimulationController {
    private final WorldSyncController worldSyncController;
    private final CollisionHandler collisionHandler;
    private final EntityBehaviourSystem entityBehaviorSystem;
    private final EntityMeshSynchronizer meshSynchronizer;

    public GameSimulationController(
        WorldSyncController worldSyncController,
        CollisionHandler collisionHandler,
        EntityBehaviourSystem entityBehaviorSystem,
        EntityMeshSynchronizer meshSynchronizer,
        PhysicsControlPort controlPort,
        Player player
    ) {
        this.worldSyncController = worldSyncController;
        this.collisionHandler = collisionHandler;
        this.entityBehaviorSystem = entityBehaviorSystem;
        this.meshSynchronizer = meshSynchronizer;
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
    }

    public void dispose() {
        worldSyncController.dispose();
        // entityBehaviorSystem.dispose(); // if needed
    }
}
