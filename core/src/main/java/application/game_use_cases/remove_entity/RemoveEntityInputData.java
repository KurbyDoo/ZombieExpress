package application.game_use_cases.remove_entity;

import domain.repositories.EntityStorage;
import infrastructure.rendering.MeshStorage;

import java.util.ArrayList;
import java.util.List;

public class RemoveEntityInputData {
    private final EntityStorage entityStorage;
    private final MeshStorage meshStorage;
    private final List<Integer> pendingRemoval = new ArrayList<>();

    public RemoveEntityInputBoundary(EntityStorage entityStorage, MeshStorage meshStorage, List<Integer> pendingRemoval){
        this.entityStorage = entityStorage;
        this.meshStorage = meshStorage;
    }

    public EntityStorage getEntityStorage() {
        return entityStorage;
    }

    public MeshStorage getMeshStorage() {
        return meshStorage;
    }

    public List<Integer> getPendingRemoval(){
        return pendingRemoval;
    }
}
