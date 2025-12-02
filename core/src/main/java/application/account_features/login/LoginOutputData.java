package application.account_features.login;

/**
 * pack the successful login data and pass them to presenter
 */

public class LoginOutputData {
    private final String uid;
    private final String email;

    public LoginOutputData(String email, String uid) {
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
