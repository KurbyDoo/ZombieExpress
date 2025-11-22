package domain.entities;

public interface GenerationInteractor {
    // Used for Factory - can be deleted later
    // Need stronger typing
    Entity create(Object... params);
}
