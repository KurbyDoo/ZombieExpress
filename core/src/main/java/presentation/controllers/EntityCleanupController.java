package presentation.controllers;

import application.game_use_cases.remove_entity.RemoveEntityInputData;
import application.game_use_cases.remove_entity.RemoveEntityInteractor;
import domain.entities.Entity;
import domain.repositories.EntityStorage;
import infrastructure.rendering.MeshStorage;

import java.util.ArrayList;
import java.util.List;

public class EntityCleanupController {
    RemoveEntityInputData inputData;
    public EntityCleanupController(RemoveEntityInputData removeEntityInputData) {
        this.inputData = removeEntityInputData;
    }
    public void processCleanup() {

        new RemoveEntityInteractor().execute(inputData);

    }

}
