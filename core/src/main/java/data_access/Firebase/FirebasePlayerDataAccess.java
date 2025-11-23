/**
 * Data access implementation for player documents stored in Firestore.
 * Loads or creates PlayerSession objects using Firebase.
 * Used by login and register interactors.
 */
package data_access.Firebase;
import UseCases.PlayerData.PlayerDataAccessInterface;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import domain.entities.PlayerSession;

import java.util.HashMap;
import java.util.Map;

public class FirebasePlayerDataAccess implements PlayerDataAccessInterface {
    private final Firestore firestore = FirestoreClient.getFirestore();

    @Override
    public PlayerSession loadPlayerData(String email, String uid) {
        try{
           DocumentSnapshot doc = firestore.collection("players")

               //Read the document corresponding to this UID from the players table in Firestore
               .document(uid)
               .get()
               .get();

            PlayerSession playerSession = new PlayerSession();
            playerSession.setEmail(email);
            playerSession.setUid(uid);

            if (doc.exists()){
                playerSession.setLastScore(doc.getLong("lastScore").intValue());
                playerSession.setHeightScore(doc.getLong("heightScore").intValue()); // loading the exit player data
            }else {
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
        return map;
    }
}
