package application.game_features.remove_entity;

import application.gateways.EntityStorage;
import framework.rendering.MeshStorage;
import java.util.List;

public class RemoveEntityInteractor implements RemoveEntityInputBoundary {

    @Override
    public void execute(RemoveEntityInputData inputData) {
        EntityStorage entityStorage = inputData.getEntityStorage();
        MeshStorage meshStorage = inputData.getMeshStorage();
        List<Integer> pendingRemoval = inputData.getPendingRemoval();

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
