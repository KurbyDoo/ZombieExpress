/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Presentation/Controllers)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package presentation.controllers;

import application.game_use_cases.ports.PhysicsControlPort;
import application.game_use_cases.update_entity.EntityBehaviourSystem;
import domain.player.Player;
import infrastructure.rendering.EntityMeshSynchronizer;
import physics.CollisionHandler;

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
