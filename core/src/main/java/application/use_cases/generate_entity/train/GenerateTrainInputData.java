package application.use_cases.generate_entity.train;

import application.use_cases.generate_entity.GenerateEntityInputData;
import com.badlogic.gdx.math.Vector3;
import domain.Chunk;
import domain.entities.EntityType;

public class GenerateTrainInputData extends GenerateEntityInputData {
    public GenerateTrainInputData(Vector3 position) {
        super(EntityType.TRAIN, position);
    }
}
