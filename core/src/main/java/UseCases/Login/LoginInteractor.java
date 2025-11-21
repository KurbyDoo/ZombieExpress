package UseCases.Login;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LoginInteractor implements LoginInputBoundary {
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
        }
        String json = userDataAccess.login(email, password);

        if (json == null){
            presenter.loginFailed("Invalid email or password");
            return;
        }
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        String uid = obj.get("localId").getAsString();
        LoginOutputData data = new LoginOutputData(email, uid);
        presenter.loginSuccess(data);
    }
}
