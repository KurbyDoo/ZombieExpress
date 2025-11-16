package data_access;

import UseCases.Login.LoginDataAccessInterface;

public class FirebaseAuthManager implements LoginDataAccessInterface {
    @Override
    public boolean login(String email, String password) {
        if (email.equals("hahaha@gmail.com")&&password.equals("hahaha123")) {
            return true;
        }
        return false;
    }
}
