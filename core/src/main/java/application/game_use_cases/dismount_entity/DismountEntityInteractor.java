/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Interactor Pattern: Implements dismount use case business logic.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - handles dismount logic.
 * - [PASS] LSP: Correctly implements DismountEntityInputBoundary.
 * - [N/A] ISP: Single interface implemented.
 * - [N/A] DIP: Depends on domain abstractions.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [WARN] Magic numbers (-2f, 4f) on line 21 should be named constants.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [WARN] Magic numbers should be extracted to constants.
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.dismount_entity;

import domain.GamePosition;
import domain.entities.Rideable;
import domain.player.Player;

public class DismountEntityInteractor implements DismountEntityInputBoundary {
    private final Player player;

    public DismountEntityInteractor(Player player) {
        this.player = player;
    }
    @Override
    public DismountEntityOutputData execute(DismountEntityInputData inputData) {
        if (player.getCurrentRide() == null) {
            return new DismountEntityOutputData(false);
        }

        Rideable ride = player.getCurrentRide();
        player.setCurrentRide(null);
        GamePosition seatOffset = ride.getRideOffset().cpy().add(0, -2f, 4f);
        player.setPosition(player.getPosition().sub(seatOffset));
        return new DismountEntityOutputData(true);
    }
}
