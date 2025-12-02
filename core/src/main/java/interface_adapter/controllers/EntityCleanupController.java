package interface_adapter.controllers;

import application.game_features.remove_entity.RemoveEntityInputData;
import application.game_features.remove_entity.RemoveEntityInteractor;

public class EntityCleanupController {
    RemoveEntityInputData inputData;

    public EntityCleanupController(RemoveEntityInputData removeEntityInputData) {
        this.inputData = removeEntityInputData;
    }

    public void processCleanup() {

        new RemoveEntityInteractor().execute(inputData);

    }

}
