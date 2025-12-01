package framework.data_access.mock_logic.login;

import application.account_features.login.LoginDataAccessInterface;
import application.account_features.register.RegisterUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

public class MockLoginRegisterDataAccess implements LoginDataAccessInterface,RegisterUserDataAccessInterface {
    private final Map<String, String> accounts = new HashMap<>();
    private final Map<String, String> uids = new HashMap<>();

    @Override
    public String newUser(String email, String password) {
        if (accounts.containsKey(email)) {
            return null;
        }
        accounts.put(email, password);
        String uid = "uid-" + email.hashCode();
        uids.put(email, uid);

        return uid;
    }

    @Override
    public String login(String email, String password) {
        if (!accounts.containsKey(email)) {
            return null;
        }
        if (!accounts.get(email).equals(password)) {
            return null;
        }
        return uids.get(email);
    }
}
