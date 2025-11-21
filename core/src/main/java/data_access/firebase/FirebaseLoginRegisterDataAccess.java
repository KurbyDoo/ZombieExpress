package data_access.firebase;

import UseCases.Login.LoginDataAccessInterface;
import UseCases.Register.RegisterUserDataAccessInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import okhttp3.*;

public class FirebaseLoginRegisterDataAccess
    implements LoginDataAccessInterface, RegisterUserDataAccessInterface {

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public FirebaseLoginRegisterDataAccess(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String newUser(String email, String password) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

            UserRecord user = FirebaseAuth.getInstance().createUser(request);

            return user.getUid();

        } catch (FirebaseAuthException e) {
            return null;
        }
    }
    @Override
    public String login(String email, String password) {
        try {
            System.out.println("DEBUG: apiKey = " + apiKey);
            System.out.println("DEBUG: email = " + email);
            System.out.println("DEBUG: password = " + password);

            String json = "{"
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\","
                + "\"returnSecureToken\":true"
                + "}";

            RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
            );

            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;
            System.out.println("DEBUG: URL = " + url);

            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

            Response response = client.newCall(request).execute();

            System.out.println("DEBUG: response code = " + response.code());
            if (response.body() != null) {
                String bodyStr = response.body().string();
                System.out.println("DEBUG: response body = " + bodyStr);
                if (!response.isSuccessful()) {
                    return null;
                }
                return bodyStr;
            } else {
                System.out.println("DEBUG: response body = null");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
