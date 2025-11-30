package application.game_use_cases.shoot;

import application.game_use_cases.generate_entity.bullet.GenerateBulletInputData;
import domain.GamePosition;
import domain.entities.EntityFactory;

public class ShootInteractor implements ShootInputBoundary{
    private final EntityFactory entityFactory;

    public ShootInteractor(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public ShootOutputData execute(ShootInputData inputData) {
        GamePosition bulletPos = new GamePosition(inputData.getPlayerPos());
        bulletPos.sub(0, 1.5f, -0.5f); // Bullet spawns at lower right corner of camera
        GamePosition bulletDir = new GamePosition(inputData.getPlayerDir());
        GenerateBulletInputData bulletInput = new GenerateBulletInputData(bulletPos, bulletDir);
        int id = entityFactory.create(bulletInput);

        ShootOutputData outputData = new ShootOutputData(id);

        return outputData;
    }
}
