/**
 * Data access implementation for player documents stored in Firestore.
 * Loads or creates PlayerSession objects using Firebase.
 * Used by login and register interactors.
 */
package data_access.firebase;
import application.interface_use_cases.player_data.PlayerDataAccessInterface;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import domain.player.PlayerData;
import domain.player.PlayerSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebasePlayerDataAccess implements PlayerDataAccessInterface {
    private final Firestore firestore = FirestoreClient.getFirestore();

    @Override
    public PlayerSession loadPlayerData(String email, String uid) {
        try {
            DocumentSnapshot doc = firestore.collection("players")

                //Read the document corresponding to this UID from the players table in Firestore
                .document(uid)
                .get()
                .get();

            PlayerSession playerSession = new PlayerSession();
            playerSession.setEmail(email);
            playerSession.setUid(uid);

            if (doc.exists()) {
                playerSession.setLastScore(doc.getLong("lastScore").intValue());
                playerSession.setHeightScore(doc.getLong("heightScore").intValue()); // loading the exit player data
            } else {
                playerSession.setLastScore(0);
                playerSession.setHeightScore(0);
                firestore.collection("players").document(uid).set(sessionToMap(playerSession));
                // if new user, create default data and store back in Firestore
            }
            return playerSession;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void savePlayerData(PlayerSession playerSession) {
        firestore.collection("players").document(playerSession.getUid()).set(sessionToMap(playerSession));
    }

    // helper function
    // Convert PlayerSession object to a Firestore-storable Map<String, Object>
    private Map<String, Object> sessionToMap(PlayerSession playerSession) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", playerSession.getUid());
        map.put("email", playerSession.getEmail());
        map.put("lastScore", playerSession.getLastScore());
        map.put("heightScore", playerSession.getHeightScore());

        System.out.println("[Firebase] Writing to Firestore: " + map);

        return map;

    }
    @Override
    public List<PlayerData> getAllPlayers() {
        List<PlayerData> result = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection("players").get();
            QuerySnapshot snapshot = future.get();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                String uid = doc.getString("uid");
                String email = doc.getString("email");

                Long last = doc.getLong("lastScore");
                Long high = doc.getLong("heightScore");

                int lastScore = (last == null ? 0 : last.intValue());
                int highScore = (high == null ? 0 : high.intValue());

                result.add(new PlayerData(uid, email, lastScore, highScore));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
