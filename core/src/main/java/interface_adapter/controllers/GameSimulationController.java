package interface_adapter.controllers;

import application.game_use_cases.ports.PhysicsControlPort;
import application.game_use_cases.update_entity.BehaviourContext;
import application.game_use_cases.update_entity.EntityBehaviourSystem;
import domain.World;
import domain.player.Player;
import infrastructure.rendering.EntityMeshSynchronizer;
import physics.CollisionHandler;

import java.util.List;

public class GameSimulationController {
    private final WorldSyncController worldSyncController;
    private final CollisionHandler collisionHandler;
    private final EntityBehaviourSystem entityBehaviorSystem;
    private final EntityMeshSynchronizer meshSynchronizer;
    private final PhysicsControlPort controlPort;
    private final World world;
    private final Player player;

    public GameSimulationController(
        WorldSyncController worldSyncController,
        CollisionHandler collisionHandler,
        EntityBehaviourSystem entityBehaviorSystem,
        EntityMeshSynchronizer meshSynchronizer,
        PhysicsControlPort controlPort,
        World world,
        Player player
    ) {
        this.worldSyncController = worldSyncController;
        this.collisionHandler = collisionHandler;
        this.entityBehaviorSystem = entityBehaviorSystem;
        this.meshSynchronizer = meshSynchronizer;
        this.controlPort = controlPort;
        this.world = world;
        this.player = player;
    }

    public void update(float deltaTime) {
        worldSyncController.loadUpdate();
        List<Integer> activeEntities = world.getEntitiesInChunks(worldSyncController.getActiveChunkPositions());

        entityBehaviorSystem.updateCache(activeEntities);
        // Run Entity Logic (AI)
        BehaviourContext context = new BehaviourContext(controlPort, player, deltaTime);
        entityBehaviorSystem.update(activeEntities, context);

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
