package data_access.mock_logic.login;

import application.interface_use_cases.login.LoginDataAccessInterface;
import application.interface_use_cases.register.RegisterUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

public class MockLoginRegisterDataAccess implements LoginDataAccessInterface,RegisterUserDataAccessInterface {
    private final Map<String, String> accounts = new HashMap<>();
    private final Map<String, String> uids = new HashMap<>();

    @Override
    public String newUser(String useremail, String password) {
        if (accounts.containsKey(useremail)) {
            return null;
        }
        accounts.put(useremail, password);
        String uid = "uid-" + useremail.hashCode();
        uids.put(useremail, uid);

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
