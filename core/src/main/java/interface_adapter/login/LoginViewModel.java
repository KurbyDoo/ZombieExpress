// TODO: Add PropertyChangeSupport to notify the LoginView when state changes.

package interface_adapter.login;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LoginViewModel {

    private  final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private String email;
    private String errorMessage;
    private boolean successfulLogin;

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

    // PropertyChange listener
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
