package application.interface_use_cases.register;

/**
 * pack the successful register data and pass them to presenter
 */
public class RegisterOutputData {
    private final String email;
    private final String uid;

    public RegisterOutputData(String email, String uid) {
        this.email = email;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }
}
