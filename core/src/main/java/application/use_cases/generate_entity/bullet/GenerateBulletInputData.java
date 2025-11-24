package application.use_cases.generate_entity.bullet;

import application.use_cases.generate_entity.GenerateEntityInputData;
import com.badlogic.gdx.math.Vector3;
import domain.Chunk;
import domain.entities.EntityType;

public class GenerateBulletInputData  extends GenerateEntityInputData {
    public GenerateBulletInputData(Vector3 position, Chunk chunk) {
        super(EntityType.BULLET, position, chunk);
    }
}
