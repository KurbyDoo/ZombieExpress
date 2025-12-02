package application.game_use_cases.GameMesh_Collision;

import domain.entities.Bullet;
import domain.entities.PlayerEntity;
import domain.entities.Zombie;

public class PlayerZombieCollisionInteractor implements PlayerZombieCollisionInputBoundary{

    @Override
    public void execute(PlayerZombieCollisionInputData inputData){
        PlayerEntity player = (PlayerEntity) inputData.getPlayerEntity();

        player.lostHealth(30f);

        System.out.println("Collision Logic: PLAYER TAKES DAMAGE");
    }

}
