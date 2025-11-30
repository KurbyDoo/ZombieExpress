package application.game_use_cases.shoot;

import application.game_use_cases.generate_entity.bullet.GenerateBulletInputData;
import domain.GamePosition;
import domain.entities.EntityFactory;

public class ShootInteractor implements ShootInputBoundary{
    private EntityFactory entityFactory;

    public ShootInteractor(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public void execute(ShootInputData inputData) {
        // Spawn bullet
        System.out.println("Spawning bullet");
        System.out.println(inputData.getPlayerPos().x + " " + inputData.getPlayerPos().y +  " " + inputData.getPlayerPos().z);
        GamePosition pos = new GamePosition(inputData.getPlayerPos());
        GenerateBulletInputData bulletInput = new GenerateBulletInputData(pos);
        entityFactory.create(bulletInput);
    }
}
