package framework.data_access.mock_logic.login;

import application.account_features.login.LoginDataAccessInterface;

public class MockfirebaseAuthManager implements LoginDataAccessInterface {

    @Override
    public String login(String email, String password) {

        // this is a mock login
        if (email.equals("hahaha@gmail.com") && password.equals("hahaha123")) {
            return "mock-uid-123";  // when login successful, return the uid
        }

        return null;  // login failed
    }
}
