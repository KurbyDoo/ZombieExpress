package domain.entities;

public class Factory <T extends GenerationInteractor> {
    private final T entityGenerationUseCase;
    private final EntityStorage storage;

    public Factory(T useCase, EntityStorage storage) {
        this.entityGenerationUseCase = useCase;
        this.storage = storage;
    }

    public void create(Object... args) {
        // Accepts a varying number of arguments of Object type
        // Needs stronger typing
        Entity e = entityGenerationUseCase.create(args);
        storage.setIDEntityPair(e.getID(), e);
    }
}
