package data_access.login;

import UseCases.Login.LoginDataAccessInterface;

public class FirebaseAuthManager implements LoginDataAccessInterface {

    @Override
    public String login(String email, String password) {

        // this is a mock login
        if (email.equals("hahaha@gmail.com") && password.equals("hahaha123")) {
            return "mock-uid-123";  // when login successful, return the uid
        }

        return null;  // login failed
    }
}
