package application.game_features.item_interaction;

public class ItemInteractionOutputData {

    private final String message;
    private final Integer removedPickupEntityId;
    private final boolean fueledTrain;
    private final boolean mounted;
    private final boolean dismounted;

    public ItemInteractionOutputData(String message,
                                     Integer removedPickupEntityId,
                                     boolean fueledTrain,
                                     boolean mounted,
                                     boolean dismounted) {
        this.message = message;
        this.removedPickupEntityId = removedPickupEntityId;
        this.fueledTrain = fueledTrain;
        this.mounted = mounted;
        this.dismounted = dismounted;
    }

    public String getMessage() {
        return message;
    }

    public Integer getRemovedPickupEntityId() {
        return removedPickupEntityId;
    }

    public boolean isFueledTrain() {
        return fueledTrain;
    }

    public boolean isMounted() {
        return mounted;
    }

    public boolean isDismounted() {
        return dismounted;
    }
}
