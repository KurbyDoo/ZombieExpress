/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Interface Adapter (Level 3 - ViewModels)
 *
 * DESIGN PATTERNS:
 * - ViewModel Pattern: Holds UI state for registration screen.
 * - Observer Pattern: Uses PropertyChangeSupport.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No outer layer imports.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure ViewModel.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Holds registration UI state.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package interface_adapter.register;
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

    public void setErrorMessage(String errorMessage) {
        String oldErrorMessage = this.errorMessage;
        this.errorMessage = errorMessage;
        propertyChangeSupport.firePropertyChange("errorMessage", oldErrorMessage, errorMessage);
    }


    public boolean isSuccessfulRegister() {
        return successfulRegister;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
