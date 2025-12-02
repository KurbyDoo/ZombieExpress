package application.game_features.shoot;

import application.game_features.generate_entity.bullet.GenerateBulletInputData;
import domain.entities.EntityFactory;
import domain.world.GamePosition;

public class ShootInteractor implements ShootInputBoundary {
    private final EntityFactory entityFactory;

    public ShootInteractor(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public ShootOutputData execute(ShootInputData inputData) {
        GamePosition bulletPos = new GamePosition(inputData.getPlayerPos());
        GamePosition bulletDir = new GamePosition(inputData.getPlayerDir());
        bulletDir.nor();
        bulletPos.add(bulletDir.x, bulletDir.y - 1, bulletDir.z);

        GenerateBulletInputData bulletInput = new GenerateBulletInputData(bulletPos, bulletDir);
        int id = entityFactory.create(bulletInput);

        return new ShootOutputData(id, bulletPos, bulletDir);
    }
}
