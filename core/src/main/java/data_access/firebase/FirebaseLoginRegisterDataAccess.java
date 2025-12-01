/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Frameworks & Drivers (Level 4 - Data Access)
 *
 * See docs/architecture_analysis.md for detailed analysis.
 */
package data_access.firebase;

import application.interface_use_cases.login.LoginDataAccessInterface;
import application.interface_use_cases.register.RegisterUserDataAccessInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import okhttp3.*;

/**
 * This class implement LoginDataAccessInterface and RegisterUserDataAccessInterface interfaces
 * Using Admin API create new account for new user
 * As exist player(user), using RESET API to login
 */
public class FirebaseLoginRegisterDataAccess
    implements LoginDataAccessInterface, RegisterUserDataAccessInterface {

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public FirebaseLoginRegisterDataAccess(String apiKey) {
        this.apiKey = apiKey;
    }


    /**
     * Creates a new Firebase Auth user using the Admin SDK
     *
     * @return the newly created user's UID, or null if creation failed.
     */
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

    /**
     * Logs a user in using Firebase Authentication REST API
     * Note: Firebase Admin SDK cannot perform user login, must
     * handle login through the REST API
     * @return raw JSON string containing idToken and refreshToken,
     *         or null if authentication failed.
     */
    @Override
    public String login(String email, String password) {
        try {
//            System.out.println("DEBUG: apiKey = " + apiKey);
//            System.out.println("DEBUG: email = " + email);
//            System.out.println("DEBUG: password = " + password);

            String json = "{"
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\","
                + "\"returnSecureToken\":true"
                + "}";  // construct login JSON

            RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
            );

            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;
            // construct URL

//            System.out.println("DEBUG: URL = " + url);

            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

            Response response = client.newCall(request).execute();

            System.out.println("DEBUG: response code = " + response.code());
            if (response.body() != null) {
                String bodyStr = response.body().string();
//                System.out.println("DEBUG: response body = " + bodyStr);
                if (!response.isSuccessful()) {
                    return null;
                }
                return bodyStr;
                // // Return the entire JSON response so the use case can extract idToken, refreshToken, etc
            } else {
//                System.out.println("DEBUG: response body = null");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
