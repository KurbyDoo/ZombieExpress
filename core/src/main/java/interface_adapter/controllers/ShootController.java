package interface_adapter.controllers;

import application.game_features.shoot.ShootInputData;
import application.game_features.shoot.ShootInteractor;
import application.game_features.shoot.ShootOutputData;
import domain.player.Player;
import framework.rendering.EntityRenderer;
import framework.rendering.MeshStorage;

public class ShootController {

    private final ShootInteractor interactor;
    private final MeshStorage meshStorage;
    private final EntityRenderer entityRenderer;
    private final Player player;

    public ShootController(
        Player player,
        ShootInteractor interactor,
        MeshStorage meshStorage, EntityRenderer entityRenderer
    ) {
        this.player = player;
        this.interactor = interactor;
        this.meshStorage = meshStorage;
        this.entityRenderer = entityRenderer;
    }

    public void onShootKeyPressed() {
        ShootInputData inputData = new ShootInputData(player.getPosition(), player.getDirection());
        ShootOutputData outputData = interactor.execute(inputData);
        entityRenderer.loadEntity(outputData.getEntityId());
    }
}
