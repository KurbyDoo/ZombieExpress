/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Port Pattern (Data Access Interface): Defines abstraction for player data.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] Only imports domain types.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure port interface.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Player data access contract.
 * - [PASS] ISP: Focused interface.
 * - [PASS] DIP: Use cases depend on this abstraction.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Interface name follows PascalCase.
 * - [PASS] Javadoc present.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [PASS] Properly documented.
 */
package application.interface_use_cases.player_data;
import domain.player.PlayerSession;

/**
 * Data access interface for retrieving and saving player session data
 */

public interface PlayerDataAccessInterface {

    /**
     * Load the stored player session associated with given user
     * @param uid
     * @param email
     * @return
     */
    PlayerSession loadPlayerData(String uid, String email);

    /**
     * Saves the player's session back to persistent storage
     * @param session
     */
    void savePlayerData(PlayerSession session);
    // store back the player info to the database
}
