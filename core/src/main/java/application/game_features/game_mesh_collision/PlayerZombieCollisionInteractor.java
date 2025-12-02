package application.game_features.game_mesh_collision;

import domain.entities.PlayerEntity;

public class PlayerZombieCollisionInteractor implements PlayerZombieCollisionInputBoundary {

    @Override
    public void execute(PlayerZombieCollisionInputData inputData) {
        PlayerEntity player = (PlayerEntity) inputData.getPlayerEntity();

        player.lostHealth(30f);

        System.out.println("Collision Logic: PLAYER TAKES DAMAGE");
    }

}
