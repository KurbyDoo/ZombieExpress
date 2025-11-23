package application.use_cases.generate_mesh;

import physics.GameMesh;

public interface GenerateMeshStrategy {
    GameMesh execute(GenerateMeshInputData inputData);
}
