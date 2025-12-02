package interface_adapter.view_models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RegisterViewModel {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private boolean successfulRegister;
    private String errorMessage;

    public void setRegisterSuccess(boolean successfullRegister) {
        boolean oldStatus = this.successfulRegister;
        this.successfulRegister = successfullRegister;
        propertyChangeSupport.firePropertyChange("successfulRegister", oldStatus, successfullRegister);
    }

    public boolean isSuccessfulRegister() {
        return successfulRegister;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String oldErrorMessage = this.errorMessage;
        this.errorMessage = errorMessage;
        propertyChangeSupport.firePropertyChange("errorMessage", oldErrorMessage, errorMessage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
