package application.game_features.item_interaction;

public class ItemInteractionInputData {

    private final ActionType actionType;

    public ItemInteractionInputData(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public enum ActionType {
        REFRESH_TARGET,
        ACTION_KEY_PRESSED,  // E key
        RIDE_KEY_PRESSED,    // F key
        DROP_KEY_PRESSED     // Q or whatever you use
    }
}
