package application.account_features.register;

/**
 * Output boundary for the Register use case
 *
 * This interface defines how the RegisterInteractor communicates the result
 * of the register process to the presenter
 *
 * Describing only the success and failure outcomes
 */
public interface RegisterOutputBoundary {

    /**
     * Called when the register process completes successfully
     * @param data the output data containing the user's email and UID
     */
    void registerSuccess(RegisterOutputData data);

    /**
     * Called when the register process fails
     * @param errorMessage
     */
    void registerFail(String errorMessage);
}
