package presentation.controllers;

import application.use_cases.EntityGeneration.EntityGenerationInputData;
import application.use_cases.EntityGeneration.EntityGenerationInteractor;
import application.use_cases.RenderZombie.RenderZombieInputData;
import application.use_cases.RenderZombie.RenderZombieInteractor;
import domain.entities.ZombieStorage;
import presentation.ZombieInstanceUpdater;

public class EntityController {
    // Take input from user, turn into Input Data Object and pass through the Input Boundary
    //(i.e., calls a method defined in the Input Boundary interface that takes an Input Data
    // object as a parameter).
    private EntityGenerationInteractor entityGenerationUseCase;
    private RenderZombieInteractor renderZombieUseCase;
    private ZombieStorage zombieStorage;
    private ZombieInstanceUpdater zombieInstanceUpdater;

    public EntityController(EntityGenerationInteractor entityGenerationUseCase, RenderZombieInteractor renderZombieUseCase, ZombieStorage zombieStorage, ZombieInstanceUpdater zombieInstanceUpdater) {
        this.entityGenerationUseCase = entityGenerationUseCase;
        this.renderZombieUseCase = renderZombieUseCase;
        this.zombieStorage = zombieStorage;
        this.zombieInstanceUpdater = zombieInstanceUpdater;
    }

    public void generateZombie() {
        EntityGenerationInputData inputData = new EntityGenerationInputData();
        entityGenerationUseCase.execute(inputData);
    }

    public void renderZombie() {
        RenderZombieInputData inputData = new RenderZombieInputData();
        renderZombieUseCase.execute(inputData);
        zombieInstanceUpdater.updateRenderList();
    }
}
