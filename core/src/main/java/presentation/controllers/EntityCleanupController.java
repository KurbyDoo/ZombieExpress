package presentation.controllers;

import domain.entities.Entity;
import domain.repositories.EntityStorage;
import infrastructure.rendering.MeshStorage;

import java.util.ArrayList;
import java.util.List;

public class EntityCleanupController {
    private final EntityStorage entityStorage;
    private final MeshStorage meshStorage;
    private final List<Integer> pendingRemoval = new ArrayList<>();
    public EntityCleanupController(EntityStorage entityStorage, MeshStorage meshStorage) {
        this.entityStorage = entityStorage;
        this.meshStorage = meshStorage;
    }
    public void processCleanup() {
        // Clear the list from the last frame
        pendingRemoval.clear();

        // 1. COLLECT: Find what needs to die
        for (Integer id : entityStorage.getAllIds()) {
            if (entityStorage.getEntityByID(id).isMarkedForRemoval()) {
                pendingRemoval.add(id);
            }
        }

        // 2. KILL: Remove them using the IDs we collected
        for (Integer id : pendingRemoval) {
            // Remove Physics/Visuals
            if (meshStorage.hasMesh(id)) {
                meshStorage.removeMesh(id);
            }

            // Remove Logic
            entityStorage.removeEntity(id);
        }
    }

}
