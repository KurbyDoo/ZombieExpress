/**
 * ARCHITECTURE ANALYSIS HEADER
 * ============================
 *
 * LAYER: Application (Level 2 - Application Business Rules / Use Cases)
 *
 * DESIGN PATTERNS:
 * - Interactor Pattern: Implements mount entity use case business logic.
 *
 * CLEAN ARCHITECTURE COMPLIANCE:
 * - [PASS] No imports from outer layers.
 * - [PASS] No LibGDX/framework dependencies.
 * - [PASS] Pure use case implementation.
 *
 * SOLID PRINCIPLES:
 * - [PASS] SRP: Single responsibility - handles mount logic.
 * - [PASS] LSP: Correctly implements MountEntityInputBoundary.
 * - [N/A] ISP: Single interface implemented.
 * - [N/A] DIP: Depends on domain abstractions.
 *
 * JAVA CONVENTIONS (Java 8):
 * - [PASS] Class name follows PascalCase.
 * - [MINOR] Missing Javadoc documentation.
 *
 * CHECKSTYLE OBSERVATIONS:
 * - [MINOR] Missing class-level Javadoc.
 */
package application.game_use_cases.mount_entity;

import domain.GamePosition;
import domain.entities.Rideable;
import domain.player.Player;

public class MountEntityInteractor implements MountEntityInputBoundary {
    private final Player player;

    public MountEntityInteractor(Player player) {
        this.player = player;
    }
    @Override
    public MountEntityOutputData execute(MountEntityInputData inputData) {
        Rideable ride = inputData.getRide();
        if (player.getCurrentRide() != null) {
            return new MountEntityOutputData(false);
        }

        if (ride == null) {
            return new MountEntityOutputData(false);
        }

        player.setCurrentRide(ride);
        GamePosition seatOffset = ride.getRideOffset();
        GamePosition newPlayerPos = ride.getPosition().cpy().add(seatOffset);
        player.setPosition(newPlayerPos);
        return new MountEntityOutputData(true);
    }
}
