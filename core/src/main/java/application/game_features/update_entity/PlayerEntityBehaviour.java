package application.game_features.update_entity;

import domain.entities.Entity;
import domain.entities.PlayerEntity;
import domain.player.Player;
import domain.world.GamePosition;

public class PlayerEntityBehaviour implements EntityBehaviour {
    private GamePosition position;
    private Player player;

    public PlayerEntityBehaviour(Player player) {
        this.player = player;
        this.position = player.getPosition();
    }

    @Override
    public void execute(EntityBehaviourInputData inputData) {
        Entity entity = inputData.getEntity();
        float deltaTime = inputData.getDeltaTime();

        if (!(entity instanceof PlayerEntity)) return;

        PlayerEntity playerEntity = (PlayerEntity) entity;

        // sync health and collision
        playerEntity.setPosition(player.getPosition());
        player.setCurrentHealth(playerEntity.getHealth());

    }
}
