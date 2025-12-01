package framework.rendering.strategies;

import framework.physics.GameMesh;

public interface GenerateMeshStrategy {
    GameMesh execute(GenerateMeshInputData inputData);
}
