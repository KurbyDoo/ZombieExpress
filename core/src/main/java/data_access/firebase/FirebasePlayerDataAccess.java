package data_access.firebase;
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
               .document(uid)
               .get()
               .get();

            PlayerSession playerSession = new PlayerSession();
            playerSession.setEmail(email);
            playerSession.setUid(uid);

            if (doc.exists()){
                playerSession.setLastScore(doc.getLong("lastScore").intValue());
                playerSession.setHeightScore(doc.getLong("heightScore").intValue());
            }else {
                playerSession.setLastScore(0);
                playerSession.setHeightScore(0);

                firestore.collection("players").document(uid).set(sessionToMap(playerSession));
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

    private Map<String, Object> sessionToMap(PlayerSession playerSession) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", playerSession.getUid());
        map.put("email", playerSession.getEmail());
        map.put("lastScore", playerSession.getLastScore());
        map.put("heightScore", playerSession.getHeightScore());
        return map;
    }
}
