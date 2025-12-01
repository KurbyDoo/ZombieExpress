package interface_adapter.controllers;

import application.game_use_cases.dismount_entity.DismountEntityInputBoundary;
import application.game_use_cases.dismount_entity.DismountEntityInputData;
import application.game_use_cases.generate_entity.bullet.GenerateBulletInputData;
import application.game_use_cases.mount_entity.MountEntityInputBoundary;
import application.game_use_cases.mount_entity.MountEntityInputData;
import application.game_use_cases.mount_entity.MountEntityOutputData;
import application.game_use_cases.pickup.PickupInteractor;
import application.game_use_cases.shoot.ShootInputData;
import application.game_use_cases.shoot.ShootInteractor;
import domain.entities.PickupEntity;
import domain.entities.Train;
import domain.player.Player;
import infrastructure.rendering.EntityRenderer;
import infrastructure.rendering.MeshStorage;

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
        int id = interactor.execute(inputData);
        entityRenderer.loadEntity(id);
    }

}
