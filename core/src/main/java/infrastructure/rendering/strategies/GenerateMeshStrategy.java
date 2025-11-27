package infrastructure.rendering.strategies;

import physics.GameMesh;

public interface GenerateMeshStrategy {
    GameMesh execute(GenerateMeshInputData inputData);
}
