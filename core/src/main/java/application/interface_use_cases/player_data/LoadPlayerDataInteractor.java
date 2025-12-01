/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Interactor Pattern: Implements player data loading use case.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Handles player data loading.
 * - [WARN] No InputBoundary interface implemented.
 * - [PASS] DIP: Uses PlayerDataAccessInterface abstraction.
 *
 * RECOMMENDED FIX:
 *   Create LoadPlayerDataInputBoundary interface.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [PASS] Javadoc present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Properly documented.
 */
package application.interface_use_cases.player_data;

/**
 * Interactor for loading player data use case
 */
import domain.player.PlayerSession;

public class LoadPlayerDataInteractor {

    private final PlayerDataAccessInterface dataAccess;

    /**
     * Constructs the interactor with its required data access dependency
     * @param dataAccess
     */
    public LoadPlayerDataInteractor(PlayerDataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * loads the player's stored session
     * For this moments. only contains email, uid, highest score, latest score
     * @param email
     * @param uid
     * @return
     */
    public PlayerSession load(String email, String uid) {
        return dataAccess.loadPlayerData(email, uid);

    }
}
