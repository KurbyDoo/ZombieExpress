/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules) / Main Component
 *
 * DESIGN PATTERNS:
 * - Factory Pattern: Creates and wires application components.
 * - Dependency Injection: Manual DI for interactors and data access.
 * - Composition Root: Entry point where dependencies are composed.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [WARN] This class imports from multiple layers:
 *   - application.interface_use_cases (OK - same layer)
 *   - data_access.firebase (Framework layer - should use interfaces)
 *   - presentation.view (Presentation layer)
 *   This is acceptable for a Composition Root/Main component, which is
 *   allowed to know about all layers to wire them together.
 *
 * - [WARN] Direct dependency on concrete FirebaseLoginRegisterDataAccess.
 *   Should depend on LoginDataAccessInterface abstraction instead.
 *
 * RECOMMENDED FIX:
 *   Use interfaces instead of concrete implementations:
 *   - private final LoginDataAccessInterface firebaseAuth;
 *   - private final PlayerDataAccessInterface playerDataAccess;
 *
 * SOLID PRINCIPLES:
 * - [WARN] SRP: Class handles initialization AND configuration.
 * - [PASS] OCP: Component wiring can be extended.
 * - [N/A] LSP: No inheritance.
 * - [N/A] ISP: No interfaces.
 * - [WARN] DIP: Depends on concrete classes instead of abstractions.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 * - [WARN] Extra blank line after imports (line 8).
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Multiple blank lines.
 * - [MINOR] Missing class-level Javadoc.
 */
package application;

import application.interface_use_cases.player_data.LoadPlayerDataInteractor;
import data_access.firebase.FirebaseLoginRegisterDataAccess;
import data_access.firebase.FirebasePlayerDataAccess;
import presentation.view.ViewFactory;
import presentation.view.ViewManager;


public class AppInitializer {

    private final FirebaseLoginRegisterDataAccess firebaseAuth;
    private final LoadPlayerDataInteractor loadPlayer;

    public AppInitializer() {
        String apiKey = System.getenv("FIREBASE_API_KEY");

        firebaseAuth = new FirebaseLoginRegisterDataAccess(apiKey);
        loadPlayer = new LoadPlayerDataInteractor(new FirebasePlayerDataAccess());
    }

    public ViewManager buildViewManager() {
        ViewFactory.init(firebaseAuth, loadPlayer);
        return new ViewManager();
    }
}
