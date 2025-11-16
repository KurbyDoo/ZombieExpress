// TODO: Add PropertyChangeSupport to notify the LoginView when state changes.

package interface_adapter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LoginViewModel {

    private  final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private String email;
    private String errorMssage;
    private boolean successfulLogin;

    public void loginEmail(String email) {
        this.email = email;
    }

    public void setSuccessfulLogin(boolean successfulLogin) {
        this.successfulLogin = successfulLogin;
    }

    public void setErrorMssage(String errorMssage) {
        this.errorMssage = errorMssage;
    }

    public String getLoginEmail() {
        return email;
    }

    public String setLoginEmail(String email) {
        String oldEmail = this.email;
        this.email = email;
        return oldEmail;
    }

    public String getErrorMessage() {
        return errorMssage;
    }

    public boolean isSuccessfulLogin() {
        return successfulLogin;
    }

    public void firePropertyChange() {
        propertyChangeSupport.firePropertyChange("login", null, this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
