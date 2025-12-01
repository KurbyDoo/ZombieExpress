package application.account_features.login;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Interactor for the login use case
 *
 * Implements the core business logic for authenticating a user:
 * - Validates credentials
 * - Calls the authentication data-access interface
 * - Parses returned authentication data
 * - Notifies the presenter of sucdcess login or failure
 */
public class LoginInteractor implements LoginInputBoundary {

    /**
     * Attempts to authenticate a user given email and password
     */
    private final LoginDataAccessInterface userDataAccess;
    private final LoginOutputBoundary presenter;

    public LoginInteractor(LoginDataAccessInterface userDataAccess, LoginOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }
    @Override
    public void login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()){
            presenter.loginFailed("Email or password is empty");
            return;
        }// blank input
        String json = userDataAccess.login(email, password);// Call DataAccess to log in to Firebase

        // Judge whether the login failed
        if (json == null){
            presenter.loginFailed("Invalid email or password");
            return;
        }
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        String uid = obj.get("localId").getAsString();
        LoginOutputData data = new LoginOutputData(email, uid);
        presenter.loginSuccess(data);
        // login successfully, call presenter handle success case
    }
}
