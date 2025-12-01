/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Interface Adapter (Level 3 - ViewModels)
 *
 * DESIGN PATTERNS:
 * - ViewModel Pattern: Holds UI state for login screen.
 * - Observer Pattern: Uses PropertyChangeSupport for reactive updates.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] Only imports domain types.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure ViewModel for Clean Architecture.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - holds login UI state.
 * - [PASS] OCP: PropertyChangeSupport allows observers without modification.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Good Javadoc documentation present.
 * - [PASS] Uses standard Java beans PropertyChangeSupport.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Well-documented ViewModel.
 */
package interface_adapter.login;
import domain.player.PlayerSession;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for the Login UI.
 *
 * This class stores all UI-related state for the login screen, including:
 *  - the email entered by the user,
 *  - the success status of the login attempt,
 *  - any error message to be displayed,
 *  - and the loaded PlayerSession after a successful login.
 *
 * It uses the observer pattern (PropertyChangeSupport) so that the UI layer
 * can listen for changes and automatically update the displayed values
 */
public class LoginViewModel {

    private  final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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

    public void setSuccessfulLogin(boolean successfulLogin) {
        boolean oldSuccessfulLogin = this.successfulLogin;
        this.successfulLogin = successfulLogin;
        propertyChangeSupport.firePropertyChange("successfulLogin", oldSuccessfulLogin, successfulLogin);
    }

    public void setErrorMessage(String errorMessage) {
        String oldErrorMessage = this.errorMessage;
        this.errorMessage = errorMessage;
        propertyChangeSupport.firePropertyChange("errorMessage", oldErrorMessage, errorMessage);
    }

    public void setPlayerSession(PlayerSession playerSession) {
        PlayerSession oldPlayerSession = this.playerSession;
        this.playerSession = playerSession;
        propertyChangeSupport.firePropertyChange("playerSession", oldPlayerSession, playerSession);
    }

    //Getters
    public String getEmail() {
        return email;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccessfulLogin() {
        return successfulLogin;
    }

    public PlayerSession getPlayerSession() {
        return playerSession;
    }

    // PropertyChange listener
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
