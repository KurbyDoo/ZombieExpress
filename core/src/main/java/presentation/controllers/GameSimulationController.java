package presentation.controllers;

import application.use_cases.update_entity.EntityBehaviourSystem;
import domain.World;
import domain.entities.Entity;
import java.util.List;

public class GameSimulationController {
    private final WorldSyncController worldSyncController;
    private final EntityBehaviourSystem entityBehaviorSystem;
    private final World world;

    public GameSimulationController(
        WorldSyncController worldSyncController,
        EntityBehaviourSystem entityBehaviorSystem,
        World world
    ) {
        this.worldSyncController = worldSyncController;
        this.entityBehaviorSystem = entityBehaviorSystem;
        this.world = world;
    }

    public void update(float deltaTime) {
        worldSyncController.loadUpdate();
        // 2. Get Active Entities
        // We only want to run logic for entities in loaded chunks
        // (You might want to optimize this getter in World later)
        List<Integer> activeEntities = world.getEntitiesInChunks(worldSyncController.getActiveChunkPositions());

        // 3. Run Entity Logic (AI)
        entityBehaviorSystem.update(activeEntities, deltaTime);

        // 4. Physics Step usually happens in the Renderer in LibGDX Bullet wrappers,
        // but logically it belongs here.
        // If you move physics stepping out of ObjectRenderer, call it here.


        // unload chunks
        worldSyncController.unloadUpdate();
    }

    public void dispose() {
        worldSyncController.dispose();
        // entityBehaviorSystem.dispose(); // if needed
    }
}
