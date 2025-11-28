package presentation.controllers;

import application.use_cases.update_entity.EntityBehaviourSystem;
import domain.World;
import infrastructure.rendering.EntityMeshSynchronizer;
import physics.CollisionHandler;

import java.util.Collection;
import java.util.List;

public class GameSimulationController {
    private final WorldSyncController worldSyncController;
    private final CollisionHandler collisionHandler;
    private final EntityBehaviourSystem entityBehaviorSystem;
    private final EntityMeshSynchronizer meshSynchronizer;
    private final World world;

    public GameSimulationController(
        WorldSyncController worldSyncController,
        CollisionHandler collisionHandler,
        EntityBehaviourSystem entityBehaviorSystem,
        EntityMeshSynchronizer meshSynchronizer,
        World world
    ) {
        this.worldSyncController = worldSyncController;
        this.collisionHandler = collisionHandler;
        this.entityBehaviorSystem = entityBehaviorSystem;
        this.meshSynchronizer = meshSynchronizer;
        this.world = world;
    }

    public void update(float deltaTime) {
        worldSyncController.loadUpdate();
        List<Integer> activeEntities = world.getEntitiesInChunks(worldSyncController.getActiveChunkPositions());

        entityBehaviorSystem.updateCache(activeEntities);
        // Run Entity Logic (AI)
        entityBehaviorSystem.update(activeEntities, deltaTime);

        meshSynchronizer.sync();

        // Update physics
        collisionHandler.dynamicsWorld.stepSimulation(deltaTime, 5, 1f/60f);


        // unload chunks
        entityBehaviorSystem.unloadCache(activeEntities);
        worldSyncController.unloadUpdate();
    }

    public void dispose() {
        worldSyncController.dispose();
        // entityBehaviorSystem.dispose(); // if needed
    }
}
