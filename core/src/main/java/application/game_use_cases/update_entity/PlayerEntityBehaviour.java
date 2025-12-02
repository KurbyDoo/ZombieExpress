package application.game_use_cases.update_entity;

import domain.GamePosition;
import domain.entities.Entity;
import domain.entities.PlayerEntity;
import domain.entities.Train;
import domain.player.Player;

public class PlayerEntityBehaviour implements EntityBehaviour{
    private GamePosition position;
    private Player player;
    public PlayerEntityBehaviour(Player player){
        this.player = player;
        this.position = player.getPosition();
    }

    @Override
    public void execute(EntityBehaviourInputData inputData){
        Entity entity = inputData.getEntity();
        float deltaTime = inputData.getDeltaTime();

        if (!(entity instanceof PlayerEntity)) return;

        PlayerEntity playerEntity = (PlayerEntity) entity;

        // update playerEntity position
        playerEntity.setPosition(player.getPosition());
        // update heatlh
        player.setCurrentHealth(playerEntity.getHealth());

    }
}
