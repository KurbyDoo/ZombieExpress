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

        pendingRemoval.clear();

        // COLLECT: Find what needs to die
        for (Integer id : entityStorage.getAllIds()) {
            if (entityStorage.getEntityByID(id).isMarkedForRemoval()) {
                pendingRemoval.add(id);
            }
        }

        // KILL: Remove them using the IDs we collected
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
