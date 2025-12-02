package application.game_features.remove_entity;

import application.gateways.EntityStorage;
import framework.rendering.MeshStorage;
import java.util.ArrayList;
import java.util.List;

public class RemoveEntityInputData {
    private final EntityStorage entityStorage;
    private final MeshStorage meshStorage;
    private final List<Integer> pendingRemoval = new ArrayList<>();

    public RemoveEntityInputData(EntityStorage entityStorage, MeshStorage meshStorage, List<Integer> pendingRemoval) {
        this.entityStorage = entityStorage;
        this.meshStorage = meshStorage;
    }

    public EntityStorage getEntityStorage() {
        return entityStorage;
    }

    public MeshStorage getMeshStorage() {
        return meshStorage;
    }

    public List<Integer> getPendingRemoval() {
        return pendingRemoval;
    }
}
