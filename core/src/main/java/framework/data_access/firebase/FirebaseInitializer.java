package framework.data_access.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.InputStream;

/**
 * Initializes the Firebase application using the service account credentials.
 * This must be called once at application startup before any Firestore operations.
 */

public class FirebaseInitializer {

    public static void init() {
        // prevent duplicate case
        if (!FirebaseApp.getApps().isEmpty()) return;

        try {
            // Load service account JSON form resources folder
            InputStream serviceAccount =
                FirebaseInitializer.class.getClassLoader()
                    .getResourceAsStream("serviceAccountKey.json");
            // Build Firebase options with credentials
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            // Initialize global FirebaseApp instance
            FirebaseApp.initializeApp(options);

        } catch (Exception e) {
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }
}
