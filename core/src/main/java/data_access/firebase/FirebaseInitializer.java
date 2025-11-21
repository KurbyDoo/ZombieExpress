package data_access.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.InputStream;

public class FirebaseInitializer {

    public static void init() {
        if (!FirebaseApp.getApps().isEmpty()) return;

        try {
            InputStream serviceAccount =
                FirebaseInitializer.class.getClassLoader()
                    .getResourceAsStream("serviceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception e) {
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }
}
