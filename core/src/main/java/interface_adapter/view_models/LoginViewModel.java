package interface_adapter.view_models;

import domain.player.PlayerSession;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for the Login UI.
 * <p>
 * This class stores all UI-related state for the login screen, including:
 * - the email entered by the user,
 * - the success status of the login attempt,
 * - any error message to be displayed,
 * - and the loaded PlayerSession after a successful login.
 * <p>
 * It uses the observer pattern (PropertyChangeSupport) so that the UI layer
 * can listen for changes and automatically update the displayed values
 */
public class LoginViewModel {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private String email; // The email displayed on the login interface
    private String errorMessage; // Errors displayed by UI
    private boolean successfulLogin;
    private PlayerSession playerSession; // Player information loaded after logging in

    // Setters
    public void setLoginEmail(String email) {
        String oldEmail = this.email;
        this.email = email;
        propertyChangeSupport.firePropertyChange("email", oldEmail, email);
    }

    //Getters
    public String getEmail() {
        return email;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String oldErrorMessage = this.errorMessage;
        this.errorMessage = errorMessage;
        propertyChangeSupport.firePropertyChange("errorMessage", oldErrorMessage, errorMessage);
    }

    public boolean isSuccessfulLogin() {
        return successfulLogin;
    }

    public void setSuccessfulLogin(boolean successfulLogin) {
        boolean oldSuccessfulLogin = this.successfulLogin;
        this.successfulLogin = successfulLogin;
        propertyChangeSupport.firePropertyChange("successfulLogin", oldSuccessfulLogin, successfulLogin);
    }

    public PlayerSession getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(PlayerSession playerSession) {
        PlayerSession oldPlayerSession = this.playerSession;
        this.playerSession = playerSession;
        propertyChangeSupport.firePropertyChange("playerSession", oldPlayerSession, playerSession);
    }

    // PropertyChange listener
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
